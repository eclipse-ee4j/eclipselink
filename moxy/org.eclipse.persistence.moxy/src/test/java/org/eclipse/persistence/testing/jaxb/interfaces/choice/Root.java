/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - February 25, 2013
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
