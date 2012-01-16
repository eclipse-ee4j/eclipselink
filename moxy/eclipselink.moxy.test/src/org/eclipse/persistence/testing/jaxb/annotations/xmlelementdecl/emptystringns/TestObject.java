/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - January 2012
 ******************************************************************************/
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
