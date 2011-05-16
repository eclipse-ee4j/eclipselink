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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.extensibility.Employee;
import org.eclipse.persistence.testing.models.jpa.extensibility.Address;
import org.eclipse.persistence.testing.models.jpa.extensibility.PhoneNumber;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeMethodInfo;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaHelper;
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
        return suite;
    }
    
    public void testSetup() {
        new ExtensibilityTableCreator().replaceTables(JUnitTestCase.getServerSession());

        // Force uppercase for Postgres.
        if (getServerSession().getPlatform().isPostgreSQL()) {
            getServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }
    
    public void testDescriptors(){
        EntityManagerFactory emf = getEntityManagerFactory("extensibility");
        ServerSession session = (ServerSession)JpaHelper.getDatabaseSession(emf);
        
        RelationalDescriptor empDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Employee.class);
        assertTrue(empDescriptor.getMappingForAttributeName("phoneNumbers") != null);
        if (isWeavingEnabled("extensibility")){
            assertTrue(empDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
        }
        assertTrue(empDescriptor.getVirtualAttributeMethods() != null);
        assertTrue(empDescriptor.getVirtualAttributeMethods().size() == 1);
        VirtualAttributeMethodInfo info = empDescriptor.getVirtualAttributeMethods().get(0);
        assertTrue(info.getGetMethodName().equals("getExt"));
        assertTrue(info.getSetMethodName().equals("putExt"));
        
        RelationalDescriptor addDescriptor = (RelationalDescriptor)session.getProject().getDescriptor(Address.class);
        assertTrue(addDescriptor.getMappingForAttributeName("pobox") != null);
        if (isWeavingEnabled("extensibility")){
            assertTrue(addDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
        }
        assertTrue(addDescriptor.getVirtualAttributeMethods() != null);
        assertTrue(addDescriptor.getVirtualAttributeMethods().size() == 1);
        info = addDescriptor.getVirtualAttributeMethods().get(0);
        assertTrue(info.getGetMethodName().equals("get"));
        assertTrue(info.getSetMethodName().equals("set"));
    }
    
    public void testBasicMapping(){
        EntityManagerFactory emf = getEntityManagerFactory("extensibility");
        EntityManager em = emf.createEntityManager();
        
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
        
        em.clear();
        clearCache();
        try{
            beginTransaction(em);
            emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Joe'").getSingleResult();
            add = emp.getAddress();
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
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp.getAddress());
            em.remove(emp);
            commitTransaction(em);
            em.close();
        }
    }
    
    public void testOneToManyMapping(){
        EntityManagerFactory emf = getEntityManagerFactory("extensibility");
        EntityManager em = emf.createEntityManager();
        
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Jim");
        emp.setLastName("Josephson");
        
        em.persist(emp);
        
        commitTransaction(em);
        
        em.clear();
        clearCache();
        PhoneNumber pn = new PhoneNumber();
        try{
            beginTransaction(em);
            emp = (Employee)em.createQuery("select e from ExtensibilityEmployee e where e.firstName = 'Jim'").getSingleResult();

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
            pn = em.find(PhoneNumber.class, pn.getId());
            em.remove(emp);
            em.remove(pn);
            commitTransaction(em);
            em.close();
        }
    }
    
    public void testRefreshEM(){
        
    }
}
