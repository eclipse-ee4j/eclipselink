/*******************************************************************************
 * Copyright (c) 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - 2.7 - initial implementation
 ******************************************************************************/
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

/**
 * This class contains BeanValidationHelper related tests.
 *
 * @author Dmitry Kornilov
 */
@RunWith(JMockit.class)
public class BeanValidationHelperTestCase {

    /**
     * Tests that managed executor service doesn't get shutdown.
     */
    @Test
    public void testManagedExecutorService(final @Mocked InitialContext initialContext,
                                           final @Mocked ExecutorService managedExecutorService) throws NamingException {
        new Expectations() {{
            new InitialContext();
            initialContext.lookup("java:comp/env/concurrent/ThreadPool"); returns(managedExecutorService);
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
                                       final @Mocked ExecutorService jdkExecutorService) throws NamingException {
        new MockUp<Executors>() {
            @Mock
            public ExecutorService newSingleThreadExecutor() {
                return jdkExecutorService;
            }
        };

        new Expectations() {
            {
                new InitialContext();
                initialContext.lookup("java:comp/env/concurrent/ThreadPool"); result = new NamingException();
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
            new ValidationXMLReader();
            reader.call();
        }};

        BeanValidationHelper beanValidationHelper = new BeanValidationHelper();
        assertNotNull(beanValidationHelper.getConstraintsMap());
    }
}
