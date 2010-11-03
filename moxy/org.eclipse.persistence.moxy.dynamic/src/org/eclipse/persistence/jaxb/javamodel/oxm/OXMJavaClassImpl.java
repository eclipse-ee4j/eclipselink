/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.compiler.XMLProcessor;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElements;
import org.eclipse.persistence.jaxb.xmlmodel.XmlInverseReference;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlValue;

public class OXMJavaClassImpl implements JavaClass {

    private JavaType javaType;
    private String javaName;
    private List<String> enumValues;
    private JavaModel javaModel;

    public OXMJavaClassImpl(JavaType aJavaType) {
        this.javaType = aJavaType;
    }

    public OXMJavaClassImpl(String aJavaTypeName) {
        this.javaName = aJavaTypeName;
    }

    public OXMJavaClassImpl(String aJavaTypeName, List<String> enumValues) {
        this.javaName = aJavaTypeName;
        this.enumValues = enumValues;
    }

    public Collection<JavaClass> getActualTypeArguments() {
        Object jType = null;
        if (this.javaType != null) {
            jType = this.javaType;
        } else {
            try {
                jType = Class.forName(this.javaName).newInstance();
            } catch (Exception e) {
                return new ArrayList<JavaClass>();
            }
        }

        ArrayList<JavaClass> argCollection = new ArrayList<JavaClass>();
        if (jType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) jType;
            Type[] params = pType.getActualTypeArguments();
            for (Type type : params) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    argCollection.add(this.javaModel.getClass(pt.getRawType().getClass()));
                } else if (type instanceof WildcardType) {
                    Type[] upperTypes = ((WildcardType) type).getUpperBounds();
                    if (upperTypes.length >0) {
                        Type upperType = upperTypes[0];
                        if (upperType instanceof Class<?>) {
                        	argCollection.add(this.javaModel.getClass(upperType.getClass()));
                        }
                    }
                } else if (type instanceof Class<?>) {
                    argCollection.add(this.javaModel.getClass(type.getClass()));
                } else if (type instanceof GenericArrayType) {
                    Class<?> genericTypeClass = (Class<?>) ((GenericArrayType) type).getGenericComponentType();
                    genericTypeClass = java.lang.reflect.Array.newInstance(genericTypeClass, 0).getClass();
                    argCollection.add(this.javaModel.getClass(genericTypeClass.getClass()));
                }
            }
        }
        return argCollection;
    }

    public JavaClass getComponentType() {
        throw new UnsupportedOperationException("getComponentType");
    }

    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    public Collection<JavaConstructor> getConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    public Collection<JavaClass> getDeclaredClasses() {
        return new ArrayList<JavaClass>();
    }

    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    public Collection<JavaConstructor> getDeclaredConstructors() {
        throw new UnsupportedOperationException("getDeclaredConstructors");
    }

    public JavaField getDeclaredField(String arg0) {
        Collection<JavaField> allFields = getDeclaredFields();

        for (Iterator<JavaField> iterator = allFields.iterator(); iterator.hasNext();) {
            JavaField field = iterator.next();
            if (field.getName().equals(arg0)) {
                return field;
            }
        }

        return null;
    }

    public Collection<JavaField> getDeclaredFields() {
        List<JavaField> fieldsToReturn = new ArrayList<JavaField>();

        if (this.enumValues != null) {
            for (Iterator<String> iterator = this.enumValues.iterator(); iterator.hasNext();) {
                fieldsToReturn.add(new OXMJavaFieldImpl(iterator.next(), JAVA_LANG_OBJECT, this));
            }
        } else {
            List<JAXBElement<? extends JavaAttribute>> fields = this.javaType.getJavaAttributes().getJavaAttribute();

            for (Iterator<JAXBElement<? extends JavaAttribute>> iterator = fields.iterator(); iterator.hasNext();) {
                JAXBElement<? extends JavaAttribute> jaxbElement = iterator.next();

                JavaAttribute att = (JavaAttribute) jaxbElement.getValue();

                if (att instanceof XmlElement) {
                    XmlElement xme = (XmlElement) att;
                    String fieldName = xme.getJavaAttribute();
                    String fieldType = xme.getType();
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlElements) {
                    XmlElements xmes = (XmlElements) att;
                    String fieldName = xmes.getJavaAttribute();
                    String fieldType = JAVA_LANG_OBJECT;
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlElementRef) {
                    XmlElementRef xmer = (XmlElementRef) att;
                    String fieldName = xmer.getJavaAttribute();
                    String fieldType = JAVA_UTIL_LIST;
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlAttribute) {
                    XmlAttribute xma = (XmlAttribute) att;
                    String fieldName = xma.getJavaAttribute();
                    String fieldType = xma.getType();
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlValue) {
                    XmlValue xmv = (XmlValue) att;
                    String fieldName = xmv.getJavaAttribute();
                    String fieldType = xmv.getType();
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlAnyElement) {
                    XmlAnyElement xmae = (XmlAnyElement) att;
                    String fieldName = xmae.getJavaAttribute();
                    String fieldType = JAVA_LANG_OBJECT;
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlAnyAttribute) {
                    XmlAnyAttribute xmaa = (XmlAnyAttribute) att;
                    String fieldName = xmaa.getJavaAttribute();
                    String fieldType = JAVA_UTIL_MAP;
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlJoinNodes) {
                    XmlJoinNodes xmjn = (XmlJoinNodes) att;
                    String fieldName = xmjn.getJavaAttribute();
                    String fieldType = xmjn.getType();
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                } else if (att instanceof XmlInverseReference) {
                    XmlInverseReference xmir = (XmlInverseReference) att;
                    String fieldName = xmir.getJavaAttribute();
                    // TODO: update after bug fix
                    String fieldType = null;
                    fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                }
            }
        }

        return fieldsToReturn;
    }

    public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1) {
        throw new UnsupportedOperationException("getDeclaredMethod");
    }

    public Collection<JavaMethod> getDeclaredMethods() {
        return new ArrayList<JavaMethod>();
    }

    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    public Collection<JavaMethod> getMethods() {
        return new ArrayList<JavaMethod>();
    }

    public int getModifiers() {
        return 0;
    }

    public String getName() {
        if (this.javaType != null) {
            return this.javaType.getName();
        }
        return this.javaName;
    }

    public JavaPackage getPackage() {
        return new OXMJavaPackageImpl(getPackageName());
    }

    public String getPackageName() {
        int lastDotIndex = getQualifiedName().lastIndexOf(DOT);
        if (lastDotIndex == -1) {
            return EMPTY_STRING;
        }

        return getQualifiedName().substring(0, lastDotIndex);
    }

    public String getQualifiedName() {
        return getName();
    }

    public String getRawName() {
        return getName();
    }

    public JavaClass getSuperclass() {
        if (this.javaType != null) {
            if (!(this.javaType.getSuperType().equals(XMLProcessor.DEFAULT))) {
                if (javaModel != null) {
                    return this.javaModel.getClass(javaType.getSuperType());
                }
                return this.javaModel.getClass(javaType.getSuperType());
            }
        }
        return this.javaModel.getClass(JAVA_LANG_OBJECT);
    }

    public boolean hasActualTypeArguments() {
        return false;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isAnnotation() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isAssignableFrom(JavaClass arg0) {
        String thisJavaName = EMPTY_STRING;
        String argJavaName = arg0.getName();

        if (this.javaName != null) {
            thisJavaName = this.javaName;
        } else {
            thisJavaName = this.javaType.getName();
        }

        if (thisJavaName.startsWith(JAVA) && argJavaName.startsWith(JAVA)) {
            // Only try class lookup if this is a JDK class, because
            // we won't ever find classes for dynamically generated types.
            try {
                Class thisClass = PrivilegedAccessHelper.getClassForName(thisJavaName);
                Class argClass = PrivilegedAccessHelper.getClassForName(argJavaName);
                return thisClass.isAssignableFrom(argClass);
            } catch (Exception e) {
                return false;
            }
        } else {
            return thisJavaName.equals(argJavaName);
        }
    }

    public boolean isEnum() {
        return this.enumValues != null;
    }

    public boolean isFinal() {
        return false;
    }

    public boolean isInterface() {
        return false;
    }

    public boolean isMemberClass() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }

    public boolean isPrivate() {
        return false;
    }

    public boolean isProtected() {
        return false;
    }

    public boolean isPublic() {
        return false;
    }

    public boolean isStatic() {
        return false;
    }

    public boolean isSynthetic() {
        return false;
    }

    public JavaAnnotation getAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection<JavaAnnotation> getAnnotations() {
        throw new UnsupportedOperationException("getAnnotations");
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

    public void setJavaModel(JavaModel model) {
        this.javaModel = model;
    }

    public JavaModel getJavaModel() {
        return this.javaModel;
    }

    // ========================================================================
    
    private static String EMPTY_STRING = "";
    private static String JAVA = "java";
    private static String DOT = ".";    
    private static String JAVA_LANG_OBJECT = "java.lang.Object";
    private static String JAVA_UTIL_LIST = "java.util.List";
    private static String JAVA_UTIL_MAP = "java.util.Map";
    
}