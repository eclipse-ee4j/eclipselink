/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - July 15, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.jaxbelement.nested;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "root", namespace = "someuri")
public class Root {

    @XmlElementRef(name = "elem1", namespace = "someuri", type = JAXBElement.class)
    protected JAXBElement<Object> elem1;
      
    public JAXBElement<Object> getElem1() {
        return elem1;
    }

    public void setElem1(JAXBElement<Object> value) {
        this.elem1 = ((JAXBElement<Object> ) value);
    }
    
    public boolean equals(Object compareObject) {
    	boolean equals = super.equals(compareObject);
    	if(!equals){
    	   if(!(compareObject instanceof Root)){
    		   return false;
    	   }
    	   if(!compareJAXBElements(elem1, ((Root)compareObject).elem1)){
    		   return false;
    	   }
    	}
    	return true;    	    	
    }
    
    public boolean compareJAXBElements(JAXBElement<Object> controlObj, JAXBElement<Object> testObj) {
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
        
        if(controlValue == null && testValue != null){
        	return false;
        }
        if(controlValue != null && testValue == null){
        	return false;
        }
        if(controlValue instanceof Node){
        	if(!new XMLComparer().isNodeEqual((Node)controlValue, (Node)testValue)){
        		return false;
        	}
        }else if(!controlValue.equals(testValue)){
        	return false;        	
        }
        return true;
    }
   
}