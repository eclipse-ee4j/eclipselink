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
package org.eclipse.persistence.testing.jaxb.xmlinverseref.list;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement
public class Address {
   public String street;
   @XmlInverseReference(mappedBy ="addrs")
   @XmlElement
   public Person owner;

   public boolean equals(Object obj){
       if(obj instanceof Address){
           Address compareAddr = (Address)obj;
           if(! street.equals(compareAddr.street)){
               return false;
           }
           return listContains(owner.addrs, this) && listContains(compareAddr.owner.addrs, compareAddr);
            //calling person.equals will be an infinite loop
       }
       return false;
   }

   private boolean listContains(List theList, Address addr){
       for(int i=0;i<theList.size(); i++){
           if(theList.get(i) == addr){
               return true;
           }
       }
       return false;
   }
}
