/***********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - hengsin                         								   *
 **********************************************************************/
package org.adempiere.base.event.annotations;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.logging.Level;

import org.adempiere.base.Model;
import org.adempiere.base.event.EventHelper;
import org.adempiere.base.event.EventManager;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.osgi.service.event.Event;

/**
 * Event handler for PO related events. <br/>
 * Developers usually don't have to use this class directly; instead, the recommended approach is 
 * to subclass {@link ModelEventDelegate} and use model event topic annotations.
 * @author hengsin
 */
public final class ModelEventHandler<T extends PO> extends BaseEventHandler {

	private Class<T> modelClassType;
	private String tableName;
	private BiFunction<T, Event, ? extends ModelEventDelegate<T>> supplier;
	private static CLogger log = CLogger.getCLogger(ModelEventHandler.class);
	
	/**
	 * @param modelClassType
	 * @param delegateClass
	 * @param supplier
	 */
	public ModelEventHandler(Class<T> modelClassType, Class<? extends ModelEventDelegate<T>> delegateClass, 
			BiFunction<T, Event, ? extends ModelEventDelegate<T>> supplier) {
		super(delegateClass);
		this.supplier = supplier;
		this.modelClassType = modelClassType;
		findTableName();
	}

	/**
	 * Find table name property from annotation or static field (Table_Name).
	 */
	private void findTableName() {
		try {
			Model model = modelClassType.getSuperclass().getAnnotation(Model.class);
			if(model != null)
				this.tableName = model.table();
			else {
				Field field = modelClassType.getField("Table_Name");
                this.tableName = (String) field.get(null);
			}
			setEventPropertyFilter(EventManager.TABLE_NAME_PROPERTY, tableName);
		} catch (Exception e) { 
			if (e instanceof RuntimeException)
				throw (RuntimeException)e;
			else
				throw new RuntimeException(e);
		}
	}

	@Override
	public void handleEvent(Event event) {
		PO po = EventHelper.getPO(event);		
		if (po == null || modelClassType == null)
			return;
		
		if (!modelClassType.isAssignableFrom(po.getClass())) {
			if (log.isLoggable(Level.INFO))
		        log.info(String.format("ModelEventHandler %s was skipped: the po class %s is not assignable to the expected type %s",
		            delegateClass.getName(), po.getClass().getName(), modelClassType.getName()));
		    return;
		}
		
			
		super.handleEvent(event);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EventDelegate newEventDelegate(Event event) {
		PO po = EventHelper.getPO(event);
		return supplier.apply((T)po, event);
	}	
}
