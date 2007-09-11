/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.helper.HelperContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.sdo.SDOType;

/**
 * <p><b>Purpose</b>: A custom classloader used to dynamically create classes as needed.
 */
public class SDOClassLoader extends ClassLoader {
    private ClassLoader delegateLoader;
    private Map generatedClasses;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOClassLoader(ClassLoader delegateLoader, HelperContext aContext) {
        aHelperContext = aContext;
        this.delegateLoader = delegateLoader;
        generatedClasses = new HashMap();
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        Class javaClass = null;
        try {
            javaClass = delegateLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            javaClass = (Class)generatedClasses.get(className);
            if (javaClass != null) {
                return javaClass;
            }
            throw e;
        } catch (NoClassDefFoundError error) {
            javaClass = (Class)generatedClasses.get(className);
            if (javaClass == null) {
                throw error;
            }
        }
        return javaClass;
    }

    public Class loadClass(String className, SDOType type) throws ClassNotFoundException {
        Class javaClass = null;

        try {
            javaClass = delegateLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            javaClass = createGeneric(className, type);
            if (javaClass == null) {
                throw e;
            }
        } catch (NoClassDefFoundError error) {
            javaClass = createGeneric(className, type);
            if (javaClass == null) {
                throw error;
            }
        }

        return javaClass;
    }

    public Class createGeneric(String className, SDOType type) {
        Class javaClass = (Class)generatedClasses.get(className);
        if (javaClass != null) {
            return javaClass;
        }

        if (className == null) {
            return null;
        }

        DynamicClassWriter dcWriter = new DynamicClassWriter(className, type, aHelperContext);

        byte[] bytes = dcWriter.createClass();

        javaClass = defineClass(className, bytes, 0, bytes.length);
        generatedClasses.put(className, javaClass);
        return javaClass;
    }

    public void setDelegateLoader(ClassLoader delegateLoader) {
        this.delegateLoader = delegateLoader;
    }

    public ClassLoader getDelegateLoader() {
        return delegateLoader;
    }

    public URL getResource(String name) {
        return delegateLoader.getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        return delegateLoader.getResourceAsStream(name);
    }

    public Enumeration getResources(String name) throws IOException {
        return delegateLoader.getResources(name);
    }
}