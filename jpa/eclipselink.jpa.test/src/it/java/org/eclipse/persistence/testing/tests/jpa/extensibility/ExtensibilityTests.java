/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation as part of extensibility feature
package org.eclipse.persistence.testing.tests.jpa.extensibility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.extensibility.Employee;
import org.eclipse.persistence.testing.models.jpa.extensibility.Address;
import org.eclipse.persistence.testing.models.jpa.extensibility.PhoneNumber;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeMethodInfo;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.sessions.coordination.MetadataRefreshCommand;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.extensibility.ExtensibilityTableCreator;

public class ExtensibilityTests extends JUnitTestCase {

    public ExtensibilityTests() {
        super();
    }

    public ExtensibilityTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ExtensibilityTestSuite");
        if (! JUnitTestCase.isJPA10()) {
            /*if (System.getProperty("run.metadata.cache.test.suite").compareTo("true") == 0) {
                suite.addTest(new ExtensibilityTests("testWriteProjectCache"));
            }*/
            suite.addTest(new ExtensibilityTests("testSetup"));
            suite.addTest(new ExtensibilityTests("testDescriptors"));
            suite.addTest(new ExtensibilityTests("testBasicMapping"));
            suite.addTest(new ExtensibilityTests("testOneToManyMapping"));
            suite.addTest(new ExtensibilityTests("testSimpleRefresh"));
            suite.addTest(new ExtensibilityTests("testMergeRefreshed"));
            suite.addTest(new ExtensibilityTests("testMergeRefreshedManyToMany"));
            suite.addTest(new ExtensibilityTests("testUntriggerVHOnDetached"));
            suite.addTest(new ExtensibilityTests("testFetchGroupOnRefresh"));
            suite.addTest(new ExtensibilityTests("testExistingEntityManagerAfterRefresh"));
            suite.addTest(new ExtensibilityTests("testSetupImplRefresh"));
            suite.addTest(new ExtensibilityTests("testRCMRefreshCommand"));
            suite.addTest(new ExtensibilityTests("testMetadatasourceProperties"));
        }
        return suite;
    }

    public String getPersistenceUnitName(){
        return "extensibility";
    }

    /*public void testWriteProjectCache(){
        new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite().testFileBasedProjectCacheLoading("extensibility");
    }*/

    public void persistEmployeeData(EntityManagerFactory emf){
        EntityManager em = emf.createEntityManager();
        try {
            beginTransaction(em);
            Employee emp = new Employee();
            emp.setFirstName("Joe");
            emp.setLastName("Josephson");

            Address add = new Address();
            add.setStreet("Main Street");
            add.setCity("Herestowm");
            add.setPostalCode("A1A1A1");
            add.setCountry("Here");

            emp.setAddress(add);

            em.persist(emp);
            em.persist(add);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
        }
        clearCache();
    }

    public void deleteEmployeeData(EntityManagerFactory emf){
        EntityManager em = emf.createEntityManager();
        try {
            beginTransaction(em);
            em.createNativeQuery("UPDATE EXTENS_EMP SET ADDRESS_ID = NULL").executeUpdate();
            em.createNativeQuery("DELETE FROM EXTENS_JOIN_TABLE").executeUpdate();
            em.createNativeQuery("DELETE FROM EXTENS_EMP").executeUpdate();
            em.createNativeQuery("DELETE FROM EXTENS_ADDR").executeUpdate();
            em.createNativeQuery("DELETE FROM EXTENS_PHONE").executeUpdate();
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            clearCache();
        }
    }

    public void testSetup() {
        ServerSession serverSession = getServerSession(getPersistenceUnitName());
        new ExtensibilityTableCreator().replaceTables(serverSession);

        // Force uppercase for Postgres.
        if (serverSession.getPlatform().isPostgreSQL()) {
            serverSession.getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    public void testDescriptors(){
        EntityManagerFactory emf = getEntityManagerFactory();
        ServerSession session = (ServerSession)getDatabaseSession();

        RelationalDescriptor empDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Employee.class);
        assertTrue(empDescriptor.getMappingForAttributeName("phoneNumbers") != null);
        if (isWeavingEnabled()){
            assertTrue(empDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
        }
        assertTrue(empDescriptor.getVirtualAttributeMethods() != null);
        assertTrue(empDescriptor.getVirtualAttributeMethods().size() == 1);
        VirtualAttributeMethodInfo info = empDescriptor.getVirtualAttributeMethods().get(0);
        assertTrue(info.getGetMethodName().equals("getExt"));
        assertTrue(info.getSetMethodName().equals("putExt"));

        RelationalDescriptor addDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Address.class);
        assertTrue(addDescriptor.getMappingForAttributeName("pobox") != null);
        if (isWeavingEnabled()){
            assertTrue(addDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
        }
        assertTrue(addDescriptor.getVirtualAttributeMethods() != null);
        assertTrue(addDescriptor.getVirtualAttributeMethods().size() == 1);
        info = addDescriptor.getVirtualAttributeMethods().get(0);
        assertTrue(info.getGetMethodName().equals("get"));
        assertTrue(info.getSetMethodName().equals("set"));
    }

    public void testBasicMapping(){
        Map props = new HashMap();
        props.put(PersistenceUnitProperties.SESSION_NAME, "bla1");

        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);

        EntityManager em = emf.createEntityManager();
        try{
            beginTransaction(em);
            Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            Address add = emp.getAddress();
            add.set("pobox", "111");
            commitTransaction(em);

            em.refresh(emp);

            assertTrue("The pobox was not properly saved", emp.getAddress().get("pobox").equals("111"));

            em.clear();
            clearCache();

            add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.pobox = '111'").getSingleResult();
            assertTrue("queries on extended Basic mappings fail", add != null);
            assertTrue("queries on extended Basic mappings return incorrect results.", add.get("pobox").equals("111"));

        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            deleteEmployeeData(emf);
        }
    }


    public void testOneToManyMapping(){
        EntityManagerFactory emf = getEntityManagerFactory();

        persistEmployeeData(emf);

        EntityManager em = emf.createEntityManager();

        PhoneNumber pn = new PhoneNumber();
        Employee emp = null;
        try{
            beginTransaction(em);
            emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();

            pn.setAreaCode("613");
            pn.setNumber("1111111");
            em.persist(pn);
            List numbers = new ArrayList();
            numbers.add(pn);
            emp.putExt("phoneNumbers", numbers);
            commitTransaction(em);

            em.refresh(emp);

            numbers = ((List)emp.getExt("phoneNumbers"));
            assertTrue("The phoneNumbers were not properly saved", numbers.size() == 1);

            em.clear();
            clearCache();

            emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e join e.phoneNumbers p where p.areaCode = '613'").getSingleResult();
            assertTrue("queries on extended OneToMany mappings fail", emp != null);
            assertTrue("queries on extended OneToMany mappings return incorrect results.", ((List)emp.getExt("phoneNumbers")).size() == 1);
        } finally {
            try {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                em.close();
                deleteEmployeeData(emf);
            } catch (RuntimeException e){
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void testSimpleRefresh(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        WeakReference<EntityManagerFactoryDelegate> emfRef = new WeakReference<EntityManagerFactoryDelegate>(((EntityManagerFactoryImpl)JpaHelper.getEntityManagerFactory(em)).unwrap());
        ServerSession session = (ServerSession)getDatabaseSession();
        RelationalDescriptor addDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Address.class);
        assertTrue(addDescriptor.getMappingForAttributeName("pobox") != null);
        RelationalDescriptor empDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Employee.class);
        assertTrue(empDescriptor.getMappingForAttributeName("phoneNumbers") != null);

        session = null;

        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");

        JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);

        session = (ServerSession)getDatabaseSession();
        addDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Address.class);
        assertTrue(addDescriptor.getMappingForAttributeName("pobox") == null);
        assertTrue(addDescriptor.getMappingForAttributeName("appartmentNumber") != null);
        empDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Employee.class);
        assertTrue(empDescriptor.getMappingForAttributeName("phoneNumbers") == null);

        persistEmployeeData(emf);

        em = emf.createEntityManager();
        try{
            beginTransaction(em);
            Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            emp.getAddress().set("appartmentNumber", "111");
            commitTransaction(em);

            em.clear();
            clearCache();

            emp = em.find(Employee.class, emp.getId());
            assertTrue(emp.getAddress().get("appartmentNumber").equals("111"));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            deleteEmployeeData(emf);
        }
        System.gc();
        assertTrue(emfRef.get() == null);
    }

    public void testMergeRefreshed(){
        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);

        EntityManager em = emf.createEntityManager();
        try{
            beginTransaction(em);
            Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            Address add = emp.getAddress();
            emp.getAddress().set("appartmentNumber", "112");
            commitTransaction(em);

            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension.xml");

            JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);

            em.close();

            em = emf.createEntityManager();

            beginTransaction(em);
            add = em.merge(add);
            add.set("pobox", "111");

            commitTransaction(em);

            em.refresh(add);

            assertTrue(add.get("pobox").equals("111"));
            assertTrue(add.get("appartmentNumber") == null);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            deleteEmployeeData(emf);
        }
    }

    public void testMergeRefreshedManyToMany(){
        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);
        EntityManager em = emf.createEntityManager();

        Employee emp = null;
        PhoneNumber pn = null;
        try{
            beginTransaction(em);
            emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            pn = new PhoneNumber();
            pn.setAreaCode("613");
            pn.setNumber("1111111");
            em.persist(pn);
            List numbers = new ArrayList();
            numbers.add(pn);
            emp.putExt("phoneNumbers", numbers);
            commitTransaction(em);

            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");

            JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);
            em.close();

            em = emf.createEntityManager();
            emp = em.merge(emp);
            assertNull(emp.getExt("phoneNumbers"));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            deleteEmployeeData(emf);
        }
    }

    public void testUntriggerVHOnDetached(){
        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);
        EntityManager em = emf.createEntityManager();

        Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
        try{
            JpaHelper.getEntityManagerFactory(em).refreshMetadata(null);
            assertTrue(emp.getAddress().getCity().equals("Herestowm"));
            em.close();
            em = null;
            emp = null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            deleteEmployeeData(emf);
        }

    }

    public void testFetchGroupOnRefresh(){
        if (!isWeavingEnabled()){
            return;
        }
        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);
        EntityManager em = emf.createEntityManager();

        Address add = null;
        try {
            FetchGroup fetch = new FetchGroup();
            fetch.addAttribute("id");
            fetch.addAttribute("city");
            fetch.addAttribute("appartmentNumber");
            add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.city = 'Herestowm'").setHint(QueryHints.FETCH_GROUP, fetch).getSingleResult();
            JpaHelper.getEntityManagerFactory(em).refreshMetadata(null);
            System.gc();
            assertTrue(add.getCity().equals("Herestowm"));
            assertTrue(add.get("appartmentNumber") == null);
            assertTrue(add.getStreet().equals("Main Street"));
        } finally {
            em.close();
            deleteEmployeeData(emf);
        }
    }

    public void testExistingEntityManagerAfterRefresh(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");
        JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);
        em.close();

        persistEmployeeData(emf);
        em = emf.createEntityManager();
        EntityManager em2 = null;

        Address add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.city = 'Herestowm'").getSingleResult();
        // workaround for server testing issue
        em.find(Address.class, add.getId());
        try {
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension.xml");
            JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);
            em2 = emf.createEntityManager();

            beginTransaction(em);
            add.set("appartmentNumber", "333");
            em.flush();

            clearCache();
            em.clear();
            add = em.find(Address.class, add.getId());
            assertTrue(add.get("appartmentNumber").equals("333"));
            rollbackTransaction(em);

            clearCache();

            beginTransaction(em2);
            add = em2.find(Address.class, add.getId());
            add.set("pobox", "1");
            commitTransaction(em2);
            clearCache();
            em.clear();
            add = em2.find(Address.class, add.getId());
            assertTrue(add.get("pobox").equals("1"));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            if (em2!=null && em2.isOpen()) {
                if (isTransactionActive(em2)) {
                    rollbackTransaction(em2);
                }
                em2.close();
            }

            deleteEmployeeData(emf);
        }
    }

    public void testSetupImplRefresh(){
        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);
        EntityManager em = emf.createEntityManager();
        try{
            EntityManagerFactoryDelegate delegate = (EntityManagerFactoryDelegate)em.unwrap(JpaEntityManager.class).getEntityManagerFactory();
            EntityManagerSetupImpl setupImpl = EntityManagerFactoryProvider.emSetupImpls.get(delegate.getSetupImpl().getSessionName());
            Map properties = new HashMap();
            properties.putAll(delegate.getProperties());
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");
            setupImpl.refreshMetadata(properties);

            em = emf.createEntityManager();
            beginTransaction(em);
            Address add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.city = 'Herestowm'").getSingleResult();
            add.set("appartmentNumber", "444");
            commitTransaction(em);
            clearCache();
            em.clear();
            add = em.find(Address.class, add.getId());
            assertTrue(add.get("appartmentNumber").equals("444"));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            deleteEmployeeData(emf);
        }
    }

    /**
     * This test checks that a MetadataRefreshCommand will refresh the metadata source the same way a call to
     * EntityManagerSetupImpl refreshMetadata would.  It also verifies that the listener has been changed on the
     * new session for RCM MetadataRefreshCommand messages.
     *
     */
    public void testRCMRefreshCommand(){
        EntityManagerFactory emf = getEntityManagerFactory();
        persistEmployeeData(emf);
        EntityManager em = emf.createEntityManager();
        try{
            EntityManagerFactoryDelegate delegate = (EntityManagerFactoryDelegate)em.unwrap(JpaEntityManager.class).getEntityManagerFactory();
            EntityManagerSetupImpl setupImpl = EntityManagerFactoryProvider.emSetupImpls.get(delegate.getSetupImpl().getSessionName());
            Map properties = new HashMap();

            //setup
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");
            properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
            MetadataRefreshCommand command = new MetadataRefreshCommand(properties);

            AbstractSession session = delegate.getAbstractSession();
            command.executeWithSession(session);

            em = emf.createEntityManager();
            beginTransaction(em);
            Address add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.city = 'Herestowm'").getSingleResult();
            add.set("appartmentNumber", "444");
            commitTransaction(em);
            clearCache();
            em.clear();
            add = em.find(Address.class, add.getId());
            assertTrue(add.get("appartmentNumber").equals("444"));
            this.assertNull("RCM Refresh command listener was not removed from old session", session.getRefreshMetadataListener());
            delegate = (EntityManagerFactoryDelegate)em.unwrap(JpaEntityManager.class).getEntityManagerFactory();
            this.assertNotNull("RCM Refresh command listener was not added to the new session", ((AbstractSession)delegate.getDatabaseSession()).getRefreshMetadataListener());
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            deleteEmployeeData(emf);
        }
    }

    public void testMetadatasourceProperties(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        JpaEntityManagerFactory jpaEmf = JpaHelper.getEntityManagerFactory(em);
        ServerSession originalSession = jpaEmf.getServerSession();
        String sessionName = originalSession.getName();

        // cleanUpProperties will be used to return the factory back to its original state
        HashMap cleanUpProperties = new HashMap(4);
        Object transactionType = originalSession.getProperty(PersistenceUnitProperties.TRANSACTION_TYPE);
        if (transactionType != null) {
            // that would remove the property
            transactionType = "";
        }
        cleanUpProperties.put(PersistenceUnitProperties.TRANSACTION_TYPE, transactionType);
        Object jtaDataSource = originalSession.getProperty(PersistenceUnitProperties.JTA_DATASOURCE);
        if (jtaDataSource != null) {
            Connector mainConnector = originalSession.getLogin().getConnector();
            if (mainConnector instanceof JNDIConnector) {
                jtaDataSource = ((JNDIConnector)mainConnector).getName();
            } else {
                // that would remove the property
                jtaDataSource = "";
            }
        }
        cleanUpProperties.put(PersistenceUnitProperties.JTA_DATASOURCE, jtaDataSource);
        Object nonJtaDataSource = originalSession.getProperty(PersistenceUnitProperties.NON_JTA_DATASOURCE);
        if (nonJtaDataSource != null) {
            Connector readConnector = ((DatabaseLogin)originalSession.getReadConnectionPool().getLogin()).getConnector();
            if (readConnector instanceof JNDIConnector) {
                nonJtaDataSource = ((JNDIConnector)readConnector).getName();
            } else {
                // that would remove the property
                nonJtaDataSource = "";
            }
        }
        cleanUpProperties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, nonJtaDataSource);
        // that would remove the property
        cleanUpProperties.put(PersistenceUnitProperties.METADATA_SOURCE_PROPERTIES_FILE, "");

        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.METADATA_SOURCE_PROPERTIES_FILE, "extension.properties");
        jpaEmf.refreshMetadata(properties);

        try {
            em = emf.createEntityManager();
            fail("PersistenceException was expected");
        } catch (PersistenceException ex) {
            // exception expected because extension.properties contains bogus settings:
            //   javax.persistence.transactionType=JTA
            //   javax.persistence.jtaDataSource=MyJtaDataSource
            //   javax.persistence.nonJtaDataSource=MyNonJtaDataSource
            // Examine the session to see whether these settings were applied
            // Note that because session login has failed the session is not accessible from emf, only directly from emSetupImpls.
            String errorMsg = "";
            ServerSession serverSession = (ServerSession)EntityManagerFactoryProvider.emSetupImpls.get(sessionName).getSession();
            if (!serverSession.getLogin().shouldUseExternalTransactionController()) {
                errorMsg += "External tarnsaction controller was expected; ";
            }
            Connector mainConnector = serverSession.getLogin().getConnector();
            if (!(mainConnector instanceof JNDIConnector)) {
                errorMsg += "Main JNDIConnector was expected; ";
            } else {
                if (!((JNDIConnector)mainConnector).getName().equals("MyJtaDataSource")) {
                    errorMsg += "MyJtaDataSource was expected; ";
                }
            }
            Connector readConnector = ((DatabaseLogin)serverSession.getReadConnectionPool().getLogin()).getConnector();
            if (!(readConnector instanceof JNDIConnector)) {
                errorMsg += "Read JNDIConnector was expected; ";
            } else {
                if (!((JNDIConnector)readConnector).getName().equals("MyNonJtaDataSource")) {
                    errorMsg += "MyNonJtaDataSource was expected; ";
                }
            }
        } finally {
            // clean-up: back to the original settings
            jpaEmf.refreshMetadata(cleanUpProperties);
            emf.createEntityManager();
        }
    }

}

