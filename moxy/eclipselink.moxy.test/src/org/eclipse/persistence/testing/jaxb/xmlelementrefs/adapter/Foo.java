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
//     Denise Smith - April 2013
package org.eclipse.persistence.testing.jaxb.xmlelementrefs.adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "foo", propOrder = {
 "e1OrE2"
})
@XmlRootElement
public class Foo {

 @XmlElementRefs({
     @XmlElementRef(name = "e1", type = JAXBElement.class),
     @XmlElementRef(name = "e2", type = JAXBElement.class)
 })
 protected List<JAXBElement<byte[]>> e1OrE2;

 public List<JAXBElement<byte[]>> getE1OrE2() {
     if (e1OrE2 == null) {
         e1OrE2 = new ArrayList<JAXBElement<byte[]>>();
     }
     return this.e1OrE2;
 }

 public boolean equals(Object obj){
     if(obj instanceof Foo){
         if(e1OrE2.size() != ((Foo)obj).e1OrE2.size()){
             return false;
         }
         for(int i=0;i<e1OrE2.size(); i++){
             JAXBElement<byte[]> next = e1OrE2.get(i);
             JAXBElement<byte[]> nextCompare = ((Foo)obj).e1OrE2.get(i);
             if(!compareJAXBElements(next, nextCompare)){
                 return false;
             }
         }
         return true;
     }
     return false;
 }

 public boolean compareJAXBElements(JAXBElement<byte[]> controlObj, JAXBElement<byte[]> testObj) {
     if(!controlObj.getName().getLocalPart().equals(testObj.getName().getLocalPart())){
         return false;
     }
     if(!controlObj.getDeclaredType().equals(testObj.getDeclaredType())){
         return false;
     }
     byte[] controlValue = controlObj.getValue();
     byte[] testValue = testObj.getValue();

    if(controlValue.length != testValue.length ){
        return false;
    }
    for(int x=0; x<controlValue.length; x++) {
        byte controlItem = Array.getByte(controlValue, x);
        byte testItem = Array.getByte(testValue, x);
        if(controlItem != testItem){
            return false;
        }
    }
    return true;
 }
}
