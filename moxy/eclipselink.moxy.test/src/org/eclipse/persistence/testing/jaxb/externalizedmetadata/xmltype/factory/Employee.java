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
//  - rbarkhouse - 08 November 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.factory;

import javax.xml.bind.annotation.XmlTransient;

public class Employee {

    public int id = -1;
    public String firstName = null;
    public String lastName = null;

    @XmlTransient
    public boolean fromFactoryMethod;

    public boolean equals(Object o) {
        Employee obj;
        try {
            obj = (Employee) o;
        } catch (ClassCastException cce) {
            return false;
        }
        if (id != obj.id) {
            return false;
        }
        if (!firstName.equals(obj.firstName)) {
            return false;
        }
        if (!lastName.equals(obj.lastName)) {
            return false;
        }
        if (fromFactoryMethod != obj.fromFactoryMethod) {
            return false;
        }
        return true;
    }

    public String toString() {
        String isFromFactoryMethod = fromFactoryMethod ? "Factory" : "--";
        return getClass().getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + " #" + id + "|" + firstName + "|" + lastName + "|" + isFromFactoryMethod;
    }

}
