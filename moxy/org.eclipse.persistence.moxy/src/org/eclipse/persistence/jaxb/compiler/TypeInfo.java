/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.jaxb.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleType;
import org.eclipse.persistence.internal.oxm.schema.model.TypeDefParticle;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;

import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlType;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b>Used to store meta data about JAXB 2.0 Annotated classes during schema and mapping generation processes.
 * 
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Store information about Class Name and the associated Schema Type name</li>
 * <li>Store information about Property Order for mapping and schema generation</li>
 * <li>Store information about XmlAdapters, XmlAccessType and other JAXB 2.0 annotation artifacts</li>
 * </ul>
 * 
 * @since Oracle TopLink 11.1.1.0.0
 * @author mmacivor
 * @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 * @see org.eclipse.persistence.jaxb.compiler.EnumTypeInfo
 * 
 */

public class TypeInfo {
    private static final String ATT = "@";
    private static final String TXT = "/text()";
    private XMLDescriptor descriptor;
    private ComplexType complexType;
    private boolean hasRootElement;
    private boolean hasElementRefs;
    private Schema schema;
    private SimpleType simpleType;
    private ArrayList<String> propOrder; // store as a collection so it can be added to if needed
    private String classNamespace;
    private String schemaTypeName;
    private TypeDefParticle compositor;
    private ArrayList<String> propertyNames;
    private ArrayList<Property> propertyList;// keep the keys in a list to preserve order
    private HashMap<String, Property> properties;
    private Property idProperty; // if there is an XmlID annotation, set the property for mappings gen
    private HashMap<String, JavaClass> adaptersByClass;
    private Helper helper;
    private String objectFactoryClassName;
    private String factoryMethodName;
    private String[] factoryMethodParamTypes;
    private Property xmlValueProperty;
    private boolean isMixed;
    private boolean isTransient;

    private boolean isPreBuilt;
    private boolean isPostBuilt;
    
    private boolean isSetXmlTransient;
    
    private List<String> xmlSeeAlso;
    private XmlRootElement xmlRootElement;
    private XmlType xmlType;
    private XmlAccessType xmlAccessType;
    private XmlAccessOrder xmlAccessOrder;

    public TypeInfo(Helper helper) {
        propertyNames = new ArrayList<String>();
        properties = new HashMap<String, Property>();
        propertyList = new ArrayList<Property>();
        adaptersByClass = new HashMap<String, JavaClass>();
        this.helper = helper;

        isSetXmlTransient = false;
        isPreBuilt = false;
        isPostBuilt = false;;
    }

    public XMLDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(XMLDescriptor desc) {
        // if there is an @XmlID annotation, we need to add
        // primary key field names to the descriptor
        if (isIDSet()) {
            String pkFieldName;
            if (helper.isAnnotationPresent(getIDProperty().getElement(), XmlAttribute.class)) {
                pkFieldName = ATT + getIDProperty().getSchemaName();
            } else { // assume element
                pkFieldName = getIDProperty().getSchemaName() + TXT;
            }
            desc.addPrimaryKeyFieldName(pkFieldName);
        }
        descriptor = desc;

        // TODO: do we need to relocate this code?
        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();

        if (classNamespace == null || classNamespace.equals("")) {
            schemaRef.setSchemaContext("/" + schemaTypeName);
        } else {
            String prefix = desc.getNonNullNamespaceResolver().resolveNamespaceURI(classNamespace);
            if (prefix != null && !prefix.equals("")) {
                schemaRef.setSchemaContext("/" + prefix + ":" + schemaTypeName);
            } else {
                String generatedPrefix = desc.getNonNullNamespaceResolver().generatePrefix();
                schemaRef.setSchemaContext("/" + generatedPrefix + ":" + schemaTypeName);
                desc.getNonNullNamespaceResolver().put(generatedPrefix, classNamespace);
            }
            schemaRef.setSchemaContextAsQName(new QName(classNamespace, schemaTypeName));
        }
        // the default type is complex; need to check for simple type case
        if (isEnumerationType() || (propertyNames.size() == 1 && helper.isAnnotationPresent(getProperties().get(propertyNames.get(0)).getElement(), XmlValue.class))) {
            schemaRef.setType(XMLSchemaReference.SIMPLE_TYPE);
        }
        descriptor.setSchemaReference(schemaRef);
    }

    public ComplexType getComplexType() {
        return complexType;
    }

    public void setComplexType(ComplexType type) {
        complexType = type;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }

    public void setSimpleType(SimpleType type) {
        simpleType = type;
    }

    public String[] getPropOrder() {
        if (propOrder == null) {
            return new String[0];
        }
        return propOrder.toArray(new String[propOrder.size()]);
    }
    
    public boolean isSetPropOrder() {
        return propOrder != null;
    }

    public void setPropOrder(String[] order) {
        if (order == null) {
            propOrder = null;
        } else if (order.length == 0) {
            propOrder = new ArrayList<String>();
        } else {
            propOrder = new ArrayList(order.length);
            for (String next : order) {
                propOrder.add(next);
            }
        }
    }

    public String getClassNamespace() {
        return classNamespace;
    }

    public void setClassNamespace(String namespace) {
        classNamespace = namespace;
    }

    public boolean isComplexType() {
        return complexType != null;
    }

    /**
     * Indicates mixed content
     */
    public boolean isMixed() {
        return isMixed;
    }

    /**
     * Set mixed content indicator
     */
    public void setMixed(boolean isMixed) {
        this.isMixed = isMixed;
    }

    public TypeDefParticle getCompositor() {
        return compositor;
    }

    public void setCompositor(TypeDefParticle particle) {
        compositor = particle;
    }
    
    public ArrayList<String> getPropertyNames() {
        return propertyNames;
    }

    /**
     * Return the TypeProperty 'idProperty'. This method will typically be used in conjunction with isIDSet method to determine if an @XmlID exists, and hence
     * 'idProperty' is non-null.
     * 
     * @return
     */
    public Property getIDProperty() {
        return idProperty;
    }

    public HashMap<String, Property> getProperties() {
        return properties;
    }

    public void addProperty(String name, Property property) {
        properties.put(name, property);
        propertyNames.add(name);
        propertyList.add(property);
    }

    /**
     * Sets the TypeProperty 'idProperty'. This indicates that an @XmlID annotation is set on a given field/method.
     * 
     * @return
     */
    public void setIDProperty(Property idProperty) {
        this.idProperty = idProperty;
    }

    public void setProperties(ArrayList<Property> properties) {
        if (properties != null) {
            for (int i = 0; i < properties.size(); i++) {
                Property next = properties.get(i);
                this.addProperty(next.getPropertyName(), next);
            }
        }
    }

    /**
     * Order the properties based on the XmlAccessOrder, if set.
     */
    public void orderProperties() {
        if (!isSetXmlAccessOrder()) {
            return;
        }
        if (xmlAccessOrder == XmlAccessOrder.ALPHABETICAL) {
            if (this.propertyNames != null) {
                Collections.sort(this.propertyNames);
            }
        }
    }

    public boolean isEnumerationType() {
        return false;
    }

    /**
     * Indicates if an @XmlID is set on a field/property. If so, the TypeProperty 'idProperty' will be non-null.
     * 
     * @return
     */
    public boolean isIDSet() {
        return idProperty != null;
    }

    public ArrayList<Property> getPropertyList() {
        return propertyList;
    }

    public String getSchemaTypeName() {
        return schemaTypeName;
    }

    public void setSchemaTypeName(String typeName) {
        schemaTypeName = typeName;
    }

    public void setSchema(Schema theSchema) {
        this.schema = theSchema;
    }

    public Schema getSchema() {
        return schema;
    }

    public JavaClass getAdapterClass(JavaClass boundType) {
        return getAdaptersByClass().get(boundType.getQualifiedName());
    }

    public JavaClass getAdapterClass(String boundTypeName) {
        return getAdaptersByClass().get(boundTypeName);
    }

    public HashMap<String, JavaClass> getAdaptersByClass() {
        return adaptersByClass;
    }

    public void addAdapterClass(JavaClass adapterClass, JavaClass boundType) {
        adaptersByClass.put(boundType.getQualifiedName(), adapterClass);
    }

    public void addAdapterClass(JavaClass adapterClass, String boundTypeName) {
        adaptersByClass.put(boundTypeName, adapterClass);
    }

    public boolean hasRootElement() {
        return hasRootElement;
    }

    public void setHasRootElement(boolean hasRoot) {
        hasRootElement = hasRoot;
    }

    public boolean hasElementRefs() {
        return hasElementRefs;
    }

    public void setHasElementRefs(boolean hasRefs) {
        this.hasElementRefs = hasRefs;
    }

    public String getObjectFactoryClassName() {
        return objectFactoryClassName;
    }

    public void setObjectFactoryClassName(String factoryClass) {
        this.objectFactoryClassName = factoryClass;
    }

    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public void setFactoryMethodName(String factoryMethod) {
        this.factoryMethodName = factoryMethod;
    }

    public String[] getFactoryMethodParamTypes() {
        return this.factoryMethodParamTypes;
    }

    public void setFactoryMethodParamTypes(String[] paramTypes) {
        this.factoryMethodParamTypes = paramTypes;
    }

    public Property getXmlValueProperty() {
        return xmlValueProperty;
    }

    public void setXmlValueProperty(Property xmlValueProperty) {
        this.xmlValueProperty = xmlValueProperty;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public java.util.List<Property> getNonTransientPropertiesInPropOrder() {
        java.util.List<Property> propertiesInOrder = new ArrayList<Property>();
        String[] propOrder = getPropOrder();
        if (propOrder.length == 0 || propOrder[0].equals("")) {
            ArrayList<String> propertyNames = getPropertyNames();
            for (int i = 0; i < propertyNames.size(); i++) {
                String nextPropertyKey = propertyNames.get(i);
                Property next = getProperties().get(nextPropertyKey);
                if (next != null && !next.isTransient()) {
                    propertiesInOrder.add(next);
                }
            }
        } else {
            ArrayList<String> propertyNamesCopy = new ArrayList<String>(getPropertyNames());
            for (int i = 0; i < propOrder.length; i++) {
                // generate mappings based on the propOrder.
                String propertyName = propOrder[i];
                Property next = getProperties().get(propertyName);
                if (next != null && !next.isTransient()) {
                    propertyNamesCopy.remove(propertyName);
                    propertiesInOrder.add(next);
                }
            }
            // attributes may not be in the prop order in which case we need to generate those mappings also
            for (int i = 0; i < propertyNamesCopy.size(); i++) {
                String nextPropertyKey = propertyNamesCopy.get(i);
                Property next = getProperties().get(nextPropertyKey);
                if (next != null && !next.isTransient()) {
                    propertiesInOrder.add(next);
                }
            }
        }
        return propertiesInOrder;
    }

    public boolean isSetXmlTransient() {
        return isSetXmlTransient;
    }

    public boolean isSetXmlSeeAlso() {
        return xmlSeeAlso != null;
    }

    public void setXmlTransient(boolean isTransient) {
        isSetXmlTransient = true;
        setTransient(isTransient);
    }
    
    public List<String> getXmlSeeAlso() {
        return xmlSeeAlso;
    }
    
    public void setXmlSeeAlso(List<String> xmlSeeAlso) {
        this.xmlSeeAlso = xmlSeeAlso;
    }
    
    public boolean isSetXmlRootElement() {
        return xmlRootElement != null;
    }
    
    public XmlRootElement getXmlRootElement() {
        return xmlRootElement;
    }

    public void setXmlRootElement(XmlRootElement xmlRootElement) {
        this.xmlRootElement = xmlRootElement;
    }
    
    public boolean isSetXmlType() {
        return xmlType != null;
    }

    public XmlType getXmlType() {
        return xmlType;
    }

    public void setXmlType(XmlType xmlType) {
        this.xmlType = xmlType;
    }
    
    public boolean isSetXmlAccessType() {
        return xmlAccessType != null;
    }
    
    public XmlAccessType getXmlAccessType() {
        return xmlAccessType;
    }
    
    public void setXmlAccessType(XmlAccessType xmlAccessType) {
        this.xmlAccessType = xmlAccessType;
    }
    
    public boolean isSetXmlAccessOrder() {
        return xmlAccessOrder != null;
    }
    
    public XmlAccessOrder getXmlAccessOrder() {
        return xmlAccessOrder;
    }
    
    public void setXmlAccessOrder(XmlAccessOrder xmlAccessOrder) {
        this.xmlAccessOrder = xmlAccessOrder;
    }

    public boolean isPreBuilt() {
        return isPreBuilt;
    }
    
    public void setPreBuilt(boolean isPreBuilt) {
        this.isPreBuilt = isPreBuilt;
    }

    public boolean isPostBuilt() {
        return isPostBuilt;
    }
    
    public void setPostBuilt(boolean isPostBuilt) {
        this.isPostBuilt = isPostBuilt;
    }
}
