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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="employee-data")
@XmlType(name = "employee-type", propOrder = {"firstName", "birthday", "id", "age", "lastName", "address", "department",
    "startTime", "phoneNumbers", "responsibilities", "peeps"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee
{
    @DecimalMax(value="10")
    @XmlAttribute(name="id", required=true)
    public int id;

    @Size(min=2, max=10)
    @XmlElement(required = true)
    public String firstName;

    public String lastName;

    @Valid
    @XmlMixed
    public Address address;

    @XmlAttribute
    public Department department;

    @XmlElement(name="phone-number")
    public java.util.Collection<PhoneNumber> phoneNumbers;

    @XmlSchemaType(name="date")
    public java.util.Calendar birthday;

    public java.util.Calendar startTime;

    public int age;

    @XmlElement(name= "responsibilities")
    @XmlList
    public java.util.Collection<String> responsibilities;

    @XmlElement(name="peep", namespace="examplenamespace")
    public java.util.Collection<Employee> peeps;
}
