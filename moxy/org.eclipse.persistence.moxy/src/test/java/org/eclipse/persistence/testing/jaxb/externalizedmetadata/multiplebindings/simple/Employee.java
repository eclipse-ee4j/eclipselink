/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.Address;

@XmlRootElement(name="employee")
@XmlType(name="employee-type", propOrder={"id", "name", "age", "address", "bytes", "jobTitle", "departments", "phone"})
public class Employee extends Person {

    public String name;

    public int age;

    public String address;

    public byte[] bytes;

    public String jobTitle;

    public List<Department> departments;

    public Phone phone;

    public boolean equals(Object obj){
        if(!(obj instanceof Employee)){
            return false;
        }
        if(!compareString(name, ((Employee)obj).name)){
            return false;
        }
        if(!compareString(address, ((Employee)obj).address)){
            return false;
        }

        if(!compareString(id, ((Employee)obj).id)){
            return false;
        }

        if(!compareString(jobTitle, ((Employee)obj).jobTitle)){
            return false;
        }

        if(age!=((Employee)obj).age){
            return false;
        }

        if (bytes == null) {
            if (((Employee)obj).bytes != null) {
                return false;
            }
        } else if (((Employee)obj).bytes == null) {
            return false;
        }
        if (!Arrays.equals(bytes, ((Employee)obj).bytes)) {
            return false;
        }

        if (phone == null){
            if(((Employee)obj).phone != null) {
               return false;
            }
        }else{
            if(!phone.equals(((Employee)obj).phone)){
                return false;
            }

        }


        return true;
    }

    private boolean compareString(String control, String test){
        if(control == null){
            if(test != null){
                return false;
            }
        }else{
            if(test == null){
                return false;
            }else{
                if(!control.equals(test)){
                    return false;
                }
            }
        }
        return true;
    }
}
