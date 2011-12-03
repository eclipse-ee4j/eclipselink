/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.awt.Image;
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
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jaxb.DefaultElementConverter;
import org.eclipse.persistence.internal.jaxb.DomHandlerConverter;
import org.eclipse.persistence.internal.jaxb.JAXBElementConverter;
import org.eclipse.persistence.internal.jaxb.JAXBElementRootConverter;
import org.eclipse.persistence.internal.jaxb.JAXBSetMethodAttributeAccessor;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.MultiArgInstantiationPolicy;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.jaxb.XMLJavaTypeConverter;
import org.eclipse.persistence.internal.jaxb.many.JAXBArrayAttributeAccessor;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.jaxb.many.MapValue;
import org.eclipse.persistence.internal.jaxb.many.MapValueAttributeAccessor;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.JAXBEnumTypeConverter;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAbstractNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper;
import org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.FixedMimeTypePolicy;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLListConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.Project;

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
    private static final String ATT = "@";
    private static final String TXT = "/text()";
    private static String OBJECT_CLASS_NAME = "java.lang.Object";
    public static final QName RESERVED_QNAME = new QName("urn:ECLIPSELINK_RESERVEDURI", "RESERVEDNAME");

    String outputDir = ".";
    private HashMap<String, QName> userDefinedSchemaTypes;
    private Helper helper;
    private JavaClass jotArrayList;
    private JavaClass jotHashSet;
    private JavaClass jotHashMap;
    private HashMap<String, PackageInfo> packageToPackageInfoMappings;
    private HashMap<String, TypeInfo> typeInfo;
    private HashMap<QName, Class> qNamesToGeneratedClasses;
    private HashMap<String, Class> classToGeneratedClasses;
    private HashMap<QName, Class> qNamesToDeclaredClasses;
    private HashMap<QName, ElementDeclaration> globalElements;
    private List<ElementDeclaration> localElements;
    private Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClasses;
    private Map<MapEntryGeneratedKey, Class> generatedMapEntryClasses;
    private Project project;
    private NamespaceResolver globalNamespaceResolver;
    private boolean isDefaultNamespaceAllowed;
    private Map<TypeMappingInfo, Class>typeMappingInfoToAdapterClasses;

    public MappingsGenerator(Helper helper) {
        this.helper = helper;
        jotArrayList = helper.getJavaClass(ArrayList.class);
        jotHashSet = helper.getJavaClass(HashSet.class);
        jotHashMap = helper.getJavaClass(HashMap.class);
        qNamesToGeneratedClasses = new HashMap<QName, Class>();
        qNamesToDeclaredClasses = new HashMap<QName, Class>();
        classToGeneratedClasses = new HashMap<String, Class>();
        globalNamespaceResolver = new NamespaceResolver();
        isDefaultNamespaceAllowed = true;
    }

    public Project generateProject(ArrayList<JavaClass> typeInfoClasses, HashMap<String, TypeInfo> typeInfo, HashMap<String, QName> userDefinedSchemaTypes, HashMap<String, PackageInfo> packageToPackageInfoMappings, HashMap<QName, ElementDeclaration> globalElements, List<ElementDeclaration> localElements, Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClass, Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses,  boolean isDefaultNamespaceAllowed) throws Exception {
        this.typeInfo = typeInfo;
        this.userDefinedSchemaTypes = userDefinedSchemaTypes;
        this.packageToPackageInfoMappings = packageToPackageInfoMappings;
        this.isDefaultNamespaceAllowed = isDefaultNamespaceAllowed;
        this.globalElements = globalElements;
        this.localElements = localElements;
        this.typeMappingInfoToGeneratedClasses = typeMappingInfoToGeneratedClass;
        this.typeMappingInfoToAdapterClasses = typeMappingInfoToAdapterClasses;
        project = new Project();

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
        
        // apply customizers if necessary
        Set<Entry<String, TypeInfo>> entrySet = this.typeInfo.entrySet();
        for (Entry<String, TypeInfo> entry : entrySet) {
            TypeInfo tInfo = entry.getValue();
            if (tInfo.getXmlCustomizer() != null) {
                String customizerClassName = tInfo.getXmlCustomizer();
                try {
                    Class customizerClass = PrivilegedAccessHelper.getClassForName(customizerClassName, true, helper.getClassLoader());
                    DescriptorCustomizer descriptorCustomizer = (DescriptorCustomizer) PrivilegedAccessHelper.newInstanceFromClass(customizerClass);
                    descriptorCustomizer.customize(tInfo.getDescriptor());
                } catch (IllegalAccessException iae) {
                    throw JAXBException.couldNotCreateCustomizerInstance(iae, customizerClassName);
                } catch (InstantiationException ie) {
                    throw JAXBException.couldNotCreateCustomizerInstance(ie, customizerClassName);
                } catch (ClassCastException cce) {
                    throw JAXBException.invalidCustomizerClass(cce, customizerClassName);
                } catch (ClassNotFoundException cnfe) {
                    throw JAXBException.couldNotCreateCustomizerInstance(cnfe, customizerClassName);
                }
            }
        }

        processGlobalElements(project);
        return project;
    }

    public void generateDescriptor(JavaClass javaClass, Project project) {
        String jClassName = javaClass.getQualifiedName();
        TypeInfo info = typeInfo.get(jClassName);
        if (info.isTransient()){
            return;
        }
        NamespaceInfo namespaceInfo = this.packageToPackageInfoMappings.get(javaClass.getPackageName()).getNamespaceInfo();
        String packageNamespace = namespaceInfo.getNamespace();
        String elementName;
        String namespace;

        if (javaClass.getSuperclass() != null && javaClass.getSuperclass().getName().equals("javax.xml.bind.JAXBElement")) {
            generateDescriptorForJAXBElementSubclass(javaClass, project, namespaceInfo.getNamespaceResolverForDescriptor());
            return;
        }

        XMLDescriptor descriptor = new XMLDescriptor();
        org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement rootElem = info.getXmlRootElement();
        if (rootElem == null) {
        	 try{                
        		 elementName = info.getXmlNameTransformer().transformRootElementName(javaClass.getName());
             }catch (Exception ex){
              	throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
             }  
            
            namespace = packageNamespace;
            descriptor.setResultAlwaysXMLRoot(true);
        } else {
            elementName = rootElem.getName();
            if (elementName.equals("##default")) {                                
                try{                
                    elementName = info.getXmlNameTransformer().transformRootElementName(javaClass.getName());
                }catch (Exception ex){
                 	throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
                }  
            }
            namespace = rootElem.getNamespace();
            descriptor.setResultAlwaysXMLRoot(false);
        }

        descriptor.setJavaClassName(jClassName);

        if (info.getFactoryMethodName() != null) {
            descriptor.getInstantiationPolicy().useFactoryInstantiationPolicy(info.getObjectFactoryClassName(), info.getFactoryMethodName());
        }

        if (namespace.equals("##default")) {
            namespace = namespaceInfo.getNamespace();
        }

        JavaClass manyValueJavaClass = helper.getJavaClass(ManyValue.class);
        if (!manyValueJavaClass.isAssignableFrom(javaClass)){
            if(namespace.length() != 0) {
                if(isDefaultNamespaceAllowed && globalNamespaceResolver.getDefaultNamespaceURI() == null && namespace.length() != 0) {
                    globalNamespaceResolver.setDefaultNamespaceURI(namespace);
                    namespaceInfo.getNamespaceResolverForDescriptor().setDefaultNamespaceURI(namespace);
                }
            }
            if (rootElem == null) {
                descriptor.setDefaultRootElement("");
            } else {
                if (namespace.length() == 0) {
                    descriptor.setDefaultRootElement(elementName);
                } else {
                    descriptor.setDefaultRootElement(getQualifiedString(getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor(), null), elementName));
    	        }
            }
        }

        descriptor.setNamespaceResolver(namespaceInfo.getNamespaceResolverForDescriptor());
        
        setSchemaContext(descriptor, info);
        // set the ClassExtractor class name if necessary
        if (info.isSetClassExtractorName()) {
            descriptor.getInheritancePolicy().setClassExtractorName(info.getClassExtractorName());
        }
        // set any user-defined properties
        if (info.getUserProperties() != null) {
            descriptor.setProperties(info.getUserProperties());
        }

        if (info.isLocationAware()) {
            Property locProp = null;
            Iterator<Property> i = info.getPropertyList().iterator();
            while (i.hasNext()) {
                Property p = i.next();
                if (p.getType().getName().equals(XMLConstants.LOCATOR_CLASS_NAME)) {
                    locProp = p;
                }
            }
            if (locProp != null && locProp.isTransient()) {
                // build accessor
                // don't make a mapping
                if (locProp.isMethodProperty()) {
                    MethodAttributeAccessor aa = new MethodAttributeAccessor();
                    aa.setAttributeName(locProp.getPropertyName());
                    aa.setSetMethodName(locProp.getSetMethodName());
                    aa.setGetMethodName(locProp.getGetMethodName());
                    descriptor.setLocationAccessor(aa);
                } else {
                    // instance variable property
                    InstanceVariableAttributeAccessor aa = new InstanceVariableAttributeAccessor();
                    aa.setAttributeName(locProp.getPropertyName());
                    descriptor.setLocationAccessor(aa);
                }
            }
        }

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
        setSchemaContext(xmlDescriptor, info);
        project.addDescriptor(xmlDescriptor);
        info.setDescriptor(xmlDescriptor);
    }
    
    private void setSchemaContext(XMLDescriptor desc, TypeInfo info) {
        XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
        if (info.getClassNamespace() == null || info.getClassNamespace().equals("")) {
            schemaRef.setSchemaContext("/" + info.getSchemaTypeName());
            schemaRef.setSchemaContextAsQName(new QName(info.getSchemaTypeName()));
        } else {
            String prefix = desc.getNonNullNamespaceResolver().resolveNamespaceURI(info.getClassNamespace());
            if (prefix != null && !prefix.equals("")) {
                schemaRef.setSchemaContext("/" + prefix + ":" + info.getSchemaTypeName());
                schemaRef.setSchemaContextAsQName(new QName(info.getClassNamespace(), info.getSchemaTypeName(), prefix));
            } else {
            	String generatedPrefix =getPrefixForNamespace(info.getClassNamespace(), desc.getNonNullNamespaceResolver(), null, false);
            	schemaRef.setSchemaContext("/" + getQualifiedString(generatedPrefix, info.getSchemaTypeName()));            	
            	if(generatedPrefix == null || generatedPrefix.equals(XMLConstants.EMPTY_STRING)){
                    schemaRef.setSchemaContextAsQName(new QName(info.getClassNamespace(), info.getSchemaTypeName()));            		
            	}else{
                    schemaRef.setSchemaContextAsQName(new QName(info.getClassNamespace(), info.getSchemaTypeName(), generatedPrefix));
            	}
            }     
        }
        // the default type is complex; need to check for simple type case
        if (info.isEnumerationType() || (info.getPropertyNames().size() == 1 && helper.isAnnotationPresent(info.getProperties().get(info.getPropertyNames().get(0)).getElement(), XmlValue.class))) {
            schemaRef.setType(XMLSchemaReference.SIMPLE_TYPE);
        }
        desc.setSchemaReference(schemaRef);

    }

    /**
     * Generate a mapping for a given Property.
     * 
     * @param property
     * @param descriptor
     * @param namespaceInfo
     * @return newly created mapping
     */
    public DatabaseMapping generateMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        if (property.isSetXmlJavaTypeAdapter()) {
            // if we are dealing with a reference, generate mapping and return 
            if (property.isReference()) {
                return generateMappingForReferenceProperty(property, descriptor, namespaceInfo);
            }
            
            XmlJavaTypeAdapter xja = property.getXmlJavaTypeAdapter();
            JavaClass adapterClass = helper.getJavaClass(xja.getValue());

            JavaClass valueType = null;
            String sValType = xja.getValueType();
            if (sValType.equals("javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT")) {
                valueType = property.getActualType();
            } else {
                valueType = helper.getJavaClass(xja.getValueType());
            }

            DatabaseMapping mapping;
            boolean isArray = property.getType().isArray() && !property.getType().getRawName().equals("byte[]");

            // if the value type is something we have a descriptor for, create
            // a composite mapping
            if (typeInfo.containsKey(valueType.getQualifiedName())) {
                if (isCollectionType(property)) {
                    mapping = generateCompositeCollectionMapping(property, descriptor, namespaceInfo, valueType.getQualifiedName());
                    ((XMLCompositeCollectionMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    mapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, valueType.getQualifiedName());
                    ((XMLCompositeObjectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                }
            } else {
                // no descriptor for value type
                if (property.isAny()) {
                    if (isCollectionType(property)){
                        mapping = generateAnyCollectionMapping(property, descriptor, namespaceInfo, property.isMixedContent());
                        ((XMLAnyCollectionMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    } else {
                        mapping = generateAnyObjectMapping(property, descriptor, namespaceInfo);
                        ((XMLAnyObjectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    }
                } else if (isCollectionType(property) || isArray) {
                    if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                    	mapping = generateBinaryDataCollectionMapping(property, descriptor, namespaceInfo);
                    	((XMLBinaryDataCollectionMapping) mapping).setValueConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    } else{
                	    mapping = generateDirectCollectionMapping(property, descriptor, namespaceInfo);
                        if(adapterClass.getQualifiedName().equals(CollapsedStringAdapter.class.getName())) {
                            ((XMLCompositeDirectCollectionMapping)mapping).setCollapsingStringValues(true);
                        } else if(adapterClass.getQualifiedName().equals(NormalizedStringAdapter.class.getName())) {
                            ((XMLCompositeDirectCollectionMapping)mapping).setNormalizingStringValues(true);
                        } else {
                            ((XMLCompositeDirectCollectionMapping) mapping).setValueConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                        }
                	}
                } else if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                    mapping = generateBinaryMapping(property, descriptor, namespaceInfo);
                    ((XMLBinaryDataMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    if (!property.isAttribute() && areEquals(valueType, Object.class)){
                        mapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, null);
                        ((XMLCompositeObjectMapping)mapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                        ((XMLCompositeObjectMapping)mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                        return mapping;
                    }
                    mapping = generateDirectMapping(property, descriptor, namespaceInfo);
                    if(adapterClass.getQualifiedName().equals(CollapsedStringAdapter.class.getName())) {
                        ((XMLDirectMapping)mapping).setCollapsingStringValues(true);
                    } else if(adapterClass.getQualifiedName().equals(NormalizedStringAdapter.class.getName())) {
                        ((XMLDirectMapping)mapping).setNormalizingStringValues(true);
                    } else {
                        ((XMLDirectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    }
                }
            }
            return mapping;
        }
        if (property.isSetXmlJoinNodes()) {
            if (isCollectionType(property)) {
                return generateXMLCollectionReferenceMapping(property, descriptor, namespaceInfo, property.getActualType());
            }
            return generateXMLObjectReferenceMapping(property, descriptor, namespaceInfo, property.getType());
        }
        if (property.isXmlTransformation()) {
            return generateTransformationMapping(property, descriptor, namespaceInfo);
        }
        if (property.isChoice()) {
            if (this.isCollectionType(property)) {
                return generateChoiceCollectionMapping(property, descriptor, namespaceInfo);
            } 
            return generateChoiceMapping(property, descriptor, namespaceInfo);
        }
        if (property.isInverseReference()) {
            return generateInverseReferenceMapping(property, descriptor, namespaceInfo);
        } 
        if (property.isAny()) {
            if (isCollectionType(property) || property.getType().isArray()){
                return generateAnyCollectionMapping(property, descriptor, namespaceInfo, property.isMixedContent());
            }
            return generateAnyObjectMapping(property, descriptor, namespaceInfo);
        }
        if (property.isReference()) {
            return generateMappingForReferenceProperty(property, descriptor, namespaceInfo);
        }
        if (property.isMap()){
        	if (property.isAnyAttribute()) {
        		return generateAnyAttributeMapping(property, descriptor, namespaceInfo);
        	}
        	return generateMapMapping(property, descriptor, namespaceInfo);
        }
        if (isCollectionType(property)) {
            return generateCollectionMapping(property, descriptor, namespaceInfo);
        }
        
        JavaClass referenceClass = property.getType();
        String referenceClassName = referenceClass.getRawName();
        if (referenceClass.isArray()  && !referenceClassName.equals("byte[]")){
            JavaClass componentType = referenceClass.getComponentType();
            TypeInfo reference = typeInfo.get(componentType.getName());
                        
            if (reference != null && reference.isEnumerationType()) {
                return generateEnumCollectionMapping(property,  descriptor, namespaceInfo,(EnumTypeInfo) reference);
            }            
            if (areEquals(componentType, Object.class)){
                XMLCompositeCollectionMapping mapping = generateCompositeCollectionMapping(property, descriptor, namespaceInfo, null);
                mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                return mapping;
            }
            
            if (reference != null ||  componentType.isArray()){
                return generateCompositeCollectionMapping(property, descriptor, namespaceInfo, componentType.getQualifiedName());
            }
            return generateDirectCollectionMapping(property, descriptor, namespaceInfo);
        }
        if (property.isXmlIdRef()) {
            return generateXMLObjectReferenceMapping(property, descriptor, namespaceInfo, referenceClass);
        }
        TypeInfo reference = typeInfo.get(referenceClass.getQualifiedName());
        if (reference != null) {
            if (reference.isEnumerationType()) {
                return generateDirectEnumerationMapping(property, descriptor, namespaceInfo, (EnumTypeInfo) reference);
            }
            if (property.isXmlLocation()) {
                XMLCompositeObjectMapping locationMapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, referenceClass.getQualifiedName());
                reference.getDescriptor().setInstantiationPolicy(new NullInstantiationPolicy());
                descriptor.setLocationAccessor(locationMapping.getAttributeAccessor());
                return locationMapping;
            } else {
                return generateCompositeObjectMapping(property, descriptor, namespaceInfo, referenceClass.getQualifiedName());
            }
        }
        if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
            return generateBinaryMapping(property, descriptor, namespaceInfo);
        }
        if (referenceClass.getQualifiedName().equals(OBJECT_CLASS_NAME) && !property.isAttribute() ) {
            XMLCompositeObjectMapping coMapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, null);
            coMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
            return coMapping;
        }
        if (property.isXmlLocation()) {
            return null;
        }
        return generateDirectMapping(property, descriptor, namespaceInfo);
    }

    private XMLInverseReferenceMapping generateInverseReferenceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        XMLInverseReferenceMapping invMapping = new XMLInverseReferenceMapping();

        if (isCollectionType(property.getType())) {
            invMapping.setReferenceClassName(property.getGenericType().getQualifiedName());
        } else {
            invMapping.setReferenceClassName(property.getType().getQualifiedName());
        }

        invMapping.setAttributeName(property.getPropertyName());

        String setMethodName = property.getInverseReferencePropertySetMethodName();
        String getMethodName = property.getInverseReferencePropertyGetMethodName();

        if (setMethodName != null && !setMethodName.equals(XMLConstants.EMPTY_STRING)) {
            invMapping.setSetMethodName(setMethodName);
        }
        if (getMethodName != null && !getMethodName.equals(XMLConstants.EMPTY_STRING)) {
            invMapping.setGetMethodName(getMethodName);
        }
        invMapping.setMappedBy(property.getInverseReferencePropertyName());

        if (isCollectionType(property.getType())) {
            JavaClass collectionType = property.getType();
            if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
                collectionType = jotArrayList;
            } else if (areEquals(collectionType, Set.class)) {
                collectionType = jotHashSet;
            }
            invMapping.useCollectionClass(helper.getClassForJavaClass(collectionType));
        }
        return invMapping;
    }

    /**
     * Generate an XMLTransformationMapping based on a given Property.  
     * 
     * @param property
     * @param descriptor
     * @param namespace
     * @return
     */
    public XMLTransformationMapping generateTransformationMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        XMLTransformationMapping mapping = new XMLTransformationMapping();
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        // handle transformation
        if (property.isSetXmlTransformation()) {
            XmlTransformation xmlTransformation = property.getXmlTransformation();
            mapping.setIsOptional(xmlTransformation.isOptional());
            // handle transformer(s)
            if (xmlTransformation.isSetXmlReadTransformer()) {
                // handle read transformer
                mapping.setAttributeName(property.getPropertyName());
                XmlReadTransformer readTransformer = xmlTransformation.getXmlReadTransformer();
                if (readTransformer.isSetTransformerClass()) {
                    mapping.setAttributeTransformerClassName(xmlTransformation.getXmlReadTransformer().getTransformerClass());
                } else {
                    mapping.setAttributeTransformation(xmlTransformation.getXmlReadTransformer().getMethod());
                }
            }
            if (xmlTransformation.isSetXmlWriteTransformers()) {
                // handle write transformer(s)
                for (XmlWriteTransformer writeTransformer : xmlTransformation.getXmlWriteTransformer()) {
                    if (writeTransformer.isSetTransformerClass()) {
                        mapping.addFieldTransformerClassName(writeTransformer.getXmlPath(), writeTransformer.getTransformerClass());
                    } else {
                        mapping.addFieldTransformation(writeTransformer.getXmlPath(), writeTransformer.getMethod());
                    }
                }
            }
        }
        return mapping;
    }
    
    public XMLChoiceObjectMapping generateChoiceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        XMLChoiceObjectMapping mapping = new XMLChoiceObjectMapping();
        mapping.setAttributeName(property.getPropertyName());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        boolean isIdRef = property.isXmlIdRef();
        Iterator<Property> choiceProperties = property.getChoiceProperties().iterator();
        while (choiceProperties.hasNext()) {
            Property next = choiceProperties.next();
            JavaClass type = next.getType();
            JavaClass originalType = next.getType();
            Converter converter = null;
            TypeInfo info = typeInfo.get(type.getName());
            if(info != null){
                XmlJavaTypeAdapter adapter = info.getXmlJavaTypeAdapter();
                if(adapter != null){
                    String adapterValue = adapter.getValue();
                    JavaClass adapterClass = helper.getJavaClass(adapterValue);
                    JavaClass theClass = CompilerHelper.getTypeFromAdapterClass(adapterClass, helper);
                    type = theClass;
                    converter = new XMLJavaTypeConverter(adapterClass.getQualifiedName());
                }
            }            
            if (next.getXmlJoinNodes() != null) {
                // handle XmlJoinNodes
                List<XMLField> srcFlds = new ArrayList<XMLField>();
                List<XMLField> tgtFlds = new ArrayList<XMLField>();
                for (XmlJoinNode xmlJoinNode: next.getXmlJoinNodes().getXmlJoinNode()) {
                    srcFlds.add(new XMLField(xmlJoinNode.getXmlPath()));
                    tgtFlds.add(new XMLField(xmlJoinNode.getReferencedXmlPath()));
                }
                mapping.addChoiceElement(srcFlds, type.getQualifiedName(), tgtFlds);
            } else if (isIdRef) {
                // handle IDREF
                String tgtXPath = null;
                TypeInfo referenceType = typeInfo.get(type.getQualifiedName());
                if (null != referenceType && referenceType.isIDSet()) {
                    Property prop = referenceType.getIDProperty();
                    tgtXPath = getXPathForField(prop, namespace, !prop.isAttribute()).getXPath();
                }
                // if the XPath is set (via xml-path) use it, otherwise figure it out
                XMLField srcXPath;
                if (next.getXmlPath() != null) {
                    srcXPath = new XMLField(next.getXmlPath());
                } else {
                    srcXPath = getXPathForField(next, namespace, true);
                }
                mapping.addChoiceElement(srcXPath.getXPath(), type.getQualifiedName(), tgtXPath);
            } else {
                XMLField xpath;
                if (next.getXmlPath() != null) {
                    xpath = new XMLField(next.getXmlPath());
                } else {
                    xpath = getXPathForField(next, namespace, (!(this.typeInfo.containsKey(type.getQualifiedName()))) || next.isMtomAttachment());
                }
                mapping.addChoiceElement(xpath, type.getQualifiedName());
                if(!originalType.getQualifiedName().equals(type.getQualifiedName())) {
                    if(mapping.getClassNameToFieldMappings().get(originalType.getQualifiedName()) == null) {
                        mapping.getClassNameToFieldMappings().put(originalType.getQualifiedName(), xpath);
                    }
                    mapping.addConverter(xpath, converter);
                }
            }
        }
        return mapping;
    }

    public XMLChoiceCollectionMapping generateChoiceCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespace) {
        XMLChoiceCollectionMapping mapping = new XMLChoiceCollectionMapping();
        mapping.setReuseContainer(true);
        mapping.setAttributeName(property.getPropertyName());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        if (collectionType.isArray() || areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());

        boolean isIdRef = property.isXmlIdRef();
        Iterator<Property> choiceProperties = property.getChoiceProperties().iterator();
        while (choiceProperties.hasNext()) {
            Property next = choiceProperties.next();
            JavaClass type = next.getType();
            JavaClass originalType = next.getType();
            Converter converter = null;
            TypeInfo info = typeInfo.get(type.getName());
            if(info != null){
                XmlJavaTypeAdapter adapter = info.getXmlJavaTypeAdapter();
                if(adapter != null){
                    String adapterValue = adapter.getValue();
                    JavaClass adapterClass = helper.getJavaClass(adapterValue);
                    JavaClass theClass = CompilerHelper.getTypeFromAdapterClass(adapterClass, helper);
                    type = theClass;
                    converter = new XMLJavaTypeConverter(adapterClass.getQualifiedName());
                }
            }            
            
            if (next.getXmlJoinNodes() != null) {
                // handle XmlJoinNodes
                List<XMLField> srcFlds = new ArrayList<XMLField>();
                List<XMLField> tgtFlds = new ArrayList<XMLField>();
                for (XmlJoinNode xmlJoinNode: next.getXmlJoinNodes().getXmlJoinNode()) {
                    srcFlds.add(new XMLField(xmlJoinNode.getXmlPath()));
                    tgtFlds.add(new XMLField(xmlJoinNode.getReferencedXmlPath()));
                }
                mapping.addChoiceElement(srcFlds, type.getQualifiedName(), tgtFlds);
            } else if (isIdRef) {
                // handle IDREF
                String tgtXPath = null;
                TypeInfo referenceType = typeInfo.get(type.getQualifiedName());
                if (null != referenceType && referenceType.isIDSet()) {
                    Property prop = referenceType.getIDProperty();
                    tgtXPath = getXPathForField(prop, namespace, !prop.isAttribute()).getXPath();
                }
                // if the XPath is set (via xml-path) use it, otherwise figure it out
                XMLField srcXPath;
                if (next.getXmlPath() != null) {
                    srcXPath = new XMLField(next.getXmlPath());
                } else {
                    srcXPath = getXPathForField(next, namespace, true);
                }
                mapping.addChoiceElement(srcXPath.getXPath(), type.getQualifiedName(), tgtXPath);
            } else {
                XMLField xpath;
                if (next.getXmlPath() != null) {
                    xpath = new XMLField(next.getXmlPath());
                } else {
                    xpath = getXPathForField(next, namespace, !(this.typeInfo.containsKey(type.getQualifiedName())));
                }
                mapping.addChoiceElement(xpath.getName(), type.getQualifiedName());
                if(!originalType.getQualifiedName().equals(type.getQualifiedName())) {
                    if(mapping.getClassNameToFieldMappings().get(originalType.getQualifiedName()) == null) {
                        mapping.getClassNameToFieldMappings().put(originalType.getQualifiedName(), xpath);
                    }
                    mapping.addConverter(xpath, converter);
                }
                
            }
        }
        return mapping;
    }

    public DatabaseMapping generateMappingForReferenceProperty(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo)  {
        boolean isCollection = isCollectionType(property) || property.getType().isArray();
        if (property.isAny()) {
            return generateAnyCollectionMapping(property, descriptor, namespaceInfo, true);
        }
        DatabaseMapping mapping;
        if (isCollection) {
            mapping = new XMLChoiceCollectionMapping();
            ((XMLChoiceCollectionMapping) mapping).setReuseContainer(true);
            JavaClass collectionType = property.getType();
            if (collectionType.isArray() || areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
                collectionType = jotArrayList;
            } else if (areEquals(collectionType, Set.class)) {
                collectionType = jotHashSet;
            }
            ((XMLChoiceCollectionMapping) mapping).useCollectionClassName(collectionType.getRawName());
            JAXBElementRootConverter jaxbERConverter = new JAXBElementRootConverter(Object.class);
            if (property.isSetXmlJavaTypeAdapter()) {
                JavaClass adapterClass = helper.getJavaClass(property.getXmlJavaTypeAdapter().getValue());
                jaxbERConverter.setNestedConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
            }
            ((XMLChoiceCollectionMapping) mapping).setConverter(jaxbERConverter);
            if (property.isSetWriteOnly()) {
                ((XMLChoiceCollectionMapping) mapping).setIsWriteOnly(property.isWriteOnly());
            }
        } else {
            mapping = new XMLChoiceObjectMapping();
            JAXBElementRootConverter jaxbERConverter = new JAXBElementRootConverter(Object.class);
            if (property.isSetXmlJavaTypeAdapter()) {
                JavaClass adapterClass = helper.getJavaClass(property.getXmlJavaTypeAdapter().getValue());
                jaxbERConverter.setNestedConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
            }
            ((XMLChoiceObjectMapping) mapping).setConverter(jaxbERConverter);
            if (property.isSetWriteOnly()) {
                ((XMLChoiceObjectMapping) mapping).setIsWriteOnly(property.isWriteOnly());
            }
        }
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
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
        JavaClass propertyType = property.getType();
        if (propertyType.isArray()) {
            JAXBArrayAttributeAccessor accessor = new JAXBArrayAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy(), helper.getClassLoader());
            accessor.setComponentClassName(property.getType().getComponentType().getQualifiedName());
            JavaClass componentType = propertyType.getComponentType();
            if(componentType.isArray()) {
                Class adaptedClass = classToGeneratedClasses.get(componentType.getQualifiedName());
                accessor.setAdaptedClassName(adaptedClass.getName());
            }
            mapping.setAttributeAccessor(accessor);
        }
        String wrapperXPath = "";
        // handle XmlElementWrapper
        if (property.isSetXmlElementWrapper()) {
            XmlElementWrapper wrapper = property.getXmlElementWrapper();
            String namespace = wrapper.getNamespace();
            if (namespace.equals("##default")) {
                if (namespaceInfo.isElementFormQualified()) {
                    namespace = namespaceInfo.getNamespace();
                } else {
                    namespace = "";
                }
            }
            if (namespace.equals("")) {
                wrapperXPath += (wrapper.getName() + "/");
            } else {
                String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver(), null);
                wrapperXPath += getQualifiedString(prefix, wrapper.getName() + "/");
            }
        }        
        if(property.isMixedContent() && isCollection) {
            if(wrapperXPath.length() == 0) {
                ((XMLChoiceCollectionMapping)mapping).setMixedContent(true);
            } else {
                ((XMLChoiceCollectionMapping)mapping).setMixedContent(wrapperXPath.substring(0, wrapperXPath.length() - 1));
            }
        }  
        for (ElementDeclaration element:referencedElements) {
            QName elementName = element.getElementName();
            JavaClass pType = element.getJavaType();
            boolean isBinaryType = (areEquals(pType, AnnotationsProcessor.JAVAX_ACTIVATION_DATAHANDLER) || areEquals(pType, byte[].class) || areEquals(pType, Image.class) || areEquals(pType, Source.class) || areEquals(pType, AnnotationsProcessor.JAVAX_MAIL_INTERNET_MIMEMULTIPART));        
            boolean isText = !isBinaryType && !(this.typeInfo.containsKey(element.getJavaTypeName())) && !(element.getJavaTypeName().equals(OBJECT_CLASS_NAME));
            String xPath = wrapperXPath;

            XMLField xmlField = this.getXPathForElement(xPath, elementName, namespaceInfo, isText);
            //ensure byte[] goes to base64 instead of the default hex.
            if(helper.getXMLToJavaTypeMap().get(element.getJavaType().getRawName()) == XMLConstants.BASE_64_BINARY_QNAME) {
                xmlField.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
            }
            if(areEquals(pType, Object.class)) {
                xmlField.setIsTypedTextField(true);
                xmlField.setSchemaType(XMLConstants.ANY_TYPE_QNAME);
            }
            DatabaseMapping nestedMapping;
            if(isCollection){
                XMLChoiceCollectionMapping xmlChoiceCollectionMapping = (XMLChoiceCollectionMapping) mapping;
                xmlChoiceCollectionMapping.addChoiceElement(xmlField, element.getJavaTypeName());
                nestedMapping = (DatabaseMapping) xmlChoiceCollectionMapping.getChoiceElementMappings().get(xmlField);
                if(nestedMapping.isAbstractCompositeCollectionMapping()){
                    ((XMLCompositeCollectionMapping)nestedMapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                }

                if (nestedMapping.isAbstractCompositeDirectCollectionMapping()) {
                    ((XMLCompositeDirectCollectionMapping) nestedMapping).getNullPolicy().setNullRepresentedByEmptyNode(false);
                }

                if (element.isList() && nestedMapping.isAbstractCompositeDirectCollectionMapping()) {
                    XMLListConverter listConverter = new XMLListConverter();
                    listConverter.setObjectClassName(element.getJavaType().getQualifiedName());
                    ((XMLCompositeDirectCollectionMapping)nestedMapping).setValueConverter(listConverter);
                }
            } else {
                XMLChoiceObjectMapping xmlChoiceObjectMapping = (XMLChoiceObjectMapping) mapping;
                xmlChoiceObjectMapping.addChoiceElement(xmlField, element.getJavaTypeName());
                nestedMapping = (DatabaseMapping) xmlChoiceObjectMapping.getChoiceElementMappings().get(xmlField);
                if(nestedMapping.isAbstractCompositeObjectMapping()){
                    ((XMLCompositeObjectMapping)nestedMapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                }
            }

            if (!element.isXmlRootElement()) {
                Class scopeClass = element.getScopeClass();
                if (scopeClass == javax.xml.bind.annotation.XmlElementDecl.GLOBAL.class){
                    scopeClass = JAXBElement.GlobalScope.class;
                }
                Class declaredType = helper.getClassForJavaClass(element.getJavaType());
                JAXBElementConverter converter = new JAXBElementConverter(xmlField, declaredType, scopeClass);
                if (isCollection){
                    XMLChoiceCollectionMapping xmlChoiceCollectionMapping = (XMLChoiceCollectionMapping) mapping;
                    Converter originalConverter = xmlChoiceCollectionMapping.getConverter(xmlField);
                    converter.setNestedConverter(originalConverter);
                    xmlChoiceCollectionMapping.addConverter(xmlField, converter);
                } else {
                    XMLChoiceObjectMapping xmlChoiceObjectMapping = (XMLChoiceObjectMapping) mapping;
                    Converter originalConverter = xmlChoiceObjectMapping.getConverter(xmlField);
                    converter.setNestedConverter(originalConverter);
                    xmlChoiceObjectMapping.addConverter(xmlField, converter);
                }
            }
        }
        return mapping;
    }

    public XMLAnyCollectionMapping generateAnyCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, boolean isMixed) {
        XMLAnyCollectionMapping  mapping = new XMLAnyCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setReuseContainer(true);
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        // if the XPath is set (via xml-path) use it
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        }

        Class declaredType = helper.getClassForJavaClass(property.getActualType());
        JAXBElementRootConverter jaxbElementRootConverter = new JAXBElementRootConverter(declaredType);
        mapping.setConverter(jaxbElementRootConverter);
        if (property.getDomHandlerClassName() != null) {
            jaxbElementRootConverter.setNestedConverter(new DomHandlerConverter(property.getDomHandlerClassName()));
        }

        if (property.isLax() || property.isReference()) {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        } else {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
        }

        mapping.setMixedContent(isMixed);
        if (isMixed) {
            mapping.setPreserveWhitespaceForMixedContent(true);
        }
        mapping.setUseXMLRoot(true);

        JavaClass collectionType = property.getType();
        if (collectionType.isArray()){
            JAXBArrayAttributeAccessor accessor = new JAXBArrayAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy(), helper.getClassLoader());
            JavaClass componentType = collectionType.getComponentType();
            if(componentType.isArray()) {
                JavaClass baseComponentType = getBaseComponentType(componentType);
                if (baseComponentType.isPrimitive()){
                    Class primitiveClass = XMLConversionManager.getDefaultManager().convertClassNameToClass(baseComponentType.getRawName());
                    accessor.setComponentClass(primitiveClass);
                } else {
                    accessor.setComponentClassName(baseComponentType.getQualifiedName());
                }
            } else {
                accessor.setComponentClassName(componentType.getQualifiedName());
            }
            mapping.setAttributeAccessor(accessor);
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClass(helper.getClassForJavaClass(collectionType));
        
        return mapping;
    }

    public XMLCompositeObjectMapping generateCompositeObjectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, String referenceClassName) {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();

        mapping.setAttributeName(property.getPropertyName());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
        if (property.isMethodProperty()) {
            if (property.getGetMethodName() == null) {
                // handle case of set with no get method
                String paramTypeAsString = property.getType().getName();
                mapping.setAttributeAccessor(new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader()));
                mapping.setIsReadOnly(true);
                mapping.setSetMethodName(property.getSetMethodName());
            } else if (property.getSetMethodName() == null) {
                mapping.setGetMethodName(property.getGetMethodName());
                if (!property.isXmlLocation()) {
                    mapping.setIsWriteOnly(true);
                }
            } else {
                mapping.setSetMethodName(property.getSetMethodName());
                mapping.setGetMethodName(property.getGetMethodName());
            }
        }
        // if the XPath is set (via xml-path) use it; otherwise figure it out
        mapping.setXPath(getXPathForField(property, namespaceInfo, false).getXPath());
        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else if (property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
            mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        }

        if (referenceClassName == null){
        	((XMLField)mapping.getField()).setIsTypedTextField(true);
        	((XMLField)mapping.getField()).setSchemaType(XMLConstants.ANY_TYPE_QNAME);
        	String defaultValue = property.getDefaultValue();
        	if (null != defaultValue) {
        	    mapping.setConverter(new DefaultElementConverter(defaultValue));
        	}
        } else {
        	mapping.setReferenceClassName(referenceClassName);
        }

        if (property.getInverseReferencePropertyName() != null) {
            mapping.setContainerAttributeName(property.getInverseReferencePropertyName());
            JavaClass backPointerPropertyType = null;
            JavaClass referenceClass = property.getActualType();
            if (property.getInverseReferencePropertyGetMethodName() != null && property.getInverseReferencePropertySetMethodName() != null && !property.getInverseReferencePropertyGetMethodName().equals("") && !property.getInverseReferencePropertySetMethodName().equals("")) {
                mapping.setContainerGetMethodName(property.getInverseReferencePropertySetMethodName());
                mapping.setContainerSetMethodName(property.getInverseReferencePropertySetMethodName());
                JavaMethod getMethod = referenceClass.getDeclaredMethod(mapping.getContainerGetMethodName(), new JavaClass[]{});
                if (getMethod != null) {
                    backPointerPropertyType = getMethod.getReturnType();
                }
            } else {
                JavaField backpointerField = referenceClass.getDeclaredField(property.getInverseReferencePropertyName());
                if(backpointerField != null) {
                    backPointerPropertyType = backpointerField.getResolvedType();
                }
            }
            if (isCollectionType(backPointerPropertyType)) {
                mapping.getInverseReferenceMapping().setContainerPolicy(ContainerPolicy.buildDefaultPolicy());
            }
        }

        if (property.isRequired()) {
            ((XMLField) mapping.getField()).setRequired(true);
        }
        return mapping;

    }

    public XMLDirectMapping generateDirectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName(property.getPropertyName());
        String fixedValue = property.getFixedValue();
        if (fixedValue != null) {
            mapping.setIsWriteOnly(true);
        }
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        // if the XPath is set (via xml-path) use it; otherwise figure it out
        XMLField xmlField = getXPathForField(property, namespaceInfo, true);
        mapping.setField(xmlField);

        if (property.getDefaultValue() != null) {
            mapping.setNullValue(property.getDefaultValue());
        }

        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else {
            if (property.isNillable()){
                mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
                mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
            } 
            mapping.getNullPolicy().setNullRepresentedByEmptyNode(false);

            if (!mapping.getXPath().equals("text()")) {
                ((NullPolicy) mapping.getNullPolicy()).setSetPerformedForAbsentNode(false);
            }
        }

        if (property.isRequired()) {
            ((XMLField) mapping.getField()).setRequired(true);
        }

        if (property.getType() != null) {
            Class theClass = null;
            if(property.isSetXmlJavaTypeAdapter()) {
                theClass = helper.getClassForJavaClass(property.getOriginalType());
            } else {
                theClass = helper.getClassForJavaClass(property.getType());
                
            }
            mapping.setAttributeClassification(theClass);
        }

        if (XMLConstants.QNAME_QNAME.equals(property.getSchemaType())){
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
        }
        // handle cdata set via metadata
        if (property.isSetCdata()) {
            mapping.setIsCDATA(property.isCdata());
        }
        return mapping;
    }

    public XMLBinaryDataMapping generateBinaryMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
        mapping.setAttributeName(property.getPropertyName());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        // if the XPath is set (via xml-path) use it
        mapping.setField(getXPathForField(property, namespaceInfo, false));
        if (property.isSwaAttachmentRef()) {
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.SWA_REF_QNAME);
            mapping.setSwaRef(true);
        } else if (property.isMtomAttachment()) {
            XMLField f = ((XMLField) mapping.getField());
            if (!f.getSchemaType().equals(XMLConstants.HEX_BINARY_QNAME)) {
                f.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
            }
        }
        if (property.isInlineBinaryData()) {
            mapping.setShouldInlineBinaryData(true);
        }
        // use a non-dynamic implementation of MimeTypePolicy to wrap the MIME string
        if (property.getMimeType() != null) {
            mapping.setMimeTypePolicy(new FixedMimeTypePolicy(property.getMimeType(), mapping));
        } else {
            if(areEquals(property.getType(), javax.xml.transform.Source.class)) {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/xml", mapping));
            } else if(areEquals(property.getType(), java.awt.Image.class)) {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("image/png", mapping));
            } else {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/octet-stream", mapping));
            }
        }
        return mapping;
    }

    public XMLBinaryDataCollectionMapping generateBinaryDataCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLBinaryDataCollectionMapping mapping = new XMLBinaryDataCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        // if the XPath is set (via xml-path) use it
        mapping.setField(getXPathForField(property, namespaceInfo, false));
        if (property.isSwaAttachmentRef()) {
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.SWA_REF_QNAME);
            mapping.setSwaRef(true);
        } else if (property.isMtomAttachment()) {
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        }
        if (property.isInlineBinaryData()) {
            mapping.setShouldInlineBinaryData(true);
        }
        // use a non-dynamic implementation of MimeTypePolicy to wrap the MIME string
        if (property.getMimeType() != null) {
            mapping.setMimeTypePolicy(new FixedMimeTypePolicy(property.getMimeType()));
        } else {
        	if(areEquals(property.getType(), javax.xml.transform.Source.class)) {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/xml"));
        	} else {
        		mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/octet-stream"));
        	}
        }

        JavaClass collectionType = property.getType();
        if(collectionType != null && isCollectionType(collectionType)){
        	if(collectionType.hasActualTypeArguments()){
        		JavaClass itemType = (JavaClass)collectionType.getActualTypeArguments().toArray()[0];
        		try{
        			Class declaredClass = PrivilegedAccessHelper.getClassForName(itemType.getQualifiedName(), false, helper.getClassLoader());
        			mapping.setAttributeElementClass(declaredClass);
        		}catch (Exception e) {

        		}
        	}
        }else{
        	mapping.setAttributeElementClass(Byte[].class);
        }
        if (collectionType.isArray() || areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());
        return mapping;
    }
    
    public XMLDirectMapping generateDirectEnumerationMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, EnumTypeInfo enumInfo) {
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
        return mapping;
    }

    private JAXBEnumTypeConverter buildJAXBEnumTypeConverter(DatabaseMapping mapping, EnumTypeInfo enumInfo){
        JAXBEnumTypeConverter converter = new JAXBEnumTypeConverter(mapping, enumInfo.getClassName(), false);
        List<String> fieldNames = enumInfo.getFieldNames();
        List<String> xmlEnumValues = enumInfo.getXmlEnumValues();
        for (int i=0; i< fieldNames.size(); i++) {
            converter.addConversionValue(xmlEnumValues.get(i), fieldNames.get(i));
        }
        return converter;
    }

    public DatabaseMapping generateCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        // check to see if this should be a composite or direct mapping
        JavaClass javaClass = property.getActualType();

        if (property.isMixedContent()) {
            return generateAnyCollectionMapping(property, descriptor, namespaceInfo, true);
        }
        if (property.isXmlIdRef() || property.isSetXmlJoinNodes()) {
            return generateXMLCollectionReferenceMapping(property, descriptor, namespaceInfo, javaClass);
        }
        
        if (javaClass != null && typeInfo.get(javaClass.getQualifiedName()) != null) {
            TypeInfo referenceInfo = typeInfo.get(javaClass.getQualifiedName());
            if (referenceInfo.isEnumerationType()) {
                return generateEnumCollectionMapping(property,  descriptor, namespaceInfo,(EnumTypeInfo) referenceInfo);
            }
            return generateCompositeCollectionMapping(property, descriptor, namespaceInfo, javaClass.getQualifiedName());
        }
        if (!property.isAttribute() && javaClass != null && javaClass.getQualifiedName().equals(OBJECT_CLASS_NAME)){
            XMLCompositeCollectionMapping ccMapping = generateCompositeCollectionMapping(property, descriptor, namespaceInfo, null);
            ccMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
            return ccMapping;
        }
        if(isBinaryData(javaClass)){
        	return generateBinaryDataCollectionMapping(property, descriptor, namespaceInfo);
        }
        return generateDirectCollectionMapping(property, descriptor, namespaceInfo);
    }

    public XMLCompositeDirectCollectionMapping generateEnumCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, EnumTypeInfo info) {
    	
    	XMLCompositeDirectCollectionMapping mapping = generateDirectCollectionMapping(property, descriptor, namespaceInfo);
    	mapping.setValueConverter(buildJAXBEnumTypeConverter(mapping, info));
    	return mapping;
    }
    public XMLAnyAttributeMapping generateAnyAttributeMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setReuseContainer(true);
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        // if the XPath is set (via xml-path) use it
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        }
        mapping.setSchemaInstanceIncluded(false);
        mapping.setNamespaceDeclarationIncluded(false);

        JavaClass mapType = property.getType();
        if (areEquals(mapType, Map.class)) {
            mapType = jotHashMap;
        }
        mapping.useMapClassName(mapType.getRawName());
        
        return mapping;
    }

    public XMLAnyObjectMapping generateAnyObjectMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo)  {
        XMLAnyObjectMapping mapping = new XMLAnyObjectMapping();
        mapping.setAttributeName(property.getPropertyName());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        // if the XPath is set (via xml-path) use it
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        }

        Class declaredType = helper.getClassForJavaClass(property.getActualType());
        JAXBElementRootConverter jaxbElementRootConverter = new JAXBElementRootConverter(declaredType);
        mapping.setConverter(jaxbElementRootConverter);
        if (property.getDomHandlerClassName() != null) {
            jaxbElementRootConverter.setNestedConverter(new DomHandlerConverter(property.getDomHandlerClassName()));
        }

        if (property.isLax()) {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        } else {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
        }

        if (property.isMixedContent()) {
            mapping.setMixedContent(true);
        } else {
            mapping.setUseXMLRoot(true);
        }
        return mapping;
    }

    protected boolean areEquals(JavaClass src, Class tgt) {
        if (src == null || tgt == null) {
            return false;
        }
        return src.getRawName().equals(tgt.getCanonicalName());
    }

    /**
     * Compares a JavaModel JavaClass to a Class.  Equality is based on
     * the raw name of the JavaClass compared to the canonical
     * name of the Class.
     *
     * @param src
     * @param tgt
     * @return
     */
    protected boolean areEquals(JavaClass src, String tgtCanonicalName) {
        if (src == null || tgtCanonicalName == null) {
            return false;
        }
        return src.getRawName().equals(tgtCanonicalName);
    }

    public XMLCompositeCollectionMapping generateMapMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
    	XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setReuseContainer(true);
        XMLField field = getXPathForField(property, namespaceInfo, false);
        JavaClass descriptorClass = helper.getJavaClass(descriptor.getJavaClassName());
        JavaClass mapValueClass = helper.getJavaClass(MapValue.class);
        if(mapValueClass.isAssignableFrom(descriptorClass)){
        	mapping.setXPath("entry");
        }else{
        	mapping.setXPath(field.getXPath() + "/entry");
        }

        Class generatedClass = generateMapEntryClassAndDescriptor(property, descriptor.getNonNullNamespaceResolver());
        mapping.setReferenceClass(generatedClass);
        String mapClassName = property.getType().getRawName();
        mapping.useCollectionClass(ArrayList.class);

        mapping.setAttributeAccessor(new MapValueAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy(), generatedClass, mapClassName));
        return mapping;
    }

    private Class generateMapEntryClassAndDescriptor(Property property, NamespaceResolver nr){
    	JavaClass keyType = property.getKeyType();
        JavaClass valueType = property.getValueType();
        if(keyType == null){
        	keyType = helper.getJavaClass("java.lang.Object");
        }
        if(valueType == null){
        	valueType = helper.getJavaClass("java.lang.Object");
        }
        
        String mapEntryClassName = getJaxbClassLoader().nextAvailableGeneratedClassName();

        MapEntryGeneratedKey mapKey = new MapEntryGeneratedKey(keyType.getQualifiedName(),valueType.getQualifiedName());
    	Class generatedClass = getGeneratedMapEntryClasses().get(mapKey);

        if(generatedClass == null){
            generatedClass = generateMapEntryClass(mapEntryClassName, keyType.getQualifiedName(), valueType.getQualifiedName());
            getGeneratedMapEntryClasses().put(mapKey, generatedClass);
            XMLDescriptor desc = new XMLDescriptor();
            desc.setJavaClass(generatedClass);

            desc.addMapping(generateMappingForType(keyType, Property.DEFAULT_KEY_NAME));
            desc.addMapping(generateMappingForType(valueType, Property.DEFAULT_VALUE_NAME));
            desc.setNamespaceResolver(nr);
            project.addDescriptor(desc);
        }
        return generatedClass;
    }

    private Class generateMapEntryClass(String className, String keyType, String valueType){

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        String qualifiedInternalClassName = className.replace('.', '/');
        String qualifiedInternalKeyClassName = keyType.replace('.', '/');
        String qualifiedInternalValueClassName = valueType.replace('.', '/');

        String sig = "Ljava/lang/Object;Lorg/eclipse/persistence/internal/jaxb/many/MapEntry<L"+qualifiedInternalKeyClassName+";L"+qualifiedInternalValueClassName+";>;";
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, qualifiedInternalClassName, sig, "java/lang/Object", new String[] { "org/eclipse/persistence/internal/jaxb/many/MapEntry" });

        cw.visitField(Opcodes.ACC_PRIVATE, "key", "L"+qualifiedInternalKeyClassName+";", null, null);

        cw.visitField(Opcodes.ACC_PRIVATE, "value", "L"+qualifiedInternalValueClassName+";", null, null);

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getKey", "()L"+qualifiedInternalKeyClassName+";", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, qualifiedInternalClassName, "key", "L"+qualifiedInternalKeyClassName+";");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setKey", "(L"+qualifiedInternalKeyClassName+";)V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, qualifiedInternalClassName, "key", "L"+qualifiedInternalKeyClassName+";");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getValue", "()L"+qualifiedInternalValueClassName+";", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, qualifiedInternalClassName, "value", "L"+qualifiedInternalValueClassName+";");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setValue", "(L"+qualifiedInternalValueClassName+";)V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, qualifiedInternalClassName, "value", "L"+qualifiedInternalValueClassName+";");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        
        if(!qualifiedInternalValueClassName.equals("java/lang/Object")){
	        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "getValue", "()Ljava/lang/Object;", null, null);
	        mv.visitVarInsn(Opcodes.ALOAD, 0);
	        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, qualifiedInternalClassName, "getValue", "()L"+qualifiedInternalValueClassName+";");
	        mv.visitInsn(Opcodes.ARETURN);
	        mv.visitMaxs(1, 1);
	        mv.visitEnd();
	        
	        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "setValue", "(Ljava/lang/Object;)V", null, null);
	        mv.visitVarInsn(Opcodes.ALOAD, 0);
	        mv.visitVarInsn(Opcodes.ALOAD, 1);
	        mv.visitTypeInsn(Opcodes.CHECKCAST, qualifiedInternalValueClassName);
	        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, qualifiedInternalClassName, "setValue", "(L"+qualifiedInternalValueClassName+";)V");
	        mv.visitInsn(Opcodes.RETURN);
	        mv.visitMaxs(2, 2);
	        mv.visitEnd();
        }

        if(!qualifiedInternalKeyClassName.equals("java/lang/Object")){
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "getKey", "()Ljava/lang/Object;", null, null);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,qualifiedInternalClassName, "getKey", "()L"+qualifiedInternalKeyClassName+";");
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
            
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "setKey", "(Ljava/lang/Object;)V", null, null);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitTypeInsn(Opcodes.CHECKCAST, qualifiedInternalKeyClassName);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, qualifiedInternalClassName, "setKey", "(L"+qualifiedInternalKeyClassName+";)V");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        cw.visitEnd();

        byte[] classBytes =cw.toByteArray();
        Class generatedClass = getJaxbClassLoader().generateClass(className, classBytes);
        return generatedClass;
    }

    private DatabaseMapping generateMappingForType(JavaClass theType, String attributeName){
        DatabaseMapping mapping;
        boolean typeIsObject =  theType.getRawName().equals(OBJECT_CLASS_NAME);
        TypeInfo info = typeInfo.get(theType.getQualifiedName());
        if ((info != null && !(info.isEnumerationType())) || typeIsObject) {
            mapping = new XMLCompositeObjectMapping();
            mapping.setAttributeName(attributeName);
            ((XMLCompositeObjectMapping)mapping).setXPath(attributeName);
            if(typeIsObject){
            	((XMLCompositeObjectMapping)mapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
            	((XMLField)((XMLCompositeObjectMapping)mapping).getField()).setIsTypedTextField(true);
            	((XMLField)((XMLCompositeObjectMapping)mapping).getField()).setSchemaType(XMLConstants.ANY_TYPE_QNAME);
            }else{
            	((XMLCompositeObjectMapping)mapping).setReferenceClassName(theType.getQualifiedName());
            }
        } else {
            mapping = new XMLDirectMapping();
            mapping.setAttributeName(attributeName);
            ((XMLDirectMapping)mapping).setXPath(attributeName + TXT);

            QName schemaType = (QName) userDefinedSchemaTypes.get(theType.getQualifiedName());

            if (schemaType == null) {
                schemaType = (QName) helper.getXMLToJavaTypeMap().get(theType);
            }
            ((XMLField)((XMLDirectMapping)mapping).getField()).setSchemaType(schemaType);
            if(info != null && info.isEnumerationType()) {
                ((XMLDirectMapping)mapping).setConverter(buildJAXBEnumTypeConverter(mapping, (EnumTypeInfo)info));
            }
        }
        return mapping;
    }

    public XMLCompositeCollectionMapping generateCompositeCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, String referenceClassName) {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setReuseContainer(true);
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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

        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else if (property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
            mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        }
        
        JavaClass collectionType = property.getType();

        if (collectionType.isArray()){
            JAXBArrayAttributeAccessor accessor = new JAXBArrayAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy(), helper.getClassLoader());
            JavaClass componentType = collectionType.getComponentType();
            if(componentType.isArray()) {
                Class adaptedClass = classToGeneratedClasses.get(componentType.getName());
                referenceClassName = adaptedClass.getName();
                accessor.setAdaptedClassName(referenceClassName);
                JavaClass baseComponentType = getBaseComponentType(componentType);
                if (baseComponentType.isPrimitive()){
                    Class primitiveClass = XMLConversionManager.getDefaultManager().convertClassNameToClass(baseComponentType.getRawName());
                    accessor.setComponentClass(primitiveClass);
                } else {
                    accessor.setComponentClassName(baseComponentType.getQualifiedName());
                }
            } else {
                accessor.setComponentClassName(componentType.getQualifiedName());
            }
            mapping.setAttributeAccessor(accessor);
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }

        mapping.useCollectionClassName(collectionType.getRawName());

        // if the XPath is set (via xml-path) use it; otherwise figure it out
        XMLField xmlField = getXPathForField(property, namespaceInfo, false);
        mapping.setXPath(xmlField.getXPath());

        if (referenceClassName == null){
        	((XMLField)mapping.getField()).setIsTypedTextField(true);
        	((XMLField)mapping.getField()).setSchemaType(XMLConstants.ANY_TYPE_QNAME);
        } else {
        	mapping.setReferenceClassName(referenceClassName);
        }

        if (property.isRequired()) {
            ((XMLField) mapping.getField()).setRequired(true);
        }

        if (property.getInverseReferencePropertyName() != null) {
            mapping.setContainerAttributeName(property.getInverseReferencePropertyName());
            JavaClass backPointerPropertyType = null;
            JavaClass referenceClass = property.getActualType();
            if(property.getInverseReferencePropertyGetMethodName() != null && property.getInverseReferencePropertySetMethodName() != null && !property.getInverseReferencePropertyGetMethodName().equals("") && !property.getInverseReferencePropertySetMethodName().equals("")) {
                mapping.setContainerGetMethodName(property.getInverseReferencePropertySetMethodName());
                mapping.setContainerSetMethodName(property.getInverseReferencePropertySetMethodName());
                JavaMethod getMethod = referenceClass.getDeclaredMethod(mapping.getContainerGetMethodName(), new JavaClass[]{});
                if(getMethod != null) {
                    backPointerPropertyType = getMethod.getReturnType();
                }
            } else {
                JavaField backpointerField = referenceClass.getDeclaredField(property.getInverseReferencePropertyName());
                if (backpointerField != null) {
                    backPointerPropertyType = backpointerField.getResolvedType();
                }
            }
            if (isCollectionType(backPointerPropertyType)) {
                mapping.getInverseReferenceMapping().setContainerPolicy(ContainerPolicy.buildDefaultPolicy());
            }
        }
        return mapping;
    }

    public XMLCompositeDirectCollectionMapping generateDirectCollectionMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setReuseContainer(true);
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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

        if (collectionType.isArray()){
            JAXBArrayAttributeAccessor accessor = new JAXBArrayAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy(), helper.getClassLoader());
            String componentClassName = collectionType.getComponentType().getQualifiedName();
            if (collectionType.getComponentType().isPrimitive()){
                Class primitiveClass = XMLConversionManager.getDefaultManager().convertClassNameToClass(componentClassName);
                accessor.setComponentClass(primitiveClass);
                mapping.setAttributeAccessor(accessor);

                Class declaredClass = XMLConversionManager.getDefaultManager().getObjectClass(primitiveClass);
                mapping.setAttributeElementClass(declaredClass);
            } else {
                accessor.setComponentClassName(componentClassName);
                mapping.setAttributeAccessor(accessor);

                JavaClass componentType = collectionType.getComponentType();
                try{
                    Class declaredClass = PrivilegedAccessHelper.getClassForName(componentType.getRawName(), false, helper.getClassLoader());
                    mapping.setAttributeElementClass(declaredClass);
                }catch (Exception e) {}
            }
    		collectionType = jotArrayList;
        } else if (isCollectionType(collectionType)){
        	if (collectionType.hasActualTypeArguments()){
        		JavaClass itemType = (JavaClass)collectionType.getActualTypeArguments().toArray()[0];
        		try {
        			Class declaredClass = PrivilegedAccessHelper.getClassForName(itemType.getRawName(), false, helper.getClassLoader());
        			mapping.setAttributeElementClass(declaredClass);
        		} catch (Exception e) {}
        	}
        }
        if (areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());

        // if the XPath is set (via xml-path) use it; otherwise figure it out
        XMLField xmlField = getXPathForField(property, namespaceInfo, true);
        mapping.setField(xmlField);

        if (helper.isAnnotationPresent(property.getElement(), XmlMixed.class)) {
            xmlField.setXPath("text()");
        }

        if (XMLConstants.QNAME_QNAME.equals(property.getSchemaType())){
            ((XMLField) mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
        }

        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else if (property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
            mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        }
        mapping.getNullPolicy().setNullRepresentedByEmptyNode(false);

        if (property.isRequired()) {
            ((XMLField) mapping.getField()).setRequired(true);
        }

        if (property.isXmlElementType() && property.getGenericType()!=null ){
        	Class theClass = helper.getClassForJavaClass(property.getGenericType());
        	mapping.setAttributeElementClass(theClass);
        }

        if (xmlField.getXPathFragment().isAttribute() || property.isXmlList() || xmlField.getXPathFragment().nameIsText()){
            mapping.setUsesSingleNode(true);
        }
        // handle cdata set via metadata
        if (property.isSetCdata()) {
            mapping.setIsCDATA(property.isCdata());
        }
        return mapping;
    }

    public String getPrefixForNamespace(String URI, org.eclipse.persistence.oxm.NamespaceResolver namespaceResolver, String suggestedPrefix) {
    	return getPrefixForNamespace(URI, namespaceResolver, suggestedPrefix, true);
    }
    
    public String getPrefixForNamespace(String URI, org.eclipse.persistence.oxm.NamespaceResolver namespaceResolver, String suggestedPrefix, boolean addPrefixToNR) {
    	String defaultNS = namespaceResolver.getDefaultNamespaceURI();
    	if(defaultNS != null && URI.equals(defaultNS)){
    		return null;
    	}
        Enumeration keys = namespaceResolver.getPrefixes();
        while (keys.hasMoreElements()) {
            String next = (String) keys.nextElement();
            String nextUri = namespaceResolver.resolveNamespacePrefix(next);
            if (nextUri.equals(URI)) {
                return next;
            }
        }
        if (XMLConstants.XML_NAMESPACE_URL.equals(URI)) {
        	return XMLConstants.XML_NAMESPACE_PREFIX;
        }           
        String prefix = globalNamespaceResolver.resolveNamespaceURI(URI);
        if(prefix == null){
            if(suggestedPrefix != null){
        	    prefix = globalNamespaceResolver.generatePrefix(suggestedPrefix);
            }else{
        	    prefix = globalNamespaceResolver.generatePrefix();
            }
        }
       
        String nrUri = namespaceResolver.resolveNamespacePrefix(prefix);
    	while(null != nrUri && !URI.equals(nrUri)){
        	prefix = globalNamespaceResolver.generatePrefix();
        	nrUri = namespaceResolver.resolveNamespacePrefix(prefix);
        }
        if(addPrefixToNR){
        	namespaceResolver.put(prefix, URI);        	
        }
        globalNamespaceResolver.put(prefix, URI);
        return prefix;
    }

    public boolean isCollectionType(Property field) {
        JavaClass type = field.getType();
        return isCollectionType(type);
    }
    public boolean isCollectionType(JavaClass type) {
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
        TypeInfo tInfo = typeInfo.get(jClass.getName());
        XMLDescriptor descriptor = tInfo.getDescriptor();
        if (descriptor == null) {
            return;
        }

        JavaClass superClass = CompilerHelper.getNextMappedSuperClass(jClass, typeInfo, helper);
        if (superClass == null){
            return;
        }

        TypeInfo superTypeInfo =  typeInfo.get(superClass.getName());
        if (superTypeInfo == null){
        	return;
        }
        XMLDescriptor superDescriptor = superTypeInfo.getDescriptor();
        if (superDescriptor != null) {
            XMLSchemaReference sRef = descriptor.getSchemaReference();
            if (sRef == null || sRef.getSchemaContext() == null) {
                return;
            }

            JavaClass rootMappedSuperClass = getRootMappedSuperClass(superClass);
            TypeInfo rootTypeInfo =  typeInfo.get(rootMappedSuperClass.getName());
            XMLDescriptor rootDescriptor = rootTypeInfo.getDescriptor();
            if (rootDescriptor.getNamespaceResolver() == null) {
                rootDescriptor.setNamespaceResolver(new NamespaceResolver());
            }

            if (rootDescriptor.getInheritancePolicy().getClassIndicatorField() == null) {
                XMLField classIndicatorField;
                if (rootTypeInfo.isSetXmlDiscriminatorNode()) {
                    classIndicatorField = new XMLField(rootTypeInfo.getXmlDiscriminatorNode());
                } else {
                    classIndicatorField = new XMLField(ATT + "type");
                    classIndicatorField.getXPathFragment().setNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
                }
            	rootDescriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
            }

            Object sCtx = null;
            //TypeInfo tInfo = typeInfo.get(jClass.getName());
            if (tInfo.isSetXmlDiscriminatorValue()) {
                sCtx = tInfo.getXmlDiscriminatorValue();
            } else if(!tInfo.isAnonymousComplexType()){
            	sCtx = sRef.getSchemaContextAsQName();
            }
            if(sCtx != null) {
                descriptor.getInheritancePolicy().setParentClassName(superClass.getName());
                rootDescriptor.getInheritancePolicy().addClassNameIndicator(jClass.getName(), sCtx);
            }
            Object value = rootDescriptor.getInheritancePolicy().getClassNameIndicatorMapping().get(rootDescriptor.getJavaClassName());
            if (value == null){
                if (rootTypeInfo.isSetXmlDiscriminatorValue()) {
                    rootDescriptor.getInheritancePolicy().addClassNameIndicator(rootDescriptor.getJavaClassName(), rootTypeInfo.getXmlDiscriminatorValue());
                } else {
                    XMLSchemaReference rootSRef = rootDescriptor.getSchemaReference();
                    if (rootSRef != null && rootSRef.getSchemaContext() != null) {
                    	QName rootSCtx = rootSRef.getSchemaContextAsQName();
                        rootDescriptor.getInheritancePolicy().addClassNameIndicator(rootDescriptor.getJavaClassName(), rootSCtx);
                    }
                }
            }
            rootDescriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        }
    }

    private JavaClass getRootMappedSuperClass(JavaClass javaClass){
        JavaClass rootMappedSuperClass = javaClass;

        JavaClass nextMappedSuperClass = rootMappedSuperClass;
        while(nextMappedSuperClass != null){
            nextMappedSuperClass = CompilerHelper.getNextMappedSuperClass(nextMappedSuperClass, this.typeInfo, helper);
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
            NamespaceInfo namespaceInfo = this.packageToPackageInfoMappings.get(javaClass.getPackageName()).getNamespaceInfo();
            if (info.isEnumerationType()) {
                continue;
            }
            XMLDescriptor descriptor = info.getDescriptor();
            if (descriptor != null) {
                generateMappings(info, descriptor, namespaceInfo);
            }
            // set primary key fields (if necessary)
            DatabaseMapping mapping;
            // handle XmlID
            if (info.isIDSet()) {
                mapping = descriptor.getMappingForAttributeName(info.getIDProperty().getPropertyName());
                if (mapping != null) {
                    descriptor.addPrimaryKeyField(mapping.getField());
                }
            }
            // handle XmlKey
            if (info.hasXmlKeyProperties()) {
                for (Property keyProp : info.getXmlKeyProperties()) {
                    mapping = descriptor.getMappingForAttributeName(keyProp.getPropertyName());
                    if (mapping != null) {
                        descriptor.addPrimaryKeyField(mapping.getField());
                    }                    
                }
            }
        }
    }

    /**
     * Generate mappings for a given TypeInfo.
     *
     * @param info
     * @param descriptor
     * @param namespaceInfo
     */
    public void generateMappings(TypeInfo info, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        if(info.isAnonymousComplexType()) {
            //may need to generate inherited mappings
            generateInheritedMappingsForAnonymousType(info, descriptor, namespaceInfo);
        }
        List<Property> propertiesInOrder = info.getNonTransientPropertiesInPropOrder();
        for (int i = 0; i < propertiesInOrder.size(); i++) {
            Property next = propertiesInOrder.get(i);
            if (next != null && (!next.isTransient() || (next.isTransient() && next.isXmlLocation()))) {
                DatabaseMapping mapping = generateMapping(next, descriptor, namespaceInfo);
                if (next.isVirtual()) {
                    VirtualAttributeAccessor accessor = new VirtualAttributeAccessor();
                    accessor.setAttributeName(mapping.getAttributeName());

                    String getMethod = info.getXmlVirtualAccessMethods().getGetMethod();
                    String setMethod = info.getXmlVirtualAccessMethods().getSetMethod();

                    // Check to see if get/set were overridden in the mapping
                    if (mapping.getAttributeAccessor().isMethodAttributeAccessor()) {
                        getMethod = ((MethodAttributeAccessor) mapping.getAttributeAccessor()).getGetMethodName();
                        setMethod = ((MethodAttributeAccessor) mapping.getAttributeAccessor()).getSetMethodName();
                        accessor.setValueType(mapping.getAttributeClassification());
                    }

                    accessor.setGetMethodName(getMethod);
                    accessor.setSetMethodName(setMethod);

                    if (mapping.getAttributeAccessor() instanceof JAXBArrayAttributeAccessor) {
                        JAXBArrayAttributeAccessor jaa = (JAXBArrayAttributeAccessor) mapping.getAttributeAccessor();
                        jaa.setNestedAccessor(accessor);
                    } else {
                        mapping.setAttributeAccessor(accessor);
                    }
                }
                if (mapping != null) {
                    descriptor.addMapping(mapping);
                }
                // set user-defined properties if necessary
                if (next.isSetUserProperties()) {
                    mapping.setProperties(next.getUserProperties());
                }
            }
        }
    }

    private void generateInheritedMappingsForAnonymousType(TypeInfo info, XMLDescriptor descriptor, NamespaceInfo namespaceInfo) {
        List<TypeInfo> mappedParents = new ArrayList<TypeInfo>();
        JavaClass next = CompilerHelper.getNextMappedSuperClass(helper.getJavaClass(info.getJavaClassName()), typeInfo, helper);
        while(next != null) {
            TypeInfo nextInfo = this.typeInfo.get(next.getName());
            mappedParents.add(0, nextInfo);
            next = CompilerHelper.getNextMappedSuperClass(helper.getJavaClass(nextInfo.getJavaClassName()), typeInfo, helper);
        }
        for(TypeInfo nextInfo:mappedParents) {
            List<Property> propertiesInOrder = nextInfo.getNonTransientPropertiesInPropOrder();
            for (int i = 0; i < propertiesInOrder.size(); i++) {
                Property nextProp = propertiesInOrder.get(i);
                if (nextProp != null){
                    DatabaseMapping mapping = generateMapping(nextProp, descriptor, namespaceInfo);
                    descriptor.addMapping(mapping);
                    // set user-defined properties if necessary
                    if (nextProp.isSetUserProperties()) {
                        mapping.setProperties(nextProp.getUserProperties());
                    }
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
    public XMLCollectionReferenceMapping generateXMLCollectionReferenceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        XMLCollectionReferenceMapping mapping = new XMLCollectionReferenceMapping();
        mapping.setAttributeName(property.getPropertyName());
        mapping.setReuseContainer(true);
        mapping.setUsesSingleNode(property.isXmlList() || property.isAttribute());
        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        if (collectionType.isArray() || areEquals(collectionType, Collection.class) || areEquals(collectionType, List.class)) {
            collectionType = jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            collectionType = jotHashSet;
        }
        mapping.useCollectionClassName(collectionType.getRawName());
        
        // here we need to setup source/target key field associations
        if (property.isSetXmlJoinNodes()) {
            for (XmlJoinNode xmlJoinNode: property.getXmlJoinNodes().getXmlJoinNode()) {
               validateJoinNode(descriptor.getJavaClassName(), property, xmlJoinNode.getReferencedXmlPath(), referenceClass);
               mapping.addSourceToTargetKeyFieldAssociation(xmlJoinNode.getXmlPath(), xmlJoinNode.getReferencedXmlPath());
            }
        } else {
            // here we need to setup source/target key field associations
            TypeInfo referenceType = typeInfo.get(referenceClass.getQualifiedName());
            String tgtXPath = null;
            if (null != referenceType && referenceType.isIDSet()) {
                Property prop = referenceType.getIDProperty();
                tgtXPath = getXPathForField(prop, namespaceInfo, !prop.isAttribute()).getXPath();
            }
            // if the XPath is set (via xml-path) use it
            XMLField srcXPath;
            if (property.getXmlPath() != null) {
                srcXPath = new XMLField(property.getXmlPath());
            } else {
                srcXPath = getXPathForField(property, namespaceInfo, true);
            }
            mapping.addSourceToTargetKeyFieldAssociation(srcXPath.getXPath(), tgtXPath);
        }
        if (property.getInverseReferencePropertyName() != null) {
            mapping.getInverseReferenceMapping().setAttributeName(property.getInverseReferencePropertyName());
            JavaClass backPointerPropertyType = null;
            if (property.getInverseReferencePropertyGetMethodName() != null && property.getInverseReferencePropertySetMethodName() != null && !property.getInverseReferencePropertyGetMethodName().equals("") && !property.getInverseReferencePropertySetMethodName().equals("")) {
                mapping.getInverseReferenceMapping().setGetMethodName(property.getInverseReferencePropertySetMethodName());
                mapping.getInverseReferenceMapping().setSetMethodName(property.getInverseReferencePropertySetMethodName());
                JavaMethod getMethod = referenceClass.getDeclaredMethod(mapping.getInverseReferenceMapping().getGetMethodName(), new JavaClass[]{});
                if (getMethod != null) {
                    backPointerPropertyType = getMethod.getReturnType();
                }
            } else {
                JavaField backpointerField = referenceClass.getDeclaredField(property.getInverseReferencePropertyName());
                if (backpointerField != null) {
                    backPointerPropertyType = backpointerField.getResolvedType();
                }
            }
            if (isCollectionType(backPointerPropertyType)) {
                mapping.getInverseReferenceMapping().setContainerPolicy(ContainerPolicy.buildDefaultPolicy());
            }
        }
        return mapping;
    }
    /**
     * Create an XMLObjectReferenceMapping and add it to the descriptor.
     *
     * @param property
     * @param descriptor
     * @param namespaceInfo
     * @param referenceClass
     */
    public XMLObjectReferenceMapping generateXMLObjectReferenceMapping(Property property, XMLDescriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        XMLObjectReferenceMapping mapping = new XMLObjectReferenceMapping();
        mapping.setAttributeName(property.getPropertyName());

        // handle read-only set via metadata
        if (property.isSetReadOnly()) {
            mapping.setIsReadOnly(property.isReadOnly());
        }
        // handle write-only set via metadata
        if (property.isSetWriteOnly()) {
            mapping.setIsWriteOnly(property.isWriteOnly());
        }
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
        if (property.isSetXmlJoinNodes()) {
            for (XmlJoinNode xmlJoinNode: property.getXmlJoinNodes().getXmlJoinNode()) {
                validateJoinNode(descriptor.getJavaClassName(), property, xmlJoinNode.getReferencedXmlPath(), referenceClass);
                mapping.addSourceToTargetKeyFieldAssociation(xmlJoinNode.getXmlPath(), xmlJoinNode.getReferencedXmlPath());
            }
        } else {
            String tgtXPath = null;
            TypeInfo referenceType = typeInfo.get(referenceClass.getQualifiedName());
            if (null != referenceType && referenceType.isIDSet()) {
                Property prop = referenceType.getIDProperty();
                tgtXPath = getXPathForField(prop, namespaceInfo, !prop.isAttribute()).getXPath();
            }
            // if the XPath is set (via xml-path) use it, otherwise figure it out
            XMLField srcXPath;
            if (property.getXmlPath() != null) {
                srcXPath = new XMLField(property.getXmlPath());
            } else {
                srcXPath = getXPathForField(property, namespaceInfo, true);
            }
            mapping.addSourceToTargetKeyFieldAssociation(srcXPath.getXPath(), tgtXPath);
        }        
        if (property.getInverseReferencePropertyName() != null) {
            mapping.getInverseReferenceMapping().setAttributeName(property.getInverseReferencePropertyName());
            JavaClass backPointerPropertyType = null;
            if (property.getInverseReferencePropertyGetMethodName() != null && property.getInverseReferencePropertySetMethodName() != null && !property.getInverseReferencePropertyGetMethodName().equals("") && !property.getInverseReferencePropertySetMethodName().equals("")) {
                mapping.getInverseReferenceMapping().setGetMethodName(property.getInverseReferencePropertySetMethodName());
                mapping.getInverseReferenceMapping().setSetMethodName(property.getInverseReferencePropertySetMethodName());
                JavaMethod getMethod = referenceClass.getDeclaredMethod(mapping.getInverseReferenceMapping().getGetMethodName(), new JavaClass[]{});
                if (getMethod != null) {
                    backPointerPropertyType = getMethod.getReturnType();
                }
            } else {
                JavaField backpointerField = referenceClass.getDeclaredField(property.getInverseReferencePropertyName());
                if (backpointerField != null) {
                    backPointerPropertyType = backpointerField.getResolvedType();
                }
            }
            if (isCollectionType(backPointerPropertyType)) {
                mapping.getInverseReferenceMapping().setContainerPolicy(ContainerPolicy.buildDefaultPolicy());
            }
        }
        return mapping;
    }

    private void validateJoinNode(String className, Property property, String referencedXmlPath, JavaClass referenceClass) {
        TypeInfo targetInfo = this.typeInfo.get(referenceClass.getQualifiedName());
        NamespaceInfo namespaceInfo = this.packageToPackageInfoMappings.get(referenceClass.getPackageName()).getNamespaceInfo();        
        Property idProp = targetInfo.getIDProperty();
        if(idProp != null) {
            String idXpath = idProp.getXmlPath();
            if(idXpath == null) {
                idXpath = this.getXPathForField(idProp, namespaceInfo, !idProp.isAttribute()).getXPath();
            }
            if (referencedXmlPath.equals(idXpath)) {
                return;
            }
        }
        boolean matched = false;
        if (targetInfo.getXmlKeyProperties() != null) {
            for (Property xmlkeyProperty : targetInfo.getXmlKeyProperties()) {
                String keyXpath = xmlkeyProperty.getXmlPath();
                if(keyXpath == null) {
                    keyXpath = this.getXPathForField(xmlkeyProperty, namespaceInfo, !xmlkeyProperty.isAttribute()).getXPath();
                }
                if (referencedXmlPath.equals(keyXpath)) {
                    matched = true;
                    break;
                }
            }
        }
        if (!matched) {
            throw JAXBException.invalidReferencedXmlPathOnJoin(className, property.getPropertyName(), referenceClass.getQualifiedName(), referencedXmlPath);
        }
    }

    public XMLField getXPathForField(Property property, NamespaceInfo namespaceInfo, boolean isTextMapping) {
        XMLField xmlField = null;
        if (property.getXmlPath() != null) {
            xmlField = new XMLField(property.getXmlPath());
        } else {
            String xPath = "";
            if (property.isSetXmlElementWrapper()) {
                XmlElementWrapper wrapper = property.getXmlElementWrapper();
                String namespace = wrapper.getNamespace();
                if (namespace.equals("##default")) {
                    if (namespaceInfo.isElementFormQualified()) {
                        namespace = namespaceInfo.getNamespace();
                    } else {
                        namespace = "";
                    }
                }
                if (namespace.equals("")) {
                    xPath += (wrapper.getName() + "/");
                } else {
                	String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor(), null);
                	xPath += getQualifiedString(prefix, wrapper.getName() + "/");
                }
            }
            if (property.isAttribute()) {
                if (property.isSetXmlPath()) {
                    xPath += property.getXmlPath();
                } else {
                    QName name = property.getSchemaName();
                    String namespace = "";
                    if (namespaceInfo.isAttributeFormQualified()) {
                        namespace = namespaceInfo.getNamespace();
                    }
                    if (!name.getNamespaceURI().equals("")) {
                        namespace = name.getNamespaceURI();
                    }
                    if (namespace.equals("")) {
                        xPath += (ATT + name.getLocalPart());
                    } else {
                        String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor(), null);
                    	xPath += ATT + getQualifiedString(prefix, name.getLocalPart());
                    }
                }
                xmlField = new XMLField(xPath);
            } else if (property.isXmlValue()) {                
            	if(isBinaryData(property.getActualType())){
            		xmlField = new XMLField(".");
            	}else{
            		xmlField = new XMLField("text()");
            	}
            } else {
                QName elementName = property.getSchemaName();
                xmlField = getXPathForElement(xPath, elementName, namespaceInfo, isTextMapping);
            }
        }

        QName schemaType = (QName) userDefinedSchemaTypes.get(property.getActualType().getQualifiedName());
        if (property.getSchemaType() != null) {
            schemaType = property.getSchemaType();
        }
        if (schemaType == null) {
            String propertyActualTypeRawName = property.getActualType().getRawName();
            if(QName.class.getCanonicalName().equals(propertyActualTypeRawName)) {
                 schemaType = (QName) helper.getXMLToJavaTypeMap().get(propertyActualTypeRawName);
            }
        }
        if(schemaType !=null && !schemaType.equals (XMLConstants.NORMALIZEDSTRING_QNAME)){
            xmlField.setSchemaType(schemaType);
        }
        return xmlField;
    }

    public XMLField getXPathForElement(String path, QName elementName, NamespaceInfo namespaceInfo, boolean isText) {
        String namespace = "";
        if (!elementName.getNamespaceURI().equals(XMLConstants.EMPTY_STRING)) {
            namespace = elementName.getNamespaceURI();
        }
        if (namespace.equals(XMLConstants.EMPTY_STRING)) {
            path += elementName.getLocalPart();
            if (isText) {
                path += TXT;
            }
        } else {
            String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor(), null);
        	path += getQualifiedString(prefix, elementName.getLocalPart());
            if (isText) {
                path += TXT;
            }
        }
        return new XMLField(path);
    }

    public Property getXmlValueFieldForSimpleContent(ArrayList<Property> properties) {
        boolean foundValue = false;
        boolean foundNonAttribute = false;
        Property valueField = null;

        for (Property prop : properties) {
            if (helper.isAnnotationPresent(prop.getElement(), XmlValue.class)) {
                foundValue = true;
                valueField = prop;
            } else if (!helper.isAnnotationPresent(prop.getElement(), XmlAttribute.class) && !helper.isAnnotationPresent(prop.getElement(), XmlTransient.class) && !prop.isAnyAttribute()) {
                foundNonAttribute = true;
            }
        }
        if (foundValue && !foundNonAttribute) {
            return valueField;
        }
        return null;
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
        if(this.globalElements == null && this.localElements == null) {
            return;
        }
        List<ElementDeclaration> elements = new ArrayList<ElementDeclaration>();
        elements.addAll(this.localElements);
        elements.addAll(this.globalElements.values());
        for(ElementDeclaration nextElement:elements) {
            QName next = nextElement.getElementName();
            String nextClassName = nextElement.getJavaTypeName();
            TypeInfo type = this.typeInfo.get(nextClassName);

            if(helper.isBuiltInJavaType(nextElement.getJavaType()) || (type !=null && type.isEnumerationType())){

                //generate a class/descriptor for this element
                String attributeTypeName = nextClassName;
                if(nextElement.getJavaType().isPrimitive()) {
                    attributeTypeName = helper.getClassForJavaClass(nextElement.getJavaType()).getName();
                }
                if (nextElement.getAdaptedJavaTypeName() != null) {
                    attributeTypeName = nextElement.getAdaptedJavaTypeName();
                }

                if(next == null){
                	if(isBinaryData(nextElement.getJavaType())){
            			Class generatedClass = addByteArrayWrapperAndDescriptor(type, nextElement.getJavaType().getRawName(), nextElement,nextClassName, attributeTypeName);
            			 this.qNamesToGeneratedClasses.put(next, generatedClass);
                         if(nextElement.getTypeMappingInfo() != null) {
                             typeMappingInfoToGeneratedClasses.put(nextElement.getTypeMappingInfo(), generatedClass);
                         }
                         try{
                             Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
                             this.qNamesToDeclaredClasses.put(next, declaredClass);
                         }catch(Exception e){
                         }
            		} else if(nextElement.getJavaType().isEnum() && !(helper.getClassLoader() instanceof DynamicClassLoader)) {
            		    //Only generate enum wrappers in non-dynamic case.
                        Class generatedClass = addEnumerationWrapperAndDescriptor(type, nextElement.getJavaType().getRawName(), nextElement, nextClassName, attributeTypeName);
                        this.qNamesToGeneratedClasses.put(next, generatedClass);
                        if(nextElement.getTypeMappingInfo() != null) {
                            typeMappingInfoToGeneratedClasses.put(nextElement.getTypeMappingInfo(), generatedClass);
                        }
                        try{
                            Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
                            this.qNamesToDeclaredClasses.put(next, declaredClass);
                        }catch(Exception e){
                        }
            		}
            		continue;
            	}
                Class generatedClass = generateWrapperClassAndDescriptor(type, next, nextElement, nextClassName, attributeTypeName);

                this.qNamesToGeneratedClasses.put(next, generatedClass);
                try{
                    Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
                    this.qNamesToDeclaredClasses.put(next, declaredClass);
                }catch(Exception e){

                }
            }else if(type != null && !type.isTransient()){
                if(next.getNamespaceURI() == null || next.getNamespaceURI().equals("")) {
                	if(type.getDescriptor().getDefaultRootElement() == null){
                		type.getDescriptor().setDefaultRootElement(next.getLocalPart());
                	}else{
                        type.getDescriptor().addRootElement(next.getLocalPart());
                	}
                } else {
                    XMLDescriptor descriptor = type.getDescriptor();
                    String uri = next.getNamespaceURI();
                    String prefix = getPrefixForNamespace(uri, descriptor.getNamespaceResolver(),null);
                    if(type.getDescriptor().getDefaultRootElement() == null){
                		descriptor.setDefaultRootElement(getQualifiedString(prefix, next.getLocalPart()));
                	}else{
                        descriptor.addRootElement(getQualifiedString(prefix, next.getLocalPart()));
                	}
                }
            }
        }
    }

    private Class addByteArrayWrapperAndDescriptor(TypeInfo type , String javaClassName,  ElementDeclaration nextElement, String nextClassName, String attributeTypeName){
    	Class generatedClass = classToGeneratedClasses.get(javaClassName);
    	if(generatedClass == null){
    		generatedClass = generateWrapperClassAndDescriptor(type, null, nextElement, nextClassName, attributeTypeName);
    		classToGeneratedClasses.put(javaClassName, generatedClass);
    	}
    	return generatedClass;
    }

    private Class addEnumerationWrapperAndDescriptor(TypeInfo type, String javaClassName, ElementDeclaration nextElement, String nextClassName, String attributeTypeName) {
        Class generatedClass = classToGeneratedClasses.get(javaClassName);
        if(generatedClass == null){
            generatedClass = generateWrapperClassAndDescriptor(type, null, nextElement, nextClassName, attributeTypeName);
            classToGeneratedClasses.put(javaClassName, generatedClass);
        }
        return generatedClass;
    }

    private Class generateWrapperClassAndDescriptor(TypeInfo type, QName next, ElementDeclaration nextElement, String nextClassName, String attributeTypeName){
        String namespaceUri = null;
      	if(next!= null){
              //generate a class/descriptor for this element
              namespaceUri = next.getNamespaceURI();
              if(namespaceUri == null || namespaceUri.equals("##default")) {
                  namespaceUri = "";
              }
      	}

      	TypeMappingInfo tmi = nextElement.getTypeMappingInfo();
      	Class generatedClass = null;

        JaxbClassLoader loader = getJaxbClassLoader();

      	if(tmi != null){
            generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses, helper.getClassLoader());
            if(generatedClass == null){
            	generatedClass = this.generateWrapperClass(loader.nextAvailableGeneratedClassName(), attributeTypeName, nextElement.isList(), next);
            }

            typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);
      	}else{
      	    generatedClass = this.generateWrapperClass(loader.nextAvailableGeneratedClassName(), attributeTypeName, nextElement.isList(), next);
      	}

          this.qNamesToGeneratedClasses.put(next, generatedClass);
          try{
              Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
              this.qNamesToDeclaredClasses.put(next, declaredClass);
          }catch(Exception e){

          }

          XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(generatedClass);

          if(desc == null){
	          desc = new XMLDescriptor();
	          desc.setJavaClass(generatedClass);


	          if(nextElement.isList()){
	              XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
	              mapping.setAttributeName("value");
	              mapping.setXPath("text()");
	              mapping.setUsesSingleNode(true);
	              mapping.setReuseContainer(true);

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
	              if(nextElement.getJavaTypeName().equals(OBJECT_CLASS_NAME)){
	                  XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
	                  mapping.setAttributeName("value");
	                  mapping.setSetMethodName("setValue");
	                  mapping.setGetMethodName("getValue");
	                  mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
	                  mapping.setXPath(".");
	                  ((XMLField)mapping.getField()).setIsTypedTextField(true);
	                  ((XMLField)mapping.getField()).setSchemaType(XMLConstants.ANY_TYPE_QNAME);
	                  desc.addMapping(mapping);	                 
	              }else if(isBinaryData(nextElement.getJavaType())){
	              	  XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
	              	  mapping.setAttributeName("value");
	              	  mapping.setXPath(".");
	                  ((XMLField)mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
	                  mapping.setSetMethodName("setValue");
	                  mapping.setGetMethodName("getValue");

	                  Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(attributeTypeName, getClass().getClassLoader());
	                  mapping.setAttributeClassification(attributeClassification);

	              	  mapping.setShouldInlineBinaryData(false);
	              	  if(nextElement.getTypeMappingInfo() != null) {
	              	      mapping.setSwaRef(nextElement.isXmlAttachmentRef());
	              	      mapping.setMimeType(nextElement.getXmlMimeType());
	              	  }
	                  desc.addMapping(mapping);

	              }else{
	                  XMLDirectMapping mapping = new XMLDirectMapping();
	                  mapping.setAttributeName("value");
	                  mapping.setXPath("text()");
	                  mapping.setSetMethodName("setValue");
	                  mapping.setGetMethodName("getValue");
	                  if(nextElement.getDefaultValue() != null) {
	                      mapping.setNullValue(nextElement.getDefaultValue());
	                      mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
	                  }
	                  

	                  if(helper.isBuiltInJavaType(nextElement.getJavaType())){
	                      Class attributeClassification = null;
	                      if(nextElement.getJavaType().isPrimitive()) {
	                          attributeClassification = XMLConversionManager.getDefaultManager().convertClassNameToClass(attributeTypeName);
	                      } else {
	                          attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(attributeTypeName, getClass().getClassLoader());
	                      }
	                      mapping.setAttributeClassification(attributeClassification);
	                  }

	                  IsSetNullPolicy nullPolicy = new IsSetNullPolicy("isSetValue", false, true, XMLNullRepresentationType.ABSENT_NODE);
	                  //nullPolicy.setNullRepresentedByEmptyNode(true);
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
	          if(next != null){
	              NamespaceInfo info = getNamespaceInfoForURI(namespaceUri);

	  			if(info != null) {
	  				NamespaceResolver resolver = info.getNamespaceResolverForDescriptor();
	  				
	  				String prefix = null;
	  				if(namespaceUri != XMLConstants.EMPTY_STRING){
	  				    prefix = resolver.resolveNamespaceURI(namespaceUri);
	  				    if(prefix == null){
	  					    prefix = getPrefixForNamespace(namespaceUri, resolver, null);	  					
	  				    }
	  				}
	  				desc.setNamespaceResolver(resolver);
	  				desc.setDefaultRootElement("");
	  				desc.addRootElement(getQualifiedString(prefix, next.getLocalPart()));
	              } else {
	                  if(namespaceUri.equals("")) {
	                      desc.setDefaultRootElement(next.getLocalPart());
	                  } else {
	                      NamespaceResolver resolver = new NamespaceResolver();
	                      String prefix = getPrefixForNamespace(namespaceUri, resolver, null);

	                      desc.setNamespaceResolver(resolver);
	                      desc.setDefaultRootElement("");
	                      desc.addRootElement(getQualifiedString(prefix, next.getLocalPart()));
	  				}
	  			}
	          }
	          project.addDescriptor(desc);
          }
          return generatedClass;
    }

    private String getQualifiedString(String prefix, String localPart){
    	if(prefix == null){
    		return localPart;
    	}
    	return prefix + XMLConstants.COLON + localPart;
    }

    private NamespaceInfo getNamespaceInfoForURI(String namespaceUri) {
        Iterator<PackageInfo> namespaces = this.packageToPackageInfoMappings.values().iterator();
        while(namespaces.hasNext()) {
            NamespaceInfo next = namespaces.next().getNamespaceInfo();
            if(next.getNamespace().equals(namespaceUri)) {
                return next;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private String getPackageNameForURI(String namespaceUri) {
        for(String next:this.packageToPackageInfoMappings.keySet()) {
            if(packageToPackageInfoMappings.get(next).getNamespace().equals(namespaceUri)) {
                return next;
            }
        }
        return null;
    }

    public Class generateWrapperClass(String className, String attributeType, boolean isList, QName theQName) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        String sig = null;
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, className.replace(".", "/"), sig, Type.getType(WrappedValue.class).getInternalName(), null);

        String fieldType = null;
        if(isList){
            fieldType ="Ljava/util/List;";
        }else{
            fieldType = attributeType.replace(".", "/");
            if(!(fieldType.startsWith("["))) {
                fieldType = "L" + fieldType + ";";
            }
        }

        	if(theQName == null){
        		theQName = RESERVED_QNAME;
        	}

	        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);

	        mv.visitVarInsn(Opcodes.ALOAD, 0);
	        mv.visitTypeInsn(Opcodes.NEW, "javax/xml/namespace/QName");
	        mv.visitInsn(Opcodes.DUP);
	        mv.visitLdcInsn(theQName.getNamespaceURI());
	        mv.visitLdcInsn(theQName.getLocalPart());
	        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "javax/xml/namespace/QName", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
	        mv.visitLdcInsn(Type.getType(fieldType));
	        mv.visitInsn(Opcodes.ACONST_NULL);

	        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/eclipse/persistence/internal/jaxb/WrappedValue", "<init>", "(Ljavax/xml/namespace/QName;Ljava/lang/Class;Ljava/lang/Object;)V");
	        mv.visitInsn(Opcodes.RETURN);
	        mv.visitMaxs(5, 1);
	        mv.visitEnd();


        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();
        //byte[] classBytes = new byte[]{};

        Class generatedClass = getJaxbClassLoader().generateClass(className, classBytes);
        return generatedClass;
    }

    public HashMap<QName, Class> getQNamesToGeneratedClasses() {
        return qNamesToGeneratedClasses;
    }

    public HashMap<String, Class> getClassToGeneratedClasses() {
        return classToGeneratedClasses;
    }
    public HashMap<QName, Class> getQNamesToDeclaredClasses() {
        return qNamesToDeclaredClasses;
    }

    private Map<MapEntryGeneratedKey, Class> getGeneratedMapEntryClasses() {
        if(generatedMapEntryClasses == null){
            generatedMapEntryClasses = new HashMap<MapEntryGeneratedKey, Class>();
        }
        return generatedMapEntryClasses;
    }

    private class MapEntryGeneratedKey {
      	@SuppressWarnings("unused")
        String keyClassName;
		@SuppressWarnings("unused")
        String valueClassName;

    	public MapEntryGeneratedKey(String keyClass, String valueClass){
    		keyClassName = keyClass;
    		valueClassName = valueClass;
    	}
	}

    /**
     * Convenience method which returns an AbstractNullPolicy built from an XmlAbstractNullPolicy.
     *
     * @param property
     * @param nsr if 'NullRepresentedByXsiNil' is true, this is the resolver
     *            that we will add the schema instance prefix/uri pair to
     * @return
     * @see org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy
     * @see org.eclipse.persistence.jaxb.xmlmodel.XmlAbstractNullPolicy
     */
    private AbstractNullPolicy getNullPolicyFromProperty(Property property, NamespaceResolver nsr) {
        AbstractNullPolicy absNullPolicy = null;
        XmlAbstractNullPolicy xmlAbsNullPolicy = property.getNullPolicy();

        // policy is assumed to be one of XmlNullPolicy or XmlIsSetNullPolicy
        if (xmlAbsNullPolicy instanceof XmlNullPolicy) {
            XmlNullPolicy xmlNullPolicy = (XmlNullPolicy) xmlAbsNullPolicy;
            NullPolicy nullPolicy = new NullPolicy();
            nullPolicy.setSetPerformedForAbsentNode(xmlNullPolicy.isIsSetPerformedForAbsentNode());
            absNullPolicy = nullPolicy;
        } else {
            XmlIsSetNullPolicy xmlIsSetNullPolicy = (XmlIsSetNullPolicy) xmlAbsNullPolicy;
            IsSetNullPolicy isSetNullPolicy = new IsSetNullPolicy();
            isSetNullPolicy.setIsSetMethodName(xmlIsSetNullPolicy.getIsSetMethodName());
            // handle isSetParams
            ArrayList<Object> parameters = new ArrayList<Object>();
            ArrayList<Class> parameterTypes = new ArrayList<Class>();
            List<XmlIsSetNullPolicy.IsSetParameter> params = xmlIsSetNullPolicy.getIsSetParameter();
            for (XmlIsSetNullPolicy.IsSetParameter param : params) {
                String valueStr = param.getValue();
                String typeStr = param.getType();
                // create a conversion manager instance with the helper's loader
                XMLConversionManager mgr = new XMLConversionManager();
                mgr.setLoader(helper.getClassLoader());
                // handle parameter type
                Class typeClass = mgr.convertClassNameToClass(typeStr);
                // handle parameter value
                Object parameterValue = mgr.convertObject(valueStr, typeClass);
                parameters.add(parameterValue);
                parameterTypes.add(typeClass);
            }
            isSetNullPolicy.setIsSetParameters(parameters.toArray());
            isSetNullPolicy.setIsSetParameterTypes(parameterTypes.toArray(new Class[parameterTypes.size()]));
            absNullPolicy = isSetNullPolicy;
        }
        // handle commmon settings
        absNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.valueOf(xmlAbsNullPolicy.getNullRepresentationForXml().name()));
        absNullPolicy.setNullRepresentedByEmptyNode(xmlAbsNullPolicy.isEmptyNodeRepresentsNull());
        boolean xsiRepresentsNull = xmlAbsNullPolicy.isXsiNilRepresentsNull();
        if (xsiRepresentsNull) {
            absNullPolicy.setNullRepresentedByXsiNil(true);
        }

        return absNullPolicy;
    }

    /**
     * Return the base component type for a class.  For example, the base 
     * component type for Integer, Integer[], and Integer[][] are all Integer.
     */
    private JavaClass getBaseComponentType(JavaClass javaClass) {
        JavaClass componentType = javaClass.getComponentType();
        if(null == componentType) {
            return javaClass;
        }
        if(!componentType.isArray()) {
            return componentType;
        }
        return getBaseComponentType(componentType);
    }

    public JaxbClassLoader getJaxbClassLoader() {
        if (helper.getClassLoader() instanceof DynamicClassLoader) {
            return (JaxbClassLoader) helper.getClassLoader().getParent();
        } else {
            return (JaxbClassLoader) helper.getClassLoader();
        }
    }
    
    private boolean isBinaryData(JavaClass type){
    	return areEquals(type, ClassConstants.APBYTE) ||areEquals(type, "javax.activation.DataHandler") || areEquals(type, "java.awt.Image") || areEquals(type, "javax.xml.transform.Source") || areEquals(type, "javax.mail.internet.MimeMultipart");
    }

    /**
     * <p>An InstantiationPolicy that does not construct any objects (and therefore
     * will not throw validation errors caused by a lack of a no-arg constructor).</p>
     *
     * <p>This is used by @XmlLocation, where we want to have a real mapping created
     * (so we can later set its value through the mapping), but where we will never
     * instantiate a Locator from XML (the Locator will be built internally during parsing).</p>
     *
     * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy
     * @see org.xml.sax.Locator
     */
    private class NullInstantiationPolicy extends InstantiationPolicy {

        /**
         * Returns a new instance of this InstantiationPolicy's Descriptor's class.
         *
         * In this case, do nothing and return null.
         */
        @Override
        public Object buildNewInstance() throws DescriptorException {
            return null;
        }

    }

}