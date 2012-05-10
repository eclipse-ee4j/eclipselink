/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaField</code> implementation wrapping XJC's <code>JFieldVar</code>.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Provide <code>Field</code> information from the underlying <code>JFieldVar</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaField
 */
public class XJCJavaFieldImpl implements JavaField {

    private JFieldVar xjcField;
    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;
    private JavaClass owningClass;

    private static Field JVAR_ANNOTATIONS = null;
    private static Field JARRAYCLASS_COMPONENTTYPE = null;
    static {
        try {
            JVAR_ANNOTATIONS = PrivilegedAccessHelper.getDeclaredField(JVar.class, "annotations", true);
            Class<?> c = Class.forName("com.sun.codemodel.JArrayClass");
            JARRAYCLASS_COMPONENTTYPE = PrivilegedAccessHelper.getDeclaredField(c, "componentType", true);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

    /**
     * Construct a new instance of <code>XJCJavaFieldImpl</code>.
     *
     * @param javaField - the XJC <code>JFieldVar</code> to be wrapped.
     * @param codeModel - the XJC <code>JCodeModel</code> this field belongs to.
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     * @param owner - the <code>JavaClass</code> this field belongs to.
     */
    public XJCJavaFieldImpl(JFieldVar javaField, JCodeModel codeModel, DynamicClassLoader loader, JavaClass owner) {
        this.xjcField = javaField;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
        this.owningClass = owner;
    }

    /**
     * If this <code>JavaField</code> is annotated with an <code>Annotation</code> matching <code>aClass</code>,
     * return its <code>JavaAnnotation</code> representation.
     *
     * @param aClass a <code>JavaClass</code> representing the <code>Annotation</code> to look for.
     *
     * @return the <code>JavaAnnotation</code> represented by <code>aClass</code>, if one exists, otherwise return <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        if (aClass != null) {
            Collection<JAnnotationUse> annotations = null;

            try {
                annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JVAR_ANNOTATIONS, xjcField);
            } catch (Exception e) {
            }

            if (annotations == null) {
                return null;
            }

            for (JAnnotationUse annotationUse : annotations) {
                XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
                if (xjcAnnotation.getJavaAnnotationClass().getCanonicalName().equals(aClass.getQualifiedName())) {
                    return xjcAnnotation;
                }
            }
            // Didn't find annotation so return null
            return null;
        }
        // aClass was null so return null
        return null;
    }

    /**
     * Return all of the <code>Annotations</code> for this <code>JavaField</code>.
     *
     * @return A <code>Collection</code> containing this <code>JavaField's</code> <code>JavaAnnotations</code>.
     */
    @SuppressWarnings("unchecked")
    public Collection<JavaAnnotation> getAnnotations() {
        ArrayList<JavaAnnotation> annotationsList = new ArrayList<JavaAnnotation>();

        Collection<JAnnotationUse> annotations = null;

        try {
            annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JVAR_ANNOTATIONS, xjcField);
        } catch (Exception e) {
        }

        for (JAnnotationUse annotationUse : annotations) {
            XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
            annotationsList.add(xjcAnnotation);
        }
        return annotationsList;
    }

    /**
     * Returns the Java language modifiers for this <code>JavaField</code>, encoded in an integer.
     *
     * @return the <code>int</code> representing the modifiers for this field.
     *
     * @see java.lang.reflect.Modifier
     */
    public int getModifiers() {
        return xjcField.mods().getValue();
    }

    /**
     * Returns the name of this <code>JavaField</code>.
     *
     * @return the <code>String</code> name of this <code>JavaField</code>.
     */
    public String getName() {
        return xjcField.name();
    }

    /**
     * Returns the <code>JavaClass</code> representing the type of this <code>JavaField</code>.
     *
     * @return the type of this <code>JavaField</code> as a <code>JavaClass</code>.
     */
    @SuppressWarnings("unchecked")
    public JavaClass getResolvedType() {
        JType type = xjcField.type();
        JType basis = null;
        boolean isArray = false;
        boolean isPrimitive = false;

        try {
            // Check to see if this type has a 'basis' field.
            // This would indicate it is a "parameterized type" (e.g. List<Employee>).
            // Cannot cache this field because JNarrowedClass is a protected class.
            Field basisField = PrivilegedAccessHelper.getDeclaredField(type.getClass(), "basis", true);
            basis = (JClass) PrivilegedAccessHelper.getValueFromField(basisField, type);
        } catch (Exception e) {
            // "basis" field not found
        }

        JClass classToReturn = null;

        if (type.isPrimitive()) {
            JPrimitiveType pType = (JPrimitiveType) type;
            classToReturn = pType.boxify();
        } else if (type.getClass().getName().contains("JArrayClass")) {
            isArray = true;
            classToReturn = (JClass) type;
            try {
                JType componentType = (JType) PrivilegedAccessHelper.getValueFromField(JARRAYCLASS_COMPONENTTYPE, type);
                if (componentType.isPrimitive()) {
                    isPrimitive = true;
                }
            } catch (Exception e) {
                throw JAXBException.errorCreatingDynamicJAXBContext(e);
            }
        } else {
            try {
                classToReturn = jCodeModel._class(basis != null ? basis.fullName() : type.fullName());
            } catch (JClassAlreadyExistsException ex) {
                classToReturn = jCodeModel._getClass(basis != null ? basis.fullName() : type.fullName());
            }
        }

        if (basis != null) {
            try {
                // Cannot cache this field because JNarrowedClass is a protected class.
                Field argsField = PrivilegedAccessHelper.getDeclaredField(type.getClass(), "args", true);
                List<JClass> args = (List<JClass>) PrivilegedAccessHelper.getValueFromField(argsField, type);
                for (JClass jClass : args) {
                    ((JDefinedClass) classToReturn).generify("param", jClass);
                }
            } catch (Exception e) {
                throw JAXBException.errorCreatingDynamicJAXBContext(e);
            }
        }

        String className = classToReturn.fullName();
        if (isArray) {
            className += "[]";
        }

        if (((XJCJavaClassImpl) getOwningClass()).getJavaModel() != null) {
            return ((XJCJavaClassImpl) getOwningClass()).getJavaModel().getClass(className);
        }
        return new XJCJavaClassImpl((JDefinedClass) classToReturn, jCodeModel, dynamicClassLoader, isArray, isPrimitive);
    }

    /**
     * Indicates if this <code>JavaField</code> is <code>final</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is <code>final</code>, otherwise <code>false</code>.
     */
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    /**
     * Indicates if this <code>JavaField</code> is <code>abstract</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is <code>abstract</code>, otherwise <code>false</code>.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    /**
     * Indicates if this <code>JavaField</code> is <code>private</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is <code>private</code>, otherwise <code>false</code>.
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    /**
     * Indicates if this <code>JavaField</code> is <code>protected</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is <code>protected</code>, otherwise <code>false</code>.
     */
    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    /**
     * Indicates if this <code>JavaField</code> is <code>public</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is <code>public</code>, otherwise <code>false</code>.
     */
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    /**
     * Indicates if this <code>JavaField</code> is <code>static</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is <code>static</code>, otherwise <code>false</code>.
     */
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    /**
     * Not supported.
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    /**
     * Indicates if this <code>JavaField</code> is an <code>enum</code> constant - i.e. its owner is an <code>enum</code>.
     *
     * @return <code>true</code> if this <code>JavaField</code> is an <code>enum</code> constant.
     */
    public boolean isEnumConstant() {
        return getOwningClass().isEnum();
    }

    /**
     * If this <code>JavaField</code> is annotated with an <code>Annotation</code> matching <code>aClass</code>,
     * return its <code>JavaAnnotation</code> representation.
     *
     * @param aClass a <code>JavaClass</code> representing the <code>Annotation</code> to look for.
     *
     * @return the <code>JavaAnnotation</code> represented by <code>aClass</code>, if one exists, otherwise return <code>null</code>.
     */
    public JavaAnnotation getDeclaredAnnotation(JavaClass aClass) {
        return getAnnotation(aClass);
    }

    /**
     * Return all of the <code>Annotations</code> for this <code>JavaField</code>.
     *
     * @return A <code>Collection</code> containing this <code>JavaField's</code> <code>JavaAnnotations</code>.
     */
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        return getAnnotations();
    }

    /**
     * Set the <code>JavaClass</code> which contains this field.
     *
     * @param owningClass the <code>JavaClass</code> representing the owner of this <code>JavaField</code>.
     */
    public void setOwningClass(JavaClass owningClass) {
        this.owningClass = owningClass;
    }

    /**
     * Returns the <code>JavaClass</code> which contains this field.
     *
     * @return <code>JavaClass</code> representing the owner of this <code>JavaField</code>.
     */
    public JavaClass getOwningClass() {
        return this.owningClass;
    }
}