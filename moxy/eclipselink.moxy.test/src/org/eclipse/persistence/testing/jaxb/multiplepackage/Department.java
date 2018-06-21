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
//     Denise Smith - September 3, 2009 Initial test
package org.eclipse.persistence.testing.jaxb.multiplepackage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmlelement.EmployeeNamespace;

@XmlRootElement(name="dept", namespace="somenamespace")
public class Department
{
    @XmlElement(name="id")
    public int id;

    public EmployeeNamespace emp;

    public String toString()
    {
        return "Dept: " + id;
    }

    public boolean equals(Object object) {
        Department dept = ((Department)object);
        if(dept.id != this.id){
            return false;
        }
        if(!emp.equals(dept.emp)){
            return false;
        }
        return true;
    }

}
