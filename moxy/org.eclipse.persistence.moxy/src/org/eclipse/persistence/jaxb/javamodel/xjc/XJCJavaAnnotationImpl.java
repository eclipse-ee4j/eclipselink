/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-12-18 13:04:58 - EclipseLink 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.javamodel.AnnotationProxy;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JStringLiteral;

public class XJCJavaAnnotationImpl implements JavaAnnotation {

    JAnnotationUse xjcAnnotation;
    DynamicClassLoader dynamicClassLoader;

    public XJCJavaAnnotationImpl(JAnnotationUse annotation, DynamicClassLoader loader) {
        this.xjcAnnotation = annotation;
        this.dynamicClassLoader = loader;
    }

    public Annotation getJavaAnnotation() {
        try {
            Map components = new HashMap<String, Object>();

            // First, get all the default values for this annotation class.
            Object xjcRefClass = XJCJavaModelHelper.getFieldValueByReflection(xjcAnnotation, "clazz");
            Class annotationClass = (Class) XJCJavaModelHelper.getFieldValueByReflection(xjcRefClass, "_class");
            Method[] methods = annotationClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                components.put(methods[i].getName(), methods[i].getDefaultValue());
            }

            // Get the property values for this annotation instance.
            Map memberValues = (Map) XJCJavaModelHelper.getFieldValueByReflection(xjcAnnotation, "memberValues");
            if (memberValues == null) {
                // Return an annotation with just the defaults set.
                return AnnotationProxy.getProxy(components, annotationClass, dynamicClassLoader, XMLConversionManager.getDefaultManager());
            }

            // Now overwrite the default values with anything we find in the XJC annotation instance.
            for (Object key : memberValues.keySet()) {
                JAnnotationValue xjcValue = (JAnnotationValue) memberValues.get(key);
                if (xjcValue instanceof JAnnotationArrayMember) {
                    List<JAnnotationArrayMember> values =
                        (List<JAnnotationArrayMember>) XJCJavaModelHelper.getFieldValueByReflection(xjcValue, "values");
                    Object[] valuesArray = new Object[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        Object value = XJCJavaModelHelper.getFieldValueByReflection(values.get(i), "value");
                        if (value instanceof JStringLiteral) {
                            JStringLiteral strvalue = (JStringLiteral) value;
                            valuesArray[i] = strvalue.str;
                        } else {
                        	// XmlSeeAlso.value = Array of JDefinedClasses
                            JDefinedClass wrappedValue = (JDefinedClass) XJCJavaModelHelper.getFieldValueByReflection(value, "val$cl");
                            Class tempDynClass = dynamicClassLoader.createDynamicClass(wrappedValue.fullName());
                            valuesArray[i] = tempDynClass;
                        }
                    }
                    components.put(key, valuesArray);
                } else if (xjcValue.getClass().getName().contains("JAnnotationStringValue")) {
                    // JAnnotationStringValue is a package-protected class so need to compare class name.
                    Object objValue = XJCJavaModelHelper.getFieldValueByReflection(xjcValue, "value");
                    if (objValue instanceof JStringLiteral) {
                        JStringLiteral value = (JStringLiteral) objValue;
                        String stringValue = value == null ? null : value.str;
                        components.put(key, stringValue);
                    }
                    if (objValue.getClass().getName().contains("JAtom")) {
                        // e.g. XmlElement.required = JAtom
                        String value = (String) XJCJavaModelHelper.getFieldValueByReflection(objValue, "what");
                        components.put(key, value);
                    }
                } else {
                    // e.g. XmlSchema.elementFormDefault = JAnnotationUse$1
                    Object value = XJCJavaModelHelper.getFieldValueByReflection(xjcValue, "val$value");
                    components.put(key, value);
                }
            }

            return AnnotationProxy.getProxy(components, annotationClass, dynamicClassLoader, XMLConversionManager.getDefaultManager());
        } catch (Exception e) {
            return null;
        }
    }

    public Class getJavaAnnotationClass() {
        try {
            Object xjcRefClass = XJCJavaModelHelper.getFieldValueByReflection(xjcAnnotation, "clazz");
            Class annotationClass = (Class) XJCJavaModelHelper.getFieldValueByReflection(xjcRefClass, "_class");

            return annotationClass;
        } catch (Exception e) {
            return null;
        }
    }

    public Map getComponents() {
        throw new UnsupportedOperationException("getComponents");
    }

}