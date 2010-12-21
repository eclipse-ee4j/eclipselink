/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="root")
public class Root {
    @XmlElement(name="employee")
    public EmployeeWithElements employee;
    
    @XmlElement(name="address")
    public Collection<Address> addresses;
    
    @XmlElement(name="phone-number")
    public Collection<PhoneNumber> phoneNumbers;
    
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
        if (!(obj instanceof Root)) {
            return false;
        }
        
        Root tgtRoot = (Root) obj;
        return tgtRoot.employee.equals(this.employee);
    }
}
