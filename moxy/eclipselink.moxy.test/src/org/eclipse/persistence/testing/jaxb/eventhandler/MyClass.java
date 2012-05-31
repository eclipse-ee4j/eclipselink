/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Denise Smith - May 2012
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.eventhandler;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
