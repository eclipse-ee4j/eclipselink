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
// dmccann - August 31/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {
    @XmlElement(name="employee")
    public List<Employee> employees;
    @XmlElement(name="address")
    public List<Address> addresses;

    public boolean equals(Object compareObj){
        if (compareObj instanceof Root){
            Root rootObj = (Root)compareObj;
            return (employees == null && rootObj.employees ==null || employees.equals(rootObj.employees) )&&
            (addresses == null && rootObj.addresses ==null || addresses.equals(rootObj.addresses) );
    }
    return false;
    }

    @Override
    public int hashCode() {
        int result = employees != null ? employees.hashCode() : 0;
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        return result;
    }
}
