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
//    Denise Smith - May 2012
package org.eclipse.persistence.testing.jaxb.eventhandler;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MyClass {
       @XmlAttribute
       public Integer myAttribute;
       @XmlElement
       public Integer myElement;
       @XmlAttribute
       public MyEnum myEnumAttribute;
       @XmlElement
       public MyEnum myEnumElement;

       public boolean equals(Object testObject){
           if(testObject instanceof MyClass){
               MyClass compareObject = (MyClass)testObject;
               if(myAttribute != compareObject.myAttribute || myElement != compareObject.myElement){
                   return false;
               }
               if(myEnumAttribute == null){
                   if(compareObject.myEnumAttribute != null){
                       return false;
                   }
               }else if(!myEnumAttribute.equals(compareObject.myEnumAttribute)){
                   return false;
               }
               if(myEnumElement == null){
                   if(compareObject.myEnumElement != null){
                       return false;
                   }
               }else if(!myEnumElement.equals(compareObject.myEnumElement)){
                   return false;
               }
               return true;
           }
           return false;
       }
}
