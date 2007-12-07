/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;

import java.awt.Image;
import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
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
    private HashMap<String, UnmarshalCallback> unmarshalCallbacks;
    private NamespaceResolver namespaceResolver;
    private Helper helper;

    public AnnotationsProcessor(Helper helper) {
        this.helper = helper;
    }
    
    public void processClassesAndProperties(JavaClass[] classes) {
        typeInfoClasses = new ArrayList<JavaClass>();
        typeInfo = new HashMap<String, TypeInfo>();
        userDefinedSchemaTypes = new HashMap<String, QName>();
        packageToNamespaceMappings = new HashMap<String, NamespaceInfo>(); 
        this.namespaceResolver = new NamespaceResolver();
        
        for (JavaClass javaClass : classes) {
            if (javaClass == null) { continue; }

            createTypeInfoFor(javaClass);
            // handle inner classes
            for (Iterator<JavaClass> jClassIt = javaClass.getDeclaredClasses().iterator(); jClassIt.hasNext(); ) {
                JavaClass innerClass = jClassIt.next();
                if (shouldGenerateTypeInfo(innerClass)) {
                    createTypeInfoFor(innerClass);
                }
            }
            JavaClass superClass = (JavaClass) javaClass.getSuperclass();
            if (shouldGenerateTypeInfo(superClass)) {
                createTypeInfoFor(superClass);
            }
        }
        checkForCallbackMethods();
    }    
    
    public SchemaTypeInfo addClass(JavaClass javaClass) {
        if (javaClass == null) { return null; }
        
        if (typeInfo == null) {
            // this is the first class. Initialize all the properties
            this.typeInfoClasses = new ArrayList<JavaClass>();
            this.typeInfo = new HashMap<String, TypeInfo>();
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
            }
            String rootNamespace = rootElemAnnotation.namespace();
            if (rootNamespace.equals("##default")) {
                schemaInfo.getGlobalElementDeclarations().add(new QName(namespaceInfo.getNamespace(), rootNamespace));
            } else {
                schemaInfo.getGlobalElementDeclarations().add(new QName(elementName, rootNamespace));
            }
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

        String[] propOrder = new String[0];
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
        if (helper.isAnnotationPresent(javaClass, XmlAccessorType.class)) {
            XmlAccessorType accessorType = (XmlAccessorType) helper.getAnnotation(javaClass, XmlAccessorType.class);
            info.setAccessType(accessorType.value());
        } else {
            info.setAccessType(packageNamespace.getAccessType());
        }
        
        info.setProperties(getPropertiesForClass(javaClass, info));
        if (helper.isAnnotationPresent(javaClass, XmlAccessorOrder.class)) {
            XmlAccessorOrder order = (XmlAccessorOrder) helper.getAnnotation(javaClass, XmlAccessorOrder.class);
            info.orderProperties(order.value());
        }
        
        JavaClass superClass = (JavaClass) javaClass.getSuperclass();
        if (shouldGenerateTypeInfo(superClass)) {
            createTypeInfoFor(superClass);
        }
        
        ArrayList<TypeProperty> properties = info.getPropertyList();
        for (TypeProperty property : properties) {
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
        return info;
    }

    public boolean shouldGenerateTypeInfo(JavaClass javaClass) {
        if (javaClass == null || javaClass.isPrimitive() || javaClass.isAnnotation() || javaClass.isInterface() || javaClass.isArray()) {
            return false;
        }
        // don't generate for anything in the java.* packages.
        if (javaClass.getPackageName().startsWith("java.") || javaClass.getPackageName().startsWith("javax.")) {
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
    
    public ArrayList<TypeProperty> getPropertiesForClass(JavaClass cls, TypeInfo info) {
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
    
    public ArrayList<TypeProperty> getFieldPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList properties = new ArrayList();
        if (cls == null) { return properties; }
        
        for (Iterator<JavaField> fieldIt = cls.getDeclaredFields().iterator(); fieldIt.hasNext(); ) {
            JavaField nextField = fieldIt.next();
            if (!helper.isAnnotationPresent(nextField, XmlTransient.class)) {
                if ((Modifier.isPublic(nextField.getModifiers()) && onlyPublic) || !onlyPublic) {
                    TypeProperty property = new TypeProperty(helper);
                    property.setElement((JavaHasAnnotations) nextField);
                    
                    JavaClass ptype = (JavaClass) nextField.getResolvedType();
                    property.setType(ptype);
                    
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
                        QName schemaTypeQname = new QName(XMLConstants.SCHEMA_INSTANCE_URL, schemaType.name());
                        property.setSchemaType(schemaTypeQname);
                    }
                    if(helper.isAnnotationPresent(property.getElement(), XmlAttribute.class) || helper.isAnnotationPresent(property.getElement(), XmlAnyAttribute.class)) {
                        property.setIsAttribute(true);
                    }                    
                    // Check for XmlElement annotation and set required (a.k.a. minOccurs) accordingly
                    if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
                        property.setIsRequired(((XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class)).required());
                    }                    
                    
                    // Figure out schema name and namesapce
                    property.setSchemaName(getQNameForProperty(Introspector.decapitalize(nextField.getName()), nextField));
                    properties.add(property);
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
    
    public ArrayList<TypeProperty> getPropertyPropertiesForClass(JavaClass cls, TypeInfo info, boolean onlyPublic) {
        ArrayList properties = new ArrayList();
        if (cls == null) { return properties; }

        // First collect all the getters
        ArrayList<JavaMethod> getMethods = new ArrayList<JavaMethod>();
        for (JavaMethod next : new ArrayList<JavaMethod>(cls.getDeclaredMethods())) {
            if (next.getName().startsWith("get") || ((areEquals((JavaClass) next.getReturnType(), Boolean.class) || areEquals((JavaClass) next.getReturnType(), boolean.class)) && next.getName().startsWith("is"))) {
                if ((onlyPublic && Modifier.isPublic(next.getModifiers())) || !onlyPublic) {
                    getMethods.add(next);                    
                }
            }
        }
        // Next iterate over the getters and find their setter methods, add whichever one is
        // annotated to the properties list. If neither is, use the getter
        for (int i=0; i<getMethods.size(); i++) {
            JavaMethod getMethod = getMethods.get(i);
            String propertyName = "";
            if (getMethod.getName().startsWith("get")) {
                propertyName = getMethod.getName().substring(3);
            } else if(getMethod.getName().startsWith("is")) {
                propertyName = getMethod.getName().substring(2);
            }
            //make the first Character lowercase
            propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);

            TypeProperty property = new TypeProperty(helper);
            property.setPropertyName(propertyName);
            property.setType((JavaClass) getMethod.getReturnType());
            property.setGenericType(helper.getGenericReturnType(getMethod));
            property.setGetMethodName(getMethod.getName());
            property.setMethodProperty(true);
            
            String setMethodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            property.setSetMethodName(setMethodName);
            JavaClass[] paramTypes = { (JavaClass) getMethod.getReturnType() };
            JavaMethod setMethod = cls.getMethod(setMethodName, paramTypes);
            if (setMethod != null && !setMethod.getAnnotations().isEmpty()) {
                // use the set method if it exists and is annotated
                if (!helper.isAnnotationPresent(setMethod, XmlTransient.class)) {
                    property.setElement(setMethod);   
                    property.setSchemaName(getQNameForProperty(propertyName, setMethod));
                }
            } else {
                if (!helper.isAnnotationPresent(getMethod, XmlTransient.class)) {
                    property.setElement(getMethod);
                    property.setSchemaName(getQNameForProperty(propertyName, getMethod));
                }
            }
            
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
                QName schemaTypeQname = new QName(XMLConstants.SCHEMA_INSTANCE_URL, schemaType.name());
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
                XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter) helper.getAnnotaion(property.getElement(), XmlJavaTypeAdapter.class);
                property.setAdapterClass(adapter.value());
            } else if(info.getAdaptersByClass().get(ptype) != null) {
                property.setAdapterClass(info.getAdaptersByClass().get(ptype));
            }
            if(helper.isAnnotationPresent(property.getElement(), XmlAttribute.class) || helper.isAnnotationPresent(property.getElement(), XmlAnyAttribute.class)) {
                property.setIsAttribute(true);
            }
            if (!helper.isAnnotationPresent(property.getElement(), XmlTransient.class)) {
                properties.add(property);
            }
            // Check for XmlElement annotation and set required (a.k.a. minOccurs) accordingly
            if (helper.isAnnotationPresent(property.getElement(), XmlElement.class)) {
                property.setIsRequired(((XmlElement) helper.getAnnotation(property.getElement(), XmlElement.class)).required());
            }                    
        }
        return properties;
    }
    
    public ArrayList getPublicMemberPropertiesForClass(JavaClass cls, TypeInfo info) {
        ArrayList<TypeProperty> publicFieldProperties = getFieldPropertiesForClass(cls, info, true);
        ArrayList<TypeProperty> publicMethodProperties = getPropertyPropertiesForClass(cls, info, true);
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
                TypeProperty next = (TypeProperty)publicMethodProperties.get(i);
                if (fieldPropertyMap.get(next.getPropertyName()) == null) {
                    publicFieldProperties.add(next);
                }
            }
            return publicFieldProperties;
        }
    }
    
    public HashMap getPropertyMapFromArrayList(ArrayList<TypeProperty> props) {
        HashMap propMap = new HashMap(props.size());
        
        Iterator propIter = props.iterator();
        while(propIter.hasNext()) {
            TypeProperty next = (TypeProperty)propIter.next();
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
            TypeProperty next = (TypeProperty) fieldProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (helper.isAnnotationPresent(elem, XmlElement.class) 
                    || helper.isAnnotationPresent(elem, XmlAttribute.class)  
                    || helper.isAnnotationPresent(elem, XmlAnyElement.class) 
                    || helper.isAnnotationPresent(elem, XmlAnyAttribute.class) 
                    || helper.isAnnotationPresent(elem, XmlValue.class)) {
                list.add(next);
            }
        }
        for (int i=0; i<methodProperties.size(); i++) {
            TypeProperty next = (TypeProperty) methodProperties.get(i);
            JavaHasAnnotations elem = next.getElement();
            if (helper.isAnnotationPresent(elem, XmlElement.class) 
                    || helper.isAnnotationPresent(elem, XmlAttribute.class)  
                    || helper.isAnnotationPresent(elem, XmlAnyElement.class) 
                    || helper.isAnnotationPresent(elem, XmlAnyAttribute.class) 
                    || helper.isAnnotationPresent(elem, XmlValue.class)) {
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
        
        QName typeQName = new QName(XMLConstants.SCHEMA_INSTANCE_URL, schemaTypeName);
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
                info.getObjectValuesToFieldValues().put(field.getName(), fieldValue);
            }
        }
        typeInfoClasses.add(javaClass);
        typeInfo.put(javaClass.getQualifiedName(), info);
    }
    
    public ArrayList getEnumerationFacetsFor(EnumTypeInfo info) {
        Collection valuesCollection = info.getObjectValuesToFieldValues().values();
        return new ArrayList(valuesCollection);
    }
    
    public String getSchemaTypeNameForClassName(String className) {
        String typeName = "";
        if (className.indexOf('$') != -1) {
            typeName = className.substring(className.lastIndexOf('$') + 1).toLowerCase();
        } else {
            typeName = className.substring(className.lastIndexOf('.') + 1).toLowerCase();
        }
        return typeName;
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

    public boolean isCollectionType(TypeProperty field) {
        JavaClass type = field.getType();
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
    
    public QName getQNameForProperty(String defaultName, JavaHasAnnotations element) {
        String name = "##default";
        String namespace = "##default";
        if (helper.isAnnotationPresent(element, XmlAttribute.class)) {
            XmlAttribute xmlAttribute = (XmlAttribute) helper.getAnnotation(element, XmlAttribute.class);
            name = xmlAttribute.name();
            namespace = xmlAttribute.namespace();
        } else if (helper.isAnnotationPresent(element, XmlElement.class)) {
            XmlElement xmlElement = (XmlElement) helper.getAnnotation(element, XmlElement.class);
            name = xmlElement.name();
            namespace = xmlElement.namespace();
        }
        if(name.equals("##default")) {
            name = defaultName;
        }
        QName qName = null;
        if (!namespace.equals("##default")) {
            qName = new QName(namespace, name);
        } else {
            qName = new QName(name);
        }
        return qName;
        
    }
    
    public HashMap<String, NamespaceInfo> getPackageToNamespaceMappings() {
        return packageToNamespaceMappings;
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
}
