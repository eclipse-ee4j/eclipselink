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
 *     Denise Smith -  January, 2010 - 2.0.1 
 ******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.internal.jaxb.AccessorFactoryWrapper;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes;

/**
 * Helper class for code that needs to be shared between AnnotationsProcessor,
 * MappingsGenerator, SchemaGenerator
 */
public class CompilerHelper {

    private static JAXBContext xmlBindingsModelContext;
    private static final String METADATA_MODEL_PACKAGE = "org.eclipse.persistence.jaxb.xmlmodel";

    private static final String XML_ACCESSOR_FACTORY_ANNOTATION_NAME = "com.sun.xml.bind.XmlAccessorFactory";
    private static final String INTERNAL_ACCESSOR_FACTORY_ANNOTATION_NAME = "com.sun.xml.internal.bind.XmlAccessorFactory";
    public static final String XML_LOCATION_ANNOTATION_NAME = "com.sun.xml.bind.annotation.XmlLocation";
    public static final String INTERNAL_XML_LOCATION_ANNOTATION_NAME = "com.sun.xml.internal.bind.annotation.XmlLocation";
    public static Class ACCESSOR_FACTORY_ANNOTATION_CLASS = null;
    public static Method ACCESSOR_FACTORY_VALUE_METHOD = null;
    public static Class INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS = null;
    public static Method INTERNAL_ACCESSOR_FACTORY_VALUE_METHOD = null;
    public static Class XML_LOCATION_ANNOTATION_CLASS = null;
    public static Class INTERNAL_XML_LOCATION_ANNOTATION_CLASS = null;
    
    static {
        try {
            ACCESSOR_FACTORY_ANNOTATION_CLASS = PrivilegedAccessHelper.getClassForName(XML_ACCESSOR_FACTORY_ANNOTATION_NAME, true, CompilerHelper.class.getClassLoader());
            ACCESSOR_FACTORY_VALUE_METHOD = PrivilegedAccessHelper.getDeclaredMethod(ACCESSOR_FACTORY_ANNOTATION_CLASS, "value", new Class[]{});            
        } catch (Exception ex) {        
        }
        
        try {
            XML_LOCATION_ANNOTATION_CLASS = PrivilegedAccessHelper.getClassForName(XML_LOCATION_ANNOTATION_NAME, true, CompilerHelper.class.getClassLoader());
        } catch (Exception ex) {
        }
        
        try{
            INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS = PrivilegedAccessHelper.getClassForName(INTERNAL_ACCESSOR_FACTORY_ANNOTATION_NAME);
            INTERNAL_ACCESSOR_FACTORY_VALUE_METHOD = PrivilegedAccessHelper.getDeclaredMethod(INTERNAL_ACCESSOR_FACTORY_ANNOTATION_CLASS, "value", new Class[]{});
        } catch (Exception ex) {         
        }
        
        try{
            INTERNAL_XML_LOCATION_ANNOTATION_CLASS = PrivilegedAccessHelper.getClassForName(INTERNAL_XML_LOCATION_ANNOTATION_NAME);
        }catch (Exception ex) {         
        }
        
        
    }
   
    /**
     * If 2 TypeMappingInfo objects would generate the same generated class (and
     * therefore complex type) then return the existing class otherwise return
     * null.
     */
    static Class getExisitingGeneratedClass(TypeMappingInfo tmi, Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedClasses, Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses, ClassLoader loader) {

        Iterator<Map.Entry<TypeMappingInfo, Class>> iter = typeMappingInfoToGeneratedClasses.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<TypeMappingInfo, Class> next = iter.next();
            TypeMappingInfo nextTMI = next.getKey();
            if (CompilerHelper.generatesSameComplexType(tmi, nextTMI, loader)) {
                return next.getValue();
            }
        }
        return null;
    }

    /**
     * Return true if the two TypeMappingInfoObjects should generate the same
     * complex type in the XSD
     */
    private static boolean generatesSameComplexType(TypeMappingInfo tmi1, TypeMappingInfo tmi2, ClassLoader loader) {

        org.eclipse.persistence.jaxb.xmlmodel.XmlElement element1 = null;
        org.eclipse.persistence.jaxb.xmlmodel.XmlElement element2 = null;

        if (tmi1.getXmlElement() != null) {
            element1 = (org.eclipse.persistence.jaxb.xmlmodel.XmlElement) getXmlElement(tmi1.getXmlElement(), loader);
        }

        if (tmi2.getXmlElement() != null) {
            element2 = (org.eclipse.persistence.jaxb.xmlmodel.XmlElement) getXmlElement(tmi2.getXmlElement(), loader);
        }

        Type actualType1 = getActualType(tmi1, element1);
        Type actualType2 = getActualType(tmi2, element2);

        if (!areTypesEqual(actualType1, actualType2)) {
            return false;
        }

        boolean isXmlList1 = isXmlList(tmi1, element1);
        boolean isXmlList2 = isXmlList(tmi2, element2);

        if (isXmlList1) {
            if (!isXmlList2) {
                return false;
            }
        } else if (isXmlList2) {
            return false;
        }

        return true;
    }

    /**
     * Return if this TypeMappingInfo has an XmlList annotation or is specified
     * to be an xmlList in an XMLElement override
     */
    private static boolean isXmlList(TypeMappingInfo tmi, org.eclipse.persistence.jaxb.xmlmodel.XmlElement element) {
        if (element != null && element.isXmlList()) {
            return true;
        }

        if (tmi.getAnnotations() != null) {
            for (int i = 0; i < tmi.getAnnotations().length; i++) {
                java.lang.annotation.Annotation nextAnnotation = tmi.getAnnotations()[i];
                if (nextAnnotation != null && nextAnnotation instanceof XmlList) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return true if the Types are equal. Accounts for Classes and
     * Parameterized types or any combintation of the two.
     */
    private static boolean areTypesEqual(java.lang.reflect.Type type, java.lang.reflect.Type type2) {
        // handle null
        if (type == null) {
        	return type2 == null;       
        } else if (type instanceof Class) {
            if (type2 instanceof ParameterizedType) {

                java.lang.reflect.Type rawType = ((ParameterizedType) type2).getRawType();
                if (!areTypesEqual(type, rawType)) {
                    return false;
                }
                                
                java.lang.reflect.Type[] args = ((ParameterizedType) type2).getActualTypeArguments();
                for (int i = 0; i < args.length; i++) {
                	Type argType = getActualArgumentType(args[i]);
                    if (!areTypesEqual(Object.class, argType)) {
                        return false;
                    }
                }
                return true;
            } else if (type2 instanceof Class) {
            	return type.equals(type2);                
            } else {
            	return false;
            }
        } else if (type instanceof ParameterizedType) {
            if (type2 instanceof Class) {

                java.lang.reflect.Type rawType = ((ParameterizedType) type).getRawType();
                if (!areTypesEqual(type2, rawType)) {
                    return false;
                }

                java.lang.reflect.Type[] args = ((ParameterizedType) type).getActualTypeArguments();
                for (int i = 0; i < args.length; i++) {
                	Type argType = getActualArgumentType(args[i]);
                    if (!areTypesEqual(Object.class, argType)) {
                        return false;
                    }
                }
                return true;
            } else if (type2 instanceof ParameterizedType) {
                // compare raw type
                if (!areTypesEqual(((ParameterizedType) type).getRawType(),
                        ((ParameterizedType) type2).getRawType())) {
                    return false;
                }

                java.lang.reflect.Type[] ta1 = ((ParameterizedType) type).getActualTypeArguments();
                java.lang.reflect.Type[] ta2 = ((ParameterizedType) type2).getActualTypeArguments();
                // check array length
                if (ta1.length != ta2.length) {
                    return false;
                }
                for (int i = 0; i < ta1.length; i++) {                
                	Type componentType1 = getActualArgumentType(ta1[i]);
                	Type componentType2 = getActualArgumentType(ta2[i]);
                    if (!areTypesEqual(componentType1, componentType2)) {
                        return false;
                    }
                }
                return true;
            } else {                      
                return false;
            }
        } 
        return false;
    }

    private static Type getActualArgumentType(Type argument){
    	if(argument instanceof WildcardType){
    		Type[] upperBounds = ((WildcardType)argument).getUpperBounds();
        	if(upperBounds != null && upperBounds.length >0){
        		return upperBounds[0];
        	}else{
        		return Object.class;
        	}
    	}else if (argument instanceof GenericArrayType){
    		return ((GenericArrayType)argument).getGenericComponentType();
    	}
    	return argument;
    }
    
    /**
     * Convenience method for creating an XmlElement object based on a given
     * Element. The method will load the eclipselink metadata model and
     * unmarshal the Element. This assumes that the Element represents an
     * xml-element to be unmarshalled.
     * 
     * @param xmlElementNode
     * @param classLoader
     * @return
     */
    static org.eclipse.persistence.jaxb.xmlmodel.XmlElement getXmlElement(org.w3c.dom.Element xmlElementNode, ClassLoader classLoader) {
        try {
            Unmarshaller unmarshaller = CompilerHelper.getXmlBindingsModelContext().createUnmarshaller();
            JAXBElement<org.eclipse.persistence.jaxb.xmlmodel.XmlElement> jelt = unmarshaller.unmarshal(xmlElementNode, org.eclipse.persistence.jaxb.xmlmodel.XmlElement.class);
            return jelt.getValue();
        } catch (javax.xml.bind.JAXBException jaxbEx) {
            throw org.eclipse.persistence.exceptions.JAXBException.couldNotUnmarshalMetadata(jaxbEx);
        }
    }

    /**
     * If adapter class is null return null If there is a marshal method that
     * returns something other than Object on the adapter class return the
     * return type of that method Otherwise return Object.class
     */
    static Class getTypeFromAdapterClass(Class adapterClass) {
        if (adapterClass != null) {
            Class declJavaType = Object.class;
            // look for marshal method
            Method[] tacMethods = PrivilegedAccessHelper.getMethods(adapterClass);
            for (int i = 0; i < tacMethods.length; i++) {
                Method method = tacMethods[i];
                if (method.getName().equals("marshal")) {
                    Class returnType = PrivilegedAccessHelper.getMethodReturnType(method);
                    if (!(returnType == declJavaType)) {
                        declJavaType = returnType;
                        return declJavaType;
                    }
                }
            }
            return declJavaType;
        }
        return null;

    }
    
    /**
     * If adapter class is null return null If there is a marshal method that
     * returns something other than Object on the adapter class return the
     * return type of that method Otherwise return Object.class
     */
    public static JavaClass getTypeFromAdapterClass(JavaClass adapterClass, Helper helper) {
        if (adapterClass != null) {
            //JavaClass declJavaType = Object.class;
            JavaClass declJavaType = helper.getJavaClass(Object.class);
            // look for marshal method
            Object[] tacMethods = (Object[]) adapterClass.getMethods().toArray();
            for (int i = 0; i < tacMethods.length; i++) {
                JavaMethod method = (JavaMethod)tacMethods[i];
                if (method.getName().equals("marshal")) {
                    JavaClass returnType = method.getReturnType();
                    if (!(returnType.getQualifiedName().equals(declJavaType.getQualifiedName()))) {
                        declJavaType = returnType;
                        return declJavaType;
                    }
                }
            }
            return declJavaType;
        }
        return null;

    }       

    /**
     * Return true if the type is a Collection, List or Set
     */
    private static boolean isCollectionType(Type theType) {
        if (theType instanceof Class) {
            if (Collection.class.isAssignableFrom((Class) theType)
                    || List.class.isAssignableFrom((Class) theType)
                    || Set.class.isAssignableFrom((Class) theType)) {
                return true;
            }
            return false;
        } else if (theType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) theType).getRawType();
            return isCollectionType(rawType);
        }
        return false;
    }

    /**
     * The actual type accounts for adapter classes or xmlelemnt types specified
     * in either an annotation or an XML override
     * 
     */
    static Type getActualType(TypeMappingInfo tmi, org.eclipse.persistence.jaxb.xmlmodel.XmlElement element) {
        try {
            if (element == null) {
                if (tmi.getAnnotations() != null) {
                    for (int i = 0; i < tmi.getAnnotations().length; i++) {
                        java.lang.annotation.Annotation nextAnnotation = tmi.getAnnotations()[i];
                        if (nextAnnotation != null) {
                            if (nextAnnotation instanceof XmlJavaTypeAdapter) {
                                Class typeClass = ((XmlJavaTypeAdapter) nextAnnotation).type();
                                if (typeClass.getName().equals("javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter$DEFAULT")) {
                                    Class adapterClass = ((XmlJavaTypeAdapter) nextAnnotation).value();
                                    return getTypeFromAdapterClass(adapterClass);
                                }
                                return typeClass;
                            } else if (nextAnnotation instanceof XmlElement) {
                                Class typeClass = ((XmlElement) nextAnnotation).type();
                                if (!typeClass.getName().equals("javax.xml.bind.annotation.XmlElement.DEFAULT")) {
                                    final Type tmiType = tmi.getType();
                                    if (isCollectionType(tmiType)) {
                                        final Class itemType = typeClass;
                                        Type parameterizedType = new ParameterizedType() {
                                            Type[] typeArgs = { itemType };

                                            public Type[] getActualTypeArguments() {
                                                return typeArgs;
                                            }

                                            public Type getOwnerType() {
                                                return null;
                                            }

                                            public Type getRawType() {
                                                return tmiType;
                                            }
                                        };
                                        return parameterizedType;
                                    } else {
                                        return typeClass;
                                    }
                                }
                            }
                        }

                    }
                }
                return tmi.getType();
            } else {

                // if it has an XMLElement specified
                // Check for an adapater, then check for XMLElement.type
                if (element.getXmlJavaTypeAdapter() != null) {
                    String actualType = element.getXmlJavaTypeAdapter().getType();
                    if (actualType != null && !actualType.equals("javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT")) {
                        return PrivilegedAccessHelper.getClassForName(actualType);
                    } else {
                        String adapterClassName = element.getXmlJavaTypeAdapter().getValue();
                        Class adapterClass = PrivilegedAccessHelper.getClassForName(adapterClassName);
                        return getTypeFromAdapterClass(adapterClass);
                    }
                }

                if (!(element.getType().equals("javax.xml.bind.annotation.XmlElement.DEFAULT"))) {
                    String actualType = element.getType();

                    final Type tmiType = tmi.getType();
                    if (isCollectionType(tmiType)) {
                        final Class itemType = PrivilegedAccessHelper.getClassForName(actualType);
                        Type parameterizedType = new ParameterizedType() {
                            Type[] typeArgs = { itemType };

                            public Type[] getActualTypeArguments() {
                                return typeArgs;
                            }

                            public Type getOwnerType() {
                                return null;
                            }

                            public Type getRawType() {
                                return tmiType;
                            }
                        };
                        return parameterizedType;
                    } else {
                        return PrivilegedAccessHelper.getClassForName(actualType);
                    }
                }
                return tmi.getType();
            }
        } catch (Exception e) {
            return tmi.getType();
        }
    }

    /**
     * The method will load the eclipselink metadata model and return the
     * corresponding JAXBContext
     */
    public static JAXBContext getXmlBindingsModelContext() {
        if (xmlBindingsModelContext == null) {
            try {
                xmlBindingsModelContext = (JAXBContext) JAXBContextFactory.createContext(METADATA_MODEL_PACKAGE,CompilerHelper.class.getClassLoader());
            } catch (JAXBException e) {
                throw org.eclipse.persistence.exceptions.JAXBException.couldNotCreateContextForXmlModel(e);
            }
            if (xmlBindingsModelContext == null) {
                throw org.eclipse.persistence.exceptions.JAXBException.couldNotCreateContextForXmlModel();
            }
        }
        return xmlBindingsModelContext;
    }
    
    public static JavaClass getNextMappedSuperClass(JavaClass cls, HashMap<String, TypeInfo> typeInfo, Helper helper) {
        JavaClass superClass = cls.getSuperclass();
        
        if(superClass == null || helper.isBuiltInJavaType(cls) || superClass.getRawName().equals("java.lang.Object")){
            return null;
        }
        TypeInfo parentTypeInfo = typeInfo.get(superClass.getQualifiedName());
        if(parentTypeInfo == null || parentTypeInfo.isTransient()) {
            return getNextMappedSuperClass(superClass, typeInfo, helper);
        }
        
        return superClass;
    }
    
    public static void addClassToClassLoader(JavaClass cls, ClassLoader loader) {
    
        if(loader.getClass() == JaxbClassLoader.class && cls.getClass() == JavaClassImpl.class) {
            Class wrappedClass = ((JavaClassImpl)cls).getJavaClass();
            ((JaxbClassLoader)loader).putClass(wrappedClass.getName(), wrappedClass);
        }
    }
    
    static boolean hasNonAttributeJoinNodes(Property property) {
        if(property.isSetXmlJoinNodes()) {
            for(XmlJoinNodes.XmlJoinNode next: property.getXmlJoinNodes().getXmlJoinNode()) {
                if(!(next.getXmlPath().startsWith("@"))) {
                    return true;
                }
            }
        } else if(property.isSetXmlJoinNodesList()) {
            for(XmlJoinNodes nextNodes:property.getXmlJoinNodesList()) {
                for(XmlJoinNodes.XmlJoinNode next: nextNodes.getXmlJoinNode()) {
                    if(!(next.getXmlPath().startsWith("@"))) {
                        return true;
                    }
                }
            }
            
        }
        return false;
    }

    public static Object createAccessorFor(JavaClass jClass, Property property, Helper helper, AccessorFactoryWrapper accessorFactory) {
        if(!(jClass.getClass() == JavaClassImpl.class)) {
            return null;
        }
        Class beanClass = ((JavaClassImpl)jClass).getJavaClass();
        if(property.isMethodProperty()) {
            try {
                Method getMethod = null;
                Method setMethod = null;
                if(property.getGetMethodName() != null) {
                    getMethod = PrivilegedAccessHelper.getMethod(beanClass, property.getGetMethodName(), new Class[]{}, true);
                }
                if(property.getSetMethodName() != null) {
                    String setMethodParamTypeName = property.getType().getName();
                    JavaClassImpl paramType = (JavaClassImpl)helper.getJavaClass(setMethodParamTypeName);
                    Class[] setMethodParams = new Class[]{paramType.getJavaClass()};
                    setMethod = PrivilegedAccessHelper.getMethod(beanClass, property.getSetMethodName(), setMethodParams, true);
                }
                return accessorFactory.createPropertyAccessor(beanClass, getMethod, setMethod);
            } catch(Exception ex) {}      
        }  else {
            try {
                Field field = PrivilegedAccessHelper.getField(beanClass, ((JavaField)property.getElement()).getName(), true);
                return accessorFactory.createFieldAccessor(beanClass, field, property.isReadOnly());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
