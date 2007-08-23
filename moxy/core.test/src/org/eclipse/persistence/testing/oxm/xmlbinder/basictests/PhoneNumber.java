/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlbinder.basictests;

/**
 *  @version $Header: PhoneNumber.java 11-nov-2003.17:02:38 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class PhoneNumber 
{
	public int areaCode;
	public int exchange;
	public int number;
	
	public int getAreaCode() 
	{
		return areaCode;
	}
	public int getExchange() 
	{
		return exchange;
	}
	public int getNumber() 
	{
		return number;
	}
	
	public void setAreaCode(int ac) 
	{
		areaCode = ac;
	}
	public void setExchange(int exch) 
	{
		exchange = exch;
	}
	public void setNumber(int num) 
	{
		number = num;
	}
}