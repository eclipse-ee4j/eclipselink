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

import java.awt.Image;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.jaxb.AccessorFactoryWrapper;
import org.eclipse.persistence.internal.jaxb.CustomAccessorAttributeAccessor;
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
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.AnyAttributeMapping;
import org.eclipse.persistence.internal.oxm.mappings.AnyCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.AnyObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.TransformationMapping;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.XMLContainerMapping;
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
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNamedObjectGraph;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNamedSubgraph;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
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
import org.eclipse.persistence.oxm.mappings.XMLVariableXPathCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLVariableXPathObjectMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLListConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.queries.AttributeGroup;
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
    private JavaClass jotLinkedList;
    private JavaClass jotTreeSet;
    private HashMap<String, PackageInfo> packageToPackageInfoMappings;
    private HashMap<String, TypeInfo> typeInfo;
    private HashMap<QName, Class> qNamesToGeneratedClasses;
    private HashMap<String, Class> classToGeneratedClasses;
    private HashMap<QName, Class> qNamesToDeclaredClasses;
    private HashMap<QName, ElementDeclaration> globalElements;
    private List<ElementDeclaration> localElements;
    private Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClasses;
    private Map<MapEntryGeneratedKey, Class> generatedMapEntryClasses;
    private CoreProject project;
    private NamespaceResolver globalNamespaceResolver;
    private boolean isDefaultNamespaceAllowed;
    private Map<TypeMappingInfo, Class>typeMappingInfoToAdapterClasses;

    public MappingsGenerator(Helper helper) {
        this.helper = helper;
        jotArrayList = helper.getJavaClass(ArrayList.class);
        jotHashSet = helper.getJavaClass(HashSet.class);
        jotHashMap = helper.getJavaClass(HashMap.class);
        jotLinkedList = helper.getJavaClass(LinkedList.class);
        jotTreeSet = helper.getJavaClass(TreeSet.class);
        qNamesToGeneratedClasses = new HashMap<QName, Class>();
        qNamesToDeclaredClasses = new HashMap<QName, Class>();
        classToGeneratedClasses = new HashMap<String, Class>();
        globalNamespaceResolver = new org.eclipse.persistence.oxm.NamespaceResolver();
        isDefaultNamespaceAllowed = true;
    }

    public CoreProject generateProject(ArrayList<JavaClass> typeInfoClasses, HashMap<String, TypeInfo> typeInfo, HashMap<String, QName> userDefinedSchemaTypes, HashMap<String, PackageInfo> packageToPackageInfoMappings, HashMap<QName, ElementDeclaration> globalElements, List<ElementDeclaration> localElements, Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClass, Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses,  boolean isDefaultNamespaceAllowed) throws Exception {
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
        
        // Setup AttributeGroups
        for(JavaClass next : typeInfoClasses) {
            setupAttributeGroups(next);
        }
        
        // apply customizers if necessary
        Set<Entry<String, TypeInfo>> entrySet = this.typeInfo.entrySet();
        for (Entry<String, TypeInfo> entry : entrySet) {
            TypeInfo tInfo = entry.getValue();
            if (tInfo.getXmlCustomizer() != null) {
                String customizerClassName = tInfo.getXmlCustomizer();
                try {
                    Class customizerClass = PrivilegedAccessHelper.getClassForName(customizerClassName, true, helper.getClassLoader());
                    DescriptorCustomizer descriptorCustomizer = (DescriptorCustomizer) PrivilegedAccessHelper.newInstanceFromClass(customizerClass);
                    descriptorCustomizer.customize((XMLDescriptor)tInfo.getDescriptor());
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

    private void setupAttributeGroups(JavaClass javaClass) {
        TypeInfo info = this.typeInfo.get(javaClass.getQualifiedName());
        XMLDescriptor descriptor = (XMLDescriptor)info.getDescriptor();
        
        if(!info.getObjectGraphs().isEmpty()) {
            for(XmlNamedObjectGraph next:info.getObjectGraphs()) {
                AttributeGroup group = descriptor.getAttributeGroup(next.getName());
                Map<String, List<CoreAttributeGroup>> subgraphs = processSubgraphs(next.getXmlNamedSubgraph());
                for(XmlNamedAttributeNode nextAttributeNode:next.getXmlNamedAttributeNode()) {
                    if(nextAttributeNode.getSubgraph() == null || nextAttributeNode.getSubgraph().length() == 0) {
                        group.addAttribute(nextAttributeNode.getName());
                    } else {
                        List<CoreAttributeGroup> nestedGroups = subgraphs.get(nextAttributeNode.getSubgraph());
                        if(nestedGroups == null || nestedGroups.isEmpty()) {
                            Property property = info.getProperties().get(nextAttributeNode.getName());
                            if(property == null) {
                                //if there's no property associated with the attributeNode, ignore
                                continue;
                            }
                            JavaClass cls = property.getActualType();
                            TypeInfo referenceType = typeInfo.get(cls.getQualifiedName());
                            if(referenceType != null) {
                                AttributeGroup targetGroup = (AttributeGroup)referenceType.getDescriptor().getAttributeGroup(nextAttributeNode.getSubgraph());
                                group.addAttribute(nextAttributeNode.getName(), targetGroup);
                            } else {
                                //TODO: Exception
                            }
                        } else {
                            if(nestedGroups.size() == 1) {
                                group.addAttribute(nextAttributeNode.getName(), nestedGroups.get(0));
                            } else {
                                group.addAttribute(nextAttributeNode.getName(), nestedGroups);
                            }
                        }
                    }
                }


                for(XmlNamedSubgraph nextSubclass:next.getXmlNamedSubclassGraph()) {
                    AttributeGroup subclassGroup = new AttributeGroup(next.getName(), nextSubclass.getType(), true);
                    group.getSubClassGroups().put(nextSubclass.getType(), subclassGroup);
                    for(XmlNamedAttributeNode nextAttributeNode:nextSubclass.getXmlNamedAttributeNode()) {
                        if(nextAttributeNode.getSubgraph() == null || nextAttributeNode.getSubgraph().length() == 0) {
                            subclassGroup.addAttribute(nextAttributeNode.getName());
                        } else {
                            List<CoreAttributeGroup> nestedGroups = subgraphs.get(nextAttributeNode.getSubgraph());
                            if(nestedGroups == null || nestedGroups.isEmpty()) {
                                Property property = info.getProperties().get(nextAttributeNode.getName());
                                JavaClass cls = property.getActualType();
                                TypeInfo referenceType = typeInfo.get(cls.getQualifiedName());
                                if(referenceType != null) {
                                    AttributeGroup targetGroup = (AttributeGroup)referenceType.getDescriptor().getAttributeGroup(nextAttributeNode.getSubgraph());
                                    subclassGroup.addAttribute(nextAttributeNode.getName(), targetGroup);
                                } else {
                                    //TODO: Exception
                                }
                            } else {
                                if(nestedGroups.size() == 1) {
                                    subclassGroup.addAttribute(nextAttributeNode.getName(), nestedGroups.get(0));
                                } else {
                                    subclassGroup.addAttribute(nextAttributeNode.getName(), nestedGroups);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Map<String, List<CoreAttributeGroup>> processSubgraphs(List<XmlNamedSubgraph> subgraphs) {
        Map<String, List<CoreAttributeGroup>> subgroups = new HashMap<String, List<CoreAttributeGroup>>();
        //Iterate through once and create all the AttributeGroups
        for(XmlNamedSubgraph next: subgraphs) {
            String type = next.getType();
            if(type == null) {
                type = "java.lang.Object";
            }
            AttributeGroup group = new AttributeGroup(next.getName(), type, false);
            if(subgroups.containsKey(group.getName())) {
                List<CoreAttributeGroup> groups = subgroups.get(group.getName());
                groups.add(group);
            } else {
                List<CoreAttributeGroup> groups = new ArrayList<CoreAttributeGroup>(1);
                groups.add(group);
                subgroups.put(group.getName(), groups);
            }
        }
        
        //Iterate through a second time to populate the groups and set up links.
        for(XmlNamedSubgraph next:subgraphs) {
            List<XmlNamedAttributeNode> attributeNodes = next.getXmlNamedAttributeNode();
            List<CoreAttributeGroup> attributeGroups = subgroups.get(next.getName());
            if(attributeGroups != null) {
                for(CoreAttributeGroup group:attributeGroups) {
                    String typeName = next.getType();
                    if(typeName == null) {
                        typeName = "java.lang.Object";
                    }
                    if(group.getTypeName().equals(typeName)) {
                        for(XmlNamedAttributeNode attributeNode:attributeNodes) {
                            if(attributeNode.getSubgraph() == null || attributeNode.getSubgraph().length() == 0) {
                                group.addAttribute(attributeNode.getName());
                            } else {
                                List<CoreAttributeGroup> nestedGroups = subgroups.get(attributeNode.getSubgraph());
                                if(nestedGroups == null || nestedGroups.size() == 0) {
                                    //TODO: Exception or check for root level ones on target class
                                } else {
                                    group.addAttribute(attributeNode.getName(), nestedGroups.get(0));
                                }

                            }
                        }
                    }
                }
            }
        }
        return subgroups;
    }

    public void generateDescriptor(JavaClass javaClass, CoreProject project) {
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

        Descriptor descriptor = new XMLDescriptor();
        org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement rootElem = info.getXmlRootElement();
        if (rootElem == null) {
        	 try{                
        		 elementName = info.getXmlNameTransformer().transformRootElementName(javaClass.getName());
             }catch (Exception ex){
              	throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
             }  
            
            namespace = packageNamespace;
        } else {
            elementName = rootElem.getName();
            if (elementName.equals(XMLProcessor.DEFAULT)) {
                try{                
                    elementName = info.getXmlNameTransformer().transformRootElementName(javaClass.getName());
                }catch (Exception ex){
                 	throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
                }  
            }
            namespace = rootElem.getNamespace();
        }

        descriptor.setJavaClassName(jClassName);

        if (info.getFactoryMethodName() != null) {
            descriptor.getInstantiationPolicy().useFactoryInstantiationPolicy(info.getObjectFactoryClassName(), info.getFactoryMethodName());
        }

        if (namespace.equals(XMLProcessor.DEFAULT)) {
            namespace = namespaceInfo.getNamespace();
        }

        JavaClass manyValueJavaClass = helper.getJavaClass(ManyValue.class);
        if (!manyValueJavaClass.isAssignableFrom(javaClass)){
            if(namespace.length() != 0) {
                if(isDefaultNamespaceAllowed && globalNamespaceResolver.getDefaultNamespaceURI() == null && namespace.length() != 0) {
                    if (!namespaceInfo.getNamespaceResolverForDescriptor().getPrefixesToNamespaces().containsValue(namespace)) {
                        globalNamespaceResolver.setDefaultNamespaceURI(namespace);
                        namespaceInfo.getNamespaceResolverForDescriptor().setDefaultNamespaceURI(namespace);
                    }
                }
            }
            if (rootElem == null) {
                descriptor.setDefaultRootElement("");
            } else {
                if (namespace.length() == 0) {
                    descriptor.setDefaultRootElement(elementName);
                } else {
                    descriptor.setDefaultRootElement(getQualifiedString(getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor()), elementName));
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
                if (p.getType().getName().equals(Constants.LOCATOR_CLASS_NAME)) {
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
        
        if(!info.getObjectGraphs().isEmpty()) {
            //create attribute groups for each object graph. 
            //these will be populated later to allow for linking
            for(XmlNamedObjectGraph next:info.getObjectGraphs()) {
                AttributeGroup attributeGroup = new AttributeGroup(next.getName(), info.getJavaClassName(), false);
                ((XMLDescriptor)descriptor).addAttributeGroup(attributeGroup);
                
                //process subclass graphs for inheritance
                //for(NamedSubgraph nextSubclass:next.getNamedSubclassGraph()) {
                    //attributeGroup.insertSubClass(new AttributeGroup(next.getName(), nextSubclass.getType()));
                //}
            }
        }

        project.addDescriptor((CoreDescriptor)descriptor);
        info.setDescriptor(descriptor);
    }

    public void generateDescriptorForJAXBElementSubclass(JavaClass javaClass, CoreProject project, NamespaceResolver nsr) {
        String jClassName = javaClass.getQualifiedName();
        TypeInfo info = typeInfo.get(jClassName);

        Descriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClassName(jClassName);

        String[] factoryMethodParamTypes = info.getFactoryMethodParamTypes();

        MultiArgInstantiationPolicy policy = new MultiArgInstantiationPolicy();
        policy.useFactoryInstantiationPolicy(info.getObjectFactoryClassName(), info.getFactoryMethodName());
        policy.setParameterTypeNames(factoryMethodParamTypes);
        policy.setDefaultValues(new String[]{null});

        xmlDescriptor.setInstantiationPolicy(policy);
        JavaClass paramClass = helper.getJavaClass(factoryMethodParamTypes[0]);
        boolean isObject = paramClass.getName().equals("java.lang.Object");        
        if(helper.isBuiltInJavaType(paramClass) && !isObject ){
            if(isBinaryData(paramClass)){
                BinaryDataMapping mapping = new XMLBinaryDataMapping();
                mapping.setAttributeName("value");
                mapping.setXPath(".");
                ((Field)mapping.getField()).setSchemaType(Constants.BASE_64_BINARY_QNAME);
                mapping.setSetMethodName("setValue");
                mapping.setGetMethodName("getValue");

                Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(factoryMethodParamTypes[0], helper.getClassLoader());
                mapping.setAttributeClassification(attributeClassification);
                mapping.getNullPolicy().setNullRepresentedByEmptyNode(false);

                mapping.setShouldInlineBinaryData(false);
                if(mapping.getMimeType() == null) {
                    if(areEquals(paramClass, javax.xml.transform.Source.class)) {
                        mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/xml"));
                    } else {
                        mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/octet-stream"));
                    }                       
                }
                xmlDescriptor.addMapping((CoreMapping)mapping); 
            } else {
                DirectMapping mapping = new XMLDirectMapping();
                mapping.setNullValueMarshalled(true);
                mapping.setAttributeName("value");
                mapping.setGetMethodName("getValue");
                mapping.setSetMethodName("setValue");
                mapping.setXPath("text()");
                Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(factoryMethodParamTypes[0], helper.getClassLoader());
                mapping.setAttributeClassification(attributeClassification);
                xmlDescriptor.addMapping((CoreMapping)mapping);
            }
        }else if(paramClass.isEnum()){
             EnumTypeInfo enumInfo = (EnumTypeInfo)typeInfo.get(paramClass.getQualifiedName());
        	
             DirectMapping mapping = new XMLDirectMapping();
             mapping.setConverter(buildJAXBEnumTypeConverter(mapping, enumInfo));
             mapping.setNullValueMarshalled(true);
             mapping.setAttributeName("value");
             mapping.setGetMethodName("getValue");
             mapping.setSetMethodName("setValue");
             mapping.setXPath("text()");
             Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(factoryMethodParamTypes[0], helper.getClassLoader());
             mapping.setAttributeClassification(attributeClassification);
             xmlDescriptor.addMapping((CoreMapping)mapping);
             
        }else{
        
            CompositeObjectMapping mapping = new XMLCompositeObjectMapping();
            mapping.setAttributeName("value");
            mapping.setGetMethodName("getValue");
            mapping.setSetMethodName("setValue");
            mapping.setXPath(".");
            if(isObject){
            	mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);                
            }else{
            	mapping.setReferenceClassName(factoryMethodParamTypes[0]);
            }
            xmlDescriptor.addMapping((CoreMapping)mapping);
        }
        xmlDescriptor.setNamespaceResolver(nsr);
        setSchemaContext(xmlDescriptor, info);
        project.addDescriptor((CoreDescriptor)xmlDescriptor);
        info.setDescriptor(xmlDescriptor);
    }
    
    private void setSchemaContext(Descriptor desc, TypeInfo info) {
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
            	String generatedPrefix =getPrefixForNamespace(info.getClassNamespace(), desc.getNonNullNamespaceResolver(), false);
            	schemaRef.setSchemaContext("/" + getQualifiedString(generatedPrefix, info.getSchemaTypeName()));            	
            	if(generatedPrefix == null || generatedPrefix.equals(Constants.EMPTY_STRING)){
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
    public Mapping generateMapping(Property property, Descriptor descriptor, JavaClass descriptorJavaClass, NamespaceInfo namespaceInfo) {
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

            Mapping mapping;
            boolean isArray = property.getType().isArray() && !property.getType().getRawName().equals("byte[]");
            
            // if the value type is something we have a descriptor for, create
            // a composite mapping
            if(property.isChoice()) {
                if(helper.isCollectionType(property.getType()) || property.getType().isArray()) {
                    mapping = generateChoiceCollectionMapping(property, descriptor, namespaceInfo);
                    ((ChoiceCollectionMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    mapping = generateChoiceMapping(property, descriptor, namespaceInfo);
                    ((ChoiceObjectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                }
            } else if (typeInfo.containsKey(valueType.getQualifiedName())) {
                TypeInfo reference = typeInfo.get(valueType.getQualifiedName());
                if (helper.isCollectionType(property.getType())) {
                    if (reference.isEnumerationType()) {
                        mapping = generateEnumCollectionMapping(property, descriptor, namespaceInfo, (EnumTypeInfo) reference);
                        XMLJavaTypeConverter converter = new XMLJavaTypeConverter(adapterClass.getQualifiedName());
                        converter.setNestedConverter(((DirectCollectionMapping)mapping).getValueConverter());
                        ((DirectCollectionMapping)mapping).setValueConverter(converter);
                    } else {   
                    	if(property.getVariableAttributeName() !=null){
                    		mapping = generateVariableXPathCollectionMapping(property, descriptor, namespaceInfo, valueType);
                            ((VariableXPathCollectionMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    	}else{
                            mapping = generateCompositeCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo, valueType.getQualifiedName());
                            ((CompositeCollectionMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    	}
                    }
                } else {
                    if (reference.isEnumerationType()) {
                        mapping = generateDirectEnumerationMapping(property, descriptor, namespaceInfo, (EnumTypeInfo) reference);
                        XMLJavaTypeConverter converter = new XMLJavaTypeConverter(adapterClass.getQualifiedName());
                        converter.setNestedConverter(((DirectMapping)mapping).getConverter());
                        ((DirectMapping)mapping).setConverter(converter);
                    } else if (property.isInverseReference()) {
                        mapping = generateInverseReferenceMapping(property, descriptor, namespaceInfo);
                    } else {                    
                    	if(property.getVariableAttributeName() !=null){
                    		mapping = generateVariableXPathObjectMapping(property, descriptor, namespaceInfo, valueType);
                            ((VariableXPathObjectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    	}else{
                            mapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, valueType.getQualifiedName());
                            ((CompositeObjectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    	}
                    }
                }
            } else {
                // no descriptor for value type
                if (property.isAny()) {
                    if (helper.isCollectionType(property.getType())){
                        mapping = generateAnyCollectionMapping(property, descriptor, namespaceInfo, property.isMixedContent());
                        ((AnyCollectionMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    } else {
                        mapping = generateAnyObjectMapping(property, descriptor, namespaceInfo);
                        ((AnyObjectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    }
                } else if (helper.isCollectionType(property.getType()) || isArray) {
                    if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                    	mapping = generateBinaryDataCollectionMapping(property, descriptor, namespaceInfo);
                    	((BinaryDataCollectionMapping) mapping).setValueConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    } else{
                	    mapping = generateDirectCollectionMapping(property, descriptor, namespaceInfo);
                        if(adapterClass.getQualifiedName().equals(CollapsedStringAdapter.class.getName())) {
                            ((DirectCollectionMapping)mapping).setCollapsingStringValues(true);
                        } else if(adapterClass.getQualifiedName().equals(NormalizedStringAdapter.class.getName())) {
                            ((DirectCollectionMapping)mapping).setNormalizingStringValues(true);
                        } else {
                            ((DirectCollectionMapping) mapping).setValueConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                        }
                	}
                } else if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
                    mapping = generateBinaryMapping(property, descriptor, namespaceInfo);
                    ((BinaryDataMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                } else {
                    if (!property.isAttribute() && areEquals(valueType, Object.class) || property.isTyped()){
                        mapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, null);
                        ((CompositeObjectMapping)mapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                        ((CompositeObjectMapping)mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                        return mapping;
                    }
                    mapping = generateDirectMapping(property, descriptor, namespaceInfo);
                    if(adapterClass.getQualifiedName().equals(CollapsedStringAdapter.class.getName())) {
                        ((DirectMapping)mapping).setCollapsingStringValues(true);
                    } else if(adapterClass.getQualifiedName().equals(NormalizedStringAdapter.class.getName())) {
                        ((DirectMapping)mapping).setNormalizingStringValues(true);
                    } else {
                        ((DirectMapping) mapping).setConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
                    }
                }
            }
            return mapping;
        }
        if (property.getVariableAttributeName() != null){
        	if (helper.isCollectionType(property.getType()) || property.getType().isArray() || property.isMap()){
                return generateVariableXPathCollectionMapping(property, descriptor, namespaceInfo, property.getActualType());
        	}else{
        		return generateVariableXPathObjectMapping(property, descriptor, namespaceInfo, property.getActualType());
        	}
        }
        if (property.isSetXmlJoinNodes()) {
            if (helper.isCollectionType(property.getType())) {
                return generateXMLCollectionReferenceMapping(property, descriptor, namespaceInfo, property.getActualType());
            }
            return generateXMLObjectReferenceMapping(property, descriptor, namespaceInfo, property.getType());
        }
        if (property.isXmlTransformation()) {
            return generateTransformationMapping(property, descriptor, namespaceInfo);
        }
        if (property.isChoice()) {
            if (helper.isCollectionType(property.getType()) || property.getType().isArray()) {
                return generateChoiceCollectionMapping(property, descriptor, namespaceInfo);
            } 
            return generateChoiceMapping(property, descriptor, namespaceInfo);
        }
        if (property.isInverseReference()) {
            return generateInverseReferenceMapping(property, descriptor, namespaceInfo);
        } 
        if (property.isReference()) {
            return generateMappingForReferenceProperty(property, descriptor, namespaceInfo);
        }
        if (property.isAny()) {
            if (helper.isCollectionType(property.getType()) || property.getType().isArray()){
                return generateAnyCollectionMapping(property, descriptor, namespaceInfo, property.isMixedContent());
            }
            return generateAnyObjectMapping(property, descriptor, namespaceInfo);
        }

        if (property.isMap()){
        	if (property.isAnyAttribute()) {
        		return generateAnyAttributeMapping(property, descriptor, namespaceInfo);
        	}
        	return generateCompositeCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo, null);
        }
        if (helper.isCollectionType(property.getType())) {
            return generateCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo);
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
                CompositeCollectionMapping mapping = generateCompositeCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo, null);
                mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                return mapping;
            }
            
            if (reference != null ||  componentType.isArray()){
            	  if (property.isXmlIdRef() || property.isSetXmlJoinNodes()) {
                      return generateXMLCollectionReferenceMapping(property, descriptor, namespaceInfo, componentType);
                  }
            	
                return generateCompositeCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo, componentType.getQualifiedName());
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
                CompositeObjectMapping locationMapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, referenceClass.getQualifiedName());
                reference.getDescriptor().setInstantiationPolicy(new NullInstantiationPolicy());
                descriptor.setLocationAccessor((CoreAttributeAccessor)locationMapping.getAttributeAccessor());
                return locationMapping;
            } else {
                return generateCompositeObjectMapping(property, descriptor, namespaceInfo, referenceClass.getQualifiedName());
            }
        }
        if (property.isSwaAttachmentRef() || property.isMtomAttachment()) {
            return generateBinaryMapping(property, descriptor, namespaceInfo);
        }
        if (referenceClass.getQualifiedName().equals(OBJECT_CLASS_NAME) && !property.isAttribute() || property.isTyped() ) {
            CompositeObjectMapping coMapping = generateCompositeObjectMapping(property, descriptor, namespaceInfo, null);
            coMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
            return coMapping;
        }
        if (property.isXmlLocation()) {
            return null;
        }
        return generateDirectMapping(property, descriptor, namespaceInfo);
    }

    private Mapping generateVariableXPathCollectionMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, JavaClass actualType) {    	
        XMLVariableXPathCollectionMapping mapping = new XMLVariableXPathCollectionMapping();        
        mapping.setAttributeName(property.getPropertyName());
        
        if(property.isMap()){
        	actualType = property.getValueType();
        }
        
        initializeXMLContainerMapping(mapping, property.getType().isArray());
        initializeXMLMapping(mapping, property);
        initializeVariableXPathMapping(mapping, property, actualType);
        
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        } else {
            if (property.isSetXmlElementWrapper()) {
                mapping.setField((XMLField)getXPathForField(property, namespaceInfo, false, true));
            }
        }
                
        if (property.isSetXmlElementWrapper()) {
            mapping.setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
        }
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
        }
        

        
        if(property.isMap()){
        	JavaClass mapType = property.getType(); 
        	if(mapType.isInterface()){        		
        		mapping.useMapClass("java.util.HashMap");
        	}else{
        	    mapping.useMapClass(property.getType().getName());
        	}
        	
        }else{
        	collectionType = containerClassImpl(collectionType);
            mapping.useCollectionClass(helper.getClassForJavaClass(collectionType));
        }
       
        
        return mapping;
    }

    private Mapping generateVariableXPathObjectMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, JavaClass actualType) {    	
        XMLVariableXPathObjectMapping mapping = new XMLVariableXPathObjectMapping();        
        initializeXMLMapping(mapping, property);
        initializeVariableXPathMapping(mapping, property, actualType);

        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else {
            NullPolicy nullPolicy = (NullPolicy) mapping.getNullPolicy();
            nullPolicy.setSetPerformedForAbsentNode(false);
            if(property.isNillable()) {
                nullPolicy.setNullRepresentedByXsiNil(true);
                nullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
            }
        }
    
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        } else {
            if (property.isSetXmlElementWrapper()) {
                mapping.setField((XMLField)getXPathForField(property, namespaceInfo, false, true));
            }
        }
                
        return mapping;
    }
    
    private void initializeVariableXPathMapping(VariableXPathObjectMapping mapping, Property property, JavaClass actualType){
        String variableAttributeName = property.getVariableAttributeName();
        
        TypeInfo refInfo = typeInfo.get(actualType.getName());
        
        if(refInfo == null){
        	throw JAXBException.unknownTypeForVariableNode(actualType.getName());
        }
        
        Property refProperty = refInfo.getProperties().get(variableAttributeName);
                
        
        while(refProperty == null){
         	JavaClass superClass = CompilerHelper.getNextMappedSuperClass(actualType, typeInfo, helper);
	        if (superClass != null){
	        	refInfo = typeInfo.get(superClass.getName());
	            refProperty = refInfo.getProperties().get(variableAttributeName);	           
	        }else{
	        	break;
	        }
        }
        
        
        if(refProperty == null){
        	throw JAXBException.unknownPropertyForVariableNode(variableAttributeName, actualType.getName());
        }
       
        String refPropertyType = refProperty.getActualType().getQualifiedName();
        
        if(!(refPropertyType.equals("java.lang.String") || refPropertyType.equals("javax.xml.namespace.QName"))){
        	throw JAXBException.invalidTypeForVariableNode(variableAttributeName, refPropertyType, actualType.getName());
        }
        if (refProperty.isMethodProperty()) {
            if (refProperty.getGetMethodName() == null) {
                // handle case of set with no get method
                String paramTypeAsString = refProperty.getType().getName();
                JAXBSetMethodAttributeAccessor accessor = new JAXBSetMethodAttributeAccessor(paramTypeAsString, helper.getClassLoader());
                accessor.setIsReadOnly(true);
                accessor.setSetMethodName(refProperty.getSetMethodName());
                mapping.setIsReadOnly(true);
                accessor.setAttributeName("thingBLAH");
                mapping.setVariableAttributeAccessor(accessor);
            } else if (refProperty.getSetMethodName() == null) {
            	mapping.setVariableGetMethodName(refProperty.getGetMethodName());
            } else {
            	mapping.setVariableGetMethodName(refProperty.getGetMethodName());
            	mapping.setVariableSetMethodName(refProperty.getSetMethodName());
            }
        }else{
        	mapping.setVariableAttributeName(property.getVariableAttributeName());
        }
        
    
        if(property.getVariableClassName() != null){
        	mapping.setReferenceClassName(property.getVariableClassName());		
        }else{
            mapping.setReferenceClassName(actualType.getQualifiedName());
        } 
        	
        mapping.setAttribute(property.isVariableNodeAttribute());
        
    }
    
    private InverseReferenceMapping generateInverseReferenceMapping(Property property, Descriptor descriptor, NamespaceInfo namespace) {
        InverseReferenceMapping invMapping = new XMLInverseReferenceMapping();
        boolean isCollection = helper.isCollectionType(property.getType());
        
        if (isCollection) {
            invMapping.setReferenceClassName(property.getGenericType().getQualifiedName());
        } else {
            invMapping.setReferenceClassName(property.getType().getQualifiedName());
        }

        invMapping.setAttributeName(property.getPropertyName());

        String setMethodName = property.getInverseReferencePropertySetMethodName();
        String getMethodName = property.getInverseReferencePropertyGetMethodName();

        if (setMethodName != null && !setMethodName.equals(Constants.EMPTY_STRING)) {
            invMapping.setSetMethodName(setMethodName);
        }
        if (getMethodName != null && !getMethodName.equals(Constants.EMPTY_STRING)) {
            invMapping.setGetMethodName(getMethodName);
        }
        invMapping.setMappedBy(property.getInverseReferencePropertyName());

        if (isCollection) {
            JavaClass collectionType = property.getType();
            collectionType = containerClassImpl(collectionType);
            invMapping.useCollectionClass(org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(collectionType.getQualifiedName(), helper.getClassLoader()));
        }
        
        if(property.isWriteableInverseReference()){
   	        if(isCollection){           	
   	            JavaClass descriptorClass = helper.getJavaClass(descriptor.getJavaClassName());
   	        	invMapping.setInlineMapping((XMLCompositeCollectionMapping)generateCompositeCollectionMapping(property, descriptor, descriptorClass, namespace, invMapping.getReferenceClassName()));
   	        }else{
   	        	invMapping.setInlineMapping((XMLCompositeObjectMapping)generateCompositeObjectMapping(property, descriptor, namespace, invMapping.getReferenceClassName()));   	        	
   	        }
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
    public TransformationMapping generateTransformationMapping(Property property, Descriptor descriptor, NamespaceInfo namespace) {
        TransformationMapping mapping = new XMLTransformationMapping();
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
    
    public ChoiceObjectMapping generateChoiceMapping(Property property, Descriptor descriptor, NamespaceInfo namespace) {
        ChoiceObjectMapping mapping = new XMLChoiceObjectMapping();
        initializeXMLMapping((XMLChoiceObjectMapping)mapping, property);
      
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
                List<Field> srcFlds = new ArrayList<Field>();
                List<Field> tgtFlds = new ArrayList<Field>();
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
                    tgtXPath = getXPathForField(prop, namespace, !prop.isAttribute(), false).getXPath();
                }
                // if the XPath is set (via xml-path) use it, otherwise figure it out
                Field srcXPath;
                if (next.getXmlPath() != null) {
                    srcXPath = new XMLField(next.getXmlPath());
                } else {
                    srcXPath = getXPathForField(next, namespace, true, false);
                }
                mapping.addChoiceElement(srcXPath.getXPath(), type.getQualifiedName(), tgtXPath);
            } else {
            	Field xpath;
                if (next.getXmlPath() != null) {
                    xpath = new XMLField(next.getXmlPath());
                } else {
                    xpath = getXPathForField(next, namespace, (!(this.typeInfo.containsKey(type.getQualifiedName()))) || next.isMtomAttachment() || type.isEnum(), false);
                }
                mapping.addChoiceElement(xpath, type.getQualifiedName());
                if(!originalType.getQualifiedName().equals(type.getQualifiedName())) {
                    if(mapping.getClassNameToFieldMappings().get(originalType.getQualifiedName()) == null) {
                        mapping.getClassNameToFieldMappings().put(originalType.getQualifiedName(), xpath);
                    }
                    mapping.addConverter(xpath, converter);
                }
                Mapping nestedMapping = (Mapping) mapping.getChoiceElementMappings().get(xpath);
                if(nestedMapping instanceof BinaryDataMapping){
                	((BinaryDataMapping)nestedMapping).getNullPolicy().setNullRepresentedByEmptyNode(false);
                }
                if (type.isEnum()) {
                    if(nestedMapping.isAbstractDirectMapping()) {
                        ((DirectMapping)nestedMapping).setConverter(buildJAXBEnumTypeConverter(nestedMapping, (EnumTypeInfo)info));
                    }
                }                
            }
        }
        return mapping;
    }

    public ChoiceCollectionMapping generateChoiceCollectionMapping(Property property, Descriptor descriptor, NamespaceInfo namespace) {
        ChoiceCollectionMapping mapping = new XMLChoiceCollectionMapping();
        initializeXMLContainerMapping(mapping, property.getType().isArray());
        initializeXMLMapping((XMLChoiceCollectionMapping)mapping, property);
       
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
        }
        
        
        collectionType = containerClassImpl(collectionType);
        mapping.useCollectionClassName(collectionType.getRawName());

        if (property.isSetXmlElementWrapper()) {
            mapping.setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
        }

        boolean isIdRef = property.isXmlIdRef();
        Iterator<Property> choiceProperties = property.getChoiceProperties().iterator();
        while (choiceProperties.hasNext()) {
            Property next = choiceProperties.next();
            JavaClass type = next.getType();
            JavaClass originalType = next.getType();
            Converter converter = null;
            Field xmlField = null;
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
                List<Field> srcFlds = new ArrayList<Field>();
                List<Field> tgtFlds = new ArrayList<Field>();
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
                    tgtXPath = getXPathForField(prop, namespace, !prop.isAttribute(), false).getXPath();
                }
                // if the XPath is set (via xml-path) use it, otherwise figure it out
                Field srcXPath;
                if (next.getXmlPath() != null) {
                    srcXPath = new XMLField(next.getXmlPath());
                } else {
                    srcXPath = getXPathForField(next, namespace, true, false);
                }
                mapping.addChoiceElement(srcXPath.getXPath(), type.getQualifiedName(), tgtXPath);
            } else {
            	Field xpath;
                if (next.getXmlPath() != null) {
                    xpath = new XMLField(next.getXmlPath());
                } else {
                    xpath = getXPathForField(next, namespace, (!(this.typeInfo.containsKey(type.getQualifiedName()))) || type.isEnum(), false);
                }
                xmlField = xpath;
                mapping.addChoiceElement(xpath.getName(), type.getQualifiedName());
                if(!originalType.getQualifiedName().equals(type.getQualifiedName())) {
                    if(mapping.getClassNameToFieldMappings().get(originalType.getQualifiedName()) == null) {
                        mapping.getClassNameToFieldMappings().put(originalType.getQualifiedName(), xpath);
                    }
                    mapping.addConverter(xpath, converter);
                }
                
            }
                        
            if(xmlField !=null){
                Mapping nestedMapping = (Mapping) mapping.getChoiceElementMappings().get(xmlField);
                if(nestedMapping.isAbstractCompositeCollectionMapping()){                   
                   // handle null policy set via xml metadata
                   if (property.isSetNullPolicy()) {
                	   ((CompositeCollectionMapping)nestedMapping).setNullPolicy(getNullPolicyFromProperty(property, namespace.getNamespaceResolverForDescriptor()));
                   } else if (next.isNillable() && property.isNillable()){
                	   ((CompositeCollectionMapping)nestedMapping).getNullPolicy().setNullRepresentedByXsiNil(true);
                	   ((CompositeCollectionMapping)nestedMapping).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
                   }
                } else if(nestedMapping.isAbstractCompositeDirectCollectionMapping()){   
                	 if (next.isSetNullPolicy()) {
                		 ((DirectCollectionMapping)nestedMapping).setNullPolicy(getNullPolicyFromProperty(next, namespace.getNamespaceResolverForDescriptor()));
                     } else if (next.isNillable() && property.isNillable()){
                    	 ((DirectCollectionMapping)nestedMapping).getNullPolicy().setNullRepresentedByXsiNil(true);
                    	 ((DirectCollectionMapping)nestedMapping).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
                     }
                	 ((DirectCollectionMapping)nestedMapping).getNullPolicy().setNullRepresentedByEmptyNode(false);
                	 
                     if (type.isEnum()) {
                         ((DirectCollectionMapping)nestedMapping).setValueConverter(buildJAXBEnumTypeConverter(nestedMapping, (EnumTypeInfo)info));
                     }
                } else if(nestedMapping instanceof BinaryDataCollectionMapping){   
               	    if (next.isSetNullPolicy()) {
            		    ((BinaryDataCollectionMapping)nestedMapping).setNullPolicy(getNullPolicyFromProperty(next, namespace.getNamespaceResolverForDescriptor()));
                    } else if (next.isNillable() && property.isNillable()){
                	    ((BinaryDataCollectionMapping)nestedMapping).getNullPolicy().setNullRepresentedByXsiNil(true);
                	    ((BinaryDataCollectionMapping)nestedMapping).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
                    }
                }
            }
        }
        return mapping;
    }

    public Mapping generateMappingForReferenceProperty(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo)  {
        boolean isCollection = helper.isCollectionType(property.getType()) || property.getType().isArray();

        Mapping mapping;
        if (isCollection) {
            mapping = new XMLChoiceCollectionMapping();
            initializeXMLContainerMapping((ChoiceCollectionMapping) mapping, property.getType().isArray());
            JavaClass collectionType = property.getType();
            collectionType = containerClassImpl(collectionType);
            ((ChoiceCollectionMapping) mapping).useCollectionClassName(collectionType.getRawName());
            JAXBElementRootConverter jaxbERConverter = new JAXBElementRootConverter(Object.class);
            if (property.isSetXmlJavaTypeAdapter()) {
                JavaClass adapterClass = helper.getJavaClass(property.getXmlJavaTypeAdapter().getValue());
                jaxbERConverter.setNestedConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
            }
            ((ChoiceCollectionMapping) mapping).setConverter(jaxbERConverter);
            if (property.isSetWriteOnly()) {
                ((ChoiceCollectionMapping) mapping).setIsWriteOnly(property.isWriteOnly());
            }
            if (property.isSetXmlElementWrapper()) {
                ((ChoiceCollectionMapping) mapping).setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
            }
        } else {
            mapping = new XMLChoiceObjectMapping();
            JAXBElementRootConverter jaxbERConverter = new JAXBElementRootConverter(Object.class);
            if (property.isSetXmlJavaTypeAdapter()) {
                JavaClass adapterClass = helper.getJavaClass(property.getXmlJavaTypeAdapter().getValue());
                jaxbERConverter.setNestedConverter(new XMLJavaTypeConverter(adapterClass.getQualifiedName()));
            }
            ((ChoiceObjectMapping) mapping).setConverter(jaxbERConverter);
            if (property.isSetWriteOnly()) {
                ((ChoiceObjectMapping) mapping).setIsWriteOnly(property.isWriteOnly());
            }
        }
      
        initializeXMLMapping((XMLMapping)mapping, property);

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
            if (namespace.equals(XMLProcessor.DEFAULT)) {
                if (namespaceInfo.isElementFormQualified()) {
                    namespace = namespaceInfo.getNamespace();
                } else {
                    namespace = "";
                }
            }
            if (namespace.equals("")) {
                wrapperXPath += (wrapper.getName() + "/");
            } else {
                String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolver());
                wrapperXPath += getQualifiedString(prefix, wrapper.getName() + "/");
            }
        }        
        if(property.isMixedContent() && isCollection) {
            if(wrapperXPath.length() == 0) {
                ((ChoiceCollectionMapping)mapping).setMixedContent(true);
            } else {
                ((ChoiceCollectionMapping)mapping).setMixedContent(wrapperXPath.substring(0, wrapperXPath.length() - 1));
            }
        }  
        for (ElementDeclaration element:referencedElements) {
            QName elementName = element.getElementName();
            JavaClass	pType = element.getJavaType();
            String	pTypeName = element.getJavaTypeName();
            boolean isBinaryType = (areEquals(pType, AnnotationsProcessor.JAVAX_ACTIVATION_DATAHANDLER) || areEquals(pType, byte[].class) || areEquals(pType, Image.class) || areEquals(pType, Source.class) || areEquals(pType, AnnotationsProcessor.JAVAX_MAIL_INTERNET_MIMEMULTIPART));        
            boolean isText = pType.isEnum() || (!isBinaryType && !(this.typeInfo.containsKey(element.getJavaTypeName())) && !(element.getJavaTypeName().equals(OBJECT_CLASS_NAME)));
            String xPath = wrapperXPath;

            Field xmlField = this.getXPathForElement(xPath, elementName, namespaceInfo, isText);
            //ensure byte[] goes to base64 instead of the default hex.
            if(helper.getXMLToJavaTypeMap().get(pType.getRawName()) == Constants.BASE_64_BINARY_QNAME) {
                xmlField.setSchemaType(Constants.BASE_64_BINARY_QNAME);
            }
            if(areEquals(pType, Object.class)) {
            	setTypedTextField(xmlField);
            }
            Mapping nestedMapping;
            AbstractNullPolicy nullPolicy = null;
            if(isCollection){
                ChoiceCollectionMapping xmlChoiceCollectionMapping = (ChoiceCollectionMapping) mapping;
                xmlChoiceCollectionMapping.addChoiceElement(xmlField, pTypeName);
                nestedMapping = (Mapping) xmlChoiceCollectionMapping.getChoiceElementMappings().get(xmlField);
                if(nestedMapping.isAbstractCompositeCollectionMapping()){
                    ((CompositeCollectionMapping)nestedMapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                    nullPolicy = ((CompositeCollectionMapping)nestedMapping).getNullPolicy();
                }

                if (nestedMapping.isAbstractCompositeDirectCollectionMapping()) {
                    DirectCollectionMapping nestedCompositeDirectCollectionMapping = (DirectCollectionMapping) nestedMapping;
                    nullPolicy = nestedCompositeDirectCollectionMapping.getNullPolicy();
                    if(pType.isEnum()) {
                        TypeInfo enumTypeInfo = typeInfo.get(pType.getQualifiedName());
                        nestedCompositeDirectCollectionMapping.setValueConverter(buildJAXBEnumTypeConverter(nestedCompositeDirectCollectionMapping, (EnumTypeInfo) enumTypeInfo));
                    }
                    if(element.isList()){
                        XMLListConverter listConverter = new XMLListConverter();
                        listConverter.setObjectClassName(pType.getQualifiedName());
                        ((DirectCollectionMapping)nestedMapping).setValueConverter(listConverter); 
                    }
                }else if(nestedMapping instanceof BinaryDataCollectionMapping){
                    nullPolicy =  ((BinaryDataCollectionMapping)nestedMapping).getNullPolicy();
                    if(element.isList()){
                    	((XMLField)((BinaryDataCollectionMapping)nestedMapping).getField()).setUsesSingleNode(true);
                    }
                }

              
            } else {
                ChoiceObjectMapping xmlChoiceObjectMapping = (ChoiceObjectMapping) mapping;
                xmlChoiceObjectMapping.addChoiceElement(xmlField, pTypeName);
                nestedMapping = (Mapping) xmlChoiceObjectMapping.getChoiceElementMappings().get(xmlField);
                if(pType.isEnum()) {
                    TypeInfo enumTypeInfo = typeInfo.get(pType.getQualifiedName());
                    ((DirectMapping)nestedMapping).setConverter(buildJAXBEnumTypeConverter(nestedMapping, (EnumTypeInfo) enumTypeInfo));
                }
                if(nestedMapping.isAbstractCompositeObjectMapping()){
                    ((CompositeObjectMapping)nestedMapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                    nullPolicy = ((CompositeObjectMapping)nestedMapping).getNullPolicy();

                }else if(nestedMapping instanceof BinaryDataMapping){
                    nullPolicy = ((BinaryDataMapping)nestedMapping).getNullPolicy();
                }else if(nestedMapping instanceof DirectMapping){
                    nullPolicy = ((DirectMapping)nestedMapping).getNullPolicy();
                }
                
            }
            if(nullPolicy != null){
                nullPolicy.setNullRepresentedByEmptyNode(false);
                nullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
                nullPolicy.setNullRepresentedByXsiNil(true);
                nullPolicy.setIgnoreAttributesForNil(false);
            }
            if (!element.isXmlRootElement()) {
                Class scopeClass = element.getScopeClass();
                if (scopeClass == javax.xml.bind.annotation.XmlElementDecl.GLOBAL.class){
                    scopeClass = JAXBElement.GlobalScope.class;
                }
                
                Class declaredType = null;
                if(element.getAdaptedJavaType() != null){
                	declaredType =  org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(element.getAdaptedJavaType().getQualifiedName(), helper.getClassLoader());
                }else{
                	declaredType =  org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(pType.getQualifiedName(), helper.getClassLoader());
                }
                
                JAXBElementConverter converter = new JAXBElementConverter(xmlField, declaredType, scopeClass);
                if (isCollection){
                    ChoiceCollectionMapping xmlChoiceCollectionMapping = (ChoiceCollectionMapping) mapping;
                    if(element.getJavaTypeAdapterClass() != null){                	                	
                    	converter.setNestedConverter(new XMLJavaTypeConverter(element.getJavaTypeAdapterClass().getName()));
                    }else{
                    	CoreConverter originalConverter = xmlChoiceCollectionMapping.getConverter(xmlField);
                        converter.setNestedConverter(originalConverter);	
                    }                    
                    xmlChoiceCollectionMapping.addConverter(xmlField, converter);
                } else {
                    ChoiceObjectMapping xmlChoiceObjectMapping = (ChoiceObjectMapping) mapping;
                    if(element.getJavaTypeAdapterClass() != null){                	                	
                    	converter.setNestedConverter(new XMLJavaTypeConverter(element.getJavaTypeAdapterClass().getName()));
                    }else{
                    	CoreConverter originalConverter = xmlChoiceObjectMapping.getConverter(xmlField);
                        converter.setNestedConverter(originalConverter);	
                    }  
                    xmlChoiceObjectMapping.addConverter(xmlField, converter);
                }
            }
        }
        if(property.isAny()){
        	if(isCollection){
                XMLChoiceCollectionMapping xmlChoiceCollectionMapping = (XMLChoiceCollectionMapping) mapping;
                xmlChoiceCollectionMapping.setIsAny(true);
        	}
        }
        
        return mapping;
    }

    private void setTypedTextField(Field field){
    	
    	field.setIsTypedTextField(true);
    	if(field.getSchemaType() == null){
    	    field.setSchemaType(Constants.ANY_TYPE_QNAME);
    	}
     	((XMLField)field).addXMLConversion(Constants.DATE_TIME_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
    	((XMLField)field).addXMLConversion(Constants.DATE_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
    	((XMLField)field).addXMLConversion(Constants.TIME_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
    	((XMLField)field).addJavaConversion(CoreClassConstants.APBYTE, Constants.BASE_64_BINARY_QNAME);
    	((XMLField)field).addJavaConversion(CoreClassConstants.ABYTE, Constants.BASE_64_BINARY_QNAME);
       	
    }
    
    public AnyCollectionMapping generateAnyCollectionMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, boolean isMixed) {
        AnyCollectionMapping  mapping = new XMLAnyCollectionMapping();
        initializeXMLContainerMapping(mapping, property.getType().isArray());
        initializeXMLMapping((XMLMapping)mapping, property);

        // if the XPath is set (via xml-path) use it
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        } else {
            if (property.isSetXmlElementWrapper()) {
                mapping.setField(getXPathForField(property, namespaceInfo, false, true));
            }
        }

        if (property.isSetXmlElementWrapper()) {
            mapping.setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
        }

        Class declaredType = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(property.getActualType().getQualifiedName(), helper.getClassLoader());
        JAXBElementRootConverter jaxbElementRootConverter = new JAXBElementRootConverter(declaredType);
        mapping.setConverter(jaxbElementRootConverter);
        if (property.getDomHandlerClassName() != null) {
            jaxbElementRootConverter.setNestedConverter(new DomHandlerConverter(property.getDomHandlerClassName()));
        }

        if (property.isLax() || property.isReference()) {
            mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        } else {
            if (property.isAny()) {
                mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
            } else {
                mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_NONE_AS_ELEMENT);
            }
        }

        mapping.setMixedContent(isMixed);
        if (isMixed) {
            mapping.setPreserveWhitespaceForMixedContent(true);
        }
        if (property.isAny()) {
            mapping.setUseXMLRoot(true);
        }

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
        }
        collectionType = containerClassImpl(collectionType);
        mapping.useCollectionClass(helper.getClassForJavaClass(collectionType));
        
        return mapping;
    }

    public CompositeObjectMapping generateCompositeObjectMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, String referenceClassName) {
        CompositeObjectMapping mapping = new XMLCompositeObjectMapping();
      
        initializeXMLMapping((XMLMapping)mapping, property);

        // if the XPath is set (via xml-path) use it; otherwise figure it out
        mapping.setField((XMLField)getXPathForField(property, namespaceInfo, false, false));
        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else {
            NullPolicy nullPolicy = (NullPolicy) mapping.getNullPolicy();
            nullPolicy.setSetPerformedForAbsentNode(false);
            if(property.isNillable()) {
                nullPolicy.setNullRepresentedByXsiNil(true);
                nullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
            }
        }

        if (referenceClassName == null){
        	setTypedTextField((Field)mapping.getField());
        	String defaultValue = property.getDefaultValue();
        	if (null != defaultValue) {
        	    mapping.setConverter(new DefaultElementConverter(defaultValue));
        	}
        } else {
        	mapping.setReferenceClassName(referenceClassName);
        }
            if(property.isTransientType()){
                mapping.setReferenceClassName(Constants.UNKNOWN_OR_TRANSIENT_CLASS);
            }
        
        if (property.isRequired()) {
            ((Field) mapping.getField()).setRequired(true);
        }
        return mapping;

    }

    public DirectMapping generateDirectMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo) {
    	DirectMapping mapping = new XMLDirectMapping();
        mapping.setNullValueMarshalled(true);
        
        String fixedValue = property.getFixedValue();
        if (fixedValue != null) {
            mapping.setIsWriteOnly(true);
        }
        initializeXMLMapping((XMLMapping)mapping, property);
   
        // if the XPath is set (via xml-path) use it; otherwise figure it out
        Field xmlField = getXPathForField(property, namespaceInfo, true, false);
        mapping.setField(xmlField);

        if (property.getDefaultValue() != null) {
            mapping.setNullValue(property.getDefaultValue());
        }

        if (property.isXmlId()) {
            mapping.setCollapsingStringValues(true);
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
            ((Field) mapping.getField()).setRequired(true);
        }

        if (property.getType() != null) {
            String theClass = null;
            String targetClass = null;
            if (property.isSetXmlJavaTypeAdapter()) {
                theClass = property.getOriginalType().getQualifiedName();
                targetClass = property.getType().getQualifiedName();                
            } else {
                theClass = property.getType().getQualifiedName();
                
            }
            // Try to get the actual Class
            try {
                JavaClass actualJavaClass = helper.getJavaClass(theClass);
                Class actualClass =  org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(actualJavaClass.getQualifiedName(), helper.getClassLoader());
                mapping.setAttributeClassification(actualClass);
                if(targetClass != null) {
                    Class fieldClass = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(targetClass, helper.getClassLoader());
                    mapping.getField().setType(fieldClass);
                }  
                
            } catch (Exception e) {
                // Couldn't find Class (Dynamic?), so set class name instead.
                mapping.setAttributeClassificationName(theClass);
            }
        }

        if (Constants.QNAME_QNAME.equals(property.getSchemaType())){
            ((Field) mapping.getField()).setSchemaType(Constants.QNAME_QNAME);
        }
        // handle cdata set via metadata
        if (property.isSetCdata()) {
            mapping.setIsCDATA(property.isCdata());
        }
        return mapping;
    }

    public BinaryDataMapping generateBinaryMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo) {
        BinaryDataMapping mapping = new XMLBinaryDataMapping();
      
        initializeXMLMapping((XMLMapping)mapping, property);

        // if the XPath is set (via xml-path) use it
        mapping.setField(getXPathForField(property, namespaceInfo, false, false));
        if (property.isSwaAttachmentRef()) {
            ((Field) mapping.getField()).setSchemaType(Constants.SWA_REF_QNAME);
            mapping.setSwaRef(true);
        } else if (property.isMtomAttachment()) {
            Field f = ((Field) mapping.getField());
            if (!f.getSchemaType().equals(Constants.HEX_BINARY_QNAME)) {
                f.setSchemaType(Constants.BASE_64_BINARY_QNAME);
            }
        }
         
        if (property.isInlineBinaryData()) {
            mapping.setShouldInlineBinaryData(true);
        }
        // use a non-dynamic implementation of MimeTypePolicy to wrap the MIME string
        if (property.getMimeType() != null) {
            mapping.setMimeTypePolicy(new FixedMimeTypePolicy(property.getMimeType(),(DatabaseMapping) mapping));
        } else {
            if(areEquals(property.getType(), javax.xml.transform.Source.class)) {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/xml", (DatabaseMapping)mapping));
            } else if(areEquals(property.getType(), java.awt.Image.class)) {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("image/png", (DatabaseMapping)mapping));
            } else {
                mapping.setMimeTypePolicy(new FixedMimeTypePolicy("application/octet-stream", (DatabaseMapping)mapping));
            }
        }
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
        mapping.setAttributeClassificationName(property.getActualType().getQualifiedName());
        return mapping;
    }

    public BinaryDataCollectionMapping generateBinaryDataCollectionMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo) {
        BinaryDataCollectionMapping mapping = new XMLBinaryDataCollectionMapping();
        initializeXMLMapping((XMLMapping)mapping, property);

        initializeXMLContainerMapping(mapping, property.getType().isArray());
    
        if (property.isSetXmlElementWrapper()) {
            mapping.setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
        }
     
        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else if (property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
            mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        }
        // if the XPath is set (via xml-path) use it
        mapping.setField(getXPathForField(property, namespaceInfo, false, false));
        if (property.isSwaAttachmentRef()) {
            ((Field) mapping.getField()).setSchemaType(Constants.SWA_REF_QNAME);
            mapping.setSwaRef(true);
        } else if (property.isMtomAttachment()) {
            Field f = (Field) mapping.getField();
            if (!f.getSchemaType().equals(Constants.HEX_BINARY_QNAME)) {
                f.setSchemaType(Constants.BASE_64_BINARY_QNAME);
            }            
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
        JavaClass itemType = property.getActualType();
        if(collectionType != null && helper.isCollectionType(collectionType)){
            try{
                Class declaredClass = PrivilegedAccessHelper.getClassForName(itemType.getQualifiedName(), false, helper.getClassLoader());
                mapping.setAttributeElementClass(declaredClass);
            }catch (Exception e) {
            }
        }
        collectionType = containerClassImpl(collectionType);
        mapping.useCollectionClassName(collectionType.getRawName());
        return mapping;
    }
    
    public DirectMapping generateDirectEnumerationMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, EnumTypeInfo enumInfo) {
    	DirectMapping mapping = new XMLDirectMapping();
    	initializeXMLMapping((XMLMapping)mapping, property);
        mapping.setNullValueMarshalled(true);
        mapping.setConverter(buildJAXBEnumTypeConverter(mapping, enumInfo));
        mapping.setField(getXPathForField(property, namespaceInfo, true, false));
        if (!mapping.getXPath().equals("text()")) {
            ((NullPolicy) mapping.getNullPolicy()).setSetPerformedForAbsentNode(false);
        }
        return mapping;
    }

    private JAXBEnumTypeConverter buildJAXBEnumTypeConverter(Mapping mapping, EnumTypeInfo enumInfo){
        JAXBEnumTypeConverter converter = new JAXBEnumTypeConverter(mapping, enumInfo.getClassName(), false);
        List<String> fieldNames = enumInfo.getFieldNames();
        List<Object> xmlEnumValues = enumInfo.getXmlEnumValues();
        for (int i=0; i< fieldNames.size(); i++) {
            converter.addConversionValue(xmlEnumValues.get(i), fieldNames.get(i));
        }
        return converter;
    }

    public Mapping generateCollectionMapping(Property property, Descriptor descriptor, JavaClass descriptorJavaClass, NamespaceInfo namespaceInfo) {
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
            return generateCompositeCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo, javaClass.getQualifiedName());
        }
        if (!property.isAttribute() && javaClass != null && javaClass.getQualifiedName().equals(OBJECT_CLASS_NAME)){
            CompositeCollectionMapping ccMapping = generateCompositeCollectionMapping(property, descriptor, descriptorJavaClass, namespaceInfo, null);
            ccMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
            return ccMapping;
        }
        if(isBinaryData(javaClass)){
        	return generateBinaryDataCollectionMapping(property, descriptor, namespaceInfo);
        }
        return generateDirectCollectionMapping(property, descriptor, namespaceInfo);
    }

    public DirectCollectionMapping generateEnumCollectionMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, EnumTypeInfo info) {
    	
    	DirectCollectionMapping mapping = generateDirectCollectionMapping(property, descriptor, namespaceInfo);
    	mapping.setValueConverter(buildJAXBEnumTypeConverter(mapping, info));
    	return mapping;
    }
    public AnyAttributeMapping generateAnyAttributeMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo) {
        AnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        initializeXMLMapping((XMLAnyAttributeMapping)mapping, property);
        initializeXMLContainerMapping(mapping, property.getType().isArray());
     
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

    public AnyObjectMapping generateAnyObjectMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo)  {
        AnyObjectMapping mapping = new XMLAnyObjectMapping();
        initializeXMLMapping((XMLMapping)mapping, property);

        // if the XPath is set (via xml-path) use it
        if (property.getXmlPath() != null) {
            mapping.setField(new XMLField(property.getXmlPath()));
        }

        Class declaredType = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(property.getActualType().getQualifiedName(), helper.getClassLoader());
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
            Descriptor desc = new XMLDescriptor();
            desc.setJavaClass(generatedClass);

            desc.addMapping((CoreMapping)generateMappingForType(keyType, Property.DEFAULT_KEY_NAME));
            desc.addMapping((CoreMapping)generateMappingForType(valueType, Property.DEFAULT_VALUE_NAME));
            desc.setNamespaceResolver(nr);
            project.addDescriptor((CoreDescriptor)desc);
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

    private Mapping generateMappingForType(JavaClass theType, String attributeName){
    	Mapping mapping;
        boolean typeIsObject =  theType.getRawName().equals(OBJECT_CLASS_NAME);
        TypeInfo info = typeInfo.get(theType.getQualifiedName());
        if ((info != null && !(info.isEnumerationType())) || typeIsObject) {
            mapping = new XMLCompositeObjectMapping();
            mapping.setAttributeName(attributeName);
            ((CompositeObjectMapping)mapping).setXPath(attributeName);
            if(typeIsObject){
            	((CompositeObjectMapping)mapping).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
            	setTypedTextField((Field)((CompositeObjectMapping)mapping).getField());
            }else{
            	((CompositeObjectMapping)mapping).setReferenceClassName(theType.getQualifiedName());
            }
        } else {
            mapping = new XMLDirectMapping();
            mapping.setAttributeName(attributeName);
            ((DirectMapping)mapping).setNullValueMarshalled(true);
            ((DirectMapping)mapping).setXPath(attributeName + TXT);

            QName schemaType = (QName) userDefinedSchemaTypes.get(theType.getQualifiedName());

            if (schemaType == null) {
                schemaType = (QName) helper.getXMLToJavaTypeMap().get(theType);
            }
            ((Field)((DirectMapping)mapping).getField()).setSchemaType(schemaType);
            if(info != null && info.isEnumerationType()) {
                ((DirectMapping)mapping).setConverter(buildJAXBEnumTypeConverter(mapping, (EnumTypeInfo)info));
            }
        }
        return mapping;
    }

    public CompositeCollectionMapping generateCompositeCollectionMapping(Property property, Descriptor descriptor, JavaClass javaClass, NamespaceInfo namespaceInfo, String referenceClassName) {
        CompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        initializeXMLMapping((XMLMapping)mapping, property);
        initializeXMLContainerMapping(mapping, property.getType().isArray());
        
        JavaClass manyValueJavaClass = helper.getJavaClass(ManyValue.class);        
        if (manyValueJavaClass.isAssignableFrom(javaClass)){
            mapping.setReuseContainer(false);
        }
     
        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else if (property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
            mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        }
        
        if (property.isSetXmlElementWrapper()) {
            mapping.setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
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
        }else if (helper.isMapType(property.getType())){
            Class generatedClass = generateMapEntryClassAndDescriptor(property, descriptor.getNonNullNamespaceResolver());
            referenceClassName = generatedClass.getName();
            String mapClassName = property.getType().getRawName();
            mapping.setAttributeAccessor(new MapValueAttributeAccessor(mapping.getAttributeAccessor(), mapping.getContainerPolicy(), generatedClass, mapClassName, helper.getClassLoader()));
        }
        collectionType = containerClassImpl(collectionType);
        mapping.useCollectionClassName(collectionType.getRawName());

        // if the XPath is set (via xml-path) use it; otherwise figure it out
        Field xmlField = getXPathForField(property, namespaceInfo, false, false);
        if(helper.isMapType(property.getType())){
    	    JavaClass mapValueClass = helper.getJavaClass(MapValue.class);
	        if(mapValueClass.isAssignableFrom(javaClass)){
	        	mapping.setXPath("entry");
	        }else{
	        	mapping.setXPath(xmlField.getXPath() + "/entry");
	        }
        }else{
        	mapping.setXPath(xmlField.getXPath());
        }

        if (referenceClassName == null){                   
        	setTypedTextField((Field)mapping.getField());
        } else {
        	mapping.setReferenceClassName(referenceClassName);
        }
        if(property.isTransientType()){
            mapping.setReferenceClassName(Constants.UNKNOWN_OR_TRANSIENT_CLASS);   
        }    

        if (property.isRequired()) {
            ((Field) mapping.getField()).setRequired(true);
        }
           
        return mapping;
    }

    public DirectCollectionMapping generateDirectCollectionMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo) {
        DirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        initializeXMLMapping((XMLMapping)mapping, property);

        initializeXMLContainerMapping(mapping, property.getType().isArray());
     
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
        } else if (helper.isCollectionType(collectionType)){
            Collection args = collectionType.getActualTypeArguments();
        	if (args.size() >0){
        		JavaClass itemType = (JavaClass)args.iterator().next();
        		try {
        			Class declaredClass = PrivilegedAccessHelper.getClassForName(itemType.getRawName(), false, helper.getClassLoader());
        			if(declaredClass != String.class){
        			    mapping.setAttributeElementClass(declaredClass);
        			}
        		} catch (Exception e) {}
        	}
        }
        collectionType = containerClassImpl(collectionType);
        mapping.useCollectionClassName(collectionType.getRawName());

        if (property.getDefaultValue() != null) {
            mapping.setNullValue(property.getDefaultValue());
        }
        
        // if the XPath is set (via xml-path) use it; otherwise figure it out
        Field xmlField = getXPathForField(property, namespaceInfo, true, false);
        mapping.setField(xmlField);

        if (helper.isAnnotationPresent(property.getElement(), XmlMixed.class)) {
            xmlField.setXPath("text()");
        }

        if (Constants.QNAME_QNAME.equals(property.getSchemaType())){
            ((Field) mapping.getField()).setSchemaType(Constants.QNAME_QNAME);
        }

        // handle null policy set via xml metadata
        if (property.isSetNullPolicy()) {
            mapping.setNullPolicy(getNullPolicyFromProperty(property, namespaceInfo.getNamespaceResolverForDescriptor()));
        } else if (property.isNillable()){
            mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
            mapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        }
        mapping.getNullPolicy().setNullRepresentedByEmptyNode(false);

        if (property.isSetXmlElementWrapper()) {
            mapping.setWrapperNullPolicy(getWrapperNullPolicyFromProperty(property));
        }

        if (property.isRequired()) {
            ((Field) mapping.getField()).setRequired(true);
        }

        if (property.isXmlElementType() && property.getGenericType()!=null ){
        	Class theClass = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(property.getGenericType().getQualifiedName(), helper.getClassLoader());
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


    public String getPrefixForNamespace(String URI, NamespaceResolver namespaceResolver) {
    	return getPrefixForNamespace(URI, namespaceResolver, true);
    }
    
    public String getPrefixForNamespace(String URI, NamespaceResolver namespaceResolver, boolean addPrefixToNR) {
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
        if (javax.xml.XMLConstants.XML_NS_URI.equals(URI)) {
        	return javax.xml.XMLConstants.XML_NS_PREFIX;
        }           
        String prefix = globalNamespaceResolver.resolveNamespaceURI(URI);
        if(prefix == null){
            if(URI.equals(globalNamespaceResolver.getDefaultNamespaceURI())) { 
                namespaceResolver.setDefaultNamespaceURI(URI);
                return null;
            } else {            	
            	//Bug 400536 before generating a new one check other resolvers
            	String suggestedPrefix = null;
                NamespaceInfo refInfo = getNamespaceInfoForURI(URI);
            	if(refInfo != null && refInfo.getNamespaceResolver() !=null){
            		suggestedPrefix = refInfo.getNamespaceResolver().resolveNamespaceURI(URI);
            	}            	
            	if(suggestedPrefix != null){
            	    prefix = globalNamespaceResolver.generatePrefix(suggestedPrefix);
                } else{
            	    prefix = globalNamespaceResolver.generatePrefix();
                }
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
        Descriptor descriptor = tInfo.getDescriptor();
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
        Descriptor superDescriptor = superTypeInfo.getDescriptor();
        if (superDescriptor != null) {
            XMLSchemaReference sRef = descriptor.getSchemaReference();
            if (sRef == null || sRef.getSchemaContext() == null) {
                return;
            }

            JavaClass rootMappedSuperClass = getRootMappedSuperClass(superClass);
            TypeInfo rootTypeInfo =  typeInfo.get(rootMappedSuperClass.getName());
            Descriptor rootDescriptor = rootTypeInfo.getDescriptor();
            if (rootDescriptor.getNamespaceResolver() == null) {
                rootDescriptor.setNamespaceResolver(new org.eclipse.persistence.oxm.NamespaceResolver());
            }

            if (rootDescriptor.getInheritancePolicy().getClassIndicatorField() == null) {
                Field classIndicatorField;
                if (rootTypeInfo.isSetXmlDiscriminatorNode()) {
                    classIndicatorField = new XMLField(rootTypeInfo.getXmlDiscriminatorNode());
                } else {
                    classIndicatorField = new XMLField(ATT + "type");
                    classIndicatorField.getXPathFragment().setNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
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
            //Check for attributeGroups
            Map<String, AttributeGroup> childGroups = ((XMLDescriptor)descriptor).getAttributeGroups();
            Map<String, AttributeGroup> parentGroups = ((XMLDescriptor)rootDescriptor).getAttributeGroups();
            if(childGroups != null && !(childGroups.isEmpty()) && parentGroups != null && !(parentGroups.isEmpty())) {
                for(String nextKey:childGroups.keySet()) {
                    AttributeGroup parentGroup = parentGroups.get(nextKey);
                    if(parentGroup != null) {
                        AttributeGroup childGroup = childGroups.get(nextKey);
                        parentGroup.getSubClassGroups().put(descriptor.getJavaClassName(), childGroup);
                    }
                }
            }
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
            if (info.isEnumerationType()) {
                continue;
            }
            NamespaceInfo namespaceInfo = this.packageToPackageInfoMappings.get(javaClass.getPackageName()).getNamespaceInfo();

            Descriptor descriptor = info.getDescriptor();
            if (descriptor != null) {
                generateMappings(info, descriptor, javaClass, namespaceInfo);
                // set primary key fields (if necessary)
                CoreMapping mapping;
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
            info.postInitialize();
        }
    }

    /**
     * Generate mappings for a given TypeInfo.
     *
     * @param info
     * @param descriptor
     * @param namespaceInfo
     */
    public void generateMappings(TypeInfo info, Descriptor descriptor, JavaClass descriptorJavaClass, NamespaceInfo namespaceInfo) {
        if(info.isAnonymousComplexType()) {
            //may need to generate inherited mappings
            generateInheritedMappingsForAnonymousType(info, descriptor, descriptorJavaClass, namespaceInfo);
        }
        List<Property> propertiesInOrder = info.getNonTransientPropertiesInPropOrder();
        for (int i = 0; i < propertiesInOrder.size(); i++) {
            Property next = propertiesInOrder.get(i);
            if (next != null && (!next.isTransient() || (next.isTransient() && next.isXmlLocation()))) {
                Mapping mapping = generateMapping(next, descriptor, descriptorJavaClass, namespaceInfo);
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
                    descriptor.addMapping((CoreMapping)mapping);
                }
                // set user-defined properties if necessary
                if (next.isSetUserProperties()) {
                    mapping.setProperties(next.getUserProperties());
                }
                //get package info
                AccessorFactoryWrapper accessorFactory = info.getXmlAccessorFactory();
                if(accessorFactory == null) {
                    accessorFactory = info.getPackageLevelXmlAccessorFactory();
                }
                if(accessorFactory != null) {
                    try {
                        Object accessor = CompilerHelper.createAccessorFor(descriptorJavaClass, next, helper, accessorFactory);
                        
                        if(accessor != null) {
                            CustomAccessorAttributeAccessor attributeAccessor = new CustomAccessorAttributeAccessor(accessor);
                            mapping.setAttributeAccessor(attributeAccessor);
                        }
                    } catch(Exception ex) {}
                }
            }
            next.postInitialize();
        }
    }

    private void generateInheritedMappingsForAnonymousType(TypeInfo info, Descriptor descriptor, JavaClass descriptorJavaClass, NamespaceInfo namespaceInfo) {
        List<TypeInfo> mappedParents = new ArrayList<TypeInfo>();
        JavaClass next = CompilerHelper.getNextMappedSuperClass(descriptorJavaClass, typeInfo, helper);
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
                    Mapping mapping = generateMapping(nextProp, descriptor, descriptorJavaClass, namespaceInfo);
                    descriptor.addMapping((CoreMapping)mapping);
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
    public CollectionReferenceMapping generateXMLCollectionReferenceMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        CollectionReferenceMapping mapping = new XMLCollectionReferenceMapping();
        initializeXMLMapping((XMLMapping)mapping, property);

        initializeXMLContainerMapping(mapping, property.getType().isArray());
        mapping.setUsesSingleNode(property.isXmlList() || (property.isAttribute() && (property.getXmlPath() == null || !property.getXmlPath().contains("/"))));
       
        String referenceClassName = referenceClass.getQualifiedName();      
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
        }
        collectionType = containerClassImpl(collectionType);
        mapping.useCollectionClassName(collectionType.getRawName());
        mapping.setReferenceClassName(referenceClassName);
        
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
                tgtXPath = getXPathForField(prop, namespaceInfo, !prop.isAttribute(), false).getXPath();
            }
            // if the XPath is set (via xml-path) use it
            Field srcXPath;
            if (property.getXmlPath() != null) {
                srcXPath = new XMLField(property.getXmlPath());
            } else {
                srcXPath = getXPathForField(property, namespaceInfo, true, false);
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
            if (helper.isCollectionType(backPointerPropertyType)) {
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
    public ObjectReferenceMapping generateXMLObjectReferenceMapping(Property property, Descriptor descriptor, NamespaceInfo namespaceInfo, JavaClass referenceClass) {
        ObjectReferenceMapping mapping = new XMLObjectReferenceMapping();
        initializeXMLMapping((XMLMapping)mapping, property);
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
                tgtXPath = getXPathForField(prop, namespaceInfo, !prop.isAttribute(), false).getXPath();
            }
            // if the XPath is set (via xml-path) use it, otherwise figure it out
            Field srcXPath;
            if (property.getXmlPath() != null) {
                srcXPath = new XMLField(property.getXmlPath());
            } else {
                srcXPath = getXPathForField(property, namespaceInfo, true, false);
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
            if (helper.isCollectionType(backPointerPropertyType)) {
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
                idXpath = this.getXPathForField(idProp, namespaceInfo, !idProp.isAttribute(), false).getXPath();
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
                    keyXpath = this.getXPathForField(xmlkeyProperty, namespaceInfo, !xmlkeyProperty.isAttribute(), false).getXPath();
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


    private String prefixCustomXPath(String unprefixedXPath, Property property, NamespaceInfo nsInfo) {
        String newXPath = "";
        QName schemaName = property.getSchemaName();
        String namespace = schemaName.getNamespaceURI();

        if (null == namespace || namespace.equals(Constants.EMPTY_STRING)) {
            return unprefixedXPath;
        }

        String prefix = getPrefixForNamespace(namespace, nsInfo.getNamespaceResolverForDescriptor());
        if (null == prefix) {
            return unprefixedXPath;
        }

        StringTokenizer st = new StringTokenizer(unprefixedXPath, Constants.XPATH_SEPARATOR);
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
                
            if (st.hasMoreTokens()) {
                if (nextToken.lastIndexOf(Constants.COLON) != -1) {
                    // Token already has a user-supplied prefix
                    newXPath += nextToken;
                } else {
                    newXPath += prefix + Constants.COLON + nextToken;
                }
                newXPath += Constants.XPATH_SEPARATOR;
            } else {
                // Last token is text()
                newXPath += nextToken;
            }

        }
        return newXPath;
    }
    
    public Field getXPathForField(Property property, NamespaceInfo namespaceInfo, boolean isTextMapping, boolean isAny) {
        Field xmlField = null;
        String xPath = property.getXmlPath();
        if (null != xPath) {
            String newXPath = prefixCustomXPath(xPath, property, namespaceInfo);
            xmlField = new XMLField(newXPath);
        } else {
            xPath = "";
            if (property.isSetXmlElementWrapper()) {
                XmlElementWrapper wrapper = property.getXmlElementWrapper();
                String namespace = wrapper.getNamespace();
                if (namespace.equals(XMLProcessor.DEFAULT)) {
                    if (namespaceInfo.isElementFormQualified()) {
                        namespace = namespaceInfo.getNamespace();
                    } else {
                        namespace = "";
                    }
                }
                
                if (namespace.equals("")) {
                    xPath += (wrapper.getName() + "/");
                } else {
                	String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor());
                	xPath += getQualifiedString(prefix, wrapper.getName() + "/");
                }

                if (isAny || property.isMap()) {
                    xPath = xPath.substring(0, xPath.length() - 1);
                    xmlField = new XMLField(xPath);
                    return xmlField;
                }

            }
            if (property.isAttribute()) {
                if (property.isSetXmlPath()) {
                    xPath += property.getXmlPath();
                } else {
                    QName name = property.getSchemaName();
                    String namespace = name.getNamespaceURI();
                    if (namespace.equals("")) {
                        xPath += (ATT + name.getLocalPart());
                    } else {
                        String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor());
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
        if(schemaType !=null && !schemaType.equals (Constants.NORMALIZEDSTRING_QNAME)){
            xmlField.setSchemaType(schemaType);
        }
        
        return xmlField;
    }

    public Field getXPathForElement(String path, QName elementName, NamespaceInfo namespaceInfo, boolean isText) {
        String namespace = "";
        if (!elementName.getNamespaceURI().equals(Constants.EMPTY_STRING)) {
            namespace = elementName.getNamespaceURI();
        }
        if (namespace.equals(Constants.EMPTY_STRING)) {
            path += elementName.getLocalPart();
            if (isText) {
                path += TXT;
            }
        } else {
            String prefix = getPrefixForNamespace(namespace, namespaceInfo.getNamespaceResolverForDescriptor());
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

    public void processGlobalElements(CoreProject project) {
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
            		}
                    if(nextElement.getJavaType().isEnum()) {
                        if(!(helper.getClassLoader() instanceof DynamicClassLoader)) {
                            //  Only generate enum wrappers in non-dynamic case.
                            Class generatedClass = addEnumerationWrapperAndDescriptor(type, nextElement.getJavaType().getRawName(), nextElement, nextClassName, attributeTypeName);
                            this.qNamesToGeneratedClasses.put(next, generatedClass);
                            if(nextElement.getTypeMappingInfo() != null) {
                                typeMappingInfoToGeneratedClasses.put(nextElement.getTypeMappingInfo(), generatedClass);
                            }
                            try{
                                Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
                                this.qNamesToDeclaredClasses.put(next, declaredClass);
                            }catch(Exception ex) {
                            
                            }
                        }                	

                    }
                    continue;
                }
                Class generatedClass = generateWrapperClassAndDescriptor(type, next, nextElement, nextClassName, attributeTypeName);
                
                this.qNamesToGeneratedClasses.put(next, generatedClass);
                if(type != null && type.isEnumerationType() && nextElement.isXmlRootElement()) {
                    this.classToGeneratedClasses.put(type.getJavaClassName(), generatedClass);
                }
                try{
                    Class declaredClass = PrivilegedAccessHelper.getClassForName(nextClassName, false, helper.getClassLoader());
                    this.qNamesToDeclaredClasses.put(next, declaredClass);
                }catch(Exception e){

                }
            }else if(type != null && !type.isTransient()){
                if(next.getNamespaceURI() == null || next.getNamespaceURI().equals("")) {
                    type.getDescriptor().addRootElement(next.getLocalPart());
                } else {
                    Descriptor descriptor = type.getDescriptor();
                    String uri = next.getNamespaceURI();
                    String prefix = getPrefixForNamespace(uri, descriptor.getNamespaceResolver());
                    descriptor.addRootElement(getQualifiedString(prefix, next.getLocalPart()));
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
            generatedClass = generateWrapperClassAndDescriptor(type, nextElement.getElementName(), nextElement, nextClassName, attributeTypeName);
            classToGeneratedClasses.put(javaClassName, generatedClass);
        }
        return generatedClass;
    }

     private Class generateWrapperClassAndDescriptor(TypeInfo type, QName next, ElementDeclaration nextElement, String nextClassName, String attributeTypeName){
        String namespaceUri = null;
      	if(next!= null){
              //generate a class/descriptor for this element
              namespaceUri = next.getNamespaceURI();
              if (namespaceUri == null || namespaceUri.equals(XMLProcessor.DEFAULT)) {
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

          Descriptor desc = (Descriptor)project.getDescriptor(generatedClass);

          if(desc == null){
	          desc = new XMLDescriptor();
	          desc.setJavaClass(generatedClass);


	          if(nextElement.isList()){
	              DirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
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
	                 ((Field)mapping.getField()).setSchemaType(Constants.BASE_64_BINARY_QNAME);
	              }
	              else if(nextClassName.equals("javax.xml.namespace.QName")){
	                  ((Field)mapping.getField()).setSchemaType(Constants.QNAME_QNAME);
	              }
	              desc.addMapping((CoreMapping)mapping);
	          } else{
	              if(nextElement.getJavaTypeName().equals(OBJECT_CLASS_NAME)){
	                  CompositeObjectMapping mapping = new XMLCompositeObjectMapping();
	                  mapping.setAttributeName("value");
	                  mapping.setSetMethodName("setValue");
	                  mapping.setGetMethodName("getValue");
	                  mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
	                  mapping.setXPath(".");
	              	  setTypedTextField((Field)mapping.getField());

	                  desc.addMapping((CoreMapping)mapping);	                 
	              }else if(isBinaryData(nextElement.getJavaType())){
	              	  BinaryDataMapping mapping = new XMLBinaryDataMapping();
	              	  mapping.setAttributeName("value");
	              	  mapping.setXPath(".");
	                  ((Field)mapping.getField()).setSchemaType(Constants.BASE_64_BINARY_QNAME);
	                  mapping.setSetMethodName("setValue");
	                  mapping.setGetMethodName("getValue");
	                  mapping.getNullPolicy().setNullRepresentedByXsiNil(true);
	                  mapping.getNullPolicy().setNullRepresentedByEmptyNode(false);

	                  Class attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(attributeTypeName, helper.getClassLoader());
	                  mapping.setAttributeClassification(attributeClassification);

	              	  mapping.setShouldInlineBinaryData(false);
	              	  //if(nextElement.getTypeMappingInfo() != null) {
	              	      mapping.setSwaRef(nextElement.isXmlAttachmentRef());
	              	      mapping.setMimeType(nextElement.getXmlMimeType());
	              	  //}
	                  desc.addMapping((CoreMapping)mapping);

	              }else{
	            	  DirectMapping mapping = new XMLDirectMapping();
	                  mapping.setNullValueMarshalled(true);
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
	                          attributeClassification = org.eclipse.persistence.internal.helper.Helper.getClassFromClasseName(attributeTypeName, helper.getClassLoader());
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
	                      ((Field)mapping.getField()).setSchemaType(Constants.BASE_64_BINARY_QNAME);
	                  }
	                  else if(nextClassName.equals("javax.xml.namespace.QName")){
	                      ((Field)mapping.getField()).setSchemaType(Constants.QNAME_QNAME);
	                  }

	                  if (nextElement.getJavaTypeAdapterClass() != null) {
	                      mapping.setConverter(new XMLJavaTypeConverter(nextElement.getJavaTypeAdapterClass()));
	                  }

	                  desc.addMapping((CoreMapping)mapping);
	              }
	          }
	          if(next != null){
	              NamespaceInfo info = getNamespaceInfoForURI(namespaceUri);

	  			if(info != null) {
	  				NamespaceResolver resolver = info.getNamespaceResolverForDescriptor();
	  				
	  				String prefix = null;
	  				if(namespaceUri != Constants.EMPTY_STRING){
	  				    prefix = resolver.resolveNamespaceURI(namespaceUri);
	  				    if(prefix == null){
	  					    prefix = getPrefixForNamespace(namespaceUri, resolver);	  					
	  				    }
	  				}
	  				desc.setNamespaceResolver(resolver);
	  				if(nextElement.isXmlRootElement()) {
	  				    desc.setDefaultRootElement(getQualifiedString(prefix, next.getLocalPart()));
	  				} else {
	  				    desc.setDefaultRootElement("");
	  				    desc.addRootElement(getQualifiedString(prefix, next.getLocalPart()));
	  				    desc.setResultAlwaysXMLRoot(true);
	  				}
	              } else {
	                  if(namespaceUri.equals("")) {
	                      desc.setDefaultRootElement(next.getLocalPart());
	                  } else {
	                      NamespaceResolver resolver = new org.eclipse.persistence.oxm.NamespaceResolver();
	                      String prefix = getPrefixForNamespace(namespaceUri, resolver);

	                      desc.setNamespaceResolver(resolver);
                          if(nextElement.isXmlRootElement()) {
                              desc.setDefaultRootElement(getQualifiedString(prefix, next.getLocalPart()));
                          } else {
                              desc.setDefaultRootElement("");
                              desc.addRootElement(getQualifiedString(prefix, next.getLocalPart()));
                              desc.setResultAlwaysXMLRoot(true);
                          }
	  				}
	  			}
	          }
	          project.addDescriptor((CoreDescriptor)desc);
          }
          return generatedClass;
    }

    private String getQualifiedString(String prefix, String localPart){
    	if(prefix == null){
    		return localPart;
    	}
    	return prefix + Constants.COLON + localPart;
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

    private AbstractNullPolicy getWrapperNullPolicyFromProperty(Property property) {
        NullPolicy nullPolicy = null;

        if (property.isSetXmlElementWrapper()) {
            nullPolicy = new NullPolicy();
            nullPolicy.setNullRepresentedByEmptyNode(false);
            nullPolicy.setSetPerformedForAbsentNode(false);

            if (property.getXmlElementWrapper().isNillable()) {
                nullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
                nullPolicy.setNullRepresentedByXsiNil(true);
            } else {
                nullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);
                nullPolicy.setNullRepresentedByXsiNil(false);
            }
        }

        return nullPolicy;
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
    	return areEquals(type, CoreClassConstants.APBYTE) ||areEquals(type, "javax.activation.DataHandler") || areEquals(type, "java.awt.Image") || areEquals(type, "javax.xml.transform.Source") || areEquals(type, "javax.mail.internet.MimeMultipart");
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

    private void initializeXMLContainerMapping(XMLContainerMapping xmlContainerMapping, boolean isArray) {
        xmlContainerMapping.setReuseContainer(!isArray);
        xmlContainerMapping.setDefaultEmptyContainer(false);
    }
    
    private void initializeXMLMapping(XMLMapping mapping, Property property){
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
    }
    
    private JavaClass containerClassImpl(JavaClass collectionType) {
        if (areEquals(collectionType, List.class) || areEquals(collectionType, Collection.class) || collectionType.isArray() || helper.isMapType(collectionType) ) {
            return jotArrayList;
        } else if (areEquals(collectionType, Set.class)) {
            return jotHashSet;
        } else if (areEquals(collectionType, Deque.class) || areEquals(collectionType, Queue.class)) {
            return jotLinkedList;
        } else if (areEquals(collectionType, NavigableSet.class) || areEquals(collectionType, SortedSet.class)) {
            return jotTreeSet;
        } else {
            return collectionType;
        }
    }

}
