/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mmacivor - Initial implementation
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

    @Override
    public int hashCode() {
        int result = employees != null ? employees.hashCode() : 0;
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        return result;
    }
}
