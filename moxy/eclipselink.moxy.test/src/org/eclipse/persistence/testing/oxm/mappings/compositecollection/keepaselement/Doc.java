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
 *     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.oxm.mappings.compositecollection.keepaselement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.RootKeepAsElement;
import org.w3c.dom.Element;

public class Doc {    
    protected List<Elem> elem;
    
    protected List<Object> elem1;

    public Doc(){
    	elem = new ArrayList<Elem>();
    	elem1 = new ArrayList<Object>();
    }
    
    public List<Elem> getElem() {
        return elem;
    }

    public void setElem(List<Elem> value) {
        this.elem = value;
    }

    public List<Object> getElem1() {
        return elem1;
    }

    public void setElem1(List<Object>  value) {
        this.elem1 = value;
    }

    public boolean equals(Object object) {
        if (object instanceof Doc) {
            Doc anotherDoc = (Doc) object;
            if (elem == null && anotherDoc.getElem() != null) {
                return false;
            } 
            if (elem != null && anotherDoc.getElem() == null) {
                return false;
            } 
            if (elem1 == null && anotherDoc.getElem1() != null) {
                return false;
            } 
            if (elem1 != null && anotherDoc.getElem1() == null) {
                return false;
            }
         
            List<Object>  obj1 = elem1;
            List<Object>  obj2 = anotherDoc.getElem1();
            
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
	            } else if(firstObject.getClass().equals(secondObject.getClass())) {
	                return firstObject.equals(secondObject);
	            } else{
	            	return false;
	            }
            }
                    
            List<Elem>  elemList = elem;
            List<Elem>  elem1List = anotherDoc.getElem();
            
            if(elemList.size() != elem1List.size()){
            	return false;
            }
            
            for(int i=0;i<elemList.size(); i++){
            	Elem firstObject = elemList.get(i);
            	Elem secondObject =elem1List.get(i);
	            if (!firstObject.equals(secondObject)) {
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