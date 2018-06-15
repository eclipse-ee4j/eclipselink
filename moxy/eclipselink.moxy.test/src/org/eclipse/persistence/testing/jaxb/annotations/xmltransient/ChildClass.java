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
//  -Matt MacIvor - Initial Implementation - 2.4.1
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="child")
@XmlType(propOrder={"firstName", "lastName"})
public class ChildClass extends SuperClass {

    private String lastName;

    public String getFirstName() {
        return super.getFirstName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public boolean equals(Object obj) {
        ChildClass chld = (ChildClass)obj;

        return getFirstName().equals(chld.getFirstName()) && getLastName().equals(chld.getLastName());
    }
}
