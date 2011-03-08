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
 *     tware - testing for delimited identifiers in JPA 2.0
 *     03/07/2011-2.3 Chris Delahunt 
 *       - bug 338585: Issue while inserting blobs with delimited identifiers on Oracle Database 
 ******************************************************************************/  

package org.eclipse.persistence.testing.tests.jpa.delimited;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.delimited.*;

/**
 * Test the EntityManager API using a model that uses delimited-identifiers
 */
public class DelimitedPUTestSuite extends JUnitTestCase {

    private static Employee emp = null;
    private static Address addr = null;
    private static PhoneNumber pn = null;
    private static Employee emp2 = null;
    private static LargeProject lproj = null;
    private static SmallProject sproj = null;
    private static SimpleImage simage = null;

    public DelimitedPUTestSuite() {
        super();
    }

    public DelimitedPUTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DelimitedPUTestSuite");
        suite.addTest(new DelimitedPUTestSuite("testPopulate"));
        suite.addTest(new DelimitedPUTestSuite("testReadEmployee"));
        suite.addTest(new DelimitedPUTestSuite("testNativeQuery"));
        suite.addTest(new DelimitedPUTestSuite("testUpdateEmployee"));
        suite.addTest(new DelimitedPUTestSuite("testReadImage"));

        return suite;
    }

    public void testPopulate(){
        EntityManager em = createEntityManager("delimited");
        beginTransaction(em);

        createEmployee();
        createAddress();
        createPhoneNumber();
        createEmployee2();
        createLargeProject();
        createSmallProject();

        em.persist(emp);
        em.persist(addr);
        em.persist(pn);
        em.persist(emp2);
        em.persist(lproj);
        em.persist(sproj);
        em.persist(createImage());

        addr.getEmployees().add(emp);
        emp.setAddress(addr);

        emp.addPhoneNumber(pn);
        pn.setOwner(emp);

        emp.addManagedEmployee(emp2);
        emp2.setManager(emp);

        lproj.setTeamLeader(emp);
        emp.addProject(lproj);
        lproj.addTeamMember(emp2);
        emp2.addProject(lproj);

        sproj.setTeamLeader(emp2);
        emp2.addProject(sproj);

        commitTransaction(em);

        clearCache("delimited");
        closeEntityManager(em);
    }

    public void testReadEmployee() {
        EntityManager em = createEntityManager("delimited");

        Employee returnedEmp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'Del' and e.lastName = 'Imited'").getSingleResult();
        Assert.assertTrue("testCreateEmployee emp not properly persisted", getServerSession("delimited").compareObjects(emp, returnedEmp));

        Employee returnedWorker = (Employee)em.createQuery("select e from Employee e where e.firstName = 'Art' and e.lastName = 'Vandeleigh'").getSingleResult();
        Assert.assertTrue("testCreateEmployee emp2 not properly persisted", getServerSession("delimited").compareObjects(emp2, returnedWorker));
        closeEntityManager(em);
    }

    public void testNativeQuery(){
        clearCache("delimited");
        EntityManager em = createEntityManager("delimited");
        Query query = em.createNamedQuery("findAllSQLEmployees");
        // Native SQL may need to be different on some platforms.
        DatabasePlatform platform = getServerSession("delimited").getPlatform();
        if (platform.getStartDelimiter() != "\"") {
            query = em.createNativeQuery("select * from " + platform.getStartDelimiter() + "CMP3_DEL_EMPLOYEE" + platform.getEndDelimiter(), Employee.class);
        }
        List result = query.getResultList();
        Assert.assertTrue("testNativeQuery did not return result ", result.size() >= 2);
        closeEntityManager(em);
    }

    public void testUpdateEmployee() {
        EntityManager em = createEntityManager("delimited");
        try {
            beginTransaction(em);
            Employee returnedEmp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'Del' and e.lastName = 'Imited'").getSingleResult();

            returnedEmp.setFirstName("Redel");
            PhoneNumber pn = new PhoneNumber();
            pn.setType("home");
            pn.setAreaCode("123");
            returnedEmp.addPhoneNumber(pn);
            returnedEmp.getAddress().setCity("Reident");
            em.flush();
            clearCache("delimited");
            returnedEmp = em.find(Employee.class, returnedEmp.getId());
            Assert.assertTrue("testUpdateEmployee did not properly update firstName", returnedEmp.getFirstName().equals("Redel"));
            Assert.assertTrue("testUpdateEmployee did not properly update address", returnedEmp.getAddress().getCity().equals("Reident"));
            Assert.assertTrue("testUpdateEmployee did not properly add phone number", returnedEmp.getPhoneNumbers().size() == 2);;
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    //bug 338585: Issue while inserting blobs with delimited identifiers on Oracle Database 
    // mostly will only occur on Oracle8+ platforms, but we needed a general delimited blob/clob test
    public void testReadImage() {
        EntityManager em = createEntityManager("delimited");
        try {
            beginTransaction(em);
            SimpleImage returnedImage = (SimpleImage)em.createQuery("select e from SimpleImage e where e.id = "+simage.getId()).getSingleResult();
            em.refresh(returnedImage);
            Assert.assertTrue("SimpleImage was not properly read back in", getServerSession("delimited").compareObjects(simage, returnedImage));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    private static Employee createEmployee(){
        emp = new Employee();
        emp.setFirstName("Del");
        emp.setLastName("Imited");
        emp.setFemale();
        emp.addResponsibility("Supervise projects");
        emp.addResponsibility("Delimit Identifiers");
        Date startDate = Date.valueOf("2009-06-01");
        Date endDate = Date.valueOf("2009-08-01");
        EmploymentPeriod period = new EmploymentPeriod(startDate, endDate);
        emp.setPeriod(period);
        return emp;
    }

    private static Employee createEmployee2(){
        emp2 = new Employee();
        emp2.setFirstName("Art");
        emp2.setLastName("Vandeleigh");
        emp2.setMale();
        return emp2;
    }

    private static Address createAddress(){
        addr = new Address();
        addr.setCity("Ident");
        addr.setCountry("Ifier");
        addr.setPostalCode("A0A1B1");
        addr.setProvince("Delimitia");
        addr.setStreet("Del St.");
        return addr;
    }

    private static PhoneNumber createPhoneNumber(){
        pn = new PhoneNumber();
        pn.setAreaCode("709");
        pn.setNumber("5551234");
        pn.setType("work");
        return pn;
    }

    private static LargeProject createLargeProject(){
        lproj = new LargeProject();
        lproj.setBudget(10000000);
        lproj.setDescription("Allow delimited identifiers in persistence.xml");
        lproj.setName("PUDefaults");
        return lproj;
    }

    private static SmallProject createSmallProject(){
        sproj = new SmallProject();
        sproj.setDescription("Allow delimited identifiers in annotations");
        sproj.setName("Annotations");
        return sproj;
    }

    private static SimpleImage createImage(){
        simage = new SimpleImage();
        simage.setPicture(org.eclipse.persistence.testing.models.jpa.lob.ImageSimulator.initObjectByteBase(100));
        simage.setScript(org.eclipse.persistence.testing.models.jpa.lob.ImageSimulator.initStringBase(100));
        return simage;
    }
}
