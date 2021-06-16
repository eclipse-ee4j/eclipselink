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
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"elementField", "elementProperty"})
public class CollapsedStringListRoot {

    @XmlElement(name="field")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    List<String> elementField;

    private List<String> elementProperty;

    @XmlElement(name="property")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
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
        CollapsedStringListRoot test = (CollapsedStringListRoot) obj;
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
