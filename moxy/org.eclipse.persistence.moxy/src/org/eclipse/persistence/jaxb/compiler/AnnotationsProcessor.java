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

import java.awt.Image;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlType.DEFAULT;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;

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

    private ArrayList<JavaClass> typeInfoClasses; 
    private HashMap<String, NamespaceInfo> packageToNamespaceMappings;
    private HashMap<String, MarshalCallback> marshalCallbacks;
    private HashMap<String, QName> userDefinedSchemaTypes;
    private HashMap<String, TypeInfo> typeInfo;
    private ArrayList<QName> typeQNames;
    private HashMap<String, UnmarshalCallback> unmarshalCallbacks;
    private HashMap<QName, ElementDeclaration> globalElements;
    private HashMap<String, ElementDeclaration> xmlRootElements;
    private HashMap<String, JavaMethod> factoryMethods;
    private NamespaceResolver namespaceResolver;
    private Helper helper;

    public AnnotationsProcessor(Helper helper) {
        this.helper = helper;
    }
    
    public void processClassesAndProperties(JavaClass[] classes) {
        typeInfoClasses = new ArrayList<JavaClass>();
        typeInfo = new HashMap<String, TypeInfo>();
        typeQNames = new ArrayList<QName>();        
        userDefinedSchemaTypes = new HashMap<String, QName>();
        packageToNamespaceMappings = new HashMap<String, NamespaceInfo>(); 
        this.factoryMethods = new HashMap<String, JavaMethod>();
        this.namespaceResolver = new NamespaceResolver();
        this.xmlRootElements = new HashMap<String, ElementDeclaration>();
        
        ArrayList<JavaClass> classesToProcess = new ArrayList<JavaClass>();
        //check for ObjectFactories and process them
        for(JavaClass javaClass:classes) {
        	if(helper.isAnnotationPresent(javaClass, XmlRegistry.class)) {
        		this.processObjectFactory(javaClass, classesToProcess);
        	} else if(!helper.isAnnotationPresent(javaClass, XmlTransient.class)){
        		classesToProcess.add(javaClass);
        		//reflectively load XmlSeeAlso class to avoid dependency
        		Class xmlSeeAlsoClass = null;
        		Method valueMethod = null;
        		try {
        		    xmlSeeAlsoClass = PrivilegedAccessHelper.getClassForName("javax.xml.bind.annotation.XmlSeeAlso");
        		    valueMethod = PrivilegedAccessHelper.getDeclaredMethod(xmlSeeAlsoClass, "value", new Class[]{});
        		} catch(ClassNotFoundException ex) {
        		    //Ignore this exception. If SeeAlso isn't available, don't try to process
        		} catch(NoSuchMethodException ex) {
        		    
        		}
        		if(xmlSeeAlsoClass != null && helper.isAnnotationPresent(javaClass, xmlSeeAlsoClass)) {
        		    Object seeAlso = helper.getAnnotation(javaClass, xmlSeeAlsoClass);
        		    Class[] values = null;
        		    try {
        		        values = (Class[])PrivilegedAccessHelper.invokeMethod(valueMethod, seeAlso, new Object[]{});
        		    } catch(Exception ex) {}
        		    for(Class next:values) {
        		        classesToProcess.add(helper.getJavaClass(next));
        		    }
        		}
        		//handle inner classes
                for (Iterator<JavaClass> jClassIt = javaClass.getDeclaredClasses().iterator(); jClassIt.hasNext(); ) {
                    JavaClass innerClass = jClassIt.next();
                    if (shouldGenerateTypeInfo(innerClass)) {
                        if(!(helper.isAnnotationPresent(innerClass, XmlTransient.class))) {
                            classesToProcess.add(innerClass);
                        }
                    }
                }
        		
        	}
        	
        }
        
        updateGlobalElements(classesToProcess);
        
        for (JavaClass javaClass : classesToProcess) {
            if (javaClass == null) { continue; }

            createTypeInfoFor(javaClass);

            JavaClass superClass = (JavaClass) javaClass.getSuperclass();
            if (shouldGenerateTypeInfo(superClass)) {
                createTypeInfoFor(superClass);
            }
        }
        checkForCallbackMethods();
    }    
    
    public SchemaTypeInfo addClass(JavaClass javaClass) {
        if (javaClass == null) { 
            return null;
        } else if(helper.isAnnotationPresent(javaClass, XmlTransient.class)) {
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
        TypeInfo info = createTypeInfoFor(javaClass);

        NamespaceInfo namespaceInfo;
        JavaPackage pack = javaClass.getPackage();
        namespaceInfo = this.packageToNamespaceMappings.get(pack.getQualifiedName());
        
        SchemaTypeInfo schemaInfo = new SchemaTypeInfo();
        schemaInfo.setSchemaTypeName(new QName(info.getClassNamespace(), info.getSchemaTypeName()));
        
        if (helper.isAnnotationPresent(javaClass, XmlRootElement.class)) {
            XmlRootElement rootElemAnnotation = (XmlRootElement) helper.getAnnotation(javaClass, XmlRootElement.class);
            String elementName = rootElemAnnotation.name();
            if (elementName.equals("##default") || elementName.equals("")) {
                if (javaClass.getName().indexOf("$") != -1) {
                    elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('$') + 1));
                } else {
                    elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('.') + 1));                    
                }
                
                // TODO - remove this TCK hack...
                if (elementName.length() >= 3) {
                    int idx = elementName.length()-1;
                    char ch = elementName.charAt(idx-1);
                    if (Character.isDigit(ch)) {
                        char lastCh = Character.toUpperCase(elementName.charAt(idx));
                        elementName = elementName.substring(0, idx) + lastCh;
                    }
                }
                
            }
            String rootNamespace = rootElemAnnotation.namespace();
            QName rootElemName = null;
            if (rootNamespace.equals("##default")) {
                rootElemName = new QName(namespaceInfo.getNamespace(), elementName);
            } else {
                rootElemName = new QName(rootNamespace, elementName);
            }
            schemaInfo.getGlobalElementDeclarations().add(rootElemName);
            ElementDeclaration declaration = new ElementDeclaration(rootElemName, javaClass, javaClass.getRawName(), false);
            this.globalElements.put(rootElemName, declaration);
        }
        
        return schemaInfo;
    }

    public TypeInfo createTypeInfoFor(JavaClass javaClass) {
        if (javaClass == null) { return null; }

        if (typeInfo.containsKey(javaClass.getQualifiedName())) {
            return typeInfo.get(javaClass.getQualifiedName());
        }
        
        TypeInfo info = null;
        if (javaClass.isEnum()) {
            info = new EnumTypeInfo(helper);
        } else {
            info = new TypeInfo(helper);
        }

        JavaMethod factoryMethod = this.factoryMethods.get(javaClass.getRawName()); 
        if(factoryMethod != null) {
            //set up factory method info for mappings.
            info.setFactoryMethodName(factoryMethod.getName());
            info.setObjectFactoryClassName(factoryMethod.getOwningClass().getRawName());
            JavaClass[] paramTypes = factoryMethod.getParameterTypes();
            if(paramTypes != null && paramTypes.length > 0) {
                String[] paramTypeNames = new String[paramTypes.length];
                for(int i = 0; i < paramTypes.length; i++) {
                    paramTypeNames[i] = paramTypes[i].getRawName();
                }
                info.setFactoryMethodParamTypes(paramTypeNames);
            }
        }
        JavaPackage pack = javaClass.getPackage();
        // handle package level adapters (add them to the type info for this class)
        if (helper.isAnnotationPresent(pack, XmlJavaTypeAdapters.class)) {
            XmlJavaTypeAdapters adapters = (XmlJavaTypeAdapters) helper.getAnnotation(pack, XmlJavaTypeAdapters.class);
            XmlJavaTypeAdapter[] adapterArray = adapters.value();
            for (XmlJavaTypeAdapter next : adapterArray) {
                JavaClass adapterClass = helper.getJavaClass(next.value());
                JavaClass boundType = helper.getJavaClass(next.type());
                if (boundType != null) {
                    info.addAdapterClass(adapterClass, boundType);
                } else {
                    // TODO: Throw an error?
                }
            }
        }
        // handle class level adapters (add them to the type info for this class)
        if (helper.isAnnotationPresent(javaClass, XmlJavaTypeAdapters.class)) {
            XmlJavaTypeAdapters adapters = (XmlJavaTypeAdapters) helper.getAnnotation(javaClass, XmlJavaTypeAdapters.class);
            XmlJavaTypeAdapter[] adapterArray = adapters.value();
            for (XmlJavaTypeAdapter next : adapterArray) {
                JavaClass adapterClass = helper.getJavaClass(next.value());
                JavaClass boundType = helper.getJavaClass(next.type());
                if(boundType != null) {
                    info.addAdapterClass(adapterClass, boundType);
                }
            }
        }

        // figure out namespace info
        NamespaceInfo packageNamespace = getNamespaceInfoForPackage(pack);

        if (helper.isAnnotationPresent(pack, XmlSchemaTypes.class)) {
            XmlSchemaTypes types = (XmlSchemaTypes) helper.getAnnotation(pack, XmlSchemaTypes.class); 
            XmlSchemaType[] typeArray = types.value();
            for (XmlSchemaType next : typeArray) {
                processSchemaType(next);
            }
        } else if (helper.isAnnotationPresent(pack, XmlSchemaType.class)) {
            processSchemaType((XmlSchemaType) helper.getAnnotation(pack, XmlSchemaType.class));
        }
      

        String[] propOrder = new String[]{""};
        String typeName = "";
        
        if (helper.isAnnotationPresent(javaClass, XmlType.class)) {
            // figure out type name
            XmlType typeAnnotation = (XmlType) helper.getAnnotation(javaClass, XmlType.class);
            typeName = typeAnnotation.name();
            if (typeName.equals("##default")) {
                typeName = getSchemaTypeNameForClassName(javaClass.getName());
            }
            propOrder = typeAnnotation.propOrder();
            if (!typeAnnotation.namespace().equals("##default")) {
                info.setClassNamespace(typeAnnotation.namespace());
            } else {
                info.setClassNamespace(packageNamespace.getNamespace());
            }
            Class factoryClass = typeAnnotation.factoryClass();
            if(factoryClass != DEFAULT.class) {
                String factoryMethodName = typeAnnotation.factoryMethod();
                if(factoryMethodName == null || factoryMethodName.equals("")) {
                    throw org.eclipse.persistence.exceptions.JAXBException.factoryClassWithoutFactoryMethod(javaClass.getName());
                }
                info.setFactoryMethodName(factoryMethodName);
                info.setObjectFactoryClassName(factoryClass.getCanonicalName());
            } else {
                String factoryMethodName = typeAnnotation.factoryMethod();
                if(factoryMethodName != null && !factoryMethodName.equals("")) {
                    //factory method applies to the current class verify method exists
                    JavaMethod method = javaClass.getDeclaredMethod(factoryMethodName, new JavaClass[]{});
                    if(method == null) {
                        throw org.eclipse.persistence.exceptions.JAXBException.factoryMethodNotDeclared(factoryMethodName, javaClass.getName());
                    }
                    info.setFactoryMethodName(factoryMethodName);
                    info.setObjectFactoryClassName(javaClass.getRawName());
                }
            }
        } else {
            typeName = getSchemaTypeNameForClassName(javaClass.getName());
            info.setClassNamespace(packageNamespace.getNamespace());
        }
        info.setPropOrder(propOrder);
        info.setSchemaTypeName(typeName);
        
        if (info.isEnumerationType()) {
            addEnumTypeInfo(javaClass, ((EnumTypeInfo)info));
            return info;
        } 
        
        typeInfoClasses.add(javaClass);
        typeInfo.put(javaClass.getQualifiedName(), info);
        if(typeName!= null && !("".equals(typeName))){
        	QName typeQName = new QName(packageNamespace.getNamespace(), typeName);
        	boolean containsQName = typeQNames.contains(typeQName);
        	if(containsQName){
        		throw JAXBException.nameCollision(typeQName.getNamespaceURI(), typeQName.getLocalPart());
        	}else{
        		typeQNames.add(typeQName);        	
       		}        	
        }
        if (helper.isAnnotationPresent(javaClass, XmlAccessorType.class)) {
            XmlAccessorType accessorType = (XmlAccessorType) helper.getAnnotation(javaClass, XmlAccessorType.class);
            info.setAccessType(accessorType.value());
        } else {
            info.setAccessType(packageNamespace.getAccessType());
        }
        
        info.setProperties(getPropertiesForClass(javaClass, info));
        XmlAccessorOrder order = null;
        if(helper.isAnnotationPresent(pack, XmlAccessorOrder.class)) {
            order = (XmlAccessorOrder) helper.getAnnotation(pack, XmlAccessorOrder.class);
        }
        
        if (helper.isAnnotationPresent(javaClass, XmlAccessorOrder.class)) {
            order = (XmlAccessorOrder) helper.getAnnotation(javaClass, XmlAccessorOrder.class);
        }
        
        if(order != null) {
            info.orderProperties(order.value());
        }
        
        JavaClass superClass = (JavaClass) javaClass.getSuperclass();
        if (shouldGenerateTypeInfo(superClass)) {
            createTypeInfoFor(superClass);
        }
        
        ArrayList<Property> properties = info.getPropertyList();
        for (Property property : properties) {
            JavaClass propertyType = property.getType();
            if (this.isCollectionType(property)) {
                // check for a generic type
                JavaClass gType = property.getGenericType();
                if (gType != null) {
                    // handle nested generics
                    if (gType.hasActualTypeArguments()) {
                        propertyType = helper.getJavaClass(gType.getRawName());                                    
                    } else if (gType instanceof JavaClass) {
                        propertyType = (JavaClass) gType;
                    }
                }
            } else if (propertyType.isArray()) {
                propertyType = (JavaClass) propertyType.getComponentType();
            }
            if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
                XmlElement element = (XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class);
                if (element.type() != XmlElement.DEFAULT.class) {
                    propertyType = helper.getJavaClass(element.type());
                }
            }
            // handle @XmlID
            if (helper.isAnnotationPresent(property.getElement(), XmlID.class)) {
                if (!areEquals(property.getType(), String.class)) {
                    // TODO: throw an exception here
                }
                if (info.isIDSet()) {
                    // TODO: throw an exception here
                }
                info.setIDProperty(property);
            }
            // handle @XmlJavaTypeAdapter
            if (helper.isAnnotationPresent(property.getElement(), XmlJavaTypeAdapter.class)) {
                property.setAdapterClass(((XmlJavaTypeAdapter) helper.getAnnotation(property.getElement(), XmlJavaTypeAdapter.class)).value());
            }

            if (shouldGenerateTypeInfo(propertyType)) {
                createTypeInfoFor(propertyType);
            }
        }
        //Make sure this class has a factory method or a zero arg constructor
        if(info.getFactoryMethodName() == null && info.getObjectFactoryClassName() == null) {
            JavaConstructor zeroArgConstructor = javaClass.getDeclaredConstructor(new JavaClass[]{});
            if(zeroArgConstructor == null) {
                throw org.eclipse.persistence.exceptions.JAXBException.factoryMethodOrConstructorRequired(javaClass.getName());
            }
        }
        return info;
    }

    public boolean shouldGenerateTypeInfo(JavaClass javaClass) {
        if (javaClass == null || javaClass.isPrimitive() || javaClass.isAnnotation() || javaClass.isInterface() || javaClass.isArray()) {
            return false;
        }
        
        if (userDefinedSchemaTypes.get(javaClass.getQualifiedName()) != null) {
            return false;
        }
        if (helper.isBuiltInJavaType(javaClass)) {
            return false;
        }
        return true;
    }
    
    public ArrayList<Property> getPropertiesForClass(JavaClass cls, TypeInfo info) {
    	ArrayList<Property> returnList;
        if (info.getAccessType() == XmlAccessType.FIELD) {
        	returnList = getFieldPropertiesForClass(cls, info, false);
        } else if (info.getAccessType() == XmlAccessType.PROPERTY) {
        	returnList = getPropertyPropertiesForClass(cls, info, false);
        } else if (info.getAccessType() == XmlAccessType.PUBLIC_MEMBER) {
        	returnList = getPublicMemberPropertiesForClass(cls, info);
        } else {
        	returnList = getNoAccessTypePropertiesForClass(cls, info);
        }
        
        if(info.getXmlValueProperty()!=null){
	        for(Property nextProp:returnList) {	        
	        	if(!nextProp.equals(info.getXmlValueProperty()) && !nextProp.isAttribute()){
	        		throw JAXBException.propertyOrFieldShouldBeAnAttribute(nextProp.getPropertyName());
	        	}
	        }
        }
        
        return returnList;
    }
    
    public ArrayList<Property> getFieldPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList<Property> properties = new ArrayList<Property>();
        if (cls == null) { return properties; }
        
        boolean hasAnyAttribteProperty = false;
        for (Iterator<JavaField> fieldIt = cls.getDeclaredFields().iterator(); fieldIt.hasNext(); ) {
            JavaField nextField = fieldIt.next();
            if (!helper.isAnnotationPresent(nextField, XmlTransient.class)) {
                int modifiers = nextField.getModifiers();
                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && ((Modifier.isPublic(nextField.getModifiers()) && onlyPublic) || !onlyPublic)) {
                    Property property = null;
                    
                    if(helper.isAnnotationPresent((JavaHasAnnotations)nextField, XmlElements.class)) {
                        property = new ChoiceProperty(helper);
                        property.setElement((JavaHasAnnotations)nextField);
                        XmlElements xmlElements = (XmlElements)helper.getAnnotation(property.getElement(), XmlElements.class);
                        XmlElement[] elements = xmlElements.value();
                        ArrayList<Property> choiceProperties = new ArrayList<Property>(elements.length);
                        for(int i = 0; i < elements.length; i++) {
                            XmlElement next = elements[i];
                            Property choiceProp = new Property();
                            String name = next.name();
                            String namespace = next.namespace();
                            QName qName = null;
                            
                            if(name.equals("##default")) {
                                name = nextField.getName();
                            }

                            if (!namespace.equals("##default")) {
                                qName = new QName(namespace, name);
                            } else {
                            	NamespaceInfo namespaceInfo = getNamespaceInfoForPackage(cls.getPackage());
                            	if (namespaceInfo.isElementFormQualified()) {                
                                    qName = new QName(namespaceInfo.getNamespace(), name);
                                }else{
                                	qName = new QName(name);
                                }
                            }                           
                            
                            choiceProp.setPropertyName(property.getPropertyName());
                            Class typeClass = next.type();
                            
                            if(typeClass.equals(XmlElement.DEFAULT.class)){                            
                            	JavaClass type = nextField.getResolvedType();                                   
                                if(isCollectionType(type)){
                                	if(type.hasActualTypeArguments()) {
                                		JavaClass itemType = (JavaClass)type.getActualTypeArguments().toArray()[0];
                                        choiceProp.setType(itemType);
                                    } else {
                                        choiceProp.setType(helper.getJavaClass("java.lang.Object"));
                                    }
                                 }else{
                                   	choiceProp.setType(type);	
                                 }                                
                            }else{
                                choiceProp.setType(helper.getJavaClass(next.type()));
                            }
                            choiceProp.setSchemaName(qName);
                            choiceProp.setSchemaType(getSchemaTypeFor(helper.getJavaClass(next.type())));
                            choiceProp.setElement(property.getElement());
                            choiceProperties.add(choiceProp);
                        }
                        ((ChoiceProperty)property).setChoiceProperties(choiceProperties);
                    }
                    else if(helper.isAnnotationPresent((JavaHasAnnotations)nextField, XmlAnyElement.class)) {
                        property = new AnyProperty(helper);
                        property.setElement((JavaHasAnnotations)nextField);
                        XmlAnyElement anyElement = (XmlAnyElement)helper.getAnnotation((JavaHasAnnotations)nextField, XmlAnyElement.class);
                        ((AnyProperty)property).setLax(anyElement.lax());
                        ((AnyProperty)property).setDomHandlerClass(anyElement.value());
                    } else if(helper.isAnnotationPresent((JavaHasAnnotations)nextField, XmlElementRef.class) || helper.isAnnotationPresent((JavaHasAnnotations)nextField, XmlElementRefs.class)) { 
                        property = new ReferenceProperty(helper);
                        property.setElement(nextField);
                        XmlElementRef[] elementRefs;
                        XmlElementRef ref = (XmlElementRef)helper.getAnnotation((JavaHasAnnotations)nextField, XmlElementRef.class);
                        if(ref != null) {
                            elementRefs = new XmlElementRef[]{ref};
                        } else {
                            XmlElementRefs refs = (XmlElementRefs)helper.getAnnotation((JavaHasAnnotations)nextField, XmlElementRefs.class);
                            elementRefs = refs.value();
                            info.setHasElementRefs(true);
                        }
                        for(XmlElementRef nextRef:elementRefs) {
                            JavaClass type = nextField.getResolvedType();
                            String typeName = type.getQualifiedName();
                            property.setType(type);
                            if(isCollectionType(property)) {
                                if(type.hasActualTypeArguments()) {
                                    type = (JavaClass)type.getActualTypeArguments().toArray()[0];
                                    typeName = type.getQualifiedName();
                                }
                            }
                            if(nextRef.type() != XmlElementRef.DEFAULT.class) {
                                typeName = helper.getJavaClass(nextRef.type()).getQualifiedName();
                            }
                            ElementDeclaration referencedElement = this.xmlRootElements.get(typeName);
                            if(referencedElement != null) {
                                addReferencedElement((ReferenceProperty)property, referencedElement);
                            } else {
                                String name = nextRef.name();
                                String namespace = nextRef.namespace();
                                if(namespace.equals("##default")) {
                                    namespace = "";
                                }
                                QName qname = new QName(namespace, name);
                                referencedElement = this.globalElements.get(qname);
                                if(referencedElement != null) {
                                    addReferencedElement((ReferenceProperty)property, referencedElement);
                                } else {
                                    throw org.eclipse.persistence.exceptions.JAXBException.invalidElementRef(property.getPropertyName(), cls.getName());
                                }
                            }
                        }
                    } else {
                        property = new Property(helper);
                        property.setElement((JavaHasAnnotations)nextField);
                    }
                    
                    // Check for mixed context
                    if (helper.isAnnotationPresent((JavaHasAnnotations)nextField, XmlMixed.class)) {
                        info.setMixed(true);
                    }

                    JavaClass ptype = (JavaClass) nextField.getResolvedType();
                    if (!helper.isAnnotationPresent(ptype, XmlTransient.class)) {
                        property.setType(ptype);
                    } else {
                        JavaClass parent = ptype.getSuperclass();
                        while (parent != null) {
                            if (parent.getName().equals("java.lang.Object")) {
                                property.setType(parent);
                                break;
                            }
                            if (!helper.isAnnotationPresent(parent, XmlTransient.class)) {
                                property.setType(parent);
                                break;
                            }
                            parent = parent.getSuperclass();
                        }
                    }
                    
                    if (helper.isAnnotationPresent(property.getElement(), XmlJavaTypeAdapter.class)) {
                        XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(property.getElement(), XmlJavaTypeAdapter.class);
                        property.setAdapterClass(adapter.value());
                    } else if (info.getAdaptersByClass().get(ptype) != null) {
                        property.setAdapterClass(info.getAdapterClass(ptype));
                    }

                    if (property.hasAdapterClass()) {
                        ptype = property.getValueType();
                    }

                    property.setGenericType(helper.getGenericType(nextField));
                    property.setPropertyName(nextField.getName());

                    if (helper.isAnnotationPresent(property.getElement(), XmlAttachmentRef.class) && areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER)) {
                        property.setIsSwaAttachmentRef(true);
                        property.setSchemaType(XMLConstants.SWA_REF_QNAME);
                    } else if (areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER) || areEquals(ptype, byte[].class) || areEquals(ptype, Byte[].class) || areEquals(ptype, Image.class) || areEquals(ptype, Source.class) || areEquals(ptype, JAVAX_MAIL_INTERNET_MIMEMULTIPART)) {
                        property.setIsMtomAttachment(true);
                        property.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
                    }
                    if (helper.isAnnotationPresent(property.getElement(), XmlMimeType.class)) {
                        property.setMimeType(((XmlMimeType) helper.getAnnotation(property.getElement(), XmlMimeType.class)).value());
                    }
                    // Get schema-type info if specified and set it on the property for later use:
                    if (helper.isAnnotationPresent(property.getElement(), XmlSchemaType.class)) {
                        XmlSchemaType schemaType = (XmlSchemaType) helper.getAnnotation(property.getElement(), XmlSchemaType.class);
                        QName schemaTypeQname = new QName(schemaType.namespace(), schemaType.name());
                        property.setSchemaType(schemaTypeQname);
                    }
                    if(helper.isAnnotationPresent(property.getElement(), XmlAttribute.class)) {
                        property.setIsAttribute(true);
                        property.setIsRequired(((XmlAttribute)helper.getAnnotation(property.getElement(), XmlAttribute.class)).required());
                    }                    
                    
                    if(helper.isAnnotationPresent(property.getElement(), XmlAnyAttribute.class)) {
                        if(hasAnyAttribteProperty) {
                            throw org.eclipse.persistence.exceptions.JAXBException.multipleAnyAttributeMapping(cls.getName());
                        }
                        if(!ptype.getName().equals("java.util.Map")) {
                            throw org.eclipse.persistence.exceptions.JAXBException.anyAttributeOnNonMap(property.getPropertyName());
                        }
                        property.setIsAttribute(true);
                        hasAnyAttribteProperty = true;
                    }

                    // Check for XmlElement annotation and set required (a.k.a. minOccurs) accordingly
                    // primitives are always required
                    if (ptype.isPrimitive()) {
                        property.setIsRequired(true);
                    } else if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
                        property.setIsRequired(((XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class)).required());
                    }                                                            
                                   
                    if (helper.isAnnotationPresent(property.getElement(), XmlValue.class)) {                    
                    	info.setXmlValueProperty(property);
                    	                    	
                    	JavaClass parent = cls.getSuperclass();                    	                    	
                        while (parent != null && !(parent.getQualifiedName().equals("java.lang.Object"))) {
                        	if(typeInfo.get(parent.getQualifiedName())!= null ){
                        		throw JAXBException.propertyOrFieldCannotBeXmlValue(nextField.getName());
                        	}
                        	parent = parent.getSuperclass();
                        }
                    }                    
                                        
                    // Figure out schema name and namesapce
                    property.setSchemaName(getQNameForProperty(Introspector.decapitalize(nextField.getName()), nextField, getNamespaceInfoForPackage(cls.getPackage())));
                    properties.add(property);
                }
            }else{
            	//If a property is marked transient ensure it doesn't exist in the propOrder            	
            	List<String> propOrderList = Arrays.asList(info.getPropOrder());
            	if(propOrderList.contains(nextField.getName())){
            		throw JAXBException.transientInProporder(nextField.getName());
            	}

            }
        }
        return properties;
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
        if (src == null || tgt == null) { return false; }
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
        if (src == null || tgtCanonicalName == null) { return false; }
        return src.getRawName().equals(tgtCanonicalName);
    }
    
    public ArrayList<Property> getPropertyPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList<Property> properties = new ArrayList<Property>();
        if (cls == null) { return properties; }

        // First collect all the getters
        ArrayList<JavaMethod> getMethods = new ArrayList<JavaMethod>();
        for (JavaMethod next : new ArrayList<JavaMethod>(cls.getDeclaredMethods())) {
            if ((next.getName().startsWith("get") && next.getName().length() > 3) || (next.getName().startsWith("is") && next.getName().length() > 2)) {
                int modifiers = next.getModifiers();
                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic)) {
                    getMethods.add(next);                    
                }
            }
        }
        // Next iterate over the getters and find their setter methods, add whichever one is
        // annotated to the properties list. If neither is, use the getter
        boolean hasAnyAttribteProperty = false;
        for (int i=0; i<getMethods.size(); i++) {
            JavaMethod getMethod = getMethods.get(i);
            String propertyName = "";
            if (getMethod.getName().startsWith("get")) {
                propertyName = getMethod.getName().substring(3);
            } else if(getMethod.getName().startsWith("is")) {
                propertyName = getMethod.getName().substring(2);
            }
            //make the first Character lowercase
            String setMethodName = "set" + propertyName;

            propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
            
            JavaClass[] paramTypes = { (JavaClass) getMethod.getReturnType() };
            JavaMethod setMethod = cls.getDeclaredMethod(setMethodName, paramTypes);
            JavaMethod propertyMethod = null;
            if (setMethod != null && !setMethod.getAnnotations().isEmpty()) {
                // use the set method if it exists and is annotated
                if (!helper.isAnnotationPresent(setMethod, XmlTransient.class)) {
                    propertyMethod = setMethod;   
                }
            } else {
                if (!helper.isAnnotationPresent(getMethod, XmlTransient.class)) {
                    propertyMethod = getMethod;
                }
            }
            Property property = null;
            if(helper.isAnnotationPresent(propertyMethod, XmlElements.class)) {
                property = new ChoiceProperty(helper);
            } else if (helper.isAnnotationPresent(propertyMethod, XmlAnyElement.class)) {
                property = new AnyProperty(helper);
            } else if (helper.isAnnotationPresent(propertyMethod, XmlElementRef.class) || helper.isAnnotationPresent(propertyMethod, XmlElementRefs.class)) {
                property = new ReferenceProperty(helper);
            } else {
                property = new Property(helper);
            }

            // Check for mixed context
            if (helper.isAnnotationPresent(propertyMethod, XmlMixed.class)) {
                info.setMixed(true);
            }
            
            property.setElement(propertyMethod);
            property.setSchemaName(getQNameForProperty(propertyName, propertyMethod, getNamespaceInfoForPackage(cls.getPackage())));
            property.setPropertyName(propertyName);
            
            JavaClass returnClass = (JavaClass) getMethod.getReturnType();
            if (!helper.isAnnotationPresent(returnClass, XmlTransient.class)) {
                property.setType(returnClass);
            } else {
                JavaClass parent = returnClass.getSuperclass();
                while (parent != null) {
                    if (parent.getName().equals("java.lang.Object")) {
                        property.setType(parent);
                        break;
                    }
                    if (!helper.isAnnotationPresent(parent, XmlTransient.class)) {
                        property.setType(parent);
                        break;
                    }
                    parent = parent.getSuperclass();
                }
            }
            property.setGenericType(helper.getGenericReturnType(getMethod));
            property.setGetMethodName(getMethod.getName());
            property.setSetMethodName(setMethodName);
            property.setMethodProperty(true);
            JavaClass ptype = property.getType();
            if (helper.isAnnotationPresent(property.getElement(), XmlJavaTypeAdapter.class)) {
                XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(property.getElement(), XmlJavaTypeAdapter.class);
                property.setAdapterClass(adapter.value());
            } else if (info.getAdaptersByClass().get(ptype) != null) {
                property.setAdapterClass(info.getAdaptersByClass().get(ptype));
            }

            if (property.hasAdapterClass()) {
                ptype = property.getValueType();
            }
            
            // Get schema-type info if specified and set it on the property for later use:
            if (helper.isAnnotationPresent(property.getElement(), XmlSchemaType.class)) {
                XmlSchemaType schemaType = (XmlSchemaType) helper.getAnnotation(property.getElement(), XmlSchemaType.class);
                QName schemaTypeQname = new QName(schemaType.namespace(), schemaType.name());
                property.setSchemaType(schemaTypeQname);
            }            
            if (helper.isAnnotationPresent(property.getElement(), XmlAttachmentRef.class) && areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER)) {
                property.setIsSwaAttachmentRef(true);
                property.setSchemaType(XMLConstants.SWA_REF_QNAME);
            } else if (areEquals(ptype, JAVAX_ACTIVATION_DATAHANDLER) || areEquals(ptype, byte[].class) || areEquals(ptype, Byte[].class) || areEquals(ptype, Image.class) || areEquals(ptype, Source.class) || areEquals(ptype, JAVAX_MAIL_INTERNET_MIMEMULTIPART)) {
                property.setIsMtomAttachment(true);
                property.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
            }
            if (helper.isAnnotationPresent(property.getElement(), XmlMimeType.class)) {
                property.setMimeType(((XmlMimeType) helper.getAnnotation(property.getElement(), XmlMimeType.class)).value());
            }
            if (helper.isAnnotationPresent(property.getElement(), XmlJavaTypeAdapter.class)) {
                XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotation(property.getElement(), XmlJavaTypeAdapter.class);
                property.setAdapterClass(adapter.value());
            } else if(info.getAdaptersByClass().get(ptype) != null) {
                property.setAdapterClass(info.getAdaptersByClass().get(ptype));
            }
            if(helper.isAnnotationPresent(property.getElement(), XmlAttribute.class)) {
                property.setIsAttribute(true);
                property.setIsRequired(((XmlAttribute)helper.getAnnotation(property.getElement(), XmlAttribute.class)).required());
            }
            
            if(helper.isAnnotationPresent(property.getElement(), XmlAnyAttribute.class)) {
                if(hasAnyAttribteProperty) {
                    throw org.eclipse.persistence.exceptions.JAXBException.multipleAnyAttributeMapping(cls.getName());
                }
                if(!ptype.getName().equals("java.util.Map")) {
                    throw org.eclipse.persistence.exceptions.JAXBException.anyAttributeOnNonMap(property.getPropertyName());
                }
                property.setIsAttribute(true);
                hasAnyAttribteProperty = true;
            }
            if(helper.isAnnotationPresent(property.getElement(), XmlElements.class)) {
                XmlElements xmlElements = (XmlElements)helper.getAnnotation(property.getElement(), XmlElements.class);
                XmlElement[] elements = xmlElements.value();
                ArrayList<Property> choiceProperties = new ArrayList<Property>(elements.length);
                for(int j = 0; j < elements.length; j++) {
                    XmlElement next = elements[j];
                    Property choiceProp = new Property();
                    String name = next.name();
                    String namespace = next.namespace();
                    QName qName = null;
                    if(name.equals("##defualt")) {
                        name = propertyName;
                    }
                    if (!namespace.equals("##default")) {
                        qName = new QName(namespace, name);
                    } else {
                        qName = new QName(name);
                    }
                    choiceProp.setPropertyName(property.getPropertyName());
                    choiceProp.setType(helper.getJavaClass(next.type()));
                    choiceProp.setSchemaName(qName);
                    choiceProp.setSchemaType(getSchemaTypeFor(helper.getJavaClass(next.type())));
                    choiceProp.setElement(property.getElement());
                    choiceProperties.add(choiceProp);
                }
                ((ChoiceProperty)property).setChoiceProperties(choiceProperties);
            }
            if(helper.isAnnotationPresent(property.getElement(), XmlAnyElement.class)) {
                XmlAnyElement anyElement = (XmlAnyElement)helper.getAnnotation(property.getElement(), XmlAnyElement.class);
                ((AnyProperty)property).setDomHandlerClass(anyElement.value());
                ((AnyProperty)property).setLax(anyElement.lax());
                
            }
            if(helper.isAnnotationPresent(property.getElement(), XmlElementRef.class) || helper.isAnnotationPresent(property.getElement(), XmlElementRefs.class)) { 
                XmlElementRef[] elementRefs;
                XmlElementRef ref = (XmlElementRef)helper.getAnnotation(property.getElement(), XmlElementRef.class);
                if(ref != null) {
                    elementRefs = new XmlElementRef[]{ref};
                } else {
                    XmlElementRefs refs = (XmlElementRefs)helper.getAnnotation(property.getElement(), XmlElementRefs.class);
                    elementRefs = refs.value();
                    info.setHasElementRefs(true);
                }
                for(XmlElementRef nextRef:elementRefs) {
                    JavaClass type = ptype;
                    String typeName = type.getQualifiedName();
                    property.setType(type);
                    if(isCollectionType(property)) {
                        if(type.hasActualTypeArguments()) {
                            type = (JavaClass)type.getActualTypeArguments().toArray()[0];
                            typeName = type.getQualifiedName();
                        }
                    }
                    if(nextRef.type() != XmlElementRef.DEFAULT.class) {
                        typeName = helper.getJavaClass(nextRef.type()).getQualifiedName();
                    }
                    ElementDeclaration referencedElement = this.xmlRootElements.get(typeName);
                    if(referencedElement != null) {
                        addReferencedElement((ReferenceProperty)property, referencedElement);
                    } else {
                        String name = nextRef.name();
                        String namespace = nextRef.namespace();
                        if(namespace.equals("##default")) {
                            namespace = "";
                        }
                        QName qname = new QName(namespace, name);
                        referencedElement = this.globalElements.get(qname);
                        if(referencedElement != null) {
                            addReferencedElement((ReferenceProperty)property, referencedElement);
                        } else {
                            throw org.eclipse.persistence.exceptions.JAXBException.invalidElementRef(property.getPropertyName(), cls.getName());
                        }
                    }
                }
            }           
            
            if (helper.isAnnotationPresent(property.getElement(), XmlValue.class)) {
            	info.setXmlValueProperty(property);
            	
            	JavaClass parent = cls.getSuperclass();
            	while (parent != null && !(parent.getQualifiedName().equals("java.lang.Object"))) {                
                	if(typeInfo.get(parent.getQualifiedName())!=null ){
                		throw JAXBException.propertyOrFieldCannotBeXmlValue(propertyName);
                	}
                	parent = parent.getSuperclass();
                }
            }

            if (!helper.isAnnotationPresent(property.getElement(), XmlTransient.class)) {
                properties.add(property);
            } else {          
                //If a property is marked transient ensure it doesn't exist in the propOrder            	
                List<String> propOrderList = Arrays.asList(info.getPropOrder());
                if(propOrderList.contains(propertyName)){
                	throw JAXBException.transientInProporder(propertyName);
                }
            }                     
               
            // Check for XmlElement annotation and set required (a.k.a. minOccurs) accordingly
            // primitives are always required
            if (ptype.isPrimitive()) {
                property.setIsRequired(true);
            } else if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
                property.setIsRequired(((XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class)).required());
            }                    
        }
        return properties;
    }
    
    public ArrayList getPublicMemberPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<Property> fieldProperties = getFieldPropertiesForClass(cls, info, false);
        ArrayList<Property> methodProperties = getPropertyPropertiesForClass(cls, info, false);
        
        //filter out non-public properties that aren't annotated
        ArrayList<Property> publicFieldProperties = new ArrayList<Property>();
        ArrayList<Property> publicMethodProperties = new ArrayList<Property>();
        
        for(Property next:fieldProperties) {
            if(Modifier.isPublic(((JavaField)next.getElement()).getModifiers())) {
                publicFieldProperties.add(next);
            } else {
                if(hasJAXBAnnotations(next.getElement())) {
                    publicFieldProperties.add(next);
                }
            }
        }
        
        for(Property next:methodProperties) {
            if(next.getElement() != null) {
                if(Modifier.isPublic(((JavaMethod)next.getElement()).getModifiers())) {
                    publicMethodProperties.add(next);
                } else {
                    if(hasJAXBAnnotations(next.getElement())) {
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
                Property next = (Property)publicMethodProperties.get(i);
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
        while(propIter.hasNext()) {
            Property next = (Property)propIter.next();
            propMap.put(next.getPropertyName(), next);
        }
        return propMap;
    }
    
    public ArrayList getNoAccessTypePropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList list = new ArrayList();
        if (cls == null) { return list; }
        ArrayList fieldProperties = getFieldPropertiesForClass(cls, info, false);
        ArrayList methodProperties = getPropertyPropertiesForClass(cls, info, false);
        
        // Iterate over the field and method properties. If ANYTHING contains an annotation and
        // doesn't appear in the other list, add it to the final list
        for (int i=0; i<fieldProperties.size(); i++) {
            Property next = (Property) fieldProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (hasJAXBAnnotations(elem)) {
                list.add(next);
            }
        }
        for (int i=0; i<methodProperties.size(); i++) {
            Property next = (Property) methodProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (hasJAXBAnnotations(elem)) {
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
        
        JavaClass jClass = helper.getJavaClass(javaType);
        if (jClass == null) { return; }
        
        QName typeQName = new QName(type.namespace(), schemaTypeName);
        this.userDefinedSchemaTypes.put(jClass.getQualifiedName(), typeQName);
    }
    
    public void addEnumTypeInfo(JavaClass javaClass, EnumTypeInfo info) {
        if (javaClass == null) { return; }
        
        info.setClassName(javaClass.getQualifiedName());
        Class restrictionClass = String.class;

        if (helper.isAnnotationPresent(javaClass, XmlEnum.class)) {
            XmlEnum xmlEnum = (XmlEnum) helper.getAnnotation(javaClass, XmlEnum.class);
            restrictionClass = xmlEnum.value();
        }
        QName restrictionBase = getSchemaTypeFor(helper.getJavaClass(restrictionClass));
        info.setRestrictionBase(restrictionBase);

        for (Iterator<JavaField> fieldIt = javaClass.getDeclaredFields().iterator(); fieldIt.hasNext(); ) {
            JavaField field = fieldIt.next();
            if (field.isEnumConstant()) {
                String fieldValue = field.getName();
                if (helper.isAnnotationPresent(field, XmlEnumValue.class)) {
                    XmlEnumValue xmlEnumValue = (XmlEnumValue) helper.getAnnotation(field, XmlEnumValue.class);
                    fieldValue = xmlEnumValue.value();
                }                
                info.addObjectToFieldValuePair(field.getName(), fieldValue);
            }
        }
        typeInfoClasses.add(javaClass);
        typeInfo.put(javaClass.getQualifiedName(), info);
    }      
    
    public String getSchemaTypeNameForClassName(String className) {
        String typeName = "";
        if (className.indexOf('$') != -1) {
            typeName = Introspector.decapitalize(className.substring(className.lastIndexOf('$') + 1));
        } else {
            typeName = Introspector.decapitalize(className.substring(className.lastIndexOf('.') + 1));
        }
        //now capitalize any characters that occur after a "break"
        boolean inBreak = false;
        StringBuffer toReturn = new StringBuffer(typeName.length());
        for(int i = 0; i < typeName.length(); i++) {
            char next = typeName.charAt(i);
            if(Character.isDigit(next)) {
                if(!inBreak) {
                    inBreak = true;
                }
                toReturn.append(next);
            } else {
                if(inBreak) {
                    toReturn.append(Character.toUpperCase(next));
                } else {
                    toReturn.append(next);
                }
            }
        }
        return toReturn.toString();
    }
    
    public QName getSchemaTypeFor(JavaClass javaClass) {
        if (javaClass == null) { return null; }
        
        // check user defined types first
        QName schemaType = (QName)userDefinedSchemaTypes.get(javaClass.getQualifiedName());
        if (schemaType == null) {
            schemaType = (QName) helper.getXMLToJavaTypeMap().get(javaClass.getRawName());
        }
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
            }
            info.setNamespace(packageNamespace);
            XmlNs[] xmlns = xmlSchema.xmlns();
            for (int i = 0; i < xmlns.length; i++) {
                XmlNs next = xmlns[i];
                info.getNamespaceResolver().put(next.prefix(), next.namespaceURI());
            }
            info.setAttributeFormQualified(xmlSchema.attributeFormDefault() == XmlNsForm.QUALIFIED);
            info.setElementFormQualified(xmlSchema.elementFormDefault() == XmlNsForm.QUALIFIED);
            
            //reflectively load XmlSchema class to avoid dependency            
            try {
                Method locationMethod = PrivilegedAccessHelper.getDeclaredMethod(XmlSchema.class, "location", new Class[]{});
                String location = (String)PrivilegedAccessHelper.invokeMethod(locationMethod, xmlSchema, new Object[]{});
                
                if(location != null){
    	            if(location.equals("##generate")){
    	            	location = null;
    	            }else if(location.equals("")){
    	            	location = null;
    	            }
                }    		        		    	   
                info.setLocation(location);
            } catch(Exception ex) {
            } 
                                               
        }
        return info;
    }
    
    public HashMap<String, TypeInfo> getTypeInfo() {
        return typeInfo;
    }

    public ArrayList<JavaClass> getTypeInfoClasses() {
        return typeInfoClasses;
    }
    
    public HashMap getUserDefinedSchemaTypes() {
        return userDefinedSchemaTypes;
    }
    
    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }
    
    public String getSchemaTypeNameFor(JavaClass javaClass, XmlType xmlType) {
        String typeName = "";
        if (javaClass == null) { return typeName; }
        
        if (helper.isAnnotationPresent(javaClass, XmlType.class)) {
            //Figure out what kind of type we have
            //figure out type name
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
    
    public QName getQNameForProperty(String defaultName, JavaHasAnnotations element, NamespaceInfo namespaceInfo) {
        String name = "##default";
        String namespace = "##default";
        QName qName = null;
        if (helper.isAnnotationPresent(element, XmlAttribute.class)) {
            XmlAttribute xmlAttribute = (XmlAttribute) helper.getAnnotation(element, XmlAttribute.class);
            name = xmlAttribute.name();
            namespace = xmlAttribute.namespace();
            
            if(name.equals("##default")) {
                name = defaultName;
            }           
            
            if (!namespace.equals("##default")) {
                qName = new QName(namespace, name);
            } else {        	
            	if (namespaceInfo.isAttributeFormQualified()) {                
                    qName = new QName(namespaceInfo.getNamespace(), name);
                }else{
                	qName = new QName(name);
                }
            }
        }else{
        	if (helper.isAnnotationPresent(element, XmlElement.class)){
        		XmlElement xmlElement = (XmlElement) helper.getAnnotation(element, XmlElement.class);
                name = xmlElement.name();
                namespace = xmlElement.namespace();	
        	}                    
       
            if(name.equals("##default")) {
                name = defaultName;
            }
            
            if (!namespace.equals("##default")) {
                qName = new QName(namespace, name);
            } else {        	
            	if (namespaceInfo.isElementFormQualified()) {                
                    qName = new QName(namespaceInfo.getNamespace(), name);
                }else{
                	qName = new QName(name);
                }
            }
        }
       
      
        return qName;
        
    }
    
    public HashMap<String, NamespaceInfo> getPackageToNamespaceMappings() {
        return packageToNamespaceMappings;
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
                packageNamespace.setAccessType(xmlAccessorType.value());
            }
            
            packageToNamespaceMappings.put(pack.getQualifiedName(), packageNamespace);
        }
        return packageNamespace;
    }
        
    private void checkForCallbackMethods() {
        for (JavaClass next : typeInfoClasses) {
            if (next == null) { continue; }
            
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
                if(this.marshalCallbacks == null) {
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
        Collection methods = objectFactoryClass.getMethods();
        Iterator methodsIter = methods.iterator();
        NamespaceInfo namespaceInfo = getNamespaceInfoForPackage(objectFactoryClass.getPackage());
        while(methodsIter.hasNext()) {
            JavaMethod next = (JavaMethod)methodsIter.next();
            if(next.getName().startsWith("create")) {
                JavaClass type = next.getReturnType();
                if(type.getName().equals("javax.xml.bind.JAXBElement")) {
                    type = (JavaClass)next.getReturnType().getActualTypeArguments().toArray()[0];
                } else {
                    this.factoryMethods.put(next.getReturnType().getRawName(), next);
                }
                if(helper.isAnnotationPresent(next, XmlElementDecl.class)) {
                    XmlElementDecl elementDecl = (XmlElementDecl)helper.getAnnotation(next, XmlElementDecl.class);
                    String url = elementDecl.namespace();
                    if("##default".equals(url)) {
                        url = namespaceInfo.getNamespace();
                    }
                    String localName = elementDecl.name();
                    QName qname = new QName(url, localName);
                    
                    if(this.globalElements == null) {
                        globalElements = new HashMap<QName, ElementDeclaration>();
                    }

                    boolean isList = false;
                    if("java.util.List".equals(type.getName())){
                        isList = true;
                        if(type.hasActualTypeArguments()){
                            type = (JavaClass)type.getActualTypeArguments().toArray()[0];                               
                        }
                    }
                        
                    ElementDeclaration declaration = new ElementDeclaration(qname, type, type.getQualifiedName(), isList, elementDecl.scope());
                    if(!elementDecl.substitutionHeadName().equals("")) {
                        String subHeadLocal = elementDecl.substitutionHeadName();
                        String subHeadNamespace = elementDecl.substitutionHeadNamespace();
                        if(subHeadNamespace.equals("##default")) {
                            subHeadNamespace = namespaceInfo.getNamespace();
                        }
                        declaration.setSubstitutionHead(new QName(subHeadNamespace, subHeadLocal));
                    }
                        
                    if (helper.isAnnotationPresent(next, XmlJavaTypeAdapter.class)) {
                        XmlJavaTypeAdapter typeAdapter = (XmlJavaTypeAdapter) helper.getAnnotation(next, XmlJavaTypeAdapter.class);
                        Class typeAdapterClass = typeAdapter.value();
                        declaration.setJavaTypeAdapterClass(typeAdapterClass);
                            
                        Method[] tacMethods = typeAdapterClass.getMethods();
                        Class declJavaType = null;
                            
                        for (int i = 0; i < tacMethods.length; i++) {
                            Method method = tacMethods[i];
                            if (method.getName().equals("marshal")) {
                                declJavaType = method.getReturnType();
                                break;
                            }
                        }                             
                            
                        declaration.setJavaType(helper.getJavaClass(declJavaType));
                        declaration.setAdaptedJavaType(type);
                    }                        
                        
                    globalElements.put(qname, declaration);
                }
                if(!helper.isBuiltInJavaType(type) && !classes.contains(type)) {
                    classes.add(type);
                }
            }
        }
        if(classes.size() > 0) {
            return classes.toArray(new JavaClass[classes.size()]);
        } else {
            return new JavaClass[0];
        }
    }
    
    public HashMap<QName, ElementDeclaration> getGlobalElements() {
        return globalElements;
    }
    
    public void updateGlobalElements(ArrayList<JavaClass> classesToProcess) {
        //Once all the global element declarations have been created, make sure that any ones that have
        //a substitution head set are added to the list of substitutable elements on the declaration for that
        //head.
        
        //Look for XmlRootElement declarations
        for(JavaClass javaClass:classesToProcess) {
            if (helper.isAnnotationPresent(javaClass, XmlRootElement.class)) {
                XmlRootElement rootElemAnnotation = (XmlRootElement) helper.getAnnotation(javaClass, XmlRootElement.class);
                NamespaceInfo namespaceInfo;
                JavaPackage pack = javaClass.getPackage();
    	        namespaceInfo = getNamespaceInfoForPackage(pack);

                String elementName = rootElemAnnotation.name();
                if (elementName.equals("##default") || elementName.equals("")) {
                    if (javaClass.getName().indexOf("$") != -1) {
                        elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('$') + 1));
                    } else {
                        elementName = Introspector.decapitalize(javaClass.getName().substring(javaClass.getName().lastIndexOf('.') + 1));                    
                    }
                    // TODO - remove this TCK hack...
                    if (elementName.length() >= 3) {
                        int idx = elementName.length()-1;
                        char ch = elementName.charAt(idx-1);
                        if (Character.isDigit(ch)) {
                            char lastCh = Character.toUpperCase(elementName.charAt(idx));
                            elementName = elementName.substring(0, idx) + lastCh;
                        }
                    }
                }
                String rootNamespace = rootElemAnnotation.namespace();
                QName rootElemName = null;
                if (rootNamespace.equals("##default")) {
                    if(namespaceInfo == null) {
                        rootElemName = new QName(elementName);
                    } else {
                        rootElemName = new QName(namespaceInfo.getNamespace(), elementName);
                    }
                } else {
                    rootElemName = new QName(rootNamespace, elementName);
                }
                ElementDeclaration declaration = new ElementDeclaration(rootElemName, javaClass, javaClass.getQualifiedName(), false);
                declaration.setIsXmlRootElement(true);
                if(this.globalElements == null) {
                    globalElements = new HashMap<QName, ElementDeclaration>();
                }
                this.globalElements.put(rootElemName, declaration);
                this.xmlRootElements.put(javaClass.getQualifiedName(), declaration);
            }
        }
        
        if(this.globalElements == null) {
            return;
        }
        
        Iterator<QName> elementQnames = this.globalElements.keySet().iterator();
        while(elementQnames.hasNext()) {
            QName next = elementQnames.next();
            ElementDeclaration nextDeclaration = this.globalElements.get(next);
            if(nextDeclaration.getSubstitutionHead() != null) {
                ElementDeclaration rootDeclaration = this.globalElements.get(nextDeclaration.getSubstitutionHead());
                rootDeclaration.addSubstitutableElement(nextDeclaration);
            }
        }
    }
    
    private void addReferencedElement(ReferenceProperty property, ElementDeclaration referencedElement) {
        property.addReferencedElement(referencedElement);
        if(referencedElement.getSubstitutableElements() != null && referencedElement.getSubstitutableElements().size() > 0) {
            for(ElementDeclaration substitutable:referencedElement.getSubstitutableElements()) {
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
                || helper.isAnnotationPresent(elem, XmlSchemaType.class)) {
           
                return true;
        }
        return false;
        
    }
}
