/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollectionHolderNillable {

    @XmlElement(nillable = true)
    protected List<Integer> collection1;

    @XmlList
    protected List<String> collection2;

    @XmlElement(nillable = true)
    protected List<Object> collection3;

    @XmlAnyElement
    protected List collection4;

    @XmlElement(nillable = true)
    protected List<CollectionHolderNillable> collection5;

    @XmlElementRefs ({@XmlElementRef(name="root2"), @XmlElementRef(name="root")})
    protected List collection6;

    @XmlElement(name="referenced-id", nillable= true)
    @XmlIDREF
    @XmlList
    protected List<ReferencedObject> collection7;

    @XmlAttribute(name="attribute-referenced-id")
    @XmlIDREF
    public List<ReferencedObject> collection8;

    @XmlElement(nillable = true)
    protected List<CoinEnum> collection9;

    @XmlAnyAttribute
    protected Map collection10;

    @XmlElement(nillable = true)
    protected List<byte[]> collection11;

    public CollectionHolderNillable(){
    }

    public Map getCollection10() {
        return collection10;
    }

    public void setCollection10(Map collection10) {
        this.collection10 = collection10;
    }
    public boolean equals(Object compareObject){

         if(compareObject instanceof CollectionHolderNillable){
             CollectionHolderNillable compareCollectionHolder = ((CollectionHolderNillable)compareObject);
             return compareCollections(collection1, compareCollectionHolder.collection1)
                    && compareCollections(collection2, compareCollectionHolder.collection2)
                    && compareCollections(collection3, compareCollectionHolder.collection3)
                    && compareCollections(collection4, compareCollectionHolder.collection4)
                    && compareCollections(collection5, compareCollectionHolder.collection5)
                    && compareCollections(collection6, compareCollectionHolder.collection6)
                    && compareCollections(collection7, compareCollectionHolder.collection7)
                    && compareCollections(collection8, compareCollectionHolder.collection8)
                    && compareCollections(collection9, compareCollectionHolder.collection9)
                    && compareMaps(collection10, compareCollectionHolder.getCollection10())
                    && compareCollections(collection11, compareCollectionHolder.collection11)
                    ;
         }
         return false;
     }

    private boolean compareMaps(Map map1, Map map2){
        if(map1 == null){
            return map2 == null;
        }else{
            if(map2 == null){
                return false;
            }

            return map1.equals(map2);
        }
    }

     private boolean compareCollections(Collection compareList1, Collection compareList2){
         if(compareList1 == null){
             return compareList2 == null;
         }else{
             if(compareList2 == null){
                 return false;
             }
             if(compareList1.size() != compareList2.size()){
                 return false;
             }
             Iterator iter1 = compareList1.iterator();
             Iterator iter2 = compareList2.iterator();

             while(iter1.hasNext()){
                 if(!(compareItems(iter1.next(), iter2.next()))){
                     return false;
                 }
             }
             return true;
         }

     }

     private boolean compareItems(Object item1, Object item2){

         if(item1 instanceof JAXBElement){
             if(!(item2 instanceof JAXBElement)){
                 return false;
             }
             return compareJAXBElements((JAXBElement)item1, (JAXBElement)item2);
         }else{
             if(item1 == null){
                 if(item2 != null){
                     return false;
                 }
                 return true;
             }else{
                 return item1.equals(item2);
             }
         }
     }

     private boolean  compareJAXBElements(JAXBElement item1, JAXBElement item2){
         if(!(item1.getName().getLocalPart().equals(item2.getName().getLocalPart()))){
             return false;
         }
         if(item1.getValue() == null){
             if(item2.getValue() != null){
                 return false;
             }
         }else if(!(item1.equals(item2))){
             return false;
         }
         return true;
     }
}
