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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for M_ShippingTransactionLine
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="M_ShippingTransactionLine")
public class X_M_ShippingTransactionLine extends PO implements I_M_ShippingTransactionLine, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241222L;

    /** Standard Constructor */
    public X_M_ShippingTransactionLine (Properties ctx, int M_ShippingTransactionLine_ID, String trxName)
    {
      super (ctx, M_ShippingTransactionLine_ID, trxName);
      /** if (M_ShippingTransactionLine_ID == 0)
        {
			setM_ShippingTransactionLine_ID (0);
			setM_ShippingTransaction_ID (0);
			setProcessed (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_M_ShippingTransactionLine (Properties ctx, int M_ShippingTransactionLine_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, M_ShippingTransactionLine_ID, trxName, virtualColumns);
      /** if (M_ShippingTransactionLine_ID == 0)
        {
			setM_ShippingTransactionLine_ID (0);
			setM_ShippingTransaction_ID (0);
			setProcessed (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_M_ShippingTransactionLine (Properties ctx, String M_ShippingTransactionLine_UU, String trxName)
    {
      super (ctx, M_ShippingTransactionLine_UU, trxName);
      /** if (M_ShippingTransactionLine_UU == null)
        {
			setM_ShippingTransactionLine_ID (0);
			setM_ShippingTransaction_ID (0);
			setProcessed (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_M_ShippingTransactionLine (Properties ctx, String M_ShippingTransactionLine_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, M_ShippingTransactionLine_UU, trxName, virtualColumns);
      /** if (M_ShippingTransactionLine_UU == null)
        {
			setM_ShippingTransactionLine_ID (0);
			setM_ShippingTransaction_ID (0);
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_M_ShippingTransactionLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_M_ShippingTransactionLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_UOM getC_UOM_Length() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
			.getPO(getC_UOM_Length_ID(), get_TrxName());
	}

	/** Set UOM for Length.
		@param C_UOM_Length_ID Standard Unit of Measure for Length
	*/
	public void setC_UOM_Length_ID (int C_UOM_Length_ID)
	{
		if (C_UOM_Length_ID < 1)
			set_Value (COLUMNNAME_C_UOM_Length_ID, null);
		else
			set_Value (COLUMNNAME_C_UOM_Length_ID, Integer.valueOf(C_UOM_Length_ID));
	}

	/** Get UOM for Length.
		@return Standard Unit of Measure for Length
	  */
	public int getC_UOM_Length_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_Length_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM_Weight() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
			.getPO(getC_UOM_Weight_ID(), get_TrxName());
	}

	/** Set UOM for Weight.
		@param C_UOM_Weight_ID Standard Unit of Measure for Weight
	*/
	public void setC_UOM_Weight_ID (int C_UOM_Weight_ID)
	{
		if (C_UOM_Weight_ID < 1)
			set_Value (COLUMNNAME_C_UOM_Weight_ID, null);
		else
			set_Value (COLUMNNAME_C_UOM_Weight_ID, Integer.valueOf(C_UOM_Weight_ID));
	}

	/** Get UOM for Weight.
		@return Standard Unit of Measure for Weight
	  */
	public int getC_UOM_Weight_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_Weight_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Height.
		@param Height Height
	*/
	public void setHeight (BigDecimal Height)
	{
		set_Value (COLUMNNAME_Height, Height);
	}

	/** Get Height.
		@return Height	  */
	public BigDecimal getHeight()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Height);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Length.
		@param Length Length
	*/
	public void setLength (BigDecimal Length)
	{
		set_Value (COLUMNNAME_Length, Length);
	}

	/** Get Length.
		@return Length	  */
	public BigDecimal getLength()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Length);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_M_PackageMPS getM_PackageMPS() throws RuntimeException
	{
		return (org.compiere.model.I_M_PackageMPS)MTable.get(getCtx(), org.compiere.model.I_M_PackageMPS.Table_ID)
			.getPO(getM_PackageMPS_ID(), get_TrxName());
	}

	/** Set Package MPS.
		@param M_PackageMPS_ID Package MPS
	*/
	public void setM_PackageMPS_ID (int M_PackageMPS_ID)
	{
		if (M_PackageMPS_ID < 1)
			set_Value (COLUMNNAME_M_PackageMPS_ID, null);
		else
			set_Value (COLUMNNAME_M_PackageMPS_ID, Integer.valueOf(M_PackageMPS_ID));
	}

	/** Get Package MPS.
		@return Package MPS	  */
	public int getM_PackageMPS_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PackageMPS_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Shipping Transaction Line.
		@param M_ShippingTransactionLine_ID Shipping Transaction Line
	*/
	public void setM_ShippingTransactionLine_ID (int M_ShippingTransactionLine_ID)
	{
		if (M_ShippingTransactionLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_ShippingTransactionLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_ShippingTransactionLine_ID, Integer.valueOf(M_ShippingTransactionLine_ID));
	}

	/** Get Shipping Transaction Line.
		@return Shipping Transaction Line	  */
	public int getM_ShippingTransactionLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ShippingTransactionLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set M_ShippingTransactionLine_UU.
		@param M_ShippingTransactionLine_UU M_ShippingTransactionLine_UU
	*/
	public void setM_ShippingTransactionLine_UU (String M_ShippingTransactionLine_UU)
	{
		set_Value (COLUMNNAME_M_ShippingTransactionLine_UU, M_ShippingTransactionLine_UU);
	}

	/** Get M_ShippingTransactionLine_UU.
		@return M_ShippingTransactionLine_UU	  */
	public String getM_ShippingTransactionLine_UU()
	{
		return (String)get_Value(COLUMNNAME_M_ShippingTransactionLine_UU);
	}

	public org.compiere.model.I_M_ShippingTransaction getM_ShippingTransaction() throws RuntimeException
	{
		return (org.compiere.model.I_M_ShippingTransaction)MTable.get(getCtx(), org.compiere.model.I_M_ShippingTransaction.Table_ID)
			.getPO(getM_ShippingTransaction_ID(), get_TrxName());
	}

	/** Set Shipping Transaction.
		@param M_ShippingTransaction_ID Shipping Transaction
	*/
	public void setM_ShippingTransaction_ID (int M_ShippingTransaction_ID)
	{
		if (M_ShippingTransaction_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_ShippingTransaction_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_ShippingTransaction_ID, Integer.valueOf(M_ShippingTransaction_ID));
	}

	/** Get Shipping Transaction.
		@return Shipping Transaction	  */
	public int getM_ShippingTransaction_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ShippingTransaction_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), String.valueOf(getM_ShippingTransaction_ID()));
    }

	/** Set Master Tracking No.
		@param MasterTrackingNo Master Tracking No
	*/
	public void setMasterTrackingNo (String MasterTrackingNo)
	{
		set_Value (COLUMNNAME_MasterTrackingNo, MasterTrackingNo);
	}

	/** Get Master Tracking No.
		@return Master Tracking No	  */
	public String getMasterTrackingNo()
	{
		return (String)get_Value(COLUMNNAME_MasterTrackingNo);
	}

	/** Set Price.
		@param Price Price
	*/
	public void setPrice (BigDecimal Price)
	{
		set_Value (COLUMNNAME_Price, Price);
	}

	/** Get Price.
		@return Price
	  */
	public BigDecimal getPrice()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Price);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sequence.
		@param SeqNo Method of ordering records; lowest number comes first
	*/
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Tracking No.
		@param TrackingNo Number to track the shipment
	*/
	public void setTrackingNo (String TrackingNo)
	{
		set_Value (COLUMNNAME_TrackingNo, TrackingNo);
	}

	/** Get Tracking No.
		@return Number to track the shipment
	  */
	public String getTrackingNo()
	{
		return (String)get_Value(COLUMNNAME_TrackingNo);
	}

	/** Set Weight.
		@param Weight Weight of a product
	*/
	public void setWeight (BigDecimal Weight)
	{
		set_Value (COLUMNNAME_Weight, Weight);
	}

	/** Get Weight.
		@return Weight of a product
	  */
	public BigDecimal getWeight()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Weight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Width.
		@param Width Width
	*/
	public void setWidth (BigDecimal Width)
	{
		set_Value (COLUMNNAME_Width, Width);
	}

	/** Get Width.
		@return Width	  */
	public BigDecimal getWidth()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Width);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}