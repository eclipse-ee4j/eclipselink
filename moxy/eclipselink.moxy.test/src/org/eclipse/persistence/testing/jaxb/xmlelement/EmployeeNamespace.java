/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelement;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee-data")
public class EmployeeNamespace
{
    @XmlElement(name="id", namespace="my.cool/namespace")
    public int id;

    // the following are invalid set/get methods and should be ignored
    public void setSomething() {}  // no input parameter
    public void getSomething() {}  // void return type
    public void isSomething() {}   // void return type
    public Object getSomething(Object it) { return new Object(); }  // has an input parameter
    public Boolean isSomething(Boolean it) { return true; }  // has an input parameter

    public String toString()
    {
        return "EMPLOYEE: " + id;
    }

    public boolean equals(Object object) {
        EmployeeNamespace emp = ((EmployeeNamespace)object);
        return emp.id == this.id;
    }
}
