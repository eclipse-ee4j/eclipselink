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
// Matt MacIvor - 2.4
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name="person", namespace="someuri")
@XmlType(name="person-type", namespace="someuri")
public class Person {

    public String firstName;
    public String lastName;
    public String address;

    public boolean equals(Object obj) {
        Person p = (Person)obj;

        return p.firstName.equals(firstName) && p.lastName.equals(lastName) && p.address.equals(address);
    }

}
