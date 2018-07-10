/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Rick Barkhouse - 2.1 - Initial implementation
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.AnnotationProxy;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JStringLiteral;

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

    private static Field JANNOTATIONUSE_CLAZZ = null;
    private static Field JANNOTATIONUSE_MEMBERVALUES = null;
    private static Field JANNOTATIONARRAYMEMBER_VALUES = null;
    static {
        try {
            JANNOTATIONUSE_CLAZZ = PrivilegedAccessHelper.getDeclaredField(JAnnotationUse.class, "clazz", true);
            JANNOTATIONUSE_MEMBERVALUES = PrivilegedAccessHelper.getDeclaredField(JAnnotationUse.class, "memberValues", true);
            JANNOTATIONARRAYMEMBER_VALUES = PrivilegedAccessHelper.getDeclaredField(JAnnotationArrayMember.class, "values", true);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

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
            Map<String, Object> components = new HashMap<String, Object>();

            // First, get all the default values for this annotation class.
            Object xjcRefClass = PrivilegedAccessHelper.getValueFromField(JANNOTATIONUSE_CLAZZ, xjcAnnotation);
            // Cannot cache this field because JReferencedClass is a protected class.
            Field _classField = PrivilegedAccessHelper.getDeclaredField(xjcRefClass.getClass(), "_class", true);
            Class<Annotation> annotationClass = (Class<Annotation>) PrivilegedAccessHelper.getValueFromField(_classField, xjcRefClass);
            Method[] methods = annotationClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                components.put(methods[i].getName(), methods[i].getDefaultValue());
            }

            // Get the property values for this annotation instance.
            Map<Object, Object> memberValues = (Map<Object, Object>) PrivilegedAccessHelper.getValueFromField(JANNOTATIONUSE_MEMBERVALUES, xjcAnnotation);
            if (memberValues == null) {
                // Return an annotation with just the defaults set.
                return AnnotationProxy.getProxy(components, annotationClass, dynamicClassLoader, XMLConversionManager.getDefaultManager());
            }

            // Now overwrite the default values with anything we find in the XJC annotation instance.
            for (Object key : memberValues.keySet()) {
                JAnnotationValue xjcValue = (JAnnotationValue) memberValues.get(key);
                if (xjcValue instanceof JAnnotationArrayMember) {
                    List<Object> values = (List<Object>) PrivilegedAccessHelper.getValueFromField(JANNOTATIONARRAYMEMBER_VALUES, xjcValue);
                    Object[] valuesArray = new Object[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i) instanceof JAnnotationUse) {
                            JAnnotationUse xjcAnno = (JAnnotationUse) values.get(i);
                            XJCJavaAnnotationImpl anno = new XJCJavaAnnotationImpl(xjcAnno, dynamicClassLoader);
                            valuesArray[i] = anno.getJavaAnnotation();
                        } else {
                            Field valueField = PrivilegedAccessHelper.getDeclaredField(values.get(i).getClass(), "value", true);
                            Object value = PrivilegedAccessHelper.getValueFromField(valueField, values.get(i));
                            if (value instanceof JStringLiteral) {
                                JStringLiteral strvalue = (JStringLiteral) value;
                                valuesArray[i] = strvalue.str;
                            } else {
                                // XmlSeeAlso.value = Array of JDefinedClasses
                                Field valClField = PrivilegedAccessHelper.getDeclaredField(value.getClass(), "val$cl", true);
                                JDefinedClass wrappedValue = (JDefinedClass) PrivilegedAccessHelper.getValueFromField(valClField, value);
                                Class<?> tempDynClass = dynamicClassLoader.createDynamicClass(wrappedValue.fullName());
                                valuesArray[i] = tempDynClass;
                            }
                        }
                    }
                    components.put(key.toString(), valuesArray);
                } else if (xjcValue.getClass().getName().contains("JAnnotationStringValue")) {
                    // JAnnotationStringValue is a package-protected class so need to compare class name.
                    // Cannot cache this field because JAnnotationStringValue is a protected class.
                    Field valueField = PrivilegedAccessHelper.getDeclaredField(xjcValue.getClass(), "value", true);
                    Object objValue = PrivilegedAccessHelper.getValueFromField(valueField, xjcValue);
                    if (objValue instanceof JStringLiteral) {
                        JStringLiteral value = (JStringLiteral) objValue;
                        String stringValue = value.str;
                        components.put(key.toString(), stringValue);
                    } else if (objValue.getClass().getName().contains("JAtom")) {
                        // e.g. XmlElement.required = JAtom
                        // Cannot cache this field because JAtom is a protected class.
                        Field whatField = PrivilegedAccessHelper.getDeclaredField(objValue.getClass(), "what", true);
                        String value = (String) PrivilegedAccessHelper.getValueFromField(whatField, objValue);
                        components.put(key.toString(), value);
                    } else if (objValue.getClass().getName().contains("JExpr$1")) {
                        // XmlJavaTypeAdapter contains a JDefinedClass
                        Field valClField = PrivilegedAccessHelper.getDeclaredField(objValue.getClass(), "val$cl", true);
                        JClass wrappedValue = (JClass) PrivilegedAccessHelper.getValueFromField(valClField, objValue);
                        Object tempDynClass = null;

                        if (!(wrappedValue.getTypeParameters().isEmpty())) {
                            // Parameterized type, so get the actual parameter type and create that.
                            wrappedValue = wrappedValue.getTypeParameters().get(0);
                        }

                        try {
                            // Attempt to look up the class normally
                            tempDynClass = Class.forName(wrappedValue.fullName());
                        } catch (Exception e) {
                            if (annotationClass.equals(XmlEnum.class)) {
                                tempDynClass = String.class;
                            } else {
                                tempDynClass = dynamicClassLoader.createDynamicClass(wrappedValue.fullName());
                            }
                        }
                        components.put(key.toString(), tempDynClass);
                    }
                } else {
                    // e.g. XmlSchema.elementFormDefault = JAnnotationUse$1
                    // Cannot cache this field because JAtom is a protected class.
                    Field valValueField = PrivilegedAccessHelper.getDeclaredField(xjcValue.getClass(), "val$value", true);
                    Object value = PrivilegedAccessHelper.getValueFromField(valValueField, xjcValue);
                    components.put(key.toString(), value);
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
            Object xjcRefClass = PrivilegedAccessHelper.getValueFromField(JANNOTATIONUSE_CLAZZ, xjcAnnotation);
            // Cannot cache this field because JReferencedClass is a protected class.
            Field _classField = PrivilegedAccessHelper.getDeclaredField(xjcRefClass.getClass(), "_class", true);
            Class<?> annotationClass = (Class<?>) PrivilegedAccessHelper.getValueFromField(_classField, xjcRefClass);
            return annotationClass;
        } catch (Exception e) {
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
        try {
            Object xjcRefClass = PrivilegedAccessHelper.getValueFromField(JANNOTATIONUSE_CLAZZ, xjcAnnotation);
            Field _classField = PrivilegedAccessHelper.getDeclaredField(xjcRefClass.getClass(), "_class", true);
            Annotation annotationClass = (Annotation) PrivilegedAccessHelper.getValueFromField(_classField, xjcRefClass);
            return annotationClass.annotationType().getName();
        } catch (Exception e) {
            return null;
        }
    }

}
