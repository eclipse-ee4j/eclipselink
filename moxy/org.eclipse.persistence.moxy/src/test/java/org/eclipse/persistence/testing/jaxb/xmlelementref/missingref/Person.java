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
//     Denise Smith  January 26, 2010 - 2.0.1
package org.eclipse.persistence.testing.jaxb.xmlelementref.missingref;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person", propOrder = {
    "arg0", "name"
})
public class Person {
    @XmlElementRef(name = "arg0", type = JAXBElement.class)
    protected JAXBElement<byte[]> arg0;

    protected String name;

    public JAXBElement<byte[]> getArg0() {
        return arg0;
    }

    public void setArg0(JAXBElement<byte[]> arg0) {
        this.arg0 = arg0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object theObject){
        if(!(theObject instanceof Person)){
            return false;
        }
        Person otherPerson = (Person)theObject;
        if(!name.equals(otherPerson.getName())){
            return false;
        }
        if(arg0 == null){
            if(otherPerson.getArg0() != null){
                return false;
            }
        }else{
            if(otherPerson.getArg0() == null){
                return false;
            }else{
                compareJAXBElements(arg0, otherPerson.getArg0());
            }
        }
        return true;
    }

    private boolean compareJAXBElements(JAXBElement<byte[]> elem1, JAXBElement<byte[]> elem2){
        if(elem1.getScope() != elem2.getScope()){
            return false;
        }
        if(elem1.getDeclaredType()!= elem2.getDeclaredType()){
            return false;
        }
        if(elem1.getName().equals(elem2.getName())){
            return false;
        }
        return compareByteArrays(elem1.getValue(), elem2.getValue());
    }

    private boolean compareByteArrays(byte[] first, byte[] second){
        if(first.length != second.length){
            return false;
        }

        for(int i=0; i<first.length; i++){
            if (first[i] != second[i]){
                return false;
            }
        }
        return true;
    }
}
