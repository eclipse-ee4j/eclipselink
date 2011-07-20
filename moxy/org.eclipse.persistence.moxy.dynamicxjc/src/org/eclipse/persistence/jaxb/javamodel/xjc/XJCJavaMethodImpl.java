/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaMethod</code> implementation wrapping XJC's <code>JMethod</code>.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Provide <code>Method</code> information from the underlying <code>JMethod</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaMethod
 */
public class XJCJavaMethodImpl implements JavaMethod {

    private JMethod xjcMethod;
    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;
    private JavaClass owningClass;

    private static Field JMETHOD_ANNOTATIONS = null;
    static {
        try {
            JMETHOD_ANNOTATIONS = PrivilegedAccessHelper.getDeclaredField(JMethod.class, "annotations", true);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

    /**
     * Construct a new instance of <code>XJCJavaMethodImpl</code>.
     * 
     * @param javaMethod - the XJC <code>JMethod</code> to be wrapped.
     * @param codeModel - the XJC <code>JCodeModel</code> this field belongs to.
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     * @param owner - the <code>JavaClass</code> this field belongs to.
     */
    public XJCJavaMethodImpl(JMethod javaMethod, JCodeModel codeModel, DynamicClassLoader loader, JavaClass owner) {
        this.xjcMethod = javaMethod;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
        this.owningClass = owner;
    }

    /**
     * If this <code>JavaMethod</code> is annotated with an <code>Annotation</code> matching <code>aClass</code>,
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
                annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JMETHOD_ANNOTATIONS, xjcMethod);
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
     * Return all of the <code>Annotations</code> for this <code>JavaMethod</code>.
     *  
     * @return A <code>Collection</code> containing this <code>JavaMethod's</code> <code>JavaAnnotations</code>.
     */
    @SuppressWarnings("unchecked")
    public Collection<JavaAnnotation> getAnnotations() {
        ArrayList<JavaAnnotation> annotationsList = new ArrayList<JavaAnnotation>();

        Collection<JAnnotationUse> annotations = null;

        try {
            annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JMETHOD_ANNOTATIONS, xjcMethod);
        } catch (Exception e) {
        }

        if (annotations == null) {
            return annotationsList;
        }

        for (JAnnotationUse annotationUse : annotations) {
            XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
            annotationsList.add(xjcAnnotation);
        }
        return annotationsList;
    }

    /**
     * Returns the name of this <code>JavaMethod</code>.
     *  
     * @return the <code>String</code> name of this <code>JavaMethod</code>.
     */
    public String getName() {
        return xjcMethod.name();
    }

    /**
     * Returns the array of parameters for this <code>JavaMethod</code>.
     *  
     * @return a <code>JavaClass[]</code> representing the argument types for this method.
     */
    public JavaClass[] getParameterTypes() {
        JType[] params = xjcMethod.listParamTypes();
        JavaClass[] paramArray = new JavaClass[params.length];

        for (int i = 0; i < params.length; i++) {
            if (((XJCJavaClassImpl) getOwningClass()).getJavaModel() != null) {
                paramArray[i] = ((XJCJavaClassImpl) getOwningClass()).getJavaModel().getClass(params[i].fullName());
            } else {
                paramArray[i] = new XJCJavaClassImpl((JDefinedClass) params[i], jCodeModel, dynamicClassLoader);
            }
        }
        return paramArray;
    }

    /**
     * Returns this <code>JavaMethod's</code> return type.
     *  
     * @return a <code>JavaClass</code> representing the return type of this method.
     */
    public JavaClass getResolvedType() {
        if (((XJCJavaClassImpl) getOwningClass()).getJavaModel() != null) {
            return ((XJCJavaClassImpl) getOwningClass()).getJavaModel().getClass(xjcMethod.type().fullName());
        }

        try {
            return new XJCJavaClassImpl(jCodeModel._class(xjcMethod.type().fullName()), jCodeModel, dynamicClassLoader);
        } catch (JClassAlreadyExistsException ex) {
            return new XJCJavaClassImpl(jCodeModel._getClass(xjcMethod.type().fullName()), jCodeModel, dynamicClassLoader);
        }
    }

    /**
     * Returns this <code>JavaMethod's</code> return type.
     *  
     * @return a <code>JavaClass</code> representing the return type of this method.
     */
    @SuppressWarnings("unchecked")
    public JavaClass getReturnType() {
        JType type = xjcMethod.type();

        try {
            Field argsField = PrivilegedAccessHelper.getDeclaredField(type.getClass(), "args", true);
            List<JClass> args = (List<JClass>) PrivilegedAccessHelper.getValueFromField(argsField, type);
            type = args.get(0);
        } catch (Exception e) {
        }

        if (((XJCJavaClassImpl) getOwningClass()).getJavaModel() != null) {
            return ((XJCJavaClassImpl) getOwningClass()).getJavaModel().getClass(type.fullName());
        }

        try {
            return new XJCJavaClassImpl(jCodeModel._class(type.fullName()), jCodeModel, dynamicClassLoader);
        } catch (JClassAlreadyExistsException ex) {
            return new XJCJavaClassImpl(jCodeModel._getClass(type.fullName()), jCodeModel, dynamicClassLoader);
        }
    }

    /**
     * Indicates if this <code>JavaMethod</code> has actual type arguments, i.e. is a
     * parameterized type (for example, <code>List&lt;Employee</code>).
     * 
     * @return <code>true</code> if this <code>JavaClass</code> is parameterized, otherwise <code>false</code>.
     */
    public boolean hasActualTypeArguments() {
        try {
            JavaClass[] allParams = getParameterTypes();

            for (JavaClass type : allParams) {
                Class<?> paramClass = Class.forName(type.getPackageName() + "." + type.getName());
                if (paramClass.newInstance() instanceof ParameterizedType) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Not supported. 
     */
    public Collection<Object> getActualTypeArguments() {
        throw new UnsupportedOperationException("getActualTypeArguments");
    }

    /**
     * Returns the Java language modifiers for this <code>JavaMethod</code>, encoded in an integer.
     *  
     * @return the <code>int</code> representing the modifiers for this method.
     * 
     * @see java.lang.reflect.Modifier
     */
    public int getModifiers() {
        return xjcMethod.mods().getValue();
    }

    /**
     * Indicates if this <code>JavaMethod</code> is <code>abstract</code>.
     * 
     * @return <code>true</code> if this <code>JavaMethod</code> is <code>abstract</code>, otherwise <code>false</code>.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    /**
     * Indicates if this <code>JavaMethod</code> is <code>private</code>.
     * 
     * @return <code>true</code> if this <code>JavaMethod</code> is <code>private</code>, otherwise <code>false</code>.
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    /**
     * Indicates if this <code>JavaMethod</code> is <code>protected</code>.
     * 
     * @return <code>true</code> if this <code>JavaMethod</code> is <code>protected</code>, otherwise <code>false</code>.
     */
    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    /**
     * Indicates if this <code>JavaMethod</code> is <code>public</code>.
     * 
     * @return <code>true</code> if this <code>JavaMethod</code> is <code>public</code>, otherwise <code>false</code>.
     */
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    /**
     * Indicates if this <code>JavaMethod</code> is <code>static</code>.
     * 
     * @return <code>true</code> if this <code>JavaMethod</code> is <code>static</code>, otherwise <code>false</code>.
     */
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    /**
     * Indicates if this <code>JavaMethod</code> is <code>final</code>.
     * 
     * @return <code>true</code> if this <code>JavaMethod</code> is <code>final</code>, otherwise <code>false</code>.
     */
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    /**
     * Not supported. 
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    /**
     * Not supported. 
     */
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    /**
     * Not supported. 
     */
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

    /**
     * Returns the <code>JavaClass</code> which contains this method.
     *  
     * @return <code>JavaClass</code> representing the owner of this <code>JavaMethod</code>.
     */
    public JavaClass getOwningClass() {
        return owningClass;
    }

    /**
     * Set the <code>JavaClass</code> which contains this method.
     *  
     * @param owningClass the <code>JavaClass</code> representing the owner of this <code>JavaMethod</code>.
     */
    public void setOwningClass(JavaClass owningClass) {
        this.owningClass = owningClass;
    }

}