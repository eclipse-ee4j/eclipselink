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
import java.lang.annotation.Annotation;
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
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBElement;
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

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jaxb.AccessorFactoryWrapper;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.many.ArrayValue;
import org.eclipse.persistence.internal.jaxb.many.CollectionValue;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.jaxb.many.MultiDimensionalArrayValue;
import org.eclipse.persistence.internal.jaxb.many.MultiDimensionalCollectionValue;
import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.internal.libraries.asm.Label;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.AnnotationProxy;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
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
import org.eclipse.persistence.oxm.XMLNameTransformer;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.annotations.XmlNamedAttributeNode;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraph;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraphs;
import org.eclipse.persistence.oxm.annotations.XmlNamedSubgraph;
import org.eclipse.persistence.oxm.annotations.XmlAccessMethods;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.eclipse.persistence.oxm.annotations.XmlContainerProperty;
import org.eclipse.persistence.oxm.annotations.XmlCustomizer;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;
import org.eclipse.persistence.oxm.annotations.XmlElementsJoinNodes;
import org.eclipse.persistence.oxm.annotations.XmlLocation;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;
import org.eclipse.persistence.oxm.annotations.XmlVirtualAccessMethods;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.eclipse.persistence.oxm.annotations.XmlIsSetNullPolicy;
import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;
import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlNameTransformer;
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
 * <p>
 * <b>Purpose:</b>To perform some initial processing of Java classes and JAXB
 * 2.0 Annotations and generate meta data that can be used by the Mappings
 * Generator and Schema Generator
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Generate a map of TypeInfo objects, keyed on class name</li>
 * <li>Generate a map of user defined schema types</li>
 * <li>Identify any class-based JAXB 2.0 callback methods, and create
 * MarshalCallback and UnmarshalCallback objects to wrap them.</li>
 * <li>Centralize processing which is common to both Schema Generation and
 * Mapping Generation tasks</li>
 * <p>
 * This class does the initial processing of the JAXB 2.0 Generation. It
 * generates meta data that can be used by the later Schema Generation and
 * Mapping Generation steps.
 * 
 * @see org.eclipse.persistence.jaxb.compiler.Generator
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */
public class AnnotationsProcessor {
    static final String JAVAX_ACTIVATION_DATAHANDLER = "javax.activation.DataHandler";
    static final String JAVAX_MAIL_INTERNET_MIMEMULTIPART = "javax.mail.internet.MimeMultipart";
    private static final String JAVAX_XML_BIND_JAXBELEMENT = "javax.xml.bind.JAXBElement";
    private static final String JAVAX_XML_BIND_ANNOTATION = "javax.xml.bind.annotation";
    private static final String OXM_ANNOTATIONS = "org.eclipse.persistence.oxm.annotations";
    private static final String TYPE_METHOD_NAME = "type";
    private static final String VALUE_METHOD_NAME = "value";
    private static final String ARRAY_PACKAGE_NAME = "jaxb.dev.java.net.array";
    private static final String ARRAY_NAMESPACE = "http://jaxb.dev.java.net/array";
    private static final String ARRAY_CLASS_NAME_SUFFIX = "Array";
    private static final String JAXB_DEV = "jaxb.dev.java.net";
    private static final String ORG_W3C_DOM = "org.w3c.dom";
    private static final String CREATE = "create";
    private static final String ELEMENT_DECL_GLOBAL = "javax.xml.bind.annotation.XmlElementDecl.GLOBAL";
    private static final String ELEMENT_DECL_DEFAULT = "\u0000";
    private static final String EMPTY_STRING = "";
    private static final String JAVA_UTIL_LIST = "java.util.List";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    private static final String SLASH = "/";
    private static final String SEMI_COLON = ";";
    private static final String L = "L";
    private static final String ITEM = "item";
    private static final String IS_STR = "is";
    private static final String GET_STR = "get";
    private static final String SET_STR = "set";
    private static final Character DOT_CHR = '.';
    private static final Character DOLLAR_SIGN_CHR = '$';
    private static final Character SLASH_CHR = '/';

    private ArrayList<JavaClass> typeInfoClasses;
    private HashMap<String, PackageInfo> packageToPackageInfoMappings;
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
    private List<String> objectFactoryClassNames;
    private List<JavaClass> classesToProcessPropertyTypes;

    private Map<String, Class> arrayClassesToGeneratedClasses;
    private Map<Class, JavaClass> generatedClassesToArrayClasses;
    private Map<java.lang.reflect.Type, Class> collectionClassesToGeneratedClasses;
    private Map<Class, java.lang.reflect.Type> generatedClassesToCollectionClasses;

    private Map<JavaClass, List<TypeMappingInfo>> javaClassToTypeMappingInfos;
    private Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClasses;
    private Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses;
    private Map<TypeMappingInfo, QName> typeMappingInfoToSchemaType;

    private Helper helper;
    private String defaultTargetNamespace;

    private JAXBMetadataLogger logger;

    private boolean isDefaultNamespaceAllowed;
    private boolean xmlAccessorFactorySupport;

    private boolean hasSwaRef;
    
    private List<String> referencedByTransformer;
    private boolean hasXmlBindings = false;

    public AnnotationsProcessor(Helper helper) {
        this.helper = helper;
        isDefaultNamespaceAllowed = true;
        hasSwaRef = false;
    }

    /**
     * This event is called when annotation processing is completed,
     * and provides a chance to deference anything that is no longer
     * needed (to reduce the memory footprint of this object).
     */
    void postInitialize() {
        typeInfoClasses = null;
        packageToPackageInfoMappings = null;
        typeInfo = null;
        typeQNames = null;
        elementDeclarations = null;
        xmlRootElements = null;
        localElements = null;
        factoryMethods = null;
        xmlRegistries = null;
        objectFactoryClassNames = null;
        classesToProcessPropertyTypes = null;
        javaClassToTypeMappingInfos = null;
        typeMappingInfoToGeneratedClasses = null;
        typeMappingInfoToAdapterClasses = null;
        helper = null;
        logger = null;
        referencedByTransformer = null;
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
        processPropertyTypes(this.typeInfoClasses.toArray(new JavaClass[this.typeInfoClasses.size()]));
        finalizeProperties();
        createElementsForTypeMappingInfo();
        processJavaClasses(null);
    }

    public void createElementsForTypeMappingInfo() {
        if (this.javaClassToTypeMappingInfos != null && !this.javaClassToTypeMappingInfos.isEmpty()) {
            Set<JavaClass> classes = this.javaClassToTypeMappingInfos.keySet();
            for (JavaClass nextClass : classes) {
                List<TypeMappingInfo> nextInfos = this.javaClassToTypeMappingInfos.get(nextClass);
                for(TypeMappingInfo nextInfo:nextInfos) {
                    if (nextInfo != null) {
                        boolean xmlAttachmentRef = false;
                        String xmlMimeType = null;
                        java.lang.annotation.Annotation[] annotations = getAnnotations(nextInfo);
                        Class adapterClass = this.typeMappingInfoToAdapterClasses.get(nextInfo);
                        Class declJavaType = null;
                        if (adapterClass != null) {
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
                                        if (!this.hasSwaRef) {
                                            this.hasSwaRef = true;
                                        }
                                    }
                                }
                            }
                        }

                        QName qname = null;

                        String nextClassName = nextClass.getQualifiedName();

                        if (declJavaType != null) {
                            nextClassName = declJavaType.getCanonicalName();
                        }

                        if (typeMappingInfoToGeneratedClasses != null) {
                            Class generatedClass = typeMappingInfoToGeneratedClasses.get(nextInfo);
                            if (generatedClass != null) {
                                nextClassName = generatedClass.getCanonicalName();
                            }
                        }

                        TypeInfo nextTypeInfo = typeInfo.get(nextClassName);
                        if (nextTypeInfo != null) {
                            qname = new QName(nextTypeInfo.getClassNamespace(), nextTypeInfo.getSchemaTypeName());
                        } else {
                            qname = getUserDefinedSchemaTypes().get(nextClassName);
                            if (qname == null) {
                                if (nextClassName.equals(ClassConstants.APBYTE.getName()) || nextClassName.equals(Image.class.getName()) || nextClassName.equals(Source.class.getName()) || nextClassName.equals("javax.activation.DataHandler")) {
                                    if (xmlAttachmentRef) {
                                        qname = Constants.SWA_REF_QNAME;
                                    } else {
                                        qname = Constants.BASE_64_BINARY_QNAME;
                                    }
                                } else if (nextClassName.equals(ClassConstants.OBJECT.getName())) {
                                    qname = Constants.ANY_TYPE_QNAME;
                                } else if (nextClassName.equals(ClassConstants.XML_GREGORIAN_CALENDAR.getName())) {
                                    qname = Constants.ANY_SIMPLE_TYPE_QNAME;
                                } else {
                                    Class theClass = helper.getClassForJavaClass(nextClass);
                                    qname = (QName) XMLConversionManager.getDefaultJavaTypes().get(theClass);
                                }
                            }
                        }

                        if (qname != null) {
                            typeMappingInfoToSchemaType.put(nextInfo, qname);
                        }

                        if (nextInfo.getXmlTagName() != null) {
                            ElementDeclaration element = new ElementDeclaration(nextInfo.getXmlTagName(), nextClass, nextClass.getQualifiedName(), false);
                            element.setTypeMappingInfo(nextInfo);
                            element.setXmlMimeType(xmlMimeType);
                            element.setXmlAttachmentRef(xmlAttachmentRef);
                            element.setNillable(nextInfo.isNillable());

                            if (declJavaType != null) {
                                element.setJavaType(helper.getJavaClass(declJavaType));
                            }
                            Class generatedClass = typeMappingInfoToGeneratedClasses.get(nextInfo);
                            if (generatedClass != null) {
                                element.setJavaType(helper.getJavaClass(generatedClass));
                            }
                            if (nextInfo.getElementScope() == TypeMappingInfo.ElementScope.Global) {
                                ElementDeclaration currentElement = this.getGlobalElements().get(element.getElementName());
                                if (currentElement == null) {
                                    addGlobalElement(element.getElementName(), element);
                                } else {
                                    //  if(currentElement.getTypeMappingInfo() == null) {
                                    //  the global element that exists came from an annotation
                                    
                                    //} else {
                                    this.localElements.add(element);
                                    //}
                                }
                            } else {
                                this.localElements.add(element);
                            }
                            String rootNamespace = element.getElementName().getNamespaceURI();
                            if (rootNamespace == null) {
                                rootNamespace = Constants.EMPTY_STRING;
                            }
                            if (rootNamespace.equals(Constants.EMPTY_STRING)) {
                                isDefaultNamespaceAllowed = false;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns an array of Annotations for a given TypeMappingInfo. This array
     * will either be populated from the TypeMappingInfo's array of annotations,
     * or based on an xml-element if present. The xml-element will take
     * precedence over the annotation array; if there is an xml-element the
     * Array of Annotations will be ignored.
     * 
     * @param tmInfo
     * @return
     */
    private java.lang.annotation.Annotation[] getAnnotations(TypeMappingInfo tmInfo) {
        if (tmInfo.getXmlElement() != null) {
            ClassLoader loader = helper.getClassLoader();
            // create a single ConversionManager for that will be shared by the
            // proxy objects
            ConversionManager cMgr = new ConversionManager();
            cMgr.setLoader(loader);

            // unmarshal the node into an XmlElement
            org.eclipse.persistence.jaxb.xmlmodel.XmlElement xElt = (org.eclipse.persistence.jaxb.xmlmodel.XmlElement) CompilerHelper.getXmlElement(tmInfo.getXmlElement(), loader);
            List annotations = new ArrayList();
            // where applicable, a given dynamic proxy will contain a Map of
            // method name/return value entries
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
        // no xml-element set on the info, (i.e. no xml overrides) so return the
        // array of Annotation objects
        return tmInfo.getAnnotations();
    }

    /**
     * Initialize maps, lists, etc. Typically called prior to processing a set
     * of classes via preBuildTypeInfo, postBuildTypeInfo, processJavaClasses.
     */
    void init(JavaClass[] classes, TypeMappingInfo[] typeMappingInfos) {
        typeInfoClasses = new ArrayList<JavaClass>();
        referencedByTransformer = new ArrayList<String>();
        typeInfo = new HashMap<String, TypeInfo>();
        typeQNames = new ArrayList<QName>();
        classesToProcessPropertyTypes = new ArrayList<JavaClass>();
        objectFactoryClassNames = new ArrayList<String>();
        userDefinedSchemaTypes = new HashMap<String, QName>();
        if (packageToPackageInfoMappings == null) {
            packageToPackageInfoMappings = new HashMap<String, PackageInfo>();
        }
        this.factoryMethods = new HashMap<String, JavaMethod>();
        this.xmlRegistries = new HashMap<String, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry>();
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

        javaClassToTypeMappingInfos = new HashMap<JavaClass, List<TypeMappingInfo>>();
        if (typeMappingInfos != null) {
            for (int i = 0; i < typeMappingInfos.length; i++) {
                List<TypeMappingInfo> infos = javaClassToTypeMappingInfos.get(classes[i]);
                if(infos == null) {
                    infos = new ArrayList<TypeMappingInfo>();
                    javaClassToTypeMappingInfos.put(classes[i], infos);
                }
                infos.add(typeMappingInfos[i]);
            }
        }
        typeMappingInfoToAdapterClasses = new HashMap<TypeMappingInfo, Class>();
        if (typeMappingInfos != null) {
            for (TypeMappingInfo next : typeMappingInfos) {
                java.lang.annotation.Annotation[] annotations = getAnnotations(next);
                if (annotations != null) {
                    for (java.lang.annotation.Annotation nextAnnotation : annotations) {
                        if (nextAnnotation instanceof XmlJavaTypeAdapter) {
                            typeMappingInfoToAdapterClasses.put(next, ((XmlJavaTypeAdapter) nextAnnotation).value());
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
            String qualifiedName = javaClass.getQualifiedName();

            TypeInfo info = typeInfo.get(qualifiedName);
            if (javaClass == null || javaClass.isArray()|| (info!=null && info.isPreBuilt()) || !shouldGenerateTypeInfo(javaClass) || isXmlRegistry(javaClass) ) {
                continue;
            }


            if (javaClass.isEnum()) {
                info = new EnumTypeInfo(helper, javaClass);
            } else {
                info = new TypeInfo(helper, javaClass);
            }
            info.setJavaClassName(qualifiedName);
            info.setPreBuilt(true);

            // handle @XmlTransient
            if (helper.isAnnotationPresent(javaClass, XmlTransient.class)) {
                info.setXmlTransient(true);
            }

            // handle @XmlExtensible
            processXmlExtensible(javaClass, info);

            // handle @XmlInlineBinaryData
            if (helper.isAnnotationPresent(javaClass, XmlInlineBinaryData.class)) {
                info.setInlineBinaryData(true);
            }
            
            // handle @NamedObjectGraph
            processNamedObjectGraphs(javaClass, info);

            // handle @XmlRootElement
            processXmlRootElement(javaClass, info);

            // handle @XmlSeeAlso
            processXmlSeeAlso(javaClass, info);

            PackageInfo packageInfo = getPackageInfoForPackage(javaClass);
            if(packageInfo != null && packageInfo.getPackageLevelAdaptersByClass().size() > 0){
            	for(String adapterClass :packageInfo.getPackageLevelAdaptersByClass().keySet()){
            		JavaClass boundType = packageInfo.getPackageLevelAdaptersByClass().get(adapterClass);
            		info.getPackageLevelAdaptersByClass().put(adapterClass, boundType);            	    
            	}
            }
            NamespaceInfo namespaceInfo = packageInfo.getNamespaceInfo();
            // handle @XmlType
            preProcessXmlType(javaClass, info, namespaceInfo);

            // handle @XmlAccessorType
            preProcessXmlAccessorType(javaClass, info, namespaceInfo);

            // handle @XmlAccessorOrder
            preProcessXmlAccessorOrder(javaClass, info, namespaceInfo);

            // handle package level @XmlJavaTypeAdapters
            processPackageLevelAdapters(javaClass, info);
            
            // handle Accessor Factory
            processAccessorFactory(javaClass, info);

            // handle class level @XmlJavaTypeAdapters
            processClassLevelAdapters(javaClass, info);

            // handle descriptor customizer
            preProcessCustomizer(javaClass, info);

            // handle package level @XmlSchemaType(s)
            processSchemaTypes(javaClass, info);

            // handle class extractor
            if (helper.isAnnotationPresent(javaClass, XmlClassExtractor.class)) {
                XmlClassExtractor classExtractor = (XmlClassExtractor) helper.getAnnotation(javaClass, XmlClassExtractor.class);
                info.setClassExtractorName(classExtractor.value().getName());
            }

            // handle user properties
            if (helper.isAnnotationPresent(javaClass, XmlProperties.class)) {
                XmlProperties xmlProperties = (XmlProperties) helper.getAnnotation(javaClass, XmlProperties.class);
                Map<Object, Object> propertiesMap = createUserPropertiesMap(xmlProperties.value());
                info.setUserProperties(propertiesMap);
            } else if (helper.isAnnotationPresent(javaClass, XmlProperty.class)) {
                XmlProperty xmlProperty = (XmlProperty) helper.getAnnotation(javaClass, XmlProperty.class);
                Map<Object, Object> propertiesMap = createUserPropertiesMap(new XmlProperty[] { xmlProperty });
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

    private void processNamedObjectGraphs(JavaClass javaClass, TypeInfo info) {
        ArrayList<XmlNamedObjectGraph> objectGraphs = new ArrayList<XmlNamedObjectGraph>();
        if(helper.isAnnotationPresent(javaClass, XmlNamedObjectGraphs.class)) {
            XmlNamedObjectGraphs graphs = (XmlNamedObjectGraphs)helper.getAnnotation(javaClass, XmlNamedObjectGraphs.class);
            for(XmlNamedObjectGraph next: graphs.value()) {
                objectGraphs.add(next);    
            }
        }
        if(helper.isAnnotationPresent(javaClass, XmlNamedObjectGraph.class)) {
            objectGraphs.add((XmlNamedObjectGraph)helper.getAnnotation(javaClass, XmlNamedObjectGraph.class));
        }
        
        for(XmlNamedObjectGraph next:objectGraphs) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlNamedObjectGraph namedGraph = new org.eclipse.persistence.jaxb.xmlmodel.XmlNamedObjectGraph();
            namedGraph.setName(next.name());

            for(XmlNamedAttributeNode nextNode:next.attributeNodes()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode namedNode = new org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode();
                namedNode.setName(nextNode.value());
                namedNode.setSubgraph(nextNode.subgraph());
                namedGraph.getXmlNamedAttributeNode().add(namedNode);
            }
            for(XmlNamedSubgraph nextSubgraph:next.subgraphs()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlNamedSubgraph namedSubGraph = new org.eclipse.persistence.jaxb.xmlmodel.XmlNamedSubgraph();
                namedSubGraph.setName(nextSubgraph.name());
                namedSubGraph.setType(nextSubgraph.type().getName());
                for(XmlNamedAttributeNode nextNode:nextSubgraph.attributeNodes()) {
                    org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode namedNode = new org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode();
                    namedNode.setName(nextNode.value());
                    namedNode.setSubgraph(nextNode.subgraph());
                    namedSubGraph.getXmlNamedAttributeNode().add(namedNode);
                }
                namedGraph.getXmlNamedSubgraph().add(namedSubGraph);
            }
            for(XmlNamedSubgraph nextSubgraph:next.subclassSubgraphs()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlNamedSubgraph namedSubGraph = new org.eclipse.persistence.jaxb.xmlmodel.XmlNamedSubgraph();
                namedSubGraph.setName(nextSubgraph.name());
                namedSubGraph.setType(nextSubgraph.type().getName());
                for(XmlNamedAttributeNode nextNode:nextSubgraph.attributeNodes()) {
                    org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode namedNode = new org.eclipse.persistence.jaxb.xmlmodel.XmlNamedAttributeNode();
                    namedNode.setName(nextNode.value());
                    namedNode.setSubgraph(nextNode.subgraph());
                    namedSubGraph.getXmlNamedAttributeNode().add(namedNode);
                }
                namedGraph.getXmlNamedSubclassGraph().add(namedSubGraph);
            }
            info.getObjectGraphs().add(namedGraph);
        }
    }

    private void processAccessorFactory(JavaClass javaClass, TypeInfo info) {
        if (!xmlAccessorFactorySupport) {
            return;
        }

        Annotation xmlAccessorFactory = helper.getAnnotation(javaClass, CompilerHelper.ACCESSOR_FACTORY_ANNOTATION_CLASS);
        Method valueMethod = null;
        if(xmlAccessorFactory != null) {
            valueMethod = CompilerHelper.ACCESSOR_FACTORY_VALUE_METHOD;
        } else {
            //try for internal annotation
            xmlAccessorFactory = helper.getAnnotation(javaClass, CompilerHelper.INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS);
            if(xmlAccessorFactory != null) {
                valueMethod = CompilerHelper.INTERNAL_ACCESSOR_FACTORY_VALUE_METHOD;
            }
        }
        if(xmlAccessorFactory != null) {
            Class xmlAccessorFactoryClass = null;
            try {
                xmlAccessorFactoryClass = (Class)PrivilegedAccessHelper.invokeMethod(valueMethod, xmlAccessorFactory, new Object[]{});
                info.setXmlAccessorFactory(new AccessorFactoryWrapper(PrivilegedAccessHelper.newInstanceFromClass(xmlAccessorFactoryClass)));
            } catch (Exception ex) {
                throw JAXBException.errorInstantiatingAccessorFactory(xmlAccessorFactoryClass, ex);
            }
        }
        PackageInfo pInfo = getPackageInfoForPackage(javaClass);
        if(pInfo != null) {
            info.setPackageLevelXmlAccessorFactory(pInfo.getAccessorFactory());
        }
    }

    /**
     * Process any additional classes (i.e. inner classes, @XmlSeeAlso,
     * 
     * @XmlRegistry, etc.) for a given set of JavaClasses, then complete
     *               building all of the required TypeInfo objects. This method
     *               is typically called after init and preBuildTypeInfo have
     *               been called.
     * 
     * @param javaClasses
     * @return updated array of JavaClasses, made up of the original classes
     *         plus any additional ones
     */
    public JavaClass[] postBuildTypeInfo(JavaClass[] javaClasses) {
        if (javaClasses.length == 0) {
            return javaClasses;
        }
        ArrayList<JavaClass> originalList = new ArrayList<JavaClass>(javaClasses.length);
        for(JavaClass next:javaClasses) {
            originalList.add(next);
        }
        // create type info instances for any additional classes
        javaClasses = processAdditionalClasses(javaClasses);
        preBuildTypeInfo(javaClasses);
        buildTypeInfo(javaClasses);
        updateGlobalElements(javaClasses);
        if(javaClasses.length > originalList.size()) {
            ArrayList<JavaClass> newClasses = new ArrayList(javaClasses.length - originalList.size());
            for(JavaClass next:javaClasses) {
                if(!(originalList.contains(next))) {
                    newClasses.add(next);
                }
            }
            postBuildTypeInfo(newClasses.toArray(new JavaClass[newClasses.size()]));
        }
        return javaClasses;
    }

    /**
     * INTERNAL:
     * 
     * Complete building TypeInfo objects for a given set of JavaClass
     * instances. This method assumes that init, preBuildTypeInfo, and
     * postBuildTypeInfo have been called.
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

            PackageInfo packageInfo = getPackageInfoForPackage(javaClass);

            XMLNameTransformer transformer = info.getXmlNameTransformer();
            if(transformer == TypeInfo.DEFAULT_NAME_TRANSFORMER){
                XMLNameTransformer nsInfoXmlNameTransformer = packageInfo.getXmlNameTransformer();

                if (nsInfoXmlNameTransformer != null) {
                    info.setXmlNameTransformer(nsInfoXmlNameTransformer);
                } else if (helper.isAnnotationPresent(javaClass, XmlNameTransformer.class)) {
                    XmlNameTransformer nameTranformer = (XmlNameTransformer) helper.getAnnotation(javaClass, XmlNameTransformer.class);
                    Class nameTransformerClass = nameTranformer.value();
                    try {
                        info.setXmlNameTransformer((XMLNameTransformer) nameTransformerClass.newInstance());
                    } catch (InstantiationException ex) {
                        throw JAXBException.exceptionWithNameTransformerClass(nameTransformerClass.getName(), ex);
                    } catch (IllegalAccessException ex) {
                        throw JAXBException.exceptionWithNameTransformerClass(nameTransformerClass.getName(), ex);
                    }
                } else if (helper.isAnnotationPresent(javaClass.getPackage(), XmlNameTransformer.class)) {
                    XmlNameTransformer nameTranformer = (XmlNameTransformer) helper.getAnnotation(javaClass.getPackage(), XmlNameTransformer.class);
                    Class nameTransformerClass = nameTranformer.value();
                    try {
                        info.setXmlNameTransformer((XMLNameTransformer) nameTransformerClass.newInstance());
                    } catch (InstantiationException ex) {
                        throw JAXBException.exceptionWithNameTransformerClass(nameTransformerClass.getName(), ex);
                    } catch (IllegalAccessException ex) {
                        throw JAXBException.exceptionWithNameTransformerClass(nameTransformerClass.getName(), ex);
                    }
                }
            }

            // handle @XmlAccessorType
            postProcessXmlAccessorType(info, packageInfo);

            // handle @XmlType
            postProcessXmlType(javaClass, info, packageInfo);

            // handle @XmlEnum
            if (info.isEnumerationType()) {
                addEnumTypeInfo(javaClass, ((EnumTypeInfo) info));
                continue;
            }

            // process schema type name
            processTypeQName(javaClass, info, packageInfo.getNamespaceInfo());

            // handle superclass if necessary
            JavaClass superClass = (JavaClass) javaClass.getSuperclass();
            processReferencedClass(superClass);
            processPropertiesSuperClass(javaClass, info);

            // add properties
            info.setProperties(getPropertiesForClass(javaClass, info));

            // process properties
            processTypeInfoProperties(javaClass, info);

            // handle @XmlAccessorOrder
            postProcessXmlAccessorOrder(info, packageInfo);

            validatePropOrderForInfo(info);
        }
        return typeInfo;
    }

    private TypeInfo processReferencedClass(JavaClass referencedClass){
        if (shouldGenerateTypeInfo(referencedClass)) {            
            TypeInfo existingInfo = typeInfo.get(referencedClass.getQualifiedName());
            if(existingInfo == null || !existingInfo.isPreBuilt()){
                PackageInfo pInfo = getPackageInfoForPackage(referencedClass);                 
                JavaClass adapterClass = pInfo.getPackageLevelAdaptersByClass().get(referencedClass);                
                if(adapterClass == null){                                   
                   CompilerHelper.addClassToClassLoader(referencedClass, helper.getClassLoader());
                   JavaClass[] jClassArray = new JavaClass[] { referencedClass };
                   buildNewTypeInfo(jClassArray);
                }
                return typeInfo.get(referencedClass.getQualifiedName());
            }else{
                if(existingInfo !=null && !existingInfo.isPostBuilt()){
                    PackageInfo pInfo = getPackageInfoForPackage(referencedClass);                 
                    JavaClass adapterClass = pInfo.getPackageLevelAdaptersByClass().get(referencedClass);                
                    if(adapterClass == null){                                   
                        CompilerHelper.addClassToClassLoader(referencedClass, helper.getClassLoader());
                        JavaClass[] javaClasses = new JavaClass[] { referencedClass };
                        javaClasses = postBuildTypeInfo(javaClasses);
                        for(JavaClass next:javaClasses) {
                            processPropertyTypes(next);
                        }
                    }                    
                }
                return existingInfo;
            }
        }
        return null;
     }
    
    /*
     * Get virtual property and XmlID information from parent and set it on info if available
     */
    public void processPropertiesSuperClass(JavaClass cls, TypeInfo info) {
        JavaClass superClass = (JavaClass) cls.getSuperclass();
        if(superClass == null) {
            return;
        }
        TypeInfo superClassInfo = this.typeInfo.get(superClass.getQualifiedName());
        if(superClassInfo != null) {
        	processPropertiesSuperClass(superClass, superClassInfo);
        	classesToProcessPropertyTypes.add(superClass);
            if(superClassInfo.getXmlVirtualAccessMethods() != null && info.getXmlVirtualAccessMethods() == null) {
                info.setXmlVirtualAccessMethods(superClassInfo.getXmlVirtualAccessMethods());
            }
            if(superClassInfo.isIDSet()){
            	info.setIDProperty(superClassInfo.getIDProperty());
            }
        }
    }

    /**
     * Perform any final generation and/or validation operations on TypeInfo
     * properties.
     * 
     */
    public void finalizeProperties() {    
    	
        for (TypeInfo tInfo: getTypeInfo().values()) {
            // don't need to validate props on a transient class at this point
            if (tInfo.isTransient()) {
                continue;
            }
            JavaClass jClass = tInfo.getJavaClass();
            String[] propOrder = tInfo.getPropOrder();
            boolean hasPropOrder = propOrder.length > 0 && !(propOrder.length == 1 && propOrder[0].equals(Constants.EMPTY_STRING));                               
            // If a property is marked transient, ensure it doesn't exist in the propOrder
            List<String> propOrderList = Arrays.asList(tInfo.getPropOrder());
            ArrayList<Property> propsList = tInfo.getPropertyList();
            for (int i = 0; i < propsList.size(); i++) {
                Property p = propsList.get(i);
                if (p.isTransient() && propOrderList.contains(p.getPropertyName())) {
                     throw org.eclipse.persistence.exceptions.JAXBException.transientInProporder(p.getPropertyName());
                }
                if(hasPropOrder && !p.isAttribute() && !p.isTransient() && !p.isInverseReference()){
                    if (!propOrderList.contains(p.getPropertyName())) {
                        throw JAXBException.missingPropertyInPropOrder(p.getPropertyName(), tInfo.getJavaClassName());
                    }
                }
            }                        
            
            if (!jClass.isInterface() && !tInfo.isEnumerationType() && !jClass.isAbstract()) {
                if (tInfo.getFactoryMethodName() == null && tInfo.getObjectFactoryClassName() == null) {
                    JavaConstructor zeroArgConstructor = jClass.getDeclaredConstructor(new JavaClass[] {});
                    if (zeroArgConstructor == null) {
                        if (tInfo.isSetXmlJavaTypeAdapter()) {
                            tInfo.setTransient(true);
                        } else {
                        	if(!referencedByTransformer.contains(jClass.getName())){
                        		throw org.eclipse.persistence.exceptions.JAXBException.factoryMethodOrConstructorRequired(jClass.getName());
                        	}
                        }
                    }
                }
            }
            // validate XmlValue
            if (tInfo.getXmlValueProperty() != null) {
                validateXmlValueFieldOrProperty(jClass, tInfo.getXmlValueProperty());
            }

            // Keep a list of "any" properties to verify if multiples exist
            // that they have different element wrappers
            List<Property> anyElementProperties = new ArrayList<Property>();
            
            for (Property property : tInfo.getPropertyList()) {
                // Check that @XmlAttribute references a Java type that maps to text in XML
                if (property.isAttribute()) {
                    validateXmlAttributeFieldOrProperty(tInfo, property);
                }

                JavaClass typeClass = property.getActualType();
            
            	if(property.isChoice()){
            		Collection<Property> choiceProps = property.getChoiceProperties();
            		Iterator<Property> choicePropsIter = choiceProps.iterator();
            		while(choicePropsIter.hasNext()){
            			Property nextChoiceProp = choicePropsIter.next();
            			JavaClass nextChoicePropTypeClass = nextChoiceProp.getActualType();
            			TypeInfo targetInfo = typeInfo.get(nextChoicePropTypeClass.getQualifiedName());
            			finalizeProperty(property, targetInfo, nextChoicePropTypeClass, jClass);
            		}
            	}else{            		
                    TypeInfo targetInfo = typeInfo.get(typeClass.getQualifiedName());
            		finalizeProperty(property, targetInfo, typeClass, jClass);
            	}           	
            
                // only one XmlValue is allowed per class, and if there is one
                // only XmlAttributes are allowed
                if (tInfo.isSetXmlValueProperty()) {
                    if (property.isXmlValue() && !(tInfo.getXmlValueProperty().getPropertyName().equals(property.getPropertyName()))) {
                        throw JAXBException.xmlValueAlreadySet(property.getPropertyName(), tInfo.getXmlValueProperty().getPropertyName(), jClass.getName());
                    }
                    if (!property.isXmlValue() && !property.isAttribute() && !property.isInverseReference() && !property.isTransient()) {
                        throw JAXBException.propertyOrFieldShouldBeAnAttribute(property.getPropertyName());
                    }
                }
                
                
                // handle XmlElementRef(s) - validate and build the required
                // ElementDeclaration object
                if (property.isReference()) {
                    processReferenceProperty(property, tInfo, jClass);
                }
                 
                if (property.isSwaAttachmentRef() && !this.hasSwaRef) {
                    this.hasSwaRef = true;
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
                if (property.isAny()) {
                    if(!anyElementProperties.isEmpty()) {
                        for(Property nextAny:anyElementProperties) {
                            if(!property.isSetXmlElementWrapper() && !nextAny.isSetXmlElementWrapper()) {
                                throw JAXBException.xmlAnyElementAlreadySet(property.getPropertyName(), nextAny.getPropertyName(), jClass.getName());
                            }
                            org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper wrapper = property.getXmlElementWrapper();
                            org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper targetWrapper = nextAny.getXmlElementWrapper();
                            if(wrapper != null && targetWrapper != null) {
                                if(wrapper.getName().equals(targetWrapper.getName()) && wrapper.getNamespace().equals(targetWrapper.getNamespace())) {
                                    throw JAXBException.xmlAnyElementAlreadySet(property.getPropertyName(), nextAny.getPropertyName(), jClass.getName());
                                }
                            }
                        }
                    }
                    anyElementProperties.add(property);
                }
                // an XmlAttachmentRef can only appear on a DataHandler property
                if (property.isSwaAttachmentRef() && !areEquals(property.getActualType(), JAVAX_ACTIVATION_DATAHANDLER)) {
                    throw JAXBException.invalidAttributeRef(property.getPropertyName(), jClass.getQualifiedName());
                }
                // an XmlElementWrapper can only appear on a Collection or Array
                if (property.getXmlElementWrapper() != null) {
                    if (!helper.isCollectionType(property.getType()) && !property.getType().isArray() && !helper.isMapType(property.getType())) {
                        throw JAXBException.invalidElementWrapper(property.getPropertyName());
                    }
                }

                // handle XmlTransformation - validate transformer class/method
                if (property.isXmlTransformation()) {
                    processXmlTransformationProperty(property);
                }
                // validate XmlJoinNodes
                if (property.isSetXmlJoinNodes()) {
                    TypeInfo targetInfo = typeInfo.get(typeClass.getQualifiedName());

                    // the target class must have an associated TypeInfo
                    if (targetInfo == null) {
                        throw JAXBException.invalidXmlJoinNodeReferencedClass(property.getPropertyName(), typeClass.getQualifiedName());
                    }
                    // validate each referencedXmlPath - target TypeInfo should
                    // have XmlID/XmlKey property with matching XmlPath
                    if (targetInfo.getIDProperty() == null && targetInfo.getXmlKeyProperties() == null) {
                        throw JAXBException.noKeyOrIDPropertyOnJoinTarget(jClass.getQualifiedName(), property.getPropertyName(), typeClass.getQualifiedName());
                    }
                }
            }
        }
    }

    private void finalizeProperty(Property property, TypeInfo targetInfo, JavaClass typeClass, JavaClass jClass){
            if (targetInfo != null && targetInfo.isTransient() && property.getXmlElements() == null) {
                property.setTransientType(true);
            }
            
            // validate XmlIDREF
            if (property.isXmlIdRef()) {
                // the target class must have an associated TypeInfo unless
                // it is Object
                if (targetInfo == null && !typeClass.getQualifiedName().equals(JAVA_LANG_OBJECT)) {
                    throw JAXBException.invalidIDREFClass(jClass.getQualifiedName(), property.getPropertyName(), typeClass.getQualifiedName());
                }
                // if the property is an XmlIDREF, the target must have an
                // XmlID set
                if (targetInfo != null && targetInfo.getIDProperty() == null) {
                    throw JAXBException.invalidIdRef(property.getPropertyName(), typeClass.getQualifiedName());
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

            // handle @XmlIDREF - validate these properties after processing of
            // all types is completed
            processXmlIDREF(property);

            if (property.isMap()){
                processReferencedClass(property.getKeyType());
                processReferencedClass(property.getValueType());
            }
        }
    }

    void processPropertyTypes(JavaClass[] classes) {
        for (JavaClass next : classes) {        	
        	processPropertyTypes(next);  
        	classesToProcessPropertyTypes.remove(next);
        }
        for (int i =0; i< classesToProcessPropertyTypes.size(); i++){
        	JavaClass next = classesToProcessPropertyTypes.get(i);        	
        	processPropertyTypes(next);
        }
    }
    	
    private void processPropertyTypes(JavaClass next){
    
        TypeInfo info = getTypeInfo().get(next.getQualifiedName());
        if (info != null) {
        	
            for (Property property : info.getPropertyList()) {
            	
                if (property.isXmlLocation()) {
                    info.setLocationAware(true);
                }
            	
            	if (property.isTransient()) {
                    continue;
                }
                JavaClass type = property.getActualType();
                
                if(property.isReference()) {
                    processReferencePropertyTypes(property, info, next);
                }
                
            	
                if (property.isChoice()) {
                	
                    processChoiceProperty(property, info, next, type);
                    for (Property choiceProp : property.getChoiceProperties()) {
                        type = choiceProp.getActualType();
                        processReferencedClass(type);
                    }
                }else{
                    processReferencedClass(type);
                }                
            }
        }
        
    }

    /**
     * This method was initially designed to handle processing one or more
     * JavaClass instances. Over time its functionality has been broken apart
     * and handled in different methods. Its sole purpose now is to check for
     * callback methods.
     * 
     * @param classes
     *            this paramater can and should be null as it is not used
     */
    public void processJavaClasses(JavaClass[] classes) {
        checkForCallbackMethods();
    }

    /**
     * Process any additional classes, such as inner classes, @XmlRegistry or
     * from @XmlSeeAlso.
     * 
     * @param classes
     * @return
     */
    private JavaClass[] processAdditionalClasses(JavaClass[] classes) {
        ArrayList<JavaClass> extraClasses = new ArrayList<JavaClass>();
        ArrayList<JavaClass> classesToProcess = new ArrayList<JavaClass>();
        for (JavaClass jClass : classes) {
            List<TypeMappingInfo> infos = this.javaClassToTypeMappingInfos.get(jClass);
            if(infos != null && infos.size() > 0) {
                for(TypeMappingInfo next:infos) {
                    processAdditionalClasses(jClass, next, extraClasses, classesToProcess);
                }
            } else {
                processAdditionalClasses(jClass, null, extraClasses, classesToProcess);
            }
        }
        // process @XmlRegistry, @XmlSeeAlso and inner classes
        for (JavaClass javaClass : extraClasses) {
            processClass(javaClass, classesToProcess);
        }

        return classesToProcess.toArray(new JavaClass[classesToProcess.size()]);
    }
    
    private void processAdditionalClasses(JavaClass cls, TypeMappingInfo tmi, ArrayList<JavaClass> extraClasses, ArrayList<JavaClass> classesToProcess) {
        Class xmlElementType = null;
        JavaClass javaClass = cls;
        if (tmi != null) {
            Class adapterClass = this.typeMappingInfoToAdapterClasses.get(tmi);
            if (adapterClass != null) {
                JavaClass adapterJavaClass = helper.getJavaClass(adapterClass);
                JavaClass newType = helper.getJavaClass(Object.class);

                // look for marshal method
                for (Object nextMethod : adapterJavaClass.getDeclaredMethods()) {
                    JavaMethod method = (JavaMethod) nextMethod;
                    if (method.getName().equals("marshal")) {
                        JavaClass returnType = method.getReturnType();
                        if (!returnType.getQualifiedName().equals(newType.getQualifiedName())) {
                            newType = (JavaClass) returnType;
                            break;
                        }
                    }
                }
                if (!helper.isBuiltInJavaType(javaClass)) {
                    extraClasses.add(javaClass);
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

        if (areEquals(javaClass, byte[].class) || areEquals(javaClass, JAVAX_ACTIVATION_DATAHANDLER) || areEquals(javaClass, Source.class) || areEquals(javaClass, Image.class) || areEquals(javaClass, JAVAX_MAIL_INTERNET_MIMEMULTIPART)) {
            if (tmi == null || tmi.getXmlTagName() == null) {
                ElementDeclaration declaration = new ElementDeclaration(null, javaClass, javaClass.getQualifiedName(), false, XmlElementDecl.GLOBAL.class);
                declaration.setTypeMappingInfo(tmi);
                this.localElements.add(declaration);
            }
        } else if (javaClass.isArray()) {
            if (!helper.isBuiltInJavaType(javaClass.getComponentType())) {
                extraClasses.add(javaClass.getComponentType());
            }
            Class generatedClass;
            if (null == tmi) {
                generatedClass = arrayClassesToGeneratedClasses.get(javaClass.getName());
            } else {
                generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses, helper.getClassLoader());
            }
            if (generatedClass == null) {
                generatedClass = generateWrapperForArrayClass(javaClass, tmi, xmlElementType, extraClasses);
                extraClasses.add(helper.getJavaClass(generatedClass));
                arrayClassesToGeneratedClasses.put(javaClass.getName(), generatedClass);
            }
            generatedClassesToArrayClasses.put(generatedClass, javaClass);
            typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);

        } else if (helper.isCollectionType(javaClass)) {
            JavaClass componentClass;
            Collection args = javaClass.getActualTypeArguments();
            if (args.size() >0) {
                componentClass = (JavaClass) args.iterator().next();
                if (!componentClass.isPrimitive()) {
                    extraClasses.add(componentClass);
                }
            } else {
                componentClass = helper.getJavaClass(Object.class);
            }

            Class generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses, helper.getClassLoader());
            if (generatedClass == null) {
                generatedClass = generateCollectionValue(javaClass, tmi, xmlElementType, extraClasses);
                extraClasses.add(helper.getJavaClass(generatedClass));
            }
            typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);
        } else if (helper.isMapType(javaClass)) {
            JavaClass keyClass;
            JavaClass valueClass;
            Collection args = javaClass.getActualTypeArguments();
            Iterator argsIter = args.iterator();
            if (args.size() > 1) {
                keyClass = (JavaClass) argsIter.next();
                if (!helper.isBuiltInJavaType(keyClass)) {
                    extraClasses.add(keyClass);
                }
                valueClass = (JavaClass) argsIter.next();
                if (!helper.isBuiltInJavaType(valueClass)) {
                    extraClasses.add(valueClass);
                }
            } else {
                keyClass = helper.getJavaClass(Object.class);
                valueClass = helper.getJavaClass(Object.class);
            }

            Class generatedClass = CompilerHelper.getExisitingGeneratedClass(tmi, typeMappingInfoToGeneratedClasses, typeMappingInfoToAdapterClasses, helper.getClassLoader());
            if (generatedClass == null) {
                generatedClass = generateWrapperForMapClass(javaClass, keyClass, valueClass, tmi);
                extraClasses.add(helper.getJavaClass(generatedClass));
            }
            typeMappingInfoToGeneratedClasses.put(tmi, generatedClass);
        } else {
            // process @XmlRegistry, @XmlSeeAlso and inner classes
            processClass(javaClass, classesToProcess);
        }
    }

    /**
     * Adds additional classes to the given List, from inner classes,
     * 
     * @XmlRegistry or @XmlSeeAlso.
     * 
     * @param javaClass
     * @param classesToProcess
     */
    private void processClass(JavaClass javaClass, ArrayList<JavaClass> classesToProcess) {
        if (shouldGenerateTypeInfo(javaClass) ){
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
            }
        }
    }

    /**
     * Process an @XmlSeeAlso annotation. TypeInfo instances will be created for
     * each class listed.
     * 
     * @param javaClass
     */
    private void processXmlSeeAlso(JavaClass javaClass, TypeInfo info) {
        // reflectively load @XmlSeeAlso class to avoid dependency
        Class xmlSeeAlsoClass = null;
        Method valueMethod = null;
        try {
            xmlSeeAlsoClass = PrivilegedAccessHelper.getClassForName("javax.xml.bind.annotation.XmlSeeAlso", false, helper.getClassLoader());
            valueMethod = PrivilegedAccessHelper.getDeclaredMethod(xmlSeeAlsoClass, "value", new Class[] {});
        } catch (ClassNotFoundException ex) {
            // Ignore this exception. If SeeAlso isn't available, don't try to
            // process
        } catch (NoSuchMethodException ex) {
        }
        if (xmlSeeAlsoClass != null && helper.isAnnotationPresent(javaClass, xmlSeeAlsoClass)) {
            Object seeAlso = helper.getAnnotation(javaClass, xmlSeeAlsoClass);
            Class[] values = null;
            try {
                values = (Class[]) PrivilegedAccessHelper.invokeMethod(valueMethod, seeAlso, new Object[] {});
            } catch (Exception ex) {
            }

            if (values != null) {
                List<String> seeAlsoClassNames = new ArrayList<String>();                
                for (Class next : values) {
                    seeAlsoClassNames.add(next.getName());
                }
                info.setXmlSeeAlso(seeAlsoClassNames);
            }
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
            info.setObjectFactoryClassName(factoryMethod.getOwningClass().getQualifiedName());
            JavaClass[] paramTypes = factoryMethod.getParameterTypes();
            if (paramTypes != null && paramTypes.length > 0) {
                String[] paramTypeNames = new String[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    processReferencedClass(paramTypes[i]);
                    paramTypeNames[i] = paramTypes[i].getQualifiedName();
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
     * Process @XmlExtensible annotation on a given JavaClass.
     * 
     * @param javaClass
     * @param info
     */
    private void processXmlExtensible(JavaClass javaClass, TypeInfo info) {
        if (helper.isAnnotationPresent(javaClass, XmlVirtualAccessMethods.class)) {
            XmlVirtualAccessMethods extAnnotation = (XmlVirtualAccessMethods) helper.getAnnotation(javaClass, XmlVirtualAccessMethods.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethods xmlExt = new org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethods();
            xmlExt.setGetMethod(extAnnotation.getMethod());
            xmlExt.setSetMethod(extAnnotation.setMethod());
            xmlExt.setSchema(org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethodsSchema.valueOf(extAnnotation.schema().toString()));
            info.setXmlVirtualAccessMethods(xmlExt);
        }
    }

    /**
     * Process @XmlType annotation on a given JavaClass and update the TypeInfo
     * for pre-processing. Note that if no @XmlType annotation is present we
     * still create a new XmlType an set it on the TypeInfo.
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
            xmlType.setNamespace(packageNamespace.getNamespace());
        }
        info.setXmlType(xmlType);
    }

    /**
     * Process XmlType for a given TypeInfo. Here we assume that the TypeInfo
     * has an XmlType set - typically via preProcessXmlType or XmlProcessor
     * override.
     * 
     * @param javaClass
     * @param info
     * @param packageNamespace
     */
    private void postProcessXmlType(JavaClass javaClass, TypeInfo info, PackageInfo packageNamespace) {
        // assumes that the TypeInfo has an XmlType set from
        org.eclipse.persistence.jaxb.xmlmodel.XmlType xmlType = info.getXmlType();

        // set/validate factoryClass and factoryMethod
        String factoryClassName = xmlType.getFactoryClass();
        String factoryMethodName = xmlType.getFactoryMethod();

        if (factoryClassName.equals("javax.xml.bind.annotation.XmlType.DEFAULT")) {
            if (factoryMethodName != null && !factoryMethodName.equals(EMPTY_STRING)) {
                // factory method applies to the current class - verify method
                // exists
                JavaMethod method = javaClass.getDeclaredMethod(factoryMethodName, new JavaClass[] {});
                if (method == null) {
                    throw org.eclipse.persistence.exceptions.JAXBException.factoryMethodNotDeclared(factoryMethodName, javaClass.getName());
                }
                info.setFactoryMethodName(factoryMethodName);
            }
        } else {
            if (factoryMethodName == null || factoryMethodName.equals(EMPTY_STRING)) {
                throw org.eclipse.persistence.exceptions.JAXBException.factoryClassWithoutFactoryMethod(javaClass.getName());
            }
            info.setObjectFactoryClassName(factoryClassName);
            info.setFactoryMethodName(factoryMethodName);
        }

        // figure out type name
        String typeName = xmlType.getName();
        if (typeName.equals(XMLProcessor.DEFAULT)) {
            try {
                typeName = info.getXmlNameTransformer().transformTypeName(javaClass.getName());
            } catch (Exception ex) {
                throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
            }
        }
        info.setSchemaTypeName(typeName);

        // set propOrder
        if (xmlType.isSetPropOrder()) {
            List<String> props = xmlType.getPropOrder();
            if (props.size() == 0) {
                info.setPropOrder(new String[0]);
            } else if (props.get(0).equals(EMPTY_STRING)) {
                info.setPropOrder(new String[] { EMPTY_STRING });
            } else {
                info.setPropOrder(xmlType.getPropOrder().toArray(new String[xmlType.getPropOrder().size()]));
            }
        }

        // figure out namespace
        if (xmlType.getNamespace().equals(XMLProcessor.DEFAULT)) {
            info.setClassNamespace(packageNamespace.getNamespace());
        } else {
            info.setClassNamespace(xmlType.getNamespace());
        }
    }

    /**
     * Process @XmlAccessorType annotation on a given JavaClass and update the
     * TypeInfo for pre-processing.
     * 
     * @param javaClass
     * @param info
     * @param packageNamespace
     */
    private void preProcessXmlAccessorType(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType xmlAccessType;
        if (javaClass.getDeclaredAnnotation(helper.getJavaClass(XmlAccessorType.class)) != null) {
            XmlAccessorType accessorType = (XmlAccessorType) helper.getAnnotation(javaClass, XmlAccessorType.class);
            xmlAccessType = org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType.fromValue(accessorType.value().name());
            info.setXmlAccessType(xmlAccessType);
        }
    }

    /**
     * Post process XmlAccessorType. In some cases, such as @XmlSeeAlso classes,
     * the access type may not have been set
     * 
     * @param info
     */
    private void postProcessXmlAccessorType(TypeInfo info, PackageInfo packageNamespace) {
        if (!info.isSetXmlAccessType()) {
            // Check for super class
            JavaClass next = helper.getJavaClass(info.getJavaClassName()).getSuperclass();
            while (next != null && !(next.getName().equals(JAVA_LANG_OBJECT))) {
                processReferencedClass(next);
                TypeInfo parentInfo = this.typeInfo.get(next.getName());
                if (parentInfo != null && parentInfo.isSetXmlAccessType()) {
                    info.setXmlAccessType(parentInfo.getXmlAccessType());
                    break;
                }
                next = next.getSuperclass();
            }
            // use value in package-info.java as last resort - will default if
            // not set
            if(!(info.isSetXmlAccessType())) {
                info.setXmlAccessType(org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType.fromValue(packageNamespace.getAccessType().name()));
            }
        }
    }

    /**
     * Process package and class @XmlAccessorOrder. Class level annotation
     * overrides a package level annotation.
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
     * Post process XmlAccessorOrder. This method assumes that the given
     * TypeInfo has already had its order set (via annotations in
     * preProcessXmlAccessorOrder or via xml metadata override in XMLProcessor).
     * 
     * @param javaClass
     * @param info
     */
    private void postProcessXmlAccessorOrder(TypeInfo info, PackageInfo packageNamespace) {
        if (!info.isSetXmlAccessOrder()) {
            // use value in package-info.java as last resort - will default if
            // not set
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
            if (element.type() != XmlElement.DEFAULT.class && !(property.isSwaAttachmentRef())) {
                property.setOriginalType(property.getType());
                if (helper.isCollectionType(property.getType()) || property.getType().isArray()) {
                    property.setGenericType(helper.getJavaClass(element.type()));
                } else {
                	JavaClass originalType = property.getType();
                	JavaClass newType =helper.getJavaClass(element.type());
                	if(!originalType.getName().equals(newType.getName())){
                		property.setTyped(true);
                		property.setSchemaType((QName) helper.getXMLToJavaTypeMap().get(newType.getName()));
                	}                		
                    property.setType(newType);
                }
                property.setHasXmlElementType(true);
            }
            // handle default value
            if (!element.defaultValue().equals(ELEMENT_DECL_DEFAULT)) {
                property.setDefaultValue(element.defaultValue());
            }
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
            org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xmlJavaTypeAdapter;
            if (ptypeInfo == null && shouldGenerateTypeInfo(ptype)) {
                if (helper.isAnnotationPresent(ptype, XmlJavaTypeAdapter.class)) {
                    XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(ptype, XmlJavaTypeAdapter.class);
                    org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
                    xja.setValue(adapter.value().getName());
                    String boundType = adapter.type().getName();
                    if (boundType == null || boundType.equals("javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT")) {
                        boundType = ptype.getRawName();
                    }
                    xja.setType(adapter.type().getName());
                    property.setXmlJavaTypeAdapter(xja);
                }
            }
            if (ptypeInfo != null) {
                if (null != (xmlJavaTypeAdapter = ptypeInfo.getXmlJavaTypeAdapter())) {
                    try {
                        property.setXmlJavaTypeAdapter(xmlJavaTypeAdapter);
                    } catch (JAXBException e) {
                        throw JAXBException.invalidTypeAdapterClass(xmlJavaTypeAdapter.getValue(), javaClass.getName());
                    }
                }
            }
            if(info.hasPackageLevelAdaptersByClass()) {
                if (info.getPackageLevelAdaptersByClass().get(ptype.getQualifiedName()) != null && !property.isSetXmlJavaTypeAdapter()) {
                    adapterClass = info.getPackageLevelAdapterClass(ptype);

                    org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
                    xja.setValue(adapterClass.getQualifiedName());
                    xja.setType(ptype.getQualifiedName());
                    property.setXmlJavaTypeAdapter(xja);
                }
            }
        }
    }

    /**
     * Store a QName (if necessary) based on a given TypeInfo's schema type
     * name.
     * 
     * @param javaClass
     * @param info
     */
    private void processTypeQName(JavaClass javaClass, TypeInfo info, NamespaceInfo packageNamespace) {
        if(info.isTransient()) {
            return;
        }
        String typeName = info.getSchemaTypeName();
        if (typeName != null && !(EMPTY_STRING.equals(typeName))) {
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
            if (!(javaClassName.equals(CoreClassConstants.APBYTE.getName()))&& !(javaClassName.equals(CoreClassConstants.ABYTE.getName()))) {
                return true;
            }
        }
        if (helper.isBuiltInJavaType(javaClass)) {
            return false;
        }
        if (helper.isCollectionType(javaClass) || helper.isMapType(javaClass)) {
            return false;
        }
        return true;
    }

    public ArrayList<Property> getPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> returnList = new ArrayList<Property>();

        if (!info.isTransient()) {
            JavaClass superClass = cls.getSuperclass();
            if (null != superClass) {
                TypeInfo superClassInfo = typeInfo.get(superClass.getQualifiedName());
                ArrayList<Property> superClassProperties;
                while (superClassInfo != null && superClassInfo.isTransient()) {
                    if (info.getXmlAccessType() == XmlAccessType.FIELD) {
                        superClassProperties = getFieldPropertiesForClass(superClass, superClassInfo, false); 
                    } else if (info.getXmlAccessType() == XmlAccessType.PROPERTY) {
                        superClassProperties = getPropertyPropertiesForClass(superClass, superClassInfo, false);
                    } else if (info.getXmlAccessType() == XmlAccessType.PUBLIC_MEMBER) {
                        superClassProperties = getPublicMemberPropertiesForClass(superClass, superClassInfo);
                    } else {
                        superClassProperties = getNoAccessTypePropertiesForClass(superClass, superClassInfo);
                    }
                    superClass = superClass.getSuperclass();
                    superClassInfo = typeInfo.get(superClass.getQualifiedName());
                    for(Property next:superClassProperties) {
                        next.setIsSuperClassProperty(true);
                    }
                    returnList.addAll(0, superClassProperties);
                }
            }
        }

        if (info.isTransient()) {
            returnList.addAll(getNoAccessTypePropertiesForClass(cls, info));
        } else if (info.getXmlAccessType() == XmlAccessType.FIELD) {
            returnList.addAll(getFieldPropertiesForClass(cls, info, false));
            returnList.addAll(getPropertyPropertiesForClass(cls, info, false, true));
        } else if (info.getXmlAccessType() == XmlAccessType.PROPERTY) {
            returnList.addAll(getFieldPropertiesForClass(cls, info, false, true));
            returnList.addAll(getPropertyPropertiesForClass(cls, info, false));
        } else if (info.getXmlAccessType() == XmlAccessType.PUBLIC_MEMBER) {
            returnList.addAll(getPublicMemberPropertiesForClass(cls, info));
        } else {
            returnList.addAll(getNoAccessTypePropertiesForClass(cls, info));
        }
        return returnList;
    }

    public ArrayList<Property> getFieldPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        return getFieldPropertiesForClass(cls, info, onlyPublic, false);
    }

    public ArrayList<Property> getFieldPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic, boolean onlyExplicit) {
        ArrayList<Property> properties = new ArrayList<Property>();
        if (cls == null) {
            return properties;
        }

        for (Iterator<JavaField> fieldIt = cls.getDeclaredFields().iterator(); fieldIt.hasNext();) {
        	Property property = null;
            JavaField nextField = fieldIt.next();
            int modifiers = nextField.getModifiers();

            if (!Modifier.isTransient(modifiers) && ((Modifier.isPublic(nextField.getModifiers()) && onlyPublic) || !onlyPublic ||hasJAXBAnnotations(nextField))) {
                if (!Modifier.isStatic(modifiers)) {
                    if ((onlyExplicit && hasJAXBAnnotations(nextField)) || !onlyExplicit) {
                        try {
                            property = buildNewProperty(info, cls, nextField, nextField.getName(), nextField.getResolvedType());
                            properties.add(property);
                        } catch(JAXBException ex) {
                            if(ex.getErrorCode() != JAXBException.INVALID_INTERFACE || !helper.isAnnotationPresent(nextField, XmlTransient.class)) {
                                throw ex;
                            }
                        }
                    }
                } else {
                    try {
                        property = buildNewProperty(info, cls, nextField, nextField.getName(), nextField.getResolvedType());
                        if (helper.isAnnotationPresent(nextField, XmlAttribute.class)) {
                            Object value = ((JavaFieldImpl) nextField).get(null);
                            if (value != null) {
                                String stringValue = (String) XMLConversionManager.getDefaultXMLManager().convertObject(value, String.class, property.getSchemaType());
                                property.setFixedValue(stringValue);
                            }
                        }
                        property.setWriteOnly(true);
                        if(!hasJAXBAnnotations(nextField)) {
                            property.setTransient(true);
                        }
                        properties.add(property);
                    } catch (ClassCastException e) {
                        // do Nothing
                    } catch (IllegalAccessException e) {
                        // do Nothing
                    } catch(JAXBException ex) {
                        if(ex.getErrorCode() != JAXBException.INVALID_INTERFACE || !helper.isAnnotationPresent(nextField, XmlTransient.class)) {
                            throw ex;
                        }
                    }
                }
            }

            if (helper.isAnnotationPresent(nextField, XmlTransient.class)) {
            	if(property != null){
            		property.setTransient(true);
            	}
            }
        }
        return properties;
    }

    /*
     * Create a new Property Object and process the annotations that are common
     * to fields and methods
     */
    Property buildNewProperty(TypeInfo info, JavaClass cls, JavaHasAnnotations javaHasAnnotations, String propertyName, JavaClass ptype) {
        Property property = null;
        if (helper.isAnnotationPresent(javaHasAnnotations, XmlElements.class)) {
            property = buildChoiceProperty(javaHasAnnotations);
        } else if (helper.isAnnotationPresent(javaHasAnnotations, XmlElementRef.class) || helper.isAnnotationPresent(javaHasAnnotations, XmlElementRefs.class)) {
        	
        	findAndProcessObjectFactory(cls);        	
        	
            property = buildReferenceProperty(info, javaHasAnnotations, propertyName, ptype);
            if (helper.isAnnotationPresent(javaHasAnnotations, XmlAnyElement.class)) {
                XmlAnyElement anyElement = (XmlAnyElement) helper.getAnnotation(javaHasAnnotations, XmlAnyElement.class);
                property.setIsAny(true);
                if (anyElement.value() != null) {
                    property.setDomHandlerClassName(anyElement.value().getName());
                }
                property.setLax(anyElement.lax());
                info.setAnyElementPropertyName(propertyName);
            }
        } else if (helper.isAnnotationPresent(javaHasAnnotations, XmlAnyElement.class)) {
        	findAndProcessObjectFactory(cls); 
            XmlAnyElement anyElement = (XmlAnyElement) helper.getAnnotation(javaHasAnnotations, XmlAnyElement.class);
            property = new Property(helper);
            property.setIsAny(true);
            if (anyElement.value() != null) {
                property.setDomHandlerClassName(anyElement.value().getName());
            }
            property.setLax(anyElement.lax());
            info.setAnyElementPropertyName(propertyName);
        } else if (helper.isAnnotationPresent(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlTransformation.class) || helper.isAnnotationPresent(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlReadTransformer.class) || helper.isAnnotationPresent(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlWriteTransformer.class) || helper.isAnnotationPresent(javaHasAnnotations, XmlWriteTransformers.class)) {
            property = buildTransformationProperty(javaHasAnnotations, cls);
        } else {
            property = new Property(helper);
        }
        property.setPropertyName(propertyName);
        property.setElement(javaHasAnnotations);

        // if there is a TypeInfo for ptype check it for transient, otherwise
        // check the class        
        if (helper.isCollectionType(ptype)) {
            JavaClass componentType = helper.getJavaClass(Object.class);;
            
            Collection typeArgs =  ptype.getActualTypeArguments();
            if(typeArgs.size() > 0) {
                componentType = (JavaClass) typeArgs.iterator().next();                    
            }            
            updatePropertyType(property, ptype, componentType);
        }else{
            updatePropertyType(property, ptype, ptype);
        }
    	
        if(helper.isAnnotationPresent(javaHasAnnotations, XmlVariableNode.class)){
            XmlVariableNode variableNode = (XmlVariableNode) helper.getAnnotation(javaHasAnnotations, XmlVariableNode.class);
            if(variableNode.type() != XmlVariableNode.DEFAULT.class){
            	property.setVariableClassName(variableNode.type().getName());            	
                            	
            	JavaClass componentType = helper.getJavaClass(variableNode.type());
            	
            	if(helper.isCollectionType(ptype)){
            		property.setGenericType(componentType);
            	}else{
            		property.setType(componentType);
            	}
            	
            }
            if(!variableNode.value().equals("##default")){
            	property.setVariableAttributeName(variableNode.value());	
            }
            property.setVariableNodeAttribute(variableNode.attribute());
        }
        
        
        if((ptype.isArray()  && !areEquals(ptype, byte[].class))  || (helper.isCollectionType(ptype) && !helper.isAnnotationPresent(javaHasAnnotations, XmlList.class)) ){
        	property.setNillable(true);
        }
        processPropertyAnnotations(info, cls, javaHasAnnotations, property);

        if (helper.isAnnotationPresent(javaHasAnnotations, XmlPath.class)) {
            XmlPath xmlPath = (XmlPath) helper.getAnnotation(javaHasAnnotations, XmlPath.class);
            property.setXmlPath(xmlPath.value()); 
            Field tempField = new XMLField(xmlPath.value());
            boolean isAttribute = tempField.getLastXPathFragment().isAttribute();
            property.setIsAttribute(isAttribute);
            // set schema name
            String schemaName = XMLProcessor.getNameFromXPath(xmlPath.value(), property.getPropertyName(), isAttribute);
            QName qName;
            NamespaceInfo nsInfo = getPackageInfoForPackage(cls).getNamespaceInfo();
            if(isAttribute){
            	if (nsInfo.isAttributeFormQualified()) {
                    qName = new QName(nsInfo.getNamespace(), schemaName);
                } else {
                    qName = new QName(schemaName);
                }
            }else{
                if (nsInfo.isElementFormQualified()) {
                    qName = new QName(nsInfo.getNamespace(), schemaName);
                } else {
                    qName = new QName(schemaName);
                }
            }
            property.setSchemaName(qName);
            //create properties for any predicates
            XPathFragment fragment = tempField.getXPathFragment();
            String currentPath = "";
            while(fragment != null && !(fragment.nameIsText()) && !(fragment.isAttribute())) {
                if(fragment.getPredicate() != null) {
                    //can't append xpath directly since it will contain the predicate
                    String fragmentPath = fragment.getLocalName();
                    if(fragment.getPrefix() != null && !(Constants.EMPTY_STRING.equals(fragment.getPrefix()))) {
                        fragmentPath = fragment.getPrefix() + ":" + fragmentPath;
                    }
                    currentPath += fragmentPath;
                    String predicatePath = currentPath;
                    TypeInfo targetInfo = info;
                    if(fragment.getNextFragment() == null) {
                        //if this is the last fragment, and there's no text after, then this is
                        //complex. May need to add the attribute property to the target type.
                        processReferencedClass(ptype);
                        TypeInfo predicateTypeInfo = typeInfo.get(ptype.getQualifiedName());
                        if(predicateTypeInfo != null) {
                            targetInfo = predicateTypeInfo;
                            predicatePath = "";
                        }
                    }
                    Property predicateProperty = new Property(helper);
                    predicateProperty.setType(helper.getJavaClass("java.lang.String"));
                    if(predicatePath.length() > 0) {
                        predicatePath += "/";
                    }
                    predicatePath += fragment.getPredicate().getXPathFragment().getXPath();
                    predicateProperty.setXmlPath(predicatePath);
                    predicateProperty.setIsAttribute(true);

                    String predschemaName = XMLProcessor.getNameFromXPath(predicatePath, property.getPropertyName(), true);
                    QName predQname;
                    if (nsInfo.isAttributeFormQualified()) {
                        predQname = new QName(nsInfo.getNamespace(), predschemaName);
                    } else {
                        predQname = new QName(predschemaName);
                    }
                    predicateProperty.setSchemaName(predQname);

                    if(!targetInfo.hasPredicateProperty(predicateProperty)) {
                        targetInfo.getPredicateProperties().add(predicateProperty);
                    }
                } else {
                    currentPath += fragment.getXPath();
                }
                currentPath += "/";
                fragment = fragment.getNextFragment();
            }
            
        } else {
            property.setSchemaName(getQNameForProperty(property, propertyName, javaHasAnnotations, getPackageInfoForPackage(cls).getNamespaceInfo(), info));
        }

        ptype = property.getActualType();
        if (ptype.isPrimitive()) {
            if (property.getType().isArray() && helper.isAnnotationPresent(javaHasAnnotations, XmlElement.class)) {
                XmlElement elemAnno = (XmlElement) helper.getAnnotation(javaHasAnnotations, XmlElement.class);
                property.setIsRequired(elemAnno.required());
            } else {
                property.setIsRequired(true);
            }
        }

        // apply class level adapters - don't override property level adapter
        if (!property.isSetXmlJavaTypeAdapter()) {
            TypeInfo refClassInfo = getTypeInfo().get(ptype.getQualifiedName());
            if (refClassInfo != null && refClassInfo.isSetXmlJavaTypeAdapter()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xmlJavaTypeAdapter = null;
                try {
                    xmlJavaTypeAdapter = refClassInfo.getXmlJavaTypeAdapter();
                    property.setXmlJavaTypeAdapter(refClassInfo.getXmlJavaTypeAdapter());
                } catch (JAXBException e) {
                    throw JAXBException.invalidTypeAdapterClass(xmlJavaTypeAdapter.getValue(), cls.getName());
                }
            }
        }

        if(property.isXmlTransformation()){
        	referencedByTransformer.add(property.getType().getName());
        }
        return property;
    }

    
    private void updatePropertyType(Property property, JavaClass ptype, JavaClass componentType){
        TypeInfo componentTypeInfo = typeInfo.get(componentType);
        if((componentTypeInfo != null && !componentTypeInfo.isTransient()) || !helper.isAnnotationPresent(componentType, XmlTransient.class)){
            property.setType(ptype);
        }else{
            JavaClass parent = componentType.getSuperclass();
            while (parent != null) {
                if (parent.getName().equals(JAVA_LANG_OBJECT)) {
                    property.setTransientType(true);
                    property.setType(ptype);
                    break;
                }
                // if there is a TypeInfo for parent check it for transient,
                // otherwise check the class
                TypeInfo parentTypeInfo = typeInfo.get(parent.getQualifiedName());
                if ((parentTypeInfo != null && !parentTypeInfo.isTransient()) || !helper.isAnnotationPresent(parent, XmlTransient.class)) {
                    property.setType(parent);
                    break;
                }
                parent = parent.getSuperclass();
            }
        }
    }
    
    /**
     * Build a new 'choice' property. Here, we flag a new property as a 'choice'
     * and create/set an XmlModel XmlElements object based on the @XmlElements
     * annotation.
     * 
     * Validation and building of the XmlElement properties will be done during
     * finalizeProperties in the processChoiceProperty method.
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

        // handle XmlElementsJoinNodes
        if (helper.isAnnotationPresent(javaHasAnnotations, XmlElementsJoinNodes.class)) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes xmlJoinNodes;
            org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode xmlJoinNode;
            List<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes> xmlJoinNodesList = new ArrayList<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes>();
            List<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode> xmlJoinNodeList = null;

            for (XmlJoinNodes xmlJNs : ((XmlElementsJoinNodes) helper.getAnnotation(javaHasAnnotations, XmlElementsJoinNodes.class)).value()) {
                xmlJoinNodeList = new ArrayList<org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode>();
                for (XmlJoinNode xmlJN : xmlJNs.value()) {
                    xmlJoinNode = new org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes.XmlJoinNode();
                    xmlJoinNode.setXmlPath(xmlJN.xmlPath());
                    xmlJoinNode.setReferencedXmlPath(xmlJN.referencedXmlPath());
                    xmlJoinNodeList.add(xmlJoinNode);
                }
                if (xmlJoinNodeList.size() > 0) {
                    xmlJoinNodes = new org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes();
                    xmlJoinNodes.setXmlJoinNode(xmlJoinNodeList);
                    xmlJoinNodesList.add(xmlJoinNodes);
                }
            }
            choiceProperty.setXmlJoinNodesList(xmlJoinNodesList);
        }
        return choiceProperty;
    }

    private Property buildTransformationProperty(JavaHasAnnotations javaHasAnnotations, JavaClass cls) {
        Property property = new Property(helper);
        org.eclipse.persistence.oxm.annotations.XmlTransformation transformationAnnotation = (org.eclipse.persistence.oxm.annotations.XmlTransformation) helper.getAnnotation(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlTransformation.class);
        XmlTransformation transformation = new XmlTransformation();
        if (transformationAnnotation != null) {
            transformation.setOptional(transformationAnnotation.optional());
        }

        // Read Transformer
        org.eclipse.persistence.oxm.annotations.XmlReadTransformer readTransformer = (org.eclipse.persistence.oxm.annotations.XmlReadTransformer) helper.getAnnotation(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlReadTransformer.class);
        if (readTransformer != null) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer xmlReadTransformer = new org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlReadTransformer();
            if (!(readTransformer.transformerClass() == AttributeTransformer.class)) {
                xmlReadTransformer.setTransformerClass(readTransformer.transformerClass().getName());
            } else if (!(readTransformer.method().equals(EMPTY_STRING))) {
                xmlReadTransformer.setMethod(readTransformer.method());
            }
            transformation.setXmlReadTransformer(xmlReadTransformer);
        }

        // Handle Write Transformers
        org.eclipse.persistence.oxm.annotations.XmlWriteTransformer[] transformers = null;
        if (helper.isAnnotationPresent(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlWriteTransformer.class)) {
            org.eclipse.persistence.oxm.annotations.XmlWriteTransformer writeTransformer = (org.eclipse.persistence.oxm.annotations.XmlWriteTransformer) helper.getAnnotation(javaHasAnnotations, org.eclipse.persistence.oxm.annotations.XmlWriteTransformer.class);
            transformers = new org.eclipse.persistence.oxm.annotations.XmlWriteTransformer[] { writeTransformer };
        } else if (helper.isAnnotationPresent(javaHasAnnotations, XmlWriteTransformers.class)) {
            XmlWriteTransformers writeTransformers = (XmlWriteTransformers) helper.getAnnotation(javaHasAnnotations, XmlWriteTransformers.class);
            transformers = writeTransformers.value();
        }

        if (transformers != null) {
            for (org.eclipse.persistence.oxm.annotations.XmlWriteTransformer next : transformers) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer xmlWriteTransformer = new org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer();
                if (!(next.transformerClass() == FieldTransformer.class)) {
                    xmlWriteTransformer.setTransformerClass(next.transformerClass().getName());
                } else if (!(next.method().equals(EMPTY_STRING))) {
                    xmlWriteTransformer.setMethod(next.method());
                }
                xmlWriteTransformer.setXmlPath(next.xmlPath());
                transformation.getXmlWriteTransformer().add(xmlWriteTransformer);
            }
        }
        property.setXmlTransformation(transformation);
        property.setIsXmlTransformation(true);
        return property;
    }

    /**
     * Complete creation of a 'choice' property. Here, a Property is created for
     * each XmlElement in the XmlElements list. Validation is performed as well.
     * Each created Property is added to the owning Property's list of choice
     * properties.
     * 
     * @param choiceProperty
     * @param info
     * @param cls
     * @param propertyType
     */
    private void processChoiceProperty(Property choiceProperty, TypeInfo info, JavaClass cls, JavaClass propertyType) {
        String propertyName = choiceProperty.getPropertyName();

        // validate XmlElementsXmlJoinNodes (if set)
        if (choiceProperty.isSetXmlJoinNodesList()) {
            // there must be one XmlJoinNodes entry per XmlElement
            if (choiceProperty.getXmlElements().getXmlElement().size() != choiceProperty.getXmlJoinNodesList().size()) {
                throw JAXBException.incorrectNumberOfXmlJoinNodesOnXmlElements(propertyName, cls.getQualifiedName());
            }
        }

        XmlPath[] paths = null;
        if (helper.isAnnotationPresent(choiceProperty.getElement(), XmlPaths.class)) {
            XmlPaths pathAnnotation = (XmlPaths) helper.getAnnotation(choiceProperty.getElement(), XmlPaths.class);
            paths = pathAnnotation.value();
        }
        ArrayList<Property> choiceProperties = new ArrayList<Property>();
        for (int i = 0; i < choiceProperty.getXmlElements().getXmlElement().size(); i++) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlElement next = choiceProperty.getXmlElements().getXmlElement().get(i);
            Property choiceProp = new Property(helper);

            String name;
            String namespace;

            choiceProp.setNillable(next.isNillable());
            choiceProp.setIsRequired(next.isRequired());
            // handle XmlPath - if xml-path is set, we ignore name/namespace
            if (paths != null && next.getXmlPath() == null) {
                // Only set the path, if the path hasn't already been set from
                // xml
                XmlPath nextPath = paths[i];
                next.setXmlPath(nextPath.value());
            }
            if (next.getXmlPath() != null) {
                choiceProp.setXmlPath(next.getXmlPath());
                boolean isAttribute = new XMLField(next.getXmlPath()).getLastXPathFragment().isAttribute();
                // validate attribute - must be in nested path, not at root
                if (isAttribute && !next.getXmlPath().contains(SLASH)) {
                    throw JAXBException.invalidXmlPathWithAttribute(propertyName, cls.getQualifiedName(), next.getXmlPath());
                }
                choiceProp.setIsAttribute(isAttribute);
                name = XMLProcessor.getNameFromXPath(next.getXmlPath(), propertyName, isAttribute);
                namespace = XMLProcessor.DEFAULT;
            } else {
                // no xml-path, so use name/namespace from xml-element
                name = next.getName();
                namespace = next.getNamespace();
            }

            if (name == null || name.equals(XMLProcessor.DEFAULT)) {
                if (next.getJavaAttribute() != null) {
                    name = next.getJavaAttribute();
                } else {
                    name = propertyName;
                }
            }

            // if the property has xml-idref, the target type of each
            // xml-element in the list must have an xml-id property
            if (choiceProperty.isXmlIdRef()) {
                
                    JavaClass nextCls =  helper.getJavaClass(next.getType());
                     processReferencedClass(nextCls);
                     TypeInfo tInfo = typeInfo.get(next.getType());
            	
            	
                if (tInfo == null || !tInfo.isIDSet()) {
                    throw JAXBException.invalidXmlElementInXmlElementsList(propertyName, name);
                }
            }

            QName qName = null;
            if (!namespace.equals(XMLProcessor.DEFAULT)) {
                qName = new QName(namespace, name);
            } else {
                NamespaceInfo namespaceInfo = getPackageInfoForPackage(cls).getNamespaceInfo();
                if (namespaceInfo.isElementFormQualified()) {
                    qName = new QName(namespaceInfo.getNamespace(), name);
                } else {
                    qName = new QName(name);
                }
            }

            choiceProp.setPropertyName(name);
            // figure out the property's type - note that for DEFAULT, if from
            // XML the value will
            // be "XmlElement.DEFAULT", and from annotations the value will be
            // "XmlElement$DEFAULT"
            if (next.getType().equals("javax.xml.bind.annotation.XmlElement.DEFAULT") || next.getType().equals("javax.xml.bind.annotation.XmlElement$DEFAULT")) {
                choiceProp.setType(propertyType);
            } else {
                choiceProp.setType(helper.getJavaClass(next.getType()));
            }
            // handle case of XmlJoinNodes w/XmlElements
            if (choiceProperty.isSetXmlJoinNodesList()) {
                // assumes one corresponding xml-join-nodes entry per
                // xml-element
                org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes xmlJoinNodes = choiceProperty.getXmlJoinNodesList().get(i);
                if (xmlJoinNodes != null) {
                    choiceProp.setXmlJoinNodes(xmlJoinNodes);
                    // set type
                    if (!xmlJoinNodes.getType().equals(XMLProcessor.DEFAULT)) {
                        JavaClass pType = helper.getJavaClass(xmlJoinNodes.getType());
                        if (helper.isCollectionType(choiceProp.getType())) {
                            choiceProp.setGenericType(pType);
                        } else {
                            choiceProp.setType(pType);
                        }
                    }
                }
            }
            
            choiceProp.setSchemaName(qName);
            choiceProp.setSchemaType(getSchemaTypeFor(choiceProp.getType()));
            choiceProp.setIsXmlIdRef(choiceProperty.isXmlIdRef());
            choiceProp.setXmlElementWrapper(choiceProperty.getXmlElementWrapper());
            choiceProperties.add(choiceProp);
            processReferencedClass(choiceProp.getType());
                TypeInfo newInfo = typeInfo.get(choiceProp.getType().getQualifiedName());
                if (newInfo != null && newInfo.isTransient()) {
                    throw JAXBException.invalidReferenceToTransientClass(info.getJavaClassName(), choiceProperty.getPropertyName(), newInfo.getJavaClassName());
                }
            }
        choiceProperty.setChoiceProperties(choiceProperties);
    }

    /**
     * Build a reference property. Here we will build a list of XML model
     * XmlElementRef objects, based on the @XmlElement(s) annotation, to store
     * on the Property. Processing of the elements and validation will be
     * performed during the finalize property phase via the
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
            property.setIsRequired(true); 
            try{
	            Method requireMethod = PrivilegedAccessHelper.getMethod(XmlElementRef.class, "required", new Class[0], true);
	            if(requireMethod != null){
	            	Boolean val = (Boolean)PrivilegedAccessHelper.invokeMethod(requireMethod, nextRef);
	            	property.setIsRequired(val); 
	            }
            } catch (Exception exception){
            }
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
   
        for (org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef nextRef : property.getXmlElementRefs()) {
            JavaClass type = property.getType();
            String typeName = type.getQualifiedName();
            if (helper.isCollectionType(property.getType())) {
                if (type.hasActualTypeArguments()) {
                    type = property.getGenericType();
                    typeName = type.getQualifiedName();
                }
            }

            if (!(nextRef.getType().equals("javax.xml.bind.annotation.XmlElementRef.DEFAULT") || nextRef.getType().equals("javax.xml.bind.annotation.XmlElementRef$DEFAULT"))) {
                typeName = nextRef.getType();
                type = helper.getJavaClass(typeName);
            }
            
            boolean missingReference = true;
            for (Entry<String, ElementDeclaration> entry : xmlRootElements.entrySet()) {
                ElementDeclaration entryValue = entry.getValue();
                if (!(areEquals(type, Object.class)) && type.isAssignableFrom(entryValue.getJavaType())) {
                    addReferencedElement(property, entryValue);
                    missingReference = false;
                }
            }
            if (missingReference) {
                String name = nextRef.getName();
                String namespace = nextRef.getNamespace();
                if (namespace.equals(XMLProcessor.DEFAULT)) {
                    namespace = EMPTY_STRING;
                }
                QName qname = new QName(namespace, name);
                JavaClass scopeClass = cls;
                ElementDeclaration referencedElement = null;
                while (!(scopeClass.getName().equals(JAVA_LANG_OBJECT))) {
                    HashMap<QName, ElementDeclaration> elements = getElementDeclarationsForScope(scopeClass.getName());
                    if (elements != null) {
                        referencedElement = elements.get(qname);
                    }
                    if (referencedElement != null) {
                        break;
                    }
                    scopeClass = scopeClass.getSuperclass();
                }
                if (referencedElement == null) {
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
    
    private void processReferencePropertyTypes(Property property, TypeInfo info, JavaClass theClass) {
        for (org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef nextRef : property.getXmlElementRefs()) {
            JavaClass type = property.getType();
            String typeName = type.getQualifiedName();
            if (helper.isCollectionType(property.getType())) {
                if (type.hasActualTypeArguments()) {
                    type = property.getGenericType();
                    typeName = type.getQualifiedName();
                }
            }
            
            if(JAVAX_XML_BIND_JAXBELEMENT.equals(typeName)){
                Collection args = type.getActualTypeArguments();
                if(args.size() > 0){            
            	    JavaClass theType = (JavaClass) args.iterator().next();
            	    processReferencedClass(theType);
                }
            }

            // for DEFAULT, if from XML the type will be
            // "XmlElementRef.DEFAULT",
            // and from annotations the value will be "XmlElementref$DEFAULT"
            if (!(nextRef.getType().equals("javax.xml.bind.annotation.XmlElementRef.DEFAULT") || nextRef.getType().equals("javax.xml.bind.annotation.XmlElementRef$DEFAULT"))) {
                typeName = nextRef.getType();
                type = helper.getJavaClass(typeName);
            }
            processReferencedClass(type);
        }
    }

    private void processPropertyAnnotations(TypeInfo info, JavaClass cls, JavaHasAnnotations javaHasAnnotations, Property property) {
        // Check for mixed context
        if (helper.isAnnotationPresent(javaHasAnnotations, XmlMixed.class)) {
            info.setMixed(true);
            property.setMixedContent(true);            
            findAndProcessObjectFactory(cls);                                   
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
                property.setInverseReferencePropertyGetMethodName(GET_STR + propName);
                property.setInverseReferencePropertySetMethodName(SET_STR + propName);
            }

            property.setInverseReference(true, helper.isAnnotationPresent(javaHasAnnotations, XmlElement.class));
        }

        processXmlJavaTypeAdapter(property, info, cls);
        if (helper.isAnnotationPresent(property.getElement(), XmlAttachmentRef.class) && areEquals(property.getActualType(), JAVAX_ACTIVATION_DATAHANDLER)) {
            property.setIsSwaAttachmentRef(true);
            property.setSchemaType(Constants.SWA_REF_QNAME);
        }
        processXmlElement(property, info);

        // JavaClass ptype = property.getActualType();
        if (!(property.isSwaAttachmentRef()) && isMtomAttachment(property)) {
            property.setIsMtomAttachment(true);
            property.setSchemaType(Constants.BASE_64_BINARY_QNAME);
        }
        if (helper.isAnnotationPresent(property.getElement(), XmlMimeType.class)) {
            property.setMimeType(((XmlMimeType) helper.getAnnotation(property.getElement(), XmlMimeType.class)).value());
        }
        // set indicator for inlining binary data - setting this to true on a
        // non-binary data type won't have any affect
        if (helper.isAnnotationPresent(property.getElement(), XmlInlineBinaryData.class) || info.isBinaryDataToBeInlined()) {
            property.setisInlineBinaryData(true);
        }

        // Get schema-type info if specified and set it on the property for
        // later use:
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
            if (info.isSetAnyAttributePropertyName() && !info.getAnyAttributePropertyName().equals(property.getPropertyName())) {
                throw org.eclipse.persistence.exceptions.JAXBException.multipleAnyAttributeMapping(cls.getName());
            }
            if (!helper.isMapType(property.getType())) {
                throw org.eclipse.persistence.exceptions.JAXBException.anyAttributeOnNonMap(property.getPropertyName());
            }
            property.setIsAnyAttribute(true);
            info.setAnyAttributePropertyName(property.getPropertyName());
        }

        // Make sure XmlElementWrapper annotation is on a collection or array
        if (helper.isAnnotationPresent(property.getElement(), XmlElementWrapper.class)) {
            XmlElementWrapper wrapper = (XmlElementWrapper) helper.getAnnotation(property.getElement(), XmlElementWrapper.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper xmlEltWrapper = new org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper();

            String wrapperName = wrapper.name();
            if (wrapperName.equals(XMLProcessor.DEFAULT)) {
                wrapperName = info.getXmlNameTransformer().transformElementName(property.getPropertyName());
            }
            xmlEltWrapper.setName(wrapperName);
            xmlEltWrapper.setNamespace(wrapper.namespace());
            xmlEltWrapper.setNillable(wrapper.nillable());
            xmlEltWrapper.setRequired(wrapper.required());
            property.setXmlElementWrapper(xmlEltWrapper);
        }

        if (helper.isAnnotationPresent(property.getElement(), XmlList.class)) {
            // Make sure XmlList annotation is on a collection or array
            if (!helper.isCollectionType(property.getType()) && !property.getType().isArray()) {
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
        if (helper.isAnnotationPresent(property.getElement(), XmlAccessMethods.class)) {
            XmlAccessMethods accessMethods = (XmlAccessMethods) helper.getAnnotation(property.getElement(), XmlAccessMethods.class);
            if (!(accessMethods.getMethodName().equals(EMPTY_STRING))) {
                property.setGetMethodName(accessMethods.getMethodName());
            }
            if (!(accessMethods.setMethodName().equals(EMPTY_STRING))) {
                property.setSetMethodName(accessMethods.setMethodName());
            }
            if (!(property.isMethodProperty())) {
                property.setMethodProperty(true);
            }
        }

        // handle user properties
        if (helper.isAnnotationPresent(property.getElement(), XmlProperties.class)) {
            XmlProperties xmlProperties = (XmlProperties) helper.getAnnotation(property.getElement(), XmlProperties.class);
            Map<Object, Object> propertiesMap = createUserPropertiesMap(xmlProperties.value());
            property.setUserProperties(propertiesMap);
        } else if (helper.isAnnotationPresent(property.getElement(), XmlProperty.class)) {
            XmlProperty xmlProperty = (XmlProperty) helper.getAnnotation(property.getElement(), XmlProperty.class);
            Map<Object, Object> propertiesMap = createUserPropertiesMap(new XmlProperty[] { xmlProperty });
            property.setUserProperties(propertiesMap);
        }
        // handle XmlKey
        if (helper.isAnnotationPresent(property.getElement(), XmlKey.class)) {
            info.addXmlKeyProperty(property);
        }
        // handle XmlJoinNode(s)
        processXmlJoinNodes(property);
        processXmlNullPolicy(property);

        // Handle XmlLocation
        JavaHasAnnotations elem = property.getElement();
        if (helper.isAnnotationPresent(elem, XmlLocation.class) || helper.isAnnotationPresent(elem, CompilerHelper.XML_LOCATION_ANNOTATION_CLASS) || helper.isAnnotationPresent(elem, CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_CLASS)) {
            if (!helper.getJavaClass(Constants.LOCATOR_CLASS).isAssignableFrom(property.getType())) {
                throw JAXBException.invalidXmlLocation(property.getPropertyName(), property.getType().getName());
            }
            property.setXmlLocation(true);
        }
    }

    /**
     * Process XmlJoinNode(s) for a given Property. An
     * org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNode(s) will be
     * created/populated using the annotation, and set on the Property for later
     * processing.
     * 
     * It is assumed that for a single join node XmlJoinNode will be used, and
     * for multiple join nodes XmlJoinNodes will be used.
     * 
     * @param property
     *            Property that may contain @XmlJoinNodes/@XmlJoinNode
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
     * method name is set (not both) and that an xml-path is set. Validates that
     * for attribute transformers either a transformer class OR method name is
     * set (not both).
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
     * Compares a JavaModel JavaClass to a Class. Equality is based on the raw
     * name of the JavaClass compared to the canonical name of the Class.
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
        if (helper.isAnnotationPresent(property.getElement(), XmlNullPolicy.class)) {
            XmlNullPolicy nullPolicy = (XmlNullPolicy) helper.getAnnotation(property.getElement(), XmlNullPolicy.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy policy = new org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy();
            policy.setEmptyNodeRepresentsNull(nullPolicy.emptyNodeRepresentsNull());
            policy.setIsSetPerformedForAbsentNode(nullPolicy.isSetPerformedForAbsentNode());
            policy.setXsiNilRepresentsNull(Boolean.valueOf(nullPolicy.xsiNilRepresentsNull()));
            policy.setNullRepresentationForXml(org.eclipse.persistence.jaxb.xmlmodel.XmlMarshalNullRepresentation.valueOf(nullPolicy.nullRepresentationForXml().toString()));
            property.setNullPolicy(policy);

        } else if (helper.isAnnotationPresent(property.getElement(), XmlIsSetNullPolicy.class)) {
            XmlIsSetNullPolicy nullPolicy = (XmlIsSetNullPolicy) helper.getAnnotation(property.getElement(), XmlIsSetNullPolicy.class);
            org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy policy = new org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy();
            policy.setEmptyNodeRepresentsNull(nullPolicy.emptyNodeRepresentsNull());
            policy.setXsiNilRepresentsNull(Boolean.valueOf(nullPolicy.xsiNilRepresentsNull()));
            policy.setNullRepresentationForXml(org.eclipse.persistence.jaxb.xmlmodel.XmlMarshalNullRepresentation.valueOf(nullPolicy.nullRepresentationForXml().toString()));
            policy.setIsSetMethodName(nullPolicy.isSetMethodName());
            for (XmlParameter next : nullPolicy.isSetParameters()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy.IsSetParameter param = new org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy.IsSetParameter();
                param.setValue(next.value());
                param.setType(next.type().getName());
                policy.getIsSetParameter().add(param);
            }
            property.setNullPolicy(policy);
        }
    }

    /**
     * Compares a JavaModel JavaClass to a Class. Equality is based on the raw
     * name of the JavaClass compared to the canonical name of the Class.
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
        return getPropertyPropertiesForClass(cls, info, onlyPublic, false);
    }

    public ArrayList<Property> getPropertyPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic, boolean onlyExplicit) {
        ArrayList<Property> properties = new ArrayList<Property>();
        if (cls == null) {
            return properties;
        }

        // First collect all the getters and setters
        ArrayList<JavaMethod> propertyMethods = new ArrayList<JavaMethod>();
        for (JavaMethod next : new ArrayList<JavaMethod>(cls.getDeclaredMethods())) {
            if(!next.isSynthetic()){
                if (((next.getName().startsWith(GET_STR) && next.getName().length() > 3) || (next.getName().startsWith(IS_STR) && next.getName().length() > 2)) && next.getParameterTypes().length == 0 && next.getReturnType() != helper.getJavaClass(java.lang.Void.class)) {
                    int modifiers = next.getModifiers();
                    
                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic || hasJAXBAnnotations(next))) {
                       propertyMethods.add(next);
                    } 
                } else if (next.getName().startsWith(SET_STR) && next.getName().length() > 3 && next.getParameterTypes().length == 1) {
                    int modifiers = next.getModifiers();
                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic || hasJAXBAnnotations(next))) {
                        propertyMethods.add(next);
                    }
                }
        	}
        }
        // Next iterate over the getters and find their setter methods, add
        // whichever one is
        // annotated to the properties list. If neither is, use the getter

        // keep track of property names to avoid processing the same property
        // twice (for getter and setter)
        ArrayList<String> propertyNames = new ArrayList<String>();
        for (int i = 0; i < propertyMethods.size(); i++) {
            boolean isPropertyTransient = false;
            JavaMethod nextMethod = propertyMethods.get(i);
            String propertyName = EMPTY_STRING;

            JavaMethod getMethod;
            JavaMethod setMethod;

            JavaMethod propertyMethod = null;

            if (!nextMethod.getName().startsWith(SET_STR)) {
                if (nextMethod.getName().startsWith(GET_STR)) {
                    propertyName = nextMethod.getName().substring(3);
                } else if (nextMethod.getName().startsWith(IS_STR)) {
                    propertyName = nextMethod.getName().substring(2);
                }
                getMethod = nextMethod;
                String setMethodName = SET_STR + propertyName;

                // use the JavaBean API to correctly decapitalize the first
                // character, if necessary
                propertyName = Introspector.decapitalize(propertyName);

                JavaClass[] paramTypes = { (JavaClass) getMethod.getReturnType() };
                setMethod = cls.getDeclaredMethod(setMethodName, paramTypes);

                if(setMethod == null) {
                    //if there's no locally declared set method, check for an inherited 
                    //set method
                    setMethod = cls.getMethod(setMethodName, paramTypes);
                }
                if(setMethod == null && !(hasJAXBAnnotations(getMethod))) {
                    //if there's no corresponding setter, and not explicitly
                    //annotated, don't process
                    isPropertyTransient = true;
                }
                    
                if (setMethod != null && hasJAXBAnnotations(setMethod)) {
                    // use the set method if it exists and is annotated
                    boolean isTransient = helper.isAnnotationPresent(setMethod, XmlTransient.class);
                    boolean isLocation = helper.isAnnotationPresent(setMethod, XmlLocation.class) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.XML_LOCATION_ANNOTATION_CLASS) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_CLASS);
                    propertyMethod = setMethod;
                    if(isTransient) {
                        isPropertyTransient = true;
                        // XmlLocation can also be transient
                        if (isLocation) {
                            propertyMethod = setMethod;
                            info.setLocationAware(true);
                        }
                    }
                } else if ((onlyExplicit && hasJAXBAnnotations(getMethod)) || !onlyExplicit) {
                    boolean isTransient = helper.isAnnotationPresent(getMethod, XmlTransient.class);
                    boolean isLocation = helper.isAnnotationPresent(getMethod, XmlLocation.class) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.XML_LOCATION_ANNOTATION_CLASS) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_CLASS);
                    propertyMethod = getMethod;
                    if(isTransient) {
                        isPropertyTransient = true;
                        // XmlLocation can also be transient
                        if (isLocation) {
                            propertyMethod = getMethod;
                            info.setLocationAware(true);
                        }
                    }
                } else if (onlyExplicit) {
                    continue;
                }
            } else {
                propertyName = nextMethod.getName().substring(3);
                setMethod = nextMethod;

                String getMethodName = GET_STR + propertyName;

                getMethod = cls.getDeclaredMethod(getMethodName, new JavaClass[] {});
                if (getMethod == null) {
                    // try is instead of get
                    getMethodName = IS_STR + propertyName;
                    getMethod = cls.getDeclaredMethod(getMethodName, new JavaClass[] {});                 
                }

                //may look for get method on parent class
                if(getMethod == null) {
                    //look for inherited getMethod
                    getMethod = cls.getMethod(GET_STR + propertyName, new JavaClass[]{});
                    if(getMethod == null) {
                        getMethod = cls.getMethod(IS_STR + propertyName, new JavaClass[]{});
                    }
                }
                if(getMethod == null && !(hasJAXBAnnotations(setMethod))) {
                    isPropertyTransient = true;
                }
                if (getMethod != null && hasJAXBAnnotations(getMethod)) {
                    // use the set method if it exists and is annotated
                    boolean isTransient = helper.isAnnotationPresent(getMethod, XmlTransient.class);
                    boolean isLocation = helper.isAnnotationPresent(getMethod, XmlLocation.class) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.XML_LOCATION_ANNOTATION_CLASS) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_CLASS);
                    if (!isTransient) {
                        propertyMethod = getMethod;
                    } else {
                        isPropertyTransient = true;
                        // XmlLocation can also be transient
                        if (isLocation) {
                            propertyMethod = getMethod;
                            info.setLocationAware(true);
                        }
                    }
                } else if ((onlyExplicit && hasJAXBAnnotations(setMethod)) || !onlyExplicit) {
                    boolean isTransient = helper.isAnnotationPresent(setMethod, XmlTransient.class);
                    boolean isLocation = helper.isAnnotationPresent(setMethod, XmlLocation.class) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.XML_LOCATION_ANNOTATION_CLASS) ||
                            helper.isAnnotationPresent(setMethod, CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_CLASS);
                    if (!isTransient) {
                        propertyMethod = setMethod;
                    } else {
                        isPropertyTransient = true;
                        // XmlLocation can also be transient
                        if (isLocation) {
                            propertyMethod = setMethod;
                            info.setLocationAware(true);
                        }
                    }
                } else if (onlyExplicit) {
                    continue;
                }
                // use the JavaBean API to correctly decapitalize the first
                // character, if necessary
                propertyName = Introspector.decapitalize(propertyName);
            }

            JavaClass ptype = null;
            if (getMethod != null) {
                ptype = (JavaClass) getMethod.getReturnType();
            } else {
                ptype = setMethod.getParameterTypes()[0];
            }

            if (!propertyNames.contains(propertyName)) {
               try {
                Property property = buildNewProperty(info, cls, propertyMethod, propertyName, ptype);
                propertyNames.add(propertyName);
                property.setTransient(isPropertyTransient);

                if (getMethod != null) {
                    property.setOriginalGetMethodName(getMethod.getName());
                    if (property.getGetMethodName() == null) {
                        property.setGetMethodName(getMethod.getName());
                    }
                }
                if (setMethod != null) {
                    property.setOriginalSetMethodName(setMethod.getName());
                    if (property.getSetMethodName() == null) {
                        property.setSetMethodName(setMethod.getName());
                    }
                }
                property.setMethodProperty(true);

                //boolean isTransient = helper.isAnnotationPresent(property.getElement(), XmlTransient.class);
                //boolean isLocation = helper.isAnnotationPresent(property.getElement(), XmlLocation.class) ||
                //        helper.isAnnotationPresent(setMethod, CompilerHelper.XML_LOCATION_ANNOTATION_CLASS) ||
                //        helper.isAnnotationPresent(setMethod, CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_CLASS);
                //if (!isTransient || (isTransient && isLocation)) {
                properties.add(property);
                //}
               } catch(JAXBException ex) {
                   if(ex.getErrorCode() != JAXBException.INVALID_INTERFACE || !isPropertyTransient) {
                       throw ex;
                   }                   
               }
            }
        }

        properties = removeSuperclassProperties(cls, properties);

        // default to alphabetical ordering
        // RI compliancy
        Collections.sort(properties, new PropertyComparitor());
        return properties;
    }

    private ArrayList<Property> removeSuperclassProperties(JavaClass cls, ArrayList<Property> properties) {
        ArrayList<Property> revisedProperties = new ArrayList<Property>();
        revisedProperties.addAll(properties);

        // Check for any get() methods that are overridden in the subclass.
        // If we find any, remove the property, because it is already defined on
        // the superclass.
        JavaClass superClass = cls.getSuperclass();
        if (null != superClass) {
            TypeInfo superClassInfo = typeInfo.get(superClass.getQualifiedName());
            if (superClassInfo != null && !superClassInfo.isTransient()) {
                for (Property prop : properties) {
                    for (Property superProp : superClassInfo.getProperties().values()) {
                        if (superProp.getGetMethodName() != null && superProp.getGetMethodName().equals(prop.getGetMethodName()) && !superProp.isTransient()) {
                            revisedProperties.remove(prop);
                        }
                    }
                }
            }
        }

        return revisedProperties;
    }

    public ArrayList getPublicMemberPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> fieldProperties = getFieldPropertiesForClass(cls, info, !hasXmlBindings());
        ArrayList<Property> methodProperties = getPropertyPropertiesForClass(cls, info, !hasXmlBindings());

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

        // Not sure who should win if a property exists for both or the correct
        // order
        if (publicFieldProperties.size() >= 0 && publicMethodProperties.size() == 0) {
            return publicFieldProperties;
        } else if (publicMethodProperties.size() > 0 && publicFieldProperties.size() == 0) {
            return publicMethodProperties;
        } else {
            // add any non-duplicate method properties to the collection.
            // - in the case of a collision if one is annotated use it,
            // otherwise
            // use the field.
            HashMap<String, Property> fieldPropertyMap = getPropertyMapFromArrayList(publicFieldProperties);       
            for (int i = 0; i < publicMethodProperties.size(); i++) {
                Property next = (Property) publicMethodProperties.get(i);
                Property fieldProp = fieldPropertyMap.get(next.getPropertyName());                
                if ( fieldProp == null) {
                    publicFieldProperties.add(next);
                } else if (fieldProp.isTransient()){
                    //bug 346461 - if a public field is transient and the public methods are not
                    // then use the method
                	publicFieldProperties.remove(fieldProp);
                	publicFieldProperties.add(next);
                }
            }
            return publicFieldProperties;
        }
    }

    public HashMap<String, Property> getPropertyMapFromArrayList(ArrayList<Property> props) {
        HashMap propMap = new HashMap(props.size());

        Iterator propIter = props.iterator();
        while (propIter.hasNext()) {
            Property next = (Property) propIter.next();
            propMap.put(next.getPropertyName(), next);
        }
        return propMap;
    }

    public ArrayList getNoAccessTypePropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> list = new ArrayList<Property>();
        if (cls == null) {
            return list;
        }

        // Iterate over the field and method properties. If ANYTHING contains an
        // annotation and
        // doesn't appear in the other list, add it to the final list
        List<Property> fieldProperties = getFieldPropertiesForClass(cls, info, false);
        Map<String, Property> fields = new HashMap<String, Property>(fieldProperties.size());
        for (Property next : fieldProperties) {
            JavaHasAnnotations elem = next.getElement();
            if (!hasJAXBAnnotations(elem)) {
                next.setTransient(true);
            }
            list.add(next);
            fields.put(next.getPropertyName(), next);
        }

        List<Property> methodProperties = getPropertyPropertiesForClass(cls, info, false);
        for (Property next : methodProperties) {
            JavaHasAnnotations elem = next.getElement();
            if (hasJAXBAnnotations(elem)) {
                // If the property is annotated remove the corresponding field
                Property fieldProperty = fields.get(next.getPropertyName());
                list.remove(fieldProperty);
                list.add(next);
            } else {
                // If the property is not annotated only add it if there is no
                // corresponding field.
                next.setTransient(true);
                if (fields.get(next.getPropertyName()) == null) {
                    list.add(next);
                }
            }
        }
        return list;
    }

    /**
     * Use name, namespace and type information to setup a user-defined schema
     * type. This method will typically be called when processing an
     * 
     * @XmlSchemaType(s) annotation or xml-schema-type(s) metadata.
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
        QName restrictionBase = getSchemaTypeFor(helper.getJavaClass(restrictionClass));

        if (helper.isAnnotationPresent(javaClass, XmlEnum.class)) {
            XmlEnum xmlEnum = (XmlEnum) helper.getAnnotation(javaClass, XmlEnum.class);
            restrictionClass = xmlEnum.value();
      	    JavaClass restrictionJavaClass= helper.getJavaClass(restrictionClass);
      	    boolean restrictionIsEnum = helper.isAnnotationPresent(restrictionJavaClass, XmlEnum.class);

      	    if(!restrictionIsEnum){
      	    	 if(helper.isBuiltInJavaType(restrictionJavaClass)){  
      	    		restrictionBase = getSchemaTypeFor(helper.getJavaClass(restrictionClass));          
      	    	 }else{
      	    		TypeInfo restrictionInfo = typeInfo.get(restrictionJavaClass.getQualifiedName());
          	    	if(restrictionInfo == null){
          	    		 JavaClass[] jClasses = new JavaClass[] { restrictionJavaClass };
                         buildNewTypeInfo(jClasses);
                         restrictionInfo = typeInfo.get(restrictionJavaClass.getQualifiedName());
          	    	}else if(restrictionInfo != null && !restrictionInfo.isPostBuilt()){
          	    		postBuildTypeInfo(new JavaClass[] { restrictionJavaClass });
          	    	}
          	    	
          	    	Property xmlValueProp  =restrictionInfo.getXmlValueProperty();
          	    	if(xmlValueProp != null){
          	    		restrictionJavaClass = xmlValueProp.getActualType();      	    		
          	    	    restrictionBase = getSchemaTypeFor(restrictionJavaClass);
          	    	    restrictionClass = helper.getClassForJavaClass(restrictionJavaClass);
          	    	}      	
      	    	 }
      	    }else{
      	    	while (restrictionIsEnum) {
	            	
      	    	     TypeInfo restrictionTypeInfo = processReferencedClass(restrictionJavaClass);
	                  restrictionBase = new QName(restrictionTypeInfo.getClassNamespace(), restrictionTypeInfo.getSchemaTypeName());
	
	                 xmlEnum = (XmlEnum) helper.getAnnotation(restrictionJavaClass, XmlEnum.class);
	                 restrictionClass = xmlEnum.value();  
	                 restrictionJavaClass= helper.getJavaClass(restrictionClass);
	                 restrictionIsEnum = helper.isAnnotationPresent(restrictionJavaClass, XmlEnum.class);
	            }
      	    }      	    
        } 
        info.setRestrictionBase(restrictionBase);
     
        for (Iterator<JavaField> fieldIt = javaClass.getDeclaredFields().iterator(); fieldIt.hasNext();) {
            JavaField field = fieldIt.next();
            if (field.isEnumConstant()) {
                Object enumValue = field.getName();
                if (helper.isAnnotationPresent(field, XmlEnumValue.class)) {
                    enumValue = ((XmlEnumValue) helper.getAnnotation(field, XmlEnumValue.class)).value();
                }
                if(restrictionClass != null){
                	try{
                	    enumValue = XMLConversionManager.getDefaultXMLManager().convertObject(enumValue, restrictionClass);
                	}catch(ConversionException e){                    	
                    	throw org.eclipse.persistence.exceptions.JAXBException.invalidEnumValue(enumValue, restrictionClass.getName(), e);
                    }

                }
                info.addJavaFieldToXmlEnumValuePair(field.getName(), enumValue);
            }
        }
        // Add a non-named element declaration for each enumeration to trigger
        // class generation
        if(info.getXmlRootElement() == null) {
            //process the root element and use that as the element
            ElementDeclaration elem = new ElementDeclaration(null, javaClass, javaClass.getQualifiedName(), false);
            this.getLocalElements().add(elem);
        }
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
            return Constants.ANY_SIMPLE_TYPE_QNAME;
        }
        return schemaType;
    }

    public NamespaceInfo processNamespaceInformation(XmlSchema xmlSchema) {
        NamespaceInfo info = new NamespaceInfo();
        info.setNamespaceResolver(new NamespaceResolver());
        String packageNamespace = null;
        if (xmlSchema != null) {
            String namespaceMapping = xmlSchema.namespace();
            if (!(namespaceMapping.equals(EMPTY_STRING) || namespaceMapping.equals(XMLProcessor.DEFAULT))) {
                packageNamespace = namespaceMapping;
            } else if (namespaceMapping.equals(XMLProcessor.DEFAULT)) {
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
                    } else if (location.equals(EMPTY_STRING)) {
                        location = null;
                    }
                }
                info.setLocation(location);
            } catch (Exception ex) {
            }

        } else {
            info.setNamespace(defaultTargetNamespace);
        }
      if (!info.isElementFormQualified() ){
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

    public QName getQNameForProperty(Property property, String defaultName, JavaHasAnnotations element, NamespaceInfo namespaceInfo, TypeInfo info) {
        String uri = info.getClassNamespace();
        String name = XMLProcessor.DEFAULT;
        String namespace = XMLProcessor.DEFAULT;
        QName qName = null;
        
        if(property.isMap()){
        	isDefaultNamespaceAllowed = false;
        }
        
        if (helper.isAnnotationPresent(element, XmlAttribute.class)) {
            XmlAttribute xmlAttribute = (XmlAttribute) helper.getAnnotation(element, XmlAttribute.class);
            name = xmlAttribute.name();
            namespace = xmlAttribute.namespace();

            if (name.equals(XMLProcessor.DEFAULT)) {
                name = defaultName;
                try {
                    name = info.getXmlNameTransformer().transformAttributeName(name);
                } catch (Exception ex) {
                    throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(name, info.getXmlNameTransformer().getClass().getName(), ex);
                }
            }

            if (!namespace.equals(XMLProcessor.DEFAULT)) {
                qName = new QName(namespace, name);
                isDefaultNamespaceAllowed = false;
            } else {
                if (namespaceInfo.isAttributeFormQualified()) {
                    qName = new QName(uri, name);
                    isDefaultNamespaceAllowed = false;    
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
            if (property.isMap() && helper.isAnnotationPresent(element, XmlElementWrapper.class)) {
       		    XmlElementWrapper xmlElementWrapper = (XmlElementWrapper) helper.getAnnotation(element, XmlElementWrapper.class);
                name = xmlElementWrapper.name();
                namespace = xmlElementWrapper.namespace();
            }

            if (name.equals(XMLProcessor.DEFAULT)) {
                name = defaultName;

                try {
                    name = info.getXmlNameTransformer().transformElementName(name);
                } catch (Exception ex) {
                    throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(name, info.getXmlNameTransformer().getClass().getName(), ex);
                }
            }

            if (!namespace.equals(XMLProcessor.DEFAULT)) {
                qName = new QName(namespace, name);
                if (namespace.equals(Constants.EMPTY_STRING)) {
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

    public HashMap<String, PackageInfo> getPackageToPackageInfoMappings() {
        return packageToPackageInfoMappings;
    }

    /**
     * Add a package name/NamespaceInfo entry to the map. This method will
     * lazy-load the map if necessary.
     * 
     * @return
     */
    public void addPackageToNamespaceMapping(String packageName, NamespaceInfo nsInfo) {
        if (packageToPackageInfoMappings == null) {
            packageToPackageInfoMappings = new HashMap<String, PackageInfo>();
        }
        PackageInfo info = packageToPackageInfoMappings.get(packageName);
        if(info == null) {
            info = new PackageInfo();
            packageToPackageInfoMappings.put(packageName, info);
        }
        info.setNamespaceInfo(nsInfo);
    }

    public void addPackageToPackageInfoMapping(String packageName, PackageInfo packageInfo) {
        if(packageToPackageInfoMappings == null) {
            packageToPackageInfoMappings = new HashMap<String, PackageInfo>();
        }
        packageToPackageInfoMappings.put(packageName, packageInfo);
    }
    
    public PackageInfo getPackageInfoForPackage(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();
        PackageInfo packageInfo = packageToPackageInfoMappings.get(packageName);
        if (packageInfo == null) {
            packageInfo = getPackageInfoForPackage(javaClass.getPackage(), packageName);
        }
        return packageInfo;
    }

    public PackageInfo getPackageInfoForPackage(JavaPackage pack, String packageName) {
        PackageInfo packageInfo = packageToPackageInfoMappings.get(packageName);
        if (packageInfo == null) {
            XmlSchema xmlSchema = (XmlSchema) helper.getAnnotation(pack, XmlSchema.class);
            packageInfo = new PackageInfo();
            NamespaceInfo   namespaceInfo = processNamespaceInformation(xmlSchema);
            
            packageInfo.setNamespaceInfo(namespaceInfo);

            // if it's still null, generate based on package name
            if (namespaceInfo.getNamespace() == null) {
                namespaceInfo.setNamespace(EMPTY_STRING);
            }
            if (helper.isAnnotationPresent(pack, XmlAccessorType.class)) {
                XmlAccessorType xmlAccessorType = (XmlAccessorType) helper.getAnnotation(pack, XmlAccessorType.class);
                packageInfo.setAccessType(XmlAccessType.fromValue(xmlAccessorType.value().name()));
            }
            if (helper.isAnnotationPresent(pack, XmlAccessorOrder.class)) {
                XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder) helper.getAnnotation(pack, XmlAccessorOrder.class);
                packageInfo.setAccessOrder(XmlAccessOrder.fromValue(xmlAccessorOrder.value().name()));
            }
            if (CompilerHelper.ACCESSOR_FACTORY_ANNOTATION_CLASS != null && helper.isAnnotationPresent(pack, CompilerHelper.ACCESSOR_FACTORY_ANNOTATION_CLASS)) {
                Annotation xmlAccessorFactory = helper.getAnnotation(pack, CompilerHelper.ACCESSOR_FACTORY_ANNOTATION_CLASS);
                Class xmlAccessorFactoryClass = null;
                try {
                     xmlAccessorFactoryClass = (Class)PrivilegedAccessHelper.invokeMethod(CompilerHelper.ACCESSOR_FACTORY_VALUE_METHOD, xmlAccessorFactory, new Object[]{});
                    packageInfo.setAccessorFactory(new AccessorFactoryWrapper(PrivilegedAccessHelper.newInstanceFromClass(xmlAccessorFactoryClass)));
                } catch (Exception ex) {
                    throw JAXBException.errorInstantiatingAccessorFactory(xmlAccessorFactoryClass, ex);
                }
            }else if (CompilerHelper.INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS != null && helper.isAnnotationPresent(pack, CompilerHelper.INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS)) {
                Annotation xmlAccessorFactory = helper.getAnnotation(pack, CompilerHelper.INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS);
                Class xmlAccessorFactoryClass = null;
                try {
                    xmlAccessorFactoryClass = (Class)PrivilegedAccessHelper.invokeMethod(CompilerHelper.INTERNAL_ACCESSOR_FACTORY_VALUE_METHOD, xmlAccessorFactory, new Object[]{});
                    packageInfo.setAccessorFactory(new AccessorFactoryWrapper(PrivilegedAccessHelper.newInstanceFromClass(xmlAccessorFactoryClass)));
                } catch (Exception ex) {
                    throw JAXBException.errorInstantiatingAccessorFactory(xmlAccessorFactoryClass, ex);
                }
            }            
            packageToPackageInfoMappings.put(packageName, packageInfo);
        }
        return packageInfo;
    }

    public NamespaceInfo findInfoForNamespace(String namespace) {
        for(PackageInfo next:this.packageToPackageInfoMappings.values()) {
            String nextUri = next.getNamespace();
            if(nextUri == null) {
                nextUri = Constants.EMPTY_STRING;
            }
            if(namespace == null) {
                namespace = Constants.EMPTY_STRING;
            }
            
            if(nextUri.equals(namespace)) {
                return next.getNamespaceInfo();
            }
        }
        return null;
    }

    private void checkForCallbackMethods() {
        JavaClass unmarshallerCls = helper.getJavaClass(Unmarshaller.class);
        JavaClass marshallerCls = helper.getJavaClass(Marshaller.class);
        JavaClass objectCls = helper.getJavaClass(Object.class);
        JavaClass[] unmarshalParams = new JavaClass[] { unmarshallerCls, objectCls };
        JavaClass[] marshalParams = new JavaClass[] { marshallerCls };

        for (JavaClass next : typeInfoClasses) {
            if (next == null) {
                continue;
            }

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
            // if before/after unmarshal callback was found, add the callback to
            // the list
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
            // if before/after marshal callback was found, add the callback to
            // the list
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
    
    private void findAndProcessObjectFactory(JavaClass cls){
        //need to make sure objectfactory gets processed.        	
        try{    		
            String className =cls.getPackageName() + ".ObjectFactory";     		
            findAndProcessObjectFactory(className);
        }catch(JAXBException e){}
    }
    
    void findAndProcessObjectFactory(String objectFactoryClassName){
        //need to make sure objectfactory gets processed.        	
        try{    		
            if(objectFactoryClassNames.contains(objectFactoryClassName)){
                return; 
            }    			
            JavaClass javaClass = helper.getJavaClass(objectFactoryClassName);        	
            if (isXmlRegistry(javaClass)) {       		
                JavaClass[] processed = this.processObjectFactory(javaClass, new ArrayList());
                preBuildTypeInfo(processed);
                buildTypeInfo(processed);
                updateGlobalElements(processed);
       	    }
        }catch(JAXBException e){}
    }
    
    public JavaClass[] processObjectFactory(JavaClass objectFactoryClass, ArrayList<JavaClass> classes) {
    	
    	String className = objectFactoryClass.getName();
    	if(objectFactoryClassNames.contains(className)){
    		return new JavaClass[0]; 
    	}
    	objectFactoryClassNames.add(className);
        // if there is an xml-registry from XML for this JavaClass, create a map
        // of method names to XmlElementDecl objects to simplify processing
        // later on in this method
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
        PackageInfo packageInfo = getPackageInfoForPackage(objectFactoryClass);
        while (methodsIter.hasNext()) {
            JavaMethod next = (JavaMethod) methodsIter.next();
            if (next.getName().startsWith(CREATE)) {
                JavaClass type = next.getReturnType();
                if (JAVAX_XML_BIND_JAXBELEMENT.equals(type.getName())) {
                	Object[] actualTypeArguments = type.getActualTypeArguments().toArray();
                    if (actualTypeArguments.length == 0) {
                        type = helper.OBJECT_CLASS;
                    } else {
                        type = (JavaClass) actualTypeArguments[0];
                    } 
                    type = processXmlElementDecl(type, next, packageInfo, elemDecls);
                }else if (helper.JAXBELEMENT_CLASS.isAssignableFrom(type)) {                                   
                	this.factoryMethods.put(type.getRawName(), next);
                	type = processXmlElementDecl(type, next, packageInfo, elemDecls);
                } else {
                    this.factoryMethods.put(type.getRawName(), next);                    
                }
                if (!helper.isBuiltInJavaType(type) && !helper.classExistsInArray(type, classes)) {
                    classes.add(type);
                }
            }
        }

        if (classes.size() > 0) {
        	classesToProcessPropertyTypes.addAll(classes);
            return classes.toArray(new JavaClass[classes.size()]);
        } else {
            return new JavaClass[0];
        }
    }
    
    private JavaClass processXmlElementDecl(JavaClass type, JavaMethod next, PackageInfo packageInfo, Map<String, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl> elemDecls){
        JavaClass returnType = type;
        // if there's an XmlElementDecl for this method from XML, use it
        // - otherwise look for an annotation
    	org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl xmlEltDecl = elemDecls.get(next.getName());
    	if (( xmlEltDecl != null) || helper.isAnnotationPresent(next, XmlElementDecl.class)) {
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
                if (!scopeClassName.equals(ELEMENT_DECL_GLOBAL)) {
                    JavaClass jScopeClass = helper.getJavaClass(scopeClassName);
                    if (jScopeClass != null) {
                        scopeClass = helper.getClassForJavaClass(jScopeClass);
                        if (scopeClass == null) {
                            scopeClass = javax.xml.bind.annotation.XmlElementDecl.GLOBAL.class;
                        }
                    }
                }
                if (!xmlEltDecl.getSubstitutionHeadName().equals(EMPTY_STRING)) {
                    String subHeadLocal = xmlEltDecl.getSubstitutionHeadName();
                    String subHeadNamespace = xmlEltDecl.getSubstitutionHeadNamespace();
                    if (subHeadNamespace.equals(XMLProcessor.DEFAULT)) {
                        subHeadNamespace = packageInfo.getNamespace();
                    }
                    substitutionHead = new QName(subHeadNamespace, subHeadLocal);
                }
                if (!(xmlEltDecl.getDefaultValue().length() == 1 && xmlEltDecl.getDefaultValue().startsWith(ELEMENT_DECL_DEFAULT))) {
                    defaultValue = xmlEltDecl.getDefaultValue();
                }
            } else {
                // there was no xml-element-decl for this method in XML,
                // so use the annotation
                XmlElementDecl elementDecl = (XmlElementDecl) helper.getAnnotation(next, XmlElementDecl.class);
                url = elementDecl.namespace();
                localName = elementDecl.name();
                scopeClass = elementDecl.scope();
                if (!elementDecl.substitutionHeadName().equals(EMPTY_STRING)) {
                    String subHeadLocal = elementDecl.substitutionHeadName();
                    String subHeadNamespace = elementDecl.substitutionHeadNamespace();
                    if (subHeadNamespace.equals(XMLProcessor.DEFAULT)) {
                        subHeadNamespace = packageInfo.getNamespace();
                    }

                    substitutionHead = new QName(subHeadNamespace, subHeadLocal);
                }
                if (!(elementDecl.defaultValue().length() == 1 && elementDecl.defaultValue().startsWith(ELEMENT_DECL_DEFAULT))) {
                    defaultValue = elementDecl.defaultValue();
                }
            }
            
            if (XMLProcessor.DEFAULT.equals(url)) {
                url = packageInfo.getNamespace();
            }
            if(Constants.EMPTY_STRING.equals(url)) {
                isDefaultNamespaceAllowed = false;
                qname = new QName(localName);
            }else{
                qname = new QName(url, localName);
            }

            boolean isList = false;
            if (JAVA_UTIL_LIST.equals(type.getName())) {
                isList = true;
                Collection args = type.getActualTypeArguments();
                if (args.size() > 0) {
                    type = (JavaClass) args.iterator().next();
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
                JavaClass adaptedType = helper.getJavaClass(declJavaType);                
                declaration.setJavaType(adaptedType);
                declaration.setAdaptedJavaType(type);
                returnType = adaptedType;
            }
            if (helper.isAnnotationPresent(next, XmlMimeType.class)) {
                XmlMimeType mimeType = (XmlMimeType)helper.getAnnotation(next, XmlMimeType.class);
                declaration.setXmlMimeType(mimeType.value());
            }
            if (helper.isAnnotationPresent(next, XmlAttachmentRef.class)) {
                declaration.setXmlAttachmentRef(true);
            }
            HashMap<QName, ElementDeclaration> elements = getElementDeclarationsForScope(scopeClass.getName());
            if (elements == null) {
                elements = new HashMap<QName, ElementDeclaration>();
                this.elementDeclarations.put(scopeClass.getName(), elements);
            }
            if(elements.containsKey(qname)){
            	throw JAXBException.duplicateElementName(qname);
            }

            elements.put(qname, declaration);
        }
    	return returnType;
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
        // Once all the global element declarations have been created, make sure
        // that any ones that have
        // a substitution head set are added to the list of substitutable
        // elements on the declaration for that
        // head.

        // Look for XmlRootElement declarations
        for (JavaClass javaClass : classesToProcess) {
            TypeInfo info = typeInfo.get(javaClass.getQualifiedName());
            if (info == null) {
                continue;
            }
            if (!info.isTransient() && info.isSetXmlRootElement()) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement xmlRE = info.getXmlRootElement();
                NamespaceInfo namespaceInfo;
                namespaceInfo = getPackageInfoForPackage(javaClass).getNamespaceInfo();

                String elementName = xmlRE.getName();
                if (elementName.equals(XMLProcessor.DEFAULT) || elementName.equals(EMPTY_STRING)) {
                    XMLNameTransformer transformer = info.getXmlNameTransformer();
                    try {
                        elementName = transformer.transformRootElementName(javaClass.getName());
                    } catch (Exception ex) {
                        throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
                    }

                }
                String rootNamespace = xmlRE.getNamespace();
                QName rootElemName = null;
                if (rootNamespace.equals(XMLProcessor.DEFAULT)) {
                    if (namespaceInfo == null) {
                        rootElemName = new QName(elementName);
                    } else {
                        String rootNS = namespaceInfo.getNamespace();
                        rootElemName = new QName(rootNS, elementName);
                        if (rootNS.equals(Constants.EMPTY_STRING)) {
                            isDefaultNamespaceAllowed = false;
                        }
                    }
                } else {
                    rootElemName = new QName(rootNamespace, elementName);
                    if (rootNamespace.equals(Constants.EMPTY_STRING)) {
                        isDefaultNamespaceAllowed = false;
                    }
                }
                ElementDeclaration declaration = new ElementDeclaration(rootElemName, javaClass, javaClass.getQualifiedName(), false);
                declaration.setIsXmlRootElement(true);
                addGlobalElement(rootElemName, declaration);
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
                if (rootDeclaration.getSubstitutionHead() != null && rootDeclaration.getSubstitutionHead().equals(substitutionHead)) {
                    // Break the loop if substitutionHead equals rootDeclaration's substitutionHead
                    // (XmlElementDecl's substitutionHeadName == name)
                    substitutionHead = null;
                } else {
                    substitutionHead = rootDeclaration.getSubstitutionHead();
                }
            }
        }
    }

    private void addReferencedElement(Property property, ElementDeclaration referencedElement) {
        property.addReferencedElement(referencedElement);
        if (referencedElement.getSubstitutableElements() != null && referencedElement.getSubstitutableElements().size() > 0) {
            for (ElementDeclaration substitutable : referencedElement.getSubstitutableElements()) {
                if (substitutable != referencedElement) {
                    addReferencedElement(property, substitutable);
                }
            }
        }
    }

    /**
     * Returns true if the field or method passed in is annotated with JAXB
     * annotations.
     */
    private boolean hasJAXBAnnotations(JavaHasAnnotations elem) {
        if(elem == null){
            return false;
        }
        Collection annotations = elem.getAnnotations();
        if (annotations == null || annotations.size() == 0) {
            return false;
        }
        Iterator annotationsIter = annotations.iterator();
        while (annotationsIter.hasNext()) {
            String nextName = ((JavaAnnotation) annotationsIter.next()).getName();
            if (nextName.startsWith(JAVAX_XML_BIND_ANNOTATION)
                    || nextName.startsWith(OXM_ANNOTATIONS)
                    || nextName.equals(CompilerHelper.XML_LOCATION_ANNOTATION_NAME)
                    || nextName.equals(CompilerHelper.INTERNAL_XML_LOCATION_ANNOTATION_NAME)) {
                return true;
            }
        }
        return false;
    }


    private void validatePropOrderForInfo(TypeInfo info) {
        if (info.isTransient()) {
            return;
        }
        if(info.getXmlVirtualAccessMethods() != null) {
            return;
        }
        // Ensure that all properties in the propOrder list actually exist
        String[] propOrder = info.getPropOrder();
        int propOrderLength = propOrder.length;
        if (propOrderLength > 0) {
            for (int i = 1; i < propOrderLength; i++) {
                String nextPropName = propOrder[i];
                if (!nextPropName.equals(EMPTY_STRING) && !info.getPropertyNames().contains(nextPropName)) {
                    throw JAXBException.nonExistentPropertyInPropOrder(nextPropName);
                }
            }
        }
    }

    private void validateXmlValueFieldOrProperty(JavaClass cls, Property property) {
        JavaClass ptype = property.getActualType();
        String propName = property.getPropertyName();
        JavaClass parent = cls.getSuperclass();
        while (parent != null && !(parent.getQualifiedName().equals(JAVA_LANG_OBJECT))) {
            TypeInfo parentTypeInfo = typeInfo.get(parent.getQualifiedName());
            if(hasElementMappedProperties(parentTypeInfo)) {
                throw JAXBException.propertyOrFieldCannotBeXmlValue(propName);
            }
            parent = parent.getSuperclass();
        }

        QName schemaQName = getSchemaTypeOrNullFor(ptype);
        if (schemaQName == null) {
            TypeInfo refInfo = processReferencedClass(ptype);
            if (refInfo != null && !refInfo.isEnumerationType() && refInfo.getXmlValueProperty() == null) {
                throw JAXBException.invalidTypeForXmlValueField(propName);
            }
        }
    }

    private boolean hasElementMappedProperties(TypeInfo typeInfo) {
        for (Property property : typeInfo.getPropertyList()) {
            if(!(property.isTransient()|| property.isAttribute() || property.isAnyAttribute())) {
                return true;
            }
        }
        return false;
    }

    private void validateXmlAttributeFieldOrProperty(TypeInfo tInfo, Property property) {
        // Check that @XmlAttribute references a Java type that maps to text in XML
        JavaClass ptype = property.getActualType();
        TypeInfo refInfo = typeInfo.get(ptype.getQualifiedName());
        if (refInfo != null) {
            if (!refInfo.isPostBuilt()) {
                postBuildTypeInfo(new JavaClass[] { ptype });
            }
            if (!refInfo.isEnumerationType()) {
                JavaClass parent = ptype.getSuperclass();
                boolean hasMapped = false;
                while (parent != null) {
                    hasMapped = hasTextMapping(refInfo);
                    if (hasMapped || parent.getQualifiedName().equals(JAVA_LANG_OBJECT)) {
                        break;
                    }
                    refInfo = typeInfo.get(parent.getQualifiedName());
                    parent = parent.getSuperclass();
                }
                if (!hasMapped) {
                    String propName = property.getPropertyName();
                    String typeName = tInfo.getJavaClassName();
                    String refTypeName = refInfo.getJavaClassName();
                    throw org.eclipse.persistence.exceptions.JAXBException.mustMapToText(propName, typeName, refTypeName);
                }
            }
        }
    }

    private boolean hasTextMapping(TypeInfo tInfo) {
        Collection<Property> props = tInfo.getProperties().values();
        for (Property property : props) {
            if (property.isAttribute()) {
                JavaClass ptype = property.getActualType();
                TypeInfo refInfo = typeInfo.get(ptype.getQualifiedName());
                if (refInfo != null && refInfo != tInfo) {
                    return hasTextMapping(refInfo);
                }
            }
        }

        boolean hasXmlId = (tInfo.getIDProperty() != null && !tInfo.getIDProperty().isTransient());
        boolean hasXmlValue = (tInfo.getXmlValueProperty() != null && !tInfo.getXmlValueProperty().isTransient());
        if (hasXmlValue) {
            // Ensure there is an @XmlValue property and nothing else
            hasXmlValue = CompilerHelper.isSimpleType(tInfo);            
        }

        return (hasXmlValue || hasXmlId);
    }
    
    private Class generateWrapperForMapClass(JavaClass mapClass, JavaClass keyClass, JavaClass valueClass, TypeMappingInfo typeMappingInfo) {
        String packageName = JAXB_DEV;
        NamespaceResolver combinedNamespaceResolver = new NamespaceResolver();
        if (!helper.isBuiltInJavaType(keyClass)) {
            String keyPackageName = keyClass.getPackageName();
            packageName = packageName + DOT_CHR + keyPackageName;
            NamespaceInfo keyNamespaceInfo = getPackageInfoForPackage(keyClass).getNamespaceInfo();
            if (keyNamespaceInfo != null) {
                java.util.Vector<Namespace> namespaces = keyNamespaceInfo.getNamespaceResolver().getNamespaces();
                for (Namespace n : namespaces) {
                    combinedNamespaceResolver.put(n.getPrefix(), n.getNamespaceURI());
                }

            }
        }

        if (!helper.isBuiltInJavaType(valueClass)) {
            String valuePackageName = valueClass.getPackageName();
            packageName = packageName + DOT_CHR + valuePackageName;
            NamespaceInfo valueNamespaceInfo = getPackageInfoForPackage(valueClass).getNamespaceInfo();
            if (valueNamespaceInfo != null) {
                java.util.Vector<Namespace> namespaces = valueNamespaceInfo.getNamespaceResolver().getNamespaces();
                for (Namespace n : namespaces) {
                    combinedNamespaceResolver.put(n.getPrefix(), n.getNamespaceURI());
                }
            }
        }
        String namespace = this.defaultTargetNamespace;
        if (namespace == null) {
            namespace = EMPTY_STRING;
        }
        PackageInfo packageInfo = packageToPackageInfoMappings.get(mapClass.getPackageName());
        if (packageInfo == null) {
            packageInfo = getPackageToPackageInfoMappings().get(packageName);
        } else {
            if (packageInfo.getNamespace() != null) {
                namespace = packageInfo.getNamespace();
            }
            getPackageToPackageInfoMappings().put(packageName, packageInfo);
        }
        if (packageInfo == null) {
            packageInfo = new PackageInfo();
            packageInfo.setNamespaceInfo(new NamespaceInfo());
            packageInfo.setNamespace(namespace);
            packageInfo.setNamespaceResolver(combinedNamespaceResolver);

            getPackageToPackageInfoMappings().put(packageName, packageInfo);
        }

        int beginIndex = keyClass.getName().lastIndexOf(DOT_CHR) + 1;
        String keyName = keyClass.getName().substring(beginIndex);
        int dollarIndex = keyName.indexOf(DOLLAR_SIGN_CHR);
        if (dollarIndex > -1) {
            keyName = keyName.substring(dollarIndex + 1);
        }

        beginIndex = valueClass.getName().lastIndexOf(DOT_CHR) + 1;
        String valueName = valueClass.getName().substring(beginIndex);
        dollarIndex = valueName.indexOf(DOLLAR_SIGN_CHR);
        if (dollarIndex > -1) {
            valueName = valueName.substring(dollarIndex + 1);
        }
        String collectionClassShortName = mapClass.getRawName().substring(mapClass.getRawName().lastIndexOf(DOT_CHR) + 1);
        String suggestedClassName = keyName + valueName + collectionClassShortName;

        String qualifiedClassName = packageName + DOT_CHR + suggestedClassName;
        qualifiedClassName = getNextAvailableClassName(qualifiedClassName);

        String qualifiedInternalClassName = qualifiedClassName.replace(DOT_CHR, SLASH_CHR);
        String internalKeyName = keyClass.getQualifiedName().replace(DOT_CHR, SLASH_CHR);
        String internalValueName = valueClass.getQualifiedName().replace(DOT_CHR, SLASH_CHR);

        Type mapType = Type.getType(L + mapClass.getRawName().replace(DOT_CHR, SLASH_CHR) + SEMI_COLON);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        String sig = "Lorg/eclipse/persistence/internal/jaxb/many/MapValue<L" + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;>;";
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, qualifiedInternalClassName, sig, "org/eclipse/persistence/internal/jaxb/many/MapValue", null);

        // Write Field: @... public Map entry
        String fieldSig = L + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;";
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC, "entry", L + mapType.getInternalName() + SEMI_COLON, fieldSig, null);
        AnnotationVisitor av = fv.visitAnnotation(Type.getDescriptor(XmlElement.class), true);
        if (typeMappingInfo != null) {
            Annotation[] annotations = typeMappingInfo.getAnnotations();
            if (annotations != null) {
                for (int i = 0; i < annotations.length; i++) {
                    Annotation nextAnnotation = annotations[i];
                    if (nextAnnotation != null && !(nextAnnotation instanceof XmlElement) && !(nextAnnotation instanceof XmlJavaTypeAdapter)) {
                        String annotationClassName = nextAnnotation.annotationType().getName();
                        av = fv.visitAnnotation(L + annotationClassName.replace(DOT_CHR, SLASH_CHR) + SEMI_COLON, true);
                        for (Method next : nextAnnotation.annotationType().getDeclaredMethods()) {
                            try {
                                Object nextValue = next.invoke(nextAnnotation, new Object[] {});
                                if (nextValue instanceof Class) {
                                    Type nextType = Type.getType(L + ((Class) nextValue).getName().replace(DOT_CHR, SLASH_CHR) + SEMI_COLON);
                                    nextValue = nextType;
                                }
                                av.visit(next.getName(), nextValue);
                            } catch (InvocationTargetException ex) {
                                // ignore the invocation target exception here.
                            } catch (IllegalAccessException ex) {
                            }
                        }
                        av.visitEnd();
                    }
                }
            }
        }
        fv.visitEnd();
        
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/eclipse/persistence/internal/jaxb/many/MapValue", "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // Write: @XmlTransitent public void setItem(???)
        String methodSig = "(L" + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;)V";
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setItem", "(L" + mapType.getInternalName() + ";)V", methodSig, null);
        // TODO: Verify that we really want to put @XmlTranient on setItem
        // method
        mv.visitAnnotation("Ljavax/xml/bind/annotation/XmlTransient;", true);
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, qualifiedInternalClassName, "entry", L + mapType.getInternalName() + SEMI_COLON);
        mv.visitInsn(Opcodes.RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);

        // Replacement?:LocalVariableTypeTableAttribute cvAttr = new
        // LocalVariableTypeTableAttribute();
        // mv.visitAttribute(cvAttr);

        mv.visitMaxs(2, 2);
        mv.visitEnd();

        // Write @XmlTransient public ??? getItem()
        methodSig = "()L" + mapType.getInternalName() + "<L" + internalKeyName + ";L" + internalValueName + ";>;";
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getItem", "()L" + mapType.getInternalName() + SEMI_COLON, methodSig, null);
        mv.visitAnnotation("Ljavax/xml/bind/annotation/XmlTransient;", true);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, qualifiedInternalClassName, "entry", L + mapType.getInternalName() + SEMI_COLON);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "getItem", "()Ljava/lang/Object;", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, qualifiedInternalClassName, "getItem", "()L" + mapType.getInternalName() + SEMI_COLON);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "setItem", "(Ljava/lang/Object;)V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.CHECKCAST, mapType.getInternalName());
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, qualifiedInternalClassName, "setItem", "(L" + mapType.getInternalName() + ";)V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        // Write @XmlType(namespace)
        av = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
        av.visit("namespace", namespace);

        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();
        return generateClassFromBytes(qualifiedClassName, classBytes);
    }

    private Class generateWrapperForArrayClass(JavaClass arrayClass, TypeMappingInfo typeMappingInfo, Class xmlElementType, List<JavaClass> classesToProcess) {
        JavaClass componentClass = null;
        if (typeMappingInfo != null && xmlElementType != null) {
            componentClass = helper.getJavaClass(xmlElementType);
        } else {
            componentClass = arrayClass.getComponentType();
        }
        if (componentClass.isArray()) {
            Class nestedArrayClass = arrayClassesToGeneratedClasses.get(componentClass.getName());
            if (nestedArrayClass == null) {
                nestedArrayClass = generateWrapperForArrayClass(componentClass, typeMappingInfo, xmlElementType, classesToProcess);
                arrayClassesToGeneratedClasses.put(componentClass.getName(), nestedArrayClass);
                classesToProcess.add(helper.getJavaClass(nestedArrayClass));
            }
            return generateArrayValue(arrayClass, helper.getJavaClass(nestedArrayClass), helper.getJavaClass(nestedArrayClass), typeMappingInfo);
        } else {
            return generateArrayValue(arrayClass, componentClass, componentClass, typeMappingInfo);
        }
    }

    private Class generateArrayValue(JavaClass arrayClass, JavaClass componentClass, JavaClass nestedClass, TypeMappingInfo typeMappingInfo) {
        String packageName;
        String qualifiedClassName;
        QName qName = null;
        if (helper.getJavaClass(ManyValue.class).isAssignableFrom(componentClass)) {
            packageName = componentClass.getPackageName();
            qualifiedClassName = nestedClass.getQualifiedName() + ARRAY_CLASS_NAME_SUFFIX;
        } else {
            if (componentClass.isPrimitive()) {
                packageName = ARRAY_PACKAGE_NAME;
                qualifiedClassName = packageName + DOT_CHR + componentClass.getName() + ARRAY_CLASS_NAME_SUFFIX;
            } else {
                packageName = ARRAY_PACKAGE_NAME + DOT_CHR + componentClass.getPackageName();
                if (componentClass.isMemberClass()) {
                    qualifiedClassName = componentClass.getName();
                    qualifiedClassName = qualifiedClassName.substring(qualifiedClassName.indexOf(DOLLAR_SIGN_CHR) + 1);
                    qualifiedClassName = ARRAY_PACKAGE_NAME + DOT_CHR + componentClass.getPackageName() + DOT_CHR + qualifiedClassName + ARRAY_CLASS_NAME_SUFFIX;
                } else {
                    qualifiedClassName = ARRAY_PACKAGE_NAME + DOT_CHR + componentClass.getQualifiedName() + ARRAY_CLASS_NAME_SUFFIX;
                }
            }

            if (componentClass.isPrimitive() || helper.isBuiltInJavaType(componentClass)) {
                qName = (QName) helper.getXMLToJavaTypeMap().get(componentClass.getQualifiedName());
                if(null != qName) {
                    packageName = ARRAY_PACKAGE_NAME;
                    qualifiedClassName = ARRAY_PACKAGE_NAME + DOT_CHR + qName.getLocalPart() + ARRAY_CLASS_NAME_SUFFIX;
                }
                PackageInfo namespaceInfo = getPackageToPackageInfoMappings().get(packageName);
                if (namespaceInfo == null) {
                    namespaceInfo = new PackageInfo();
                    namespaceInfo.setNamespaceInfo(new NamespaceInfo());
                    namespaceInfo.setNamespace(ARRAY_NAMESPACE);
                    namespaceInfo.setNamespaceResolver(new NamespaceResolver());
                    getPackageToPackageInfoMappings().put(packageName, namespaceInfo);
                }
            } else {
                PackageInfo namespaceInfo = getPackageInfoForPackage(componentClass.getPackage(), componentClass.getPackageName());
                getPackageToPackageInfoMappings().put(packageName, namespaceInfo);
            }
        }

        try {
            String qualifiedInternalClassName = qualifiedClassName.replace(DOT_CHR, SLASH_CHR);
            if (helper.getJavaClass(ManyValue.class).isAssignableFrom(componentClass)) {
                return generateClassFromBytes(qualifiedClassName, generateMultiDimensionalManyValueClass(typeMappingInfo, null, MultiDimensionalArrayValue.class, qualifiedInternalClassName, componentClass, arrayClass.getComponentType()));
            } else {
                return generateClassFromBytes(qualifiedClassName, generateManyValue(typeMappingInfo, null, ArrayValue.class, qualifiedInternalClassName, componentClass, componentClass));
            }
        } catch(LinkageError e) {
            if(null != qName) {
                throw JAXBException.nameCollision(qName.getNamespaceURI(), qName.getLocalPart());
            }
            throw e;
        }
    }

    private JavaClass getObjectType(JavaClass javaClass) {
        if (javaClass.isPrimitive()) {
            String primitiveClassName = javaClass.getRawName();
            Class primitiveClass = getPrimitiveClass(primitiveClassName);
            return helper.getJavaClass(getObjectClass(primitiveClass));
        }
        return javaClass;
    }

    private Class generateCollectionValue(JavaClass collectionClass, TypeMappingInfo typeMappingInfo, Class xmlElementType, List<JavaClass> classesToProcess) {

        JavaClass componentClass;

        if (typeMappingInfo != null && xmlElementType != null) {
            componentClass = helper.getJavaClass(xmlElementType);
        } else{
            Collection args = collectionClass.getActualTypeArguments();
            if(args.size() >0 ){
                componentClass = ((JavaClass) args.toArray()[0]);    
            }else{
                componentClass = helper.getJavaClass(Object.class);                
            }
        }       

        boolean multiDimensional = false;
        if (componentClass.isPrimitive()) {
            Class primitiveClass = getPrimitiveClass(componentClass.getRawName());
            componentClass = helper.getJavaClass(getObjectClass(primitiveClass));
        } else if(helper.getJavaClass(Collection.class).isAssignableFrom(componentClass)) {
            multiDimensional = true;
            Class nestedCollectionClass = collectionClassesToGeneratedClasses.get(componentClass.getName());
            if (nestedCollectionClass == null) {
                nestedCollectionClass = generateCollectionValue(componentClass, typeMappingInfo, xmlElementType, classesToProcess);
                arrayClassesToGeneratedClasses.put(componentClass.getName(), nestedCollectionClass);
                classesToProcess.add(helper.getJavaClass(nestedCollectionClass));
            }
            componentClass = helper.getJavaClass(nestedCollectionClass);
        } else if(componentClass.isArray()) {
            if(componentClass.getName().equals("[B")) {
                multiDimensional = false;
            } else {
                multiDimensional = true;
                Class nestedArrayClass = arrayClassesToGeneratedClasses.get(componentClass.getName());
                if (nestedArrayClass == null) {
                    nestedArrayClass = generateWrapperForArrayClass(componentClass, typeMappingInfo, xmlElementType, classesToProcess);
                    arrayClassesToGeneratedClasses.put(componentClass.getName(), nestedArrayClass);
                }
                componentClass = helper.getJavaClass(nestedArrayClass);
            }
        }

        PackageInfo packageInfo = packageToPackageInfoMappings.get(collectionClass.getPackageName());

        String namespace = EMPTY_STRING;
        if (this.defaultTargetNamespace != null) {
            namespace = this.defaultTargetNamespace;
        }

        PackageInfo componentNamespaceInfo = getPackageInfoForPackage(componentClass);
        String packageName = componentClass.getPackageName();
        packageName = "jaxb.dev.java.net." + packageName;
        if (packageInfo == null) {
            packageInfo = getPackageToPackageInfoMappings().get(packageName);
        } else {
            getPackageToPackageInfoMappings().put(packageName, packageInfo);
            if (packageInfo.getNamespace() != null) {
                namespace = packageInfo.getNamespace();
            }
        }
        if (packageInfo == null) {
            if (componentNamespaceInfo != null) {
                packageInfo = componentNamespaceInfo;
            } else {
                packageInfo = new PackageInfo();
                packageInfo.setNamespaceInfo(new NamespaceInfo());
                packageInfo.setNamespaceResolver(new NamespaceResolver());
            }
            getPackageToPackageInfoMappings().put(packageName, packageInfo);
        }

        String name = componentClass.getName();
        if("[B".equals(name)) {
            name = "byteArray";
        }

        int beginIndex = name.lastIndexOf(DOT_CHR) + 1;
        name = name.substring(beginIndex);
        int dollarIndex = name.indexOf(DOLLAR_SIGN_CHR);
        if (dollarIndex > -1) {
            name = name.substring(dollarIndex + 1);
        }
        String collectionClassRawName = collectionClass.getRawName();

        String collectionClassShortName = collectionClassRawName.substring(collectionClassRawName.lastIndexOf(DOT_CHR) + 1);
        String suggestedClassName = collectionClassShortName + "Of" + name;
        String qualifiedClassName = packageName + DOT_CHR + suggestedClassName;
        qualifiedClassName = getNextAvailableClassName(qualifiedClassName);

        String qualifiedInternalClassName = qualifiedClassName.replace(DOT_CHR, SLASH_CHR);

        byte[] classBytes;
        if(multiDimensional) {
            classBytes = generateMultiDimensionalManyValueClass(typeMappingInfo, namespace, MultiDimensionalCollectionValue.class, qualifiedInternalClassName, componentClass, collectionClass);
        } else {
            classBytes = generateManyValue(typeMappingInfo, namespace, CollectionValue.class, qualifiedInternalClassName, componentClass, collectionClass);
        }
        return generateClassFromBytes(qualifiedClassName, classBytes);
    }

    private byte[] generateManyValue(TypeMappingInfo typeMappingInfo, String namespace, Class superType, String classNameSeparatedBySlash, JavaClass componentType, JavaClass containerType) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        generateManyValueClass(cw, typeMappingInfo, namespace, superType, classNameSeparatedBySlash, componentType, containerType);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private void generateManyValueClass(ClassWriter cw, TypeMappingInfo typeMappingInfo, String namespace, Class superType, String classNameSeparatedBySlash, JavaClass componentType, JavaClass containerType) {
        String componentClassNameSeparatedBySlash = getObjectType(componentType).getQualifiedName().replace(DOT_CHR, SLASH_CHR);
        String containerClassNameSeperatedBySlash = containerType.getQualifiedName().replace(DOT_CHR, SLASH_CHR);
        if("[B".equals(componentClassNameSeparatedBySlash)) {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNameSeparatedBySlash, "L" + Type.getInternalName(superType) + "<" + componentClassNameSeparatedBySlash + ">;", Type.getInternalName(superType), null);
        } else {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNameSeparatedBySlash, "L" + Type.getInternalName(superType) + "<L" + componentClassNameSeparatedBySlash + ";>;", Type.getInternalName(superType), null);
        }

        // Write @XmlType(namespace)
        AnnotationVisitor av = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
        if(null != namespace) {
            av.visit("namespace", namespace);
        }
        if(classNameSeparatedBySlash.startsWith(ARRAY_PACKAGE_NAME.replace('.', '/')) && classNameSeparatedBySlash.contains("QName") ) {
            av.visit("name", classNameSeparatedBySlash.substring(classNameSeparatedBySlash.lastIndexOf('/') + 1));
        }
        av.visitEnd();

        // Public No-Arg Constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superType), "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        if(!componentType.isPrimitive() &&  ArrayValue.class.isAssignableFrom(superType)){
        
        	//@Override
    	    //public Object getItem() {
    	    //    if(null == adaptedValue) {
    	    //        return null;
    	    //    }    	      
    	    //    int len = adaptedValue.size();
    		//    Float[] array = new Float[len];
    	    //    adaptedValue.toArray(array);
    	    //    return array;      
    	    // }     
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getItem", "()Ljava/lang/Object;", null, null);
        	mv.visitCode();
        	mv.visitVarInsn(Opcodes.ALOAD, 0);
        	mv.visitFieldInsn(Opcodes.GETFIELD, classNameSeparatedBySlash, "adaptedValue", "Ljava/util/Collection;");
        	Label l0 = new Label();
        	mv.visitJumpInsn(Opcodes.IFNONNULL, l0);
        	mv.visitInsn(Opcodes.ACONST_NULL);
        	mv.visitInsn(Opcodes.ARETURN);
        	mv.visitLabel(l0);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitVarInsn(Opcodes.ALOAD, 0);
        	mv.visitFieldInsn(Opcodes.GETFIELD, classNameSeparatedBySlash, "adaptedValue", "Ljava/util/Collection;");
        	mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Collection", "size", "()I");
        	mv.visitVarInsn(Opcodes.ISTORE, 1);
        	mv.visitVarInsn(Opcodes.ILOAD, 1);
        	mv.visitTypeInsn(Opcodes.ANEWARRAY, componentClassNameSeparatedBySlash);
        	mv.visitVarInsn(Opcodes.ASTORE, 2);
        	mv.visitVarInsn(Opcodes.ALOAD, 0);
        	mv.visitFieldInsn(Opcodes.GETFIELD, classNameSeparatedBySlash, "adaptedValue", "Ljava/util/Collection;");
        	mv.visitVarInsn(Opcodes.ALOAD, 2);
        	mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Collection", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;");
        	mv.visitInsn(Opcodes.POP);        	
        	
        	mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitInsn(Opcodes.ARETURN);
        	mv.visitMaxs(2, 3);        	
        	mv.visitEnd();
        	
        		
        	//@Override
          	//public void setItem(Object array) {
          	//    Float[] floatArray = (Float[])array;
          	//	adaptedValue =   (Collection<T>) Arrays.asList(floatArray);
          	//}          	    
        	mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setItem", "(Ljava/lang/Object;)V", null, null);
        	mv.visitCode();
        	mv.visitVarInsn(Opcodes.ALOAD, 1);
        	mv.visitTypeInsn(Opcodes.CHECKCAST, "[L"+componentClassNameSeparatedBySlash+";");
        	mv.visitVarInsn(Opcodes.ASTORE, 2);
        	mv.visitVarInsn(Opcodes.ALOAD, 0);
        	mv.visitVarInsn(Opcodes.ALOAD, 2);
        	mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;");
        	mv.visitFieldInsn(Opcodes.PUTFIELD, classNameSeparatedBySlash, "adaptedValue", "Ljava/util/Collection;");
        	mv.visitInsn(Opcodes.RETURN);
        	mv.visitMaxs(2, 3);
        	mv.visitEnd();        		
        }
        
        
        // @XmlElement(name="item", nillable=true)
        // public Collection<COMPONENT_TYPE> getAdaptedValue() {
        //    return super.getAdaptedValue();
        // }
        // OR
        // @XmlValue
        // public Collection<COMPONENT_TYPE> getAdaptedValue() {
        //    return super.getAdaptedValue();
        // }
        if("[B".equals(componentClassNameSeparatedBySlash)) {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getAdaptedValue", "()Ljava/util/Collection;", "()Ljava/util/Collection<" + componentClassNameSeparatedBySlash + ">;", null);
        } else {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getAdaptedValue", "()Ljava/util/Collection;", "()Ljava/util/Collection<L" + componentClassNameSeparatedBySlash + ";>;", null);
        }
        // Copy annotations
        boolean hasXmlList = false;
        Annotation[] annotations;
        if (typeMappingInfo != null && ((annotations = getAnnotations(typeMappingInfo)) != null)) {
            for (Annotation annotation : annotations) {
                if(!(annotation instanceof XmlElement || annotation instanceof XmlJavaTypeAdapter)) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    //if(annotationType.equals(XmlList.class)) {
                    if(annotation instanceof XmlList) {
                        hasXmlList = true;
                    }
                    av = mv.visitAnnotation(L + annotationType.getName().replace(DOT_CHR, SLASH_CHR) + SEMI_COLON, true);
                    for (Method next : annotation.annotationType().getDeclaredMethods()) {
                        try {
                            Object nextValue = next.invoke(annotation, new Object[] {});
                            if (nextValue instanceof Class) {
                                nextValue = Type.getType(L + ((Class) nextValue).getName().replace(DOT_CHR, SLASH_CHR) + SEMI_COLON);
                            }
                            av.visit(next.getName(), nextValue);
                        } catch (InvocationTargetException ex) {
                        } catch (IllegalAccessException ex) {
                        }
                    }
                    av.visitEnd();
                }
            }
        }
        if(hasXmlList) {
            // @XmlValue
            av = mv.visitAnnotation("Ljavax/xml/bind/annotation/XmlValue;", true);
            av.visitEnd();
        } else {
            // @XmlElement(name="item", nillable=true)
            av = mv.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
            av.visit("name", ITEM);
            av.visit("nillable", true);
            av.visitEnd();
        }

        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superType), "getAdaptedValue", "()Ljava/util/Collection;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // public void setAdaptedValue(Collection<COMPONENT_TYPE> adaptedValue) {
        //     super.setAdaptedValue(adaptedValue);
        // }
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setAdaptedValue", "(Ljava/util/Collection;)V", "(Ljava/util/Collection<L" + componentClassNameSeparatedBySlash + ";>;)V", null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superType), "setAdaptedValue", "(Ljava/util/Collection;)V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        // public Class<?> containerClass() {
        //     return CONTAINER_TYPE.class;
        // }
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED, "containerClass", "()Ljava/lang/Class;", "()Ljava/lang/Class<*>;", null);
        mv.visitCode();
        if(componentType.isPrimitive()) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, getObjectType(componentType).getQualifiedName().replace(DOT_CHR, SLASH_CHR), "TYPE", "Ljava/lang/Class;");
        } else {
            if(containerClassNameSeperatedBySlash.contains(";")) {
                mv.visitLdcInsn(Type.getType(containerClassNameSeperatedBySlash));
            } else {
                mv.visitLdcInsn(Type.getType("L" + containerClassNameSeperatedBySlash + ";"));
            }
        }
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private byte[] generateMultiDimensionalManyValueClass(TypeMappingInfo typeMappingInfo, String namespace, Class superType, String classNameSeparatedBySlash, JavaClass componentType, JavaClass containerType) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        generateManyValueClass(cw, typeMappingInfo, namespace, superType, classNameSeparatedBySlash, componentType, containerType);
        generateMultiDimensionalManyValueClass(cw, componentType);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private void generateMultiDimensionalManyValueClass(ClassWriter cw, JavaClass componentType) {
        // public Class<?> componentClass() {
        //    return COMPONENT_TYPE.class;
        // }
        String componentClassNameSeparatedBySlash = componentType.getQualifiedName().replace(DOT_CHR, SLASH_CHR);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PROTECTED, "componentClass", "()Ljava/lang/Class;", "()Ljava/lang/Class<L" + componentClassNameSeparatedBySlash + ";>;", null);
        mv.visitCode();
        mv.visitLdcInsn(Type.getType("L" + componentClassNameSeparatedBySlash + ";"));
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private Class generateClassFromBytes(String className, byte[] classBytes) {
        JaxbClassLoader loader = (JaxbClassLoader) helper.getClassLoader();
        Class generatedClass = loader.generateClass(className, classBytes);
        return generatedClass;
    }

    /**
     * Inner class used for ordering a list of Properties alphabetically by
     * property name.
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
     * Convenience method for returning all of the TypeInfo objects for a given
     * package name.
     * 
     * This method is inefficient as we need to iterate over the entire typeinfo
     * map for each call. We should eventually store the TypeInfos in a Map
     * based on package name, i.e.:
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
        //this.packageToNamespaceMappings = packageToNamespaceMappings;
    }
    
    public void setPackageToPackageInfoMappings(HashMap<String, PackageInfo> packageToPackageInfoMappings) {
        this.packageToPackageInfoMappings = packageToPackageInfoMappings;
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
            this.packageToPackageInfoMappings = new HashMap<String, PackageInfo>();
        }

        JavaClass[] jClasses = new JavaClass[] { javaClass };
        buildNewTypeInfo(jClasses);
        TypeInfo info = typeInfo.get(javaClass.getQualifiedName());

        PackageInfo packageInfo;
        String packageName = javaClass.getPackageName();
        packageInfo = this.packageToPackageInfoMappings.get(packageName);

        SchemaTypeInfo schemaInfo = new SchemaTypeInfo();
        schemaInfo.setSchemaTypeName(new QName(info.getClassNamespace(), info.getSchemaTypeName()));

        if (info.isSetXmlRootElement()) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement xmlRE = info.getXmlRootElement();
            String elementName = xmlRE.getName();
            if (elementName.equals(XMLProcessor.DEFAULT) || elementName.equals(EMPTY_STRING)) {
                try {
                    elementName = info.getXmlNameTransformer().transformRootElementName(javaClass.getName());
                } catch (Exception ex) {
                    throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringNameTransformation(javaClass.getName(), info.getXmlNameTransformer().getClass().getName(), ex);
                }
            }
            String rootNamespace = xmlRE.getNamespace();
            QName rootElemName = null;
            if (rootNamespace.equals(XMLProcessor.DEFAULT)) {
                rootElemName = new QName(packageInfo.getNamespace(), elementName);
            } else {
                rootElemName = new QName(rootNamespace, elementName);
            }
            schemaInfo.getGlobalElementDeclarations().add(rootElemName);
            ElementDeclaration declaration = new ElementDeclaration(rootElemName, javaClass, javaClass.getRawName(), false);
            addGlobalElement(rootElemName, declaration);
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
        javaClasses = postBuildTypeInfo(javaClasses);
        for(JavaClass next:javaClasses) {
            processPropertyTypes(next);
        }
    }

    /**
     * Pre-process a descriptor customizer. Here, the given JavaClass is checked
     * for the existence of an @XmlCustomizer annotation.
     * 
     * Note that the post processing of the descriptor customizers will take
     * place in MappingsGenerator's generateProject method, after the
     * descriptors and mappings have been generated.
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
     * @param factoryClassName
     *            ObjectFactory class name
     * @param xmlReg
     *            org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry instance
     */
    public void addXmlRegistry(String factoryClassName, org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry xmlReg) {
        this.xmlRegistries.put(factoryClassName, xmlReg);
    }

    /**
     * Convenience method for determining if a given JavaClass should be
     * processed as an ObjectFactory class.
     * 
     * @param javaClass
     * @return true if the JavaClass is annotated with @XmlRegistry or the map
     *         of XmlRegistries contains a key equal to the JavaClass' qualified
     *         name
     */
    private boolean isXmlRegistry(JavaClass javaClass) {
        if (javaClass == null) {
            return false;
        }
        return (helper.isAnnotationPresent(javaClass, XmlRegistry.class) || xmlRegistries.get(javaClass.getQualifiedName()) != null);
    }

    public Map<TypeMappingInfo, QName> getTypeMappingInfoToSchemaType() {
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

    private void addGlobalElement(QName key, ElementDeclaration declaration){
        getGlobalElements().put(key, declaration);
        classesToProcessPropertyTypes.add(declaration.getJavaType());
    }
    
    private Map<Object, Object> createUserPropertiesMap(XmlProperty[] properties) {
        Map<Object, Object> propMap = new HashMap<Object, Object>();
        for (XmlProperty prop : properties) {
            Object pvalue = prop.value();
            if (!(prop.valueType() == String.class)) {
                pvalue = XMLConversionManager.getDefaultXMLManager().convertObject(prop.value(), prop.valueType());
            }
            propMap.put(prop.name(), pvalue);
        }
        return propMap;
    }

    /**
     * Indicates if a given Property represents an MTOM attachment. Will return
     * true if the given Property's actual type is one of:
     * 
     * - DataHandler - byte[] - Byte[] - Image - Source - MimeMultipart
     * 
     * @param property
     * @return
     */
    public boolean isMtomAttachment(Property property) {
        JavaClass ptype = property.getActualType();
        return (areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER) || areEquals(ptype, byte[].class) || areEquals(ptype, Image.class) || areEquals(ptype, Source.class) || areEquals(ptype, JAVAX_MAIL_INTERNET_MIMEMULTIPART));
    }

    public boolean hasSwaRef() {
        return this.hasSwaRef;
    }

    public void setHasSwaRef(boolean swaRef) {
        this.hasSwaRef = swaRef;
    }
    
    public List getReferencedByTransformer(){
    	return referencedByTransformer;
    }

    /**
     * Indicates whether this AnnotationsProcessor has been configured to enable
     * processing of XmlAccessorFactory annotations.
     * 
     * @see com.sun.xml.bind.XmlAccessorFactory
     */
    public boolean isXmlAccessorFactorySupport() {
        return xmlAccessorFactorySupport;
    }

    /**
     * Sets whether this AnnotationsProcessor should process XmlAccessorFactory annotations.
     * 
     * @see com.sun.xml.bind.XmlAccessorFactory
     */
    public void setXmlAccessorFactorySupport(boolean value) {
        this.xmlAccessorFactorySupport = value;
    }

    public void setHasXmlBindings(boolean b) {
        this.hasXmlBindings = true;
    }
    
    public boolean hasXmlBindings() {
        return this.hasXmlBindings;
    }
}
