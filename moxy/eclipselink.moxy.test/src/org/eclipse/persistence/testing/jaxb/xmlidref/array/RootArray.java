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
package org.eclipse.persistence.testing.jaxb.xmlidref.array;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="root")
@XmlType(name = "root")
public class RootArray {
    @XmlElement(name="employee")
	public EmployeeArray employee;
    
    @XmlElement(name="address")
	public Collection<AddressArray> addresses;
    
    @XmlElement(name="phone-number")
    public Collection<PhoneNumberArray> phoneNumbers;
	
	/**
	 * For the purpose of ID/IDREF  tests, equality will be performed 
     * on the Root's Employee - more specifically, the address(es) 
     * attribute will be compared to ensure that the correct target 
     * Address(es) was returned based on the key(s).
	 * 
	 * @param obj a Root containing an Employee whose Address(es) will
	 * be checked to verify correctness.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof RootArray)) {
			return false;
		}
		
		RootArray tgtRoot = (RootArray) obj;
		return tgtRoot.employee.equals(this.employee);
	}
}
