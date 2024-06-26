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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import org.compiere.util.CLogger;
import org.compiere.util.Util;

/**
 *	Column Info Value Object
 *	
 *  @author Jorg Janke
 *  @version $Id: POInfoColumn.java,v 1.3 2006/07/30 00:58:04 jjanke Exp $
 */
public class POInfoColumn implements Serializable
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -6550300505836470875L;

	/**
	 *  Constructor
	 *	@param ad_Column_ID Column ID
	 *	@param columnName Column name
	 *	@param columnSQL virtual column
	 *	@param displayType Display Type
	 *	@param isMandatory Mandatory
	 *	@param isUpdateable Updateable
	 *	@param defaultLogic Default Logic
	 *	@param columnLabel Column Label
	 *	@param columnDescription Column Description
	 *	@param isKey true if key
	 *	@param isParent true if parent
	 *	@param ad_Reference_Value_ID reference value
	 *	@param validationCode sql validation code
	 *	@param fieldLength Field Length
	 * 	@param valueMin minimal value
	 * 	@param valueMax maximal value
	 * 	@param isTranslated translated
	 * 	@param isEncrypted encrypted 
	 * 	@param isAllowLogging allow logging 
	 * 	@param isAllowCopy allow copy 
	 */
	public POInfoColumn (int ad_Column_ID, String columnName, String columnSQL, int displayType,
		boolean isMandatory, boolean isUpdateable, String defaultLogic,
		String columnLabel, String columnDescription,
		boolean isKey, boolean isParent,
		int ad_Reference_Value_ID, String validationCode,
		int fieldLength, String valueMin, String valueMax,
		boolean isTranslated, boolean isEncrypted, boolean isAllowLogging,  boolean isAllowCopy)
	{
		AD_Column_ID = ad_Column_ID;
		ColumnName = columnName;
		ColumnSQL = columnSQL;
		DisplayType = displayType;
		if (columnName.equals("AD_Language") || columnName.equals("EntityType"))
		{
			DisplayType = org.compiere.util.DisplayType.String;
			ColumnClass = String.class;
		}
		else if (columnName.equals("Posted") 
			|| columnName.equals("Processed")
			|| columnName.equals("Processing"))
		{
			ColumnClass = Boolean.class;
		}
		else if (columnName.equals("Record_ID"))
		{
			DisplayType = org.compiere.util.DisplayType.ID;
			ColumnClass = Integer.class;
		}
		else if (displayType == org.compiere.util.DisplayType.Button && columnName.endsWith("_ID"))
		{
			ColumnClass = Integer.class;
		}
		else
			ColumnClass = org.compiere.util.DisplayType.getClass(displayType, true);
		IsMandatory = isMandatory;
		IsUpdateable = isUpdateable;
		DefaultLogic = defaultLogic;
		ColumnLabel = columnLabel;
		ColumnDescription = columnDescription;
		IsKey = isKey;
		IsParent = isParent;
		//
		AD_Reference_Value_ID = ad_Reference_Value_ID;
		ValidationCode = validationCode;
		//
		FieldLength = fieldLength;
		ValueMin = valueMin;
		if (!Util.isEmpty(ValueMin)) {
			try {
				ValueMin_BD = new BigDecimal(ValueMin);
			} catch (Exception ex) {
				ValueMin_BD = null;
			}
			try {
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			    ValueMin_TS = new java.sql.Timestamp(dateFormat.parse(ValueMin).getTime());
			} catch (Exception ex) {
				ValueMin_TS = null;
			}
			if (ValueMin_BD == null && ValueMin_TS == null) {
				CLogger.get().log(Level.SEVERE, "ValueMin cannot be parsed to a number or date = " + ValueMin);
			}
		}
		ValueMax = valueMax;
		if (!Util.isEmpty(ValueMax)) {
			try {
				ValueMax_BD = new BigDecimal(ValueMax);
			} catch (Exception ex) {
				ValueMax_BD = null;
			}
			try {
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			    ValueMax_TS = new java.sql.Timestamp(dateFormat.parse(ValueMax).getTime());
			} catch (Exception ex) {
				ValueMax_TS = null;
			}
			if (ValueMax_BD == null && ValueMax_TS == null) {
				CLogger.get().log(Level.SEVERE, "ValueMax cannot be parsed to a number or date = " + ValueMax);
			}
		}
		IsTranslated = isTranslated;
		IsEncrypted = isEncrypted;
		IsAllowLogging = isAllowLogging;
		IsAllowCopy = isAllowCopy;
	}   //  Column

	/** Column ID		*/
	public int          AD_Column_ID;
	/** Column Name		*/
	public String       ColumnName;
	/** Virtual Column 	*/
	public String       ColumnSQL;
	/** Display Type	*/
	public int          DisplayType;
	/**	Data Type		*/
	public Class<?>        ColumnClass;
	/**	Mandatory		*/
	public boolean      IsMandatory;
	/**	Default Value	*/
	public String       DefaultLogic;
	/**	Updateable		*/
	public boolean      IsUpdateable;
	/**	Label			*/
	public String       ColumnLabel;
	/**	Description		*/
	public String       ColumnDescription;
	/**	PK				*/
	public boolean		IsKey;
	/**	FK to Parent	*/
	public boolean		IsParent;
	/**	Translated		*/
	public boolean		IsTranslated;
	/**	Encrypted		*/
	public boolean		IsEncrypted;
	/**	Allow Logging		*/
	public boolean		IsAllowLogging;
	/**	Allow Copy		*/
	public boolean		IsAllowCopy;
	
	/** Reference Value	*/
	public int			AD_Reference_Value_ID;
	/** Validation		*/
	public String		ValidationCode;
	
	/** Field Length	*/
	public int			FieldLength;
	/**	Min Value		*/
	public String		ValueMin;
	/**	Max Value		*/
	public String		ValueMax;
	/**	Min Value		*/
	public BigDecimal	ValueMin_BD = null;
	/**	Max Value		*/
	public BigDecimal	ValueMax_BD = null;
	/**	Min Value		*/
	public Timestamp	ValueMin_TS = null;
	/**	Max Value		*/
	public Timestamp	ValueMax_TS = null;

	/**
	 * 	String representation
	 *  @return info
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("POInfo.Column[");
		sb.append(ColumnName).append(",ID=").append(AD_Column_ID)
			.append(",DisplayType=").append(DisplayType)
			.append(",ColumnClass=").append(ColumnClass);
		sb.append("]");
		return sb.toString();
	}	//	toString

}	//	POInfoColumn
