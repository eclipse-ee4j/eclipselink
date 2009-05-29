/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.annotations.*;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.JAXBEnumTypeConverter;
import org.eclipse.persistence.internal.jaxb.JAXBSetMethodAttributeAccessor;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.DomHandlerConverter;
import org.eclipse.persistence.internal.jaxb.MultiArgInstantiationPolicy;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.jaxb.JAXBElementAttributeAccessor;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.oxm.mappings.converters.XMLListConverter;
import org.eclipse.persistence.oxm.mappings.converters.XMLRootConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.internal.jaxb.XMLJavaTypeConverter;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.internal.libraries.asm.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>To generate a TopLink OXM Project based on Java Class and TypeInfo information
 * <p><b>Responsibilities:</b><ul>
 * <li>Generate a XMLDescriptor for each TypeInfo object</li>
 * <li>Generate a mapping for each TypeProperty object</li>
 * <li>Determine the correct mapping type based on the type of each property</li>
 * <li>Set up Converters on mappings for XmlAdapters or JDK 1.5 Enumeration types.</li>
 * </ul>
 * <p>This class is invoked by a Generator in order to create a TopLink Project.
 * This is generally used by JAXBContextFactory to create the runtime project. A Descriptor will
 * be generated for each TypeInfo and Mappings generated for each Property. In the case that a
 * non-transient property's type is a user defined class, a Descriptor and Mappings will be generated
 * for that class as well.
 * @see org.eclipse.persistence.jaxb.compiler.Generator
 * @see org.eclipse.persistence.jaxb.compiler.TypeInfo
 * @see org.eclipse.persistence.jaxb.compiler.Property 
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 *
 */
public class MappingsGenerator {
    private static String WRAPPER_CLASS = "org.eclipse.persistence.jaxb.generated";
    private static int wrapperCounter = 0;

    String outputDir = ".";
    private int nextNamespaceNumber = 0;
    private HashMap userDefinedSchemaTypes;
    private Helper helper;
    private JavaClass jotArrayList;
    private JavaClass jotHashSet;
    private HashMap<String, NamespaceInfo> packageToNamespaceMappings;
    private HashMap<String, TypeInfo> typeInfo;
    private HashMap<QName, Class> qNamesToGeneratedClasses;
    private HashMap<QName, Class> qNamesToDeclaredClasses;
    private HashMap<QName, ElementDeclaration> globalElements;
    
    public MappingsGenerator(Helper helper) {
        this.helper = helper;
        jotArrayList = helper.getJavaClass(ArrayList.class);
        jotHashSet = helper.getJavaClass(HashSet.class);               
        qNamesToGeneratedClasses = new HashMap<QName, Class>();
        qNamesToDeclaredClasses = new HashMap<QName, Class>();
    }
    
    public Project generateProject(ArrayList<JavaClass> typeInfoClasses, HashMap<String, TypeInfo> typeInfo, HashMap userDefinedSchemaTypes, HashMap<String, NamespaceInfo> packageToNamespaceMappings, HashMap<QName, ElementDeclaration> globalElements) throws Exception {
        this.typeInfo = typeInfo;
        this.userDefinedSchemaTypes = userDefinedSchemaTypes;
        this.packageToNamespaceMappings = packageToNamespaceMappings;
        this.globalElements = globalElements;
        Project project = new Project();

        // Generate descriptors
        for (JavaClass next : typeInfoClasses) {
            if (!next.isEnum()) {
                generateDescriptor(next, project);
            }
        }
        // Setup inheritance
        for (JavaClass next : typeInfoClasses) {
            if (!next.isEnum()) {
                setupInheritance(next);
            }
        }
        
        // Now create mappings
        generateMappings();
        processGlobalElements(project);
        wrapperCounter = 0;
        return project;
    }
    
    public void generateDescriptor(JavaClass javaClass, Project project) {
        String jClassName = javaClass.getQualifiedName();
        TypeInfo info = typeInfo.get(jClassName);
        if(info.isTransient()){
            return;
        }
        NamespaceInfo namespaceInfo = this.packageToNamespaceMappings.get(javaClass.getPackage().getQualifiedName());
        String packageNamespace = namespaceInfo.getNamespace();
        String elementName;
        String namespace;

        if(javaClass.getSuperclass() != null && javaClass.getSuperclass().getName().equals("javax.xml.bind.JAXBElement")) {
            generateDescriptorForJAXBElementSubclass(javaClass, project, namespaceInfo.getNamespaceResolver());
            return;
        }
        
        XMLDescriptor descriptor = new XMLDescriptor();
        
        XmlRootElement rootElem = (XmlRootElement) helper.getAnnotation(javaClass, XmlRootElement.class);
        if (rootElem == null) {
            elementName = Introspector.decapitalize(jClassName.substring(jClassName.lastIndexOf(".") + 1));
            namespace = packageNamespace;
            descriptor.setResultAlwaysXMLRoot(true);
        } else {
            elementName = rootElem.name();
            if (elementName.equals("##default")) {
                elementName = Introspector.decapitalize(jClassName.substring(jClassName.lastIndexOf(".") + 1));
            }
            namespace = rootElem.namespace();
            descriptor.setResultAlwaysXMLRoot(false);
        }
                
        descriptor.setJavaClassName(jClassName);

        if(info.getFactoryMethodName() != null) {
            descriptor.getInstantiationPolicy().useFactoryInstantiationPolicy(info.getObjectFactoryClassName(), info.getFactoryMethodName());
        }
        
        if (namespace.equals("##default")) {
            namespace = namespaceInfo.getNamespace();
        }
        if (namespace.equals("")) {
            descriptor.setDefaultRootElement(elementName);
        } else {
            descriptor.setDefaultRootElement(getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver()) + ":" + elementName);
        }
        descriptor.setNamespaceResolver(namespaceInfo.getNamespaceResolver());
        project.addDescriptor(descriptor);
        info.setDescriptor(descriptor);
    }

    public void generateDescriptorForJAXBElementSubclass(JavaClass javaClass, Project project, NamespaceResolver nsr) {
        String jClassName = javaClass.getQualifiedName();
        TypeInfo info = typeInfo.get(jClassName);

        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClassName(jClassName);
        
        String[] factoryMethodParamTypes = info.getFactoryMethodParamTypes();
        
        MultiArgInstantiationPolicy policy = new MultiArgInstantiationPolicy();
        policy.useFactoryInstantiationPolicy(info.getObjectFactoryClassName(), info.getFactoryMethodName());
        policy.setParameterTypeNames(factoryMethodParamTypes);
        policy.setDefaultValues(new String[]{null});
        
        xmlDescriptor.setInstantiationPolicy(policy);
        JavaClass paramClass = helper.getJavaClass(factoryMethodParamTypes[0]);        
        if(helper.isBuiltInJavaType(paramClass)){        
            XMLDirectMapping mapping = new XMLDirectMapping();
            mapping.setAttributeName("value");
            mapping.setGetMethodName("getValue");
            mapping.setSetMethodName("setValue");
            mapping.setXPath("text()");
            Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(factoryMethodParamTypes[0], getClass().getClassLoader());
            mapping.setAttributeClassification(attributeClassification);                        
            xmlDescriptor.addMapping(mapping);
        }else{
            XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
            mapping.setAttributeName("value");
            mapping.setGetMethodName("getValue");
            mapping.setSetMethodName("setValue");
            mapping.setXPath(".");          
            mapping.setReferenceClassName(factoryMethodParamTypes[0]);
            xmlDescriptor.addMapping(mapping);
        }
        xmlDescriptor.setNamespaceResolver(nsr);
        
        project.addDescriptor(xmlDescriptor);
        info.setDescriptor(xmlDescriptor);
    }
    public void generateMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        if (property.getAdapterClass() != null) {
            // need to check the adapter to determine whether we require a
            // direct mapping (anything we can create a descriptor for) or
            // a composite mapping
            JavaClass adapterClass = property.getAdapterClass();
            JavaClass valueType = helper.getJavaClass(Object.class);

            // look for marshal method
            for (JavaMethod method : new ArrayList<JavaMethod>(adapterClass.getDeclaredMethods())) {
                if (method.getName().equals("marshal")) {
                    JavaClass returnType = (JavaClass) method.getReturnType();
                    if (!returnType.getQualifiedName().equals(valueType.getQualifiedName())) {
                        valueType = returnType;
                        break;
                    }
                }
            }

            // if the value type is something we have a descriptor for, create
            // a composite object mapping, otherwise create a direct mapping
            if (typeInfo.containsKey(valueType.getQualifiedName())) {
                if (isCollectionType(property)) {
                    generateCompositeCollectionMapping(property, descriptor, namespaceInfo, valueType.getQualifiedName()).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    generateCompositeObjectMapping(property, descriptor, namespaceInfo, valueType.getQualifiedName()).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                }
            } else {
                if (isCollectionType(property)) {
                    generateDirectCollectionMapping(property, descriptor, namespaceInfo).setValueConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                        generateBinaryMapping(property, descriptor, namespaceInfo).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    } else {
                        generateDirectMapping(property, descriptor, namespaceInfo).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    }
                }
            }
            return;
        }

        if (property.isChoice()) {
            if(this.isCollectionType(property)) {
                generateChoiceCollectionMapping(property, descriptor, namespaceInfo);
            } else {
                generateChoiceMapping(property, descriptor, namespaceInfo);
            }
        } else if(property.isAny()) {
            if(isCollectionType(property)){
                generateAnyCollectionMapping(property, descriptor, namespaceInfo, false);
            }else{
                generateAnyObjectMapping(property, descriptor, namespaceInfo);
            }
            
        } else if(property.isReference()) {
            generateMappingForReferenceProperty((ReferenceProperty)property, descriptor, namespaceInfo);            
        }else if (isMapType(property) && helper.isAnnotationPresent(property.getElement(), XmlAnyAttribute.class)) {
            generateAnyAttributeMapping(property, descriptor, namespaceInfo);
        } else if (isCollectionType(property)) {
            generateCollectionMapping(property, descriptor, namespaceInfo);
        } else {
            JavaClass referenceClass = property.getType();
            TypeInfo reference = typeInfo.get(referenceClass.getQualifiedName());
            if (reference != null) {
                if (helper.isAnnotationPresent(property.getElement(), XmlIDREF.class)) {
                    generateXMLObjectReferenceMapping(property, descriptor, namespaceInfo, referenceClass);
                } else {
                    if (reference.isEnumerationType()) {
                        generateDirectEnumerationMapping(property, descriptor, namespaceInfo, (EnumTypeInfo) reference);
                    } else {
                        generateCompositeObjectMapping(property, descriptor, namespaceInfo, referenceClass.getQualifiedName());
                    }
                }
            } else {
                if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                    generateBinaryMapping(property, descriptor, namespaceInfo);
                } else {
                    if (referenceClass.getQualifiedName().equals("java.lang.Object")) {
                        XMLCompositeObjectMapping coMapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, null);
                        coMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                    } else {
                        generateDirectMapping(property, descriptor, namespaceInfo);
                    }
                }
            }
        }
    }
    
    public XMLChoiceObjectMapping generateChoiceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        ChoiceProperty prop = (ChoiceProperty)property;
        XMLChoiceObjectMapping mapping = new XMLChoiceObjectMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        Iterator<Property> choiceProperties = prop.getChoiceProperties().iterator();
        while(choiceProperties.hasNext()) {
            Property next = choiceProperties.next();
            JavaClass type = next.getType();
            XMLField xpath = getXPathForField(next, namespace, !(this.typeInfo.containsKey(type.getQualifiedName())));
            mapping.addChoiceElement(xpath.getName(), type.getQualifiedName(), false);
        }
        descriptor.addMapping(mapping);
        return mapping;
    }
    
    public XMLChoiceCollectionMapping generateChoiceCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        ChoiceProperty prop = (ChoiceProperty)property;
        XMLChoiceCollectionMapping mapping = new XMLChoiceCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        JavaClass collectionType = property.getType();
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());
        
        Iterator<Property> choiceProperties = prop.getChoiceProperties().iterator();
        while(choiceProperties.hasNext()) {
            Property next = choiceProperties.next();
            JavaClass type = next.getType();
            XMLField xpath = getXPathForField(next, namespace, !(this.typeInfo.containsKey(type.getQualifiedName())));
            mapping.addChoiceElement(xpath.getName(), type.getQualifiedName());
        }
        descriptor.addMapping(mapping);
        return mapping;
    }
    
    public XMLMapping generateMappingForReferenceProperty(ReferenceProperty property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
                
        boolean isCollection = isCollectionType(property);
        DatabaseMapping mapping;
        if(isCollection) {
            mapping = new XMLChoiceCollectionMapping();
        } else {
            mapping = new XMLChoiceObjectMapping();
        }
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                ((XMLMapping)mapping).setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }

        List<ElementDeclaration> referencedElements = property.getReferencedElements();
        boolean hasJAXBElements = false;
        AttributeAccessor mappingAccessor = mapping.getAttributeAccessor();
        Map<QName, Class> qNamesToScopeClass = new HashMap<QName, Class>();
        for(ElementDeclaration element:referencedElements) {
            QName elementName = element.getElementName();
            boolean isText = !(this.typeInfo.containsKey(element.getJavaTypeName())) && !(element.getJavaTypeName().equals("java.lang.Object"));            
            XMLField xmlField = this.getXPathForElement("", elementName, namespaceInfo, isText);
            if(isCollection){
                ((XMLChoiceCollectionMapping)mapping).addChoiceElement(xmlField, element.getJavaTypeName());
                XMLMapping nestedMapping = ((XMLChoiceCollectionMapping)mapping).getChoiceElementMappings().get(xmlField);
                if(((DatabaseMapping)nestedMapping).isAbstractCompositeCollectionMapping()){
                    ((XMLCompositeCollectionMapping)nestedMapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                }
                if (element.isList() && ((DatabaseMapping)nestedMapping).isAbstractCompositeDirectCollectionMapping()) {
                    XMLListConverter listConverter = new XMLListConverter();
                    listConverter.setObjectClassName(element.getJavaType().getQualifiedName());
                    ((XMLCompositeDirectCollectionMapping)nestedMapping).setValueConverter(listConverter);
                }
            }else{
                ((XMLChoiceObjectMapping)mapping).addChoiceElement(xmlField, element.getJavaTypeName());
                XMLMapping nestedMapping = ((XMLChoiceObjectMapping)mapping).getChoiceElementMappings().get(xmlField);
                if(((DatabaseMapping)nestedMapping).isAbstractCompositeObjectMapping()){
                    ((XMLCompositeObjectMapping)nestedMapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                }
 
            }
            
            if(!element.isXmlRootElement()) {
                XMLRootConverter converter = new XMLRootConverter(xmlField);
                if(isCollection){
                    ((XMLChoiceCollectionMapping)mapping).addConverter(xmlField, converter);
                }else{
                    ((XMLChoiceObjectMapping)mapping).addConverter(xmlField, converter);
                }
                qNamesToScopeClass.put(elementName, element.getScopeClass());
            }
            hasJAXBElements = hasJAXBElements || !element.isXmlRootElement();           
        }
        if(hasJAXBElements) {
            JAXBElementAttributeAccessor accessor;
            if(isCollection){
                accessor = new JAXBElementAttributeAccessor(mappingAccessor, mapping.getContainerPolicy());
            }else{
                accessor = new JAXBElementAttributeAccessor(mappingAccessor);
            }
            accessor.setQNamesToScopes(qNamesToScopeClass);
            mapping.setAttributeAccessor(accessor);
        }
        descriptor.addMapping(mapping);
        return (XMLMapping)mapping;

    }
    
    
    public XMLAnyCollectionMapping generateAnyCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, boolean isMixed) {
        boolean isLax = false;
        Class domHandlerClass = null;
        
        if(property instanceof AnyProperty) {
            AnyProperty prop = (AnyProperty)property;
            isLax = prop.isLax();
            domHandlerClass = prop.getDomHandlerClass();
        }
        XMLAnyCollectionMapping  mapping = new XMLAnyCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        if(!isMixed) {
            mapping.setUseXMLRoot(true);
        }
        mapping.setAttributeAccessor(new JAXBElementAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy()));
        if(isLax) {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        } else {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
        }
        if(domHandlerClass != null) {
            DomHandlerConverter converter = new DomHandlerConverter(domHandlerClass);
            mapping.setConverter(converter);
        }
        descriptor.addMapping(mapping);
        mapping.setMixedContent(isMixed);
        if(isMixed) {
            mapping.setPreserveWhitespaceForMixedContent(true);
        }
        return mapping;
    }
    
    public XMLCompositeObjectMapping generateCompositeObjectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, String referenceClassName) {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setReferenceClassName(referenceClassName);
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        if(property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
        }
        mapping.setXPath(getXPathForField(property, namespaceInfo, false).getXPath());
        if(helper.isAnnotationPresent(property.getElement(), XmlContainerProperty.class)) {
            XmlContainerProperty containerProp = (XmlContainerProperty)helper.getAnnotation(property.getElement(), XmlContainerProperty.class);
            String name = containerProp.value();
            mapping.setContainerAttributeName(name);
            if(!containerProp.getMethodName().equals("") && !containerProp.setMethodName().equals("")) {
                mapping.setContainerGetMethodName(containerProp.getMethodName());
                mapping.setContainerSetMethodName(containerProp.setMethodName());
            }
        }

        descriptor.addMapping(mapping);
        return mapping;
        
    }
    public XMLDirectMapping generateDirectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName(property.getPropertyName());

        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        if(property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
        }
        mapping.setField(getXPathForField(property, namespaceInfo, true));
        if(XMLConstants.QNAME_QNAME.equals(property.getSchemaType())){
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
        }
        descriptor.addMapping(mapping);
        return mapping;
    }
    
    public XMLBinaryDataMapping generateBinaryMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        mapping.setField(getXPathForField(property, namespaceInfo, false));
        if (property.isSwaAttachmentRef()) {
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.SWA_REF_QNAME);
            mapping.setSwaRef(true);
        } else if (property.isMtomAttachment()) {
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlInlineBinaryData.class)) {
            mapping.setShouldInlineBinaryData(true);
        }
        // use a non-dynamic implementation of MimeTypePolicy to wrap the MIME string
        mapping.setMimeTypePolicy(new FixedMimeTypePolicy(property.getMimeType()));
        descriptor.addMapping(mapping);
        
        return mapping;
    }
    
    public void generateDirectEnumerationMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, EnumTypeInfo enumInfo) {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setConverter(buildJAXBEnumTypeConverter(mapping, enumInfo));
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        mapping.setField(getXPathForField(property, namespaceInfo, true));
   
        descriptor.addMapping(mapping);
    }
    
    private JAXBEnumTypeConverter buildJAXBEnumTypeConverter(DatabaseMapping mapping, EnumTypeInfo enumInfo){
        JAXBEnumTypeConverter converter = new JAXBEnumTypeConverter(mapping, enumInfo.getClassName(), false);
        List<Object> objects = enumInfo.getObjectValues();
        List<String> fieldValues = enumInfo.getFieldValues();
        for (int i=0; i< objects.size(); i++) {         
            converter.addConversionValue(fieldValues.get(i), objects.get(i));
        }
        return converter;
    }
    
    public void generateCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        // check to see if this should be a composite or direct mapping
        JavaClass javaClass = null;
        if (property.getGenericType() != null) {
            javaClass = (JavaClass) property.getGenericType();
        }
       
        if (property.isMixedContent()) {
            generateAnyCollectionMapping(property, descriptor, namespaceInfo, true);
            return;
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
            XmlElement xmlElement = (XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class);
            if (xmlElement.type() != XmlElement.DEFAULT.class) {
                javaClass = helper.getJavaClass(xmlElement.type());
            }
        }
        if (javaClass != null && typeInfo.get(javaClass.getQualifiedName()) != null) {
            TypeInfo referenceInfo = typeInfo.get(javaClass.getQualifiedName());
            if (referenceInfo.isEnumerationType()) {
                generateEnumCollectionMapping(property,  descriptor, namespaceInfo,(EnumTypeInfo) referenceInfo);
            } else {
                if (helper.isAnnotationPresent(property.getElement(), XmlIDREF.class)) {
                    generateXMLCollectionReferenceMapping(property, descriptor, namespaceInfo, javaClass);
                } else {
                    generateCompositeCollectionMapping(property, descriptor, namespaceInfo, javaClass.getQualifiedName());
                }
            }
        } else if(!property.isAttribute() && javaClass != null && javaClass.getQualifiedName().equals("java.lang.Object")){         
            XMLCompositeCollectionMapping ccMapping = generateCompositeCollectionMapping(property, descriptor, namespaceInfo, null);
            ccMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        } else {
            generateDirectCollectionMapping(property, descriptor, namespaceInfo);
        }
    } 
    
    public void generateEnumCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, EnumTypeInfo info) {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }

        mapping.setValueConverter(buildJAXBEnumTypeConverter(mapping, info));
        
        JavaClass collectionType = property.getType();
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());
        
        mapping.setField(getXPathForField(property, namespaceInfo, true));
        if (helper.isAnnotationPresent(property.getElement(), XmlList.class)) {         
            mapping.setUsesSingleNode(true);
        }        
        
        descriptor.addMapping(mapping);
    }

    public void generateAnyAttributeMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        mapping.setSchemaInstanceIncluded(false);
        mapping.setNamespaceDeclarationIncluded(false);
        descriptor.addMapping(mapping);
    }
    
    public void generateAnyObjectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLAnyObjectMapping mapping = new XMLAnyObjectMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setMixedContent(false);
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        if(property.getType().getQualifiedName().equals("org.w3c.dom.Element")){
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);           
        }else{
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);   
        }
        mapping.setUseXMLRoot(true);
        mapping.setAttributeAccessor(new JAXBElementAttributeAccessor(mapping.getAttributeAccessor()));
        descriptor.addMapping(mapping);
    }
    
    protected boolean areEquals(JavaClass src, Class tgt) {
        if (src == null || tgt == null) {
            return false;
        }
        return src.getRawName().equals(tgt.getCanonicalName());
    }
    
    public XMLCompositeCollectionMapping generateCompositeCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, String referenceClassName) {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        mapping.setReferenceClassName(referenceClassName);
        if(property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
        }
        JavaClass collectionType = property.getType();
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
      
        mapping.useCollectionClassName(collectionType.getRawName());
        XMLField xmlField = getXPathForField(property, namespaceInfo, false);
        mapping.setXPath(xmlField.getXPath());
        if(helper.isAnnotationPresent(property.getElement(), XmlContainerProperty.class)) {
            XmlContainerProperty containerProp = (XmlContainerProperty)helper.getAnnotation(property.getElement(), XmlContainerProperty.class);
            String name = containerProp.value();
            mapping.setContainerAttributeName(name);
            if(!containerProp.getMethodName().equals("") && !containerProp.setMethodName().equals("")) {
                mapping.setContainerGetMethodName(containerProp.getMethodName());
                mapping.setContainerSetMethodName(containerProp.setMethodName());
            }
        }
        
        descriptor.addMapping(mapping);
        
        return mapping;
    }
    
    public XMLCompositeDirectCollectionMapping generateDirectCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        JavaClass collectionType = property.getType();
        
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());
        XMLField xmlField = getXPathForField(property, namespaceInfo, true);
        mapping.setField(xmlField);
        if(helper.isAnnotationPresent(property.getElement(), XmlMixed.class)) {
            xmlField.setXPath("text()");
        }
        
        if(XMLConstants.QNAME_QNAME.equals(property.getSchemaType())){
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
        }
        
        if(xmlField.getXPathFragment().isAttribute()){
            mapping.setUsesSingleNode(true);
        }                
        if (helper.isAnnotationPresent(property.getElement(), XmlList.class)) {
            mapping.setUsesSingleNode(true);
        }
        descriptor.addMapping(mapping);
        return mapping;
    }

    public String getPrefixForNamespace(String URI, org.eclipse.persistence.oxm.NamespaceResolver namespaceResolver) {
        Enumeration keys = namespaceResolver.getPrefixes();
        while (keys.hasMoreElements()) {
            String next = (String) keys.nextElement();
            String nextUri = namespaceResolver.resolveNamespacePrefix(next);
            if (nextUri.equals(URI)) {
                return next;
            }
        }
        String prefix = "ns" + nextNamespaceNumber;
        nextNamespaceNumber++;
        namespaceResolver.put(prefix, URI);
        return prefix;
    }

    public boolean isCollectionType(Property field) {
        JavaClass type = field.getType();
        if (helper.getJavaClass(Collection.class).isAssignableFrom(type) 
                || helper.getJavaClass(List.class).isAssignableFrom(type) 
                || helper.getJavaClass(Set.class).isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    /**
     * Setup inheritance for abstract superclass.  
     * 
     * NOTE: We currently only handle one level of inheritance in this case.  
     * For multiple levels the code will need to be modified. The logic in 
     * generateMappings() that determines when to copy down inherited 
     * methods from the parent class will need to be changed as well. 
     * 
     * @param jClass
     */
    private void setupInheritance(JavaClass jClass) {
        XMLDescriptor descriptor = typeInfo.get(jClass.getName()).getDescriptor();
        NamespaceInfo namespaceInfo = this.packageToNamespaceMappings.get(jClass.getPackageName());
        if (descriptor == null) {
            return;
        }
           
        JavaClass superClass = getNextMappedSuperClass(jClass, descriptor, namespaceInfo);
        if(superClass == null){
            return;
        }
    
        TypeInfo superTypeInfo =  typeInfo.get(superClass.getName());
        XMLDescriptor superDescriptor = superTypeInfo.getDescriptor();
        if (superDescriptor != null) {                          
            XMLSchemaReference sRef = descriptor.getSchemaReference();
            if (sRef == null || sRef.getSchemaContext() == null) {
                return;
            }         
            
            JavaClass rootMappedSuperClass = getRootMappedSuperClass(superClass, descriptor, namespaceInfo);                  
            
            TypeInfo rootTypeInfo =  typeInfo.get(rootMappedSuperClass.getName());

            XMLDescriptor rootDescriptor = rootTypeInfo.getDescriptor();
            if (rootDescriptor.getNamespaceResolver() == null) {
                rootDescriptor.setNamespaceResolver(new NamespaceResolver());
            }
            
            if(rootDescriptor.getInheritancePolicy().getClassIndicatorField() == null){
                String prefix = rootDescriptor.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
                if(prefix == null){
                    prefix = rootDescriptor.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
                }
                XMLField classIndicatorField = new XMLField("@"+ prefix + ":type");
                rootDescriptor.getNamespaceResolver().put(prefix, XMLConstants.SCHEMA_INSTANCE_URL); 
                rootDescriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);                  
            }
                        
            String sCtx = sRef.getSchemaContext();
            if (sCtx.length() > 1 && sCtx.startsWith("/")) {
                sCtx = sCtx.substring(1);
            }
            descriptor.getInheritancePolicy().setParentClassName(superClass.getName());
            rootDescriptor.getInheritancePolicy().addClassNameIndicator(jClass.getName(), sCtx);
            Object value = rootDescriptor.getInheritancePolicy().getClassNameIndicatorMapping().get(rootDescriptor.getJavaClassName());
            if(value == null){
                XMLSchemaReference rootSRef = rootDescriptor.getSchemaReference();
                if (rootSRef != null && rootSRef.getSchemaContext() != null) {
                    String rootSCtx = rootSRef.getSchemaContext();
                    if (rootSCtx.length() > 1 && rootSCtx.startsWith("/")) {
                        rootSCtx = rootSCtx.substring(1);
                    }
                    rootDescriptor.getInheritancePolicy().addClassNameIndicator(rootDescriptor.getJavaClassName(), rootSCtx);
                }                   
            }
            rootDescriptor.getInheritancePolicy().setShouldReadSubclasses(true);  
        }                      
    }
    
    private JavaClass getNextMappedSuperClass(JavaClass jClass, XMLDescriptor descriptor, NamespaceInfo namespaceInfo){ 
        JavaClass superClass = jClass.getSuperclass();
        
        if(superClass == null){
            return null;
        }
        
        TypeInfo superTypeInfo =  typeInfo.get(superClass.getName());
        if(superTypeInfo == null){
            return null;
        }
        
        if(superTypeInfo.isTransient()){    
            generateMappings(superTypeInfo, descriptor, namespaceInfo);
            return getNextMappedSuperClass(superClass, descriptor, namespaceInfo);
        }
        return superClass;
    }
    
    private JavaClass getRootMappedSuperClass(JavaClass javaClass, XMLDescriptor descriptor, NamespaceInfo namespaceInfo){
        JavaClass rootMappedSuperClass = javaClass;
        
        JavaClass nextMappedSuperClass = rootMappedSuperClass;
        while(nextMappedSuperClass != null){
            nextMappedSuperClass = getNextMappedSuperClass(nextMappedSuperClass, descriptor, namespaceInfo);
            if(nextMappedSuperClass == null){
                return rootMappedSuperClass;
            }
            rootMappedSuperClass = nextMappedSuperClass;
        }
        
        return rootMappedSuperClass;        
    }
    
    public void generateMappings() {
        Iterator javaClasses = this.typeInfo.keySet().iterator();
        while (javaClasses.hasNext()) {
            String next = (String)javaClasses.next();
            JavaClass javaClass = helper.getJavaClass(next);
            TypeInfo info = (TypeInfo) this.typeInfo.get(next);
            NamespaceInfo namespaceInfo = this.packageToNamespaceMappings.get(javaClass.getPackageName());
            if (info.isEnumerationType()) {
                continue;
            }
            XMLDescriptor descriptor = info.getDescriptor();
            if (descriptor != null) {
                generateMappings(info, descriptor, namespaceInfo);
            }
        }
    }

    public void generateMappings(TypeInfo info, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {    
    	List<Property> propertiesInOrder = info.getNonTransientPropertiesInPropOrder();
    	for (int i = 0; i < propertiesInOrder.size(); i++) {
    		Property next = propertiesInOrder.get(i);
    		if(next != null){
            	generateMapping(next, descriptor, namespaceInfo);
            }
    	}
    }

    /**
     * Create an XMLCollectionReferenceMapping and add it to the descriptor.
     * 
     * @param property
     * @param descriptor
     * @param namespaceInfo
     * @param referenceClass
     */
    public void generateXMLCollectionReferenceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        XMLField srcXPath = getXPathForField(property, namespaceInfo, true);
        XMLCollectionReferenceMapping mapping = new XMLCollectionReferenceMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        mapping.setReferenceClassName(referenceClass.getQualifiedName());

        JavaClass collectionType = property.getType();
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());

        // here we need to setup source/target key field associations
        TypeInfo referenceType = typeInfo.get(referenceClass.getQualifiedName());
        if (referenceType.isIDSet()) {
            Property prop = referenceType.getIDProperty();
            XMLField tgtXPath = getXPathForField(prop, namespaceInfo, !(helper.isAnnotationPresent(prop.getElement(), XmlAttribute.class)));
            mapping.addSourceToTargetKeyFieldAssociation(srcXPath.getXPath(), tgtXPath.getXPath());
        }

        // TODO: if reference class is not in typeinfo list OR the ID is not
        // set, throw an exception...
        descriptor.addMapping(mapping);
    }
    /**
     * Create an XMLObjectReferenceMapping and add it to the descriptor.
     * 
     * @param property
     * @param descriptor
     * @param namespaceInfo
     * @param referenceClass
     */
    public void generateXMLObjectReferenceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        XMLField srcXPath = getXPathForField(property, namespaceInfo, true);

        XMLObjectReferenceMapping mapping = new XMLObjectReferenceMapping();
        mapping.setAttributeName(property.getPropertyName());
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method 
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                mapping.setIsWriteOnly(true);
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        mapping.setReferenceClassName(referenceClass.getQualifiedName());

        // here we need to setup source/target key field associations
        TypeInfo referenceType = typeInfo.get(referenceClass.getQualifiedName());
        if (referenceType.isIDSet()) {
            Property prop = referenceType.getIDProperty();
            XMLField tgtXPath = getXPathForField(prop, namespaceInfo, !(helper.isAnnotationPresent(prop.getElement(), XmlAttribute.class)));
            mapping.addSourceToTargetKeyFieldAssociation(srcXPath.getXPath(), tgtXPath.getXPath());
        }

        // TODO: if reference class is not in typeinfo list OR the ID is not
        // set, throw an exception...
        descriptor.addMapping(mapping);
    }
    
    public XMLField getXPathForField(Property property, NamespaceInfo namespaceInfo, boolean isTextMapping) {
        String xPath = "";
        XMLField xmlField = null;
        if (helper.isAnnotationPresent(property.getElement(), XmlElementWrapper.class)) {
            XmlElementWrapper wrapper = (XmlElementWrapper) helper.getAnnotation(property.getElement(), XmlElementWrapper.class);
            String namespace = wrapper.namespace();
            if (namespace.equals("##default")) {
                if (namespaceInfo.isElementFormQualified()) {
                    namespace = namespaceInfo.getNamespace();
                } else {
                    namespace = "";
                }
            }
            if (namespace.equals("")) {
                xPath += (wrapper.name() + "/");
            } else {
                xPath += (getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver()) + ":" + wrapper.name() + "/");
            }
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlAttribute.class)) {
            QName name = property.getSchemaName();
            String namespace = "";
            if (namespaceInfo.isAttributeFormQualified()) {
                namespace = namespaceInfo.getNamespace();
            }
            if (!name.getNamespaceURI().equals("")) {
                namespace = name.getNamespaceURI();
            }
            if (namespace.equals("")) {
                xPath += ("@" + name.getLocalPart());
            } else {
                String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver());
                xPath += ("@" + prefix + ":" + name.getLocalPart());
            }
            QName schemaType = (QName) userDefinedSchemaTypes.get(property.getClass());
            if (property.getSchemaType() != null) {
                schemaType = property.getSchemaType();
            }
            if (schemaType == null) {
                schemaType = (QName) helper.getXMLToJavaTypeMap().get(property.getType().getRawName());
            }
            XMLField field = new XMLField(xPath);
            field.setSchemaType(schemaType);
            return field;
        } else if (helper.isAnnotationPresent(property.getElement(), XmlValue.class)) {
            xPath = "text()";
            XMLField field = new XMLField(xPath);
            QName schemaType = (QName) userDefinedSchemaTypes.get(property.getType());
            if (property.getSchemaType() != null) {
                schemaType = property.getSchemaType();
            }
            if (schemaType == null) {
                schemaType = (QName) helper.getXMLToJavaTypeMap().get(property.getType());
            }
            field.setSchemaType(schemaType);
            return field;
        } else {
            QName elementName = property.getSchemaName();
            xmlField = getXPathForElement(xPath, elementName, namespaceInfo, isTextMapping);

            QName schemaType = (QName) userDefinedSchemaTypes.get(property.getType());
            if (property.getSchemaType() != null) {
                schemaType = property.getSchemaType();
            }
            if (schemaType == null) {
                schemaType = (QName) helper.getXMLToJavaTypeMap().get(property.getType());
            }
            xmlField.setSchemaType(schemaType);
        }
        return xmlField;
    }

    public XMLField getXPathForElement(String path, QName elementName, NamespaceInfo namespaceInfo, boolean isText) {
        String namespace = "";
        if (!elementName.getNamespaceURI().equals("")) {
            namespace = elementName.getNamespaceURI();
        }
        if (namespace.equals("")) {
            path += elementName.getLocalPart();
            if (isText) {
                path += "/text()";
            }
        } else {
            String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver());
            path += prefix + ":" + elementName.getLocalPart();
            if (isText) {
                path += "/text()";
            }
        }
        XMLField xmlField = new XMLField(path);
        return xmlField;
    }
    
    public Property getXmlValueFieldForSimpleContent(ArrayList<Property> properties) {
        boolean foundValue = false;
        boolean foundNonAttribute = false;
        Property valueField = null;

        for (Property prop : properties) {
            if (helper.isAnnotationPresent(prop.getElement(), XmlValue.class)) {
                foundValue = true;
                valueField = prop;
            } else if (!helper.isAnnotationPresent(prop.getElement(), XmlAttribute.class) && !helper.isAnnotationPresent(prop.getElement(), XmlTransient.class) && !helper.isAnnotationPresent(prop.getElement(), XmlAnyAttribute.class)) {
                foundNonAttribute = true;
            }
        }
        if (foundValue && !foundNonAttribute) {
            return valueField;
        }
        return null;
    }
    
    public void processSchemaType(XmlSchemaType type) {
        String schemaTypeName = type.name();
        Class javaType = type.type();

        if (javaType == null) {
            return;
        }
        QName typeQName = new QName(type.namespace(), schemaTypeName);
        this.userDefinedSchemaTypes.put(javaType, typeQName);
    }
   
    public String getSchemaTypeNameForClassName(String className) {
        String typeName = Introspector.decapitalize(className.substring(className.lastIndexOf('.') + 1));
        return typeName;
    }
    
    public boolean isMapType(Property property) {
        JavaClass mapCls = helper.getJavaClass(java.util.Map.class);
        return mapCls.isAssignableFrom(property.getType());
    }
    
    public void processGlobalElements(Project project) {
        //Find any global elements for classes we've generated descriptors for, and add them as possible
        //root elements.
        if(this.globalElements == null) {
            return;
        }
        Iterator<QName> keys = this.globalElements.keySet().iterator();
        while(keys.hasNext()) {
            QName next = keys.next();
            ElementDeclaration nextElement = this.globalElements.get(next);
            String nextClassName = nextElement.getJavaTypeName();
            TypeInfo type = this.typeInfo.get(nextClassName);
                
            if(helper.isBuiltInJavaType(nextElement.getJavaType()) || (type !=null && type.isEnumerationType())){
                //generate a class/descriptor for this element
                
                String namespaceUri = next.getNamespaceURI();
                if(namespaceUri == null || namespaceUri.equals("##default")) {
                    namespaceUri = "";
                }

                String attributeTypeName = nextClassName;
                if (nextElement.getAdaptedJavaTypeName() != null) {
                    attributeTypeName = nextElement.getAdaptedJavaTypeName(); 
                }
                Class generatedClass = this.generateWrapperClass(WRAPPER_CLASS + wrapperCounter++, attributeTypeName, nextElement.isList(), next);
                
                this.qNamesToGeneratedClasses.put(next, generatedClass);
                try{
                    Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
                    this.qNamesToDeclaredClasses.put(next, declaredClass);
                }catch(Exception e){
                    
                }
                
                XMLDescriptor desc = new XMLDescriptor();
                desc.setJavaClass(generatedClass);
                            
                
                if(nextElement.isList()){
                    XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
                    mapping.setAttributeName("value");
                    mapping.setXPath("text()");
                    mapping.setUsesSingleNode(true);
                    
                    if(type != null && type.isEnumerationType()){
                        mapping.setValueConverter(buildJAXBEnumTypeConverter(mapping, (EnumTypeInfo)type));
                    }else{
                        try{
                            Class fieldElementClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());                                                                                        
                            mapping.setFieldElementClass(fieldElementClass);                        
                        }catch(ClassNotFoundException e){                       
                        }
                    }
                                        
                    if(nextClassName.equals("[B") || nextClassName.equals("[Ljava.lang.Byte;")) {
                       ((XMLField)mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
                    }
                    else if(nextClassName.equals("javax.xml.namespace.QName")){
                        ((XMLField)mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
                    }
                    desc.addMapping(mapping);
                } else{                 
                    if(nextElement.getJavaTypeName().equals("java.lang.Object")){
                        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
                        mapping.setAttributeName("value");
                        mapping.setSetMethodName("setWrappedValue");
                        mapping.setGetMethodName("getWrappedValue");                                
                        mapping.getNullPolicy().setNullRepresentedByXsiNil(true);                                                       
                        mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                        mapping.setXPath(".");                      
                        desc.addMapping(mapping);
                    }else{                      
                        XMLDirectMapping mapping = new XMLDirectMapping();
                        mapping.setAttributeName("value");
                        mapping.setXPath("text()");
                        mapping.setSetMethodName("setWrappedValue");
                        mapping.setGetMethodName("getWrappedValue");
                                            
                        if(helper.isBuiltInJavaType(nextElement.getJavaType())){                                    
                            Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(attributeTypeName, getClass().getClassLoader());                      
                            mapping.setAttributeClassification(attributeClassification);
                        }               
                        
                        IsSetNullPolicy nullPolicy = new IsSetNullPolicy("isSetValue", false, true, XMLNullRepresentationType.ABSENT_NODE);
                        mapping.setNullPolicy(nullPolicy);
                                            
                        if(type != null && type.isEnumerationType()){
                            mapping.setConverter(buildJAXBEnumTypeConverter(mapping, (EnumTypeInfo)type));
                        }
                        if(nextClassName.equals("[B") || nextClassName.equals("[Ljava.lang.Byte;")) {
                            ((XMLField)mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
                        }
                        else if(nextClassName.equals("javax.xml.namespace.QName")){
                            ((XMLField)mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
                        }
                                            
                        if (nextElement.getJavaTypeAdapterClass() != null) {
                            mapping.setConverter(new XMLJavaTypeConverter(nextElement.getJavaTypeAdapterClass()));
                        }
    
                        desc.addMapping(mapping);
                    }
                }
                NamespaceInfo info = getNamespaceInfoForURI(namespaceUri);
                
    			if(info != null) {
    				NamespaceResolver resolver = info.getNamespaceResolver();
    				String prefix = resolver.resolveNamespaceURI(namespaceUri);
    				desc.setNamespaceResolver(resolver);
                    desc.setDefaultRootElement(prefix + ":" + next.getLocalPart());
                } else {
                    if(namespaceUri.equals("")) {
                        desc.setDefaultRootElement(next.getLocalPart());
                    } else {
                        NamespaceResolver resolver = new NamespaceResolver();
                        String prefix = resolver.generatePrefix();
                        resolver.put(prefix, namespaceUri);
                        desc.setNamespaceResolver(resolver);
                        desc.setDefaultRootElement(prefix + ":" + next.getLocalPart());
    				}
    			}
                project.addDescriptor(desc);
            }else if(type != null && !type.isTransient()){
                if(next.getNamespaceURI() == null || next.getNamespaceURI().equals("")) {
                    type.getDescriptor().addRootElement(next.getLocalPart());
                } else {                    
                    XMLDescriptor descriptor = type.getDescriptor();
                    String uri = next.getNamespaceURI();
                    String prefix = descriptor.getNamespaceResolver().resolveNamespaceURI(uri);
                    if(prefix == null) {
                        prefix = descriptor.getNamespaceResolver().generatePrefix();
                        descriptor.getNamespaceResolver().put(prefix, uri);
                    }
                    descriptor.addRootElement(prefix + ":" + next.getLocalPart());
                }
            }
        }
    }
    
    private NamespaceInfo getNamespaceInfoForURI(String namespaceUri) {
        Iterator<NamespaceInfo> namespaces = this.packageToNamespaceMappings.values().iterator();
        while(namespaces.hasNext()) {
            NamespaceInfo next = namespaces.next();
            if(next.getNamespace().equals(namespaceUri)) {
                return next;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unused")
    private String getPackageNameForURI(String namespaceUri) {
        for(String next:this.packageToNamespaceMappings.keySet()) {
            if(packageToNamespaceMappings.get(next).getNamespace().equals(namespaceUri)) {
                return next;
            }
        }
        return null;
    }
    
    public Class generateWrapperClass(String className, String attributeType, boolean isList, QName theQName) {
        org.eclipse.persistence.internal.libraries.asm.ClassWriter cw = new org.eclipse.persistence.internal.libraries.asm.ClassWriter(false);
        
        CodeVisitor cv;
        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC, className.replace(".", "/"), org.eclipse.persistence.internal.libraries.asm.Type.getType(Object.class).getInternalName(), new String[]{Type.getType(WrappedValue.class).getInternalName()}, null); 
        
        String fieldType = null;
        if(isList){
            fieldType ="Ljava/util/List;";
        }else{
            fieldType = attributeType.replace(".", "/");
            if(!(fieldType.startsWith("["))) {
                fieldType = "L" + fieldType + ";";
            }
        }
                
        cw.visitField(Constants.ACC_PRIVATE, "value", fieldType, null, null);
               
        cw.visitField(Constants.ACC_PRIVATE, "isSetValue", "Z", null, null);
                
        cw.visitField(Constants.ACC_PRIVATE + Constants.ACC_STATIC, "wrappedValueQName", "Ljavax/xml/namespace/QName;", null, null);
              
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", "()V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitMethodInsn(Constants.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(1, 1);
             
        cv = cw.visitMethod(Constants.ACC_STATIC, "<clinit>", "()V", null, null);
        cv.visitTypeInsn(Constants.NEW, "javax/xml/namespace/QName");
        cv.visitInsn(Constants.DUP);
        cv.visitLdcInsn(theQName.getNamespaceURI());
        cv.visitLdcInsn(theQName.getLocalPart());
        cv.visitMethodInsn(Constants.INVOKESPECIAL, "javax/xml/namespace/QName", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
        cv.visitFieldInsn(Constants.PUTSTATIC, className.replace(".", "/"), "wrappedValueQName", "Ljavax/xml/namespace/QName;");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(4, 0);
               
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "getWrappedValue", Type.getMethodDescriptor(Type.getType(Object.class), new Type[]{}), null, null);       
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitFieldInsn(Constants.GETFIELD, className.replace(".", "/"), "value", fieldType);        
        cv.visitInsn(Type.getType(Object.class).getOpcode(Constants.IRETURN));
        cv.visitMaxs(1, 1);
                
        String castType = fieldType;
        if(castType.endsWith(";") && !castType.startsWith("[L")){
            castType = castType.substring(0, castType.length()-1);
        }
    
        if(castType.startsWith("L")){
            castType = castType.substring(1, castType.length());
        }
            
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "setWrappedValue", "(Ljava/lang/Object;)V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitVarInsn(Constants.ALOAD, 1);
        cv.visitTypeInsn(Constants.CHECKCAST, castType);
        cv.visitFieldInsn(Constants.PUTFIELD, className.replace(".", "/"), "value", fieldType);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitInsn(Constants.ICONST_1);   
        cv.visitFieldInsn(Constants.PUTFIELD, className.replace(".", "/"), "isSetValue", "Z");        
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(2, 2);        
        
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "isSetValue", "()Z", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitFieldInsn(Constants.GETFIELD, className.replace(".", "/"), "isSetValue", "Z");
        cv.visitInsn(Constants.IRETURN);
        cv.visitMaxs(1, 1);        
        
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "getQName", "()Ljavax/xml/namespace/QName;", null, null);
        cv.visitFieldInsn(Constants.GETSTATIC, className.replace(".", "/"), "wrappedValueQName", "Ljavax/xml/namespace/QName;");
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);
        
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "getWrappedValueClass", "()Ljava/lang/Class;", null, null);
        cv.visitLdcInsn(Type.getType(fieldType));
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);
      
        cw.visitEnd();
        
        byte[] classBytes = cw.toByteArray();

        JaxbClassLoader loader = (JaxbClassLoader)helper.getClassLoader();
        Class generatedClass = loader.generateClass(className, classBytes);
        return generatedClass;
    }

    public HashMap<QName, Class> getQNamesToGeneratedClasses() {
        return qNamesToGeneratedClasses;
    }       
    public HashMap<QName, Class> getQNamesToDeclaredClasses() {
        return qNamesToDeclaredClasses;
    }
}
