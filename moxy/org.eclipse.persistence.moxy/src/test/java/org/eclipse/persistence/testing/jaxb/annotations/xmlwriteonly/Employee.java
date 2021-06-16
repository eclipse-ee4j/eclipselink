/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlWriteOnly;

@XmlRootElement(name="employee")
public class Employee {
    public String name;

    @XmlElement
    @XmlWriteOnly
    public String writeOnlyField;

    public boolean equals(Object obj) {
        if(obj instanceof Employee) {
            Employee emp = (Employee)obj;
            if(name.equals(emp.name)) {
                if(writeOnlyField == null && emp.writeOnlyField == null) {
                    return true;
                }
                if(writeOnlyField != null && (writeOnlyField.equals(emp.writeOnlyField))) {
                    return true;
                }
            }
        }
        return false;
    }

}
