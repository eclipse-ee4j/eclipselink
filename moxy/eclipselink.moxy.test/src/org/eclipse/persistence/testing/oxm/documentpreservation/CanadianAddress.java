/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.documentpreservation;

/**
 *  @version $Header: CanadianAddress.java 11-nov-2003.17:02:38 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class CanadianAddress extends Address 
{
	public String province;
	public String postalCode;
	
	public String getProvince() 
	{
		return province;
	}
	public String getPostalCode() 
	{
		return postalCode;
	}
	
	public void setProvince(String prov) 
	{
		province = prov;
	}
	public void setPostalCode(String postal) 
	{
		postalCode = postal;
	}
}
