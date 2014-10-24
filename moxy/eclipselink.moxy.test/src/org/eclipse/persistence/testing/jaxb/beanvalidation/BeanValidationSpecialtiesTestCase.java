/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import javax.validation.Validation;
import java.io.File;

/**
 * Test case storing non-standard tests, i.e. those that didn't fit neither in
 * {@link org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationRuntimeTestCase} nor in
 * {@link org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationBindingsTestCase}.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class BeanValidationSpecialtiesTestCase extends junit.framework.TestCase {

    private static final String SYSTEM_PROPERTY_JAXBCONTEXT = "javax.xml.bind.JAXBContext";
    private static final String MOXY_JAXBCONTEXT_FACTORY = JAXBContextFactory.class.getName();
    private static final boolean DEBUG = false;
    private static File DEACTIVATED_VALIDATION_XML;
    private static File ACTIVATED_VALIDATION_XML;

    static {
        try {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            //noinspection ConstantConditions
            DEACTIVATED_VALIDATION_XML =
                    new File(cl.getResource("META-INF/deactivated_validation.xml").toURI());
            ACTIVATED_VALIDATION_XML =
                    new File(DEACTIVATED_VALIDATION_XML.getCanonicalPath().replace("deactivated_", ""));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /**
     * Tests fix for endless invocation loop between
     * unmarshaller - validator - unmarshaller.
     * File {@link #DEACTIVATED_VALIDATION_XML} must be present on classpath.
     */
    public void testEndlessLoopInvocations() throws Exception{
        final String previous = System.getProperty(SYSTEM_PROPERTY_JAXBCONTEXT);
        try {
            System.setProperty(SYSTEM_PROPERTY_JAXBCONTEXT, MOXY_JAXBCONTEXT_FACTORY);
            assertEquals(MOXY_JAXBCONTEXT_FACTORY,
                    System.getProperty(SYSTEM_PROPERTY_JAXBCONTEXT));
            if (DEBUG) System.out.println("System property \"" + SYSTEM_PROPERTY_JAXBCONTEXT +
                    "\"'s value has been set to " + System.getProperty(SYSTEM_PROPERTY_JAXBCONTEXT));

            assertFalse(ACTIVATED_VALIDATION_XML.exists());
            boolean renamingSucceeded = DEACTIVATED_VALIDATION_XML.renameTo(ACTIVATED_VALIDATION_XML);
            assertTrue(renamingSucceeded);

            Validation.buildDefaultValidatorFactory();
        } finally {
            final String clearedProperty = previous != null ?
                    System.setProperty(SYSTEM_PROPERTY_JAXBCONTEXT, previous)
                    : System.clearProperty(SYSTEM_PROPERTY_JAXBCONTEXT);
            assertEquals(MOXY_JAXBCONTEXT_FACTORY, clearedProperty);
            if (DEBUG) System.out.println("System property \"" + SYSTEM_PROPERTY_JAXBCONTEXT
                    + "\"'s value has been cleared,"
                    + "unless it previously was set, in which case it's original value has been restored.");

            boolean restoringOriginalNameSucceeded = ACTIVATED_VALIDATION_XML.renameTo(DEACTIVATED_VALIDATION_XML);
            assertTrue(restoringOriginalNameSucceeded);
            assertFalse(ACTIVATED_VALIDATION_XML.exists());
        }
    }

}
