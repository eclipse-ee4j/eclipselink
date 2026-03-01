/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.schema.model;

import org.eclipse.persistence.oxm.NamespaceResolver;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Schema {
    private String name;//non-persistant, used to give a schema an identifier
    private List<Import> imports;
    private List<Include> includes;
    private String targetNamespace;
    private String defaultNamespace;

    private boolean elementFormDefault;//error mapping in mw
    private boolean attributeFormDefault;//error mapping in mw
    private Map<String, SimpleType> topLevelSimpleTypes;
    private Map<String, ComplexType> topLevelComplexTypes;
    private Map<String, Element> topLevelElements;
    private Map<String, Attribute> topLevelAttributes;
    private NamespaceResolver namespaceResolver;
    private Map<QName, String> attributesMap;
    private Map<String, AttributeGroup> attributeGroups;
    private Map<String, Group> groups;
    private Annotation annotation;
    private Result result;

    public Schema() {
        namespaceResolver = new NamespaceResolver();
        imports = new ArrayList<>();
        includes = new ArrayList<>();
        //LinkedHashMaps are needed to force determinism of generated schemas.
        //It is important to generate always the same schema (e.g. with SchemaGenerator) with given input Schema.
        //Without LinkedHashMap it would be JDK dependent and the output schema would be different with different JDKs used.
        topLevelSimpleTypes = new LinkedHashMap<>();
        topLevelComplexTypes = new LinkedHashMap<>();
        topLevelElements = new LinkedHashMap<>();
        topLevelAttributes = new LinkedHashMap<>();
        attributesMap = new LinkedHashMap<>();
        attributeGroups = new LinkedHashMap<>();
        groups = new LinkedHashMap<>();
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getDefaultNamespace() {
        return this.defaultNamespace;
    }

    public void setTopLevelSimpleTypes(Map<String, SimpleType> topLevelSimpleTypes) {
        this.topLevelSimpleTypes = topLevelSimpleTypes;
    }

    public Map<String, SimpleType> getTopLevelSimpleTypes() {
        return topLevelSimpleTypes;
    }

    public void addTopLevelSimpleTypes(SimpleType simpleType) {
        topLevelSimpleTypes.put(simpleType.getName(), simpleType);
    }

    public void setTopLevelComplexTypes(Map<String, ComplexType> topLevelComplexTypes) {
        this.topLevelComplexTypes = topLevelComplexTypes;
    }

    public Map<String, ComplexType> getTopLevelComplexTypes() {
        return topLevelComplexTypes;
    }

    public void addTopLevelComplexTypes(ComplexType complexType) {
        topLevelComplexTypes.put(complexType.getName(), complexType);
    }

    public void setTopLevelElements(Map<String, Element> topLevelElements) {
        this.topLevelElements = topLevelElements;
    }

    public Map<String, Element> getTopLevelElements() {
        return topLevelElements;
    }

    public void addTopLevelElement(Element element) {
        topLevelElements.put(element.getName(), element);
    }

    public void setElementFormDefault(boolean elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    public boolean isElementFormDefault() {
        return elementFormDefault;
    }

    public void setAttributeFormDefault(boolean attributeFormDefault) {
        this.attributeFormDefault = attributeFormDefault;
    }

    public boolean isAttributeFormDefault() {
        return attributeFormDefault;
    }

    public void setTopLevelAttributes(Map<String, Attribute> topLevelAttributes) {
        this.topLevelAttributes = topLevelAttributes;
    }

    public Map<String, Attribute> getTopLevelAttributes() {
        return topLevelAttributes;
    }

    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public void setImports(List<Import> imports) {
        this.imports = imports;
    }

    public List<Import> getImports() {
        return imports;
    }

    public void setIncludes(List<Include> includes) {
        this.includes = includes;
    }

    public List<Include> getIncludes() {
        return includes;
    }

    public void setAttributesMap(Map<QName, String> attributesMap) {
        this.attributesMap = attributesMap;
        Iterator<Entry<QName, String>> iter = attributesMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<QName, String> nextEntry = iter.next();
            QName key = nextEntry.getKey();
            if (key.getNamespaceURI().equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                String value = nextEntry.getValue();
                String prefix = key.getLocalPart();
                int index = prefix.indexOf(':');
                if (index > -1) {
                    prefix = prefix.substring(index + 1, prefix.length());
                }
                namespaceResolver.put(prefix, value);
            }
        }
    }

    public Map<QName, String> getAttributesMap() {
        return attributesMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAttributeGroups(Map<String, AttributeGroup> attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    public Map<String, AttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    public AttributeGroup getAttributeGroup(String uri, String localName) {
        if (uri.equals(targetNamespace)) {
            AttributeGroup globalAttributeGroup = getAttributeGroups().get(localName);
            if (globalAttributeGroup != null) {
                return globalAttributeGroup;
            }
        }
        return getAttributeGroupFromReferencedSchemas(uri, localName);
    }

    protected AttributeGroup getAttributeGroupFromReferencedSchemas(String uri, String localName) {
        AttributeGroup globalAttributeGroup = null;
        Iterator<Include> iter = getIncludes().iterator();
        while (iter.hasNext() && (globalAttributeGroup == null)) {
            Schema includedSchema = iter.next().getSchema();
            globalAttributeGroup = includedSchema.getAttributeGroup(uri, localName);
        }
        if (globalAttributeGroup == null) {
            Iterator<Import> iter2 = getImports().iterator();
             while (iter2.hasNext() && (globalAttributeGroup == null)) {
                Schema importedSchema = iter2.next().getSchema();
                globalAttributeGroup = importedSchema.getAttributeGroup(uri, localName);
            }
        }
        return globalAttributeGroup;
    }

    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }

    public Map<String, Group> getGroups() {
        return groups;
    }

    public Group getGroup(String uri, String localName) {
        if (uri.equals(targetNamespace)) {
            Group globalGroup = getGroups().get(localName);
            if (globalGroup != null) {
                return globalGroup;
            }
        }
        return getGroupFromReferencedSchemas(uri, localName);
    }

    protected Group getGroupFromReferencedSchemas(String uri, String localName) {
        Group globalGroup = null;
        Iterator<Include> iter = getIncludes().iterator();
        while (iter.hasNext() && (globalGroup == null)) {
            Schema includedSchema = iter.next().getSchema();
            globalGroup = includedSchema.getGroup(uri, localName);
        }
        if (globalGroup == null) {
            Iterator<Import> iter2 = getImports().iterator();
            while (iter2.hasNext() && (globalGroup == null)) {
                Schema importedSchema = iter2.next().getSchema();
                globalGroup = importedSchema.getGroup(uri, localName);
            }
        }
        return globalGroup;
    }

    /**
     * Return the Result for this Schema.  This will typically be set
     * after a call to SchemaOutputResolver.createOutput().
     *
     * @return the Result for this instance, or null if not set
     */
    public Result getResult() {
        return result;
    }

    /**
     * Set the Result for this Schema.  This method will typically be
     * called after a call to SchemaOutputResolver.createOutput().
     *
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Indicates if a Result has been set for this Schema.
     *
     * @return true if a Result has been set, false otherwise
     */
    public boolean hasResult() {
        return getResult() != null;
    }

    /**
     * Indicates if this Schema has a Result, and that Result has
     * a non-null systemID.
     *
     * @return true if this Schema has a non-null Result has a
     *         non-null systemID.
     */
    public boolean hasSystemId() {
        return getSystemId() != null;
    }

    /**
     * Get the SystemId for this Schema.  This value will typically be
     * used as the schemaLocation in an import statement.
     *
     * @return the systemID set on this Schema's Result object if both
     *         the Result and the Result's systemID are non-null,
     *         otherwise null
     */
    public String getSystemId() {
        if (hasResult()) {
            return getResult().getSystemId();
        }
        return null;
    }
}
