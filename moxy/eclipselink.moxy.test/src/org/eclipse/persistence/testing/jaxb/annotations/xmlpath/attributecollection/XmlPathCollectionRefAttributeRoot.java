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
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="root")
public class XmlPathCollectionRefAttributeRoot {

    private List<XmlPathCollectionRefAttributeChild> element = new ArrayList<XmlPathCollectionRefAttributeChild>(2);
    private List<XmlPathCollectionRefAttributeChild> attribute = new ArrayList<XmlPathCollectionRefAttributeChild>(2);
    private List<XmlPathCollectionRefAttributeChild> xpathAttribute = new ArrayList<XmlPathCollectionRefAttributeChild>(2);
    private List<XmlPathCollectionRefAttributeChild> xpathAttributeList = new ArrayList<XmlPathCollectionRefAttributeChild>(2);

    public List<XmlPathCollectionRefAttributeChild> getElement() {
        return element;
    }

    public void setElement(List<XmlPathCollectionRefAttributeChild> element) {
        this.element = element;
    }

    @XmlAttribute
    @XmlIDREF
    public List<XmlPathCollectionRefAttributeChild> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<XmlPathCollectionRefAttributeChild> attribute) {
        this.attribute = attribute;
    }

    @XmlPath("attribute1/@id")
    @XmlIDREF
    public List<XmlPathCollectionRefAttributeChild> getXpathAttribute() {
        return xpathAttribute;
    }

    public void setXpathAttribute(
            List<XmlPathCollectionRefAttributeChild> xpathAttribute) {
        this.xpathAttribute = xpathAttribute;
    }

    @XmlPath("attribute2/@ids")
    @XmlIDREF
    @XmlList
    public List<XmlPathCollectionRefAttributeChild> getXpathAttributeList() {
        return xpathAttributeList;
    }

    public void setXpathAttributeList(
            List<XmlPathCollectionRefAttributeChild> xpathAttributeList) {
        this.xpathAttributeList = xpathAttributeList;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        XmlPathCollectionRefAttributeRoot test = (XmlPathCollectionRefAttributeRoot) obj;
        if(!equals(this.attribute, test.attribute)) {
            return false;
        }
        if(!equals(this.element, test.element)) {
            return false;
        }
        if(!equals(this.xpathAttribute, test.xpathAttribute)) {
            return false;
        }
        if(!equals(this.xpathAttributeList, test.xpathAttributeList)) {
            return false;
        }
        return true;
    }

    private boolean equals(List<XmlPathCollectionRefAttributeChild> control, List<XmlPathCollectionRefAttributeChild> test) {
        if(control.size() != test.size()) {
            return false;
        }
        for(int x=0, size=control.size(); x<size; x++) {
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
