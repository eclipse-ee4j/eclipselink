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
// rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.NONE)
public class PhoneNumber {

    private int id;
    private int areaCode;
    private int number;
    private String type;

    @XmlTransient
    private Map<String, Object> extensions;

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaCode() {
        return areaCode;
    }

    @XmlElement(name="area-code")
    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    @XmlElement
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getExt(String name) {
        if (extensions == null) {
            extensions = new HashMap<String, Object>();
        }
        return extensions.get(name);
    }

    public void putExt(String name, Object value) {
        if (extensions == null) {
            extensions = new HashMap<String, Object>();
        }
        extensions.put(name, value);
    }

}
