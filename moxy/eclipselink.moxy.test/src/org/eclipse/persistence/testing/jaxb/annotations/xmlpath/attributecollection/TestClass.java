/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name = "test")
@XmlAccessorType(XmlAccessType.NONE)
public class TestClass {

    private ArrayList<String> itemList = new ArrayList<String>();
    private ArrayList<String> attributeList = new ArrayList<String>();
    private ArrayList<String> elementList = new ArrayList<String>();

    @XmlPath("item/@type")
    public ArrayList<String> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
    }

    @XmlPath("attribute/@list")
    @XmlList
    public ArrayList<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(ArrayList<String> attributeList) {
        this.attributeList = attributeList;
    }

    @XmlPath("element/list/text()")
    @XmlList
    public ArrayList<String> getElementList() {
        return elementList;
    }

    public void setElementList(ArrayList<String> elementList) {
        this.elementList = elementList;
    }

    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        TestClass testObj = (TestClass) obj;
        if(!equals(this.attributeList, testObj.attributeList)) {
            return false;
        }
        if(!equals(this.elementList, testObj.elementList)) {
            return false;
        }
        if(!equals(this.itemList, testObj.itemList)) {
            return false;
        }
        return true;
    }

    private boolean equals(List<String> control, List<String> test) {
        if(null == control) {
            return null == test;
        } else if(null == test) {
            return null == control;
        } else if(control.size() != test.size()) {
            return false;
        }
        for(int x=0,size=control.size(); x<size; x++) {
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
