/******************************************************************************
 * Product: Posterita Ajax UI                                                 *
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

package org.adempiere.webui.component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.compiere.util.KeyNamePair;
import org.compiere.util.ValueNamePair;

/**
 * Extend {@link org.zkoss.zul.Listitem}
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Mar 25, 2007
 */
public class ListItem extends org.zkoss.zul.Listitem
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -8052056834118074979L;
	@Deprecated
	private PropertyChangeSupport m_propertyChangeListeners = new PropertyChangeSupport(this);
    
	/**
	 * @param label
	 * @param value
	 */
    public ListItem(String label, Object value)
    {
        super(label, value);
    }
    
    /**
     * Default constructor
     */
    public ListItem()
    {
    	super();
    }
    
    /**
     * @param l PropertyChangeListener
     * @deprecated not implemented
     */
    @Deprecated
    public synchronized void addPropertyChangeListener(PropertyChangeListener l)
	{
		m_propertyChangeListeners.addPropertyChangeListener(l);
	}
    
    /**
     * @return KeyNamePair(Value, Label)
     */
    public KeyNamePair toKeyNamePair() 
    {
    	return new KeyNamePair((Integer)getValue(), getLabel());
    }
    
    /**
     * @return ValueNamePair(Value, Label)
     */
    public ValueNamePair toValueNamePair() {
    	return new ValueNamePair((String)getValue(), getLabel());
    }
    
    @Override
    public String toString() {
    	return getValue() != null ? getValue().toString() : "";
    }

}
