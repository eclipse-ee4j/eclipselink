/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith, February 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinverseref.list;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
