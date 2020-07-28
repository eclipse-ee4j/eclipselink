/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee
{

    public static final int uid = 1234567;

    @XmlAttribute(name="id")
    public int id;

    public String firstName;

    public String lastName;

    private transient String blah;

    public java.util.Calendar birthday;

    @XmlTransient
    public int age;

    public Address address;

    @XmlElement(name="responsibility")
    @XmlElementWrapper(name="responsibilities")
    public java.util.Collection responsibilities;

  public String toString()
    {
        return "EMPLOYEE: " + id +" " + firstName +" " + lastName +" " + birthday;
    }

    public boolean equals(Object object) {
         Employee emp = ((Employee)object);
        if((emp.id != this.id) ||(!(emp.firstName.equals(this.firstName))) || (!(emp.lastName.equals(this.lastName))) ||(emp.age != this.age))
        {
            return false;
        }
        if(!(emp.birthday.getTimeInMillis() == (this.birthday.getTimeInMillis())))
    {
        return false;
    }
        if((emp.responsibilities == null) && (this.responsibilities != null))
        {
            return false;
        }
      if((this.responsibilities == null) && (emp.responsibilities != null))
      {
          return false;
      }
        if(this.responsibilities !=null)
        {
            if(!(this.responsibilities.containsAll(emp.responsibilities))){
                return false;
            }
          if(!(emp.responsibilities.containsAll(this.responsibilities))){
              return false;
          }
        }

        if((this.address == null) && (emp.address != null)){
            return false;
        }
        if((emp.address == null) && (this.address != null)){
            return false;
        }
        if(!address.equals(emp.address)){
            return false;
        }
        //need to compare responsibilities
        return true;
    }

    public void setBlah(String blah) {
        this.blah = blah;
    }
}
