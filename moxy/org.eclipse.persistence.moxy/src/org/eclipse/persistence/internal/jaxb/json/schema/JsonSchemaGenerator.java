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
package org.eclipse.persistence.internal.jaxb.json.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.jaxb.json.schema.model.JsonSchema;
import org.eclipse.persistence.internal.jaxb.json.schema.model.JsonType;
import org.eclipse.persistence.internal.jaxb.json.schema.model.Property;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.record.namespaces.MapNamespacePrefixMapper;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBEnumTypeConverter;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.sessions.Project;

/**
 * INTERNAL:
 * <p><b>Purpose:</b> This class generates an instance of JsonSchema based on an EclipseLink
 * project and given a root class. The descriptor for the root class' mappings are traversed and
 * the associated schema artifacts are created.
 * 
 */
public class JsonSchemaGenerator {
    Project project;
    JsonSchema schema;
    Map contextProperties;
    String attributePrefix;
    Class rootClass;
    boolean namespaceAware;
    NamespaceResolver resolver;
    String NAMESPACE_SEPARATOR = ".";
    NamespacePrefixMapper prefixMapper = null;
    Property[] xopIncludeProp = null;

    private static String DEFINITION_PATH="#/definitions";
    
    private static HashMap<Class, JsonType> javaTypeToJsonType;
    
    
    public JsonSchemaGenerator(Project project, Map properties) {
        this.project = project;
        this.contextProperties = properties;
        if(properties != null) {
            attributePrefix = (String)properties.get(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX);
            Object prefixMapperValue = properties.get(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER);
            if(prefixMapperValue != null) {
                if(prefixMapperValue instanceof Map){
                    prefixMapper = new MapNamespacePrefixMapper((Map)prefixMapperValue);
                }else{
                    prefixMapper = (NamespacePrefixMapper)prefixMapperValue;
                }
            }
            if(prefixMapper != null) {
                //check for customer separator
                String namespaceSeparator = (String) properties.get(JAXBContextProperties.JSON_NAMESPACE_SEPARATOR);
                if(namespaceSeparator != null) {
                    this.NAMESPACE_SEPARATOR = namespaceSeparator;
                }
            }
        }
    }
    
    public JsonSchema generateSchema(Class rootClass) {
        this.rootClass = rootClass;
        schema = new JsonSchema();
        schema.setTitle(rootClass.getName());
        XMLDescriptor descriptor = (XMLDescriptor)project.getDescriptor(rootClass);
        schema.setType(JsonType.OBJECT);
        Property rootProperty = null;
        Map<String, Property> properties = schema.getProperties();
        if(contextProperties != null && Boolean.TRUE.equals(this.contextProperties.get(JAXBContextProperties.JSON_INCLUDE_ROOT))) {
            XMLField field = descriptor.getDefaultRootElementField();
            if(field != null) {
                rootProperty = new Property();
                rootProperty.setType(JsonType.OBJECT);
                rootProperty.setName(field.getXPathFragment().getLocalName());
                properties.put(rootProperty.getName(), rootProperty);
                properties = rootProperty.getProperties();
            }
        }
        JsonType type = populateProperties(properties, descriptor);
        if(type != null) {
            if(rootProperty != null) {
                rootProperty.setType(type);
            } else {
                schema.setType(type);
            }
        }
        return schema;
    }

    /**
     * Populates the map of properties based on the mappings in this descriptor. If the descriptor represents a
     * simple type (a single direct mapping with xpath of text) then the name of the simple type is returned. 
     * Otherwise null is returned.
     * 
     * @param properties
     * @param descriptor
     * @return null for a complex type, the simple type name for a simple type.
     */
    private JsonType populateProperties(Map<String, Property> properties, XMLDescriptor descriptor) {
        
        List<DatabaseMapping> mappings = descriptor.getMappings();
        if(mappings.size() == 1) {
            //check for simple type
            DatabaseMapping mapping = mappings.get(0);
            if(mapping instanceof DirectMapping) {
                DirectMapping directMapping = (DirectMapping)mapping;
                XPathFragment frag = ((XMLField)directMapping.getField()).getXPathFragment();
                if(frag.nameIsText()) {
                    return getJsonTypeForJavaType(directMapping.getAttributeClassification());
                }
            } else if(mapping instanceof DirectCollectionMapping) {
                DirectCollectionMapping directMapping = (DirectCollectionMapping)mapping;
                XPathFragment frag = ((XMLField)directMapping.getField()).getXPathFragment();
                if(frag.nameIsText()) {
                    return getJsonTypeForJavaType(directMapping.getAttributeElementClass());
                }
            }
        }
        for(DatabaseMapping next:mappings) {
            if(next instanceof ChoiceObjectMapping) {
                ChoiceObjectMapping coMapping = (ChoiceObjectMapping)next;
                for(Object nestedMapping:coMapping.getChoiceElementMappingsByClass().values()) {
                    Property prop = generateProperty((XMLMapping)nestedMapping, descriptor, properties);
                    if(!(properties.containsKey(prop.getName()))) {
                        properties.put(prop.getName(), prop);
                    }                    
                }
            } else if(next instanceof ChoiceCollectionMapping) {
                ChoiceCollectionMapping coMapping = (ChoiceCollectionMapping)next;
                for(Object nestedMapping:coMapping.getChoiceElementMappingsByClass().values()) {
                    Property prop = generateProperty((XMLMapping)nestedMapping, descriptor, properties);
                    if(!(properties.containsKey(prop.getName()))) {
                        properties.put(prop.getName(), prop);
                    }                    
                }
            } else {
                Property prop = generateProperty((XMLMapping)next, descriptor, properties);
                if(prop != null && !(properties.containsKey(prop.getName()))) {
                    properties.put(prop.getName(), prop);
                }
            }
        }
        return null;
    }

    private Property generateProperty(XMLMapping next, XMLDescriptor descriptor, Map<String, Property> properties) {
        Property prop = null;
        if(((XMLMapping)next).isCollectionMapping()) {
            if(next instanceof CollectionReferenceMapping) {
                CollectionReferenceMapping mapping = (CollectionReferenceMapping)next;
                Set<XMLField> sourceFields = mapping.getSourceToTargetKeyFieldAssociations().keySet();
                XMLDescriptor reference = (XMLDescriptor) mapping.getReferenceDescriptor();
                for(XMLField nextField: sourceFields) {
                    XPathFragment frag = nextField.getXPathFragment();
                    String propertyName = getNameForFragment(frag);

                    XMLField targetField = (XMLField) mapping.getSourceToTargetKeyFieldAssociations().get(nextField);
                    Class type = getTypeForTargetField(targetField, reference);
                    
                    prop = properties.get(propertyName);
                    if(prop == null) {
                        prop = new Property();
                        prop.setName(propertyName);
                    }
                    Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                    nestedProperty.setType(JsonType.ARRAY);
                    nestedProperty.setItem(new Property());
                    nestedProperty.getItem().setType(getJsonTypeForJavaType(type));
                    if(!properties.containsKey(prop.getName())) {
                        properties.put(prop.getName(), prop);                    
                    }
                }
                return null;                
            } else if(next.isAbstractCompositeCollectionMapping()) {
                CompositeCollectionMapping mapping = (CompositeCollectionMapping)next;
                XMLField field = (XMLField)mapping.getField();
                XPathFragment frag = field.getXPathFragment();
                String propName = getNameForFragment(frag);
                //for paths, there may already be an existing property
                prop = properties.get(propName);
                if(prop == null) {
                    prop = new Property();
                    prop.setName(propName);
                }
                Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                nestedProperty.setType(JsonType.ARRAY);
                nestedProperty.setItem(new Property());
                nestedProperty.getItem().setRef(getReferenceForDescriptor((XMLDescriptor)mapping.getReferenceDescriptor()));
                //populateProperties(nestedProperty.getItem().getProperties(), (XMLDescriptor)mapping.getReferenceDescriptor());                    
            } else if(next.isAbstractCompositeDirectCollectionMapping()) {
                DirectCollectionMapping mapping = (DirectCollectionMapping)next;
                XMLField field = (XMLField)mapping.getField();
                XPathFragment frag = field.getXPathFragment();
                List<String> enumeration = null;
                if(mapping.getValueConverter() instanceof JAXBEnumTypeConverter) {
                    JAXBEnumTypeConverter conv = (JAXBEnumTypeConverter)mapping.getValueConverter();
                    enumeration = new ArrayList<String>();
                    for(Object nextValue: conv.getAttributeToFieldValues().values()) {
                        enumeration.add(nextValue.toString());
                    }
                }
                String propertyName = getNameForFragment(frag);
                if(frag.nameIsText()) {
                    propertyName = (String)this.contextProperties.get(MarshallerProperties.JSON_VALUE_WRAPPER);
                }

                if(frag.isAttribute() && this.attributePrefix != null) {
                    propertyName = attributePrefix + propertyName;
                }
                prop = properties.get(propertyName);
                if(prop == null) {
                    prop = new Property();
                    prop.setName(propertyName);
                }
                Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                nestedProperty.setType(JsonType.ARRAY);
                nestedProperty.setItem(new Property());
                if(enumeration != null) {
                    nestedProperty.getItem().setEnumeration(enumeration);
                } 
                nestedProperty.getItem().setType(getJsonTypeForJavaType(mapping.getAttributeElementClass()));
                return prop;
            } else if(next instanceof BinaryDataCollectionMapping) {
                BinaryDataCollectionMapping mapping = (BinaryDataCollectionMapping)next;
                XMLField field = (XMLField)mapping.getField();
                XPathFragment frag = field.getXPathFragment();

                String propertyName = getNameForFragment(frag);
                if(frag.nameIsText()) {
                    propertyName = (String)this.contextProperties.get(MarshallerProperties.JSON_VALUE_WRAPPER);
                }

                if(frag.isAttribute() && this.attributePrefix != null) {
                    propertyName = attributePrefix + propertyName;
                }
                prop = properties.get(propertyName);
                if(prop == null) {
                    prop = new Property();
                    prop.setName(propertyName);
                }
                Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                nestedProperty.setType(JsonType.ARRAY);
                nestedProperty.setItem(new Property());

                if(mapping.shouldInlineBinaryData()) {
                    nestedProperty.getItem().setType(JsonType.STRING);
                } else {
                    nestedProperty.getItem().setAnyOf(getXopIncludeProperties());
                }
                return prop;          
            }
        } else {
            if(next.isAbstractDirectMapping()) {
                //handle direct mapping
                DirectMapping directMapping = (DirectMapping)next;
                XMLField field = (XMLField)directMapping.getField();
                XPathFragment frag = field.getXPathFragment();
                List<String> enumeration = null;
                if(directMapping.getConverter() instanceof JAXBEnumTypeConverter) {
                    JAXBEnumTypeConverter conv = (JAXBEnumTypeConverter)directMapping.getConverter();
                    enumeration = new ArrayList<String>();
                    for(Object nextValue: conv.getAttributeToFieldValues().values()) {
                        enumeration.add(nextValue.toString());
                    }
                }                
                String propertyName = getNameForFragment(frag);
                if(frag.nameIsText()) {
                    propertyName = Constants.VALUE_WRAPPER;
                    if(this.contextProperties != null)  {
                        String valueWrapper = (String) this.contextProperties.get(MarshallerProperties.JSON_VALUE_WRAPPER);
                        if(valueWrapper != null) {
                            propertyName = valueWrapper;
                        }
                    }
                }                
                if(frag.isAttribute() && this.attributePrefix != null) {
                    propertyName = attributePrefix + propertyName;
                }
                prop = properties.get(propertyName);
                if(prop == null) {
                    prop = new Property();
                    prop.setName(propertyName);
                }
                Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                if(enumeration != null) {
                    nestedProperty.setEnumeration(enumeration);
                } 
                if(directMapping instanceof BinaryDataMapping) {
                    BinaryDataMapping binaryMapping = (BinaryDataMapping)directMapping;
                    if(binaryMapping.shouldInlineBinaryData() || binaryMapping.isSwaRef()) {
                        nestedProperty.setType(JsonType.STRING);
                    } else {
                        if(this.xopIncludeProp == null) {
                            initXopIncludeProp();
                        }
                        nestedProperty.setAnyOf(this.xopIncludeProp);
                    }
                } else {
                    nestedProperty.setType(getJsonTypeForJavaType(directMapping.getAttributeClassification()));
                }
                return prop;
            } else if(next instanceof ObjectReferenceMapping) {
                ObjectReferenceMapping mapping = (ObjectReferenceMapping)next;
                Set<XMLField> sourceFields = mapping.getSourceToTargetKeyFieldAssociations().keySet();
                XMLDescriptor reference = (XMLDescriptor) mapping.getReferenceDescriptor();
                for(XMLField nextField: sourceFields) {
                    XPathFragment frag = nextField.getXPathFragment();
                    String propName = getNameForFragment(frag);
                    XMLField targetField = (XMLField) mapping.getSourceToTargetKeyFieldAssociations().get(nextField);
                    Class type = getTypeForTargetField(targetField, reference);
                    
                    prop = properties.get(propName);
                    if(prop == null) {
                        prop = new Property();
                        prop.setName(propName);
                    }
                    Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                    //nestedProperty.setType(JsonType.ARRAY);
                    //nestedProperty.setItem(new Property());
                    nestedProperty.setType(getJsonTypeForJavaType(type));
                    if(!properties.containsKey(prop.getName())) {
                        properties.put(prop.getName(), prop);                    
                    }
                }
                return null;                
            } else if(next.isAbstractCompositeObjectMapping()) {
                CompositeObjectMapping mapping = (CompositeObjectMapping)next;
                XMLDescriptor nextDescriptor = (XMLDescriptor)mapping.getReferenceDescriptor();
                XMLField field = (XMLField)mapping.getField();
                XPathFragment firstFragment = field.getXPathFragment();
                String propName = getNameForFragment(firstFragment);
                prop = properties.get(propName);
                if(prop == null) {
                    prop = new Property();
                    prop.setName(propName);
                }
                //prop.setType(JsonType.OBJECT);
                prop.setName(propName);
                Property nestedProperty = getNestedPropertyForFragment(firstFragment, prop);
                nestedProperty.setRef(getReferenceForDescriptor(nextDescriptor));
                //populateProperties(nestedProperty.getProperties(), nextDescriptor);
            } else if(next instanceof BinaryDataMapping) {
                BinaryDataMapping binaryMapping = (BinaryDataMapping)next;
                XMLField field = (XMLField)binaryMapping.getField();
                XPathFragment frag = field.getXPathFragment();
                String propertyName = getNameForFragment(frag);
                if(frag.nameIsText()) {
                    propertyName = Constants.VALUE_WRAPPER;
                    if(this.contextProperties != null)  {
                        String valueWrapper = (String) this.contextProperties.get(MarshallerProperties.JSON_VALUE_WRAPPER);
                        if(valueWrapper != null) {
                            propertyName = valueWrapper;
                        }
                    }
                }                
                if(frag.isAttribute() && this.attributePrefix != null) {
                    propertyName = attributePrefix + propertyName;
                }
                prop = properties.get(propertyName);
                if(prop == null) {
                    prop = new Property();
                    prop.setName(propertyName);
                }
                Property nestedProperty = getNestedPropertyForFragment(frag, prop);
                if(binaryMapping.shouldInlineBinaryData() || binaryMapping.isSwaRef()) {
                    nestedProperty.setType(JsonType.STRING);
                } else {
                    if(this.xopIncludeProp == null) {
                        initXopIncludeProp();
                    }
                    nestedProperty.setAnyOf(this.xopIncludeProp);
                }
                return prop;                
            }
        }
        
        return prop;
    }

    private String getNameForFragment(XPathFragment frag) {
        String name = frag.getLocalName();
        
        if(this.prefixMapper != null) {
            String namespaceUri = frag.getNamespaceURI();
            if(namespaceUri != null && namespaceUri.length() != 0) {
                String prefix = prefixMapper.getPreferredPrefix(namespaceUri, null, true);
                if(prefix != null) {
                    name = prefix + NAMESPACE_SEPARATOR + name;
                }
            }
        }
        return name;
    }

    private void initXopIncludeProp() {
        this.xopIncludeProp = new Property[2];
        Property p = new Property();
        p.setType(JsonType.STRING);
        this.xopIncludeProp[0] = p;
        
        p = new Property();
        this.xopIncludeProp[1] = p;
        p.setType(JsonType.OBJECT);
        Property includeProperty = new Property();
        includeProperty.setName("Include");
        includeProperty.setType(JsonType.OBJECT);
        p.getProperties().put(includeProperty.getName(), includeProperty);
        
        Property hrefProp = new Property();
        String propName = "href";
        if(this.attributePrefix != null) {
            propName = this.attributePrefix + propName;
        }
        hrefProp.setName(propName);
        hrefProp.setType(JsonType.STRING);
        includeProperty.getProperties().put(propName, hrefProp);
            
        
    }



    private String getReferenceForDescriptor(XMLDescriptor referenceDescriptor) {
        if(referenceDescriptor == null) {
            return null;
        }
        String className = referenceDescriptor.getJavaClass().getSimpleName();
        String referenceName = DEFINITION_PATH + "/" + className;
        
        if(referenceDescriptor.getJavaClass() == this.rootClass) {
            return "#";
        }
        if(!this.schema.getDefinitions().containsKey(className)) {
            Property definition = new Property();
            definition.setName(className);
            definition.setType(JsonType.OBJECT);
            this.schema.getDefinitions().put(definition.getName(), definition);
            JsonType jType = populateProperties(definition.getProperties(), referenceDescriptor);
            if(jType != null) {
                //this represents a simple type
                definition.setType(jType);
                definition.setProperties(null);
            }
        }
        // TODO Auto-generated method stub
        return referenceName;
    }

    private Class getTypeForTargetField(XMLField targetField, XMLDescriptor reference) {
        for(DatabaseMapping next: reference.getMappings()) {
            if(next.isDirectToFieldMapping()) {
                DirectMapping directMapping = (DirectMapping)next;
                if(directMapping.getField().equals(targetField)) {
                    return directMapping.getAttributeClassification();
                }
            }
        }
        return null;
    }

    private JsonType getJsonTypeForJavaType(Class attributeClassification) {
        HashMap<Class, JsonType> types = getJavaTypeToJsonType();
        JsonType jsonType = types.get(attributeClassification);
        if(jsonType == null) {
            return JsonType.OBJECT;
        }
        return jsonType;
    }
    
    private static HashMap<Class, JsonType> getJavaTypeToJsonType() {
        if(javaTypeToJsonType == null) {
            initJavaTypeToJsonType();
        }
        return javaTypeToJsonType;
    }

    private static void initJavaTypeToJsonType() {
        javaTypeToJsonType = new HashMap<Class, JsonType>();
        javaTypeToJsonType.put(CoreClassConstants.APBYTE, JsonType.ARRAY);
        javaTypeToJsonType.put(CoreClassConstants.BIGDECIMAL, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.BIGINTEGER, JsonType.INTEGER);
        javaTypeToJsonType.put(CoreClassConstants.PBOOLEAN, JsonType.BOOLEAN);
        javaTypeToJsonType.put(CoreClassConstants.PBYTE, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.CALENDAR, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.PDOUBLE, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.PFLOAT, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.PINT, JsonType.INTEGER);
        javaTypeToJsonType.put(CoreClassConstants.PLONG, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.PSHORT, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.STRING, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.CHAR, JsonType.STRING);       
        // other pairs
        javaTypeToJsonType.put(CoreClassConstants.ABYTE, JsonType.ARRAY);
        javaTypeToJsonType.put(CoreClassConstants.BOOLEAN, JsonType.BOOLEAN);
        javaTypeToJsonType.put(CoreClassConstants.BYTE, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.CLASS, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.GREGORIAN_CALENDAR, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.DOUBLE, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.FLOAT, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.INTEGER, JsonType.INTEGER);
        javaTypeToJsonType.put(CoreClassConstants.LONG, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.OBJECT, JsonType.OBJECT);
        javaTypeToJsonType.put(CoreClassConstants.SHORT, JsonType.NUMBER);
        javaTypeToJsonType.put(CoreClassConstants.UTILDATE, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.SQLDATE, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.TIME, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.TIMESTAMP, JsonType.STRING);
        javaTypeToJsonType.put(CoreClassConstants.DURATION, JsonType.STRING);
       
        
    }

    private Property getNestedPropertyForFragment(XPathFragment frag, Property prop) {
        if(frag.getNextFragment() == null  || frag.getNextFragment().nameIsText() ) {
            return prop;
        }
        Map<String, Property> currentProperties = prop.getProperties();
        prop.setProperties(currentProperties);
        prop.setType(JsonType.OBJECT);
        frag = frag.getNextFragment();
        String propertyName = getNameForFragment(frag);
        if(frag.isAttribute() && this.attributePrefix != null) {
            propertyName = this.attributePrefix + "propertyName";
        }
        while(frag != null && !frag.nameIsText()) {
            Property nestedProperty = prop.getProperty(propertyName);
            if(nestedProperty == null) {
                nestedProperty = new Property();
                nestedProperty.setName(propertyName);
            }
            currentProperties.put(nestedProperty.getName(), nestedProperty);
            if(frag.getNextFragment() == null || frag.getNextFragment().nameIsText()) {
                return nestedProperty;
            } else {
                nestedProperty.setType(JsonType.OBJECT);

                currentProperties = nestedProperty.getProperties();
            }
            frag = frag.getNextFragment();
        }
        return null;
    }
    
    private Property[] getXopIncludeProperties() {
        if(this.xopIncludeProp == null) {
            this.initXopIncludeProp();
        }
        return this.xopIncludeProp;
    }

}
