/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import java.net.URL;

import org.eclipse.persistence.jaxb.BeanValidationHelper;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ExternallyConstrainedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ExternallyConstrainedEmployee2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class contains BeanValidationHelper related tests.
 */
public class BeanValidationHelperTestCase {

    private static final String VALIDATION_XML = "META-INF/validation.xml";
    private static final String CONSTRAINTS_XML = "META-INF/validation/constraints.xml";
    private static final String CONSTRAINTS2_XML = "META-INF/validation/constraints2.xml";
    private static final String VALIDATION_XML_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/validation.xml";
    private static final String CONSTRAINTS_XML_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/constraints.xml";
    private static final String CONSTRAINTS2_XML_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/constraints2.xml";

    private ClassLoader classLoader;
    private Thread currentThread = Thread.currentThread();

    private static final class ValidationClassLoader extends ClassLoader {
        String validationXmlPath;

        ValidationClassLoader(ClassLoader parent, String validationXmlPath) {
            super(parent);
            this.validationXmlPath = validationXmlPath;
        }

        @Override
        public URL getResource(String name) {
            if (name != null) {
                switch (name) {
                    case VALIDATION_XML:
                        return getParent().getResource(this.validationXmlPath);
                    case CONSTRAINTS_XML:
                        return getParent().getResource(CONSTRAINTS_XML_PATH);
                    case CONSTRAINTS2_XML:
                        return getParent().getResource(CONSTRAINTS2_XML_PATH);
                }
            }
            return super.getResource(name);
        }
    }

/**
 * Tests that validation.xml parsing is not called if validation.xml doesn't exist.
 */
    @Test
    public void testValidationXmlNotExists() {
        try {
            classLoader = new BeanValidationHelperTestCase.ValidationClassLoader(currentThread.getContextClassLoader(), "NON_EXISTING_PATH");
            currentThread.setContextClassLoader(classLoader);

            BeanValidationHelper beanValidationHelper = new BeanValidationHelper();
            assertNotNull(beanValidationHelper.getConstraintsMap());
            assertTrue(beanValidationHelper.getConstraintsMap().size() == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            currentThread.setContextClassLoader(classLoader.getParent());
        }
    }

    /**
     * Tests that validation.xml parsing is called if validation.xml exist.
     */
    @Test
    public void testValidationXmlExists() {
        try {
            classLoader = new BeanValidationHelperTestCase.ValidationClassLoader(currentThread.getContextClassLoader(), VALIDATION_XML_PATH);
            currentThread.setContextClassLoader(classLoader);

            BeanValidationHelper beanValidationHelper = new BeanValidationHelper();
            assertNotNull(beanValidationHelper.getConstraintsMap());
            assertTrue(beanValidationHelper.getConstraintsMap().size() == 2);
            assertTrue(beanValidationHelper.getConstraintsMap().get(ExternallyConstrainedEmployee.class));
            assertTrue(beanValidationHelper.getConstraintsMap().get(ExternallyConstrainedEmployee2.class));
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            currentThread.setContextClassLoader(classLoader.getParent());
        }
    }
}
