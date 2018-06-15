/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.utility.Classpath;


/**
 * This repository holds all the classes found on the "system" classpath.
 */
final class SystemCLExternalClassRepository
    extends AbstractCLExternalClassRepository
{

    // singleton
    private static SystemCLExternalClassRepository INSTANCE;        // pseudo-final

    /**
     * Return the singleton.
     */
    public static synchronized SystemCLExternalClassRepository instance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemCLExternalClassRepository();
        }
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    private SystemCLExternalClassRepository() {
        super();
    }


    // ********** AbstractCLExternalClassRepository implementation **********

    /**
     * @see AbstractCLExternalClassRepository#buildClassDescriptions()
     */
    Map buildClassDescriptions() {
        Map classDescriptions = new HashMap(10000);    // this should be big enough for now (jdk1.4)...
        // first add all the primitives, since they do not show up on the "system" classpath
        this.addPrimitiveExternalClassDescriptionsTo(classDescriptions);
        // then add the "system" classes
        this.addClassDescriptionsFromClasspathTo(Classpath.completeClasspath(), classDescriptions);
        return classDescriptions;
    }

    /**
     * @see AbstractCLExternalClassRepository#buildClassLoader()
     */
    ClassLoader buildClassLoader() {
        return this.getClass().getClassLoader();
    }


    // ********** internal methods **********

    /**
     * Add all the "primitive" class descriptions to the specified map
     * of ExternalClassDescription objects.
     */
    private void addPrimitiveExternalClassDescriptionsTo(Map classDescriptions) {
        for (int i = PRIMITIVE_EXTERNAL_CLASS_DESCRIPTIONS.length; i-- > 0; ) {
            ExternalClassDescription externalClassDescription = PRIMITIVE_EXTERNAL_CLASS_DESCRIPTIONS[i];
            classDescriptions.put(externalClassDescription.getName(), externalClassDescription);
        }
    }

}
