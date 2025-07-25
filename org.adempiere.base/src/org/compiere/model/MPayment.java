/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
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
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.base.CreditStatus;
import org.adempiere.base.ICreditManager;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.PeriodClosedException;
import org.adempiere.util.IProcessUI;
import org.adempiere.util.PaymentUtil;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.IDocsPostProcess;
import org.compiere.process.ProcessCall;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.IBAN;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.compiere.util.ValueNamePair;

/**
 *  Payment Model.
 *  @author 	Jorg Janke
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li>FR [ 1948157  ]  Is necessary the reference for document reverse
 *  		@see https://sourceforge.net/p/adempiere/feature-requests/412/
 *			<li> FR [ 1866214 ]  
 *			@sse https://sourceforge.net/p/adempiere/feature-requests/298/
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org 
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *
 *  @author Carlos Ruiz - globalqss [ 2141475 ] Payment &lt;&gt; allocations must not be completed - implement lots of validations on prepareIt
 *  @version 	$Id: MPayment.java,v 1.4 2006/10/02 05:18:39 jjanke Exp $
 */
public class MPayment extends X_C_Payment 
	implements DocAction, ProcessCall, PaymentInterface, IDocsPostProcess
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157628050370126666L;

	/**
	 * 	Get Payments Of BPartner
	 *	@param ctx context
	 *	@param C_BPartner_ID id
	 *	@param trxName transaction
	 *	@return array of payment
	 */
	public static MPayment[] getOfBPartner (Properties ctx, int C_BPartner_ID, String trxName)
	{
		//FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
		final String whereClause = "C_BPartner_ID=?";
		List <MPayment> list = new Query(ctx, I_C_Payment.Table_Name, whereClause, trxName)
		.setParameters(C_BPartner_ID)
		.list();

		//
		MPayment[] retValue = new MPayment[list.size()];
		list.toArray(retValue);
		return retValue;
	}	//	getOfBPartner
	
	/**
	 * 	Get Payments of Bank Transfer
	 *	@param ctx context
	 *	@param C_BankTransfer_ID id
	 *	@param trxName transaction
	 *	@return array of payment
	 */
	public static MPayment[] getOfBankTransfer (Properties ctx, int C_BankTransfer_ID, String trxName)
	{
		final String whereClause = "C_BankTransfer_ID=?";
		List <MPayment> list = new Query(ctx, Table_Name, whereClause, trxName)
				.setParameters(C_BankTransfer_ID)
				.setOrderBy(COLUMNNAME_C_Payment_ID)
				.list();
		MPayment[] retValue = new MPayment[list.size()];
		list.toArray(retValue);
		return retValue;
	}	//	getOfBankTransfer
	
    /**
     * UUID based Constructor
     * @param ctx  Context
     * @param C_Payment_UU  UUID key
     * @param trxName Transaction
     */
    public MPayment(Properties ctx, String C_Payment_UU, String trxName) {
        super(ctx, C_Payment_UU, trxName);
		if (Util.isEmpty(C_Payment_UU))
			setInitialDefaults();
    }

	/**
	 *  Default Constructor
	 *  @param ctx context
	 *  @param  C_Payment_ID    payment to load, (0 create new payment)
	 *  @param trxName trx name
	 */
	public MPayment (Properties ctx, int C_Payment_ID, String trxName)
	{
		super (ctx, C_Payment_ID, trxName);
		//  New
		if (C_Payment_ID == 0)
			setInitialDefaults();
	}   //  MPayment
	
	/**
	 * Set the initial defaults for a new record
	 */
	private void setInitialDefaults() {
		setDocAction(DOCACTION_Complete);
		setDocStatus(DOCSTATUS_Drafted);
		setTrxType(TRXTYPE_Sales);
		//
		setR_AvsAddr (R_AVSZIP_Unavailable);
		setR_AvsZip (R_AVSZIP_Unavailable);
		//
		setIsReceipt (true);
		setIsApproved (false);
		setIsReconciled (false);
		setIsAllocated(false);
		setIsOnline (false);
		setIsSelfService(false);
		setIsDelayedCapture (false);
		setIsPrepayment(false);
		setProcessed(false);
		setProcessing(false);
		setPosted (false);
		//
		setPayAmt(Env.ZERO);
		setDiscountAmt(Env.ZERO);
		setTaxAmt(Env.ZERO);
		setWriteOffAmt(Env.ZERO);
		setIsOverUnderPayment (true);
		setOverUnderAmt(Env.ZERO);
		//
		setDateTrx (new Timestamp(System.currentTimeMillis()));
		setDateAcct (getDateTrx());
		setTenderType(TENDERTYPE_Check);
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *	@param trxName transaction
	 */
	public MPayment (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MPayment

	/**	Temporary	Bank Account Processors		*/
	protected MBankAccountProcessor[]	m_mBankAccountProcessors = null;
	/**	Temporary	Bank Account Processor		*/
	protected MBankAccountProcessor	m_mBankAccountProcessor = null;
	/** Logger								*/
	protected static CLogger		s_log = CLogger.getCLogger (MPayment.class);
	/** Error Message						*/
	protected String				m_errorMessage = null;
	
	/** Reversal Indicator			*/
	public static String	REVERSE_INDICATOR = "^";
	
	/**
	 *  Reset Payment to new status
	 */
	public void resetNew()
	{
		setC_Payment_ID(0);		//	forces new Record
		set_ValueNoCheck ("DocumentNo", null);
		setDocAction(DOCACTION_Prepare);
		setDocStatus(DOCSTATUS_Drafted);
		setProcessed(false);
		setPosted (false);
		setIsReconciled (false);
		setIsAllocated(false);
		setIsOnline(false);
		setIsDelayedCapture (false);
		setC_Invoice_ID(0);
		setC_Order_ID(0);
		setC_Charge_ID(0);
		setC_Project_ID(0);
		setIsPrepayment(false);
	}	//	resetNew

	/**
	 * 	Is Cash Trx
	 *	@return true if Cash Trx
	 */
	public boolean isCashTrx()
	{
		return "X".equals(getTenderType());
	}	//	isCashTrx
	
	/**
	 * Is Cashbook Trx
	 * @return true if this is a cashbook trx
	 */
	public boolean isCashbookTrx() {
		return isCashTrx() && !MSysConfig.getBooleanValue(MSysConfig.CASH_AS_PAYMENT, true , getAD_Client_ID());
	}
	
	/**
	 *  Set Credit Card details.
	 *  Need to set PatmentProcessor after Amount/Currency Set.
	 *
	 *  @param TrxType Transaction Type see TRX_
	 *  @param creditCardType CC type
	 *  @param creditCardNumber CC number
	 *  @param creditCardVV CC verification
	 *  @param creditCardExpMM CC Exp MM
	 *  @param creditCardExpYY CC Exp YY
	 *  @return true if valid
	 */
	public boolean setCreditCard (String TrxType, String creditCardType, String creditCardNumber,
		String creditCardVV, int creditCardExpMM, int creditCardExpYY)
	{
		setTenderType(TENDERTYPE_CreditCard);
		setTrxType(TrxType);
		//
		setCreditCardType (creditCardType);
		setCreditCardNumber (creditCardNumber);
		setCreditCardVV (creditCardVV);
		setCreditCardExpMM (creditCardExpMM);
		setCreditCardExpYY (creditCardExpYY);
		//
		int check = MPaymentValidate.validateCreditCardNumber(creditCardNumber, creditCardType).length()
			+ MPaymentValidate.validateCreditCardExp(creditCardExpMM, creditCardExpYY).length();
		if (creditCardVV.length() > 0)
			check += MPaymentValidate.validateCreditCardVV(creditCardVV, creditCardType).length();
		return check == 0;
	}   //  setCreditCard

	/**
	 *  Set Credit Card details.
	 *  Need to set PatmentProcessor after Amount/Currency Set.
	 *
	 *  @param TrxType Transaction Type see TRX_
	 *  @param creditCardType CC type
	 *  @param creditCardNumber CC number
	 *  @param creditCardVV CC verification
	 *  @param creditCardExp CC Exp (include both year and month)
	 *  @return true if valid
	 */
	public boolean setCreditCard (String TrxType, String creditCardType, String creditCardNumber,
		String creditCardVV, String creditCardExp)
	{
		return setCreditCard(TrxType, creditCardType, creditCardNumber,
			creditCardVV, MPaymentValidate.getCreditCardExpMM(creditCardExp), 
			MPaymentValidate.getCreditCardExpYY(creditCardExp));
	}   //  setCreditCard

	/**
	 *  Set ACH BankAccount Info
	 *
	 *  @param preparedPayment
	 *  @return true if valid
	 */
	public boolean setBankACH (MPaySelectionCheck preparedPayment)
	{
		//	Our Bank
		setC_BankAccount_ID(preparedPayment.getParent().getC_BankAccount_ID());
		//	Target Bank
		int C_BP_BankAccount_ID = preparedPayment.getC_BP_BankAccount_ID();
		MBPBankAccount ba = new MBPBankAccount (preparedPayment.getCtx(), C_BP_BankAccount_ID, null);
		setRoutingNo(ba.getRoutingNo());
		setAccountNo(ba.getAccountNo());
		setIBAN(ba.getIBAN());
		setSwiftCode(ba.getSwiftCode()) ;
		setDescription(preparedPayment.getC_PaySelection().getName());
		setIsReceipt (X_C_Order.PAYMENTRULE_DirectDebit.equals	//	AR only
				(preparedPayment.getPaymentRule()));
		if ( MPaySelectionCheck.PAYMENTRULE_DirectDebit.equals(preparedPayment.getPaymentRule()) )
			setTenderType(MPayment.TENDERTYPE_DirectDebit);
		else if ( MPaySelectionCheck.PAYMENTRULE_DirectDeposit.equals(preparedPayment.getPaymentRule()))
			setTenderType(MPayment.TENDERTYPE_DirectDeposit);
		//
		int check = MPaymentValidate.validateRoutingNo(getRoutingNo()).length()
			+ MPaymentValidate.validateAccountNo(getAccountNo()).length();
		return check == 0;
	}	//	setBankACH

	/**
	 *  Set ACH BankAccount Info
	 *
	 *  @param C_BankAccount_ID bank account
	 *  @param isReceipt true if receipt
	 * 	@param tenderType - Direct Debit or Direct Deposit
	 *  @param routingNo routing
	 *  @param accountNo account
	 *  @return true if valid
	 */
	public boolean setBankACH (int C_BankAccount_ID, boolean isReceipt, String tenderType, 
		String routingNo, String accountNo)
	{
		setTenderType (tenderType);
		setIsReceipt (isReceipt);
		//
		if (C_BankAccount_ID > 0
			&& (routingNo == null || routingNo.length() == 0 || accountNo == null || accountNo.length() == 0))
			setBankAccountDetails(C_BankAccount_ID);
		else
		{
			setC_BankAccount_ID(C_BankAccount_ID);
			setRoutingNo (routingNo);
			setAccountNo (accountNo);
		}
		setCheckNo ("");
		//
		int check = MPaymentValidate.validateRoutingNo(routingNo).length()
			+ MPaymentValidate.validateAccountNo(accountNo).length();
		return check == 0;
	}   //  setBankACH
	
	/**
	 *  Set Cash BankAccount Info
	 *
	 *  @param C_BankAccount_ID bank account
	 *  @param isReceipt true if receipt
	 * 	@param tenderType - Cash (Payment)
	 *  @return true if valid
	 */
	public boolean setBankCash (int C_BankAccount_ID, boolean isReceipt, String tenderType)
	{
		setTenderType (tenderType);
		setIsReceipt (isReceipt);
		//
		if (C_BankAccount_ID > 0)
			setBankAccountDetails(C_BankAccount_ID);
		else
		{
			setC_BankAccount_ID(C_BankAccount_ID);
		}
		//
		return true;
	}   //  setBankCash

	/**
	 *  Set Check BankAccount Info
	 *
	 *  @param C_BankAccount_ID bank account
	 *  @param isReceipt true if receipt
	 *  @param checkNo check no
	 *  @return true if valid
	 */
	public boolean setBankCheck (int C_BankAccount_ID, boolean isReceipt, String checkNo)
	{
		return setBankCheck (C_BankAccount_ID, isReceipt, null, null, checkNo);
	}	//	setBankCheck

	/**
	 *  Set Check BankAccount Info
	 *
	 *  @param C_BankAccount_ID bank account
	 *  @param isReceipt true if receipt
	 *  @param routingNo routing no
	 *  @param accountNo account no
	 *  @param checkNo check no
	 *  @return true if valid
	 */
	public boolean setBankCheck (int C_BankAccount_ID, boolean isReceipt, 
		String routingNo, String accountNo, String checkNo)
	{
		setTenderType (TENDERTYPE_Check);
		setIsReceipt (isReceipt);
		//
		if (C_BankAccount_ID > 0
			&& (routingNo == null || routingNo.length() == 0 
				|| accountNo == null || accountNo.length() == 0))
			setBankAccountDetails(C_BankAccount_ID);
		else
		{
			setC_BankAccount_ID(C_BankAccount_ID);
			setRoutingNo (routingNo);
			setAccountNo (accountNo);
		}
		setCheckNo (checkNo);
		//
		int check = MPaymentValidate.validateRoutingNo(routingNo).length()
			+ MPaymentValidate.validateAccountNo(accountNo).length()
			+ MPaymentValidate.validateCheckNo(checkNo).length();
		return check == 0;       //  no error message
	}   //  setBankCheck

	/**
	 * 	Set Bank Account Details.
	 * 	Look up Routing No and Bank Acct No
	 * 	@param C_BankAccount_ID bank account
	 */
	public void setBankAccountDetails (int C_BankAccount_ID)
	{
		if (C_BankAccount_ID == 0)
			return;
		setC_BankAccount_ID(C_BankAccount_ID);
		//
		String sql = "SELECT b.RoutingNo, ba.AccountNo, ba.IBAN, b.SwiftCode "
			+ "FROM C_BankAccount ba"
			+ " INNER JOIN C_Bank b ON (ba.C_Bank_ID=b.C_Bank_ID) "
			+ "WHERE C_BankAccount_ID=?";
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, C_BankAccount_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				setRoutingNo (rs.getString(1));
				setAccountNo (rs.getString(2));
				setIBAN(rs.getString(3)) ;
				setSwiftCode(rs.getString(4)) ;
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}	//	setBankAccountDetails

	/**
	 *  Set Account Address
	 *
	 *  @param name name
	 *  @param street street
	 *  @param city city
	 *  @param state state
	 *  @param zip zip
	 * 	@param country country
	 */
	public void setAccountAddress (String name, String street,
		String city, String state, String zip, String country)
	{
		setA_Name (name);
		setA_Street (street);
		setA_City (city);
		setA_State (state);
		setA_Zip (zip);
		setA_Country(country);
	}   //  setAccountAddress
	
	/**
	 *  Execute online processing of payment
	 *  @return true if approved
	 */
	public boolean processOnline()
	{
		if (log.isLoggable(Level.INFO)) log.info ("Amt=" + getPayAmt());
		//
		setIsOnline(true);
		setErrorMessage(null);
		//	prevent charging twice
		if(getTrxType().equals(TRXTYPE_Void) || getTrxType().equals(TRXTYPE_CreditPayment))
		{
			if (isVoided())
			{
				if (log.isLoggable(Level.INFO)) log.info("Already voided - " + getR_Result() + " - " + getR_RespMsg());
				setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentAlreadyVoided"));
				return true;
			}
		}
		else if(getTrxType().equals(TRXTYPE_DelayedCapture))
		{
			if (isDelayedCapture())
			{
				if (log.isLoggable(Level.INFO)) log.info("Already delayed capture - " + getR_Result() + " - " + getR_RespMsg());
				setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentAlreadyDelayedCapture"));
				return true;
			}
		}
		else
		{
			if (isApproved())
			{
				if (log.isLoggable(Level.INFO)) log.info("Already processed - " + getR_Result() + " - " + getR_RespMsg());
				setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentAlreadyProcessed"));
				return true;
			}
		}

		if (m_mBankAccountProcessor == null)
			setPaymentProcessor();
		if (m_mBankAccountProcessor == null)
		{
			if (getC_PaymentProcessor_ID() > 0)
			{
				MPaymentProcessor pp = new MPaymentProcessor(getCtx(), getC_PaymentProcessor_ID(), get_TrxName());
				log.log(Level.WARNING, "No Payment Processor Model " + pp.toString());
				setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentNoProcessorModel") + ": " + pp.toString());
			}
			else
			{
				log.log(Level.WARNING, "No Payment Processor Model");
				setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentNoProcessorModel"));
			}
			return false;
		}

		boolean approved = false;

		try
		{
			PaymentProcessor pp = PaymentProcessor.create(m_mBankAccountProcessor, this);
			if (pp == null)
				setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentNoProcessor"));
			else
			{
				approved = pp.processCC();

				if (approved)
					setErrorMessage(null);
				else
				{
					if(getTrxType().equals(TRXTYPE_Void) || getTrxType().equals(TRXTYPE_CreditPayment))
						setErrorMessage("From " +  getCreditCardName() + ": " + getR_VoidMsg());
					else
						setErrorMessage("From " +  getCreditCardName() + ": " + getR_RespMsg());							
				}
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "processOnline", e);
			setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentNotProcessed") + ": " + e.getMessage());
		}
		
		if (approved)
		{
			setCreditCardNumber(PaymentUtil.encrpytCreditCard(getCreditCardNumber()));
			setCreditCardVV(PaymentUtil.encrpytCvv(getCreditCardVV()));
			
			setDateTrx(new Timestamp(System.currentTimeMillis()));
			setDateAcct(new Timestamp(System.currentTimeMillis()));
			setProcessed(true);		// prevent editing of payment details once approved
		}
		
		setIsApproved(approved);
		
		Trx trx = Trx.get(Trx.createTrxName("ppt-"), true);
		trx.setDisplayName(getClass().getName()+"_processOnline");
		
		try
		{
			trx.start();
			
			MPaymentTransaction m_mPaymentTransaction = createPaymentTransaction(trx.getTrxName());
			m_mPaymentTransaction.setIsApproved(approved);
			if(getTrxType().equals(TRXTYPE_Void) || getTrxType().equals(TRXTYPE_CreditPayment))
				m_mPaymentTransaction.setIsVoided(approved);	
			m_mPaymentTransaction.setProcessed(approved);
			m_mPaymentTransaction.setC_Payment_ID(getC_Payment_ID());
			m_mPaymentTransaction.saveEx();
			
			MOnlineTrxHistory history = new MOnlineTrxHistory(getCtx(), 0, trx.getTrxName());
			history.setAD_Table_ID(MPaymentTransaction.Table_ID);
			history.setRecord_ID(m_mPaymentTransaction.getC_PaymentTransaction_ID());
			history.setIsError(!approved);
			history.setProcessed(approved);
			
			StringBuilder msg = new StringBuilder();
			if (approved)
			{
				if(getTrxType().equals(TRXTYPE_Void) || getTrxType().equals(TRXTYPE_CreditPayment))
					msg.append(getR_VoidMsg() + "\n");
				else
				{
					msg.append("Result: " + getR_Result() + "\n");
					msg.append("Response Message: " + getR_RespMsg() + "\n");
					msg.append("Reference: " + getR_PnRef() + "\n");
					msg.append("Authorization Code: " + getR_AuthCode() + "\n");
				}
			}
			else
				msg.append("ERROR: " + getErrorMessage() + "\n");
			msg.append("Transaction Type: " + getTrxType());
			history.setTextMsg(msg.toString());
			
			history.saveEx();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "processOnline", e);
			setErrorMessage(Msg.getMsg(Env.getCtx(), "PaymentNotProcessed") + ": " + e.getMessage());
		}
		finally
		{
			if (trx != null)
			{
				trx.commit();
				trx.close();
			}
		}
		
		if(getTrxType().equals(TRXTYPE_Void) || getTrxType().equals(TRXTYPE_CreditPayment))
			setIsVoided(approved);
		
		return approved;
	}   //  processOnline

	/**
	 *  Execute online processing of payment (delegate to {@link #processOnline()}).
	 *
	 *  @param ctx Context
	 *  @param pi Process Info
	 *  @param trx transaction
	 *  @return true if the next process should be performed
	 */
	public boolean startProcess (Properties ctx, ProcessInfo pi, Trx trx)
	{
		if (log.isLoggable(Level.INFO)) log.info("startProcess - " + pi.getRecord_ID());
		boolean retValue = false;
		//
		if (pi.getRecord_ID() != get_ID())
		{
			log.log(Level.SEVERE, "startProcess - Not same Payment - " + pi.getRecord_ID());
			return false;
		}
		//  Process it
		retValue = processOnline();
		saveEx();
		return retValue;    //  Payment processed
	}   //  startProcess
	
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		// Disallow changes to fields with financial implications if payment have been processed
		if (isProcessed() && 
			! is_ValueChanged(COLUMNNAME_Processed) &&
            (   is_ValueChanged(COLUMNNAME_C_BankAccount_ID)
             || is_ValueChanged(COLUMNNAME_C_BPartner_ID)
             || is_ValueChanged(COLUMNNAME_C_Charge_ID)
             || is_ValueChanged(COLUMNNAME_C_Currency_ID)
             || is_ValueChanged(COLUMNNAME_C_DocType_ID)
             || is_ValueChanged(COLUMNNAME_DateAcct)
             || is_ValueChanged(COLUMNNAME_DateTrx)
             || is_ValueChanged(COLUMNNAME_DiscountAmt)
             || is_ValueChanged(COLUMNNAME_PayAmt)
             || is_ValueChanged(COLUMNNAME_WriteOffAmt))) {
			log.saveError("PaymentAlreadyProcessed", Msg.translate(getCtx(), "C_Payment_ID"));
			return false;
		}
		// Validate that either cash book or bank account is mandatory depending on whether this is a cash book transaction
		if ( isCashbookTrx()) {
			// Cash Book Is mandatory
			if ( getC_CashBook_ID() <= 0 ) {
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@Mandatory@: @C_CashBook_ID@"));
				return false;
			}
		} else {
			// Bank Account Is mandatory
			if ( getC_BankAccount_ID() <= 0 ) {
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@Mandatory@: @C_BankAccount_ID@"));
				return false;
			}
		}
		
		// Reset order, invoice, write off, discount, isOverUnderPayment, OverUnderAmt and IsPrepayment for new Charge payment 
		// or after Charge have been changed
		if (getC_Charge_ID() != 0) 
		{
			if (newRecord || is_ValueChanged("C_Charge_ID"))
			{
				setC_Order_ID(0);
				setC_Invoice_ID(0);
				setWriteOffAmt(Env.ZERO);
				setDiscountAmt(Env.ZERO);
				setIsOverUnderPayment(false);
				setOverUnderAmt(Env.ZERO);
				setIsPrepayment(false);
			}
		}		
		else if (getC_BPartner_ID() == 0 && !isCashTrx())
		{
			if (getC_Invoice_ID() != 0)
				;
			else if (getC_Order_ID() != 0)
				;
			else
			{
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@: @C_BPartner_ID@"));
				return false;
			}
		}
		// Update IsPrepayment flag
		if (newRecord 
			|| is_ValueChanged("C_Charge_ID") || is_ValueChanged("C_Invoice_ID")
			|| is_ValueChanged("C_Order_ID") || is_ValueChanged("C_Project_ID"))
		{
			if (getReversal_ID() > 0)
			{
				setIsPrepayment(getReversal().isPrepayment());
			}
			else
			{
				setIsPrepayment (getC_Charge_ID() == 0 
					&& getC_BPartner_ID() != 0
					&& (getC_Order_ID() != 0 
						|| (getC_Project_ID() != 0 && getC_Invoice_ID() == 0)));
			}
		}
		// Prepayment: reset write off, discount,IsOverUnderPayment and OverUnderAmt for new record or after change of order/project.
		if (isPrepayment())
		{
			if (newRecord 
				|| is_ValueChanged("C_Order_ID") || is_ValueChanged("C_Project_ID"))
			{
				setWriteOffAmt(Env.ZERO);
				setDiscountAmt(Env.ZERO);
				setIsOverUnderPayment(false);
				setOverUnderAmt(Env.ZERO);
			}
		}
		
		//	Document Type/Receipt
		if (getC_DocType_ID() == 0)
			setC_DocType_ID();
		else
		{
			MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
			setIsReceipt(dt.isSOTrx());
		}
		setDocumentNo();
		//
		if (getDateAcct() == null)
			setDateAcct(getDateTrx());
		//
		if (!isOverUnderPayment())
			setOverUnderAmt(Env.ZERO);
		
		//	Organization
		if ((newRecord || is_ValueChanged("C_BankAccount_ID"))
			&& getC_Charge_ID() == 0)	//	allow different org for charge
		{
			MBankAccount ba = MBankAccount.get(getCtx(), getC_BankAccount_ID());
			if (ba.getAD_Org_ID() != 0)
				setAD_Org_ID(ba.getAD_Org_ID());
		}
		
		// Validate C_BPartner_ID same as C_BPartner_ID from order and invoice
		if (getC_BPartner_ID() != 0 && (getC_Invoice_ID() != 0 || getC_Order_ID() != 0)) {
			if (getC_Invoice_ID() != 0) {
				MInvoice inv = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
				if (inv.getC_BPartner_ID() != getC_BPartner_ID()) {
					log.saveError("Error", Msg.getMsg(getCtx(), "BPDifferentFromBPInvoice"));
					return false;
				}
			}
			if (getC_Order_ID() != 0) {
				MOrder ord = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
				if (ord.getC_BPartner_ID() != getC_BPartner_ID()) {
					log.saveError("Error", Msg.getMsg(getCtx(), "BPDifferentFromBPOrder"));
					return false;
				}
			}
		}
		// Encrypt credit card number and cvv
		if (isProcessed())
		{
			if (getCreditCardNumber() != null)
			{
				String encrpytedCCNo = PaymentUtil.encrpytCreditCard(getCreditCardNumber());
				if (!encrpytedCCNo.equals(getCreditCardNumber()))
					setCreditCardNumber(encrpytedCCNo);
			}
			
			if (getCreditCardVV() != null)
			{
				String encrpytedCvv = PaymentUtil.encrpytCvv(getCreditCardVV());
				if (!encrpytedCvv.equals(getCreditCardVV()))
					setCreditCardVV(encrpytedCvv);
			}
		}
		// Validate IBAN
		if (MSysConfig.getBooleanValue(MSysConfig.IBAN_VALIDATION, true, Env.getAD_Client_ID(Env.getCtx()))) {
			if (!Util.isEmpty(getIBAN())) {
				setIBAN(IBAN.normalizeIBAN(getIBAN()));
				if (!IBAN.isValid(getIBAN())) {
					log.saveError("Error", Msg.getMsg(getCtx(), "InvalidIBAN"));
					return false;
				}
			}
		}
		// Validate IsOverrideCurrencyRate and Currency Rate
		if (!isProcessed())
		{
			MClientInfo info = MClientInfo.get(getCtx(), getAD_Client_ID(), get_TrxName()); 
			MAcctSchema as = MAcctSchema.get (getCtx(), info.getC_AcctSchema1_ID(), get_TrxName());
			if (as.getC_Currency_ID() != getC_Currency_ID())
			{
				if (isOverrideCurrencyRate())
				{
					if(getCurrencyRate() == null || getCurrencyRate().signum() == 0)
					{
						log.saveError("FillMandatory", Msg.getElement(getCtx(), COLUMNNAME_CurrencyRate));
						return false;
					}
					if (getConvertedAmt() == null || getConvertedAmt().signum() == 0)
					{
						log.saveError("FillMandatory", Msg.getElement(getCtx(), COLUMNNAME_ConvertedAmt));
						return false;
					}
					BigDecimal converted = getPayAmt().multiply(getCurrencyRate());
					int stdPrecision = MCurrency.getStdPrecision(getCtx(), as.getC_Currency_ID());
					if (converted.scale() > stdPrecision)
						converted = converted.setScale(stdPrecision, RoundingMode.HALF_UP);
					setConvertedAmt(converted);
				}
				else
				{
					setCurrencyRate(null);
					setConvertedAmt(null);
				}
			}
			else
			{
				setCurrencyRate(null);
				setConvertedAmt(null);
			}
		}

		// Clear credit card fields if tender type is not credit card
		if (!isProcessed())
		{
			if (!TENDERTYPE_CreditCard.equals(getTenderType()))
			{
				if (!Util.isEmpty(getCreditCardType(), true))
				{
					setCreditCardType(null);					
				}
				
				if (!Util.isEmpty(getCreditCardNumber(), true))
				{
					setCreditCardNumber(null);
				}
				
				if (!Util.isEmpty(getCreditCardVV(), true))
				{
					setCreditCardVV(null);
				}
				
				if (getCreditCardExpMM() > 0)
				{
					set_Value(COLUMNNAME_CreditCardExpMM, null);
				}
				
				if (getCreditCardExpYY() > 0)
				{
					set_Value(COLUMNNAME_CreditCardExpYY, null);
				}
			}
		}
		
		return true;
	}	//	beforeSave

	@Override
	protected boolean beforeDelete() {
		@SuppressWarnings("unused")
		boolean ok = MPaySelectionCheck.deleteGeneratedDraft(getCtx(), getC_Payment_ID(), get_TrxName());
		return true;
	}

	/**
	 * 	Document Status is Complete or Closed
	 *	@return true if CO, CL or RE
	 */
	public boolean isComplete()
	{
		String ds = getDocStatus();
		return DOCSTATUS_Completed.equals(ds)
			|| DOCSTATUS_Closed.equals(ds)
			|| DOCSTATUS_Reversed.equals(ds);
	}	//	isComplete

	/**
	 * 	Get Allocated Amt in Payment Currency
	 *	@return allocated amount or null
	 */
	public BigDecimal getAllocatedAmt ()
	{
		BigDecimal retValue = null;
		if (getC_Charge_ID() != 0)
			return getPayAmt();
		//
		String sql = "SELECT SUM(currencyConvert(al.Amount,"
				+ "ah.C_Currency_ID, p.C_Currency_ID,ah.DateTrx,p.C_ConversionType_ID, al.AD_Client_ID,al.AD_Org_ID)) "
			+ "FROM C_AllocationLine al"
			+ " INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID=ah.C_AllocationHdr_ID) "
			+ " INNER JOIN C_Payment p ON (al.C_Payment_ID=p.C_Payment_ID) "
			+ "WHERE al.C_Payment_ID=?"
			+ " AND ah.IsActive='Y' AND al.IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Payment_ID());
			rs = pstmt.executeQuery();
			if (rs.next())
				retValue = rs.getBigDecimal(1);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "getAllocatedAmt", e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		return retValue;
	}	//	getAllocatedAmt

	/**
	 * 	Test Allocation (and set allocated flag)
	 *	@return true if IsAllocated updated
	 */
	public boolean testAllocation()
	{
		//
		BigDecimal alloc = getAllocatedAmt();
		if (alloc == null)
			alloc = Env.ZERO;
		BigDecimal total = getPayAmt();
		if (!isReceipt())
			total = total.negate();
		boolean test = total.compareTo(alloc) == 0;
		boolean change = test != isAllocated();
		if (change)
			setIsAllocated(test);
		if (log.isLoggable(Level.FINE)) log.fine("Allocated=" + test 
			+ " (" + alloc + "=" + total + ")");
		return change;
	}	//	testAllocation
	
	/**
	 * 	Set Allocated Flag for payments
	 * 	@param ctx context
	 *	@param C_BPartner_ID if 0 all
	 *	@param trxName trx
	 */
	public static void setIsAllocated (Properties ctx, int C_BPartner_ID, String trxName)
	{
		int counter = 0;
		String sql = "SELECT * FROM C_Payment "
			+ "WHERE IsAllocated='N' AND DocStatus IN ('CO','CL')";
		if (C_BPartner_ID > 1)
			sql += " AND C_BPartner_ID=?";
		else
			sql += " AND AD_Client_ID=" + Env.getAD_Client_ID(ctx);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			if (C_BPartner_ID > 1)
				pstmt.setInt (1, C_BPartner_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				MPayment pay = new MPayment (ctx, rs, trxName);
				if (pay.testAllocation())
					if (pay.save())
						counter++;
			}
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		if (s_log.isLoggable(Level.CONFIG)) s_log.config("#" + counter);
	}	//	setIsAllocated

	/**
	 * 	Set Error Message
	 *	@param errorMessage error message
	 */
	public void setErrorMessage(String errorMessage)
	{
		m_errorMessage = errorMessage;
	}	//	setErrorMessage

	/**
	 * 	Get Error Message
	 *	@return error message
	 */
	public String getErrorMessage()
	{
		return m_errorMessage;
	}	//	getErrorMessage

	/**
	 *  Set Bank Account for Payment.
	 *  @param C_BankAccount_ID C_BankAccount_ID
	 */
	public void setC_BankAccount_ID (int C_BankAccount_ID)
	{
		if (C_BankAccount_ID == 0)
		{
			setPaymentProcessor();
			if (getC_BankAccount_ID() == 0)
				throw new IllegalArgumentException("Can't find Bank Account");
		}
		else
			super.setC_BankAccount_ID(C_BankAccount_ID);
	}	//	setC_BankAccount_ID

	/**
	 *  Set BankAccount and PaymentProcessor
	 *  @return true if found
	 */
	public boolean setPaymentProcessor ()
	{
		return setPaymentProcessor (getTenderType(), getCreditCardType(), getC_PaymentProcessor_ID());
	}	//	setPaymentProcessor

	/**
	 *  Find and Set BankAccount and PaymentProcessor
	 *  @param tender TenderType see TENDER_
	 *  @param CCType CC Type see CC_
	 *  @return true if found
	 */
	public boolean setPaymentProcessor (String tender, String CCType, int C_PaymentProcessor_ID)
	{
		m_mBankAccountProcessor = null;
		//	Get Processor List
		if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0)
			m_mBankAccountProcessors = MBankAccountProcessor.find(getCtx(), tender, CCType, getAD_Client_ID(),
				getC_Currency_ID(), getPayAmt(), get_TrxName());
		//	Relax Amount
		if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0)
			m_mBankAccountProcessors = MBankAccountProcessor.find(getCtx(), tender, CCType, getAD_Client_ID(),
				getC_Currency_ID(), Env.ZERO, get_TrxName());
		if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0)
			return false;

		//	Find the first right one
		for (int i = 0; i < m_mBankAccountProcessors.length; i++)
		{
			MBankAccountProcessor bankAccountProcessor = m_mBankAccountProcessors[i];
			if (bankAccountProcessor.accepts(tender, CCType))
			{
				if (C_PaymentProcessor_ID == 0 || bankAccountProcessor.getC_PaymentProcessor_ID() == C_PaymentProcessor_ID)
				{
					m_mBankAccountProcessor = m_mBankAccountProcessors[i];
					break;
				}
			}
		}
		if (m_mBankAccountProcessor != null)
		{
			setC_BankAccount_ID (m_mBankAccountProcessor.getC_BankAccount_ID());
			setC_PaymentProcessor_ID (m_mBankAccountProcessor.getC_PaymentProcessor_ID());
		}
		//
		return m_mBankAccountProcessor != null;
	}   //  setPaymentProcessor


	/**
	 * 	Get Accepted Credit Cards for PayAmt (default 0)
	 *	@return credit cards
	 */
	public ValueNamePair[] getCreditCards ()
	{
		return getCreditCards(getPayAmt());
	}	//	getCreditCards


	/**
	 * 	Get Accepted Credit Cards for amount
	 *	@param amt trx amount
	 *	@return credit cards
	 */
	public ValueNamePair[] getCreditCards (BigDecimal amt)
	{
		try
		{
			if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0)
				m_mBankAccountProcessors = MBankAccountProcessor.find(getCtx (), null, null, 
					getAD_Client_ID (), getC_Currency_ID (), amt, get_TrxName());
			//
			HashMap<String,ValueNamePair> map = new HashMap<String,ValueNamePair>(); //	to eliminate duplicates
			for (int i = 0; i < m_mBankAccountProcessors.length; i++)
			{
				MBankAccountProcessor bankAccountProcessor = m_mBankAccountProcessors[i];
				MPaymentProcessor paymentProcessor = new MPaymentProcessor(getCtx(), bankAccountProcessor.getC_PaymentProcessor_ID(), get_TrxName());
				
				if (bankAccountProcessor.isAcceptAMEX() && paymentProcessor.isAcceptAMEX())
					map.put (CREDITCARDTYPE_Amex, getCreditCardPair (CREDITCARDTYPE_Amex));
				if (bankAccountProcessor.isAcceptDiners() && paymentProcessor.isAcceptDiners())
					map.put (CREDITCARDTYPE_Diners, getCreditCardPair (CREDITCARDTYPE_Diners));
				if (bankAccountProcessor.isAcceptDiscover() && paymentProcessor.isAcceptDiscover())
					map.put (CREDITCARDTYPE_Discover, getCreditCardPair (CREDITCARDTYPE_Discover));
				if (bankAccountProcessor.isAcceptMC() && paymentProcessor.isAcceptMC())
					map.put (CREDITCARDTYPE_MasterCard, getCreditCardPair (CREDITCARDTYPE_MasterCard));
				if (bankAccountProcessor.isAcceptCorporate() && paymentProcessor.isAcceptCorporate())
					map.put (CREDITCARDTYPE_PurchaseCard, getCreditCardPair (CREDITCARDTYPE_PurchaseCard));
				if (bankAccountProcessor.isAcceptVisa() && paymentProcessor.isAcceptVisa())
					map.put (CREDITCARDTYPE_Visa, getCreditCardPair (CREDITCARDTYPE_Visa));
			} //	for all payment processors
			//
			ValueNamePair[] retValue = new ValueNamePair[map.size ()];
			map.values ().toArray (retValue);
			if (log.isLoggable(Level.FINE)) log.fine("getCreditCards - #" + retValue.length + " - Processors=" + m_mBankAccountProcessors.length);
			return retValue;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}	//	getCreditCards

	/**
	 * 	Get Type and name pair
	 *	@param CreditCardType credit card Type
	 *	@return ValueNamePair (CreditCardType, Name)
	 */
	protected ValueNamePair getCreditCardPair (String CreditCardType)
	{
		return new ValueNamePair (CreditCardType, getCreditCardName(CreditCardType));
	}	//	getCreditCardPair
	
	/**
	 *  Set Credit Card Number.
	 *  @param CreditCardNumber CreditCard Number
	 */
	@Override
	public void setCreditCardNumber (String CreditCardNumber)
	{
		super.setCreditCardNumber (MPaymentValidate.checkNumeric(CreditCardNumber));
	}	//	setCreditCardNumber
	
	/**
	 *  Set Verification Code
	 *  @param newCreditCardVV CC verification
	 */
	@Override
	public void setCreditCardVV(String newCreditCardVV)
	{
		super.setCreditCardVV (MPaymentValidate.checkNumeric(newCreditCardVV));
	}	//	setCreditCardVV

	/**
	 *  Set Two Digit CreditCard MM
	 *  @param CreditCardExpMM Exp month
	 */
	@Override
	public void setCreditCardExpMM (int CreditCardExpMM)
	{
		if (CreditCardExpMM < 1 || CreditCardExpMM > 12)
			;
		else
			super.setCreditCardExpMM (CreditCardExpMM);
	}	//	setCreditCardExpMM

	/**
	 *  Set Two digit CreditCard YY (til 2020)
	 *  @param newCreditCardExpYY 2 or 4 digit year
	 */
	@Override
	public void setCreditCardExpYY (int newCreditCardExpYY)
	{
		int CreditCardExpYY = newCreditCardExpYY;
		if (newCreditCardExpYY > 1999)
			CreditCardExpYY = newCreditCardExpYY-2000;
		super.setCreditCardExpYY(CreditCardExpYY);
	}	//	setCreditCardExpYY

	/**
	 *  Set CreditCard Exp  MMYY
	 *  @param mmyy Exp in form of mmyy
	 *  @return true if valid
	 */
	public boolean setCreditCardExp (String mmyy)
	{
		if (MPaymentValidate.validateCreditCardExp(mmyy).length() != 0)
			return false;
		//
		String exp = MPaymentValidate.checkNumeric(mmyy);
		String mmStr = exp.substring(0,2);
		String yyStr = exp.substring(2,4);
		setCreditCardExpMM (Integer.parseInt(mmStr));
		setCreditCardExpYY (Integer.parseInt(yyStr));
		return true;
	}   //  setCreditCardExp


	/**
	 *  CreditCard Exp  MMYY
	 *  @param delimiter / - or null
	 *  @return Exp (mm + delimiter + yy)
	 */
	public String getCreditCardExp(String delimiter)
	{
		String mm = String.valueOf(getCreditCardExpMM());
		String yy = String.valueOf(getCreditCardExpYY());

		StringBuilder retValue = new StringBuilder();
		if (mm.length() == 1)
			retValue.append("0");
		retValue.append(mm);
		//
		if (delimiter != null)
			retValue.append(delimiter);
		//
		if (yy.length() == 1)
			retValue.append("0");
		retValue.append(yy);
		//
		return (retValue.toString());
	}   //  getCreditCardExp

	/**
	 *  MICR
	 *  @param MICR MICR
	 */
	@Override
	public void setMicr (String MICR)
	{
		super.setMicr (MPaymentValidate.checkNumeric(MICR));
	}	//	setBankMICR

	/**
	 *  Routing No
	 *  @param RoutingNo Routing No
	 */
	@Override
	public void setRoutingNo(String RoutingNo)
	{
		super.setRoutingNo (RoutingNo);
	}	//	setBankRoutingNo

	/**
	 *  Bank Account No
	 *  @param AccountNo AccountNo
	 */
	@Override
	public void setAccountNo (String AccountNo)
	{
		super.setAccountNo (MPaymentValidate.checkNumeric(AccountNo));
	}	//	setBankAccountNo

	/**
	 *  Check No
	 *  @param CheckNo Check No
	 */
	@Override
	public void setCheckNo(String CheckNo)
	{
		super.setCheckNo(MPaymentValidate.checkNumeric(CheckNo));
	}	//	setBankCheckNo


	/**
	 *  Derive DocumentNo from Payment info.
	 * 	If there is a R_PnRef, take R_PnRef as DocumentNo.
	 */
	protected void setDocumentNo()
	{
		//	Cash Transfer
		if ("X".equals(getTenderType()))
			return;
		//	Current Document No
		String documentNo = getDocumentNo();
		//	Existing reversal
		if (documentNo != null 
			&& documentNo.indexOf(REVERSE_INDICATOR) >= 0)
			return;
		
		//	If external number exists - enforce it 
		if (getR_PnRef() != null && getR_PnRef().length() > 0)
		{
			if (!getR_PnRef().equals(documentNo))
				setDocumentNo(getR_PnRef());
			return;
		}
		
		documentNo = "";
		// globalqss - read configuration to assign credit card or check number number for Payments
		//	Credit Card
		if (TENDERTYPE_CreditCard.equals(getTenderType()))
		{
			if (MSysConfig.getBooleanValue(MSysConfig.PAYMENT_OVERWRITE_DOCUMENTNO_WITH_CREDIT_CARD, true, getAD_Client_ID())) {
				documentNo = getCreditCardType()
					+ " " + Obscure.obscure(getCreditCardNumber())
					+ " " + getCreditCardExpMM() 
					+ "/" + getCreditCardExpYY();
			}
		}
		//	Own Check No
		else if (TENDERTYPE_Check.equals(getTenderType())
			&& !isReceipt()
			&& getCheckNo() != null && getCheckNo().length() > 0)
		{
			if (MSysConfig.getBooleanValue(MSysConfig.PAYMENT_OVERWRITE_DOCUMENTNO_WITH_CHECK_ON_PAYMENT, true, getAD_Client_ID())) {
				documentNo = getCheckNo();
			}
		}
		//	Customer Check: Routing: Account #Check 
		else if (TENDERTYPE_Check.equals(getTenderType())
			&& isReceipt())
		{
			if (MSysConfig.getBooleanValue(MSysConfig.PAYMENT_OVERWRITE_DOCUMENTNO_WITH_CHECK_ON_RECEIPT, true, getAD_Client_ID())) {
				if (getRoutingNo() != null)
					documentNo = getRoutingNo() + ": ";
				if (getAccountNo() != null)
					documentNo += getAccountNo();
				if (getCheckNo() != null)
				{
					if (documentNo.length() > 0)
						documentNo += " ";
					documentNo += "#" + getCheckNo();
				}
			}
		}

		//	Set Document No
		documentNo = documentNo.trim();
		if (documentNo.length() > 0)
			setDocumentNo(documentNo);
	}	//	setDocumentNo

	/**
	 * 	Set Reference No (and Document No)
	 *	@param R_PnRef reference
	 */
	@Override
	public void setR_PnRef (String R_PnRef)
	{
		super.setR_PnRef (R_PnRef);
		if (R_PnRef != null)
			setDocumentNo (R_PnRef);
	}	//	setR_PnRef
	
	/**
	 *  Set Payment Amount
	 *  @param PayAmt Pay Amt
	 */
	@Override
	public void setPayAmt (BigDecimal PayAmt)
	{
		super.setPayAmt(PayAmt == null ? Env.ZERO : PayAmt);
	}	//	setPayAmt

	/**
	 * Set Payment Amount and Currency
	 *
	 * @param C_Currency_ID currency
	 * @param payAmt amount
	 */
	public void setAmount (int C_Currency_ID, BigDecimal payAmt)
	{
		if (C_Currency_ID == 0)
			C_Currency_ID = MClient.get(getCtx()).getC_Currency_ID();
		setC_Currency_ID(C_Currency_ID);
		setPayAmt(payAmt);
	}   //  setAmount

	/**
	 *  Discount Amt
	 *  @param DiscountAmt Discount
	 */
	@Override
	public void setDiscountAmt (BigDecimal DiscountAmt)
	{
		super.setDiscountAmt (DiscountAmt == null ? Env.ZERO : DiscountAmt);
	}	//	setDiscountAmt

	/**
	 *  WriteOff Amt
	 *  @param WriteOffAmt WriteOff
	 */
	@Override
	public void setWriteOffAmt (BigDecimal WriteOffAmt)
	{
		super.setWriteOffAmt (WriteOffAmt == null ? Env.ZERO : WriteOffAmt);
	}	//	setWriteOffAmt

	/**
	 *  OverUnder Amt
	 *  @param OverUnderAmt OverUnder
	 */
	@Override
	public void setOverUnderAmt (BigDecimal OverUnderAmt)
	{
		super.setOverUnderAmt (OverUnderAmt == null ? Env.ZERO : OverUnderAmt);
		setIsOverUnderPayment(getOverUnderAmt().compareTo(Env.ZERO) != 0);
	}	//	setOverUnderAmt

	/**
	 *  Tax Amt
	 *  @param TaxAmt Tax
	 */
	@Override
	public void setTaxAmt (BigDecimal TaxAmt)
	{
		super.setTaxAmt (TaxAmt == null ? Env.ZERO : TaxAmt);
	}	//	setTaxAmt

	/**
	 * 	Set Info from BP Bank Account
	 *	@param ba BP bank account
	 */
	public void setBP_BankAccount (MBPBankAccount ba)
	{
		if (log.isLoggable(Level.FINE)) log.fine("" + ba);
		if (ba == null)
			return;
		setC_BPartner_ID(ba.getC_BPartner_ID());
		setAccountAddress(ba.getA_Name(), ba.getA_Street(), ba.getA_City(),
			ba.getA_State(), ba.getA_Zip(), ba.getA_Country());
		setA_EMail(ba.getA_EMail());
		setA_Ident_DL(ba.getA_Ident_DL());
		setA_Ident_SSN(ba.getA_Ident_SSN());
		//	CC
		if (ba.getCreditCardType() != null)
			setCreditCardType(ba.getCreditCardType());
		if (ba.getCreditCardNumber() != null)
			setCreditCardNumber(ba.getCreditCardNumber());
		if (ba.getCreditCardExpMM() != 0)
			setCreditCardExpMM(ba.getCreditCardExpMM());
		if (ba.getCreditCardExpYY() != 0)
			setCreditCardExpYY(ba.getCreditCardExpYY());
		if (ba.getCreditCardVV() != null)
			setCreditCardVV(ba.getCreditCardVV());
		//	Bank
		if (ba.getAccountNo() != null)
			setAccountNo(ba.getAccountNo());
		if (ba.getRoutingNo() != null)
			setRoutingNo(ba.getRoutingNo());
		if (ba.getIBAN() != null)
			setIBAN(ba.getIBAN());
		if (ba.getSwiftCode() != null)
			setSwiftCode(ba.getSwiftCode()) ;
	}	//	setBP_BankAccount

	/**
	 * 	Save Info to BP Bank Account
	 *	@param ba BP bank account
	 * 	@return true if saved
	 */
	public boolean saveToBP_BankAccount (MBPBankAccount ba)
	{
		if (ba == null)
			return false;
		ba.setA_Name(getA_Name());
		ba.setA_Street(getA_Street());
		ba.setA_City(getA_City());
		ba.setA_State(getA_State());
		ba.setA_Zip(getA_Zip());
		ba.setA_Country(getA_Country());
		ba.setA_EMail(getA_EMail());
		ba.setA_Ident_DL(getA_Ident_DL());
		ba.setA_Ident_SSN(getA_Ident_SSN());
		//	CC
		ba.setCreditCardType(getCreditCardType());
		ba.setCreditCardNumber(getCreditCardNumber());
		ba.setCreditCardExpMM(getCreditCardExpMM());
		ba.setCreditCardExpYY(getCreditCardExpYY());
		ba.setCreditCardVV(getCreditCardVV());
		//	Bank
		if (getAccountNo() != null)
			ba.setAccountNo(getAccountNo());
		if (getRoutingNo() != null)
			ba.setRoutingNo(getRoutingNo());
		if (getIBAN() != null)
			ba.setIBAN(getIBAN());
		//	Trx
		ba.setR_AvsAddr(getR_AvsAddr());
		ba.setR_AvsZip(getR_AvsZip());
		//
		boolean ok = ba.save(get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("saveToBP_BankAccount - " + ba);
		return ok;
	}	//	setBP_BankAccount

	/**
	 * 	Set Doc Type bases on IsReceipt
	 */
	protected void setC_DocType_ID ()
	{
		setC_DocType_ID(isReceipt());
	}	//	setC_DocType_ID

	/**
	 * 	Set Doc Type
	 * 	@param isReceipt true for receipt, false for payment
	 */
	public void setC_DocType_ID (boolean isReceipt)
	{
		setIsReceipt(isReceipt);
		String sql = "SELECT C_DocType_ID FROM C_DocType WHERE IsActive='Y' AND AD_Client_ID=? AND DocBaseType=? ORDER BY IsDefault DESC";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_Client_ID());
			if (isReceipt)
				pstmt.setString(2, X_C_DocType.DOCBASETYPE_ARReceipt);
			else
				pstmt.setString(2, X_C_DocType.DOCBASETYPE_APPayment);
			rs = pstmt.executeQuery();
			if (rs.next())
				setC_DocType_ID(rs.getInt(1));
			else
				log.warning ("setDocType - NOT found - isReceipt=" + isReceipt);
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}	//	setC_DocType_ID

	
	/**
	 * 	Set Document Type
	 *	@param C_DocType_ID doc type
	 */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		super.setC_DocType_ID(C_DocType_ID);
	}	//	setC_DocType_ID
	
	/**
	 * 	Verify Document Type with Invoice
	 *  @param pAllocs 
	 *	@return true if ok
	 */
	protected boolean verifyDocType(MPaymentAllocate[] pAllocs)
	{
		if (getC_DocType_ID() == 0)
			return false;
		//
		Boolean documentSO = null;
		//	Check Invoice First
		if (getC_Invoice_ID() > 0)
		{
			String sql = "SELECT idt.IsSOTrx "
				+ "FROM C_Invoice i"
				+ " INNER JOIN C_DocType idt ON (CASE WHEN i.C_DocType_ID=0 THEN i.C_DocTypeTarget_ID ELSE i.C_DocType_ID END=idt.C_DocType_ID) "
				+ "WHERE i.C_Invoice_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getC_Invoice_ID());
				rs = pstmt.executeQuery();
				if (rs.next())
					documentSO = Boolean.valueOf("Y".equals(rs.getString(1)));
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}	//	now Order - in Adempiere is allowed to pay PO or receive SO
		else if (getC_Order_ID() > 0)
		{
			String sql = "SELECT odt.IsSOTrx "
				+ "FROM C_Order o"
				+ " INNER JOIN C_DocType odt ON (o.C_DocType_ID=odt.C_DocType_ID) "
				+ "WHERE o.C_Order_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getC_Order_ID());
				rs = pstmt.executeQuery();
				if (rs.next())
					documentSO = Boolean.valueOf("Y".equals(rs.getString(1)));
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}	//	now Charge
		else if (getC_Charge_ID() > 0) 
		{
			// do nothing about charge
		} // now payment allocate
		else
		{
			if (pAllocs.length > 0) {
				for (MPaymentAllocate pAlloc : pAllocs) {
					String sql = "SELECT idt.IsSOTrx "
						+ "FROM C_Invoice i"
						+ " INNER JOIN C_DocType idt ON (i.C_DocType_ID=idt.C_DocType_ID) "
						+ "WHERE i.C_Invoice_ID=?";
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, get_TrxName());
						pstmt.setInt(1, pAlloc.getC_Invoice_ID());
						rs = pstmt.executeQuery();
						if (rs.next()) {
							if (documentSO != null) { // already set, compare with current
								if (documentSO.booleanValue() != ("Y".equals(rs.getString(1)))) {
									return false;
								}
							} else {
								documentSO = Boolean.valueOf("Y".equals(rs.getString(1)));
							}
						}
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, sql, e);
					}
					finally
					{
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
			}
		}
		
		//	DocumentType
		Boolean paymentSO = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT IsSOTrx "
			+ "FROM C_DocType "
			+ "WHERE C_DocType_ID=?";
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_DocType_ID());
			rs = pstmt.executeQuery();
			if (rs.next())
				paymentSO = Boolean.valueOf("Y".equals(rs.getString(1)));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		//	No Payment info
		if (paymentSO == null)
			return false;
		setIsReceipt(paymentSO.booleanValue());
			
		//	We have an Invoice .. and it does not match
		if (documentSO != null 
				&& documentSO.booleanValue() != paymentSO.booleanValue())
			return false;
		//	OK
		return true;
	}	//	verifyDocType

	/**
	 * 	Verify that payment has no Payment Allocate records if the payment header has charge/invoice/order.
	 *  @param pAllocs 
	 *	@return true if pAllocs is empty
	 */
	protected boolean verifyPaymentAllocateVsHeader(MPaymentAllocate[] pAllocs) {
		if (pAllocs.length > 0) {
			if (getC_Charge_ID() > 0 || getC_Invoice_ID() > 0 || getC_Order_ID() > 0)
				return false;
		}
		return true;
	}

	/**
	 * 	Verify Payment Allocate Sum must be equal to the Payment Amount
	 *  @param pAllocs 
	 *	@return true if ok
	 */
	protected boolean verifyPaymentAllocateSum(MPaymentAllocate[] pAllocs) {
		BigDecimal sumPaymentAllocates = Env.ZERO;
		if (pAllocs.length > 0) {
			for (MPaymentAllocate pAlloc : pAllocs)
				sumPaymentAllocates = sumPaymentAllocates.add(pAlloc.getAmount());
			if (getPayAmt().compareTo(sumPaymentAllocates) != 0) {
				if (isReceipt() && getPayAmt().compareTo(sumPaymentAllocates) < 0) {
					if (MSysConfig.getBooleanValue(MSysConfig.ALLOW_OVER_APPLIED_PAYMENT, false, Env.getAD_Client_ID(Env.getCtx()))) {
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	/**
	 *	Get ISO Code of Currency
	 *	@return Currency ISO code
	 */
	public String getCurrencyISO()
	{
		return MCurrency.getISO_Code (getCtx(), getC_Currency_ID());
	}	//	getCurrencyISO

	/**
	 * 	Get Document Status Name
	 *	@return Document Status Name
	 */
	public String getDocStatusName()
	{
		return MRefList.getListName(getCtx(), 131, getDocStatus());
	}	//	getDocStatusName

	/**
	 *	Get Name of Credit Card Type
	 *	@return Name of Credit Card Type (Master, Visa, etc)
	 */
	public String getCreditCardName()
	{
		return getCreditCardName(getCreditCardType());
	}	//	getCreditCardName

	/**
	 *	Get Name of Credit Card Type
	 * 	@param CreditCardType credit card type
	 *	@return Name of Credit Card Type (Master, Visa, etc)
	 */
	public String getCreditCardName(String CreditCardType)
	{
		if (CreditCardType == null)
			return "--";
		else if (CREDITCARDTYPE_MasterCard.equals(CreditCardType))
			return "MasterCard";
		else if (CREDITCARDTYPE_Visa.equals(CreditCardType))
			return "Visa";
		else if (CREDITCARDTYPE_Amex.equals(CreditCardType))
			return "Amex";
		else if (CREDITCARDTYPE_ATM.equals(CreditCardType))
			return "ATM";
		else if (CREDITCARDTYPE_Diners.equals(CreditCardType))
			return "Diners";
		else if (CREDITCARDTYPE_Discover.equals(CreditCardType))
			return "Discover";
		else if (CREDITCARDTYPE_PurchaseCard.equals(CreditCardType))
			return "PurchaseCard";
		return "?" + CreditCardType + "?";
	}	//	getCreditCardName

	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else
			setDescription(desc + " | " + description);
	}	//	addDescription
		
	/**
	 * 	Get Pay Amt
	 * 	@param absolute ignore
	 *	@return pay amt if this is receipt, otherwise it return the negate of pay amt
	 */
	public BigDecimal getPayAmt (boolean absolute)
	{
		if (isReceipt())
			return super.getPayAmt();
		return super.getPayAmt().negate();
	}	//	getPayAmt
	
	/**
	 * 	Get Pay Amt in cents
	 *	@return amount in cents (multiply by 100 and truncate to integer)
	 */
	public int getPayAmtInCents ()
	{
		BigDecimal bd = super.getPayAmt().multiply(Env.ONEHUNDRED);
		return bd.intValue();
	}	//	getPayAmtInCents
	
	/**
	 * 	Process document
	 *	@param processAction document action
	 *	@return true if performed
	 */
	@Override
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}	//	process
	
	/**	Process Message 			*/
	protected String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	protected boolean		m_justPrepared = false;
	protected IProcessUI m_processUI;

	/**
	 * 	Unlock Document.
	 * 	@return true if success 
	 */
	@Override
	public boolean unlockIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setProcessing(false);
		return true;
	}	//	unlockIt
	
	/**
	 * 	Invalidate Document
	 * 	@return true if success 
	 */
	@Override
	public boolean invalidateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}	//	invalidateIt

	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid) 
	 */
	@Override
	public String prepareIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		if (! MPaySelectionCheck.deleteGeneratedDraft(getCtx(), getC_Payment_ID(), get_TrxName())) {
			m_processMsg = "Could not delete draft generated payment selection lines";
			return DocAction.STATUS_Invalid;
		}

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), 
			isReceipt() ? X_C_DocType.DOCBASETYPE_ARReceipt : X_C_DocType.DOCBASETYPE_APPayment, getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		
		//	Unsuccessful Online Payment
		if (isOnline() && !isApproved())
		{
			if (getR_Result() != null)
				m_processMsg = "@OnlinePaymentFailed@";
			else
				m_processMsg = "@PaymentNotProcessed@";
			return DocAction.STATUS_Invalid;
		}
		
		//	Waiting Payment - Need to create Invoice & Shipment
		if (getC_Order_ID() != 0 && getC_Invoice_ID() == 0)
		{	//	see WebOrder.process
			MOrder order = new MOrder (getCtx(), getC_Order_ID(), get_TrxName());
			if (DOCSTATUS_WaitingPayment.equals(order.getDocStatus()))
			{
				order.setC_Payment_ID(getC_Payment_ID());
				order.setDocAction(X_C_Order.DOCACTION_WaitComplete);
				order.set_TrxName(get_TrxName());
				// added AdempiereException by zuhri 
				if (!order.processIt (X_C_Order.DOCACTION_WaitComplete))
					throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + order.getProcessMsg());
				// end added
				m_processMsg = order.getProcessMsg();
				order.saveEx(get_TrxName());
				//	Set Invoice
				MInvoice[] invoices = order.getInvoices();
				int length = invoices.length;
				if (length > 0)		//	get last invoice
					setC_Invoice_ID (invoices[length-1].getC_Invoice_ID());
				//
				MDocType orderDocType = MDocType.get(getCtx(), order.getC_DocType_ID());
				if (orderDocType.isAutoGenerateInvoice() && getC_Invoice_ID() == 0)
				{
					m_processMsg = "@NotFound@ @C_Invoice_ID@";
					return DocAction.STATUS_Invalid;
				}
			}	//	WaitingPayment
		}
		
		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(this);
		
		//	Consistency of Invoice / Document Type and IsReceipt
		if (!verifyDocType(pAllocs))
		{
			m_processMsg = "@PaymentDocTypeInvoiceInconsistent@";
			return DocAction.STATUS_Invalid;
		}

		//	Payment Allocate is ignored if charge/invoice/order exists in header
		if (!verifyPaymentAllocateVsHeader(pAllocs))
		{
			m_processMsg = "@PaymentAllocateIgnored@";
			return DocAction.STATUS_Invalid;
		}

		//	Payment Amount must be equal to sum of Allocate amounts
		if (!verifyPaymentAllocateSum(pAllocs))
		{
			m_processMsg = "@PaymentAllocateSumInconsistent@";
			return DocAction.STATUS_Invalid;
		}

		ICreditManager creditManager = Core.getCreditManager(this);
		if (creditManager != null)
		{
			CreditStatus status = creditManager.checkCreditStatus(DOCACTION_Prepare);
			if (status.isError())
			{
				m_processMsg = status.getErrorMsg();
				return DocAction.STATUS_Invalid;
			}
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
	/**
	 * 	Approve Document
	 * 	@return true if success 
	 */
	@Override
	public boolean  approveIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(true);
		return true;
	}	//	approveIt
	
	/**
	 * 	Reject Approval
	 * 	@return true if success 
	 */
	@Override
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(false);
		return true;
	}	//	rejectIt
	
	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	@Override
	public String completeIt()
	{
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			m_justPrepared = false;
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		// Set the definite document number after completed (if needed)
		setDefiniteDocumentNo();

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());

		//	Charge Handling
		if (getC_Charge_ID() != 0)
		{
			setIsAllocated(true);
		}

		ICreditManager creditManager = Core.getCreditManager(this);
		if (creditManager != null)
		{
			CreditStatus status = creditManager.checkCreditStatus(DOCACTION_Complete);
			if (status.isError())
			{
				m_processMsg = status.getErrorMsg();
				return DocAction.STATUS_Invalid;
			}
		}
		
		//	Counter Doc
		MPayment counter = createCounterDoc();
		if (counter != null)
			m_processMsg += " @CounterDoc@: @C_Payment_ID@=" + counter.getDocumentNo();

		// @Trifon - CashPayments
		if ( isCashbookTrx()) {
			// Create Cash Book entry
			if ( getC_CashBook_ID() <= 0 ) {
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@Mandatory@: @C_CashBook_ID@"));
				m_processMsg = "@NoCashBook@";
				return DocAction.STATUS_Invalid;
			}
			MCash cash = MCash.get (getCtx(), getAD_Org_ID(), getDateAcct(), getC_Currency_ID(), get_TrxName());
			if (cash == null || cash.get_ID() == 0)
			{
				m_processMsg = "@NoCashBook@";
				return DocAction.STATUS_Invalid;
			}
			MCashLine cl = new MCashLine( cash );
			cl.setCashType( X_C_CashLine.CASHTYPE_GeneralReceipts );
			cl.setDescription("Generated From Payment #" + getDocumentNo());
			cl.setC_Currency_ID( this.getC_Currency_ID() );
			cl.setC_Payment_ID( getC_Payment_ID() ); // Set Reference to payment.
			StringBuilder info=new StringBuilder();
			info.append("Cash journal ( ")
				.append(cash.getDocumentNo()).append(" )");				
			m_processMsg = info.toString();
			//	Amount
			BigDecimal amt = this.getPayAmt();
			cl.setAmount( amt );
			//
			cl.setDiscountAmt( Env.ZERO );
			cl.setWriteOffAmt( Env.ZERO );
			cl.setIsGenerated( true );
			
			if (!cl.save(get_TrxName()))
			{
				m_processMsg = "Could not save Cash Journal Line";
				return DocAction.STATUS_Invalid;
			}
		}
		// End Trifon - CashPayments
		
		//	update C_Invoice.C_Payment_ID and C_Order.C_Payment_ID reference
		if (getC_Invoice_ID() != 0)
		{
			MInvoice inv = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
			if (inv.getC_Payment_ID() != getC_Payment_ID())
			{
				inv.setC_Payment_ID(getC_Payment_ID());
				inv.saveEx();
			}
		}		
		if (getC_Order_ID() != 0)
		{
			MOrder ord = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
			if (ord.getC_Payment_ID() != getC_Payment_ID())
			{
				ord.setC_Payment_ID(getC_Payment_ID());
				ord.saveEx();
			}
		}
		
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		//
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt

	/* Save array of documents to process AFTER completing this one */
	protected ArrayList<PO> docsPostProcess = new ArrayList<PO>();

	/**
	 * Add document for processing after document action
	 * @param doc
	 */
	protected void addDocsPostProcess(PO doc) {
		docsPostProcess.add(doc);
	}

	@Override
	public List<PO> getDocsPostProcess() {
		return docsPostProcess;
	}

	/**
	 * 	Set the definite document number after completed
	 */
	protected void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateTrx(TimeUtil.getDay(0));
			if (getDateAcct().before(getDateTrx())) {
				setDateAcct(getDateTrx());
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
		}
		if (dt.isOverwriteSeqOnComplete()) {
			if (this.getProcessedOn().signum() == 0) {
				String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
				if (value != null)
					setDocumentNo(value);
			}
		}
	}

	/**
	 * 	Create Counter Document
	 * 	@return payment
	 */
	protected MPayment createCounterDoc()
	{
		//	Is this a counter doc ?
		if (getRef_Payment_ID() != 0)
			return null;

		//	Org Must be linked to BPartner
		MOrg org = MOrg.get(getCtx(), getAD_Org_ID());
		int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(get_TrxName()); 
		if (counterC_BPartner_ID == 0)
			return null;
		//	Business Partner needs to be linked to Org
		MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), get_TrxName());
		int counterAD_Org_ID = bp.getAD_OrgBP_ID(); 
		if (counterAD_Org_ID == 0)
			return null;
		
		MBPartner counterBP = new MBPartner (getCtx(), counterC_BPartner_ID, get_TrxName());

		if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.getName());

		//	Document Type
		int C_DocTypeTarget_ID = 0;
		MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getCtx(), getC_DocType_ID());
		if (counterDT != null)
		{
			if (log.isLoggable(Level.FINE)) log.fine(counterDT.toString());
			if (!counterDT.isCreateCounter() || !counterDT.isValid())
				return null;
			C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
		}
		else	//	indirect
		{
			C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID(getCtx(), getC_DocType_ID());
			if (log.isLoggable(Level.FINE)) log.fine("Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);
			if (C_DocTypeTarget_ID <= 0)
				return null;
		}

		//	Deep Copy
		MPayment counter = new MPayment (getCtx(), 0, get_TrxName());
		counter.setAD_Org_ID(counterAD_Org_ID);
		counter.setC_BPartner_ID(counterBP.getC_BPartner_ID());
		counter.setIsReceipt(!isReceipt());
		counter.setC_DocType_ID(C_DocTypeTarget_ID);
		counter.setTrxType(getTrxType());
		counter.setTenderType(getTenderType());
		//
		counter.setPayAmt(getPayAmt());
		counter.setDiscountAmt(getDiscountAmt());
		counter.setTaxAmt(getTaxAmt());
		counter.setWriteOffAmt(getWriteOffAmt());
		counter.setIsOverUnderPayment (isOverUnderPayment());
		counter.setOverUnderAmt(getOverUnderAmt());
		counter.setC_Currency_ID(getC_Currency_ID());
		counter.setC_ConversionType_ID(getC_ConversionType_ID());
		//
		counter.setDateTrx (getDateTrx());
		counter.setDateAcct (getDateAcct());
		counter.setRef_Payment_ID(getC_Payment_ID());
		//
		String sql = "SELECT C_BankAccount_ID FROM C_BankAccount "
			+ "WHERE C_Currency_ID=? AND AD_Org_ID IN (0,?) AND IsActive='Y' AND AD_Client_ID = ? "
			+ "ORDER BY IsDefault DESC";
		int C_BankAccount_ID = DB.getSQLValue(get_TrxName(), sql, getC_Currency_ID(), counterAD_Org_ID,getAD_Client_ID());
		counter.setC_BankAccount_ID(C_BankAccount_ID);

		//	References
		counter.setC_Activity_ID(getC_Activity_ID());
		counter.setC_Campaign_ID(getC_Campaign_ID());
		counter.setC_Project_ID(getC_Project_ID());
		counter.setUser1_ID(getUser1_ID());
		counter.setUser2_ID(getUser2_ID());
		counter.setC_CostCenter_ID(getC_CostCenter_ID());
		counter.setC_Department_ID(getC_Department_ID());
		counter.saveEx(get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine(counter.toString());
		setRef_Payment_ID(counter.getC_Payment_ID());
		
		//	Document Action
		if (counterDT != null)
		{
			if (counterDT.getDocAction() != null)
			{
				counter.setDocAction(counterDT.getDocAction());
				// added AdempiereException by zuhri
				if (!counter.processIt(counterDT.getDocAction()))
					throw new AdempiereException("Failed when rocessing document - " + counter.getProcessMsg());
				// end added
				counter.saveEx(get_TrxName());
			}
		}
		return counter;
	}	//	createCounterDoc
	
	/**
	 * 	Allocate this payment.<br/>
	 * 	Only call this when there is NO allocations (MAllocationHdr and MAllocationLine) as it will create duplicates.<br/>
	 * 	If an invoice exists, it will allocates that, otherwise it will allocates to Payment Selection.
	 *	@return true if allocated
	 */
	public boolean allocateIt()
	{
		//	Create invoice Allocation
		if (getC_Invoice_ID() != 0)
		{	
			return allocateInvoice();
		}	
		//	Invoices of a AP Payment Selection
		if (allocatePaySelection())
			return true;
		
		if (getC_Order_ID() != 0)
			return false;
			
		//	Allocate to multiple Payments based on entry
		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(this);
		if (pAllocs.length == 0)
			return false;
		
		MAllocationHdr alloc = new MAllocationHdr(getCtx(), false, 
			getDateTrx(), getC_Currency_ID(), 
				Msg.translate(getCtx(), "C_Payment_ID")	+ ": " + getDocumentNo(), 
				get_TrxName());
		alloc.setAD_Org_ID(getAD_Org_ID());
		alloc.setDateAcct(getDateAcct()); // in case date acct is different from datetrx in payment; IDEMPIERE-1532 tbayen
		if (!alloc.save())
		{
			log.severe("P.Allocations not created");
			return false;
		}
		//	Lines
		for (int i = 0; i < pAllocs.length; i++)
		{
			MPaymentAllocate pa = pAllocs[i];

			BigDecimal allocationAmt = pa.getAmount();			//	underpayment
			if (pa.getOverUnderAmt().signum() < 0 && pa.getAmount().signum() > 0)
				allocationAmt = allocationAmt.add(pa.getOverUnderAmt());	//	overpayment (negative)

			MAllocationLine aLine = null;
			if (isReceipt())
				aLine = new MAllocationLine (alloc, allocationAmt,
					pa.getDiscountAmt(), pa.getWriteOffAmt(), pa.getOverUnderAmt());
			else
				aLine = new MAllocationLine (alloc, allocationAmt.negate(),
					pa.getDiscountAmt().negate(), pa.getWriteOffAmt().negate(), pa.getOverUnderAmt().negate());
			aLine.setDocInfo(pa.getC_BPartner_ID(), 0, pa.getC_Invoice_ID());
			aLine.setPaymentInfo(getC_Payment_ID(), 0, getC_BankTransfer_ID());
			if (!aLine.save(get_TrxName()))
				log.warning("P.Allocations - line not saved");
			else
			{
				pa.setC_AllocationLine_ID(aLine.getC_AllocationLine_ID());
				pa.saveEx();
			}
		}
		//do not post immediate alloc, alloc should post after payment
		alloc.set_Attribute(DocumentEngine.DOCUMENT_POST_IMMEDIATE_AFTER_COMPLETE, Boolean.FALSE);
		// added AdempiereException by zuhri
		if (!alloc.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + alloc.getProcessMsg());
		addDocsPostProcess(alloc);
		// end added
		m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();
		return alloc.save(get_TrxName());
	}	//	allocateIt

	/**
	 * 	Allocate to single AP/AR Invoice
	 * 	@return true if allocated
	 */
	protected boolean allocateInvoice()
	{
		//	calculate actual allocation
		BigDecimal allocationAmt = getPayAmt();			//	underpayment
		if (getOverUnderAmt().signum() < 0 && getPayAmt().signum() > 0)
			allocationAmt = allocationAmt.add(getOverUnderAmt());	//	overpayment (negative)

		MAllocationHdr alloc = new MAllocationHdr(getCtx(), false, 
			getDateTrx(), getC_Currency_ID(),
			Msg.translate(getCtx(), "C_Payment_ID") + ": " + getDocumentNo() + " [1]", get_TrxName());
		alloc.setAD_Org_ID(getAD_Org_ID());
		alloc.setDateAcct(getDateAcct()); // in case date acct is different from datetrx in payment
		MInvoice invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
		if (invoice.getDateAcct().after(alloc.getDateAcct())) {
			alloc.setDateAcct(invoice.getDateAcct());
		}
		alloc.saveEx();
		MAllocationLine aLine = null;
		if (isReceipt())
			aLine = new MAllocationLine (alloc, allocationAmt, 
				getDiscountAmt(), getWriteOffAmt(), getOverUnderAmt());
		else
			aLine = new MAllocationLine (alloc, allocationAmt.negate(), 
				getDiscountAmt().negate(), getWriteOffAmt().negate(), getOverUnderAmt().negate());
		aLine.setDocInfo(getC_BPartner_ID(), 0, getC_Invoice_ID());
		aLine.setC_Payment_ID(getC_Payment_ID());
		aLine.saveEx(get_TrxName());
		//do not post immediate alloc
		alloc.set_Attribute(DocumentEngine.DOCUMENT_POST_IMMEDIATE_AFTER_COMPLETE, Boolean.FALSE);
		// added AdempiereException by zuhri
		if (!alloc.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + alloc.getProcessMsg());
		addDocsPostProcess(alloc);
		// end added
		alloc.saveEx(get_TrxName());
		m_justCreatedAllocInv = alloc;
		m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();
			
		//	Get Project from Invoice
		int C_Project_ID = DB.getSQLValue(get_TrxName(), 
			"SELECT MAX(C_Project_ID) FROM C_Invoice WHERE C_Invoice_ID=?", getC_Invoice_ID());
		if (C_Project_ID > 0 && getC_Project_ID() == 0)
			setC_Project_ID(C_Project_ID);
		else if (C_Project_ID > 0 && getC_Project_ID() > 0 && C_Project_ID != getC_Project_ID())
			log.warning("Invoice C_Project_ID=" + C_Project_ID 
				+ " <> Payment C_Project_ID=" + getC_Project_ID());
		return true;
	}	//	allocateInvoice
	
	/**
	 * 	Allocate to Payment Selection
	 * 	@return true if allocated
	 */
	protected boolean allocatePaySelection()
	{
		MAllocationHdr alloc = new MAllocationHdr(getCtx(), false, 
			getDateTrx(), getC_Currency_ID(),
			Msg.translate(getCtx(), "C_Payment_ID")	+ ": " + getDocumentNo() + " [n]", get_TrxName());
		alloc.setAD_Org_ID(getAD_Org_ID());
		alloc.setDateAcct(getDateAcct()); // in case date acct is different from datetrx in payment
		
		String sql = "SELECT psc.C_BPartner_ID, psl.C_Invoice_ID, psl.IsSOTrx, "	//	1..3
			+ " psl.PayAmt, psl.DiscountAmt, psl.DifferenceAmt, psl.OpenAmt, psl.WriteOffAmt "  // 4..8
			+ "FROM C_PaySelectionLine psl"
			+ " INNER JOIN C_PaySelectionCheck psc ON (psl.C_PaySelectionCheck_ID=psc.C_PaySelectionCheck_ID) "
			+ "WHERE psc.C_Payment_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Payment_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				int C_BPartner_ID = rs.getInt(1);
				int C_Invoice_ID = rs.getInt(2);
				if (C_BPartner_ID == 0 && C_Invoice_ID == 0)
					continue;
				boolean isSOTrx = "Y".equals(rs.getString(3));
				BigDecimal PayAmt = rs.getBigDecimal(4);
				BigDecimal DiscountAmt = rs.getBigDecimal(5);
				BigDecimal WriteOffAmt = rs.getBigDecimal(8);
				BigDecimal OpenAmt = rs.getBigDecimal(7);
				BigDecimal OverUnderAmt = OpenAmt.subtract(PayAmt)
					.subtract(DiscountAmt).subtract(WriteOffAmt);
				//
				if (alloc.get_ID() == 0 && !alloc.save(get_TrxName()))
				{
					log.log(Level.SEVERE, "Could not create Allocation Hdr");
					return false;
				}
				MAllocationLine aLine = null;
				if (isSOTrx)
					aLine = new MAllocationLine (alloc, PayAmt, 
						DiscountAmt, WriteOffAmt, OverUnderAmt);
				else
					aLine = new MAllocationLine (alloc, PayAmt.negate(), 
						DiscountAmt.negate(), WriteOffAmt.negate(), OverUnderAmt.negate());
				aLine.setDocInfo(C_BPartner_ID, 0, C_Invoice_ID);
				aLine.setC_Payment_ID(getC_Payment_ID());
				if (!aLine.save(get_TrxName()))
					log.log(Level.SEVERE, "Could not create Allocation Line");
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "allocatePaySelection", e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		//	Should start WF
		boolean ok = true;
		if (alloc.get_ID() == 0)
		{
			if (log.isLoggable(Level.FINE)) log.fine("No Allocation created - C_Payment_ID=" 
				+ getC_Payment_ID());
			ok = false;
		}
		else
		{
			//do not post immediate alloc
			alloc.set_Attribute(DocumentEngine.DOCUMENT_POST_IMMEDIATE_AFTER_COMPLETE, Boolean.FALSE);
			// added Adempiere Exception by zuhri
			if (alloc.processIt(DocAction.ACTION_Complete)) {
				addDocsPostProcess(alloc);
				ok = alloc.save(get_TrxName());
			} else {
				throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + alloc.getProcessMsg());
			}
			// end added by zuhri
			m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();
		}
		return ok;
	}	//	allocatePaySelection
	
	/**
	 * 	Deallocate Payment.
	 * 	Unlink Invoices and Orders and delete Allocations.
	 *  @param accrual 
	 */
	protected void deAllocate(boolean accrual)
	{
		MAllocationHdr[] allocations = MAllocationHdr.getOfPayment(getCtx(), 
			getC_Payment_ID(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("#" + allocations.length);
		for (int i = 0; i < allocations.length; i++)
		{
			allocations[i].set_TrxName(get_TrxName());
			if (DOCSTATUS_Reversed.equals(allocations[i].getDocStatus())
				|| DOCSTATUS_Voided.equals(allocations[i].getDocStatus()))
			{
				continue;
			}
			
			if (accrual) 
			{
				allocations[i].setDocAction(DocAction.ACTION_Reverse_Accrual);
				if (!allocations[i].processIt(DocAction.ACTION_Reverse_Accrual))
					throw new AdempiereException(allocations[i].getProcessMsg());
			}
			else
			{
				allocations[i].setDocAction(DocAction.ACTION_Reverse_Correct);
				if (!allocations[i].processIt(DocAction.ACTION_Reverse_Correct))
					throw new AdempiereException(allocations[i].getProcessMsg());
			}
			allocations[i].saveEx();
		}
		
		// 	Unlink (in case allocation did not get it)
		if (getC_Invoice_ID() != 0)
		{
			//	Invoice					
			String sql = "UPDATE C_Invoice "
				+ "SET C_Payment_ID = NULL, IsPaid='N' "
				+ "WHERE C_Invoice_ID=" + getC_Invoice_ID()
				+ " AND C_Payment_ID=" + getC_Payment_ID();
			int no = DB.executeUpdate(sql, get_TrxName());
			if (no != 0)
				if (log.isLoggable(Level.FINE)) log.fine("Unlink Invoice #" + no);
			//	Order
			sql = "UPDATE C_Order o "
				+ "SET C_Payment_ID = NULL "
				+ "WHERE EXISTS (SELECT * FROM C_Invoice i "
					+ "WHERE o.C_Order_ID=i.C_Order_ID AND i.C_Invoice_ID=" + getC_Invoice_ID() + ")"
				+ " AND C_Payment_ID=" + getC_Payment_ID();
			no = DB.executeUpdate(sql, get_TrxName());
			if (no != 0)
				if (log.isLoggable(Level.FINE)) log.fine("Unlink Order #" + no);
		}
		//
		setC_Invoice_ID(0);
		setIsAllocated(false);
	}	//	deallocate

	/**
	 * 	Void Document.
	 * 	@return true if success 
	 */
	@Override
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());		
		
		if (getC_DepositBatch_ID() > 0 && getC_DepositBatch().isProcessed()) {
			m_processMsg = Msg.translate(getCtx(), "DepositBatchProcessed") + getC_DepositBatch();
			return false;
		}
		
		if (DOCSTATUS_Closed.equals(getDocStatus())
			|| DOCSTATUS_Reversed.equals(getDocStatus())
			|| DOCSTATUS_Voided.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}
		//	If on Bank Statement, don't void it - reverse it
		if (getC_BankStatementLine_ID() > 0)
			return reverseCorrectIt();
		
		//	Not Processed
		if (DOCSTATUS_Drafted.equals(getDocStatus())
			|| DOCSTATUS_Invalid.equals(getDocStatus())
			|| DOCSTATUS_InProgress.equals(getDocStatus())
			|| DOCSTATUS_Approved.equals(getDocStatus())
			|| DOCSTATUS_NotApproved.equals(getDocStatus()) )
		{
			// Before Void
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
			if (m_processMsg != null)
				return false;
			
			if (!voidOnlinePayment())
				return false;
			
			addDescription(Msg.getMsg(getCtx(), "Voided") + " (" + getPayAmt() + ")");
			setPayAmt(Env.ZERO);
			setDiscountAmt(Env.ZERO);
			setWriteOffAmt(Env.ZERO);
			setOverUnderAmt(Env.ZERO);
			setIsAllocated(false);
			//	Unlink & De-Allocate
			deAllocate(false);
		}
		else
		{
			boolean accrual = false;
			try 
			{
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
			catch (PeriodClosedException e) 
			{
				accrual = true;
			}
			
			if (accrual)
				return reverseAccrualIt();
			else
				return reverseCorrectIt();
		}
		
		//
		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;
		
		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}	//	voidIt
	
	/**
	 * 	Close Document.
	 * 	@return true if success 
	 */
	@Override
	public boolean closeIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;
		
		setDocAction(DOCACTION_None);
		
		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;		
		return true;
	}	//	closeIt
	
	/**
	 * 	Reverse Correction
	 * 	@return true if success 
	 */
	@Override
	public boolean reverseCorrectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		
		if (getC_DepositBatch_ID() != 0 && getC_DepositBatch().isProcessed()) {
			m_processMsg = Msg.translate(getCtx(), "DepositBatchProcessed" )+ getC_DepositBatch();
			return false;
		}
		
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;
		
		StringBuilder info = reverse(false);
		if (info == null) {
			return false;
		}
		
		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		m_processMsg = info.toString();
		return true;
	}	//	reverseCorrectionIt

	/**
	 * Reverse this payment
	 * @param accrual true to use current date, false to use this document's accounting date
	 * @return process message or null if there's error
	 */
	protected StringBuilder reverse(boolean accrual) {
		if (!voidOnlinePayment())
			return null;
		
		//	Std Period open?
		Timestamp dateAcct = accrual ? Env.getContextAsDate(getCtx(), Env.DATE) : getDateAcct();
		if (dateAcct == null) {
			dateAcct = new Timestamp(System.currentTimeMillis());
		}
		MPeriod.testPeriodOpen(getCtx(), dateAcct, getC_DocType_ID(), getAD_Org_ID());
		
		if (getC_BankStatementLine_ID() > 0 && isReconciled()) {
			boolean allow = MSysConfig.getBooleanValue(MSysConfig.ALLOW_REVERSAL_OF_RECONCILED_PAYMENT, true, Env.getAD_Client_ID(getCtx()));
			if (!allow) {
				m_processMsg = Msg.getMsg(getCtx(), "NotAllowReversalOfReconciledPayment");
				return null;
			}
		}
		
		if (getC_DepositBatch_ID() != 0) 
		{
			MDepositBatchLine batchLine = new Query(getCtx(),
					MDepositBatchLine.Table_Name, "C_Payment_ID = ? AND C_DepositBatch_ID = ?", get_TrxName())
							.setParameters(getC_Payment_ID(), getC_DepositBatch_ID()).first();
			
			if (batchLine != null) {
				batchLine.deleteEx(false, get_TrxName());
			}
		}
		
		//	Create Reversal
		MPayment reversal = new MPayment (getCtx(), 0, get_TrxName());
		copyValues(this, reversal);
		reversal.setClientOrg(this);
		// reversal.setC_Order_ID(0); // IDEMPIERE-1764
		reversal.setC_Invoice_ID(0);
		reversal.setDateAcct(dateAcct);
		//
		reversal.setDocumentNo(getDocumentNo() + REVERSE_INDICATOR);	//	indicate reversals
		reversal.setDocStatus(DOCSTATUS_Drafted);
		reversal.setDocAction(DOCACTION_Complete);
		//
		reversal.setPayAmt(getPayAmt().negate());
		reversal.setDiscountAmt(getDiscountAmt().negate());
		reversal.setWriteOffAmt(getWriteOffAmt().negate());
		reversal.setOverUnderAmt(getOverUnderAmt().negate());
		//
		reversal.setIsAllocated(true);
		reversal.setIsReconciled(false);
		reversal.setIsOnline(false);
		reversal.setIsApproved(true); 
		reversal.setR_PnRef(null);
		reversal.setR_Result(null);
		reversal.setR_RespMsg(null);
		reversal.setR_AuthCode(null);
		reversal.setR_Info(null);
		reversal.setProcessing(false);
		reversal.setOProcessing("N");
		reversal.setProcessed(false);
		reversal.setPosted(false);
		reversal.setDescription(getDescription());
		reversal.addDescription("{->" + getDocumentNo() + ")");
		//FR [ 1948157  ] 
		reversal.setReversal_ID(getC_Payment_ID());
		reversal.saveEx(get_TrxName());
		//	Post Reversal
		if (!reversal.processIt(DocAction.ACTION_Complete))
		{
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return null;
		}
		reversal.closeIt();
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx(get_TrxName());

		//	Unlink & De-Allocate
		deAllocate(accrual);
		setIsAllocated (true);	//	the allocation below is overwritten
		//	Set Status 
		addDescription("(" + reversal.getDocumentNo() + "<-)");
		setDocStatus(DOCSTATUS_Reversed);
		setDocAction(DOCACTION_None);
		setProcessed(true);
		//FR [ 1948157  ] 
		setReversal_ID(reversal.getC_Payment_ID());
		
		StringBuilder info = new StringBuilder(reversal.getDocumentNo());

		//	Create automatic Allocation
		MAllocationHdr alloc = new MAllocationHdr (getCtx(), false, 
			getDateTrx(), 
			getC_Currency_ID(),
			Msg.translate(getCtx(), "C_Payment_ID")	+ ": " + reversal.getDocumentNo(), get_TrxName());
		alloc.setAD_Org_ID(getAD_Org_ID());
		alloc.setDateAcct(dateAcct); // dateAcct variable already take into account the accrual parameter
		alloc.saveEx(get_TrxName());

		//	Original Allocation
		MAllocationLine aLine = new MAllocationLine (alloc, getPayAmt(true), 
			Env.ZERO, Env.ZERO, Env.ZERO);
		aLine.setDocInfo(getC_BPartner_ID(), 0, 0);
		aLine.setPaymentInfo(getC_Payment_ID(), 0);
		if (!aLine.save(get_TrxName()))
			log.warning("Automatic allocation - line not saved");
		//	Reversal Allocation
		aLine = new MAllocationLine (alloc, reversal.getPayAmt(true), 
			Env.ZERO, Env.ZERO, Env.ZERO);
		aLine.setDocInfo(reversal.getC_BPartner_ID(), 0, 0);
		aLine.setPaymentInfo(reversal.getC_Payment_ID(), 0, reversal.getC_BankTransfer_ID());
		if (!aLine.save(get_TrxName()))
			log.warning("Automatic allocation - reversal line not saved");
		
		//do not post immediate alloc
		alloc.set_Attribute(DocumentEngine.DOCUMENT_POST_IMMEDIATE_AFTER_COMPLETE, Boolean.FALSE);
		// added AdempiereException by zuhri
		if (!alloc.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + alloc.getProcessMsg());
		addDocsPostProcess(alloc);
		// end added
		alloc.saveEx(get_TrxName());
		//			
		info.append(" - @C_AllocationHdr_ID@: ").append(alloc.getDocumentNo());
		
		ICreditManager creditManager = Core.getCreditManager(this);
		//	Update BPartner
		if (creditManager != null)
			creditManager.checkCreditStatus(accrual ? DOCACTION_Reverse_Accrual : DOCACTION_Reverse_Correct);
		
		return info;
	}

	/**
	 * 	Get Bank Statement Line of payment or 0
	 *	@return C_BankStatementLine_ID or 0
	 */
	protected int getC_BankStatementLine_ID()
	{
		String sql = "SELECT C_BankStatementLine_ID FROM C_BankStatementLine WHERE C_Payment_ID=?";
		int id = DB.getSQLValue(get_TrxName(), sql, getC_Payment_ID());
		if (id < 0)
			return 0;
		return id;
	}	//	getC_BankStatementLine_ID
	
	/**
	 * 	Reverse Accrual
	 * 	@return true if success 
	 */
	@Override
	public boolean reverseAccrualIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		
		if (getC_DepositBatch_ID() != 0 && getC_DepositBatch().isProcessed()) {
			m_processMsg = Msg.translate(getCtx(), "DepositBatchProcessed") + getC_DepositBatch();
			return false;
		}
		
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;
		
		StringBuilder info = reverse(true);
		if (info == null) {
			return false;
		}
		
		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;
				
		m_processMsg = info.toString();
		return true;
	}	//	reverseAccrualIt
	
	/** 
	 * 	Re-activate
	 * 	@return true if success 
	 */
	@Override
	public boolean reActivateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;	
		
		MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());

		if (!DocumentEngine.canReactivateThisDocType(getC_DocType_ID())) {
			m_processMsg = Msg.getMsg(getCtx(), "DocTypeCannotBeReactivated", new Object[] {MDocType.get(getC_DocType_ID()).getNameTrl()});
			return false;
		}

		MAllocationHdr[] allocations = MAllocationHdr.getOfPayment(getCtx(), getC_Payment_ID(), get_TrxName());
		if (allocations.length > 0) {
			m_processMsg = Msg.getMsg(getCtx(), "PaymentReactivationFailedAllocationLine");
			return false;
		}

		if (DB.getSQLValueEx(get_TrxName(), "SELECT 1 FROM C_BankStatementLine WHERE C_Payment_ID = ?", getC_Payment_ID()) == 1) {
			m_processMsg = Msg.getMsg(getCtx(), "PaymentReactivationFailedBankStatementLine");
			return false;
		}

		if (getC_BankTransfer_ID() > 0) {
			m_processMsg = Msg.getMsg(getCtx(), "PaymentReactivationFailedBankTransfer");
			return false;
		}

		if (DB.getSQLValueEx(get_TrxName(), "SELECT 1 FROM C_DunningRunLine WHERE C_Payment_ID = ?", getC_Payment_ID()) == 1) {
			m_processMsg = Msg.getMsg(getCtx(), "PaymentReactivationFailedDunningLine");
			return false;
		}

		if (DB.getSQLValueEx(get_TrxName(), "SELECT 1 FROM R_Request WHERE C_Payment_ID = ?", getC_Payment_ID()) == 1) {
			m_processMsg = Msg.getMsg(getCtx(), "PaymentReactivationFailedRequest");
			return false;
		}

		MFactAcct.deleteEx(Table_ID, getC_Payment_ID(), get_TrxName());
		setPosted(false);
		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;				
		
		return true;
	}	//	reActivateIt
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("MPayment[");
		sb.append(get_ID()).append("-").append(getDocumentNo())
			.append(",Receipt=").append(isReceipt())
			.append(",PayAmt=").append(getPayAmt())
			.append(",Discount=").append(getDiscountAmt())
			.append(",WriteOff=").append(getWriteOffAmt())
			.append(",OverUnder=").append(getOverUnderAmt());
		return sb.toString ();
	}	//	toString
	
	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	@Override
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		return dt.getNameTrl() + " " + getDocumentNo();
	}	//	getDocumentInfo

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	@Override
	public File createPDF ()
	{
		try
		{
			File temp = File.createTempFile(get_TableName()+get_ID()+"_", ".pdf");
			return createPDF (temp);
		}
		catch (Exception e)
		{
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}	//	getPDF

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return not implemented, always return null
	 */
	public File createPDF (File file)
	{
		return null;
	}	//	createPDF

	/**
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	@Override
	public String getSummary()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		//	: Total Lines = 123.00 (#1)
		sb.append(": ")
			.append(Msg.translate(getCtx(),"PayAmt")).append("=").append(getPayAmt())
			.append(",").append(Msg.translate(getCtx(),"WriteOffAmt")).append("=").append(getWriteOffAmt());
		//	 - Description
		if (getDescription() != null && getDescription().length() > 0)
			sb.append(" - ").append(getDescription());
		return sb.toString();
	}	//	getSummary
	
	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	@Override
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg
	
	/**
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	@Override
	public int getDoc_User_ID()
	{
		return getCreatedBy();
	}	//	getDoc_User_ID

	/**
	 * 	Get Document Approval Amount
	 *	@return amount payment(AP) or write-off(AR)
	 */
	@Override
	public BigDecimal getApprovalAmt()
	{
		if (isReceipt())
			return getWriteOffAmt();
		return getPayAmt();
	}	//	getApprovalAmt


	@Override
	public void setProcessUI(IProcessUI processMonitor) {
		m_processUI = processMonitor;
	}
	
	/**
	 * Create online payment transaction
	 * @param trxName
	 * @return MPaymentTransaction
	 */
	public MPaymentTransaction createPaymentTransaction(String trxName)
	{
		MPaymentTransaction paymentTransaction = new MPaymentTransaction(getCtx(), 0, trxName);
		paymentTransaction.setA_City(getA_City());
		paymentTransaction.setA_Country(getA_Country());
		paymentTransaction.setA_EMail(getA_EMail());
		paymentTransaction.setA_Ident_DL(getA_Ident_DL());
		paymentTransaction.setA_Ident_SSN(getA_Ident_SSN());
		paymentTransaction.setA_Name(getA_Name());
		paymentTransaction.setA_State(getA_State());
		paymentTransaction.setA_Street(getA_Street());
		paymentTransaction.setA_Zip(getA_Zip());
		paymentTransaction.setAccountNo(getAccountNo());
		paymentTransaction.setIBAN(getIBAN());
		paymentTransaction.setAD_Org_ID(getAD_Org_ID());
		paymentTransaction.setC_BankAccount_ID(getC_BankAccount_ID());
		paymentTransaction.setC_BP_BankAccount_ID(getC_BP_BankAccount_ID());
		paymentTransaction.setC_BPartner_ID(getC_BPartner_ID());
		paymentTransaction.setC_ConversionType_ID(getC_ConversionType_ID());
		paymentTransaction.setC_Currency_ID(getC_Currency_ID());
		paymentTransaction.setC_Invoice_ID(getC_Invoice_ID());
		paymentTransaction.setC_Order_ID(getC_Order_ID());
		paymentTransaction.setC_PaymentProcessor_ID(getC_PaymentProcessor_ID());
		paymentTransaction.setC_POSTenderType_ID(getC_POSTenderType_ID());
		paymentTransaction.setCheckNo(getCheckNo());
		paymentTransaction.setCreditCardExpMM(getCreditCardExpMM());
		paymentTransaction.setCreditCardExpYY(getCreditCardExpYY());
		paymentTransaction.setCreditCardNumber(getCreditCardNumber());
		paymentTransaction.setCreditCardType(getCreditCardType());
		paymentTransaction.setCreditCardVV(getCreditCardVV());
		paymentTransaction.setCustomerAddressID(getCustomerAddressID());
		paymentTransaction.setCustomerPaymentProfileID(getCustomerPaymentProfileID());
		paymentTransaction.setCustomerProfileID(getCustomerProfileID());
		paymentTransaction.setDateTrx(getDateTrx());
		paymentTransaction.setDescription(getDescription());
		paymentTransaction.setIsActive(isActive());
		paymentTransaction.setIsApproved(isApproved());
		paymentTransaction.setIsDelayedCapture(isDelayedCapture());
		paymentTransaction.setIsOnline(isOnline());
		paymentTransaction.setIsReceipt(isReceipt());
		paymentTransaction.setIsSelfService(isSelfService());
		paymentTransaction.setIsVoided(isVoided());
		paymentTransaction.setMicr(getMicr());
		paymentTransaction.setOrig_TrxID(getOrig_TrxID());
		paymentTransaction.setPayAmt(getPayAmt());
		paymentTransaction.setPONum(getPONum());
		paymentTransaction.setProcessed(isProcessed());
		paymentTransaction.setR_AuthCode(getR_AuthCode());
		paymentTransaction.setR_AvsAddr(getR_AvsAddr());
		paymentTransaction.setR_AvsZip(getR_AvsZip());
		paymentTransaction.setR_CVV2Match(isR_CVV2Match());
		paymentTransaction.setR_Info(getR_Info());
		paymentTransaction.setR_PnRef(getR_PnRef());
		paymentTransaction.setR_RespMsg(getR_RespMsg());
		paymentTransaction.setR_Result(getR_Result());
		paymentTransaction.setR_VoidMsg(getR_VoidMsg());
		paymentTransaction.setRoutingNo(getRoutingNo());
		paymentTransaction.setSwiftCode(getSwiftCode());
		paymentTransaction.setTaxAmt(getTaxAmt());
		paymentTransaction.setTenderType(getTenderType());
		paymentTransaction.setTrxType(getTrxType());
		paymentTransaction.setVoiceAuthCode(getVoiceAuthCode());
		
		return paymentTransaction;
	}
	
	/**
	 * @return true if success
	 */
	protected boolean voidOnlinePayment() 
	{
		if (getTenderType().equals(TENDERTYPE_CreditCard) && isOnline())
		{
			setOrig_TrxID(getR_PnRef());
			setTrxType(TRXTYPE_Void);
			if(!processOnline())
			{
				setTrxType(TRXTYPE_CreditPayment);
				if(!processOnline())
				{
					log.log(Level.SEVERE, "Failed to cancel payment online");
					m_processMsg = Msg.getMsg(getCtx(), "PaymentNotCancelled");
					return false;
				}
			}
		}

		if (getC_Invoice_ID() != 0)
		{
			MInvoice inv = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
			inv.setC_Payment_ID(0);
			inv.saveEx();
		}
		if (getC_Order_ID() != 0)
		{
			MOrder ord = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
			ord.setC_Payment_ID(0);
			ord.saveEx();
		}
		
		return true;
	}
	
	@Override
	public PO getPO() {
		return this;
	}
	
	/**
	 * Get ids of completed credit card payment
	 * @param C_Order_ID
	 * @param C_Invoice_ID
	 * @param trxName
	 * @return array of C_Payment_ID
	 */
	public static int[] getCompletedPaymentIDs(int C_Order_ID, int C_Invoice_ID, String trxName)
	{
		StringBuilder whereClause = new StringBuilder();
		whereClause.append("TenderType='").append(MPayment.TENDERTYPE_CreditCard).append("' ");
		whereClause.append("AND TrxType IN ('").append(MPayment.TRXTYPE_DelayedCapture).append("', ");
		whereClause.append("'").append(MPayment.TRXTYPE_Sales).append("', ");
		whereClause.append("'").append(MPayment.TRXTYPE_CreditPayment).append("') ");
		if (C_Order_ID > 0 && C_Invoice_ID > 0)
			whereClause.append(" AND (C_Order_ID=").append(C_Order_ID).append(" OR C_Invoice_ID=").append(C_Invoice_ID).append(")");
		else if (C_Order_ID > 0)
			whereClause.append(" AND C_Order_ID=").append(C_Order_ID);
		else if (C_Invoice_ID > 0)
			whereClause.append(" AND C_Invoice_ID=").append(C_Invoice_ID);
		whereClause.append(" AND IsApproved='Y' AND DocStatus IN ('CO','CL') ");
		whereClause.append("ORDER BY DateTrx DESC");

		return MPaymentTransaction.getAllIDs(Table_Name, whereClause.toString(), trxName);
	}

	// IDEMPIERE-2588
	protected MAllocationHdr m_justCreatedAllocInv = null;
	
	/**
	 * @return just created invoice allocation (inside {@link #allocateInvoice()})
	 */
	public MAllocationHdr getJustCreatedAllocInv() {
		return m_justCreatedAllocInv;
	}
	
	/**
	 * Index constants for Vector<Object> record return by getUnAllocatedPaymentData.
	 * Use MULTI_CURRENCY index if isMultiCurrency=true.
	 * Use SINGLE_CURRENCY index if isMultiCurrency=false;
	 */
	//selected row, boolean
	public static final int UNALLOCATED_PAYMENT_SELECTED=0;
	//transaction date, timestamp
	public static final int UNALLOCATED_PAYMENT_TRX_DATE=1;
	//KeyNamePair, DocumentNo and C_Payment_ID
	public static final int UNALLOCATED_PAYMENT_DOCUMENT_KEY_NAME_PAIR=2;
	//multi currency record, currency iso code
	public static final int UNALLOCATED_PAYMENT_MULTI_CURRENCY_ISO=3;
	//multi currency record, payment amount
	public static final int UNALLOCATED_PAYMENT_MULTI_CURRENCY_PAYMENT_AMT=4;
	//multi currency record, payment amount converted to base currency
	public static final int UNALLOCATED_PAYMENT_MULTI_CURRENCY_CONVERTED_AMT=5;
	//multi currency record, open payment amount
	public static final int UNALLOCATED_PAYMENT_MULTI_CURRENCY_OPEN_AMT=6;
	//multi currency record, payment applied amount
	public static final int UNALLOCATED_PAYMENT_MULTI_CURRENCY_APPLIED_AMT=7;
	//single currency record, payment amount
	public static final int UNALLOCATED_PAYMENT_SINGLE_CURRENCY_AMT=3;
	//single currency record, open payment amount
	public static final int UNALLOCATED_PAYMENT_SINGLE_CURRENCY_OPEN_AMT=4;
	//single currency record, payment applied amount
	public static final int UNALLOCATED_PAYMENT_SINGLE_CURRENCY_APPLIED_AMT=5;
	
	/**
	 * 
	 * @param C_BPartner_ID mandatory bpartner filter
	 * @param C_Currency_ID 0 to use login currency. use for payment filter if isMultiCurrency=false
	 * @param isMultiCurrency false to apply currency filter
	 * @param date payment allocation as at date
	 * @param AD_Org_ID 0 for all org
	 * @param trxName optional transaction name
	 * @return list of unallocated payment records.<br/>
	 * - Payment record: Boolean.False, DateTrx, KeyNamePair(C_Payment_ID,DocumentNo), Currency ISO_Code, PayAmt, Converted Amt,Open Amt, 0 <br/> 
	 * - Without Currency ISO_Code and PayAmt if isMultiCurrency is false.
	 */
	public static Vector<Vector<Object>> getUnAllocatedPaymentData(int C_BPartner_ID, int C_Currency_ID, boolean isMultiCurrency, 
			Timestamp date, int AD_Org_ID, String trxName)
	{
		if (C_Currency_ID==0)
			C_Currency_ID = Env.getContextAsInt(Env.getCtx(), Env.C_CURRENCY_ID);   //  default
		
		/********************************
		 *  Load unallocated Payments
		 *      1-TrxDate, 2-DocumentNo, (3-Currency, 4-PayAmt,)
		 *      5-ConvAmt, 6-ConvOpen, 7-Allocated
		 */
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sql = new StringBuilder("SELECT p.DateTrx,p.DocumentNo,p.C_Payment_ID,"  //  1..3
			+ "c.ISO_Code,p.PayAmt,"                            //  4..5
			+ "currencyConvertPayment(p.C_Payment_ID,?,null,?),"//  6   #1, #2
			+ "currencyConvertPayment(p.C_Payment_ID,?,paymentAvailable(p.C_Payment_ID),?),"  //  7   #3, #4
			+ "p.MultiplierAP "
			+ "FROM C_Payment_v p"		//	Corrected for AP/AR
			+ " INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) "
			+ "WHERE p.IsAllocated='N' AND p.Processed='Y'"
			+ " AND p.C_Charge_ID IS NULL"		//	Prepayments OK
			+ " AND p.C_BPartner_ID=?");                   		//      #5
		if (!isMultiCurrency)
			sql.append(" AND p.C_Currency_ID=?");				//      #6
		if (AD_Org_ID != 0 )
			sql.append(" AND p.AD_Org_ID=" + AD_Org_ID);
		sql.append(" ORDER BY p.DateTrx,p.DocumentNo");
		
		// role security
		sql = new StringBuilder( MRole.getDefault(Env.getCtx(), false).addAccessSQL( sql.toString(), "p", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO ) );
		
		if (s_log.isLoggable(Level.FINE)) s_log.fine("PaySQL=" + sql.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, C_Currency_ID);
			pstmt.setTimestamp(2, (Timestamp)date);
			pstmt.setInt(3, C_Currency_ID);
			pstmt.setTimestamp(4, (Timestamp)date);
			pstmt.setInt(5, C_BPartner_ID);
			if (!isMultiCurrency)
				pstmt.setInt(6, C_Currency_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(Boolean.FALSE);       //  0-Selection
				line.add(rs.getTimestamp(1));       //  1-TrxDate
				KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2));
				line.add(pp);                       //  2-DocumentNo
				if (isMultiCurrency)
				{
					line.add(rs.getString(4));      //  3-Currency
					line.add(rs.getBigDecimal(5));  //  4-PayAmt
				}
				line.add(rs.getBigDecimal(6));      //  3/5-ConvAmt
				BigDecimal available = rs.getBigDecimal(7);
				if (available == null || available.signum() == 0)	//	nothing available
					continue;
				line.add(available);				//  4/6-ConvOpen/Available
				line.add(Env.ZERO);					//  5/7-Applied
				//
				data.add(line);
			}
		}
		catch (SQLException e)
		{
			s_log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
		}
		
		return data;
	}
}   //  MPayment
