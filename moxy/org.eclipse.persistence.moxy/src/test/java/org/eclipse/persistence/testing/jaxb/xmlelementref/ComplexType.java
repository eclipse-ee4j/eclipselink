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
//     Denise Smith - November 5, 2009
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.math.BigDecimal;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexType", propOrder = {
 "global",
 "local"
})
public class ComplexType {

 @XmlElement(name = "Global")
 protected boolean global;
 @XmlElementRef(name = "Local", type = ComplexType.TestLocal.class)
 protected ComplexType.TestLocal local;

 /**
  * Gets the value of the global property.
  *
  */
 public boolean isGlobal() {
     return global;
 }

 /**
  * Sets the value of the global property.
  *
  */
 public void setGlobal(boolean value) {
     this.global = value;
 }


 public ComplexType.TestLocal getLocal() {
     return local;
 }

 public void setLocal(ComplexType.TestLocal value) {
     this.local = value;
 }

 public static class TestLocal
     extends JAXBElement<BigDecimal>
 {

     protected final static QName NAME = new QName("", "Local");

     public TestLocal(BigDecimal value) {
         super(NAME, ((Class) BigDecimal.class), ComplexType.class, value);
     }

     public boolean equals(Object theObject){
         if(!(theObject instanceof TestLocal)){
             return false;
         }
         if(((TestLocal)theObject).getDeclaredType() != getDeclaredType()){
             return false;
         }
         if(((TestLocal)theObject).getName() != getName()){
             return false;
         }
         if(((TestLocal)theObject).getScope() != getScope()){
             return false;
         }
         if(!(((TestLocal)theObject).getValue().equals(getValue()))){
             return false;
         }
         return true;
     }
 }

 public boolean equals(Object theObject){
     if(!(theObject instanceof ComplexType)){
         return false;
     }
     if(((ComplexType)theObject).isGlobal() != isGlobal()){
         return false;
     }
     if(!(((ComplexType)theObject).getLocal().equals(getLocal()))){
         return false;
     }
     return true;
 }

}
