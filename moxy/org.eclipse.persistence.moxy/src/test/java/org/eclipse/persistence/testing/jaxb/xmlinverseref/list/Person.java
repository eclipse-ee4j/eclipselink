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
public class Person {
   public String name;
   @XmlInverseReference(mappedBy="owner")
   @XmlElement
   public List<Address> addrs;

   public boolean equals(Object obj){
       if(obj instanceof Person){
           Person comparePerson = (Person)obj;
           if(! name.equals(comparePerson.name)){
               return false;
           }
           if(addrs.size() != comparePerson.addrs.size()){
               return false;
           }
           for(int i=0;i<addrs.size();i++){
               Address next = addrs.get(i);
               Address nextCompare = comparePerson.addrs.get(i);
               if(!next.equals(nextCompare)){// || next.owner != this || nextCompare.owner != this){
                   return false;
               }
               /*
               if(next.owner == null && nextCompare.owner != null){
                   return false;
               }
               if(next.owner != null && nextCompare.owner == null){
                   return false;
               }*/
               if(next.owner != this || nextCompare.owner != comparePerson){
                   return false;
               }

           }
           return true;

       }
       return false;
   }
}
