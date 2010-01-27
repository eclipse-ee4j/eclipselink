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
 *     Matt MacIvor -  January, 2010
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import javax.xml.bind.annotation.XmlRootElement;

public class Customer {
	
	public String firstName;
	public String lastName;
	public String phoneNumber;
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Customer)) {
			return false;
		}
		Customer cust = (Customer)obj;
		
		return firstName.equals(cust.firstName) && lastName.equals(cust.lastName) && phoneNumber.equals(cust.phoneNumber);
	}

}
