/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.adwindow;

import static org.compiere.model.SystemIDs.PROCESS_AD_CHANGELOG_REDO;
import static org.compiere.model.SystemIDs.PROCESS_AD_CHANGELOG_UNDO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.adempiere.exceptions.DBException;
import org.adempiere.util.Callback;
import org.adempiere.webui.AdempiereIdGenerator;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.Extensions;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.WArchive;
import org.adempiere.webui.WRequest;
import org.adempiere.webui.WZoomAcross;
import org.adempiere.webui.acct.WAcctViewer;
import org.adempiere.webui.adwindow.validator.WindowValidatorEvent;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.adempiere.webui.adwindow.validator.WindowValidatorManager;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.BusyDialogTemplate;
import org.adempiere.webui.apps.HelpWindow;
import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.apps.form.WCreateFromFactory;
import org.adempiere.webui.apps.form.WCreateFromWindow;
import org.adempiere.webui.apps.form.WQuickForm;
import org.adempiere.webui.component.DesktopTabpanel;
import org.adempiere.webui.component.Mask;
import org.adempiere.webui.component.ProcessInfoDialog;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.component.ZkCssHelper;
import org.adempiere.webui.editor.IProcessButton;
import org.adempiere.webui.editor.WButtonEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ToolbarListener;
import org.adempiere.webui.exception.ApplicationException;
import org.adempiere.webui.factory.InfoManager;
import org.adempiere.webui.info.InfoWindow;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.InfoPanel;
import org.adempiere.webui.panel.WAttachment;
import org.adempiere.webui.panel.WDocActionPanel;
import org.adempiere.webui.panel.action.CSVImportAction;
import org.adempiere.webui.panel.action.ExportAction;
import org.adempiere.webui.panel.action.FileImportAction;
import org.adempiere.webui.panel.action.ReportAction;
import org.adempiere.webui.part.AbstractUIPart;
import org.adempiere.webui.part.ITabOnSelectHandler;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.CustomizeGridViewDialog;
import org.adempiere.webui.window.Dialog;
import org.adempiere.webui.window.FindWindow;
import org.adempiere.webui.window.LabelAction;
import org.adempiere.webui.window.WChat;
import org.adempiere.webui.window.WPostIt;
import org.adempiere.webui.window.WRecordAccessDialog;
import org.adempiere.webui.window.WTableAttribute;
import org.compiere.grid.ICreateFrom;
import org.compiere.model.DataStatusEvent;
import org.compiere.model.DataStatusListener;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.GridWindow;
import org.compiere.model.GridWindowVO;
import org.compiere.model.I_M_Product;
import org.compiere.model.MImage;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MProjectIssue;
import org.compiere.model.MQuery;
import org.compiere.model.MRecentItem;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MTableAttributeSet;
import org.compiere.model.MWindow;
import org.compiere.model.PO;
import org.compiere.model.StateChangeEvent;
import org.compiere.model.SystemIDs;
import org.compiere.model.SystemProperties;
import org.compiere.model.X_AD_CtxHelp;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoLog;
import org.compiere.process.ProcessInfoUtil;
import org.compiere.tools.FileUtil;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Popup;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window.Mode;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * Abstract controller class for the content of AD Window (toolbar+breadcrumb+tabs+statusbar).
 * 
 * @author <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @author <a href="mailto:hengsin@gmail.com">Low Heng Sin</a>
 * @date Feb 25, 2007
 *
 * @author Cristina Ghita, www.arhipac.ro
 * see FR [ 2877111 ] See identifiers columns when delete records https://sourceforge.net/p/adempiere/feature-requests/855/
 *
 * @author hengsin, hengsin.low@idalica.com
 * see FR [2887701] https://sourceforge.net/p/adempiere/feature-requests/866/
 * @sponsor www.metas.de
 *
 * @author Teo Sarca, teo.sarca@gmail.com
 *  	<li>BF [ 2992540 ] Grid/Panel not refreshed after process run
 *  		https://sourceforge.net/p/adempiere/zk-web-client/418/
 *  	<li>BF [ 2985892 ] Opening a window using a new record query is not working
 *  		https://sourceforge.net/p/adempiere/zk-web-client/411/
 */
public abstract class AbstractADWindowContent extends AbstractUIPart implements ToolbarListener,
        EventListener<Event>, DataStatusListener, ActionListener, ITabOnSelectHandler
{
	/**
	 * Execution attribute to hold detail ADTabpanel that changes have just been
	 * saved
	 */
	private static final String DETAIL_TABPANEL_SAVED_ATTR = "detail.adtabpanel.saved";

	/** onFocus event that's defer behind other event using echo */
    private static final String ON_FOCUS_DEFER_EVENT = "onFocusDefer";

    /**
     * Event to set selected tab of detail pane. Defer behind other event using echo.<br/>
     * Event data: data[0] is tab index and data[1] is current row
     */
	private static final String ON_DEFER_SET_DETAILPANE_SELECTION_EVENT = "onDeferSetDetailpaneSelection";

	/** ProcessModalDialog attribute to store reference for post process execution callback */
	private static final String PROCESS_POST_CALLBACK_ATTRIBUTE = "processPostCallbackAttribute";
	
	private static final CLogger logger;

    static
    {
        logger = CLogger.getCLogger(AbstractADWindowContent.class);
    }

    /** Env ctx */
    private Properties           ctx;

    /** VO for AD Window */
    private GridWindow           gridWindow;

    /** status bar for message and record info */
    protected StatusBar     	 statusBar;

    /** UI part for AD_Tabs */
    protected IADTabbox          adTabbox;

    /** register window (desktop tab) no */ 
    private int                  curWindowNo;

    /**
     * True to show only unprocessed or the one updated within x days (default is 1 day before today)
     */
    private boolean              m_onlyCurrentRows = true;

    protected ADWindowToolbar    toolbar;

    /** window title **/
    protected String             title;

    /**
     * if > 0, filter records with created >= current_date - m_onlyCurrentDays
     */
	private int m_onlyCurrentDays = 0;

	/** true if find window cancel by user */
	private boolean m_findCancelled;

	/** true if user press new button at find window */
	private boolean m_findCreateNew;

	/** true when initial query for first tab is running */
	private boolean m_queryInitiating;

	/** path to selected tab */
	protected BreadCrumb breadCrumb;

	/** AD_Window.AD_Window_ID */
	private int adWindowId;

	/** image for window title */
	private MImage image;
	
	/** delete confirmation logic for selected tab */
	private String deleteConfirmationLogic;

	/**
	 * Quick Form Status bar
	 */
	protected StatusBar statusBarQF;

	/**
	 * Maintain no of quick form tabs open
	 */
	protected ArrayList <Integer> quickFormOpenTabs	= new ArrayList <Integer>();

	/** track last focus field editor component */
	protected Component lastFocusEditor = null;
	
	/**
	 * Constructor
	 * @param ctx
	 * @param windowNo
	 * @param adWindowId 
	 */
    public AbstractADWindowContent(Properties ctx, int windowNo, int adWindowId)
    {
        this.ctx = ctx;
        this.curWindowNo = windowNo;
        this.adWindowId = adWindowId;

        initComponents();
    }

    /**
     * Create {@link IADTabbox} and setup listeners.<br/>
     * Call from {@link ADWindow}, don't call this directly.
     * @param parent
     * @return Component
     */
	public Component createPart(Object parent)
    {
		adTabbox = createADTab();
		adTabbox.setSelectionEventListener(this);
		adTabbox.setADWindowPanel(this);

        Component comp = super.createPart(parent);
        comp.addEventListener(LayoutUtils.ON_REDRAW_EVENT, this);
        comp.addEventListener(ON_DEFER_SET_DETAILPANE_SELECTION_EVENT, this);
        comp.addEventListener(ON_FOCUS_DEFER_EVENT, this);
        comp.setAttribute(ITabOnSelectHandler.ATTRIBUTE_KEY, this);
        
        return comp;
    }

	/**
	 * Get bread crumb component
	 * @return {@link BreadCrumb}
	 */
	public BreadCrumb getBreadCrumb()
	{
		return breadCrumb;
	}

	/**
	 * Get status bar component
	 * @return {@link StatusBar}
	 */
	public StatusBar getStatusBar()
    {
    	return statusBar;
    }

	/**
	 * Create {@link #toolbar}, {@link #statusBar} and {@link #gridWindow}.
	 */
    private void initComponents()
    {
        /** Initialise toolbar */
        toolbar = new ADWindowToolbar(this, getWindowNo());
        toolbar.setId("windowToolbar");
        toolbar.addListener(this);

        statusBar = new StatusBar();
        
        GridWindowVO gWindowVO = AEnv.getMWindowVO(curWindowNo, adWindowId, 0);
        if (gWindowVO == null)
        {
            throw new ApplicationException(Msg.getMsg(ctx,
                    "AccessTableNoView")
                    + "(No Window Model Info)");
        }
        gridWindow = new GridWindow(gWindowVO, true);
        title = gridWindow.getName();
        image = gridWindow.getMImage();
    }    

    /**
     * Override to create {@link IADTabbox} instance.
     * @return {@link IADTabbox}
     */
    protected abstract IADTabbox createADTab();

    /**
     * Handle switching of editing status.<br/>
     * Override to set isEditting to true/false at widget side.
     * @param editStatus true if editing (dirty)
     */
    protected abstract void switchEditStatus(boolean editStatus);
    
    /**
     * Set focus to selected tab panel
     */
    public void focusToActivePanel() {
    	IADTabpanel adTabPanel = adTabbox.getSelectedTabpanel();
		focusToTabpanel(adTabPanel);
	}

    /**
     * Echo ON_FOCUS_DEFER_EVENT event for {@link ADTabpanel}
     * @param adTabPanel
     */
    private void focusToTabpanel(IADTabpanel adTabPanel ) {
		if (adTabPanel != null && adTabPanel instanceof HtmlBasedComponent) {
			Events.echoEvent(ON_FOCUS_DEFER_EVENT, getComponent(), (HtmlBasedComponent)adTabPanel);
		}
	}

    /**
     * Init all tab panels.<br/>
     * Delegate the init of each individual tab panel to {@link #initTab(MQuery, int)}.
     * @param query
     * @return boolean
     */
	public boolean initPanel(MQuery query)
    {
		// This temporary validation code is added to check the reported bug
		// [ adempiere-ZK Web Client-2832968 ] User context lost?
		// https://sourceforge.net/p/adempiere/zk-web-client/303/
		// it's harmless, if there is no bug then this must never fail
		Session currSess = Executions.getCurrent().getDesktop().getSession();
		int checkad_user_id = -1;
		if (currSess != null && currSess.getAttribute(AdempiereWebUI.CHECK_AD_USER_ID_ATTR) != null)
			checkad_user_id = (Integer)currSess.getAttribute(AdempiereWebUI.CHECK_AD_USER_ID_ATTR);
		if (checkad_user_id!=Env.getAD_User_ID(ctx))
		{
			String msg = "Timestamp=" + new Date()
					+ ", Bug 2832968 SessionUser="
					+ checkad_user_id
					+ ", ContextUser="
					+ Env.getAD_User_ID(ctx)
					+ ".  Please report conditions to your system administrator or in sf tracker 2832968";
			ApplicationException ex = new ApplicationException(msg);
			logger.log(Level.SEVERE, msg, ex);
			throw ex;
		}
		// End of temporary code for [ adempiere-ZK Web Client-2832968 ] User context lost?

		// Set AutoCommit for this Window
		Env.setAutoCommit(ctx, curWindowNo, Env.isAutoCommit(ctx));
		boolean autoNew = Env.isAutoNew(ctx);
		Env.setAutoNew(ctx, curWindowNo, autoNew);

        // WindowName variable preserved for backward compatibility
        // please consider it as DEPRECATED and use _WinInfo_WindowName instead 
        Env.setContext(ctx, curWindowNo, "WindowName", gridWindow.getName()); // deprecated
        Env.setContext(ctx, curWindowNo, "_WinInfo_WindowName", gridWindow.getName());
        Env.setContext(ctx, curWindowNo, "_WinInfo_AD_Window_ID", gridWindow.getAD_Window_ID());
        Env.setContext(ctx, curWindowNo, "_WinInfo_AD_Window_UU", gridWindow.getAD_Window_UU());

        // Set SO/AutoNew for Window
        Env.setContext(ctx, curWindowNo, "IsSOTrx", gridWindow.isSOTrx());
        if (!autoNew && gridWindow.isTransaction())
        {
            Env.setAutoNew(ctx, curWindowNo, true);
        }

        m_onlyCurrentRows =  gridWindow.isTransaction();

        MQuery detailQuery = null;
        
        // Check is the query argument a query for detail tab
        if (query != null && query.getZoomTableName() != null && query.getZoomColumnName() != null)
    	{
    		if (!query.getZoomTableName().equalsIgnoreCase(gridWindow.getTab(0).getTableName()))
    		{
    			detailQuery = query;
    			query = new MQuery();
    			query.addRestriction("1=2");
    			query.setRecordCount(0);
    		}
    	}

        int tabSize = gridWindow.getTabCount();

        GridTab gridTab = null;
        for (int tab = 0; tab < tabSize; tab++)
        {
            gridTab = initTab(query, tab);
            if (tab == 0 && gridTab == null && m_findCancelled)
            	return false;
        }

        if (gridTab != null)
        	gridTab.getTableModel().setChanged(false);

        adTabbox.setSelectedIndex(0);
        // set again IsSOTrx for window if context for window is clear at AbstractADTab.prepareContext, 
        if (Env.getContext(ctx, curWindowNo, "IsSOTrx", true) == null)
        	Env.setContext(ctx, curWindowNo, "IsSOTrx", gridWindow.isSOTrx());
        toolbar.enableTabNavigation(adTabbox.getTabCount() > 1);
        toolbar.enableFind(true);
        adTabbox.evaluate(null);

        toolbar.updateToolbarAccess();
        updateToolbar();
        if (query == null && toolbar.initDefaultQuery()) {
        	doOnQueryChange();
        }
        
        if (detailQuery != null && zoomToDetailTab(detailQuery))
        {
        	return true;
        }

        SessionManager.getAppDesktop().updateHelpContext(X_AD_CtxHelp.CTXTYPE_Tab, adTabbox.getSelectedGridTab().getAD_Tab_ID());

        return true;
    }

	/**
	 * Zoom to detail tab.<br/>
	 * Delegate to {@link #doZoomToDetail(GridTab, MQuery, int)}.
	 * @param query detail tab query
	 * @return true if zoom is ok
	 */
	private boolean zoomToDetailTab(MQuery query) {
		//zoom to detail
        if (query != null && query.getZoomTableName() != null && query.getZoomColumnName() != null)
    	{
    		GridTab gTab = gridWindow.getTab(0);
    		if (!query.getZoomTableName().equalsIgnoreCase(gTab.getTableName()))
    		{
    			int tabSize = gridWindow.getTabCount();

    	        for (int tab = 0; tab < tabSize; tab++)
    	        {
    	        	gTab = gridWindow.getTab(tab);
    	        	if (gTab.isSortTab())
    	        		continue;
    	        	if (gTab.getTableName().equalsIgnoreCase(query.getZoomTableName()))
    	        	{
    	        		if (doZoomToDetail(gTab, query, tab)) {
	        				return true;
	        			}
    	        	}
    	        }
    		}
    	}
        return false;
	}

	/**
	 * Execute zoom to detail tab.
	 * @param gTab GridTab of tab at tabIndex
	 * @param query detail tab query
	 * @param tabIndex target tab index
	 * @return true if successfully zoom to detail tab
	 */
	private boolean doZoomToDetail(GridTab gTab, MQuery query, int tabIndex) {
		GridField[] fields = gTab.getFields();
		for (GridField field : fields)
		{
			if (field.getColumnName().equalsIgnoreCase(query.getZoomColumnName()))
			{
				gridWindow.initTab(tabIndex);
				//init parent tab by parent ids
				StringBuilder sql = new StringBuilder("SELECT ").append(gTab.getLinkColumnName()).append(" FROM ").append(gTab.getTableName()).append(" WHERE ").append(query.getWhereClause());
				List<List<Object>> parentIds = DB.getSQLArrayObjectsEx(null, sql.toString());
				if (parentIds!=null && parentIds.size() > 0)
				{
					//Tab Index:MQuery
					Map<Integer, MQuery>queryMap = new TreeMap<Integer, MQuery>();

					for (List<Object>parentIdList : parentIds)
					{
						Object parentId = parentIdList.get(0);
						//Tab Index:(ColumnName,Value)
						Map<Integer, Object[]>parentMap = new TreeMap<Integer, Object[]>();
						int index = tabIndex;
						Object oldpid = parentId;
						GridTab currentTab = gTab;
						//walk backward to level 0 tab
						while (index > 0)
						{
							index--;
							GridTab pTab = gridWindow.getTab(index);
							if (pTab.getTabLevel() < currentTab.getTabLevel())
							{
								gridWindow.initTab(index);
								if (index > 0)
								{									
									if (pTab.getLinkColumnName() != null && pTab.getLinkColumnName().trim().length() > 0)
									{
										int pid = DB.getSQLValue(null, "SELECT " + pTab.getLinkColumnName() + " FROM " + pTab.getTableName() + " WHERE " + currentTab.getLinkColumnName() + " = ?", oldpid);
										if (pid > 0)
										{
											//store current link column, parent id and move one level up
											parentMap.put(index, new Object[]{currentTab.getLinkColumnName(), oldpid});
											oldpid = pid;
											currentTab = pTab;
										}
										else
										{
											parentMap.clear();
											break;
										}
									}
								}
								else
								{
									//reach tab 0/top level
									parentMap.put(index, new Object[]{currentTab.getLinkColumnName(), oldpid});
								}
							}
						}
	
						//create query for each parent tab
						for(Map.Entry<Integer, Object[]> entry : parentMap.entrySet())
						{
							GridTab pTab = gridWindow.getTab(entry.getKey());
							Object[] value = entry.getValue();
							MQuery pquery = new MQuery(pTab.getAD_Table_ID());
							queryMap.put(entry.getKey(), pquery);
							pquery.addRestriction((String)value[0], "=", value[1]);
						}
					}

					//execute query for each parent tab
					GridTab pTab = null;
					for (Map.Entry<Integer, MQuery> entry : queryMap.entrySet())
					{
						pTab = gridWindow.getTab(entry.getKey());
						IADTabpanel tp = adTabbox.findADTabpanel(pTab);
        				tp.createUI();
        				if (tp.getTabLevel() == 0)
        				{
        					pTab.setQuery(entry.getValue());
        					tp.query();
        					//update context
        					if (pTab.getRowCount() > 0)
        					{
        						boolean pTabUpdateWindowContext = pTab != null ? pTab.isUpdateWindowContext(): false ;
        						if (pTab != null && !pTabUpdateWindowContext)
        							pTab.setUpdateWindowContext(true);
        						pTab.setCurrentRow(0, false);
        						if (pTab != null && !pTabUpdateWindowContext)
        							pTab.setUpdateWindowContext(false);
        					}
        				}
        				else 
        				{     
        					//retrieve records of sub tab
        					tp.query();
        					//find sub tab row that match the stored query
        					MQuery tabQuery = entry.getValue();
        					int rowFound = -1;
        					for(int i = 0; i < pTab.getRowCount(); i++) 
        					{
        						Object tabValue = pTab.getValue(i, tabQuery.getColumnName(0));
        						if (tabValue != null && tabQuery.getCode(0) != null) 
        						{
        							//handle potential difference in numeric datatype, for e.g integer vs bigdecimal
        							if (tabQuery.getColumnName(0).endsWith("_ID") && tabValue instanceof Number n1 && tabQuery.getCode(0) instanceof Number n2)
        							{
        								if (n1.intValue() == n2.intValue())
        								{
        									rowFound = i;
            								break;
        								}
        							}
        							else if (tabValue.equals(tabQuery.getCode(0)))
        							{
        								rowFound = i;
        								break;
        							}
        						}
        					}        					
        					if (rowFound == -1)
        					{
        						//fall back to execution of stored query
        						pTab.setQuery(entry.getValue());
        						tp.query();
        						rowFound = 0;
        					}
        					//update context
        					if (pTab.getRowCount() > 0) {
        						boolean pTabUpdateWindowContext = pTab != null ? pTab.isUpdateWindowContext(): false ;
        						if (pTab != null && !pTabUpdateWindowContext)
        							pTab.setUpdateWindowContext(true);
        						pTab.setCurrentRow(rowFound, false);
        						if (pTab != null && !pTabUpdateWindowContext)
        							pTab.setUpdateWindowContext(false);
        					}
        				}
					}

					//execute query for detail tab
					IADTabpanel gc = null;
					gc = adTabbox.findADTabpanel(gTab);
					gc.createUI();
					gc.query(false, 0, gTab.getMaxQueryRecords());

					int zoomColumnIndex = -1;
					GridTable table = gTab.getTableModel();
					for (int i = 0; i < table.getColumnCount(); i++)
					{
						if (table.getColumnName(i).equalsIgnoreCase(query.getZoomColumnName()))
						{
							zoomColumnIndex = i;
							break;
						}
					}
					
					//set target detail tab as active tab and navigate to target record
    				int count = table.getRowCount();
    				MTable tbl = MTable.get(ctx, table.getTableName());
    				for(int i = 0; i < count; i++)
    				{
    					Object id = null;
    					if (zoomColumnIndex >= 0) 
    					{
    						Object zoomValue = table.getValueAt(i, zoomColumnIndex);
    						if (zoomValue != null && zoomValue instanceof Number)
    						{
    							id = ((Number)zoomValue).intValue();
    						}
    						else if (zoomValue != null && zoomValue instanceof String)
    						{
    							id = zoomValue.toString();
    						}
    					}
    					else
    					{
    						if (tbl.isUUIDKeyTable())
    							id = table.getUUID(i);
    						else
    							id = table.getKeyID(i);
    					}
    					if (id != null && id.equals(query.getZoomValue()))
    					{
    						//make sure last parent tab will update window context
    						boolean pTabUpdateWindowContext = pTab != null ? pTab.isUpdateWindowContext(): false ;
    						if (pTab != null && !pTabUpdateWindowContext)
    							pTab.setUpdateWindowContext(true);
    						setActiveTab(gridWindow.getTabIndex(gTab), null);
    						if (pTab != null && !pTabUpdateWindowContext)
    							pTab.setUpdateWindowContext(false);
    						gTab.navigate(i);
    						return true;
    					}
    				}
				}
			}
		}
		return false;
	}

	/**
	 * Schedule onNew event if needed
	 * @param result query result
	 */
	private void initQueryOnNew(MQuery result) {
		GridTab curTab = adTabbox.getSelectedGridTab();
		boolean onNew = false;
		if (curTab.isHighVolume() && m_findCreateNew)
			onNew = true;
		else if (result == null && curTab.getRowCount() == 0 && Env.isAutoNew(ctx, curWindowNo))
			onNew = true;
		else if (!curTab.isReadOnly() && curTab.isQueryNewRecord())
			onNew = true;
		if (onNew) {
			Executions.schedule(AEnv.getDesktop(), new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					onNew();
					ADTabpanel adtabpanel = (ADTabpanel) getADTab().getSelectedTabpanel();
					adtabpanel.focusToFirstEditor(false);
				}
			}, new Event("onInsert"));
		}
	}

	/**
	 * Init tab at tabIndex.<br/>
	 * Further delegate to {@link #initTabPanel(MQuery, int, GridTab, IADTabpanel)}.
	 * @param query
	 * @param tabIndex
	 * @return {@link GridTab}
	 */
	protected GridTab initTab(MQuery query, int tabIndex) {
		gridWindow.initTab(tabIndex);

		final GridTab gTab = gridWindow.getTab(tabIndex);
		Env.setContext(ctx, curWindowNo, tabIndex, GridTab.CTX_TabLevel, Integer.toString(gTab.getTabLevel()));

		AtomicBoolean zoomQuery = new AtomicBoolean(false);
		// Query first tab
		if (tabIndex == 0)
		{
			gTab.setUpdateWindowContext(true);
			m_queryInitiating = true;
			getComponent().setVisible(false);
		    initialQuery(query, gTab, new Callback<MQuery>() {
				@Override
				public void onCallback(MQuery result) {
					m_queryInitiating = false;

					if (m_findCancelled) {
						SessionManager.getAppDesktop().closeWindow(curWindowNo);
				    	return;
					}

					if (!getComponent().isVisible())
						getComponent().setVisible(true);

					// Set query from find window on first tab
				    if (result != null)
				    {
				        m_onlyCurrentRows = false;
				        gTab.setQuery(result);
				    }

				    if (adTabbox.getSelectedTabpanel() != null)
				    {
					    initFirstTabpanel();

					    initQueryOnNew(result);
				    }
					if (query != null && query == result)
						zoomQuery.set(true);
				}

			});
		}
		else
		{
			gTab.setUpdateWindowContext(false);
		}

		String type = gTab.getTabType();
		if (!Util.isEmpty(type))
		{
			IADTabpanel adTabPanal = Extensions.getADTabPanel(type);
			if (adTabPanal != null)
			{
				initTabPanel(query, tabIndex, gTab, adTabPanal);
			}
			else
			{
				logger.log(Level.SEVERE, "No implementation for tab type " + type + " Found", new Exception("No implementation for tab type " + type + " Found"));
			}
		}
		else if (gTab.isSortTab())
		{
			ADSortTab sortTab = new ADSortTab();
			sortTab.init(this, gTab);
			adTabbox.addTab(gTab, sortTab);
			sortTab.registerAPanel(this);
			if (tabIndex == 0) {
				sortTab.createUI();
				if (!m_queryInitiating)
				{
					initFirstTabpanel();
				}
			}
			gTab.addDataStatusListener(this);
		}
		else
		{
			//fallback to ADTabpanel
			ADTabpanel fTabPanel = new ADTabpanel();
			initTabPanel(query, tabIndex, gTab, fTabPanel);
			// force single row mode for zoom query that return 1 record
			if (query != null && zoomQuery.get())
			{
				if (gTab.getRowCount() == 1 && !gTab.isNew() && adTabbox.getSelectedTabpanel().isGridView()
						&& adTabbox.getSelectedTabpanel().getGridTab() == gTab)
				{
					adTabbox.getSelectedTabpanel().switchRowPresentation();
				}
			}
		}

		return gTab;
	} // initTab

	/**
	 * Initialize ADTabpanel
	 * @param query
	 * @param tabIndex
	 * @param gTab
	 * @param adTabPanal
	 */
	private void initTabPanel(MQuery query, int tabIndex, final GridTab gTab, IADTabpanel adTabPanal)
	{
		adTabPanal.addEventListener(ADTabpanel.ON_DYNAMIC_DISPLAY_EVENT, this);
		gTab.addDataStatusListener(this);
		adTabPanal.init(this, gTab);
		adTabbox.addTab(gTab, adTabPanal);
		if (tabIndex == 0)
		{
			adTabPanal.createUI();
			if (!m_queryInitiating)
			{
		    		try {
						initFirstTabpanel();
		    		} catch (Exception e) {
		        		if (DBException.isTimeout(e)) {
		        			Dialog.error(curWindowNo, GridTable.LOAD_TIMEOUT_ERROR_MESSAGE);
		        		} else {
		        			Dialog.error(curWindowNo, "Error", e.getLocalizedMessage());
		        		}
		    		}
			}
		}

		if (!m_queryInitiating && tabIndex == 0)
		{
			initQueryOnNew(query);
		}
	} // initTabPanel

	/**
	 * Execute query and activate first tab panel
	 */
	private void initFirstTabpanel() {
		adTabbox.getSelectedTabpanel().query(m_onlyCurrentRows, m_onlyCurrentDays, adTabbox.getSelectedGridTab().getMaxQueryRecords());
		adTabbox.getSelectedTabpanel().activate(true);
		Events.echoEvent(new Event(ADTabpanel.ON_POST_INIT_EVENT, adTabbox.getSelectedTabpanel()));
	}

    /**
     * Show initial find window for first tab (if necessary)
     *
     * @param query initial query
     * @param mTab tab
     * @param callback callback for query to apply, must not be null
     * @return query or null
     */
    private void initialQuery(final MQuery query, GridTab mTab, final Callback<MQuery> callback)
    {
        // We have a (Zoom) query
        if (query != null && query.isActive())
        {
    		callback.onCallback(query);
    		return;
        }

        //
		StringBuffer where = new StringBuffer(Env.parseContext(ctx, curWindowNo, mTab.getWhereExtended(), false));
        // Query automatically if high volume and no query
        boolean require = mTab.isHighVolume();
        if (!require && !m_onlyCurrentRows) // No Trx Window
        {
            if (query != null)
            {
                String wh2 = query.getWhereClause();
                if (wh2.length() > 0)
                {
                    if (where.length() > 0)
                        where.append(" AND ");
                    where.append(wh2);
                }
            }
            //
            int no = getRecordCount(mTab, where);
            // show find dialog if count timeout/exception
            require = no == -1 ? true : mTab.isQueryRequire(no);
        }
        
        // Show find window (if required)
        if (require)
        {
        	m_findCancelled = false;
        	m_findCreateNew = false;
            GridField[] findFields = mTab.getFields();
            
            FindWindow findWindow = Extensions.getFindWindow(curWindowNo, 0, title, mTab.getAD_Table_ID(), mTab.getTableName(), where.toString(), findFields, 10, mTab.getAD_Tab_ID(), this);
            
           	tabFindWindowHashMap.put(mTab, findWindow);
            setupEmbeddedFindwindow(findWindow);
            if (findWindow.initialize())
            {
	        	findWindow.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						if (!findWindow.isCancel())
			            {
			            	m_findCreateNew = findWindow.isCreateNew();
			            	MQuery result = findWindow.getQuery();
			            	callback.onCallback(result);
			            	EventListener<? extends Event> listener = findWindow.getEventListeners(DialogEvents.ON_WINDOW_CLOSE).iterator().next();
			            	findWindow.removeEventListener(DialogEvents.ON_WINDOW_CLOSE, listener);
			            	adTabbox.getSelectedTabpanel().onAfterFind();
			            }
			            else
			            {
			            	m_findCancelled = true;
			            	callback.onCallback(null);
			            }
					}
				});	        	
	        	getComponent().addEventListener("onInitialQuery", new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						if (findWindow.getParent() != getComponent().getParent())
							getComponent().getParent().appendChild(findWindow);
						LayoutUtils.openEmbeddedWindow(getComponent().getParent(), findWindow, "overlap");						
					}
				});
	        	Events.echoEvent("onInitialQuery", getComponent(), null);
            }
            else
            {
            	callback.onCallback(query);
            }
        }
        else
        {
        	callback.onCallback(query);
        }
    } // initialQuery

    /**
     * Get record count
     * @param mTab
     * @param where
     * @return record count
     */
	private int getRecordCount(GridTab mTab, StringBuffer where) {
		StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ")
		        .append(mTab.getTableName());
		if (where.length() > 0)
		    sql.append(" WHERE ").append(where);
		String finalSQL = MRole.getDefault().addAccessSQL(sql.toString(),
				mTab.getTableName(), MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);
		int no = -1;
        int timeout = MSysConfig.getIntValue(MSysConfig.GRIDTABLE_INITIAL_COUNT_TIMEOUT_IN_SECONDS, 
        		GridTable.DEFAULT_GRIDTABLE_COUNT_TIMEOUT_IN_SECONDS, Env.getAD_Client_ID(Env.getCtx()));
		try (PreparedStatement stmt = DB.prepareStatement(finalSQL, null)) {
			if (timeout > 0)
				stmt.setQueryTimeout(timeout);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				no = rs.getInt(1);
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.getMessage() + " -> " + finalSQL, e);
			no = -1;
		}
		return no;
	}	

    /**
     * Setup find window UI properties
     * @param findWindow
     */
	private void setupEmbeddedFindwindow(FindWindow findWindow) {
		findWindow.setTitle(null);
		findWindow.setBorder("none");	
		findWindow.setStyle("position: absolute;background-color: #fff;");
		ZKUpdateUtil.setWidth(findWindow, "100%");
		if (ClientInfo.maxHeight(ClientInfo.MEDIUM_HEIGHT-1))
			ZKUpdateUtil.setHeight(findWindow, "100%");
		else
			ZKUpdateUtil.setHeight(findWindow, "60%");
		findWindow.setZindex(1000);
		findWindow.setSizable(false);
		findWindow.setContentStyle("background-color: #fff; width: 99%; margin: auto;");
	}

	/**
	 * Get title of window
	 * @return title of window
	 */
    public String getTitle()
    {
        return title;
    }
    
    /**
     * Get image of window title
     * @return {@link MImage} of window title
     */
    public MImage getImage()
    {
    	return image;
    }

    /**
     * @see ToolbarListener#onDetailRecord()
     */
    @Override
    public void onDetailRecord()
    {
    	adTabbox.onDetailRecord();
    }

	/**
	 * Navigate to parent record.<br/>
	 * Delegate to {@link BreadCrumbLink} ON_Click event.
     * @see ToolbarListener#onParentRecord()
     */
    @Override
    public void onParentRecord()
    {
    	List<BreadCrumbLink> parents = breadCrumb.getParentLinks();
    	if (!parents.isEmpty()) {
    		Events.sendEvent(parents.get(parents.size()-1), new Event(Events.ON_CLICK, parents.get(parents.size()-1)));
    	}
    }

    /**
     * @see ToolbarListener#onFirst()
     */
    @Override
    public void onFirst()
    {
    	Callback<Boolean> callback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					adTabbox.getSelectedGridTab().navigate(-1); // not zero because of IDEMPIERE-3736 
			        focusToActivePanel();
				}
			}
		};
		saveAndNavigate(callback);
    }

    /**
     * @see ToolbarListener#onLast()
     */
    @Override
    public void onLast()
    {
        Callback<Boolean> callback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					adTabbox.getSelectedGridTab().navigate(adTabbox.getSelectedGridTab().getRowCount() - 1);
			        focusToActivePanel();
				}
			}
		};
		onSave(false, true, callback);
    }

    /**
     * @see ToolbarListener#onNext()
     */
    @Override
    public void onNext()
    {
        Callback<Boolean> callback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					adTabbox.getSelectedGridTab().navigateRelative(+1);
					focusToActivePanel();
				}
			}
		};
		saveAndNavigate(callback);
    }

    /**
     * @see ToolbarListener#onPrevious()
     */
    @Override
    public void onPrevious()
    {
        Callback<Boolean> callback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					adTabbox.getSelectedGridTab().navigateRelative(-1);
			        focusToActivePanel();
				}
			}
		};
		saveAndNavigate(callback);
    }

    /**
     * Handle tree navigation
     * @param gt
     * @param rowIndex
     */
    public void onTreeNavigate(final GridTab gt, final int rowIndex)
    {
    	Callback<Boolean> callback = new Callback<Boolean>() {
    		@Override
    		public void onCallback(Boolean result) {
    			if (result) {
    				gt.navigate(rowIndex);
    			}
    		}
    	};
    	saveAndNavigate(callback);
    }

	private Menupopup 	m_popup = null;
	private Menuitem 	m_lock = null;
	private Menuitem 	m_access = null;

	private HashMap<GridTab, FindWindow> tabFindWindowHashMap = new HashMap<GridTab, FindWindow>();
	
	/**
	 * Record id of tab 0.
	 * Updated in {@link #dataStatusChanged(DataStatusEvent)}
	 */
	private int masterRecord = -1;

	private Div mask;

	protected ADWindow adwindow;

	/** true if confirmation for exit dialog is visible */
	protected boolean showingOnExitDialog;

	/**
	 *	@see ToolbarListener#onLock()
	 */
	@Override
	public void onLock()
	{
		if (!toolbar.isPersonalLock)
			return;
		if (adTabbox.getSelectedGridTab().getRecord_ID() == -1)	//	No Key
			return;

		if(m_popup == null)
		{
			m_popup = new Menupopup();

			m_lock = new Menuitem(Msg.translate(Env.getCtx(), "Lock"));
			m_popup.appendChild(m_lock);
			m_lock.addEventListener(Events.ON_CLICK, new EventListener<Event>()
			{
				public void onEvent(Event event) throws Exception
				{
					adTabbox.getSelectedGridTab().lock(Env.getCtx(), adTabbox.getSelectedGridTab().getRecord_ID(), !toolbar.getButton("Lock").isPressed());
					adTabbox.getSelectedGridTab().loadLocks();			//	reload

					toolbar.lock(adTabbox.getSelectedGridTab().isLocked());
				}
			});

			m_access = new Menuitem(Msg.translate(Env.getCtx(), "RecordAccessDialog"));
			m_popup.appendChild(m_access);
			m_access.addEventListener(Events.ON_CLICK, new EventListener<Event>()
			{
				public void onEvent(Event event) throws Exception
				{
					WRecordAccessDialog recordAccessDialog = new WRecordAccessDialog(null, adTabbox.getSelectedGridTab().getAD_Table_ID(), adTabbox.getSelectedGridTab().getRecord_ID());
					recordAccessDialog.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							toolbar.lock(adTabbox.getSelectedGridTab().isLocked());
							focusToLastFocusEditor();
						}
					});

					AEnv.showWindow(recordAccessDialog);
				}
			});
			LayoutUtils.autoDetachOnClose(m_popup);
		}
		if (m_popup.getPage() == null) {
			LabelImageElement btn = toolbar.getToolbarItem("Lock");
			Popup popup = LayoutUtils.findPopup(btn.getParent());
			if (popup != null) {
				popup.appendChild(m_popup);				
			} else {
				m_popup.setPage(toolbar.getToolbarItem("Lock").getPage());
			}
		}
		m_popup.open(toolbar.getToolbarItem("Lock"), "after_start");		
	}

    /**
     * @see ToolbarListener#onAttachment()
     */
	@Override
    public void onAttachment()
    {
		int record_ID = adTabbox.getSelectedGridTab().getRecord_ID();
    	String recordUU = adTabbox.getSelectedGridTab().getRecord_UU();
		if (logger.isLoggable(Level.INFO)) logger.info("Record_ID=" + record_ID + ", Record_UU=" + recordUU);

		if (record_ID== -1 && Util.isEmpty(recordUU))	//	No Key
		{
			return;
		}

		EventListener<Event> listener = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				toolbar.setPressed("Attachment",adTabbox.getSelectedGridTab().hasAttachment());
				focusToLastFocusEditor();
			}
		};
		//	Attachment va =
		WAttachment win = new WAttachment (	curWindowNo, adTabbox.getSelectedGridTab().getAD_AttachmentID(),
							adTabbox.getSelectedGridTab().getAD_Table_ID(), record_ID, recordUU, null, listener);		
		win.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				hideBusyMask();
			}
		});
		getComponent().getParent().appendChild(win);
		showBusyMask(win);		
		LayoutUtils.openOverlappedWindow(getComponent(), win, "middle_center");
		win.focus();
	}

    @Override
    public void onChat()
    {
    	int recordId = adTabbox.getSelectedGridTab().getRecord_ID();
    	String recordUU = adTabbox.getSelectedGridTab().getRecord_UU();
    	if (logger.isLoggable(Level.INFO)) logger.info("Record_ID=" + recordId + ", Record_UU=" + recordUU);

		if (recordId== -1 && Util.isEmpty(recordUU))	//	No Key
		{
			return;
		}

		//	Find display
		String infoName = null;
		String infoDisplay = null;
		for (int i = 0; i < adTabbox.getSelectedGridTab().getFieldCount(); i++)
		{
			GridField field = adTabbox.getSelectedGridTab().getField(i);
			if (field.isKey())
				infoName = field.getHeader();
			if ((field.getColumnName().equals("Name") || field.getColumnName().equals("DocumentNo") )
				&& field.getValue() != null)
				infoDisplay = field.getValue().toString();
			if (infoName != null && infoDisplay != null)
				break;
		}
    	if (infoName == null)
    		infoName = adTabbox.getSelectedGridTab().getName();
    	if (infoDisplay == null)
    		infoDisplay = "";
		String description = infoName + ": " + infoDisplay;

    	WChat chat = new WChat(curWindowNo, adTabbox.getSelectedGridTab().getCM_ChatID(), adTabbox.getSelectedGridTab().getAD_Table_ID(),
    			recordId, recordUU, description, null);
    	chat.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				hideBusyMask();
				toolbar.setPressed("Chat",adTabbox.getSelectedGridTab().hasChat());
				focusToLastFocusEditor();				
			}
		});
    	getComponent().getParent().appendChild(chat);
    	showBusyMask(chat);    	    	
    	LayoutUtils.openOverlappedWindow(getComponent(), chat, "middle_center");
    	chat.showWindow();
    }

    /**
     * Open Post It note dialog.
     */
    public void onPostIt()
    {
    	int recordId = adTabbox.getSelectedGridTab().getRecord_ID();
    	String recordUU = adTabbox.getSelectedGridTab().getRecord_UU();
		if (recordId== -1 && Util.isEmpty(recordUU))	//	No Key
    	{
    		return;
    	}

    	//	Find display
    	String infoName = null;
    	String infoDisplay = null;
    	for (int i = 0; i < adTabbox.getSelectedGridTab().getFieldCount(); i++)
    	{
    		GridField field = adTabbox.getSelectedGridTab().getField(i);
    		if (field.isKey())
    			infoName = field.getHeader();
    		if ((field.getColumnName().equals("Name") || field.getColumnName().equals("DocumentNo") )
    				&& field.getValue() != null)
    			infoDisplay = field.getValue().toString();
    		if (infoName != null && infoDisplay != null)
    			break;
    	}
    	if (infoName == null)
    		infoName = adTabbox.getSelectedGridTab().getName();
    	if (infoDisplay == null)
    		infoDisplay = "";
    	String header = infoName + ": " + infoDisplay;

    	WPostIt postit = new WPostIt(header, adTabbox.getSelectedGridTab().getAD_PostIt_ID(), adTabbox.getSelectedGridTab().getAD_Table_ID(), recordId, recordUU, null);
    	postit.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
    		@Override
    		public void onEvent(Event event) throws Exception {
    			hideBusyMask();
    			toolbar.setPressed("PostIt",adTabbox.getSelectedGridTab().hasPostIt());
    			focusToLastFocusEditor();
    		}
    	});
    	getComponent().getParent().appendChild(postit);
    	showBusyMask(postit);    	    	
    	LayoutUtils.openOverlappedWindow(getComponent(), postit, "middle_center");
    	postit.showWindow();
    }
    
    /**
     * Open Label panel.
     * Delegate to {@link LabelAction}
     */
	public void onLabel() {
		toolbar.setPressed("Label",false);
		LabelAction labelAction = new LabelAction(this);
		labelAction.show();
	}
    
    /**
     * @see ToolbarListener#onToggle()
     */
	@Override
    public void onToggle()
    {
    	adTabbox.getSelectedTabpanel().switchRowPresentation();
    	toolbar.enableCustomize(adTabbox.getSelectedTabpanel().isEnableCustomizeButton());
    	focusToActivePanel();
    }

	/**
	 * handle on exit/close of window
     * @param callback
     */
    public synchronized void onExit(Callback<Boolean> callback)
    {
    	if (isPendingChanges())
    	{
    		showingOnExitDialog = true;
    		Dialog.ask(curWindowNo, "CloseUnSave?", b -> {
    			showingOnExitDialog = false;
    			callback.onCallback(b);
    			if (!b)
    			{
    				//restore focus
    				focusToLastFocusEditor();
    			}
    		});
    	}
    	else
    	{
    		callback.onCallback(Boolean.TRUE);
    	}
    	
    }

    /**
     * restore focus to last known focus editor (if any)
     * @return true if there's last focus editor
     */
	public boolean focusToLastFocusEditor() {
		if (ClientInfo.isMobile())
			return false;

		return focusToLastFocusEditor(false);
	}
	
    /**
     * restore focus to last known focus editor (if any)
     * @param defer true to schedule for later/defer execution
     * @return true if there's last focus editor
     */
	public boolean focusToLastFocusEditor(boolean defer) {
		if (lastFocusEditor != null && lastFocusEditor instanceof HtmlBasedComponent && 
			lastFocusEditor.getPage() != null && LayoutUtils.isReallyVisible(lastFocusEditor)) {
			if (defer) {
				final HtmlBasedComponent editor = (HtmlBasedComponent) lastFocusEditor;
				Executions.schedule(getComponent().getDesktop(), e -> editor.focus(), new Event("onScheduleFocusToLastFocusEditor"));
			} else {
				((HtmlBasedComponent)lastFocusEditor).focus();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Open quick form
	 */
	public void onQuickForm() {
		onQuickForm(false);
	}

	/**
	 * Open quick form
	 * @param stayInParent true to goes back to parent record after quick form have been closed
	 */
	public void onQuickForm(boolean stayInParent)
	{
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Invoke Quick Form");
		// Prevent to open Quick Form if already opened.
		if (!this.registerQuickFormTab(getADTab().getSelectedGridTab().getAD_Tab_ID()))
		{
			if (logger.isLoggable(Level.FINE))
				logger.fine("TabID=" + getActiveGridTab().getAD_Tab_ID() + "  is already open.");
			return;
		}
		int table_ID = adTabbox.getSelectedGridTab().getAD_Table_ID();
		if (table_ID == -1)
			return;

		statusBarQF = new StatusBar();
		// Remove Key-listener of parent Quick Form
		int tabLevel = getToolbar().getQuickFormTabHrchyLevel();
		if (tabLevel > 0 && getCurrQGV() != null)
		{
			SessionManager.getSessionApplication().getKeylistener().removeEventListener(Events.ON_CTRL_KEY, getCurrQGV());
		}

		WQuickForm form = new WQuickForm(this, m_onlyCurrentRows, m_onlyCurrentDays);
		form.setTitle(this.getADTab().getSelectedGridTab().getName());
		form.setVisible(true);
		form.setSizable(true);
		form.setMaximizable(true);
		form.setMaximized(true);
		form.setPosition("center");
		form.setStayInParent(stayInParent);
		ZKUpdateUtil.setWindowHeightX(form, 550);
		ZKUpdateUtil.setWindowWidthX(form, 900);
		ZkCssHelper.appendStyle(form, "z-index: 900;");

		AEnv.showWindow(form);
	} // onQuickForm

	/**
	 * Open Table Attribute Window
	 */
	public void onAttributeForm()
	{
		new WTableAttribute(adTabbox.getSelectedGridTab().getAD_Table_ID(), adTabbox.getSelectedGridTab().getRecord_ID());
		
	}
	
    /**
     * @param event
     * @see EventListener#onEvent(Event)
     */
	@Override
    public void onEvent(Event event)
    {
    	if (CompositeADTabbox.ON_SELECTION_CHANGED_EVENT.equals(event.getName()))
    	{
    		Object eventData = event.getData();

	        if (eventData != null && eventData instanceof Object[] && ((Object[])eventData).length == 2)
	        {
	        	Object[] indexes = (Object[]) eventData;
	        	final int newTabIndex = (Integer)indexes[1];

	        	final int originalTabIndex = adTabbox.getSelectedIndex();
	        	final int originalTabRow = adTabbox.getSelectedGridTab().getCurrentRow();
	            setActiveTab(newTabIndex, new Callback<Boolean>() {

					@Override
					public void onCallback(Boolean result) {
						if (result)
						{
							if (newTabIndex < originalTabIndex)
							{
								if (adTabbox.isDetailPaneLoaded())
									adTabbox.setDetailPaneSelectedTab(originalTabIndex, originalTabRow);
								else {
									Events.echoEvent(new Event(ON_DEFER_SET_DETAILPANE_SELECTION_EVENT, getComponent(), new Integer[]{originalTabIndex, originalTabRow}));
								}
							}
			            }
			            else
			            {
			            	//reset to original
			            	adTabbox.setSelectedIndex(originalTabIndex);
			            }

					}
				});
	            
	            SessionManager.getAppDesktop().updateHelpContext(X_AD_CtxHelp.CTXTYPE_Tab, adTabbox.getSelectedGridTab().getAD_Tab_ID());
	        }
    	}
    	else if (event.getTarget() instanceof ProcessModalDialog)
    	{
    		if (!DialogEvents.ON_WINDOW_CLOSE.equals(event.getName())){
    			return;
    		}

    		hideBusyMask();
    		ProcessModalDialog dialog = (ProcessModalDialog) event.getTarget();
    		ProcessInfo pi = dialog.getProcessInfo();

    		onModalClose(pi);

			String s = null;
			boolean b = false;
			ProcessInfoLog[] logs = null;
			if (getActiveGridTab().isQuickForm)
			{
				s = statusBarQF.getStatusLine();
				b = statusBarQF.getStatusError();
				logs = statusBarQF.getPLogs();
			}
			else
			{
				s = statusBar.getStatusLine();
				b = statusBar.getStatusError();
				logs = statusBar.getPLogs();
			}

			MPInstance instance = new MPInstance(ctx, pi.getAD_PInstance_ID(), "false");
			if (!instance.isRunAsJob() && !dialog.isCancel()){
				// when run as job or canceled, don't expect see its effect when close parameter panel, so don't refresh
				onRefresh(true, false);
			}

			if (getActiveGridTab().isQuickForm)
			{
				statusBarQF.setStatusLine(s, b, logs);
			}
			else
			{
				statusBar.setStatusLine(s, b, logs);
			}
						
			if (dialog.getAttribute(PROCESS_POST_CALLBACK_ATTRIBUTE) != null && dialog.getAttribute(PROCESS_POST_CALLBACK_ATTRIBUTE) instanceof Callback<?>)
			{
				@SuppressWarnings("unchecked")
				Callback<Boolean> callback = (Callback<Boolean>) dialog.getAttribute(PROCESS_POST_CALLBACK_ATTRIBUTE);
	    		callback.onCallback(dialog.getProcessInfo() != null && !dialog.getProcessInfo().isError());
			}
    	}
    	else if (ADTabpanel.ON_DYNAMIC_DISPLAY_EVENT.equals(event.getName()))
    	{
    		IADTabpanel adtab = (IADTabpanel) event.getTarget();
    		if (adtab == adTabbox.getSelectedTabpanel()) {
    			toolbar.enableProcessButton(adtab.isEnableProcessButton());
    			toolbar.dynamicDisplay();
    		}
    	}
    	else if (event.getTarget() == getComponent() && event.getName().equals(LayoutUtils.ON_REDRAW_EVENT)) {
    		ExecutionCtrl ctrl = (ExecutionCtrl) Executions.getCurrent();
    		Event evt = ctrl.getNextEvent();
    		if (evt != null) {
    			Events.sendEvent(evt);
    			Events.postEvent(new Event(LayoutUtils.ON_REDRAW_EVENT, getComponent()));
    			return;
    		}
    		LayoutUtils.redraw((AbstractComponent) getComponent());
    	}
    	else if (event.getName().equals(ON_DEFER_SET_DETAILPANE_SELECTION_EVENT)) {
    		Integer[] data = (Integer[]) event.getData();
    		adTabbox.setDetailPaneSelectedTab(data[0], data[1]);
    	}
    	else if (event.getName().equals(ON_FOCUS_DEFER_EVENT) && !ClientInfo.isMobile()) {
    		HtmlBasedComponent comp = (HtmlBasedComponent) event.getData();
    		if (comp instanceof ADTabpanel)
    			((ADTabpanel)comp).focusToFirstEditor(false);
    		else
    			comp.focus();
    		// 
    	}    		
    }

	/**
	 * Change active tab to newTabIndex.<br/>
	 * Delegate to {@link #setActiveTab0(int, int, Callback)}
	 * @param newTabIndex
	 * @param callback optional callback
	 */
	private void setActiveTab(final int newTabIndex, final Callback<Boolean> callback) {

		final int oldTabIndex = adTabbox.getSelectedIndex();

		if (oldTabIndex == newTabIndex)
		{
			if (callback != null)
				callback.onCallback(true);
		}
		else
		{
			Callback<Boolean> command = new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result) {
					if (result) {
						setActiveTab0(oldTabIndex, newTabIndex, callback);
					} else if (callback != null) {
						callback.onCallback(false);
					}
				}
			};
			Object value = Executions.getCurrent().getAttribute(CompositeADTabbox.AD_TABBOX_ON_EDIT_DETAIL_ATTRIBUTE);
			if (value != null && value == adTabbox.getSelectedDetailADTabpanel()
				&& (adTabbox.getDirtyADTabpanel() == adTabbox.getSelectedDetailADTabpanel() 
				    || (adTabbox.getDirtyADTabpanel() == null 
				        && adTabbox.getSelectedDetailADTabpanel().getGridTab().isNew()))) {
				command.onCallback(true);
			} else {
				saveAndNavigate(command);
			}
		}

	}

	/**
	 * Save (if needed) and execute callback
	 * @param callback callback for result of execution, must not be null
	 */
	public void saveAndNavigate(final Callback<Boolean> callback) {
		if (adTabbox != null)
		{
			boolean newrecod = adTabbox.getSelectedGridTab().isNew();
			if (adTabbox.isSortTab())
			{
				onSave(false, true, callback);
			}
			else if (adTabbox.needSave(true, false))
		    {
		    	if (adTabbox.needSave(true, true))
				{
		    		onSave(false, true, callback);
				}
				else
				{
					//  new record, but nothing changed
					adTabbox.dataIgnore();
			        if (newrecod)
			        	onRefresh(true, false);
					callback.onCallback(true);
				}
			}   //  there is a change
			else {
				// just in case
				adTabbox.dataIgnore();
		        if (newrecod)
		        	onRefresh(true, false);
				callback.onCallback(true);
			}
		}
		else
			callback.onCallback(true);
	}

	/**
	 * Change selected tab from oldTabIndex to newTabIndex
	 * @param oldTabIndex
	 * @param newTabIndex
	 * @param callback optional callback for result of execution
	 */
	private void setActiveTab0(int oldTabIndex, int newTabIndex,
			final Callback<Boolean> callback) {
		boolean back = false;
		IADTabpanel oldTabpanel = adTabbox.getSelectedTabpanel();

		if (!adTabbox.updateSelectedIndex(oldTabIndex, newTabIndex))
		{
		    Dialog.warn(curWindowNo, "TabSwitchJumpGo", title);
		    if (callback != null)
				callback.onCallback(false);
		    return;
		}

		IADTabpanel newTabpanel = adTabbox.getSelectedTabpanel();
		
		//toggle window context update
		if (newTabpanel.getGridTab() != null)
			newTabpanel.getGridTab().setUpdateWindowContext(true);
		if (oldTabIndex > newTabIndex && oldTabpanel.getGridTab() != null)
			oldTabpanel.getGridTab().setUpdateWindowContext(false);

		boolean activated = newTabpanel.isActivated();
		if (oldTabpanel != null)
			oldTabpanel.activate(false);
		if (!activated)
			newTabpanel.activate(true);

		back = (newTabIndex < oldTabIndex);
		if (back && newTabpanel.getTabLevel() > 0)
		{
			if (newTabpanel.getTabLevel() >= oldTabpanel.getTabLevel())
				back = false;
			else if ((newTabIndex - oldTabIndex) > 1)
			{
				for (int i = oldTabIndex - 1; i > newTabIndex; i--)
				{
					IADTabpanel next = adTabbox.getADTabpanel(i);
					if (next != null && next.getTabLevel() <= newTabpanel.getTabLevel())
					{
						back = false;
						break;
					}
				}
			}
		}

		if (!back)
		{
			Object value = Executions.getCurrent().removeAttribute(CompositeADTabbox.AD_TABBOX_ON_EDIT_DETAIL_ATTRIBUTE);
			if (value != newTabpanel)
			{
				newTabpanel.query();
				if (newTabpanel instanceof ADTabpanel)
				{
					ADTabpanel adtabpanel = (ADTabpanel) newTabpanel;
					Events.echoEvent(ADTabpanel.ON_POST_INIT_EVENT, adtabpanel, null);
				}			
			}
			else 
			{
				//detail pane of the new header tab might need refresh
				if (newTabpanel instanceof ADTabpanel)
				{
					ADTabpanel adtabpanel = (ADTabpanel) newTabpanel;
					Events.echoEvent(ADTabpanel.ON_POST_INIT_EVENT, adtabpanel, null);
				}
			}				
		}
		else
		{
		    newTabpanel.refresh();
		}

		if (adTabbox.getSelectedTabpanel() instanceof ADSortTab)
		{
			((ADSortTab)adTabbox.getSelectedTabpanel()).registerAPanel(this);
		}
		else
		{
			if (adTabbox.getSelectedGridTab().getRowCount() == 0 && Env.isAutoNew(ctx, getWindowNo()))
			{
				onNew();
			}
		}

		updateToolbar();

		breadCrumb.setNavigationToolbarVisibility(!adTabbox.getSelectedGridTab().isSortTab());

		if (callback != null)
			callback.onCallback(true);
	}
	
	/**
	 * Update toolbar buttons state
	 */
	private void updateToolbar()
	{
		toolbar.enableTabNavigation(breadCrumb.hasParentLink(), adTabbox.getSelectedDetailADTabpanel() != null);

		toolbar.setPressed("Attachment",adTabbox.getSelectedGridTab().hasAttachment());
		toolbar.setPressed("PostIt",adTabbox.getSelectedGridTab().hasPostIt());
		toolbar.setPressed("Chat",adTabbox.getSelectedGridTab().hasChat());

		if (toolbar.isPersonalLock)
		{
			toolbar.lock(adTabbox.getSelectedGridTab().isLocked());
		}

		toolbar.enablePrint(adTabbox.getSelectedGridTab().isPrinted() && !adTabbox.getSelectedGridTab().isNew());

		toolbar.enableQuickForm(adTabbox.getSelectedTabpanel().isEnableQuickFormButton() && !adTabbox.getSelectedGridTab().isReadOnly());

		toolbar.enableAttributeForm(MTableAttributeSet.hasTableAttributeSet(adTabbox.getSelectedGridTab().getAD_Table_ID()));
		
		boolean isNewRow = adTabbox.getSelectedGridTab().getRowCount() == 0 || adTabbox.getSelectedGridTab().isNew();
		IADTabpanel adtab = adTabbox.getSelectedTabpanel();
        toolbar.enableProcessButton(adtab != null && adtab.isEnableProcessButton());
        toolbar.enableCustomize(adtab.isEnableCustomizeButton());
        
		toolbar.setPressed("Find",adTabbox.getSelectedGridTab().isQueryActive() || 
				(!isNewRow && (m_onlyCurrentRows || m_onlyCurrentDays > 0)));
		
		toolbar.refreshUserQuery(adTabbox.getSelectedGridTab().getAD_Tab_ID(), getCurrentFindWindow() != null ? getCurrentFindWindow().getAD_UserQuery_ID() : 0);

		// update from customized implementation
		adtab.updateToolbar(toolbar);
	}

	/**
	 * @param e
	 * @see DataStatusListener#dataStatusChanged(DataStatusEvent)
	 */
	@Override
    public void dataStatusChanged(DataStatusEvent e)
    {
    	//ignore non-ui thread event.
    	if (Executions.getCurrent() == null)
    	{
    		// Re-post incremental loading event to UI thread
    		if (   e.isLoading() && e.getSource() != null
    			&& (   e.getSource().equals(adTabbox.getSelectedGridTab().getTableModel())
    				|| (   adTabbox.getSelectedDetailADTabpanel() != null 
    				    && adTabbox.getSelectedDetailADTabpanel().getGridTab() != null
    				    && e.getSource().equals(adTabbox.getSelectedDetailADTabpanel().getGridTab().getTableModel()))))
    		{
    			Executions.schedule(getComponent().getDesktop(), evt -> {
    				this.dataStatusChanged(e);
    			}, new Event("onAsynchronousDataStatusChanged"));
    		}
    		return;
    	}

    	boolean detailTab = false;
    	if (e.getSource() instanceof GridTable)
    	{
    		GridTable gridTable = (GridTable) e.getSource();
    		if (adTabbox.getSelectedGridTab() != null && adTabbox.getSelectedGridTab().getTableModel() != gridTable) {
    			detailTab = true;
    		}
    	} else if (e.getSource() instanceof GridTab)
    	{
    		GridTab gridTab = (GridTab)e.getSource();
    		if (adTabbox.getSelectedGridTab() != gridTab) detailTab = true;
    	}

    	//update window title
        String adInfo = e.getAD_Message();
        if (!detailTab
        	&& (   adInfo == null
        	|| GridTab.DEFAULT_STATUS_MESSAGE.equals(adInfo)
        	|| GridTable.DATA_REFRESH_MESSAGE.equals(adInfo)
        	|| GridTable.DATA_INSERTED_MESSAGE.equals(adInfo)
        	|| GridTable.DATA_IGNORED_MESSAGE.equals(adInfo)
        	|| GridTable.DATA_UPDATE_COPIED_MESSAGE.equals(adInfo)
        	|| GridTable.DATA_SAVED_MESSAGE.equals(adInfo)
           )) {

	        String prefix = null;
	        if (adTabbox.needSave(true, false) ||
        		adTabbox.getSelectedGridTab().isNew() ||
        		(adTabbox.getSelectedDetailADTabpanel() != null && adTabbox.getSelectedDetailADTabpanel().getGridTab().isNew())) {
	        	// same condition as enableSave below
	        	prefix = "*";
	        }

	        String titleLogic = null;
	        int windowID = getADTab().getSelectedGridTab().getAD_Window_ID();
	        if (windowID > 0) {
	        	titleLogic = MWindow.get(Env.getCtx(), windowID).getTitleLogic();
	        }
	        String header = null;
	        if (! Util.isEmpty(titleLogic)) {
		        StringBuilder sb = new StringBuilder();
		        if (prefix != null)
		        	sb.append(prefix);
				sb.append(Env.getContext(ctx, curWindowNo, "_WinInfo_WindowName", false)).append(": ");
				if (titleLogic.contains("<")) {
					// IDEMPIERE-1328 - enable using format or subcolumns on title
					if (   getADTab() != null
						&& getADTab().getADTabpanel(0) != null
						&& getADTab().getADTabpanel(0).getGridTab() != null
						&& getADTab().getADTabpanel(0).getGridTab().getTableModel() != null) {
						GridTab tab = getADTab().getADTabpanel(0).getGridTab();
						int row = tab.getCurrentRow();
						int cnt = tab.getRowCount();
						boolean inserting = tab.getTableModel().isInserting();
						if (row >= 0 && cnt > 0 && !inserting) {
							PO po = tab.getTableModel().getPO(row);
							titleLogic = Env.parseVariable(titleLogic, po, null, false);
						} else {
							titleLogic = Env.parseContext(Env.getCtx(), curWindowNo, titleLogic, false, true);
						}
					}
				} else {
					titleLogic = Env.parseContext(Env.getCtx(), curWindowNo, titleLogic, false, true);
				}
        		sb.append(titleLogic);
        		header = sb.toString().trim();
        		if (header.endsWith(":"))
        			header = header.substring(0, header.length()-1);
	        }
	        if (Util.isEmpty(header))
	        	header = AEnv.getDialogHeader(Env.getCtx(), curWindowNo, prefix);

	        SessionManager.getAppDesktop().setTabTitle(header, curWindowNo);
        }

    	if (!detailTab)
    	{
	        String dbInfo = e.getMessage();
	        if (logger.isLoggable(Level.INFO)) logger.info(dbInfo);
	        if (adTabbox.getSelectedGridTab() != null && adTabbox.getSelectedGridTab().isQueryActive())
	            dbInfo = "[ " + dbInfo + " ]";
	        breadCrumb.setStatusDB(dbInfo, e, adTabbox.getSelectedGridTab());
    	}
    	else if (adTabbox.getSelectedDetailADTabpanel() == null)
    	{
    		return;
    	}

        //  Set Message / Info
        if (e.getAD_Message() != null || e.getInfo() != null)
        {
        	if (GridTab.DEFAULT_STATUS_MESSAGE.equals(e.getAD_Message()))
        	{
        		if (detailTab) {
        			String msg = e.getTotalRows() + " " + Msg.getMsg(Env.getCtx(), "Records");
                	adTabbox.setDetailPaneStatusMessage(msg, false);
				} else {
					if (getActiveGridTab().isQuickForm)
					{
						statusBarQF.setStatusLine("", false);
					}
					else
					{
						statusBar.setStatusLine("", false);
					}
				}
        	}
        	else
        	{
	            StringBuilder sb = new StringBuilder();
	            String msg = e.getMessage();
	            StringBuilder adMessage = new StringBuilder();
	            String origmsg = null;
	            if (msg != null && msg.length() > 0)
	            {
	            	if (detailTab && GridTable.DATA_REFRESH_MESSAGE.equals(e.getAD_Message()))
	            	{
	            		origmsg = e.getTotalRows() + " " + Msg.getMsg(Env.getCtx(), "Records");
	            	}
	            	else
	            	{
	            		origmsg = Msg.getMsg(Env.getCtx(), e.getAD_Message());
	            	}
	            	adMessage.append(origmsg);
	            }
	            String info = e.getInfo();
	            if (info != null && info.length() > 0)
	            {
	            	Object[] arguments = info.split("[;]");
	            	int index = 0;
	            	while(index < arguments.length)
	            	{
	            		String expr = "{"+index+"}";
	            		if (adMessage.indexOf(expr) >= 0)
	            		{
	            			index++;
	            		}
	            		else
	            		{
	            			break;
	            		}
	            	}
	            	if (index < arguments.length)
	            	{
	            		if (adMessage.length() > 0 && !adMessage.toString().trim().endsWith(":"))
		                    adMessage.append(": ");
	            		StringBuilder tail = new StringBuilder();
	            		while(index < arguments.length)
	            		{
	            			if (tail.length() > 0) tail.append(", ");
	            			tail.append("{").append(index).append("}");
	            			index++;
	            		}
	            		adMessage.append(tail);
	            	}
					if (   arguments.length == 1 
						&& origmsg != null 
						&& origmsg.equals(arguments[0])) { // check dup message
		            	sb.append(origmsg);
					} else {
		            	String adMessageQuot = Util.replace(adMessage.toString(), "'", "''");
		            	sb.append(new MessageFormat(adMessageQuot, Env.getLanguage(Env.getCtx()).getLocale()).format(arguments));
	            	}
	            }
	            else
	            {
	            	sb.append(adMessage);
	            }
	            if (sb.length() > 0)
	            {
	                int pos = sb.indexOf("\n");
	                if (pos != -1 && pos+1 < sb.length())  // replace CR/NL
	                {
	                    sb.replace(pos, pos+1, " - ");
	            	}
	                if (detailTab) {
	                	adTabbox.setDetailPaneStatusMessage(sb.toString (), e.isError ());
	                } else {
	                	if (getActiveGridTab().isQuickForm)
						{
	                		statusBarQF.setStatusLine(sb.toString(), e.isError());
						}
						else
						{
							statusBar.setStatusLine(sb.toString(), e.isError());
						}
	                }
	            }
        	}
        } else if (detailTab && e.isLoading()) {
			String msg = e.getMessage();
        	adTabbox.setDetailPaneStatusMessage(msg, false);
        }

        IADTabpanel tabPanel = detailTab ? adTabbox.getSelectedDetailADTabpanel()
    			: getADTab().getSelectedTabpanel();

        //  Process Error
        if (e.isError() && !e.isConfirmed() && tabPanel instanceof ADTabpanel)
        {
        	//focus to error field
        	GridField[] fields = tabPanel.getGridTab().getFields();
        	for (GridField field : fields)
        	{
        		if (field.isError())
        		{
        			((ADTabpanel)tabPanel).setFocusToField(field.getColumnName());
        			break;
        		}
        	}
            e.setConfirmed(true);   //  show just once - if MTable.setCurrentRow is involved the status event is re-issued
        }
        //  Process Warning
        else if (e.isWarning() && !e.isConfirmed())
        {
        	boolean isImporting = false; 
        	if (e.getSource() instanceof GridTab) {
        		GridTab gridTab = (GridTab)e.getSource();
        		isImporting = gridTab.getTableModel().isImporting();
        	} else if (e.getSource() instanceof GridTable) {
        		GridTable gridTable = (GridTable) e.getSource();
        		isImporting = gridTable.isImporting();
        	}
        	if (!isImporting) {
        		Dialog.warn(curWindowNo, e.getAD_Message(), e.getInfo(), null);
        		e.setConfirmed(true);   //  show just once - if MTable.setCurrentRow is involved the status event is re-issued
        	}
        }

        //update toolbar buttons state
        boolean changed = e.isChanged() || e.isInserting();
        boolean readOnly = adTabbox.getSelectedGridTab().isReadOnly();
        boolean processed = adTabbox.getSelectedGridTab().isProcessed();
        boolean insertRecord = !readOnly;
        boolean deleteRecord = !readOnly;
        if (!detailTab)
        {
	        if (insertRecord)
	        {
	            insertRecord = tabPanel.getGridTab().isInsertRecord();
	        }
	        toolbar.enableNew(!changed && insertRecord && !tabPanel.getGridTab().isSortTab());
	        toolbar.enableCopy(!changed && insertRecord && !tabPanel.getGridTab().isSortTab() && adTabbox.getSelectedGridTab().getRowCount()>0);
	        toolbar.enableRefresh(!changed);
	        if (deleteRecord)
	        {
	        	deleteRecord = tabPanel.getGridTab().isDeleteRecord();
	        }
	        toolbar.enableDelete(!changed && deleteRecord && !tabPanel.getGridTab().isSortTab() && !processed);
	        //
	        if (readOnly && adTabbox.getSelectedGridTab().isAlwaysUpdateField())
	        {
	            readOnly = false;
	        }
        }
        else
        {
        	adTabbox.updateDetailPaneToolbar(changed, readOnly);
        }
        boolean isEditting = adTabbox.needSave(true, false) ||
        		adTabbox.getSelectedGridTab().isNew() ||
        		(adTabbox.getSelectedDetailADTabpanel() != null && adTabbox.getSelectedDetailADTabpanel().getGridTab().isNew());
        toolbar.enableIgnore(isEditting);
        
        switchEditStatus (isEditting);
        
        //update recent item
        if (changed && !readOnly && !toolbar.isSaveEnable() ) {
        	if (!Util.isEmpty(tabPanel.getGridTab().getRecord_UU()) || tabPanel.getGridTab().getRecord_ID() > 0) {
            	if (adTabbox.getSelectedIndex() == 0 && !detailTab) {
            		MRecentItem.addModifiedField(ctx, adTabbox.getSelectedGridTab().getAD_Table_ID(),
            				adTabbox.getSelectedGridTab().getRecord_ID(), adTabbox.getSelectedGridTab().getRecord_UU(), Env.getAD_User_ID(ctx),
            				Env.getAD_Role_ID(ctx), adTabbox.getSelectedGridTab().getAD_Window_ID(),
            				adTabbox.getSelectedGridTab().getAD_Tab_ID());
            	} else {
	        		GridTab mainTab = getMainTabAbove();
	        		if (mainTab != null) {
			        	MRecentItem.addModifiedField(ctx, mainTab.getAD_Table_ID(),
			        			mainTab.getRecord_ID(), mainTab.getRecord_UU(), Env.getAD_User_ID(ctx),
			        			Env.getAD_Role_ID(ctx), mainTab.getAD_Window_ID(),
			        			mainTab.getAD_Tab_ID());
	        		}
            	}
        	}
        }

        toolbar.enableSave(adTabbox.needSave(true, false) ||
        		adTabbox.getSelectedGridTab().isNew() ||
        		(adTabbox.getSelectedDetailADTabpanel() != null && adTabbox.getSelectedDetailADTabpanel().getGridTab().isNew()));

        if (!e.isError() && Util.isEmpty(adInfo)) {
        	autoSaveChanges(e);
        }
        
        //
        //  No Rows
        if (e.getTotalRows() == 0 && insertRecord && !detailTab && !tabPanel.getGridTab().isSortTab())
        {
            toolbar.enableNew(true);
            toolbar.enableCopy(false);
            toolbar.enableDelete(false);
        }

        //  Transaction info
        if (!detailTab)
        {
        	GridTab gt = adTabbox.getSelectedGridTab();
	        String trxInfo = gt.getStatusLine();
	        if (trxInfo == null)
	        	trxInfo = "";
            statusBar.setInfo(trxInfo);
	        SessionManager.getAppDesktop().updateHelpQuickInfo(gt);
        }

	    //  Check Attachment
        boolean canHaveAttachment = adTabbox.getSelectedGridTab().canHaveAttachment();       //  not single _ID column
        //
        if (canHaveAttachment && e.isLoading() &&
                adTabbox.getSelectedGridTab().getCurrentRow() > e.getLoadedRows())
        {
            canHaveAttachment = false;
        }
        if (canHaveAttachment && adTabbox.getSelectedGridTab().getRecord_ID() == -1 && Util.isEmpty(adTabbox.getSelectedGridTab().getRecord_UU()))    //   No Key
        {
            canHaveAttachment = false;
        }
        if (canHaveAttachment)
        {
            toolbar.enableAttachment(true);
            toolbar.setPressed("Attachment",adTabbox.getSelectedGridTab().hasAttachment());
        }
        else
        {
            toolbar.enableAttachment(false);
        }

        // Check Chat and PostIt
        boolean canHaveChat = true;
        if (e.isLoading() &&
                adTabbox.getSelectedGridTab().getCurrentRow() > e.getLoadedRows())
        {
            canHaveChat = false;
        }
        if (canHaveChat && adTabbox.getSelectedGridTab().getRecord_ID() == -1 && Util.isEmpty(adTabbox.getSelectedGridTab().getRecord_UU()))    //   No Key
        {
            canHaveChat = false;
        }
        if (canHaveChat)
        {
            toolbar.enableChat(true);
            toolbar.setPressed("Chat",adTabbox.getSelectedGridTab().hasChat());
            toolbar.enablePostIt(true);
            toolbar.setPressed("PostIt",adTabbox.getSelectedGridTab().hasPostIt());
            toolbar.enableLabel(true);
            toolbar.setPressed("Label",adTabbox.getSelectedGridTab().hasLabel());
        }
        else
        {
        	toolbar.enableChat(false);
        	toolbar.enablePostIt(false);
        	toolbar.enableLabel(false);
        }

        //  Lock Indicator
        if (toolbar.isPersonalLock)
        {
			toolbar.lock(adTabbox.getSelectedGridTab().isLocked());
        }
        //

        if (!detailTab) 
        {
        	adTabbox.evaluate(e);
        }
        
        //clean cache find windows (if needed)
		int record_ID = adTabbox.getSelectedGridTab().getRecord_ID();

        if (adTabbox.getSelectedGridTab().getTabLevel() == 0 && record_ID != masterRecord) {
        	cleanFindWindowHashMap();
        	masterRecord = record_ID;
        }

        boolean isNewRow = adTabbox.getSelectedGridTab().getRowCount() == 0 || adTabbox.getSelectedGridTab().isNew();
        toolbar.enableArchive(!isNewRow);
        toolbar.enableZoomAcross(!isNewRow);
        toolbar.enableActiveWorkflows(!isNewRow);
        toolbar.enableRequests(!isNewRow);
		toolbar.setPressed("Find", adTabbox.getSelectedGridTab().isQueryActive() || 
				(!isNewRow && (m_onlyCurrentRows || m_onlyCurrentDays > 0)));
		toolbar.refreshUserQuery(adTabbox.getSelectedGridTab().getAD_Tab_ID(), getCurrentFindWindow() != null ? getCurrentFindWindow().getAD_UserQuery_ID() : 0);

        toolbar.enablePrint(adTabbox.getSelectedGridTab().isPrinted() && !isNewRow);
        toolbar.enableReport(!isNewRow);
        toolbar.enableExport(!isNewRow && !adTabbox.getSelectedGridTab().isSortTab());
        toolbar.enableFileImport(toolbar.isNewEnabled());
		toolbar.enableCSVImport(toolbar.isNewEnabled() && adTabbox.getSelectedGridTab().hasTemplate());
        
        toolbar.enableTabNavigation(breadCrumb.hasParentLink(), adTabbox.getSelectedDetailADTabpanel() != null);
        
        IADTabpanel adtab = adTabbox.getSelectedTabpanel();
        toolbar.enableProcessButton(adtab != null && adtab.isEnableProcessButton());
        toolbar.enableCustomize(adtab.isEnableCustomizeButton());

    }

	/**
	 * Auto save current changes (if auto save is enable).<br/>
	 * Delegate to {@link #asyncAutoSave()}
	 * @param e DataStatusEvent
	 */
	private synchronized void autoSaveChanges(DataStatusEvent e) {		
		if (!e.isInitEdit() && toolbar.isSaveEnable() && MSysConfig.getBooleanValue(MSysConfig.ZK_AUTO_SAVE_CHANGES, false, Env.getAD_Client_ID(Env.getCtx()))) {
        	final IADTabpanel dirtyTabpanel = adTabbox.getDirtyADTabpanel();
        	if (dirtyTabpanel != null && !dirtyTabpanel.getGridTab().isSortTab() 
        		&& Util.isEmpty(dirtyTabpanel.getGridTab().getCommitWarning(), true)
        		&& Env.isAutoCommit(ctx, curWindowNo)) {
        		if (dirtyTabpanel.getGridTab().isNeedSaveAndMandatoryFill()) {
            		String tabsExcluded = MSysConfig.getValue(MSysConfig.ZK_AUTO_SAVE_TABS_EXCLUDED, Env.getAD_Client_ID(Env.getCtx()));
            		boolean isTabExcluded = false;
            		if (!Util.isEmpty(tabsExcluded)) {
            			String tabID = String.valueOf(dirtyTabpanel.getGridTab().getAD_Tab_ID());
            			String tabUU = dirtyTabpanel.getGridTab().getAD_Tab_UU();
            			for (String excl : tabsExcluded.split(",")) {
           					if (excl.equals(tabID) || excl.equals(tabUU)) {
           						isTabExcluded = true;
            				}
            			}
            		}
            		if (!isTabExcluded) {
            			//schedule for onClose to show confirmation dialog
            			Executions.schedule(getComponent().getDesktop(), 
            					e1 -> {
            						if (!showingOnExitDialog)
                        				Executions.schedule(getComponent().getDesktop(), e2 -> asyncAutoSave(), new Event("onAsyncAutoSave"));
            					},  new Event("onAutoSaveChangesSchedule"));
            		}
        		}
        	}
        }
	}

	/**
	 * Asynchronous execution of auto save.
	 */
	private synchronized void asyncAutoSave() {
		//ensure still dirty and can save
		if (toolbar.isSaveEnable() && !showingOnExitDialog) {
        	final IADTabpanel dirtyTabpanel = adTabbox.getDirtyADTabpanel();
        	if (dirtyTabpanel != null && dirtyTabpanel.getGridTab().isNeedSaveAndMandatoryFill()) {
        		onSave(false, false, null);
        	}
        }
	}
	
    /**
     * Is first tab selected
     * @return true if selected tab is first tab
     */
    public boolean isFirstTab()
    {
        int selTabIndex = adTabbox.getSelectedIndex();
        return (selTabIndex == 0);
    }

    /**
     * Refresh all row.<br/>
     * Delegate to {@link #onRefresh(boolean, boolean)}
     * @param fireEvent
     */
    public void onRefresh(final boolean fireEvent)
    {
    	onRefresh(fireEvent, true);
    }

    /**
     * Refresh all row.<br/>
     * Delegate to {@link #doOnRefresh(boolean)}
     * @param fireEvent
     * @param saveCurrentRow if true, save before refresh
     */
    public void onRefresh(final boolean fireEvent, final boolean saveCurrentRow)
    {
    	if (saveCurrentRow)
    	{
	    	onSave(false, true, new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result) {
					doOnRefresh(fireEvent);
				}
			});
    	}
    	else
    	{
    		doOnRefresh(fireEvent);
    	}
    }

	/**
	 * Refresh all rows
	 * @param fireEvent true to fire {@link StateChangeEvent}
	 */
	protected void doOnRefresh(final boolean fireEvent) {		
		IADTabpanel headerTab = adTabbox.getSelectedTabpanel();
		IADTabpanel detailTab = adTabbox.getSelectedDetailADTabpanel();
		try {
			adTabbox.getSelectedGridTab().dataRefreshAll(fireEvent, true);			
		} catch (Exception e) {			
			if (DBException.isTimeout(e)) {
				Dialog.error(getWindowNo(), "GridTabLoadTimeoutError");
			} else {
				Dialog.error(getWindowNo(), "Error", e.getMessage());
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
			adTabbox.getSelectedGridTab().reset();
			return;
		}
		
		adTabbox.getSelectedGridTab().refreshParentTabs();		
		headerTab.dynamicDisplay(0);
		if (detailTab != null)
		{
			detailTab.dynamicDisplay(0);
		}
		focusToActivePanel();
		// IDEMPIERE-1328 - refresh recent item after running a process, i.e. completing a doc that changes documentno
    	MRecentItem.touchUpdatedRecord(ctx, adTabbox.getSelectedGridTab().getAD_Table_ID(),
    			adTabbox.getSelectedGridTab().getRecord_ID(), adTabbox.getSelectedGridTab().getRecord_UU(), Env.getAD_User_ID(ctx));
	}

    /**
     * @see ToolbarListener#onRefresh()
     */
	@Override
    public void onRefresh()
    {
    	GridTab gridTab = adTabbox.getSelectedGridTab();
    	onRefresh(true, false);
    	if (gridTab.isSortTab()) { // refresh is not refreshing sort tabs
    		IADTabpanel tabPanel = adTabbox.getSelectedTabpanel();
    		tabPanel.query(false, 0, gridTab.getMaxQueryRecords());
    	}
    }
	
    /**
     * @see ToolbarListener#onHelp()
     */
	@Override
    public void onHelp()
    {	
    	closeToolbarPopup("Help");
    	SessionManager.getAppDesktop().showWindow(new HelpWindow(gridWindow), "center");    	    
    }

    @Override
    public void onNew()
    {
    	final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_NEW.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
				}
			}
		};
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onNewCallback(postCallback);
				}
			}
		};
		
		WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_NEW.getName());
    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, preCallback);
    }
    
    /**
     * Callback after execution of WindowValidatorEventType.BEFORE_NEW.
     * Create new record for edit by user.
     * @param postCallback
     */
    private void onNewCallback(final Callback<Boolean> postCallback)
    {
        if (!adTabbox.getSelectedGridTab().isInsertRecord())
        {
            logger.warning("Insert Record disabled for Tab");
            if (postCallback != null)
            	postCallback.onCallback(false);
            return;
        }

        saveAndNavigate(new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result)
				{
					boolean newRecord = adTabbox.getSelectedGridTab().dataNew(false);
			        if (newRecord)
			        {
			            adTabbox.getSelectedTabpanel().dynamicDisplay(0);
			            toolbar.enableNew(false);
			            toolbar.enableCopy(false);
			            toolbar.enableDelete(false);
			            breadCrumb.enableFirstNavigation(adTabbox.getSelectedGridTab().getCurrentRow() > 0);
			            breadCrumb.enableLastNavigation(adTabbox.getSelectedGridTab().getCurrentRow() + 1 < adTabbox.getSelectedGridTab().getRowCount());
			            toolbar.enableTabNavigation(breadCrumb.hasParentLink(), adTabbox.getSelectedDetailADTabpanel() != null);
			            toolbar.enableIgnore(true);
			            if (adTabbox.getSelectedGridTab().isSingleRow()) 
			            {
			            	if (adTabbox.getSelectedTabpanel().isGridView())
			            	{
			            		adTabbox.getSelectedTabpanel().switchRowPresentation();
			            	}
			            }
			            
			            if (adTabbox.getSelectedTabpanel().isGridView())
			            {
			            	adTabbox.getSelectedTabpanel().getGridView().onEditCurrentRow();
			            }
			            if (postCallback != null)
			            	postCallback.onCallback(true);
			        }
			        else
			        {
			            logger.severe("Could not create new record");
			            if (postCallback != null)
			            	postCallback.onCallback(false);
			        }
			        focusToActivePanel();
				}
				else
				{
					if (postCallback != null)
		            	postCallback.onCallback(result);
				}
			}
		});
    }

    @Override
    public void onCopy()
    {
    	final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_COPY.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
				}
			}
		};
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onCopyCallback(postCallback);
				}
			}
		};
		
		WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_COPY.getName());
    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, preCallback);
    }
    
	/**
	 * Callback after execution of WindowValidatorEventType.BEFORE_COPY.<br/>
	 * Create new record as a copy of current record.
	 * @param postCallback
	 */
    private void onCopyCallback(Callback<Boolean> postCallback)
    {
        if (!adTabbox.getSelectedGridTab().isInsertRecord())
        {
            logger.warning("Insert Record disabled for Tab");
            if (postCallback != null)
            	postCallback.onCallback(false);
            return;
        }

        boolean newRecord = adTabbox.getSelectedGridTab().dataNew(true);
        if (newRecord)
        {
            adTabbox.getSelectedTabpanel().dynamicDisplay(0);
            toolbar.enableNew(false);
            toolbar.enableCopy(false);
            toolbar.enableDelete(false);
            breadCrumb.enableFirstNavigation(adTabbox.getSelectedGridTab().getCurrentRow() > 0);
            breadCrumb.enableLastNavigation(adTabbox.getSelectedGridTab().getCurrentRow() + 1 < adTabbox.getSelectedGridTab().getRowCount());
            toolbar.enableTabNavigation(false);
            toolbar.enableIgnore(true);

            if (adTabbox.getSelectedGridTab().isSingleRow()) 
            {
            	if (adTabbox.getSelectedTabpanel().isGridView())
            	{
            		adTabbox.getSelectedTabpanel().switchRowPresentation();
            	}
            }

            if (adTabbox.getSelectedTabpanel().isGridView())
            {
            	adTabbox.getSelectedTabpanel().getGridView().onEditCurrentRow();
            }

            focusToActivePanel();

            if (postCallback != null)
            	postCallback.onCallback(true);
            
        }
        else
        {
            logger.severe("Could not create new record");
            if (postCallback != null)
            	postCallback.onCallback(false);
        }
        focusToActivePanel();
    }
    //

    /**
     * Delegate to {@link #doOnFind()}
     * @see ToolbarListener#onFind()
     */
    @Override
    public void onFind()
    {
        if (adTabbox.getSelectedGridTab() == null)
            return;

        clearTitleRelatedContext();
        
        // The record was not changed locally
        if (adTabbox.getDirtyADTabpanel() == null) {
        	doOnFind();
        } else {
            onSave(false, true, new Callback<Boolean>() {
    			@Override
    			public void onCallback(Boolean result) {
    				if (result) {
    					doOnFind();
    				}
    			}
    		});        	
        }
    }

    /**
     * Show find window
     */
	private void doOnFind() {
		//  Gets Fields from AD_Field_v
        GridField[] findFields = adTabbox.getSelectedGridTab().getFields();
        if (!isCurrentFindWindowValid()) {
        	if (!getFindWindow(findFields))
        		return;
        }

        if (!getCurrentFindWindow().getEventListeners(DialogEvents.ON_WINDOW_CLOSE).iterator().hasNext()) {
        	getCurrentFindWindow().addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					hideBusyMask();
					if (!getCurrentFindWindow().isCancel())
			        {
				        MQuery query = getCurrentFindWindow().getQuery();

				        //  Execute query
				        if (query != null)
				        {
				            m_onlyCurrentRows = false;          //  search history too
				            adTabbox.getSelectedGridTab().setQuery(query);
				            adTabbox.getSelectedTabpanel().query(m_onlyCurrentRows, m_onlyCurrentDays, adTabbox.getSelectedGridTab().getMaxQueryRecords());   //  autoSize
				        }

				        if (getCurrentFindWindow().isCreateNew()) {
				        	onNew();
				        } else {
				        	adTabbox.getSelectedGridTab().dataRefresh(false); // Elaine 2008/07/25
		        			adTabbox.getSelectedTabpanel().onAfterFind();
				        }
				        toolbar.refreshUserQuery(adTabbox.getSelectedGridTab().getAD_Tab_ID(), getCurrentFindWindow().getAD_UserQuery_ID());
				        focusToActivePanel();
			        }
					else
					{
						toolbar.setPressed("Find",adTabbox.getSelectedGridTab().isQueryActive());
						focusToLastFocusEditor();
					}
			        
				}
			});
        }

        if (getCurrentFindWindow().getParent() != getComponent().getParent())
        	getComponent().getParent().appendChild(getCurrentFindWindow());
        else
        	getCurrentFindWindow().setVisible(true);
        showBusyMask(getCurrentFindWindow());                
        LayoutUtils.openEmbeddedWindow(toolbar, getCurrentFindWindow(), "after_start");
	}
	
	/**
	 * Validates if the current FindWindow corresponds to the active tab and record  
	 * @return true if the current find window is good to use
	 */
	private boolean isCurrentFindWindowValid() {
        GridField[] findFields = adTabbox.getSelectedGridTab().getFields();
		return getCurrentFindWindow() != null && getCurrentFindWindow().validate(adTabbox.getSelectedGridTab().getWindowNo(), 
				adTabbox.getSelectedGridTab().getName(),
	            adTabbox.getSelectedGridTab().getAD_Table_ID(), 
	            adTabbox.getSelectedGridTab().getTableName(),
	            adTabbox.getSelectedGridTab().getWhereExtended(), 
	            findFields, 
	            1, 
	            adTabbox.getSelectedGridTab().getAD_Tab_ID());
	}

	@Override
	public void onIgnore() 
	{
    	final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_IGNORE.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
				}
			}
		};
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onIgnoreCallback(postCallback);
				}
			}
		};
		
		WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_IGNORE.getName());
    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, preCallback);
    }
	
	/**
	 * Callback after execution of WindowValidatorEventType.BEFORE_IGNORE.
	 * Undo current changes (if any).
	 * @param postCallback
	 */
    private void onIgnoreCallback(Callback<Boolean> postCallback)
    {
    	IADTabpanel dirtyTabpanel = adTabbox.getDirtyADTabpanel();
    	boolean newrecod = adTabbox.getSelectedGridTab().isNew();
    	if (dirtyTabpanel != null && dirtyTabpanel.getGridTab().isSortTab())
    	{
    		adTabbox.dataIgnore();
    		toolbar.enableIgnore(false);
    	}
    	else
    	{
    		clearTitleRelatedContext();

	        adTabbox.dataIgnore();
	        toolbar.enableIgnore(false);
	        if (newrecod) {
	        	onRefresh(true, false);
	        } else if (dirtyTabpanel != null) {
	        	dirtyTabpanel.getGridTab().dataRefresh(true);	// update statusbar & toolbar
	        	dirtyTabpanel.dynamicDisplay(0);
	        } else {
	        	onRefresh(true, false);
	        }

    	}
    	if (dirtyTabpanel != null) {
    		focusToTabpanel(dirtyTabpanel);
    		//ensure row indicator is not lost
    		if (dirtyTabpanel.getGridView() != null && 
    				dirtyTabpanel.getGridView().getListbox() != null &&
    				dirtyTabpanel.getGridView().getListbox().getRowRenderer() != null) {
        		RowRenderer<Object[]> renderer = dirtyTabpanel.getGridView().getListbox().getRowRenderer();
        		GridTabRowRenderer gtr = (GridTabRowRenderer)renderer;
        		org.zkoss.zul.Row row = gtr.getCurrentRow();
        		if (row != null)
        			gtr.setCurrentRow(row);    			
    		}
    	}
    	else
    		focusToActivePanel();

    	updateToolbar();
    	
    	if (postCallback != null)
    		postCallback.onCallback(true);
    }

    /**
     * Delegate to {@link #onSave(boolean, boolean, Callback)}
     * @see ToolbarListener#onSave()
     */
    @Override
    public void onSave()
    {
    	final IADTabpanel dirtyTabpanel = adTabbox.getDirtyADTabpanel();
		onSave(true, false, new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result)
			{
				if (result)
				{
					String statusLine = null;
					if (getActiveGridTab().isQuickForm)
					{
						statusLine = statusBarQF.getStatusLine();
					}
					else
					{
						statusLine = statusBar.getStatusLine();
					}
					adTabbox.getSelectedGridTab().dataRefreshAll(true, true);
					adTabbox.getSelectedGridTab().refreshParentTabs();
					if (getActiveGridTab().isQuickForm)
					{
						statusBarQF.setStatusLine(statusLine);
					}
					else
					{
						statusBar.setStatusLine(statusLine);
					}
					if (adTabbox.getSelectedDetailADTabpanel() != null && adTabbox.getSelectedDetailADTabpanel().getGridTab() != null)
						adTabbox.getSelectedDetailADTabpanel().getGridTab().dataRefreshAll(true, true);
				}
				if (dirtyTabpanel != null) {
					if (dirtyTabpanel == adTabbox.getSelectedDetailADTabpanel())
						Clients.scrollIntoView(dirtyTabpanel);
					focusToTabpanel(dirtyTabpanel);
				} else {
					focusToActivePanel();
				}
				
				if(adTabbox.getSelectedGridTab().isQuickForm())
					onRefresh(true, true);
			}
		});
    }

    /**
     * Fire {@link WindowValidatorEvent} and delegate actual save to {@link #onSaveCallback(boolean, boolean, Callback)}
     * @param onSaveEvent
     * @param onNavigationEvent
     * @param callback
     */
    public void onSave(final boolean onSaveEvent, final boolean onNavigationEvent, final Callback<Boolean> callback) {
    	final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_SAVE.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, callback);
				} else if (callback != null) {
					callback.onCallback(result);
				}
			}
		};
		
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onSaveCallback(onSaveEvent, onNavigationEvent, postCallback);
				} else if (callback != null) {
					callback.onCallback(result);
				}
			}
		};
		
    	WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_SAVE.getName());
    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, preCallback);
	}

    /**
     * Handle WPaymentEditor.ON_SAVE_PAYMENT event.
     * Do not call this directly.
     */
	public void onSavePayment()
    {
    	onSave(false, false, new Callback<Boolean>() {

			@Override
			public void onCallback(Boolean result) {
				onRefresh(true, false);
			}

		});
    }

	/**
	 * Callback after execution of WindowValidatorEventType.BEFORE_SAVE.
	 * Delegate to {@link #onSave0(boolean, boolean, boolean, boolean, Callback)}.
	 * @param onSaveEvent
	 * @param onNavigationEvent true if trigger by navigation event
	 * @param callback
	 */
    private void onSaveCallback(final boolean onSaveEvent, final boolean onNavigationEvent, final Callback<Boolean> callback)
    {
    	final boolean wasChanged = toolbar.isSaveEnable();
    	IADTabpanel dirtyTabpanel = adTabbox.getDirtyADTabpanel();
    	final boolean newRecord = dirtyTabpanel != null ? (dirtyTabpanel.getGridTab().isNew()) : adTabbox.getSelectedGridTab().isNew();
    	if (dirtyTabpanel == null) {
			onSave0(onSaveEvent, onNavigationEvent, newRecord, wasChanged, callback);
			return;
    	}
    	if (!Util.isEmpty(dirtyTabpanel.getGridTab().getCommitWarning()) ||
			(!Env.isAutoCommit(ctx, curWindowNo) && onNavigationEvent))
		{
			Dialog.ask(curWindowNo, "SaveChanges?", dirtyTabpanel.getGridTab().getCommitWarning(), new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						onSave0(onSaveEvent, onNavigationEvent, newRecord, wasChanged, callback);
					}
					else
					{
						if (callback != null)
			    			callback.onCallback(false);
					}
				}
			});
		}
		else
		{
			onSave0(onSaveEvent, onNavigationEvent, newRecord, wasChanged, callback);
		}
    }

    /**
     * Save changes.
     * Delegate to {@link CompositeADTabbox#dataSave(boolean)}
     * @param onSaveEvent
     * @param navigationEvent
     * @param newRecord
     * @param wasChanged
     * @param callback optional callback for result of save
     */
	private void onSave0(boolean onSaveEvent, boolean navigationEvent,
			boolean newRecord, boolean wasChanged, Callback<Boolean> callback) {
		IADTabpanel dirtyTabpanel = adTabbox.getDirtyADTabpanel();
		boolean retValue = adTabbox.dataSave(onSaveEvent);

		if (!retValue)
		{
			if (CLogger.peekError() == null && ! wasChanged && callback != null) {
				callback.onCallback(true);
				return;
			}
			showLastError();
			if (callback != null)
				callback.onCallback(false);
			return;
		} else if (!onSaveEvent && dirtyTabpanel != null && !(dirtyTabpanel instanceof ADSortTab)) //need manual refresh
		{
			dirtyTabpanel.getGridTab().setCurrentRow(dirtyTabpanel.getGridTab().getCurrentRow());
		}

		if (!navigationEvent && dirtyTabpanel != null) {
			dirtyTabpanel.dynamicDisplay(0);
			dirtyTabpanel.afterSave(onSaveEvent);
		}

		IADTabpanel dirtyTabpanel2 = adTabbox.getDirtyADTabpanel();
		if (dirtyTabpanel2 != null && dirtyTabpanel2 != dirtyTabpanel) {
			onSave(onSaveEvent, navigationEvent, callback);
			return;
		} else if (dirtyTabpanel instanceof ADSortTab) {
			ADSortTab sortTab = (ADSortTab) dirtyTabpanel;
			if (!sortTab.isChanged()) {
				if (sortTab == adTabbox.getSelectedTabpanel()) {
					if (getActiveGridTab().isQuickForm)
					{
						statusBarQF.setStatusLine(Msg.getMsg(Env.getCtx(), "Saved"));
					}
					else
					{
						statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "Saved"));
					}
				} else {
    				adTabbox.setDetailPaneStatusMessage(Msg.getMsg(Env.getCtx(), "Saved"), false);
    			}
    		}
		}

		if (wasChanged) {
		    if (newRecord) {
		    	if (!Util.isEmpty(adTabbox.getSelectedGridTab().getRecord_UU()) || adTabbox.getSelectedGridTab().getRecord_ID() > 0) {
		        	if (adTabbox.getSelectedIndex() == 0) {
			        	MRecentItem.addModifiedField(ctx, adTabbox.getSelectedGridTab().getAD_Table_ID(),
			        			adTabbox.getSelectedGridTab().getRecord_ID(), adTabbox.getSelectedGridTab().getRecord_UU(), Env.getAD_User_ID(ctx),
			        			Env.getAD_Role_ID(ctx), adTabbox.getSelectedGridTab().getAD_Window_ID(),
			        			adTabbox.getSelectedGridTab().getAD_Tab_ID());
		        	} else {
		        		GridTab mainTab = getMainTabAbove();
		        		if (mainTab != null) {
				        	MRecentItem.addModifiedField(ctx, mainTab.getAD_Table_ID(),
				        			mainTab.getRecord_ID(), mainTab.getRecord_UU(), Env.getAD_User_ID(ctx),
				        			Env.getAD_Role_ID(ctx), mainTab.getAD_Window_ID(),
				        			mainTab.getAD_Tab_ID());
		        		}
		        	}
		    	}
		    } else {
		    	if (adTabbox.getSelectedIndex() == 0) {
		        	MRecentItem.touchUpdatedRecord(ctx, adTabbox.getSelectedGridTab().getAD_Table_ID(),
		        			adTabbox.getSelectedGridTab().getRecord_ID(), adTabbox.getSelectedGridTab().getRecord_UU(), Env.getAD_User_ID(ctx));
		    	} else {
	        		GridTab mainTab = getMainTabAbove();
		    		if (mainTab != null) {
			        	MRecentItem.touchUpdatedRecord(ctx, mainTab.getAD_Table_ID(),
			        			mainTab.getRecord_ID(), mainTab.getRecord_UU(), Env.getAD_User_ID(ctx));
		    		}
		    	}
		    }
		}

		if (dirtyTabpanel != null && dirtyTabpanel != adTabbox.getSelectedTabpanel()) {
			Executions.getCurrent().setAttribute(DETAIL_TABPANEL_SAVED_ATTR, dirtyTabpanel);
			dirtyTabpanel.getGridTab().refreshParentTabs();
		}
		
		if (callback != null)
			callback.onCallback(true);
	}

	/**
	 * 
	 * @return root GridTab
	 */
	private GridTab getMainTabAbove() {
		/* when a detail record is modified add header to recent items */
		GridTab mainTab = adTabbox.getSelectedGridTab(); // find parent tab (IDEMPIERE-2125 - tbayen)
		while (mainTab != null && mainTab.getTabLevel() > 0) {
			GridTab parentTab = mainTab.getParentTab();
			if (parentTab == mainTab)
				break;
			mainTab = parentTab;
		}
		return mainTab;
	}

	/**
	 * Update status bar with last error from CLogger (if any)
	 */
	private void showLastError() {
		String msg = CLogger.retrieveErrorString(null);
		if (msg != null)
		{
			if (getActiveGridTab().isQuickForm)
			{
				statusBarQF.setStatusLine(Msg.getMsg(Env.getCtx(), msg), true);
			}
			else
			{
				statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), msg), true);
			}
		}
		//other error will be catch in the dataStatusChanged event
	}

	/**
	 * Update status bar with last warning from CLogger (if any)
	 */
	private void showLastWarning() {
		String msg = CLogger.retrieveWarningString(null);
		if (msg != null)
		{
			statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), msg), true);
		}
	}

	/**
	 * Save and create new record.
	 * Delegate to {@link #onSave(boolean, boolean, Callback)}.
	 * @see ToolbarListener#onSaveCreate()
	 */
	@Override
	public void onSaveCreate()
    {
    	onSave(true, true, new Callback<Boolean>() {

			@Override
			public void onCallback(Boolean result)
			{
				if(result)
		    	{
		    		adTabbox.getSelectedGridTab().dataRefreshAll(true, true);
		    		adTabbox.getSelectedGridTab().refreshParentTabs();
		    		IADTabpanel dirtyTabpanel = (IADTabpanel) Executions.getCurrent().removeAttribute(DETAIL_TABPANEL_SAVED_ATTR);
		    		if (dirtyTabpanel != null && dirtyTabpanel.getGridTab().isDetail()) {
		    			try {
							adTabbox.getSelectedTabpanel().getDetailPane().onNew();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
		    		} else {
		    			onNew();
		    		}
		    	}
			}
		});
    }

	@Override
	public void onDelete()
	{
		final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_DELETE.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
				}
			}
		};
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onDeleteCallback(postCallback);
				}
			}
		};
		
		WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_DELETE.getName());
    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, preCallback);
	}
	
	/**
	 * Callback after execution of WindowValidatorEventType.BEFORE_DELETE.
	 * Delete current record.
	 * @param postCallback
	 */
    private void onDeleteCallback(final Callback<Boolean> postCallback)
    {
        if (adTabbox.getSelectedGridTab().isReadOnly())
        {
        	if (postCallback != null)
        		postCallback.onCallback(false);
            return;
        }
        
        //delete selected if it is grid view and row selection
        final int[] indices = adTabbox.getSelectedGridTab().getSelection();
		if (indices.length > 0 && adTabbox.getSelectedTabpanel().isGridView())
		{
			onDeleteSelected(postCallback);
			return;
		}
		
		deleteConfirmationLogic = adTabbox.getSelectedGridTab().getDeleteConfirmationLogic();
		if(Util.isEmpty(deleteConfirmationLogic)) {
			Dialog.ask(curWindowNo, "DeleteRecord?", new Callback<Boolean>() {
				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
			        	//error will be catch in the dataStatusChanged event
			            boolean success = adTabbox.getSelectedGridTab().dataDelete();
			            adTabbox.getSelectedGridTab().dataRefreshAll(true, true);
			    		adTabbox.getSelectedGridTab().refreshParentTabs();
			    		if (!success)
			    			showLastWarning();
	
			            adTabbox.getSelectedTabpanel().dynamicDisplay(0);
			            focusToActivePanel();
			            MRecentItem.publishChangedEvent(Env.getAD_User_ID(ctx));		           
					}
					if (postCallback != null)
						postCallback.onCallback(result);
				}
			});
		}
		else {
			int tableID = adTabbox.getSelectedGridTab().getAD_Table_ID();
			int recordID = adTabbox.getSelectedGridTab().getRecord_ID();
			deleteConfirmationLogic = FileUtil.parseTitle(ctx, deleteConfirmationLogic, tableID, recordID, curWindowNo, null);
			deleteConfirmationLogic = Msg.parseTranslation(ctx, deleteConfirmationLogic);
			
			WEditor editor = new WStringEditor();
			editor.fillHorizontal();
			editor.setValidInput(deleteConfirmationLogic);
			
			Dialog.askForInputTextConfirmation(curWindowNo, editor, "DeleteRecordWithConfirm?", new Object[] {deleteConfirmationLogic}, null, 
					new Callback<Map.Entry<Boolean, String>>() {
				@Override
				public void onCallback(Map.Entry<Boolean, String> result)
				{
					if(!result.getKey() || !(result.getValue() instanceof String))
						return;
						
					if(result.getValue().equals(deleteConfirmationLogic)) {
						boolean success = adTabbox.getSelectedGridTab().dataDelete();
			            adTabbox.getSelectedGridTab().dataRefreshAll(true, true);
			    		adTabbox.getSelectedGridTab().refreshParentTabs();
			    		if (!success)
			    			showLastWarning();
	
			            adTabbox.getSelectedTabpanel().dynamicDisplay(0);
			            focusToActivePanel();
			            MRecentItem.publishChangedEvent(Env.getAD_User_ID(ctx));
			            
					}
					if (postCallback != null)
						postCallback.onCallback(result.getValue().equals(deleteConfirmationLogic));
				}
			});		
		}
    }

    /**
     * Call from onDeleteCallback to delete selected records.
     * @param postCallback
     */
    private void onDeleteSelected(final Callback<Boolean> postCallback)
	{
    	if (adTabbox.getSelectedGridTab().isReadOnly() || !adTabbox.getSelectedTabpanel().isGridView())
        {
    		if (postCallback != null)
    			postCallback.onCallback(false);
            return;
        }
    	deleteConfirmationLogic = adTabbox.getSelectedGridTab().getDeleteConfirmationLogic();
		final int[] indices = adTabbox.getSelectedGridTab().getSelection();
		if(indices.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(Env.getContext(ctx, curWindowNo, "_WinInfo_WindowName", false)).append(" - ")
				.append(indices.length).append(" ").append(Msg.getMsg(Env.getCtx(), "Selected"));
			if(Util.isEmpty(deleteConfirmationLogic)) {
				Dialog.ask(sb.toString(), curWindowNo,"DeleteSelection", new Callback<Boolean>() {
					@Override
					public void onCallback(Boolean result) {
						if(result){
							adTabbox.getSelectedGridTab().clearSelection();						
							Arrays.sort(indices);
							int offset = 0;
							int count = 0;
							for (int i = 0; i < indices.length; i++)
							{
								adTabbox.getSelectedGridTab().navigate(indices[i]-offset);
								if (adTabbox.getSelectedGridTab().dataDelete())
								{
									offset++;
									count++;
								}
							}
				            adTabbox.getSelectedGridTab().dataRefreshAll(true, true);
				    		adTabbox.getSelectedGridTab().refreshParentTabs();
							
							adTabbox.getSelectedTabpanel().dynamicDisplay(0);
							if (getActiveGridTab().isQuickForm)
							{
								statusBarQF.setStatusLine(Msg.getMsg(Env.getCtx(), "Deleted") + ": " + count, false);
							}
							else
							{
								statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "Deleted") + ": " + count, false);
							}
				            MRecentItem.publishChangedEvent(Env.getAD_User_ID(ctx));
						}
						if (postCallback != null)
							postCallback.onCallback(result);
					}
				});
			}
			else {
				deleteConfirmationLogic = Msg.getMsg(ctx, "DeleteSelection");
				
				WEditor editor = new WStringEditor();
				editor.fillHorizontal();
				editor.setValidInput(deleteConfirmationLogic);
				
				Dialog.askForInputTextConfirmation(curWindowNo, editor, "DeleteSelectionWithConfirm?", new String[] {Integer.toString(indices.length), deleteConfirmationLogic}, null, 
						new Callback<Map.Entry<Boolean, String>>() {
					@Override
					public void onCallback(Map.Entry<Boolean, String> result)
					{
						if(!result.getKey() || !(result.getValue() instanceof String))
							return;
							
						if(result.getValue().equals(deleteConfirmationLogic)) {
							adTabbox.getSelectedGridTab().clearSelection();						
							Arrays.sort(indices);
							int offset = 0;
							int count = 0;
							for (int i = 0; i < indices.length; i++)
							{
								adTabbox.getSelectedGridTab().navigate(indices[i]-offset);
								if (adTabbox.getSelectedGridTab().dataDelete())
								{
									offset++;
									count++;
								}
							}
				            adTabbox.getSelectedGridTab().dataRefreshAll(true, true);
				    		adTabbox.getSelectedGridTab().refreshParentTabs();
							
							adTabbox.getSelectedTabpanel().dynamicDisplay(0);
							if (getActiveGridTab().isQuickForm)
							{
								statusBarQF.setStatusLine(Msg.getMsg(Env.getCtx(), "Deleted") + ": " + count, false);
							}
							else
							{
								statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "Deleted") + ": " + count, false);
							}
				            MRecentItem.publishChangedEvent(Env.getAD_User_ID(ctx));
						}
						if (postCallback != null)
							postCallback.onCallback(result.getValue().equals(deleteConfirmationLogic));
					}
				});		
			}
		} else {
			if (getActiveGridTab().isQuickForm)
			{
				statusBarQF.setStatusLine(Msg.getMsg(Env.getCtx(), "Selected") + ": 0", false);
			}
			else
			{
				statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "Selected") + ": 0", false);
			}
			if (postCallback != null)
				postCallback.onCallback(false);
		}
	}

    /**
     * Delegate to {@link #onPrintCallback(Callback)}
     */
    @Override
    public void onPrint() {
    	closeToolbarPopup("Print");
    	final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_PRINT.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
				}
			}
		};
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onPrintCallback(postCallback);
				}
			}
		};
		
		WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_PRINT.getName());
    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, preCallback);
    }
    
    /**
     * Callback after execution of WindowValidatorEventType.BEFORE_PRINT.
     * Print current record.
     * @param postCallback
     */
	private void onPrintCallback(final Callback<Boolean> postCallback) {
		//Get process defined for this tab
		final int AD_Process_ID = adTabbox.getSelectedGridTab().getAD_Process_ID();

		//	No document print process defined
		if (AD_Process_ID == 0)
		{
			onReport();

			return;
		}

		Callback<Boolean> callback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					int table_ID = adTabbox.getSelectedGridTab().getAD_Table_ID();
					int record_ID = adTabbox.getSelectedGridTab().getRecord_ID();
					String record_UU = adTabbox.getSelectedGridTab().getRecord_UU();

					final ProcessModalDialog dialog = new ProcessModalDialog(AbstractADWindowContent.this, getWindowNo(), AD_Process_ID,table_ID, record_ID, record_UU, true);
					if (dialog.isValid()) {
						dialog.setBorder("normal");						
						getComponent().getParent().appendChild(dialog);
						showBusyMask(dialog);
						LayoutUtils.openOverlappedWindow(getComponent(), dialog, "middle_center");
						if (postCallback != null) {
							dialog.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
								@Override
								public void onEvent(Event event) throws Exception {
									postCallback.onCallback(!dialog.isCancel());
								}
							});
						}
						dialog.focus();
					} else if (postCallback != null) {
						postCallback.onCallback(result);
					}
				} else if (postCallback != null) {
					postCallback.onCallback(result);
				}
			}
		};
		onSave(false, true, callback);
	}

	/**
	 * Delegate to {@link #onReport0()}
     * @see ToolbarListener#onReport()
     */
	@Override
	public void onReport() {
		if (!MRole.getDefault().isCanReport(adTabbox.getSelectedGridTab().getAD_Table_ID()))
		{
			Dialog.error(curWindowNo, "AccessCannotReport");
			return;
		}

		Callback<Boolean> callback = new Callback<Boolean>() {

			@Override
			public void onCallback(Boolean result) {
				if (result) {
					onReport0();
				} else {
					focusToLastFocusEditor();
				}
			}
		};
		onSave(false, true, callback);
	}

	/**
	 * Call {@link ReportAction}
	 */
	private void onReport0() {
		ReportAction reportAction = new ReportAction(this);
		reportAction.show();
	}

	/**
	 * Delegate to {@link WZoomAcross}.
     * @see ToolbarListener#onZoomAcross()
     */
	@Override
	public void onZoomAcross() {
		if (toolbar.getEvent() != null)
		{
			int record_ID = adTabbox.getSelectedGridTab().getRecord_ID();
			if (record_ID <= 0) {
				MTable table = MTable.get(adTabbox.getSelectedGridTab().getAD_Table_ID());
				if (!table.isUUIDKeyTable())
					return;
			}

			//	Query
			MQuery query = new MQuery();
			//	Current row
			String link = adTabbox.getSelectedGridTab().getKeyColumnName();
			//	Link for detail records
			if (link.length() == 0)
				link = adTabbox.getSelectedGridTab().getLinkColumnName();
			if (link.length() != 0)
			{
				if (link.endsWith("_ID"))
					query.addRestriction(link, MQuery.EQUAL,
						Integer.valueOf(Env.getContextAsInt(ctx, curWindowNo, link)));
				else
					query.addRestriction(link, MQuery.EQUAL,
						Env.getContext(ctx, curWindowNo, link));
			}
			new WZoomAcross(toolbar.getToolbarItem("ZoomAcross"), adTabbox.getSelectedGridTab()
					.getTableName(), adTabbox.getSelectedGridTab().getAD_Window_ID(), query);
		}
	}

	/**
     * @see ToolbarListener#onActiveWorkflows()
     */
	@Override
	public void onActiveWorkflows() {
		if (toolbar.getEvent() != null)
		{
			if (adTabbox.getSelectedGridTab().getRecord_ID() <= 0) {
				return;
			} else {
				closeToolbarPopup("ActiveWorkflows");
				try {
					AEnv.startWorkflowProcess(adTabbox.getSelectedGridTab().getAD_Table_ID(), adTabbox.getSelectedGridTab().getRecord_ID());
				} catch (Exception e) {
					CLogger.get().saveError("Error", e);
					throw new ApplicationException(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Close popup for toolbar button
	 * @param btnName toobar button name
	 */
	private void closeToolbarPopup(String btnName) {
		LabelImageElement btn = toolbar.getToolbarItem(btnName);
		Popup popup = LayoutUtils.findPopup(btn.getParent());
		if (popup != null) {
			popup.close();
		}
	}

	/**
	 * Delegate to {@link WRequest}
     * @see ToolbarListener#onRequests()
     */
	@Override
	public void onRequests()
	{
		if (toolbar.getEvent() != null)
		{
			int C_BPartner_ID = 0;
			Object bpartner = adTabbox.getSelectedGridTab().getValue("C_BPartner_ID");
			if(bpartner != null)
				C_BPartner_ID = Integer.valueOf(bpartner.toString());

			new WRequest(toolbar.getToolbarItem("Requests"), adTabbox.getSelectedGridTab().getAD_Table_ID(), adTabbox.getSelectedGridTab().getRecord_ID(), adTabbox.getSelectedGridTab().getRecord_UU(), C_BPartner_ID);
		}
	}
	
	/**
     * @see ToolbarListener#onProductInfo()
     */
	@Override
	public void onProductInfo()
	{
		closeToolbarPopup("ProductInfo");
		InfoPanel.showPanel(I_M_Product.Table_Name);
	}
	
	/**
	 * Delegate to {@link WArchive}
     * @see ToolbarListener#onArchive()
     */
	@Override
	public void onArchive()
	{
		if (toolbar.getEvent() != null)
		{
			new WArchive(toolbar.getToolbarItem("Archive"),
					adTabbox.getSelectedGridTab().getAD_Table_ID(),
					adTabbox.getSelectedGridTab().getRecord_ID(),
					adTabbox.getSelectedGridTab().getRecord_UU());
		}
	}

	/**
	 * Delegate to {@link ExportAction}
	 */
	@Override
	public void onExport() {
		int AD_Table_ID=getActiveGridTab().getAD_Table_ID();
		final boolean isCanExport=MRole.getDefault().isCanExport(AD_Table_ID);
		if (!isCanExport) {
			Dialog.error(curWindowNo, "AccessCannotExport");
			return;
		} else {
			ExportAction action = new ExportAction(this);
			action.export();
		}
	}

	/**
	 * Delegate to {@link FileImportAction}
	 */
	@Override
	public void onFileImport() {
		FileImportAction action = new FileImportAction(this);
		action.fileImport();
	}

	/**
	 * Delegate to {@link CSVImportAction}
	 */
	@Override
	public void onCSVImport() {
		CSVImportAction action = new CSVImportAction(this);
		action.fileImport();
	}
	
	/**
	 * Delegate to {@link #doOnQueryChange()}
	 */
	@Override
	public void onSearchQuery() {
		if (adTabbox.getSelectedGridTab() == null)
            return;

        clearTitleRelatedContext();

		// The record was not changed locally
        if (adTabbox.getDirtyADTabpanel() == null) {
        	doOnQueryChange();
        } else {
            onSave(false, false, new Callback<Boolean>() {
    			@Override
    			public void onCallback(Boolean result) {
    				if (result) {
    					doOnQueryChange();
    				}
    			}
    		});        	
        }
	}
	
	/**
	 * Simulate opening the Find Window, selecting a user query and click ok
	 */
	public void doOnQueryChange() {
		//  Gets Fields from AD_Field_v
		GridField[] findFields = adTabbox.getSelectedGridTab().getFields();
		if (!isCurrentFindWindowValid()) {
        	if (!getFindWindow(findFields))
        		return;
		}

		getCurrentFindWindow().setAD_UserQuery_ID(toolbar.getAD_UserQuery_ID());
		getCurrentFindWindow().advancedOkClick();
		MQuery query = getCurrentFindWindow().getQuery();

		//  Execute query
		if (query != null) {
			m_onlyCurrentRows = false;
			adTabbox.getSelectedGridTab().setQuery(query);
			try {
				adTabbox.getSelectedTabpanel().query(m_onlyCurrentRows, m_onlyCurrentDays, adTabbox.getSelectedTabpanel().getGridTab().getMaxQueryRecords());   //  autoSize
			} catch (Exception e) {
				if (   e.getCause() != null 
					&& e.getCause() instanceof SQLException
					&& DB.getDatabase().isQueryTimeout((SQLException)e.getCause())) {
					// ignore, is captured somewhere else
	        		return;
				} else {
					throw new DBException(e);
				}
			}
		}

		adTabbox.getSelectedGridTab().dataRefresh(false);

		focusToActivePanel();
		getCurrentFindWindow().dispose();
	}

	/**
	 *	Execute action for button.<br/>
	 *  With the exception of zoom to record_id, delegate to {@link #actionButton0(String, IProcessButton)}.
	 *  @param wButton {@link IProcessButton}
	 */
	private void actionButton (final IProcessButton wButton)
	{
		if (adTabbox.getSelectedGridTab().hasChangedCurrentTabAndParents()) {
			String msg = CLogger.retrieveErrorString("Please ReQuery Window");
			Dialog.error(curWindowNo, null, msg);
			return;
		}

		if (logger.isLoggable(Level.INFO)) logger.info(wButton.toString());

		final String col = wButton.getColumnName();

		//  Zoom for Record_ID
		if (col.equals("Record_ID"))
		{
			int AD_Table_ID = -1;
			int Record_ID = -1;

			if (wButton instanceof WButtonEditor) {
				int curTabNo = 0;
				WButtonEditor be = (WButtonEditor)wButton;
				if (be.getGridField() != null && be.getGridField().getGridTab() != null) {
					curTabNo = ((WButtonEditor)wButton).getGridField().getGridTab().getTabNo();
					AD_Table_ID = Env.getContextAsInt (ctx, curWindowNo, curTabNo, "AD_Table_ID");
					Record_ID = Env.getContextAsInt (ctx, curWindowNo, curTabNo, "Record_ID");
				}
			}
			if (AD_Table_ID < 0)
				AD_Table_ID = Env.getContextAsInt (ctx, curWindowNo, "AD_Table_ID");
			if (Record_ID < 0)
				Record_ID = Env.getContextAsInt (ctx, curWindowNo, "Record_ID");

			AEnv.zoom(AD_Table_ID, Record_ID);
			return;
		} // Zoom

		//  save first	---------------

		if (adTabbox.needSave(true, false))
		{
			onSave(false, true, new Callback<Boolean>() {
				@Override
				public void onCallback(Boolean result) {
					if (result) {
						actionButton0(col, wButton);
					}
				}
			});
		}
		else
		{
			actionButton0(col, wButton);
		}
	}

	/**
	 * Execution action of button.<br/>
	 * Delegate to {@link #executeButtonProcess(IProcessButton, boolean, int, int, boolean)} for process
	 * @param col column name
	 * @param wButton {@link IProcessButton}
	 */
	private void actionButton0 (String col, final IProcessButton wButton)
	{
		//To perform button action (adtabPanel is null in QuickForm)  
		IADTabpanel adtabPanel = null;
		if (adTabbox.getSelectedGridTab().isQuickForm())
		{
			adtabPanel=this.getADTab().getSelectedTabpanel();
		}
		else
		{
			adtabPanel = findADTabpanel(wButton);
		}
		boolean startWOasking = false;
		if (adtabPanel == null) {
			return;
		}
		final int table_ID = adtabPanel.getGridTab().getAD_Table_ID();

		//	Record_ID

		int record_ID = adtabPanel.getGridTab().getRecord_ID();
		String record_UU = adtabPanel.getGridTab().getRecord_UU();

		//	Record_ID - Language Handling

		if (record_ID == -1 && adtabPanel.getGridTab().getKeyColumnName().equals("AD_Language"))
			record_ID = Env.getContextAsInt (ctx, curWindowNo, "AD_Language_ID");

		//	Record_ID - Change Log ID

		if (record_ID == -1
			&& (wButton.getProcess_ID() == PROCESS_AD_CHANGELOG_UNDO || wButton.getProcess_ID() == PROCESS_AD_CHANGELOG_REDO))
		{
			Integer id = (Integer)adtabPanel.getGridTab().getValue("AD_ChangeLog_ID");
			record_ID = id.intValue();
		}

		//	Ensure it's saved

		if (record_ID == -1 && adtabPanel.getGridTab().getKeyColumnName().endsWith("_ID"))
		{
			Dialog.error(curWindowNo, "SaveErrorRowNotFound");
			return;
		}

		boolean isProcessMandatory = false;
		//	Show Document Action (Workflow) dialog
		if (col.equals("DocAction"))
		{
			final WDocActionPanel win = new WDocActionPanel(adtabPanel.getGridTab());
			if (win.getNumberOfOptions() == 0)
			{
				logger.info("DocAction - No Options");
				return;
			}
			else
			{
				final int recordIdParam = record_ID;				
				win.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						hideBusyMask();
						focusToLastFocusEditor();
						if (!win.isStartProcess()) {								
							return;
						}
						
						final Callback<Boolean> postCallback = new Callback<Boolean>() {
							@Override
							public void onCallback(Boolean result) {
								if (result) {
									WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_DOC_ACTION.getName());
							    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
								}
							}
						};
				    	Callback<Boolean> preCallback = new Callback<Boolean>() {
							@Override
							public void onCallback(Boolean result) {
								if (result) {
									boolean startWOasking = true;
									boolean isProcessMandatory = true;
									executeButtonProcess(wButton, startWOasking, table_ID, recordIdParam, isProcessMandatory, postCallback);
								}
							}
						};
						
						WindowValidatorEvent validatorEvent = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_DOC_ACTION.getName(), wButton);
				    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(validatorEvent, preCallback);						
					}
				});				
				getComponent().getParent().appendChild(win);
				showBusyMask(win);
				LayoutUtils.openOverlappedWindow(getComponent(), win, "middle_center");
				win.focus();
				return;
			}
		} // DocAction

		//  Show Create From dialog 
		else if (col.equals("CreateFrom"))
		{
			ICreateFrom cf = WCreateFromFactory.create(adtabPanel.getGridTab());

			if(cf != null)
			{
				if(cf.isInitOK())
				{					
					final WCreateFromWindow window = (WCreateFromWindow) cf.getWindow();
					if (SystemProperties.isZkUnitTest())
						window.setClientAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, AdempiereIdGenerator.escapeId(window.getTitle()));
					window.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							hideBusyMask();
							if (!window.isCancel()) {
								onRefresh(true, false);
							} else {
								focusToLastFocusEditor();
							}
						}
					});
					window.setZindex(1000);
					window.setMaximizable(true);
					window.setSizable(true);
					ZkCssHelper.appendStyle(window, "position: absolute; ");					
					getComponent().getParent().appendChild(window);
					showBusyMask(window);
					cf.showWindow();
					LayoutUtils.openOverlappedWindow(getComponent(), window, "middle_center");
					window.focus();
				}
				return;
			}
			// else may start process
		} // CreateFrom

		// Posting -----
		// Post record if not posted, show WAcctViewer if posted
		else if (col.equals("Posted") && MRole.getDefault().isShowAcct())
		{
			//  Check Doc Status

			String processed = Env.getContext(ctx, curWindowNo, "Processed");

			if (!processed.equals("Y"))
			{
				String docStatus = Env.getContext(ctx, curWindowNo, "DocStatus");

				if (DocAction.STATUS_Completed.equals(docStatus)
					|| DocAction.STATUS_Closed.equals(docStatus)
					|| DocAction.STATUS_Reversed.equals(docStatus)
					|| DocAction.STATUS_Voided.equals(docStatus)
					|| table_ID == MProjectIssue.Table_ID) // document without status
					;
				else
				{
					Dialog.error(curWindowNo, "PostDocNotComplete");
					return;
				}
			}

			// try to get table and record id from context data (eg for unposted view)
			// otherwise use current table/record
			int tableId = Env.getContextAsInt(ctx, curWindowNo, "AD_Table_ID", true);
			int recordId = Env.getContextAsInt(ctx, curWindowNo, "Record_ID", true);
			if ( tableId == 0 || recordId == 0 )
			{
				tableId = adtabPanel.getGridTab().getAD_Table_ID();
				recordId = adtabPanel.getGridTab().getRecord_ID();
			}

			//  Check Post Status
			final Object ps = adtabPanel.getGridTab().getValue("Posted");

			if (ps != null && ps.equals("Y"))
			{
				ADForm form = ADForm.openForm(SystemIDs.FORM_ACCOUNT_INFO,
						WAcctViewer.INITIAL_AD_TABLE_ID + "=" + tableId + "\n" + WAcctViewer.INITIAL_RECORD_ID + "=" + recordId);
				AEnv.showWindow(form);
			}
			else
			{
				final int tableIdRef = tableId;
				final int recordIdRef = recordId;
				Dialog.ask(curWindowNo, "PostImmediate?", new Callback<Boolean>() {

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							boolean force = ps != null && !ps.equals ("N");		//	force when problems

							String error = AEnv.postImmediate (curWindowNo, Env.getAD_Client_ID(ctx),
								tableIdRef, recordIdRef, force);

							onRefresh(true, false);

							if (error != null)
							{
								if (getActiveGridTab().isQuickForm)
								{
									statusBarQF.setStatusLine(error, true);
								}
								else
								{
									statusBar.setStatusLine(error, true);
								}
							}
						}
					}
				});
			}
			return;
		}   //  Posted

		final int finalRecordId = record_ID;
		final String finalRecordUU = record_UU;
		final Callback<Boolean> postCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					WindowValidatorEvent event = new WindowValidatorEvent(adwindow, WindowValidatorEventType.AFTER_PROCESS.getName());
			    	WindowValidatorManager.getInstance().fireWindowValidatorEvent(event, null);
				}
			}
		};
    	Callback<Boolean> preCallback = new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					executeButtonProcess(wButton, startWOasking, table_ID, finalRecordId, finalRecordUU, isProcessMandatory, postCallback);
				}
			}
		};
		
		WindowValidatorEvent validatorEvent = new WindowValidatorEvent(adwindow, WindowValidatorEventType.BEFORE_PROCESS.getName(), wButton);
		WindowValidatorManager.getInstance().fireWindowValidatorEvent(validatorEvent, preCallback);		
	} // actionButton

	/**
	 * Get mask for window
	 * @return {@link Div}
	 */
	private Div getMask() {
		if (mask == null) {
			mask = new Mask();
		}
		return mask;
	}
	
	/**
	 * Hide busy mask
	 */
	public void hideBusyMask() {
		if (mask != null && mask.getParent() != null) {
			mask.detach();
			StringBuilder script = new StringBuilder("(function(){let w=zk.Widget.$('#");
			script.append(getComponent().getParent().getUuid()).append("');if(w) w.busy=false;");
			script.append("})()");
			Clients.response(new AuScript(script.toString()));
		}
	}
	
	/**
	 * Show busy mask over window
	 * @param window
	 */
	public void showBusyMask(Window window) {
		getComponent().getParent().appendChild(getMask());
		StringBuilder script = new StringBuilder("(function(){let w=zk.Widget.$('#");
		script.append(getComponent().getParent().getUuid()).append("');");
		if (window != null) {
			script.append("let d=zk.Widget.$('#").append(window.getUuid()).append("');w.busy=d;");
		} else {
			script.append("w.busy=true;");
		}
		script.append("})()");
		Clients.response(new AuScript(script.toString()));
	}

	/**
	 * Is this window block by mask or highlighted window
	 * @return true if window is block by mask or highlighted window
	 */
	public boolean isBlock() {
		//check blocking by local mask
		if (mask != null && mask.getParent() != null) {
			return true;
		}
		
		//check blocking by highlighted window
		if (getComponent() != null && getComponent().getPage() != null) {
			Collection<Component> roots = getComponent().getPage().getRoots();
			for(Component comp : roots) {
				if (comp instanceof org.zkoss.zul.Window) {
					org.zkoss.zul.Window wnd = (org.zkoss.zul.Window) comp;
					if (wnd.isVisible() && wnd.inHighlighted())
						return true;
				}
			}
		}
		
		//check blocking by mask from ISupportMask (window, tabpanel)
		if (getComponent() != null) {
			Component p = getComponent().getParent();
			while (p != null) {
				if (p instanceof Mask) {
					if (p.isVisible()) {
						return true;
					}
				} else if (p instanceof DesktopTabpanel) {
					for(Component c : p.getChildren()) {
						if (c instanceof Mask) {
							if (c.isVisible())
								return true;
						}
					}
				}
				p = p.getParent();
			}
		}
		return false;
	}
	
	/**
	 * Show process, form or info window dialog for button.<br/>
	 * Delegate to {@link #executeButtonProcess0(IProcessButton, boolean, int, int, String, Callback)} or {@link #executionButtonInfoWindow0(IProcessButton)}.
	 * @param wButton
	 * @param startWOasking
	 * @param table_ID
	 * @param record_ID
	 * @param isProcessMandatory
	 * @param callback 
	 */
	public void executeButtonProcess(final IProcessButton wButton,
			final boolean startWOasking, final int table_ID, final int record_ID,
			boolean isProcessMandatory, Callback<Boolean> callback) {
		executeButtonProcess(wButton, startWOasking, table_ID, record_ID, null, isProcessMandatory, callback);
	}

	/**
	 * Show process, form or info window dialog for button.<br/>
	 * Delegate to {@link #executeButtonProcess0(IProcessButton, boolean, int, int, String, Callback)} or {@link #executionButtonInfoWindow0(IProcessButton)}.
	 * @param wButton
	 * @param startWOasking
	 * @param table_ID
	 * @param record_ID
	 * @param record_UU
	 * @param isProcessMandatory
	 * @param callback 
	 */
	public void executeButtonProcess(final IProcessButton wButton,
			final boolean startWOasking, final int table_ID, final int record_ID, final String record_UU,
			boolean isProcessMandatory, Callback<Boolean> callback) {
		/**
		 *  Start Process ----
		 */

		if (logger.isLoggable(Level.CONFIG)) logger.config("Process_ID=" + wButton.getProcess_ID() + ", InfoWindow_ID=" + wButton.getInfoWindow_ID() + ", Record_ID=" + record_ID + ", Record_UU=" + record_UU);

		if (wButton.getProcess_ID() == 0 && wButton.getInfoWindow_ID() == 0)
		{
			if (isProcessMandatory)
			{
				Dialog.error(curWindowNo, null, Msg.parseTranslation(ctx, "@NotFound@ @AD_Process_ID@ @AD_InfoWindow_ID@"));
			}
			return;
		}

		//	Save item changed

		if (adTabbox.needSave(true, false))
		{
			onSave(false, false, new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result) {
					if (result) {
						if (wButton.getInfoWindow_ID() > 0)
							executionButtonInfoWindow0(wButton);
						else
							executeButtonProcess0(wButton, startWOasking, table_ID, record_ID, callback);
					}
				}
			});
		}
		else
		{
			if (wButton.getInfoWindow_ID() > 0)
				executionButtonInfoWindow0(wButton);
			else
				executeButtonProcess0(wButton, startWOasking, table_ID, record_ID, record_UU, callback);
		}
	}

	/**
	 * Show {@link ADForm} or {@link ProcessModalDialog}.
	 * @param wButton
	 * @param startWOasking
	 * @param table_ID
	 * @param record_ID
	 * @param callback 
	 */
	private void executeButtonProcess0(final IProcessButton wButton,
			boolean startWOasking, int table_ID, int record_ID, Callback<Boolean> callback) {
		executeButtonProcess0(wButton, startWOasking, table_ID, record_ID, null, callback);	
	}

	/**
	 * Show {@link ADForm} or {@link ProcessModalDialog}.
	 * @param wButton
	 * @param startWOasking
	 * @param table_ID
	 * @param record_ID
	 * @param record_UU
	 * @param callback 
	 */
	private void executeButtonProcess0(final IProcessButton wButton,
			boolean startWOasking, int table_ID, int record_ID, String record_UU, Callback<Boolean> callback) {
		// call form
		MProcess pr = new MProcess(ctx, wButton.getProcess_ID(), null);
		int adFormID = pr.getAD_Form_ID();
		if (adFormID != 0 )
		{
			String title = wButton.getDescription();
			if (title == null || title.length() == 0)
				title = wButton.getDisplay();							
			ProcessInfo pi = new ProcessInfo (title, wButton.getProcess_ID(), table_ID, record_ID, record_UU);
			pi.setAD_User_ID (Env.getAD_User_ID(ctx));
			pi.setAD_Client_ID (Env.getAD_Client_ID(ctx));
			IADTabpanel adtabPanel = null;
			if (adTabbox.getSelectedGridTab().isQuickForm())
			{
				adtabPanel=this.getADTab().getSelectedTabpanel();
			}
			else
			{
				adtabPanel = findADTabpanel(wButton);
			}
			GridTab gridTab = null;
			if (adtabPanel != null)
				gridTab = adtabPanel.getGridTab();
			ADForm form = ADForm.openForm(adFormID, gridTab, pi, null, gridWindow.isSOTrx());
			Mode mode = form.getWindowMode();
			form.setAttribute(Window.MODE_KEY, form.getWindowMode());
			form.setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
			
			if (mode == Mode.HIGHLIGHTED || mode == Mode.MODAL) {
				form.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						hideBusyMask();
						onRefresh(true, false);						
					}
				});
				form.setPage(getComponent().getPage());
				form.doHighlighted();
				form.focus();
			}
			else {
				SessionManager.getAppDesktop().showWindow(form);
			}
		}
		else
		{
			IADTabpanel adtabPanel = null;
			if (adTabbox.getSelectedGridTab().isQuickForm())
				adtabPanel = this.getADTab().getSelectedTabpanel();
			else
				adtabPanel = findADTabpanel(wButton);

			ProcessInfo pi = new ProcessInfo("", wButton.getProcess_ID(), table_ID, record_ID, record_UU);
			if (adtabPanel != null && adtabPanel.isGridView() && adtabPanel.getGridTab() != null)
			{
				int[] indices = adtabPanel.getGridTab().getSelection();
				if (indices.length > 0)
				{
					MTable table = MTable.get(adtabPanel.getGridTab().getAD_Table_ID());
					if (table.isUUIDKeyTable()) {
						List<String> records = new ArrayList<String>();
						for (int i = 0; i < indices.length; i++) {
							String keyUUID = adtabPanel.getGridTab().getKeyUUID(indices[i]);
							if (!Util.isEmpty(keyUUID))
								records.add(keyUUID);
						}
						pi.setRecord_UUs(records);
					} else {
						List<Integer> records = new ArrayList<Integer>();
						for (int i = 0; i < indices.length; i++) {
							int keyID = adtabPanel.getGridTab().getKeyID(indices[i]);
							if (keyID > 0)
								records.add(keyID);
						}
						// IDEMPIERE-3998 Set multiple selected grid records into process info
						pi.setRecord_IDs(records);
					}

				}
			}

			ProcessModalDialog dialog;
			if (adtabPanel != null && adtabPanel.getGridTab() != null)
				dialog = new ProcessModalDialog(this, curWindowNo, adtabPanel.getGridTab().getTabNo(), pi, startWOasking);
			else
				dialog = new ProcessModalDialog(this, curWindowNo, pi, startWOasking);

			if (dialog.isValid())
			{
				dialog.setBorder("normal");				
				getComponent().getParent().appendChild(dialog);
				if (callback != null)
					dialog.setAttribute(PROCESS_POST_CALLBACK_ATTRIBUTE, callback);
				if (ClientInfo.isMobile())
				{
					dialog.doHighlighted();
				}
				else
				{
					showBusyMask(dialog);
					LayoutUtils.openOverlappedWindow(getComponent(), dialog, "middle_center");
				}
				Executions.schedule(getComponent().getDesktop(), e -> dialog.focus(), new Event("onPostShowProcessModalDialog"));
			}
			else if (callback != null)
			{
				if (dialog.isCancel()) 
				{
					callback.onCallback(Boolean.FALSE);
				} 
				else 
				{
					pi = dialog.getProcessInfo();
					if (pi == null || pi.isError())
						callback.onCallback(Boolean.FALSE);
					else
						callback.onCallback(Boolean.TRUE);
				}
			}
		}
	}

	/**
	 * Show {@link InfoWindow} for button
	 * @param wButton
	 */
	private void executionButtonInfoWindow0(final IProcessButton wButton) {
		IADTabpanel adtabPanel = null;
		if (adTabbox.getSelectedGridTab().isQuickForm())
		{
			adtabPanel = this.getADTab().getSelectedTabpanel();
		}
		else
		{
			adtabPanel = findADTabpanel(wButton);
		}
		if (adtabPanel == null)
			return;
			
		GridTab gridTab = adtabPanel.getGridTab();		
		if (gridTab == null)
			return;
		
		InfoWindow infoWindow = InfoManager.create(gridTab.getWindowNo(), wButton.getInfoWindow_ID(), (String)null);
		infoWindow.setAttribute(Window.MODE_KEY, Mode.OVERLAPPED);
		infoWindow.setCloseAfterExecutionOfProcess(true);		
		infoWindow.setBorder("normal");
		infoWindow.setClosable(true);
		infoWindow.moveProcessButtonsToBeforeRight();
		int height = ClientInfo.get().desktopHeight;
		int width = ClientInfo.get().desktopWidth;
		if (width <= ClientInfo.MEDIUM_WIDTH)
		{
			ZKUpdateUtil.setWidth(infoWindow, "100%");
			ZKUpdateUtil.setHeight(infoWindow, "100%");
		}
		else
		{
			height = height * 85 / 100;
    		width = width * 80 / 100;
    		ZKUpdateUtil.setWidth(infoWindow, width + "px");
    		ZKUpdateUtil.setHeight(infoWindow, height + "px");
		}
		infoWindow.setContentStyle("overflow: auto");

		if (SystemProperties.isZkUnitTest())
			infoWindow.setClientAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, AdempiereIdGenerator.escapeId(infoWindow.getTitle()));
		infoWindow.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				hideBusyMask();
				if (!infoWindow.isCancelled())
					onRefresh(true, false);
				else
					focusToLastFocusEditor();
			}
		});
		infoWindow.setZindex(1000);
		infoWindow.setMaximizable(true);
		infoWindow.setSizable(true);
		getComponent().getParent().appendChild(infoWindow);
		showBusyMask(infoWindow);
		LayoutUtils.openOverlappedWindow(getComponent(), infoWindow, "middle_center");	
		infoWindow.focusToFirstEditor();
	}
	
	/**
	 * Delegate to {@link #actionButton(IProcessButton)}
	 * @param event
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent event)
	{
		Runnable runnable = new Runnable() {
			public void run() {
				String error = processButtonCallout((IProcessButton) event.getSource());
				if (error != null && error.trim().length() > 0)
				{
					if (getActiveGridTab().isQuickForm)
					{
						statusBarQF.setStatusLine(error, true);
					}
					else
					{
						statusBar.setStatusLine(error, true);
					}
					focusToLastFocusEditor();
					return;
				}
				actionButton((IProcessButton) event.getSource());
			}
		};
		BusyDialogTemplate template = new BusyDialogTemplate(runnable);
		template.run();
	}

	/**
	 * Process Callout(s) for button.
	 *
	 * @param button
	 * @return error message or ""
	 * @see org.compiere.model.Callout
	 */
	private String processButtonCallout (IProcessButton button)
	{
		IADTabpanel adtab = null;
		if (adTabbox.getSelectedGridTab().isQuickForm())
		{
			adtab=this.getADTab().getSelectedTabpanel();
		}
		else
		{
			adtab = findADTabpanel(button);
		}
		if (adtab != null) {
			GridField field = adtab.getGridTab().getField(button.getColumnName());
			if (field != null)
				return adtab.getGridTab().processCallout(field);
			else
				return "";
		} else {
			return "";
		}
	}	//	processButtonCallout

	/**
	 * Find parent IADTabpanel for button
	 * @param button
	 * @return {@link IADTabpanel}
	 */
	public IADTabpanel findADTabpanel(IProcessButton button) {
		IADTabpanel adtab = null;
		if (button.getADTabpanel() != null)
			return button.getADTabpanel();

		Component c = button instanceof WEditor ? ((WEditor)button).getComponent() : (Component)button;
		while (c != null) {
			if (c instanceof IADTabpanel) {
				adtab = (IADTabpanel) c;
				break;
			}
			c = c.getParent();
		}
		return adtab;
	}

	/**
	 * Get IADTabbox instance
	 * @return {@link IADTabbox}
	 */
	public IADTabbox getADTab() {
		return adTabbox;
	}

	/**
	 * @param pi
	 */
	@Deprecated(forRemoval = true, since = "11")
	public void executeASync(ProcessInfo pi) {
	}

	/**
	 * Handle DialogEvents.ON_WINDOW_CLOSE event for {@link ProcessModalDialog}.<br/>
	 * Delegate update of UI to {@link #updateUI(ProcessInfo)}.
	 * @param pi
	 */
	private void onModalClose(ProcessInfo pi) {
		if (getActiveGridTab().isQuickForm){
			statusBarQF.setStatusLine(null);
		}else{
			statusBar.setStatusLine(null);
		}
		
		boolean notPrint = pi != null
		&& pi.getAD_Process_ID() != adTabbox.getSelectedGridTab().getAD_Process_ID()
		&& pi.isReportingProcess() == false;
		//
		//  Process Result

		if (Executions.getCurrent() != null)
		{
			if (notPrint || pi.isError()) // show process info if it is not print or have error
			{
				updateUI(pi);
			}
		}
		else
		{
			try {
				//acquire desktop, 2 second timeout
				Executions.activate(getComponent().getDesktop(), 2000);
				try {
					if (notPrint || pi.isError()) // show process info if it is not print or have error
					{
						updateUI(pi);
					}
                } catch(Error ex){
                	throw ex;
                } finally{
                	//release full control of desktop
                	Executions.deactivate(getComponent().getDesktop());
                }
			} catch (Exception e) {
				logger.log(Level.WARNING, "Failed to update UI upon unlock.", e);
			}
		}
	}

	/**
	 * Update UI from {@link ProcessModalDialog} DialogEvents.ON_WINDOW_CLOSE event
	 * @param pi
	 */
	private void updateUI(ProcessInfo pi) {						
		//	Timeout
		if (pi.isTimeout())		//	set temporarily to R/O
			Env.setContext(ctx, curWindowNo, "Processed", "Y");
		//	Update Status Line
		String summary = pi.getSummary();
		if (summary != null && summary.indexOf('@') != -1)
			pi.setSummary(Msg.parseTranslation(Env.getCtx(), summary));

		//		Get Log Info
		ProcessInfoUtil.setLogFromDB(pi);
		ProcessInfoLog m_logs[] = pi.getLogs();
		if (getActiveGridTab().isQuickForm)
		{
			statusBarQF.setStatusLine(pi.getSummary(), pi.isError(), m_logs);
		}
		else
		{
			statusBar.setStatusLine(pi.getSummary(), pi.isError(), m_logs);
		}
		
		
		if (m_logs != null && m_logs.length > 0) {
			ProcessInfoDialog dialog = ProcessInfoDialog.showProcessInfo(pi, curWindowNo, getComponent(), false);
			dialog.addEventListener(DialogEvents.ON_WINDOW_CLOSE, e -> focusToActivePanel());
		}		
	}

	/**
	 * Get toolbar component
	 * @return toolbar instance
	 */
	public ADWindowToolbar getToolbar() {
		return toolbar;
	}

	/**
	 * Get grid tab of selected tab
	 * @return {@link GridTab} of selected tab
	 */
	public GridTab getActiveGridTab() {
		return adTabbox.getSelectedGridTab();
	}

	/**
	 * Get window number
	 * @return windowNo
	 */
	public int getWindowNo() {
		return curWindowNo;
	}

	/**
	 * Show dialog to customize fields (hidden, display, order of field) in grid mode 
	 * @see CustomizeGridViewDialog
     * @see ToolbarListener#onCustomize()
     */
	@Override
	public void onCustomize() {
		ADTabpanel tabPanel = (ADTabpanel) getADTab().getSelectedTabpanel();
		CustomizeGridViewDialog.onCustomize(tabPanel, b -> {
			focusToLastFocusEditor();
		});
	}

	/**
	 * Show popup menu for process toolbar button (the gear button)
	 * @see org.adempiere.webui.event.ToolbarListener#onProcess()
	 * @see ProcessButtonPopup
	 */
	@Override
	public void onProcess() {
		ProcessButtonPopup popup = new ProcessButtonPopup();
		popup.setClientAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");
		IADTabpanel adtab = adTabbox.getSelectedTabpanel();
		popup.render(adtab.getToolbarButtons());
		if (popup.getChildren().size() > 0) {
			popup.setPage(this.getComponent().getPage());
			popup.open(getToolbar().getToolbarItem("Process"), "after_start");
		}
	}

	@Override
	public void onSelect() {
		if (getCurrentFindWindow() != null && getCurrentFindWindow().getPage() != null && getCurrentFindWindow().isVisible() && m_queryInitiating) {
			LayoutUtils.openEmbeddedWindow(getComponent().getParent(), getCurrentFindWindow(), "overlap");
		} else {
			focusToLastFocusEditor();
		}
	}

	/**
	 * Is selected tab has pending changes
	 * @return true if dirty
	 */
	public boolean isPendingChanges() {
		return adTabbox.getDirtyADTabpanel() != null;
	}

	/**
	 * Set parent AD window
	 * @param adwindow
	 */
	public void setADWindow(ADWindow adwindow) {
		this.adwindow = adwindow;
	}
	
	/**
	 * Get parent AD window instance 
	 * @return {@link ADWindow}
	 */
	public ADWindow getADWindow() {
		return adwindow;
	}
	
	/**
	 * Get find window
	 * @param findFields
	 * @return true if find window found and init ok
	 */
	public boolean getFindWindow(GridField[] findFields) {
		FindWindow findWindow = getCurrentFindWindow();
		if (findWindow != null && isCurrentFindWindowValid()) {
			toolbar.setSelectedUserQuery(findWindow.getAD_UserQuery_ID());
		} else {
			if (findWindow != null) {
				//reset to no/auto id
				FindWindow old = findWindow;
				old.setId("");
				if (old.getDesktop() != null) {
					AEnv.detachInputElement(old);
					Executions.schedule(old.getDesktop(), e -> old.detach(), new Event("onDetachOldFindWindow"));
				}
			}

			findWindow = Extensions.getFindWindow(adTabbox.getSelectedGridTab().getWindowNo(), adTabbox.getSelectedGridTab().getTabNo(), adTabbox.getSelectedGridTab().getName(),
					adTabbox.getSelectedGridTab().getAD_Table_ID(), adTabbox.getSelectedGridTab().getTableName(),
					adTabbox.getSelectedGridTab().getWhereExtended(), findFields, 1, adTabbox.getSelectedGridTab().getAD_Tab_ID(), this);
			
			setupEmbeddedFindwindow(findWindow);
			if (!findWindow.initialize()) {
				if (findWindow.getTotalRecords() == 0) {
					Dialog.info(curWindowNo, "NoRecordsFound");
				}
				return false;
			}
			tabFindWindowHashMap.put(adTabbox.getSelectedGridTab(), findWindow);
		}
		return true;
	}
	
	/**
	 * Get cache find window for current selected tab
	 * @return {@link FindWindow}
	 */
	public FindWindow getCurrentFindWindow() {
		return tabFindWindowHashMap.get(adTabbox.getSelectedGridTab());
	}
	
	/**
	 * Clean all the cached FindWindow objects for detail tabs
	 * when user navigate to a different master record.
	 */
	private void cleanFindWindowHashMap() {		
		List<FindWindow> list = tabFindWindowHashMap.entrySet().stream().filter(e -> e.getKey().getTabLevel() != 0)
			.map(Map.Entry::getValue)
	        .collect(Collectors.toList());
		tabFindWindowHashMap.keySet().removeIf(tab -> tab.getTabLevel() != 0);
		if (!list.isEmpty()) {
			list.forEach(e -> AEnv.detachInputElement(e));
			Executions.schedule(getComponent().getDesktop(), e -> list.forEach(f -> f.detach()), new Event("onDetachCacheFindWindow"));
		}
	}
	
	/**
	 * Clear environment context for title logic
	 */
	private void clearTitleRelatedContext() {
		// IDEMPIERE-1328
		// clear the values for the tab header
        String titleLogic = null;
        int windowID = getADTab().getSelectedGridTab().getAD_Window_ID();
        if (windowID > 0) {
        	titleLogic = MWindow.get(Env.getCtx(), windowID).getTitleLogic();
        }
        if (titleLogic != null) {
    		String token;
    		String inStr = new String(titleLogic);

    		int i = inStr.indexOf('@');
    		while (i != -1)
    		{
    			inStr = inStr.substring(i+1, inStr.length());	// from first @

    			int j = inStr.indexOf('@');						// next @
    			if (j < 0)
    			{
    				logger.log(Level.SEVERE, "No second tag: " + inStr);
    				return;						//	no second tag
    			}

    			token = inStr.substring(0, j);
        		Env.setContext(ctx, curWindowNo, token, "");

    			inStr = inStr.substring(j+1, inStr.length());	// from second @
    			i = inStr.indexOf('@');
    		}
        } else {
    		Env.setContext(ctx, curWindowNo, "DocumentNo", "");
    		Env.setContext(ctx, curWindowNo, "Value", "");
    		Env.setContext(ctx, curWindowNo, "Name", "");
        }
	}
	
	/**
	 * Get status bar for quick form
	 * @return Quick Form StatusBar
	 */
	public StatusBar getStatusBarQF()
	{
		return statusBarQF;
	}
	
	/**
	 * Set status bar for quick form
	 * @param statusBar
	 */
	public void setStatusBarQF(StatusBar statusBar)
	{
		statusBarQF = statusBar;
	}	
	
	/**
	 * Implementation to work key listener for the current open Quick Form.
	 */
	protected QuickGridView currQGV = null;

	/**
	 * Get grid view for quick form
	 * @return {@link QuickGridView}
	 */
	public QuickGridView getCurrQGV()
	{
		return currQGV;
	}

	/**
	 * Set grid view for quick form
	 * @param currQGV
	 */
	public void setCurrQGV(QuickGridView currQGV)
	{
		this.currQGV = currQGV;
	}

	/**
	 * Close Quick form to remove tabID from the list
	 * 
	 * @param AD_Tab_ID
	 */
	public void closeQuickFormTab(Integer AD_Tab_ID)
	{
		quickFormOpenTabs.remove(AD_Tab_ID);
	} // closeQuickFormTab

	/**
	 * Get list of open quick form tabs
	 * 
	 * @return list of tabIDs
	 */
	public ArrayList <Integer> getOpenQuickFormTabs( )
	{
		return quickFormOpenTabs;
	} // getOpenQuickFormTabs

	/**
	 * Register Quick form against tabID
	 * 
	 * @param AD_Tab_ID
	 * @return False when already quick form opens for same tab
	 */
	public boolean registerQuickFormTab(Integer AD_Tab_ID)
	{
		if (quickFormOpenTabs.contains(AD_Tab_ID))
		{
			return false;
		}

		quickFormOpenTabs.add(AD_Tab_ID);

		return true;
	} // registerQuickFormTab
	
	/**
	 * Get grid window model
	 * @return {@link GridWindow}
	 */
	public GridWindow getGridWindow() {
		return gridWindow;
	}
	
	/**
	 * Set component of last focus editor.<br/>
	 * Use in onClose/Exit to restore focus.
	 * @param component
	 */
	public void setLastFocusEditor(Component component) {
		lastFocusEditor = component;
	}
}
