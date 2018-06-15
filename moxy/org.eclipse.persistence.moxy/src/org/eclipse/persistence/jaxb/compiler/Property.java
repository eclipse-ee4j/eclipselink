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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.compiler;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.compiler.facets.Facet;
import org.eclipse.persistence.internal.jaxb.GenericsClassHelper;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.jaxb.javamodel.*;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAbstractNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElements;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlMarshalNullRepresentation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation;
import org.eclipse.persistence.oxm.XMLField;

/**
 *  INTERNAL:
 *  <p><b>Purpose:</b>To store information about a property on a class during JAXB 2.0 Generation
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Store information about the java property such as property name, if it's a method or field,
 *  and it's type.</li>
 *  <li>Store some schema-specific information such as the schema name, the schema type, and mimeType</li>
 *  <li>Store some JAXB 2.0 Runtime specific information such as JAXB 2.0 Adapter classes</li>
 *  </ul>
 *  <p>This class is used to store information about a property on a JAXB 2.0 annotated class. It is
 *  created by the AnnotationsProcessor during pre-processing and stored on a TypeInfo object
 *
 *  @see org.eclipse.persistence.jaxb.compiler.TypeInfo
 *  @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 *
 *  @author mmacivor
 */
public class Property implements Cloneable {
    private String propertyName;
    private QName schemaName;
    private boolean isMethodProperty;
    private QName schemaType;
    private boolean isSwaAttachmentRef;
    private boolean isMtomAttachment;
    private boolean isInlineBinaryData;
    private String mimeType;
    private JavaClass type;
    private JavaHasAnnotations element;
    private JavaClass genericType;
    private boolean isAttribute = false;
    private boolean isAnyAttribute = false;
    private boolean isAnyElement = false;
    private Helper helper;
    private Map<Object, Object> userProperties;
    private List<Facet> facets = new ArrayList<Facet>();
    private boolean isXmlValueExtension;
    private boolean isXmlIdExtension;

    //Original get and set methods for this property
    //Used to keep track of overrides
    private String originalGetMethodName;
    private String originalSetMethodName;

    private String getMethodName;
    private String setMethodName;
    private boolean isRequired = false;
    private boolean isNillable = false;
    private boolean isXmlList = false;
    private boolean isTransient;
    private String defaultValue;
    private boolean isMixedContent = false;
    private boolean xmlElementType = false;
    private JavaClass originalType;
    private String fixedValue;
    private Boolean isReadOnly;
    private Boolean isWriteOnly;
    private Boolean isCdata;
    private boolean isVirtual = false;
    private XmlTransformation xmlTransformation;
    private XmlAbstractNullPolicy nullPolicy;
    private XmlJavaTypeAdapter xmlJavaTypeAdapter;
    private XmlElementWrapper xmlElementWrapper;
    private boolean isXmlValue = false;
    private boolean isXmlId = false;
    private boolean isXmlIdRef = false;
    private boolean isXmlTransformation = false;
    private boolean isXmlLocation = false;

    private String inverseReferencePropertyName;
    private String inverseReferencePropertyGetMethodName;
    private String inverseReferencePropertySetMethodName;
    private JavaClass inverseReferencePropertyContainerClass;
    private boolean isInverseReference;
    private boolean isWriteableInverseReference;

    // XmlAnyElement specific attributes
    private boolean lax;
    private String domHandlerClassName;
    private String variableAttributeName;
    private String variableClassName;
    private boolean variableNodeAttribute;

    // XmlMap specific attributes
    private JavaClass keyType;
    private JavaClass valueType;
    private JavaClass valueGenericType;
    public static final String DEFAULT_KEY_NAME =  "key";
    public static final String DEFAULT_VALUE_NAME =  "value";
    private boolean isMap = false;
    private String xmlPath;

    // XmlElements specific attributes
    private Collection<Property> choiceProperties;
    private XmlElements xmlElements;
    private boolean isChoice = false;

    // XmlElementRef specific attributes
    private ArrayList<ElementDeclaration> referencedElements;
    private List<XmlElementRef> xmlElementRefs;
    private boolean isReference = false;

    // XmlJoinNodes specific attributes
    private XmlJoinNodes xmlJoinNodes;
    private List<XmlJoinNodes> xmlJoinNodesList;
    private boolean isSuperClassProperty;

    private boolean isTransientType;
    private static final String MARSHAL_METHOD_NAME = "marshal";

    private JavaClass xmlAdapterClass;
    private JavaClass objectClass;

    private boolean isTyped;
    private Integer minOccurs;
    private Integer maxOccurs;
    private boolean notNullAnnotated;


    public Property() {}

    public Property(Helper helper) {
        this.helper = helper;

        xmlAdapterClass = helper.getJavaClass(XmlAdapter.class);
        objectClass = helper.getJavaClass(Object.class);
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    /**
     * Set an XmlAdapter on this Property.  The type, generic type and
     * original type will be set as required based on the XmlAdapter's
     * marshal method return type and input parameters.
     *
     * @param adapterCls
     */
    public void setAdapterClass(JavaClass adapterCls) {

        if (adapterCls.instanceOf() == JavaClassInstanceOf.JAVA_CLASS_IMPL) {
            Type[] parameterizedTypeArguments = GenericsClassHelper.getParameterizedTypeArguments(((JavaClassImpl)adapterCls).getJavaClass(), XmlAdapter.class);
            if (null != parameterizedTypeArguments && null != parameterizedTypeArguments[0]) {
                JavaClass valueTypeClass = getJavaClassFromType(parameterizedTypeArguments[0]);
                JavaClass boundType = getJavaClassFromType(parameterizedTypeArguments[1]);

                if (valueTypeClass.isInterface()) {
                    valueTypeClass = objectClass; // during unmarshalling we'll need to instantiate this, so -> no interfaces
                }

                setTypeFromAdapterClass(valueTypeClass, boundType);
                return;
            }
        }

        // If no generic superclass was found, use the old method of looking at
        // marshal method return type.  This mechanism is used for Dynamic JAXB.
        ArrayList<JavaMethod> marshalMethods = new ArrayList<JavaMethod>();

        // Look for marshal method
        for (JavaMethod method : (Collection<JavaMethod>) adapterCls.getMethods()) {
            if (!method.isBridge() && method.getName().equals(MARSHAL_METHOD_NAME)) {
                final JavaClass[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1)
                    continue;
                JavaClass returnType = method.getReturnType();

                // Try and find a marshal method where Object is not the return type,
                // to avoid processing an inherited default marshal method
                if (!returnType.getQualifiedName().equals(objectClass.getQualifiedName()) && !returnType.isInterface()) { // if it's interface, we'll use OBJECT instead later
                    setTypeFromAdapterClass(returnType, parameterTypes[0]);
                    return;
                }
            }
            // Found a marshal method with an Object return type; add
            // it to the list in case we need to process it later
            marshalMethods.add(method);
        }
        // At this point we didn't find a marshal method with a non-Object return type
        for (JavaMethod method : marshalMethods) {
            JavaClass paramType = method.getParameterTypes()[0];
            // look for non-Object parameter type
            if (!paramType.getQualifiedName().equals(objectClass.getQualifiedName())) {
                setTypeFromAdapterClass(objectClass, paramType);
                return;
            }
        }
        if (!marshalMethods.isEmpty())
            setTypeFromAdapterClass(objectClass, null);
        // else impossible? - looks like provided adapted doesn't contain marshal(...) method
    }

    private JavaClass getJavaClassFromType(Type t) {
        if (t instanceof Class) {
            return helper.getJavaClass((Class) t);
        } else if (t instanceof ParameterizedType) {
            ParameterizedType paramValueType = (ParameterizedType) t;
            Type rawType = paramValueType.getRawType();
            if (rawType instanceof Class) {
                return helper.getJavaClass((Class) rawType);
            }
        } else if (t instanceof TypeVariable<?>) {
            TypeVariable<?> valueTypeVariable = (TypeVariable<?>) t;
            return helper.getJavaClass((Class) valueTypeVariable.getBounds()[0]);
        } else if (t instanceof GenericArrayType) {
            GenericArrayType genericArrayValueType = (GenericArrayType) t;
            Type rawType = genericArrayValueType.getGenericComponentType();
            if (rawType instanceof Class) {
                return helper.getJavaClass(Array.newInstance((Class) rawType, 1).getClass());
            }
        }
        return objectClass;
    }

    /**
     * This convenience method will set the generic type, type and original type
     * for this Porperty as required based on a given 'new' type and parameter
     * type. This method will typically be called when setting the type during
     * adapter processing.
     *
     * @param newType
     * @param parameterType
     */
    private void setTypeFromAdapterClass(JavaClass newType, JavaClass parameterType) {
        boolean isArray = this.getType().isArray() && !this.getType().getRawName().equals("byte[]");
        boolean isParamTypeArray = false;
        if(parameterType != null) {
            isParamTypeArray = parameterType.isArray() && !parameterType.getRawName().equals("byte[]");
        }
        if ((helper.isCollectionType(this.getType()) || isArray) && !(helper.isCollectionType(parameterType) || isParamTypeArray)) {
            this.setGenericType(newType);
        } else {
            this.setOriginalType(this.getType());
            this.setType(newType);
        }
    }

    public JavaHasAnnotations getElement() {
        return element;
    }

    public void setElement(JavaHasAnnotations element) {
        this.element = element;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public QName getSchemaName() {
        return this.schemaName;
    }

    public void setSchemaName(QName schemaName) {
        this.schemaName = schemaName;
    }

    public boolean isMethodProperty() {
        return isMethodProperty;
    }

    public void setMethodProperty(boolean isMethod) {
        isMethodProperty = isMethod;
    }

    public void setType(JavaClass cls) {
        if(cls == null){
            return;
        }
        if(type != null && type == cls){
            return;
        }
        String clsName= cls.getRawName();
        if(helper.isCollectionType(cls)){
               genericType = getGenericType(cls, 0, helper);
            type = cls;
        }else if(cls.isArray()  && !clsName.equals("byte[]") ){
            type = cls;
            genericType = cls.getComponentType();
        }else{
            type = cls;
            genericType = null;
        }

        boolean isNewTypeMap = helper.getJavaClass(java.util.Map.class).isAssignableFrom(type);
        setIsMap(isNewTypeMap);

        if(isMap()){
            if(type.getPackageName().startsWith("java.")) {
                Object[] types = type.getActualTypeArguments().toArray();

                 if(types.length >=2){
                     keyType = (JavaClass)types[0];
                     valueType = (JavaClass)types[1];
                     if (helper.isCollectionType(valueType)) {
                         valueGenericType = getGenericType(valueType, 0, helper);
                     } else if (valueType.isArray() && !"byte[]".equals(valueType.getRawName())) {
                         valueGenericType = valueType.getComponentType();
                     } else {
                         valueGenericType = null;
                     }
                 }else{
                     keyType = helper.getJavaClass(Object.class);
                     valueType = helper.getJavaClass(Object.class);
                 }
            } else {
                keyType = getGenericType(type, 0, helper);
                valueType = getGenericType(type, 1, helper);
            }
        }

    }

    private JavaClass getGenericType(JavaClass cls, int argument, Helper helper){
        Collection typeArgs = cls.getActualTypeArguments();
        Object[] typeArgsArray = typeArgs.toArray();
        JavaClass genericType = null;
        if(cls.getPackageName().startsWith("java.")) {
            if(typeArgs.size() > argument){
                Iterator iterator = typeArgs.iterator();
                for(int x=0; x<argument; x++) {
                    iterator.next();
                }
                return (JavaClass) iterator.next();
            } else {
                return helper.getJavaClass(Object.class);
            }
        }else{
            Map<String, JavaClass> variableToType = null;

            if(cls instanceof JavaClassImpl){
                variableToType = new HashMap<String, JavaClass>();
                TypeVariable[] tvs = ((JavaClassImpl)cls).getJavaClass().getTypeParameters();
                if(tvs.length == typeArgsArray.length){
                    for (int x = 0; x < tvs.length; x++) {
                        variableToType.put(tvs[x].getName(), (JavaClass)typeArgsArray[x]);
                    }
                }
            }

            Type genericTypeType = getGenericType(cls.getGenericSuperclass(), argument, variableToType);

            if(null == genericTypeType) {
                for(Type genericInterface : cls.getGenericInterfaces()) {
                    genericTypeType = getGenericType(genericInterface, argument, variableToType);
                    if(null != genericTypeType) {
                        break;
                    }
                }
            }
            if(genericTypeType instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) genericTypeType;
                JavaClass existing = variableToType.get(typeVariable.getName());
                if( existing != null){
                    return existing;
                }else{
                    Type[] typeVariableBounds = typeVariable.getBounds();
                    if(typeVariableBounds.length > 0) {
                        genericTypeType = typeVariableBounds[0];
                    }
                }
            }

            if(genericTypeType instanceof Class) {
                genericType = helper.getJavaClass((Class) genericTypeType);
            }
            if(null == genericType || genericTypeType == Object.class) {
                if(typeArgs.size() > argument){
                    Iterator iterator = typeArgs.iterator();
                    for(int x=0; x<argument; x++) {
                        iterator.next();
                    }
                    genericType = (JavaClass) iterator.next();
                } else {
                    genericType = helper.getJavaClass(Object.class);
                }
            }
            return genericType;
        }

    }

    private Type getGenericType(Type type, int argument, Map<String, JavaClass> variableToType) {
        if(type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if(rawType instanceof Class) {
                Class rawTypeClass = (Class) rawType;
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                TypeVariable[] tvs = rawTypeClass.getTypeParameters();
                if(tvs.length == typeArgs.length){
                    for (int x = 0; x < tvs.length; x++) {
                        String name = tvs[x].getName();
                        Type theType = typeArgs[x];
                        if(!variableToType.containsKey(name) && theType instanceof Class){
                            variableToType.put(name, helper.getJavaClass((Class)theType));
                        }
                    }
                }

                if(rawTypeClass.getPackage().getName().startsWith("java.")) {
                    Type actualType = parameterizedType.getActualTypeArguments()[argument];
                    return actualType;
                }
                Type genericType = getGenericType(rawType, argument, variableToType);
                if(genericType != null) {
                    return genericType;
                } else {
                    return parameterizedType.getActualTypeArguments()[argument];
                }
            } else {
                return getGenericType(parameterizedType.getRawType(), argument,variableToType);
            }
        } else if(type instanceof Class) {
            Class clazz = (Class) type;
            for(Type genericInterface : clazz.getGenericInterfaces()) {
                Type genericType = getGenericType(genericInterface, argument, variableToType);
                if(null != genericType) {
                    return genericType;
                }
            }
            return getGenericType(clazz.getGenericSuperclass(), argument, variableToType);
        }
        return null;
    }

    public JavaClass getType() {
        return type;
    }

    public JavaClass getGenericType() {
        return genericType;
    }

    public void setGenericType(JavaClass genericType) {
        this.genericType = genericType;
    }

    public QName getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(QName type) {
        schemaType = type;
    }

    public boolean isSwaAttachmentRef() {
        return isSwaAttachmentRef;
    }

    public void setIsSwaAttachmentRef(boolean b) {
        isSwaAttachmentRef = b;
    }

    public boolean isMtomAttachment() {
        return isMtomAttachment;
    }

    public void setIsMtomAttachment(boolean b) {
        isMtomAttachment = b;
    }

    /**
     * Indicates if XOP encoding should be disabled for datatypes that
     * are bound to base64-encoded binary data in XML.
     *
     * @return true if XOP encoding should be disabled for datatypes
     * that are bound to base64-encoded binary data in XML; false if
     * not
     */
    public boolean isInlineBinaryData() {
        return isInlineBinaryData;
    }

    /**
     * Sets the flag that indicates if XOP encoding should  be disabled
     * for datatypes that are bound to base64-encoded binary data in
     * XML.

     * @param b if true, XOP encoding will be disabled for datatypes
     * that are bound to base64-encoded binary data in XML.
     */
    public void setisInlineBinaryData(boolean b) {
        isInlineBinaryData = b;
    }

    public boolean isRequired() {
        return isRequired;
    }



    public boolean isTransientType() {
        return isTransientType;
    }

    public void setTransientType(boolean isTransientType) {
        this.isTransientType = isTransientType;
    }

    public void setIsRequired(boolean b) {
        isRequired = b;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mType) {
        mimeType = mType;
    }

    public boolean isAttribute() {
        return isAttribute || isAnyAttribute;
    }

    public boolean isAnyAttribute() {
        return isAnyAttribute;
    }

    public void setIsAttribute(boolean attribute) {
        isAttribute = attribute;
    }

    public void setIsAnyAttribute(boolean anyAtribute) {
        isAnyAttribute = anyAtribute;
    }

    public String getGetMethodName() {
        return getMethodName;
    }

    public void setGetMethodName(String methodName) {
        getMethodName = methodName;
    }

    public String getSetMethodName() {
        return setMethodName;
    }

    public void setSetMethodName(String methodName) {
        setMethodName = methodName;
    }

    public String getOriginalGetMethodName() {
        return originalGetMethodName;
    }

    public void setOriginalGetMethodName(String methodName) {
        originalGetMethodName = methodName;
    }

    public String getOriginalSetMethodName() {
        return originalSetMethodName;
    }

    public void setOriginalSetMethodName(String methodName) {
        originalSetMethodName = methodName;
    }

    /**
     * Indicates if this property represents a choice property.
     *
     * @return
     */
    public boolean isChoice() {
        return isChoice;
    }

    /**
     * Set flag to indicate whether this property represents a choice
     * property.
     *
     * @param choice
     */
    public void setChoice(boolean choice) {
        isChoice = choice;
    }

    /**
     * Returns indicator for XmlAnyElement
     *
     * @return
     */
    public boolean isAny() {
        return isAnyElement;
    }

    /**
     * Set indicator for XmlAnyElement.
     *
     * @param isAnyElement
     */
    public void setIsAny(boolean isAnyElement) {
        this.isAnyElement = isAnyElement;
    }

    /**
     * Indicates if this Property is a reference property.
     *
     * @return
     */
    public boolean isReference() {
        return isReference;
    }

    /**
     * Set flag to indicate whether this property represents a reference
     * property.
     *
     * @param isReference
     */
    public void setIsReference(boolean isReference) {
        this.isReference = isReference;
    }

    public boolean isNillable() {
        return isNillable;
    }

    public void setNillable(boolean isNillable) {
        this.isNillable = isNillable;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return the isSetDefaultValue
     */
    public boolean isSetDefaultValue() {
        return defaultValue != null;
    }

    public boolean isMixedContent() {
        return isMixedContent;
    }

    public void setMixedContent(boolean b) {
        this.isMixedContent = b;
    }

    public void setHasXmlElementType(boolean hasXmlElementType) {
        this.xmlElementType = hasXmlElementType;
    }

    public boolean isXmlElementType() {
        return xmlElementType;
    }

    /**
     * Return the generic type if it was set (collection or array item type) otherwise return the
     * type of this property
     *
     * @return
     */
    public JavaClass getActualType() {
        if (genericType != null) {
            return genericType;
        }
        return type;
    }

    /**
     * Get the original type of the property.  This is typically used when
     * the type has been changed via @XmlElement annotation and the
     * original type is desired.
     *
     * @return
     */
    public JavaClass getOriginalType() {
        if (originalType == null) // in case of adapter which returns the same type - original type is the same as type
            return type;
        return originalType;
    }

    /**
     * Set the original type of the property.  This is typically used when
     * the type will been changed via @XmlElement annotation and the
     * original type may be needed.
     */
    public void  setOriginalType(JavaClass type) {
        originalType = type;
    }

    /**
     * Indicates if an XmlJavaTypeAdapter has been set, i.e. the
     * xmlJavaTypeAdapter property is non-null.
     *
     * @return true if xmlJavaTypeAdapter is non-null, false otherwise
     * @see XmlJavaTypeAdapter
     */
    public boolean isSetXmlJavaTypeAdapter() {
        return getXmlJavaTypeAdapter() != null;
    }

    /**
     * Return the xmlJavaTypeAdapter set on this Property.
     *
     * @return xmlJavaTypeAdapter, or null if not set
     * @see XmlJavaTypeAdapter
     */
    public XmlJavaTypeAdapter getXmlJavaTypeAdapter() {
        return xmlJavaTypeAdapter;
    }

    /**
     * Set an XmlJavaTypeAdapter on this Property.  This call sets the adapterClass
     * property to the given adapter's value.
     *
     * @param xmlJavaTypeAdapter
     * @see XmlJavaTypeAdapter
     */
    public void setXmlJavaTypeAdapter(XmlJavaTypeAdapter xmlJavaTypeAdapter) {
        this.xmlJavaTypeAdapter = xmlJavaTypeAdapter;
        if (xmlJavaTypeAdapter == null) {
            setType(originalType);
        } else {
            // set the adapter class
            setAdapterClass(helper.getJavaClass(xmlJavaTypeAdapter.getValue()));
        }
    }

    /**
     * Indicates if an XmlElementWrapper has been set, i.e. the
     * xmlElementWrapper property is non-null.

     * @return true if xmlElementWrapper is non-null, false otherwise
     */
    public boolean isSetXmlElementWrapper() {
        return getXmlElementWrapper() != null;
    }

    /**
     * Return the XmlElementWrapper set on this property.
     *
     * @return XmlElementWrapper instance if non-null, null otherwise
     */
    public XmlElementWrapper getXmlElementWrapper() {
        return xmlElementWrapper;
    }

    /**
     * Set the XmlElementWrapper for this property.
     *
     * @param xmlElementWrapper
     */
    public void setXmlElementWrapper(XmlElementWrapper xmlElementWrapper) {
        this.xmlElementWrapper = xmlElementWrapper;
    }

    /**
     * Set the isXmlValue property.
     *
     * @param isXmlValue
     */
    public void setIsXmlValue(boolean isXmlValue) {
        this.isXmlValue = isXmlValue;
    }

    /**
     * Indicates if this property is an XmlValue.
     *
     * @return
     */
    public boolean isXmlValue() {
        return this.isXmlValue;
    }

    /**
     * Set the isXmlValueExtension property.
     *
     * @param isXmlValueExtension
     */
    public void setIsXmlValueExtension(boolean isXmlValueExtension) {
        this.isXmlValueExtension = isXmlValueExtension;
    }

    /**
     * Indicates if this property is an XmlValueExtension.
     *
     * @return
     */
    public boolean isXmlValueExtension() {
        return this.isXmlValueExtension;
    }

    /**
     * Set the isXmlList property.
     *
     * @param isXmlList
     */
    public void setIsXmlList(boolean isXmlList) {
        this.isXmlList = isXmlList;
        if(isXmlList){
            this.setNillable(false);
        }
    }

    /**
     * Indicates if this property is an XmlList.
     *
     * @return
     */
    public boolean isXmlList() {
        return this.isXmlList;
    }

    public String getInverseReferencePropertyName() {
        return this.inverseReferencePropertyName;
    }

    public void setInverseReferencePropertyName(String name) {
        this.inverseReferencePropertyName = name;
    }

    public String getInverseReferencePropertyGetMethodName() {
        return this.inverseReferencePropertyGetMethodName;
    }

    public String getInverseReferencePropertySetMethodName() {
        return this.inverseReferencePropertySetMethodName;
    }

    public void setInverseReferencePropertyGetMethodName(String methodName) {
        this.inverseReferencePropertyGetMethodName = methodName;
    }

    public void setInverseReferencePropertySetMethodName(String methodName) {
        this.inverseReferencePropertySetMethodName = methodName;
    }

    public JavaClass getInverseReferencePropertyContainerClass() {
        return this.inverseReferencePropertyContainerClass;
    }

    public void setInverseReferencePropertyContainerClass(JavaClass cls) {
        this.inverseReferencePropertyContainerClass = cls;
    }

    /**
     * Indicates if this property is an ID field.
     *
     * @return
     */
    public boolean isXmlId() {
        return isXmlId;
    }

    /**
     * Sets the indicator that identifies this property as an ID field.
     *
     * @param isXmlId
     */
    public void setIsXmlId(boolean isXmlId) {
        this.isXmlId = isXmlId;
    }

    /**
     * Indicates if this property is an ID extension field.
     *
     * @return
     */
    public boolean isXmlIdExtension() {
        return isXmlIdExtension;
    }

    /**
     * Sets the indicator that identifies this property as an ID extension field.
     *
     * @param isXmlIdExtension
     */
    public void setIsXmlIdExtension(boolean isXmlIdExtension) {
        this.isXmlIdExtension = isXmlIdExtension;
    }

    /**
     * Indicates if this property is a reference to an ID field.
     *
     * @return
     */
    public boolean isXmlIdRef() {
        return isXmlIdRef;
    }

    /**
     * Sets the indicator that identifies this property as a reference
     * to an ID field.
     *
     * @param isXmlIdRef
     */
    public void setIsXmlIdRef(boolean isXmlIdRef) {
        this.isXmlIdRef = isXmlIdRef;
    }

    // XmlAnyElement specific methods

    /**
     * Used with XmlAnyElement.
     *
     * @return
     */
    public boolean isLax() {
        return lax;
    }

    /**
     * Used with XmlAnyElement.
     *
     * @param b
     */
    public void setLax(boolean b) {
        lax = b;
    }

    /**
     * Return the DomHandler class name.
     * Used with XmlAnyElement.
     *
     * @return
     */
    public String getDomHandlerClassName() {
        return domHandlerClassName;
    }

    /**
     * Set the DomHandler class name.
     * Used with XmlAnyElement.
     *
     * @param domHandlerClassName
     */
    public void setDomHandlerClassName(String domHandlerClassName) {
        this.domHandlerClassName = domHandlerClassName;
    }

    public JavaClass getKeyType() {
        return keyType;
    }

    public void setKeyType(JavaClass keyType) {
        this.keyType = keyType;
    }

    public JavaClass getValueType() {
        return valueType;
    }

    /**
     * Return the generic type if it was set (collection or array item type) otherwise return the
     * type of this property
     *
     * @return
     */
    public JavaClass getActualValueType() {
        if (valueGenericType != null) {
            return valueGenericType;
        }
        return valueType;
    }

    public void setValueType(JavaClass valueType) {
        this.valueType = valueType;
    }

    public JavaClass getValueGenericType() {
        return valueGenericType;
    }

    public void setValueGenericType(JavaClass valueGenericType) {
        this.valueGenericType = valueGenericType;
    }

    public boolean isMap() {
        return isMap;
    }

    private void setIsMap(boolean isMap) {
        this.isMap = isMap;
    }

    public boolean isInverseReference() {
        return isInverseReference;
    }

    public void setInverseReference(boolean isInverseReference, boolean isWriteable) {
        this.isInverseReference = isInverseReference;
        this.isWriteableInverseReference = isWriteable;
    }

    public boolean isWriteableInverseReference(){
        return isWriteableInverseReference;
    }

    /**
     * Return the XmlElements object set for this Property.  Typically
     * this will only be set if we are dealing with a 'choice'.
     *
     * @return
     */
    public XmlElements getXmlElements() {
        return xmlElements;
    }

    /**
     * Set the XmlElements object for this Property.  Typically
     * this will only be set if we are dealing with a 'choice'.
     *
     * @param xmlElements
     */
    public void setXmlElements(XmlElements xmlElements) {
        this.xmlElements = xmlElements;
    }

    /**
     * Return the choice properties set on this property.  Typically this
     * will only contain properties if we are dealing with a 'choice'.
     *
     * @return
     */
    public Collection<Property> getChoiceProperties() {
        return this.choiceProperties;
    }

    /**
     * Set the choice properties for this property.  Typically this
     * will only contain properties if we are dealing with a 'choice'.
     *
     * @param properties
     */
    public void setChoiceProperties(Collection<Property> properties) {
        this.choiceProperties = properties;
    }

    /**
     * Return the List of XmlElementRef(s) for this Property.
     *
     * @return
     */
    public List<XmlElementRef> getXmlElementRefs() {
        return xmlElementRefs;
    }

    /**
     * Set the List of XmlElementRef(s) for this Property.
     *
     * @param xmlElementRefs
     */
    public void setXmlElementRefs(List<XmlElementRef> xmlElementRefs) {
        this.xmlElementRefs = xmlElementRefs;
    }

    /**
     * Add an ElementDeclaration to the list of referenced elements. Typically this
     * will only contain ElementDeclarations if we are dealing with a 'reference'.
     *
     * @param element
     */
    public void addReferencedElement(ElementDeclaration element) {
        if (referencedElements == null) {
            referencedElements = new ArrayList<ElementDeclaration>();
        }
        if (!referencedElements.contains(element)) {
            referencedElements.add(element);
        }
    }

    /**
     * Return the list of referenced elements.  Typically this will only
     * contain ElementDeclarations if we are dealing with a 'reference'.
     *
     * @return
     */
    public List<ElementDeclaration> getReferencedElements() {
        return referencedElements;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    /**
     * Indicates if this property is mapped by position, i.e. 'name="data[1]"',
     * or is mapped by attribute value (predicate mapping), i.e.
     * 'personal-info[@pi-type='last-name']/name[@name-type='surname']/text()'
     *
     */
    public boolean isPositional() {
        if (getXmlPath() == null) {
            return false;
        }
        Field field = new XMLField(getXmlPath());
        XPathFragment frag = field.getXPathFragment();
        // loop until we have the last non-null, non-attribute, non-text fragment
        while (true) {
            if (frag.getNextFragment() != null && !frag.getNextFragment().isAttribute() && !frag.getNextFragment().nameIsText()) {
                frag = frag.getNextFragment();
            } else {
                break;
            }
        }
        return frag.containsIndex() || frag.getPredicate() != null;
    }

    /**
     * Flag the mapping for this Property as read-only.
     *
     * @param isReadOnly the true/false value to set
     */
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    /**
     * Indicates if the mapping for this Property is read-only.
     *
     * @return true if read-only, false if not
     */
    public boolean isReadOnly() {
        if (isReadOnly == null) {
            return false;
        }
        return isReadOnly;
    }

    /**
     * Indicates if the isReadOnly flag was set via external metadata.
     *
     * @return
     */
    public boolean isSetReadOnly() {
        return isReadOnly != null;
    }

    /**
     * Flag the mapping for this Property as write-only.
     *
     * @param isWriteOnly the true/false value to set
     */
    public void setWriteOnly(boolean isWriteOnly) {
        this.isWriteOnly = isWriteOnly;
    }

    /**
     * @return true if write-only, false if not
     */
    public boolean isWriteOnly() {
        if (isWriteOnly == null) {
            return false;
        }
        return isWriteOnly;
    }

    /**
     * Indicates if the isWriteOnly flag was set via external metadata.
     *
     * @return
     */
    public boolean isSetWriteOnly() {
        return isWriteOnly != null;
    }

    /**
     * Flag the mapping for this Property as containing character data.
     *
     * @param isCdata the true/false value to set
     */
    public void setCdata(boolean isCdata) {
        this.isCdata = isCdata;
    }

    /**
     * @return true if character data, false if not
     */
    public boolean isCdata() {
        if (isCdata == null) {
            return false;
        }
        return isCdata;
    }

    /**
     * Indicates if the isCdata flag was set via external metadata.
     *
     * @return
     */
    public boolean isSetCdata() {
        return isCdata != null;
    }

    /**
     * Return the xpath for this property.
     *
     * @return
     */
    public String getXmlPath() {
        return xmlPath;
    }

    /**
     * Set the xpath for this property.
     *
     * @param xmlPath
     */
    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    /**
     * Indicates if an xpath is set for this property.
     *
     * @return
     */
    public boolean isSetXmlPath() {
        return xmlPath != null;
    }

    /**
     * Returns the null policy for this property.
     *
     * @return null policy or null if not set
     */
    public XmlAbstractNullPolicy getNullPolicy() {
        return nullPolicy;
    }

    /**
     * Set the null policy for this property.
     *
     * @param nullPolicy
     */
    public void setNullPolicy(XmlAbstractNullPolicy nullPolicy) {
        this.nullPolicy = nullPolicy;
    }

    /**
     * Indicates if a null policy is set for this property.
     *
     * @return
     */
    public boolean isSetNullPolicy() {
        return nullPolicy != null;
    }

    /**
     * Indicates if nillable='true' should be set on a given schema component.
     * This will typically be called by SchemaGenerator.
     * The value returned will be true if one of the following is true:
     *
     * - isNillable
     * - isSetNullPolicy {@literal &&} xsi-nil-represents-null == 'true'
     * - isSetNullPolicy {@literal &&} null-representation-for-xml == 'XSI_NIL'
     *
     * @return
     */
    public boolean shouldSetNillable() {
        if (isNillable()) {
            return true;
        }
        if (isSetNullPolicy()) {
            return (getNullPolicy().isXsiNilRepresentsNull() || getNullPolicy().getNullRepresentationForXml() == XmlMarshalNullRepresentation.XSI_NIL);
        }
        return false;
    }

    /**
     * Return the Map of user-defined properties.
     *
     * @return
     */
    public Map<Object, Object> getUserProperties() {
        return userProperties;
    }

    /**
     * Set the Map of user-defined properties.
     *
     * @param userProperties
     */
    public void setUserProperties(Map<Object, Object> userProperties) {
        this.userProperties = userProperties;
    }

    /**
     * Indicates if a map of userProperties is set for this property.
     *
     * @return true if the userProperties property has been set,
     *         i.e. is non-null, otherwise false
     */
    public boolean isSetUserProperties() {
        return userProperties != null;
    }

    /**
     * Return the XmlTransformation set on this property.
     *
     * @return the XmlTransformation set on this property, or null if one has not been set.
     */
    public XmlTransformation getXmlTransformation() {
        return xmlTransformation;
    }

    /**
     * Set the XmlTransformation for this property.  The info contained in
     * the XmlTransformation will be used to construct an
     * XmlTransformationMapping.
     *
     * @param xmlTransformation
     */
    public void setXmlTransformation(XmlTransformation xmlTransformation) {
        this.xmlTransformation = xmlTransformation;
    }

    /**
     * Indicates if an XmlTransformation is set for this porperty.
     *
     * @return true if the xmlTransformation property has been set,
     *         i.e. is non-null, otherwise false
     */
    public boolean isSetXmlTransformation() {
        return xmlTransformation != null;
    }

    /**
     * Indicates if this property represents an XmlTransformation.
     *
     * @return value of isXmlTransformation property
     */
    public boolean isXmlTransformation() {
        return isXmlTransformation;
    }

    /**
     * Set flag that indicates if this property represents an XmlTransformation.
     *
     * @param isXmlTransformation
     */
    public void setIsXmlTransformation(boolean isXmlTransformation) {
        this.isXmlTransformation = isXmlTransformation;
    }

    /**
     * Set XmlJoinNodes for this property.
     *
     * @param xmlJoinNodes the xmlJoinNodes to set
     */
    public void setXmlJoinNodes(XmlJoinNodes xmlJoinNodes) {
        this.xmlJoinNodes = xmlJoinNodes;
        if(xmlJoinNodes != null && !CompilerHelper.hasNonAttributeJoinNodes(this)) {
            this.isAttribute = true;
        }
    }

    /**
     * Return the XmlJoinNodes for this property.
     *
     * @return the xmlJoinNodes
     */
    public XmlJoinNodes getXmlJoinNodes() {
        return xmlJoinNodes;
    }

    /**
     * Indicates if this property has XmlJoinNodes set.
     *
     * return true if xmlJoinNodes is non-null, otherwise false
     */
    public boolean isSetXmlJoinNodes() {
        return this.xmlJoinNodes != null;
    }

    /**
     * Return a shallow copy of this Property.
     * Simply calls super.clone().
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // will never get here
        }
        return null;
    }

    /**
     * Return the list of XmlJoinNodes for this Property, if any.
     * This will typically be set when processing an XmlElements
     * declaration containing XmlJoinNodes.
     *
     */
    public List<XmlJoinNodes> getXmlJoinNodesList() {
        return xmlJoinNodesList;
    }

    /**
     * Set the list of XmlJoinNodes for this Property. This method
     * will typically be called when processing an XmlElements
     * declaration containing XmlJoinNodes.
     *
     */
    public void setXmlJoinNodesList(List<XmlJoinNodes> xmlJoinNodesList) {
        this.xmlJoinNodesList = xmlJoinNodesList;
        if(xmlJoinNodesList != null && !(xmlJoinNodesList.isEmpty()) && !CompilerHelper.hasNonAttributeJoinNodes(this)) {
            this.isAttribute = true;
        }
    }

    /**
     * Indicates if xmlJoinNodesList has been set, i.e. is non-null
     *
     * @return true if xmlJoinNodesList is non-null, false otherwise
     */
    public boolean isSetXmlJoinNodesList() {
        return this.xmlJoinNodesList != null;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setExtension(boolean value) {
        this.isVirtual = value;
    }

    public boolean isXmlLocation() {
        return isXmlLocation;
    }

    public void setXmlLocation(boolean isXmlLocation) {
        this.isXmlLocation = isXmlLocation;
    }

    /**
     * This event is called when all of the metadata for this property has been
     * processed and provides a chance to deference anything that is no longer
     * needed to reduce the memory footprint of this object.
     */
    void postInitialize() {
        this.element = null;
    }

    public boolean isSuperClassProperty() {
        return this.isSuperClassProperty;
    }

    public void setIsSuperClassProperty(boolean b) {
        this.isSuperClassProperty = b;
    }

    public String getVariableAttributeName() {
        return variableAttributeName;
    }

    public void setVariableAttributeName(String variableAttributeName) {
        this.variableAttributeName = variableAttributeName;
    }

    public String getVariableClassName() {
        return variableClassName;
    }

    public void setVariableClassName(String variableClassName) {
        this.variableClassName = variableClassName;
     }

    public boolean isVariableNodeAttribute() {
        return variableNodeAttribute;
    }

    public void setVariableNodeAttribute(boolean variableNodeAttribute) {
         this.variableNodeAttribute = variableNodeAttribute;
    }

    public boolean isTyped() {
        return isTyped;
    }

    public void setTyped(boolean isTyped) {
        this.isTyped = isTyped;
    }

    public List<Facet> getFacets() {
        return facets;
    }

    public void addFacet(Facet facet) {
        facets.add(facet);
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public Integer getMinOccurs() {
        return minOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    public void setNotNullAnnotated(boolean notNullAnnotated) {
        this.notNullAnnotated = notNullAnnotated;
    }

    public boolean isNotNullAnnotated() {
        return notNullAnnotated;
    }
}
