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
 * Denise Smith - September 15 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.any;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"any"})
@XmlRootElement(name = "theRoot")
public class Root {

 @XmlAnyElement(lax = true)
 protected Object any;


 public Object getAny() {
     return any;
 }

 public void setAny(Object value) {
     this.any = value;
 }
 
 public boolean equals(Object obj){
	 Root compareObject = ((Root)obj);
	 
	 return compareJAXBElementObjects((JAXBElement)this.any, (JAXBElement)compareObject.any);
 }
 
 public boolean compareJAXBElementObjects(JAXBElement controlObj, JAXBElement testObj) {
	 if(!controlObj.getName().getLocalPart().equals(testObj.getName().getLocalPart())){
		 return false;
	 }
	 if(!controlObj.getName().getNamespaceURI().equals(testObj.getName().getNamespaceURI())){
		 return false;
	 }
	 if(!controlObj.getDeclaredType().equals(testObj.getDeclaredType())){
		 return false;
	 }
 
     Object controlValue = controlObj.getValue();
     Object testValue = testObj.getValue();

     if(controlValue == null) {
     	if(testValue == null){
     		return true;
     	}
     	return false;
     }else{
     	if(testValue == null){
     		return false;	
     	}
     }
     
   
     if(!controlValue.equals(testValue)){
    	 return false;
     }
     return true;
 }

}
