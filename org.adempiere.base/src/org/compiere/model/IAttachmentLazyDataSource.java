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
 * Sponsor:                                                            *
 * - FH                                                                *
 * Contributors:                                                       *
 * - Carlos Ruiz                                                       *
 **********************************************************************/

package org.compiere.model;

import java.io.File;
import java.io.InputStream;

/**
 *	IDEMPIERE-4889 interface for lazy loading of attachment content
 * 	@author Carlos Ruiz - globalqss
 */
public interface IAttachmentLazyDataSource {

	/**
	 * Return a byte array containing the data from the Attachment Entry
	 * Usually the implementing class must have a constructor with the variable(s) required for loading later the data
	 * @return byte[] attachment content
	 */
	public byte[] getData();

    /**
     * Get input stream for attachment entry
     * @return input stream
     */
    InputStream getInputStream();

    /**
     * Get size of attachment entry
     * @return size
     */
    long getSize();

    /**
     * Get file attachment
     * @return file attachment or null
     */
    File getFile();

    /**
     * Clean up resources held
     */
    default void cleanUp() {}
}
