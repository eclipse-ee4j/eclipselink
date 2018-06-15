/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - November 02/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

public class Employee {
    public String name;

    @javax.xml.bind.annotation.XmlIDREF
    public Address homeAddress;

    public Address workAddress;

    public boolean equals(Object compareObj){
        if (compareObj instanceof Employee){
            Employee emp = (Employee)compareObj;
            return name.equals(emp.name) && homeAddress.equals(emp.homeAddress) && workAddress.equals(emp.workAddress);
        }
        return false;
    }
}
