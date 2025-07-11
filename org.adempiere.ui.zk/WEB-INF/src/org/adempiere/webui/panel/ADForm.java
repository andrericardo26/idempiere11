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

package org.adempiere.webui.panel;

import java.util.logging.Level;

import org.adempiere.webui.Extensions;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.exception.ApplicationException;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridTab;
import org.compiere.model.MForm;
import org.compiere.model.MSysConfig;
import org.compiere.model.X_AD_CtxHelp;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;

/**
 * Abstract base class for iDempiere Web UI custom form (AD_Form).
 *
 * @author Andrew Kimball
 */
public abstract class ADForm extends Window implements EventListener<Event>, IHelpContext
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -836186022208822051L;

	/** The class' logging enabler */
    protected static final CLogger logger;
    
    static
    {
        logger = CLogger.getCLogger(ADForm.class);
    }

    /** AD_Form_ID */
    private int m_adFormId;
    /** window number of desktop tab */
    protected int m_WindowNo;

    /** Name of form */
	private String m_name;

	private ProcessInfo m_pi;

	private IFormController m_customForm;

	/**
	 * SysConfig USE_ESC_FOR_TAB_CLOSING
	 */
	private boolean isUseEscForTabClosing = MSysConfig.getBooleanValue(MSysConfig.USE_ESC_FOR_TAB_CLOSING, false, Env.getAD_Client_ID(Env.getCtx()));

    /**
     * Constructor
     */
    protected ADForm()
    {
         m_WindowNo = SessionManager.getAppDesktop().registerWindow(this);

         ZKUpdateUtil.setWidth(this, "100%");
         ZKUpdateUtil.setHeight(this, "99%");
         this.setStyle("position:relative");
         this.setContentSclass("adform-content");
    }

    /**
     * Get registered window number
     * @return window number
     */
    public int getWindowNo()
    {
    	return m_WindowNo;
    }

    /**
     * Get AD_Form_ID
     * @return AD_Form_ID
     */
    protected int getAdFormId()
    {
    	return m_adFormId;
    }

    /**
     * Initialise the form
     *
     * @param adFormId	AD_Form_ID
     * @param name		Name of form
     */

    protected void init(int adFormId, String name)
    {
        if(adFormId <= 0)
        {
        	throw new IllegalArgumentException("Form Id is invalid");
	   	}

        m_adFormId = adFormId;
        //window title
        setTitle(name);
        m_name = name;

        initForm();
        
        addEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, this);
    }

    /**
     * Initialize form layout
     */
    abstract protected void initForm();

	/**
	 * Get form name
     * @return form name
     */
    public String getFormName() {
    	return m_name;
    }

	/**
	 * Create a new form corresponding to the specified identifier
	 *
	 * @param adFormID	AD_Form_ID
	 * @return The created form
	 */
	public static ADForm openForm (int adFormID)
	{
        return openForm(adFormID, null, null, null, false);
	}
	
	/**
	 * Open a form based on it's ID with the predefined context variables from menu
	 *
	 * @param formId
	 * @param predefinedContextVariables optional predefined context variables from menu
	 * @return The created form
	 */
	public static ADForm openForm(int formId, String predefinedContextVariables) {
		return openForm(formId, null, null, predefinedContextVariables, false);
	}

	/**
     * Open a form base on it's ID
     *
     * @param adFormID
     * @param gridTab
     * @return The created form
     */
	public static ADForm openForm (int adFormID, GridTab gridTab)
	{
        return openForm(adFormID, gridTab, null, null, false);
    }

	/**
     * Open a form base on it's ID and a Process Info parameters
     *
	 * @param adFormID
	 * @param gridTab
	 * @param pi
	 * @return The created form
	 */
	public static ADForm openForm (int adFormID, GridTab gridTab, ProcessInfo pi)
	{
        return openForm(adFormID, gridTab, pi, null, false);
    }

    /**
     * Open a form base on it's ID and a Process Info parameters with the predefined context variables from menu
     *
     * @param adFormID
     * @param gridTab
     * @param pi
     * @param predefinedContextVariables optional predefined context variables from menu
     * @param isSOTrx
     * @return The created form
     */
    public static ADForm openForm (int adFormID, GridTab gridTab, ProcessInfo pi, String predefinedContextVariables, boolean isSOTrx)
    {
		ADForm form;
		MForm mform = MForm.get(adFormID);
    	String formName = mform.getClassname();
    	String name = mform.get_Translation(MForm.COLUMNNAME_Name);

    	if (mform.get_ID() == 0 || formName == null)
    	{
			throw new ApplicationException("There is no form associated with the specified form ID");
    	}
    	else
    	{
    		if (logger.isLoggable(Level.INFO)) logger.info("AD_Form_ID=" + adFormID + " - Class=" + formName);

    		form = Extensions.getForm(formName);
    		if (form != null)
    		{
    			form.gridTab = gridTab;
                form.setProcessInfo(pi);
        		Env.setPredefinedVariables(Env.getCtx(), form.getWindowNo(), predefinedContextVariables);
        		Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", isSOTrx);
				form.init(adFormID, name);
		    	form.setAttribute(IDesktop.WINDOWNO_ATTRIBUTE, form.getWindowNo());	// for closing the window with shortcut
		    	SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, form);
		    	form.addEventListener(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT, form);
				return form;
    		}
    		else
    		{
    			throw new ApplicationException("Failed to open " + formName);
    		}
    	}
	}	//	openForm

    @Override
	public void onEvent(Event event) throws Exception
    {
		if (event.getName().equals(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT)) {
    		SessionManager.getAppDesktop().updateHelpContext(X_AD_CtxHelp.CTXTYPE_Form, getAdFormId());
		}
		else if (event.getName().equals(Events.ON_CTRL_KEY)) {
        	KeyEvent keyEvent = (KeyEvent) event;
        	if (LayoutUtils.isReallyVisible(this))
	        	this.onCtrlKeyEvent(keyEvent);
		}
		else if(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT.equals(event.getName())) {
        	IDesktop desktop = SessionManager.getAppDesktop();
        	if (m_WindowNo > 0 && desktop.isCloseTabWithShortcut())
        		desktop.closeWindow(m_WindowNo);
        	else
        		desktop.setCloseTabWithShortcut(true);
        }
    }

	/**
	 * @param pi
	 */
	public void setProcessInfo(ProcessInfo pi) {
		m_pi = pi;
	}

	/**
	 * @return ProcessInfo
	 */
	public ProcessInfo getProcessInfo()
	{
		return m_pi;
	}

	/**
	 * Set form controller
	 * @param customForm
	 */
	public void setICustomForm(IFormController customForm)
	{
		m_customForm = customForm;
	}

	/**
	 * Get form controller
	 * @return IFormController
	 */
	public IFormController getICustomForm()
	{
		return m_customForm;
	}
	
	/**
	 * Default to embedded mode, subclass may override this to open form in different mode
	 * @return Window mode
	 */
	public Mode getWindowMode() {
		return Mode.EMBEDDED;
	}
	
	private GridTab gridTab;
	
	/**
	 * @return GridTab
	 */
	public GridTab getGridTab()
	{
		return gridTab;
	}

	/**
	 * Handle shortcut key event
	 * @param keyEvent
	 */
	private void onCtrlKeyEvent(KeyEvent keyEvent) {
		if ((keyEvent.isAltKey() && keyEvent.getKeyCode() == 0x58)	// Alt-X
				|| (keyEvent.getKeyCode() == 0x1B && isUseEscForTabClosing)) { 	// ESC
			keyEvent.stopPropagation();
			Events.echoEvent(new Event(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT, this));
		}
	}

	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		if (m_WindowNo > 0)
			Env.clearWinContext(m_WindowNo);
	}
}
