/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.5.1 - Initial Implementation
 ******************************************************************************/
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
    
    @XmlVariableNode("name")
    @XmlElementWrapper(name="properties")
    private Map<String, Property> properties;
    
    @XmlVariableNode("name")
    @XmlElementWrapper(name="definitions")
    private Map<String, Property> definitions;
    
    @XmlElement(name="item")
    private JsonSchema item;
    
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


}
