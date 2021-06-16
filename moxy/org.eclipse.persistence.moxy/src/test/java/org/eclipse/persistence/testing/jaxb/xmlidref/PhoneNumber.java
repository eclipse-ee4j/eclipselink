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
// mmacivor - October 16th 2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref;

import jakarta.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class PhoneNumber {

    @XmlID
    public String id;

    public String number;

    @XmlInverseReference(mappedBy = "phones")
    public Employee emp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (emp != null ? !emp.equalsWithoutCyclicDependency(that.emp) : that.emp != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        // Can't do hashCode on emp field, because it's hashCode uses a cyclic reference to this object.
        // result = 31 * result + (emp != null ? emp.hashCode() : 0);
        result = 31 * result + (emp != null ? emp.hashCodeWithoutCyclicDependency() : 0);
        return result;
    }
}
