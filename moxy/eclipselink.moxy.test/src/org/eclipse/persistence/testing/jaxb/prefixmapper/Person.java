/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Matt MacIvor - 2.4
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
