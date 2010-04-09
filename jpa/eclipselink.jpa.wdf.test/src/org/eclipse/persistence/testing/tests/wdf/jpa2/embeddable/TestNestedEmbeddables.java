/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa2.embeddable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa2.employee.Address;
import org.eclipse.persistence.testing.models.wdf.jpa2.employee.ContactInfo;
import org.eclipse.persistence.testing.models.wdf.jpa2.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa2.employee.Person;
import org.eclipse.persistence.testing.tests.wdf.jpa2.JPA2Base;
import org.junit.Assert;
import org.junit.Test;

public class TestNestedEmbeddables extends JPA2Base {

    private static final String FRITZ = "Fritz";
    private static final String FELDWEG = "Feldweg";
    private static final String HOPP = "Dietmar-Hopp-Allee";
    private static final String SALUTATION = "Sir";
    private static final String ZIP = "08540";
    private static final String SUNSET_BVLD = "Sunset Bvld.";
    private static final String CA = "CA";
    private static final String ORANGE = "Orange";

    @Test
    @Skip(server=true)
    public void testNonNested() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Person person = new Person();
            person.setSsn(1234);
            person.setName("Emil");

            em.persist(person);

            env.commitTransaction(em);

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Skip(server=true)
    public void testNested() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Address address = new Address();
            address.setCounty(ORANGE);
            address.setState(CA);
            address.setStreet(SUNSET_BVLD);
            address.setZipcode(ZIP);
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setSalutation(SALUTATION);
            contactInfo.setAddress(address);
            Employee employee = new Employee();
            employee.setSsn(5678);
            employee.setName("Hans");
            employee.setContactinfo(contactInfo);

            env.beginTransaction(em);
            em.persist(employee);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            Employee employee2 = em.find(Employee.class, 5678);
            assertNotNull(employee2);

            ContactInfo contactInfo2 = employee2.getContactinfo();
            assertNotNull(contactInfo2);
            assertEquals(SALUTATION, contactInfo2.getSalutation());

            Address address2 = contactInfo2.getAddress();
            assertNotNull(address2);
            Assert.assertEquals(ORANGE, address.getCounty());
            Assert.assertEquals(CA, address.getState());
            Assert.assertEquals(ZIP, address.getZipcode());
            Assert.assertEquals(SUNSET_BVLD, address.getStreet());

            address2.setStreet(HOPP);
            env.commitTransactionAndClear(em);

            Employee employee3 = em.find(Employee.class, 5678);
            assertNotNull(employee3);
            ContactInfo contactInfo3 = employee3.getContactinfo();
            assertNotNull(contactInfo3);
            Address address3 = contactInfo3.getAddress();
            assertNotNull(address3);
            assertEquals(HOPP, address3.getStreet());

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Skip(server=true)
    public void testNestedOuterNull() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Employee employee = new Employee();
            employee.setSsn(2345);
            employee.setName("Peter");

            env.beginTransaction(em);
            em.persist(employee);
            env.commitTransactionAndClear(em);

            Employee employee2 = em.find(Employee.class, 2345);
            assertNotNull(employee2);

            ContactInfo contactinfo = employee2.getContactinfo();
            if (contactinfo != null) {
                assertNull(contactinfo.getSalutation());
                Address address = contactinfo.getAddress();
                if (address != null) {
                    assertNull(address.getCounty());
                    assertNull(address.getState());
                    assertNull(address.getZipcode());
                    assertNull(address.getStreet());
                }
            }

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Skip(server=true)
    public void testNestedInnerNull() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            ContactInfo contactInfo1 = new ContactInfo();
            contactInfo1.setSalutation(SALUTATION);

            Employee employee = new Employee();
            employee.setSsn(3456);
            employee.setName("Peter");
            employee.setContactinfo(contactInfo1);

            env.beginTransaction(em);
            em.persist(employee);
            env.commitTransactionAndClear(em);

            Employee employee2 = em.find(Employee.class, 3456);
            assertNotNull(employee2);

            ContactInfo contactInfo2 = employee2.getContactinfo();
            assertNotNull(contactInfo2);
            assertEquals(SALUTATION, contactInfo2.getSalutation());

            Address address = contactInfo2.getAddress();
            if (address != null) {
                assertNull(address.getCounty());
                assertNull(address.getState());
                assertNull(address.getZipcode());
                assertNull(address.getStreet());
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Skip(server=true)
    public void testNestedQuery() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Address address = new Address();
            address.setCounty(ORANGE);
            address.setState(CA);
            address.setStreet(FELDWEG);
            address.setZipcode(ZIP);
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setSalutation(SALUTATION);
            contactInfo.setAddress(address);
            Employee employee = new Employee();
            employee.setSsn(6789);
            employee.setName(FRITZ);
            employee.setContactinfo(contactInfo);

            env.beginTransaction(em);
            em.persist(employee);
            env.commitTransactionAndClear(em);

            Query query = em.createQuery("select e.name from Employee2 e where e.contactinfo.address.street = :street");
            query.setParameter("street", FELDWEG);
            @SuppressWarnings("unchecked")
            List<String> names = query.getResultList();
            assertEquals(1, names.size());
            assertEquals(FRITZ, names.get(0));
        } finally {
            closeEntityManager(em);
        }
    }

}
