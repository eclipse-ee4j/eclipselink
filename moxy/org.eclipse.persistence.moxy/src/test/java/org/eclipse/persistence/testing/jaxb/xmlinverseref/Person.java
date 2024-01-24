/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement
public class Person implements AddressOwner{
   public String name;
   @XmlInverseReference(mappedBy="owner")
   @XmlElement
   public Address addr;

   @Override
   public Address getAddr(){
       return addr;
   }

   public boolean equals(Object obj){
       if(obj instanceof Person comparePerson){
           return name.equals(comparePerson.name) &&
              addr.equals(comparePerson.addr) &&
              addr.owner == this;

       }
       return false;
   }
}
