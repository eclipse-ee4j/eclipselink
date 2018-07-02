/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.helper.HelperContext;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.sdo.SDOType;

/**
 * <p><b>Purpose</b>: A custom classloader used to dynamically create classes as needed.
 */
public class SDOClassLoader extends ClassLoader {

    private Map<String, Class> generatedClasses;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOClassLoader(ClassLoader delegateLoader, HelperContext aContext) {
        super(delegateLoader);
        aHelperContext = aContext;
        generatedClasses = new HashMap();
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        Class javaClass = generatedClasses.get(className);
        if (javaClass != null) {
            return javaClass;
        }
        return getParent().loadClass(className);
    }

    public Class loadClass(String className, SDOType type) throws ClassNotFoundException {
        // To maximize performance, check the generated classes first
        Class javaClass = generatedClasses.get(className);
        if (javaClass != null) {
            return javaClass;
        }

        try {
            javaClass = getParent().loadClass(className);
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
        if(!type.isDataType() && null != aHelperContext && null != aHelperContext.getTypeHelper()) {
            ((SDOTypeHelper) aHelperContext.getTypeHelper()).getImplClassesToSDOType().put(javaClass, type);
        }
        return javaClass;
    }

    public Class createGeneric(String className, SDOType type) {
        Class javaClass = generatedClasses.get(className);
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

}
