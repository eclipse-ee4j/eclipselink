/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - April 30/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.mappings.pathbased;

import java.util.List;
import java.util.Vector;

public class Customer  {

	private String firstName;
	private String lastName;
	private String gender;
	private Address billingAddress;
	private Address shippingAddress;
	private Vector phoneNumbers;
	public List<String> stuff;

	public Customer() {
		super();
		phoneNumbers = new Vector();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Vector getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(Vector phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	private String getPhoneNumbersAsString() {
		String phones = "";
		PhoneNumber phone;
		
		for (int p=0; p < getPhoneNumbers().size(); p++) {
			phone = (PhoneNumber) getPhoneNumbers().elementAt(p);
			phones += phone.toString();
			if (p+1 <  getPhoneNumbers().size()) {
				phones += ", ";
			}
		}
		return phones;
	}

	public String toString() {
		return  "Customer[" 
			+ "\n\tName: " + lastName + ", " + firstName 
			+ "\n\tGender: " + gender 
			+ "\n\tBilling address: " + getBillingAddress()
			+ "\n\tShipping address: " + getShippingAddress()
			+ "\n\tPhone number(s): " +  getPhoneNumbersAsString()
			+ "]";
	}

}