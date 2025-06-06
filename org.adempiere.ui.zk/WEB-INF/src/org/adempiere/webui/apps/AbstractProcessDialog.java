/******************************************************************************
 * Copyright (C) 2014 Elaine Tan                                              *
 * Copyright (C) 2014 Trek Global
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempiere.webui.apps;

import static org.adempiere.webui.LayoutUtils.isLabelAboveInputForSmallWidth;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.ComboItem;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.info.InfoWindow;
import org.adempiere.webui.process.WProcessInfo;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.util.ZkContextRunnable;
import org.adempiere.webui.window.Dialog;
import org.adempiere.webui.window.MultiFileDownloadDialog;
import org.adempiere.webui.window.SimplePDFViewer;
import org.compiere.Adempiere;
import org.compiere.model.Lookup;
import org.compiere.model.MClient;
import org.compiere.model.MLanguage;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MProcess;
import org.compiere.model.MReportView;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUser;
import org.compiere.model.MUserDefProc;
import org.compiere.model.Query;
import org.compiere.model.SystemIDs;
import org.compiere.model.X_AD_PInstance;
import org.compiere.print.MPrintFormat;
import org.compiere.process.ProcessInfo;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuEcho;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vlayout;

/**
 * Abstract dialog base class for execution of process/report.
 * @see ProcessModalDialog
 * @see ProcessDialog 
 */
public abstract class AbstractProcessDialog extends Window implements IProcessUI, EventListener<Event>
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 484056046177205235L;

	/** Event to fire on complete of execution of process/report **/
	private static final String ON_COMPLETE_EVENT = "onComplete";
	/** Event to update status text of process dialog. Event data: status text message. **/
	private static final String ON_STATUS_UPDATE_EVENT = "onStatusUpdate";
	
	private static final CLogger log = CLogger.getCLogger(AbstractProcessDialog.class);

	protected int m_WindowNo;
	protected int m_TabNo;
	private Properties m_ctx;
	private int m_AD_Process_ID;
	private ProcessInfo m_pi = null;
	/** if true, auto call {@link #dispose()} in {@link #ON_COMPLETE_EVENT} handler. **/
	private boolean m_disposeOnComplete;
	/** Panel for process parameters **/
	private ProcessParameterPanel parameterPanel = null;
	/** Checkbox to toggle running process/report as background job **/
	private Checkbox runAsJobField = null;
	private Label notificationTypeLabel = null;
	/** 
	 * Drop down editor for {@link X_AD_PInstance#NOTIFICATIONTYPE_AD_Reference_ID} list.
	 * For background job notification when {@link #runAsJobField} is checked. 
	 */
	private WTableDirEditor notificationTypeField = null;

	private BusyDialog progressWindow;	
	
	/** translated process name */
	private String		    m_Name = null;
	/** translated process description */
	private String		    m_Description = null;
	/** translated process help */
	private String		    m_Help = null;
	/** Determine if a Help Process Window is shown **/
	private String          m_ShowHelp = null; 
	/** initial panel header message **/
	private String initialMessage;
	/** true if dialog is still valid, i.e not dispose yet **/
	private boolean m_valid = true;
	/** true if dialog have been cancelled by user **/
	private boolean m_cancel = false;
	
	/** Reference to process thread/task **/
	private Future<?> future;
	/** files for download by user **/
	private List<File> downloadFiles;
	/** true when UI have been locked, i.e busy **/
	private boolean m_locked = false;
	private String	m_AD_Process_UU = "";
		
	/**
	 * default constructor
	 */
	protected AbstractProcessDialog()
	{
		super();		
	}
	
	/**
	 * layout as below
	 * 
	 * @param ctx
	 * @param WindowNo
	 * @param AD_Process_ID
	 * @param pi
	 * @param autoStart
	 * @param isDisposeOnComplete
	 * @return true if init is ok.
	 */
	protected boolean init(Properties ctx, int WindowNo, int AD_Process_ID, ProcessInfo pi, boolean autoStart, boolean isDisposeOnComplete)
	{
		return init(ctx, WindowNo, 0, AD_Process_ID, pi, autoStart, isDisposeOnComplete);
	}

	/**
	 * Layout dialog
	 * 
	 * @param  ctx
	 * @param  WindowNo
	 * @param  TabNo
	 * @param  AD_Process_ID
	 * @param  pi
	 * @param  autoStart
	 * @param  isDisposeOnComplete
	 * @return true if init is ok.
	 */
	protected boolean init(Properties ctx, int WindowNo, int TabNo, int AD_Process_ID, ProcessInfo pi, boolean autoStart, boolean isDisposeOnComplete)
	{
		m_ctx = ctx;
		m_WindowNo = WindowNo;
		m_TabNo = TabNo;
		m_AD_Process_ID = AD_Process_ID;
		setProcessInfo(pi);
		m_disposeOnComplete = isDisposeOnComplete;
		
		if (log.isLoggable(Level.CONFIG))
			log.config("");
		//
		StringBuilder buildMsg = new StringBuilder();
		boolean trl = !Env.isBaseLanguage(m_ctx, "AD_Process");
		MProcess process = MProcess.get(AD_Process_ID);
		m_Name = trl ? process.get_Translation(MProcess.COLUMNNAME_Name) : process.getName();
		m_Description = trl ? process.get_Translation(MProcess.COLUMNNAME_Description) : process.getDescription();
		m_Help = trl ? process.get_Translation(MProcess.COLUMNNAME_Help) : process.getHelp();
		if((pi != null) && !Util.isEmpty(pi.getShowHelp()))
			m_ShowHelp = pi.getShowHelp();
		else
			m_ShowHelp = process.getShowHelp();

		// User Customization
		MUserDefProc userDef = MUserDefProc.getBestMatch(ctx, AD_Process_ID);
		if (userDef != null) {
			if (userDef.getName() != null)
				m_Name = userDef.getName();
			if (userDef.getDescription() != null)
				m_Description = userDef.getDescription();
			if (userDef.getHelp() != null)
				m_Help = userDef.getHelp();
		}

		buildMsg.append("<p><b>");
		buildMsg.append(Util.isEmpty(m_Description) ? Msg.getMsg(m_ctx, "StartProcess?") : m_Description);
		buildMsg.append("</b></p>");

		if (!Util.isEmpty(m_Help))
			buildMsg.append("<p>").append(m_Help).append("</p>");
		m_AD_Process_UU = process.getAD_Process_UU();
	
		initialMessage = buildMsg.toString();
		
		if (m_Name == null)
			return false;
		//
		this.setTitle(m_Name);

		if (m_pi == null) {
			m_pi = new WProcessInfo(m_Name, AD_Process_ID);
			// Set Replace Tab Content
			m_pi.setReplaceTabContent();
		}
		m_pi.setAD_User_ID (Env.getAD_User_ID(Env.getCtx()));
		m_pi.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));
		m_pi.setTitle(m_Name);
		m_pi.setAD_Process_UU(m_AD_Process_UU);
		
		parameterPanel = new ProcessParameterPanel(m_WindowNo, m_TabNo, m_pi);
		if ( !parameterPanel.init() ) {
			//auto start if no parameters and DonTShowHelp.
			if (m_ShowHelp != null && MProcess.SHOWHELP_DonTShowHelp.equals(m_ShowHelp))
				autoStart = true;
			
			if (autoStart)
			{
				layout();
				bOK.setDisabled(true);
				bCancel.setDisabled(true);
				autoStart();
				return true;
			}
		}

		// Check if the process is a silent one
		if (isValid() && m_ShowHelp != null && MProcess.SHOWHELP_RunSilently_TakeDefaults.equals(m_ShowHelp))
		{
			layout();
			bOK.setDisabled(true);
			bCancel.setDisabled(true);
			autoStart();
			return true;
		}
		
		layout();
		
		return true;
	}
	
	/** top part of {@link #mainParameterLayout} **/
	protected HtmlBasedComponent topParameterLayout;
	/** bottom part of {@link #mainParameterLayout} **/
	protected HtmlBasedComponent bottomParameterLayout;
	/** main content layout **/
	protected HtmlBasedComponent mainParameterLayout;
	protected WTableDirEditor fPrintFormat;
	private WEditor fLanguageType;
	protected Combobox freportType;
	private Checkbox chbIsSummary;
	/** ok button to run process/report **/
	protected Button bOK;
	/** cancel button to dismiss dialog **/
	protected Button bCancel;
	/** List of name/label for save process parameters **/
	protected Combobox fSavedName=new Combobox();
	/** button to save process parameters **/
	private Button bSave=ButtonFactory.createNamedButton("Save");
	/** button to delete saved process parameters **/
	private Button bDelete=ButtonFactory.createNamedButton("Delete");
	/** List of save parameters **/
	private List<MPInstance> savedParams;
	private Label lSaved;
	
	/**
	 * layout dialog
	 */
	protected void layout(){
		overalLayout();
		topLayout(topParameterLayout);
		bottomLayout(bottomParameterLayout);
		
	}
	
	/**
	 * Layout {@link #mainParameterLayout}, {@link #topParameterLayout} and {@link #bottomParameterLayout}.
	 */
	protected void overalLayout(){
		mainParameterLayout = new Div();
		mainParameterLayout.setSclass("main-parameter-layout"); 
		this.appendChild(mainParameterLayout);
		// header and input component
		topParameterLayout = new Vlayout();
		topParameterLayout.setSclass("top-parameter-layout"); 
		mainParameterLayout.appendChild(topParameterLayout);
		ZKUpdateUtil.setVflex(topParameterLayout, "true");
		// button and advanced control
		bottomParameterLayout = new Vlayout();
		bottomParameterLayout.setSclass("bottom-parameter-layout"); 
		mainParameterLayout.appendChild(bottomParameterLayout);		
	}
	
	/**
	 * Layout content of {@link #topParameterLayout} (process message and parameters)
	 * @param topParameterLayout
	 */
	protected void topLayout(HtmlBasedComponent topParameterLayout) {
		// message
		setHeadMessage (topParameterLayout, initialMessage);
		
		// input component
		HtmlBasedComponent inputParameterLayout = new Div();
		inputParameterLayout.setSclass("input-paramenter-layout");
		if (isLabelAboveInputForSmallWidth())
			LayoutUtils.addSclass("form-label-above-input", inputParameterLayout);
		topParameterLayout.appendChild(inputParameterLayout);
		
		// input parameter content
		inputParameterLayout(inputParameterLayout);
	}
	
	/**
	 * Create header message of {@link #topParameterLayout}
	 * @param parent
	 * @param contentMsg
	 * @return content component for contentMsg
	 */
	protected HtmlBasedComponent setHeadMessage (HtmlBasedComponent parent, String contentMsg){
		// message
		HtmlBasedComponent messageParameterLayout = new Vlayout();
		parent.appendChild(messageParameterLayout);
		messageParameterLayout.setSclass("message-parameter-layout");
		
		// header content
		HtmlBasedComponent messageDiv = new Div();
		Html content = new Html();
		if (contentMsg != null){
			content.setContent(contentMsg);
		}
		messageDiv.appendChild(content);
		messageDiv.setSclass("message-parameter");
		messageParameterLayout.appendChild(messageDiv);
		
		return content;
	}
	
	/**
	 * Layout parameter part of {@link #topParameterLayout}.
	 * {@link #parameterPanel}, {@link #runAsJobField} and {@link #notificationTypeField}.
	 * @param parent
	 */
	protected void inputParameterLayout (HtmlBasedComponent parent) {
		parent.appendChild(parameterPanel);
		
		if (MSysConfig.getBooleanValue(MSysConfig.BACKGROUND_JOB_ALLOWED, false))
		{
			Grid grid = GridFactory.newGridLayout();
			parent.appendChild(grid);

			Columns columns = new Columns();
			grid.appendChild(columns);
			Column col = new Column();
			ZKUpdateUtil.setWidth(col, "30%");
			columns.appendChild(col);
			col = new Column();
			ZKUpdateUtil.setWidth(col, "70%");
			columns.appendChild(col);
			
			Rows rows = new Rows();
			grid.appendChild(rows);
			
			Row row = new Row();
			rows.appendChild(row);
			
			row.appendChild(new Space());
			runAsJobField = new Checkbox();
			runAsJobField.setLabel(Msg.getElement(m_ctx, MPInstance.COLUMNNAME_IsRunAsJob));
			row.appendChild(runAsJobField);
			runAsJobField.addEventListener(Events.ON_CHECK, this);
			
			row = new Row();
			rows.appendChild(row);
			
			Div div = new Div();
	        div.setStyle("text-align: right;");
	        notificationTypeLabel = new Label(Msg.getElement(m_ctx, MPInstance.COLUMNNAME_NotificationType));
	        div.appendChild(notificationTypeLabel);
	        row.appendChild(div);	        
			
	        MLookupInfo info = MLookupFactory.getLookup_List(Env.getLanguage(m_ctx), MPInstance.NOTIFICATIONTYPE_AD_Reference_ID);
	        notificationTypeField = new WTableDirEditor(MPInstance.COLUMNNAME_NotificationType, true, false, true, new MLookup(info, 0));
	        Combobox combobox = notificationTypeField.getComponent();
			List<?> items = combobox.getItems();
			for (int i = 0; i < items.size(); i++) {
				ComboItem item = (ComboItem)items.get(i);
				if (MPInstance.NOTIFICATIONTYPE_None.equals(item.getValue()))
					combobox.removeItemAt(i);
			}
			
			MUser user = MUser.get(m_ctx);
			String notificationType = user.getNotificationType();
			if (!MPInstance.NOTIFICATIONTYPE_None.equals(notificationType))
				notificationTypeField.setValue(notificationType);

			row.appendChild(notificationTypeField.getComponent());
			runAsJobField.setChecked(MSysConfig.getBooleanValue(MSysConfig.BACKGROUND_JOB_BY_DEFAULT, false));
			
			//Check force background
			MProcess process = MProcess.get(Env.getCtx(), m_AD_Process_ID);
			if (process.isForceBackground()) {
				runAsJobField.setChecked(true);
				runAsJobField.setEnabled(false);
			} else if (process.isForceForeground()) {
				runAsJobField.setChecked(false);
				runAsJobField.setEnabled(false);
				runAsJobField.setVisible(false);
			}
			notificationTypeField.getComponent().getParent().setVisible(runAsJobField.isChecked());
			notificationTypeField.fillHorizontal();
		}
	}
	
	/**
	 * Layout content of {@link #bottomParameterLayout}.<br/>
	 * Report option, save parameter and action buttons.
	 * @param bottomParameterLayout
	 */
	protected void bottomLayout(HtmlBasedComponent bottomParameterLayout) {
		reportOptionLayout(bottomParameterLayout);
		
		HtmlBasedComponent bottomContainer = new Div ();
		bottomContainer.setSclass("bottom-container");
		bottomParameterLayout.appendChild(bottomContainer);
		
		MProcess process = MProcess.get(Env.getCtx(), m_AD_Process_ID);
	    int count = process.getParameters().length;
	    if (count > 0)
	    	savePrameterLayout (bottomContainer);
		
		buttonLayout (bottomContainer);
	}
	
	/**
	 * Render report option part of {@link #bottomParameterLayout} (output type, IsSummary and print format)
	 * @param bottomParameterLayout
	 */
	protected void reportOptionLayout(HtmlBasedComponent bottomParameterLayout) {
		if (!isReport() && !isJasperReport())
			return;//if not a report not need show this panel

		// option control
		Hlayout reportOptionLayout =  new Hlayout();
		reportOptionLayout.setSclass("report-option-container");
		reportOptionLayout.setValign("middle");
		if (!isLabelAboveInputForSmallWidth())
			bottomParameterLayout.appendChild(reportOptionLayout);

		//output type: html, pdf, etc
		Label lreportType = new Label(Msg.translate(Env.getCtx(), "view.report"));
		if (!isLabelAboveInputForSmallWidth())
			lreportType.setSclass("option-input-parameter view-report-label");
		freportType = new Combobox();
		freportType.setSclass("option-input-parameter view-report-list");
		if (isLabelAboveInputForSmallWidth()) {
			freportType.setHflex("1");
			freportType.setPlaceholder(lreportType.getValue());
		}
		if (isLabelAboveInputForSmallWidth()) {
			bottomParameterLayout.appendChild(freportType);
		} else {
			reportOptionLayout.appendChild(lreportType);
			reportOptionLayout.appendChild(freportType);
		}

		if (isJasperReport())
			listReportTypeJasper();
		
		if (!isReport())
			return;
		
		//summary option
		chbIsSummary = new Checkbox();
		chbIsSummary.setSclass("option-input-parameter");
		chbIsSummary.setLabel(Msg.translate(Env.getCtx(), "Summary"));
		Label lPrintFormat = new Label(Msg.translate(Env.getCtx(), "AD_PrintFormat_ID"));
		if (!isLabelAboveInputForSmallWidth())
			lPrintFormat.setSclass("option-input-parameter print-format-label");

		//print formats
		MClient client = MClient.get(m_ctx);
		listPrintFormat(client);

		if (isLabelAboveInputForSmallWidth()) {
			bottomParameterLayout.appendChild(fPrintFormat.getComponent());
			fPrintFormat.getComponent().setWidth(null);
			fPrintFormat.getComponent().setHflex("1");
		} else {
			reportOptionLayout.appendChild(lPrintFormat);
			reportOptionLayout.appendChild(fPrintFormat.getComponent());
		}
		//selection of language
		if (client.isMultiLingualDocument()){
			Label lLanguageType = new Label(Msg.translate(Env.getCtx(), MLanguage.COLUMNNAME_AD_Language_ID));
			if (isLabelAboveInputForSmallWidth()) {
				bottomParameterLayout.appendChild(fLanguageType.getComponent());
				((Combobox)fLanguageType.getComponent()).setPlaceholder(lLanguageType.getValue());
				((Combobox) fLanguageType.getComponent()).setWidth(null);
				((Combobox) fLanguageType.getComponent()).setHflex("1");
			} else {
				reportOptionLayout.appendChild(lLanguageType);
				reportOptionLayout.appendChild(fLanguageType.getComponent());
			}
			((Combobox)fLanguageType.getComponent()).setSclass("option-input-parameter");
		}
		fPrintFormat.getComponent().setSclass("option-input-parameter print-format-list");
		fPrintFormat.getComponent().setPlaceholder(lPrintFormat.getValue());
		if (isLabelAboveInputForSmallWidth())
			bottomParameterLayout.appendChild(chbIsSummary);
		else
			reportOptionLayout.appendChild(chbIsSummary);
	}

	/**
	 * Is current process with IsReport=Y AND JasperReport Is NULL.
	 * @return true if current process is with IsReport=Y AND JasperReport Is NULL.
	 */
	protected boolean isReport () {
		MProcess pr = MProcess.get(m_ctx, m_AD_Process_ID);
		return pr.isReport() && pr.getJasperReport() == null;
	}
	
	/**
	 * Is current process with IsReport=Y AND JasperReport Is Not NULL.
	 * @return true if current process is with IsReport=Y AND JasperReport Is Not NULL.
	 */
	protected boolean isJasperReport () {
		MProcess pr = MProcess.get(m_ctx, m_AD_Process_ID);
		return pr.isReport() && pr.getJasperReport() != null;
	}
	
	/**
	 * Layout UI to load/save process parameters
	 * @param bottomParameterLayout
	 */
	protected void savePrameterLayout(HtmlBasedComponent bottomParameterLayout) {
		Hlayout savePrameterLayout = new Hlayout();
		savePrameterLayout.setSclass("save-parameter-container");
		bottomParameterLayout.appendChild(savePrameterLayout);
		savePrameterLayout.setValign("middle");
		
		lSaved = new Label(Msg.getMsg(Env.getCtx(), "SavedParameter"));
		lSaved.setClass("saved-parameter-label");
		savePrameterLayout.appendChild(lSaved);
		fSavedName = new Combobox();
		fSavedName.addEventListener(Events.ON_CHANGE, this);
		savePrameterLayout.appendChild(fSavedName);
		fSavedName.setPlaceholder(lSaved.getValue());
		fSavedName.setSclass("saved-parameter-list");

		bSave.setEnabled(false);
		bSave.addActionListener(this);
		savePrameterLayout.appendChild(bSave);

		bDelete.setEnabled(false);
		bDelete.addActionListener(this);
		savePrameterLayout.appendChild(bDelete);

		LayoutUtils.addSclass("btn-small", bSave);
		LayoutUtils.addSclass("btn-small", bDelete);
		
		querySaved();
	}
	
	/**
	 * Load saved process parameters
	 */
	protected void querySaved() 
	{
		//user query
		savedParams = MPInstance.get(Env.getCtx(), getAD_Process_ID(), Env.getContextAsInt(Env.getCtx(), Env.AD_USER_ID));
		fSavedName.removeAllItems();
		for (MPInstance instance : savedParams)
		{
			String queries = instance.get_ValueAsString("Name");
			fSavedName.appendItem(queries);
		}

		fSavedName.setValue("");
	}
	
	/**
	 * Action buttons for dialog
	 * @param bottomParameterLayout
	 */
	protected void buttonLayout (HtmlBasedComponent bottomParameterLayout) {
		HtmlBasedComponent confParaPanel =new Div();
		confParaPanel.setSclass("button-container");
		bottomParameterLayout.appendChild(confParaPanel);
		
		// Invert - Unify  OK/Cancel IDEMPIERE-77
		bOK = ButtonFactory.createNamedButton(ConfirmPanel.A_OK, true, true);
		bOK.setId("Ok");
		bOK.addEventListener(Events.ON_CLICK, this);
		confParaPanel.appendChild(bOK);
		confParaPanel.appendChild(new Space());
		
		bCancel = ButtonFactory.createNamedButton(ConfirmPanel.A_CANCEL, true, true);
		bCancel.setId("Cancel");
		bCancel.addEventListener(Events.ON_CLICK, this);
		confParaPanel.appendChild(bCancel);
		
	}
	
	/**
	 * Fill {@link #fPrintFormat}
	 * @param client
	 */
	private void listPrintFormat(MClient client)
	{
		int AD_Column_ID = 0;
		boolean m_isCanExport = false; 
		
		MProcess pr = MProcess.get(m_ctx, m_AD_Process_ID);
		int table_ID = 0;
		try 
		{
			if (pr.getAD_ReportView_ID() > 0)
			{
				MReportView m_Reportview = MReportView.get(m_ctx, pr.getAD_ReportView_ID());
				table_ID = m_Reportview.getAD_Table_ID();
			}
			else if (pr.getAD_PrintFormat_ID() > 0)
			{
				MPrintFormat format = new MPrintFormat(m_ctx, pr.getAD_PrintFormat_ID(), null);
				table_ID = format.getAD_Table_ID();
			}
			StringBuilder valCode = new StringBuilder();
			if (table_ID > 0)
			{
				valCode.append("AD_PrintFormat.AD_Table_ID=").append(table_ID);
				m_isCanExport = MRole.getDefault().isCanExport(table_ID);
			}

			if (pr.getAD_ReportView_ID() > 0 && MSysConfig.getBooleanValue(MSysConfig.ZK_REPORT_ONLY_PRINTFORMAT_LINKEDTO_REPORTVIEW, false, client.getAD_Client_ID())) {
				if (valCode.length() > 0)
					valCode.append(" AND ");
				valCode.append("AD_PrintFormat.AD_ReportView_ID=").append(pr.getAD_ReportView_ID());
			}

			Lookup lookup = MLookupFactory.get (Env.getCtx(), m_WindowNo, 
					AD_Column_ID, DisplayType.TableDir,
					Env.getLanguage(Env.getCtx()), "AD_PrintFormat_ID", 0, false,
					valCode.toString());
			
			fPrintFormat = new WTableDirEditor("AD_PrintFormat_ID", false, false, true, lookup);
			
			if (client.isMultiLingualDocument()){
				fLanguageType = AEnv.getListDocumentLanguage(client);
			}
			
		} 
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.getLocalizedMessage());
		}

		fillReportType(m_isCanExport);
		
		setReportTypeAndPrintFormat(getLastRun());
	}
	
	/**
	 * Fill {@link #freportType} for Jasper Report.
	 */
	private void listReportTypeJasper()
	{
		boolean m_isCanExport = MRole.getDefault().isCanExport();
		fillReportType(m_isCanExport);

		setReportTypeAndPrintFormat(getLastRun());
	}
	
	/**
	 * @return Last run {@link MPInstance} record for current logged in user.
	 */
	protected MPInstance getLastRun() {
		final String where = "AD_Process_ID = ? AND AD_User_ID = ? AND Name IS NULL ";
		return new Query(Env.getCtx(), MPInstance.Table_Name, where, null)
				.setOnlyActiveRecords(true).setClient_ID()
				.setParameters(m_AD_Process_ID, Env.getContextAsInt(Env.getCtx(), Env.AD_USER_ID))
				.setOrderBy("Created DESC")
				.first();
	}

	/**
	 * Fill {@link #freportType}
	 * @param m_isCanExport true to include excel and csv.
	 */
	private void fillReportType(boolean m_isCanExport) {
		freportType.removeAllItems();
		freportType.appendItem("", "");
		freportType.appendItem("PDF", "PDF");
		freportType.appendItem("HTML", "HTML");
		
		if (m_isCanExport)
		{
			freportType.appendItem("XLS", "XLS");
			freportType.appendItem("CSV", "CSV");
			freportType.appendItem("XLSX", "XLSX");
		}
		freportType.setSelectedIndex(-1);
	}

	/**
	 * Set value for {@link #fPrintFormat}, {@link #fLanguageType}, {@link #freportType} and {@link #chbIsSummary} from instance.
	 * @param instance
	 */
	private void setReportTypeAndPrintFormat(MPInstance instance)
	{
		if (fPrintFormat != null && instance != null
			&& instance.getAD_Process_ID() != SystemIDs.PROCESS_RPT_FINREPORT) {
			fPrintFormat.setValue((Integer) instance.getAD_PrintFormat_ID());
		}
		
		if (fLanguageType != null && instance != null) {
			fLanguageType.setValue((Integer) instance.getAD_Language_ID());
		}
		
		if (freportType != null && instance != null) {
			if (instance.getReportType() == null)
				freportType.setSelectedIndex(-1);
			else 
				freportType.setValue(instance.getReportType());
		}
		
       if (instance != null && chbIsSummary != null)       
		    chbIsSummary.setSelected(instance.isSummary());
	}
	
	/**
	 * Update process info ({@link ProcessInfo}) with selected report options ({@link #freportType},
	 * {@link #fPrintFormat}, {@link #fLanguageType} and {@link #chbIsSummary}).
	 */
	protected void saveReportOption (){
		if (!isReport() && !isJasperReport()){
			return;
		}
		if(freportType.getSelectedItem() != null) {
			getProcessInfo().setReportType(freportType.getSelectedItem().getValue().toString());
		}
		if (!isReport()){
			return;
		}
		if(fPrintFormat != null && fPrintFormat.getValue() != null) {
			MPrintFormat format = new MPrintFormat(m_ctx, (Integer) fPrintFormat.getValue(), null);
			if (format != null) {
				getProcessInfo().setSerializableObject(format);
			}
		}
		
		getProcessInfo().setIsSummary(chbIsSummary.isChecked());
		if (fLanguageType != null && fLanguageType.getValue() != null)
			getProcessInfo().setLanguageID(fLanguageType.getValue() == null?0:(int)fLanguageType.getValue());
		else
			getProcessInfo().setLanguageID(MLanguage.get(getCtx(), Env.getLanguage(getCtx())).getAD_Language_ID());
	}
	
	/**
	 * Auto start process upon instantiation of process dialog.<br/>
	 * Delegate to {@link #startProcess0()}.
	 */
	protected void autoStart()
	{
		startProcess0();
	}
	
	@Override
	public void onEvent(Event event) 
	{
		Component component = event.getTarget();
		if (component == runAsJobField && event.getName().equals(Events.ON_CHECK))
		{
			notificationTypeField.getComponent().getParent().setVisible(runAsJobField.isChecked());
			mainParameterLayout.invalidate();

		}
		else if (event.getName().equals(ON_COMPLETE_EVENT))
			onComplete();
		else if (event.getName().equals(ON_STATUS_UPDATE_EVENT))
			onStatusUpdate(event);
		else if (event.getTarget().equals(bSave) || event.getTarget().equals(bDelete) || event.getTarget().equals(fSavedName)){
			String saveName = null;
			boolean lastRun = false;
			if (fSavedName.getRawText() != null) {
				saveName = fSavedName.getRawText();
				lastRun = ("** " + Msg.getMsg(Env.getCtx(), "LastRun") + " **")
						.equals(saveName);
			}
			if (bSave.equals(event.getTarget()))
				updateSaveParameter(saveName);
			else if (bDelete.equals(event.getTarget()))
				deleteSaveParameter(saveName);
			else
				chooseSaveParameter(saveName, lastRun);
		}else if (event.getTarget().equals(bOK)){
			if (isBackgroundJob() && getNotificationType() == null)
				throw new WrongValueException(notificationTypeField.getComponent(), Msg.getMsg(m_ctx, "FillMandatory") + notificationTypeLabel.getValue());
			saveReportOption();
		}
	}
	
	/**
	 * Save process parameters and report options.<br/>
	 * Set MPInstance.Name = saveName.
	 * @param saveName
	 */
	protected void updateSaveParameter(String saveName) {
		// Update existing
		if (fSavedName.getSelectedIndex() > -1 && savedParams != null) {
			for (int i = 0; i < savedParams.size(); i++) {
				if (savedParams.get(i).getName().equals(saveName)) {
					getProcessInfo().setAD_PInstance_ID(savedParams.get(i)
							.getAD_PInstance_ID());
					for (MPInstancePara para : savedParams.get(i)
							.getParameters()) {
						para.deleteEx(true);
					}
					getParameterPanel().saveParameters();
					
					saveReportOptionToInstance(savedParams.get(i));
					
					savedParams.get(i).saveEx();
					
					getProcessInfo().setAD_PInstance_ID(0);
				}
			}
		}
		// create new
		else {
			MPInstance instance = null;
			try {
				instance = new MPInstance(Env.getCtx(),
						getProcessInfo().getAD_Process_ID(), getProcessInfo().getTable_ID(), getProcessInfo().getRecord_ID(), getProcessInfo().getRecord_UU());
				instance.setName(saveName);
				saveReportOptionToInstance(instance);
				instance.saveEx();
				getProcessInfo().setAD_PInstance_ID(instance.getAD_PInstance_ID());
				// Get Parameters
				if (getParameterPanel() != null) {
					if (!getParameterPanel().saveParameters()) {
						throw new AdempiereSystemError(Msg.getMsg(
								Env.getCtx(), "SaveParameterError"));
					}
				}
			} catch (Exception ex) {
				log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			}
		}
		//reload fSavedName
		querySaved();
		fSavedName.setSelectedItem(getComboItem(saveName));
	}
	
	/**
	 * Save report options (output type, print format, language and IsSummary) to instance.
	 * @param instance {@link MPInstance}
	 */
	protected void saveReportOptionToInstance (MPInstance instance){
		if (!isReport() && !isJasperReport())
			return;

		if (freportType.getSelectedItem() != null)
			instance.setReportType(freportType.getSelectedItem().getValue().toString());

		if (!isReport())
			return;
		Object value = fPrintFormat.getValue();
		if (value == null){
			instance.setAD_PrintFormat_ID(0);
		}else{
			instance.setAD_PrintFormat_ID((Integer)value);
		}
		
		if (fLanguageType != null){
			value = fLanguageType.getValue();
			if (value == null){
				instance.setAD_Language_ID(0);
			}else{
				instance.setAD_Language_ID((Integer)value);
			}
		}
		
		instance.setIsSummary(chbIsSummary.isSelected());
	}
	
	/**
	 * Find {@link #fSavedName} item for value.
	 * @param value
	 * @return {@link Comboitem} found.
	 */
	public  Comboitem getComboItem( String value) {
		Comboitem item = null;
		for (int i = 0; i < fSavedName.getItems().size(); i++) {
			if (fSavedName.getItems().get(i) != null) {
				item = (Comboitem)fSavedName.getItems().get(i);
				if (value.equals(item.getLabel().toString())) {
					break;
				}
			}
		}
		return item;
	}

	/**
	 * Delete saved MPInstance by saveName.
	 * @param saveName
	 */
	protected void deleteSaveParameter(String saveName) {
		Object o = fSavedName.getSelectedItem();
		if (savedParams != null && o != null) {
			String selected = fSavedName.getSelectedItem().getLabel();
			for (int i = 0; i < savedParams.size(); i++) {
				if (savedParams.get(i).getName().equals(selected)) {
					savedParams.get(i).deleteEx(true);
				}
			}
		}
		querySaved();
	}

	/**
	 * Load MPInstance by saveName.
	 * @param saveName
	 * @param lastRun
	 */
	protected void chooseSaveParameter(String saveName, boolean lastRun) {
		if (savedParams != null && saveName != null) {
			for (int i = 0; i < savedParams.size(); i++) {
				if (savedParams.get(i).getName().equals(saveName)) {
					loadSavedParams(savedParams.get(i));
				}
			}
		}
		boolean enabled = !Util.isEmpty(saveName);
		bSave.setEnabled(enabled && !lastRun);
		bDelete.setEnabled(enabled && fSavedName.getSelectedIndex() > -1
				&& !lastRun);	
	}
	
	/**
	 * Load parameter values and report options from instance.
	 * @param instance {@link MPInstance}
	 */
	protected void loadSavedParams(MPInstance instance) {
		getParameterPanel().loadParameters(instance);
		setReportTypeAndPrintFormat(instance);
	}
	
	/**
	 * Run process.<pr/>
	 * Delegate to {@link #startProcess0()}.
	 */
	protected void startProcess()
	{
		if (!parameterPanel.validateParameters())
			return;

		if (m_pi.isProcessRunning(parameterPanel.getParameters())) {
			Dialog.error(getWindowNo(), "ProcessAlreadyRunning");
			log.log(Level.WARNING, "Abort process " + m_AD_Process_ID + " because it is already running");
			return;
		}

		startProcess0();
	}
	
	/**
	 * Cancel/dismiss process dialog.
	 */
	protected void cancelProcess() 
	{
		m_cancel = true;
		if(getParent() != null && getParent() instanceof HtmlBasedComponent)
			((HtmlBasedComponent)getParent()).focus();
		this.dispose();
	}
	
	/**
	 * Create new {@link #progressWindow}.
	 * @return {@link BusyDialog}
	 */
	protected BusyDialog createBusyDialog() 
	{
		progressWindow = new BusyDialog();
		this.appendChild(progressWindow);
		return progressWindow;
	}
	
	/**
	 * Close {@link #progressWindow}.
	 */
	protected void closeBusyDialog() 
	{
		if (progressWindow != null) {
			progressWindow.dispose();
			progressWindow = null;
		}
	}
	
	@Override
	public void dispose()
	{
		m_valid = false;
	}	//	dispose
	
	/**
	 * Run process.
	 */
	private void startProcess0()
	{		
		if (!isBackgroundJob())
			getProcessInfo().setPrintPreview(true);

		lockUI(getProcessInfo());
		
		downloadFiles = new ArrayList<File>();

		//use echo, otherwise lock ui wouldn't work
		Clients.response(new AuEcho(this, isBackgroundJob() ? "runBackgroundJob" : "runProcess", this));
	}
	
	/**
	 * Run process. Echo event from {@link #startProcess0()}.
	 */
	public void runProcess() 
	{
		Events.sendEvent(DialogEvents.ON_BEFORE_RUN_PROCESS, this, null);
		future = Adempiere.getThreadPoolExecutor().submit(new DesktopRunnable(new ProcessDialogRunnable(null), getDesktop()));
	}

	/**
	 * Run process as background job (runBackgroundJob event echo from {@link #startProcess0()}).
	 * <br/>
	 * The different with {@link #runProcess()} is this method doesn't wait for completion of process.
	 */
	public void runBackgroundJob() 
	{
		Properties context = getCtx();
		ProcessInfo processInfo = getProcessInfo();
		
		Callback<Integer> createInstanceParaCallback = id -> {
			if (id > 0)
				getParameterPanel().saveParameters();
		};
		
		try 
		{
			BackgroundJob.create(processInfo)
				.withContext(context)
				.withNotificationType(getNotificationType())
				.withProcessUI(this)
				.withInitialDelay(1000)
				.run(createInstanceParaCallback);			
		}
		finally 
		{
			unlockUI(processInfo);
			
			if (m_disposeOnComplete)
				dispose();
		}
	}
	
	/**
	 * Handle {@link #ON_COMPLETE_EVENT}
	 */
	private void onComplete()
	{
		ProcessInfo m_pi = getProcessInfo();
		
		if (future != null) {
			try {
				future.get();
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				if (!m_pi.isError()) {
					m_pi.setSummary(e.getLocalizedMessage(), true);
				}
			}
		}
		future = null;
		unlockUI(m_pi);
		if (downloadFiles.size() > 0) {
			MultiFileDownloadDialog downloadDialog = new MultiFileDownloadDialog(downloadFiles.toArray(new File[0]));
			downloadDialog.setPage(getPage());
			downloadDialog.setTitle(m_pi.getTitle());
			Events.postEvent(downloadDialog, new Event(MultiFileDownloadDialog.ON_SHOW));
		}
		
		if (m_disposeOnComplete)
			dispose();
	}
	
	/**
	 * Handle {@link #ON_STATUS_UPDATE_EVENT}
	 * @param event
	 */
	private void onStatusUpdate(Event event) 
	{
		String message = (String) event.getData();
		if (progressWindow != null)
			progressWindow.statusUpdate(message);
	}

	/**
	 * Lock UI by showing of busy dialog ({@link #progressWindow}).
	 */
	@Override
	public void lockUI(ProcessInfo pi) {
		if (m_locked || Executions.getCurrent() == null) 
			return;
		m_locked = true;
		showBusyDialog();
	}
	
	/**
	 * Show process in progress dialog.
	 */
	public abstract void showBusyDialog();

	/**
	 * Unlock dialog upon completion of process (or upon submission of job if process is running as background job).
	 */
	@Override
	public void unlockUI(ProcessInfo pi) {
		if (!m_locked) 
			return;
		m_locked = false;
		
		if (Executions.getCurrent() == null) 
		{
			if (getDesktop() != null) 
			{
				Executions.schedule(getDesktop(), new EventListener<Event>() 
				{
					@Override
					public void onEvent(Event event) throws Exception {
						doUnlockUI();
					}
				}, new Event("onUnLockUI"));
			}
		} else {
			doUnlockUI();
		}
	}
	
	/**
	 * Close process in progress dialog and update UI with the result of process execution. 
	 */
	private void doUnlockUI()
	{
		hideBusyDialog();
		updateUI();		
	}
	
	/**
	 * Close process in progress dialog.
	 */
	public abstract void hideBusyDialog();
	
	/**
	 * Update UI with the result of process execution.
	 */
	public abstract void updateUI();

	@Override
	public boolean isUILocked() {
		return m_locked;
	}

	@Override
	public void statusUpdate(String message) {
		Desktop desktop = getDesktop();
		if (desktop != null && desktop.isAlive())
			Executions.schedule(desktop, this, new Event(ON_STATUS_UPDATE_EVENT, this, message));
	}

	@Override
	public void ask(final String message, final Callback<Boolean> callback) {
		Executions.schedule(getDesktop(), new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Dialog.ask(getWindowNo(), message, callback);
			}
		}, new Event("onAsk"));
	}

	@Override
	public void download(File file) {
		downloadFiles.add(file);
	}

	/**
	 * @return {@link ProcessInfo}
	 */
	public ProcessInfo getProcessInfo() {
		return m_pi;
	}
	
	/**
	 * @param pi
	 */
	public void setProcessInfo(ProcessInfo pi) {
		m_pi = pi;
	}

	/**
	 * @return true if dialog is still valid (i.e not completed and not cancel).
	 */
	public boolean isValid()
	{
		return m_valid;
	}
	
	/**
	 * @return true if user have press the cancel button to close the dialog
	 */
	public boolean isCancel()
	{
		return m_cancel;
	}
	
	/**
	 * @return cache environment context reference
	 */
	public Properties getCtx()
	{
		return m_ctx;
	}

	/**
	 * @return register window number.
	 */
	public int getWindowNo()
	{
		return m_WindowNo;
	}
	
	/**
	 * @return AD_Process_ID
	 */
	public int getAD_Process_ID()
	{
		return m_AD_Process_ID;
	}
		
	/**
	 * @return {@link ProcessParameterPanel} instance
	 */
	public ProcessParameterPanel getParameterPanel()
	{
		return parameterPanel;
	}
	
	/**
	 * @return translated process name
	 */
	public String getName()
	{
		return m_Name;
	}

	/**
	 * @return DonTShowHelp, ShowHelp or Silent.
	 */
	public String getShowHelp()
	{
		return m_ShowHelp;
	}

	/**
	 * @return initial panel header message
	 */
	public String getInitialMessage()
	{
		return initialMessage;
	}
	
	/**
	 * @return true if run process as background job.
	 */
	public boolean isBackgroundJob()
	{
		return runAsJobField != null && runAsJobField.isChecked();
	}
	
	/**
	 * @return Notification type - None, Email, Notice or Email+Notice.
	 */
	public String getNotificationType()
	{
		return (String) notificationTypeField.getValue();
	}
	
	/**
	 * @return list of files for user download
	 */
	public List<File> getDownloadFiles()
	{
		return downloadFiles;
	}
	
	/**
	 * Runnable to run process in background thread.<br/>
	 * Notify process dialog with {@link AbstractProcessDialog#ON_COMPLETE_EVENT} event. 
	 */
	private class ProcessDialogRunnable extends ZkContextRunnable
	{
		private Trx m_trx;
		
		/**
		 * @param trx
		 */
		private ProcessDialogRunnable(Trx trx) 
		{
			super();			
			m_trx = trx;
		}
		
		protected void doRun() 
		{
			ProcessInfo m_pi = getProcessInfo();
			try {
				if (log.isLoggable(Level.INFO))
					log.log(Level.INFO, "Process Info=" + m_pi + " AD_Client_ID="+ Env.getAD_Client_ID(Env.getCtx()));
				WProcessCtl.process(AbstractProcessDialog.this, getWindowNo(), getParameterPanel(), m_pi, m_trx);
			} catch (Exception ex) {
				m_pi.setError(true);
				m_pi.setSummary(ex.getLocalizedMessage());
				log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			} finally {
				Executions.schedule(getDesktop(), AbstractProcessDialog.this, new Event(ON_COMPLETE_EVENT, AbstractProcessDialog.this, null));
			}		
		}
	}
	
	@Override
	public void askForSecretInput(final String message, final Callback<String> callback) {
		Executions.schedule(getDesktop(), new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Dialog.askForSecretInput(m_WindowNo, message, callback);
			}
		}, new Event("onAskForInput"));
	}

	@Override
	public void askForInput(final String message, final Callback<String> callback) {
		Executions.schedule(getDesktop(), new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Dialog.askForInput(m_WindowNo, message, callback);
			}
		}, new Event("onAskForInput"));
	}

	@Override
	public void askForInput(final String message, MLookup lookup, int editorType, final Callback<Object> callback) {
		Dialog.askForInput(message, lookup, editorType, callback, getDesktop(), m_WindowNo);
	}

	/**
	 * Merge pdfList and show with {@link SimplePDFViewer}.
	 */
	@Override
	public void showReports(List<File> pdfList) {

		if (pdfList == null || pdfList.isEmpty())
			return;

		AEnv.executeAsyncDesktopTask(new Runnable() {
			@Override
			public void run() {
				if (pdfList.size() > 1) {
					try {
						File outFile = File.createTempFile(m_Name, ".pdf");
						AEnv.mergePdf(pdfList, outFile);
						Window win = new SimplePDFViewer(m_Name, new FileInputStream(outFile));
						win.setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
						SessionManager.getAppDesktop().showWindow(win, "center");
					} catch (Exception e) {
						log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				} else if (pdfList.size() > 0) {
					try {
						Window win = new SimplePDFViewer(m_Name, new FileInputStream(pdfList.get(0)));
						win.setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
						SessionManager.getAppDesktop().showWindow(win, "center");
					} catch (Exception e) {
						log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			}
		});
	}

	@Override
	public void showInfoWindow(int WindowNo, String tableName, String keyColumn, String queryValue,
			boolean multipleSelection, String whereClause, Integer AD_InfoWindow_ID, boolean lookup) {

		if (AD_InfoWindow_ID <= 0)
			return;

		AEnv.executeAsyncDesktopTask(new Runnable() {
			@Override
			public void run() {
				try {
					Window win = new InfoWindow(WindowNo, tableName, keyColumn, queryValue, multipleSelection,
							whereClause, AD_InfoWindow_ID, lookup);

					SessionManager.getAppDesktop().showWindow(win, "center");
				} catch (Exception e) {
					log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}

			}
		});
	}

	@Override
	public void focus() {
		super.focus();
		if (getParameterPanel() != null) {
			if (getParameterPanel().focusToFirstEditor())
				return;
		}
		if (bOK != null)
			bOK.focus();
	}		
}
