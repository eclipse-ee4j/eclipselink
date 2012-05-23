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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.eis.employee;

import java.io.Serializable;
import java.io.StringWriter;

public class Address 
	implements Serializable 
{
	/** Direct Mapping */
	private String street1;
	
	/** Direct Mapping */
	private String street2;
	
	/** Direct Mapping */
	private String city;
	
	/** Direct Mapping */
	private String province;
	
	/** Direct Mapping */
	private String postalCode;
	
	
	public Address() { 
		this.street1 = "";
		this.street2 = "";
		this.city = "";
		this.province = "";
		this.postalCode = "";
	}
	
	public String getStreet1() {
		return this.street1;
	}
	
	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	
	public String getStreet2() {
		return this.street2;
	}
	
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public String getProvince(){
		return this.province;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getPostalCode() {
		return this.postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	/** Print the address city and province. */
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		
		writer.write("Address: ");	
		writer.write(this.getStreet1());
		writer.write(" ");
		writer.write(this.getStreet2());
		writer.write(", ");
		writer.write(this.getCity());
		writer.write(", ");
		writer.write(this.getProvince());
		writer.write(" ");
		writer.write(this.getPostalCode());
		
		return writer.toString();
	}
}
