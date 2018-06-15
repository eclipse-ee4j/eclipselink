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
package org.eclipse.persistence.internal.jaxb.json.schema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.jaxb.json.schema.model.JsonSchema;
import org.eclipse.persistence.internal.jaxb.json.schema.model.JsonType;
import org.eclipse.persistence.internal.jaxb.json.schema.model.Property;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.QNameInheritancePolicy;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
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
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.record.namespaces.MapNamespacePrefixMapper;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBEnumTypeConverter;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
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
    XMLContext xmlContext;
    Property rootProperty = null;
    private JAXBContext jaxbContext;

    private static String DEFINITION_PATH="#/definitions";

    private static HashMap<Class, JsonType> javaTypeToJsonType;


    public JsonSchemaGenerator(JAXBContext jaxbContext, Map properties) {
        //this.project = project;
        this.xmlContext = jaxbContext.getXMLContext();
        this.jaxbContext = jaxbContext;
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

        if(rootClass.isEnum()) {
            Class generatedWrapper = jaxbContext.getClassToGeneratedClasses().get(rootClass.getName());
            if(generatedWrapper != null) {
                rootClass = generatedWrapper;
            } else {
                schema.setType(JsonType.STRING);
                return schema;
            }
        }
        //Check for simple type
        JsonType rootType = getJsonTypeForJavaType(rootClass);
        if(rootType != JsonType.OBJECT) {
            if(rootType == JsonType.BINARYTYPE) {
                schema.setAnyOf(getXopIncludeProperties());
                return schema;
            } else {
                schema.setType(rootType);
                return schema;
            }
        }

        Map<String, Property> properties = null;
        //Check for root level list or array
        if(rootClass.isArray() || isCollection(rootClass)) {
            schema.setType(JsonType.ARRAY);
            schema.setItems(new Property());
            Class itemType = Object.class;

            if(rootClass.isArray()) {
                itemType = rootClass.getComponentType();
            } else {
                Type pType = rootClass.getGenericSuperclass();
                if(pType instanceof ParameterizedType) {
                    itemType = (Class)((ParameterizedType)pType).getActualTypeArguments()[0];
                }
            }
            rootType = getJsonTypeForJavaType(itemType);
            schema.getItems().setType(rootType);
            if(rootType != JsonType.OBJECT) {
                return schema;
            }
            rootClass = itemType;
            properties = schema.getItems().getProperties();

        } else {
            schema.setType(JsonType.OBJECT);
            properties = schema.getProperties();
        }
        this.project = this.xmlContext.getSession(rootClass).getProject();

        XMLDescriptor descriptor = (XMLDescriptor)project.getDescriptor(rootClass);

        Boolean includeRoot = Boolean.TRUE;
        if(contextProperties != null) {
            includeRoot = (Boolean) this.contextProperties.get(JAXBContextProperties.JSON_INCLUDE_ROOT);
            if(includeRoot == null) {
                includeRoot = Boolean.TRUE;
            }
        }
        if(Boolean.TRUE.equals(includeRoot)) {
            XMLField field = descriptor.getDefaultRootElementField();
            if(field != null) {
                rootProperty = new Property();
                rootProperty.setType(JsonType.OBJECT);
                rootProperty.setName(getNameForFragment(field.getXPathFragment()));
                properties.put(rootProperty.getName(), rootProperty);
                properties = rootProperty.getProperties();
            }
        }

        boolean allowsAdditionalProperties = hasAnyMappings(descriptor);
        if(descriptor.hasInheritance()) {
            //handle inheritence
            //schema.setAnyOf(new Property[descriptor.getInheritancePolicy().getAllChildDescriptors().size()]);
            List<ClassDescriptor> descriptors = this.getAllDescriptorsForInheritance(descriptor);
            Property[] props = new Property[descriptors.size()];
            for(int i = 0; i < props.length; i++) {
                XMLDescriptor nextDescriptor = (XMLDescriptor)descriptors.get(i);

                Property ref = new Property();
                ref.setRef(getReferenceForDescriptor(nextDescriptor, true));
                props[i] = ref;
            }
            if(rootProperty != null) {
                rootProperty.setAnyOf(props);
                rootProperty.setProperties(null);
                rootProperty.setType(null);
                rootProperty.setAdditionalProperties(null);
                rootProperty.setAdditionalProperties(null);
            } else {
                this.schema.setAnyOf(props);
                this.schema.setProperties(null);
                this.schema.setType(null);
                this.schema.setAdditionalProperties(null);
            }
        } else {
            JsonType type = populateProperties(properties, descriptor);
            if(type != null) {
                if(type == JsonType.BINARYTYPE) {
                    if(rootProperty != null) {
                        rootProperty.setAnyOf(getXopIncludeProperties());
                        rootProperty.setProperties(null);
                        rootProperty.setAdditionalProperties(null);
                        rootProperty.setType(null);
                    } else {
                        this.schema.setAnyOf(getXopIncludeProperties());
                        this.schema.setProperties(null);
                        this.schema.setType(null);
                        this.schema.setAdditionalProperties(null);
                    }
                } else if(type == JsonType.ENUMTYPE) {
                    if(rootProperty != null) {
                        rootProperty.setType(JsonType.STRING);
                        rootProperty.setProperties(null);
                        rootProperty.setEnumeration(getEnumeration(descriptor));
                    } else {
                        this.schema.setType(JsonType.STRING);
                        this.schema.setProperties(null);
                        this.schema.setEnumeration(getEnumeration(descriptor));
                }
                } else {
                    if(rootProperty != null) {
                        rootProperty.setType(type);
                    } else {
                        schema.setType(type);
                    }
                }
            } else {
                if(rootProperty != null) {
                    rootProperty.setAdditionalProperties(allowsAdditionalProperties);
                } else {
                    this.schema.setAdditionalProperties(allowsAdditionalProperties);
                }
            }
        }
        return schema;
    }

    private List<String> getEnumeration(XMLDescriptor desc) {
        return getEnumeration(getTextMapping(desc));
    }

    private List<String> getEnumeration(DatabaseMapping textMapping) {
        JAXBEnumTypeConverter converter = null;
        if(textMapping.isAbstractDirectMapping()) {
            converter = (JAXBEnumTypeConverter) ((DirectMapping)textMapping).getConverter();
        } else if(textMapping.isAbstractCompositeDirectCollectionMapping()) {
            converter = (JAXBEnumTypeConverter) ((DirectCollectionMapping)textMapping).getValueConverter();
        }
        if(converter == null) {
            return null;
        }
        List<String> enumeration = new ArrayList<String>();
        for(Object nextValue: converter.getAttributeToFieldValues().values()) {
            enumeration.add(nextValue.toString());
        }
        return enumeration;
    }

    private boolean hasAnyMappings(XMLDescriptor descriptor) {
        for(DatabaseMapping next:descriptor.getMappings()) {
            if(next instanceof XMLAnyAttributeMapping ||
                    next instanceof XMLAnyObjectMapping ||
                    next instanceof XMLAnyCollectionMapping ||
                    next instanceof XMLFragmentCollectionMapping ||
                    next instanceof XMLFragmentMapping

             ) {
                return true;
            } else if(next instanceof CompositeCollectionMapping) {
                CompositeCollectionMapping ccm = (CompositeCollectionMapping)next;
                if(ccm.getReferenceDescriptor() == null && ((XMLField)ccm.getField()).isSelfField()) {
                    return true;
                }
            } else if(next instanceof CompositeObjectMapping) {
                CompositeObjectMapping ccm = (CompositeObjectMapping)next;
                if(ccm.getReferenceDescriptor() == null && ((XMLField)ccm.getField()).isSelfField()) {
                    return true;
                }
            }
        }
        return false;
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
        if(mappings == null || mappings.isEmpty()) {
            return null;
        }

        if(isSimpleType(descriptor)) {
            //check for simple type
            DatabaseMapping mapping = getTextMapping(descriptor);
            if(mapping.isAbstractDirectMapping()) {
                DirectMapping directMapping = (DirectMapping)mapping;
                if(directMapping.getConverter() instanceof JAXBEnumTypeConverter) {
                    return JsonType.ENUMTYPE;
                }
                return getJsonTypeForJavaType(directMapping.getAttributeClassification());
            } else if(mapping.isAbstractCompositeDirectCollectionMapping()) {
                DirectCollectionMapping directMapping = (DirectCollectionMapping)mapping;
                if(directMapping.getValueConverter() instanceof JAXBEnumTypeConverter) {
                    return JsonType.ENUMTYPE;
                }
                Class type = directMapping.getAttributeElementClass();
                if(type == null) {
                    type = CoreClassConstants.STRING;
                }
                return getJsonTypeForJavaType(type);
            } else {
                //only other option is binary
                return JsonType.BINARYTYPE;
            }
        }
        for(DatabaseMapping next:mappings) {
            if(next instanceof ChoiceObjectMapping) {
                ChoiceObjectMapping coMapping = (ChoiceObjectMapping)next;
                for(Object nestedMapping:coMapping.getChoiceElementMappingsByClass().values()) {
                    Property prop = generateProperty((Mapping)nestedMapping, descriptor, properties);
                    if(prop != null && !(properties.containsKey(prop.getName()))) {
                        properties.put(prop.getName(), prop);
                    }
                }
            } else if(next instanceof ChoiceCollectionMapping) {
                ChoiceCollectionMapping coMapping = (ChoiceCollectionMapping)next;
                for(Object nestedMapping:coMapping.getChoiceElementMappingsByClass().values()) {
                    Property prop = generateProperty((Mapping)nestedMapping, descriptor, properties);
                    if(prop != null && !(properties.containsKey(prop.getName()))) {
                        properties.put(prop.getName(), prop);
                    }
                }
            } else {
                Property prop = generateProperty((Mapping)next, descriptor, properties);
                if(prop != null && !(properties.containsKey(prop.getName()))) {
                    properties.put(prop.getName(), prop);
                }
            }
        }
        return null;
    }

    private DatabaseMapping getTextMapping(XMLDescriptor descriptor) {
        for(DatabaseMapping next:descriptor.getMappings()) {
            if(next.isAbstractDirectMapping()) {
                DirectMapping mapping = (DirectMapping)next;
                if(((XMLField)mapping.getField()).getXPathFragment().nameIsText()) {
                    return next;
                }
            }
            if(next.isAbstractCompositeDirectCollectionMapping()) {
                DirectCollectionMapping mapping = (DirectCollectionMapping)next;
                if(((XMLField)mapping.getField()).getXPathFragment().nameIsText()) {
                    return next;
                }
            }
            if(next instanceof BinaryDataMapping) {
                BinaryDataMapping mapping = (BinaryDataMapping)next;
                if(((XMLField)mapping.getField()).isSelfField()) {
                    return next;
                }
            }
            if(next instanceof BinaryDataCollectionMapping) {
                BinaryDataCollectionMapping mapping = (BinaryDataCollectionMapping)next;
                if(((XMLField)mapping.getField()).isSelfField()) {
                    return next;
                }
            }
        }
        return null;
    }

    private boolean isSimpleType(XMLDescriptor descriptor) {
        DatabaseMapping mapping = null;
        if(descriptor.getMappings().size() == 1) {
            mapping = descriptor.getMappings().get(0);
        } else if(descriptor.getMappings().size() == 2) {
            boolean hasInverseRef = false;
            for(DatabaseMapping next:descriptor.getMappings()) {
                if(next instanceof InverseReferenceMapping) {
                    hasInverseRef = true;
                } else {
                    mapping = next;
                }
            }
            if(!hasInverseRef) {
                return false;
            }
        } else {
            return false;
        }

        if(mapping.isAbstractDirectMapping()) {
            if(((XMLField)mapping.getField()).getXPathFragment().nameIsText()) {
                return true;
            }
        }
        if(mapping.isAbstractCompositeDirectCollectionMapping()) {
            if(((XMLField)mapping.getField()).getXPathFragment().nameIsText()) {
                return true;
            }
        }
        if(mapping instanceof BinaryDataMapping) {
            if(((XMLField)mapping.getField()).isSelfField()) {
                return true;
            }
        }
        if(mapping instanceof BinaryDataCollectionMapping) {
            if(((XMLField)mapping.getField()).isSelfField()) {
                return true;
            }
        }
        return false;
    }

    private Property generateProperty(Mapping next, XMLDescriptor descriptor, Map<String, Property> properties) {
        Property prop = null;
        if(next.isCollectionMapping()) {
            if(next instanceof CollectionReferenceMapping) {
                CollectionReferenceMapping mapping = (CollectionReferenceMapping)next;
                Set<XMLField> sourceFields = mapping.getSourceToTargetKeyFieldAssociations().keySet();
                XMLDescriptor reference = (XMLDescriptor) mapping.getReferenceDescriptor();
                for(XMLField nextField: sourceFields) {
                    XPathFragment frag = nextField.getXPathFragment();
                    String propertyName = getNameForFragment(frag);

                    XMLField targetField = (XMLField) mapping.getSourceToTargetKeyFieldAssociations().get(nextField);
                    Class type = CoreClassConstants.STRING;
                    if(reference != null) {
                        type = getTypeForTargetField(targetField, reference);
                    }

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
                return prop;
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
                XMLDescriptor referenceDescriptor = (XMLDescriptor)mapping.getReferenceDescriptor();
                if(referenceDescriptor != null && referenceDescriptor.hasInheritance()) {
                    //handle inheritence
                    //schema.setAnyOf(new Property[descriptor.getInheritancePolicy().getAllChildDescriptors().size()]);
                    List<ClassDescriptor> descriptors = getAllDescriptorsForInheritance(referenceDescriptor);
                    Property[] props = new Property[descriptors.size()];
                    for(int i = 0; i < props.length; i++) {
                        XMLDescriptor nextDescriptor = null;
                        nextDescriptor = (XMLDescriptor)descriptors.get(i);
                        Property ref = new Property();
                        ref.setRef(getReferenceForDescriptor(nextDescriptor, true));
                        props[i] = ref;
                    }
                    nestedProperty.getItem().setAnyOf(props);
                } else {
                    nestedProperty.getItem().setRef(getReferenceForDescriptor(referenceDescriptor, false));
                    //populateProperties(nestedProperty.getItem().getProperties(), (XMLDescriptor)mapping.getReferenceDescriptor());
                }
            } else if(next.isAbstractCompositeDirectCollectionMapping()) {
                DirectCollectionMapping mapping = (DirectCollectionMapping)next;
                XMLField field = (XMLField)mapping.getField();
                XPathFragment frag = field.getXPathFragment();
                List<String> enumeration = null;
                if(mapping.getValueConverter() instanceof JAXBEnumTypeConverter) {
                    enumeration = getEnumeration((DatabaseMapping)next);
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
                Class type = mapping.getAttributeElementClass();
                if(type == null) {
                    type = CoreClassConstants.STRING;
                }
                nestedProperty.getItem().setType(getJsonTypeForJavaType(type));
                return prop;
            } else if(next instanceof BinaryDataCollectionMapping) {
                BinaryDataCollectionMapping mapping = (BinaryDataCollectionMapping)next;
                XMLField field = (XMLField)mapping.getField();
                XPathFragment frag = field.getXPathFragment();

                String propertyName = getNameForFragment(frag);
                if(frag.isSelfFragment()) {
                    propertyName = Constants.VALUE_WRAPPER;
                    if(this.contextProperties != null)  {
                        String valueWrapper = (String) this.contextProperties.get(JAXBContextProperties.JSON_VALUE_WRAPPER);
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
                    enumeration = getEnumeration((DatabaseMapping)directMapping);
                }
                String propertyName = getNameForFragment(frag);
                if(frag.nameIsText()) {
                    propertyName = Constants.VALUE_WRAPPER;
                    if(this.contextProperties != null)  {
                        String valueWrapper = (String) this.contextProperties.get(JAXBContextProperties.JSON_VALUE_WRAPPER);
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
                    Class type = CoreClassConstants.STRING;
                    if(reference != null) {
                        type = getTypeForTargetField(targetField, reference);
                    }

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
                return prop;
            } else if(next.isAbstractCompositeObjectMapping()) {
                CompositeObjectMapping mapping = (CompositeObjectMapping)next;
                XMLDescriptor nextDescriptor = (XMLDescriptor)mapping.getReferenceDescriptor();
                XMLField field = (XMLField)mapping.getField();
                XPathFragment firstFragment = field.getXPathFragment();
                if(firstFragment.isSelfFragment() || firstFragment.nameIsText()) {
                    if(nextDescriptor != null) {
                        populateProperties(properties, nextDescriptor);
                    }
                } else {
                    String propName = getNameForFragment(firstFragment);
                    prop = properties.get(propName);
                    if(prop == null) {
                        prop = new Property();
                        prop.setName(propName);
                    }
                    //prop.setType(JsonType.OBJECT);
                    prop.setName(propName);
                    Property nestedProperty = getNestedPropertyForFragment(firstFragment, prop);
                    XMLDescriptor referenceDescriptor = (XMLDescriptor)mapping.getReferenceDescriptor();
                    if(referenceDescriptor != null && referenceDescriptor.hasInheritance()) {
                        //handle inheritence
                        //schema.setAnyOf(new Property[descriptor.getInheritancePolicy().getAllChildDescriptors().size()]);
                        List<ClassDescriptor> descriptors = getAllDescriptorsForInheritance(referenceDescriptor);
                        Property[] props = new Property[descriptors.size()];
                        for(int i = 0; i < props.length; i++) {
                            XMLDescriptor nextDesc = (XMLDescriptor)descriptors.get(i);
                            Property ref = new Property();
                            ref.setRef(getReferenceForDescriptor(nextDesc, true));
                            props[i] = ref;
                        }
                        nestedProperty.setAnyOf(props);
                    } else {
                        nestedProperty.setRef(getReferenceForDescriptor(referenceDescriptor, false));
                        //populateProperties(nestedProperty.getItem().getProperties(), (XMLDescriptor)mapping.getReferenceDescriptor());
                    }                    //populateProperties(nestedProperty.getProperties(), nextDescriptor);
                }
            } else if(next instanceof BinaryDataMapping) {
                BinaryDataMapping binaryMapping = (BinaryDataMapping)next;
                XMLField field = (XMLField)binaryMapping.getField();
                XPathFragment frag = field.getXPathFragment();
                String propertyName = getNameForFragment(frag);
                if(frag.isSelfFragment()) {
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



    private String getReferenceForDescriptor(XMLDescriptor referenceDescriptor, boolean generateRoot) {
        if(referenceDescriptor == null) {
            return null;
        }
        String className = referenceDescriptor.getJavaClass().getSimpleName();
        String referenceName = DEFINITION_PATH + "/" + className;

        if(referenceDescriptor.getJavaClass() == this.rootClass && !generateRoot) {
            String ref = "#";
            if(this.rootProperty != null) {
                ref += "/properties/" + rootProperty.getName();
            }
            return ref;
        }
        if(!this.schema.getDefinitions().containsKey(className)) {
            Property definition = new Property();
            definition.setName(className);
            this.schema.getDefinitions().put(definition.getName(), definition);
            definition.setType(JsonType.OBJECT);
            if(referenceDescriptor.hasInheritance() && referenceDescriptor.getInheritancePolicy().hasClassIndicator()) {
                XMLField f = (XMLField)referenceDescriptor.getInheritancePolicy().getClassIndicatorField();
                Property indicatorProp = new Property();
                indicatorProp.setName(getNameForFragment(f.getXPathFragment()));
                indicatorProp.setType(JsonType.STRING);
                definition.getProperties().put(indicatorProp.getName(), indicatorProp);
            }
            JsonType jType = populateProperties(definition.getProperties(), referenceDescriptor);
            if(jType != null) {
                if(jType == JsonType.BINARYTYPE) {
                    definition.setAnyOf(getXopIncludeProperties());
                    definition.setProperties(null);
                    definition.setAdditionalProperties(null);
                    definition.setType(null);
                } else {
                    //this represents a simple type
                    definition.setType(jType);
                    definition.setProperties(null);
                }
            }
            definition.setAdditionalProperties(hasAnyMappings(referenceDescriptor));
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
        if(attributeClassification.isEnum()) {
            return JsonType.ENUMTYPE;
        }
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
        javaTypeToJsonType.put(Constants.QNAME_CLASS, JsonType.STRING);
        javaTypeToJsonType.put(Constants.URI, JsonType.STRING);
        javaTypeToJsonType.put(Constants.UUID, JsonType.STRING);

        javaTypeToJsonType.put(XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER, JsonType.BINARYTYPE);
        javaTypeToJsonType.put(XMLBinaryDataHelper.getXMLBinaryDataHelper().IMAGE, JsonType.BINARYTYPE);
        javaTypeToJsonType.put(XMLBinaryDataHelper.getXMLBinaryDataHelper().SOURCE, JsonType.BINARYTYPE);
        javaTypeToJsonType.put(XMLBinaryDataHelper.getXMLBinaryDataHelper().MULTIPART, JsonType.BINARYTYPE);

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
            propertyName = getNameForFragment(frag);
        }
        return null;
    }

    private Property[] getXopIncludeProperties() {
        if(this.xopIncludeProp == null) {
            this.initXopIncludeProp();
        }
        return this.xopIncludeProp;
    }

    private boolean isCollection(Class type) {
        if (CoreClassConstants.Collection_Class.isAssignableFrom(type)
                || CoreClassConstants.List_Class.isAssignableFrom(type)
                || CoreClassConstants.Set_Class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    private List<ClassDescriptor> getAllDescriptorsForInheritance(XMLDescriptor descriptor) {
        ArrayList<ClassDescriptor> descriptors = new ArrayList<ClassDescriptor>();
        QNameInheritancePolicy policy = (QNameInheritancePolicy) descriptor.getInheritancePolicy();
        descriptors.add(descriptor);
        descriptors.addAll(policy.getAllChildDescriptors());
        ClassDescriptor parent = policy.getParentDescriptor();
        while(parent != null) {
            descriptors.add(parent);
            parent = parent.getInheritancePolicy().getParentDescriptor();
        }
        return descriptors;

    }

}
