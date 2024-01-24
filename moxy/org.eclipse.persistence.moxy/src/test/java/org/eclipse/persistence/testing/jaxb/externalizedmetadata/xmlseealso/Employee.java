/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="employee")
@XmlSeeAlso({XmlSeeAlsoTestCases.class})
public class Employee {
    public String firstName;
    public String lastName;

    public void setMyInt(int newInt) {}
    public int getMyInt() {
        return 66;
    }

    public boolean equals(Object obj){
        if(obj instanceof Employee empObj){
            return firstName.equals(empObj.firstName) && lastName.equals(empObj.lastName);
        }
        return false;
    }
}
