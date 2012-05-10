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
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

public class Address {
	
	private String id;
	private String street;
	private String city;
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean equals(Object object) {
	    try {
	        Address testAddress = (Address) object;
	        if(!Comparer.equals(id, testAddress.getId())) {
	            return false;
	        }
            if(!Comparer.equals(street, testAddress.getStreet())) {
                return false;
            }
            if(!Comparer.equals(city, testAddress.getCity())) {
                return false;
            }
	        return true;
	    } catch(ClassCastException e) {
	        return false;
	    }
	    
	}
	
}
