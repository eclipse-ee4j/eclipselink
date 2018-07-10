/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"elementField", "elementProperty"})
public class CollapsedStringRoot {

    @XmlAttribute(name="field")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    String attributeField;

    @XmlElement(name="field")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    String elementField;

    private String attributeProperty;
    private String elementProperty;

    @XmlAttribute(name="property")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public String getAttributeProperty() {
        return attributeProperty;
    }

    public void setAttributeProperty(String attributeProperty) {
        this.attributeProperty = attributeProperty;
    }

    @XmlElement(name="property")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public String getElementProperty() {
        return elementProperty;
    }

    public void setElementProperty(String elementProperty) {
        this.elementProperty = elementProperty;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        CollapsedStringRoot test = (CollapsedStringRoot) obj;
        if(!equals(attributeField, test.attributeField)) {
            return false;
        }
        if(!equals(elementField, test.elementField)) {
            return false;
        }
        if(!equals(attributeProperty, test.attributeProperty)) {
            return false;
        }
        if(!equals(elementProperty, test.elementProperty)) {
            return false;
        }
        return true;
    }

    private boolean equals(String control, String test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
