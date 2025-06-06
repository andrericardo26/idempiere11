/******************************************************************************
 * Copyright (C) 2012 Elaine Tan                                              *
 * Copyright (C) 2012 Trek Global
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

package org.adempiere.webui.panel;

import static org.compiere.model.SystemIDs.TREE_MENUPRIMARY;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import org.adempiere.webui.apps.MenuSearchController;
import org.adempiere.webui.exception.ApplicationException;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.Icon;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MMenu;
import org.compiere.model.MToolBarButtonRestrict;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

/**
 * Abstract base class for Menu Tree Panel.<br/>
 * Menu tree component is loaded and created but not added to panel.
 * @author Elaine
 * @date July 31, 2012
 */
public abstract class AbstractMenuPanel extends Panel implements EventListener<Event> {

	/** Treeitem attribute to store the type of menu item (report, window, etc) */
	public static final String MENU_TYPE_ATTRIBUTE = "menu.type";
	
	/** Treeitem attribute to store the name of a menu item */
	public static final String MENU_LABEL_ATTRIBUTE = "menu.label";

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -6160708371157917922L;
	
	/** Event queue name */
	public static final String MENU_ITEM_SELECTED_QUEUE = "MENU_ITEM_SELECTED_QUEUE";
	
	private Properties ctx;
    private Tree menuTree;

    /** Listener for {@link #MENU_ITEM_SELECTED_QUEUE} event queue */
	private EventListener<Event> listener;
    
	/**
	 * @param parent
	 */
    public AbstractMenuPanel(Component parent)
    {
    	if (parent != null)
    		this.setParent(parent);
        init();            	
    }
    
    /**
     * Initialize {@link #menuTree}
     */
    protected void init() {
		ctx = Env.getCtx();
        int adRoleId = Env.getAD_Role_ID(ctx);
        int adTreeId = getTreeId(ctx, adRoleId);
        MTree mTree = new MTree(ctx, adTreeId, false, true, null);        
        MTreeNode rootNode = mTree.getRoot();
        initComponents();
        initMenu(rootNode);
    }
    
    /**
     * Create components
     */
    protected void initComponents()
    {
    	this.setSclass("menu-panel");
    	ZKUpdateUtil.setVflex(this, "1");
    	
        menuTree = new Tree();
        menuTree.setMultiple(false);
        menuTree.setId("mnuMain");
        ZKUpdateUtil.setVflex(menuTree, "1");
        menuTree.setSizedByContent(false);
        menuTree.setPageSize(-1); // Due to bug in the new paging functionality
    }
    
    /**
     * Fill {@link #menuTree} from rootNode
     * @param rootNode
     */
    private void initMenu(MTreeNode rootNode)
    {
        Treecols treeCols = new Treecols();
        Treecol treeCol = new Treecol();
        
        Treechildren rootTreeChildren = new Treechildren();        
        
        treeCols.appendChild(treeCol);
        menuTree.appendChild(treeCols);
        menuTree.appendChild(rootTreeChildren);
        
        generateMenu(rootTreeChildren, rootNode);
    }
    
    /**
     * @param ctx
     * @param adRoleId
     * @return AD_Tree_ID for role
     */
    private int getTreeId(Properties ctx, int adRoleId)
    {
        int AD_Tree_ID = DB.getSQLValue(null,
                "SELECT COALESCE(r.AD_Tree_Menu_ID, ci.AD_Tree_Menu_ID)" 
                + "FROM AD_ClientInfo ci" 
                + " INNER JOIN AD_Role r ON (ci.AD_Client_ID=r.AD_Client_ID) "
                + "WHERE AD_Role_ID=?", adRoleId);
        if (AD_Tree_ID <= 0)
            AD_Tree_ID = TREE_MENUPRIMARY;    //  Menu
        return AD_Tree_ID;
    }
    
    /**
     * Fill treeChildren from mNode
     * @param treeChildren
     * @param mNode
     */
    private void generateMenu(Treechildren treeChildren, MTreeNode mNode)
    {
        Enumeration<?> nodeEnum = mNode.children();
      
        while(nodeEnum.hasMoreElements())
        {
            MTreeNode mChildNode = (MTreeNode)nodeEnum.nextElement();
            Treeitem treeitem = new Treeitem();
            treeChildren.appendChild(treeitem);
            treeitem.setTooltiptext(mChildNode.getDescription());            
            
            if(mChildNode.getChildCount() != 0)
            {
                treeitem.setOpen(false);
                treeitem.setLabel(mChildNode.getName());
                Treecell cell = (Treecell)treeitem.getTreerow().getFirstChild();
                cell.setSclass("menu-treecell-cnt");
                Treechildren treeItemChildren = new Treechildren();
                treeitem.appendChild(treeItemChildren);
                generateMenu(treeItemChildren, mChildNode);
                if (treeItemChildren.getChildren().size() == 0)
                {
                	treeItemChildren.detach();
                }
                
                treeitem.getTreerow().addEventListener(Events.ON_CLICK, this);
            }
            else
            {
                treeitem.setValue(String.valueOf(mChildNode.getNode_ID()));
                Treerow treeRow = new Treerow();
                treeitem.appendChild(treeRow);
                Treecell treeCell = new Treecell();
                treeCell.setSclass("menu-treecell-cnt");
                treeRow.appendChild(treeCell);
                A link = new A();
                treeCell.appendChild(link);
                if (mChildNode.isReport())
                {
                	if (ThemeManager.isUseFontIconForImage())
                		link.setIconSclass(Icon.getIconSclass(Icon.REPORT));
                	else
                		link.setImage(ThemeManager.getThemeResource("images/mReport.png"));
                	treeitem.setAttribute(MENU_TYPE_ATTRIBUTE, "report");
                }
                else if (mChildNode.isProcess() || mChildNode.isTask())
                {
                	if (ThemeManager.isUseFontIconForImage())
                		link.setIconSclass(Icon.getIconSclass(Icon.PROCESS));
                	else
                		link.setImage(ThemeManager.getThemeResource("images/mProcess.png"));
                	treeitem.setAttribute(MENU_TYPE_ATTRIBUTE, "process");
                }
                else if (mChildNode.isWorkFlow())
                {
                	if (ThemeManager.isUseFontIconForImage())
                		link.setIconSclass(Icon.getIconSclass(Icon.WORKFLOW));
                	else
                		link.setImage(ThemeManager.getThemeResource("images/mWorkFlow.png"));
                	treeitem.setAttribute(MENU_TYPE_ATTRIBUTE, "workflow");
                }
                else if (mChildNode.isForm())
                {
                	if (ThemeManager.isUseFontIconForImage())
                		link.setIconSclass(Icon.getIconSclass(Icon.FORM));
                	else
                		link.setImage(ThemeManager.getThemeResource("images/mForm.png"));
                	treeitem.setAttribute(MENU_TYPE_ATTRIBUTE, "form");
                }
                else if (mChildNode.isInfo())
                {
                	if (ThemeManager.isUseFontIconForImage())
                		link.setIconSclass(Icon.getIconSclass(Icon.INFO));
                	else
                		link.setImage(ThemeManager.getThemeResource("images/mInfo.png"));
                	treeitem.setAttribute(MENU_TYPE_ATTRIBUTE, "info");
                }
                else // Window
                {
                	if (ThemeManager.isUseFontIconForImage())
                		link.setIconSclass(Icon.getIconSclass(Icon.WINDOW));
                	else
                		link.setImage(ThemeManager.getThemeResource("images/mWindow.png"));
                	treeitem.setAttribute(MENU_TYPE_ATTRIBUTE, "window");

                	Toolbarbutton newBtn = createNewButton();
                	treeCell.appendChild(newBtn);
                	newBtn.addEventListener(Events.ON_CLICK, this);
                }
                treeitem.addEventListener(Events.ON_OK, this);
                link.setLabel(mChildNode.getName());
                treeitem.setAttribute(MENU_LABEL_ATTRIBUTE, link.getLabel());
                
                link.addEventListener(Events.ON_CLICK, this);
                link.setSclass("menu-href");
                treeitem.getTreerow().setDraggable("favourite"); // Elaine 2008/07/24
                treeitem.setAttribute(MenuSearchController.M_TREE_NODE_ATTR, mChildNode);
            }
        }
    }
    
    /**
     * Create new record button
     * @return Toolbarbutton
     */
    public Toolbarbutton createNewButton()
    {
    	Toolbarbutton newBtn = new Toolbarbutton(null);
    	if (ThemeManager.isUseFontIconForImage())
		{
			newBtn.setIconSclass(Icon.getIconSclass(Icon.NEW));
		}
    	else {
    		newBtn.setImage(ThemeManager.getThemeResource("images/New10.png"));
    	}
    	newBtn.setSclass("menu-href-newbtn");
    	return newBtn;
    }
    
    @Override
    public void onEvent(Event event)
    {
        Component comp = event.getTarget();
        String eventName = event.getName();
        if ((eventName.equals(Events.ON_CLICK)) || (eventName.equals(Events.ON_OK)))
        {
        	doOnClick(comp, event.getData());
        }
    }
    
    /**
     * Handle onClick and onOk event for menu tree item.<br/>
     * The event from global search and application menu tree will be routed to here.
     * @param comp
     * @param eventData
     */
    private void doOnClick(Component comp, Object eventData) {
    	boolean newRecord = false;
		if (comp instanceof A) {
			comp = comp.getParent().getParent();
		}
		if (comp instanceof Toolbarbutton) {
			comp = comp.getParent().getParent();
			newRecord = true;
		} else if (eventData != null && eventData instanceof Boolean) {
			newRecord = (Boolean)eventData;
		}
		if (comp instanceof Treeitem)
		{
			Treeitem selectedItem = (Treeitem) comp;
			if (newRecord) {
				MMenu menu = MMenu.get(Integer.parseInt(selectedItem.getValue()));
				if (MToolBarButtonRestrict.isNewButtonRestricted(menu.getAD_Window_ID()))
					newRecord = false;
			}
			if(selectedItem.getValue() != null)
			{
				if (newRecord)
				{
					onNewRecord(selectedItem);
				}
				else
				{
					fireMenuSelectedEvent(selectedItem);
				}
			}
		}
		if (comp instanceof Treerow) 
		{
			Treeitem selectedItem = (Treeitem) comp.getParent();
			if (newRecord) {
				MMenu menu = MMenu.get(Integer.parseInt(selectedItem.getValue()));
				if (MToolBarButtonRestrict.isNewButtonRestricted(menu.getAD_Window_ID()))
					newRecord = false;
			}
		    if(selectedItem.getValue() != null)
		    {
		    	if (newRecord)
		    	{
		    		onNewRecord(selectedItem);
		    	}
		    	else
		    	{
		    		fireMenuSelectedEvent(selectedItem);
		    	}
		    }
		    else
		    	selectedItem.setOpen(!selectedItem.isOpen());
		    selectedItem.setSelected(true);
		    
		    //publish event to sync the selection of other menu tree (if any)
	        EventQueues.lookup(MENU_ITEM_SELECTED_QUEUE, EventQueues.DESKTOP, true).publish(new Event(Events.ON_SELECT, null, selectedItem));
		}
	}
    
    /**
     * Handle new record event
     * @param selectedItem
     */
    private void onNewRecord(Treeitem selectedItem) {
    	try
        {
    		if (getParent() instanceof Popup) {
    			((Popup)getParent()).close();
    		}
			int menuId = Integer.parseInt((String)selectedItem.getValue());
			SessionManager.getAppDesktop().onNewRecord(menuId);			
        }
        catch (Exception e)
        {
            throw new ApplicationException(e.getMessage(), e);
        }
		
	}

    /**
     * Handle onClick event of tree item
     * @param selectedItem
     */
	protected void fireMenuSelectedEvent(Treeitem selectedItem) {
    	int nodeId = Integer.parseInt((String)selectedItem.getValue());
       
    	try
        {
    		if (getParent() instanceof Popup) {
				((Popup)getParent()).close();
			}
    		SessionManager.getAppDesktop().onMenuSelected(nodeId);
        }
        catch (Exception e)
        {
            throw new ApplicationException(e.getMessage(), e);
        }		
	}

	/**
	 * @return Menu Tree
	 */
	public Tree getMenuTree() 
	{
		return menuTree;
	}
	
	/**
	 * @return ctx
	 */
	public Properties getCtx()
	{
		return ctx;
	}
	
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#onPageDetached(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		if (listener != null) {
			try {
				EventQueue<Event> queue = EventQueues.lookup(MENU_ITEM_SELECTED_QUEUE, EventQueues.DESKTOP, true);
				if (queue != null)
					queue.unsubscribe(listener);
			} finally {
				listener = null;
			}
			
		}
	}
	
	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (listener == null) {
			listener = new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					if (event.getName() == Events.ON_SELECT)
					{
						Treeitem selectedItem = (Treeitem) event.getData();
					    	
						if (selectedItem != null)
						{
							Object value = selectedItem.getValue();
							if (value != null)
							{
								if (menuTree.getSelectedItem() != null && menuTree.getSelectedItem().getValue() != null && menuTree.getSelectedItem().getValue().equals(value))
									return;
								
								Collection<Treeitem> items = menuTree.getItems();
								for (Treeitem item : items)
								{
									if (item != null && item.getValue() != null && item.getValue().equals(value))
									{
										TreeSearchPanel.select(item);
										return;
									}
								}
							}
							else
							{
								String label = selectedItem.getLabel();
								if (menuTree.getSelectedItem() != null && menuTree.getSelectedItem().getLabel() != null && menuTree.getSelectedItem().getLabel().equals(label))
									return;
								
								Collection<Treeitem> items = menuTree.getItems();
								for (Treeitem item : items)
								{
									if (item != null && item.getLabel() != null && item.getLabel().equals(label))
									{
										TreeSearchPanel.select(item);								
										return;
									}
								}
							}
						}
					}
				}
			};
		}
		EventQueues.lookup(MENU_ITEM_SELECTED_QUEUE, EventQueues.DESKTOP, true).subscribe(listener);
	}
	
}
