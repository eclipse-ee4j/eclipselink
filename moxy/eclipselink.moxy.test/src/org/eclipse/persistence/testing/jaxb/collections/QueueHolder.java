/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="container")
public class QueueHolder {

    protected Queue<Integer> collection1;

    @XmlList
    protected Queue<String> collection2;

    protected Queue<Object> collection3;

    @XmlAnyElement(lax=true)
    protected Queue collection4;

    protected Queue<QueueHolder> collection5;

    @XmlElementRefs({@XmlElementRef(name="root"), @XmlElementRef(name="root2")})
    protected Queue collection6;

    @XmlElement(name="referenced-id")
    @XmlIDREF
    @XmlList
    protected Queue<ReferencedObject> collection7;

    @XmlAttribute(name="attribute-referenced-id")
    @XmlIDREF
    public Queue<ReferencedObject> collection8;

    protected Queue<CoinEnum> collection9;

    @XmlAnyAttribute
    protected Map collection10;

    protected Queue<byte[]> collection11;

    @XmlAttribute
    protected Queue<String> collection12;

    @XmlElements({@XmlElement(name="collection13integer", type=Integer.class), @XmlElement(name="collection13string", type=String.class)})   
    protected Queue collection13;
    
    @XmlElements({@XmlElement(name="collection14integer", type=Integer.class), @XmlElement(name="collection14string", type=String.class, nillable=true)})   
    protected Queue collection14;

    @XmlElement
    protected List<ReferencedObject> referenced = new ArrayList<ReferencedObject>();

    public QueueHolder(){
    }

    public Queue<Integer> getCollection1() {
        return collection1;
    }
    public void setCollection1(Queue<Integer> collection1) {
        this.collection1 = collection1;
    }
    public Queue<String> getCollection2() {
        return collection2;
    }
    public void setCollection2(Queue<String> collection2) {
        this.collection2 = collection2;
    }
    public Queue<Object> getCollection3() {
        return collection3;
    }
    public void setCollection3(Queue<Object> collection3) {
        this.collection3 = collection3;
    }
    public Queue getCollection4() {
        return collection4;
    }
    public void setCollection4(Queue collection4) {
        this.collection4 = collection4;
    }
    public Queue<QueueHolder> getCollection5() {
        return collection5;
    }
    public void setCollection5(Queue<QueueHolder> collection5) {
        this.collection5 = collection5;
    }

     public Queue getCollection6() {
        return collection6;
    }

    public void setCollection6(Queue collection6) {
        this.collection6 = collection6;
    }

    public Queue<ReferencedObject> getCollection7() {
        return collection7;
    }

    public void setCollection7(Queue<ReferencedObject> collection7) {
        this.collection7 = collection7;
    }

    public Queue<ReferencedObject> getCollection8() {
        return collection8;
    }

    public void setCollection8(Queue<ReferencedObject> collection8) {
        this.collection8 = collection8;
    }

    public Queue<CoinEnum> getCollection9() {
        return collection9;
    }

    public void setCollection9(Queue<CoinEnum> collection9) {
        this.collection9 = collection9;
    }

    public Map getCollection10() {
        return collection10;
    }

    public void setCollection10(Map collection10) {
        this.collection10 = collection10;
    }

    public Queue<byte[]> getCollection11() {
        return collection11;
    }

    public void setCollection11(Queue<byte[]> collection11) {
        this.collection11 = collection11;
    }

    public Queue<String> getCollection12() {
        return collection12;
    }

    public void setCollection12(Queue<String> collection12) {
        this.collection12 = collection12;
    }
    
    public Queue getCollection13() {
        return collection13;
    }

    public void setCollection13(Queue collection13) {
        this.collection13 = collection13;
    }

    public List<ReferencedObject> getReferenced() {
        return referenced;
    }

    public void setReferenced(List<ReferencedObject> referenced) {
        this.referenced = referenced;
    }

    public boolean equals(Object compareObject){

         if(compareObject instanceof QueueHolder){
             QueueHolder compareCollectionHolder = ((QueueHolder)compareObject);
             return compareCollections(collection1, compareCollectionHolder.getCollection1())
                    && compareCollections(collection2, compareCollectionHolder.getCollection2())
                    && compareCollections(collection3, compareCollectionHolder.getCollection3())
                    && compareCollections(collection4, compareCollectionHolder.getCollection4())
                    && compareCollections(collection5, compareCollectionHolder.getCollection5())
                    && compareCollections(collection6, compareCollectionHolder.getCollection6())
                    && compareCollections(collection7, compareCollectionHolder.getCollection7())
                    && compareCollections(collection8, compareCollectionHolder.getCollection8())
                    && compareCollections(collection9, compareCollectionHolder.getCollection9())
                    && compareMaps(collection10, compareCollectionHolder.getCollection10())
                    && compareCollections(collection11, compareCollectionHolder.getCollection11())
                    && compareCollections(collection12, compareCollectionHolder.getCollection12())
                    && compareCollections(collection13, compareCollectionHolder.getCollection13())
                    ;
         }
         return false;
     }

    private boolean compareMaps(Map map1, Map map2) {
    	if(map1 == null){
    		return map2 == null;
    	}
    	if(map1.size() != map2.size()){
    		return false;
    	}
    	return map1.equals(map2);
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
    	if(obj1 == null & obj2 == null){
    		return true;
    	}
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
     	}else{
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
