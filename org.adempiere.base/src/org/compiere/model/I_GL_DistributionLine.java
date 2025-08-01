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

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.util.KeyNamePair;

/** Generated Interface for GL_DistributionLine
 *  @author iDempiere (generated) 
 *  @version Release 13
 */
public interface I_GL_DistributionLine 
{

    /** TableName=GL_DistributionLine */
    public static final String Table_Name = "GL_DistributionLine";

    /** AD_Table_ID=707 */
    public static final int Table_ID = 707;

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 2 - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(2);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name A_Asset_ID */
    public static final String COLUMNNAME_A_Asset_ID = "A_Asset_ID";

	/** Set Asset.
	  * Asset used internally or by customers
	  */
	public void setA_Asset_ID (int A_Asset_ID);

	/** Get Asset.
	  * Asset used internally or by customers
	  */
	public int getA_Asset_ID();

	public org.compiere.model.I_A_Asset getA_Asset() throws RuntimeException;

    /** Column name Account_ID */
    public static final String COLUMNNAME_Account_ID = "Account_ID";

	/** Set Account.
	  * Account used
	  */
	public void setAccount_ID (int Account_ID);

	/** Get Account.
	  * Account used
	  */
	public int getAccount_ID();

    /** Column name C_Activity_ID */
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";

	/** Set Activity.
	  * Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID);

	/** Get Activity.
	  * Business Activity
	  */
	public int getC_Activity_ID();

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException;

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner.
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner.
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_Campaign_ID */
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";

	/** Set Campaign.
	  * Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID);

	/** Get Campaign.
	  * Marketing Campaign
	  */
	public int getC_Campaign_ID();

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException;

    /** Column name C_Charge_ID */
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";

	/** Set Charge.
	  * Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID);

	/** Get Charge.
	  * Additional document charges
	  */
	public int getC_Charge_ID();

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException;

    /** Column name C_CostCenter_ID */
    public static final String COLUMNNAME_C_CostCenter_ID = "C_CostCenter_ID";

	/** Set Cost Center	  */
	public void setC_CostCenter_ID (int C_CostCenter_ID);

	/** Get Cost Center	  */
	public int getC_CostCenter_ID();

	public org.compiere.model.I_C_CostCenter getC_CostCenter() throws RuntimeException;

    /** Column name C_Department_ID */
    public static final String COLUMNNAME_C_Department_ID = "C_Department_ID";

	/** Set Department	  */
	public void setC_Department_ID (int C_Department_ID);

	/** Get Department	  */
	public int getC_Department_ID();

	public org.compiere.model.I_C_Department getC_Department() throws RuntimeException;

    /** Column name C_Employee_ID */
    public static final String COLUMNNAME_C_Employee_ID = "C_Employee_ID";

	/** Set Employee.
	  * Identifies a Business Partner
	  */
	public void setC_Employee_ID (int C_Employee_ID);

	/** Get Employee.
	  * Identifies a Business Partner
	  */
	public int getC_Employee_ID();

	public org.compiere.model.I_C_BPartner getC_Employee() throws RuntimeException;

    /** Column name C_LocFrom_ID */
    public static final String COLUMNNAME_C_LocFrom_ID = "C_LocFrom_ID";

	/** Set Location From.
	  * Location that inventory was moved from
	  */
	public void setC_LocFrom_ID (int C_LocFrom_ID);

	/** Get Location From.
	  * Location that inventory was moved from
	  */
	public int getC_LocFrom_ID();

	public org.compiere.model.I_C_Location getC_LocFrom() throws RuntimeException;

    /** Column name C_LocTo_ID */
    public static final String COLUMNNAME_C_LocTo_ID = "C_LocTo_ID";

	/** Set Location To.
	  * Location that inventory was moved to
	  */
	public void setC_LocTo_ID (int C_LocTo_ID);

	/** Get Location To.
	  * Location that inventory was moved to
	  */
	public int getC_LocTo_ID();

	public org.compiere.model.I_C_Location getC_LocTo() throws RuntimeException;

    /** Column name C_Project_ID */
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";

	/** Set Project.
	  * Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID);

	/** Get Project.
	  * Financial Project
	  */
	public int getC_Project_ID();

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException;

    /** Column name C_SalesRegion_ID */
    public static final String COLUMNNAME_C_SalesRegion_ID = "C_SalesRegion_ID";

	/** Set Sales Region.
	  * Sales coverage region
	  */
	public void setC_SalesRegion_ID (int C_SalesRegion_ID);

	/** Get Sales Region.
	  * Sales coverage region
	  */
	public int getC_SalesRegion_ID();

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name GL_DistributionLine_ID */
    public static final String COLUMNNAME_GL_DistributionLine_ID = "GL_DistributionLine_ID";

	/** Set GL Distribution Line.
	  * General Ledger Distribution Line
	  */
	public void setGL_DistributionLine_ID (int GL_DistributionLine_ID);

	/** Get GL Distribution Line.
	  * General Ledger Distribution Line
	  */
	public int getGL_DistributionLine_ID();

    /** Column name GL_DistributionLine_UU */
    public static final String COLUMNNAME_GL_DistributionLine_UU = "GL_DistributionLine_UU";

	/** Set GL_DistributionLine_UU	  */
	public void setGL_DistributionLine_UU (String GL_DistributionLine_UU);

	/** Get GL_DistributionLine_UU	  */
	public String getGL_DistributionLine_UU();

    /** Column name GL_Distribution_ID */
    public static final String COLUMNNAME_GL_Distribution_ID = "GL_Distribution_ID";

	/** Set GL Distribution.
	  * General Ledger Distribution
	  */
	public void setGL_Distribution_ID (int GL_Distribution_ID);

	/** Get GL Distribution.
	  * General Ledger Distribution
	  */
	public int getGL_Distribution_ID();

	public org.compiere.model.I_GL_Distribution getGL_Distribution() throws RuntimeException;

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name M_AttributeSetInstance_ID */
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";

	/** Set Attribute Set Instance.
	  * Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID);

	/** Get Attribute Set Instance.
	  * Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID();

	public org.compiere.model.I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name M_Warehouse_ID */
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";

	/** Set Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID);

	/** Get Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID();

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException;

    /** Column name Org_ID */
    public static final String COLUMNNAME_Org_ID = "Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setOrg_ID (int Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getOrg_ID();

    /** Column name OverwriteAcct */
    public static final String COLUMNNAME_OverwriteAcct = "OverwriteAcct";

	/** Set Overwrite Account.
	  * Overwrite the account segment Account with the value specified
	  */
	public void setOverwriteAcct (boolean OverwriteAcct);

	/** Get Overwrite Account.
	  * Overwrite the account segment Account with the value specified
	  */
	public boolean isOverwriteAcct();

    /** Column name OverwriteActivity */
    public static final String COLUMNNAME_OverwriteActivity = "OverwriteActivity";

	/** Set Overwrite Activity.
	  * Overwrite the account segment Activity with the value specified
	  */
	public void setOverwriteActivity (boolean OverwriteActivity);

	/** Get Overwrite Activity.
	  * Overwrite the account segment Activity with the value specified
	  */
	public boolean isOverwriteActivity();

    /** Column name OverwriteAsset */
    public static final String COLUMNNAME_OverwriteAsset = "OverwriteAsset";

	/** Set Overwrite Asset.
	  * Overwrite the account segment Asset with the value specified
	  */
	public void setOverwriteAsset (boolean OverwriteAsset);

	/** Get Overwrite Asset.
	  * Overwrite the account segment Asset with the value specified
	  */
	public boolean isOverwriteAsset();

    /** Column name OverwriteAttributeSetInstance */
    public static final String COLUMNNAME_OverwriteAttributeSetInstance = "OverwriteAttributeSetInstance";

	/** Set Overwrite Attribute Set Instance.
	  * Overwrite the account segment Attribute Set Instance with the value specified
	  */
	public void setOverwriteAttributeSetInstance (boolean OverwriteAttributeSetInstance);

	/** Get Overwrite Attribute Set Instance.
	  * Overwrite the account segment Attribute Set Instance with the value specified
	  */
	public boolean isOverwriteAttributeSetInstance();

    /** Column name OverwriteBPartner */
    public static final String COLUMNNAME_OverwriteBPartner = "OverwriteBPartner";

	/** Set Overwrite Bus.Partner.
	  * Overwrite the account segment Business Partner with the value specified
	  */
	public void setOverwriteBPartner (boolean OverwriteBPartner);

	/** Get Overwrite Bus.Partner.
	  * Overwrite the account segment Business Partner with the value specified
	  */
	public boolean isOverwriteBPartner();

    /** Column name OverwriteCampaign */
    public static final String COLUMNNAME_OverwriteCampaign = "OverwriteCampaign";

	/** Set Overwrite Campaign.
	  * Overwrite the account segment Campaign with the value specified
	  */
	public void setOverwriteCampaign (boolean OverwriteCampaign);

	/** Get Overwrite Campaign.
	  * Overwrite the account segment Campaign with the value specified
	  */
	public boolean isOverwriteCampaign();

    /** Column name OverwriteCharge */
    public static final String COLUMNNAME_OverwriteCharge = "OverwriteCharge";

	/** Set Overwrite Charge.
	  * Overwrite the account segment Charge with the value specified
	  */
	public void setOverwriteCharge (boolean OverwriteCharge);

	/** Get Overwrite Charge.
	  * Overwrite the account segment Charge with the value specified
	  */
	public boolean isOverwriteCharge();

    /** Column name OverwriteCostCenter */
    public static final String COLUMNNAME_OverwriteCostCenter = "OverwriteCostCenter";

	/** Set Overwrite Cost Center.
	  * Overwrite the account segment Cost Center with the value specified
	  */
	public void setOverwriteCostCenter (boolean OverwriteCostCenter);

	/** Get Overwrite Cost Center.
	  * Overwrite the account segment Cost Center with the value specified
	  */
	public boolean isOverwriteCostCenter();

    /** Column name OverwriteDepartment */
    public static final String COLUMNNAME_OverwriteDepartment = "OverwriteDepartment";

	/** Set Overwrite Department.
	  * Overwrite the account segment Department with the value specified
	  */
	public void setOverwriteDepartment (boolean OverwriteDepartment);

	/** Get Overwrite Department.
	  * Overwrite the account segment Department with the value specified
	  */
	public boolean isOverwriteDepartment();

    /** Column name OverwriteEmployee */
    public static final String COLUMNNAME_OverwriteEmployee = "OverwriteEmployee";

	/** Set Overwrite Employee.
	  * Overwrite the account segment Employee with the value specified
	  */
	public void setOverwriteEmployee (boolean OverwriteEmployee);

	/** Get Overwrite Employee.
	  * Overwrite the account segment Employee with the value specified
	  */
	public boolean isOverwriteEmployee();

    /** Column name OverwriteLocFrom */
    public static final String COLUMNNAME_OverwriteLocFrom = "OverwriteLocFrom";

	/** Set Overwrite Location From.
	  * Overwrite the account segment Location From with the value specified
	  */
	public void setOverwriteLocFrom (boolean OverwriteLocFrom);

	/** Get Overwrite Location From.
	  * Overwrite the account segment Location From with the value specified
	  */
	public boolean isOverwriteLocFrom();

    /** Column name OverwriteLocTo */
    public static final String COLUMNNAME_OverwriteLocTo = "OverwriteLocTo";

	/** Set Overwrite Location To.
	  * Overwrite the account segment Location From with the value specified
	  */
	public void setOverwriteLocTo (boolean OverwriteLocTo);

	/** Get Overwrite Location To.
	  * Overwrite the account segment Location From with the value specified
	  */
	public boolean isOverwriteLocTo();

    /** Column name OverwriteOrg */
    public static final String COLUMNNAME_OverwriteOrg = "OverwriteOrg";

	/** Set Overwrite Organization.
	  * Overwrite the account segment Organization with the value specified
	  */
	public void setOverwriteOrg (boolean OverwriteOrg);

	/** Get Overwrite Organization.
	  * Overwrite the account segment Organization with the value specified
	  */
	public boolean isOverwriteOrg();

    /** Column name OverwriteOrgTrx */
    public static final String COLUMNNAME_OverwriteOrgTrx = "OverwriteOrgTrx";

	/** Set Overwrite Trx Organization.
	  * Overwrite the account segment Transaction Organization with the value specified
	  */
	public void setOverwriteOrgTrx (boolean OverwriteOrgTrx);

	/** Get Overwrite Trx Organization.
	  * Overwrite the account segment Transaction Organization with the value specified
	  */
	public boolean isOverwriteOrgTrx();

    /** Column name OverwriteProduct */
    public static final String COLUMNNAME_OverwriteProduct = "OverwriteProduct";

	/** Set Overwrite Product.
	  * Overwrite the account segment Product with the value specified
	  */
	public void setOverwriteProduct (boolean OverwriteProduct);

	/** Get Overwrite Product.
	  * Overwrite the account segment Product with the value specified
	  */
	public boolean isOverwriteProduct();

    /** Column name OverwriteProject */
    public static final String COLUMNNAME_OverwriteProject = "OverwriteProject";

	/** Set Overwrite Project.
	  * Overwrite the account segment Project with the value specified
	  */
	public void setOverwriteProject (boolean OverwriteProject);

	/** Get Overwrite Project.
	  * Overwrite the account segment Project with the value specified
	  */
	public boolean isOverwriteProject();

    /** Column name OverwriteSalesRegion */
    public static final String COLUMNNAME_OverwriteSalesRegion = "OverwriteSalesRegion";

	/** Set Overwrite Sales Region.
	  * Overwrite the account segment Sales Region with the value specified
	  */
	public void setOverwriteSalesRegion (boolean OverwriteSalesRegion);

	/** Get Overwrite Sales Region.
	  * Overwrite the account segment Sales Region with the value specified
	  */
	public boolean isOverwriteSalesRegion();

    /** Column name OverwriteUser1 */
    public static final String COLUMNNAME_OverwriteUser1 = "OverwriteUser1";

	/** Set Overwrite User1.
	  * Overwrite the account segment User 1 with the value specified
	  */
	public void setOverwriteUser1 (boolean OverwriteUser1);

	/** Get Overwrite User1.
	  * Overwrite the account segment User 1 with the value specified
	  */
	public boolean isOverwriteUser1();

    /** Column name OverwriteUser2 */
    public static final String COLUMNNAME_OverwriteUser2 = "OverwriteUser2";

	/** Set Overwrite User2.
	  * Overwrite the account segment User 2 with the value specified
	  */
	public void setOverwriteUser2 (boolean OverwriteUser2);

	/** Get Overwrite User2.
	  * Overwrite the account segment User 2 with the value specified
	  */
	public boolean isOverwriteUser2();

    /** Column name OverwriteWarehouse */
    public static final String COLUMNNAME_OverwriteWarehouse = "OverwriteWarehouse";

	/** Set Overwrite Warehouse.
	  * Overwrite the account segment Warehouse with the value specified
	  */
	public void setOverwriteWarehouse (boolean OverwriteWarehouse);

	/** Get Overwrite Warehouse.
	  * Overwrite the account segment Warehouse with the value specified
	  */
	public boolean isOverwriteWarehouse();

    /** Column name Percent */
    public static final String COLUMNNAME_Percent = "Percent";

	/** Set Percent.
	  * Percentage
	  */
	public void setPercent (BigDecimal Percent);

	/** Get Percent.
	  * Percentage
	  */
	public BigDecimal getPercent();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User Element List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User Element List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException;

    /** Column name User2_ID */
    public static final String COLUMNNAME_User2_ID = "User2_ID";

	/** Set User Element List 2.
	  * User defined list element #2
	  */
	public void setUser2_ID (int User2_ID);

	/** Get User Element List 2.
	  * User defined list element #2
	  */
	public int getUser2_ID();

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException;
}
