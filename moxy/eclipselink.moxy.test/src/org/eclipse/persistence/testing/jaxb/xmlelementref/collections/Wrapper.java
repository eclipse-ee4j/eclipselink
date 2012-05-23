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
 *     Denise Smith - February 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Wrapper {

	@XmlElementRef(name = "return", type = JAXBElement.class)
    protected List<JAXBElement<DataHandler>> content;

    public List<JAXBElement<DataHandler>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<DataHandler>>();
        }
        return this.content;
    }
    
    public boolean equals(Object compareobject){
    	if(compareobject instanceof Wrapper){
    		if(getContent().size() != ((Wrapper)compareobject).getContent().size()){
    			return false;
    		}
    		for(int i=0;i<getContent().size(); i++){
    			Object object1 = getContent().get(i);
    			Object object2 = ((Wrapper)compareobject).getContent().get(i);
    			if(object1 instanceof JAXBElement && object2 instanceof JAXBElement){
    				if(!compareJAXBElements((JAXBElement)object1, (JAXBElement)object2)){
    					return false;
    				}
    			}else if(!object1.equals(object2)){
					return false;
				}
    			
    		}
    		return true;
    	}
    	return false;
    }
    
    private boolean compareJAXBElements(JAXBElement object1, JAXBElement object2){
    	if (! object1.getName().getLocalPart().equals(object2.getName().getLocalPart())){
    		return false;
    	}
    	if (! object1.getName().getNamespaceURI().equals(object2.getName().getNamespaceURI())){
    		return false;
    	}
    	if (! object1.getDeclaredType().equals(object2.getDeclaredType())){
    		return false;
    	}	

        Object controlValue = object1.getValue();
        Object testValue = object2.getValue();

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
       
        return controlValue.equals(testValue);
         
    }


}