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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.eclipse.persistence.oxm.NamespaceResolver;

public class Schema {
    private String name;//non-persistant, used to give a schema an identifier
    private java.util.List imports;
    private java.util.List includes;
    private String targetNamespace;
    private String defaultNamespace;

    private boolean elementFormDefault;//error mapping in mw
    private boolean attributeFormDefault;//error mapping in mw
    private Map topLevelSimpleTypes;
    private Map topLevelComplexTypes;
    private Map topLevelElements;
    private Map topLevelAttributes;
    private NamespaceResolver namespaceResolver;
    private Map attributesMap;
    private Map attributeGroups;
    private Map groups;
    private Annotation annotation;
    private Result result;

    public Schema() {
        namespaceResolver = new NamespaceResolver();
        imports = new ArrayList();
        includes = new ArrayList();
        topLevelSimpleTypes = new HashMap();
        topLevelComplexTypes = new HashMap();
        topLevelElements = new HashMap();
        topLevelAttributes = new HashMap();
        attributesMap = new HashMap();
        attributeGroups = new HashMap();
        groups = new HashMap();
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
    public void setTopLevelSimpleTypes(Map topLevelSimpleTypes) {
        this.topLevelSimpleTypes = topLevelSimpleTypes;
    }

    public Map getTopLevelSimpleTypes() {
        return topLevelSimpleTypes;
    }

    public void addTopLevelSimpleTypes(SimpleType simpleType) {
        topLevelSimpleTypes.put(simpleType.getName(), simpleType);
    }

    public void setTopLevelComplexTypes(Map topLevelComplexTypes) {
        this.topLevelComplexTypes = topLevelComplexTypes;
    }

    public Map getTopLevelComplexTypes() {
        return topLevelComplexTypes;
    }

    public void addTopLevelComplexTypes(ComplexType complexType) {
        topLevelComplexTypes.put(complexType.getName(), complexType);
    }

    public void setTopLevelElements(Map topLevelElements) {
        this.topLevelElements = topLevelElements;
    }

    public Map getTopLevelElements() {
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

    public void setTopLevelAttributes(Map topLevelAttributes) {
        this.topLevelAttributes = topLevelAttributes;
    }

    public Map getTopLevelAttributes() {
        return topLevelAttributes;
    }

    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public void setImports(java.util.List imports) {
        this.imports = imports;
    }

    public java.util.List getImports() {
        return imports;
    }

    public void setIncludes(java.util.List includes) {
        this.includes = includes;
    }

    public java.util.List getIncludes() {
        return includes;
    }

    public void setAttributesMap(Map attributesMap) {
        this.attributesMap = attributesMap;
        Iterator<Entry> iter = attributesMap.entrySet().iterator();       
        while (iter.hasNext()) {
        	Entry nextEntry = iter.next();
            QName key = (QName)nextEntry.getKey();
            if (key.getNamespaceURI().equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                String value = (String)nextEntry.getValue();
                String prefix = key.getLocalPart();
                int index = prefix.indexOf(':');
                if (index > -1) {
                    prefix = prefix.substring(index + 1, prefix.length());
                }
                namespaceResolver.put(prefix, value);
            }
        }
    }

    public Map getAttributesMap() {
        return attributesMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAttributeGroups(Map attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    public Map getAttributeGroups() {
        return attributeGroups;
    }

    public AttributeGroup getAttributeGroup(String uri, String localName) {
        AttributeGroup globalAttributeGroup = null;
        if (uri.equals(targetNamespace)) {
            globalAttributeGroup = (AttributeGroup)getAttributeGroups().get(localName);
            if (globalAttributeGroup != null) {
                return globalAttributeGroup;
            }
        }
        globalAttributeGroup = getAttributeGroupFromReferencedSchemas(uri, localName);

        return globalAttributeGroup;
    }

    protected AttributeGroup getAttributeGroupFromReferencedSchemas(String uri, String localName) {
        AttributeGroup globalAttributeGroup = null;
        Iterator iter = getIncludes().iterator();
        while (iter.hasNext() && (globalAttributeGroup == null)) {
            Schema includedSchema = ((Include)iter.next()).getSchema();
            globalAttributeGroup = includedSchema.getAttributeGroup(uri, localName);
        }
        if (globalAttributeGroup == null) {
            iter = getImports().iterator();
             while (iter.hasNext() && (globalAttributeGroup == null)) {
                Schema importedSchema = ((Import)iter.next()).getSchema();
                globalAttributeGroup = importedSchema.getAttributeGroup(uri, localName);
            }                    
        }
        return globalAttributeGroup;
    }

    public void setGroups(Map groups) {
        this.groups = groups;
    }

    public Map getGroups() {
        return groups;
    }

    public Group getGroup(String uri, String localName) {
        Group globalGroup = null;
        if (uri.equals(targetNamespace)) {
            globalGroup = (Group)getGroups().get(localName);
            if (globalGroup != null) {
                return globalGroup;
            }
        }
        globalGroup = getGroupFromReferencedSchemas(uri, localName);

        return globalGroup;
    }

    protected Group getGroupFromReferencedSchemas(String uri, String localName) {
        Group globalGroup = null;        
        Iterator iter = getIncludes().iterator();
        while (iter.hasNext() && (globalGroup == null)) {
            Schema includedSchema = ((Include)iter.next()).getSchema();
            globalGroup = includedSchema.getGroup(uri, localName);
        }
        if (globalGroup == null) {            
            iter = getImports().iterator();
            while (iter.hasNext() && (globalGroup == null)) {
                Schema importedSchema = ((Import)iter.next()).getSchema();
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
     * @param result
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
