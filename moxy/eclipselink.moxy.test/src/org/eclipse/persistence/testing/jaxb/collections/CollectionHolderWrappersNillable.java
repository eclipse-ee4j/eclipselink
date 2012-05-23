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

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CollectionHolderWrappersNillable {

    @XmlElementWrapper(nillable=true)
    protected List<Integer> collection1;

    @XmlElementWrapper(name="collection2-wrapper", nillable=true)
    protected List<Object> collection2;

    @XmlElementWrapper(name="collection3-wrapper", nillable=true)
    @XmlAnyElement
    protected List collection3;

    @XmlElementWrapper(name="collection4-wrapper", nillable=true)
    protected List<CollectionHolderWrappersNillable> collection4;

    @XmlElementWrapper(name="collection5-wrapper", nillable=true)
    @XmlElementRefs({@XmlElementRef(name="root"), @XmlElementRef(name="root2")})
    protected List<JAXBElement<String>> collection5;

    @XmlElementWrapper(name="collection6-wrapper", nillable=true)
    protected List<CoinEnum> collection6;

    @XmlElementWrapper(name="collection7-wrapper", nillable=true)
    protected List<byte[]> collection7;

    public CollectionHolderWrappersNillable() {
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

    public List<CollectionHolderWrappersNillable> getCollection4() {
        return collection4;
    }

    public void setCollection4(List<CollectionHolderWrappersNillable> collection4) {
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
         if (compareObject instanceof CollectionHolderWrappersNillable) {
             CollectionHolderWrappersNillable compareCollectionHolder = ((CollectionHolderWrappersNillable) compareObject);
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

    private boolean compareCollections(Object compareList1, Object compareList2) {
        if (compareList1 == null) {
            return compareList2 == null;
        } else {
            if (compareList2 == null) {
                return false;
            }
            return compareList1 == compareList2;
        }
    }

}
