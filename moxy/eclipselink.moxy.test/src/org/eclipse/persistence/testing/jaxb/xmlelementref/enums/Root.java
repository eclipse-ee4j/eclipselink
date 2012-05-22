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
package org.eclipse.persistence.testing.jaxb.xmlelementref.enums;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    private JAXBElement<?> stringOrEnumSingle;
    private List<JAXBElement<?>> stringOrEnum = new ArrayList<JAXBElement<?>>();

    @XmlElementWrapper(name="collection")
    @XmlElementRefs({
        @XmlElementRef(name="string", type=JAXBElement.class),
        @XmlElementRef(name="enum", type=JAXBElement.class)
    })
    public List<JAXBElement<?>> getStringOrEnum() {
        return stringOrEnum;
    }

    public void setStringOrEnum(List<JAXBElement<?>> stringOrEnum) {
        this.stringOrEnum = stringOrEnum;
    }

    @XmlElementRefs({
        @XmlElementRef(name="string", type=JAXBElement.class),
        @XmlElementRef(name="enum", type=JAXBElement.class)
    })
    public JAXBElement<?> getStringOrEnumSingle() {
        return stringOrEnumSingle;
    }

    public void setStringOrEnumSingle(JAXBElement<?> stringOrEnumSingle) {
        this.stringOrEnumSingle = stringOrEnumSingle;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Root test = (Root) obj;
        if(!stringOrEnumSingle.getValue().equals(test.getStringOrEnumSingle().getValue())) {
            return false;
        }
        if(!equals(stringOrEnum, test.getStringOrEnum())) {
            return false;
        }
        return true;
    }

    private boolean equals(List<JAXBElement<?>> control, List<JAXBElement<?>> test) {
        if(null == control) {
            return null == test;
        } else if(null == test) {
            return null == control;
        } else if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!control.get(x).getValue().equals(test.get(x).getValue())) {
                return false;
            }
        }
        return true;
    }

}