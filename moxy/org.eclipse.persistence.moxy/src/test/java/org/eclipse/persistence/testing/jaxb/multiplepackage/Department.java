/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - September 3, 2009 Initial test
package org.eclipse.persistence.testing.jaxb.multiplepackage;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
