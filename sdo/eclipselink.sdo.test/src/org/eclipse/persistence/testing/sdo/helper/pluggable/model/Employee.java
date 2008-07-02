/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/*
   DESCRIPTION

   PRIVATE CLASSES

   NOTES

   MODIFIED    (MM/DD/YY)
    mfobrien    07/28/06 - Creation
 */

/**
 *  @version $Header: Employee.java 22-aug-2006.16:29:40 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.0
 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.helper.ListWrapper;

/**
 * This simple POJO class has a loose 1-1 aggregation relationship with Address,
 * and a 1-n aggregation relationship with Phone.<br>
 */
public class Employee extends POJO {
    // instance variables
    // simple direct relationship
    private String id;

    // simple direct relationship
    private String name;

    // 1-1 object relationship
    private Address address;

    // 1-n object relationship
    private List phones;

    // default constructor
    public Employee() {
    }

    public Employee(String id, String name, Address address, List phones) {
        this(id, name, address);
        this.phones = phones;
    }

    public Employee(String id, String name, Address address) {
        this(id, name);
        this.address = address;
    }

    public Employee(String id, String name) {
        this(id);
        this.name = name;
    }

    public Employee(String id) {
        this();
        this.id = id;
    }

    // TODO: we need a generic set method    
    public void setPhone(Phone aPhone) {
        if (phones == null) {
            phones = new ArrayList();
        }
        phones.add(aPhone);
    }

    public void setPhones(List aList) {
        phones = aList;
    }

    public void setPhones(ArrayList aList) {
        phones = aList;
    }

    // overloaded function to handle setName(null)
    public void setPhones() {
        phones = null;
    }

    public List getPhones() {
        return phones;
    }

    public List getPhone() {
        return phones;
    }

    public Phone getPhone(int index) {
        if (phones != null) {
            return (Phone)phones.get(index);
        } else {
            return null;
        }
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address anAddress) {
        address = anAddress;
    }

    // overloaded function to handle setName(null)
    public void setAddress() {
        address = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // overloaded function to handle setName(null)
    public void setId() {
        id = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // overloaded function to handle setName(null)
    public void setName() {
        this.name = null;
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(getClass().getSimpleName());
        aBuffer.append("(");
        // use reflection instead
        aBuffer.append(id);
        aBuffer.append(",");
        aBuffer.append(name);
        aBuffer.append(",");
        aBuffer.append(address);
        aBuffer.append(",");
        aBuffer.append(phones);
        aBuffer.append(")");
        return aBuffer.toString();
    }
}