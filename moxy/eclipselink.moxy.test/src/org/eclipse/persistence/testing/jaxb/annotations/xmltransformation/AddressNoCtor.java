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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

public class AddressNoCtor {
	public String street;
	public String city;
		
	public AddressNoCtor(String street, String city){
		this.street = street;
		this.city = city;
	}
	
    public boolean equals(Object obj) {
    	AddressNoCtor addr = (AddressNoCtor)obj;
        return addr.street.equals(this.street) && addr.city.equals(this.city);
    }
}
