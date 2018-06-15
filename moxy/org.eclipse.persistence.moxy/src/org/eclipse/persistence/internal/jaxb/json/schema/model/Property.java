/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.internal.jaxb.json.schema.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;


@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

    @XmlTransient
    private String name;

    @XmlElement(name="type")
    private JsonType type;

    @XmlVariableNode("name")
    @XmlElementWrapper(name="properties")
    private Map<String, Property> properties;

    @XmlElement(name="additionalProperties")
    private Boolean additionalProperties = null;

    @XmlElement(name="items")
    private Property item;

    @XmlElement(name="enum")
    private List<String> enumeration;

    @XmlElement(name="$ref")
    private String ref;

    @XmlElement(name="anyOf")
    private Property[] anyOf;

    public void setType(JsonType type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;

    }

    public Map<String, Property> getProperties() {
        if(this.properties == null) {
            this.properties = new LinkedHashMap<String, Property>();
        }
        return this.properties;
    }

    public Property getItem() {
        return item;
    }

    public void setItem(Property item) {
        this.item = item;
    }

    public List<String> getEnumeration() {
        if(enumeration == null) {
            enumeration = new ArrayList<String>();
        }
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration) {
        this.enumeration = enumeration;
    }

    public Property getProperty(String name) {
        if(properties != null) {
            return properties.get(name);
        }
        return null;
    }


    public String getRef() {
        return ref;
    }


    public void setRef(String ref) {
        this.ref = ref;
    }


    public void setAnyOf(Property[] anyOf) {
       this.anyOf = anyOf;
    }


    public Property[] getAnyOf() {
        return anyOf;
    }

    public Boolean isAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
