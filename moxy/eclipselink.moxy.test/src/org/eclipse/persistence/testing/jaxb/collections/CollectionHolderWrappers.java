/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CollectionHolderWrappers {

    @XmlElementWrapper()
    protected List<Integer> collection1;

    @XmlElementWrapper(name="collection2-wrapper")
    protected List<Object> collection2;

    @XmlElementWrapper(name="collection3-wrapper")
    @XmlAnyElement
    protected List collection3;

    @XmlElementWrapper(name="collection4-wrapper")
    protected List<CollectionHolderWrappers> collection4;

    @XmlElementWrapper(name="collection5-wrapper")
    @XmlElementRefs({@XmlElementRef(name="root"), @XmlElementRef(name="root2")})
    protected List<JAXBElement<String>> collection5;

    @XmlElementWrapper(name="collection6-wrapper")
    protected List<CoinEnum> collection6;

    @XmlElementWrapper(name="collection7-wrapper")
    protected List<byte[]> collection7;

    public CollectionHolderWrappers() {
    }

    public List<Integer> getCollection1() {
        return collection1;
    }

    public void setCollection1(List<Integer> collection1) {
        this.collection1 = collection1;
    }

    public List<Object> getCollection2() {
        return collection2;
    }

    public void setCollection2(List<Object> collection2) {
        this.collection2 = collection2;
    }

    public List getCollection3() {
        return collection3;
    }

    public void setCollection3(List collection3) {
        this.collection3 = collection3;
    }

    public List<CollectionHolderWrappers> getCollection4() {
        return collection4;
    }

    public void setCollection4(List<CollectionHolderWrappers> collection4) {
        this.collection4 = collection4;
    }

    public List<JAXBElement<String>> getCollection5() {
        return collection5;
    }

    public void setCollection5(List<JAXBElement<String>> collection5) {
        this.collection5 = collection5;
    }

    public List<CoinEnum> getCollection6() {
        return collection6;
    }

    public void setCollection6(List<CoinEnum> collection6) {
        this.collection6 = collection6;
    }

    public List<byte[]> getCollection7() {
        return collection7;
    }

    public void setCollection7(List<byte[]> collection7) {
        this.collection7 = collection7;
    }

    public boolean equals(Object compareObject){
         if (compareObject instanceof CollectionHolderWrappers) {
             CollectionHolderWrappers compareCollectionHolder = ((CollectionHolderWrappers)compareObject);
             return compareCollections(collection1, compareCollectionHolder.getCollection1())
                    && compareCollections(collection2, compareCollectionHolder.getCollection2())
                    && compareCollections(collection3, compareCollectionHolder.getCollection3())
                    && compareCollections(collection4, compareCollectionHolder.getCollection4())
                    && compareCollections(collection5, compareCollectionHolder.getCollection5())
                    && compareCollections(collection6, compareCollectionHolder.getCollection6())
                    && compareCollections(collection7, compareCollectionHolder.getCollection7())
                    ;
         }
         return false;
    }

    private boolean compareCollections(Collection compareList1, Collection compareList2) {
        if (compareList1 == null) {
            return compareList2 == null;
        } else {
            if (compareList2 == null) {
                return false;
            }
            if(compareList1.size() != compareList2.size()){
                return false;
            }
            Iterator iter1 = compareList1.iterator();
            Iterator iter2 = compareList2.iterator();
            while(iter1.hasNext()){
            	Object next1 = iter1.next();
            	Object next2 = iter2.next();
            	if(!compareObjects(next1, next2)){
            		return false;
            	}
            }
            return true;       
        }
    }
    
    private boolean compareObjects(Object obj1, Object obj2){
    	if(obj1 instanceof JAXBElement){
    		 if(obj2 instanceof JAXBElement){
	    		 if(! ((JAXBElement)obj1).getName().getLocalPart().equals(((JAXBElement)obj2).getName().getLocalPart())){
	    			 return false;
	    		 }
	    		 if(! ((JAXBElement)obj1).getDeclaredType().equals(((JAXBElement)obj2).getDeclaredType())){
	    			 return false;
	    		 }
	    		 if(! ((JAXBElement)obj1).getValue().equals(((JAXBElement)obj2).getValue())){
	    			 return false;
	    		 } 
	    	     return true;
    		 }
    		 return false;
    	} else if (obj1 instanceof org.w3c.dom.Element) {
    	    if(obj2 instanceof org.w3c.dom.Element) {
    	        return ((Element)obj1).getLocalName().equals(((Element)obj2).getLocalName());
    	    }
    	    return false;
    	} else{
    		 if(obj1.getClass().isArray() && obj2.getClass().isArray()){
    	         return compareArrays(obj1, obj2);
    	     }else{
    		    return obj1.equals(obj2);
    	     }
    	}
    }

    protected boolean compareArrays(Object controlValue, Object testValue) {    	
        int controlSize = Array.getLength(controlValue);
        int objSize = Array.getLength(testValue);
        if(controlSize != objSize){
        	return false;
        }
        for(int x=0; x<controlSize; x++) {
            Object controlItem = Array.get(controlValue, x);
            Object testItem = Array.get(testValue, x);
                           
            if(!controlItem.equals(testItem)){
             	return false;
            }           
        }
        return true;
    }
}
