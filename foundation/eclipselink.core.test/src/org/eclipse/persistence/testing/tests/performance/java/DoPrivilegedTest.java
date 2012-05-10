/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.java;

import java.lang.reflect.*;
import java.security.AccessController;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedGetContextClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredField;
import org.eclipse.persistence.internal.security.PrivilegedGetFields;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedGetMethodParameterTypes;
import org.eclipse.persistence.internal.security.PrivilegedGetMethodReturnType;
import org.eclipse.persistence.internal.security.PrivilegedGetValueFromField;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.security.PrivilegedSetValueInField;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance for
 * 1. Direct calls - Baseline
 * 2. doPrivileged actions in org.eclipse.persistence.internal.security.PriviledgedAccessController
 * 3. Bypassing doPrivileged actions in org.eclipse.persistence.internal.security.PriviledgedAccessController
 */
public class DoPrivilegedTest extends PerformanceComparisonTestCase {
    public DoPrivilegedTest() {
        setName("Direct calls vs. doPrivileged vs. Bypassing doPrivileged PerformanceComparisonTest");
        setDescription("This test compares the performance for doPrivileged.");

        // check TopLink and OC4j doPrivileged flag.
        String usePrivileged = System.getProperty("eclipselink.security.usedoprivileged");
        if ((System.getSecurityManager() == null) || ((usePrivileged == null) || usePrivileged.equalsIgnoreCase("false"))) {
            addBypassDoPrivilegedTest();
        } else {
            addDoPrivilegedTest();
        }
    }

    /**
     * Direct calls
     */
    public void test() throws Exception {
        String className = "org.eclipse.persistence.Version";
        String fieldName = "buildNumber";
        String fieldValue = "TopLink Blah";
        String methodName = "setProduct";

        Class clazz = null;
        Version version = null;
        Method method = null;
        try {
            clazz = Class.forName(className);
            Class[] methodParameterTypes = { Class.forName("java.lang.String") };
            ClassLoader clazzloader = clazz.getClassLoader();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            Class.forName(className, true, clazzloader);
            version = (Version)clazz.newInstance();

            Field[] fields = clazz.getFields();
            Field field = getDeclaredField(clazz, fieldName, true);
            try {
                int intValueFromField = field.getInt(version);
            } catch (Exception e) {
            }
            field.get(version);
            try {
                field.set(version, fieldValue);
            } catch (Exception e) {
            }

            String lineSeparator = System.getProperty("file.separator");

            method = getDeclaredMethod(clazz, methodName, methodParameterTypes, true);
            method.getParameterTypes();
            method.getReturnType();
            Object[] parameters = { "TopLink Blah." };
            method.invoke(version, parameters);

            Constructor constructor = clazz.getConstructor((Class[])null);
            constructor.setAccessible(true);
            Constructor declaredConstructor = clazz.getDeclaredConstructor((Class[])null);
            declaredConstructor.setAccessible(true);
            constructor.newInstance((Object[])null);

            //					new XMLSessionConfigLoader().loadInternal(new SessionManager(), clazzloader, false, false);
        } catch (Exception e) {
            throw (new TestProblemException("An exception has been caught."));
        }
    }

    /**
     * doPrivileged
     */
    public void addDoPrivilegedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                testDoPrivileged();
            }
        };
        test.setName("DoPrivilegedTest");
        test.setAllowableDecrease(-35);
        addTest(test);
    }

    /**
     * Bypassing doPrivileged
     */
    public void addBypassDoPrivilegedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                testDoPrivileged();
            }
        };
        test.setName("BypassDoPrivilegedTest");
        test.setAllowableDecrease(-10);
        addTest(test);
    }

    public void testDoPrivileged() {
        String className = "org.eclipse.persistence.Version";
        String fieldName = "product";
        String fieldValue = "TopLink Blah";
        String methodName = "setProduct";

        Class clazz = null;
        Version version = null;
        Method method = null;
        try {
            clazz = (Class)AccessController.doPrivileged(new PrivilegedClassForName(className));
            Class[] methodParameterTypes = { (Class)AccessController.doPrivileged(new PrivilegedClassForName("java.lang.String")) };
            ClassLoader clazzloader = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(clazz));
            ClassLoader classloader = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetContextClassLoader(Thread.currentThread()));
            AccessController.doPrivileged(new PrivilegedClassForName(className, true, clazzloader));
            version = (Version)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(clazz));

            Field[] fields = (Field[])AccessController.doPrivileged(new PrivilegedGetFields(clazz));
            Field field = (Field)AccessController.doPrivileged(new PrivilegedGetDeclaredField(clazz, fieldName, true));
            try {
                int intValueFromField = ((Integer)AccessController.doPrivileged(new PrivilegedGetValueFromField(field, version))).intValue();
            } catch (Exception e) {
            }
            AccessController.doPrivileged(new PrivilegedGetValueFromField(field, version));
            try {
                AccessController.doPrivileged(new PrivilegedSetValueInField(field, version, fieldValue));
            } catch (Exception e) {
            }

            String lineSeparator = PrivilegedAccessHelper.getLineSeparator();

            method = AccessController.doPrivileged(new PrivilegedGetMethod(clazz, methodName, methodParameterTypes, true));
            AccessController.doPrivileged(new PrivilegedGetMethodParameterTypes(method));
            AccessController.doPrivileged(new PrivilegedGetMethodReturnType(method));
            Object[] parameters = { "TopLink Blah." };
            AccessController.doPrivileged(new PrivilegedMethodInvoker(method, version, parameters));

            Constructor constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(clazz, null, true));
            Constructor declaredConstructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetDeclaredConstructorFor(clazz, null, true));
            AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, null));

            //					PrivilegedAccessController.loadDeploymentXML(new XMLSessionConfigLoader(),new SessionManager(), clazzloader, false, false);
        } catch (Exception e) {
            throw (new TestProblemException("An exception has been caught."));
        }
    }

    public Field getDeclaredField(final Class javaClass, final String fieldName, final boolean shouldSetAccessible) throws NoSuchFieldException {
        Field field = findDeclaredField(javaClass, fieldName);
        if (shouldSetAccessible) {
            field.setAccessible(true);
        }
        return field;
    }

    public Method getDeclaredMethod(final Class javaClass, final String methodName, final Class[] methodParameterTypes, final boolean shouldSetAccessible) throws NoSuchMethodException {
        Method method = findDeclaredMethod(javaClass, methodName, methodParameterTypes);
        if (shouldSetAccessible) {
            method.setAccessible(true);
        }
        return method;
    }

    /**
     * Finding a field within a class potentially has to navigate through it's superclasses to eventually
     * find the field.  This method is called by the public getDeclaredField() method and does a recursive
     * search for the named field in the given classes or it's superclasses.
     */
    private Field findDeclaredField(Class javaClass, String fieldName) throws NoSuchFieldException {
        try {
            return javaClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            Class superclass = javaClass.getSuperclass();
            if (superclass == null) {
                throw ex;
            } else {
                return findDeclaredField(superclass, fieldName);
            }
        }
    }

    /**
     * Finding a method within a class potentially has to navigate through it's superclasses to eventually
     * find the method.  This method is called by the public getDeclaredMethod() method and does a recursive
     * search for the named method in the given classes or it's superclasses.
     */
    private Method findDeclaredMethod(Class javaClass, String methodName, Class[] methodParameterTypes) throws NoSuchMethodException {
        try {
            return javaClass.getDeclaredMethod(methodName, methodParameterTypes);
        } catch (NoSuchMethodException ex) {
            Class superclass = javaClass.getSuperclass();
            if (superclass == null) {
                throw ex;
            } else {
                return findDeclaredMethod(superclass, methodName, methodParameterTypes);
            }
        }
    }
}
