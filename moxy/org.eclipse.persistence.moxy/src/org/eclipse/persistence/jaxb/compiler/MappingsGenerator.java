/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.annotations.*;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.JAXBEnumTypeConverter;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.DomHandlerConverter;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.jaxb.JAXBElementAttributeAccessor;
import org.eclipse.persistence.mappings.AttributeAccessor;


import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.oxm.mappings.converters.XMLRootConverter;
import org.eclipse.persistence.internal.jaxb.XMLJavaTypeConverter;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.internal.libraries.asm.*;
import org.eclipse.persistence.internal.libraries.asm.Type;

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
    private HashMap<Class, QName> generatedClassesToQNames;
    private HashMap<QName, ElementDeclaration> globalElements;
    
    public MappingsGenerator(Helper helper) {
        this.helper = helper;
        jotArrayList = helper.getJavaClass(ArrayList.class);
        jotHashSet = helper.getJavaClass(HashSet.class);
        generatedClassesToQNames = new HashMap<Class, QName>();
    }
    
    public Project generateProject(ArrayList<JavaClass> typeInfoClasses, HashMap<String, TypeInfo> typeInfo, HashMap userDefinedSchemaTypes, HashMap<String, NamespaceInfo> packageToNamespaceMappings, HashMap<QName, ElementDeclaration> globalElements) throws Exception {
        this.typeInfo = typeInfo;
        this.userDefinedSchemaTypes = userDefinedSchemaTypes;
        this.packageToNamespaceMappings = packageToNamespaceMappings;
        this.globalElements = globalElements;
        Project project = new Project();

        for (JavaClass next : typeInfoClasses) {
            if (!next.isEnum()) {
                generateDescriptor(next, project);
            }
        }
        // now create mappings
        generateMappings();
        processGlobalElements(project);
        return project;
    }
    
    public void generateDescriptor(JavaClass javaClass, Project project) {
        String jClassName = javaClass.getQualifiedName();
        TypeInfo info = typeInfo.get(jClassName);
        NamespaceInfo namespaceInfo = this.packageToNamespaceMappings.get(javaClass.getPackage().getQualifiedName());
        String packageNamespace = namespaceInfo.getNamespace();
        String elementName;
        String namespace;
        
        XmlRootElement rootElem = (XmlRootElement) helper.getAnnotation(javaClass, XmlRootElement.class);
        if (rootElem == null) {
            elementName = Introspector.decapitalize(jClassName.substring(jClassName.lastIndexOf(".") + 1));
            namespace = packageNamespace;
        } else {
            elementName = rootElem.name();
            if (elementName.equals("##default")) {
                elementName = Introspector.decapitalize(jClassName.substring(jClassName.lastIndexOf(".") + 1));
            }
            namespace = rootElem.namespace();
        }
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName(jClassName);
        if (namespace.equals("##default")) {
            namespace = namespaceInfo.getNamespace();
        }
        if (namespace.equals("")) {
            descriptor.setDefaultRootElement(elementName);
        } else {
            String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver());
            descriptor.setDefaultRootElement(prefix + ":" + elementName);
        }
        descriptor.setNamespaceResolver(namespaceInfo.getNamespaceResolver());
        
        project.addDescriptor(descriptor);
        info.setDescriptor(descriptor);
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
                    generateCompositeCollectionMapping(property, descriptor, namespaceInfo, valueType).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    generateCompositeObjectMapping(property, descriptor, namespaceInfo, valueType).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
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
        	generateAnyCollectionMapping(property, descriptor, namespaceInfo);
        } else if(property.isReference()) {
        	if(this.isCollectionType(property)) {
        		generateCollectionMappingForReferenceProperty((ReferenceProperty)property, descriptor, namespaceInfo);
        	} else {
        		generateMappingForReferenceProperty((ReferenceProperty)property, descriptor, namespaceInfo);
        	}
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
                        generateCompositeObjectMapping(property, descriptor, namespaceInfo, referenceClass);
                    }
                }
            } else {
                if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                    generateBinaryMapping(property, descriptor, namespaceInfo);
                } else {
                    generateDirectMapping(property, descriptor, namespaceInfo);
                }
            }
        }
    }
    
    public XMLChoiceObjectMapping generateChoiceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        ChoiceProperty prop = (ChoiceProperty)property;
    	XMLChoiceObjectMapping mapping = new XMLChoiceObjectMapping();
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
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
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
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
    	XMLChoiceObjectMapping mapping = new XMLChoiceObjectMapping();
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }

        List<ElementDeclaration> referencedElements = property.getReferencedElements();
        boolean hasJAXBElements = false;
        AttributeAccessor mappingAccessor = mapping.getAttributeAccessor();
    	for(ElementDeclaration element:referencedElements) {
    		QName elementName = element.getElementName();
    		XMLField xmlField = this.getXPathForElement("", elementName, namespaceInfo, !(this.typeInfo.containsKey(element.getJavaTypeName())));
    		mapping.addChoiceElement(xmlField, element.getJavaTypeName());
            if(!element.isXmlRootElement()) {
                XMLRootConverter converter = new XMLRootConverter(xmlField);
                mapping.addConverter(xmlField, converter);
            }
    		hasJAXBElements = hasJAXBElements || !element.isXmlRootElement();
    		if(hasJAXBElements) {
    			mapping.setAttributeAccessor(new JAXBElementAttributeAccessor(mappingAccessor));
    		}
    	}
    	descriptor.addMapping(mapping);
    	return mapping;
    }
    
    public XMLMapping generateCollectionMappingForReferenceProperty(ReferenceProperty property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
    	XMLChoiceCollectionMapping mapping = new XMLChoiceCollectionMapping();
    	
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }

        List<ElementDeclaration> referencedElements = property.getReferencedElements();
        boolean hasJAXBElements = false;
        AttributeAccessor mappingAccessor = mapping.getAttributeAccessor();
    	
        for(ElementDeclaration element:referencedElements) {
    		QName elementName = element.getElementName();
    		XMLField xmlField = this.getXPathForElement("", elementName, namespaceInfo, !(this.typeInfo.containsKey(element.getJavaTypeName())));
    		mapping.addChoiceElement(xmlField, element.getJavaTypeName());
    		if(!element.isXmlRootElement()) {
    		    XMLRootConverter converter = new XMLRootConverter(xmlField);
    		    mapping.addConverter(xmlField, converter);
    		}
    		hasJAXBElements = hasJAXBElements || !element.isXmlRootElement();
    	}
    	if(hasJAXBElements) {
    		mapping.setAttributeAccessor(new JAXBElementAttributeAccessor(mappingAccessor, mapping.getContainerPolicy()));
    	}
    	descriptor.addMapping(mapping);
    	return mapping;
    }
    public XMLAnyCollectionMapping generateAnyCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
    	AnyProperty prop = (AnyProperty)property;
    	XMLAnyCollectionMapping  mapping = new XMLAnyCollectionMapping();
    	mapping.setAttributeName(property.getPropertyName());
    	if(prop.isLax()) {
    		mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
    	} else {
    		mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
    	}
    	if(prop.getDomHandlerClass() != null) {
    		DomHandlerConverter converter = new DomHandlerConverter(prop.getDomHandlerClass());
    		mapping.setConverter(converter);
    	}
    	descriptor.addMapping(mapping);
    	return mapping;
    }
    public XMLCompositeObjectMapping generateCompositeObjectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setReferenceClassName(referenceClass.getQualifiedName());
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
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
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }
        mapping.setField(getXPathForField(property, namespaceInfo, true));
        descriptor.addMapping(mapping);
        return mapping;
    }
    public XMLBinaryDataMapping generateBinaryMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
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
        JAXBEnumTypeConverter converter = new JAXBEnumTypeConverter(mapping, enumInfo.getClassName(), false);
        mapping.setConverter(converter);
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }
        mapping.setField(getXPathForField(property, namespaceInfo, true));
        HashMap<Object, String> enumValuesMap = enumInfo.getObjectValuesToFieldValues();
        for (Object o : enumValuesMap.keySet()) {
            String fieldValue = enumValuesMap.get(o);
            converter.addConversionValue(fieldValue, o);
        }
        descriptor.addMapping(mapping);
    }
    
    public void generateCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        // check to see if this should be a composite or direct mapping
        JavaClass javaClass = null;
        if (property.getGenericType() != null) {
            javaClass = (JavaClass) property.getGenericType();
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
                generateEnumCollectionMapping(property, (EnumTypeInfo) referenceInfo, descriptor, namespaceInfo);
            } else {
                if (helper.isAnnotationPresent(property.getElement(), XmlIDREF.class)) {
                    generateXMLCollectionReferenceMapping(property, descriptor, namespaceInfo, javaClass);
                } else {
                    generateCompositeCollectionMapping(property, descriptor, namespaceInfo, javaClass);
                }
            }
        } else {
            generateDirectCollectionMapping(property, descriptor, namespaceInfo);
        }
    }

    public void generateEnumCollectionMapping(Property property, EnumTypeInfo info, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }

        JAXBEnumTypeConverter converter = new JAXBEnumTypeConverter(mapping, info.getClassName(), false);
        HashMap<Object, String> enumValuesMap = info.getObjectValuesToFieldValues();
        for (Object o : enumValuesMap.keySet()) {
            String fieldValue = enumValuesMap.get(o);
            converter.addConversionValue(fieldValue, o);
        }

        mapping.setValueConverter(converter);
        JavaClass collectionType = property.getType();
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());
        XMLField xmlField = getXPathForField(property, namespaceInfo, true);
        mapping.setField(xmlField);
        if (helper.isAnnotationPresent(property.getElement(), XmlList.class)) {
            mapping.setUsesSingleNode(true);
        }
        descriptor.addMapping(mapping);
    }

    public void generateAnyAttributeMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }
        descriptor.addMapping(mapping);
    }
    
    protected boolean areEquals(JavaClass src, Class tgt) {
        if (src == null || tgt == null) {
            return false;
        }
        return src.getRawName().equals(tgt.getCanonicalName());
    }
    
    public XMLCompositeCollectionMapping generateCompositeCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
        }
        mapping.setReferenceClassName(referenceClass.getQualifiedName());
        
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
        if(property.isMethodProperty()) {
            mapping.setGetMethodName(property.getGetMethodName());
            mapping.setSetMethodName(property.getSetMethodName());
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
            TypeInfo parentInfo = this.typeInfo.get(javaClass.getSuperclass().getQualifiedName());
            if (parentInfo != null) {
                // generate inherited mappings first.
                generateMappings(parentInfo, descriptor, namespaceInfo);
            }
            generateMappings(info, descriptor, namespaceInfo);
        }
    }

    public void generateMappings(TypeInfo info, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        String[] propOrder = info.getPropOrder();
        if (propOrder.length == 0 || propOrder[0].equals("")) {
            ArrayList<String> propertyNames = info.getPropertyNames();
            for (int i = 0; i < propertyNames.size(); i++) {
                String nextPropertyKey = propertyNames.get(i);
                Property next = info.getProperties().get(nextPropertyKey);
                generateMapping(next, descriptor, namespaceInfo);
            }
        } else {
            for (int i = 0; i < propOrder.length; i++) {
                Property next = info.getProperties().get(propOrder[i]);
                if (next != null) {
                    generateMapping(next, descriptor, namespaceInfo);
                }
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
        if (namespaceInfo.isElementFormQualified()) {
            namespace = namespaceInfo.getNamespace();
        }
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
    
    public ArrayList<Property> getPropertiesForClass(JavaClass cls, TypeInfo info) {
        if (info.getAccessType() == XmlAccessType.FIELD) {
            return getFieldPropertiesForClass(cls, info, false);
        } else if (info.getAccessType() == XmlAccessType.PROPERTY) {
            return getPropertyPropertiesForClass(cls, info, false);
        } else if (info.getAccessType() == XmlAccessType.PUBLIC_MEMBER) {
            return getPublicMemberPropertiesForClass(cls, info);
        } else {
            return getNoAccessTypePropertiesForClass(cls, info);
        }
    }
    
    public ArrayList<Property> getFieldPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList properties = new ArrayList();

        for (JavaField nextField : new ArrayList<JavaField>(cls.getDeclaredFields())) {
            if (!helper.isAnnotationPresent(nextField, XmlTransient.class)) {
                if ((Modifier.isPublic(nextField.getModifiers()) && onlyPublic) || !onlyPublic) {
                    Property property = new Property();
                    property.setPropertyName(nextField.getName());
                    property.setElement(nextField);
                    property.setType(helper.getType(nextField));
                    property.setGenericType(helper.getGenericType(nextField));
                    properties.add(property);
                }
            }
        }
        return properties;
    }
    
    public ArrayList<Property> getPropertyPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList properties = new ArrayList();
        // First collect all the getters
        ArrayList<JavaMethod> getMethods = new ArrayList<JavaMethod>();
        for (JavaMethod next : new ArrayList<JavaMethod>(cls.getDeclaredMethods())) {
            if (next.getName().startsWith("get") || ((areEquals((JavaClass) next.getReturnType(), Boolean.class) || areEquals((JavaClass) next.getReturnType(), boolean.class)) && next.getName().startsWith("is"))) {
                if ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic) {
                    getMethods.add(next);
                }
            }
        }
        // Next iterate over the getters and find their setter methods, add
        // whichever one is annotated to the properties list. If neither is, 
        // use the getter
        for (JavaMethod getMethod : new ArrayList<JavaMethod>(cls.getDeclaredMethods())) {
            String propertyName = "";
            if (getMethod.getName().startsWith("get")) {
                propertyName = getMethod.getName().substring(3);
            } else if (getMethod.getName().startsWith("is")) {
                propertyName = getMethod.getName().substring(2);
            }
            // make the first Character lowercase
            propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);

            Property property = new Property(helper);
            property.setPropertyName(propertyName);
            property.setType((JavaClass) getMethod.getReturnType());
            property.setGenericType(helper.getGenericReturnType(getMethod));
            
            String setMethodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            
            JavaClass[] paramTypes = { (JavaClass) getMethod.getReturnType() };
            JavaMethod setMethod = cls.getMethod(setMethodName, paramTypes);
            if (setMethod != null && !setMethod.getAnnotations().isEmpty()) {
                // use the set method if it exists and is annotated
                property.setElement(setMethod);
            } else {
                if (!helper.isAnnotationPresent(getMethod, XmlTransient.class)) {
                    property.setElement(getMethod);
                }
            }
            if (!helper.isAnnotationPresent(property.getElement(), XmlTransient.class)) {
                properties.add(property);
            }
        }
        return properties;
    }
    
    public ArrayList getPublicMemberPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> publicFieldProperties = getFieldPropertiesForClass(cls, info, true);
        ArrayList<Property> publicMethodProperties = getPropertyPropertiesForClass(cls, info, true);
        // Not sure who should win if a property exists for both or the correct
        // order
        if (publicFieldProperties.size() >= 0 && publicMethodProperties.size() == 0) {
            return publicFieldProperties;
        } else if (publicMethodProperties.size() > 0 && publicFieldProperties.size() == 0) {
            return publicMethodProperties;
        } else {
            // add any non-duplicate method properties to the collection.
            // In the case of a collision if one is annotated use it, otherwise
            // use the field.
            HashMap fieldPropertyMap = getPropertyMapFromArrayList(publicFieldProperties);

            for (int i = 0; i < publicMethodProperties.size(); i++) {
                Property next = (Property) publicMethodProperties.get(i);
                if (fieldPropertyMap.get(next.getPropertyName()) == null) {
                    publicFieldProperties.add(next);
                }
            }
            return publicFieldProperties;
        }
    }
    
    public HashMap getPropertyMapFromArrayList(ArrayList<Property> props) {
        HashMap propMap = new HashMap(props.size());
        Iterator propIter = props.iterator();
        while (propIter.hasNext()) {
            Property next = (Property) propIter.next();
            propMap.put(next.getPropertyName(), next);
        }
        return propMap;
    }
    
    public ArrayList getNoAccessTypePropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList list = new ArrayList();
        ArrayList fieldProperties = getFieldPropertiesForClass(cls, info, false);
        ArrayList methodProperties = getPropertyPropertiesForClass(cls, info, false);

        // Iterate over the field and method properties. If ANYTHING contains an
        // annotation and
        // Doesn't appear in the other list, add it to the final list
        for (int i = 0; i < fieldProperties.size(); i++) {
            Property next = (Property) fieldProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (helper.isAnnotationPresent(elem, XmlElement.class) || helper.isAnnotationPresent(elem, XmlAttribute.class) || helper.isAnnotationPresent(elem, XmlAnyAttribute.class) || helper.isAnnotationPresent(elem, XmlAnyElement.class) || helper.isAnnotationPresent(elem, XmlValue.class)) {
                list.add(next);
            }
        }
        for (int i = 0; i < methodProperties.size(); i++) {
            Property next = (Property) methodProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (helper.isAnnotationPresent(elem, XmlElement.class) || helper.isAnnotationPresent(elem, XmlAttribute.class) || helper.isAnnotationPresent(elem, XmlAnyAttribute.class) || helper.isAnnotationPresent(elem, XmlAnyElement.class) || helper.isAnnotationPresent(elem, XmlValue.class)) {
                list.add(next);
            }
        }
        return list;
    }
    
    public void processSchemaType(XmlSchemaType type) {
        String schemaTypeName = type.name();
        Class javaType = type.type();

        if (javaType == null) {
            return;
        }
        QName typeQName = new QName(XMLConstants.SCHEMA_INSTANCE_URL, schemaTypeName);
        this.userDefinedSchemaTypes.put(javaType, typeQName);
    }

    public ArrayList getEnumerationFacetsFor(EnumTypeInfo info) {
        Collection valuesCollection = info.getObjectValuesToFieldValues().values();
        return new ArrayList(valuesCollection);
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
    		if(type != null) {
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
    		} else if(helper.isBuiltInJavaType(nextElement.getJavaType())) {
    			//generate a class/descriptor for this element
    			
    			String namespaceUri = next.getNamespaceURI();
    			if(namespaceUri == null || namespaceUri.equals("##default")) {
    				namespaceUri = "";
    			}
    			Class generatedClass = this.generateWrapperClass(WRAPPER_CLASS + wrapperCounter++, nextClassName);
    			this.generatedClassesToQNames.put(generatedClass, next);
    			
    			XMLDescriptor desc = new XMLDescriptor();
    			desc.setJavaClass(generatedClass);
    			
    			XMLDirectMapping mapping = new XMLDirectMapping();
    			mapping.setAttributeName("value");
    			mapping.setXPath("text()");
    			if(nextClassName.equals("[B") || nextClassName.equals("[Ljava.lang.Byte;")) {
    				((XMLField)mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
    			}
    			desc.addMapping(mapping);

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
    
    public HashMap<Class, QName> getGeneratedClassesToQName() {
    	return this.generatedClassesToQNames;
    }
    
    public Class generateWrapperClass(String className, String attributeType) {
		org.eclipse.persistence.internal.libraries.asm.ClassWriter classWriter = new org.eclipse.persistence.internal.libraries.asm.ClassWriter(false);
		classWriter.visit(Constants.V1_5, Constants.ACC_PUBLIC, className.replace(".", "/"), org.eclipse.persistence.internal.libraries.asm.Type.getType(Object.class).getInternalName(), new String[]{Type.getType(WrappedValue.class).getInternalName()}, null);
		
		CodeVisitor mv = classWriter.visitMethod(Constants.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
		
        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitMethodInsn(Constants.INVOKESPECIAL, Type.getType(Object.class).getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
        mv.visitInsn(Constants.RETURN);
        mv.visitMaxs(1, 1);

        String fieldType = attributeType.replace(".", "/");
        if(!(fieldType.startsWith("["))) {
        	fieldType = "L" + fieldType + ";";
        }
       	classWriter.visitField(Constants.ACC_PUBLIC, "value", fieldType, null, null);
		
		Type objectType = Type.getType(Object.class);
		mv = classWriter.visitMethod(Constants.ACC_PUBLIC, "getWrappedValue", Type.getMethodDescriptor(Type.getType(Object.class), new Type[]{}), null, null);
		mv.visitVarInsn(Constants.ALOAD, 0);
		mv.visitFieldInsn(Constants.GETFIELD, className.replace(".", "/"), "value", fieldType);
		mv.visitInsn(Type.getType(Object.class).getOpcode(Constants.IRETURN));
		mv.visitMaxs(1 + objectType.getSize(), 1);    			
		classWriter.visitEnd();
		
		byte[] classBytes = classWriter.toByteArray();
		
		JaxbClassLoader loader = (JaxbClassLoader)helper.getClassLoader();
		Class generatedClass = loader.generateClass(className, classBytes);
		return generatedClass;
    }    	
}
