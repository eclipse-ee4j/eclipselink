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

import java.awt.Image;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlType.DEFAULT;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.Constants;
import org.eclipse.persistence.internal.libraries.asm.Label;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.attrs.Annotation;
import org.eclipse.persistence.internal.libraries.asm.attrs.LocalVariableTypeTableAttribute;
import org.eclipse.persistence.internal.libraries.asm.attrs.RuntimeVisibleAnnotations;
import org.eclipse.persistence.internal.libraries.asm.attrs.SignatureAttribute;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.AnnotationProxy;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaFieldImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.annotations.XmlAccessMethods;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.eclipse.persistence.oxm.annotations.XmlContainerProperty;
import org.eclipse.persistence.oxm.annotations.XmlCustomizer;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.eclipse.persistence.oxm.annotations.XmlIsSetNullPolicy;
import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;
import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;
import org.eclipse.persistence.oxm.annotations.XmlParameter;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;
import org.eclipse.persistence.oxm.annotations.XmlProperties;
import org.eclipse.persistence.oxm.annotations.XmlProperty;
import org.eclipse.persistence.oxm.annotations.XmlReadOnly;
import org.eclipse.persistence.oxm.annotations.XmlWriteOnly;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>To perform some initial processing of Java classes and JAXB 2.0 
 * Annotations and generate meta data that can be used by the Mappings Generator and Schema Generator
 * <p><b>Responsibilities:</b><ul>
 * <li>Generate a map of TypeInfo objects, keyed on class name</li>
 * <li>Generate a map of user defined schema types</li>
 * <li>Identify any class-based JAXB 2.0 callback methods, and create MarshalCallback and
 * UnmarshalCallback objects to wrap them.</li>
 * <li>Centralize processing which is common to both Schema Generation and Mapping Generation tasks</li>
 * <p>This class does the initial processing of the JAXB 2.0 Generation. It generates meta data 
 * that can be used by the later Schema Generation and Mapping Generation steps. 
 * 
 * @see org.eclipse.persistence.jaxb.compiler.Generator
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */
public class AnnotationsProcessor {
    private static final String JAVAX_ACTIVATION_DATAHANDLER = "javax.activation.DataHandler";
    private static final String JAVAX_MAIL_INTERNET_MIMEMULTIPART = "javax.mail.internet.MimeMultipart";
    private static final String TYPE_METHOD_NAME = "type";
    private static final String VALUE_METHOD_NAME = "value";
    private static final String ARRAY_PACKAGE_NAME = "jaxb.dev.java.net.array";
    private static final String ARRAY_NAMESPACE = "http://jaxb.dev.java.net/array";
    private static final String ARRAY_CLASS_NAME_SUFFIX = "Array";
    private static final String ORG_W3C_DOM = "org.w3c.dom";
    
    private ArrayList<JavaClass> typeInfoClasses;
    private HashMap<String, NamespaceInfo> packageToNamespaceMappings;
    private HashMap<String, MarshalCallback> marshalCallbacks;
    private HashMap<String, QName> userDefinedSchemaTypes;
    private HashMap<String, TypeInfo> typeInfo;
    private ArrayList<QName> typeQNames;
    private HashMap<String, UnmarshalCallback> unmarshalCallbacks;
    private HashMap<String, HashMap<QName, ElementDeclaration>> elementDeclarations;
    private HashMap<String, ElementDeclaration> xmlRootElements;
    private List<ElementDeclaration> localElements;
    private HashMap<String, JavaMethod> factoryMethods;
    private Map<String, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry> xmlRegistries;

    private Map<String, Class> arrayClassesToGeneratedClasses;
    private Map<Class, JavaClass> generatedClassesToArrayClasses;
    private Map<java.lang.reflect.Type, Class> collectionClassesToGeneratedClasses;
    private Map<Class, java.lang.reflect.Type> generatedClassesToCollectionClasses;

    private Map<JavaClass, TypeMappingInfo> javaClassToTypeMappingInfos;
    private Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClasses;
    private Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses;
    private Map<TypeMappingInfo, QName> typeMappingInfoToSchemaType;
    
    private NamespaceResolver namespaceResolver;
    private Helper helper;
    private String defaultTargetNamespace;

    private JAXBMetadataLogger logger;

    private boolean isDefaultNamespaceAllowed;

    public AnnotationsProcessor(Helper helper) {
        this.helper = helper;
        isDefaultNamespaceAllowed = true;
    }

    /**
     * Generate TypeInfo instances for a given array of JavaClasses.
     * 
     * @param classes
     */
    void processClassesAndProperties(JavaClass[] classes, TypeMappingInfo[] typeMappingInfos) {
        init(classes, typeMappingInfos);
        preBuildTypeInfo(classes);
        classes = postBuildTypeInfo(classes);
        processJavaClasses(null);
        finalizeProperties();
        createElementsForTypeMappingInfo();        
    }
    
    public void createElementsForTypeMappingInfo() {
        if (this.javaClassToTypeMappingInfos != null && !this.javaClassToTypeMappingInfos.isEmpty()) {
            Set<JavaClass> classes = this.javaClassToTypeMappingInfos.keySet();
            for (JavaClass nextClass : classes) {
                TypeMappingInfo nextInfo = this.javaClassToTypeMappingInfos.get(nextClass);
                if (nextInfo != null) {
                    boolean xmlAttachmentRef = false;
                    String xmlMimeType = null;
                    java.lang.annotation.Annotation[] annotations = getAnnotations(nextInfo);
                    Class adapterClass = this.typeMappingInfoToAdapterClasses.get(nextInfo);
                    Class declJavaType = null;
                    if(adapterClass != null){
                        declJavaType = CompilerHelper.getTypeFromAdapterClass(adapterClass);
                    }
                    if (annotations != null) {
                        for (int j = 0; j < annotations.length; j++) {
                            java.lang.annotation.Annotation nextAnnotation = annotations[j];
                            if (nextAnnotation != null) {
                                if (nextAnnotation instanceof XmlMimeType) {
                                    XmlMimeType javaAnnotation = (XmlMimeType) nextAnnotation;
                                    xmlMimeType = javaAnnotation.value();
                                } else if (nextAnnotation instanceof XmlAttachmentRef) {
                                    xmlAttachmentRef = true;
                                }
                            }
                        }
                    }                                       
                    
                    QName qname = null;
                                        
                    String nextClassName = nextClass.getQualifiedName();
                    
                                        
                    if(declJavaType != null){
                        nextClassName = declJavaType.getCanonicalName();                            
                    }
                    
                    if(typeMappingInfoToGeneratedClasses != null){
                        Class generatedClass = typeMappingInfoToGeneratedClasses.get(nextInfo);
                        if(generatedClass != null){
                            nextClassName = generatedClass.getCanonicalName();
                        }
                    }
                    
                    TypeInfo nextTypeInfo = typeInfo.get(nextClassName);
                    if(nextTypeInfo != null){
                        qname = new QName(nextTypeInfo.getClassNamespace(), nextTypeInfo.getSchemaTypeName());
                    } else {
                        qname = getUserDefinedSchemaTypes().get(nextClassName);
                        if(qname == null){
                           if (nextClassName.equals(ClassConstants.ABYTE.getName()) || nextClassName.equals(ClassConstants.APBYTE.getName()) || nextClassName.equals(Image.class.getName()) || nextClassName.equals(Source.class.getName()) || nextClassName.equals("javax.activation.DataHandler") ) {
                                if(xmlAttachmentRef){
                                    qname = XMLConstants.SWA_REF_QNAME;
                                }else{
                                    qname = XMLConstants.BASE_64_BINARY_QNAME;
                                }
                            } else if(nextClassName.equals(ClassConstants.OBJECT.getName())){
                               qname = XMLConstants.ANY_TYPE_QNAME;
                           } else {
                               Class theClass = helper.getClassForJavaClass(nextClass);
                               qname = (QName)XMLConversionManager.getDefaultJavaTypes().get(theClass);
                           }
                        }
                    }
                
                    if(qname != null){
                        typeMappingInfoToSchemaType.put(nextInfo, qname);
                    }
                    
                    if (nextInfo.getXmlTagName() != null) {
                        ElementDeclaration element = new ElementDeclaration(nextInfo.getXmlTagName(), nextClass, nextClass.getQualifiedName(), false);
                        element.setTypeMappingInfo(nextInfo);
                        element.setXmlMimeType(xmlMimeType);
                        element.setXmlAttachmentRef(xmlAttachmentRef);
                                                    
                        if(declJavaType != null){
                            element.setJavaType(helper.getJavaClass(declJavaType));
                        }
                        Class generatedClass = typeMappingInfoToGeneratedClasses.get(nextInfo);
                        if (generatedClass != null) {
                            element.setJavaType(helper.getJavaClass(generatedClass));
                        }
                        if (nextInfo.getElementScope() == TypeMappingInfo.ElementScope.Global) {
                            this.getGlobalElements().put(element.getElementName(), element);
                        } else {
                            this.localElements.add(element);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns an array of Annotations for a given TypeMappingInfo.  This array will
     * either be populated from the TypeMappingInfo's array of annotations, or based
     * on an xml-element if present.  The xml-element will take precedence over
     * the annotation array; if there is an xml-element the Array of Annotations
     * will be ignored.
     * 
     * @param tmInfo
     * @return
     */
    private java.lang.annotation.Annotation[] getAnnotations(TypeMappingInfo tmInfo) {
        if (tmInfo.getXmlElement() != null) {
            ClassLoader loader = helper.getClassLoader();
            // create a single ConversionManager for that will be shared by the proxy objects
            ConversionManager cMgr = new ConversionManager();
            cMgr.setLoader(loader);

            // unmarshal the node into an XmlElement
            org.eclipse.persistence.jaxb.xmlmodel.XmlElement xElt = (org.eclipse.persistence.jaxb.xmlmodel.XmlElement) CompilerHelper.getXmlElement(tmInfo.getXmlElement(), loader);
            List annotations = new ArrayList();
            // where applicable, a given dynamic proxy will contain a Map of method name/return value entries
            Map<String, Object> components = null;
            // handle @XmlElement: set 'type' method
            if (!(xElt.getType().equals("javax.xml.bind.annotation.XmlElement.DEFAULT"))) {
                components = new HashMap();
                components.put(TYPE_METHOD_NAME, xElt.getType());
                annotations.add(AnnotationProxy.getProxy(components, XmlElement.class, loader, cMgr));
            }
            // handle @XmlList
            if (xElt.isXmlList()) {
                annotations.add(AnnotationProxy.getProxy(components, XmlList.class, loader, cMgr));
            }
            // handle @XmlAttachmentRef
            if (xElt.isXmlAttachmentRef()) {
                annotations.add(AnnotationProxy.getProxy(components, XmlAttachmentRef.class, loader, cMgr));
            }
            // handle @XmlMimeType: set 'value' method
            if (xElt.getXmlMimeType() != null) {
                components = new HashMap();
                components.put(VALUE_METHOD_NAME, xElt.getXmlMimeType());
                annotations.add(AnnotationProxy.getProxy(components, XmlMimeType.class, loader, cMgr));
            }
            // handle @XmlJavaTypeAdapter: set 'type' and 'value' methods
            if (xElt.getXmlJavaTypeAdapter() != null) {
                components = new HashMap();
                components.put(TYPE_METHOD_NAME, xElt.getXmlJavaTypeAdapter().getType());
                components.put(VALUE_METHOD_NAME, xElt.getXmlJavaTypeAdapter().getValue());
                annotations.add(AnnotationProxy.getProxy(components, XmlJavaTypeAdapter.class, loader, cMgr));
            }
            // return the newly created array of dynamic proxy objects
            return (java.lang.annotation.Annotation[]) annotations.toArray(new java.lang.annotation.Annotation[annotations.size()]);
        }
        // no xml-element set on the info, (i.e. no xml overrides) so return the array of Annotation objects  
        return tmInfo.getAnnotations();
    }
    
    /**
     * Initialize maps, lists, etc. Typically called prior to processing a set of 
     * classes via preBuildTypeInfo, postBuildTypeInfo, processJavaClasses.
     */
    void init(JavaClass[] classes, TypeMappingInfo[] typeMappingInfos) {
        typeInfoClasses = new ArrayList<JavaClass>();
        typeInfo = new HashMap<String, TypeInfo>();
        typeQNames = new ArrayList<QName>();
        userDefinedSchemaTypes = new HashMap<String, QName>();
        if (packageToNamespaceMappings == null) {
            packageToNamespaceMappings = new HashMap<String, NamespaceInfo>();
        }
        this.factoryMethods = new HashMap<String, JavaMethod>();
        this.xmlRegistries = new HashMap<String, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry>();
        this.namespaceResolver = new NamespaceResolver();
        this.xmlRootElements = new HashMap<String, ElementDeclaration>();

        arrayClassesToGeneratedClasses = new HashMap<String, Class>();
        collectionClassesToGeneratedClasses = new HashMap<java.lang.reflect.Type, Class>();
        generatedClassesToArrayClasses = new HashMap<Class, JavaClass>();
        generatedClassesToCollectionClasses = new HashMap<Class, java.lang.reflect.Type>();
        typeMappingInfoToGeneratedClasses = new HashMap<TypeMappingInfo, Class>();
        typeMappingInfoToSchemaType = new HashMap<TypeMappingInfo, QName>();
        elementDeclarations = new HashMap<String, HashMap<QName, ElementDeclaration>>();
        HashMap globalElements = new HashMap<QName, ElementDeclaration>();
        elementDeclarations.put(XmlElementDecl.GLOBAL.class.getName(), globalElements);
        localElements = new ArrayList<ElementDeclaration>();

        javaClassToTypeMappingInfos = new HashMap<JavaClass, TypeMappingInfo>();
        if (typeMappingInfos != null) {
            for (int i = 0; i < typeMappingInfos.length; i++) {
                javaClassToTypeMappingInfos.put(classes[i], typeMappingInfos[i]);
            }
        }
        typeMappingInfoToAdapterClasses = new HashMap<TypeMappingInfo, Class>();
        if (typeMappingInfos != null) {
            for(TypeMappingInfo next:typeMappingInfos) {
                java.lang.annotation.Annotation[] annotations = getAnnotations(next);
                if(annotations != null) {
                    for(java.lang.annotation.Annotation nextAnnotation:annotations) {
                        if(nextAnnotation instanceof XmlJavaTypeAdapter) {
                            typeMappingInfoToAdapterClasses.put(next, ((XmlJavaTypeAdapter)nextAnnotation).value());
                        }
                    }
                }
            }
        }
    }

    /**
     * Process class level annotations only. It is assumed that a call to init() 
     * has been made prior to calling this method. After the types created via 
     * this method have been modified (if necessary) postBuildTypeInfo and 
     * processJavaClasses should be called to finish processing.
     * 
     * @param javaClasses
     * @return
     */
    public Map<String, TypeInfo> preBuildTypeInfo(JavaClass[] javaClasses) {
        for (JavaClass javaClass : javaClasses) {
            if (javaClass == null || !shouldGenerateTypeInfo(javaClass) || isXmlRegistry(javaClass) || javaClass.isArray()) {
                continue;
            }

            TypeInfo info = typeInfo.get(javaClass.getQualifiedName());
            if (info != null) {
                if (info.isPreBuilt()) {
                    continue;
                }
            }

            if (javaClass.isEnum()) {
                info = new EnumTypeInfo(helper);
            } else {
                info = new TypeInfo(helper);
            }
            info.setJavaClassName(javaClass.getQualifiedName());
            info.setPreBuilt(true);

            // handle @XmlTransient
            if (helper.isAnnotationPresent(javaClass, XmlTransient.class)) {
                info.setXmlTransient(true);
            }

            // handle @XmlInlineBinaryData
            if (helper.isAnnotationPresent(javaClass, XmlInlineBinaryData.class)) {
                info.setInlineBinaryData(true);
            }
            
            // handle @XmlRootElement
            processXmlRootElement(javaClass, info);

            // handle @XmlSeeAlso
            processXmlSeeAlso(javaClass, info);

            NamespaceInfo packageNamespace = getNamespaceInfoForPackage(javaClass);

            // handle @XmlType
            preProcessXmlType(javaClass, info, packageNamespace);

            // handle @XmlAccessorType
            preProcessXmlAccessorType(javaClass, info, packageNamespace);

            // handle @XmlAccessorOrder
            preProcessXmlAccessorOrder(javaClass, info, packageNamespace);

            // handle package level @XmlJavaTypeAdapters
            processPackageLevelAdapters(javaClass, info);

            // handle class level @XmlJavaTypeAdapters
            processClassLevelAdapters(javaClass, info);

            // handle descriptor customizer
            preProcessCustomizer(javaClass, info);

            // handle package level @XmlSchemaType(s)
            processSchemaTypes(javaClass, info);
            
            // handle class extractor
            if(helper.isAnnotationPresent(javaClass, XmlClassExtractor.class)) {
                XmlClassExtractor classExtractor = (XmlClassExtractor)helper.getAnnotation(javaClass, XmlClassExtractor.class);
                info.setClassExtractorName(classExtractor.value().getName());
            }
            
            // handle user properties
            if(helper.isAnnotationPresent(javaClass, XmlProperties.class)) {
                XmlProperties xmlProperties = (XmlProperties)helper.getAnnotation(javaClass, XmlProperties.class);
                Map<Object, Object> propertiesMap = createUserPropertiesMap(xmlProperties.value());
                info.setUserProperties(propertiesMap);
            } else if(helper.isAnnotationPresent(javaClass, XmlProperty.class)) {
                XmlProperty xmlProperty = (XmlProperty)helper.getAnnotation(javaClass, XmlProperty.class);
                Map<Object, Object> propertiesMap = createUserPropertiesMap(new XmlProperty[]{xmlProperty});
                info.setUserProperties(propertiesMap);
            }
            
            // handle class indicator field name
            if (helper.isAnnotationPresent(javaClass, XmlDiscriminatorNode.class)) {
                XmlDiscriminatorNode xmlDiscriminatorNode = (XmlDiscriminatorNode) helper.getAnnotation(javaClass, XmlDiscriminatorNode.class);
                info.setXmlDiscriminatorNode(xmlDiscriminatorNode.value());
            }
            // handle class indicator
            if (helper.isAnnotationPresent(javaClass, XmlDiscriminatorValue.class)) {
                XmlDiscriminatorValue xmlDiscriminatorValue = (XmlDiscriminatorValue) helper.getAnnotation(javaClass, XmlDiscriminatorValue.class);
                info.setXmlDiscriminatorValue(xmlDiscriminatorValue.value());
            }
            
            typeInfoClasses.add(javaClass);
            typeInfo.put(info.getJavaClassName(), info);
        }
        return typeInfo;
    }

    /**
     * Process any additional classes (i.e. inner classes, @XmlSeeAlso, @XmlRegisrty, etc.) 
     * for a given set of JavaClasses, then complete building all of the required TypeInfo 
     * objects. This method is typically called after init and preBuildTypeInfo have been 
     * called.
     * 
     * @param javaClasses
     * @return updated array of JavaClasses, made up of the original classes plus any additional ones
     */
    public JavaClass[] postBuildTypeInfo(JavaClass[] javaClasses) {
        if (javaClasses.length == 0) {
            return javaClasses;
        }
        // create type info instances for any additional classes
        javaClasses = processAdditionalClasses(javaClasses);
        preBuildTypeInfo(javaClasses);
        updateGlobalElements(javaClasses);
        buildTypeInfo(javaClasses);
        return javaClasses;
    }

    /**
     * INTERNAL:
     * 
     * Complete building TypeInfo objects for a given set of JavaClass instances. This method assumes 
     * that init, preBuildTypeInfo, and postBuildTypeInfo have been called.
     * 
     * @param allClasses
     * @return
     */
    private Map<String, TypeInfo> buildTypeInfo(JavaClass[] allClasses) {
        for (JavaClass javaClass : allClasses) {
            if (javaClass == null) {
                continue;
            }

            TypeInfo info = typeInfo.get(javaClass.getQualifiedName());
            if (info == null || info.isPostBuilt()) {
                continue;
            }
            info.setPostBuilt(true);

            // handle factory methods
            processFactoryMethods(javaClass, info);

            NamespaceInfo packageNamespace = getNamespaceInfoForPackage(javaClass);

            // handle @XmlAccessorType
            postProcessXmlAccessorType(info, packageNamespace);

            // handle @XmlType
            postProcessXmlType(javaClass, info, packageNamespace);

            // handle @XmlEnum
            if (info.isEnumerationType()) {
                addEnumTypeInfo(javaClass, ((EnumTypeInfo) info));
                continue;
            }

            // process schema type name
            processTypeQName(javaClass, info, packageNamespace);

            // handle superclass if necessary
            JavaClass superClass = (JavaClass) javaClass.getSuperclass();
            if (shouldGenerateTypeInfo(superClass)) {
                JavaClass[] jClassArray = new JavaClass[] { superClass };
                buildNewTypeInfo(jClassArray);
            }

            // add properties
            info.setProperties(getPropertiesForClass(javaClass, info));

            // process properties
            processTypeInfoProperties(javaClass, info);

            // handle @XmlAccessorOrder
            postProcessXmlAccessorOrder(info, packageNamespace);

            // Make sure this class has a factory method or a zero arg constructor
            if(!javaClass.isInterface()) {
                if (info.getFactoryMethodName() == null && info.getObjectFactoryClassName() == null) {
                    JavaConstructor zeroArgConstructor = javaClass.getDeclaredConstructor(new JavaClass[] {});
                    if (zeroArgConstructor == null) {
                        throw org.eclipse.persistence.exceptions.JAXBException.factoryMethodOrConstructorRequired(javaClass.getName());
                    }
                }
            }

            validatePropOrderForInfo(info);
        }
        return typeInfo;
    }

    /**
     * Perform any final generation and/or validation operations on TypeInfo 
     * properties.
     * 
     */
    public void finalizeProperties() {
        ArrayList<JavaClass> jClasses = getTypeInfoClasses();
        for (JavaClass jClass : jClasses) {
            TypeInfo tInfo = getTypeInfo().get(jClass.getQualifiedName());
            // don't need to validate props on a transient class at this point
            if (tInfo.isTransient()) { 
                continue;
            }
            // validate XmlValue
            if (tInfo.getXmlValueProperty() != null) {
                validateXmlValueFieldOrProperty(jClass, tInfo.getXmlValueProperty());
            }
            for (Property property : tInfo.getPropertyList()) {
                // need to check for transient reference class
                JavaClass typeClass = property.getActualType();
                TypeInfo targetInfo = typeInfo.get(typeClass.getQualifiedName());
                if (targetInfo != null && targetInfo.isTransient()) {
                    throw JAXBException.invalidReferenceToTransientClass(jClass.getQualifiedName(), property.getPropertyName(), typeClass.getQualifiedName());
                }
                // only one XmlValue is allowed per class, and if there is one only XmlAttributes are allowed
                if (tInfo.isSetXmlValueProperty()) {
                    if (property.isXmlValue() && !(tInfo.getXmlValueProperty().getPropertyName().equals(property.getPropertyName()))) {
                        throw JAXBException.xmlValueAlreadySet(property.getPropertyName(), tInfo.getXmlValueProperty().getPropertyName(), jClass.getName());
                    }
                    if (!property.isXmlValue() && !property.isAttribute() && !property.isInverseReference() && !property.isTransient()) {
                        throw JAXBException.propertyOrFieldShouldBeAnAttribute(property.getPropertyName());
                    }
                }
                // if the property is an XmlIDREF, the target must have an XmlID set
                if (property.isXmlIdRef()) {
                    if (targetInfo != null && targetInfo.getIDProperty() == null) {
                        throw JAXBException.invalidIdRef(property.getPropertyName(), typeClass.getQualifiedName());
                    }
                }
                // there can only be one XmlID per type info 
                if (property.isXmlId() && tInfo.getIDProperty() != null && !(tInfo.getIDProperty().getPropertyName().equals(property.getPropertyName()))) {
                    throw JAXBException.idAlreadySet(property.getPropertyName(), tInfo.getIDProperty().getPropertyName(), jClass.getName());
                }
                // there can only be one XmlAnyAttribute per type info
                if (property.isAnyAttribute() && tInfo.isSetAnyAttributePropertyName() && !(tInfo.getAnyAttributePropertyName().equals(property.getPropertyName()))) {
                    throw JAXBException.multipleAnyAttributeMapping(jClass.getName());
                }
                // there can only be one XmlAnyElement per type info
                if (property.isAny() && tInfo.isSetAnyElementPropertyName() && !(tInfo.getAnyElementPropertyName().equals(property.getPropertyName()))) {
                    throw JAXBException.xmlAnyElementAlreadySet(property.getPropertyName(), tInfo.getAnyElementPropertyName(), jClass.getName());
                }
                // an XmlAttachmentRef can only appear on a DataHandler property
                if (property.isSwaAttachmentRef() && !areEquals(property.getActualType(), JAVAX_ACTIVATION_DATAHANDLER)) {
                    throw JAXBException.invalidAttributeRef(property.getPropertyName(), jClass.getQualifiedName());
                }
                // an XmlElementWrapper can only appear on a Collection or Array
                if (property.getXmlElementWrapper() != null) {
                    if (!isCollectionType(property) && !property.getType().isArray()) {
                        throw JAXBException.invalidElementWrapper(property.getPropertyName());
                    }
                }
                // handle XmlElements - validate and build the required properties
                if (property.isChoice()) {
                    processChoiceProperty(property, tInfo, jClass, property.getActualType());
                }
                // handle XmlElementRef(s) - validate and build the required ElementDeclaration object
                if (property.isReference()) {
                    processReferenceProperty(property, tInfo, jClass);
                }
                // handle XmlTransformation - validate transformer class/method
                if (property.isXmlTransformation()) {
                    processXmlTransformationProperty(property);
                }
                // validate XmlJoinNodes
                if (property.isSetXmlJoinNodes()) {
                    // the target class must have an associated TypeInfo
                    if (targetInfo == null) {
                        throw JAXBException.invalidXmlJoinNodeReferencedClass(property.getPropertyName(), property.getActualType().getQualifiedName());
                    }
                    // validate each referencedXmlPath - target TypeInfo should have XmlID/XmlKey property with matching XmlPath
                    if (targetInfo.getIDProperty() == null && targetInfo.getXmlKeyProperties() == null) {
                        throw JAXBException.noKeyOrIDPropertyOnJoinTarget(jClass.getQualifiedName(), property.getPropertyName(), property.getActualType().getQualifiedName());
                    }
                    for (org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode xmlJoinNode: property.getXmlJoinNodes().getXmlJoinNode()) {
                        String refXPath = xmlJoinNode.getReferencedXmlPath();
                        if (targetInfo.getIDProperty() != null && refXPath.equals(targetInfo.getIDProperty().getXmlPath())) {
                            continue;
                        }
                        boolean matched = false; 
                        for (Property xmlkeyProperty : targetInfo.getXmlKeyProperties()) {
                            if (refXPath.equals(xmlkeyProperty.getXmlPath())) {
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) {
                            throw JAXBException.invalidReferencedXmlPathOnJoin(jClass.getQualifiedName(), property.getPropertyName(), property.getActualType().getQualifiedName(), refXPath);
                        }
                    }
                }
            }
        }
    }

    /**
     * Process a given TypeInfo instance's properties.
     * 
     * @param info
     */
    private void processTypeInfoProperties(JavaClass javaClass, TypeInfo info) {
        ArrayList<Property> properties = info.getPropertyList();
        for (Property property : properties) {
            // handle @XmlID
            processXmlID(property, javaClass, info);

            // handle @XmlIDREF - validate these properties after processing of all types is completed
            processXmlIDREF(property);

            JavaClass propertyType = property.getActualType();

            if (shouldGenerateTypeInfo(propertyType)) {
                JavaClass[] jClassArray = new JavaClass[] { propertyType };
                buildNewTypeInfo(jClassArray);
            }
            
            if(property.isMap()) {
                JavaClass keyType = property.getKeyType();
                if(shouldGenerateTypeInfo(keyType)) {
                    JavaClass[] jClassArray = new JavaClass[] { keyType };
                    buildNewTypeInfo(jClassArray);
                }
                
                JavaClass valueType = property.getValueType();
                if(shouldGenerateTypeInfo(valueType)) {
                    JavaClass[] jClassArray = new JavaClass[] { valueType };
                    buildNewTypeInfo(jClassArray);
                }
            }
        }
    }

    /**
     * This method was initially designed to handle processing one or more
     * JavaClass instances.  Over time its functionality has been broken
     * apart and handled in different methods. Its sole purpose now is to
     * check for callback methods. 
     * 
     * @param classes this paramater can and should be null as it is not 
     * used
     */
    public void processJavaClasses(JavaClass[] classes) {
        checkForCallbackMethods();
    }

    /**
     * Process any additional classes, such as inner classes, @XmlRegistry or from @XmlSeeAlso.
     * 
     * @param classes
     * @return
     */
    private JavaClass[] processAdditionalClasses(JavaClass[] classes) {
        ArrayList<JavaClass> extraClasses = new ArrayList<JavaClass>();
        ArrayList<JavaClass> classesToProcess = new ArrayList<JavaClass>();
        for (JavaClass jClass : classes) {
            Class xmlElementType = null;
            JavaClass javaClass = jClass;
            TypeMappingInfo tmi = javaClassToTypeMappingInfos.get(javaClass);
            if (tmi != null) {
                Class adapterClass = this.typeMappingInfoToAdapterClasses.get(tmi);
                if(adapterClass != null) {
                    JavaClass adapterJavaClass = helper.getJavaClass(adapterClass);
                    JavaClass newType  = helper.getJavaClass(Object.class);
                    
                    // look for marshal method
                    for (Object nextMethod:adapterJavaClass.getDeclaredMethods()) {
                        JavaMethod method = (JavaMethod)nextMethod;
                        if (method.getName().equals("marshal")) {
                            JavaClass returnType = method.getReturnType();              
                            if(!returnType.getQualifiedName().equals(newType.getQualifiedName())) {
                                newType = (JavaClass) returnType;
                                break;
                            }
                        }
                    }
                    javaClass = newType;
                }
                java.lang.annotation.Annotation[] annotations = getAnnotations(tmi);
                if (annotations != null) {
                    for (int j = 0; j < annotations.length; j++) {
                        java.lang.annotation.Annotation nextAnnotation = annotations[j];

                        if (nextAnnotation != null) {
                            if (nextAnnotation instanceof XmlElement) {
                                XmlElement javaAnnotation = (XmlElement) nextAnnotation;
                                if (javaAnnotation.type() != XmlElement.DEFAULT.class) {
                                    xmlElementType = javaAnnotation.type();
                                }
                            }
                        }
                    }
                }
            }

            if (areEquals(javaClass, byte[].class) || areEquals(javaClass, Byte[].class) || areEquals(javaClass, JAVAX_ACTIVATION_DATAHANDLER) || areEquals(javaClass, Source.class) || areEquals(javaClass, Image.class) || areEquals(javaClass, JAVAX_MAIL_INTERNET_MIMEMULTIPART)) {
                if (tmi == null || tmi.getXmlTagName() == null) {
                    ElementDeclaration declaration = new ElementDeclaration(null, javaClass, javaClass.getQualifiedName(), false, XmlElementDecl.GLOBAL.class);
                    declaration.setTypeMappingInfo(tmi);
                    getGlobalElements().put(null, declaration);
                }
            } else if (javaClass.isArray()) {
                if (!helper.isBuiltInJavaType(javaClass.getComponentType())) {
                    extraClasses.add(javaClass.getComponentType());
                }
               Class generatedClass;
               if(null == tmi) {
                   generatedClass = arrayClassesToGeneratedClasses.get(javaClass.getName());
               } else {
                   generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses,  helper.getClassLoader());
               }
                if(generatedClass == null) {
                    generatedClass = generateWrapperForArrayClass(javaClass, tmi, xmlElementType);
                    extraClasses.add(helper.getJavaClass(generatedClass));
                    arrayClassesToGeneratedClasses.put(javaClass.getName(), generatedClass);
                }
                generatedClassesToArrayClasses.put(generatedClass, javaClass);
                typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);

            } else if (isCollectionType(javaClass)) {
                JavaClass componentClass;
                if (javaClass.hasActualTypeArguments()) {
                    componentClass = (JavaClass) javaClass.getActualTypeArguments().toArray()[0];
                    if (!componentClass.isPrimitive()) {
                        extraClasses.add(componentClass);
                    }
                } else {
                    componentClass = helper.getJavaClass(Object.class);
                }

                Class generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses, helper.getClassLoader());                
                if(generatedClass == null){                      
                    generatedClass = generateCollectionValue(javaClass, tmi, xmlElementType);
                    extraClasses.add(helper.getJavaClass(generatedClass));
                }
                typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);
            } else if (isMapType(javaClass)) {
                JavaClass keyClass;
                JavaClass valueClass;
                if (javaClass.hasActualTypeArguments()) {
                    keyClass = (JavaClass) javaClass.getActualTypeArguments().toArray()[0];
                    if (!helper.isBuiltInJavaType(keyClass)) {
                        extraClasses.add(keyClass);
                    }
                    valueClass = (JavaClass) javaClass.getActualTypeArguments().toArray()[1];
                    if (!helper.isBuiltInJavaType(valueClass)) {
                        extraClasses.add(valueClass);
                    }
                } else {
                    keyClass = helper.getJavaClass(Object.class);
                    valueClass = helper.getJavaClass(Object.class);
                }

                Class generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses,  helper.getClassLoader());                
                if(generatedClass == null){              
                    generatedClass = generateWrapperForMapClass(javaClass, keyClass, valueClass, tmi);
                    extraClasses.add(helper.getJavaClass(generatedClass));
                }
                typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);
            } else {
                // process @XmlRegistry, @XmlSeeAlso and inner classes
                processClass(javaClass, classesToProcess);
            }
        }
        // process @XmlRegistry, @XmlSeeAlso and inner classes
        for (JavaClass javaClass : extraClasses) {
            processClass(javaClass, classesToProcess);
        }

        return classesToProcess.toArray(new JavaClass[classesToProcess.size()]);
    }

    /**
     * Adds additional classes to the given List, from inner classes, 
     * @XmlRegistry or @XmlSeeAlso.
     * 
     * @param javaClass
     * @param classesToProcess
     */
    private void processClass(JavaClass javaClass, ArrayList<JavaClass> classesToProcess) {
        if (shouldGenerateTypeInfo(javaClass)) {
            if (isXmlRegistry(javaClass)) {
                this.processObjectFactory(javaClass, classesToProcess);
            } else {
                classesToProcess.add(javaClass);
                // handle @XmlSeeAlso
                TypeInfo info = typeInfo.get(javaClass.getQualifiedName());
                if (info != null && info.isSetXmlSeeAlso()) {
                    for (String jClassName : info.getXmlSeeAlso()) {
                        classesToProcess.add(helper.getJavaClass(jClassName));
                    }
                }
                // handle inner classes
                for (Iterator<JavaClass> jClassIt = javaClass.getDeclaredClasses().iterator(); jClassIt.hasNext();) {
                    JavaClass innerClass = jClassIt.next();
                    if (shouldGenerateTypeInfo(innerClass)) {
                        TypeInfo tInfo = typeInfo.get(innerClass.getQualifiedName());
                        if ((tInfo != null && !tInfo.isTransient()) || !helper.isAnnotationPresent(innerClass, XmlTransient.class)) {
                            classesToProcess.add(innerClass);
                        }
                    }
                }
            }
        }
    }

    /**
     * Process an @XmlSeeAlso annotation. TypeInfo instances will be created for each class listed.
     * 
     * @param javaClass
     */
    private void processXmlSeeAlso(JavaClass javaClass, TypeInfo info) {
        // reflectively load @XmlSeeAlso class to avoid dependency
        Class xmlSeeAlsoClass = null;
        Method valueMethod = null;
        try {
            xmlSeeAlsoClass = PrivilegedAccessHelper.getClassForName("javax.xml.bind.annotation.XmlSeeAlso");
            valueMethod = PrivilegedAccessHelper.getDeclaredMethod(xmlSeeAlsoClass, "value", new Class[] {});
        } catch (ClassNotFoundException ex) {
            // Ignore this exception. If SeeAlso isn't available, don't try to process
        } catch (NoSuchMethodException ex) {
        }
        if (xmlSeeAlsoClass != null && helper.isAnnotationPresent(javaClass, xmlSeeAlsoClass)) {
            Object seeAlso = helper.getAnnotation(javaClass, xmlSeeAlsoClass);
            Class[] values = null;
            try {
                values = (Class[]) PrivilegedAccessHelper.invokeMethod(valueMethod, seeAlso, new Object[] {});
            } catch (Exception ex) {
            }
            List<String> seeAlsoClassNames = new ArrayList<String>();
            for (Class next : values) {
                seeAlsoClassNames.add(next.getName());
            }
            info.setXmlSeeAlso(seeAlsoClassNames);
        }
    }

    /**
     * Process any factory methods.
     * 
     * @param javaClass
     * @param info
     */
    private void processFactoryMethods(JavaClass javaClass, TypeInfo info) {
        JavaMethod factoryMethod = this.factoryMethods.get(javaClass.getRawName());
        if (factoryMethod != null) {
            // set up factory method info for mappings.
            info.setFactoryMethodName(factoryMethod.getName());
            info.setObjectFactoryClassName(factoryMethod.getOwningClass().getRawName());
            JavaClass[] paramTypes = factoryMethod.getParameterTypes();
            if (paramTypes != null && paramTypes.length > 0) {
                String[] paramTypeNames = new String[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    paramTypeNames[i] = paramTypes[i].getRawName();
                }
                info.setFactoryMethodParamTypes(paramTypeNames);
            }
        }
    }

    /**
     * Process any package-level @XmlJavaTypeAdapters.
     * 
     * @param javaClass
     * @param info
     */
    private void processPackageLevelAdapters(JavaClass javaClass, TypeInfo info) {
        JavaPackage pack = javaClass.getPackage();
        if (helper.isAnnotationPresent(pack, XmlJavaTypeAdapters.class)) {
            XmlJavaTypeAdapters adapters = (XmlJavaTypeAdapters) helper.getAnnotation(pack, XmlJavaTypeAdapters.class);
            XmlJavaTypeAdapter[] adapterArray = adapters.value();
            for (XmlJavaTypeAdapter next : adapterArray) {
                processPackageLevelAdapter(next, info);
            }
        }

        if (helper.isAnnotationPresent(pack, XmlJavaTypeAdapter.class)) {
            XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(pack, XmlJavaTypeAdapter.class);
            processPackageLevelAdapter(adapter, info);
        }
    }

    private void processPackageLevelAdapter(XmlJavaTypeAdapter next, TypeInfo info) {
        JavaClass adapterClass = helper.getJavaClass(next.value());
        JavaClass boundType = helper.getJavaClass(next.type());
        if (boundType != null) {
            info.addPackageLevelAdapterClass(adapterClass, boundType);
        } else {
            getLogger().logWarning(JAXBMetadataLogger.INVALID_BOUND_TYPE, new Object[] { boundType, adapterClass });
        }
    }

    /**
     * Process any class-level @XmlJavaTypeAdapters.
     * 
     * @param javaClass
     * @param info
     */
    private void processClassLevelAdapters(JavaClass javaClass, TypeInfo info) {
        if (helper.isAnnotationPresent(javaClass, XmlJavaTypeAdapter.class)) {
            XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(javaClass, XmlJavaTypeAdapter.class);
            String boundType = adapter.type().getName();

            if (boundType == null || boundType.equals("javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT")) {
                boundType = javaClass.getRawName();
            }
            org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
            xja.setValue(adapter.value().getName());
            xja.setType(boundType);

            info.setXmlJavaTypeAdapter(xja);
        }
    }

    /**
     * Process any @XmlSchemaType(s).
     * 
     * @param javaClass
     * @param info
     */
    private void processSchemaTypes(JavaClass javaClass, TypeInfo info) {
        JavaPackage pack = javaClass.getPackage();
        if (helper.isAnnotationPresent(pack, XmlSchemaTypes.class)) {
            XmlSchemaTypes types = (XmlSchemaTypes) helper.getAnnotation(pack, XmlSchemaTypes.class);
            XmlSchemaType[] typeArray = types.value();
            for (XmlSchemaType next : typeArray) {
                processSchemaType(next);
            }
        } else if (helper.isAnnotationPresent(pack, XmlSchemaType.class)) {
            processSchemaType((XmlSchemaType) helper.getAnnotation(pack, XmlSchemaType.class));
        }
    }

    /**
     * Process @XmlRootElement annotation on a given JavaClass.
     * 
     * @param javaClass
     * @param info
     */
    private void processXmlRootElement(JavaClass javaClass, TypeInfo info) {
        if (helper.isAnnotationPresent(javaClass, XmlRootElement.class)) {
            XmlRootElement rootElemAnnotation = (XmlRootElement) helper.getAnnotation(javaClass, XmlRootElement.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement xmlRE = new org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement();
            xmlRE.setName(rootElemAnnotation.name());
            xmlRE.setNamespace(rootElemAnnotation.namespace());
            info.setXmlRootElement(xmlRE);
        }
    }

    /**
     * Process @XmlType annotation on a given JavaClass and update the TypeInfo for pre-processing.
     * Note that if no @XmlType annotation is present we still create a new XmlType an set it on 
     * the TypeInfo.
     * 
     * @param javaClass
     * @param info
     * @param packageNamespace
     */
    private void preProcessXmlType(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        org.eclipse.persistence.jaxb.xmlmodel.XmlType xmlType = new org.eclipse.persistence.jaxb.xmlmodel.XmlType();
        if (helper.isAnnotationPresent(javaClass, XmlType.class)) {
            XmlType typeAnnotation = (XmlType) helper.getAnnotation(javaClass, XmlType.class);
            // set name
            xmlType.setName(typeAnnotation.name());
            // set namespace
            xmlType.setNamespace(typeAnnotation.namespace());
            // set propOrder
            String[] propOrder = typeAnnotation.propOrder();
            // handle case where propOrder is an empty array
            if (propOrder != null) {
                xmlType.getPropOrder();
            }
            for (String prop : propOrder) {
                xmlType.getPropOrder().add(prop);
            }
            // set factoryClass
            Class factoryClass = typeAnnotation.factoryClass();
            if (factoryClass == DEFAULT.class) {
                xmlType.setFactoryClass("javax.xml.bind.annotation.XmlType.DEFAULT");
            } else {
                xmlType.setFactoryClass(factoryClass.getCanonicalName());
            }
            // set factoryMethodName
            xmlType.setFactoryMethod(typeAnnotation.factoryMethod());
        } else {
            // set defaults
            xmlType.setName(getSchemaTypeNameForClassName(javaClass.getName()));
            xmlType.setNamespace(packageNamespace.getNamespace());
        }
        info.setXmlType(xmlType);
    }

    /**
     * Process XmlType for a given TypeInfo. Here we assume that the TypeInfo has an XmlType 
     * set - typically via preProcessXmlType or XmlProcessor override.
     * 
     * @param javaClass
     * @param info
     * @param packageNamespace
     */
    private void postProcessXmlType(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        // assumes that the TypeInfo has an XmlType set from
        org.eclipse.persistence.jaxb.xmlmodel.XmlType xmlType = info.getXmlType();

        // set/validate factoryClass and factoryMethod
        String factoryClassName = xmlType.getFactoryClass();
        String factoryMethodName = xmlType.getFactoryMethod();

        if (factoryClassName.equals("javax.xml.bind.annotation.XmlType.DEFAULT")) {
            if (factoryMethodName != null && !factoryMethodName.equals("")) {
                // factory method applies to the current class verify method exists
                JavaMethod method = javaClass.getDeclaredMethod(factoryMethodName, new JavaClass[] {});
                if (method == null) {
                    throw org.eclipse.persistence.exceptions.JAXBException.factoryMethodNotDeclared(factoryMethodName, javaClass.getName());
                }
                info.setObjectFactoryClassName(javaClass.getRawName());
                info.setFactoryMethodName(factoryMethodName);
            }
        } else {
            if (factoryMethodName == null || factoryMethodName.equals("")) {
                throw org.eclipse.persistence.exceptions.JAXBException.factoryClassWithoutFactoryMethod(javaClass.getName());
            }
            info.setObjectFactoryClassName(factoryClassName);
            info.setFactoryMethodName(factoryMethodName);
        }

        // figure out type name
        String typeName = xmlType.getName();
        if (typeName.equals("##default")) {
            typeName = getSchemaTypeNameForClassName(javaClass.getName());
        }
        info.setSchemaTypeName(typeName);

        // set propOrder
        if (xmlType.isSetPropOrder()) {
            List<String> props = xmlType.getPropOrder();
            if (props.size() == 0) {
                info.setPropOrder(new String[0]);
            } else if (props.get(0).equals("")) {
                info.setPropOrder(new String[] { "" });
            } else {
                info.setPropOrder(xmlType.getPropOrder().toArray(new String[xmlType.getPropOrder().size()]));
            }
        }

        // figure out namespace
        if (xmlType.getNamespace().equals("##default")) {
            info.setClassNamespace(packageNamespace.getNamespace());
        } else {
            info.setClassNamespace(xmlType.getNamespace());
        }
    }

    /**
     * Process @XmlAccessorType annotation on a given JavaClass and update the TypeInfo for pre-processing.
     * 
     * @param javaClass
     * @param info
     * @param packageNamespace
     */
    private void preProcessXmlAccessorType(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType xmlAccessType;
        if (helper.isAnnotationPresent(javaClass, XmlAccessorType.class)) {
            XmlAccessorType accessorType = (XmlAccessorType) helper.getAnnotation(javaClass, XmlAccessorType.class);
            xmlAccessType = org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType.fromValue(accessorType.value().name());
            info.setXmlAccessType(xmlAccessType);
        }
    }

    /**
     * Post process XmlAccessorType.  In some cases, such as @XmlSeeAlso classes, the access type
     * may not have been set
     * 
     * @param info
     */
    private void postProcessXmlAccessorType(TypeInfo info, NamespaceInfo packageNamespace) {
        if (!info.isSetXmlAccessType()) {
            // use value in package-info.java as last resort - will default if not set
            info.setXmlAccessType(org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType.fromValue(packageNamespace.getAccessType().name()));
        }
    }

    /**
     * Process package and class @XmlAccessorOrder. Class level annotation overrides a package level annotation.
     * 
     * @param javaClass
     * @param info
     * @param packageNamespace
     */
    private void preProcessXmlAccessorOrder(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        XmlAccessorOrder order = null;
        // class level annotation overrides package level annotation
        if (helper.isAnnotationPresent(javaClass, XmlAccessorOrder.class)) {
            order = (XmlAccessorOrder) helper.getAnnotation(javaClass, XmlAccessorOrder.class);
            info.setXmlAccessOrder(XmlAccessOrder.fromValue(order.value().name()));
        }
    }

    /**
     * Post process XmlAccessorOrder.  This method assumes that the given TypeInfo has 
     * already had its order set (via annotations in preProcessXmlAccessorOrder or
     * via xml metadata override in XMLProcessor).
     * 
     * @param javaClass
     * @param info
     */
    private void postProcessXmlAccessorOrder(TypeInfo info, NamespaceInfo packageNamespace) {
        if (!info.isSetXmlAccessOrder()) {
            // use value in package-info.java as last resort - will default if not set
            info.setXmlAccessOrder(org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder.fromValue(packageNamespace.getAccessOrder().name()));
        }
        info.orderProperties();
    }

    /**
     * Process @XmlElement annotation on a given property.
     * 
     * @param property
     */
    private void processXmlElement(Property property, TypeInfo info) {
        if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
            XmlElement element = (XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class);
            property.setIsRequired(element.required());
            property.setNillable(element.nillable());
            if (element.type() != XmlElement.DEFAULT.class) {
                property.setOriginalType(property.getType());
                if(isCollectionType(property.getType())) {
                    property.setGenericType(helper.getJavaClass(element.type()));
                } else {
                    property.setType(helper.getJavaClass(element.type()));
                }
                property.setHasXmlElementType(true);
            }
            // handle default value
            if (!element.defaultValue().equals("\u0000")) {
                property.setDefaultValue(element.defaultValue());
            }
            validateElementIsInPropOrder(info, property.getPropertyName());
        }
    }

    /**
     * Process @XmlID annotation on a given property
     * 
     * @param property
     * @param info
     */
    private void processXmlID(Property property, JavaClass javaClass, TypeInfo info) {
        if (helper.isAnnotationPresent(property.getElement(), XmlID.class)) {
            property.setIsXmlId(true);
            info.setIDProperty(property);
        }
    }

    /**
     * Process @XmlIDREF on a given property.
     * 
     * @param property
     */
    private void processXmlIDREF(Property property) {
        if (helper.isAnnotationPresent(property.getElement(), XmlIDREF.class)) {
            property.setIsXmlIdRef(true);
        }
    }

    /**
     * Process @XmlJavaTypeAdapter on a given property.
     * 
     * @param property
     * @param propertyType
     * @return if @XmlJavaTypeAdapter exists return property's value type; otherwise propertyType
     */
    private void processXmlJavaTypeAdapter(Property property, TypeInfo info, JavaClass javaClass) {
        JavaClass adapterClass = null;
        JavaClass ptype = property.getActualType();
        if (helper.isAnnotationPresent(property.getElement(), XmlJavaTypeAdapter.class)) {
            XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(property.getElement(), XmlJavaTypeAdapter.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
            xja.setValue(adapter.value().getName());
            xja.setType(adapter.type().getName());
            property.setXmlJavaTypeAdapter(xja);
        } else {
            TypeInfo ptypeInfo = typeInfo.get(ptype.getQualifiedName());
            if (ptypeInfo == null && shouldGenerateTypeInfo(ptype)) {
                JavaClass[] jClassArray = new JavaClass[] { ptype };
                buildNewTypeInfo(jClassArray);
            }
            org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xmlJavaTypeAdapter;
            if (ptypeInfo != null && null != (xmlJavaTypeAdapter = ptypeInfo.getXmlJavaTypeAdapter())) {
                try {
                     property.setXmlJavaTypeAdapter(xmlJavaTypeAdapter);
                } catch(JAXBException e) {
                    String[] messageParams = new String[2];
                    messageParams[0] = xmlJavaTypeAdapter.getValue();
                    messageParams[1] = javaClass.getName();
                    ptypeInfo.setXmlJavaTypeAdapter(null);
                    this.getLogger().logWarning(JAXBMetadataLogger.INVALID_TYPE_LEVEL_XML_JAVA_TYPE_ADAPTER, messageParams);
                }

            } else if (info.getPackageLevelAdaptersByClass().get(ptype.getQualifiedName()) != null) {
                adapterClass = info.getPackageLevelAdapterClass(ptype);

                org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
                xja.setValue(adapterClass.getQualifiedName());
                xja.setType(ptype.getQualifiedName());
                property.setXmlJavaTypeAdapter(xja);
            }

        }
    }

    /**
     * Store a QName (if necessary) based on a given TypeInfo's schema type name.
     * 
     * @param javaClass
     * @param info
     */
    private void processTypeQName(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        String typeName = info.getSchemaTypeName();
        if (typeName != null && !("".equals(typeName))) {
            QName typeQName = new QName(info.getClassNamespace(), typeName);

            boolean containsQName = typeQNames.contains(typeQName);
            if (containsQName) {
                throw JAXBException.nameCollision(typeQName.getNamespaceURI(), typeQName.getLocalPart());
            } else {
                typeQNames.add(typeQName);
            }
        }
    }

    public boolean shouldGenerateTypeInfo(JavaClass javaClass) {
        if (javaClass == null || javaClass.isPrimitive() || javaClass.isAnnotation() || ORG_W3C_DOM.equals(javaClass.getPackageName())) {
            return false;
        }
        if (userDefinedSchemaTypes.get(javaClass.getQualifiedName()) != null) {
            return false;
        }
        if (javaClass.isArray()) {
            String javaClassName = javaClass.getName();
            if(!(javaClassName.equals(ClassConstants.ABYTE.getName()) || javaClassName.equals(ClassConstants.APBYTE.getName()))) {
                return true;
            }
        }
        if (helper.isBuiltInJavaType(javaClass)) {
            return false;
        }
        if (isCollectionType(javaClass) || isMapType(javaClass)) {
            return false;
        }
        return true;
    }

    public ArrayList<Property> getPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> returnList = new ArrayList<Property>();

        if (!info.isTransient()) {
            JavaClass superClass = cls.getSuperclass();
            if(null != superClass) {
                TypeInfo superClassInfo = typeInfo.get(superClass.getQualifiedName());
                while (superClassInfo != null && superClassInfo.isTransient()) {
                    List<Property> superProps = getPublicMemberPropertiesForClass(superClass, superClassInfo);
                    returnList.addAll(0, superProps);
                    superClass = superClass.getSuperclass();
                    superClassInfo = typeInfo.get(superClass.getQualifiedName());
                }
            }
        }

        if (info.isTransient()) {
            returnList.addAll(getNoAccessTypePropertiesForClass(cls, info));
        } else if (info.getXmlAccessType() == XmlAccessType.FIELD) {
            returnList.addAll(getFieldPropertiesForClass(cls, info, false));
        } else if (info.getXmlAccessType() == XmlAccessType.PROPERTY) {
            returnList.addAll(getPropertyPropertiesForClass(cls, info, false));
        } else if (info.getXmlAccessType() == XmlAccessType.PUBLIC_MEMBER) {
            returnList.addAll(getPublicMemberPropertiesForClass(cls, info));
        } else {
            returnList.addAll(getNoAccessTypePropertiesForClass(cls, info));
        }
        return returnList;
    }

    public ArrayList<Property> getFieldPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList<Property> properties = new ArrayList<Property>();
        if (cls == null) {
            return properties;
        }

        for (Iterator<JavaField> fieldIt = cls.getDeclaredFields().iterator(); fieldIt.hasNext();) {
            JavaField nextField = fieldIt.next();
            int modifiers = nextField.getModifiers();
            if (!helper.isAnnotationPresent(nextField, XmlTransient.class)) {
                if (!Modifier.isTransient(modifiers) && ((Modifier.isPublic(nextField.getModifiers()) && onlyPublic) || !onlyPublic)) {
                    if(!Modifier.isStatic(modifiers)){
                            Property property = buildNewProperty(info, cls, nextField, nextField.getName(), nextField.getResolvedType());
                            properties.add(property);	
                    }else if(Modifier.isFinal(modifiers) && helper.isAnnotationPresent(nextField, XmlAttribute.class)){
                        try{
                            Property property = buildNewProperty(info, cls, nextField, nextField.getName(), nextField.getResolvedType());                            	  
                            Object value =  ((JavaFieldImpl)nextField).get(null);
                            String stringValue = (String)XMLConversionManager.getDefaultXMLManager().convertObject(value, String.class, property.getSchemaType());
                            property.setFixedValue(stringValue);
                            properties.add(property);
                        }catch(ClassCastException e){
                           //do Nothing
                        }catch(IllegalAccessException e){
                            //do Nothing
                        }
                    }
                }
            } else {
                // If a property is marked transient ensure it doesn't exist in the propOrder
                List<String> propOrderList = Arrays.asList(info.getPropOrder());
                if (propOrderList.contains(nextField.getName())) {
                    throw JAXBException.transientInProporder(nextField.getName());
                }
            }
        }
        return properties;
    }

    /*
     * Create a new Property Object and process the annotations that are common to fields and methods 
     */
    private Property buildNewProperty(TypeInfo info, JavaClass cls, JavaHasAnnotations javaHasAnnotations, String propertyName, JavaClass ptype) {
        Property property = null;
        if (helper.isAnnotationPresent(javaHasAnnotations, XmlElements.class)) {
            property = buildChoiceProperty(javaHasAnnotations);
        } else if (helper.isAnnotationPresent(javaHasAnnotations, XmlAnyElement.class)) {
            XmlAnyElement anyElement = (XmlAnyElement) helper.getAnnotation(javaHasAnnotations, XmlAnyElement.class);
            property = new Property(helper);
            property.setIsAny(true);
            if (anyElement.value() != null) {
                property.setDomHandlerClassName(anyElement.value().getName());
            }
            property.setLax(anyElement.lax());
            info.setAnyElementPropertyName(propertyName);
        } else if (helper.isAnnotationPresent(javaHasAnnotations, XmlElementRef.class) || helper.isAnnotationPresent(javaHasAnnotations, XmlElementRefs.class)) {
            property = buildReferenceProperty(info, javaHasAnnotations, propertyName, ptype);
        } else if (helper.isAnnotationPresent(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlTransformation.class)) {
            property = buildTransformationProperty(javaHasAnnotations, cls);
        } else {
            property = new Property(helper);
        }
        property.setPropertyName(propertyName);
        property.setElement(javaHasAnnotations);
        
        // if there is a TypeInfo for ptype check it for transient, otherwise check the class
        TypeInfo pTypeInfo = typeInfo.get(ptype.getQualifiedName());
        if ((pTypeInfo != null && !pTypeInfo.isTransient()) || !helper.isAnnotationPresent(ptype, XmlTransient.class)) {
            property.setType(ptype);
        } else {
            JavaClass parent = ptype.getSuperclass();
            while (parent != null) {
                if (parent.getName().equals("java.lang.Object")) {
                    property.setType(parent);
                    break;
                }
                // if there is a TypeInfo for parent check it for transient, otherwise check the class
                TypeInfo parentTypeInfo = typeInfo.get(parent.getQualifiedName());
                if ((parentTypeInfo != null && !parentTypeInfo.isTransient()) || !helper.isAnnotationPresent(parent, XmlTransient.class)) {
                    property.setType(parent);
                    break;
                }
                parent = parent.getSuperclass();
            }
        }

        processPropertyAnnotations(info, cls, javaHasAnnotations, property);

        if (helper.isAnnotationPresent(javaHasAnnotations, XmlPath.class)) {
            XmlPath xmlPath = (XmlPath)helper.getAnnotation(javaHasAnnotations, XmlPath.class);
            property.setXmlPath(xmlPath.value());
            
            // set schema name
            String schemaName = XMLProcessor.getNameFromXPath(xmlPath.value(), property.getPropertyName(), property.isAttribute());
            QName qName;
            NamespaceInfo nsInfo = getNamespaceInfoForPackage(cls);
            if (nsInfo.isElementFormQualified()) {
                qName = new QName(nsInfo.getNamespace(), schemaName);
            } else {
                qName = new QName(schemaName);
            }
            property.setSchemaName(qName);
        } else {
            property.setSchemaName(getQNameForProperty(propertyName, javaHasAnnotations, getNamespaceInfoForPackage(cls), info.getClassNamespace()));
        }
        
        ptype = property.getActualType();
        if (ptype.isPrimitive()) {
            property.setIsRequired(true);
        }

        // apply class level adapters - don't override property level adapter
        if (!property.isSetXmlJavaTypeAdapter()) {
            TypeInfo refClassInfo = getTypeInfo().get(ptype.getQualifiedName());
            if (refClassInfo != null && refClassInfo.isSetXmlJavaTypeAdapter()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xmlJavaTypeAdapter = null;
                try {
                    xmlJavaTypeAdapter = refClassInfo.getXmlJavaTypeAdapter();
                    property.setXmlJavaTypeAdapter(refClassInfo.getXmlJavaTypeAdapter());
                } catch(JAXBException e) {
                    String[] messageParams = new String[2];
                    messageParams[0] = xmlJavaTypeAdapter.getValue();
                    messageParams[1] = cls.getName();
                    property.setXmlJavaTypeAdapter(null);
                    this.getLogger().logWarning(JAXBMetadataLogger.INVALID_TYPE_LEVEL_XML_JAVA_TYPE_ADAPTER, messageParams);
                }
            }
        }

        return property;
    }

    /**
     * Build a new 'choice' property.  Here, we flag a new property as a 'choice' and create/set an
     * XmlModel XmlElements object based on the @XmlElements annotation.
     * 
     * Validation and building of the XmlElement properties will be done during finalizeProperties 
     * in the processChoiceProperty method.
     * 
     * @param javaHasAnnotations
     * @return
     */
    private Property buildChoiceProperty(JavaHasAnnotations javaHasAnnotations) {
        Property choiceProperty = new Property(helper);
        choiceProperty.setChoice(true);
        boolean isIdRef = helper.isAnnotationPresent(javaHasAnnotations, XmlIDREF.class);
        choiceProperty.setIsXmlIdRef(isIdRef);
        // build an XmlElement to set on the Property
        org.eclipse.persistence.jaxb.xmlmodel.XmlElements xmlElements = new org.eclipse.persistence.jaxb.xmlmodel.XmlElements();
        XmlElement[] elements = ((XmlElements) helper.getAnnotation(javaHasAnnotations, XmlElements.class)).value();
        for (int i = 0; i < elements.length; i++) {
            XmlElement next = elements[i];
            org.eclipse.persistence.jaxb.xmlmodel.XmlElement xmlElement = new org.eclipse.persistence.jaxb.xmlmodel.XmlElement();
            xmlElement.setDefaultValue(next.defaultValue());
            xmlElement.setName(next.name());
            xmlElement.setNamespace(next.namespace());
            xmlElement.setNillable(next.nillable());
            xmlElement.setRequired(next.required());
            xmlElement.setType(next.type().getName());
            xmlElements.getXmlElement().add(xmlElement);
        }
        choiceProperty.setXmlElements(xmlElements);
        return choiceProperty;
    }
    
    private Property buildTransformationProperty(JavaHasAnnotations javaHasAnnotations, JavaClass cls) {
        Property property = new Property(helper);
        org.eclipse.persistence.oxm.annotations.XmlTransformation transformationAnnotation = (org.eclipse.persistence.oxm.annotations.XmlTransformation)helper.getAnnotation(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlTransformation.class);
        XmlTransformation transformation = new XmlTransformation();
        transformation.setOptional(transformationAnnotation.optional());
        
        //Read Transformer
        org.eclipse.persistence.oxm.annotations.XmlReadTransformer readTransformer = (org.eclipse.persistence.oxm.annotations.XmlReadTransformer)helper.getAnnotation(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlReadTransformer.class);
        if(readTransformer != null) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer xmlReadTransformer = new org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer();
            if(!(readTransformer.transformerClass() == AttributeTransformer.class)) {
                xmlReadTransformer.setTransformerClass(readTransformer.transformerClass().getName());
            } else if (!(readTransformer.method().equals(""))){
                xmlReadTransformer.setMethod(readTransformer.method());
            }
            transformation.setXmlReadTransformer(xmlReadTransformer);
        }
        
        //Handle Write Transformers
        org.eclipse.persistence.oxm.annotations.XmlWriteTransformer[] transformers = null;
        if(helper.isAnnotationPresent(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlWriteTransformer.class)) {
            org.eclipse.persistence.oxm.annotations.XmlWriteTransformer writeTransformer = (org.eclipse.persistence.oxm.annotations.XmlWriteTransformer)helper.getAnnotation(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlWriteTransformer.class);
            transformers = new org.eclipse.persistence.oxm.annotations.XmlWriteTransformer[]{writeTransformer};
        } else if(helper.isAnnotationPresent(javaHasAnnotations, XmlWriteTransformers.class)) {
            XmlWriteTransformers writeTransformers = (XmlWriteTransformers)helper.getAnnotation(javaHasAnnotations, XmlWriteTransformers.class);
            transformers = writeTransformers.value();
        }
        
        if(transformers != null) {
            for(org.eclipse.persistence.oxm.annotations.XmlWriteTransformer next:transformers) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer xmlWriteTransformer = new org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer();
                if(!(next.transformerClass() == FieldTransformer.class)) {
                    xmlWriteTransformer.setTransformerClass(next.transformerClass().getName());
                } else if (!(next.method().equals(""))){
                    xmlWriteTransformer.setMethod(next.method());
                }
                xmlWriteTransformer.setXmlPath(next.xpath());
                transformation.getXmlWriteTransformer().add(xmlWriteTransformer);
            }
        }
        property.setXmlTransformation(transformation);
        property.setIsXmlTransformation(true);
        return property;
    }

    /**
     * Complete creation of a 'choice' property.  Here, a Property is created for each XmlElement in the 
     * XmlElements list.  Validation is performed as well.  Each created Property is added to the owning
     * Property's list of choice properties.
     *      
     * @param choiceProperty
     * @param info
     * @param cls
     * @param propertyType
     */
    private void processChoiceProperty(Property choiceProperty, TypeInfo info, JavaClass cls, JavaClass propertyType) {
        String propertyName = choiceProperty.getPropertyName();
        validateElementIsInPropOrder(info, propertyName);

        XmlPath[] paths = null;
        if(helper.isAnnotationPresent(choiceProperty.getElement(), XmlPaths.class)) {
            XmlPaths pathAnnotation = (XmlPaths)helper.getAnnotation(choiceProperty.getElement(), XmlPaths.class);
            paths = pathAnnotation.value();
        }
        ArrayList<Property> choiceProperties = new ArrayList<Property>();
        for(int i = 0; i < choiceProperty.getXmlElements().getXmlElement().size(); i++) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlElement next = choiceProperty.getXmlElements().getXmlElement().get(i);
            Property choiceProp = new Property(helper);
            
            String name;
            String namespace;
            
            // handle XmlPath
            // if xml-path is set, we ignore name/namespace
            if(paths != null && next.getXmlPath() == null) {
                //Only set the path, if the path hasn't already been set from xml
                XmlPath nextPath = paths[i];
                next.setXmlPath(nextPath.value());
            }
            if (next.getXmlPath() != null) {
                choiceProp.setXmlPath(next.getXmlPath());
                name = XMLProcessor.getNameFromXPath(next.getXmlPath(), propertyName, false);
                namespace = "##default";
            } else {
                // no xml-path, so use name/namespace from xml-element
                name = next.getName();
                namespace = next.getNamespace();
            }

            if (name == null || name.equals("##default")) {
                if (next.getJavaAttribute() != null) {
                    name = next.getJavaAttribute();
                } else {
                    name = propertyName;
                }
            }

            // if the property has xml-idref, the target type of each xml-element in the list must have an xml-id property
            if (choiceProperty.isXmlIdRef()) {
                TypeInfo tInfo = typeInfo.get(next.getType());
                if (tInfo == null || !tInfo.isIDSet()) {
                    throw JAXBException.invalidXmlElementInXmlElementsList(propertyName, name);
                }
            }

            QName qName = null;
            if (!namespace.equals("##default")) {
                qName = new QName(namespace, name);
            } else {
                NamespaceInfo namespaceInfo = getNamespaceInfoForPackage(cls);
                if (namespaceInfo.isElementFormQualified()) {
                    qName = new QName(namespaceInfo.getNamespace(), name);
                } else {
                    qName = new QName(name);
                }
            }

            choiceProp.setPropertyName(name);

            // figure out the property's type - note that for DEFAULT, if from XML the value will be 
            // "XmlElement.DEFAULT", and from annotations the value will be "XmlElement$DEFAULT"
            if (next.getType().equals("javax.xml.bind.annotation.XmlElement.DEFAULT") ||
                    next.getType().equals("javax.xml.bind.annotation.XmlElement$DEFAULT")) {
                choiceProp.setType(propertyType);
            } else {
                choiceProp.setType(helper.getJavaClass(next.getType()));
            }

            choiceProp.setSchemaName(qName);
            choiceProp.setSchemaType(getSchemaTypeFor(choiceProp.getType()));
            choiceProp.setIsXmlIdRef(choiceProperty.isXmlIdRef());
            choiceProp.setXmlElementWrapper(choiceProperty.getXmlElementWrapper());
            choiceProperties.add(choiceProp);
        }
        choiceProperty.setChoiceProperties(choiceProperties);
    }

    /**
     * Build a reference property.  Here we will build a list of XML model XmlElementRef objects, 
     * based on the @XmlElement(s) annotation, to store on the Property.  Processing of the 
     * elements and validation will be performed during the finalize property phase via the
     * processReferenceProperty method. 
     * 
     * @param info
     * @param javaHasAnnotations
     * @param propertyName
     * @param ptype
     * @return
     */
    private Property buildReferenceProperty(TypeInfo info, JavaHasAnnotations javaHasAnnotations, String propertyName, JavaClass ptype) {
        Property property = new Property(helper);
        property.setType(ptype);

        XmlElementRef[] elementRefs;
        XmlElementRef ref = (XmlElementRef) helper.getAnnotation(javaHasAnnotations, XmlElementRef.class);
        if (ref != null) {
            elementRefs = new XmlElementRef[] { ref };
        } else {
            XmlElementRefs refs = (XmlElementRefs) helper.getAnnotation(javaHasAnnotations, XmlElementRefs.class);
            elementRefs = refs.value();
            info.setElementRefsPropertyName(propertyName);
        }

        List<org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef> eltRefs = new ArrayList<org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef>();
        for (XmlElementRef nextRef : elementRefs) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef eltRef = new org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef();
            eltRef.setName(nextRef.name());
            eltRef.setNamespace(nextRef.namespace());
            eltRef.setType(nextRef.type().getName());
            eltRefs.add(eltRef);
        }

        property.setIsReference(true);
        property.setXmlElementRefs(eltRefs);
        return property;
    }

    /**
     * Build a reference property.
     * 
     * @param property
     * @param info
     * @param javaHasAnnotations
     * @return
     */
    private Property processReferenceProperty(Property property, TypeInfo info, JavaClass cls) {
        String propertyName = property.getPropertyName();
        validateElementIsInPropOrder(info, propertyName);

        for (org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef nextRef : property.getXmlElementRefs()) {
            JavaClass type = property.getType();
            String typeName = type.getQualifiedName();
            if (isCollectionType(property)) {
                if (type.hasActualTypeArguments()) {
                    type = (JavaClass) type.getActualTypeArguments().toArray()[0];
                    typeName = type.getQualifiedName();
                }
            }

            // for DEFAULT, if from XML the type will be "XmlElementRef.DEFAULT", 
            // and from annotations the value will be "XmlElementref$DEFAULT"
            if (!(nextRef.getType().equals("javax.xml.bind.annotation.XmlElementRef.DEFAULT") || nextRef.getType().equals("javax.xml.bind.annotation.XmlElementRef$DEFAULT"))) {
                typeName = nextRef.getType();
            }
            
            ElementDeclaration referencedElement = this.xmlRootElements.get(typeName);
            if (referencedElement != null) {
                addReferencedElement(property, referencedElement);
            } else {
                String name = nextRef.getName();
                String namespace = nextRef.getNamespace();
                if (namespace.equals("##default")) {
                    namespace = "";
                }
                QName qname = new QName(namespace, name);
                JavaClass scopeClass = cls;
                while(!(scopeClass.getName().equals("java.lang.Object"))) {
                    HashMap<QName, ElementDeclaration> elements = getElementDeclarationsForScope(scopeClass.getName()); 
                    if(elements != null) {
                        referencedElement = elements.get(qname);
                    }
                    if(referencedElement != null) {
                        break;
                    }
                    scopeClass = scopeClass.getSuperclass();
                }
                if(referencedElement == null) {
                    referencedElement = this.getGlobalElements().get(qname);
                }
                if (referencedElement != null) {
                    addReferencedElement(property, referencedElement);
                } else {
                    throw org.eclipse.persistence.exceptions.JAXBException.invalidElementRef(property.getPropertyName(), cls.getName());
                }
            }
        }
        return property;
    }

    private void processPropertyAnnotations(TypeInfo info, JavaClass cls, JavaHasAnnotations javaHasAnnotations, Property property) {
        //Check for mixed context
        if (helper.isAnnotationPresent(javaHasAnnotations, XmlMixed.class)) {
            info.setMixed(true);
            property.setMixedContent(true);
        }
        if (helper.isAnnotationPresent(javaHasAnnotations, XmlContainerProperty.class)) {
            XmlContainerProperty container = (XmlContainerProperty) helper.getAnnotation(javaHasAnnotations, XmlContainerProperty.class);
            property.setInverseReferencePropertyName(container.value());
            property.setInverseReferencePropertyGetMethodName(container.getMethodName());
            property.setInverseReferencePropertySetMethodName(container.setMethodName());
        } else if (helper.isAnnotationPresent(javaHasAnnotations, XmlInverseReference.class)) {
            XmlInverseReference inverseReference = (XmlInverseReference) helper.getAnnotation(javaHasAnnotations, XmlInverseReference.class);
            property.setInverseReferencePropertyName(inverseReference.mappedBy());

            TypeInfo targetInfo = this.getTypeInfo().get(property.getActualType().getName());
            if (targetInfo != null && targetInfo.getXmlAccessType() == XmlAccessType.PROPERTY) {
                String propName = property.getPropertyName();
                propName = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
                property.setInverseReferencePropertyGetMethodName("get" + propName);
                property.setInverseReferencePropertySetMethodName("set" + propName);
            }

            property.setInverseReference(true);
        }
        processXmlJavaTypeAdapter(property, info, cls);

        processXmlElement(property, info);

        JavaClass ptype = property.getActualType();

        if (helper.isAnnotationPresent(property.getElement(), XmlAttachmentRef.class) && areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER)) {
            property.setIsSwaAttachmentRef(true);
            property.setSchemaType(XMLConstants.SWA_REF_QNAME);
        } else if (areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER) || areEquals(ptype, byte[].class) || areEquals(ptype, Byte[].class) || areEquals(ptype, Image.class) || areEquals(ptype, Source.class)
                || areEquals(ptype, JAVAX_MAIL_INTERNET_MIMEMULTIPART)) {
            property.setIsMtomAttachment(true);
            property.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlMimeType.class)) {
            property.setMimeType(((XmlMimeType) helper.getAnnotation(property.getElement(), XmlMimeType.class)).value());
        }
        // set indicator for inlining binary data - setting this to true on a non-binary data type won't have any affect 
        if (helper.isAnnotationPresent(property.getElement(), XmlInlineBinaryData.class) || info.isBinaryDataToBeInlined()) {
            property.setisInlineBinaryData(true);
        }

        // Get schema-type info if specified and set it on the property for later use:
        if (helper.isAnnotationPresent(property.getElement(), XmlSchemaType.class)) {
            XmlSchemaType schemaType = (XmlSchemaType) helper.getAnnotation(property.getElement(), XmlSchemaType.class);
            QName schemaTypeQname = new QName(schemaType.namespace(), schemaType.name());
            property.setSchemaType(schemaTypeQname);
        }

        if (helper.isAnnotationPresent(property.getElement(), XmlAttribute.class)) {
            property.setIsAttribute(true);
            property.setIsRequired(((XmlAttribute) helper.getAnnotation(property.getElement(), XmlAttribute.class)).required());
        }

        if (helper.isAnnotationPresent(property.getElement(), XmlAnyAttribute.class)) {
            if (info.isSetAnyAttributePropertyName()) {
                throw org.eclipse.persistence.exceptions.JAXBException.multipleAnyAttributeMapping(cls.getName());
            }
            if (!property.getType().getName().equals("java.util.Map")) {
                throw org.eclipse.persistence.exceptions.JAXBException.anyAttributeOnNonMap(property.getPropertyName());
            }
            property.setIsAnyAttribute(true);
            info.setAnyAttributePropertyName(property.getPropertyName());
        }

        // Make sure XmlElementWrapper annotation is on a collection or array
        if (helper.isAnnotationPresent(property.getElement(), XmlElementWrapper.class)) {
            XmlElementWrapper wrapper = (XmlElementWrapper) helper.getAnnotation(property.getElement(), XmlElementWrapper.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper xmlEltWrapper = new org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper();
            xmlEltWrapper.setName(wrapper.name());
            xmlEltWrapper.setNamespace(wrapper.namespace());
            xmlEltWrapper.setNillable(wrapper.nillable());
            xmlEltWrapper.setRequired(wrapper.required());
            property.setXmlElementWrapper(xmlEltWrapper);
        }

        if (helper.isAnnotationPresent(property.getElement(), XmlList.class)) {
            // Make sure XmlList annotation is on a collection or array
            if (!isCollectionType(property) && !property.getType().isArray()) {
                throw JAXBException.invalidList(property.getPropertyName());
            }
            property.setIsXmlList(true);
        }

        if (helper.isAnnotationPresent(property.getElement(), XmlValue.class)) {
            property.setIsXmlValue(true);
            info.setXmlValueProperty(property);
        }
        
        if (helper.isAnnotationPresent(property.getElement(), XmlReadOnly.class)) {
            property.setReadOnly(true);
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlWriteOnly.class)) {
            property.setWriteOnly(true);
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlCDATA.class)) {
            property.setCdata(true);
        }
        if(helper.isAnnotationPresent(property.getElement(), XmlAccessMethods.class)) {
            XmlAccessMethods accessMethods = (XmlAccessMethods)helper.getAnnotation(property.getElement(), XmlAccessMethods.class);
            if(!(accessMethods.getMethodName().equals(""))) {
                property.setGetMethodName(accessMethods.getMethodName());
            }
            if(!(accessMethods.setMethodName().equals(""))) {
                property.setSetMethodName(accessMethods.setMethodName());
            }
            if(!(property.isMethodProperty())) {
                property.setMethodProperty(true);
            }
        }

        // handle user properties
        if(helper.isAnnotationPresent(property.getElement(), XmlProperties.class)) {
            XmlProperties xmlProperties = (XmlProperties)helper.getAnnotation(property.getElement(), XmlProperties.class);
            Map<Object, Object> propertiesMap = createUserPropertiesMap(xmlProperties.value());
            property.setUserProperties(propertiesMap);
        } else if(helper.isAnnotationPresent(property.getElement(), XmlProperty.class)) {
            XmlProperty xmlProperty = (XmlProperty)helper.getAnnotation(property.getElement(), XmlProperty.class);
            Map<Object, Object> propertiesMap = createUserPropertiesMap(new XmlProperty[]{xmlProperty});
            property.setUserProperties(propertiesMap);
        }
        // handle XmlKey
        if (helper.isAnnotationPresent(property.getElement(), XmlKey.class)) {
            info.addXmlKeyProperty(property);
        }
        // handle XmlJoinNode(s)
        processXmlJoinNodes(property);
        processXmlNullPolicy(property);
    }

    /**
     * Process XmlJoinNode(s) for a given Property.  An org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNode(s)
     * will be created/populated using the annotation, and set on the Property for later processing.
     * 
     * It is assumed that for a single join node XmlJoinNode will be used, and for multiple join nodes
     * XmlJoinNodes will be used.
     *  
     * @param property Property that may contain @XmlJoinNodes/@XmlJoinNode
     */
    private void processXmlJoinNodes(Property property) {
        List<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode> xmlJoinNodeList;
        org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode xmlJoinNode;
        org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes xmlJoinNodes;
        // handle XmlJoinNodes
        if (helper.isAnnotationPresent(property.getElement(), XmlJoinNodes.class)) {
            xmlJoinNodeList = new ArrayList<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode>();
            for (XmlJoinNode xmlJN : ((XmlJoinNodes) helper.getAnnotation(property.getElement(), XmlJoinNodes.class)).value()) {
                xmlJoinNode = new org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode();
                xmlJoinNode.setXmlPath(xmlJN.xmlPath());
                xmlJoinNode.setReferencedXmlPath(xmlJN.referencedXmlPath());
                xmlJoinNodeList.add(xmlJoinNode);
            }
            xmlJoinNodes = new org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes();
            xmlJoinNodes.setXmlJoinNode(xmlJoinNodeList);
            property.setXmlJoinNodes(xmlJoinNodes);
        } 
        // handle XmlJoinNode
        else if (helper.isAnnotationPresent(property.getElement(), XmlJoinNode.class)) {
            XmlJoinNode xmlJN = (XmlJoinNode) helper.getAnnotation(property.getElement(), XmlJoinNode.class);
            xmlJoinNode = new org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode();
            xmlJoinNode.setXmlPath(xmlJN.xmlPath());
            xmlJoinNode.setReferencedXmlPath(xmlJN.referencedXmlPath());
            xmlJoinNodeList = new ArrayList<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode>();
            xmlJoinNodeList.add(xmlJoinNode);
            xmlJoinNodes = new org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes();
            xmlJoinNodes.setXmlJoinNode(xmlJoinNodeList);
            property.setXmlJoinNodes(xmlJoinNodes);
        }
    }
    
    /**
     * Responsible for validating transformer settings on a given property.
     * Validates that for field transformers either a transformer class OR
     * method name is set (not both) and that an xml-path is set.  
     * Validates that for attribute transformers either a transformer class 
     * OR method name is set (not both).    
     * 
     * @param property
     */
    private void processXmlTransformationProperty(Property property) {
        if (property.isSetXmlTransformation()) {
            XmlTransformation xmlTransformation = property.getXmlTransformation();
            // validate transformer(s)
            if (xmlTransformation.isSetXmlReadTransformer()) {
                // validate read transformer
                XmlReadTransformer readTransformer = xmlTransformation.getXmlReadTransformer();
                if (readTransformer.isSetTransformerClass()) {
                    // handle read transformer class
                    if (readTransformer.isSetMethod()) {
                        // cannot have both class and method set
                        throw JAXBException.readTransformerHasBothClassAndMethod(property.getPropertyName());
                    }
                } else {
                    // handle read transformer method
                    if (!readTransformer.isSetMethod()) {
                        // require class or method to be set
                        throw JAXBException.readTransformerHasNeitherClassNorMethod(property.getPropertyName());
                    }
                }
            }
            if (xmlTransformation.isSetXmlWriteTransformers()) {
                // handle write transformer(s)
                for (XmlWriteTransformer writeTransformer : xmlTransformation.getXmlWriteTransformer()) {
                    // must have an xml-path set
                    if (!writeTransformer.isSetXmlPath()) {
                        throw JAXBException.writeTransformerHasNoXmlPath(property.getPropertyName());
                    }
                    if (writeTransformer.isSetTransformerClass()) {
                        // handle write transformer class
                        if (writeTransformer.isSetMethod()) {
                            // cannot have both class and method set
                            throw JAXBException.writeTransformerHasBothClassAndMethod(property.getPropertyName(), writeTransformer.getXmlPath());
                        }
                    } else {
                        // handle write transformer method
                        if (!writeTransformer.isSetMethod()) {
                            // require class or method to be set
                            throw JAXBException.writeTransformerHasNeitherClassNorMethod(property.getPropertyName(), writeTransformer.getXmlPath());
                        }
                    }
                }
            }
        }
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
    protected boolean areEquals(JavaClass src, Class tgt) {
        if (src == null || tgt == null) {
            return false;
        }
        return src.getRawName().equals(tgt.getCanonicalName());
    }
    
    private void processXmlNullPolicy(Property property) {
        if(helper.isAnnotationPresent(property.getElement(), XmlNullPolicy.class)) {
            XmlNullPolicy nullPolicy = (XmlNullPolicy)helper.getAnnotation(property.getElement(), XmlNullPolicy.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy policy = new org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy();
            policy.setEmptyNodeRepresentsNull(nullPolicy.emptyNodeRepresentsNull());
            policy.setIsSetPerformedForAbsentNode(nullPolicy.isSetPerformedForAbsentNode());
            policy.setXsiNilRepresentsNull(new Boolean(nullPolicy.xsiNilRepresentsNull()));
            policy.setNullRepresentationForXml(org.eclipse.persistence.jaxb.xmlmodel.XmlMarshalNullRepresentation.valueOf(nullPolicy.nullRepresentationForXml().toString()));
            property.setNullPolicy(policy);
            
        } else if(helper.isAnnotationPresent(property.getElement(), XmlIsSetNullPolicy.class)) {
            XmlIsSetNullPolicy nullPolicy = (XmlIsSetNullPolicy)helper.getAnnotation(property.getElement(), XmlIsSetNullPolicy.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy policy = new org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy();
            policy.setEmptyNodeRepresentsNull(nullPolicy.emptyNodeRepresentsNull());
            policy.setXsiNilRepresentsNull(new Boolean(nullPolicy.xsiNilRepresentsNull()));
            policy.setNullRepresentationForXml(org.eclipse.persistence.jaxb.xmlmodel.XmlMarshalNullRepresentation.valueOf(nullPolicy.nullRepresentationForXml().toString()));
            policy.setIsSetMethodName(nullPolicy.isSetMethodName());
            for(XmlParameter next: nullPolicy.isSetMethodParameters()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy.IsSetParameter param = new org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy.IsSetParameter();
                param.setValue(next.value());
                param.setType(next.type().getName());
                policy.getIsSetParameter().add(param);
            }
            property.setNullPolicy(policy);
        }
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

    public ArrayList<Property> getPropertyPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList<Property> properties = new ArrayList<Property>();
        if (cls == null) {
            return properties;
        }

        // First collect all the getters and setters
        ArrayList<JavaMethod> propertyMethods = new ArrayList<JavaMethod>();
        for (JavaMethod next : new ArrayList<JavaMethod>(cls.getDeclaredMethods())) {
            if ((next.getName().startsWith("get") && next.getName().length() > 3) || (next.getName().startsWith("is") && next.getName().length() > 2)) {
                int modifiers = next.getModifiers();
                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic)) {
                    propertyMethods.add(next);
                }
            } else if (next.getName().startsWith("set") && next.getName().length() > 3 && next.getParameterTypes().length == 1) {
                int modifiers = next.getModifiers();
                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic)) {
                    propertyMethods.add(next);
                }
            }
        }
        // Next iterate over the getters and find their setter methods, add whichever one is
        // annotated to the properties list. If neither is, use the getter

        // keep track of property names to avoid processing the same property twice (for getter and setter)
        ArrayList<String> propertyNames = new ArrayList<String>();
        for (int i = 0; i < propertyMethods.size(); i++) {
            boolean isPropertyTransient = false;
            JavaMethod nextMethod = propertyMethods.get(i);
            String propertyName = "";

            JavaMethod getMethod;
            JavaMethod setMethod;

            JavaMethod propertyMethod = null;

            if (!nextMethod.getName().startsWith("set")) {
                if (nextMethod.getName().startsWith("get")) {
                    propertyName = nextMethod.getName().substring(3);
                } else if (nextMethod.getName().startsWith("is")) {
                    propertyName = nextMethod.getName().substring(2);
                }
                getMethod = nextMethod;
                String setMethodName = "set" + propertyName;

                // use the JavaBean API to correctly decapitalize the first character, if necessary
                propertyName = Introspector.decapitalize(propertyName);

                JavaClass[] paramTypes = { (JavaClass) getMethod.getReturnType() };
                setMethod = cls.getDeclaredMethod(setMethodName, paramTypes);
                if (setMethod != null && !setMethod.getAnnotations().isEmpty()) {
                    // use the set method if it exists and is annotated
                    if (!helper.isAnnotationPresent(setMethod, XmlTransient.class)) {
                        propertyMethod = setMethod;
                    } else {
                        isPropertyTransient = true;
                    }
                } else {
                    if (!helper.isAnnotationPresent(getMethod, XmlTransient.class)) {
                        propertyMethod = getMethod;
                    } else {
                        isPropertyTransient = true;
                    }
                }
            } else {
                propertyName = nextMethod.getName().substring(3);
                setMethod = nextMethod;

                String getMethodName = "get" + propertyName;

                getMethod = cls.getDeclaredMethod(getMethodName, new JavaClass[] {});
                if (getMethod == null) {
                    // try is instead of get
                    getMethodName = "is" + propertyName;
                    getMethod = cls.getDeclaredMethod(getMethodName, new JavaClass[] {});
                }
                if (getMethod != null && !getMethod.getAnnotations().isEmpty()) {
                    // use the set method if it exists and is annotated
                    if (!helper.isAnnotationPresent(getMethod, XmlTransient.class)) {
                        propertyMethod = getMethod;
                    } else {
                        isPropertyTransient = true;
                    }
                } else {
                    if (!helper.isAnnotationPresent(setMethod, XmlTransient.class)) {
                        propertyMethod = setMethod;
                    } else {
                        isPropertyTransient = true;
                    }
                }
                // use the JavaBean API to correctly decapitalize the first character, if necessary
                propertyName = Introspector.decapitalize(propertyName);
            }
            
            JavaClass ptype = null;
            if (getMethod != null) {
                ptype = (JavaClass) getMethod.getReturnType();
            } else {
                ptype = setMethod.getParameterTypes()[0];
            }

            if (!propertyNames.contains(propertyName)) {
                propertyNames.add(propertyName);

                Property property = buildNewProperty(info, cls, propertyMethod, propertyName, ptype);

                property.setTransient(isPropertyTransient);

                if (getMethod != null) {
                    property.setOriginalGetMethodName(getMethod.getName());
                    if(property.getGetMethodName() == null) {
                        property.setGetMethodName(getMethod.getName());
                    }
                }
                if (setMethod != null) {
                    property.setOriginalSetMethodName(setMethod.getName());
                    if(property.getSetMethodName() == null) {
                        property.setSetMethodName(setMethod.getName());
                    }
                }
                property.setMethodProperty(true);

                if (!helper.isAnnotationPresent(property.getElement(), XmlTransient.class)) {
                    properties.add(property);
                } else {
                    // If a property is marked transient ensure it doesn't exist in the propOrder
                    List<String> propOrderList = Arrays.asList(info.getPropOrder());
                    if (propOrderList.contains(propertyName)) {
                        throw JAXBException.transientInProporder(propertyName);
                    }
                    property.setTransient(true);
                }
            }
        }
        // default to alphabetical ordering
        // RI compliancy
        Collections.sort(properties, new PropertyComparitor());
        return properties;
    }

    public ArrayList getPublicMemberPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> fieldProperties = getFieldPropertiesForClass(cls, info, false);
        ArrayList<Property> methodProperties = getPropertyPropertiesForClass(cls, info, false);

        // filter out non-public properties that aren't annotated
        ArrayList<Property> publicFieldProperties = new ArrayList<Property>();
        ArrayList<Property> publicMethodProperties = new ArrayList<Property>();

        for (Property next : fieldProperties) {
            if (Modifier.isPublic(((JavaField) next.getElement()).getModifiers())) {
                publicFieldProperties.add(next);
            } else {
                if (hasJAXBAnnotations(next.getElement())) {
                    publicFieldProperties.add(next);
                }
            }
        }

        for (Property next : methodProperties) {
            if (next.getElement() != null) {
                if (Modifier.isPublic(((JavaMethod) next.getElement()).getModifiers())) {
                    publicMethodProperties.add(next);
                } else {
                    if (hasJAXBAnnotations(next.getElement())) {
                        publicMethodProperties.add(next);
                    }
                }
            }
        }

        // Not sure who should win if a property exists for both or the correct order
        if (publicFieldProperties.size() >= 0 && publicMethodProperties.size() == 0) {
            return publicFieldProperties;
        } else if (publicMethodProperties.size() > 0 && publicFieldProperties.size() == 0) {
            return publicMethodProperties;
        } else {
            // add any non-duplicate method properties to the collection.
            // - in the case of a collision if one is annotated use it, otherwise
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
        if (cls == null) {
            return list;
        }
        ArrayList fieldProperties = getFieldPropertiesForClass(cls, info, false);
        ArrayList methodProperties = getPropertyPropertiesForClass(cls, info, false);

        // Iterate over the field and method properties. If ANYTHING contains an annotation and
        // doesn't appear in the other list, add it to the final list
        for (int i = 0; i < fieldProperties.size(); i++) {
            Property next = (Property) fieldProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (!hasJAXBAnnotations(elem)) {
                next.setTransient(true);
            }
            list.add(next);
        }
        for (int i = 0; i < methodProperties.size(); i++) {
            Property next = (Property) methodProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (!hasJAXBAnnotations(elem)) {
                next.setTransient(true);
            }
            list.add(next);
        }
        return list;
    }

    /**
     * Use name, namespace and type information to setup a user-defined
     * schema type.  This method will typically be called when processing 
     * an @XmlSchemaType(s) annotation or xml-schema-type(s) metadata.
     * 
     * @param name
     * @param namespace
     * @param jClassQualifiedName
     */
    public void processSchemaType(String name, String namespace, String jClassQualifiedName) {
        this.userDefinedSchemaTypes.put(jClassQualifiedName, new QName(namespace, name));
    }
    
    public void processSchemaType(XmlSchemaType type) {
        JavaClass jClass = helper.getJavaClass(type.type());
        if (jClass == null) {
            return;
        }
        processSchemaType(type.name(), type.namespace(), jClass.getQualifiedName());
    }

    public void addEnumTypeInfo(JavaClass javaClass, EnumTypeInfo info) {
        if (javaClass == null) {
            return;
        }

        info.setClassName(javaClass.getQualifiedName());
        Class restrictionClass = String.class;

        if (helper.isAnnotationPresent(javaClass, XmlEnum.class)) {
            XmlEnum xmlEnum = (XmlEnum) helper.getAnnotation(javaClass, XmlEnum.class);
            restrictionClass = xmlEnum.value();
        }
        QName restrictionBase = getSchemaTypeFor(helper.getJavaClass(restrictionClass));
        info.setRestrictionBase(restrictionBase);

        for (Iterator<JavaField> fieldIt = javaClass.getDeclaredFields().iterator(); fieldIt.hasNext();) {
            JavaField field = fieldIt.next();
            if (field.isEnumConstant()) {
                String enumValue = field.getName();
                if (helper.isAnnotationPresent(field, XmlEnumValue.class)) {
                    enumValue = ((XmlEnumValue) helper.getAnnotation(field, XmlEnumValue.class)).value();
                }
                info.addJavaFieldToXmlEnumValuePair(field.getName(), enumValue);
            }
        }
    }

    private String decapitalize(String javaName) {
        // return Introspector.decapitalize(name); Spec Compliant
        // RI compliancy
        char[] name = javaName.toCharArray();
        int i = 0;
        while (i < name.length && Character.isUpperCase(name[i])) {
            i++;
        }
        if (i > 0) {
            name[0] = Character.toLowerCase(name[0]);
            for (int j = 1; j < i - 1; j++) {
                name[j] = Character.toLowerCase(name[j]);
            }
            return new String(name);
        } else {
            return javaName;
        }
    }

    public String getSchemaTypeNameForClassName(String className) {
        String typeName = "";
        if (className.indexOf('$') != -1) {
            typeName = decapitalize(className.substring(className.lastIndexOf('$') + 1));
        } else {
            typeName = decapitalize(className.substring(className.lastIndexOf('.') + 1));
        }
        // now capitalize any characters that occur after a "break"
        boolean inBreak = false;
        StringBuffer toReturn = new StringBuffer(typeName.length());
        for (int i = 0; i < typeName.length(); i++) {
            char next = typeName.charAt(i);
            if (Character.isDigit(next)) {
                if (!inBreak) {
                    inBreak = true;
                }
                toReturn.append(next);
            } else {
                if (inBreak) {
                    toReturn.append(Character.toUpperCase(next));
                } else {
                    toReturn.append(next);
                }
            }
        }
        return toReturn.toString();
    }

    public QName getSchemaTypeOrNullFor(JavaClass javaClass) {
        if (javaClass == null) {
            return null;
        }

        // check user defined types first
        QName schemaType = (QName) userDefinedSchemaTypes.get(javaClass.getQualifiedName());
        if (schemaType == null) {
            schemaType = (QName) helper.getXMLToJavaTypeMap().get(javaClass.getRawName());
        }
        return schemaType;
    }

    public QName getSchemaTypeFor(JavaClass javaClass) {
        QName schemaType = getSchemaTypeOrNullFor(javaClass);
        if (schemaType == null) {
            return XMLConstants.ANY_SIMPLE_TYPE_QNAME;
        }
        return schemaType;
    }

    public boolean isCollectionType(Property field) {
        return isCollectionType(field.getType());
    }

    public boolean isCollectionType(JavaClass type) {
        if (helper.getJavaClass(java.util.Collection.class).isAssignableFrom(type) 
                || helper.getJavaClass(java.util.List.class).isAssignableFrom(type) 
                || helper.getJavaClass(java.util.Set.class).isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    public NamespaceInfo processNamespaceInformation(XmlSchema xmlSchema) {
        NamespaceInfo info = new NamespaceInfo();
        info.setNamespaceResolver(new NamespaceResolver());
        String packageNamespace = null;
        if (xmlSchema != null) {
            String namespaceMapping = xmlSchema.namespace();
            if (!(namespaceMapping.equals("") || namespaceMapping.equals("##default"))) {
                packageNamespace = namespaceMapping;
            } else if(namespaceMapping.equals("##default")) {
                packageNamespace = this.defaultTargetNamespace;
            }
            info.setNamespace(packageNamespace);
            XmlNs[] xmlns = xmlSchema.xmlns();
            for (int i = 0; i < xmlns.length; i++) {
                XmlNs next = xmlns[i];
                info.getNamespaceResolver().put(next.prefix(), next.namespaceURI());
            }
            info.setAttributeFormQualified(xmlSchema.attributeFormDefault() == XmlNsForm.QUALIFIED);
            info.setElementFormQualified(xmlSchema.elementFormDefault() == XmlNsForm.QUALIFIED);

            // reflectively load XmlSchema class to avoid dependency
            try {
                Method locationMethod = PrivilegedAccessHelper.getDeclaredMethod(XmlSchema.class, "location", new Class[] {});
                String location = (String) PrivilegedAccessHelper.invokeMethod(locationMethod, xmlSchema, new Object[] {});

                if (location != null) {
                    if (location.equals("##generate")) {
                        location = null;
                    } else if (location.equals("")) {
                        location = null;
                    }
                }
                info.setLocation(location);
            } catch (Exception ex) {
            }

        } else {
            info.setNamespace(defaultTargetNamespace);
        }
        if (!info.isElementFormQualified() || info.isAttributeFormQualified()) {
            isDefaultNamespaceAllowed = false;
        }
        return info;
    }

    public HashMap<String, TypeInfo> getTypeInfo() {
        return typeInfo;
    }

    public ArrayList<JavaClass> getTypeInfoClasses() {
        return typeInfoClasses;
    }

    public HashMap<String, QName> getUserDefinedSchemaTypes() {
        return userDefinedSchemaTypes;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public String getSchemaTypeNameFor(JavaClass javaClass, XmlType xmlType) {
        String typeName = "";
        if (javaClass == null) {
            return typeName;
        }

        if (helper.isAnnotationPresent(javaClass, XmlType.class)) {
            // Figure out what kind of type we have
            // figure out type name
            XmlType typeAnnotation = (XmlType) helper.getAnnotation(javaClass, XmlType.class);
            typeName = typeAnnotation.name();
            if (typeName.equals("#default")) {
                typeName = getSchemaTypeNameForClassName(javaClass.getName());
            }
        } else {
            typeName = getSchemaTypeNameForClassName(javaClass.getName());
        }
        return typeName;
    }

    public QName getQNameForProperty(String defaultName, JavaHasAnnotations element, NamespaceInfo namespaceInfo, String uri) {
        String name = "##default";
        String namespace = "##default";
        QName qName = null;
        if (helper.isAnnotationPresent(element, XmlAttribute.class)) {
            XmlAttribute xmlAttribute = (XmlAttribute) helper.getAnnotation(element, XmlAttribute.class);
            name = xmlAttribute.name();
            namespace = xmlAttribute.namespace();

            if (name.equals("##default")) {
                name = defaultName;
            }

            if (!namespace.equals("##default")) {
                qName = new QName(namespace, name);
                isDefaultNamespaceAllowed = false;
            } else {
                if (namespaceInfo.isAttributeFormQualified()) {
                    qName = new QName(uri, name);
                } else {
                    qName = new QName(name);
                }
            }
        } else {
            if (helper.isAnnotationPresent(element, XmlElement.class)) {
                XmlElement xmlElement = (XmlElement) helper.getAnnotation(element, XmlElement.class);
                name = xmlElement.name();
                namespace = xmlElement.namespace();
            }

            if (name.equals("##default")) {
                name = defaultName;
            }

            if (!namespace.equals("##default")) {
                qName = new QName(namespace, name);
                if (namespace.equals(XMLConstants.EMPTY_STRING)) {
                    isDefaultNamespaceAllowed = false;
                }
            } else {
                if (namespaceInfo.isElementFormQualified()) {
                    qName = new QName(uri, name);
                } else {
                    qName = new QName(name);
                }
            }
        }
        return qName;
    }

    public HashMap<String, NamespaceInfo> getPackageToNamespaceMappings() {
        return packageToNamespaceMappings;
    }

    /**
     * Add a package name/NamespaceInfo entry to the map.  This method will lazy-load
     * the map if necessary.
     * 
     * @return
     */
    public void addPackageToNamespaceMapping(String packageName, NamespaceInfo nsInfo) {
        if (packageToNamespaceMappings == null) {
            packageToNamespaceMappings = new HashMap<String, NamespaceInfo>();
        }
        packageToNamespaceMappings.put(packageName, nsInfo);
    }

    public NamespaceInfo getNamespaceInfoForPackage(JavaClass javaClass) {
        NamespaceInfo packageNamespace = packageToNamespaceMappings.get(javaClass.getPackageName());
        if (packageNamespace == null) {
            packageNamespace = getNamespaceInfoForPackage(javaClass.getPackage());
        }
        return packageNamespace;
    }

    public NamespaceInfo getNamespaceInfoForPackage(JavaPackage pack) {
        NamespaceInfo packageNamespace = packageToNamespaceMappings.get(pack.getQualifiedName());
        if (packageNamespace == null) {
            XmlSchema xmlSchema = (XmlSchema) helper.getAnnotation(pack, XmlSchema.class);
            packageNamespace = processNamespaceInformation(xmlSchema);

            // if it's still null, generate based on package name
            if (packageNamespace.getNamespace() == null) {
                packageNamespace.setNamespace("");
            }
            if (helper.isAnnotationPresent(pack, XmlAccessorType.class)) {
                XmlAccessorType xmlAccessorType = (XmlAccessorType) helper.getAnnotation(pack, XmlAccessorType.class);
                packageNamespace.setAccessType(XmlAccessType.fromValue(xmlAccessorType.value().name()));
            }
            if (helper.isAnnotationPresent(pack, XmlAccessorOrder.class)) {
                XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder) helper.getAnnotation(pack, XmlAccessorOrder.class);
                packageNamespace.setAccessOrder(XmlAccessOrder.fromValue(xmlAccessorOrder.value().name()));
            }

            packageToNamespaceMappings.put(pack.getQualifiedName(), packageNamespace);
        }
        return packageNamespace;
    }

    private void checkForCallbackMethods() {
        for (JavaClass next : typeInfoClasses) {
            if (next == null) {
                continue;
            }

            JavaClass unmarshallerCls = helper.getJavaClass(Unmarshaller.class);
            JavaClass marshallerCls = helper.getJavaClass(Marshaller.class);
            JavaClass objectCls = helper.getJavaClass(Object.class);
            JavaClass[] unmarshalParams = new JavaClass[] { unmarshallerCls, objectCls };
            JavaClass[] marshalParams = new JavaClass[] { marshallerCls };
            UnmarshalCallback unmarshalCallback = null;
            MarshalCallback marshalCallback = null;
            // look for before unmarshal callback
            if (next.getMethod("beforeUnmarshal", unmarshalParams) != null) {
                unmarshalCallback = new UnmarshalCallback();
                unmarshalCallback.setDomainClassName(next.getQualifiedName());
                unmarshalCallback.setHasBeforeUnmarshalCallback();
            }
            // look for after unmarshal callback
            if (next.getMethod("afterUnmarshal", unmarshalParams) != null) {
                if (unmarshalCallback == null) {
                    unmarshalCallback = new UnmarshalCallback();
                    unmarshalCallback.setDomainClassName(next.getQualifiedName());
                }
                unmarshalCallback.setHasAfterUnmarshalCallback();
            }
            // if before/after unmarshal callback was found, add the callback to the list
            if (unmarshalCallback != null) {
                if (this.unmarshalCallbacks == null) {
                    this.unmarshalCallbacks = new HashMap<String, UnmarshalCallback>();
                }
                unmarshalCallbacks.put(next.getQualifiedName(), unmarshalCallback);
            }
            // look for before marshal callback
            if (next.getMethod("beforeMarshal", marshalParams) != null) {
                marshalCallback = new MarshalCallback();
                marshalCallback.setDomainClassName(next.getQualifiedName());
                marshalCallback.setHasBeforeMarshalCallback();
            }
            // look for after marshal callback
            if (next.getMethod("afterMarshal", marshalParams) != null) {
                if (marshalCallback == null) {
                    marshalCallback = new MarshalCallback();
                    marshalCallback.setDomainClassName(next.getQualifiedName());
                }
                marshalCallback.setHasAfterMarshalCallback();
            }
            // if before/after marshal callback was found, add the callback to the list
            if (marshalCallback != null) {
                if (this.marshalCallbacks == null) {
                    this.marshalCallbacks = new HashMap<String, MarshalCallback>();
                }
                marshalCallbacks.put(next.getQualifiedName(), marshalCallback);
            }
        }
    }

    public HashMap<String, MarshalCallback> getMarshalCallbacks() {
        return this.marshalCallbacks;
    }

    public HashMap<String, UnmarshalCallback> getUnmarshalCallbacks() {
        return this.unmarshalCallbacks;
    }
    
    public JavaClass[] processObjectFactory(JavaClass objectFactoryClass, ArrayList<JavaClass> classes) {
        // if there is an xml-registry from XML for this JavaClass, create a map of method
        // names to XmlElementDecl objects to simplify processing later on in this method
        Map<String, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl> elemDecls = new HashMap<String, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl>();
        org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry xmlReg = xmlRegistries.get(objectFactoryClass.getQualifiedName());
        if (xmlReg != null) {
            // process xml-element-decl entries
            for (org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl xmlElementDecl : xmlReg.getXmlElementDecl()) {
                // key each element-decl on method name
                elemDecls.put(xmlElementDecl.getJavaMethod(), xmlElementDecl);
            }
        }
        
        Collection methods = objectFactoryClass.getDeclaredMethods();
        Iterator methodsIter = methods.iterator();
        NamespaceInfo namespaceInfo = getNamespaceInfoForPackage(objectFactoryClass);
        while (methodsIter.hasNext()) {
            JavaMethod next = (JavaMethod) methodsIter.next();
            if (next.getName().startsWith("create")) {
                JavaClass type = next.getReturnType();
                if (type.getName().equals("javax.xml.bind.JAXBElement")) {
                    type = (JavaClass) next.getReturnType().getActualTypeArguments().toArray()[0];
                } else {
                    this.factoryMethods.put(next.getReturnType().getRawName(), next);
                }
                // if there's an XmlElementDecl for this method from XML, use it - otherwise look for an annotation
                org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl xmlEltDecl = elemDecls.get(next.getName());
                if (xmlEltDecl != null || helper.isAnnotationPresent(next, XmlElementDecl.class)) {
                    QName qname;
                    QName substitutionHead = null;
                    String url;
                    String localName;
                    String defaultValue = null;
                    Class scopeClass = javax.xml.bind.annotation.XmlElementDecl.GLOBAL.class;
                    
                    if (xmlEltDecl != null) {
                        url = xmlEltDecl.getNamespace();
                        localName = xmlEltDecl.getName();
                        String scopeClassName = xmlEltDecl.getScope();
                        if (!scopeClassName.equals("javax.xml.bind.annotation.XmlElementDecl.GLOBAL")) {
                            JavaClass jScopeClass = helper.getJavaClass(scopeClassName);
                            if (jScopeClass != null) {
                                scopeClass = helper.getClassForJavaClass(jScopeClass);
                                if (scopeClass == null) {
                                    scopeClass = javax.xml.bind.annotation.XmlElementDecl.GLOBAL.class;
                                }
                            }
                        }
                        if (!xmlEltDecl.getSubstitutionHeadName().equals("")) {
                            String subHeadLocal = xmlEltDecl.getSubstitutionHeadName();
                            String subHeadNamespace = xmlEltDecl.getSubstitutionHeadNamespace();
                            if (subHeadNamespace.equals("##default")) {
                                subHeadNamespace = namespaceInfo.getNamespace();
                            }
                            substitutionHead = new QName(subHeadNamespace, subHeadLocal);
                        }
                        if (!(xmlEltDecl.getDefaultValue().length() == 1 && xmlEltDecl.getDefaultValue().startsWith("\u0000"))) {
                            defaultValue = xmlEltDecl.getDefaultValue();
                        }
                    } else {
                        // there was no xml-element-decl for this method in XML, so use the annotation
                        XmlElementDecl elementDecl = (XmlElementDecl) helper.getAnnotation(next, XmlElementDecl.class);
                        url = elementDecl.namespace();
                        localName = elementDecl.name();
                        scopeClass = elementDecl.scope(); 
                        if (!elementDecl.substitutionHeadName().equals("")) {
                            String subHeadLocal = elementDecl.substitutionHeadName();
                            String subHeadNamespace = elementDecl.substitutionHeadNamespace();
                            if (subHeadNamespace.equals("##default")) {
                                subHeadNamespace = namespaceInfo.getNamespace();
                            }
                            
                            substitutionHead = new QName(subHeadNamespace, subHeadLocal);
                        }
                        if (!(elementDecl.defaultValue().length() == 1 && elementDecl.defaultValue().startsWith("\u0000"))) {
                            defaultValue = elementDecl.defaultValue();
                        }
                    }
                    
                    if ("##default".equals(url)) {
                        url = namespaceInfo.getNamespace();
                    }
                    qname = new QName(url, localName);


                    boolean isList = false;
                    if ("java.util.List".equals(type.getName())) {
                        isList = true;
                        if (type.hasActualTypeArguments()) {
                            type = (JavaClass) type.getActualTypeArguments().toArray()[0];
                        }
                    }

                    ElementDeclaration declaration = new ElementDeclaration(qname, type, type.getQualifiedName(), isList, scopeClass);
                    if (substitutionHead != null) {
                        declaration.setSubstitutionHead(substitutionHead);
                    }
                    if (defaultValue != null) {
                        declaration.setDefaultValue(defaultValue);
                    }
                    
                    if (helper.isAnnotationPresent(next, XmlJavaTypeAdapter.class)) {
                        XmlJavaTypeAdapter typeAdapter = (XmlJavaTypeAdapter) helper.getAnnotation(next, XmlJavaTypeAdapter.class);
                        Class typeAdapterClass = typeAdapter.value();
                        declaration.setJavaTypeAdapterClass(typeAdapterClass);

                        Class declJavaType = CompilerHelper.getTypeFromAdapterClass(typeAdapterClass);

                        declaration.setJavaType(helper.getJavaClass(declJavaType));
                        declaration.setAdaptedJavaType(type);
                    }
                    HashMap<QName, ElementDeclaration> elements = getElementDeclarationsForScope(scopeClass.getName()); 
                    if(elements == null) {
                        elements = new HashMap<QName, ElementDeclaration>();
                        this.elementDeclarations.put(scopeClass.getName(), elements);
                    } 
                    elements.put(qname, declaration);
                }
                if (!helper.isBuiltInJavaType(type) && !helper.classExistsInArray(type, classes)) {
                    classes.add(type);
                }
            }
        }
        
        if (classes.size() > 0) {
            return classes.toArray(new JavaClass[classes.size()]);
        } else {
            return new JavaClass[0];
        }
    }

    /**
     * Lazy load and return the map of global elements.
     * 
     * @return
     */
    public HashMap<QName, ElementDeclaration> getGlobalElements() {
        return this.elementDeclarations.get(XmlElementDecl.GLOBAL.class.getName());
    }

    public void updateGlobalElements(JavaClass[] classesToProcess) {
        //Once all the global element declarations have been created, make sure that any ones that have
        //a substitution head set are added to the list of substitutable elements on the declaration for that
        //head.

        // Look for XmlRootElement declarations
        for (JavaClass javaClass : classesToProcess) {
            TypeInfo info = typeInfo.get(javaClass.getQualifiedName());
            if (info == null) {
                continue;
            }
            if (!info.isTransient() && info.isSetXmlRootElement()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement xmlRE = info.getXmlRootElement();
                NamespaceInfo namespaceInfo;
                namespaceInfo = getNamespaceInfoForPackage(javaClass);

                String elementName = xmlRE.getName();
                if (elementName.equals("##default") || elementName.equals("")) {
                    if (javaClass.getName().indexOf("$") != -1) {
                        elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('$') + 1));
                    } else {
                        elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('.') + 1));
                    }
                    // TCK Compliancy
                    if (elementName.length() >= 3) {
                        int idx = elementName.length() - 1;
                        char ch = elementName.charAt(idx - 1);
                        if (Character.isDigit(ch)) {
                            char lastCh = Character.toUpperCase(elementName.charAt(idx));
                            elementName = elementName.substring(0, idx) + lastCh;
                        }
                    }
                }
                String rootNamespace = xmlRE.getNamespace();
                QName rootElemName = null;
                if (rootNamespace.equals("##default")) {
                    if (namespaceInfo == null) {
                        rootElemName = new QName(elementName);
                    } else {
                        String rootNS = namespaceInfo.getNamespace();
                        rootElemName = new QName(rootNS, elementName);
                        if (rootNS.equals(XMLConstants.EMPTY_STRING)) {
                            isDefaultNamespaceAllowed = false;
                        }
                    }
                } else {
                    rootElemName = new QName(rootNamespace, elementName);
                    if (rootNamespace.equals(XMLConstants.EMPTY_STRING)) {
                        isDefaultNamespaceAllowed = false;
                    }
                }
                ElementDeclaration declaration = new ElementDeclaration(rootElemName, javaClass, javaClass.getQualifiedName(), false);
                declaration.setIsXmlRootElement(true);
                this.getGlobalElements().put(rootElemName, declaration);
                this.xmlRootElements.put(javaClass.getQualifiedName(), declaration);
            }
        }


        Iterator<QName> elementQnames = this.getGlobalElements().keySet().iterator();
        while (elementQnames.hasNext()) {
            QName next = elementQnames.next();
            ElementDeclaration nextDeclaration = this.getGlobalElements().get(next);
            QName substitutionHead = nextDeclaration.getSubstitutionHead();
            while (substitutionHead != null) {
                ElementDeclaration rootDeclaration = this.getGlobalElements().get(substitutionHead);
                rootDeclaration.addSubstitutableElement(nextDeclaration);
                substitutionHead = rootDeclaration.getSubstitutionHead();
            }
        }
    }

    private void addReferencedElement(Property property, ElementDeclaration referencedElement) {
        property.addReferencedElement(referencedElement);
        if (referencedElement.getSubstitutableElements() != null && referencedElement.getSubstitutableElements().size() > 0) {
            for (ElementDeclaration substitutable : referencedElement.getSubstitutableElements()) {
                addReferencedElement(property, substitutable);
            }
        }
    }

    /**
     * Returns true if the field or method passed in is annotated with JAXB annotations.
     */
    private boolean hasJAXBAnnotations(JavaHasAnnotations elem) {
        if (helper.isAnnotationPresent(elem, XmlElement.class)
                || helper.isAnnotationPresent(elem, XmlAttribute.class)
                || helper.isAnnotationPresent(elem, XmlAnyElement.class)
                || helper.isAnnotationPresent(elem, XmlAnyAttribute.class)
                || helper.isAnnotationPresent(elem, XmlValue.class)
                || helper.isAnnotationPresent(elem, XmlElements.class)
                || helper.isAnnotationPresent(elem, XmlElementRef.class)
                || helper.isAnnotationPresent(elem, XmlElementRefs.class)
                || helper.isAnnotationPresent(elem, XmlID.class)
                || helper.isAnnotationPresent(elem, XmlSchemaType.class)
                || helper.isAnnotationPresent(elem, XmlElementWrapper.class)
                || helper.isAnnotationPresent(elem, XmlList.class)
                || helper.isAnnotationPresent(elem, XmlMimeType.class)
                || helper.isAnnotationPresent(elem, XmlIDREF.class)
                || helper.isAnnotationPresent(elem, XmlPath.class)
                || helper.isAnnotationPresent(elem, XmlPaths.class)
                || helper.isAnnotationPresent(elem, XmlInverseReference.class)
                || helper.isAnnotationPresent(elem, XmlReadOnly.class)
                || helper.isAnnotationPresent(elem, XmlWriteOnly.class)
                || helper.isAnnotationPresent(elem, XmlCDATA.class)
                || helper.isAnnotationPresent(elem, XmlAccessMethods.class)
                || helper.isAnnotationPresent(elem, XmlNullPolicy.class)) {
            return true;
        
        }
        return false;
    }

    private void validateElementIsInPropOrder(TypeInfo info, String name) {
        if (info.isTransient()) {
            return;
        }
        // If a property is marked with XMLElement, XMLElements, XMLElementRef or XMLElementRefs
        // and propOrder is not empty then it must be in the proporder list
        String[] propOrder = info.getPropOrder();
        if (propOrder.length > 0) {
            if (propOrder.length == 1 && propOrder[0].equals("")) {
                return;
            }
            List<String> propOrderList = Arrays.asList(info.getPropOrder());
            if (!propOrderList.contains(name)) {
                throw JAXBException.missingPropertyInPropOrder(name);
            }
        }
    }

    private void validatePropOrderForInfo(TypeInfo info) {
        if (info.isTransient()) {
            return;
        }
        // Ensure that all properties in the propOrder list actually exist
        String[] propOrder = info.getPropOrder();
        int propOrderLength = propOrder.length;
        if (propOrderLength > 0) {
            for (int i = 1; i < propOrderLength; i++) {
                String nextPropName = propOrder[i];
                if (!nextPropName.equals("") && !info.getPropertyNames().contains(nextPropName)) {
                    throw JAXBException.nonExistentPropertyInPropOrder(nextPropName);
                }
            }
        }
    }

    private void validateXmlValueFieldOrProperty(JavaClass cls, Property property) {
        JavaClass ptype = property.getActualType();
        String propName = property.getPropertyName();
        JavaClass parent = cls.getSuperclass();
        while (parent != null && !(parent.getQualifiedName().equals("java.lang.Object"))) {
            TypeInfo parentTypeInfo = typeInfo.get(parent.getQualifiedName());
            if (parentTypeInfo != null || shouldGenerateTypeInfo(parent)) {
                throw JAXBException.propertyOrFieldCannotBeXmlValue(propName);
            }
            parent = parent.getSuperclass();
        }

        QName schemaQName = getSchemaTypeOrNullFor(ptype);
        if (schemaQName == null) {
            TypeInfo refInfo = typeInfo.get(ptype.getQualifiedName());
            if (refInfo != null) {
                if (!refInfo.isPostBuilt()) {
                    postBuildTypeInfo(new JavaClass[] { ptype });
                }
            } else if (shouldGenerateTypeInfo(ptype)) {
                JavaClass[] jClasses = new JavaClass[] { ptype };
                buildNewTypeInfo(jClasses);
                refInfo = typeInfo.get(ptype.getQualifiedName());
            }
            if (refInfo != null && !refInfo.isEnumerationType() && refInfo.getXmlValueProperty() == null) {
                throw JAXBException.invalidTypeForXmlValueField(propName);
            }
        }
    }

    public boolean isMapType(JavaClass type) {
        return helper.getJavaClass(java.util.Map.class).isAssignableFrom(type);
    }

    private Class generateWrapperForMapClass(JavaClass mapClass, JavaClass keyClass, JavaClass valueClass, TypeMappingInfo typeMappingInfo) {
        String packageName = "jaxb.dev.java.net";
        NamespaceResolver combinedNamespaceResolver = new NamespaceResolver();
        if (!helper.isBuiltInJavaType(keyClass)) {
            String keyPackageName = keyClass.getPackageName();
            packageName = packageName + "." + keyPackageName;
            NamespaceInfo keyNamespaceInfo = getNamespaceInfoForPackage(keyClass);
            if(keyNamespaceInfo != null) {
                java.util.Vector<Namespace> namespaces= keyNamespaceInfo.getNamespaceResolver().getNamespaces();
                for(Namespace n:namespaces){
                    combinedNamespaceResolver.put(n.getPrefix(), n.getNamespaceURI());  
                }           

            }
        }

        if (!helper.isBuiltInJavaType(valueClass)) {
            String valuePackageName = valueClass.getPackageName();
            packageName = packageName + "." + valuePackageName;
            NamespaceInfo valueNamespaceInfo = getNamespaceInfoForPackage(valueClass);
            if(valueNamespaceInfo != null) {
                java.util.Vector<Namespace> namespaces= valueNamespaceInfo.getNamespaceResolver().getNamespaces();
                for(Namespace n:namespaces){
                    combinedNamespaceResolver.put(n.getPrefix(), n.getNamespaceURI());  
                }           
            }            
        }
        String namespace = this.defaultTargetNamespace;
        if(namespace == null) {
            namespace = "";
        }        
        NamespaceInfo namespaceInfo = packageToNamespaceMappings.get(mapClass.getPackageName());
        if(namespaceInfo == null) {
            namespaceInfo = getPackageToNamespaceMappings().get(packageName);
        } else {
            if(namespaceInfo.getNamespace() != null) {
                namespace = namespaceInfo.getNamespace();
            }
            getPackageToNamespaceMappings().put(packageName, namespaceInfo);
        }
        if(namespaceInfo == null) {
            namespaceInfo = new NamespaceInfo();
            namespaceInfo.setNamespace(namespace);
            namespaceInfo.setNamespaceResolver(combinedNamespaceResolver);
            
            getPackageToNamespaceMappings().put(packageName, namespaceInfo);
        }


        int beginIndex = keyClass.getName().lastIndexOf(".") + 1;
        String keyName = keyClass.getName().substring(beginIndex);
        int dollarIndex = keyName.indexOf('$'); 
        if(dollarIndex > -1){
            keyName = keyName.substring(dollarIndex + 1);
        }
        
        beginIndex = valueClass.getName().lastIndexOf(".")+1;
        String valueName = valueClass.getName().substring(beginIndex);
        dollarIndex = valueName.indexOf('$'); 
        if(dollarIndex > -1){
            valueName = valueName.substring(dollarIndex + 1);
        }
        String collectionClassShortName = mapClass.getRawName().substring(mapClass.getRawName().lastIndexOf('.') + 1);
        String suggestedClassName = keyName + valueName + collectionClassShortName;
        
        String qualifiedClassName = packageName + "." + suggestedClassName;
        qualifiedClassName = getNextAvailableClassName(qualifiedClassName);

        String qualifiedInternalClassName = qualifiedClassName.replace('.', '/');
        String internalKeyName = keyClass.getQualifiedName().replace('.', '/');
        String internalValueName = valueClass.getQualifiedName().replace('.', '/');
        
        Type mapType = Type.getType("L" + mapClass.getRawName().replace('.', '/') + ";");

        ClassWriter cw = new ClassWriter(false);
        CodeVisitor cv;

        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_SUPER, qualifiedInternalClassName, "org/eclipse/persistence/internal/jaxb/many/MapValue", null, "StringEmployeeMap.java");

        // FIELD ATTRIBUTES       
        RuntimeVisibleAnnotations fieldAttrs1 = new RuntimeVisibleAnnotations();

        if (typeMappingInfo != null) {
            java.lang.annotation.Annotation[] annotations = typeMappingInfo.getAnnotations();
            if (annotations != null) {
                for (int i = 0; i < annotations.length; i++) {
                    java.lang.annotation.Annotation nextAnnotation = annotations[i];
                    if (nextAnnotation != null && !(nextAnnotation instanceof XmlElement) && !(nextAnnotation instanceof XmlJavaTypeAdapter)) {                    
                        String annotationClassName = nextAnnotation.annotationType().getName();
                        Annotation fieldAttrs1ann0 = new Annotation("L" + annotationClassName.replace('.', '/') + ";");
                        fieldAttrs1.annotations.add(fieldAttrs1ann0);
                        for(Method next:nextAnnotation.annotationType().getDeclaredMethods()) {
                            try {
                                Object nextValue = next.invoke(nextAnnotation, new Object[]{});
                                if(nextValue instanceof Class) {
                                    Type nextType = Type.getType("L" + ((Class)nextValue).getName().replace('.', '/') + ";");
                                    nextValue = nextType;
                                }
                                fieldAttrs1ann0.add(next.getName(), nextValue);
                            } catch(InvocationTargetException ex) {
                                //ignore the invocation target exception here.
                            } catch(IllegalAccessException ex) {
                                
                            }
                        }
                    }
                }
            }
        }
        // FIELD ATTRIBUTES
        SignatureAttribute fieldAttrs2 = new SignatureAttribute("L" + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;");
        fieldAttrs1.next = fieldAttrs2;
        cw.visitField(Constants.ACC_PUBLIC, "entry", "L" + mapType.getInternalName() + ";", null, fieldAttrs1);

        cv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", "()V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitMethodInsn(Constants.INVOKESPECIAL, "org/eclipse/persistence/internal/jaxb/many/MapValue", "<init>", "()V");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(1, 1);

        // METHOD ATTRIBUTES
        RuntimeVisibleAnnotations methodAttrs1 = new RuntimeVisibleAnnotations();

        Annotation methodAttrs1ann0 = new Annotation("Ljavax/xml/bind/annotation/XmlTransient;");
        methodAttrs1.annotations.add(methodAttrs1ann0);

        SignatureAttribute methodAttrs2 = new SignatureAttribute("(L" + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;)V");
        methodAttrs1.next = methodAttrs2;
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "setItem", "(L" + mapType.getInternalName() + ";)V", null, methodAttrs1);
        Label l0 = new Label();
        cv.visitLabel(l0);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitVarInsn(Constants.ALOAD, 1);
        cv.visitFieldInsn(Constants.PUTFIELD, qualifiedInternalClassName, "entry", "L" + mapType.getInternalName() + ";");
        cv.visitInsn(Constants.RETURN);
        Label l1 = new Label();
        cv.visitLabel(l1);
        // CODE ATTRIBUTE

        LocalVariableTypeTableAttribute cvAttr = new LocalVariableTypeTableAttribute();
        cv.visitAttribute(cvAttr);

        cv.visitMaxs(2, 2);

        // METHOD ATTRIBUTES
        methodAttrs1 = new RuntimeVisibleAnnotations();

        methodAttrs1ann0 = new Annotation("Ljavax/xml/bind/annotation/XmlTransient;");
        methodAttrs1.annotations.add(methodAttrs1ann0);

        methodAttrs2 = new SignatureAttribute("()L" + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;");
        methodAttrs1.next = methodAttrs2;
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "getItem", "()L" + mapType.getInternalName() + ";", null, methodAttrs1);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitFieldInsn(Constants.GETFIELD, qualifiedInternalClassName, "entry", "L" + mapType.getInternalName() + ";");
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);

        cv = cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_BRIDGE + Constants.ACC_SYNTHETIC, "getItem", "()Ljava/lang/Object;", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitMethodInsn(Constants.INVOKEVIRTUAL, qualifiedInternalClassName, "getItem", "()L" + mapType.getInternalName() + ";");
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);

        cv = cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_BRIDGE + Constants.ACC_SYNTHETIC, "setItem", "(Ljava/lang/Object;)V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitVarInsn(Constants.ALOAD, 1);
        cv.visitTypeInsn(Constants.CHECKCAST, mapType.getInternalName());
        cv.visitMethodInsn(Constants.INVOKEVIRTUAL, qualifiedInternalClassName, "setItem", "(L" + mapType.getInternalName() + ";)V");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(2, 2);

        // CLASS ATTRIBUTE
        RuntimeVisibleAnnotations annotationsAttr = new RuntimeVisibleAnnotations();
        
        Annotation attrann0 = new Annotation("Ljavax/xml/bind/annotation/XmlType;");
        attrann0.add( "namespace", namespace);
        annotationsAttr.annotations.add( attrann0);
        cw.visitAttribute(annotationsAttr);
        
        SignatureAttribute attr = new SignatureAttribute("Lorg/eclipse/persistence/internal/jaxb/many/MapValue<L"+ mapType.getInternalName()+ "<L" + internalKeyName + ";L" + internalValueName + ";>;>;");
        cw.visitAttribute(attr);

        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();
        return generateClassFromBytes(qualifiedClassName, classBytes);
    }

    private Class generateWrapperForArrayClass(JavaClass arrayClass, TypeMappingInfo typeMappingInfo, Class xmlElementType) {
        JavaClass componentClass = null;
        if (typeMappingInfo != null && xmlElementType != null) {
            componentClass = helper.getJavaClass(xmlElementType);
        } else {
            componentClass = arrayClass.getComponentType();
        }
        if (componentClass.isArray()) {
            Class nestedArrayClass = generateWrapperForArrayClass(componentClass, typeMappingInfo, xmlElementType);
            arrayClassesToGeneratedClasses.put(componentClass.getName(), nestedArrayClass);
            return generateArrayValue(arrayClass, componentClass, helper.getJavaClass(nestedArrayClass), typeMappingInfo);
        } else {
            return generateArrayValue(arrayClass, componentClass, componentClass, typeMappingInfo);
        }
    }

    private Class generateArrayValue(JavaClass arrayClass, JavaClass componentClass, JavaClass nestedClass, TypeMappingInfo typeMappingInfo) {
        String packageName;
        String qualifiedClassName;
        if(componentClass.isArray()) {
            packageName = componentClass.getPackageName();
            qualifiedClassName = nestedClass.getQualifiedName() + ARRAY_CLASS_NAME_SUFFIX;
        } else {
            if(componentClass.isPrimitive()) {
                packageName = ARRAY_PACKAGE_NAME;
                qualifiedClassName = packageName + "." + componentClass.getName() + ARRAY_CLASS_NAME_SUFFIX;
            } else {
                packageName = ARRAY_PACKAGE_NAME + "." + componentClass.getPackageName();
                if(componentClass.isMemberClass()) {
                    qualifiedClassName = componentClass.getName();
                    qualifiedClassName = qualifiedClassName.substring(qualifiedClassName.indexOf('$') + 1);
                    qualifiedClassName = ARRAY_PACKAGE_NAME + "." + componentClass.getPackageName() + "." + qualifiedClassName + ARRAY_CLASS_NAME_SUFFIX;
                } else {
                    qualifiedClassName = ARRAY_PACKAGE_NAME + "." + componentClass.getQualifiedName() + ARRAY_CLASS_NAME_SUFFIX;
                }
            }

            if (componentClass.isPrimitive() || helper.isBuiltInJavaType(componentClass)) {
                NamespaceInfo namespaceInfo = getPackageToNamespaceMappings().get(packageName);
                if (namespaceInfo == null) {
                    namespaceInfo = new NamespaceInfo();
                    namespaceInfo.setNamespace(ARRAY_NAMESPACE);
                    namespaceInfo.setNamespaceResolver(new NamespaceResolver());
                    getPackageToNamespaceMappings().put(packageName, namespaceInfo);
                }
            } else {
                NamespaceInfo namespaceInfo = getNamespaceInfoForPackage(componentClass.getPackage());
                getPackageToNamespaceMappings().put(packageName, namespaceInfo);
            }
        }

        String qualifiedInternalClassName = qualifiedClassName.replace('.', '/');
        String superClassName;
        if(componentClass.isArray()) {
            superClassName = "org/eclipse/persistence/internal/jaxb/many/MultiDimensionalArrayValue";
        } else {
            superClassName = "org/eclipse/persistence/internal/jaxb/many/ArrayValue";
        }

        ClassWriter cw = new ClassWriter(false);
        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_SUPER, qualifiedInternalClassName, superClassName, null, null);
        CodeVisitor cv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", "()V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitMethodInsn(Constants.INVOKESPECIAL, superClassName, "<init>", "()V");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(1, 1);

        if(componentClass.isArray()) {
            cv = cw.visitMethod(Constants.ACC_PROTECTED, "adaptedClass", "()Ljava/lang/Class;", null, null);
            cv.visitLdcInsn(Type.getType("L" + nestedClass.getQualifiedName().replace('.', '/') + ";"));
            cv.visitInsn(Constants.ARETURN);
            cv.visitMaxs(1, 1);
        }

        cv = cw.visitMethod(Constants.ACC_PROTECTED, "componentClass", "()Ljava/lang/Class;", null, null);
        JavaClass baseComponentClass = getBaseComponentType(componentClass);
        if(baseComponentClass.isPrimitive()) {
            cv.visitFieldInsn(Constants.GETSTATIC, getObjectType(baseComponentClass).getQualifiedName().replace('.', '/'), "TYPE", "Ljava/lang/Class;");
        } else {
            cv.visitLdcInsn(Type.getType("L" + baseComponentClass.getQualifiedName().replace('.', '/') + ";"));
        }
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);

        RuntimeVisibleAnnotations getAdaptedValueMethodAnnotations = new RuntimeVisibleAnnotations();
        Annotation xmlElementAnnotation = new Annotation("Ljavax/xml/bind/annotation/XmlElement;");
        xmlElementAnnotation.add("name", "item");
        xmlElementAnnotation.add("type",  Type.getType("L" + getObjectType(nestedClass).getName().replace('.', '/') + ";"));
        getAdaptedValueMethodAnnotations.annotations.add(xmlElementAnnotation);
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "getAdaptedValue", "()Ljava/util/List;", null, getAdaptedValueMethodAnnotations);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitFieldInsn(Constants.GETFIELD, qualifiedInternalClassName, "adaptedValue", "Ljava/util/List;");
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);

        return generateClassFromBytes(qualifiedClassName, cw.toByteArray());
    }

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

    private JavaClass getObjectType(JavaClass javaClass) {
        if(javaClass.isPrimitive()) {
            String primitiveClassName = javaClass.getRawName();
            Class primitiveClass = getPrimitiveClass(primitiveClassName);
            return helper.getJavaClass(getObjectClass(primitiveClass));
        }
        return javaClass;
    }

    private Class generateCollectionValue(JavaClass collectionClass, TypeMappingInfo typeMappingInfo, Class xmlElementType) {

        JavaClass componentClass;

        if (typeMappingInfo != null && xmlElementType != null) {
            componentClass = helper.getJavaClass(xmlElementType);
        } else if (collectionClass.hasActualTypeArguments()) {
            componentClass = ((JavaClass) collectionClass.getActualTypeArguments().toArray()[0]);
        } else {
            componentClass = helper.getJavaClass(Object.class);
        }
        
        if(componentClass.isPrimitive()){            
            Class primitiveClass = getPrimitiveClass(componentClass.getRawName());
            componentClass = helper.getJavaClass(getObjectClass(primitiveClass));        
        }
       
        NamespaceInfo namespaceInfo = packageToNamespaceMappings.get(collectionClass.getPackageName());

        String namespace = "";
        if(this.defaultTargetNamespace != null) {
            namespace = this.defaultTargetNamespace;
        }
        
        NamespaceInfo componentNamespaceInfo = getNamespaceInfoForPackage(componentClass);
        String packageName = componentClass.getPackageName();
        packageName = "jaxb.dev.java.net." + packageName;
        if(namespaceInfo == null) {
            namespaceInfo = getPackageToNamespaceMappings().get(packageName);
        } else {
            getPackageToNamespaceMappings().put(packageName, namespaceInfo);
            if(namespaceInfo.getNamespace() != null) {
                namespace = namespaceInfo.getNamespace();
            }
        }
        if (namespaceInfo == null) {
            if(componentNamespaceInfo != null) {
                namespaceInfo = componentNamespaceInfo;
            } else {
                namespaceInfo = new NamespaceInfo();
                namespaceInfo.setNamespaceResolver(new NamespaceResolver());
            }
            getPackageToNamespaceMappings().put(packageName, namespaceInfo);
        }
        
        String name = componentClass.getName();

        Type componentType = Type.getType("L" + componentClass.getName().replace('.', '/') + ";");
        String componentTypeInternalName = null;
        if(name.equals("[B")){          
            name = "byteArray";
            componentTypeInternalName = componentType.getInternalName();
        }else if(name.equals("[Ljava.lang.Byte;")){         
            name = "ByteArray";
            componentTypeInternalName = componentType.getInternalName() + ";";  
        }else{
            componentTypeInternalName = "L" + componentType.getInternalName() + ";";  
        }
        
        int beginIndex = name.lastIndexOf('.') + 1;
        name = name.substring(beginIndex);
        int dollarIndex = name.indexOf('$'); 
        if(dollarIndex > -1){
            name = name.substring(dollarIndex + 1);
        }
        String collectionClassRawName = collectionClass.getRawName();
        
        String collectionClassShortName = collectionClassRawName.substring(collectionClassRawName.lastIndexOf('.') + 1);
        String suggestedClassName = collectionClassShortName + "Of" + name;
        String qualifiedClassName = packageName + "." + suggestedClassName;
        qualifiedClassName = getNextAvailableClassName(qualifiedClassName);
        String className = qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);

        Type collectionType = Type.getType("L" + collectionClassRawName.replace('.', '/') + ";");
        String qualifiedInternalClassName = qualifiedClassName.replace('.', '/');
        ClassWriter cw = new ClassWriter(false);
        CodeVisitor cv;

        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_SUPER, qualifiedInternalClassName, "org/eclipse/persistence/internal/jaxb/many/CollectionValue", null, className.replace('.', '/') + ".java");

        // FIELD ATTRIBUTES       
        RuntimeVisibleAnnotations fieldAttrs1 = new RuntimeVisibleAnnotations();

        if (typeMappingInfo != null) {
            java.lang.annotation.Annotation[] annotations = getAnnotations(typeMappingInfo);
            if (annotations != null) {
                for (int i = 0; i < annotations.length; i++) {
                    java.lang.annotation.Annotation nextAnnotation = annotations[i];
                    if (nextAnnotation != null && !(nextAnnotation instanceof XmlElement) && !(nextAnnotation instanceof XmlJavaTypeAdapter)) {
                        String annotationClassName = nextAnnotation.annotationType().getName();
                        Annotation fieldAttrs1ann0 = new Annotation("L" + annotationClassName.replace('.', '/') + ";");
                        fieldAttrs1.annotations.add(fieldAttrs1ann0);
                        for(Method next:nextAnnotation.annotationType().getDeclaredMethods()) {
                            try {
                                Object nextValue = next.invoke(nextAnnotation, new Object[]{});
                                if(nextValue instanceof Class) {
                                    Type nextType = Type.getType("L" + ((Class)nextValue).getName().replace('.', '/') + ";");
                                    nextValue = nextType;
                                }
                                fieldAttrs1ann0.add(next.getName(), nextValue);
                            } catch(InvocationTargetException ex) {
                                //ignore the invocation target exception here.
                            } catch(IllegalAccessException ex) {
                                
                            }
                        }

                    }
                }
            }
        }

        SignatureAttribute fieldAttrs2 = new SignatureAttribute("L" + collectionType.getInternalName() + "<" + componentTypeInternalName + ">;");
        fieldAttrs1.next = fieldAttrs2;
        cw.visitField(Constants.ACC_PUBLIC, "item", "L" + collectionType.getInternalName() + ";", null, fieldAttrs1);

        cv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", "()V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitMethodInsn(Constants.INVOKESPECIAL, "org/eclipse/persistence/internal/jaxb/many/CollectionValue", "<init>", "()V");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(1, 1);

        // METHOD ATTRIBUTES
        RuntimeVisibleAnnotations methodAttrs1 = new RuntimeVisibleAnnotations();

        Annotation methodAttrs1ann0 = new Annotation("Ljavax/xml/bind/annotation/XmlTransient;");
        methodAttrs1.annotations.add(methodAttrs1ann0);

        SignatureAttribute methodAttrs2 = new SignatureAttribute("(L" + collectionType.getInternalName() + "<" + componentTypeInternalName + ">;)V");
        methodAttrs1.next = methodAttrs2;
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "setItem", "(L" + collectionType.getInternalName() + ";)V", null, methodAttrs1);
        Label l0 = new Label();
        cv.visitLabel(l0);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitVarInsn(Constants.ALOAD, 1);
        cv.visitFieldInsn(Constants.PUTFIELD, qualifiedInternalClassName, "item", "L" + collectionType.getInternalName() + ";");
        cv.visitInsn(Constants.RETURN);
        Label l1 = new Label();
        cv.visitLabel(l1);
        // CODE ATTRIBUTE
                
        LocalVariableTypeTableAttribute cvAttr = new LocalVariableTypeTableAttribute();
        cv.visitAttribute(cvAttr);

        cv.visitMaxs(2, 2);

        // METHOD ATTRIBUTES
        methodAttrs1 = new RuntimeVisibleAnnotations();

        methodAttrs1ann0 = new Annotation("Ljavax/xml/bind/annotation/XmlTransient;");
        methodAttrs1.annotations.add(methodAttrs1ann0);

        methodAttrs2 = new SignatureAttribute("()L" + collectionType.getInternalName() + "<" + componentTypeInternalName  + ">;");
        methodAttrs1.next = methodAttrs2;
        cv = cw.visitMethod(Constants.ACC_PUBLIC, "getItem", "()L" + collectionType.getInternalName() + ";", null, methodAttrs1);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitFieldInsn(Constants.GETFIELD, qualifiedInternalClassName, "item", "L" + collectionType.getInternalName() + ";");
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);

        cv = cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_BRIDGE + Constants.ACC_SYNTHETIC, "getItem", "()Ljava/lang/Object;", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitMethodInsn(Constants.INVOKEVIRTUAL, qualifiedInternalClassName, "getItem", "()L" + collectionType.getInternalName() + ";");
        cv.visitInsn(Constants.ARETURN);
        cv.visitMaxs(1, 1);

        cv = cw.visitMethod(Constants.ACC_PUBLIC + Constants.ACC_BRIDGE + Constants.ACC_SYNTHETIC, "setItem", "(Ljava/lang/Object;)V", null, null);
        cv.visitVarInsn(Constants.ALOAD, 0);
        cv.visitVarInsn(Constants.ALOAD, 1);
        cv.visitTypeInsn(Constants.CHECKCAST, "" + collectionType.getInternalName() + "");
        cv.visitMethodInsn(Constants.INVOKEVIRTUAL, qualifiedInternalClassName, "setItem", "(L" + collectionType.getInternalName() + ";)V");
        cv.visitInsn(Constants.RETURN);
        cv.visitMaxs(2, 2);

        // CLASS ATRIBUTE

        // CLASS ATTRIBUTE
        RuntimeVisibleAnnotations annotationsAttr = new RuntimeVisibleAnnotations();
        
        Annotation attrann0 = new Annotation("Ljavax/xml/bind/annotation/XmlType;");
        attrann0.add( "namespace", namespace);
        annotationsAttr.annotations.add( attrann0);
        cw.visitAttribute(annotationsAttr);
        
        SignatureAttribute attr = new SignatureAttribute("Lorg/eclipse/persistence/internal/jaxb/many/CollectionValue<L"+collectionType.getInternalName()+"<" + componentTypeInternalName + ">;>;");
        cw.visitAttribute(attr);

        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();
        return generateClassFromBytes(qualifiedClassName, classBytes);
    }

    private Class generateClassFromBytes(String className, byte[] classBytes) {
        JaxbClassLoader loader = (JaxbClassLoader) helper.getClassLoader();
        Class generatedClass = loader.generateClass(className, classBytes);
        return generatedClass;

    }

    /**
     * Inner class used for ordering a list of Properties 
     * alphabetically by property name.
     *
     */
    class PropertyComparitor implements Comparator<Property> {
        public int compare(Property p1, Property p2) {
            return p1.getPropertyName().compareTo(p2.getPropertyName());
        }
    }

    private String getNextAvailableClassName(String suggestedName) {
        int counter = 1;
        return getNextAvailableClassName(suggestedName, suggestedName, counter);
    }

    private String getNextAvailableClassName(String suggestedBaseName, String suggestedName, int counter) {

        Iterator<Class> iter = typeMappingInfoToGeneratedClasses.values().iterator();
        while (iter.hasNext()) {
            Class nextClass = iter.next();
            if (nextClass.getName().equals(suggestedName)) {
                counter = counter + 1;
                return getNextAvailableClassName(suggestedBaseName, suggestedBaseName + counter, counter);
            }
        }
        return suggestedName;
    }

    private Class getPrimitiveClass(String primitiveClassName) {
        return ConversionManager.getDefaultManager().convertClassNameToClass(primitiveClassName);
    }

    private Class getObjectClass(Class primitiveClass) {
        return ConversionManager.getDefaultManager().getObjectClass(primitiveClass);
    }

    public Map<java.lang.reflect.Type, Class> getCollectionClassesToGeneratedClasses() {
        return collectionClassesToGeneratedClasses;
    }

    public Map<String, Class> getArrayClassesToGeneratedClasses() {
        return arrayClassesToGeneratedClasses;
    }

    public Map<Class, java.lang.reflect.Type> getGeneratedClassesToCollectionClasses() {
        return generatedClassesToCollectionClasses;
    }

    public Map<Class, JavaClass> getGeneratedClassesToArrayClasses() {
        return generatedClassesToArrayClasses;
    }

    /**
     * Convenience method for returning all of the TypeInfo objects for a given package name.
     * 
     * This method is inefficient as we need to iterate over the entire typeinfo map for each
     * call.  We should eventually store the TypeInfos in a Map based on package name, i.e.:
     * 
     * Map<String, Map<String, TypeInfo>>
     * 
     * @param packageName
     * @return List of TypeInfo objects for a given package name
     */
    public Map<String, TypeInfo> getTypeInfosForPackage(String packageName) {
        Map<String, TypeInfo> typeInfos = new HashMap<String, TypeInfo>();
        ArrayList<JavaClass> jClasses = getTypeInfoClasses();
        for (JavaClass jClass : jClasses) {
            if (jClass.getPackageName().equals(packageName)) {
                String key = jClass.getQualifiedName();
                typeInfos.put(key, typeInfo.get(key));
            }
        }
        return typeInfos;
    }

    /**
     * Set namespace override info from XML bindings file. This will typically
     * be called from the XMLProcessor.
     * 
     * @param packageToNamespaceMappings
     */
    public void setPackageToNamespaceMappings(HashMap<String, NamespaceInfo> packageToNamespaceMappings) {
        this.packageToNamespaceMappings = packageToNamespaceMappings;
    }

    public SchemaTypeInfo addClass(JavaClass javaClass) {
        if (javaClass == null) {
            return null;
        } else if (helper.isAnnotationPresent(javaClass, XmlTransient.class)) {
            return null;
        }

        if (typeInfo == null) {
            // this is the first class. Initialize all the properties
            this.typeInfoClasses = new ArrayList<JavaClass>();
            this.typeInfo = new HashMap<String, TypeInfo>();
            this.typeQNames = new ArrayList<QName>();
            this.userDefinedSchemaTypes = new HashMap<String, QName>();
            this.packageToNamespaceMappings = new HashMap<String, NamespaceInfo>();
            this.namespaceResolver = new NamespaceResolver();
        }

        JavaClass[] jClasses = new JavaClass[] { javaClass };
        buildNewTypeInfo(jClasses);
        TypeInfo info = typeInfo.get(javaClass.getQualifiedName());

        NamespaceInfo namespaceInfo;
        String packageName = javaClass.getPackageName();
        namespaceInfo = this.packageToNamespaceMappings.get(packageName);

        SchemaTypeInfo schemaInfo = new SchemaTypeInfo();
        schemaInfo.setSchemaTypeName(new QName(info.getClassNamespace(), info.getSchemaTypeName()));

        if (info.isSetXmlRootElement()) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement xmlRE = info.getXmlRootElement();
            String elementName = xmlRE.getName();
            if (elementName.equals("##default") || elementName.equals("")) {
                if (javaClass.getName().indexOf("$") != -1) {
                    elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('$') + 1));
                } else {
                    elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('.') + 1));
                }

                // TCK Compliancy
                if (elementName.length() >= 3) {
                    int idx = elementName.length() - 1;
                    char ch = elementName.charAt(idx - 1);
                    if (Character.isDigit(ch)) {
                        char lastCh = Character.toUpperCase(elementName.charAt(idx));
                        elementName = elementName.substring(0, idx) + lastCh;
                    }
                }

            }
            String rootNamespace = xmlRE.getNamespace();
            QName rootElemName = null;
            if (rootNamespace.equals("##default")) {
                rootElemName = new QName(namespaceInfo.getNamespace(), elementName);
            } else {
                rootElemName = new QName(rootNamespace, elementName);
            }
            schemaInfo.getGlobalElementDeclarations().add(rootElemName);
            ElementDeclaration declaration = new ElementDeclaration(rootElemName, javaClass, javaClass.getRawName(), false);
            this.getGlobalElements().put(rootElemName, declaration);
        }

        return schemaInfo;
    }

    /**
     * Convenience method which class pre and postBuildTypeInfo for a given set
     * of JavaClasses.
     * 
     * @param javaClasses
     */
    public void buildNewTypeInfo(JavaClass[] javaClasses) {
        preBuildTypeInfo(javaClasses);
        postBuildTypeInfo(javaClasses);
    }

    /**
     * Pre-process a descriptor customizer.  Here, the given JavaClass is checked
     * for the existence of an @XmlCustomizer annotation.
     * 
     * Note that the post processing of the descriptor customizers will take place
     * in MappingsGenerator's generateProject method, after the descriptors and 
     * mappings have been generated.
     * 
     * @param jClass
     * @param tInfo
     * @see XmlCustomizer
     * @see MappingsGenerator
     */
    private void preProcessCustomizer(JavaClass jClass, TypeInfo tInfo) {
        XmlCustomizer xmlCustomizer = (XmlCustomizer) helper.getAnnotation(jClass, XmlCustomizer.class);
        if (xmlCustomizer != null) {
            tInfo.setXmlCustomizer(xmlCustomizer.value().getName());
        }
    }

    /**
     * Lazy load the metadata logger.
     * 
     * @return
     */
    private JAXBMetadataLogger getLogger() {
        if (logger == null) {
            logger = new JAXBMetadataLogger();
        }
        return logger;
    }

    /**
     * Return the Helper object set on this processor.
     * 
     * @return
     */
    Helper getHelper() {
        return this.helper;
    }

    public boolean isDefaultNamespaceAllowed() {
        return isDefaultNamespaceAllowed;
    }

    public List<ElementDeclaration> getLocalElements() {
        return this.localElements;
    }

    public Map<TypeMappingInfo, Class> getTypeMappingInfoToGeneratedClasses() {
        return this.typeMappingInfoToGeneratedClasses;
    }
      
    public Map<TypeMappingInfo, Class> getTypeMappingInfoToAdapterClasses() {
        return this.typeMappingInfoToAdapterClasses;
    }
    
    /**
     * Add an XmlRegistry to ObjectFactory class name pair to the map.
     * 
     * @param factoryClassName ObjectFactory class name
     * @param xmlReg org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry instance
     */
    public void addXmlRegistry(String factoryClassName, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry xmlReg) {
        this.xmlRegistries.put(factoryClassName, xmlReg);
    }

    /**
     * Convenience method for determining if a given JavaClass should be processed
     * as an ObjectFactory class.
     * 
     * @param javaClass
     * @return true if the JavaClass is annotated with @XmlRegistry  or the map of 
     *         XmlRegistries contains a key equal to the JavaClass' qualified name
     */
    private boolean isXmlRegistry(JavaClass javaClass) {
        return (helper.isAnnotationPresent(javaClass, XmlRegistry.class) || xmlRegistries.get(javaClass.getQualifiedName()) != null);
    }
    
    public Map<TypeMappingInfo, QName> getTypeMappingInfoToSchemaType(){
        return this.typeMappingInfoToSchemaType;
    }

    String getDefaultTargetNamespace() {
        return this.defaultTargetNamespace;
    }
    
    void setDefaultTargetNamespace(String defaultTargetNamespace) {
        this.defaultTargetNamespace = defaultTargetNamespace;
    }

    public void setDefaultNamespaceAllowed(boolean isDefaultNamespaceAllowed) {
        this.isDefaultNamespaceAllowed = isDefaultNamespaceAllowed;
    }
    
    HashMap<QName, ElementDeclaration> getElementDeclarationsForScope(String scopeClassName) {
        return this.elementDeclarations.get(scopeClassName);
    }
    
    private Map<Object, Object> createUserPropertiesMap(XmlProperty[] properties) {
        Map<Object, Object> propMap = new HashMap<Object, Object>();
        for (XmlProperty prop : properties) {
            Object pvalue = prop.value();
            if (!(prop.valueType() == String.class)) {
                pvalue = XMLConversionManager.getDefaultXMLManager().convertObject(
                        prop.value(), prop.valueType());
            }
            propMap.put(prop.name(), pvalue);
        }
        return propMap;
    }
}
