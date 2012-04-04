/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.List;
import java.util.Map;

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
public class CollectionHolder {

    protected List<Integer> collection1;

    @XmlList
    protected List<String> collection2;

    protected List<Object> collection3;

    @XmlAnyElement
    protected List collection4;

    protected List<CollectionHolder> collection5;

    @XmlElementRefs({@XmlElementRef(name="root"), @XmlElementRef(name="root2")})
    protected List collection6;

    @XmlElement(name="referenced-id")
    @XmlIDREF
    @XmlList
    protected List<ReferencedObject> collection7;

    @XmlAttribute(name="attribute-referenced-id")
    @XmlIDREF
    public List<ReferencedObject> collection8;

    protected List<CoinEnum> collection9;

    @XmlAnyAttribute
    protected Map collection10;

    protected List<byte[]> collection11;

    @XmlAttribute
    protected List<String> collection12;

    public CollectionHolder(){
    }

    public List<Integer> getCollection1() {
        return collection1;
    }
    public void setCollection1(List<Integer> collection1) {
        this.collection1 = collection1;
    }
    public List<String> getCollection2() {
        return collection2;
    }
    public void setCollection2(List<String> collection2) {
        this.collection2 = collection2;
    }
    public List<Object> getCollection3() {
        return collection3;
    }
    public void setCollection3(List<Object> collection3) {
        this.collection3 = collection3;
    }
    public List getCollection4() {
        return collection4;
    }
    public void setCollection4(List collection4) {
        this.collection4 = collection4;
    }
    public List<CollectionHolder> getCollection5() {
        return collection5;
    }
    public void setCollection5(List<CollectionHolder> collection5) {
        this.collection5 = collection5;
    }

     public List getCollection6() {
        return collection6;
    }

    public void setCollection6(List collection6) {
        this.collection6 = collection6;
    }

    public List<ReferencedObject> getCollection7() {
        return collection7;
    }

    public void setCollection7(List<ReferencedObject> collection7) {
        this.collection7 = collection7;
    }

    public List<ReferencedObject> getCollection8() {
        return collection8;
    }

    public void setCollection8(List<ReferencedObject> collection8) {
        this.collection8 = collection8;
    }

    public List<CoinEnum> getCollection9() {
        return collection9;
    }

    public void setCollection9(List<CoinEnum> collection9) {
        this.collection9 = collection9;
    }

    public Map getCollection10() {
        return collection10;
    }

    public void setCollection10(Map collection10) {
        this.collection10 = collection10;
    }

    public List<byte[]> getCollection11() {
        return collection11;
    }

    public void setCollection11(List<byte[]> collection11) {
        this.collection11 = collection11;
    }

    public List<String> getCollection12() {
        return collection12;
    }

    public void setCollection12(List<String> collection12) {
        this.collection12 = collection12;
    }
    
    public boolean equals(Object compareObject){

         if(compareObject instanceof CollectionHolder){
             CollectionHolder compareCollectionHolder = ((CollectionHolder)compareObject);
             return compareCollections(collection1, compareCollectionHolder.getCollection1())
                    && compareCollections(collection2, compareCollectionHolder.getCollection2())
                    && compareCollections(collection3, compareCollectionHolder.getCollection3())
                    && compareCollections(collection4, compareCollectionHolder.getCollection4())
                    && compareCollections(collection5, compareCollectionHolder.getCollection5())
                    && compareCollections(collection6, compareCollectionHolder.getCollection6())
                    && compareCollections(collection7, compareCollectionHolder.getCollection7())
                    && compareCollections(collection8, compareCollectionHolder.getCollection8())
                    && compareCollections(collection9, compareCollectionHolder.getCollection9())
                    && compareCollections(collection10, compareCollectionHolder.getCollection10())
                    && compareCollections(collection11, compareCollectionHolder.getCollection11())
                    && compareCollections(collection12, compareCollectionHolder.getCollection12())
                    ;
         }
         return false;
     }

     private boolean compareCollections(Object compareList1, Object compareList2){
         if(compareList1 == null){
             return compareList2 == null;
         }else{
             if(compareList2 == null){
                 return false;
             }
             return compareList1.equals(compareList2);
         }

     }

}
