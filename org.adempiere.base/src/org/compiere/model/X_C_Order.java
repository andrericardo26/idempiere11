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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for C_Order
 *  @author iDempiere (generated)
 *  @version Release 13 - $Id$ */
@org.adempiere.base.Model(table="C_Order")
public class X_C_Order extends PO implements I_C_Order, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250307L;

    /** Standard Constructor */
    public X_C_Order (Properties ctx, int C_Order_ID, String trxName)
    {
      super (ctx, C_Order_ID, trxName);
      /** if (C_Order_ID == 0)
        {
			setC_BPartner_ID (0);
			setC_BPartner_Location_ID (0);
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_DocTypeTarget_ID (0);
			setC_DocType_ID (0);
// 0
			setC_Order_ID (0);
			setC_PaymentTerm_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDatePromised (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDeliveryRule (null);
// F
			setDeliveryViaRule (null);
// P
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setFreightAmt (Env.ZERO);
			setFreightCostRule (null);
// I
			setGrandTotal (Env.ZERO);
			setInvoiceRule (null);
// I
			setIsApproved (false);
// N
			setIsCreditApproved (false);
			setIsDelivered (false);
			setIsDiscountPrinted (false);
			setIsDropShip (false);
// N
			setIsInvoiced (false);
			setIsPayScheduleValid (false);
// N
			setIsPrinted (false);
			setIsPriviledgedRate (false);
// N
			setIsSOTrx (false);
// @IsSOTrx@
			setIsSelected (false);
			setIsSelfService (false);
			setIsTaxIncluded (false);
			setIsTransferred (false);
			setM_PriceList_ID (0);
			setM_Warehouse_ID (0);
			setPaymentRule (null);
// B
			setPosted (false);
// N
			setPriorityRule (null);
// 5
			setProcessed (false);
			setSalesRep_ID (0);
			setSendEMail (false);
			setTotalLines (Env.ZERO);
        } */
    }

    /** Standard Constructor */
    public X_C_Order (Properties ctx, int C_Order_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, C_Order_ID, trxName, virtualColumns);
      /** if (C_Order_ID == 0)
        {
			setC_BPartner_ID (0);
			setC_BPartner_Location_ID (0);
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_DocTypeTarget_ID (0);
			setC_DocType_ID (0);
// 0
			setC_Order_ID (0);
			setC_PaymentTerm_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDatePromised (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDeliveryRule (null);
// F
			setDeliveryViaRule (null);
// P
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setFreightAmt (Env.ZERO);
			setFreightCostRule (null);
// I
			setGrandTotal (Env.ZERO);
			setInvoiceRule (null);
// I
			setIsApproved (false);
// N
			setIsCreditApproved (false);
			setIsDelivered (false);
			setIsDiscountPrinted (false);
			setIsDropShip (false);
// N
			setIsInvoiced (false);
			setIsPayScheduleValid (false);
// N
			setIsPrinted (false);
			setIsPriviledgedRate (false);
// N
			setIsSOTrx (false);
// @IsSOTrx@
			setIsSelected (false);
			setIsSelfService (false);
			setIsTaxIncluded (false);
			setIsTransferred (false);
			setM_PriceList_ID (0);
			setM_Warehouse_ID (0);
			setPaymentRule (null);
// B
			setPosted (false);
// N
			setPriorityRule (null);
// 5
			setProcessed (false);
			setSalesRep_ID (0);
			setSendEMail (false);
			setTotalLines (Env.ZERO);
        } */
    }

    /** Standard Constructor */
    public X_C_Order (Properties ctx, String C_Order_UU, String trxName)
    {
      super (ctx, C_Order_UU, trxName);
      /** if (C_Order_UU == null)
        {
			setC_BPartner_ID (0);
			setC_BPartner_Location_ID (0);
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_DocTypeTarget_ID (0);
			setC_DocType_ID (0);
// 0
			setC_Order_ID (0);
			setC_PaymentTerm_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDatePromised (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDeliveryRule (null);
// F
			setDeliveryViaRule (null);
// P
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setFreightAmt (Env.ZERO);
			setFreightCostRule (null);
// I
			setGrandTotal (Env.ZERO);
			setInvoiceRule (null);
// I
			setIsApproved (false);
// N
			setIsCreditApproved (false);
			setIsDelivered (false);
			setIsDiscountPrinted (false);
			setIsDropShip (false);
// N
			setIsInvoiced (false);
			setIsPayScheduleValid (false);
// N
			setIsPrinted (false);
			setIsPriviledgedRate (false);
// N
			setIsSOTrx (false);
// @IsSOTrx@
			setIsSelected (false);
			setIsSelfService (false);
			setIsTaxIncluded (false);
			setIsTransferred (false);
			setM_PriceList_ID (0);
			setM_Warehouse_ID (0);
			setPaymentRule (null);
// B
			setPosted (false);
// N
			setPriorityRule (null);
// 5
			setProcessed (false);
			setSalesRep_ID (0);
			setSendEMail (false);
			setTotalLines (Env.ZERO);
        } */
    }

    /** Standard Constructor */
    public X_C_Order (Properties ctx, String C_Order_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, C_Order_UU, trxName, virtualColumns);
      /** if (C_Order_UU == null)
        {
			setC_BPartner_ID (0);
			setC_BPartner_Location_ID (0);
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_DocTypeTarget_ID (0);
			setC_DocType_ID (0);
// 0
			setC_Order_ID (0);
			setC_PaymentTerm_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDatePromised (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDeliveryRule (null);
// F
			setDeliveryViaRule (null);
// P
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setFreightAmt (Env.ZERO);
			setFreightCostRule (null);
// I
			setGrandTotal (Env.ZERO);
			setInvoiceRule (null);
// I
			setIsApproved (false);
// N
			setIsCreditApproved (false);
			setIsDelivered (false);
			setIsDiscountPrinted (false);
			setIsDropShip (false);
// N
			setIsInvoiced (false);
			setIsPayScheduleValid (false);
// N
			setIsPrinted (false);
			setIsPriviledgedRate (false);
// N
			setIsSOTrx (false);
// @IsSOTrx@
			setIsSelected (false);
			setIsSelfService (false);
			setIsTaxIncluded (false);
			setIsTransferred (false);
			setM_PriceList_ID (0);
			setM_Warehouse_ID (0);
			setPaymentRule (null);
// B
			setPosted (false);
// N
			setPriorityRule (null);
// 5
			setProcessed (false);
			setSalesRep_ID (0);
			setSendEMail (false);
			setTotalLines (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_C_Order (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org
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
      StringBuilder sb = new StringBuilder ("X_C_Order[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
	*/
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1)
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_Value (COLUMNNAME_AD_User_ID, null);
		else
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Amount Refunded.
		@param AmountRefunded Amount Refunded
	*/
	public void setAmountRefunded (BigDecimal AmountRefunded)
	{
		set_Value (COLUMNNAME_AmountRefunded, AmountRefunded);
	}

	/** Get Amount Refunded.
		@return Amount Refunded	  */
	public BigDecimal getAmountRefunded()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmountRefunded);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Amount Tendered.
		@param AmountTendered Amount Tendered
	*/
	public void setAmountTendered (BigDecimal AmountTendered)
	{
		set_Value (COLUMNNAME_AmountTendered, AmountTendered);
	}

	/** Get Amount Tendered.
		@return Amount Tendered	  */
	public BigDecimal getAmountTendered()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmountTendered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_BPartner getBill_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getBill_BPartner_ID(), get_TrxName());
	}

	/** Set Invoice Partner.
		@param Bill_BPartner_ID Business Partner to be invoiced
	*/
	public void setBill_BPartner_ID (int Bill_BPartner_ID)
	{
		if (Bill_BPartner_ID < 1)
			set_Value (COLUMNNAME_Bill_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_Bill_BPartner_ID, Integer.valueOf(Bill_BPartner_ID));
	}

	/** Get Invoice Partner.
		@return Business Partner to be invoiced
	  */
	public int getBill_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Bill_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getBill_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getBill_Location_ID(), get_TrxName());
	}

	/** Set Invoice Location.
		@param Bill_Location_ID Business Partner Location for invoicing
	*/
	public void setBill_Location_ID (int Bill_Location_ID)
	{
		if (Bill_Location_ID < 1)
			set_Value (COLUMNNAME_Bill_Location_ID, null);
		else
			set_Value (COLUMNNAME_Bill_Location_ID, Integer.valueOf(Bill_Location_ID));
	}

	/** Get Invoice Location.
		@return Business Partner Location for invoicing
	  */
	public int getBill_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Bill_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getBill_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getBill_User_ID(), get_TrxName());
	}

	/** Set Invoice Contact.
		@param Bill_User_ID Business Partner Contact for invoicing
	*/
	public void setBill_User_ID (int Bill_User_ID)
	{
		if (Bill_User_ID < 1)
			set_Value (COLUMNNAME_Bill_User_ID, null);
		else
			set_Value (COLUMNNAME_Bill_User_ID, Integer.valueOf(Bill_User_ID));
	}

	/** Get Invoice Contact.
		@return Business Partner Contact for invoicing
	  */
	public int getBill_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Bill_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_ID)
			.getPO(getC_Activity_ID(), get_TrxName());
	}

	/** Set Activity.
		@param C_Activity_ID Business Activity
	*/
	public void setC_Activity_ID (int C_Activity_ID)
	{
		if (C_Activity_ID < 1)
			set_Value (COLUMNNAME_C_Activity_ID, null);
		else
			set_Value (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
	}

	/** Get Activity.
		@return Business Activity
	  */
	public int getC_Activity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());
	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
	*/
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
	{
		if (C_BPartner_Location_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_Location_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
	}

	/** Get Partner Location.
		@return Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
	{
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_ID)
			.getPO(getC_Campaign_ID(), get_TrxName());
	}

	/** Set Campaign.
		@param C_Campaign_ID Marketing Campaign
	*/
	public void setC_Campaign_ID (int C_Campaign_ID)
	{
		if (C_Campaign_ID < 1)
			set_Value (COLUMNNAME_C_Campaign_ID, null);
		else
			set_Value (COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
	}

	/** Get Campaign.
		@return Marketing Campaign
	  */
	public int getC_Campaign_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_CashLine getC_CashLine() throws RuntimeException
	{
		return (org.compiere.model.I_C_CashLine)MTable.get(getCtx(), org.compiere.model.I_C_CashLine.Table_ID)
			.getPO(getC_CashLine_ID(), get_TrxName());
	}

	/** Set Cash Journal Line.
		@param C_CashLine_ID Cash Journal Line
	*/
	public void setC_CashLine_ID (int C_CashLine_ID)
	{
		if (C_CashLine_ID < 1)
			set_Value (COLUMNNAME_C_CashLine_ID, null);
		else
			set_Value (COLUMNNAME_C_CashLine_ID, Integer.valueOf(C_CashLine_ID));
	}

	/** Get Cash Journal Line.
		@return Cash Journal Line
	  */
	public int getC_CashLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_CashLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_CashPlanLine getC_CashPlanLine() throws RuntimeException
	{
		return (org.compiere.model.I_C_CashPlanLine)MTable.get(getCtx(), org.compiere.model.I_C_CashPlanLine.Table_ID)
			.getPO(getC_CashPlanLine_ID(), get_TrxName());
	}

	/** Set Cash Plan Line.
		@param C_CashPlanLine_ID Cash Plan Line
	*/
	public void setC_CashPlanLine_ID (int C_CashPlanLine_ID)
	{
		if (C_CashPlanLine_ID < 1)
			set_Value (COLUMNNAME_C_CashPlanLine_ID, null);
		else
			set_Value (COLUMNNAME_C_CashPlanLine_ID, Integer.valueOf(C_CashPlanLine_ID));
	}

	/** Get Cash Plan Line.
		@return Cash Plan Line	  */
	public int getC_CashPlanLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_CashPlanLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
	{
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_ID)
			.getPO(getC_Charge_ID(), get_TrxName());
	}

	/** Set Charge.
		@param C_Charge_ID Additional document charges
	*/
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1)
			set_Value (COLUMNNAME_C_Charge_ID, null);
		else
			set_Value (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ConversionType getC_ConversionType() throws RuntimeException
	{
		return (org.compiere.model.I_C_ConversionType)MTable.get(getCtx(), org.compiere.model.I_C_ConversionType.Table_ID)
			.getPO(getC_ConversionType_ID(), get_TrxName());
	}

	/** Set Currency Type.
		@param C_ConversionType_ID Currency Conversion Rate Type
	*/
	public void setC_ConversionType_ID (int C_ConversionType_ID)
	{
		if (C_ConversionType_ID < 1)
			set_Value (COLUMNNAME_C_ConversionType_ID, null);
		else
			set_Value (COLUMNNAME_C_ConversionType_ID, Integer.valueOf(C_ConversionType_ID));
	}

	/** Get Currency Type.
		@return Currency Conversion Rate Type
	  */
	public int getC_ConversionType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ConversionType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_CostCenter getC_CostCenter() throws RuntimeException
	{
		return (org.compiere.model.I_C_CostCenter)MTable.get(getCtx(), org.compiere.model.I_C_CostCenter.Table_ID)
			.getPO(getC_CostCenter_ID(), get_TrxName());
	}

	/** Set Cost Center.
		@param C_CostCenter_ID Cost Center
	*/
	public void setC_CostCenter_ID (int C_CostCenter_ID)
	{
		if (C_CostCenter_ID < 1)
			set_Value (COLUMNNAME_C_CostCenter_ID, null);
		else
			set_Value (COLUMNNAME_C_CostCenter_ID, Integer.valueOf(C_CostCenter_ID));
	}

	/** Get Cost Center.
		@return Cost Center	  */
	public int getC_CostCenter_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_CostCenter_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
	{
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_ID)
			.getPO(getC_Currency_ID(), get_TrxName());
	}

	/** Set Currency.
		@param C_Currency_ID The Currency for this record
	*/
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Currency_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Department getC_Department() throws RuntimeException
	{
		return (org.compiere.model.I_C_Department)MTable.get(getCtx(), org.compiere.model.I_C_Department.Table_ID)
			.getPO(getC_Department_ID(), get_TrxName());
	}

	/** Set Department.
		@param C_Department_ID Department
	*/
	public void setC_Department_ID (int C_Department_ID)
	{
		if (C_Department_ID < 1)
			set_Value (COLUMNNAME_C_Department_ID, null);
		else
			set_Value (COLUMNNAME_C_Department_ID, Integer.valueOf(C_Department_ID));
	}

	/** Get Department.
		@return Department	  */
	public int getC_Department_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Department_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocTypeTarget() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getC_DocTypeTarget_ID(), get_TrxName());
	}

	/** Set Target Document Type.
		@param C_DocTypeTarget_ID Target document type for conversing documents
	*/
	public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID)
	{
		if (C_DocTypeTarget_ID < 1)
			set_Value (COLUMNNAME_C_DocTypeTarget_ID, null);
		else
			set_Value (COLUMNNAME_C_DocTypeTarget_ID, Integer.valueOf(C_DocTypeTarget_ID));
	}

	/** Get Target Document Type.
		@return Target document type for conversing documents
	  */
	public int getC_DocTypeTarget_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocTypeTarget_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getC_DocType_ID(), get_TrxName());
	}

	/** Set Document Type.
		@param C_DocType_ID Document type or rules
	*/
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0)
			set_ValueNoCheck (COLUMNNAME_C_DocType_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Opportunity getC_Opportunity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Opportunity)MTable.get(getCtx(), org.compiere.model.I_C_Opportunity.Table_ID)
			.getPO(getC_Opportunity_ID(), get_TrxName());
	}

	/** Set Sales Opportunity.
		@param C_Opportunity_ID Sales Opportunity
	*/
	public void setC_Opportunity_ID (int C_Opportunity_ID)
	{
		if (C_Opportunity_ID < 1)
			set_Value (COLUMNNAME_C_Opportunity_ID, null);
		else
			set_Value (COLUMNNAME_C_Opportunity_ID, Integer.valueOf(C_Opportunity_ID));
	}

	/** Get Sales Opportunity.
		@return Sales Opportunity	  */
	public int getC_Opportunity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Opportunity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_OrderSource getC_OrderSource() throws RuntimeException
	{
		return (org.compiere.model.I_C_OrderSource)MTable.get(getCtx(), org.compiere.model.I_C_OrderSource.Table_ID)
			.getPO(getC_OrderSource_ID(), get_TrxName());
	}

	/** Set Order Source.
		@param C_OrderSource_ID Order Source
	*/
	public void setC_OrderSource_ID (int C_OrderSource_ID)
	{
		if (C_OrderSource_ID < 1)
			set_Value (COLUMNNAME_C_OrderSource_ID, null);
		else
			set_Value (COLUMNNAME_C_OrderSource_ID, Integer.valueOf(C_OrderSource_ID));
	}

	/** Get Order Source.
		@return Order Source	  */
	public int getC_OrderSource_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderSource_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Order.
		@param C_Order_ID Order
	*/
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_Order_UU.
		@param C_Order_UU C_Order_UU
	*/
	public void setC_Order_UU (String C_Order_UU)
	{
		set_Value (COLUMNNAME_C_Order_UU, C_Order_UU);
	}

	/** Get C_Order_UU.
		@return C_Order_UU	  */
	public String getC_Order_UU()
	{
		return (String)get_Value(COLUMNNAME_C_Order_UU);
	}

	public org.compiere.model.I_C_POS getC_POS() throws RuntimeException
	{
		return (org.compiere.model.I_C_POS)MTable.get(getCtx(), org.compiere.model.I_C_POS.Table_ID)
			.getPO(getC_POS_ID(), get_TrxName());
	}

	/** Set POS Terminal.
		@param C_POS_ID Point of Sales Terminal
	*/
	public void setC_POS_ID (int C_POS_ID)
	{
		if (C_POS_ID < 1)
			set_Value (COLUMNNAME_C_POS_ID, null);
		else
			set_Value (COLUMNNAME_C_POS_ID, Integer.valueOf(C_POS_ID));
	}

	/** Get POS Terminal.
		@return Point of Sales Terminal
	  */
	public int getC_POS_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_POS_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaymentTerm getC_PaymentTerm() throws RuntimeException
	{
		return (org.compiere.model.I_C_PaymentTerm)MTable.get(getCtx(), org.compiere.model.I_C_PaymentTerm.Table_ID)
			.getPO(getC_PaymentTerm_ID(), get_TrxName());
	}

	/** Set Payment Term.
		@param C_PaymentTerm_ID The terms of Payment (timing, discount)
	*/
	public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
	{
		if (C_PaymentTerm_ID < 1)
			set_Value (COLUMNNAME_C_PaymentTerm_ID, null);
		else
			set_Value (COLUMNNAME_C_PaymentTerm_ID, Integer.valueOf(C_PaymentTerm_ID));
	}

	/** Get Payment Term.
		@return The terms of Payment (timing, discount)
	  */
	public int getC_PaymentTerm_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaymentTerm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Payment getC_Payment() throws RuntimeException
	{
		return (org.compiere.model.I_C_Payment)MTable.get(getCtx(), org.compiere.model.I_C_Payment.Table_ID)
			.getPO(getC_Payment_ID(), get_TrxName());
	}

	/** Set Payment.
		@param C_Payment_ID Payment identifier
	*/
	public void setC_Payment_ID (int C_Payment_ID)
	{
		if (C_Payment_ID < 1)
			set_Value (COLUMNNAME_C_Payment_ID, null);
		else
			set_Value (COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
	}

	/** Get Payment.
		@return Payment identifier
	  */
	public int getC_Payment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Payment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
	{
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_ID)
			.getPO(getC_Project_ID(), get_TrxName());
	}

	/** Set Project.
		@param C_Project_ID Financial Project
	*/
	public void setC_Project_ID (int C_Project_ID)
	{
		if (C_Project_ID < 1)
			set_Value (COLUMNNAME_C_Project_ID, null);
		else
			set_Value (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
	}

	/** Get Project.
		@return Financial Project
	  */
	public int getC_Project_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Charge amount.
		@param ChargeAmt Charge Amount
	*/
	public void setChargeAmt (BigDecimal ChargeAmt)
	{
		set_Value (COLUMNNAME_ChargeAmt, ChargeAmt);
	}

	/** Get Charge amount.
		@return Charge Amount
	  */
	public BigDecimal getChargeAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ChargeAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Copy From.
		@param CopyFrom Copy From Record
	*/
	public void setCopyFrom (String CopyFrom)
	{
		set_Value (COLUMNNAME_CopyFrom, CopyFrom);
	}

	/** Get Copy From.
		@return Copy From Record
	  */
	public String getCopyFrom()
	{
		return (String)get_Value(COLUMNNAME_CopyFrom);
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Date Ordered.
		@param DateOrdered Date of Order
	*/
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_Value (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Date Printed.
		@param DatePrinted Date the document was printed.
	*/
	public void setDatePrinted (Timestamp DatePrinted)
	{
		set_Value (COLUMNNAME_DatePrinted, DatePrinted);
	}

	/** Get Date Printed.
		@return Date the document was printed.
	  */
	public Timestamp getDatePrinted()
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePrinted);
	}

	/** Set Date Promised.
		@param DatePromised Date Order was promised
	*/
	public void setDatePromised (Timestamp DatePromised)
	{
		set_Value (COLUMNNAME_DatePromised, DatePromised);
	}

	/** Get Date Promised.
		@return Date Order was promised
	  */
	public Timestamp getDatePromised()
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePromised);
	}

	/** DeliveryRule AD_Reference_ID=151 */
	public static final int DELIVERYRULE_AD_Reference_ID=151;
	/** Availability = A */
	public static final String DELIVERYRULE_Availability = "A";
	/** Force = F */
	public static final String DELIVERYRULE_Force = "F";
	/** Complete Line = L */
	public static final String DELIVERYRULE_CompleteLine = "L";
	/** Manual = M */
	public static final String DELIVERYRULE_Manual = "M";
	/** Complete Order = O */
	public static final String DELIVERYRULE_CompleteOrder = "O";
	/** After Payment = R */
	public static final String DELIVERYRULE_AfterPayment = "R";
	/** Set Delivery Rule.
		@param DeliveryRule Defines the timing of Delivery
	*/
	public void setDeliveryRule (String DeliveryRule)
	{

		set_Value (COLUMNNAME_DeliveryRule, DeliveryRule);
	}

	/** Get Delivery Rule.
		@return Defines the timing of Delivery
	  */
	public String getDeliveryRule()
	{
		return (String)get_Value(COLUMNNAME_DeliveryRule);
	}

	/** DeliveryViaRule AD_Reference_ID=152 */
	public static final int DELIVERYVIARULE_AD_Reference_ID=152;
	/** Delivery = D */
	public static final String DELIVERYVIARULE_Delivery = "D";
	/** Pickup = P */
	public static final String DELIVERYVIARULE_Pickup = "P";
	/** Shipper = S */
	public static final String DELIVERYVIARULE_Shipper = "S";
	/** Set Delivery Via.
		@param DeliveryViaRule How the order will be delivered
	*/
	public void setDeliveryViaRule (String DeliveryViaRule)
	{

		set_Value (COLUMNNAME_DeliveryViaRule, DeliveryViaRule);
	}

	/** Get Delivery Via.
		@return How the order will be delivered
	  */
	public String getDeliveryViaRule()
	{
		return (String)get_Value(COLUMNNAME_DeliveryViaRule);
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** &lt;None&gt; = -- */
	public static final String DOCACTION_None = "--";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Set Document Action.
		@param DocAction The targeted status of the document
	*/
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction()
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Set Document Status.
		@param DocStatus The current status of the document
	*/
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus()
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo Document sequence number of the document
	*/
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo()
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getDocumentNo());
    }

	public org.compiere.model.I_C_BPartner getDropShip_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getDropShip_BPartner_ID(), get_TrxName());
	}

	/** Set Drop Ship Business Partner.
		@param DropShip_BPartner_ID Business Partner to ship to
	*/
	public void setDropShip_BPartner_ID (int DropShip_BPartner_ID)
	{
		if (DropShip_BPartner_ID < 1)
			set_Value (COLUMNNAME_DropShip_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_DropShip_BPartner_ID, Integer.valueOf(DropShip_BPartner_ID));
	}

	/** Get Drop Ship Business Partner.
		@return Business Partner to ship to
	  */
	public int getDropShip_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getDropShip_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getDropShip_Location_ID(), get_TrxName());
	}

	/** Set Drop Shipment Location.
		@param DropShip_Location_ID Business Partner Location for shipping to
	*/
	public void setDropShip_Location_ID (int DropShip_Location_ID)
	{
		if (DropShip_Location_ID < 1)
			set_Value (COLUMNNAME_DropShip_Location_ID, null);
		else
			set_Value (COLUMNNAME_DropShip_Location_ID, Integer.valueOf(DropShip_Location_ID));
	}

	/** Get Drop Shipment Location.
		@return Business Partner Location for shipping to
	  */
	public int getDropShip_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getDropShip_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getDropShip_User_ID(), get_TrxName());
	}

	/** Set Drop Shipment Contact.
		@param DropShip_User_ID Business Partner Contact for drop shipment
	*/
	public void setDropShip_User_ID (int DropShip_User_ID)
	{
		if (DropShip_User_ID < 1)
			set_Value (COLUMNNAME_DropShip_User_ID, null);
		else
			set_Value (COLUMNNAME_DropShip_User_ID, Integer.valueOf(DropShip_User_ID));
	}

	/** Get Drop Shipment Contact.
		@return Business Partner Contact for drop shipment
	  */
	public int getDropShip_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Freight Amount.
		@param FreightAmt Freight Amount 
	*/
	public void setFreightAmt (BigDecimal FreightAmt)
	{
		set_Value (COLUMNNAME_FreightAmt, FreightAmt);
	}

	/** Get Freight Amount.
		@return Freight Amount 
	  */
	public BigDecimal getFreightAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FreightAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** FreightCostRule AD_Reference_ID=153 */
	public static final int FREIGHTCOSTRULE_AD_Reference_ID=153;
	/** Calculated = C */
	public static final String FREIGHTCOSTRULE_Calculated = "C";
	/** Fix price = F */
	public static final String FREIGHTCOSTRULE_FixPrice = "F";
	/** Freight included = I */
	public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
	/** Line = L */
	public static final String FREIGHTCOSTRULE_Line = "L";
	/** Customer Account = U */
	public static final String FREIGHTCOSTRULE_CustomerAccount = "U";
	/** Set Freight Cost Rule.
		@param FreightCostRule Method for charging Freight
	*/
	public void setFreightCostRule (String FreightCostRule)
	{

		set_Value (COLUMNNAME_FreightCostRule, FreightCostRule);
	}

	/** Get Freight Cost Rule.
		@return Method for charging Freight
	  */
	public String getFreightCostRule()
	{
		return (String)get_Value(COLUMNNAME_FreightCostRule);
	}

	/** Set Grand Total.
		@param GrandTotal Total amount of document
	*/
	public void setGrandTotal (BigDecimal GrandTotal)
	{
		set_ValueNoCheck (COLUMNNAME_GrandTotal, GrandTotal);
	}

	/** Get Grand Total.
		@return Total amount of document
	  */
	public BigDecimal getGrandTotal()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_GrandTotal);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** InvoiceRule AD_Reference_ID=150 */
	public static final int INVOICERULE_AD_Reference_ID=150;
	/** After Delivery = D */
	public static final String INVOICERULE_AfterDelivery = "D";
	/** Immediate = I */
	public static final String INVOICERULE_Immediate = "I";
	/** After Order delivered = O */
	public static final String INVOICERULE_AfterOrderDelivered = "O";
	/** Customer Schedule after Delivery = S */
	public static final String INVOICERULE_CustomerScheduleAfterDelivery = "S";
	/** Set Invoice Rule.
		@param InvoiceRule Frequency and method of invoicing 
	*/
	public void setInvoiceRule (String InvoiceRule)
	{

		set_Value (COLUMNNAME_InvoiceRule, InvoiceRule);
	}

	/** Get Invoice Rule.
		@return Frequency and method of invoicing 
	  */
	public String getInvoiceRule()
	{
		return (String)get_Value(COLUMNNAME_InvoiceRule);
	}

	/** Set Approved.
		@param IsApproved Indicates if this document requires approval
	*/
	public void setIsApproved (boolean IsApproved)
	{
		set_ValueNoCheck (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved()
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Credit Approved.
		@param IsCreditApproved Credit  has been approved
	*/
	public void setIsCreditApproved (boolean IsCreditApproved)
	{
		set_ValueNoCheck (COLUMNNAME_IsCreditApproved, Boolean.valueOf(IsCreditApproved));
	}

	/** Get Credit Approved.
		@return Credit  has been approved
	  */
	public boolean isCreditApproved()
	{
		Object oo = get_Value(COLUMNNAME_IsCreditApproved);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Delivered.
		@param IsDelivered Delivered
	*/
	public void setIsDelivered (boolean IsDelivered)
	{
		set_ValueNoCheck (COLUMNNAME_IsDelivered, Boolean.valueOf(IsDelivered));
	}

	/** Get Delivered.
		@return Delivered	  */
	public boolean isDelivered()
	{
		Object oo = get_Value(COLUMNNAME_IsDelivered);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Discount Printed.
		@param IsDiscountPrinted Print Discount on Invoice and Order
	*/
	public void setIsDiscountPrinted (boolean IsDiscountPrinted)
	{
		set_Value (COLUMNNAME_IsDiscountPrinted, Boolean.valueOf(IsDiscountPrinted));
	}

	/** Get Discount Printed.
		@return Print Discount on Invoice and Order
	  */
	public boolean isDiscountPrinted()
	{
		Object oo = get_Value(COLUMNNAME_IsDiscountPrinted);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Drop Shipment.
		@param IsDropShip Drop Shipments are sent directly to the Drop Shipment Location
	*/
	public void setIsDropShip (boolean IsDropShip)
	{
		set_Value (COLUMNNAME_IsDropShip, Boolean.valueOf(IsDropShip));
	}

	/** Get Drop Shipment.
		@return Drop Shipments are sent directly to the Drop Shipment Location
	  */
	public boolean isDropShip()
	{
		Object oo = get_Value(COLUMNNAME_IsDropShip);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Invoiced.
		@param IsInvoiced Is this invoiced?
	*/
	public void setIsInvoiced (boolean IsInvoiced)
	{
		set_ValueNoCheck (COLUMNNAME_IsInvoiced, Boolean.valueOf(IsInvoiced));
	}

	/** Get Invoiced.
		@return Is this invoiced?
	  */
	public boolean isInvoiced()
	{
		Object oo = get_Value(COLUMNNAME_IsInvoiced);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Pay Schedule valid.
		@param IsPayScheduleValid Is the Payment Schedule is valid
	*/
	public void setIsPayScheduleValid (boolean IsPayScheduleValid)
	{
		set_Value (COLUMNNAME_IsPayScheduleValid, Boolean.valueOf(IsPayScheduleValid));
	}

	/** Get Pay Schedule valid.
		@return Is the Payment Schedule is valid
	  */
	public boolean isPayScheduleValid()
	{
		Object oo = get_Value(COLUMNNAME_IsPayScheduleValid);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Printed.
		@param IsPrinted Indicates if this document / line is printed
	*/
	public void setIsPrinted (boolean IsPrinted)
	{
		set_ValueNoCheck (COLUMNNAME_IsPrinted, Boolean.valueOf(IsPrinted));
	}

	/** Get Printed.
		@return Indicates if this document / line is printed
	  */
	public boolean isPrinted()
	{
		Object oo = get_Value(COLUMNNAME_IsPrinted);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Privileged Rate.
		@param IsPriviledgedRate Privileged Rate
	*/
	public void setIsPriviledgedRate (boolean IsPriviledgedRate)
	{
		set_Value (COLUMNNAME_IsPriviledgedRate, Boolean.valueOf(IsPriviledgedRate));
	}

	/** Get Privileged Rate.
		@return Privileged Rate	  */
	public boolean isPriviledgedRate()
	{
		Object oo = get_Value(COLUMNNAME_IsPriviledgedRate);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx This is a Sales Transaction
	*/
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx()
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Selected.
		@param IsSelected Selected
	*/
	public void setIsSelected (boolean IsSelected)
	{
		set_Value (COLUMNNAME_IsSelected, Boolean.valueOf(IsSelected));
	}

	/** Get Selected.
		@return Selected	  */
	public boolean isSelected()
	{
		Object oo = get_Value(COLUMNNAME_IsSelected);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Self-Service.
		@param IsSelfService This is a Self-Service entry or this entry can be changed via Self-Service
	*/
	public void setIsSelfService (boolean IsSelfService)
	{
		set_Value (COLUMNNAME_IsSelfService, Boolean.valueOf(IsSelfService));
	}

	/** Get Self-Service.
		@return This is a Self-Service entry or this entry can be changed via Self-Service
	  */
	public boolean isSelfService()
	{
		Object oo = get_Value(COLUMNNAME_IsSelfService);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Price includes Tax.
		@param IsTaxIncluded Tax is included in the price 
	*/
	public void setIsTaxIncluded (boolean IsTaxIncluded)
	{
		set_Value (COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
	}

	/** Get Price includes Tax.
		@return Tax is included in the price 
	  */
	public boolean isTaxIncluded()
	{
		Object oo = get_Value(COLUMNNAME_IsTaxIncluded);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Transferred.
		@param IsTransferred Transferred to General Ledger (i.e. accounted)
	*/
	public void setIsTransferred (boolean IsTransferred)
	{
		set_ValueNoCheck (COLUMNNAME_IsTransferred, Boolean.valueOf(IsTransferred));
	}

	/** Get Transferred.
		@return Transferred to General Ledger (i.e. accounted)
	  */
	public boolean isTransferred()
	{
		Object oo = get_Value(COLUMNNAME_IsTransferred);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_C_Order getLink_Order() throws RuntimeException
	{
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_ID)
			.getPO(getLink_Order_ID(), get_TrxName());
	}

	/** Set Linked Order.
		@param Link_Order_ID This field links a sales order to the purchase order that is generated from it.
	*/
	public void setLink_Order_ID (int Link_Order_ID)
	{
		if (Link_Order_ID < 1)
			set_ValueNoCheck (COLUMNNAME_Link_Order_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Link_Order_ID, Integer.valueOf(Link_Order_ID));
	}

	/** Get Linked Order.
		@return This field links a sales order to the purchase order that is generated from it.
	  */
	public int getLink_Order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Link_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_FreightCategory getM_FreightCategory() throws RuntimeException
	{
		return (org.compiere.model.I_M_FreightCategory)MTable.get(getCtx(), org.compiere.model.I_M_FreightCategory.Table_ID)
			.getPO(getM_FreightCategory_ID(), get_TrxName());
	}

	/** Set Freight Category.
		@param M_FreightCategory_ID Category of the Freight
	*/
	public void setM_FreightCategory_ID (int M_FreightCategory_ID)
	{
		if (M_FreightCategory_ID < 1)
			set_Value (COLUMNNAME_M_FreightCategory_ID, null);
		else
			set_Value (COLUMNNAME_M_FreightCategory_ID, Integer.valueOf(M_FreightCategory_ID));
	}

	/** Get Freight Category.
		@return Category of the Freight
	  */
	public int getM_FreightCategory_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_FreightCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
	{
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_ID)
			.getPO(getM_PriceList_ID(), get_TrxName());
	}

	/** Set Price List.
		@param M_PriceList_ID Unique identifier of a Price List
	*/
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1)
			set_Value (COLUMNNAME_M_PriceList_ID, null);
		else
			set_Value (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException
	{
		return (org.compiere.model.I_M_Shipper)MTable.get(getCtx(), org.compiere.model.I_M_Shipper.Table_ID)
			.getPO(getM_Shipper_ID(), get_TrxName());
	}

	/** Set Shipper.
		@param M_Shipper_ID Method or manner of product delivery
	*/
	public void setM_Shipper_ID (int M_Shipper_ID)
	{
		if (M_Shipper_ID < 1)
			set_Value (COLUMNNAME_M_Shipper_ID, null);
		else
			set_Value (COLUMNNAME_M_Shipper_ID, Integer.valueOf(M_Shipper_ID));
	}

	/** Get Shipper.
		@return Method or manner of product delivery
	  */
	public int getM_Shipper_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Shipper_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getM_Warehouse_ID(), get_TrxName());
	}

	/** Set Warehouse.
		@param M_Warehouse_ID Storage Warehouse and Service Point
	*/
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1)
			set_Value (COLUMNNAME_M_Warehouse_ID, null);
		else
			set_Value (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Order Reference.
		@param POReference Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	*/
	public void setPOReference (String POReference)
	{
		set_Value (COLUMNNAME_POReference, POReference);
	}

	/** Get Order Reference.
		@return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public String getPOReference()
	{
		return (String)get_Value(COLUMNNAME_POReference);
	}

	/** Set Payment BPartner.
		@param Pay_BPartner_ID Business Partner responsible for the payment
	*/
	public void setPay_BPartner_ID (int Pay_BPartner_ID)
	{
		if (Pay_BPartner_ID < 1)
			set_Value (COLUMNNAME_Pay_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_Pay_BPartner_ID, Integer.valueOf(Pay_BPartner_ID));
	}

	/** Get Payment BPartner.
		@return Business Partner responsible for the payment
	  */
	public int getPay_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Pay_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Payment Location.
		@param Pay_Location_ID Location of the Business Partner responsible for the payment
	*/
	public void setPay_Location_ID (int Pay_Location_ID)
	{
		if (Pay_Location_ID < 1)
			set_Value (COLUMNNAME_Pay_Location_ID, null);
		else
			set_Value (COLUMNNAME_Pay_Location_ID, Integer.valueOf(Pay_Location_ID));
	}

	/** Get Payment Location.
		@return Location of the Business Partner responsible for the payment
	  */
	public int getPay_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Pay_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** PaymentRule AD_Reference_ID=195 */
	public static final int PAYMENTRULE_AD_Reference_ID=195;
	/** Cash = B */
	public static final String PAYMENTRULE_Cash = "B";
	/** Direct Debit = D */
	public static final String PAYMENTRULE_DirectDebit = "D";
	/** Credit Card = K */
	public static final String PAYMENTRULE_CreditCard = "K";
	/** Mixed POS Payment = M */
	public static final String PAYMENTRULE_MixedPOSPayment = "M";
	/** On Credit = P */
	public static final String PAYMENTRULE_OnCredit = "P";
	/** Check = S */
	public static final String PAYMENTRULE_Check = "S";
	/** Direct Deposit = T */
	public static final String PAYMENTRULE_DirectDeposit = "T";
	/** Set Payment Rule.
		@param PaymentRule How you pay the invoice
	*/
	public void setPaymentRule (String PaymentRule)
	{

		set_Value (COLUMNNAME_PaymentRule, PaymentRule);
	}

	/** Get Payment Rule.
		@return How you pay the invoice
	  */
	public String getPaymentRule()
	{
		return (String)get_Value(COLUMNNAME_PaymentRule);
	}

	/** Set Posted.
		@param Posted Posting status
	*/
	public void setPosted (boolean Posted)
	{
		set_Value (COLUMNNAME_Posted, Boolean.valueOf(Posted));
	}

	/** Get Posted.
		@return Posting status
	  */
	public boolean isPosted()
	{
		Object oo = get_Value(COLUMNNAME_Posted);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** PriorityRule AD_Reference_ID=154 */
	public static final int PRIORITYRULE_AD_Reference_ID=154;
	/** Urgent = 1 */
	public static final String PRIORITYRULE_Urgent = "1";
	/** High = 3 */
	public static final String PRIORITYRULE_High = "3";
	/** Medium = 5 */
	public static final String PRIORITYRULE_Medium = "5";
	/** Low = 7 */
	public static final String PRIORITYRULE_Low = "7";
	/** Minor = 9 */
	public static final String PRIORITYRULE_Minor = "9";
	/** Set Priority.
		@param PriorityRule Priority of a document
	*/
	public void setPriorityRule (String PriorityRule)
	{

		set_Value (COLUMNNAME_PriorityRule, PriorityRule);
	}

	/** Get Priority.
		@return Priority of a document
	  */
	public String getPriorityRule()
	{
		return (String)get_Value(COLUMNNAME_PriorityRule);
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_ValueNoCheck (COLUMNNAME_Processed, Boolean.valueOf(Processed));
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

	/** Set Processed On.
		@param ProcessedOn The date+time (expressed in decimal format) when the document has been processed
	*/
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Process Now.
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Promotion Code.
		@param PromotionCode User entered promotion code at sales time
	*/
	public void setPromotionCode (String PromotionCode)
	{
		set_Value (COLUMNNAME_PromotionCode, PromotionCode);
	}

	/** Get Promotion Code.
		@return User entered promotion code at sales time
	  */
	public String getPromotionCode()
	{
		return (String)get_Value(COLUMNNAME_PromotionCode);
	}

	public org.compiere.model.I_C_Order getQuotationOrder() throws RuntimeException
	{
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_ID)
			.getPO(getQuotationOrder_ID(), get_TrxName());
	}

	/** Set Quotation.
		@param QuotationOrder_ID Quotation used for generating this order
	*/
	public void setQuotationOrder_ID (int QuotationOrder_ID)
	{
		if (QuotationOrder_ID < 1)
			set_Value (COLUMNNAME_QuotationOrder_ID, null);
		else
			set_Value (COLUMNNAME_QuotationOrder_ID, Integer.valueOf(QuotationOrder_ID));
	}

	/** Get Quotation.
		@return Quotation used for generating this order
	  */
	public int getQuotationOrder_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_QuotationOrder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Order getRef_Order() throws RuntimeException
	{
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_ID)
			.getPO(getRef_Order_ID(), get_TrxName());
	}

	/** Set Referenced Order.
		@param Ref_Order_ID Reference to corresponding Sales/Purchase Order
	*/
	public void setRef_Order_ID (int Ref_Order_ID)
	{
		if (Ref_Order_ID < 1)
			set_Value (COLUMNNAME_Ref_Order_ID, null);
		else
			set_Value (COLUMNNAME_Ref_Order_ID, Integer.valueOf(Ref_Order_ID));
	}

	/** Get Referenced Order.
		@return Reference to corresponding Sales/Purchase Order
	  */
	public int getRef_Order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Ref_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getSalesRep_ID(), get_TrxName());
	}

	/** Set Sales Representative.
		@param SalesRep_ID Sales Representative or Company Agent
	*/
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1)
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Representative.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Send EMail.
		@param SendEMail Enable sending Document EMail
	*/
	public void setSendEMail (boolean SendEMail)
	{
		set_Value (COLUMNNAME_SendEMail, Boolean.valueOf(SendEMail));
	}

	/** Get Send EMail.
		@return Enable sending Document EMail
	  */
	public boolean isSendEMail()
	{
		Object oo = get_Value(COLUMNNAME_SendEMail);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Rate Inquiry.
		@param ShippingRateInquiry Rate Inquiry
	*/
	public void setShippingRateInquiry (String ShippingRateInquiry)
	{
		set_Value (COLUMNNAME_ShippingRateInquiry, ShippingRateInquiry);
	}

	/** Get Rate Inquiry.
		@return Rate Inquiry	  */
	public String getShippingRateInquiry()
	{
		return (String)get_Value(COLUMNNAME_ShippingRateInquiry);
	}

	/** Set Total Lines.
		@param TotalLines Total of all document lines
	*/
	public void setTotalLines (BigDecimal TotalLines)
	{
		set_ValueNoCheck (COLUMNNAME_TotalLines, TotalLines);
	}

	/** Get Total Lines.
		@return Total of all document lines
	  */
	public BigDecimal getTotalLines()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalLines);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser1_ID(), get_TrxName());
	}

	/** Set User Element List 1.
		@param User1_ID User defined list element #1
	*/
	public void setUser1_ID (int User1_ID)
	{
		if (User1_ID < 1)
			set_Value (COLUMNNAME_User1_ID, null);
		else
			set_Value (COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
	}

	/** Get User Element List 1.
		@return User defined list element #1
	  */
	public int getUser1_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser2_ID(), get_TrxName());
	}

	/** Set User Element List 2.
		@param User2_ID User defined list element #2
	*/
	public void setUser2_ID (int User2_ID)
	{
		if (User2_ID < 1)
			set_Value (COLUMNNAME_User2_ID, null);
		else
			set_Value (COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
	}

	/** Get User Element List 2.
		@return User defined list element #2
	  */
	public int getUser2_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Volume.
		@param Volume Volume of a product
	*/
	public void setVolume (BigDecimal Volume)
	{
		set_Value (COLUMNNAME_Volume, Volume);
	}

	/** Get Volume.
		@return Volume of a product
	  */
	public BigDecimal getVolume()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Volume);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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
}