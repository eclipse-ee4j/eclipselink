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
 *     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.oxm.mappings.compositecollection.keepaselement;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

public class Elem {

    protected List<Object>  elem;
    
    protected List<Object>  elem1;

    public List<Object>  getElem() {
        return elem;
    }

    public void setElem(List<Object>  value) {
        this.elem = value;
    }

    public List<Object>  getElem1() {
        return elem1;
    }

    public void setElem1(List<Object>  value) {
        this.elem1 = value;
    }

    public boolean equals(Object object) {
        if (object instanceof Elem) {
            Elem anotherElem = (Elem) object;
            if (elem == null && anotherElem.getElem() != null) {
                return false;
            } 
            if (elem != null && anotherElem.getElem() == null) {
                return false;
            } 
            if (elem1 == null && anotherElem.getElem1() != null) {
                return false;
            } 
            if (elem1 != null && anotherElem.getElem1() == null) {
                return false;
            }

            List<Object>  obj1 = elem;
            List<Object>  obj2 = anotherElem.getElem();
            if(obj1.size() != obj2.size()){
            	return false;
            }
            for(int i=0;i<obj1.size(); i++){
            	Object firstObject = obj1.get(i);
            	Object secondObject =obj2.get(i);
	            if ((firstObject instanceof Element) && (secondObject instanceof Element)) {
	                Element elem1 = (Element )firstObject;
	                Element elem2 = (Element) secondObject;
	                
	                if (!(elem1.getLocalName().equals(elem2.getLocalName()))) {
	                    return false;
	                }
	            } else {
	                return false;
	            }
            }
                        
            List<Object>  obj3 = elem;
            List<Object>  obj4 = anotherElem.getElem();
            
            if(obj3.size() != obj4.size()){
            	return false;
            }
            
            for(int i=0;i<obj3.size(); i++){
            	Object firstObject = obj3.get(i);
            	Object secondObject =obj4.get(i);
	            if ((firstObject instanceof Element) && (secondObject instanceof Element)) {
	                Element elem1 = (Element )firstObject;
	                Element elem2 = (Element) secondObject;
	                
	                if (!(elem1.getLocalName().equals(elem2.getLocalName()))) {
	                    return false;
	                }
	            } else {
	                return false;
	            }
            }

            // Passed all equality tests
            return true;
        } else {
            return false;
        }
    }    
    
}