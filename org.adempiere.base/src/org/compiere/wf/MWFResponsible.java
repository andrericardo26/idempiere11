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
package org.compiere.wf;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MRole;
import org.compiere.model.Query;
import org.compiere.model.X_AD_WF_Responsible;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.idempiere.cache.ImmutableIntPOCache;
import org.idempiere.cache.ImmutablePOCache;
import org.idempiere.cache.ImmutablePOSupport;

/**
 *	Extended Workflow Responsible model for AD_WF_Responsible
 *	
 *  @author Jorg Janke
 *  @version $Id: MWFResponsible.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MWFResponsible extends X_AD_WF_Responsible implements ImmutablePOSupport
{
    /**
	 * generated serial id
	 */
	private static final long serialVersionUID = -5073542640376766737L;

	/**
	 * 	Get WF Responsible from Cache (immutable)
	 *	@param AD_WF_Responsible_ID id
	 *	@return MWFResponsible
	 */
	public static MWFResponsible get (int AD_WF_Responsible_ID)
	{
		return get(Env.getCtx(), AD_WF_Responsible_ID);
	}
	
    /**
	 * 	Get WF Responsible from Cache (immutable)
	 *	@param ctx context
	 *	@param AD_WF_Responsible_ID id
	 *	@return MWFResponsible
	 */
	public static MWFResponsible get (Properties ctx, int AD_WF_Responsible_ID)
	{
		Integer key = Integer.valueOf(AD_WF_Responsible_ID);
		MWFResponsible retValue = s_cache.get (ctx, key, e -> new MWFResponsible(ctx, e));
		if (retValue != null)
			return retValue;
		retValue = new MWFResponsible (ctx, AD_WF_Responsible_ID, (String)null);
		if (retValue.get_ID () == AD_WF_Responsible_ID)
		{
			s_cache.put (key, retValue, e -> new MWFResponsible(Env.getCtx(), e));
			return retValue;
		}
		return null;
	} //	get
	
	/**
	 * Cache for Overridden Client Workflow Responsible
	 */
	private static ImmutablePOCache<String, MWFResponsible> s_cacheCliWFResp = new ImmutablePOCache<String, MWFResponsible>(
			"ClientWFResponsible", 10);

	/**
	 * Get Overridden Client Workflow Responsible from the Cache
	 * 
	 * @param ctx
	 * @param AD_WF_Responsible_ID - System Workflow Responsible
	 * @return
	 */
	public static MWFResponsible getClientWFResp(Properties ctx, int AD_WF_Responsible_ID) {
		if(AD_WF_Responsible_ID==0)
			return null;
		
		int clientID = Env.getAD_Client_ID(ctx);
		String key = clientID + "_" + AD_WF_Responsible_ID;
		 
		MWFResponsible resp = null;
		if(s_cacheCliWFResp.containsKey(key))
		{
			resp = s_cacheCliWFResp.get(ctx, key, e->new MWFResponsible(ctx, e));
			return resp;
		}
		
		resp = new Query(ctx, Table_Name, "AD_Client_ID=? AND Override_ID=?", null).setOnlyActiveRecords(true)
					.setParameters(Env.getAD_Client_ID(ctx), AD_WF_Responsible_ID).first();
		
		//if no override then put null value so it reduce query to check is over ride
		s_cacheCliWFResp.put(key, resp);
		return resp;
	}

	/**
	 * Get updateable copy of MWFResponsible from cache
	 * @param ctx
	 * @param AD_WF_Responsible_ID
	 * @param trxName
	 * @return MWFResponsible 
	 */
	public static MWFResponsible getCopy(Properties ctx, int AD_WF_Responsible_ID, String trxName)
	{
		MWFResponsible r = get(AD_WF_Responsible_ID);
		if (r != null)
			r = new MWFResponsible(ctx, r, trxName);
		return r;
	}
	
	/**	Cache						*/
	private static ImmutableIntPOCache<Integer,MWFResponsible>	s_cache	= new ImmutableIntPOCache<Integer,MWFResponsible>(Table_Name, 10);
	
    /**
     * UUID based Constructor
     * @param ctx  Context
     * @param AD_WF_Responsible_UU  UUID key
     * @param trxName Transaction
     */
    public MWFResponsible(Properties ctx, String AD_WF_Responsible_UU, String trxName) {
        super(ctx, AD_WF_Responsible_UU, trxName);
    }

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param AD_WF_Responsible_ID id
	 * 	@param trxName transaction
	 */
	public MWFResponsible (Properties ctx, int AD_WF_Responsible_ID, String trxName)
	{
		super (ctx, AD_WF_Responsible_ID, trxName);
	}	//	MWFResponsible

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 * 	@param trxName transaction
	 */
	public MWFResponsible (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MWFResponsible
	
	/**
	 * Copy constructor
	 * @param copy
	 */
	public MWFResponsible(MWFResponsible copy) 
	{
		this(Env.getCtx(), copy);
	}

	/**
	 * Copy constructor
	 * @param ctx
	 * @param copy
	 */
	public MWFResponsible(Properties ctx, MWFResponsible copy) 
	{
		this(ctx, copy, (String) null);
	}

	/**
	 * Copy constructor
	 * @param ctx
	 * @param copy
	 * @param trxName
	 */
	public MWFResponsible(Properties ctx, MWFResponsible copy, String trxName) 
	{
		this(ctx, 0, trxName);
		copyPO(copy);
	}
	
	/**
	 * 	Invoker - return true if no user and no role 
	 *	@return true if invoker
	 */
	public boolean isInvoker()
	{
		return getAD_User_ID() == 0 && getAD_Role_ID() == 0 && !isManual();
	}	//	isInvoker
	
	/**
	 * 	Is Role Responsible Type
	 *	@return true if role
	 */
	public boolean isRole()
	{
		return RESPONSIBLETYPE_Role.equals(getResponsibleType()) 
			&& getAD_Role_ID() != 0;
	}	//	isRole

	/**
	 * 	Get reference role instance
	 *	@return true if role
	 */
	public MRole getRole()
	{
		if (!isRole())
			return null;
		return MRole.get(getCtx(), getAD_Role_ID());
	}	//	getRole

	/**
	 * 	Is Human Responsible Type
	 *	@return true if human
	 */
	public boolean isHuman()
	{
		return RESPONSIBLETYPE_Human.equals(getResponsibleType()) 
			&& getAD_User_ID() != 0;
	}	//	isHuman
	
	/**
	 * 	Is Org Responsible Type
	 *	@return true if Org
	 */
	public boolean isOrganization()
	{
		return RESPONSIBLETYPE_Organization.equals(getResponsibleType()) 
			&& getAD_Org_ID() != 0;
	}	//	isOrg
	
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		// AD_Role_ID mandatory for role responsible type
		if (RESPONSIBLETYPE_Role.equals(getResponsibleType()) 
			&& getAD_Role_ID() == 0
			&& getAD_Client_ID() > 0)
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@RequiredEnter@ @AD_Role_ID@"));
			return false;
		}
		//	User not used
		if (!RESPONSIBLETYPE_Human.equals(getResponsibleType()) && getAD_User_ID() > 0)
			setAD_User_ID(0);
		
		//	Role not used
		if (!RESPONSIBLETYPE_Role.equals(getResponsibleType()) && getAD_Role_ID() > 0)
			setAD_Role_ID(0);
		//  User and role not used for manual responsible type
		if (RESPONSIBLETYPE_Manual.equals(getResponsibleType())) {
		    setAD_User_ID(0);
		    setAD_Role_ID(0);
		}
		return true;
	}	//	beforeSave
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder("MWFResponsible[");
		sb.append (get_ID())
			.append("-").append(getName())
			.append(",Type=").append(getResponsibleType());
		if (getAD_User_ID() != 0)
			sb.append(",AD_User_ID=").append(getAD_User_ID());
		if (getAD_Role_ID() != 0)
			sb.append(",AD_Role_ID=").append(getAD_Role_ID());
		sb.append ("]");
		return sb.toString ();
	}	//	toString
	
	/**
	 * Is manual responsible type
	 * @return true if this is manual responsible type
	 */
	public boolean isManual() {
	    return RESPONSIBLETYPE_Manual.equals(getResponsibleType());
	}
	
	@Override
	public MWFResponsible markImmutable() {
		if (is_Immutable())
			return this;

		makeImmutable();
		return this;
	}

}	//	MWFResponsible
