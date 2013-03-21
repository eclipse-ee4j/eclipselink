/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElements;
import org.eclipse.persistence.jaxb.xmlmodel.XmlInverseReference;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlValue;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaClass</code> implementation wrapping MOXy's <code>xmlmodel.JavaType</code>.
 * Used when bootstrapping a <code>DynamicJAXBContext</code> from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Provide Class information from the underlying <code>JavaType</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaClass
 * @see org.eclipse.persistence.jaxb.xmlmodel.JavaType
 */
public class OXMJavaClassImpl implements JavaClass {

    private JavaType javaType;
    private String javaName;
    private List<String> enumValues;
    private JavaModel javaModel;

    /**
     * Construct a new instance of <code>OXMJavaClassImpl</code>.
     *
     * @param aJavaType - the XJC <code>JavaType</code> to be wrapped.
     */
    public OXMJavaClassImpl(JavaType aJavaType) {
        this.javaType = aJavaType;
    }

    /**
     * Construct a new instance of <code>OXMJavaClassImpl</code>.
     *
     * @param aJavaTypeName - the name of the JavaType to create.
     */
    public OXMJavaClassImpl(String aJavaTypeName) {
        this.javaName = aJavaTypeName;
    }

    /**
     * Construct a new instance of <code>OXMJavaClassImpl</code>
     * representing a Java <code>enum</code>.
     *
     * @param aJavaTypeName - the name of the JavaType to create.
     * @param enumValues - the list of values for this <code>enum</code>.
     */
    public OXMJavaClassImpl(String aJavaTypeName, List<String> enumValues) {
        this.javaName = aJavaTypeName;
        this.enumValues = enumValues;
    }

    // ========================================================================

    /**
     * Return the "actual" type from a parameterized type.  For example, if this
     * <code>JavaClass</code> represents <code>List&lt;Employee</code>, this method will return the
     * <code>Employee</code> <code>JavaClass</code>.
     *
     * @return a <code>Collection</code> containing the actual type's <code>JavaClass</code>.
     */
    public Collection<JavaClass> getActualTypeArguments() {
        Object jType = null;
        if (this.javaType != null) {
            jType = this.javaType;
        } else {
            try {
                Class<?> jTypeClass = PrivilegedAccessHelper.getClassForName(this.javaName);
                jType = PrivilegedAccessHelper.newInstanceFromClass(jTypeClass);
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

    /**
     * If this <code>JavaClass</code> is an array type, return the type of the
     * array components.
     *
     * @return always returns <code>null</code>, as <code>JavaTypes</code> do not represent arrays.
     */
    public JavaClass getComponentType() {
        return null;
    }

    /**
     * Return the <code>JavaConstructor</code> for this <code>JavaClass</code> that has the
     * provided parameter types.
     *
     * @param parameterTypes the parameter list used to identify the constructor.
     *
     * @return the <code>JavaConstructor</code> with the signature matching parameterTypes.
     */
    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    /**
     * Return all of the <code>JavaConstructors</code> for this JavaClass.
     *
     * @return A <code>Collection</code> containing this <code>JavaClass'</code> <code>JavaConstructors</code>.
     */
    public Collection<JavaConstructor> getConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    /**
     * Return this <code>JavaClass'</code> inner classes.
     *
     * @return always returns an empty <code>ArrayList</code> as <code>JavaTypes</code> do not represent inner classes.
     */
    public Collection<JavaClass> getDeclaredClasses() {
        return new ArrayList<JavaClass>();
    }

    /**
     * Return the declared <code>JavaConstructor</code> for this <code>JavaClass</code>.
     *
     * @return the <code>JavaConstructor</code> for this <code>JavaClass</code>.
     */
    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    /**
     * Return all of the declared <code>JavaConstructors</code> for this <code>JavaClass</code>.
     *
     * @return A <code>Collection</code> containing this <code>JavaClass'</code> <code>JavaConstructors</code>.
     */
    public Collection<JavaConstructor> getDeclaredConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    /**
     * Return the declared <code>JavaField</code> for this <code>JavaClass</code>, identified
     * by <code>fieldName</code>.
     *
     * @param name the name of the <code>JavaField</code> to return.
     *
     * @return the <code>JavaField</code> named <code>fieldName</code> from this <code>JavaClass</code>.
     */
    public JavaField getDeclaredField(String name) {
        Collection<JavaField> allFields = getDeclaredFields();

        for (Iterator<JavaField> iterator = allFields.iterator(); iterator.hasNext();) {
            JavaField field = iterator.next();
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    /**
     * Return all of the declared <code>JavaFields</code> for this <code>JavaClass</code>.
     *
     * @return A <code>Collection</code> containing this <code>JavaClass'</code> <code>JavaFields</code>.
     */
    public Collection<JavaField> getDeclaredFields() {
        List<JavaField> fieldsToReturn = new ArrayList<JavaField>();

        if (this.enumValues != null) {
            for (Iterator<String> iterator = this.enumValues.iterator(); iterator.hasNext();) {
                fieldsToReturn.add(new OXMJavaFieldImpl(iterator.next(), JAVA_LANG_OBJECT, this));
            }
        } else {
            JavaAttributes javaAttributes = this.javaType.getJavaAttributes();
            if(null != javaAttributes) {
                List<JAXBElement<? extends JavaAttribute>> fields = javaAttributes.getJavaAttribute();
    
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
                        String fieldType = xmer.getType();
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
                        String fieldType = xmir.getType();
                        fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, fieldType, this));
                    }
                }
            }
        }

        return fieldsToReturn;
    }

    /**
     * Return the declared <code>JavaMethod</code> for this <code>JavaClass</code>,
     * identified by <code>name</code>, with the signature matching <code>args</code>.
     *
     * @param name the name of the <code>JavaMethod</code> to return.
     * @param args the parameter list used to identify the method.
     *
     * @return always returns <code>null</code>, as <code>JavaTypes</code> do not have methods.
     */
    public JavaMethod getDeclaredMethod(String name, JavaClass[] args) {
        return null;
    }

    /**
     * Return all of the declared <code>JavaMethods</code> for this <code>JavaClass</code>.
     *
     * @return always returns an empty <code>ArrayList</code>, as <code>JavaTypes</code> do not have methods.
     */
    public Collection<JavaMethod> getDeclaredMethods() {
        return new ArrayList<JavaMethod>();
    }

    /**
     * Return the <code>JavaMethod</code> for this <code>JavaClass</code>,
     * identified by <code>name</code>, with the signature matching <code>args</code>.
     *
     * @param name the name of the <code>JavaMethod</code> to return.
     * @param args the parameter list used to identify the method.
     *
     * @return always returns <code>null</code>, as <code>JavaTypes</code> do not have methods.
     */
    public JavaMethod getMethod(String name, JavaClass[] args) {
        return null;
    }

    /**
     * Return all of the <code>JavaMethods</code> for this <code>JavaClass</code>.
     *
     * @return always returns an empty <code>ArrayList</code>, as <code>JavaTypes</code> do not have methods.
     */
    public Collection<JavaMethod> getMethods() {
        return new ArrayList<JavaMethod>();
    }

    /**
     * Returns the Java language modifiers for this <code>JavaClass</code>, encoded in an integer.
     *
     * @return always returns <code>0</code> as <code>JavaTypes</code> do not have modifiers.
     *
     * @see java.lang.reflect.Modifier
     */
    public int getModifiers() {
        return 0;
    }

    /**
     * Returns the name of this <code>JavaClass</code>.
     *
     * @return the <code>String</code> name of this <code>JavaClass</code>.
     */
    public String getName() {
        if (this.javaType != null) {
            return this.javaType.getName();
        }
        return this.javaName;
    }

    /**
     * Returns the <code>JavaPackage</code> that this <code>JavaClass</code> belongs to.
     *
     * @return the <code>JavaPackage</code> of this <code>JavaClass</code>.
     */
    public JavaPackage getPackage() {
        return new OXMJavaPackageImpl(getPackageName());
    }

    /**
     * Returns the package name of this <code>JavaClass</code>.
     *
     * @return the <code>String</code> name of this <code>JavaClass'</code> <code>JavaPackage</code>.
     */
    public String getPackageName() {
        int lastDotIndex = getQualifiedName().lastIndexOf(DOT);
        if (lastDotIndex == -1) {
            return EMPTY_STRING;
        }

        return getQualifiedName().substring(0, lastDotIndex);
    }

    /**
     * Returns the fully-qualified name of this <code>JavaClass</code>.
     *
     * @return the <code>String</code> name of this <code>JavaClass</code>.
     */
    public String getQualifiedName() {
        return getName();
    }

    /**
     * Returns the raw name of this <code>JavaClass</code>.  Array types will
     * have "[]" appended to the name.
     *
     * @return the <code>String</code> raw name of this <code>JavaClass</code>.
     */
    public String getRawName() {
        return getName();
    }

    /**
     * Returns the super class of this <code>JavaClass</code>.
     *
     * @return <code>JavaClass</code> representing the super class of this <code>JavaClass</code>.
     */
    public JavaClass getSuperclass() {
        if (this.javaModel == null) {
            return null;
        }
        if (this.javaType != null) {
            if (!(this.javaType.getSuperType().equals(XMLProcessor.DEFAULT))) {
                return this.javaModel.getClass(javaType.getSuperType());
            }
        }
        return this.javaModel.getClass(JAVA_LANG_OBJECT);
    }

    public Type getGenericSuperclass() {
        return null;
    }

    /**
     * Indicates if this <code>JavaClass</code> has actual type arguments, i.e. is a
     * parameterized type (for example, <code>List&lt;Employee</code>).
     *
     * @return always returns <code>false</code> as <code>JavaTypes</code> are not parameterized.
     */
    public boolean hasActualTypeArguments() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is <code>abstract</code>.
     *
     * @return always returns <code>false</code> as <code>JavaTypes</code> are never <code>abstract</code>.
     */
    public boolean isAbstract() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is an <code>Annotation</code>.
     *
     * @return always returns <code>false</code> as <code>JavaTypes</code> are never <code>Annotations</code>.
     */
    public boolean isAnnotation() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is an Array type.
     *
     * @return always returns <code>false</code>, as <code>JavaTypes</code> do not represent arrays.
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is either the same as, or is a superclass of,
     * the <code>javaClass</code> argument.
     *
     * @param javaClass the <code>Class</code> to test.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is assignable from
     *         <code>javaClass</code>, otherwise <code>false</code>.
     *
     * @see java.lang.Class#isAssignableFrom(Class)
     */
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

    /**
     * Indicates if this <code>JavaClass</code> is an <code>enum</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is an <code>enum</code>, otherwise <code>false</code>.
     */
    public boolean isEnum() {
        return this.enumValues != null;
    }

    /**
     * Indicates if this <code>JavaClass</code> is <code>final</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is <code>final</code>, otherwise <code>false</code>.
     */
    public boolean isFinal() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is an <code>interface</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is an <code>interface</code>, otherwise <code>false</code>.
     */
    public boolean isInterface() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is an inner <code>Class</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is an inner </code>Class</code>, otherwise <code>false</code>.
     */
    public boolean isMemberClass() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> represents a primitive type.
     *
     * @return <code>true</code> if this <code>JavaClass</code> represents a primitive type, otherwise <code>false</code>.
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is <code>private</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is <code>private</code>, otherwise <code>false</code>.
     */
    public boolean isPrivate() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is <code>protected</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is <code>protected</code>, otherwise <code>false</code>.
     */
    public boolean isProtected() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is <code>public</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is <code>public</code>, otherwise <code>false</code>.
     */
    public boolean isPublic() {
        return false;
    }

    /**
     * Indicates if this <code>JavaClass</code> is <code>static</code>.
     *
     * @return <code>true</code> if this <code>JavaClass</code> is <code>static</code>, otherwise <code>false</code>.
     */
    public boolean isStatic() {
        return false;
    }

    /**
     * Not supported.
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    /**
     * If this <code>JavaClass</code> is annotated with an <code>Annotation</code> matching <code>aClass</code>,
     * return its <code>JavaAnnotation</code> representation.
     *
     * @param aClass a <code>JavaClass</code> representing the <code>Annotation</code> to look for.
     *
     * @return always returns <code>null</code>, as <code>JavaTypes</code> do not have <code>Annotations</code>.
     */
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        return null;
    }

    /**
     * Return all of the <code>Annotations</code> for this <code>JavaClass</code>.
     *
     * @return always returns an empty <code>ArrayList</code>, as <code>JavaTypes</code> do not have <code>Annotations</code>.
     */
    public Collection<JavaAnnotation> getAnnotations() {
        return new ArrayList<JavaAnnotation>();
    }

    /**
     * If this <code>JavaClass</code> declares an <code>Annotation</code> matching <code>aClass</code>,
     * return its <code>JavaAnnotation</code> representation.
     *
     * @param aClass a <code>JavaClass</code> representing the <code>Annotation</code> to look for.
     *
     * @return always returns <code>null</code>, as <code>JavaTypes</code> do not have <code>Annotations</code>.
     */
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    /**
     * Return all of the declared <code>Annotations</code> for this <code>JavaClass</code>.
     *
     * @return always returns an empty <code>ArrayList</code>, as <code>JavaTypes</code> do not have <code>Annotations</code>.
     */
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        return new ArrayList<JavaAnnotation>();
    }

    /**
     * Get this <code>JavaClass'</code> <code>JavaModel</code>.
     *
     * @return The <code>JavaModel</code> associated with this <code>JavaClass<code>.
     */
    public void setJavaModel(JavaModel model) {
        this.javaModel = model;
    }

    /**
     * Set this <code>JavaClass'</code> <code>JavaModel</code>.
     *
     * @param javaModel The <code>JavaModel</code> to set.
     */
    public JavaModel getJavaModel() {
        return this.javaModel;
    }

    // ========================================================================

    private static String EMPTY_STRING = "";
    private static String JAVA = "java";
    private static String DOT = ".";
    private static String JAVA_LANG_OBJECT = "java.lang.Object";
    private static String JAVA_UTIL_MAP = "java.util.Map";

}