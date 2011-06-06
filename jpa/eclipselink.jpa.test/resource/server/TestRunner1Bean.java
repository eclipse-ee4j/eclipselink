/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.testing.framework.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import junit.framework.TestCase;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * Server side JUnit test invocation implemented as a stateless session bean.
 * 
 * @author mschinca
 */
@Stateless(name="TestRunner1", mappedName="TestRunner1")
@Remote(TestRunner1.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class TestRunner1Bean implements TestRunner1 {
    
    /** The entity manager for the test is injected and passed to the test server platform. */
    @PersistenceContext(unitName="MulitPU-1")
    private EntityManager entityManager;

    /** The entity manager factory for the test is injected and passed to the test server platform. */
    @PersistenceUnit(unitName="MulitPU-1")
    private EntityManagerFactory entityManagerFactory;
    
    /**
     * Execute a test case method. The test class is loaded dynamically and
     * must therefore be visible to the TestRunnerBean classloader.
     */
    public Throwable runTest(String className, String test, Properties props) {
        // load the test class and create an instance
        TestCase testInstance = null;
        try {
            Class testClass = getClass().getClassLoader().loadClass(className);
            Constructor c = testClass.getConstructor(new Class[] { String.class });
            testInstance = (TestCase) c.newInstance(new Object[] { test });
        } catch (ClassNotFoundException e) {
            throw new EJBException(e);
        } catch (NoSuchMethodException e) {
            throw new EJBException(e);
        } catch (InstantiationException e) {
            throw new EJBException(e);
        } catch (IllegalAccessException e) {
            throw new EJBException(e);
        } catch (InvocationTargetException e) {
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
            if (testInstance instanceof JUnitTestCase) {
                JUnitTestCase jpaTest = (JUnitTestCase)testInstance;
                JEEPlatform.entityManager = this.entityManager;
                JEEPlatform.entityManagerFactory = this.entityManagerFactory;
                jpaTest.runBareServer();
            } else {
                testInstance.runBare();
            }
        } catch (Throwable t) {
            result = t;
        }
        return result;
    }

}
