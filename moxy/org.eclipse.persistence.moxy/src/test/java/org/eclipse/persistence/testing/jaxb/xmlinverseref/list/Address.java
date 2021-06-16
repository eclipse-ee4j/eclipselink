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
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
