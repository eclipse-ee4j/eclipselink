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
//     Matt MacIvor - 2.3
package org.eclipse.persistence.testing.jaxb.binder.nscollison;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
