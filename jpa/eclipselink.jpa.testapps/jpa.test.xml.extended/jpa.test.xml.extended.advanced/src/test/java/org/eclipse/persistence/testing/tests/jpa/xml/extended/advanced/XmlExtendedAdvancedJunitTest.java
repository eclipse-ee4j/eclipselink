/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.xml.extended.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.RollbackException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.CacheAuditor;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.tests.jpa.xml.advanced.XmlAdvancedJunitTest;

public class XmlExtendedAdvancedJunitTest extends XmlAdvancedJunitTest {

    public XmlExtendedAdvancedJunitTest() {
        super();
    }

    public XmlExtendedAdvancedJunitTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-advanced";
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("XmlExtendedAdvancedJunitTest - extended-advanced");

        suite.addTest(new XmlExtendedAdvancedJunitTest("testSetup"));
        suite.addTest(new XmlExtendedAdvancedJunitTest("testEL254937"));
        suite.addTest(new XmlExtendedAdvancedJunitTest("testGF1894"));
        suite.addTest(new XmlExtendedAdvancedJunitTest("testManAndWoman"));

        suite.addTest(new XmlExtendedAdvancedJunitTest("testForRedirectorsAndInterceptors"));
        suite.addTest(new XmlExtendedAdvancedJunitTest("testForExceptionsFromInterceptors"));
        suite.addTest(new XmlExtendedAdvancedJunitTest("testCacheAccessCount"));
        suite.addTest(new XmlExtendedAdvancedJunitTest("testCacheAccessAppendLock"));

        return suite;
    }

    public void testForRedirectorsAndInterceptors() {
        ClassDescriptor descriptor = getPersistenceUnitServerSession().getDescriptor(Address.class);
        assertNotNull("CacheInterceptor was not set on decriptor", descriptor.getCacheInterceptorClassName());
        assertNotNull("All queries default redirector was not set on decriptor", descriptor.getDefaultQueryRedirector());
        assertNotNull("Read All queries default redirector was not set on decriptor", descriptor.getDefaultReadAllQueryRedirector());
        assertNotNull("Read Object queries default redirector was not set on decriptor", descriptor.getDefaultReadObjectQueryRedirector());
        assertNotNull("Report queries default redirector was not set on decriptor", descriptor.getDefaultReportQueryRedirector());
        assertNotNull("Insert queries default redirector was not set on decriptor", descriptor.getDefaultInsertObjectQueryRedirector());
        assertNotNull("Update queries default redirector was not set on decriptor", descriptor.getDefaultUpdateObjectQueryRedirector());
        assertNotNull("Delete queries default redirector was not set on decriptor", descriptor.getDefaultDeleteObjectQueryRedirector());
    }
    public void testForExceptionsFromInterceptors() {
        ClassDescriptor descriptor = getPersistenceUnitServerSession().getDescriptor(Address.class);
        CacheAuditor interceptor = (CacheAuditor) getPersistenceUnitServerSession().getIdentityMapAccessorInstance().getIdentityMap(descriptor);
        interceptor.setShouldThrow(true);
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address addr = new Address();
            addr.setCity("WhaHa");
            addr.setProvince("NFLD");
            em.persist(addr);
            commitTransaction(em);
            beginTransaction(em);
            em.remove(addr);
            commitTransaction(em);
            fail("There was no Optimistic Lock Exception");
        } catch (RollbackException e) {
            assertTrue("Not caused by OptimisticLockException", (e.getCause() instanceof jakarta.persistence.OptimisticLockException));

        }finally{
            interceptor.setShouldThrow(false);
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testCacheAccessAppendLock() {
        ClassDescriptor descriptor = getPersistenceUnitServerSession().getDescriptor(Address.class);
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        em.persist(emp);
        Address address = new Address("SomeStreet", "SomeCity", "SomeProvince", "SomeCountry", "S0M1O1");
        em.persist(address);
        Address address2 = new Address("SomeStreet2", "SomeCity2", "SomeProvince2", "SomeCountry2", "S0M2O2");
        em.persist(address2);
        emp.setAddress(address);
        commitTransaction(em);
        closeEntityManager(em);
        em = createEntityManager();
        CacheAuditor interceptor = (CacheAuditor) getPersistenceUnitServerSession().getIdentityMapAccessorInstance().getIdentityMap(descriptor);
        interceptor.resetAccessCount();
        try{
            emp = em.find(Employee.class, emp.getId());
            address2 = em.find(Address.class, address2.getId());
            interceptor.remove(address2.getId(), address2);
            beginTransaction(em);
            emp.setAddress(address2);
            commitTransaction(em);
            assertTrue("AppendLock identified as merge", interceptor.getLastAcquireNoWait() != null && !interceptor.getLastAcquireNoWait());
        }finally{
            interceptor.resetAccessCount();
            closeEntityManager(em);
        }
    }

    public void testCacheAccessCount() {
        ClassDescriptor descriptor = getPersistenceUnitServerSession().getDescriptor(Address.class);
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        em.persist(emp);
        Address address = new Address("SomeStreet", "SomeCity", "SomeProvince", "SomeCountry", "S0M1O1");
        em.persist(address);
        emp.setAddress(address);
        commitTransaction(em);
        closeEntityManager(em);
        clearCache();
        em = createEntityManager();
        CacheAuditor interceptor = (CacheAuditor) getPersistenceUnitServerSession().getIdentityMapAccessorInstance().getIdentityMap(descriptor);
        interceptor.resetAccessCount();
        try{
            em.find(Address.class, (int) System.currentTimeMillis());
            assertEquals("To many calls to cache for missing Entity", 1, interceptor.getAccessCount());
        }finally{
            interceptor.resetAccessCount();
            closeEntityManager(em);
        }
        clearCache();
        interceptor = (CacheAuditor) getPersistenceUnitServerSession().getIdentityMapAccessorInstance().getIdentityMap(descriptor);
        em = createEntityManager();
        descriptor.setShouldLockForClone(false);
        try{
            Employee localEmp = em.find(Employee.class, emp.getId());
            localEmp.getAddress().getCity();
            assertEquals("To many calls to cache for loading relationship ", 1, interceptor.getAccessCount());
        }finally{
            interceptor.resetAccessCount();
            descriptor.setShouldLockForClone(true);
            closeEntityManager(em);
        }
    }
}
