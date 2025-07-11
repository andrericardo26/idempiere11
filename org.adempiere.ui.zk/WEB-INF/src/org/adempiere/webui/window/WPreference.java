/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
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
package org.adempiere.webui.window;

import java.util.logging.Level;

import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ToolBar;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.grid.WQuickEntry;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.Icon;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.I_AD_UserPreference;
import org.compiere.model.MMenu;
import org.compiere.model.MRole;
import org.compiere.model.MUserPreference;
import org.compiere.model.SystemIDs;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Space;

/**
 * User preference dialog
 * @author hengsin
 */
public class WPreference extends WQuickEntry implements EventListener<Event>, ValueChangeListener {	
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 3259434079666571595L;
	private static final CLogger log = CLogger.getCLogger(WPreference.class);
		
	private static final String LINE_DIV_STYLE = "background-color: transparent !important; border: none; margin: 5px;";

	private WYesNoEditor 	adempiereSys;
	private WYesNoEditor 	logMigrationScript;
	private WGadgets 		gadgets;
	private A 				addgadgets;
	private A 				morePreferences;
	private A 				myProfile;
	private MUserPreference preferences = null;
	private int             recordId    = 0;
	private ToolBar 		toolbar = null;

	/**
	 * Default constructor
	 */
	public WPreference() {
		super(Env.getZoomWindowID(MUserPreference.Table_ID, 0));

		try
		{
			jbInit();
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, ex.getMessage());
		}

		Boolean canAccessPreference = MRole.getDefault().getWindowAccess(m_AD_Window_ID);
		if (canAccessPreference != null && canAccessPreference) {
			initPOs();
			loadPreferences();
			moveMigrationScriptComment();
		}

		this.setTitle("");
	} //WPreference

	/**
	 * Load user preferences
	 */
	protected void loadPreferences(){
		preferences = MUserPreference.getUserPreference(Env.getAD_User_ID(Env.getCtx()), Env.getAD_Client_ID(Env.getCtx()));
		recordId = preferences.get_ID();

		if(getQuickFields()>0)
			loadRecord(recordId);
	} //loadPreferences

	/**
	 * Layout dialog
	 * @throws Exception
	 */
	protected void jbInit() throws Exception
	{
		ZKUpdateUtil.setWidth(this, "230px");
		this.setBorder("normal");
		this.setClosable(true);
		this.setSizable(true);
		this.appendChild(centerPanel);
		init();
	} //jbInit

	/**
	 * Layout dialog
	 */
	protected void init() {
		this.appendChild(getMorePreferences());
		this.appendChild(getMyProfile());
		this.appendChild(getGadgets());

		if (Env.getAD_Client_ID(Env.getCtx()) <= 20 && Env.getAD_User_ID(Env.getCtx()) <= 102) {
			this.appendChild(new Space());
			this.appendChild(getDictionaryMaintenance());
			this.appendChild(getLogMigrationScript());
		}

		initToolbar();
		this.appendChild(getToolbar());

	} //init

	/**
	 * Move field editor for migration script comment next to {@link #logMigrationScript}.
	 */
	private void moveMigrationScriptComment() {
		if (logMigrationScript == null)
			return;
		WEditor commentEditor = null;
		for (WEditor editor : quickEditors) {
			if (I_AD_UserPreference.COLUMNNAME_MigrationScriptComment.equals(editor.getColumnName())) {
				commentEditor = editor;
				break;
			}
		}
		if (commentEditor != null) {
			Component field = commentEditor.getComponent();
			Component parent = field.getParent();
			if (parent != null) {
				parent.detach();
				Div div = new Div();
				div.setStyle(LINE_DIV_STYLE);
				Label label = commentEditor.getLabel();
				LayoutUtils.removeSclass("field-label", label);
				if (!label.getValue().endsWith(":"))
					label.setValue(label.getValue()+":");
				div.appendChild(label);
				this.insertBefore(div, logMigrationScript.getComponent().getParent().getNextSibling());
				Component ref = div.getNextSibling();
				div = new Div();
				div.setStyle(LINE_DIV_STYLE);
				div.appendChild(field);
				this.insertBefore(div, ref);
			}
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		String nameEvent=event.getName();
		Component com =event.getTarget();

		if (Events.ON_CLICK.equals(nameEvent)) {
			if (com instanceof ToolBarButton) {
				onSave();
			}

			if (com == addgadgets) {
				gadgets = new WGadgets();
				gadgets.setClosable(true);
				gadgets.setSizable(true);
				if (ClientInfo.maxWidth(ClientInfo.SMALL_WIDTH-1))
					ZKUpdateUtil.setWidth(gadgets, "90%");
				else if (ClientInfo.maxWidth(ClientInfo.MEDIUM_WIDTH-1))
					ZKUpdateUtil.setWidth(gadgets, "70%");
				else
					ZKUpdateUtil.setWidth(gadgets, "50%");
				if (ClientInfo.maxHeight(ClientInfo.MEDIUM_HEIGHT-1))
					ZKUpdateUtil.setHeight(gadgets, "80%");
				else
					ZKUpdateUtil.setHeight(gadgets, "60%");
				gadgets.setTitle( Msg.translate(Env.getCtx(), "DashboardGadgets"));
				gadgets.setAttribute(Window.MODE_KEY, Mode.HIGHLIGHTED);
				AEnv.showWindow(gadgets);
				gadgets.focus();
			} else if (com == morePreferences) {
				AEnv.zoom(MUserPreference.Table_ID, recordId);
				this.detach();
			} else if (com == myProfile) {
				MMenu myProfileMenu = MMenu.get(SystemIDs.MY_PROFILE_MENU_ID);
				AEnv.zoom(myProfileMenu.getAD_Window_ID(), null);
				this.detach();
			}
		}
	} //onEvent

	/**
	 * Save changes
	 */
	protected void onSave() {
		actionSave();

		// Log Migration Script and AdempiereSys are just in-memory preferences, must not be saved
		if (logMigrationScript != null) {
			Env.getCtx().setProperty(Ini.P_LOGMIGRATIONSCRIPT, (Boolean)logMigrationScript.getValue() ? "Y" : "N");
			Env.getCtx().setProperty("P|"+Ini.P_LOGMIGRATIONSCRIPT, (Boolean)logMigrationScript.getValue() ? "Y" : "N");
		}
		if (adempiereSys != null) {
			Env.getCtx().setProperty(Ini.P_ADEMPIERESYS, (Boolean)adempiereSys.getValue() ? "Y" : "N");
			Env.getCtx().setProperty("P|"+Ini.P_ADEMPIERESYS, (Boolean)adempiereSys.getValue() ? "Y" : "N");
		}

		this.detach();
	} //onSave

	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (evt.getSource() instanceof WYesNoEditor) {
			// Log Migration Script and AdempiereSys are just in-memory preferences, set them without need to save
			if (evt.getSource() == logMigrationScript) {
				Env.getCtx().setProperty(Ini.P_LOGMIGRATIONSCRIPT, (Boolean)logMigrationScript.getValue() ? "Y" : "N");
				Env.getCtx().setProperty("P|"+Ini.P_LOGMIGRATIONSCRIPT, (Boolean)logMigrationScript.getValue() ? "Y" : "N");
				dynamicDisplay();
			} else if (evt.getSource() == adempiereSys) {
				Env.getCtx().setProperty(Ini.P_ADEMPIERESYS, (Boolean)adempiereSys.getValue() ? "Y" : "N");
				Env.getCtx().setProperty("P|"+Ini.P_ADEMPIERESYS, (Boolean)adempiereSys.getValue() ? "Y" : "N");
			}
		}
		super.valueChange(evt);
	}

	/**
	 * @return the Div which allow to open the User Preferences window
	 */
	protected Div getMorePreferences() {
		Div div = new Div();
		div.setStyle(LINE_DIV_STYLE);
		morePreferences= new A();	
		morePreferences.setLabel(Msg.translate(Env.getCtx(), "MorePreferences"));
		morePreferences.addEventListener(Events.ON_CLICK, this);

		int windowID = Env.getZoomWindowID(MUserPreference.Table_ID, recordId);
		if (windowID > 0) {
			Boolean access = MRole.getDefault().getWindowAccess(windowID);
			if (access != null && access.booleanValue())
				div.appendChild(morePreferences);
		}

		return div;
	}

	/**
	 * @return the preference entry associated with the standard menuID
	 */
	protected Div getMyProfile() {
		return getMyProfile(SystemIDs.MY_PROFILE_MENU_ID);
	}

	/**
	 * @param menuID - the menu associated with the window My Profile
	 * @return the preference entry associated with the menuID
	 */
	protected Div getMyProfile(int menuID) {
		MMenu myProfileMenu = MMenu.get(SystemIDs.MY_PROFILE_MENU_ID);		
		Div div = new Div();
		div.setStyle(LINE_DIV_STYLE);
		myProfile= new A();	
		myProfile.setLabel(myProfileMenu.get_Translation("Name"));
		myProfile.addEventListener(Events.ON_CLICK, this);

		int windowID = MMenu.get(menuID).getAD_Window_ID();
		if (windowID > 0) {
			Boolean access = MRole.getDefault().getWindowAccess(windowID);
			if (access != null && access.booleanValue())
				div.appendChild(myProfile);
		}

		return div;
	}

	/**
	 * @return the Div which allow to open the ManageGadgets panel
	 */
	protected Div getGadgets() {
		Div div = new Div();
		div.setStyle(LINE_DIV_STYLE);
		addgadgets= new A();	
		addgadgets.setLabel( Msg.translate(Env.getCtx(), "ManageGadgets"));
		addgadgets.addEventListener(Events.ON_CLICK, this);
		div.appendChild(addgadgets);		
		return div;
	}

	/**
	 * @return the Div which allow to register Dictionary objects
	 */
	protected Div getDictionaryMaintenance() {
		adempiereSys = new WYesNoEditor(Ini.P_ADEMPIERESYS, Msg.getMsg(Env.getCtx(), Ini.P_ADEMPIERESYS, true),
				null, false, false, true);
		adempiereSys.getComponent().setTooltiptext(Msg.getMsg(Env.getCtx(), Ini.P_ADEMPIERESYS, false));
		Div div = new Div();
		div.setStyle(LINE_DIV_STYLE);
		div.appendChild(adempiereSys.getComponent());

		adempiereSys.setValue(Env.getCtx().getProperty(Ini.P_ADEMPIERESYS));
		adempiereSys.addValueChangeListener(this);

		return div;
	}

	/**
	 * @return the Div which allow to register save migration scripts
	 */
	protected Div getLogMigrationScript() {
		logMigrationScript = new WYesNoEditor(Ini.P_LOGMIGRATIONSCRIPT, Msg.getMsg(Env.getCtx(), Ini.P_LOGMIGRATIONSCRIPT, true),
				null, false, false, true);
		logMigrationScript.getComponent().setTooltiptext(Msg.getMsg(Env.getCtx(), Ini.P_LOGMIGRATIONSCRIPT, false));
		Div div = new Div();
		div.setStyle(LINE_DIV_STYLE);
		div.appendChild(logMigrationScript.getComponent());

		logMigrationScript.setValue(Env.getCtx().getProperty(Ini.P_LOGMIGRATIONSCRIPT));
		logMigrationScript.addValueChangeListener(this);
		return div;
	}

	protected void initToolbar() {
		toolbar = new ToolBar();
		toolbar.setAlign("end");

		ToolBarButton btn = new ToolBarButton("");
		btn.setName("btnSave");
		if (ThemeManager.isUseFontIconForImage())
			btn.setIconSclass(Icon.getIconSclass(Icon.SAVE));
		else
			btn.setImage(ThemeManager.getThemeResource("images/Save24.png"));
		btn.setTooltiptext(Msg.getMsg(Env.getCtx(),"Save"));
		btn.addEventListener(Events.ON_CLICK, this);
		toolbar.appendChild(btn);
		toolbar.setStyle("border: none");
		if (ThemeManager.isUseFontIconForImage())
			LayoutUtils.addSclass("large-toolbarbutton", btn);
	}

	public ToolBar getToolbar() {
		return toolbar;
	}
}
