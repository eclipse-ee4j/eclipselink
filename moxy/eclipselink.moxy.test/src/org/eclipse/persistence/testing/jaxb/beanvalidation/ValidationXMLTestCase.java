/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.internal.cache.AdvancedProcessor;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ExternallyConstrainedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ExternallyConstrainedEmployee2;
import org.junit.After;
import org.junit.Before;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class ValidationXMLTestCase extends junit.framework.TestCase {

    private static final String NOT_NULL_MESSAGE = "{javax.validation.constraints.NotNull.message}";
    private static final String MIN_MESSAGE = "{javax.validation.constraints.Min.message}";
    private static final String MOXY_JAXBCONTEXT_FACTORY = JAXBContextFactory.class.getName();
    private static final String SYSTEM_PROPERTY_JAXBCONTEXT = "javax.xml.bind.JAXBContext";
    private static final boolean DEBUG = false;
    private static File DEACTIVATED_VALIDATION_XML;
    private static File ACTIVATED_VALIDATION_XML;

    /**
     * Tests fix for endless invocation loop between
     * unmarshaller - validator - unmarshaller.
     * File {@link #DEACTIVATED_VALIDATION_XML} must be present on classpath.
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

        resetValidationXMLReader();

        JAXBMarshaller marshaller = (JAXBMarshaller) JAXBContextFactory.createContext(new
                Class[]{ExternallyConstrainedEmployee.class, ExternallyConstrainedEmployee2.class},
                null).createMarshaller();
        ExternallyConstrainedEmployee employee = new ExternallyConstrainedEmployee().withId(null).withAge(15);
        ExternallyConstrainedEmployee2 employee2 = new ExternallyConstrainedEmployee2().withId(null).withAge(15);

        try {
            marshaller.marshal(employee, new StringWriter());
        } catch (BeanValidationException ignored) {
        }

        Set<ConstraintViolation<Object>> violations = marshaller.getConstraintViolations();

        try {
            marshaller.marshal(employee2, new StringWriter());
        } catch (BeanValidationException ignored) {
        }

        violations.addAll(marshaller.getConstraintViolations());

        assertFalse("Some constraints were not validated, even though they should have been.", violations.isEmpty());

        int i = 0;
        for (ConstraintViolation constraintViolation : violations) {
            if (NOT_NULL_MESSAGE.equals(constraintViolation.getMessageTemplate())) i += 0b1000;
            if (MIN_MESSAGE.equals(constraintViolation.getMessageTemplate())) i += 0b0001;
        }
        assertTrue(i == 0b1001);
    }

    @Before
    public void setUp() throws Exception {
        createTimeWindow();
    }

    @After
    public void tearDown() throws Exception {
        closeTimeWindow();
    }

    private void createTimeWindow() throws URISyntaxException, IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        //noinspection ConstantConditions
        DEACTIVATED_VALIDATION_XML = new File(cl.getResource("META-INF/deactivated_validation.xml").toURI());
        ACTIVATED_VALIDATION_XML = new File(DEACTIVATED_VALIDATION_XML.getCanonicalPath().replace("deactivated_", ""));
        assertFalse(ACTIVATED_VALIDATION_XML.exists());
        boolean renamingSucceeded = DEACTIVATED_VALIDATION_XML.renameTo(ACTIVATED_VALIDATION_XML);
        assertTrue(renamingSucceeded);
    }

    private void closeTimeWindow() {
        boolean restoringOriginalNameSucceeded = ACTIVATED_VALIDATION_XML.renameTo(DEACTIVATED_VALIDATION_XML);
        assertTrue(restoringOriginalNameSucceeded);
        assertFalse(ACTIVATED_VALIDATION_XML.exists());
    }

    private void resetValidationXMLReader() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This reflection block is trade-off for the convenience of not having visible validation.xml during first
        // initialization of JAXBContext. Validation.xml is deactivated in order to prevent log congestion during
        // testing :)
        Class<?> clazz = Class.forName("org.eclipse.persistence.jaxb.ValidationXMLReader");
        {
            Field f = clazz.getDeclaredField("latch");
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(f, new CountDownLatch(1));
            f.setAccessible(false);
        }
        changeValueOfField(clazz, "firstTime", true);
        changeValueOfField(clazz, "didSomething", false);
        changeValueOfField(clazz, "jdkExecutor", false);
        changeValueOfField(clazz, "possibleResourceVisibilityFail", false);
        {
            Class<?> aClass = Class.forName("org.eclipse.persistence.jaxb.Concurrent");
            Field f = aClass.getDeclaredField("cache");
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(f, new AdvancedProcessor());
            f.setAccessible(false);
        }
        {
            Class<?> aClass = Class.forName("org.eclipse.persistence.jaxb.BeanValidationHelper");
            Field f = aClass.getDeclaredField("xmlParsed");
            f.setAccessible(true);
            f.set(f, false);
            f.setAccessible(false);
        }
        {
            Class<?> aClass = Class.forName("org.eclipse.persistence.jaxb.BeanValidationHelper");
            Field f = aClass.getDeclaredField("TIMEOUT");
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            /* Interesting thing is that under normal circumstances, even with TIMEOUT = 0,
                the async (pool) thread Always finishes before program gets to unmarshalling -> "very nice".
                However, if some tests share the same context and this method is run beforehand,
                to reset all validation xml reading associated fields, then the async thread will perform slowly and
                forced synchronized execution will be called.
                That block of code where it is decided to call sync exec (where breakpoint should be put) is
                BeanValidationHelper#ensureValidationXmlWasParsed, after !ValidationXMLReader.latch.await(TIMEOUT,
                TimeUnit.MILLISECONDS) check.
                Problem could be associated with the way JUnit prioritizes threads in parallel execution
                 of tests, but from info provided by debugger, both main and pool threads have the same priority '5'.
                 Discarding a frame on main thread miraculously helps and makes the async thread finish before the
                 decision point.
                The async thread behaviour should be tested under different framework than JUnit (e.g. JMH),
                with logging in the decision point and it should be measured if/how often the program reaches forced
                sync execution branch of execution. */
            f.set(f, 50);
            f.setAccessible(false);
        }
    }

    private void changeValueOfField(Class<?> clazz, String fieldName, Object newValue) throws NoSuchFieldException,
            IllegalAccessException {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(f, newValue);
        f.setAccessible(false);
    }

}
