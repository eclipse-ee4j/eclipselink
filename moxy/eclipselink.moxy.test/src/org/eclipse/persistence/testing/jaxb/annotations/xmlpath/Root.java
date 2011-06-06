/*******************************************************************************
* Copyright (c) 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="root")
public class Root {

    @XmlPath("employees/employee")
    public List<Employee> employees;
    
    @XmlPath("addresses/address")
    public List<Address> addresses;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Root)) {
            return false;
        } else {
            Root root = (Root)obj;
            if(root.employees.size() != employees.size() || root.addresses.size() != addresses.size()) {
                return false;
            }
            Iterator<Employee> emps = employees.iterator();
            Iterator<Employee> emps2 = root.employees.iterator();
            while(emps.hasNext()) {
                if(!(emps.next().equals(emps2.next()))) {
                    return false;
                }
            }
            
            Iterator<Address> addr = addresses.iterator();
            Iterator<Address> addr2 = root.addresses.iterator();
            while(addr.hasNext()) {
                if(!(addr.next().equals(addr2.next()))) {
                    return false;
                }
            }
            
            return true;
        }
    }
}
