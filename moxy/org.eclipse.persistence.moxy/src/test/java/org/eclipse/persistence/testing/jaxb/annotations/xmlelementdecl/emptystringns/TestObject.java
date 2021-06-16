/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - January 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


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
