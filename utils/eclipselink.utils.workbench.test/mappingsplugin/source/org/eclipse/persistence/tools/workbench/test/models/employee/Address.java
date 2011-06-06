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
package org.eclipse.persistence.tools.workbench.test.models.employee;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;

/** 
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */

public class Address implements Serializable {
	public BigDecimal id;
	public String street;
	public String city;
	public String province;
	public String postalCode;
	public String country;	
public Address()
{ 
	this.city = "";
	this.province = "";
	this.postalCode = "";
	this.street = "";
	this.country = "";
}

public String getCity()
{
	return this.city;
}
public String getCountry()
{
	return this.country;
}
/**
 * Return the persistent identifier of the receiver.
 */
public BigDecimal getId()
{
	return this.id;
}
public String getPostalCode()
{
	return this.postalCode;
}
public String getProvince(){
	return this.province;
}
public String getStreet()
{
	return this.street;
}
public void setCity(String city)
{
	this.city = city;
}
public void setCountry(String country)
{
	this.country = country;
}
/**
 * Set the persistent identifier of the receiver.
 */
public void setId(BigDecimal id)
{
	this.id = id;
}
public void setPostalCode(String postalCode)
{
	this.postalCode = postalCode;
}
public void setProvince(String province)
{
	this.province = province;
}
public void setStreet(String street)
{
	this.street = street;
}
/**
 * Print the address city and province.
 */

@Override
public String toString()
{
	StringWriter writer = new StringWriter();
	
	writer.write("Address: ");	
	writer.write(getStreet());
	writer.write(", ");
	writer.write(getCity());
	writer.write(", ");
	writer.write(getProvince());
	writer.write(", ");
	writer.write(getCountry());
	return writer.toString();
}
}
