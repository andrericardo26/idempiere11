/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/

package org.compiere.model;

/**
 * Store provider interface for storage of attachment content
 */
public interface IAttachmentStore {

	/**
	 * Load binary attachment content
	 * @param attach
	 * @param prov
	 * @return true if successfully loaded
	 */
    public boolean loadLOBData(MAttachment attach,MStorageProvider prov);

    /**
     * Save attachment content
     * @param attach
     * @param prov
     * @return true if successfully save
     */
	boolean save(MAttachment attach, MStorageProvider prov);

    /**
     * Save attachment content
     * @param attach
     * @param prov
     * @param beforeSave true if call from beforeSave of attachment record, false if call from afterSave
     * @return true if success, false otherwise
     */
    default boolean save(MAttachment attach, MStorageProvider prov, boolean beforeSave) {
        // default to handle beforeSave only for existing behaviour before addition of the beforeSave flag
        if (beforeSave)
            return save(attach, prov);
        else
            return true;
    }

	/**
	 * Delete stored attachment content
	 * @param attach
	 * @param prov
	 * @return true if successfully deleted
	 */
	public boolean delete(MAttachment attach, MStorageProvider prov);

	/**
	 * Delete attachment content by index
	 * @param mAttachment
	 * @param provider
	 * @param index index of content to delete (for e.g zip entry #2 of a zip file)
	 * @return true if successfully deleted
	 */
	public boolean deleteEntry(MAttachment mAttachment, MStorageProvider provider, int index);

}
