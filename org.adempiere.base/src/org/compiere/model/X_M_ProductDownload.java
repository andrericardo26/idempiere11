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
/** Generated Model - DO NOT CHANGE */
package org.compiere.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.util.KeyNamePair;

/** Generated Model for M_ProductDownload
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="M_ProductDownload")
public class X_M_ProductDownload extends PO implements I_M_ProductDownload, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241222L;

    /** Standard Constructor */
    public X_M_ProductDownload (Properties ctx, int M_ProductDownload_ID, String trxName)
    {
      super (ctx, M_ProductDownload_ID, trxName);
      /** if (M_ProductDownload_ID == 0)
        {
			setDownloadURL (null);
			setM_ProductDownload_ID (0);
			setM_Product_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_M_ProductDownload (Properties ctx, int M_ProductDownload_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, M_ProductDownload_ID, trxName, virtualColumns);
      /** if (M_ProductDownload_ID == 0)
        {
			setDownloadURL (null);
			setM_ProductDownload_ID (0);
			setM_Product_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_M_ProductDownload (Properties ctx, String M_ProductDownload_UU, String trxName)
    {
      super (ctx, M_ProductDownload_UU, trxName);
      /** if (M_ProductDownload_UU == null)
        {
			setDownloadURL (null);
			setM_ProductDownload_ID (0);
			setM_Product_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_M_ProductDownload (Properties ctx, String M_ProductDownload_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, M_ProductDownload_UU, trxName, virtualColumns);
      /** if (M_ProductDownload_UU == null)
        {
			setDownloadURL (null);
			setM_ProductDownload_ID (0);
			setM_Product_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_M_ProductDownload (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_M_ProductDownload[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Download URL.
		@param DownloadURL URL of the Download files
	*/
	public void setDownloadURL (String DownloadURL)
	{
		set_Value (COLUMNNAME_DownloadURL, DownloadURL);
	}

	/** Get Download URL.
		@return URL of the Download files
	  */
	public String getDownloadURL()
	{
		return (String)get_Value(COLUMNNAME_DownloadURL);
	}

	/** Set Product Download.
		@param M_ProductDownload_ID Product downloads
	*/
	public void setM_ProductDownload_ID (int M_ProductDownload_ID)
	{
		if (M_ProductDownload_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_ProductDownload_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_ProductDownload_ID, Integer.valueOf(M_ProductDownload_ID));
	}

	/** Get Product Download.
		@return Product downloads
	  */
	public int getM_ProductDownload_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ProductDownload_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set M_ProductDownload_UU.
		@param M_ProductDownload_UU M_ProductDownload_UU
	*/
	public void setM_ProductDownload_UU (String M_ProductDownload_UU)
	{
		set_Value (COLUMNNAME_M_ProductDownload_UU, M_ProductDownload_UU);
	}

	/** Get M_ProductDownload_UU.
		@return M_ProductDownload_UU	  */
	public String getM_ProductDownload_UU()
	{
		return (String)get_Value(COLUMNNAME_M_ProductDownload_UU);
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
	*/
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getName());
    }
}