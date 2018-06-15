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
//     Dmitry Kornilov - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.eclipse.persistence.jaxb.BeanValidationHelper;
import org.eclipse.persistence.jaxb.ValidationXMLReader;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class contains BeanValidationHelper related tests.
 *
 * @author Dmitry Kornilov
 */
@RunWith(JMockit.class)
public class BeanValidationHelperTestCase {

    /**
     * Tests that validation.xml parsing is not called if validation.xml doesn't exist.
     */
    @Test
    public void testValidationXmlExists(final @Mocked ValidationXMLReader reader) throws NamingException {
        new Expectations() {{
            ValidationXMLReader.isValidationXmlPresent(); result = false;
            new ValidationXMLReader(); times = 0;
        }};

        BeanValidationHelper beanValidationHelper = new BeanValidationHelper();
        assertNotNull(beanValidationHelper.getConstraintsMap());
        assertTrue(beanValidationHelper.getConstraintsMap().size() == 0);
    }

    /**
     * Tests that managed executor service doesn't get shutdown.
     */
    @Test
    public void testManagedExecutorService(final @Mocked InitialContext initialContext,
                                           final @Mocked ExecutorService managedExecutorService,
                                           final @Mocked ValidationXMLReader reader) throws NamingException {
        new Expectations() {{
            ValidationXMLReader.isValidationXmlPresent(); result = true;
            new InitialContext(); result = initialContext;
            initialContext.lookup("java:comp/env/concurrent/ThreadPool"); result = managedExecutorService;
            new ValidationXMLReader(); result = reader;
            managedExecutorService.submit((Callable) any);
            managedExecutorService.shutdown(); times=0;
        }};

        new BeanValidationHelper();
    }

    /**
     * Tests that JDK executor service gets properly shutdown.
     */
    @Test
    public void testJDKExecutorService(final @Mocked InitialContext initialContext,
                                       final @Mocked ExecutorService jdkExecutorService,
                                       final @Mocked ValidationXMLReader reader) throws NamingException {
        new MockUp<Executors>() {
            @Mock
            public ExecutorService newFixedThreadPool(int nThreads) {
                return jdkExecutorService;
            }
        };

        new Expectations() {
            {
                ValidationXMLReader.isValidationXmlPresent(); result = true;
                new InitialContext(); result = initialContext;
                initialContext.lookup("java:comp/env/concurrent/ThreadPool"); result = new NamingException();
                new ValidationXMLReader(); result = reader;
                jdkExecutorService.submit((Callable) any);
                jdkExecutorService.shutdown();
            }
        };

        new BeanValidationHelper();
    }

    /**
     * Tests that validation.xml gets parsed when asynchronous attempt failed.
     */
    @Test
    public void testAsyncParsingFailed(final @Mocked ValidationXMLReader reader) throws Exception {
        new MockUp<Future<Map<Class<?>, Boolean>>>() {
            @Mock
            public Future<Map<Class<?>, Boolean>> get() throws InterruptedException, ExecutionException {
                throw new InterruptedException();
            }
        };

        new Expectations() {{
            ValidationXMLReader.isValidationXmlPresent(); result = true;
            new ValidationXMLReader(); result = reader;
            reader.call();
        }};

        BeanValidationHelper beanValidationHelper = new BeanValidationHelper();
        assertNotNull(beanValidationHelper.getConstraintsMap());
    }

    /**
     * Tests that validation.xml gets parsed if async task submission failed.
     */
    @Test
    public void testAsyncSubmissionFailed(final @Mocked InitialContext initialContext,
                                          final @Mocked ExecutorService jdkExecutorService,
                                          final @Mocked ValidationXMLReader reader) throws Exception {
        new MockUp<Executors>() {
            @Mock
            public ExecutorService newFixedThreadPool(int nThreads) {
                return jdkExecutorService;
            }
        };

        new Expectations() {
            {
                ValidationXMLReader.isValidationXmlPresent(); result = true;
                new InitialContext(); result = initialContext;
                initialContext.lookup("java:comp/env/concurrent/ThreadPool"); result = new NamingException();
                new ValidationXMLReader(); result = reader;
                jdkExecutorService.submit((Callable) any); result = new OutOfMemoryError();
                jdkExecutorService.shutdown();
                reader.call();
            }
        };

        BeanValidationHelper beanValidationHelper = new BeanValidationHelper();
        assertNotNull(beanValidationHelper.getConstraintsMap());
    }
}
