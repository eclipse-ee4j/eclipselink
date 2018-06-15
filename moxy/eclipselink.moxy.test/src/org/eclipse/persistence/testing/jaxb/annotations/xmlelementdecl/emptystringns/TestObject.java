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
//     Denise Smith - January 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TestObject {
   private Object theValue;

   public Object getTheValue() {
    return theValue;
   }

   public void setTheValue(Object theValue) {
    this.theValue = theValue;
   }

public boolean equals(Object o){
       if(o instanceof TestObject){
           if(theValue == null){
               return ((TestObject)o).theValue == null;
           }else{
             return theValue.equals(((TestObject)o).theValue);
           }
       }
       return false;
   }

}
