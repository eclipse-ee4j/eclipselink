/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlbinder.basictests;
import java.util.Vector;

/**
 *  @version $Header: Employee.java 11-nov-2003.17:02:38 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Employee 
{
    public String id;
	public String firstName;
	public String lastName;
	public Address address;
	public Vector phoneNumbers;
	public Vector responsibilities;
	
	public String getFirstName() 
	{
		return firstName;
	}
	public String getLastName() 
	{
		return lastName;
	}
	public Address getAddress() 
	{
		return address;
	}
	public Vector getPhoneNumbers() 
	{
		return phoneNumbers;
	}
	public Vector getResponsibilities() 
	{
		return responsibilities;
	}
	public void addResponsibility(String responsibility) 
	{
		if(responsibilities == null) 
		{
			responsibilities = new Vector();
		}
		responsibilities.addElement(responsibility);
	}
	
	public void setFirstName(String fn) 
	{
		firstName = fn;
	}
	public void setLastName(String ln) 
	{
		lastName = ln;
	}
	public void setAddress(Address addr) 
	{
		address = addr;
	}
	public void setPhoneNumbers(Vector phones) 
	{
		phoneNumbers = phones;
	}
	public void addPhoneNumber(PhoneNumber pn) 
	{
		if(phoneNumbers == null) 
		{
			phoneNumbers = new Vector();
		}
	}
	public void setResponsibilities(Vector resp) 
	{
		responsibilities = resp;	
	}	
}
