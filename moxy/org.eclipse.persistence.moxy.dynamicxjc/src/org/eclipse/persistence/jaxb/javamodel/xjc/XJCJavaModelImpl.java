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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaModel</code> implementation wrapping XJC's <code>JCodeModel</code>.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Return a <code>JavaClass</code> based on a <code>Class</code> or <code>Class</code> name.</li>
 *    <li>Return a Java <code>Annotation</code> for a given <code>JavaAnnotation</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModel
 */
public class XJCJavaModelImpl implements JavaModel {

    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;
    private Map<String, JavaClass> javaModelClasses = new HashMap<String, JavaClass>();

    /**
     * Construct a new instance of <code>XJCJavaModelImpl</code>.
     *
     * @param codeModel - the XJC <code>JCodeModel</code> to be wrapped.
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     */
    public XJCJavaModelImpl(JCodeModel codeModel, DynamicClassLoader loader) {
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
    }

    /**
     * Obtain the <code>JavaClass</code> given the corresponding Java <code>Class</code>.
     *
     * @param jClass - the Java <code>Class</code> to search for.
     *
     * @return the <code>JavaClass</code> corresponding to <code>jClass</code>.
     */
    public JavaClass getClass(Class<?> jClass) {
        if (jClass == null) {
            return null;
        }

        JavaClass cachedClass = this.javaModelClasses.get(jClass.getCanonicalName());
        if (cachedClass != null) {
            return cachedClass;
        }

        try {
            XJCJavaClassImpl jc = new XJCJavaClassImpl(jCodeModel._class(jClass.getCanonicalName()), jCodeModel, dynamicClassLoader);
            jc.setJavaModel(this);
            this.javaModelClasses.put(jClass.getCanonicalName(), jc);
            return jc;
        } catch (JClassAlreadyExistsException ex) {
            XJCJavaClassImpl jc = new XJCJavaClassImpl(jCodeModel._getClass(jClass.getCanonicalName()), jCodeModel, dynamicClassLoader);
            this.javaModelClasses.put(jClass.getCanonicalName(), jc);
            jc.setJavaModel(this);
            return jc;
        }
    }

    /**
     * Obtain the <code>JavaClass</code> given the corresponding Java <code>Class'</code> name.
     *
     * @param className - the name of the Java <code>Class</code> to search for.
     *
     * @return the <code>JavaClass</code> corresponding to <code>className</code>.
     */
    public JavaClass getClass(String className) {
        JavaClass cachedClass = this.javaModelClasses.get(className);
        if (cachedClass != null) {
            return cachedClass;
        }

        String componentName = className;
        boolean isArray = className.contains("[]");
        if (isArray) {
            componentName = className.replace("[]", "");
        }

        boolean isTyped = className.contains("<");
        if (isTyped) {
            // Only keep the generic part
            componentName = componentName.substring(0, className.indexOf('<'));
        }

        boolean isPrimitive = XMLConversionManager.getPrimitiveClass(componentName) != null;
        try {
            JavaClass jc = new XJCJavaClassImpl(jCodeModel._class(componentName), jCodeModel, dynamicClassLoader, isArray, isPrimitive);
            this.javaModelClasses.put(className, jc);
            return jc;
        } catch (JClassAlreadyExistsException ex) {
            JavaClass jc = new XJCJavaClassImpl(jCodeModel._getClass(componentName), jCodeModel, dynamicClassLoader, isArray, isPrimitive);
            this.javaModelClasses.put(className, jc);
            return jc;
        }
    }

    /**
     * Return a Java <code>Annotation</code> representation of the given <code>JavaAnnotation</code>.
     *
     * @param annotation - the <code>JavaAnnotation</code> to be converted.
     * @param jClass - the Java <code>Class</code> this annotation belogs to.
     *
     * @return a Java <code>Annotation</code> representation of the given <code>JavaAnnotation</code>.
     */
    public Annotation getAnnotation(JavaAnnotation annotation, Class<?> jClass) {
        return ((XJCJavaAnnotationImpl) annotation).getJavaAnnotation();
    }

    /**
     * Returns a <code>Map</code> of this <code>JavaModel's</code> <code>JavaClasses</code>, keyed on class name.
     *
     * @return this <code>JavaModel's</code> <code>Map</code> of <code>JavaClasses</code>.
     */
    public Map<String, JavaClass> getJavaModelClasses() {
        return javaModelClasses;
    }

    /**
     * Sets the <code>Map</code> of <code>JavaClasses</code> for this <code>JavaModel's</code>, keyed on class name.
     *
     * @param javaModelClasses - a <code>Map</code> of <code>JavaClasses</code>, keyed on class name.
     */
    public void setJavaModelClasses(Map<String, JavaClass> javaModelClasses) {
        this.javaModelClasses = javaModelClasses;
    }

    /**
     * Returns this <code>JavaModel's</code> <code>ClassLoader</code>.
     *
     * @return the <code>ClassLoader</code> used by this <code>JavaModel</code>.
     */
    public ClassLoader getClassLoader() {
        return this.dynamicClassLoader;
    }

}