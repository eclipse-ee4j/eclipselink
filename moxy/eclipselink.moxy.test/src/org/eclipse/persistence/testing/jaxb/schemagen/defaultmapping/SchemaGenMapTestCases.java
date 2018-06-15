/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Martin Vojtek - November 14/2014 - 2.6.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.defaultmapping;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

/**
 * Tests Map schema generation.
 *
 */
public class SchemaGenMapTestCases  extends SchemaGenTestCases {

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public SchemaGenMapTestCases(String name) throws Exception {
        super(name);
    }

    /**
     * Exception case - Schema for Map should be generated without any exception.
     */
    public void testMapSchemaGeneration() {

        Class[] loadedClasses = {MapTest.class};

        org.eclipse.persistence.jaxb.compiler.Generator generator = new Generator(new JavaModelInputImpl(loadedClasses, new JavaModelImpl(MapTest.class.getClassLoader())));
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        boolean exception = false;
        try {
            generator.generateSchemaFiles(outputResolver, null);
        } catch (Exception e) {
            exception = true;
        }
        assertFalse("Unexpected exception occured", exception);

    }
}
