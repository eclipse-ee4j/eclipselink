/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee-data")
@XmlType(name = "employee-type", propOrder = {"firstName", "birthday", "id", "age", "lastName", "address", "department", 
    "startTime", "phoneNumbers", "responsibilities"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee 
{
    @XmlAttribute(name="id")
    public int id;
    
    public String firstName;

    public String lastName;
    
    public Address address;
    
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
}