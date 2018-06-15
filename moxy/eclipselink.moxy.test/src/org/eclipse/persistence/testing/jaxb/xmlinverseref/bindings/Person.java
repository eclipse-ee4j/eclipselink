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
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement
public class Person {
   public String name;
   public Address addr;

   public Address getAddr(){
       return addr;
   }

   public boolean equals(Object obj){
       if(obj instanceof Person){
           Person comparePerson = (Person)obj;
           return name.equals(comparePerson.name) &&
              addr.equals(comparePerson.addr) &&
              addr.owner == this;

       }
       return false;
   }
}
