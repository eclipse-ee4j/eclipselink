/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen.imports;

import javax.xml.bind.annotation.*;

import org.eclipse.persistence.testing.jaxb.schemagen.imports.address.Address;

@XmlRootElement(name="employee-data")
@XmlType(name = "employee-type", namespace="employeeNamespace", propOrder = {"firstName", "birthday", "id", "age", "lastName", "address",
    "startTime", "responsibilities"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee
{
    @XmlAttribute(name="id", required=true)
    public int id;

    @XmlElement(required = true)
    public String firstName;

    public String lastName;

    public Address address;

    @XmlSchemaType(name="date")
    public java.util.Calendar birthday;

    public java.util.Calendar startTime;

    public int age;

    @XmlElement(name= "responsibilities")
    @XmlList
    public java.util.Collection<String> responsibilities;

}
