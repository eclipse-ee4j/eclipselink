/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - February 25, 2013
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ListmyObject", namespace = "someNamespace")
@XmlType(name = "ListmyObject", namespace = "someNamespace")
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {

    /*
     * customer is using a List and specifies the Interface ImyObject that just contains
     * getters and setters
     */
    @XmlElements(value = { @XmlElement(name = "myObject", type = MyObject.class), @XmlElement(name = "otherObject", type = MyOtherObject.class) })
    protected List<MyInterface> myList = new ArrayList<MyInterface>();

    public List<MyInterface> getMyList() {
        return myList;
    }

    public void setMyList(List<MyInterface> m_listMyObject) {
        this.myList = m_listMyObject;
    }

    public boolean equals(Object obj){
        if(obj instanceof Root){
            Root compare = (Root)obj;
            return myList.equals(compare.myList);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return myList != null ? myList.hashCode() : 0;
    }
}
