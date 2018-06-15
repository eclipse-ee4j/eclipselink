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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.persistence.internal.jaxb.json.schema.JsonSchemaGenerator;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>
 * This class is the root of a JAXB model representing a JSON Schema. An instance is created
 * by the JsonSchemaGenerator based on an EclipseLink project and marshalled out using EclipseLink
 * to create a JsonSchema.
 *
 * @see JsonSchemaGenerator
 * @author mmacivor
 *
 */
public class JsonSchema {

    @XmlElement(name="$schema")
    private String schemaVersion = "http://json-schema.org/draft-04/schema#";

    @XmlElement(name="title")
    private String title;

    @XmlElement(name="type")
    private JsonType type;

    @XmlElement(name="anyOf")
    private Property[] anyOf;

    @XmlElement(name="enumeration")
    private List<String> enumeration;

    @XmlVariableNode("name")
    @XmlElementWrapper(name="properties")
    private Map<String, Property> properties;

    @XmlElement(name="items")
    private Property items;

    @XmlElement(name="additionalProperties")
    private Boolean additionalProperties = null;

    @XmlVariableNode("name")
    @XmlElementWrapper(name="definitions")
    private Map<String, Property> definitions;



    private List<String> required;


    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(JsonType type) {
        this.type = type;
    }

    public void addProperty(Property property) {
        this.getProperties().put(property.getName(), property);

    }

    public Map<String, Property> getProperties() {
        if(properties == null) {
            properties = new LinkedHashMap<String, Property>();
        }
        return properties;
    }

    public void setProperties(Map<String, Property> props) {
        this.properties = props;
    }

    public Map<String, Property> getDefinitions() {
        if(definitions == null) {
            definitions = new LinkedHashMap<String, Property>();
        }
        return definitions;
    }


    public Property getProperty(String name) {
        if(properties == null) {
            return null;
        }
        return properties.get(name);
    }

    public Property getItems() {
        return items;
    }

    public void setItems(Property items) {
        this.items = items;
    }

    public Boolean isAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAnyOf(Property[] anyOf) {
        this.anyOf = anyOf;
     }


     public Property[] getAnyOf() {
         return anyOf;
     }

    public List<String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration) {
        this.enumeration = enumeration;
    }
}
