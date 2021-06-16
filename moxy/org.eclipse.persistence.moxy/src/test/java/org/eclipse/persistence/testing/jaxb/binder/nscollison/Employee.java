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
//     Matt MacIvor - 2.3
package org.eclipse.persistence.testing.jaxb.binder.nscollison;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.jaxb.binder.nscollison.package2.Address;

@XmlRootElement(namespace="mynamespace1")
@XmlType(name="employee-type", namespace="mynamespace1")
public class Employee {

    public Employee() {

    }

    @XmlElement(namespace="mynamespace2")
    public Address address;

    public String firstName;
    public String lastName;

    public int id;
}
