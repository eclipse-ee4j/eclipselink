/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - November 5, 2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexType", propOrder = {
 "global",
 "local"
})
public class ComplexType {

 @XmlElement(name = "Global")
 protected boolean global;
 @XmlElementRef(name = "Local", type = ComplexType.TestLocal.class)
 protected ComplexType.TestLocal local;

 /**
  * Gets the value of the global property.
  * 
  */
 public boolean isGlobal() {
     return global;
 }

 /**
  * Sets the value of the global property.
  * 
  */
 public void setGlobal(boolean value) {
     this.global = value;
 }


 public ComplexType.TestLocal getLocal() {
     return local;
 }

 public void setLocal(ComplexType.TestLocal value) {
     this.local = value;
 }

 public static class TestLocal
     extends JAXBElement<BigDecimal>
 {

     protected final static QName NAME = new QName("", "Local");

     public TestLocal(BigDecimal value) {
         super(NAME, ((Class) BigDecimal.class), ComplexType.class, value);
     }

     public boolean equals(Object theObject){
    	 if(!(theObject instanceof TestLocal)){
    		 return false;
    	 }    	 
    	 if(((TestLocal)theObject).getDeclaredType() != getDeclaredType()){
    		 return false;
    	 }
    	 if(((TestLocal)theObject).getName() != getName()){
    		 return false;
    	 }
    	 if(((TestLocal)theObject).getScope() != getScope()){
    		 return false;
    	 }
    	 if(!(((TestLocal)theObject).getValue().equals(getValue()))){
    		 return false;
    	 }
    	 return true;
     }
 }
 
 public boolean equals(Object theObject){
	 if(!(theObject instanceof ComplexType)){
		 return false;
	 }
	 if(((ComplexType)theObject).isGlobal() != isGlobal()){
		 return false;
	 }
	 if(!(((ComplexType)theObject).getLocal().equals(getLocal()))){
		 return false;
	 } 
	 return true;
 }

}
