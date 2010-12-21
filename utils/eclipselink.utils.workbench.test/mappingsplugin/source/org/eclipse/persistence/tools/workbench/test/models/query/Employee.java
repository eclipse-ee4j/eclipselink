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
package org.eclipse.persistence.tools.workbench.test.models.query;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.indirection.ValueHolderInterface;

public class Employee implements EmployeeInterface
{
	
	public BigDecimal id;
	/** Direct-to-field mapping, String -> VARCHAR. */
	public String firstName;
	/** Direct-to-field mapping, String -> VARCHAR. */
	public String lastName;
	
	public ValueHolderInterface manager;
	public ValueHolderInterface phoneNumbers;
	public EmploymentPeriod period;
	
	public Employee()
	{
		this.firstName = "";
		this.lastName = "";
	}


	/**
	 * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
	 */
	
	public void addPhoneNumber(PhoneNumber phoneNumber)
	{
		getPhoneNumbers().addElement(phoneNumber);
		phoneNumber.setOwner(this);
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * Notice that the usage of value holders does not effect the public interface or usage of the class.
	 * The get/set methods must however be changed to wrap/unwrap the value holder.
	 */
	
	public EmployeeInterface getManager()
	{
		return (EmployeeInterface) manager.getValue();
	}
	
	public EmploymentPeriod getPeriod()
	{
		return period;
	}
	
	/**
	 * Notice that the usage of value holders does not effect the public interface or usage of the class.
	 * The get/set methods must however be changed to wrap/unwrap the value holder.
	 */
	
	public Vector getPhoneNumbers()
	{
		return (Vector) phoneNumbers.getValue();
	}
	
	/**
	 * Remove the phone number.
	 * The phone number's owner must not be set to null as it is part of it primary key,
	 * and you can never change the primary key of an existing object.
	 * Only in independent relationships should you null out the back reference.
	 */
	
	public void removePhoneNumber(PhoneNumber phoneNumber)
	{
		getPhoneNumbers().removeElement(phoneNumber);
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	/**
	 * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
	 * Notice that the usage of value holders does not effect the public interface or usage of the class.
	 * The get/set methods must however be changed to wrap/unwrap the value holder.
	 */
	
	public void setManager(EmployeeInterface manager)
	{
		this.manager.setValue(manager);
	}
	
	public void setPeriod(EmploymentPeriod period)
	{
		this.period = period;
	}
	
	/**
	 * Notice that the usage of value holders does not effect the public interface or usage of the class.
	 * The get/set methods must however be changed to wrap/unwrap the value holder.
	 */
	
	public void setPhoneNumbers(Vector phoneNumbers)
	{
		this.phoneNumbers.setValue(phoneNumbers);
	}

}
