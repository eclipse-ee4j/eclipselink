/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ValidationMode;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.validation.Item;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ValidationTest extends JUnitTestCase {
    public ValidationTest() {
    }

    public ValidationTest(String name) {
        super(name);
    }

    @Override
    public void setUp () {
        super.setUp();
        // Don't clear the cache here since it bring the default pu into play.
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ValidationTest.class) {

            protected void setUp(){
            }

            protected void tearDown() {
            }

        };
        suite.setName("ValidationTest");
        return suite;
    }

    public void testCacheIsolation_PropertiesDefault_Config() {
        // Be sure not to modify the global properties.
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties());
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, Boolean.FALSE.toString());

        EntityManager em = getEntityManagerFactory("isolated1053").createEntityManager(properties);
        assertTrue("Item descriptor did not have an isolated cache setting from a TopLink properties setting.", ((EntityManagerImpl)em).getServerSession().getClassDescriptorForAlias("Item").isIsolated());
        closeEntityManager(em);

        // Ensure this is done to avoid consecutive tests picking up our
        // very specific isolated persistence unit.
        closeEntityManagerFactory("isolated1053");
    }

    /**
     * This test assumes the persistence unit has the following property set:
     *       <pre>{@code </pre><property name="eclipselink.cache.shared.default" value="false"/>}</pre>
     */

    public void testCacheIsolation_XMLDefault_Config() {
        EntityManager em = getEntityManagerFactory("isolated1053").createEntityManager();
        assertTrue("Item descriptor did not have an isolated cache setting from an XML setting.", ((EntityManagerImpl)em).getServerSession().getClassDescriptorForAlias("Item").isIsolated());
        closeEntityManager(em);

        // Ensure this is done to avoid consecutive tests picking up our
        // very specific isolated persistence unit.
        closeEntityManagerFactory("isolated1053");
    }

    /**
     * This tests fix for gf bug 2492, specifically testing
     * jakarta.persistence.jtaDataSource property.  There is no easy way outside a container
     * to ensure the look up fails, but this test ensures that the datasource passed in
     * is used to acquire a connection on login.
     */
    public void testJTADatasource_Config_Override() {
        boolean pass = false;
        Map<String, Object> properties = new HashMap<>();
        tmpDataSourceImp jtadatasourece = new tmpDataSourceImp();
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, jtadatasourece);
        EntityManager em = null;
        try {
            EntityManagerFactory emf = getEntityManagerFactory("ignore");
            emf.unwrap(JpaEntityManagerFactory.class).refreshMetadata(properties);
            em = emf.createEntityManager();
        } catch (RuntimeException expected) {
            pass = expected.getMessage().contains("tmpDataSourceImp getConnection called");
            if (!pass) {
                throw expected;
            }
        } finally {
            if (em != null) {
                closeEntityManager(em);
            }
            // Ensure this is done to avoid consecutive tests picking up our
            // very specific isolated persistence unit.
            closeEntityManagerFactory("ignore");
        }
        assertTrue("JTA datasource was not set or accessed as expected through map of properties", pass);
    }

    /**
     * This tests fix for gf bug 2492, specifically testing
     * jakarta.persistence.jtaDataSource property.  There is no easy way outside a container
     * to ensure the look up fails, but this test ensures that the datasource passed in
     * is used to acquire a connection on login.
     */
    public void testNonJTADatasource_Config_Override() {
        boolean pass = false;
        Map<String, Object> properties = new HashMap<>();
        tmpDataSourceImp nonJTADatasourece = new tmpDataSourceImp();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, nonJTADatasourece);
        EntityManager em = null;
        try {
            EntityManagerFactory emf = getEntityManagerFactory("ignore");
            emf.unwrap(JpaEntityManagerFactory.class).refreshMetadata(properties);
            em = emf.createEntityManager();
        } catch (RuntimeException expected) {
            pass = expected.getMessage().contains("tmpDataSourceImp getConnection called");
            if (!pass) {
                throw expected;
            }
        } finally {
            if (em != null) {
                closeEntityManager(em);
            }
            // Ensure this is done to avoid consecutive tests picking up our
            // very specific isolated persistence unit.
            closeEntityManagerFactory("ignore");
        }
        assertTrue("Non JTA datasource was not set or accessed as expected through map of properties", pass);
    }

    public void testPKClassTypeValidation(){
        try{
            getEntityManagerFactory("isolated1053").createEntityManager().find(Item.class, new Item());
        }catch (IllegalArgumentException ex){
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect PKClass is used in find call");
    }

    /**
     * Bug 367007 - map values of jakarta.persistence.validation.mode are incorrectly throwing exception
     * Create an EntityManager with valid parameter values for jakarta.persistence.validation.mode
     */
    public void testValidValidationModes() {
        String property = "jakarta.persistence.validation.mode";
        String[] validationModes = {
                "none", "NONE", "NoNe", ValidationMode.NONE.name(),
                "auto", "AUTO", "AuTo", ValidationMode.AUTO.name(),
        };

        // close the emf first to allow properties to be specified
        closeEntityManagerFactory();

        for (String validationMode : validationModes) {
            EntityManager em = null;
            try {
                Map<String, Object> props = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties());
                props.put(property, validationMode);
                EntityManagerFactory emf = getEntityManagerFactory("isolated1053");
                emf.unwrap(JpaEntityManagerFactory.class).refreshMetadata(props);
                em = emf.createEntityManager();
            } catch (RuntimeException exception) {
                fail("Exception caught when passing property: [" + property + "] = [" + validationMode + "] when creating an EntityManager: " + exception.getMessage());
            } finally {
                if (em != null) {
                    closeEntityManager(em);
                }
                closeEntityManagerFactory();
            }
        }
    }

    public class tmpDataSourceImp implements DataSource{
        public tmpDataSourceImp(){
            super();
        }
        @Override
        public Connection getConnection() {
            throw new RuntimeException("tmpDataSourceImp getConnection called");
        }
        @Override
        public Connection getConnection(String username, String password) {
            return getConnection();
        }
        //rest are ignored
        @Override
        public java.io.PrintWriter getLogWriter() {
            return null;
        }
        @Override
        public void setLogWriter(PrintWriter out) {}
        @Override
        public void setLoginTimeout(int seconds) {}
        @Override
        public int getLoginTimeout() {return 1;}
        @Override
        public <T> T unwrap(Class<T> iface) { return null; }
        @Override
        public boolean isWrapperFor(Class<?> iface) { return false; }
        @Override
        public Logger getParentLogger(){return null;}
    }
}
