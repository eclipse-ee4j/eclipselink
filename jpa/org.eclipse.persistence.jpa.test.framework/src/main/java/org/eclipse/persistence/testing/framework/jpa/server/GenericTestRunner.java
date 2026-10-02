/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.framework.jpa.server;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.ejb.Remote;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import junit.framework.TestCase;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

@Stateless(name="GenericTestRunner")
@Remote(TestRunner.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class GenericTestRunner implements TestRunner {

    @Resource
    private SessionContext ctx;

    public GenericTestRunner() {
    }

    /**
     * Execute a test case method. The test class is loaded dynamically and
     * must therefore be visible to the TestRunnerBean classloader.
     */
    public Throwable runTest(String className, String test, Properties props) {
        // load the test class and create an instance
        TestCase testInstance = null;
        try {
            @SuppressWarnings({"unchecked"})
            Class<? extends TestCase> testClass = (Class<? extends TestCase>) getClass().getClassLoader().loadClass(className);
            Constructor<? extends TestCase> c = testClass.getConstructor(String.class);
            testInstance = c.newInstance(test);
        } catch (ReflectiveOperationException e) {
            throw new EJBException(e);
        }

        // if any properties were passed in, set them into
        // the server's VM
        if (props != null) {
            System.getProperties().putAll(props);
        }

        // execute the bare test case
        Throwable result = null;
        try {
            if (testInstance instanceof JUnitTestCase jpaTest) {
                String puName = jpaTest.getPuName();
                if (puName != null && !"default".equals(puName)) {
                    JEEPlatform.entityManager = lookup("persistence/" + puName + "/entity-manager");
                    JEEPlatform.entityManagerFactory = lookup("persistence/" + puName + "/factory");
                } else {
                    JEEPlatform.entityManager = getEntityManager();
                    JEEPlatform.entityManagerFactory = getEntityManagerFactory();
                }
                JEEPlatform.ejbLookup = getEjbLookup();
                jpaTest.runBareServer();
            } else {
                testInstance.runBare();
            }
        } catch (Throwable t) {
            result = t;
        }
        return serializable(result);
    }

    // The failure goes back to the test client by a remote call, so it has to be serializable as a whole.
    // It is not when something in it references a non-serializable object - an entity in the constraint
    // violations of a ConstraintViolationException, for instance - and the client then only gets a
    // marshalling error instead of the failure. Such a failure is replaced by a copy that keeps what
    // describes it (class name, message, stack trace, causes and suppressed exceptions) but not the
    // references.
    static Throwable serializable(Throwable failure) {
        if (failure == null || isSerializable(failure)) {
            return failure;
        }
        return copyOf(failure, 0);
    }

    private static boolean isSerializable(Object object) {
        try (ObjectOutputStream out = new ObjectOutputStream(OutputStream.nullOutputStream())) {
            out.writeObject(object);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static Throwable copyOf(Throwable failure, int depth) {
        Throwable cause = failure.getCause();
        Throwable causeCopy = null;
        if (cause != null && cause != failure && depth < 32) {
            causeCopy = isSerializable(cause) ? cause : copyOf(cause, depth + 1);
        }
        String message = failure.getMessage();
        RuntimeException copy = new RuntimeException(
                failure.getClass().getName() + (message == null ? "" : ": " + message), causeCopy);
        copy.setStackTrace(failure.getStackTrace());
        for (Throwable suppressed : failure.getSuppressed()) {
            copy.addSuppressed(isSerializable(suppressed) ? suppressed : copyOf(suppressed, depth + 1));
        }
        return copy;
    }

    protected EntityManager getEntityManager() {
        return null;
    }

    protected EntityManagerFactory getEntityManagerFactory() {
        return null;
    }

    protected boolean getEjbLookup() {
        return true;
    }

    @SuppressWarnings({"unchecked"})
    private <T> T lookup(String jndiName) {
        return (T) ctx.lookup(jndiName);
    }
}
