/******************************************************************************
 * Copyright (C) 2016 Logilite Technologies LLP								  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *                                                                            * 
 * Contributor:                                                               * 
 *   Andreas Sumerauer                                                        * 
 *****************************************************************************/

package org.adempiere.webui.apps.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.util.Callback;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.AbstractADWindowContent;
import org.adempiere.webui.adwindow.QuickGridView;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.CustomizeGridViewDialog;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.DataStatusEvent;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkforge.keylistener.Keylistener;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;

/**
 * Quick entry form.
 * 
 * @author Logilite Technologies
 * @since Nov 03, 2017
 */
public class WQuickForm extends Window implements IQuickForm
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -5363771364595732977L;

	/** Main layout of form */
	private Borderlayout			mainLayout			= new Borderlayout();
	/** Calling ADWindowContent instance */
	private AbstractADWindowContent	adWinContent		= null;
	/** Center of {@link #mainLayout}. Grid/List view for multi record entry. */
	private QuickGridView			quickGridView		= null;
	/** Current selected grid tab of {@link #adWinContent} */
	private GridTab					gridTab;

	/** Action buttons panel. South of {@link #mainLayout} */
	private ConfirmPanel			confirmPanel		= new ConfirmPanel(true, true, false, false, false, false);
	private Button					bDelete				= confirmPanel.createButton(ConfirmPanel.A_DELETE);
	private Button					bSave				= confirmPanel.createButton("Save");
	private Button					bIgnore				= confirmPanel.createButton("Ignore");
	private Button					bCustomize			= confirmPanel.createButton("Customize");
	private Button					bUnSort				= confirmPanel.createButton("UnSort");

	private boolean					onlyCurrentRows		= false;

	private int						onlyCurrentDays		= 0;

	protected QuickGridView			prevQGV				= null;

	private int						windowNo;

	private boolean stayInParent;
	/* SysConfig USE_ESC_FOR_TAB_CLOSING */
	private boolean isUseEscForTabClosing = MSysConfig.getBooleanValue(MSysConfig.USE_ESC_FOR_TAB_CLOSING, false, Env.getAD_Client_ID(Env.getCtx()));

	/**
	 * @param winContent
	 * @param m_onlyCurrentRows
	 * @param m_onlyCurrentDays
	 */
	public WQuickForm(AbstractADWindowContent winContent, boolean m_onlyCurrentRows, int m_onlyCurrentDays)
	{
		super();

		this.setMode(Mode.POPUP);
		LayoutUtils.addSclass("quick-form", this);
		windowNo = SessionManager.getAppDesktop().registerWindow(this);
		adWinContent = winContent;
		onlyCurrentRows = m_onlyCurrentRows;
		onlyCurrentDays = m_onlyCurrentDays;
		this.gridTab = adWinContent.getADTab().getSelectedGridTab();
		this.quickGridView = new QuickGridView(adWinContent, gridTab, this);
		this.quickGridView.setVisible(true);
		initForm();
		gridTab.isQuickForm = true;

		gridTab.addDataStatusListener(this);

		// To maintain parent-child Quick Form
		prevQGV = adWinContent.getCurrQGV();
		adWinContent.setCurrQGV(quickGridView);
		
		addCallback(AFTER_PAGE_DETACHED, t -> adWinContent.focusToLastFocusEditor());
	}

	/**
	 * Initialize form.
	 */
	protected void initForm( )
	{
		initZk();
		createNewRow();
		quickGridView.refresh(gridTab);
	}

	/**
	 * Layout form.
	 */
	private void initZk( )
	{
		// Center
		Panel Center = new Panel();
		Center.appendChild(quickGridView);

		// South
		Panel south = new Panel();

		// Adding statusBar for Quick Form
		south.appendChild(adWinContent.getStatusBarQF());
		south.appendChild(confirmPanel);

		bSave.setEnabled(!gridTab.isReadOnly());
		bDelete.setEnabled(!gridTab.isReadOnly());
		bIgnore.setEnabled(!gridTab.isReadOnly());
		bUnSort.setEnabled(!gridTab.isReadOnly());

		bSave.addEventListener(Events.ON_CLICK, this);
		bDelete.addEventListener(Events.ON_CLICK, this);
		bIgnore.addEventListener(Events.ON_CLICK, this);
		bCustomize.addEventListener(Events.ON_CLICK, this);
		bUnSort.addEventListener(Events.ON_CLICK, this);

		Button bRefresh = confirmPanel.getButton(ConfirmPanel.A_REFRESH);
		Button bCancel = confirmPanel.getButton(ConfirmPanel.A_CANCEL);
		Button bOk = confirmPanel.getButton(ConfirmPanel.A_OK);

		// Set tool-tip information
		bSave.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormSave")); // 'Alt + S'
		bDelete.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormDelete")); // 'Alt + D'
		bIgnore.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormIgnore")); // 'Alt + Z'
		bUnSort.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormUnSort")); // 'Alt + R'
		bCustomize.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormCustomize")); // 'Alt + L'
		bOk.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormOk")); // 'Alt + K' - Save_Close
		bCancel.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormCancel")); // 'Alt + X'
		bRefresh.setTooltiptext(Msg.translate(Env.getCtx(), "QuickFormRefresh")); // 'Alt + E'

		confirmPanel.addComponentsLeft(bSave);
		confirmPanel.addComponentsLeft(bDelete);
		confirmPanel.addComponentsLeft(bIgnore);
		confirmPanel.addComponentsLeft(bCustomize);
		confirmPanel.addComponentsLeft(bUnSort);
		confirmPanel.addActionListener(this);

		mainLayout.appendCenter(Center);
		mainLayout.appendSouth(south);

		this.appendChild(mainLayout);
	} // initZk

	@Override
	public void onEvent(Event event) throws Exception
	{
		if (event.getTarget() == confirmPanel.getButton(ConfirmPanel.A_CANCEL))
		{
			onCancel();
		}
		else if (event.getTarget() == confirmPanel.getButton(ConfirmPanel.A_REFRESH))
		{
			quickGridView.getRenderer().setCurrentCell(null);
			onRefresh();
		}
		if (event.getTarget() == confirmPanel.getButton(ConfirmPanel.A_OK))
		{
			onSave();
			dispose();
		}
		else if (event.getTarget() == confirmPanel.getButton("Save"))
		{
			quickGridView.getRenderer().setCurrentCell(null);
			onSave();
		}
		else if (event.getTarget() == confirmPanel.getButton(ConfirmPanel.A_DELETE))
		{
			quickGridView.getRenderer().setCurrentCell(null);
			onDelete();
		}
		else if (event.getTarget() == confirmPanel.getButton("Ignore"))
		{
			quickGridView.getRenderer().setCurrentCell(null);
			onIgnore();
		}
		else if (event.getTarget() == confirmPanel.getButton("Customize"))
		{
			onCustomize();
		}
		else if (event.getTarget() == confirmPanel.getButton("UnSort"))
		{
			onUnSort();
		}
		event.stopPropagation();
	} // onEvent

	/**
	 * Cancel/Close form.
	 */
	@Override
	public void onCancel( )
	{
		if (gridTab.getTableModel().getRowChanged() > -1)
		{
			Dialog.ask(windowNo, "SaveChanges?", new Callback <Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
						onSave();
					dispose();
				}
			});
		}
		else
		{
			dispose();
		}
	} // onCancel

	/**
	 * Reset sort state
	 */
	@Override
	public void onUnSort( )
	{
		adWinContent.getActiveGridTab().getTableModel().resetCacheSortState();
		Column sortColumn = quickGridView.findCurrentSortColumn();

		onRefresh();

		if (sortColumn != null)
			sortColumn.setSortDirection("natural");

		adWinContent.getStatusBarQF().setStatusLine(Msg.getMsg(Env.getCtx(), "UnSort"), false);
	} // onUnSort

	/**
	 * Open {@link CustomizeGridViewDialog} for {@link #quickGridView}.
	 */
	@Override
	public void onCustomize( )
	{
		onSave();
		//
		Columns columns = quickGridView.getListbox().getColumns();
		List <Component> columnList = columns.getChildren();
		GridField[] fields = quickGridView.getGridField();
		Map <Integer, String> columnsWidth = new HashMap <Integer, String>();
		ArrayList <Integer> gridFieldIds = new ArrayList <Integer>();

		for (int i = 0; i < fields.length; i++)
		{
			Column column = (Column) columnList.get(i + 1);
			String width = column.getWidth();
			columnsWidth.put(fields[i].getAD_Field_ID(), width);
			gridFieldIds.add(fields[i].getAD_Field_ID());
		}

		ZKUpdateUtil.setWidth(quickGridView, getWidth());
		ZKUpdateUtil.setHeight(quickGridView, getHeight());

		CustomizeGridViewDialog.showCustomize(0, gridTab.getAD_Tab_ID(), columnsWidth, gridFieldIds, null, quickGridView, true, null);
	} // onCustomize

	/**
	 * Ignore/Undo changes
	 */
	@Override
	public void onIgnore( )
	{
		gridTab.dataIgnore();
		gridTab.dataRefreshAll();
		adWinContent.getStatusBarQF().setStatusLine(Msg.getMsg(Env.getCtx(), "Ignored"), false);
		quickGridView.isNewLineSaved = true;
		// Create new record if no record present.
		if (gridTab.getRowCount() <= 0)
			createNewRow();
		quickGridView.updateListIndex();
		Events.echoEvent(QuickGridView.EVENT_ON_SET_FOCUS_TO_FIRST_CELL, quickGridView, null);
	} // onIgnore

	/**
	 * Delete selected rows.
	 */
	@Override
	public void onDelete( )
	{
		if (gridTab == null || !quickGridView.isNewLineSaved)
			return;

		// if no any row selected then delete current record
		if (gridTab.getSelection().length == 0)
			gridTab.addToSelection(quickGridView.getRenderer().getCurrentRowIndex());

		final int[] indices = gridTab.getSelection();
		if (indices.length > 0)
		{
			Dialog.ask(windowNo, "DeleteRecord?", new Callback <Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						boolean isAllSelected = quickGridView.isAllSelected();
						quickGridView.isNewLineSaved = true;
						gridTab.clearSelection();
						quickGridView.toggleSelectionForAll(false);
						Arrays.sort(indices);
						int count = 0;
						for (int i = 0, offset = 0; i < indices.length; i++)
						{
							gridTab.navigate(indices[i] - offset);
							if (gridTab.dataDelete())
							{
								offset++;
								count++;
							}
							else
							{
								break;
							}
						}

						adWinContent.getStatusBarQF().setStatusLine(Msg.getMsg(Env.getCtx(), "Deleted") + ": " + count + " / " + indices.length, false);

						// if all records is deleted then it will show default with new record.
						if (gridTab.getRowCount() <= 0)
							quickGridView.createNewLine();
						quickGridView.updateListIndex();

						// Set focus on the first row if all Row's are selected.
						if (isAllSelected)
							Events.echoEvent(QuickGridView.EVENT_ON_PAGE_NAVIGATE, quickGridView, null);
						else
							Events.echoEvent(QuickGridView.EVENT_ON_SET_FOCUS_TO_FIRST_CELL, quickGridView, null);
					}
					else
					{
						gridTab.clearSelection();
						quickGridView.toggleSelectionForAll(false);
					}
				}
			});
		}
	} // onDelete

	/**
	 * Save {@link #quickGridView} changes.
	 */
	@Override
	public void onSave( )
	{
		if (gridTab.getTableModel().getRowChanged() == gridTab.getCurrentRow())
		{
			if (quickGridView.dataSave(0))
			{
				gridTab.dataRefreshAll();
				adWinContent.getStatusBarQF().setStatusLine(Msg.getMsg(Env.getCtx(), "Saved"), false);
				Events.echoEvent(QuickGridView.EVENT_ON_SET_FOCUS_TO_FIRST_CELL, quickGridView, null);
			}
		}
		else
		{
			onIgnore();
		}
	} // onSave

	/**
	 * Refresh {@link #gridTab} and {@link #quickGridView}.
	 */
	@Override
	public void onRefresh( )
	{
		gridTab.dataRefreshAll();
		adWinContent.getStatusBarQF().setStatusLine(Msg.getMsg(Env.getCtx(), "Refresh"), false);
		quickGridView.isNewLineSaved = true;
		quickGridView.updateListIndex();
		Events.echoEvent(QuickGridView.EVENT_ON_SET_FOCUS_TO_FIRST_CELL, quickGridView, null);
		// Create new record if no record present.
		if (gridTab.getRowCount() <= 0)
			createNewRow();
	} // onRefresh

	/**
	 * Close form.
	 */
	@Override
	public void dispose( )
	{
		super.dispose();

		// do not allow to close tab for Events.ON_CTRL_KEY event
		if(isUseEscForTabClosing)
			SessionManager.getAppDesktop().setCloseTabWithShortcut(false);
		
		gridTab.setQuickForm(false);
		onIgnore();
		gridTab.removeDataStatusListener(this);
		adWinContent.closeQuickFormTab(gridTab.getAD_Tab_ID());
		quickGridView.getRenderer().clearMaps();
		int tabLevel = adWinContent.getToolbar().getQuickFormTabHrchyLevel();
		if (tabLevel > 0)
		{
			adWinContent.getToolbar().setQuickFormTabHrchyLevel(tabLevel - 1);
			Keylistener keyListener = SessionManager.getSessionApplication().getKeylistener();
			keyListener.setCtrlKeys(keyListener.getCtrlKeys() + QuickGridView.CNTRL_KEYS);

			// Add Key-listener of parent Quick Form
			if (prevQGV != null)
			{
				adWinContent.onParentRecord();
				SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, prevQGV);
				// need to set focus on last focused row of parent Form.
				Events.echoEvent(QuickGridView.EVENT_ON_PAGE_NAVIGATE, prevQGV, null);
			}
			adWinContent.setCurrQGV(prevQGV);
		}
		else
		{
			adWinContent.setCurrQGV(null);
		}
		adWinContent.getADTab().getSelectedTabpanel().query(onlyCurrentRows, onlyCurrentDays, adWinContent.getADTab().getSelectedTabpanel().getGridTab().getMaxQueryRecords()); // autoSize

		if (stayInParent) {
			adWinContent.onParentRecord();
		}
	} // dispose

	/**
	 * Add new row to {@link #quickGridView}.
	 */
	private void createNewRow( )
	{
		int row = gridTab.getRowCount();
		// creating the first record from the parent tab first record is duplicated on KEY DOWN.
		// If a grid does not have a record or blank record then create a new row.
		if (row <= 0 || (gridTab.isNew() && row == 1))
		{
			gridTab.dataIgnore();
			if (gridTab.isInsertRecord())
			{
				quickGridView.createNewLine();
			}
			else
			{
				adWinContent.getStatusBarQF().setStatusLine(Msg.getMsg(Env.getCtx(), "NewError"), true);
			}
		}
	} // createNewRow

	@Override
	public void dataStatusChanged(DataStatusEvent e)
	{
		// ignore background event
		if (Executions.getCurrent() == null || e.isInitEdit())
			return;

		// update Dynamic display on data Status change.
		int col = e.getChangedColumn();
		quickGridView.dynamicDisplay(col);
	} // dataStatusChanged

	/**
	 * If stayInParent is true, {@link #adWinContent} should navigate to parent record after closing this form instance.
	 * @param stayInParent
	 */
	public void setStayInParent(boolean stayInParent) {
		this.stayInParent = stayInParent;
	}
}
