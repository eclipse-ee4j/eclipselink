/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.internal.oxm.schema.model.*;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Used to store meta data about JAXB 2.0 Annotated classes during 
 * schema and mapping generation processes.
 * 
 * <p><b>Responsibilities:</b><ul>
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
    private Schema schema;
    private SimpleType simpleType;
    private ArrayList<String> propOrder; //store as a collection so it can be added to if needed
    private String classNamespace;
    private String schemaTypeName;
    private TypeDefParticle compositor;
    private XmlAccessType accessType;
    private ArrayList<String> propertyNames;
    private ArrayList<Property> propertyList;//keep the keys in a list to preserve order
    private HashMap<String, Property> properties;
    private Property idProperty;  // if there is an XmlID annotation, set the property for mappings gen
    private HashMap<String, JavaClass> adaptersByClass;
    private Helper helper;
    
    public TypeInfo(Helper helper) {
        propertyNames = new ArrayList<String>();
        properties = new HashMap<String, Property>();
        propertyList = new ArrayList<Property>();
        adaptersByClass = new HashMap<String, JavaClass>();
        this.helper = helper;
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
            } else {    // assume element
                pkFieldName = getIDProperty().getSchemaName() + TXT;
            }
            desc.addPrimaryKeyFieldName(pkFieldName);
        }
        descriptor = desc;
        
        // TODO: do we need to relocate this code? 
        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        schemaRef.setSchemaContext("/" + schemaTypeName);
        // the default type is complex;  need to check for simple type case
        if(isEnumerationType() || (propertyNames.size() == 1 && helper.isAnnotationPresent(getProperties().get(propertyNames.get(0)).getElement(), XmlValue.class))) {
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
        if(propOrder == null) {
            return new String[0];
        }
        return propOrder.toArray(new String[propOrder.size()]);
    }

    public void setPropOrder(String[] order) {
        if(order.length == 0 || order[0].equals("")) {
            propOrder = null;
        } else {
            propOrder = new ArrayList(order.length);
            for(String next: order) {
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
    
    public TypeDefParticle getCompositor() {
        return compositor;
    }
    public void setCompositor(TypeDefParticle particle) {
        compositor = particle;
    }
    
    public XmlAccessType getAccessType() {
        return accessType;
    }
    
    public void setAccessType(XmlAccessType type) {
        accessType = type;
    }
    
    public ArrayList<String> getPropertyNames() {
        return propertyNames;
    }
    
    /**
     * Return the TypeProperty 'idProperty'.  This method
     * will typically be used in conjunction with isIDSet 
     * method to determine if an @XmlID exists, and hence 
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
        if(propOrder != null) {
            if(property.isAttribute() && !propOrder.contains(property.getPropertyName())) {
                propOrder.add(property.getPropertyName());
            }
        }
    }
    
    /**
     * Sets the TypeProperty 'idProperty'.  This indicates that 
     * an @XmlID annotation is set on a given field/method.
     * 
     * @return
     */
    public void setIDProperty(Property idProperty) {
    	this.idProperty = idProperty;
    }
    
    public void setProperties(ArrayList<Property> properties) {
        if(properties != null) {
            for(int i = 0; i < properties.size(); i++) {
                Property next = properties.get(i);
                this.addProperty(next.getPropertyName(), next);
            }
        }
    }
    
    public void orderProperties(XmlAccessOrder order) {
        //Order the arraylist of property names according to the 
        //XmlAccessorOrder annotation
        if(order == XmlAccessOrder.ALPHABETICAL) {
            if(this.propertyNames != null) {
                Collections.sort(this.propertyNames);
            }
        }
    }
    
    public boolean isEnumerationType() {
        return false;
    }
    
    /**
     * Indicates if an @XmlID is set on a field/property.  
     * If so, the TypeProperty 'idProperty' will be non-null.
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
}
        
