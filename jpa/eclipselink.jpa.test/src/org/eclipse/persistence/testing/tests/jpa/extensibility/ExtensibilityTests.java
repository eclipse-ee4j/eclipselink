/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation as part of extensibility feature
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.extensibility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.extensibility.Employee;
import org.eclipse.persistence.testing.models.jpa.extensibility.Address;
import org.eclipse.persistence.testing.models.jpa.extensibility.PhoneNumber;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeMethodInfo;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.extensibility.ExtensibilityTableCreator;
import org.eclipse.persistence.testing.tests.jpa.relationships.ExpressionJUnitTestSuite;

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
        return suite;
    }
    
    public String getPersistenceUnitName(){
        return "extensibility";
    }
    
    public void persistEmployeeData(EntityManager em){
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
    }
    
    public void deleteEmployeeData(EntityManager em){
        Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe' and e.lastName = 'Josephson'").getSingleResult();
        Address add = emp.getAddress();
        emp.setAddress(null);
        em.remove(emp);
        em.remove(add);
    }
    
    public void testSetup() {
        new ExtensibilityTableCreator().replaceTables(JUnitTestCase.getServerSession());

        // Force uppercase for Postgres.
        if (getServerSession().getPlatform().isPostgreSQL()) {
            getServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }
    
    public void testDescriptors(){
        EntityManagerFactory emf = getEntityManagerFactory();
        ServerSession session = (ServerSession)JpaHelper.getDatabaseSession(emf);
        
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
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        
        em.clear();
        clearCache();
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
            beginTransaction(em);
            deleteEmployeeData(em);
            commitTransaction(em);
            em.close();
            clearCache();
        }
    }

    
    public void testOneToManyMapping(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        
        em.clear();
        clearCache();
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
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            pn = (PhoneNumber)((List)emp.getExt("phoneNumbers")).get(0);
            ((List)emp.getExt("phoneNumbers")).remove(0);
            em.remove(pn);
            deleteEmployeeData(em);
            commitTransaction(em);
            em.close();
            clearCache();
        }
    }
    
    public void testSimpleRefresh(){
        EntityManagerFactory emf = getEntityManagerFactory();
        WeakReference<EntityManagerFactoryDelegate> emfRef = new WeakReference<EntityManagerFactoryDelegate>(((JpaEntityManagerFactory)emf).unwrap());
        EntityManager em = emf.createEntityManager();
        ServerSession session = (ServerSession)JpaHelper.getDatabaseSession(emf);
        RelationalDescriptor addDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Address.class);
        assertTrue(addDescriptor.getMappingForAttributeName("pobox") != null);
        RelationalDescriptor empDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Employee.class);
        assertTrue(empDescriptor.getMappingForAttributeName("phoneNumbers") != null);
        
        session = null;
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");
        
        JpaHelper.refreshMetadata(emf, properties);
        session = (ServerSession)JpaHelper.getDatabaseSession(emf);
        addDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Address.class);
        assertTrue(addDescriptor.getMappingForAttributeName("pobox") == null);
        assertTrue(addDescriptor.getMappingForAttributeName("appartmentNumber") != null);
        empDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Employee.class);
        assertTrue(empDescriptor.getMappingForAttributeName("phoneNumbers") == null);
        
        
        em = emf.createEntityManager();
        try{
            persistEmployeeData(em);
            
            beginTransaction(em);
            Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            emp.getAddress().set("appartmentNumber", "111");
            commitTransaction(em);
            
            em.clear();
            clearCache();
            
            emp = em.find(Employee.class, emp.getId());
            
            assertTrue(emp.getAddress().get("appartmentNumber").equals("111"));
        } finally {
            beginTransaction(em);
            deleteEmployeeData(em);
            commitTransaction(em);
            clearCache();
        }
        em.close();
        System.gc();
        assertTrue(emfRef.get() == null);
    }
    
    public void testMergeRefreshed(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        
        em.clear();
        clearCache();
        try{
            beginTransaction(em);
            Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            Address add = emp.getAddress();
            emp.getAddress().set("appartmentNumber", "112");
            commitTransaction(em);
            
            em.close();
            
            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension.xml");
            
            JpaHelper.refreshMetadata(emf, properties);
            
            em = emf.createEntityManager();

            
            beginTransaction(em);
            add = em.merge(add);
            add.set("pobox", "111");

            commitTransaction(em);
            
            em.refresh(add);
            
            assertTrue(add.get("pobox").equals("111"));
            assertTrue(add.get("appartmentNumber") == null);
        } finally {
            beginTransaction(em);
            deleteEmployeeData(em);
            commitTransaction(em);
            clearCache();
        }
    }
    
    public void testMergeRefreshedManyToMany(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        
        em.clear();
        clearCache();
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
            
            em.close();
            
            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension2.xml");
            
            JpaHelper.refreshMetadata(emf, properties);
            
            em = emf.createEntityManager();
            emp = em.merge(emp);
            assertNull(emp.getExt("phoneNumbers"));
        } finally {
            beginTransaction(em);
            em.createNativeQuery("delete from EXTENS_JOIN_TABLE").executeUpdate();
            pn = em.find(PhoneNumber.class, pn.getId());
            em.remove(pn);
            deleteEmployeeData(em);
            commitTransaction(em);
            clearCache();
        }
    }
    
    public void testUntriggerVHOnDetached(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        clearCache();
        em.clear();
        Employee emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
        try{
            JpaHelper.refreshMetadata(emf, null);
            System.gc();
            assertNotNull(sesRef.get());
            assertTrue(emp.getAddress().getCity().equals("Herestowm"));
            em.close();
            em = null;
            emp = null;
        } finally {
            em = emf.createEntityManager();
            beginTransaction(em);
            deleteEmployeeData(em);
            commitTransaction(em);
        }
        
    }
    
    public void testFetchGroupOnRefresh(){
        if (!isWeavingEnabled()){
            return;
        }
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        clearCache();
        em.clear();
        Address add = null;
        try {
            FetchGroup fetch = new FetchGroup();
            fetch.addAttribute("id");
            fetch.addAttribute("city");
            fetch.addAttribute("appartmentNumber");
            add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.city = 'Herestowm'").setHint(QueryHints.FETCH_GROUP, fetch).getSingleResult();
            JpaHelper.refreshMetadata(emf, null);
            System.gc();
            assertTrue(add.getCity().equals("Herestowm"));
            assertTrue(add.get("appartmentNumber") == null);
            assertTrue(add.getStreet().equals("Main Street"));
        } finally {
            em = emf.createEntityManager();
            beginTransaction(em);
            deleteEmployeeData(em);
            commitTransaction(em);
        }
    }
    
    public void testExistingEntityManagerAfterRefresh(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        
        persistEmployeeData(em);
        clearCache();
        em.clear();
        Address add = (Address)em.createQuery("select a from ExtensibilityAddress a where a.city = 'Herestowm'").getSingleResult();
        try {
            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.METADATA_SOURCE_XML_FILE, "extension.xml");
            JpaHelper.refreshMetadata(emf, properties);
            EntityManager em2 = emf.createEntityManager();
            
            beginTransaction(em);
            add.set("appartmentNumber", "333");
            commitTransaction(em);
            clearCache();
            em.clear();
            add = em.find(Address.class, add.getId());
            assertTrue(add.get("appartmentNumber").equals("333"));
            
            beginTransaction(em2);
            add = em2.find(Address.class, add.getId());
            add.set("pobox", "1");
            commitTransaction(em2);
            clearCache();
            em.clear();
            add = em2.find(Address.class, add.getId());
            assertTrue(add.get("pobox").equals("1"));
        } finally {
            em = emf.createEntityManager();
            beginTransaction(em);
            deleteEmployeeData(em);
            commitTransaction(em);
        }
    }
}

