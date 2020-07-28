/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.internal.jpa.rs.metadata.model.v2;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * This class describes an entity schema. Used in JPARS 2.0 metadata model.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
@XmlRootElement
@XmlType(propOrder = {"schema", "allOf", "title", "properties", "definitions", "links"})
public class ResourceSchema {
    /** Schema name **/
    @XmlElement(name="$schema")
    private String schema;

    /** Inheritance **/
    private List<Reference> allOf;

    /** Schema title **/
    private String title;

    /** List of entity properties **/
    @XmlElementWrapper(name="properties")
    @XmlAnyElement(lax=true)
    private List<JAXBElement> properties;

    /** Type definitions **/
    @XmlElementWrapper(name="definitions")
    @XmlAnyElement(lax=true)
    private List<JAXBElement> definitions;

    /** Links **/
    private List<LinkV2> links;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<Reference> getAllOf() {
        return allOf;
    }

    public void setAllOf(List<Reference> allOf) {
        this.allOf = allOf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<JAXBElement> getProperties() {
        return properties;
    }

    public void setProperties(List<JAXBElement> properties) {
        this.properties = properties;
    }

    public List<JAXBElement> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<JAXBElement> definitions) {
        this.definitions = definitions;
    }

    public List<LinkV2> getLinks() {
        return links;
    }

    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }

    public void addProperty(String name, Property property) {
        if (properties == null) {
            properties = new ArrayList<JAXBElement>();
        }
        properties.add(new JAXBElement<Property>(new QName(name), Property.class, property));
    }

    public void addDefinition(String name, ResourceSchema definition) {
        // Lazy initialization
        if (definitions == null) {
            definitions = new ArrayList<JAXBElement>(1);
        }
        definitions.add(new JAXBElement<ResourceSchema>(new QName(name), ResourceSchema.class, definition));
    }

    public void addAllOf(Reference reference) {
        // Lazy initialization
        if (allOf == null) {
            allOf = new ArrayList<Reference>(1);
        }
        allOf.add(reference);
    }
}
