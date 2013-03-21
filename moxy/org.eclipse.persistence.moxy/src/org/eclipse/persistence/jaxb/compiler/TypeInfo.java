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
package org.eclipse.persistence.jaxb.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.internal.jaxb.AccessorFactoryWrapper;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleType;
import org.eclipse.persistence.internal.oxm.schema.model.TypeDefParticle;

import org.eclipse.persistence.oxm.XMLNameTransformer;

import org.eclipse.persistence.jaxb.DefaultXMLNameTransformer;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;

import org.eclipse.persistence.jaxb.xmlmodel.XmlNamedObjectGraph;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethods;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlType;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b>Used to store meta data about JAXB 2.0 Annotated classes during schema and mapping
 * generation processes.
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
    private Descriptor descriptor;
    private String javaClassName;
    private ComplexType complexType;
    private boolean hasRootElement;
    private String elementRefsPropertyName;
    private Schema schema;
    private SimpleType simpleType;
    private ArrayList<String> propOrder; // store as a collection so it can be added to if needed
    private String classNamespace;
    private String schemaTypeName;
    private TypeDefParticle compositor;
    private ArrayList<String> propertyNames;
    private ArrayList<Property> propertyList;// keep the keys in a list to preserve order
    private HashMap<String, Property> properties;
    private HashMap<String, Property> originalProperties;
    private Map<String, List<Property>> additionalProperties;
    private Property idProperty; // if there is an XmlID annotation, set the property for mappings gen
    private HashMap<String, JavaClass> packageLevelAdaptersByClass;
    private String objectFactoryClassName;
    private String factoryMethodName;
    private String[] factoryMethodParamTypes;
    private Property xmlValueProperty;
    private String classExtractorName;
    private Map<Object, Object> userProperties;
    private List<Property> xmlKeyProperties;
    private List<Property> predicateProperties;
    
    private List<XmlNamedObjectGraph> objectGraphs;

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
    private XmlJavaTypeAdapter xmlJavaTypeAdapter;
    private String xmlCustomizer;
    private XmlVirtualAccessMethods xmlExtensible;

    private String anyAttributePropertyName;
    private String anyElementPropertyName;
    
    private boolean isBinaryDataInlined;
    
    private String xmlDiscriminatorNode;
    private String xmlDiscriminatorValue;
    
    private AccessorFactoryWrapper xmlAccessorFactory;
    private AccessorFactoryWrapper packageLevelXmlAccessorFactory;

    private static String EMPTY_STRING = "";
    
    private XMLNameTransformer xmlNameTransformer;
    
    private boolean isLocationAware = false;

    public static XMLNameTransformer DEFAULT_NAME_TRANSFORMER = new DefaultXMLNameTransformer();
 
	/**
     * This constructor sets the Helper to be used throughout XML and Annotations
     * processing.  Other fields are initialized here as well.
     * 
     * @param helper
     */
    public TypeInfo(Helper helper) {
        propertyNames = new ArrayList<String>();
        properties = new HashMap<String, Property>();
        originalProperties = new HashMap<String, Property>();
        propertyList = new ArrayList<Property>();
        xmlNameTransformer = DEFAULT_NAME_TRANSFORMER;
        isSetXmlTransient = false;
        isPreBuilt = false;
        isPostBuilt = false;
    }

    /**
     * Return the XmlDescriptor set on this TypeInfo.
     * 
     * @return the XmlDescriptor set on this TypeInfo, or null if none
     */
    public Descriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Set the XmlDescriptor on this TypeInfo.
     * 
     * @param desc
     */
    public void setDescriptor(Descriptor desc) {
    	descriptor = desc;
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

    /**
     * Indicates that the propOrder has been set, i.e. is non-null
     * 
     * @return true if propOrder is non-null, false otherwise
     */
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
     * Return the TypeProperty 'idProperty'. This method will typically be used in conjunction with
     * isIDSet method to determine if an @XmlID exists, and hence 'idProperty' is non-null.
     * 
     * @return
     */
    public Property getIDProperty() {
        return idProperty;
    }

    /**
     * Return the Map of Properties for this TypeInfo.
     * 
     * @return
     */
    public HashMap<String, Property> getProperties() {
        return properties;
    }

    /**
     * Put a Property in the Map of Properties for this TypeInfo. Here, the
     * original property is put in the originalProperties map, the 
     * properties map, and the propertyList list.  It is assumed that
     * the originalProperties map will remain unchanged during processing.
     * 
     */
    public void addProperty(String name, Property property) {
        if(originalProperties.get(name) != null) {
            // Duplicate property, see if either is transient and only keep the non-transient one
            Property existingProperty = originalProperties.get(name);
            if (existingProperty.isTransient() && property.isTransient()) {
                // Both transient, keep the one that matches AccessType
                if (this.getXmlAccessType().equals(XmlAccessType.FIELD)) {
                    if (!property.isMethodProperty()) {
                        // Existing property must be method property
                        // Remove it and use the new property
                        propertyList.remove(existingProperty);
                        propertyNames.remove(name);
                    } else {
                        return;
                    }
                } else {
                    if (property.isMethodProperty()) {
                        // Existing property must be field property
                        // Remove it and use the new property
                        propertyList.remove(existingProperty);
                        propertyNames.remove(name);
                    } else {
                        return;
                    }
                }
            } else if (existingProperty.isTransient() || existingProperty.isSuperClassProperty()) {
                // Continue, and overwrite it
                propertyList.remove(existingProperty);
                propertyNames.remove(name);
            } else if (property.isTransient()) {
                // Do nothing
                return;
            } else {
                // Neither is transient - truly a duplicate property collision
                throw org.eclipse.persistence.exceptions.JAXBException.duplicatePropertyName(name, this.getJavaClassName());
            }
        }
        originalProperties.put(name, property);
        properties.put(name, property);
        propertyNames.add(name);
        propertyList.add(property);
    }

    /**
     * Sets the TypeProperty 'idProperty'. This indicates that an @XmlID annotation is set on a
     * given field/method.
     * 
     * @return
     */
    public void setIDProperty(Property idProperty) {
        this.idProperty = idProperty;
    }

    /**
     * Set the Map of Properties for this TypeInfo.
     * 
     * @param properties
     */
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

    /**
     * Indicates if this type info represents an enumeration.  Since 
     * EnumTypeInfo is used for enum types, this method will always
     * return false.
     * 
     * @return false
     */
    public boolean isEnumerationType() {
        return false;
    }

    /**
     * Indicates if an @XmlID is set on a field/property. If so, the TypeProperty 'idProperty' will
     * be non-null.
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

    /**
     * Return the xmladapter class for a given bound type class.
     * 
     * @param boundType
     * @return
     */
    public JavaClass getPackageLevelAdapterClass(JavaClass boundType) {
        if(hasPackageLevelAdaptersByClass()) {
            return getPackageLevelAdaptersByClass().get(boundType.getQualifiedName());
        }
        return null;
    }
    
    public boolean isAnonymousComplexType() {
        return this.schemaTypeName == null || this.schemaTypeName.equals("");
    }

    /**
     * Return the xmladapter class for a given bound type class name.
     * 
     * @param boundType
     * @return
     */
    public JavaClass getPackageLevelAdapterClass(String boundTypeName) {
        if(hasPackageLevelAdaptersByClass()) {
            return getPackageLevelAdaptersByClass().get(boundTypeName);
        }
        return null;
    }

    /**
     * Return the Map of XmlAdapters for this TypeInfo.
     * 
     * @return
     */
    public HashMap<String, JavaClass> getPackageLevelAdaptersByClass() {
        if(!hasPackageLevelAdaptersByClass()) {
            packageLevelAdaptersByClass = new HashMap<String, JavaClass>();
        }
        return packageLevelAdaptersByClass;
    }

    boolean hasPackageLevelAdaptersByClass() {
        return null != packageLevelAdaptersByClass;
    }

    /**
     * Put a bound type class to adapter class entry in the Map.
     * 
     * @param adapterClass
     * @param boundType
     */
    public void addPackageLevelAdapterClass(JavaClass adapterClass, JavaClass boundType) {
        getPackageLevelAdaptersByClass().put(boundType.getQualifiedName(), adapterClass);
    }

    public boolean hasRootElement() {
        return hasRootElement;
    }

    public void setHasRootElement(boolean hasRoot) {
        hasRootElement = hasRoot;
    }

    /**
     * Indicates if this TypeInfo contains an XmlElementRefs property.
     * 
     * @return
     */
    public boolean hasElementRefs() {
        return elementRefsPropertyName != null;
    }

    /**
     * Return the XmlElementRefs property name, if one has been set.
     * 
     * @return
     */
    public String getElementRefsPropName() {
        return elementRefsPropertyName;
    }
    
    /**
     * Set the XmlElementRefs property name.
     * 
     * @return
     */
    public void setElementRefsPropertyName(String propName) {
        elementRefsPropertyName = propName;
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

    /**
     * Indicates if an xmlValueProperty is set on this TypeInfo, i.e.
     * is non-null.
     *  
     * @return
     */
    public boolean isSetXmlValueProperty() {
        return xmlValueProperty != null;
    }

    public Property getXmlValueProperty() {
        return xmlValueProperty;
    }

    public void setXmlValueProperty(Property xmlValueProperty) {
        this.xmlValueProperty = xmlValueProperty;
    }

    /**
     * Indicates if the class represented by this TypeInfo is marked XmlTransient.
     *  
     * @return
     */
    public boolean isTransient() {
        return isTransient;
    }

    /**
     * Used to indicate that the class represented by this TypeInfo is marked 
     * XmlTransient.
     *  
     * @return
     */
    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    /**
     * Return all non-transient properties that exist in the propOrder array.
     * 
     * @return
     */
    public List<Property> getNonTransientPropertiesInPropOrder() {
        List<Property> propertiesInOrder = new ArrayList<Property>();
        String[] propOrder = getPropOrder();
        if (propOrder.length == 0 || propOrder[0].equals(EMPTY_STRING)) {
            ArrayList<String> propertyNames = getPropertyNames();
            for (int i = 0; i < propertyNames.size(); i++) {
                addPropertyToList(propertiesInOrder, propertyNames.get(i), null);
            }
        } else {
            ArrayList<String> propertyNamesCopy = new ArrayList<String>(getPropertyNames());
            for (int i = 0; i < propOrder.length; i++) {
                // generate mappings based on the propOrder.
                addPropertyToList(propertiesInOrder, propOrder[i], propertyNamesCopy);
            }
            // attributes may not be in the prop order in which 
            // case we need to generate those mappings also
            for (int i = 0; i < propertyNamesCopy.size(); i++) {
                addPropertyToList(propertiesInOrder, propertyNamesCopy.get(i), null);
            }
        }
        return propertiesInOrder;
    }

    /**
     * Convenience method that adds non-null, non-transient properties to a given 
     * List<Property>.  The propertyName parameter is used to lookup the Property.
     * If propertyNamesCopy is non-null, the Property will be removed from that 
     * List.  Any additional properties that exist for propertyName (as in the 
     * case of multiple mappings to the same field) will be added as well.
     *  
     */
    private void addPropertyToList(List<Property> propertiesInOrder, String propertyName, ArrayList<String> propertyNamesCopy) {
        Property next = getProperties().get(propertyName);
        if (next != null && !next.isTransient()) {
            if (propertyNamesCopy != null) { 
                propertyNamesCopy.remove(propertyName);
            }
            propertiesInOrder.add(next);
            // check for additional properties (i.e. multiple mappings to the same field)
            if(hasAdditionalProperties()) {
                List<Property> addProps = getAdditionalProperties().get(propertyName);
                if (addProps != null) {
                    for (Property addProp : addProps) {
                        if (!addProp.isTransient()) {
                            propertiesInOrder.add(addProp);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Indicates if XmlTransient is set.
     * 
     * @return
     */
    public boolean isSetXmlTransient() {
        return isSetXmlTransient;
    }

    /**
     * Set the XmlTransient for this TypeInfo.
     * 
     * @param isTransient
     */
    public void setXmlTransient(boolean isTransient) {
        isSetXmlTransient = true;
        setTransient(isTransient);
    }

    /**
     * Indicates if xmlSeeAlso has been set, i.e. is non-null
     * 
     * @return true is xmlSeeAlso has been set, i.e. is non-null, false otherwise
     */
    public boolean isSetXmlSeeAlso() {
        return xmlSeeAlso != null;
    }

    /**
     * Return the List of XmlSeeAlso class names for this TypeInfo.
     * 
     * @return
     */
    public List<String> getXmlSeeAlso() {
        return xmlSeeAlso;
    }

    /**
     * Set the List of XmlSeeAlso class names for this TypeInfo.
     * 
     * @param xmlSeeAlso
     */
    public void setXmlSeeAlso(List<String> xmlSeeAlso) {
        this.xmlSeeAlso = xmlSeeAlso;
    }

    /**
     * Indicates if xmlRootElement has been set, i.e. is non-null
     * 
     * @return true is xmlRootElement has been set, i.e. is non-null, false otherwise
     */
    public boolean isSetXmlRootElement() {
        return xmlRootElement != null;
    }

    /**
     * Return the xmlRootElement set on this TypeInfo.
     * 
     * @return
     */
    public XmlRootElement getXmlRootElement() {
        return xmlRootElement;
    }

    /**
     * Set the xmlRootElement for this TypeInfo.
     * 
     * @param xmlRootElement
     */
    public void setXmlRootElement(XmlRootElement xmlRootElement) {
        this.xmlRootElement = xmlRootElement;
    }

    /**
     * Indicates if xmlType has been set, i.e. is non-null
     * 
     * @return true is xmlType has been set, i.e. is non-null, false otherwise
     */
    public boolean isSetXmlType() {
        return xmlType != null;
    }

    /**
     * Return the xmlType set on this TypeInfo.
     * 
     * @return
     */
    public XmlType getXmlType() {
        return xmlType;
    }

    /**
     * Set the xmlType for this TypeInfo.
     * 
     * @param xmlType
     */
    public void setXmlType(XmlType xmlType) {
        this.xmlType = xmlType;
    }
    
    /**
     * Indicates if xmlAccessType has been set, i.e. is non-null
     * 
     * @return true is xmlAccessType has been set, i.e. is non-null, false otherwise
     */
    public boolean isSetXmlAccessType() {
        return xmlAccessType != null;
    }

    /**
     * Return the xmlAccessType for this TypeInfo.
     * 
     * @return
     */
    public XmlAccessType getXmlAccessType() {
        return xmlAccessType;
    }

    /**
     * Set the xmlAccessType for this TypeInfo.
     * 
     * @param xmlAccessType
     */
    public void setXmlAccessType(XmlAccessType xmlAccessType) {
        this.xmlAccessType = xmlAccessType;
    }

    /**
     * Indicates if xmlAccessOrder has been set, i.e. is non-null
     * 
     * @return true is xmlAccessOrder has been set, i.e. is non-null, false otherwise
     */
    public boolean isSetXmlAccessOrder() {
        return xmlAccessOrder != null;
    }

    /**
     * Return the xmlAccessOrder for this TypeInfo.
     * 
     * @return
     */
    public XmlAccessOrder getXmlAccessOrder() {
        return xmlAccessOrder;
    }

    /**
     * Set the xmlAccessOrder for this TypeInfo.
     * 
     * @param xmlAccessOrder
     */
    public void setXmlAccessOrder(XmlAccessOrder xmlAccessOrder) {
        this.xmlAccessOrder = xmlAccessOrder;
    }

    /**
     * Indicates if this TypeInfo has completed the preBuildTypeInfo phase of
     * processing.
     * 
     * @return true if this TypeInfo has completed the preBuildTypeInfo phase of
     * processing, false otherwise
     */
    public boolean isPreBuilt() {
        return isPreBuilt;
    }

    /**
     * Set indicator that this TypeInfo has completed the preBuildTypeInfo
     * phase of processing.
     * 
     */
    public void setPreBuilt(boolean isPreBuilt) {
        this.isPreBuilt = isPreBuilt;
    }

    /**
     * Indicates if this TypeInfo has completed the postBuildTypeInfo phase of
     * processing.
     * 
     * @return true if this TypeInfo has completed the postBuildTypeInfo phase of
     * processing, false otherwise
     */
    public boolean isPostBuilt() {
        return isPostBuilt;
    }

    /**
     * Set indicator that this TypeInfo has completed the postBuildTypeInfo
     * phase of processing.
     * 
     */
    public void setPostBuilt(boolean isPostBuilt) {
        this.isPostBuilt = isPostBuilt;
    }
    
    /**
     * Indicates if an XmlJavaTypeAdapter has been set, i.e. the
     * xmlJavaTypeAdapter property is non-null.
     * 
     * @return true if xmlJavaTypeAdapter is non-null, false otherwise
     * @see XmlJavaTypeAdapter
     */
    public boolean isSetXmlJavaTypeAdapter() {
        return getXmlJavaTypeAdapter() != null;
    }

    /**
     * Return the xmlJavaTypeAdapter set on this Type.
     * 
     * @return xmlJavaTypeAdapter, or null if not set
     * @see XmlJavaTypeAdapter
     */
    public XmlJavaTypeAdapter getXmlJavaTypeAdapter() {
        return xmlJavaTypeAdapter;
    }

    /**
     * Set an XmlJavaTypeAdapter on this Type.  
     * 
     * @param xmlJavaTypeAdapter
     * @see XmlJavaTypeAdapter
     */
    public void setXmlJavaTypeAdapter(XmlJavaTypeAdapter xmlJavaTypeAdapter) {
        this.xmlJavaTypeAdapter = xmlJavaTypeAdapter;
    }
    
    /**
     * Return the XmlCustomizer class name set on this TypeInfo, or null if none
     * is set.
     * 
     * @return the XmlCustomizer class name set on this TypeInfo, or null if none is set
     * @see DescriptorCustomizer
     */
    public String getXmlCustomizer() {
        return xmlCustomizer;
    }

    /**
     * Sets the XmlCustomizer class name on this TypeInfo.
     * 
     * @param xmlCustomizer
     * @see DescriptorCustomizer
     */
    public void setXmlCustomizer(String xmlCustomizerClassName) {
        this.xmlCustomizer = xmlCustomizerClassName;
    }
    
    /**
     * Indicates if the name of the XmlAnyElement property has been set.
     * 
     * @return
     */
    public boolean isSetAnyElementPropertyName() {
        return getAnyElementPropertyName() != null;
    }

    /**
     * Return the name of the XmlAnyElement property, if one is set.
     * 
     * @return
     */
    public String getAnyElementPropertyName() {
        return anyElementPropertyName;
    }

    /**
     * Set the name of the XmlAnyElement property
     * 
     * @param anyElementPropertyName
     */
    public void setAnyElementPropertyName(String anyElementPropertyName) {
        this.anyElementPropertyName = anyElementPropertyName;
    }

    /**
     * Indicates if the name of the XmlAnyAttribute property has been set.
     * 
     * @return
     */
    public boolean isSetAnyAttributePropertyName() {
        return getAnyAttributePropertyName() != null;
    }

    /**
     * Return the name of the XmlAnyAttribute property, if one is set.
     * 
     * @return
     */
    public String getAnyAttributePropertyName() {
        return anyAttributePropertyName;
    }

    /**
     * Set the name of the XmlAnyAttribute property
     * 
     * @param anyAttributePropertyName
     */
    public void setAnyAttributePropertyName(String anyAttributePropertyName) {
        this.anyAttributePropertyName = anyAttributePropertyName;
    }

    /**
     * Indicates if all binary data types are to be inlined for this class.
     *     
     * @return
     */
    public boolean isBinaryDataToBeInlined() {
        return isBinaryDataInlined;
    }

    /**
     * Sets the flag that indicates if all binary data types are to 
     * be inlined for this class.
     * 
     * @param isBinaryDataInlined
     */
    public void setInlineBinaryData(boolean isBinaryDataInlined) {
        this.isBinaryDataInlined = isBinaryDataInlined;
    }

    /**
     * Indicates if a ClassExtractor class name is set, 
     * i.e. is non-null  
     * 
     * @return true if classExtractorName != null, otherwise false
     */
    public boolean isSetClassExtractorName() {
        return classExtractorName != null;
    }

    /**
     * Return the ClassExtractor class name.  
     * 
     * @return classExtractorName
     */
    public String getClassExtractorName() {
        return classExtractorName;
    }

    /**
     * Set the ClassExtractor class name.
     * 
     * @param classExtractorName
     */
    public void setClassExtractorName(String classExtractorName) {
        this.classExtractorName = classExtractorName;
    }

    /**
     * Return the Map of user-defined properties.
     * 
     * @return
     */
    public Map<Object, Object> getUserProperties() {
        return userProperties;
    }

    /**
     * Set the Map of user-defined properties.
     * 
     * @param userProperties
     */
    public void setUserProperties(Map<Object, Object> userProperties) {
        this.userProperties = userProperties;
    }

    /**
     * Return the JavaClass name for this typeinfo.
     * 
     * @return
     */
    public String getJavaClassName() {
        return javaClassName;
    }

    /**
     * Set the JavaClass name for this typeinfo.
     * 
     * @param javaClassName
     */
    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }

    /**
     * Return the class indicator field name for this type info.
     * 
     * @return
     */
    public String getXmlDiscriminatorNode() {
        return xmlDiscriminatorNode;
    }

    /**
     * Sets the class indicator field name (for use with inheritance).  
     *  
     * @param xmlDiscriminatorNode
     */
    public void setXmlDiscriminatorNode(String xmlDiscriminatorNode) {
        this.xmlDiscriminatorNode = xmlDiscriminatorNode;
    }

    /**
     * Indicates if the class indicator field name is set, i.e. is non-null  
     * 
     * @return true if xmlDiscriminatorNode != null, otherwise false
     */
    public boolean isSetXmlDiscriminatorNode() {
        return xmlDiscriminatorNode != null;
    }

    /**
     * Return the value of the class indicator field for the java-type 
     * associated with this type info.
     * 
     * @return
     */
    public String getXmlDiscriminatorValue() {
        return xmlDiscriminatorValue;
    }

    /**
     * Set the value of the class indicator field for the java-type 
     * associated with this type info.
     *    
     * @param xmlDiscriminatorValue
     */
    public void setXmlDiscriminatorValue(String xmlDiscriminatorValue) {
        this.xmlDiscriminatorValue = xmlDiscriminatorValue;
    }
    
    /**
     * Indicates if a class indicator field value has been set, 
     * i.e. is non-null  
     * 
     * @return true if xmlDiscriminatorValue != null, otherwise false
     */
    public boolean isSetXmlDiscriminatorValue() {
        return xmlDiscriminatorValue != null;
    }

    /**
     * Add an XmlKey property to the list.  Each entry will be 
     * set as a primary key on the owning descriptor.
     * 
     * @param xmlKeyProp
     */
    public void addXmlKeyProperty(Property xmlKeyProp) {
        if (xmlKeyProperties == null) {
            xmlKeyProperties = new ArrayList<Property>();
        }
        xmlKeyProperties.add(xmlKeyProp);
    }
    
    /**
     * Return the list of XmlKey property entries.  Each entry
     * will be set as a primary key on the owning descriptor.
     * 
     * @return
     */
    public List<Property> getXmlKeyProperties() {
        return xmlKeyProperties;
    }
    
    /**
     * Indicates if the list of XmlKey property entries has 
     * been set, i.e. is non-null.
     *  
     * @return
     */
    public boolean hasXmlKeyProperties() {
        return xmlKeyProperties != null;
    }

    /**
     * Return a Map of property names to list of properties.  This Map will
     * contain entries when one or more properties exist for a given field,
     * as in the case of multiple mappings for a single field.
     * 
     */
    public Map<String, List<Property>> getAdditionalProperties() {
        if (!hasAdditionalProperties()) {
            additionalProperties = new HashMap<String, List<Property>>();
        }
        return additionalProperties;
    }

    boolean hasAdditionalProperties() {
        return null != additionalProperties;
    }

    /**
     * Return the list of original properties for this type info.  These
     * properties were created during annotations processing, and have 
     * not been modified since.  
     * 
     * @return
     */
    public HashMap<String, Property> getOriginalProperties() {
        return originalProperties;
    }

    public XmlVirtualAccessMethods getXmlVirtualAccessMethods() {
        return xmlExtensible;
    }

    public void setXmlVirtualAccessMethods(XmlVirtualAccessMethods xmlExtensible) {
        this.xmlExtensible = xmlExtensible;
    }

    
    /**
     * Return the XMLNameTransformer used when converting Java names to XML names
     * @return
     */
    public XMLNameTransformer getXmlNameTransformer() {    	
        return xmlNameTransformer;
    }

    /**
     * Set the XMLNameTransformer used when converting Java names to XML names
     * @param xmlNameTransformer
     */
    public void setXmlNameTransformer(XMLNameTransformer xmlNameTransformer) {
        this.xmlNameTransformer = xmlNameTransformer;
    }

    
    /**
     * INTERNAL:
     * Return the list of predicate properties. Lazy initializes the list.
     * @return
     */
    public List<Property> getPredicateProperties() {
        if(predicateProperties == null) {
            predicateProperties = new ArrayList<Property>();
        }
        return predicateProperties;
    }

    /**
     * INTERNAL:
     * Return true if the property handed in is a duplicate of any existing predicate property.
     */
    public boolean hasPredicateProperty(Property property) {
        if(this.predicateProperties != null) {
            for(Property next:this.predicateProperties) {
                if(next.getXmlPath().equals(property.getXmlPath())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if the list of predicate properties hasn't been initialized.
     * @return
     */
    public boolean hasPredicateProperties() {
        return this.predicateProperties != null && this.predicateProperties.size() > 0;
    }

    public boolean isLocationAware() {
        return this.isLocationAware;
    }

    public void setLocationAware(boolean value) {
        this.isLocationAware = value;
    }

    /**
     * This event is called when all of the metadata for this type has been 
     * processed and provides a chance to deference anything that is no longer 
     * needed to reduce the memory footprint of this object.
     */
    void postInitialize() {
        this.originalProperties = null;
    }

    public AccessorFactoryWrapper getXmlAccessorFactory() {
        return xmlAccessorFactory;
    }

    public void setXmlAccessorFactory(AccessorFactoryWrapper xmlAccessorFactory) {
        this.xmlAccessorFactory = xmlAccessorFactory;
    }

    public AccessorFactoryWrapper getPackageLevelXmlAccessorFactory() {
        return packageLevelXmlAccessorFactory;
    }

    public void setPackageLevelXmlAccessorFactory(AccessorFactoryWrapper packageLevelXmlAccessorFactory) {
        this.packageLevelXmlAccessorFactory = packageLevelXmlAccessorFactory;
    }
    
    public List<XmlNamedObjectGraph> getObjectGraphs() {
        if(this.objectGraphs == null) {
            this.objectGraphs = new ArrayList<XmlNamedObjectGraph>();
        }
        return this.objectGraphs;
    }
}