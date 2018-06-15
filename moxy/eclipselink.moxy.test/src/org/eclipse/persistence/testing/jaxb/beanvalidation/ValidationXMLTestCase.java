/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.jaxb.ConstraintViolationWrapper;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ExternallyConstrainedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ExternallyConstrainedEmployee2;
import org.junit.After;
import org.junit.Before;

import javax.validation.Validation;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;

/**
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class ValidationXMLTestCase extends junit.framework.TestCase {

    private static final String NOT_NULL_MESSAGE = "{javax.validation.constraints.NotNull.message}";
    private static final String MIN_MESSAGE = "{javax.validation.constraints.Min.message}";
    private static final String MOXY_JAXBCONTEXT_FACTORY = JAXBContextFactory.class.getName();
    private static final String SYSTEM_PROPERTY_JAXBCONTEXT = "javax.xml.bind.JAXBContext";
    private static final String VALIDATION_XML = "META-INF/validation.xml";
    private static final String CONSTRAINTS_XML = "META-INF/validation/constraints.xml";
    private static final String CONSTRAINTS2_XML = "META-INF/validation/constraints2.xml";
    private static final String VALIDATION_XML_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/validation.xml";
    private static final String CONSTRAINTS_XML_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/constraints.xml";
    private static final String CONSTRAINTS2_XML_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/constraints2.xml";
    private static final boolean DEBUG = false;

    private ClassLoader classLoader;
    private Thread currentThread = Thread.currentThread();

    private static final class ValidationClassLoader extends ClassLoader {
        ValidationClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public URL getResource(String name) {
            if (name != null) {
                switch (name) {
                case VALIDATION_XML:
                    return getParent().getResource(VALIDATION_XML_PATH);
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
     * Tests fix for endless invocation loop between
     * unmarshaller - validator - unmarshaller.
     * File {@link #VALIDATION_XML} must be present on classpath.
     */
    public void testEndlessLoopInvocations() throws Exception {
        final String previous = System.getProperty(SYSTEM_PROPERTY_JAXBCONTEXT);
        try {
            System.setProperty(SYSTEM_PROPERTY_JAXBCONTEXT, MOXY_JAXBCONTEXT_FACTORY);
            assertEquals(MOXY_JAXBCONTEXT_FACTORY, System.getProperty(SYSTEM_PROPERTY_JAXBCONTEXT));

            if (DEBUG) System.out.println("System property \"" + SYSTEM_PROPERTY_JAXBCONTEXT +
                    "\"'s value has been set to " + System.getProperty(SYSTEM_PROPERTY_JAXBCONTEXT));

            Validation.buildDefaultValidatorFactory();
        } finally {
            final String clearedProperty = previous != null
                    ? System.setProperty(SYSTEM_PROPERTY_JAXBCONTEXT, previous)
                    : System.clearProperty(SYSTEM_PROPERTY_JAXBCONTEXT);

            assertEquals(MOXY_JAXBCONTEXT_FACTORY, clearedProperty);

            if (DEBUG) System.out.println("System property \"" + SYSTEM_PROPERTY_JAXBCONTEXT
                    + "\"'s value has been cleared,"
                    + "unless it previously was set, in which case it's original value has been restored.");
        }
    }

    /**
     * Tests that we do not skip validation on classes that are constrained through validation.xml.
     */
    public void testExternalConstraints() throws Exception {
        JAXBMarshaller marshaller = (JAXBMarshaller) JAXBContextFactory.createContext(new
                Class[]{ExternallyConstrainedEmployee.class, ExternallyConstrainedEmployee2.class},
                null).createMarshaller();
        ExternallyConstrainedEmployee employee = new ExternallyConstrainedEmployee().withId(null).withAge(15);
        ExternallyConstrainedEmployee2 employee2 = new ExternallyConstrainedEmployee2().withId(null).withAge(15);

        try {
            marshaller.marshal(employee, new StringWriter());
        } catch (BeanValidationException ignored) {
        }

        Set<ConstraintViolationWrapper<Object>> violations = marshaller.getConstraintViolations();

        try {
            marshaller.marshal(employee2, new StringWriter());
        } catch (BeanValidationException ignored) {
        }

        violations.addAll(marshaller.getConstraintViolations());

        assertFalse("Some constraints were not validated, even though they should have been.", violations.isEmpty());

        int i = 0;
        for (ConstraintViolationWrapper<Object> cv : violations) {
            if (NOT_NULL_MESSAGE.equals(cv.getMessageTemplate())) i += 0b1000;
            if (MIN_MESSAGE.equals(cv.getMessageTemplate())) i += 0b0001;
        }
        assertTrue(i == 0b1001);
    }

    @Before
    public void setUp() throws Exception {
        classLoader = new ValidationClassLoader(currentThread.getContextClassLoader());
        currentThread.setContextClassLoader(classLoader);
        resetBeanValidation();
    }

    private void resetBeanValidation() throws Exception {
        Field beanValidationHelper = JAXBContext.class.getDeclaredField("beanValidationHelper");
        Field beanValidationPresent = JAXBContext.class.getDeclaredField("beanValidationPresent");

        beanValidationHelper.setAccessible(true);
        beanValidationPresent.setAccessible(true);

        beanValidationHelper.set(JAXBContext.class, null);
        beanValidationPresent.set(JAXBContext.class, null);

        beanValidationHelper.setAccessible(false);
        beanValidationPresent.setAccessible(false);
    }

    @After
    public void tearDown() throws Exception {
        currentThread.setContextClassLoader(classLoader.getParent());
    }
}
