/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Rick Barkhouse - 2.1 - Initial implementation
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlEnum;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.javamodel.AnnotationProxy;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationClassValue;
import com.sun.codemodel.JAnnotationStringValue;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaAnnotation</code> implementation wrapping XJC's <code>JAnnotationUse</code>.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 *    <li>Provide <code>Annotation</code> information from the underlying <code>JAnnotationUse</code>.</li>
 * </ul>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaAnnotation
 */
public class XJCJavaAnnotationImpl implements JavaAnnotation {

    private JAnnotationUse xjcAnnotation;
    private DynamicClassLoader dynamicClassLoader;

    /**
     * Construct a new instance of <code>XJCJavaAnnotationImpl</code>.
     *
     * @param annotation - the XJC <code>JAnnotationUse</code> to be wrapped.
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     */
    public XJCJavaAnnotationImpl(JAnnotationUse annotation, DynamicClassLoader loader) {
        this.xjcAnnotation = annotation;
        this.dynamicClassLoader = loader;
    }

    /**
     * Return a Java <code>Annotation</code> representation of this <code>JavaAnnotation</code>.
     *
     * @return a Java <code>Annotation</code> representation of this <code>JavaAnnotation</code>.
     */
    @SuppressWarnings("unchecked")
    public Annotation getJavaAnnotation() {
        try {
            Map<String, Object> components = new HashMap<>();

            // First, get all the default values for this annotation class.
            Class<Annotation> annotationClass = (Class<Annotation>) getJavaAnnotationClass();
            Method[] methods = annotationClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                components.put(methods[i].getName(), methods[i].getDefaultValue());
            }

            // Get the property values for this annotation instance.
            Map<String, JAnnotationValue> memberValues = xjcAnnotation.getAnnotationMembers();
            if (memberValues == null) {
                // Return an annotation with just the defaults set.
                return AnnotationProxy.getProxy(components, annotationClass, dynamicClassLoader, XMLConversionManager.getDefaultManager());
            }

            boolean isXmlEnum = annotationClass.equals(XmlEnum.class);
            // Now overwrite the default values with anything we find in the XJC annotation instance.
            for (String key : memberValues.keySet()) {
                JAnnotationValue xjcValue = memberValues.get(key);
                if (xjcValue instanceof JAnnotationArrayMember) {
                    Collection<JAnnotationValue> values = ((JAnnotationArrayMember) xjcValue).annotations2();
                    List<Object> valuesArray = new ArrayList<>(values.size());
                    for (JAnnotationValue val : values) {
                        if (val instanceof JAnnotationUse) {
                            JAnnotationUse xjcAnno = (JAnnotationUse) val;
                            XJCJavaAnnotationImpl anno = new XJCJavaAnnotationImpl(xjcAnno, dynamicClassLoader);
                            valuesArray.add(anno.getJavaAnnotation());
                        } else if (val instanceof JAnnotationStringValue) {
                            JAnnotationStringValue value = (JAnnotationStringValue) val;
                            valuesArray.add(value.toString());
                        } else if (val instanceof JAnnotationClassValue) {
                            JAnnotationClassValue cval = (JAnnotationClassValue) val;
                            valuesArray.add(getValueFromClsValue(cval, isXmlEnum));
                        } else {
                            throw new RuntimeException("got " + val.getClass().getName());
                        }
                    }
                    components.put(key, valuesArray.toArray(new Object[valuesArray.size()]));
                } else if (xjcValue instanceof JAnnotationStringValue) {
                    JAnnotationStringValue value = (JAnnotationStringValue) xjcValue;
                    components.put(key, value.toString());
                } else if (xjcValue instanceof JAnnotationClassValue) {
                    JAnnotationClassValue cval = (JAnnotationClassValue) xjcValue;
                    components.put(key, getValueFromClsValue(cval, isXmlEnum));
                } else {
                    throw new RuntimeException("got " + xjcValue.getClass().getName());
                }
            }

            return AnnotationProxy.getProxy(components, annotationClass, dynamicClassLoader, XMLConversionManager.getDefaultManager());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return the Java <code>Class</code> of the <code>Annotation</code> represented by this <code>JavaAnnotation</code>.
     *
     * @return the Java <code>Class</code> of this <code>JavaAnnotation's</code> <code>Annotation</code>.
     */
    public Class<?> getJavaAnnotationClass() {
        try {
            return Class.forName(getName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Not supported.
     */
    @Override
    public Map<Object, Object> getComponents() {
        throw new UnsupportedOperationException("getComponents");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getName() {
        return xjcAnnotation.getAnnotationClass().binaryName();
    }

    private Object getValueFromClsValue(JAnnotationClassValue value, boolean isXmlEnum) {
        JClass cls = value.type();
        for (JType param : cls.getTypeParameters()) {
            String name = param.boxify().fullName();
            getTempClass(name, isXmlEnum);
        }
        Class<?> tempDynClass = getTempClass(cls.fullName(), isXmlEnum);
        if (tempDynClass.isEnum() && !isXmlEnum) {
            return Enum.valueOf(tempDynClass.asSubclass(Enum.class), value.value());
        }
        return tempDynClass;
    }

    private Class<?> getTempClass(String name, boolean isXmlEnum) {
        Class<?> tempDynClass;
        try {
            // Attempt to look up the class normally
            tempDynClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            if (isXmlEnum) {
                tempDynClass = String.class;
            } else {
                tempDynClass = dynamicClassLoader.createDynamicClass(name);
            }
        }
        return tempDynClass;
    }
}
