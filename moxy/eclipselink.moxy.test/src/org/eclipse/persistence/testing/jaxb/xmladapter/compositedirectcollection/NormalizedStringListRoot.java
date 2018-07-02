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
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"elementField", "elementProperty"})
public class NormalizedStringListRoot {

    @XmlElement(name="field")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    List<String> elementField;

    private List<String> elementProperty;

    @XmlElement(name="property")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public List<String> getElementProperty() {
        return elementProperty;
    }

    public void setElementProperty(List<String> elementProperty) {
        this.elementProperty = elementProperty;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        NormalizedStringListRoot test = (NormalizedStringListRoot) obj;
        if(!equals(elementField, test.elementField)) {
            return false;
        }
        if(!equals(elementProperty, test.elementProperty)) {
            return false;
        }
        return true;
    }

    private boolean equals(List<String> control, List<String> test) {
        if(null == control) {
            return null == test;
        }
        if(control.size() != test.size()) {
           return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!equals(control.get(x), test.get(x))) {
                return false;
            }
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
