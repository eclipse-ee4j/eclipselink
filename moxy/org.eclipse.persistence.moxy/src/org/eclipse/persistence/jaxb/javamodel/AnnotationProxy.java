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
 * dmccann - December 10/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * <p>
 * <b>Purpose:</b>The purpose of this class is to act as a dynamic proxy that
 * allows JDK Annotation method calls to be made on a non Annotation object.
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Create and return a dynamic proxy instance based on an Annotation class
 * and a <code>Map</code> of components (method name to value pairs)</li>
 * <li>Allow JDK Annotation method calls to be invoked on the proxy object</li>
 * </ul>
 * <p>
 * This class provides a means to invoke JDK Annotation method calls on a non
 * Annotation instance.
 *
 * @see java.lang.reflect.Proxy
 */
public class AnnotationProxy implements InvocationHandler {
    private Map<String, Object> components;
    private ConversionManager conversionMgr;
    private static final String ANNOTATION_TYPE_METHOD_NAME = "annotationType";

    /**
     * This constructor sets the <code>Map</code> of components (method name to
     * value pairs)and the ConversionManager to be used when converting values
     * in the <code>Map</code> based on the return type of the associated
     * <code>Method</code> Note that the preferred method of obtaining an
     * instance of this proxy class is via
     * <code>getProxy(Map, Class<A>, ClassLoader, ConversionManager)</code>
     *
     * @param components
     *            <code>Map</code> of method name to value pairs
     * @param conversionMgr
     *            <code>ConversionManager</code> instance for converting to the
     *            correct return type in the <code>invoke</code> method
     */
    private AnnotationProxy(Map<String, Object> components, ConversionManager conversionMgr) {
        this.components = components;
        this.conversionMgr = conversionMgr;
    }

    /**
     * This is the preferred way to obtain an instance of a dynamic proxy.
     *
     * The method takes a <code>ClassLoader</code> (which is used to load the
     * target <code>Annotation</code>), a <code>Class</code> (which indicates
     * the target <code>Annotation</code>, i.e.
     * <code>javax.xml.bind.annotation.XmlElement.class)</code>, and a
     * <code>Map</code> of method name to value pairs, which represent the
     * method names on the <code>Annotation</code> and the values that are to be
     * returned from each method call. For example, if this proxy is to be used
     * for an <code>@XmlElement</code>, the <code>Map</code> should contain the
     * following keys:
     *
     * <ul>
     * <li>defaultValue</li> <li>name</li> <li>namespace</li> <li>nillable</li>
     * <li>required</li> <li> type</li>
     * </ul>
     *
     * Following are example key/value pairs :
     *
     * <ul>
     * <li>"defaultValue", "##default"</li> <li>"name", "employee"</li> <li>
     * "namespace", "www.example.org"</li> <li>"nillable", false</li> <li>
     * "required", false</li> <li>"type",
     * javax.xml.bind.annotation.XmlElement.DEFAULT.class</li>
     * </ul>
     *
     * @param components
     *            <code>Map</code> of method name/value pairs for this proxy
     *            instance
     * @param annoClass
     *            The interface for the proxy class to implement
     * @param cl
     *            The <code>ClassLoader</code> to define the proxy class
     * @param conversionMgr
     *            <code>ConversionManager</code> instance for converting to the
     *            correct return type in the <code>invoke</code> method
     * @return A dynamic proxy instance based on a Java model JavaAnnotation
     */
    public static <A extends Annotation> A getProxy(Map<String, Object> components, Class<A> annoClass, ClassLoader cl, ConversionManager conversionMgr) {
        // add the 'annotationType' method name/value pair to the components map
        if (components == null) {
            components = new HashMap<String, Object>();
        }
        components.put(ANNOTATION_TYPE_METHOD_NAME, annoClass.getName());

        // Pass the classloader to the ConversionManager as well
        // conversionMgr.setLoader(cl);

        return (A) Proxy.newProxyInstance(cl, new Class[] { annoClass }, new AnnotationProxy(components, conversionMgr));
    }

    /**
     * Return the <code>Map</code> of method name/value pairs for this proxy
     * instance.
     *
     * @return <code>Map</code> of method name/value pairs for this proxy
     *         instance
     */
    public Map<String, Object> getComponents() {
        return this.components;
    }

    /**
     * Invoke a given <code>Method</code> on this proxy. The component
     * <code>Map</code> will be accessed using the given <code>Method</code>'s
     * name, and if an entry exists, the associated value is returned.
     *
     * @param proxy
     *            Satisfy the <code>InvocationHandler</code> interface - not
     *            used
     * @param method
     *            The <code>Method</code> instance corresponding to the
     *            interface method invoked on the proxy instance
     * @param args
     *            Satisfy the <code>InvocationHandler</code> interface - not
     *            used
     * @return The value from the method invocation on the proxy instance
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (components == null) {
            return null;
        }

        Class returnType = method.getReturnType();
        Object value = getComponents().get(method.getName());

        if (value == null && returnType == boolean.class) {
            return false;
        }
        if (value == null && returnType == Boolean.class) {
            return Boolean.FALSE;
        }

        if (returnType.isArray()) {
            return handleArrayData(returnType, value);
        }

        // use the ConversionManager to ensure that the correct type is returned
        return conversionMgr.convertObject(value, returnType);
    }

    private Object handleArrayData(Class returnType, Object value) {
        if (value == null) {
            return null;
        }

        Object[] data = (Object[]) value;
        Class componentType = returnType.getComponentType();
        Object[] convertedArray = (Object[]) Array.newInstance(componentType, data.length);

        for (int i = 0; i < data.length; i++) {
            convertedArray[i] = conversionMgr.convertObject(data[i], componentType);
        }

        return convertedArray;
    }

}