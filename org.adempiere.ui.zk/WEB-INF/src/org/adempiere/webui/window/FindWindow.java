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

package org.adempiere.webui.window;

import static org.adempiere.webui.LayoutUtils.isLabelAboveInputForSmallWidth;
import static org.compiere.model.SystemIDs.REFERENCE_YESNO;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.adempiere.exceptions.DBException;
import org.adempiere.util.Callback;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.AbstractADWindowContent;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.ComboItem;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.DatetimeBox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Group;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListCell;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.ToolBar;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WPaymentEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.part.MultiTabPart;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.Icon;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.Lookup;
import org.compiere.model.MAttribute;
import org.compiere.model.MAttributeValue;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MProduct;
import org.compiere.model.MQuery;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MUserQuery;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.SystemIDs;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.util.ValueNamePair;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.North;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Separator;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Vlayout;

/**
 *  Find/Search Records dialog.
 *
 *  @author     Sendy Yagambrum
 *  @date       June 27, 2007
 */
public class FindWindow extends Window implements EventListener<Event>, ValueChangeListener, DialogEvents
{
	/**
	 * generated serial id
	 */
	protected static final long serialVersionUID = -5087378621976257241L;

	protected static final String FIND_ROW_EDITOR = "find.row.editor";

	protected static final String FIND_ROW_EDITOR_TO = "find.row.editor.to";

	// values and label for history combo (historyCombo).
	protected static final String HISTORY_DAY_ALL = "All";
	protected static final String HISTORY_DAY_YEAR = "Year";
	protected static final String HISTORY_DAY_MONTH = "Month";
	protected static final String HISTORY_DAY_WEEK = "Week";
	protected static final String HISTORY_DAY_DAY = "Day";	
	protected ValueNamePair[] historyItems = new ValueNamePair[] {
			new ValueNamePair("",                " "),
			new ValueNamePair(HISTORY_DAY_ALL,   Msg.getMsg(Env.getCtx(), HISTORY_DAY_ALL)),
			new ValueNamePair(HISTORY_DAY_YEAR,  Msg.getMsg(Env.getCtx(), HISTORY_DAY_YEAR)),
			new ValueNamePair(HISTORY_DAY_MONTH, Msg.getMsg(Env.getCtx(), HISTORY_DAY_MONTH)),
			new ValueNamePair(HISTORY_DAY_WEEK,  Msg.getMsg(Env.getCtx(), HISTORY_DAY_WEEK)),
			new ValueNamePair(HISTORY_DAY_DAY,   Msg.getMsg(Env.getCtx(), HISTORY_DAY_DAY))
	};
	protected static final String HISTORY_LABEL= "History";
	/** Tabbox for window. Center of find window.   */
    protected MultiTabPart winMain;
    /**  Simple search Tab  */
    protected Window winLookupRecord;
    /** Advanced search Tab */
    protected Window winAdvanced;
    /** Drop down for user query names */
    protected Combobox fQueryName;
    /** Center of {@link #winAdvanced}. List of search criterias. */
    protected Listbox advancedPanel;
    /** Center of {@link #winLookupRecord}. Grid layout of search fields. */
    protected Grid contentSimple;
    /** Target Window No            */
    protected int             m_targetWindowNo;
    /** Target Tab No            */
    protected int             m_targetTabNo;
    /** Table ID                    */
    protected int             m_AD_Table_ID;
    /** Table Name                  */
    protected String          m_tableName;
    /** Where                       */
    protected String          m_whereExtended;
    /** Search fields of calling tab ({@link #m_AD_Tab_ID}) */
    protected GridField[]     m_findFields;
    /** Grid tab for current row of {@link #advancedPanel} */
	protected GridTab m_gridTab = null;
    /** Resulting query             */
    protected MQuery          m_query = null;
    /** Is cancel ?                 */
    protected boolean         m_isCancel = false; // teo_sarca [ 1708717 ]
    /** Logger          */
    protected static final CLogger log = CLogger.getCLogger(FindWindow.class);
    /** Number of records           */
    protected int             m_total;
    /** Set to true if query timeout to get record count  */
    protected boolean         initialSlowQuery = false;
    /** target tab ({@link #m_AD_Tab_ID}) and its child tabs */
    protected MTab[] m_tabs;
    
    /** List of WEditors for simple search tab */
    protected ArrayList<WEditor>          m_sEditors = new ArrayList<WEditor>();
    /** List of button to toggle visibility of to editor. For date and numeric field in simple search tab. */
    protected ArrayList<ToolBarButton>    m_sEditorsFlag = new ArrayList<ToolBarButton>();
    /** List of to editor. For date and numeric field in simple search tab. */
    protected ArrayList<WEditor>          m_sEditorsTo = new ArrayList<WEditor>();
    /** Tab number for lookup and context */
    public static final int     TABNO = 99;
    /** Max display length of Fields on simple search tab   */
    public static final int     FIELDLENGTH = 20;
    /** AD_Tab_ID of calling tab */
    protected int m_AD_Tab_ID = 0;
    /** AD_Tab_UU of calling tab */
    protected String m_AD_Tab_UU = null;
    /** User queries for target tab ({@link #m_AD_Tab_ID}) */
	protected MUserQuery[] userQueries;
	/** Rows of {@link #contentSimple} */
	protected Rows contentSimpleRows;
	/** true if user click the new record button at simple search tab */
	protected boolean m_createNew = false;
	protected boolean isvalid = true;
	protected int m_minRecords;
	protected String m_title;
	/** Button to save current user query */
	protected ToolBarButton btnSave;
	/** Elements to show advanced options for saved queries */
	protected ToolBarButton btnMoreOptions;
	protected Popup popupOptions;
	protected Checkbox chkShare;
	protected Checkbox chkSaveDefault;

	//Column index for advance search listbox (advancedPanel)	
	/** Index ColumnName = 0		*/
	public static final int		INDEX_COLUMNNAME = 0;
	/** Index Operator = 1			*/
	public static final int		INDEX_OPERATOR = 1;
	/** Index Value = 2				*/
	public static final int		INDEX_VALUE = 2;
	/** Index Value2 = 3			*/
	public static final int		INDEX_VALUE2 = 3;
	
	/** Index AndOr = 4		*/
	public static final int		INDEX_ANDOR = 4;
	/** Index LeftBracket = 5		*/
	public static final int		INDEX_LEFTBRACKET = 5;
	/** Index RightBracket = 6		*/
	public static final int		INDEX_RIGHTBRACKET = 6;
	/** Index History		*/
	public static final int		INDEX_HISTORY = 1;
	/** Index Table		*/
	public static final int		INDEX_TABLE = 7;
	
	/** Search messages using translation */
	protected String				m_sNew;	

	//for user query code
	protected static final String FIELD_SEPARATOR = "<^>";
	protected static final String SEGMENT_SEPARATOR = "<~>";
	protected static final String HISTORY_SEPARATOR = "<#>";
	
	/** List of history scope options (all, year, month, etc) */
	protected Combobox historyCombo = new Combobox();
    /** Context for simple search tab */
	protected Properties m_simpleCtx;
	/** Context for advance search tab */
	protected Properties m_advanceCtx;
	/** Row count of {@link #advancedPanel} */
	protected int rowCount;
	
	protected static final String ON_POST_VISIBLE_ATTR = "onPostVisible.Event.Posted";

	/** {@link #m_total} is set to this constant if get record count hit query timeout exception */
	protected static final int COUNTING_RECORDS_TIMED_OUT = -255;

	/** Status bar. Bottom of find window */
	protected StatusBarPanel statusBar = new StatusBarPanel();
	
    /** IDEMPIERE-2836  User Query Where */
    protected String          m_whereUserQuery;
    /** Toolbar for advance search tab. North of {@link #winAdvanced}. */
    protected ToolBar advancedPanelToolBar;
    
    /**IDEMPIERE-4085*/
    protected int m_AD_UserQuery_ID = 0;    
    /** ADWindow Content part that own this find window instance */
	protected AbstractADWindowContent m_windowPanel;

	/** Column name attribute set instance */
	protected static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
	
	/* SysConfig USE_ESC_FOR_TAB_CLOSING */
	protected boolean isUseEscForTabClosing = MSysConfig.getBooleanValue(MSysConfig.USE_ESC_FOR_TAB_CLOSING, false, Env.getAD_Client_ID(Env.getCtx()));

    /**
     * FindWindow Constructor
     * @param targetWindowNo targetWindowNo
     * @param targetTabNo
     * @param title title
     * @param AD_Table_ID AD_Table_ID
     * @param tableName tableName
     * @param whereExtended whereExtended
     * @param findFields findFields
     * @param minRecords minRecords
     * @param adTabId
     */
 	public FindWindow (int targetWindowNo, int targetTabNo, String title,
            int AD_Table_ID, String tableName, String whereExtended,
            GridField[] findFields, int minRecords, int adTabId)
 	{
    	this(targetWindowNo, targetTabNo, title, AD_Table_ID, tableName, whereExtended, findFields, minRecords, adTabId, null);
    }

    /**
     * FindWindow Constructor
     * @param targetWindowNo targetWindowNo
     * @param targetTabNo
     * @param title title
     * @param AD_Table_ID AD_Table_ID
     * @param tableName tableName
     * @param whereExtended whereExtended
     * @param findFields findFields
     * @param minRecords minRecords
     * @param adTabId
     * @param windowPanel AbstractADWindowContent
     */
    public FindWindow (int targetWindowNo, int targetTabNo, String title,
            int AD_Table_ID, String tableName, String whereExtended,
            GridField[] findFields, int minRecords, int adTabId, AbstractADWindowContent windowPanel)
    {
        m_targetWindowNo = targetWindowNo;
        m_targetTabNo = targetTabNo;
        m_title = title;
        m_AD_Table_ID = AD_Table_ID;
        m_tableName = tableName;
        m_whereExtended = whereExtended;
        m_findFields = findFields;
        if (findFields != null && findFields.length > 0)
        	m_gridTab = findFields[0].getGridTab();
        m_sNew = "** ".concat(Msg.getMsg(Env.getCtx(), "New Query")).concat(" **");		
        m_AD_Tab_ID = adTabId;
        m_AD_Tab_UU = MTab.get(adTabId).getAD_Tab_UU();
        m_minRecords = minRecords;
        m_isCancel = true;
        m_windowPanel = windowPanel;
        //
        m_simpleCtx = new Properties(Env.getCtx());
        m_advanceCtx = new Properties(Env.getCtx());
        
        this.setBorder("normal");
        this.setShadow(false);
        ZKUpdateUtil.setWidth(this, "900px");
        ZKUpdateUtil.setHeight(this, "350px");
        this.setTitle(Msg.getMsg(Env.getCtx(), "Find").replace("&", "") + ": " + title);
        this.setClosable(false);
        this.setSizable(true);  
        this.setMaximizable(false);
        
        this.setClientAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "findWindow");
        this.setId("findWindow_"+targetWindowNo+"_"+targetTabNo);
        LayoutUtils.addSclass("find-window", this);
        
        addEventListener(Events.ON_CANCEL, e -> onCancel());
        setFireWindowCloseEventOnDetach(false);
    }
    
    /**
     * Initialize find window
     * @return true if init ok
     */
    public boolean initialize() 
    {
    	m_query = new MQuery (m_tableName);
        m_query.addRestriction(m_whereExtended);
        //  Required for Column Validation
        Env.setContext(Env.getCtx(), m_targetWindowNo, "Find_Table_ID", m_AD_Table_ID);
        //
        initPanel();
        initFind();
        initFindAdvanced();

        if (m_total != COUNTING_RECORDS_TIMED_OUT && m_total < m_minRecords)
        {
            return false;
        }
                
        if (!winMain.getComponent().getTabpanel(0).isVisible())
        {
        	winMain.getComponent().setSelectedIndex(1);
        	onAdvanceTabSelected();
        }
        
        return true;
    }
    
    /**
     * @param targetWindowNo
     * @param title
     * @param AD_Table_ID
     * @param tableName
     * @param whereExtended
     * @param findFields
     * @param minRecords
     * @param adTabId
     * @return false if this find window instance doesn't match one of the input parameters 
     */
    public boolean validate(int targetWindowNo, String title,
            int AD_Table_ID, String tableName, String whereExtended,
            GridField[] findFields, int minRecords, int adTabId)
    {
    	if (m_targetWindowNo != targetWindowNo) return false;
    	if ((title == null && m_title != null) || (title != null && m_title == null) || !(title.equals(m_title))) return false;
    	if (AD_Table_ID != m_AD_Table_ID) return false;
    	if ((tableName == null && m_tableName != null) || (tableName != null && m_tableName == null) || !(tableName.equals(m_tableName))) return false;
    	if (whereExtended.contains("@"))
    		whereExtended = Env.parseContext(Env.getCtx(), targetWindowNo, whereExtended, false);
    	if (m_whereExtended.contains("@"))
    		m_whereExtended = Env.parseContext(Env.getCtx(), targetWindowNo, whereExtended, false);
    	if ((whereExtended == null && m_whereExtended != null) || (whereExtended != null && m_whereExtended == null) || !(whereExtended.equals(m_whereExtended))) return false;
    	if (adTabId != m_AD_Tab_ID) return false;
    	if ((findFields == null && m_findFields != null) || (findFields != null && m_findFields == null) || (findFields.length != m_findFields.length)) return false;
    	if (findFields != null && findFields.length > 0) 
    	{
    		for(int i = 0; i < findFields.length; i++)
    		{
    			if (m_findFields[i] != null && findFields[i].getAD_Field_ID() != m_findFields[i].getAD_Field_ID()) return false;
    		}
    	}
    	
    	m_minRecords = minRecords;
    	m_total = getNoOfRecords(null, false);
    	if (m_total != COUNTING_RECORDS_TIMED_OUT && m_total < m_minRecords)
        {
            return false;
        }
    	
    	m_query = new MQuery (m_tableName);
        m_query.addRestriction(m_whereExtended);
        
    	return true;
    }
    
    /**
     * Layout simple search tab ({@link #winLookupRecord}).
     */
    protected void initSimple()
    {
        Button btnNew = ButtonFactory.createNamedButton(ConfirmPanel.A_NEW);
        btnNew.setId("btnNew");
        btnNew.addEventListener(Events.ON_CLICK,this);

        Button btnClear = ButtonFactory.createNamedButton(ConfirmPanel.A_RESET);
        btnClear.setId("btnReset");
        btnClear.addEventListener(Events.ON_CLICK,this);

        Button btnOk = ButtonFactory.createNamedButton(ConfirmPanel.A_OK);
        btnOk.setName("btnOkSimple");
        btnOk.setId("btnOk");
        btnOk.addEventListener(Events.ON_CLICK,this);

        Button btnCancel = ButtonFactory.createNamedButton(ConfirmPanel.A_CANCEL);
        btnCancel.setId("btnCancel");
        btnCancel.addEventListener(Events.ON_CLICK,this);

        Panel pnlButtonRight = new Panel();
        pnlButtonRight.appendChild(btnOk);
        Separator sep = new Separator("vertical");
        sep.setWidth("2px");
        pnlButtonRight.appendChild(sep);
        pnlButtonRight.appendChild(btnCancel);
        pnlButtonRight.setStyle("text-align:right");
        ZKUpdateUtil.setWidth(pnlButtonRight, "100%");
        ZKUpdateUtil.setHflex(pnlButtonRight, "1");

        Panel pnlButtonLeft = new Panel();
        pnlButtonLeft.appendChild(btnNew);
        sep = new Separator("vertical");
        sep.setWidth("2px");
        pnlButtonLeft.appendChild(sep);
        pnlButtonLeft.appendChild(btnClear);
        ZKUpdateUtil.setHflex(pnlButtonLeft, "1");

        Hbox hboxButton = new Hbox();
        hboxButton.appendChild(pnlButtonLeft);
        hboxButton.appendChild(pnlButtonRight);

        contentSimple = new Grid();
        contentSimple.setId("contentSimple");
        contentSimple.makeNoStrip();
        
        Columns columns = new Columns();
        Column column = new Column();
        if (isLabelAboveInputForSmallWidth()) {
            ZKUpdateUtil.setWidth(column, "100%");
        } else {
            column.setAlign("right");
            ZKUpdateUtil.setWidth(column, "30%");
        }
        columns.appendChild(column);

        if (!isLabelAboveInputForSmallWidth()) {
            column = new Column();
            column.setAlign("left");
            ZKUpdateUtil.setWidth(column, "50%");
            columns.appendChild(column);

            column = new Column();
            ZKUpdateUtil.setWidth(column, "20%");
            columns.appendChild(column);
        } else {
            contentSimple.setSclass("form-label-above-input");
        }

        contentSimple.appendChild(columns);

        contentSimpleRows = new Rows();
        contentSimple.appendChild(contentSimpleRows);
        ZKUpdateUtil.setVflex(contentSimple, true);

        Borderlayout layout = new Borderlayout();
        ZKUpdateUtil.setHflex(layout, "1");
        ZKUpdateUtil.setVflex(layout, "1");
        winLookupRecord.appendChild(layout);

        Center center = new Center();
        layout.appendChild(center);
        center.appendChild(contentSimple);
        ZKUpdateUtil.setVflex(contentSimple, "1");

        South south = new South();
        layout.appendChild(south);
        south.appendChild(hboxButton);

        ZKUpdateUtil.setWidth(winLookupRecord, "100%");
        ZKUpdateUtil.setHeight(winLookupRecord, "100%");
        winLookupRecord.addEventListener(Events.ON_OK, this);
        LayoutUtils.addSclass("find-window-simple", winLookupRecord);
    }   //  initSimple

    /**
     * Layout advance search tab ({@link #winAdvanced}). 
     */
    protected void initAdvanced()
    {
        ToolBarButton btnNew = new ToolBarButton();
        if (ThemeManager.isUseFontIconForImage())
        	btnNew.setIconSclass(Icon.getIconSclass(Icon.NEW));
        else
        	btnNew.setImage(ThemeManager.getThemeResource("images/New24.png"));
        btnNew.setAttribute("name", "btnNewAdv");
        btnNew.addEventListener(Events.ON_CLICK, this);

        ToolBarButton btnDelete = new ToolBarButton();
        btnDelete.setAttribute("name","btnDeleteAdv");
        if (ThemeManager.isUseFontIconForImage())
        	btnDelete.setIconSclass(Icon.getIconSclass(Icon.DELETE));
        else
        	btnDelete.setImage(ThemeManager.getThemeResource("images/Delete24.png"));
        btnDelete.addEventListener(Events.ON_CLICK, this);

        Button btnOk = ButtonFactory.createNamedButton(ConfirmPanel.A_OK);
        btnOk.setName("btnOkAdv");
        btnOk.addEventListener(Events.ON_CLICK, this);

        Button btnCancel = ButtonFactory.createNamedButton(ConfirmPanel.A_CANCEL);
        btnCancel.addEventListener(Events.ON_CLICK, this);

        Panel pnlButtonRight = new Panel();
        pnlButtonRight.appendChild(btnOk);
        Separator sep = new Separator("vertical");
        sep.setWidth("2px");
        pnlButtonRight.appendChild(sep);
        pnlButtonRight.appendChild(btnCancel);
        pnlButtonRight.setStyle("text-align: right");

        advancedPanelToolBar = new ToolBar();
        advancedPanelToolBar.appendChild(btnNew);
        advancedPanelToolBar.appendChild(btnDelete);

        fQueryName.addEventListener(Events.ON_SELECT, this);

        Hbox confirmPanel = new Hbox();
        confirmPanel.appendChild(pnlButtonRight);
        confirmPanel.setPack("end");

        advancedPanel = new Listbox();
        ListHead listhead = new ListHead();
        listhead.setSizable(true);

        ListHeader lstHAndOr = new ListHeader();
        lstHAndOr.setLabel(Msg.getMsg(Env.getCtx(), "And/Or"));
        ZKUpdateUtil.setWidth(lstHAndOr, "60px");

        ListHeader lstHLeftBracket = new ListHeader();
        lstHLeftBracket.setLabel("(");
        lstHLeftBracket.setAlign("center");
        ZKUpdateUtil.setWidth(lstHLeftBracket, "50px");

        ListHeader lstHTable = new ListHeader();
		lstHTable.setLabel(Msg.translate(Env.getCtx(), "AD_Tab_ID"));
		ZKUpdateUtil.setWidth(lstHTable, "12%");
		
        ListHeader lstHColumn = new ListHeader();
        lstHColumn.setLabel(Msg.translate(Env.getCtx(), "AD_Field_ID"));
        ZKUpdateUtil.setWidth(lstHColumn, "18%");

        ListHeader lstHOperator = new ListHeader();
        lstHOperator.setLabel(Msg.getMsg(Env.getCtx(), "Operator"));
        ZKUpdateUtil.setWidth(lstHOperator, "70px");

        ListHeader lstHQueryValue = new ListHeader();
        lstHQueryValue.setLabel(Msg.getMsg(Env.getCtx(), "QueryValue"));
        ZKUpdateUtil.setWidth(lstHQueryValue, "24%");

        ListHeader lstHQueryTo = new ListHeader();
        lstHQueryTo.setLabel(Msg.getMsg(Env.getCtx(), "QueryValue2"));
        ZKUpdateUtil.setWidth(lstHQueryTo, "20%");

        ListHeader lstHRightBracket = new ListHeader();
        lstHRightBracket.setLabel(")");
        lstHRightBracket.setAlign("center");
        ZKUpdateUtil.setWidth(lstHRightBracket, "50px");
        
        if (ClientInfo.maxWidth(ClientInfo.SMALL_WIDTH-1)) {        	
        	ZKUpdateUtil.setWidth(lstHColumn, "200px");
        	ZKUpdateUtil.setWidth(lstHQueryValue, "200px");
        	ZKUpdateUtil.setWidth(lstHQueryTo, "200px");
        }

        listhead.appendChild(lstHAndOr);
        listhead.appendChild(lstHLeftBracket);

        listhead.appendChild(lstHTable);
        listhead.appendChild(lstHColumn);
        listhead.appendChild(lstHOperator);
        listhead.appendChild(lstHQueryValue);
        listhead.appendChild(lstHQueryTo);
        listhead.appendChild(lstHRightBracket);
        advancedPanel.appendChild(listhead);

        Borderlayout layout = new Borderlayout();
        ZKUpdateUtil.setHflex(layout, "1");
        ZKUpdateUtil.setVflex(layout, "1");
        winAdvanced.appendChild(layout);
        
        North north =new North();
        layout.appendChild(north);        
        north.appendChild(advancedPanelToolBar);
       
        ZKUpdateUtil.setVflex(advancedPanelToolBar, "0");

        Center center = new Center();
        layout.appendChild(center);
        center.appendChild(advancedPanel);
        ZKUpdateUtil.setVflex(advancedPanel, "1");

        South south = new South();
        layout.appendChild(south);
        south.appendChild(confirmPanel);
        ZKUpdateUtil.setVflex(south, "min");

        ZKUpdateUtil.setHeight(winAdvanced, "100%");
        ZKUpdateUtil.setWidth(winAdvanced, "100%");
        winAdvanced.addEventListener(Events.ON_OK,this);
        LayoutUtils.addSclass("find-window-advanced", winAdvanced);

    } // initAdvanced

    /**
     * Layout window
     */
    protected void initPanel()
    {
    	setShadow(true);
    	
    	Vlayout layout = new Vlayout();
    	layout.setStyle("height: 100%; width: 100%; position: relative;");
    	ZKUpdateUtil.setVflex(layout, "1");
    	this.appendChild(layout);
    	
    	Div div = new Div();
    	ZKUpdateUtil.setVflex(div, "0");
    	layout.appendChild(div);
    	
    	btnSave = new ToolBarButton();
        btnSave.setAttribute("name","btnSaveAdv");
        if (ThemeManager.isUseFontIconForImage())
        	btnSave.setIconSclass(Icon.getIconSclass(Icon.SAVE));
        else
        	btnSave.setImage(ThemeManager.getThemeResource("images/Save24.png"));
        btnSave.addEventListener(Events.ON_CLICK, this);
        btnSave.setId("btnSave");
        btnSave.setStyle("vertical-align: middle;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("medium-toolbarbutton", btnSave);

        fQueryName = new Combobox();
        fQueryName.setTooltiptext(Msg.getMsg(Env.getCtx(),"QueryName"));
		fQueryName.setId("savedQueryCombo");

		//user query
		userQueries = MUserQuery.get(Env.getCtx(), m_AD_Tab_ID);
		for (int i = 0; i < userQueries.length; i++)
			fQueryName.appendItem(userQueries[i].getName(), userQueries[i].getAD_UserQuery_ID());
		fQueryName.setValue("");
		fQueryName.addEventListener(Events.ON_SELECT, this);
		
		initSavedQueryMoreOptions();
		
		Label label = new Label(Msg.getMsg(Env.getCtx(), "SavedQuery"));
		if (ClientInfo.maxWidth(639))
			label.setStyle("vertical-align: middle;display: block; padding-left: 4px; padding-top: 4px;");
		else
			label.setStyle("vertical-align: middle;");
		div.appendChild(label);
		div.appendChild(fQueryName);
        div.appendChild(btnSave);
        div.appendChild(btnMoreOptions);
        div.appendChild(popupOptions);
       	
        fQueryName.setStyle("margin-left: 3px; margin-right: 3px; position: relative; vertical-align: middle;");
        
        // adding history combo
        prepareHistoryCombo();
        Label labelHistory = new Label(Msg.getMsg(Env.getCtx(), HISTORY_LABEL));
        if (ClientInfo.maxWidth(639))
        	labelHistory.setStyle("vertical-align: middle; display: block;padding-left: 4px; padding-top: 4px;");
        else
        	labelHistory.setStyle("vertical-align: middle;");
        div.appendChild(labelHistory);
        div.appendChild(historyCombo);
        historyCombo.setStyle("margin-left: 3px; margin-right: 3px; position: relative; vertical-align: middle;");
        div.setClass("toolbar");
        winMain = new MultiTabPart();
        winMain.createPart(layout);
        winMain.getComponent().setStyle("position: relative; margin-left: auto; margin-right: auto; margin-top: 3px; margin-bottom: 3px;");
        ZKUpdateUtil.setVflex(winMain.getComponent(), "1");
        winMain.getComponent().addEventListener(Events.ON_SELECT, this);
        winAdvanced = new Window();
        winLookupRecord = new Window();
        Tabpanel tabPanel = new Tabpanel();
        tabPanel.setStyle("height: 100%; width: 100%;");
        tabPanel.appendChild(winLookupRecord);
        tabPanel.setId("simpleSearch");
        winMain.addTab(tabPanel, Msg.getMsg(Env.getCtx(), "Find").replace("&", ""),false, true);
        tabPanel = new Tabpanel();
        tabPanel.setStyle("height: 100%; width: 100%");
        tabPanel.appendChild(winAdvanced);
        tabPanel.setId("advancedSearch");
        winMain.addTab(tabPanel, Msg.getMsg(Env.getCtx(), "Advanced"), false, false);
        initSimple();
        initAdvanced();

		if (!(MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx())).isAccessAdvanced()
				|| "Y".equals(Env.getContext(Env.getCtx(), m_targetWindowNo, m_targetTabNo, GridTab.CTX_IsAllowAdvancedLookup))))
		{
			winMain.getComponent().getTabpanel(1).getLinkedTab().setDisabled(true);
			winMain.getComponent().getTabpanel(1).getLinkedTab().setVisible(false);
		}
        
        statusBar.setClass("statusbar");
        layout.appendChild(statusBar);
    } // initPanel
    
    private void initSavedQueryMoreOptions() {
    	btnMoreOptions = new ToolBarButton();
		btnMoreOptions.setAttribute("name","btnAdvOptions");
		btnMoreOptions.setTooltiptext(Msg.getMsg(Env.getCtx(), "AdvancedOptions"));
		btnMoreOptions.setDisabled(true);
        if (ThemeManager.isUseFontIconForImage())
        	btnMoreOptions.setIconSclass("z-icon-ellipsis-v");
        else
        	btnMoreOptions.setImage(ThemeManager.getThemeResource("images/ShowMore24.png"));
        btnMoreOptions.addEventListener(Events.ON_CLICK, this);
        btnMoreOptions.setId("btnAdvOptions");
        btnMoreOptions.setStyle("margin-left: 3px;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("medium-toolbarbutton", btnMoreOptions);
        
        popupOptions = new Popup();
        popupOptions.setSclass("modern-popup");
        
        Vlayout vlayout = new Vlayout();
        vlayout.setSclass("modern-popup-container");

        // Save as default
        chkSaveDefault = new Checkbox();
        chkSaveDefault.setLabel(Msg.getMsg(Env.getCtx(), "SetDefault"));
        chkSaveDefault.setSclass("modern-checkbox-item");
        chkSaveDefault.addEventListener(Events.ON_CHECK, e -> {
            Checkbox checkbox = (Checkbox) e.getTarget();
        	boolean isSelected = checkbox.isSelected();

            setAsDefaultQuery(isSelected, (isSuccess) -> {
            	if (isSuccess) {
                    Clients.showNotification(isSelected ? Msg.getMsg(Env.getCtx(), "SetSavedQueryDefault") : Msg.getMsg(Env.getCtx(), "RemoveSavedQueryDefault"),
                            Clients.NOTIFICATION_TYPE_INFO, this, "middle_center", 3000);
                } else {
                    // Operation failed or user cancelled - revert checkbox state
                    checkbox.setChecked(!isSelected);
                }
            });
        });

        // Share with all users
        chkShare = new Checkbox();
        chkShare.setSclass("modern-checkbox-item");
        chkShare.setLabel(Msg.getMsg(Env.getCtx(), "ShareFilter"));
        chkShare.addEventListener(Events.ON_CHECK, e -> {
			Checkbox checkbox = (Checkbox) e.getTarget();
			boolean isSelected = checkbox.isSelected();

			if (shareSavedQuery(isSelected)) {
                Clients.showNotification(isSelected ? Msg.getMsg(Env.getCtx(), "SavedQueryShared") : Msg.getMsg(Env.getCtx(), "UnshareSavedQuery"),
                        Clients.NOTIFICATION_TYPE_INFO, this, "middle_center", 3000);
            } 
        });

        // Horizontal line
        Separator separator = new Separator();
        separator.setSclass("modern-separator");

        // Row 4: Delete (as a button)
        Button btnDelete = new Button(Msg.getMsg(Env.getCtx(), "delete"));
        btnDelete.setSclass("modern-menu-item modern-menu-delete");
        btnDelete.addEventListener(Events.ON_CLICK, e -> {
            deleteSavedQuery((isSuccess) -> {
            	if (isSuccess) {
                    Clients.showNotification(Msg.getMsg(Env.getCtx(), "DeleteSavedQuery"),
                            Clients.NOTIFICATION_TYPE_INFO, this, "middle_center", 3000);
                }
            });
        });

        vlayout.appendChild(chkSaveDefault);
        vlayout.appendChild(chkShare);
        vlayout.appendChild(separator);
        vlayout.appendChild(btnDelete);

        popupOptions.appendChild(vlayout);
        btnMoreOptions.addEventListener(Events.ON_CLICK, e -> {
            popupOptions.open(btnMoreOptions, "after_start"); // Align bottom-left of the button
        });
    }
    
	private void enableSavedQueryMoreOptions(MUserQuery userQuery) {
		if (userQuery == null) {
            btnMoreOptions.setDisabled(true);
            btnMoreOptions.setTooltiptext(Msg.getMsg(Env.getCtx(), "NoSavedQuerySelected"));
            chkShare.setVisible(false);
            chkSaveDefault.setSelected(false);
            return;
        }

		btnMoreOptions.setDisabled(false);
		btnMoreOptions.setTooltiptext(Msg.getMsg(Env.getCtx(), "AdvancedOptions"));
		chkSaveDefault.setSelected(userQuery.isDefault());
		if (userQuery.userCanShare()) {
			chkShare.setVisible(true);
			chkShare.setSelected(userQuery.isShared());
		} else {
			chkShare.setVisible(false);
			if (userQuery.isShared()) {
				// If the query is shared but user cannot share, disable updating
	            btnMoreOptions.setDisabled(true);
                btnSave.setDisabled(true);
			}
		}
	}
    
    /**
     * Prepare combo of history scope options
     */
    protected void prepareHistoryCombo()
    {
    	historyCombo.setAutodrop(true);
    	historyCombo.setAutocomplete(true);
    	historyCombo.setButtonVisible(true);
    	historyCombo.setReadonly(false);
    	historyCombo.setId("historyCombo");
    	for (ValueNamePair vnp : historyItems) {
        	historyCombo.appendItem(vnp.getName(), vnp.getValue());
    	}
    	historyCombo.addEventListener(Events.ON_CHANGE, this);
    }

    /**
     * Initialize simple search tab 
     */
    protected void initFind()
    {
        if (log.isLoggable(Level.CONFIG)) log.config("");
        
        ArrayList<GridField> gridFieldList = new ArrayList<GridField>();
        ArrayList<GridField> moreFieldList = new ArrayList<GridField>();
        boolean IsLookupOnlySelection = "Y".equals(Env.getContext(Env.getCtx(), m_targetWindowNo, m_targetTabNo, GridTab.CTX_IsLookupOnlySelection));
        //  Get Info from target Tab
        for (int i = 0; i < m_findFields.length; i++)
        {
            GridField mField = m_findFields[i];
            boolean isDisplayed = mField.isDisplayed();
            
            if (mField.getVO().displayType == DisplayType.YesNo || mField.isEncrypted() || mField.isEncryptedColumn()) {
				// Make Yes-No searchable as list
				GridFieldVO vo = mField.getVO();
				GridFieldVO ynvo = vo.clone(m_simpleCtx, vo.WindowNo, vo.TabNo, vo.AD_Window_ID, vo.AD_Tab_ID, vo.tabReadOnly);
				ynvo.IsDisplayed = true;
				ynvo.displayType = DisplayType.List;
				ynvo.AD_Reference_Value_ID = REFERENCE_YESNO;
				String validationCode = ynvo.ValidationCode;
				if (ynvo.ValidationCodeLookup != null && !ynvo.ValidationCodeLookup.isEmpty())
					validationCode = ynvo.ValidationCodeLookup;

				ynvo.lookupInfo = MLookupFactory.getLookupInfo (ynvo.ctx, ynvo.WindowNo, ynvo.AD_Column_ID, ynvo.displayType,
						Env.getLanguage(ynvo.ctx), ynvo.ColumnName, ynvo.AD_Reference_Value_ID,
						ynvo.IsParent, validationCode);
				ynvo.lookupInfo.tabNo = TABNO;

				GridField ynfield = new GridField(ynvo);

				// replace the original field by the YN List field
				m_findFields[i] = ynfield;
				mField = ynfield;
			} else if  ( mField.getVO().displayType == DisplayType.Button ) {
				// Make Buttons searchable
				GridFieldVO vo = mField.getVO();
				if ( vo.AD_Reference_Value_ID > 0 )
				{
					GridFieldVO postedvo = vo.clone(m_simpleCtx, vo.WindowNo, vo.TabNo, vo.AD_Window_ID, vo.AD_Tab_ID, vo.tabReadOnly);
					postedvo.IsDisplayed = true;
					postedvo.displayType = DisplayType.List;
					String validationCode = postedvo.ValidationCode;
					if (postedvo.ValidationCodeLookup != null && !postedvo.ValidationCodeLookup.isEmpty())
						validationCode = postedvo.ValidationCodeLookup;

					postedvo.lookupInfo = MLookupFactory.getLookupInfo (postedvo.ctx, postedvo.WindowNo, postedvo.AD_Column_ID, postedvo.displayType,
							Env.getLanguage(postedvo.ctx), postedvo.ColumnName, postedvo.AD_Reference_Value_ID,
							postedvo.IsParent, validationCode);
					postedvo.lookupInfo.tabNo = TABNO;

					GridField postedfield = new GridField(postedvo);

					// replace the original field by the Posted List field
					m_findFields[i] = postedfield;
					mField = postedfield;
				}
			} else if  (   mField.getVO().displayType == DisplayType.Account
				        || mField.getVO().displayType == DisplayType.Assignment
					    || mField.getVO().displayType == DisplayType.Chart
					    || mField.getVO().displayType == DisplayType.Image
					    || mField.getVO().displayType == DisplayType.Location
					    || mField.getVO().displayType == DisplayType.Locator
					    || mField.getVO().displayType == DisplayType.PAttribute
				       ) {
				// Make special fields usable for search
				GridFieldVO vo = mField.getVO();
				GridFieldVO newvo = vo.clone(m_simpleCtx, vo.WindowNo, vo.TabNo, vo.AD_Window_ID, vo.AD_Tab_ID, vo.tabReadOnly);
				newvo.IsDisplayed = true;
				newvo.displayType = DisplayType.Search;
				switch (mField.getVO().displayType) {
				case DisplayType.Account:    newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_ACCOUNT;    break;
				case DisplayType.Assignment: newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_ASSIGNMENT; break;
				case DisplayType.Chart:      newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_CHART;      break;
				case DisplayType.Image:      newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_IMAGE;      break;
				case DisplayType.Location:   newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_LOCATION;   break;
				case DisplayType.Locator:    newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_LOCATOR;    break;
				case DisplayType.PAttribute: newvo.AD_Reference_Value_ID = SystemIDs.REFERENCE_PATTRIBUTE; break;
				}
				newvo.lookupInfo = MLookupFactory.getLookupInfo (newvo.ctx, newvo.WindowNo, newvo.AD_Column_ID, newvo.displayType,
						Env.getLanguage(newvo.ctx), newvo.ColumnName, newvo.AD_Reference_Value_ID,
						newvo.IsParent, null);
				newvo.lookupInfo.tabNo = TABNO;

				GridField newfield = new GridField(newvo);
				// replace the original field by the the new Search field
				m_findFields[i] = newfield;
				mField = newfield;
			} else {
				// clone the field and clean gridtab - IDEMPIERE-1105
		        GridField findField = (GridField) mField.clone(m_simpleCtx);
		        if (findField.isLookup()) {
		        	Lookup lookup = findField.getLookup();
		        	if (lookup != null && lookup instanceof MLookup) {
		        		MLookup mLookup = (MLookup) lookup;
		        		mLookup.getLookupInfo().ctx = m_simpleCtx;
		        		mLookup.getLookupInfo().tabNo = TABNO;
		        		if (findField.getVO().ValidationCodeLookup != null && !findField.getVO().ValidationCodeLookup.isEmpty())
		        		{
		        			mLookup.getLookupInfo().ValidationCode = findField.getVO().ValidationCodeLookup;
		        			mLookup.getLookupInfo().IsValidated = false;
		        		}
		        	}
		        }
		        findField.setGridTab(null);
				m_findFields[i] = findField;
				findField.setPlaceholder(null);
				mField = findField;
			}
			
			if (mField.isSelectionColumn()) {
            	gridFieldList.add(mField); // isSelectionColumn 
            } else {
				if (!IsLookupOnlySelection && (isDisplayed || mField.isVirtualSearchColumn()) && mField.getDisplayType() != DisplayType.Button && !mField.getColumnName().equals("AD_Client_ID"))
					moreFieldList.add(mField);
            }
        }   //  for all target tab fields

        //show well known column or the first 2 column in the form if no selection columns have been defined 
        if (gridFieldList.isEmpty() && !moreFieldList.isEmpty())
        {
        	for(GridField field:moreFieldList){
        		if (field.getColumnName().equals("Value") || field.getColumnName().equals("Name") 
        			|| field.getColumnName().equals("DocumentNo") || field.getColumnName().equals("Description"))
        		{
        			gridFieldList.add(field);        			
        		}
        	}
        	if (gridFieldList.isEmpty()) {
        		int i = 0;
        		for(GridField field:moreFieldList){
        			if(field.getColumnName().equals("AD_Client_ID") || field.getColumnName().equals("AD_Org_ID") 
        					|| field.getDisplayType() == DisplayType.ID)
        				continue;
        			gridFieldList.add(field);
        			i++;
        			if (i == 2) break;
        		}
        	}
        	for(GridField field:gridFieldList){
        		moreFieldList.remove(field);
        	}
        }
       
        // added comparator on sequence of selection column for IDEMPIERE-377
        Collections.sort(gridFieldList, new Comparator<GridField>() {
			@Override
			public int compare(GridField o1, GridField o2) {
				// order by SeqNoSelection, sending the zeroes to the end
				int sel1 = o1.getSeqNoSelection();
				if (sel1 == 0)
					sel1 = Integer.MAX_VALUE;
				int sel2 = o2.getSeqNoSelection();
				if (sel2 == 0)
					sel2 = Integer.MAX_VALUE;
				return sel1-sel2;
			}
		});
        
        List<GridField> excludes = new ArrayList<GridField>();
        // adding sorted columns
        for(GridField field:gridFieldList){
        	if (field.isVirtualUIColumn())
        		continue;
        	if (! hasAccessSpecialFields(field))
        		continue;
        	if (!addSelectionColumn (field))
        		excludes.add(field);
		} 
        
        //add ... link to show the rest of the columns
        if (!moreFieldList.isEmpty() && !gridFieldList.isEmpty()) {
        	Group rowg = new Group("...");
        	contentSimpleRows.appendChild(rowg);
			Cell cell = (Cell) rowg.getFirstChild();
			cell.setSclass("z-group-inner");
			cell.setColspan(3);
			cell.setAlign("left");
        	for(GridField field:moreFieldList){
        		if (field.isVirtualUIColumn())
        			continue;
            	if (! hasAccessSpecialFields(field))
            		continue;
            	if (!addSelectionColumn (field, rowg))
            		excludes.add(field);
    		}
        	rowg.setOpen(false);
        }
        
        if (!excludes.isEmpty()) {
        	for(GridField field : excludes) {
        		for(int i = 0; i < m_findFields.length; i++) {
        			if (m_findFields[i] == field) {
        				m_findFields[i] = null;
        				break;
        			}
        		}
        	}
        }
        
        //hide simple search tab if no search fields
        if (m_sEditors.isEmpty()) {
        	Tabpanel tabPanel = winMain.getComponent().getTabpanel(0);
        	tabPanel.getLinkedTab().setVisible(false);
        	tabPanel.setVisible(false);        	
        }
        
        gridFieldList = null;
        m_total = getNoOfRecords(null, false);

    	//	Show Total
		setStatusDB (m_total);
		statusBar.setStatusLine("");
    }   //  initFind

    /**
     * @param field
     * @return true if log in role has access to window for special field (account combination, resource,
     * chat, image, location, locator and attribute set instance)
     */
    protected boolean hasAccessSpecialFields(GridField field) {
    	int windowId = 0;
		switch (field.getAD_Reference_Value_ID()) {
		case SystemIDs.REFERENCE_ACCOUNT:    windowId = SystemIDs.WINDOW_ACCOUNTCOMBINATION;   break;
		case SystemIDs.REFERENCE_ASSIGNMENT: windowId = SystemIDs.WINDOW_RESOURCE;             break;
		case SystemIDs.REFERENCE_CHART:      windowId = SystemIDs.WINDOW_CHART;                break;
		case SystemIDs.REFERENCE_IMAGE:      windowId = SystemIDs.WINDOW_IMAGE;                break;
		case SystemIDs.REFERENCE_LOCATION:   windowId = SystemIDs.WINDOW_LOCATION;             break;
		case SystemIDs.REFERENCE_LOCATOR:    windowId = SystemIDs.WINDOW_LOCATOR;              break;
		case SystemIDs.REFERENCE_PATTRIBUTE: windowId = SystemIDs.WINDOW_ATTRIBUTESETINSTANCE; break;
		}
		if (windowId > 0) {
			Boolean access = MRole.getDefault().getWindowAccess(windowId);
			return access != null;
		}
		return true;
	}

	/**
     * Initialise advance search tab
     */
    protected void initFindAdvanced()
    {
        if (log.isLoggable(Level.CONFIG)) log.config("");
        createFields();
		refreshUserQueries(); //Initializes and sets fQueryName		
    }   //  initFindAdvanced

    /**
     * add new row to {@link #advancedPanel}
     */
    protected void createFields()
    {
        List<?> rowList = advancedPanel.getChildren();
        int row = rowList.size()-1;
    	createFields(null,row);
    }
    
    /**
     * add new row({@link ListItem}) to {@link #advancedPanel}
     * @param fields
     * @param row current last row index
     */
    protected void createFields(String[] fields, int row)
    {
    	if (null!=fields && fields.length>=1 && fields[0].contains(HISTORY_SEPARATOR))
    			return;
        ListItem listItem = new ListItem();
        listItem.setId("Row"+ rowCount++);

        Combobox listTable = new Combobox();
        listTable.setId("listTable"+listItem.getId());
        listTable.setName("listTable");
		listTable.addEventListener(Events.ON_SELECT,this);
		ZKUpdateUtil.setHflex(listTable, "true");
		listTable.setAutodrop(true);
		listTable.setAutocomplete(true);		
		listTable.setInstantSelect(false);
		listTable.addEventListener(Events.ON_BLUR, e -> {
        	if (listTable.getSelectedItem() == null) {
        		listTable.setSelectedIndex(0);
        	}
        });
		if (m_gridTab == null)
			listTable.setDisabled(true);
        
        Combobox listColumn = new Combobox();        
        listColumn.setId("listColumn"+listItem.getId());
        listColumn.setName("listColumn");
        listColumn.addEventListener(Events.ON_SELECT,this);
        ZKUpdateUtil.setHflex(listColumn, "true");
        listColumn.setAutodrop(true);
        listColumn.setAutocomplete(true);		
        listColumn.setInstantSelect(false);
        listColumn.addEventListener(Events.ON_BLUR, e -> {
        	if (listColumn.getSelectedItem() == null) {
        		listColumn.setValue(null);
        	}
        });     
        listColumn.addEventListener(Events.ON_FOCUS, e -> {
        	listColumn.select();
        }); 
        
        Combobox listOperator = new Combobox();
        listOperator.setId("listOperator"+listItem.getId());
        listOperator.setName("listOperator");
        listOperator.addEventListener(Events.ON_SELECT,this);
        ZKUpdateUtil.setHflex(listOperator, "true");
        listOperator.setInstantSelect(false);
        listOperator.setAutocomplete(true);
        listOperator.addEventListener(Events.ON_BLUR, e -> {
        	if (listOperator.getSelectedItem() == null) {
        		listOperator.setSelectedIndex(0);
        	}
        });
        
        Listbox listAndOr = new Listbox();
        listAndOr.setId("listAndOr"+listItem.getId());
        listAndOr.setName("listAndOr");
        listAndOr.setMold("select");
        listAndOr.setRows(0);
        listAndOr.addEventListener(Events.ON_SELECT,this);
        ZKUpdateUtil.setHflex(listAndOr, "true");

        Listbox listLeftBracket = new Listbox();
        listLeftBracket.setId("listLeftBracket"+listItem.getId());
        listLeftBracket.setName("listLeftBracket");
        listLeftBracket.setMold("select");
        listLeftBracket.setRows(0);
        listLeftBracket.addEventListener(Events.ON_SELECT,this);
        ZKUpdateUtil.setHflex(listLeftBracket, "true");

        Listbox listRightBracket = new Listbox();
        listRightBracket.setId("listRightBracket"+listItem.getId());
        listRightBracket.setName("listRightBracket");
        listRightBracket.setMold("select");
        listRightBracket.setRows(0);
        listRightBracket.addEventListener(Events.ON_SELECT,this);
        ZKUpdateUtil.setHflex(listRightBracket, "true");

        setValues(m_findFields, listTable, listColumn, listOperator, fields);

        // And / Or / And Not / Or Not
    	ValueNamePair[]	andOr = new ValueNamePair[] {
    		new ValueNamePair ("AND",		Msg.getMsg(Env.getCtx(),"AND")),
    		new ValueNamePair ("OR",		Msg.getMsg(Env.getCtx(),"OR")),
    		new ValueNamePair ("AND NOT",	Msg.getMsg(Env.getCtx(),"ANDNOT")),
    		new ValueNamePair ("OR NOT",	Msg.getMsg(Env.getCtx(),"ORNOT"))
    	};
        
    	for (ValueNamePair item: andOr)
            listAndOr.appendItem(item.getName(), item.getValue());
    	listAndOr.setSelectedIndex(0); //And - default
    	if (row<=0){ // don't show and/or on the first row.
    		listAndOr.setVisible(false);
    	} else {
    		listAndOr.setVisible(true);
    	}

        // Left Bracket
    	ValueNamePair[]	leftBracket = new ValueNamePair[] {
    		new ValueNamePair ("",			""),	
    		new ValueNamePair ("(",			"("),
    		new ValueNamePair ("((",		"(("),	
    		new ValueNamePair ("(((",		"(((")	
    	};
        
    	for (ValueNamePair item: leftBracket)
            listLeftBracket.appendItem(item.getName(), item.getValue());
    	listLeftBracket.setSelectedIndex(0);
        
        // Right Bracket
    	ValueNamePair[]	rightBracket = new ValueNamePair[] {
    		new ValueNamePair ("",			""),	
    		new ValueNamePair (")",			")"),
    		new ValueNamePair ("))",		"))"),	
    		new ValueNamePair (")))",		")))")	
    	};
        
    	for (ValueNamePair item: rightBracket)
            listRightBracket.appendItem(item.getName(), item.getValue());
    	listRightBracket.setSelectedIndex(0);
        
    	ListCell cellTable = new ListCell();
    	cellTable.appendChild(listTable);
    	cellTable.setId("cellTable"+listItem.getId());
    	
        ListCell cellColumn = new ListCell();
        cellColumn.appendChild(listColumn);
        cellColumn.setId("cellColumn"+listItem.getId());

        ListCell cellOperator = new ListCell();
        cellOperator.appendChild(listOperator);
        cellOperator.setId("cellOperator"+listItem.getId());

        ListCell cellQueryFrom = new ListCell();
        cellQueryFrom.setId("cellQueryFrom"+listItem.getId());

        ListCell cellQueryTo = new ListCell();
        cellQueryTo.setId("cellQueryTo"+listItem.getId());

        ListCell cellAndOr = new ListCell();
        cellAndOr.appendChild(listAndOr);
        cellAndOr.setId("cellAndOr"+listItem.getId());

        ListCell cellLeftBracket = new ListCell();
        cellLeftBracket.appendChild(listLeftBracket);
        cellLeftBracket.setId("cellLeftBracket"+listItem.getId());

        ListCell cellRightBracket = new ListCell();
        cellRightBracket.appendChild(listRightBracket);
        cellRightBracket.setId("cellRightBracket"+listItem.getId());

        listItem.appendChild(cellAndOr);
        listItem.appendChild(cellLeftBracket);
        listItem.appendChild(cellTable);
        listItem.appendChild(cellColumn);
        listItem.appendChild(cellOperator);
        listItem.appendChild(cellQueryFrom);
        listItem.appendChild(cellQueryTo);
        listItem.appendChild(cellRightBracket);

        // after selected row or append to end
        int selectedIndex = advancedPanel.getSelectedIndex();
		if (selectedIndex >= 0)
		{
			ListItem item = advancedPanel.getItemAtIndex(selectedIndex + 1);
			advancedPanel.insertBefore(listItem, item);
		}
		else
		{
			advancedPanel.appendChild(listItem);
		}

		advancedPanel.setSelectedItem(listItem);

        if (fields != null){
        	// QueryFrom
        	ValueNamePair selected = listColumn.getSelectedItem().getValue();
	        String columnName = selected.getValue();
	        String tableName = listTable != null && listTable.getSelectedItem() != null ? listTable.getSelectedItem().getValue() : m_tableName;
	        if (columnName == null || columnName == "")
	        	return;
	        WEditor editorFrom = null;
	    	String value = fields.length > INDEX_VALUE ? fields[INDEX_VALUE] : "";
	    	if(value.length() > 0)
	    	{
	    		cellQueryFrom.setAttribute("value", value);
	    		//Attribute Values Parsing
				if(tableName.equals(MAttribute.COLUMNNAME_M_Attribute_ID))
				{
		    		cellQueryFrom.appendChild(parseAttributeString( Integer.valueOf(columnName), value, listItem, false));
				}
				else
				{
					editorFrom = parseString(getTargetMField(columnName), value, listItem, false);
					if (editorFrom != null)
						cellQueryFrom.appendChild(editorFrom.getComponent());
				}
	    	}
	    	// QueryTo
	    	WEditor editorTo = null;
	    	String value2 = fields.length > INDEX_VALUE2 ? fields[INDEX_VALUE2] : "";
	    	if(value2.length() > 0)
	    	{
	    		cellQueryTo.setAttribute("value", value2);
	    		// Attribute Parsing
	    		if(tableName.equals(MAttribute.COLUMNNAME_M_Attribute_ID))
	    		{
	    			cellQueryTo.appendChild(parseAttributeString( Integer.valueOf(columnName), value2, listItem, true));
	    		}
		    	else
				{
					editorTo = parseString(getTargetMField(columnName), value2, listItem, true);
					if (editorTo != null)
					{
						if (editorFrom != null && editorFrom.getGridField() != null && DisplayType.isDate(editorFrom.getGridField().getDisplayType()))
						{
							Div div = createDateRangeWrapper(editorFrom, editorTo);
							cellQueryTo.appendChild(div);
						}
						else
						{
							cellQueryTo.appendChild(editorTo.getComponent());
						}
					}
				}
	    	}
	    	
	    	// AndOr
	    	String sandOr = fields.length > INDEX_ANDOR ? fields[INDEX_ANDOR] : "";
	    	if(sandOr.length() > 0)
	    	{
	        	for (int i=0; i< listAndOr.getItems().size(); i++) {
	        		if (listAndOr.getItemAtIndex(i).getValue().equals(sandOr)){
	                	listAndOr.setSelectedIndex(i);
	                	break;
	        		}
	        	}
	    	}

	    	// Left Bracket
	    	String sLeftBracket = fields.length > INDEX_LEFTBRACKET ? fields[INDEX_LEFTBRACKET] : "";
	    	if(sLeftBracket.length() > 0)
	    	{
	        	for (int i=0; i < listLeftBracket.getItems().size(); i++) {
	        		if (listLeftBracket.getItemAtIndex(i).getValue().equals(sLeftBracket)){
	        			listLeftBracket.setSelectedIndex(i);
	                	break;
	        		}
	        	}
	    	}

	    	// Right Bracket
	    	String sRightBracket = fields.length > INDEX_RIGHTBRACKET ? fields[INDEX_RIGHTBRACKET] : "";
	    	if(sRightBracket.length() > 0)
	    	{
	        	for (int i=0; i < listRightBracket.getItems().size(); i++) {
	        		if (listRightBracket.getItemAtIndex(i).getValue().equals(sRightBracket)){
	        			listRightBracket.setSelectedIndex(i);
	                	break;
	        		}
	        	}
	    	}	    	
        }        
   }    // createFields

    /**
     * @param editorFrom
     * @param editorTo
     * @return Div wrapper with {@link DateRangeButton}
     */
	protected Div createDateRangeWrapper(WEditor editorFrom, WEditor editorTo) {
		Div div = new Div();
		div.setWidth("100%");
		div.appendChild(editorTo.getComponent());
		div.appendChild(new DateRangeButton(editorFrom, editorTo));
		return div;
	}

	/**
	 * Populate listTable, listColumn and listOperator values for {@link #advancedPanel} row.
	 * @param findFields
	 * @param listTable tables combo box
	 * @param listColumn columns combo box
	 * @param listOperator operators combo box
	 * @param fields values for {@link #advancedPanel} column
	 */
    protected void setValues(GridField[] findFields, Combobox listTable, Combobox listColumn, Combobox listOperator, String[] fields)
    {
    	ArrayList<ValueNamePair> tables = new ArrayList<ValueNamePair>();
    	String columnName =  "";
    	String operator =  "";
    	String tableName= "";
    	
    	 // Load tab and tables
    	if (m_tabs==null||listTable.getItemCount()==0)
	    {
    		initTabs();

	        for (int c = 0; c < m_tabs.length; c++)
	        {
	            String header = m_tabs[c].get_Translation("Name", Env.getAD_Language(Env.getCtx()));
	            ValueNamePair pp = new ValueNamePair(m_tabs[c].getAD_Tab_UU(), header);
	            tables.add(pp);
	        }

	        //Add Attribute
	        if(isAttributeTable()){
	        	String header = Msg.translate(Env.getCtx(), MAttribute.COLUMNNAME_M_Attribute_ID);
	        	ValueNamePair pp = new ValueNamePair(MAttribute.COLUMNNAME_M_Attribute_ID , header);
	            tables.add(pp);	        	
	        }
	    }
        ValueNamePair[] tabs = new ValueNamePair[tables.size()];
        tables.toArray(tabs);

        //process fields parameter
        if(fields != null)
        {    
	    	boolean selected = false;

	    	tableName = fields.length > INDEX_TABLE ? fields[INDEX_TABLE] : "";
	    	columnName = fields.length > INDEX_COLUMNNAME ? fields[INDEX_COLUMNNAME] : "";
        	operator = fields.length > INDEX_OPERATOR ? fields[INDEX_OPERATOR] : ""; 

        	if (m_windowPanel != null)
        	{	
        		// update listTable
		        for (int i = 0; i < tabs.length; i++)
		        {
		        	ValueNamePair item = tabs[i];
		            listTable.appendItem(item.getName(), item.getValue());
	
		            if (item.getValue().equals(MAttribute.COLUMNNAME_M_Attribute_ID)) {
		            	if(item.getValue().equals(tableName))
			 	        {
			 	           	listTable.setSelectedIndex(listTable.getItemCount()-1);
			 	        	selected = true;		 	        	
			 	        }	
		            } else {
		 	            if (item.getValue().equals(tableName))
		 	        	{
		 	            	GridTab mtab = m_windowPanel.getGridWindow().getGridTab(tableName);
		 	            	listTable.setSelectedIndex(listTable.getItemCount()-1);
		 	        		selected = true;
		 	        		findFields=m_windowPanel.getGridWindow().getGridTab(mtab.getAD_Tab_ID()).getFields();
		 	        		m_gridTab=m_windowPanel.getGridWindow().getGridTab(mtab.getAD_Tab_ID());
		 	        	}
		            }	           
		        }
		        if(!selected) listTable.setSelectedIndex(0);
        	}
        }
        
        // Update listColumn and listOperator
        if(!tableName.equals(MAttribute.COLUMNNAME_M_Attribute_ID)){
	     	ArrayList<ValueNamePair> columnItems = new ArrayList<ValueNamePair>();
	     	columnItems.add(new ValueNamePair("", " "));
	     	for (int c = 0; c < findFields.length; c++)
	     	{
	     		GridField field = findFields[c];
	            if (field == null || field.isVirtualUIColumn()
	            	|| ! hasAccessSpecialFields(field))
	            	continue;
		    WEditor editor = WebEditorFactory.getEditor(field, true);
        	    if (editor == null || !editor.isSearchable())
			continue;

				boolean IsLookupOnlySelection = !MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx())).isAccessAdvanced()
												&& "Y".equals(Env.getContext(Env.getCtx(), m_targetWindowNo, m_targetTabNo, GridTab.CTX_IsLookupOnlySelection));
				if (IsLookupOnlySelection && !field.isSelectionColumn())
					continue;
				String l_columnName = field.getColumnName();
				String header = field.getHeader();
	            if (header == null || header.length() == 0)
	            {
	                header = Msg.translate(Env.getCtx(), l_columnName);
	
	                if (header == null || header.length() == 0)
	                    continue;
	            }
	            if (field.isKey())
	                header += (" (ID)");
	            ValueNamePair pp = new ValueNamePair(l_columnName, header.toString());
	            columnItems.add(pp);
	     	}
	     	ValueNamePair[] cols = new ValueNamePair[columnItems.size()];
	        columnItems.toArray(cols);
	        Arrays.sort(cols);      //  sort alpha
	        ValueNamePair[] op = MQuery.OPERATORS;
	        
	        updateColumnListModel(listColumn, cols);
	        if (!isFilterColumnList()) {
		        listColumn.addScrollSelectedIntoViewListener();
	        }	        
	        
	        if(fields == null)
	        {
	        	if (listTable.getItemCount()==0){
	        		// update listTable
	        		for (ValueNamePair item: tabs)
	        			listTable.appendItem(item.getName(), item.getValue());
	        		listTable.setSelectedIndex(0);
	        	}
	        	listColumn.setSelectedIndex(0);

	            for (ValueNamePair item: op)
	                listOperator.appendItem(Msg.getMsg(Env.getCtx(), item.getName()).trim(), item.getValue());
	            listOperator.setSelectedIndex(0);
	        }
	        else
	        {
	        	columnName = fields.length > INDEX_COLUMNNAME ? fields[INDEX_COLUMNNAME] : "";
	        	operator = fields.length > INDEX_OPERATOR ? fields[INDEX_OPERATOR] : "";

	        	boolean selected = false;
	            Comboitem selectedColumnItem = null;
	            for (int i = 0; i < cols.length; i++)
	            {
	            	ValueNamePair columnValueName = cols[i];
	                if(columnValueName.getValue().equals(columnName))
	            	{
	                	listColumn.setSelectedIndex(i);
	                	Comboitem li = listColumn.getItemAtIndex(i);
	            		selected = true;
	            		selectedColumnItem = li;
	            	}
	            }
	            if(!selected) listColumn.setSelectedIndex(0);

	            selected = false;
	            if (selectedColumnItem != null) {
	            	addOperators(selectedColumnItem, listOperator);
	            	for (Component listitem : listOperator.getChildren()) {
	            		if (listitem instanceof ComboItem) {
	            			if (((ComboItem)listitem).getValue().equals(operator)) {
	            				listOperator.setSelectedItem((ComboItem)listitem);
	            				selected = true;
	            				break;
	            			}
	            		}
	            	}
	            } else {
	            	for (int i = 0; i < op.length; i++)
	            	{
	            		ValueNamePair operatorValueName = op[i];
	            		ComboItem li = new ComboItem(Msg.getMsg(Env.getCtx(), operatorValueName.getName()), operatorValueName.getValue()); 
	            		listOperator.appendChild(li);
	            		if(operatorValueName.getValue().equals(operator))
	            		{
	            			listOperator.setSelectedItem(li);
	            			selected = true;
	            		}
	            	}
	            }
	            if(!selected) listOperator.setSelectedIndex(0);
	        }
        } else {
        	setAttributes(listColumn, listOperator, fields);	    	
	    }
    }   // setValues

    /**
     * Create list model from cols for listColumn 
     * @param listColumn
     * @param cols columns
     */
	protected void updateColumnListModel(Combobox listColumn, ValueNamePair[] cols) {
		AbstractListModel<ValueNamePair> columnListModel = null;
		if (isFilterColumnList()) {
		    columnListModel = new SimpleListModel<ValueNamePair>(cols, true) {
				protected static final long serialVersionUID = -8319240524315831047L;

				@Override
				protected boolean inSubModel(Object key, Object value) {
					if (key == null) {
						return true;
					} else if (key instanceof String) {
						if (((String) key).length() == 0)
							return true;
					}
					return value.toString().toLowerCase().startsWith(key.toString().toLowerCase());
				}
				
				protected int getMaxNumberInSubModel(int nRows) {
					return Integer.MAX_VALUE;			
				}
		    	
		    };
		} else {
			columnListModel = new ListModelList<ValueNamePair>(cols);
		}
		listColumn.setModel(columnListModel);
		Events.sendEvent("onInitRender", listColumn, null);
	}

	/**
	 * @return true if columns combo in advance search should filter by user enter text
	 */
    protected boolean isFilterColumnList() {
		return MSysConfig.getBooleanValue(MSysConfig.ZK_ADVANCE_FIND_FILTER_COLUMN_LIST, false, Env.getAD_Client_ID(Env.getCtx()));
	}

	/**
     *  Create editor for mField and add to simple search tab
     *  @param mField field
     *  @return true if editor created and added
     */
    public boolean addSelectionColumn(GridField mField)
    {
    	return addSelectionColumn(mField, null);
    }
    
    /**
     *  Create editor for mField and add to simple search tab
     *  @param mField field
     *  @param group
     *  @return true if editor created and added
     */
    public boolean addSelectionColumn(GridField mField, Group group)
    {
        if (log.isLoggable(Level.CONFIG)) log.config(mField.getHeader());
        int displayLength = mField.getDisplayLength();
        if (displayLength <= 0 || displayLength > FIELDLENGTH)
            mField.setDisplayLength(FIELDLENGTH);
        else
            displayLength = 0;

        //  Editor
        WEditor editor = null;
        editor = WebEditorFactory.getEditor(mField, false);
        if (editor == null || !editor.isSearchable()) {
        	return false;
        }
        editor.setMandatory(false);
        editor.setReadWrite(true);
        editor.dynamicDisplay();
        editor.updateStyle(false);
        editor.addValueChangeListener(this);
        Label label = editor.getLabel();
        Component fieldEditor = editor.getComponent();
        setLengthStringField(mField, fieldEditor);
        //Fix miss label of checkbox
        label.setValue(mField.getHeader());
        //
        if (displayLength > 0)      //  set it back
            mField.setDisplayLength(displayLength);
        
        WEditor editorTo = null;
        Component fieldEditorTo = null;
        if (   DisplayType.isDate(mField.getDisplayType())
        	|| DisplayType.isNumeric(mField.getDisplayType())) {
            //  Editor To
            editorTo = WebEditorFactory.getEditor(mField, true);
            editorTo.setMandatory(false);
            editorTo.setReadWrite(true);
            editorTo.dynamicDisplay();
            editorTo.updateStyle(false);
            editorTo.addValueChangeListener(this);
            //
            if (displayLength > 0)      //  set it back
                mField.setDisplayLength(displayLength);
            fieldEditorTo = editorTo.getComponent();
            fieldEditorTo.addEventListener(Events.ON_OK,this);
        }

        Row panel = new Row();
        panel.appendChild(label);
        if (isLabelAboveInputForSmallWidth()) {
            contentSimpleRows.appendChild(panel);
            if (group != null)
                panel.setGroup(group);
            panel = new Row();
        }
        Div div = new Div();
        panel.appendChild(div);
        div.appendChild(fieldEditor);
        if (editorTo != null) {
        	ToolBarButton editorFlag = new ToolBarButton();
        	editorFlag.setLabel(".. " + Msg.getMsg(Env.getCtx(), "search.result.to") + " ..");
        	editorFlag.setStyle("margin-left: 5px; margin-right: 5px;");
            m_sEditorsFlag.add(editorFlag);
            editorFlag.setMode("toggle");
            div.appendChild(editorFlag);
			div.appendChild(fieldEditorTo);
			DateRangeButton drb = null;
			if (editor.getGridField() != null && DisplayType.isDate(editor.getGridField().getDisplayType()))
			{
				drb = new DateRangeButton(editor, editorTo);
				div.appendChild(drb);
				drb.setVisible(false);
			}
        	fieldEditorTo.setVisible(false);        	
        	final Component editorRef = fieldEditorTo;
        	final DateRangeButton drbRef = drb;
        	editorFlag.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					ToolBarButton btn = (ToolBarButton) event.getTarget();
					editorRef.setVisible(btn.isChecked());
					if (drbRef != null)
						drbRef.setVisible(editorRef.isVisible());
				}
			});
            m_sEditorsTo.add(editorTo);
        } else {
            m_sEditorsFlag.add(null);
            m_sEditorsTo.add(null);
        	editor.fillHorizontal();
        	editor.updateStyle(false);
        }
        if (!isLabelAboveInputForSmallWidth())
            panel.appendChild(new Space());
        if (group != null)
        	panel.setGroup(group);

        contentSimpleRows.appendChild(panel);
        m_sEditors.add(editor);

        fieldEditor.addEventListener(Events.ON_OK,this);
        return true;
    }   // addSelectionColumn
    
    protected void setLengthStringField(GridField field, Component fieldEditor) {
        if (DisplayType.isText(field.getVO().displayType) && fieldEditor instanceof Textbox) {
        	// for string fields allow searching long strings - useful for like and similar to searches
        	((Textbox) fieldEditor).setMaxlength(32767);  // a conservative max literal string - like oracle extended
        }
	}

    @Override
    public void onEvent(Event event) throws Exception
    {
        m_createNew  = false;
        if (Events.ON_CHANGE.equals(event.getName()))
        {
            if (event.getTarget() == historyCombo)
            {
            	// do not allow values out of the list
            	String value = historyCombo.getValue();
            	boolean valid = false;
    			if (value == null) {
    				valid = true;
    			} else {
        	    	for (ValueNamePair vnp : historyItems) {
        	    		if (value.equals(vnp.getName())) {
            				valid = true;
            				break;
        	    		}
        	    	}
    			}
    			if (! valid) {
    				historyCombo.setSelectedIndex(0);
    			}
            }
        }
        else if (Events.ON_SELECT.equals(event.getName()))
        {
        	if (event.getTarget() == fQueryName)
            {
            	int index = fQueryName.getSelectedIndex();
            	if(index < 0) return;
            	if (winMain.getComponent().getSelectedIndex() != 1) 
            	{
            		winMain.getComponent().setSelectedIndex(1);
            		btnSave.setDisabled(m_AD_Tab_ID <= 0);
            		historyCombo.setSelectedItem(null);
            		fQueryName.setReadonly(false); 
            	}
            	onSelectedQueryChanged();
    		}
        	else if (event.getTarget() instanceof Combobox)
            {
                ListItem row = (ListItem)(event.getTarget().getParent().getParent());
                Combobox eventTarget = (Combobox)event.getTarget();
                advancedPanel.setSelectedItem(row);
                Combobox listColumn = (Combobox)row.getFellow("listColumn"+row.getId());
                Combobox listOperator = (Combobox)row.getFellow("listOperator"+row.getId());
                Combobox listTable = (Combobox)row.getFellow("listTable"+row.getId());
                
                if (eventTarget.getId().equals(listTable.getId()))
                {
               		Comboitem table = listTable.getSelectedItem();
               		//Attribute
               		if (table != null && table.getValue().equals(MAttribute.COLUMNNAME_M_Attribute_ID)) {
               			setAttributes(listColumn, listOperator, null);
               		} else {
               			if (table != null && table.getValue().toString().length() > 0)
               			{
               				m_gridTab=m_windowPanel.getGridWindow().getGridTab(table.getValue());
               			} 
               			else
               			{
               				m_gridTab=m_windowPanel.getGridWindow().getGridTab(m_AD_Tab_ID);
               			}

               			setValues(m_gridTab.getFields(), listTable, listColumn, listOperator, null);  
               		}
                }
                else if (eventTarget.getId().equals(listColumn.getId()) || eventTarget.getId().equals(listOperator.getId()))
                {
                	Comboitem table = listTable.getSelectedItem();

                	//Attribute
                	if (table != null && table.getValue().equals(MAttribute.COLUMNNAME_M_Attribute_ID)) {	
                		if (eventTarget.getId().equals(listColumn.getId()))
	                	{
	                		Comboitem column = listColumn.getSelectedItem();
	                		ValueNamePair selected = column.getValue();
	                		if (column != null && selected.getValue().length() > 0)
	                		{
	                			addOperatorsAttribute(column, listOperator);
	                		}
	                	}
                	} else {
                		if (m_gridTab != null) {
                			if (table != null && table.getValue().toString().length() > 0) {
                				m_gridTab=m_windowPanel.getGridWindow().getGridTab(table.getValue());
                			} else {
                				m_gridTab=m_windowPanel.getGridWindow().getGridTab(m_AD_Tab_ID);
                			}
                		}

	                	if (eventTarget.getId().equals(listColumn.getId()))
	                	{
	                		Comboitem column = listColumn.getSelectedItem();
	                		ValueNamePair selected = column != null ? column.getValue() : null;
	                		if (column != null && selected.getValue().length() > 0)
	                		{
	                			addOperators(column, listOperator);
	                		}
	                	}
                	}
                }
                
                // Attribute
                Comboitem table = listTable.getSelectedItem();
                if (table != null && table.getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID)) {
            		Component componentFrom = getAttributeValuesListComponent(row, false);
   	                componentFrom.setId("searchFieldFrom"+row.getId());

   	                Component componentTo = getAttributeValuesListComponent(row, true);
	                componentTo.setId("searchFieldTo"+row.getId());	               

   	                Combobox listOp = (Combobox) row.getFellow("listOperator"+row.getId());
   	                String betweenValue = listOp.getSelectedItem().getValue().toString();

   	                if (betweenValue.equals(MQuery.NULL) || betweenValue.equals(MQuery.NOT_NULL))
   	                {
   	                	// to not display any editor
   	                	row.getFellow("cellQueryFrom"+row.getId()).getChildren().clear();
   	                	row.getFellow("cellQueryTo"+row.getId()).getChildren().clear();
   	                }
   	                else if (eventTarget.getId().equals(listColumn.getId()) || eventTarget.getId().equals(listOperator.getId())) 
   	                {
   	                	addRowEditor(componentFrom, (ListCell)row.getFellow("cellQueryFrom"+row.getId()));
   	                	addRowEditor(componentTo,(ListCell)row.getFellow("cellQueryTo"+row.getId()));   		               
   	                }
                } else {
                	WEditor editorFrom = getEditor(row, false);
	                Component componentFrom = editorFrom != null ? editorFrom.getComponent() : new Label("");
	                componentFrom.setId("searchFieldFrom"+row.getId());
	                WEditor editorTo = getEditor(row, true);
	                Component componentTo = editorTo != null ? editorTo.getComponent() : new Label("");
	                componentTo.setId("searchFieldTo"+row.getId());
	                Combobox listOp = (Combobox) row.getFellow("listOperator"+row.getId());
	                String betweenValue = listOp.getSelectedItem() != null ? listOp.getSelectedItem().getValue().toString() : "";
	                
	                if(betweenValue.equals(MQuery.NULL) || betweenValue.equals(MQuery.NOT_NULL))
	                {
	                	// to not display any editor
	                	row.getFellow("cellQueryFrom"+row.getId()).getChildren().clear();
	                	row.getFellow("cellQueryTo"+row.getId()).getChildren().clear();
	                }
	                else if (eventTarget.getId().equals(listColumn.getId()) || eventTarget.getId().equals(listOperator.getId())) 
	                {
	                	addRowEditor(componentFrom, (ListCell)row.getFellow("cellQueryFrom"+row.getId()));
	                	if (editorFrom instanceof WYesNoEditor)
	                		((ListCell)row.getFellow("cellQueryFrom"+row.getId())).setAttribute("value", editorFrom.getValue());
						if (editorTo != null && editorTo.getGridField() != null && DisplayType.isDate(editorTo.getGridField().getDisplayType()))
						{
							Div div = createDateRangeWrapper(editorFrom, editorTo);
							addRowEditor(div,(ListCell)row.getFellow("cellQueryTo"+row.getId()));
						}
						else
						{
							addRowEditor(componentTo,(ListCell)row.getFellow("cellQueryTo"+row.getId()));
						}
	                }
                }
            }
    		else if (event.getTarget() instanceof Tab) {
    			if (winMain.getComponent().getSelectedIndex() == 1) {
    				onAdvanceTabSelected();
    			} else {
    				onSimpleTabSelected();
    			}
    		}
        }   //
        else if (Events.ON_CLICK.equals(event.getName()))
        {
            //  Toolbar Buttons actions
            if(event.getTarget() instanceof ToolBarButton)
            {
                ToolBarButton button = (ToolBarButton)event.getTarget();

                if ("btnNewAdv".equals(button.getAttribute("name").toString()))
                {
                	createFields();
                	focusToLastAdvanceRow();
                }

                else if ("btnDeleteAdv".equals(button.getAttribute("name").toString()))
                {
                    int index = advancedPanel.getSelectedIndex();
                    if (advancedPanel.getSelectedItem() != null) 
                    {
                    	advancedPanel.getSelectedItem().detach();
                    	advancedPanel.setSelectedIndex(--index);
                    }
                    focusToLastAdvanceRow();
                }

                else if ("btnSaveAdv".equals(button.getAttribute("name").toString()))
                {
                	if (winMain.getComponent().getSelectedIndex() == 1) {
                    	cmd_saveAdvanced(true);
                	} else {
                    	cmd_saveSimple(true);
                	}
                }
            }
            //  Confirm panel actions
            else if(event.getTarget() instanceof Button)
            {
                Button btn = (Button)event.getTarget();

                if ("btnOkSimple".equals(btn.getName()))
                {
                    fQueryName.setSelectedIndex(0);
                    cmd_ok_Simple();
                    fQueryName.setValue("");
                }
                else if ("btnOkAdv".equals(btn.getName()))
                {
                    cmd_ok_Advanced();
                }
                else if("btnCancel".equals(btn.getName()))
                {
                	onCancel();
                }
                else if ("btnNew".equals(btn.getName()))
                {
                    m_query = MQuery.getNoRecordQuery(m_tableName, true);
                    m_total = 0;
                    m_createNew  = true;
                    m_isCancel = false;
                    dispose();
                }
                else if ("btnReset".equals(btn.getName())){
                	for (WEditor clearField : m_sEditors){
                		clearField.setValue(null);
                	}

                	for (WEditor clearField : m_sEditorsTo){
                		if (clearField != null){
                			clearField.setValue(null);
                			clearField.setVisible(false);

                			ToolBarButton moreButtor = m_sEditorsFlag.get(m_sEditorsTo.indexOf(clearField));
                			moreButtor.setChecked(false);
                		}
                	}
                }
            }
        }
        else if (Events.ON_OK.equals(event.getName()))
        {
            if (winLookupRecord.equals(event.getTarget()))
            {
                cmd_ok_Simple();
            }
            else if (winAdvanced.equals(event.getTarget()))
            {
                cmd_ok_Advanced();
            }
            // Check simple search fields
            for (int i = 0; i < m_sEditors.size(); i++)
            {
                WEditor editor = (WEditor)m_sEditors.get(i);
            	if (editor.getComponent() == event.getTarget())
            	{
                    cmd_ok_Simple();
            	}
                WEditor editorTo = (WEditor)m_sEditorsTo.get(i);
            	if (editorTo != null && editor.getComponent() == event.getTarget())
            	{
                    cmd_ok_Simple();
            	}
            }
        }
    }   //  onEvent
    
    /**
     * Sets the query as default (asynchronous version).
     *
     * @param isDefault Whether to set as default
     * @param callback Callback to handle the result
     */
    private void setAsDefaultQuery(boolean isDefault,  Callback<Boolean> callback) {
        MUserQuery userQuery = getActiveUserQuery();

        if (!isValidUserQuery(userQuery)) {
            callback.onCallback(false);
            return;
        }

        if (isDefault) {
            handleDefaultQuery(userQuery, callback);
        } else {
            userQuery.setIsDefault(false);
            userQuery.saveEx();
            callback.onCallback(true);
        }
    }
    
    /**
     * Validates the user query.
     *
     * @param userQuery The user query to validate.
     * @return `true` if the user query is valid, `false` otherwise.
     */
    private boolean isValidUserQuery(MUserQuery userQuery) {
        return userQuery != null && userQuery.getAD_UserQuery_ID() == m_AD_UserQuery_ID;
    }

    /**
    * Handles the logic for saving a query as the default
    *
    * @param userQuery The user query to save as default.
    * @param callback Callback to handle the result
    */
   private void handleDefaultQuery(MUserQuery userQuery, Callback<Boolean> callback) {
       MUserQuery existingDefault = userQuery.getDefaultQueryForUserAndTab();

       if (existingDefault == null) {
           userQuery.setIsDefault(true);
           userQuery.saveEx();
           callback.onCallback(true);
       } else {
           confirmAndSaveDefaultQuery(userQuery, existingDefault, callback);
       }
   }


   /**
    * Confirms and saves the query as default if the user agrees.
    *
    * @param userQuery The user query to save.
    * @param callback Callback to handle the result
    */
   private void confirmAndSaveDefaultQuery(MUserQuery userQuery, MUserQuery existingDefault, Callback<Boolean> callback) {
	   Dialog.ask("", m_targetWindowNo, "ReplaceDefaultQuery", result -> {
	        if (Boolean.TRUE.equals(result)) {
	            try {
	                if (existingDefault != null) {
	                    existingDefault.setIsDefault(false);
	                    existingDefault.saveEx();
	                }
	                userQuery.setIsDefault(true);
	                userQuery.saveEx();
	                callback.onCallback(true);
	            } catch (Exception e) {
	                log.severe("Error while saving default query: " + e.getMessage());
	                callback.onCallback(false);
	            }
	        } else {
	            callback.onCallback(false);
	        }
	   }, existingDefault.getName());
   }
   
   private boolean shareSavedQuery(boolean isShared) {
	   MUserQuery userQuery = getActiveUserQuery();

       if (!isValidUserQuery(userQuery) || !userQuery.userCanShare()) {
           return false;
       }
       
       int AD_User_ID = isShared ? -1 : Env.getAD_User_ID(Env.getCtx());
       userQuery.setAD_User_ID(AD_User_ID);
       userQuery.saveEx();
       return true;
   }
   
   /**
    * Deletes the currently active user query after user confirmation.
    *
    * @param callback A callback to handle the result of the deletion operation.
    *                 The callback is invoked with `true` if the query is successfully deleted,
    *                 and `false` otherwise.
    *
    * The method performs the following steps:
    * 1. Retrieves the active user query using `getActiveUserQuery()`.
    * 2. Validates the user query using `isValidUserQuery()`. If invalid, the callback is invoked with `false`.
    * 3. Displays a confirmation dialog using `Dialog.ask()`.
    *    - If the user confirms, the query is deleted using `userQuery.deleteEx(true)`.
    *    - If an exception occurs during deletion, logs the error and invokes the callback with `false`.
    *    - If the user cancels or an error occurs, the callback is invoked with `false`.
    */
   private void deleteSavedQuery(Callback<Boolean> callback) {
	   MUserQuery userQuery = getActiveUserQuery();

       if (!isValidUserQuery(userQuery)) {
           callback.onCallback(false);
           return;
       }
       
	   Dialog.ask("", m_targetWindowNo, "DeleteSavedQuery?", result -> {
		   if (Boolean.TRUE.equals(result)) {
	            try {
	                userQuery.deleteEx(true);
					refreshUserQueries();
	                callback.onCallback(true);
	            } catch (Exception e) {
	                log.severe("Error while deleting query: " + e.getMessage());
	                callback.onCallback(false);
	            }
	        } else {
	            callback.onCallback(false);
	        }
	   });
   }

    /**
     * user cancellation, close dialog
     */
	protected void onCancel() {
		// do not allow to close tab for Events.ON_CTRL_KEY event
		if(isUseEscForTabClosing)
			SessionManager.getAppDesktop().setCloseTabWithShortcut(false);

		m_isCancel = true;
		dispose();
	}

	/**
	 * On selection of user query
	 */
    public void onSelectedQueryChanged() {
    	m_whereUserQuery = null;
		showAdvanced();
    	btnSave.setDisabled(false);
    	int index = fQueryName.getSelectedIndex();
    	if(index < 0) return;
    	if (winMain.getComponent().getSelectedIndex() != 1) 
    	{
    		winMain.getComponent().setSelectedIndex(1);
    		btnSave.setDisabled(m_AD_Tab_ID <= 0);
    		historyCombo.setSelectedItem(null);
    		fQueryName.setReadonly(false); 
    	}

    	if(index == 0) 
    	{ // no query - wipe and start over.
    		List<?> rowList = advancedPanel.getChildren();
    		for (int rowIndex = rowList.size() - 1; rowIndex >= 1; rowIndex--)
    			rowList.remove(rowIndex);
    		createFields();
    		enableSavedQueryMoreOptions(null);
    	}
		else {
			MUserQuery uq = userQueries[index-1];
			btnSave.setDisabled(!uq.userCanSave());
			parseUserQuery(userQueries[index-1]);
			enableSavedQueryMoreOptions(uq);
		}
    }

    /**
     * On switch to simple search tab
     */
	protected void onSimpleTabSelected() {
		historyCombo.setDisabled(false);
		if (m_findFields != null && m_findFields.length > 0 && m_findFields[0].getGridTab() != m_gridTab)
        	m_gridTab = m_findFields[0].getGridTab();
		if (m_sEditors.size() > 0)
			Clients.response(new AuFocus(m_sEditors.get(0).getComponent()));
	}

	/**
	 * On switch to advance search tab
	 */
	protected void onAdvanceTabSelected() {
		historyCombo.setSelectedItem(null);
		if (advancedPanel.getItems().size() == 0) {
			createFields();
		}
		focusToLastAdvanceRow();
	}

	/**
	 * Set focus to last row of {@link #advancedPanel}
	 */
	protected void focusToLastAdvanceRow() {
		if (advancedPanel.getItemCount() > 0) {
			ListItem li = advancedPanel.getItemAtIndex(advancedPanel.getItemCount()-1);
			Combobox combo = (Combobox) li.getFellow("listColumn"+li.getId());
			combo.focus();
		}
	}

	/**
	 * Parse user query
	 * @param userQuery
	 */
    protected void parseUserQuery(MUserQuery userQuery)
    {
    	if (userQuery == null) 
    		return;

    	String code = userQuery.getCode();
    	if (code.startsWith(MColumn.VIRTUAL_UI_COLUMN_PREFIX)) {
			winMain.getComponent().getTabpanel(1) .getLinkedTab().setLabel(Msg.getMsg(Env.getCtx(), "SQL"));
			m_whereUserQuery = "(" + code.substring(code.indexOf("=")+1, code.length()) + ")";
			if (log.isLoggable(Level.INFO))
				log.log(Level.INFO, m_whereUserQuery);
			hideAdvanced();
    	} else {
			winMain.getComponent().getTabpanel(1) .getLinkedTab().setLabel(Msg.getMsg(Env.getCtx(), "Advanced"));
        	String[] segments = code.split(Pattern.quote(SEGMENT_SEPARATOR));

            List<?> rowList = advancedPanel.getChildren();
            for (int rowIndex = rowList.size() - 1; rowIndex >= 1; rowIndex--)
            	rowList.remove(rowIndex);

    		for (int i = 0; i < segments.length; i++)
    		{
    			String[] fields = segments[i].split(Pattern.quote(FIELD_SEPARATOR));

    			createFields(fields, i);
    		}
    		
    		String[] historysegments = code.split(Pattern.quote(HISTORY_SEPARATOR));
        	
    		String history = historysegments.length > INDEX_HISTORY? historysegments[INDEX_HISTORY] : "";
        	if(history.length() > 0)
        	{
        		historyCombo.setAttribute("history", history);
        		historyCombo.setSelectedIndex(getHistoryIndex(history)+1);
        	}
    	}

		winAdvanced.invalidate();
	}
    
    protected int getHistoryIndex(String value)
    {
    	int myIndex = 0;
    	if(value.equals(HISTORY_DAY_YEAR))
    		myIndex = 1;
    	else if(value.equals(HISTORY_DAY_MONTH))
    		myIndex = 2;
    	else if(value.equals(HISTORY_DAY_WEEK))
    		myIndex = 3;
    	else if(value.equals(HISTORY_DAY_DAY))
    		myIndex = 4;
    	return myIndex;
    }

	/**
	 * 	Create editor and set value to in (for {@link #advancedPanel} row).
	 * 	@param field column
	 * 	@param in value
	 *  @param listItem row of {@link #advancedPanel}
	 *  @param to
	 * 	@return WEditor
	 */
	protected WEditor parseString(GridField field, String in, ListItem listItem, boolean to)
	{
		if (in == null)
			return null;
		int dt = field.getDisplayType();
		try
		{
			WEditor editor = null;
			if (field.isKey())
	            editor = new WNumberEditor(field);
	        else
	            editor = WebEditorFactory.getEditor(field, true);
	        if (editor == null)
	            editor = new WStringEditor(field);

			//	Return Integer
			if (dt == DisplayType.Integer
				|| (DisplayType.isID(dt) && field.getColumnName().endsWith("_ID")))
			{
				int i = Integer.parseInt(in);
		        editor.setValue(Integer.valueOf(i));
			}
			//	Return BigDecimal
			else if (DisplayType.isNumeric(dt))
			{
				editor.setValue(DisplayType.getNumberFormat(dt).parse(in));
			}
			//	Return Timestamp
			else if (DisplayType.isDate(dt))
			{
				long time = 0;
				try
				{
					time = DisplayType.getTimestampFormat_Default().parse(in).getTime();
					editor.setValue(new Timestamp(time));
				}
				catch (Exception e)
				{
					StringBuilder msglog = new StringBuilder().append(in.toString()).append("(").append(in.getClass()).append(")").append(e);
					log.log(Level.SEVERE, msglog.toString());
					time = DisplayType.getDateFormat(dt).parse(in).getTime();
				}

				editor.setValue(new Timestamp(time));
			}
			else if (dt == DisplayType.YesNo)
			{
				boolean val = ("Y".equalsIgnoreCase(in) || "true".equalsIgnoreCase(in));
				editor.setValue(val);
			}
			else
			{
				editor.setValue(in);
			}

			editor.addValueChangeListener(this);

			boolean between = false;
			Combobox listOp = (Combobox) listItem.getFellow("listOperator"+listItem.getId());
	        String betweenValue = listOp.getSelectedItem() != null ? listOp.getSelectedItem().getValue().toString() : null;
	        String opValue = MQuery.OPERATORS[MQuery.BETWEEN_INDEX].getValue();
	        if (to &&  betweenValue != null
	            && betweenValue.equals(opValue))
	            between = true;

	        boolean enabled = !to || (to && between);

			editor.setReadWrite(enabled);
	        editor.setVisible(enabled);
	        editor.dynamicDisplay();

			return editor;
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "Object=" + in, ex);
			return null;
		}

	}	//	parseValue

	/**
	 * Create advance search query
	 * @param saveQuery true to save as user query
	 */
    protected void cmd_saveAdvanced(boolean saveQuery)
	{
		//
		m_query = new MQuery(m_tableName);
		m_query.addRestriction(Env.parseContext(Env.getCtx(), m_targetWindowNo, m_whereExtended, false));
		
		if (m_whereUserQuery == null) {
			StringBuilder code = new StringBuilder();
			
			int openBrackets = 0;

			boolean isCompositeExists = false;		// if we will have composite exists query		
	        List<?> rowList = advancedPanel.getChildren();

	        for (int rowIndex = 1; rowIndex < rowList.size() ; rowIndex++)
	        {
	            //  Column
	            ListItem row = (ListItem)rowList.get(rowIndex);
	            Combobox table = (Combobox)row.getFellow("listTable"+row.getId());
    	        StringBuilder exists = new StringBuilder();

    	        boolean isExists = false;
    	        boolean isExistCondition = false;
    	        boolean isRightBracketCompositeExists = false;

				if(table.getSelectedItem() != null && !table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID)) {
					if (m_windowPanel != null) {
						if (!table.getSelectedItem().getValue().toString().isEmpty())
							m_gridTab=m_windowPanel.getGridWindow().getGridTab(table.getSelectedItem().getValue());
						else
							m_gridTab=m_windowPanel.getGridWindow().getGridTab(m_AD_Tab_ID);
					}
				}

	            Combobox column = (Combobox)row.getFellow("listColumn"+row.getId());
	            if (column == null)
	                continue;
	            if (column.getSelectedItem() == null)
	            {
	            	column.setSelectedIndex(0);
	            	continue;
	            }
	            ValueNamePair vnp = column.getSelectedItem().getValue();
	            String ColumnName = vnp.getValue();
	            String tableUID = table.getSelectedItem() != null ? table.getSelectedItem().getValue().toString() : "";
	            String infoName = ColumnName;
	            
	            GridField field = null;
				boolean isProductCategoryField = false;
				String ColumnSQL = null;

				if (table.getSelectedItem() != null && table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID)) {					
					ColumnSQL = getAttributeSQL(Integer.valueOf(ColumnName));            	
				} else {
					//
					field = getTargetMField(ColumnName);
		            if (field == null || field.isVirtualUIColumn())
		            	continue;
		            isProductCategoryField = isProductCategoryField(field.getColumnName());
		            ColumnSQL = field.getSearchColumnSQL() != null ? field.getSearchColumnSQL() : field.getColumnSQL(false);
		            if (table.getSelectedItem() != null && !table.getSelectedItem().getValue().equals(m_AD_Tab_UU))
					{       
						if (!isCompositeExists) {
							MTable refTable = MTable.get(Env.getCtx(), m_tableName);
							exists.append("SELECT 1 FROM ").append(m_gridTab.getTableName())
							.append(" WHERE ").append(m_gridTab.getTableName()).append(".").append(m_gridTab.getLinkColumnName())
							.append(" = ").append(m_tableName).append(".");
							if (refTable.isUUIDKeyTable())
								exists.append(PO.getUUIDColumnName(m_tableName));
							else
								exists.append(m_tableName).append("_ID ");
							
							exists.append(" AND ")
								.append(getLeftBracketValue(row))
								.append(ColumnSQL);
							ColumnSQL = exists.toString();
						}         

						isExists = true;
					}
				}
	            // Left brackets
	            String lBrackets = getLeftBracketValue(row);
				if (lBrackets != null) {
					openBrackets += lBrackets.length();
					if (isExists && !lBrackets.isEmpty()) {
						isCompositeExists = true;
						isExistCondition = true;
					}
				} else {
					lBrackets = "";
				}
				// Right brackets
	            String rBrackets = getRightBracketValue(row);
				if (rBrackets != null) {
					openBrackets -= rBrackets.length();
					if(isCompositeExists && !rBrackets.isEmpty())				
						isRightBracketCompositeExists = true;	// Reset isCompositeExists at end
				} else {
					rBrackets = "";
				}
				// And Or
	            Listbox listAndOr = (Listbox)row.getFellow("listAndOr"+row.getId());
	            String andOr = listAndOr.getSelectedItem().getValue().toString();
	            boolean and = true;
				boolean not = false;
				if (rowIndex > 1) {
					and = !"OR".contains(andOr);  	//if contains OR
				}
				// NOT
				if (andOr.contains("NOT")) {
					not = true;
				}
	            //  Op
				Combobox op = (Combobox)row.getFellow("listOperator"+row.getId());
	            if (op == null)
	                continue;
	            String Operator = op.getSelectedItem() != null ? op.getSelectedItem().getValue().toString() : "";

	            //  Value   ******
	            ListCell cellQueryFrom = (ListCell)row.getFellow("cellQueryFrom"+row.getId());
	            Object value = null;
	            
	            //Allowing Date validation before save
	            Component compo = cellQueryFrom.getFirstChild();
	            if(compo instanceof Datebox) {
	               Datebox dbox = (Datebox)compo;
	               if(dbox.getValue() != null)
	            	  value = new Timestamp(((Date)dbox.getValue()).getTime());
	            }
	            else if(compo instanceof DatetimeBox) {
	            	  DatetimeBox dtbox = (DatetimeBox)compo;
	            	  if(dtbox.getValue() != null)
	            		 value = new Timestamp(((Date)dtbox.getValue()).getTime());
	            }
	            else if(compo instanceof Checkbox && cellQueryFrom.getAttribute("value") instanceof Boolean) {
	            	value = ((boolean) cellQueryFrom.getAttribute("value") ? "Y" : "N");
	            }
	            else {
	            	value = cellQueryFrom.getAttribute("value");
	            }
	            
	            if (value == null)
	            {
	            	if(Operator.equals(MQuery.NULL) || Operator.equals(MQuery.NOT_NULL))
	            	{
	            		//Foreign Table for OPERAND NULL/NOT NULL
	            		if (table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID)
	                   			|| isExists) {
	    					String where = "";	

	    					if(!isCompositeExists)
	    						where += "EXISTS(";	

	    					where += m_query.getRestrictionSQL  (ColumnSQL, Operator, null,
	    							infoName, null, and, openBrackets);

	    					if(!isCompositeExists)
	    						where += ")";

	    					m_query.addRestriction(where, and, not, isExistCondition, openBrackets);
	            		} else {
		            		m_query.addRestriction(ColumnSQL, Operator, null,
		            				infoName, null, andOr, openBrackets);
	            		}
	            		appendCode(code, ColumnName, Operator, "", "", andOr, lBrackets, rBrackets, tableUID);
	            	}
	            	continue;
	            }else {
	            	if(MQuery.ILIKE.equals(Operator)) {
	            		ColumnSQL = "UPPER("+ColumnSQL+")";
	            	}
	            }
	            Object parsedValue = null;
	            //Parse AttributeValue
	            if (table.getSelectedItem() != null && table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID))
	            	parsedValue = parseAttributeValue(Integer.valueOf(ColumnName), value);
	            else            	
	            	parsedValue = parseValue(field, value);
	            if (parsedValue == null)
	                continue;
	            String infoDisplay = (value == null ? "" : value.toString());
	            // When Attribute is set Field is null
	            if(table.getSelectedItem() != null && !table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID))
	            {
		            if (field.isLookup())
		                infoDisplay = field.getLookup().getDisplay(value);
		            else if (field.getDisplayType() == DisplayType.YesNo)
		                infoDisplay = Msg.getMsg(Env.getCtx(), infoDisplay);
	            }
	            //  Value2  ******
	            Object value2 = null;
	            if (MQuery.OPERATORS[MQuery.BETWEEN_INDEX].getValue().equals(Operator))
	            {
	                ListCell cellQueryTo = (ListCell)row.getFellow("cellQueryTo"+row.getId());
	                
	                //Allowing Date validation before save
	                compo = cellQueryTo.getFirstChild();
	                //check DateRangeButton wrapper div
	                if (compo instanceof Div) {
				if (compo.getFirstChild() instanceof Datebox || compo.getFirstChild() instanceof DatetimeBox)
					compo = compo.getFirstChild();
	                }
	                if(compo instanceof Datebox) {
	                   Datebox dbox = (Datebox)compo;
	                   if(dbox.getValue() != null)
	                	  value2 = new Timestamp(((Date)dbox.getValue()).getTime());
	                }
	                else if(compo instanceof DatetimeBox) {
	                	  DatetimeBox dtbox = (DatetimeBox)compo;
	                	  if(dtbox.getValue() != null)
	                		 value2 = new Timestamp(((Date)dtbox.getValue()).getTime());
	                }
	                else if(compo instanceof Checkbox && cellQueryTo.getAttribute("value") instanceof Boolean) {
	                	value2 = ((boolean) cellQueryTo.getAttribute("value") ? "Y" : "N");
	                }
	                else {
	                	value2 = cellQueryTo.getAttribute("value");
	                }
	                
	                if (value2 == null)
	                    continue;
	                Object parsedValue2 = null;
	                //Parse Attribute Value 2 by field/Attribute
	                if (table.getSelectedItem() != null && table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID))
	                	parsedValue2 = parseAttributeValue(Integer.valueOf(ColumnName), value2);
	                else
	                	parsedValue2 = parseValue(field, value2);
	                String infoDisplay_to = value2.toString();
	                if (parsedValue2 == null)
	                    continue;
	                if (table.getSelectedItem() != null && table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID)
	               			|| isExists) {

	                	String where = ""; 

	                	if(!isCompositeExists)
							where += "EXISTS(";	

	                	where +=  m_query.getRestrictionSQL(ColumnSQL, parsedValue, parsedValue2, 
	                    				 infoName, infoDisplay, infoDisplay_to, and, openBrackets);

	                	if(!isCompositeExists)
							where += ")";

	               	 	m_query.addRestriction(where, and, not, isExistCondition, openBrackets);
	                } else {
		                m_query.addRangeRestriction(ColumnSQL, parsedValue, parsedValue2,
		                    infoName, infoDisplay, infoDisplay_to, andOr, openBrackets);
	                }
	            } else if (isProductCategoryField && MQuery.OPERATORS[MQuery.EQUAL_INDEX].getValue().equals(Operator)) {
	                if (!(parsedValue instanceof Integer)) {
	                    continue;
	                }
	                String where_rest= getSubCategoryWhereClause(field, ((Integer) parsedValue).intValue());
	                if (isExists && !isCompositeExists)
	                	where_rest="EXISTS("+where_rest+")";
	                m_query.addRestriction(where_rest, and, not, isExistCondition, openBrackets);
	            }
	            else if (field != null && (field.getDisplayType()==DisplayType.ChosenMultipleSelectionList||field.getDisplayType()==DisplayType.ChosenMultipleSelectionSearch||field.getDisplayType()==DisplayType.ChosenMultipleSelectionTable) &&
	            		(MQuery.OPERATORS[MQuery.EQUAL_INDEX].getValue().equals(Operator) || MQuery.OPERATORS[MQuery.NOT_EQUAL_INDEX].getValue().equals(Operator)))
	            {
	            	String clause = DB.intersectClauseForCSV(ColumnSQL, parsedValue.toString());
	            	if (MQuery.OPERATORS[MQuery.EQUAL_INDEX].getValue().equals(Operator))
	            		m_query.addRestriction(clause, openBrackets, andOr);
	            	else
	            		m_query.addRestriction("NOT (" + clause + ")", openBrackets, andOr);
	            } else {
	            	if (table.getSelectedItem() != null && table.getSelectedItem().getValue().toString().equals(MAttribute.COLUMNNAME_M_Attribute_ID)
	               			|| isExists) {

	                	String where = "";

	                	if(!isCompositeExists)
							where += "EXISTS(";	

	                	where += m_query.getRestrictionSQL  (ColumnSQL, Operator, parsedValue,
	    						infoName, infoDisplay, and, openBrackets);

	                	if(!isCompositeExists)
							where += ")";
	                	
	                	where += getRightBracketValue(row);

						m_query.addRestriction(where, and, not, isExistCondition, openBrackets);
					} else {
						m_query.addRestriction  (ColumnSQL, Operator, parsedValue,
								infoName, infoDisplay, and, not, openBrackets);
					}
	            }

	            if (isRightBracketCompositeExists) {
	            	isCompositeExists = false;
	            }

	            appendCode(code, ColumnName, Operator, value.toString(), value2 != null ? value2.toString() : "", andOr, lBrackets, rBrackets, tableUID);
	        }
	        
	        saveQuery(saveQuery, code);			
		} else {
			m_query.addRestriction(Env.parseContext(Env.getCtx(), m_targetWindowNo, m_whereUserQuery, false));
		}

	}	//	cmd_saveAdvanced
    
    /**
     * Returns the value selected for the left bracket list item
     * in the current row 
     * @param row
     * @return empty, (, (( or (((
     */
    protected String getLeftBracketValue(ListItem row) {
    	Listbox listLeftBracket = (Listbox)row.getFellow("listLeftBracket"+row.getId());
        return listLeftBracket.getSelectedItem().getValue().toString();
    }
    
    /**
     * Returns the value selected for the right bracket list item
     * in the current row 
     * @param row
     * @return empty, ), )) or )))
     */
    protected String getRightBracketValue(ListItem row) {
        Listbox listRightBracket = (Listbox)row.getFellow("listRightBracket"+row.getId());
        return listRightBracket.getSelectedItem().getValue().toString();
    }

    /**
     * Append values to code
     * @param code
     * @param columnName
     * @param operator
     * @param value1
     * @param value2
     * @param andOr
     * @param lBrackets
     * @param rBrackets
     * @param tableUID
     */
    protected void appendCode(StringBuilder code, String columnName,
			String operator, String value1, String value2, String andOr,
			String lBrackets, String rBrackets, String tableUID) {
		if (code.length() > 0)
			code.append(SEGMENT_SEPARATOR);
		code.append(columnName)
			.append(FIELD_SEPARATOR)
			.append(operator)
			.append(FIELD_SEPARATOR)
			.append(value1)
			.append(FIELD_SEPARATOR)
			.append(value2)
			.append(FIELD_SEPARATOR)
			.append(andOr)
			.append(FIELD_SEPARATOR)
			.append(lBrackets)
			.append(FIELD_SEPARATOR)
			.append(rBrackets)
			.append(FIELD_SEPARATOR)
			.append(tableUID);
	}

    /**
     * @param saveQuery false to save code as user query, false to do nothing
     * @param code
     */
	protected void saveQuery(boolean saveQuery, StringBuilder code) {
        
        String selected = fQueryName.getValue();
		if (selected != null) {
			String name = selected;
			if ((fQueryName.getSelectedIndex() == 0 || name.equals(m_sNew)) && saveQuery){ // New query - needs a name

				Dialog.warn(m_targetWindowNo, "NeedsName", name, null);
				return;
			}
			if (saveQuery){
			
				if(historyCombo.getSelectedItem()!=null)
		        {
					code.append(SEGMENT_SEPARATOR);
					code.append(HISTORY_SEPARATOR);
					code.append( (Object) historyCombo.getSelectedItem().getValue());
		        }
				
				MUserQuery uq = MUserQuery.getUserQueryByName(Env.getCtx(), m_AD_Tab_ID, name);
				if (code.length() > 0) { // New or updated
					if (uq == null) // Create a new record
					{
						uq = new MUserQuery (Env.getCtx(), 0, null);
						uq.setName (name);
						uq.setAD_Tab_ID(m_AD_Tab_ID); //red1 UserQuery [ 1798539 ] taking in new field from Compiere
						uq.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
					}

				} else	if (code.length() <= 0){ // Delete the query
					if (uq == null) 
					{
						Dialog.warn(m_targetWindowNo, "NeedsQuery", name, null);
						return;
					}
					if (uq.delete(true))
					{
						Dialog.info(m_targetWindowNo, "Deleted", name);
						refreshUserQueries();
					}
					else
						Dialog.warn(m_targetWindowNo, "DeleteError", name, null);
					return;
				}
				uq.setCode (code.toString());
				uq.setAD_Table_ID (m_AD_Table_ID);
				//
				if (uq.save())
				{
                    Clients.showNotification(Msg.getMsg(Env.getCtx(), "Saved"),
                            Clients.NOTIFICATION_TYPE_INFO, this, "middle_center", 1000);
					refreshUserQueries();
		    		enableSavedQueryMoreOptions(uq);
				}
				else
                    Clients.showNotification(Msg.getMsg(Env.getCtx(), "SaveError"),
                            Clients.NOTIFICATION_TYPE_ERROR, this, "middle_center", 1000);
			}
			//

		}
	}

	/**
	 * Create simple search query
	 * @param saveQuery true to save as user query
	 */
	protected void cmd_saveSimple(boolean saveQuery)
	{
        //  Create Query String
        m_query = new MQuery(m_tableName);
        m_query.addRestriction(Env.parseContext(Env.getCtx(), m_targetWindowNo, m_whereExtended, false));
		StringBuilder code = new StringBuilder();
        //  Special Editors
        for (int i = 0; i < m_sEditors.size(); i++)
        {
            WEditor wed = (WEditor)m_sEditors.get(i);
            Object value = wed.getValue();
            String ColumnName = wed.getColumnName();
            WEditor wedTo = (WEditor)m_sEditorsTo.get(i);
            Object valueTo = null;
            if (wedTo != null && wedTo.getComponent().isVisible())
            	valueTo = wedTo.getValue();
            if (value != null && value.toString().length() > 0)
            {
            	if (valueTo != null && valueTo.toString().length() > 0) {
            		// range
                    if (log.isLoggable(Level.FINE)) {
                        StringBuilder msglog = new StringBuilder(ColumnName).append(">=").append(value).append("<=").append(valueTo);
                    	log.fine(msglog.toString());
                    }

                    GridField field = getTargetMField(ColumnName);
                    if (field.isVirtualUIColumn())
                    	continue;
                    StringBuilder ColumnSQL = new StringBuilder(field.getSearchColumnSQL());
                    m_query.addRangeRestriction(ColumnSQL.toString(), value, valueTo,
                    		ColumnName, wed.getDisplay(), wedTo.getDisplay(), true, 0);
                    appendCode(code, ColumnName, MQuery.BETWEEN, value.toString(), valueTo.toString(), "AND", "", "", m_AD_Tab_UU);
            	} else {
                    if (log.isLoggable(Level.FINE)) {
                        StringBuilder msglog = new StringBuilder(ColumnName).append("=").append(value);
                    	log.fine(msglog.toString());
                    }

                    // globalqss - Carlos Ruiz - 20060711
                    // fix a bug with virtualColumn + isSelectionColumn not yielding results
                    GridField field = getTargetMField(ColumnName);
                    
                    boolean isProductCategoryField = isProductCategoryField(field.getColumnName());
                    StringBuilder ColumnSQL = field.getSearchColumnSQL() != null ? new StringBuilder(field.getSearchColumnSQL()) : new StringBuilder(field.getColumnSQL(false)); 

                    // add encryption here if the field is encrypted.
                    if (field.isEncrypted()) {
                    	String Operator = MQuery.NULL;
                    	if ("Y".equals(value)){
                    		Operator = MQuery.NOT_NULL;
                    	}
                    	m_query.addRestriction(ColumnSQL.toString(), Operator, null,
                    			ColumnName, wed.getDisplay());
                    	appendCode(code, ColumnName, Operator, "", "", "AND", "", "", m_AD_Tab_UU);
                    	continue;
                    }

                    if (field.getDisplayType()==DisplayType.ChosenMultipleSelectionList||field.getDisplayType()==DisplayType.ChosenMultipleSelectionSearch||field.getDisplayType()==DisplayType.ChosenMultipleSelectionTable)
                    {
                    	String clause = DB.intersectClauseForCSV(ColumnSQL.toString(), value.toString());
                    	m_query.addRestriction(clause);
                    	continue;
                    }

                    //
                    // Be more permissive for String columns
                    if (isSearchLike(field))
                    {
                    	StringBuilder valueStr = new StringBuilder(value.toString().toUpperCase());
                        if (!valueStr.toString().endsWith("%"))
                            valueStr.append("%");
                        //
                        ColumnSQL = new StringBuilder("UPPER(").append(ColumnSQL).append(")");
                        value = valueStr.toString();
                    }
                    //
                    if (value.toString().indexOf('%') != -1) {
                        m_query.addRestriction(ColumnSQL.toString(), MQuery.ILIKE, value, ColumnName, wed.getDisplay());
                        appendCode(code, ColumnName, MQuery.ILIKE, value.toString(), "", "AND", "", "", m_AD_Tab_UU);
                    } else if (isProductCategoryField && value instanceof Integer) {
                        m_query.addRestriction(getSubCategoryWhereClause(field, ((Integer) value).intValue()));
                        appendCode(code, ColumnName, MQuery.EQUAL, value.toString(), "", "AND", "", "", m_AD_Tab_UU);
                    } else {
                    	String oper = MQuery.EQUAL;
                    	if (wedTo != null) {
                            ToolBarButton wedFlag = m_sEditorsFlag.get(i);
                            if (wedFlag.isChecked())
                            	oper = MQuery.GREATER_EQUAL;
                    	}
                        m_query.addRestriction(ColumnSQL.toString(), oper, value, ColumnName, wed.getDisplay());
                        appendCode(code, ColumnName, oper, value.toString(), "", "AND", "", "", m_AD_Tab_UU);
                    }
            	}
            } else if (valueTo != null && valueTo.toString().length() > 0) {
            	// filled upper limit without filling lower limit
                if (log.isLoggable(Level.FINE)) {
                    StringBuilder msglog = new StringBuilder(ColumnName).append("<=").append(valueTo);
                	log.fine(msglog.toString());
                }

                GridField field = getTargetMField(ColumnName);
                StringBuilder ColumnSQL = new StringBuilder(field.getSearchColumnSQL());
                //
                m_query.addRestriction(ColumnSQL.toString(), MQuery.LESS_EQUAL, valueTo, ColumnName, wed.getDisplay());
                appendCode(code, ColumnName, MQuery.LESS_EQUAL, valueTo.toString(), "", "AND", "", "", m_AD_Tab_UU);
            }
        }   //  editors
        
        if (historyCombo.getSelectedItem()!=null)
        {
        	addHistoryRestriction(historyCombo.getSelectedItem());
        }

        saveQuery(saveQuery, code);

	}	//	cmd_saveSimple

	/**
	 * reload user queries
	 */
	protected void refreshUserQueries()
	{
		String value = m_sNew;
		if (fQueryName.getItemCount()>0){ // The list is initialized
			value = fQueryName.getValue();
		}
		userQueries = MUserQuery.get(Env.getCtx(), m_AD_Tab_ID);
		fQueryName.getItems().clear();
		boolean selected = false;
		fQueryName.appendItem(m_sNew, 0);  
		for (int i = 0; i < userQueries.length; i++)
		{
			Comboitem ci = fQueryName.appendItem(userQueries[i].getName());
			ci.setValue(userQueries[i].getAD_UserQuery_ID());
			if(value.equals(userQueries[i].getName()))
			{
				fQueryName.setSelectedItem(ci);
				parseUserQuery(userQueries[i]);
				selected = true;
			}
		}

		if(!selected) { 
			fQueryName.setSelectedIndex(0);
			enableSavedQueryMoreOptions(null);
		}
	}

    /**
     * retrieve the columnName of the Column item selected
     * @param row
     * @return column name
     */
    protected String getColumnName(ListItem row)
    {
    	Combobox listColumn = (Combobox)row.getFellow("listColumn"+row.getId());
        String columnName = "";
        if (listColumn.getSelectedItem() != null)
        {
        	ValueNamePair vnp = listColumn.getSelectedItem().getValue();
        	columnName = vnp.getValue();
        }

        return columnName;

    }   // getColumnName

    /**
     * add component to listcell
     * @param component editor component
     * @param listcell
    **/
    protected void addRowEditor(Component component, ListCell listcell)
    {
        listcell.setLabel("");
        listcell.getChildren().clear();
        listcell.appendChild(component);
        ZKUpdateUtil.setHflex((HtmlBasedComponent)component, "1");
        listcell.invalidate();
     }   //  addComponent

    /**
     * Retrieve operators depending on the item selected in the 'Column' field
     * and add them to the selection
     * @param column Column field selected
     * @param listOperator
     */
    protected void addOperators(Comboitem column, Combobox listOperator)
    {
    	ValueNamePair pair = column.getValue();
    	String columnName = pair.getValue();
    	int referenceType = -1;
		boolean isEncrypted = false;
    	if (columnName != null) {
    		MTable table = null;
    		if (m_gridTab != null)
        		table = MTable.get(Env.getCtx(), m_gridTab.getTableName());
    		else
        		table = MTable.get(Env.getCtx(), m_tableName);
    		MColumn col = table.getColumn(columnName);
    		referenceType = col.getAD_Reference_ID();
    		GridField field = getTargetMField(columnName);
    		isEncrypted = (col.isEncrypted() || field.isEncrypted());
    	}
    	if (isEncrypted)
    	{
        	addOperators(MQuery.OPERATORS_ENCRYPTED, listOperator);
    	}
    	else if (DisplayType.isLookup(referenceType)
        		|| DisplayType.YesNo == referenceType
        		|| DisplayType.Button == referenceType
        		|| DisplayType.Account == referenceType
        		|| DisplayType.Assignment == referenceType
        		|| DisplayType.Chart == referenceType
        		|| DisplayType.Image == referenceType
        		|| DisplayType.Location == referenceType
        		|| DisplayType.Locator == referenceType
        		|| DisplayType.PAttribute == referenceType)
        {
        	addOperators(MQuery.OPERATORS_LOOKUP, listOperator);
        }
        else if (DisplayType.isNumeric(referenceType)
        		|| DisplayType.isID(referenceType)) // Note that lookups were filtered above
        {
        	addOperators(MQuery.OPERATORS_NUMBERS, listOperator);
        }
        else if (DisplayType.isDate(referenceType))
        {
        	addOperators(MQuery.OPERATORS_DATES, listOperator);
        }
        else // DisplayType.isText
        {
        	addOperators(MQuery.OPERATORS_STRINGS, listOperator);
        }
    } //    addOperators

    /**
     * add op to listOperator
     * @param op array of operators
     * @param listOperator
     */
    protected void addOperators(ValueNamePair[] op, Combobox listOperator)
    {
        List<?> itemList = listOperator.getChildren();
        itemList.clear();
        for (ValueNamePair item: op)
        {
            listOperator.appendItem(Msg.getMsg(Env.getCtx(), item.getName()).trim(), item.getValue());
        }
        listOperator.setSelectedIndex(0);
    }   //  addOperators

    /**
     *  Create editor for {@link #advancedPanel} row
     *  @param row row
     *  @param to
     *  @return WEditor
    **/
    public WEditor getEditor(ListItem row, boolean to)
    {
        String columnName = getColumnName(row);
        boolean between = false;
        Combobox listOp = (Combobox) row.getFellow("listOperator"+row.getId());
        String betweenValue = listOp.getSelectedItem() != null ? listOp.getSelectedItem().getValue().toString() : null;
        String opValue = MQuery.OPERATORS[MQuery.BETWEEN_INDEX].getValue();
        if (to &&  betweenValue != null
            && betweenValue.equals(opValue))
            between = true;

        boolean enabled = !to || (to && between);

        //  Create Editor
        GridField field = getTargetMField(columnName);        
        if(field == null) return null;

        GridField findField = (GridField) field.clone(m_advanceCtx);        
        findField.setGridTab(null);
        WEditor editor = null;
        if (findField.isKey() 
			|| (!DisplayType.isLookup(findField.getDisplayType()) && DisplayType.isID(findField.getDisplayType())
				 && findField.getDisplayType() != DisplayType.RecordID))
        {
            editor = new WNumberEditor(findField);
		}
        else if (findField.getDisplayType() == DisplayType.Button)        	
        {
			if (findField.getAD_Reference_Value_ID() > 0) {		
				MLookupInfo info = MLookupFactory.getLookup_List(Env.getLanguage(Env.getCtx()), findField.getAD_Reference_Value_ID());
				info.tabNo = TABNO;
				MLookup mLookup = new MLookup(info, 0);
				editor = new WTableDirEditor(columnName, false,false, true, mLookup);
        		findField.addPropertyChangeListener(editor);

			} else {
				if (columnName.endsWith("_ID")) {
					editor = new WNumberEditor(findField);
				} else {
					editor = new WStringEditor(findField);
				}
			}
        }
        else
        {
        	//reload lookupinfo for find window
        	if (DisplayType.isLookup(findField.getDisplayType()) ) 
        	{        		
        		findField.loadLookupNoValidate(); 
        		Lookup lookup = findField.getLookup();
        		if (lookup != null && lookup instanceof MLookup)
        		{
        			MLookup mLookup = (MLookup) lookup;
        			mLookup.getLookupInfo().tabNo = TABNO;

        			if (findField.getVO().ValidationCodeLookup != null && !findField.getVO().ValidationCodeLookup.isEmpty())
	        		{
	        			mLookup.getLookupInfo().ValidationCode = findField.getVO().ValidationCodeLookup;
						mLookup.getLookupInfo().IsValidated = false;
	        		}
        		}
        		editor = WebEditorFactory.getEditor(findField, true);
        		findField.addPropertyChangeListener(editor);
        	} 
        	else 
        	{
        		editor = WebEditorFactory.getEditor(findField, true);
        		findField.addPropertyChangeListener(editor);
        	}
        }
        if (editor == null)
        {
            editor = new WStringEditor(findField);
            findField.addPropertyChangeListener(editor);
        }
        setLengthStringField(findField, editor.getComponent());
        
        editor.addValueChangeListener(this);
        editor.setValue(null);
        editor.setReadWrite(enabled);
        editor.setVisible(enabled);
        editor.dynamicDisplay();
        editor.updateStyle(false);
        if (editor instanceof WPaymentEditor) {
        	((WPaymentEditor)editor).getComponent().setEnabled(true, false);
        }
        //
        if (to)
        	row.setAttribute(FIND_ROW_EDITOR_TO, editor);
        else
        	row.setAttribute(FIND_ROW_EDITOR, editor);
        return editor;

    }

    /**
     *  Get editor component for {@link #advancedPanel} row
     *  @param row row
     *  @param to
     *  @return Editor component
     */
    public Component getEditorComponent(ListItem row, boolean to)
    {
		WEditor editor = getEditor(row, to);
		return editor != null ? editor.getComponent() : new Label("");
    }

    /**
     *  Find grid field from column name
     *  @param columnName column name
     *  @return GridField
     */
    public GridField getTargetMField (String columnName)
    {
        if (columnName == null)
            return null;
        if (m_gridTab == null) { // when filtering a report dialog
            for (int c = 0; c < m_findFields.length; c++) {
                GridField field = m_findFields[c];
                if (field != null && columnName.equals(field.getColumnName()))
                    return field;
            }
        } else {
            for (int c = 0; c < m_gridTab.getFields().length; c++) {
                GridField field = m_gridTab.getFields()[c];
                if (field != null && columnName.equals(field.getColumnName()))
                    return field;
            }
        }
        return null;
    }   //  getTargetMField

    /**
     * Simple OK Button pressed
     */
    protected void cmd_ok_Simple()
    {
        m_isCancel = false; // teo_sarca [ 1708717 ]
        //  save pending
        cmd_saveSimple(false);
        
        //  Test for no records
        if (getNoOfRecords(m_query, true) != 0) {
        	if (advancedPanel != null) {
        		advancedPanel.getItems().clear();
        	}
        	dispose();
        }
    }   //  cmd_ok_Simple
    
    /**
     * Get days from selected values of history combo
     * @param selectedItem
     * @return number of days
     */
    protected int getHistoryDays(String selectedItem) 
	{
    	int retDays = 0;
		if (selectedItem.equals(HISTORY_DAY_DAY))
			retDays = 1;
		else if (selectedItem.equals(HISTORY_DAY_WEEK))
			retDays = 7;
		else if (selectedItem.equals(HISTORY_DAY_MONTH))
			retDays = 31;
		else if (selectedItem.equals(HISTORY_DAY_YEAR))
			retDays = 365;
		return retDays;
		
	}
    

    /**
     * Adding where clause from history data
     * @param selectedHistoryItem
     */
    protected void addHistoryRestriction(Comboitem selectedHistoryItem)
    {
    	String selectedHistoryValue = historyCombo.getSelectedItem().getValue();
    	if (log.isLoggable(Level.INFO))
    		log.info("History combo selected value  =" +selectedHistoryValue);

    	if (null!=selectedHistoryItem && selectedHistoryItem.toString().length() > 0 && getHistoryDays(selectedHistoryValue) > 0)
    	{
    		StringBuilder where = new StringBuilder(m_tableName);
    		where.append(".Created >= ");
    		where.append("getDate()-").append(getHistoryDays(selectedHistoryValue));
    		m_query.addRestriction(where.toString());
    	}
    }
    
    /**
     * hide window and fire {@link DialogEvents#ON_WINDOW_CLOSE} event
     */
    public void dispose()
    {
        setVisible(false);        
        isvalid = false;
        
        //simulate real dispose/detach
        Events.sendEvent(this, new Event(DialogEvents.ON_WINDOW_CLOSE, this, null));
    }   //  dispose

    /**
     * Advanced OK Button pressed
     */
    protected void cmd_ok_Advanced()
    {
        m_isCancel = false; // teo_sarca [ 1708717 ]
        //  save pending
        cmd_saveAdvanced(false);
        
        if(historyCombo.getSelectedItem()!=null)
        {
        	addHistoryRestriction(historyCombo.getSelectedItem());
        }
        
        if (getNoOfRecords(m_query, true) != 0) {
        	dispose();
        }
    }   //  cmd_ok_Advanced
    
    /**
     * Simulate the user query selection and click ok
     */
    public void advancedOkClick() {
    	onSelectedQueryChanged();
    	cmd_ok_Advanced();
    }
    
    /**
     *  Get the number of records of query
     *  @param query where clause for target tab
     *  @param alertRecords show dialog if there are no records or there are more records than allowed for role/tab
     *  @return record count;
     *          if the results are more then allowed this method will return 0
     */
    protected int getNoOfRecords (MQuery query, boolean alertRecords)
    {
        if (log.isLoggable(Level.CONFIG)) log.config("" + query);
        if (initialSlowQuery && (query == null || query.getRestrictionCount() == 0))
        	return COUNTING_RECORDS_TIMED_OUT;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(m_tableName);
        boolean hasWhere = false;
        if (m_whereExtended != null && m_whereExtended.length() > 0)
        {
            sql.append(" WHERE ").append(m_whereExtended);
            hasWhere = true;
        }
        if (query != null && query.isActive())
        {
            if (hasWhere)
                sql.append(" AND ");
            else
                sql.append(" WHERE ");
            sql.append(query.getWhereClause());
        }
        //  Add Access
        String finalSQL = MRole.getDefault().addAccessSQL(sql.toString(),
            m_tableName, MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);
        finalSQL = Env.parseContext(Env.getCtx(), m_targetWindowNo, finalSQL, false);
        if (log.isLoggable(Level.INFO))
        	Env.setContext(Env.getCtx(), m_targetWindowNo, TABNO, GridTab.CTX_FindSQL, finalSQL);

        //  Execute Query
        int timeout = MSysConfig.getIntValue(MSysConfig.GRIDTABLE_INITIAL_COUNT_TIMEOUT_IN_SECONDS, 
        		GridTable.DEFAULT_GRIDTABLE_COUNT_TIMEOUT_IN_SECONDS, Env.getAD_Client_ID(Env.getCtx()));
        m_total = 999999;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = DB.createStatement();
            if (timeout > 0)
            	stmt.setQueryTimeout(timeout);
            rs = stmt.executeQuery(finalSQL);
            if (rs.next())
                m_total = rs.getInt(1);
        }
        catch (SQLException e)
        {
        	if (DB.getDatabase().isQueryTimeout(e))
        	{
       			m_total = COUNTING_RECORDS_TIMED_OUT; // unknown
       			if (query == null) {
       				initialSlowQuery = true;
       			}
        	}
        	else
        	{
        		throw new DBException(e);
        	}
        }
        finally
        {
        	DB.close(rs, stmt);
        	rs = null;
        	stmt = null;
        }
        //  No Records
        if (m_total == 0 && alertRecords)
            Dialog.warn(m_targetWindowNo, "FindZeroRecords", null);
        //  Load not more than max allow
        if (m_gridTab != null && alertRecords && m_total != COUNTING_RECORDS_TIMED_OUT && m_gridTab.isQueryMax(m_total))
        {
        	// no need to show warning here, it will be thrown on GridTable
            // Dialog.info(m_targetWindowNo, "FindOverMax", m_total + " > " + m_gridTab.getMaxQueryRecords());
            m_total = m_gridTab.getMaxQueryRecords();
        }
        else
            if (log.isLoggable(Level.CONFIG)) log.config("#" + m_total);

        return m_total;
    }   //  getNoOfRecords

    /**
     * Checks the given column.
     * @param columnName
     * @return true if the column is a product category column
     */
    protected boolean isProductCategoryField(String columnName) {
        return MProduct.COLUMNNAME_M_Product_Category_ID.equals(columnName);
    }   //  isProductCategoryField

    /**
     * Returns a sql where clause with the given category id and all of its subcategory ids.
     * It is used as restriction in MQuery.
     * @param field
     * @param productCategoryId
     * @return sql where clause
     */
    protected String getSubCategoryWhereClause(GridField field, int productCategoryId) {
        //if a node with this id is found later in the search we have a loop in the tree
        int subTreeRootParentId = 0;
        StringBuilder retString = new StringBuilder(field.getSearchColumnSQL()).append(" IN (");
        String sql = "SELECT M_Product_Category_ID, M_Product_Category_Parent_ID FROM M_Product_Category WHERE AD_Client_ID=? AND IsActive='Y'";
        final Vector<SimpleTreeNode> categories = new Vector<SimpleTreeNode>(100);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == productCategoryId) {
                    subTreeRootParentId = rs.getInt(2);
                }
                categories.add(new SimpleTreeNode(rs.getInt(1), rs.getInt(2)));
            }
            retString.append(getSubCategoriesString(productCategoryId, categories, subTreeRootParentId))
            		 .append(") ");
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
            retString = new StringBuilder();
        } catch (AdempiereSystemError e) {
            log.log(Level.SEVERE, sql, e);
            retString = new StringBuilder();
        }
        finally{
        	DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }
        return retString.toString();

    }   //  getSubCategoryWhereClause

    /**
     * Recursive search for subcategories with loop detection.
     * @param productCategoryId
     * @param categories
     * @param loopIndicatorId
     * @return comma separated list of category ids
     * @throws AdempiereSystemError if a loop is detected
     */
    protected String getSubCategoriesString(int productCategoryId, Vector<SimpleTreeNode> categories, int loopIndicatorId) throws AdempiereSystemError {
        StringBuilder ret = new StringBuilder();
        final Iterator<SimpleTreeNode> iter = categories.iterator();
        while (iter.hasNext()) {
            SimpleTreeNode node = (SimpleTreeNode) iter.next();
            if (node.getParentId() == productCategoryId) {
                if (node.getNodeId() == loopIndicatorId) {
                    throw new AdempiereSystemError("The product category tree contains a loop on categoryId: " + loopIndicatorId);
                }
                ret.append(getSubCategoriesString(node.getNodeId(), categories, loopIndicatorId)).append(",");
            }
        }
        if (log.isLoggable(Level.FINE)) log.fine(ret.toString());
        StringBuilder msgreturn = new StringBuilder(ret.toString()).append(productCategoryId);
        return msgreturn.toString();
    }   //  getSubCategoriesString

    /**
     * Simple tree node class for product category tree search.
     * @author Karsten Thiemann, kthiemann@adempiere.org
     */
    protected static class SimpleTreeNode {

        protected int nodeId;

        protected int parentId;

        public SimpleTreeNode(int nodeId, int parentId) {
            this.nodeId = nodeId;
            this.parentId = parentId;
        }

        public int getNodeId() {
            return nodeId;
        }

        public int getParentId() {
            return parentId;
        }
    }   //  SimpleTreeNode

    /**
     *  Parse and convert type (if needed)
     *  @param field {@link GridField}
     *  @param in input value
     *  @return in converted to appropriate type for field
     */
    protected Object parseValue (GridField field, Object in)
    {
        if (in == null)
            return null;
        int dt = field.getDisplayType();
        try
        {
            //  Return Integer
            if (dt == DisplayType.Integer
                || (DisplayType.isID(dt) && field.getColumnName().endsWith("_ID")))
            {
                if (in instanceof Integer)
                    return in;
                int i = Integer.parseInt(in.toString());
                return Integer.valueOf(i);
            }
            //  Return BigDecimal
            else if (DisplayType.isNumeric(dt))
            {
                if (in instanceof BigDecimal)
                    return in;
                return DisplayType.getNumberFormat(dt).parse(in.toString());
            }
            //  Return Timestamp
            else if (DisplayType.isDate(dt))
            {
                if (in instanceof Timestamp)
                    return in;
                long time = 0;
                try
                {
                    time = DisplayType.getDateFormat_JDBC().parse(in.toString()).getTime();
                    return new Timestamp(time);
                }
                catch (Exception e)
                {
                    StringBuilder msglog = new StringBuilder(in.toString()).append("(").append(in.getClass()).append(")").append(e);
                	log.log(Level.SEVERE, msglog.toString());
                    time = DisplayType.getDateFormat(dt).parse(in.toString()).getTime();
                }
                return new Timestamp(time);
            }
            //  Return Y/N for Boolean
            else if (in instanceof Boolean)
                return ((Boolean)in).booleanValue() ? "Y" : "N";
        }
        catch (Exception ex)
        {
            log.log(Level.SEVERE, "Object=" + in, ex);
            String error = ex.getLocalizedMessage();
            if (error == null || error.length() == 0)
                error = ex.toString();
            StringBuilder errMsg = new StringBuilder();
            errMsg.append(field.getColumnName()).append(" = ").append(in).append(" - ").append(error);
            //
            Dialog.error(0, "ValidationError", errMsg.toString());
            return null;
        }

        return in;
    }   //  parseValue

    /**
     *  Get Query - Retrieve result
     *  @return String representation of query
     */
    public MQuery getQuery()
    {
        if (m_gridTab != null && m_total != COUNTING_RECORDS_TIMED_OUT && m_gridTab.isQueryMax(m_total) && !m_isCancel)
        {
            m_query = MQuery.getNoRecordQuery (m_tableName, false);
            m_total = 0;
            if (log.isLoggable(Level.WARNING))
            	log.warning("Query - over max");
        }
        else
        {
        	if (log.isLoggable(Level.INFO))
        		log.info("Query=" + m_query);
        }
        return m_query;
    }   //  getQuery

    /**
     *  Get Total Records
     *  @return no of records
     */
    public int getTotalRecords()
    {
        return m_total;
    }   //  getTotalRecords

    @Override
    public void valueChange(ValueChangeEvent evt)
    {
        if (evt != null && evt.getSource() instanceof WEditor)
        {
            WEditor editor = (WEditor)evt.getSource();
            // Editor component
            ListCell listcell = null;
            Properties ctx = null;
            if (winMain.getComponent().getSelectedIndex() == 1)
            {
	            Component component = editor.getComponent();
	            Component parent = component.getParent();
	            if(parent != null) {
	            	if(parent instanceof ListCell) {
	            		listcell = (ListCell) parent;
	            		listcell.setAttribute("value", evt.getNewValue());
	            	}
	            	else {	// use case: Date To editor with Date Range Button
	            		Component secondParent = parent.getParent();
	            		if(secondParent instanceof ListCell) {
		            		listcell = (ListCell) secondParent;
		            		listcell.setAttribute("value", evt.getNewValue());
		            	}
	            	}
	            }
	            ctx = m_advanceCtx;
            }
            else
            {
            	ctx = m_simpleCtx;
            }
            if (evt.getNewValue() == null)
            {
            	Env.setContext(ctx, m_targetWindowNo, editor.getColumnName(), "");
            	Env.setContext(ctx, m_targetWindowNo, TABNO, editor.getColumnName(), "");
            }
            else if (evt.getNewValue() instanceof Boolean)
            {
            	Env.setContext(ctx, m_targetWindowNo, editor.getColumnName(), (Boolean)evt.getNewValue());
            	Env.setContext(ctx, m_targetWindowNo, TABNO, editor.getColumnName(), (Boolean)evt.getNewValue());
            }
            else if (evt.getNewValue() instanceof Timestamp)
            {
            	Env.setContext(ctx, m_targetWindowNo, editor.getColumnName(), (Timestamp)evt.getNewValue());
            	Env.setContext(ctx, m_targetWindowNo, TABNO + "|" + editor.getColumnName(), (Timestamp)evt.getNewValue());
            }
            else
            {
            	Env.setContext(ctx, m_targetWindowNo, editor.getColumnName(), evt.getNewValue().toString());
            	Env.setContext(ctx, m_targetWindowNo, TABNO, editor.getColumnName(), evt.getNewValue().toString());
            }
            
            dynamicDisplay(editor, listcell);
        }
    }

    /**
     * Dynamic display of editor
     * @param editor
     * @param listcell
     */
	protected void dynamicDisplay(WEditor editor, ListCell listcell) {
		if (winMain.getComponent().getSelectedIndex() == 1)
		{
			 List<?> rowList = advancedPanel.getChildren();

		     for (int rowIndex = 1; rowIndex < rowList.size() ; rowIndex++)
		     {
		         //  Column
		         ListItem row = (ListItem)rowList.get(rowIndex);
		         if (Components.isAncestor(row, listcell))
		        	 continue;
		         WEditor other = (WEditor) row.getAttribute(FIND_ROW_EDITOR);
		         if (other != null && other.getGridField() != null && other.getGridField().isLookup())
		         {
		        	 Lookup lookup = other.getGridField().getLookup();
		        	 if (!Util.isEmpty(lookup.getValidation()))
		        	 {
		        		 other.dynamicDisplay();
		        		 other = (WEditor) row.getAttribute(FIND_ROW_EDITOR_TO);
		        		 if (other != null)
		        			 other.dynamicDisplay();
		        	 }
		         }
		     }
		}
		else
		{
			for (int i = 0; i < m_sEditors.size(); i++)
		    {
		        WEditor wed = (WEditor)m_sEditors.get(i);
		        if (wed == editor)
		        	continue;
		        if (wed.getGridField() != null && wed.getGridField().isLookup())
		        {
		        	Lookup lookup = wed.getGridField().getLookup();
		        	if (!Util.isEmpty(lookup.getValidation()))
		        	{
		        		wed.dynamicDisplay();
		        		wed = m_sEditorsTo.get(i);
		                if (wed != null && wed != editor)
		                	wed.dynamicDisplay();
		        	}
		        }
		        
		    }
		}
	}

	/**
	 * Post visible event. Echo from {@link #setVisible(boolean)}.
	 */
	public void OnPostVisible() {
		removeAttribute(ON_POST_VISIBLE_ATTR);
		if (winMain.getComponent().getSelectedIndex() == 0) {
			if (m_sEditors.size() > 0)
				Clients.response(new AuFocus(m_sEditors.get(0).getComponent()));
		} else {
			focusToLastAdvanceRow();
		}
	}

	/**
	 * @return true if dialog cancel by user, false otherwise
	 */
	public boolean isCancel() {
		return m_isCancel;
	}

	/**
	 * @return true if user press the new button
	 */
	public boolean isCreateNew() {
		return m_createNew;
	}

	@Override
	public boolean setVisible(boolean visible) {
		boolean ret = super.setVisible(visible);
		if (visible) {
			if (getAttribute(ON_POST_VISIBLE_ATTR) == null) {
				setAttribute(ON_POST_VISIBLE_ATTR, Boolean.TRUE);
				Events.echoEvent("OnPostVisible", this, null);
			}
			isvalid = true;
		}
		return ret;
	}
	
	/**
	 * @param field
	 * @return true to use Like, false otherwise
	 */
	protected boolean isSearchLike(GridField field)
	{
		return DisplayType.isText(field.getDisplayType()) && !field.isVirtualColumn()
		&& (field.isSelectionColumn() || MColumn.isSuggestSelectionColumn(field.getColumnName(), true));
	}

	/**
	 * @return true if dialog is visible and not cancel
	 */
	public boolean isValid()
	{
		return isvalid;
	}

	/**
	 *	Display current count
	 *  @param currentCount String representation of current/total
	 */
	protected void setStatusDB (int currentCount)
	{
		StringBuilder text = new StringBuilder(" ").append(Msg.getMsg(Env.getCtx(), "Records")).append(" = ").append(m_total == COUNTING_RECORDS_TIMED_OUT ? "?" : m_total).append(" ");
		statusBar.setStatusDB(text.toString());
	}	//	setDtatusDB

	/**
	 * Hide advance search
	 */
	protected void hideAdvanced() {
		advancedPanelToolBar.setVisible(false);
		advancedPanel.setVisible(false);
	}
	
	/**
	 * Show advance search
	 */
	protected void showAdvanced() {
		advancedPanelToolBar.setVisible(true);
		advancedPanel.setVisible(true);
		winAdvanced.invalidate();
	}
	
	/**
	 * Retrieves the currently active user query based on the selected item in the `fQueryName` combobox.
	 *
	 * <p>This method checks if a valid user query is selected in the `fQueryName` combobox and returns
	 * the corresponding `MUserQuery` object. If no valid selection is made or the user queries list is empty,
	 * it returns `null`.</p>
	 *
	 * @return The active `MUserQuery` object corresponding to the selected item in the `fQueryName` combobox,
	 *         or `null` if no valid selection exists.
	 */
	private MUserQuery getActiveUserQuery() {
		if (getAD_UserQuery_ID() <= 0 || userQueries == null || userQueries.length == 0)
			return null;
		return userQueries[fQueryName.getSelectedIndex()-1];
	}
	
	/**
	 * @return AD_UserQuery_ID of selected user query ({@link #fQueryName})
	 */
	public int getAD_UserQuery_ID() {
		if (fQueryName.getSelectedIndex() <= 0 || userQueries[fQueryName.getSelectedIndex()-1] == null)
			m_AD_UserQuery_ID = 0;
		else if (m_AD_UserQuery_ID != userQueries[fQueryName.getSelectedIndex()-1].getAD_UserQuery_ID())
			m_AD_UserQuery_ID = userQueries[fQueryName.getSelectedIndex()-1].getAD_UserQuery_ID();
		return m_AD_UserQuery_ID;
	}

	/**
	 * Set selected user query to AD_UserQuery_ID
	 * @param AD_UserQuery_ID
	 */
	public void setAD_UserQuery_ID(int AD_UserQuery_ID) {
		m_AD_UserQuery_ID = AD_UserQuery_ID;
		for (Comboitem li : fQueryName.getItems()) {
			if (m_AD_UserQuery_ID == (Integer) li.getValue()) {
				fQueryName.setSelectedItem(li);
				break;
			}
		}
	}
	
	/**
     * @param M_Attribute_ID 
     * @return sql clause for attribute value
     */
    protected String getAttributeSQL(Integer M_Attribute_ID) {
    	StringBuilder attributeSQL = new StringBuilder();

		MAttribute attribute = new MAttribute(Env.getCtx(), M_Attribute_ID, null);

		attributeSQL.append(" SELECT 1 FROM M_AttributeInstance ")
			.append(" WHERE ")
			.append(" M_AttributeInstance.M_AttributeSetInstance_ID = "  )
			.append( m_tableName + ".M_AttributeSetInstance_ID " );	

		attributeSQL.append(" AND ")
			.append(" M_AttributeInstance.M_Attribute_ID  = ")
			.append( M_Attribute_ID );

	    if(attribute.getAttributeValueType().equals(String.valueOf(MAttribute.ATTRIBUTEVALUETYPE_AD_Reference_ID))) {	        	
        	//TODO Reference        	

        }
        else if(attribute.getAttributeValueType().equals(MAttribute.ATTRIBUTEVALUETYPE_Date)) {	        	

        	attributeSQL.append(" AND datevalue ");

        }
        else if(attribute.getAttributeValueType().equals(MAttribute.ATTRIBUTEVALUETYPE_List)) {	        	

        	attributeSQL.append(" AND M_AttributeValue_ID ");

        }
        else if(attribute.getAttributeValueType().equals(MAttribute.ATTRIBUTEVALUETYPE_Number)) {

        	attributeSQL.append(" AND valuenumber ");	        	

        }
        else if(attribute.getAttributeValueType().equals(MAttribute.ATTRIBUTEVALUETYPE_StringMax40)) {        	

        	attributeSQL.append(" AND value ");

        }

		return attributeSQL.toString();
	}	// getAttributeSQL

	/**
	 * 	Create editor and set editor value to pass in value parameter
	 * 	@param M_Attribute_ID
	 * 	@param value value
	 *  @param listItem
	 *  @param isValueTo
	 * 	@return Component of created editor
	 */
	protected Component parseAttributeString(int M_Attribute_ID, String value, ListItem listItem, boolean isValueTo)
	{
		if (value == null)
			return null;
		try
		{

		boolean between = false;
        Combobox listOp = (Combobox) listItem.getFellow("listOperator"+listItem.getId());
        String betweenValue = listOp.getSelectedItem().getValue().toString();
        String opValue = MQuery.OPERATORS[MQuery.BETWEEN_INDEX].getValue();
        if (isValueTo &&  betweenValue != null
            && betweenValue.equals(opValue))
            between = true;

        boolean enabled = !isValueTo || (isValueTo && between); 

		MAttribute attribute = new MAttribute(Env.getCtx(), M_Attribute_ID, null);

		//  Create Editor
	    WEditor editor = null;  

	    String attributeValue = attribute.getAttributeValueType();

	    if(attributeValue.equals(String.valueOf(MAttribute.ATTRIBUTEVALUETYPE_AD_Reference_ID))) {
	    	editor = new WNumberEditor();

	    	int i = Integer.parseInt(value);
	       	editor.setValue(i);
	    }
	    else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_Date)) {
	       	editor = new WDateEditor(); 	      

            long time = DisplayType.getDateFormat_JDBC().parse(value.toString()).getTime();

	       	editor.setValue(new Timestamp(time));
	    }
	    else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_List)) {
	       	int AD_Column_ID = MColumn.getColumn_ID(MAttributeValue.Table_Name, MAttributeValue.COLUMNNAME_M_AttributeValue_ID);
	    	MLookup attributeValues = MLookupFactory.get(Env.getCtx(), m_targetWindowNo, AD_Column_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), 
	    			MAttributeValue.COLUMNNAME_M_AttributeValue_ID, 0, true, 
	    			" M_AttributeValue.M_Attribute_ID = " + attribute.get_ID());
	    	editor = new WTableDirEditor("M_AttributeValue_ID", true, false, true, attributeValues); 

	    	editor.setValue(Integer.valueOf(value));	
	    }
	    else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_Number)) {
	       	editor = new WNumberEditor();
	       	//BigDecimal
	       	editor.setValue(new BigDecimal(value));
	    }
	    else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_StringMax40)) {        	
	       	editor = new WStringEditor("Test", true, false, true, 40, 40, null, null);
	       	//String
	       	editor.setValue(value);
	    }

	    editor.addValueChangeListener(this);	       
	    editor.setReadWrite(enabled);
	    editor.setVisible(enabled);

	    return editor.getComponent();	
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "Object=" + value, ex);
			return null;
		}

	}	//	parseAttributeString

	/**
	 * Populate listColumn and listOperator
     * @param listColumn
     * @param listOperator
	 * @param fields value for {@link #advancedPanel} columns
     */
	protected void setAttributes(Combobox listColumn, Combobox listOperator, String[] fields) {
		String columnName = null;
		String operator = null;
		if(fields != null) {
			columnName = fields.length > INDEX_COLUMNNAME ? fields[INDEX_COLUMNNAME] : "";
			operator  = fields.length > INDEX_OPERATOR ? fields[INDEX_OPERATOR] : ""; 
		}
		   //  0 = Columns
     	listColumn.getChildren().clear();

        ArrayList<ValueNamePair> items = new ArrayList<ValueNamePair>();
        items.add(new ValueNamePair("", " "));

		List<MAttribute> attributes = new Query(Env.getCtx(), MAttribute.Table_Name, " AD_Client_ID IN (? , ?) " , null)
						.setParameters(new Object[]{0, Env.getAD_Client_ID(Env.getCtx()) })
						.list();

        for (MAttribute attribute:attributes)
        { 
            String l_columnName = attribute.getName();
            String header = String.valueOf(attribute.get_ID());           

            ValueNamePair pp = new ValueNamePair(header, l_columnName);
            items.add(pp);
        }
        ValueNamePair[] cols = new ValueNamePair[items.size()];
        items.toArray(cols);
        Arrays.sort(cols);      //  sort alpha
        ValueNamePair[] operators = MQuery.OPERATORS;   

        if(fields == null)
        {
            updateColumnListModel(listColumn, cols);
            listColumn.setSelectedIndex(0);

            listOperator.getItems().clear(); //clear operand
            for (ValueNamePair operatorValueName: operators)
            	listOperator.appendItem(Msg.getMsg(Env.getCtx(), operatorValueName.getName()).trim(), operatorValueName.getValue());
            listOperator.setSelectedIndex(0);
        }
        else
        {
        	boolean selected = false;
        	updateColumnListModel(listColumn, cols);
            for (int i = 0; i < cols.length; i++)
            {
            	ValueNamePair item = cols[i];
                if(item.getValue().equals(columnName))
            	{
                	listColumn.setSelectedIndex(i);
            		selected = true;
            	}
            }
            if(!selected) listColumn.setSelectedIndex(0);

            selected = false;
            for (int i = 0; i < operators.length; i++)
            {
            	ValueNamePair item = operators[i];
            	listOperator.appendItem(Msg.getMsg(Env.getCtx(), item.getName()).trim(), item.getValue());
            	Comboitem li = listOperator.getItemAtIndex(listOperator.getItemCount()-1); 
            	if(item.getValue().equals(operator))
            	{
            		listOperator.setSelectedItem(li);
            		selected = true;
            	}
            }
            if(!selected) listOperator.setSelectedIndex(0);
        }	

	} //setAttributes

	/**
	 * add operators to listOperator depends on the type of attribute
     * @param column
     * @param listOperator
     */
    protected void addOperatorsAttribute(Comboitem column, Combobox listOperator) {
    	ValueNamePair pair = column.getValue();
    	MAttribute attribute = new MAttribute(Env.getCtx(), Integer.valueOf(pair.getID()), null);

		if(attribute.getAttributeValueType().equals(MAttribute.ATTRIBUTEVALUETYPE_Date) ||
				attribute.getAttributeValueType().equals(MAttribute.ATTRIBUTEVALUETYPE_Number)) {
			addOperators(MQuery.OPERATORS_NUMBERS, listOperator);          	
		}
		else {
			addOperators(MQuery.OPERATORS_LOOKUP, listOperator);          	
		} 
  	} // addOperatorsAttribute

    /**
     * Create editor for attribute value
     * @param row
     * @param isValueTo
     * @return Component of created editor
     * @throws Exception
     */
    public Component getAttributeValuesListComponent(ListItem row, boolean isValueTo) throws Exception
    {
        String columnName = getColumnName(row);        

        if(columnName == null || columnName.isEmpty()) return new Label("");

        boolean between = false;
        Combobox listOp = (Combobox) row.getFellow("listOperator"+row.getId());
        String betweenValue = listOp.getSelectedItem().getValue().toString();
        String opValue = MQuery.OPERATORS[MQuery.BETWEEN_INDEX].getValue();
        if (isValueTo &&  betweenValue != null
            && betweenValue.equals(opValue))
            between = true;

        boolean enabled = !isValueTo || (isValueTo && between);

        MAttribute attribute = new MAttribute(Env.getCtx(), Integer.valueOf(columnName), null);

        //  Create Editor
        WEditor editor = null;  

        String attributeValue = attribute.getAttributeValueType();

        if(attributeValue.equals(String.valueOf(MAttribute.ATTRIBUTEVALUETYPE_AD_Reference_ID))) {
        	editor = new WNumberEditor();
        }
        else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_Date)) {
        	editor = new WDateEditor();
        }
        else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_List)) {
        	int AD_Column_ID = MColumn.getColumn_ID(MAttributeValue.Table_Name, MAttributeValue.COLUMNNAME_M_AttributeValue_ID);
    		MLookup attributeValues = MLookupFactory.get(Env.getCtx(), m_targetWindowNo, AD_Column_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), 
    				MAttributeValue.COLUMNNAME_M_AttributeValue_ID, 0, true, 
    				" M_AttributeValue.M_Attribute_ID = " + attribute.get_ID());
    		editor = new WTableDirEditor("M_AttributeValue_ID", true, false, true, attributeValues);     
        }
        else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_Number)) {
        	editor = new WNumberEditor();
        }
        else if(attributeValue.equals(MAttribute.ATTRIBUTEVALUETYPE_StringMax40)) {        	
        	editor = new WStringEditor("Test", true, false, true, 40, 40, null, null);
        }

        editor.addValueChangeListener(this);
        editor.setValue(null);
        editor.setReadWrite(enabled);
        editor.setVisible(enabled);

        return editor.getComponent();

    }


    /**
     * Convert value to type appropriate for M_Attribute_ID (if needed)
     * @param M_Attribute_ID
     * @param value
     * @return converted value to desire type
     */
    protected Object parseAttributeValue (int M_Attribute_ID, Object value)
    {
    	MAttribute attribute = new MAttribute(Env.getCtx(), M_Attribute_ID, null);
        if (value == null)
            return null;

        String dt = attribute.getAttributeValueType();   
        try
        {
            //  Return Integer
            if (dt.equals(String.valueOf(MAttribute.ATTRIBUTEVALUETYPE_AD_Reference_ID))
            		|| dt.equals(MAttribute.ATTRIBUTEVALUETYPE_List))
            {
                if (value instanceof Integer)
                    return value;
                int i = Integer.parseInt(value.toString());
                return Integer.valueOf(i);
            }
            //  Return BigDecimal
            else if (dt.equals(MAttribute.ATTRIBUTEVALUETYPE_Number))
            {
                if (value instanceof BigDecimal)
                    return value;
                return  new BigDecimal(value.toString());
            }
            //  Return Timestamp
            else if (dt.equals(MAttribute.ATTRIBUTEVALUETYPE_Date))
            {
                if (value instanceof Timestamp)
                    return value;
                long time = 0;
                try
                {
                    time = DisplayType.getDateFormat_JDBC().parse(value.toString()).getTime();
                    return new Timestamp(time);
                }
                catch (Exception e)
                {
                    StringBuilder msglog = new StringBuilder(value.toString()).append("(").append(value.getClass()).append(")").append(e);
                	log.log(Level.SEVERE, msglog.toString());                    
                }
                return new Timestamp(time);
            }
            //  Return Y/N for Boolean
            else if (value instanceof Boolean)
                return ((Boolean)value).booleanValue() ? "Y" : "N";
        }
        catch (Exception ex)
        {
            log.log(Level.SEVERE, "Object=" + value, ex);
            String error = ex.getLocalizedMessage();
            if (error == null || error.length() == 0)
                error = ex.toString();
            StringBuilder errMsg = new StringBuilder();
            errMsg.append(attribute.getName()).append(" = ").append(value).append(" - ").append(error);
            //
            Dialog.error(0, "ValidationError", errMsg.toString());
            return null;
        }

        return value;
    }   //  parseAttributeValue

	/**
     * Get target tab ({@link #m_AD_Tab_ID}) and its child tabs into {@link #m_tabs}
     */
	protected void initTabs ()
	{
		MTab tab= new MTab(Env.getCtx(), m_AD_Tab_ID, null);
		String whereClause = I_AD_Tab.COLUMNNAME_AD_Window_ID+"=?";
		String whereID = " OR AD_Tab_ID = " +m_AD_Tab_ID;
	    if (tab.getTabLevel()>0) 
	    {
	    	MTab nextSameLevelTab = new Query(Env.getCtx(),I_AD_Tab.Table_Name,whereClause + " AND TabLevel=? AND SeqNo>?",null)
			.setParameters(tab.getAD_Window_ID(),tab.getTabLevel(),tab.getSeqNo())
			.setOrderBy(I_AD_Tab.COLUMNNAME_SeqNo)
			.first();

	    	if (nextSameLevelTab != null) {
	    		whereClause = whereClause+" AND SeqNo<"+nextSameLevelTab.getSeqNo();
	    	}
	    }

		List<MTab> list = new Query(Env.getCtx(),I_AD_Tab.Table_Name," ( " + whereClause + " AND TabLevel=? AND SeqNo>?)" + whereID,null)
		.setParameters(tab.getAD_Window_ID(),tab.getTabLevel()+1,tab.getSeqNo())
		.setOnlyActiveRecords(true)
		.setOrderBy(I_AD_Tab.COLUMNNAME_SeqNo + " ASC")
		.list();

		m_tabs = new MTab[list.size ()];
		list.toArray (m_tabs);
	}

    /**
     * @return true if target tab/table contains column M_AttributeSetInstance_ID
     */
	 protected boolean isAttributeTable() {	        
	   	MTable table = new MTable(Env.getCtx(), m_AD_Table_ID, null);
    	return table.getColumnIndex(COLUMNNAME_M_AttributeSetInstance_ID) > 0? true:false; 
	}	// isAttributeTable
}
