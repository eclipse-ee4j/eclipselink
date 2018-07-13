/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
