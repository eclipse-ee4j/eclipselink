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
package org.eclipse.persistence.tools.workbench.test.models.insurance;

import java.io.Serializable;

/** 
 * <p><b>Purpose</b>: Represents the mailing address of the PolicyHolder
 * <p><b>Description</b>: Held in a private 1:1 relationship from PolicyHolder
 * @see PolicyHolder
 * @since TOPLink/Java 1.0
 */
 
public class Address implements Serializable 
{
	private String street;
	private String city;
	private String state;
	private String zipCode;
	private String country;
	
	//Back referenec to PolicyHolder is needed in the relational model as target foreign key is used in 
	//the 1:1 mapping, and the target object must have a relationship mapping to the source.
	//In the object-relational model, this is not required as in the alternative structure mapping, the 
	//Address object is aggregately stored (as STRUCT type in Oracle8i) in the HOLDER (PolicyHolder) source table. 
	private PolicyHolder policyHolder;

	
/**
 * Initialize a new address.
 */
public Address ( )
{
	this.street = "";
	this.city = "";
	this.state = "";
	this.zipCode = "";
	this.country = "";
}
public static Address example1( ) {
	
	Address address = new Address();
	address.setStreet("4 Garden Way");
	address.setCity("Boston");
	address.setState("MA");
	address.setCountry("United States");
	address.setZipCode("28150");
	return address;
}
public static Address example2( ) {
	
	Address address = new Address();
	address.setStreet("10 Wall Street");
	address.setCity("Manhattan");
	address.setState("NY");
	address.setCountry("United States");
	address.setZipCode("50124");
	return address;
}
public static Address example3( ) {
	
	Address address = new Address();
	address.setStreet("5511 Capital Center Dr");
	address.setCity("Raleigh");
	address.setState("NC");
	address.setCountry("United States");
	address.setZipCode("27606");
	return address;
}
public String getCity ( ) {
	return this.city;
}
public String getCountry ( ) {
	return this.country;
}
public PolicyHolder getPolicyHolder ( ) {
	return this.policyHolder;
}
public String getState ( ) {
	return this.state;
}
public String getStreet ( ) {
	return this.street;
}
public String getZipCode ( ) {
	return this.zipCode;
}
public void setCity(String city) {
	this.city = city;
}
public void setCountry(String country) {
	this.country = country;
}
public void setPolicyHolder(PolicyHolder policyHolder) {
	this.policyHolder = policyHolder;
}
public void setState(String state) {
	this.state = state;
}
public void setStreet(String street) {
	this.street = street;
}
public void setZipCode(String zipCode) {
	this.zipCode = zipCode;
}
@Override
public String toString ( ) {
	return "Address: " + getStreet() + ", " + getCity() + "," + getState();
}
}
