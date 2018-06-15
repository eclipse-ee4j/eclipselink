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
package org.eclipse.persistence.testing.jaxb.annotations.xmlreadonly;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlReadOnly;

@XmlRootElement(name="employee")
public class Employee {
    public String name;

    @XmlReadOnly
    public String readOnlyField;

    public boolean equals(Object obj) {
        if(obj instanceof Employee) {
            return name.equals(((Employee)obj).name) && readOnlyField.equals(((Employee)obj).readOnlyField);
        }
        return false;
    }

}
