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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     04/09/2010-2.1 Guy Pelletier
//       - 307050: Add defaults for access methods of a VIRTUAL access type
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     07/03/2011-2.3.1 Guy Pelletier
//       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.mappings.CascadeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Dealer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Project;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class XmlAdvancedTest extends JUnitTestCase {
    private static Integer employeeId, extendedEmployeeId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;

    public XmlAdvancedTest() {
        super();
    }

    public XmlAdvancedTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "default";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Advanced Model - default");
        suite.addTest(new XmlAdvancedTest("testSetup"));
        suite.addTest(new XmlAdvancedTest("testCreateEmployee"));
        suite.addTest(new XmlAdvancedTest("testReadEmployee"));
        suite.addTest(new XmlAdvancedTest("testNamedNativeQueryOnAddress"));
        suite.addTest(new XmlAdvancedTest("testNamedQueryOnEmployee"));
        suite.addTest(new XmlAdvancedTest("testUpdateEmployee"));
        suite.addTest(new XmlAdvancedTest("testRefreshNotManagedEmployee"));
        suite.addTest(new XmlAdvancedTest("testRefreshRemovedEmployee"));
        suite.addTest(new XmlAdvancedTest("testDeleteEmployee"));
        suite.addTest(new XmlAdvancedTest("testUnidirectionalPersist"));
        suite.addTest(new XmlAdvancedTest("testUnidirectionalUpdate"));
        suite.addTest(new XmlAdvancedTest("testUnidirectionalFetchJoin"));
        suite.addTest(new XmlAdvancedTest("testUnidirectionalTargetLocking_AddRemoveTarget"));
        suite.addTest(new XmlAdvancedTest("testUnidirectionalTargetLocking_DeleteSource"));
        suite.addTest(new XmlAdvancedTest("testXMLEntityMappingsWriteOut"));
        suite.addTest(new XmlAdvancedTest("testInheritFromNonMapped"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = getPersistenceUnitServerSession();
        AdvancedTableCreator atc = new AdvancedTableCreator();
        atc.replaceTables(session);
        // Populate the database with our examples.
        EmployeePopulator employeePopulator = new EmployeePopulator(supportsStoredProcedures());
        employeePopulator.buildExamples();
        employeePopulator.persistExample(session);
        clearCache();
    }

    public void testCreateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            List<Project> projects = new ArrayList<>();
            projects.add(ModelExamples.projectExample1());
            projects.add(ModelExamples.projectExample2());
            employee.setProjects(projects);
            em.persist(employee);
            employeeId = employee.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

    }

    public void testDeleteEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Employee.class, employeeId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Employee", em.find(Employee.class, employeeId));
    }

    public void testNamedNativeQueryOnAddress() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Address address1 = ModelExamples.addressExample1();
            em.persist(address1);
            Address address2 = ModelExamples.addressExample2();
            em.persist(address2);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        Query query = em.createNamedQuery("findAllXMLAddresses");
        List<?> addresses = query.getResultList();
        assertNotNull("Error executing named native query 'findAllXMLAddresses'", addresses);
    }

    public void testNamedQueryOnEmployee() {
        Query query = createEntityManager().createNamedQuery("findAllXMLEmployeesByFirstName");
        query.setParameter("firstname", "Brady");
        Employee employee = (Employee) query.getSingleResult();
        assertNotNull("Error executing named query 'findAllXMLEmployeesByFirstName'", employee);
    }

    public void testReadEmployee() {
        Employee employee = createEntityManager().find(Employee.class, employeeId);
        assertEquals("Error reading Employee", (int) employee.getId(), employeeId.intValue());
    }

    public void testUpdateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        int version = 0;

        try {
            Employee employee = em.find(Employee.class, employeeId);
            version = employee.getVersion();
            employee.setSalary(50000);

            em.merge(employee);
            commitTransaction(em);

            // Clear cache and clear the entity manager
            clearCache();
            em.clear();

            Employee emp = em.find(Employee.class, employeeId);
            assertEquals("Error updating Employee", 50000, emp.getSalary());
            assertEquals("Version field not updated", emp.getVersion(), version + 1);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);
    }

    public void testRefreshNotManagedEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setGivenName("NotManaged");
            em.refresh(emp);
            fail("entityManager.refresh(notManagedObject) didn't throw exception");
        } catch (IllegalArgumentException illegalArgumentException) {
            // expected behavior
        } catch (RuntimeException e ) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testRefreshRemovedEmployee() {
        // find an existing or create a new Employee
        String firstName = "testRefreshRemovedEmployee";
        Employee emp;
        EntityManager em = createEntityManager();
        List<Employee> result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'", Employee.class).getResultList();
        if(!result.isEmpty()) {
            emp = result.get(0);
        } else {
            emp = new Employee();
            emp.setGivenName(firstName);
            // persist the Employee
            beginTransaction(em);
            try {
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }

        beginTransaction(em);
        try{
            emp = em.find(Employee.class, emp.getId());

            if (getPersistenceUnitServerSession().getPlatform().isSymfoware()){
                // Symfoware does not support deleteall with multiple table
                em.createNativeQuery("DELETE FROM CMP3_XML_EMPLOYEE WHERE F_NAME = '"+firstName+"'").executeUpdate();
            } else {
                // delete the Employee from the db
                em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            }

            // refresh the Employee - should fail with EntityNotFoundException
            em.refresh(emp);
            fail("entityManager.refresh(removedObject) didn't throw exception");
        } catch (EntityNotFoundException entityNotFoundException) {
            // expected behavior
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testContainsRemovedEmployee() {
        // find an existing or create a new Employee
        String firstName = "testContainsRemovedEmployee";
        Employee emp;
        EntityManager em = createEntityManager();
        List<Employee> result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'", Employee.class).getResultList();
        if(!result.isEmpty()) {
            emp = result.get(0);
        } else {
            emp = new Employee();
            emp.setGivenName(firstName);
            // persist the Employee
            beginTransaction(em);
            try{
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }

        boolean containsRemoved = true;
        beginTransaction(em);
        try{
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            containsRemoved = em.contains(emp);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        assertFalse("entityManager.contains(removedObject)==true ", containsRemoved);
    }

    public void testSubString() {
        // find an existing or create a new Employee
        String firstName = "testSubString";
        Employee emp;
        EntityManager em = createEntityManager();
        List<Employee> result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'", Employee.class).getResultList();
        if(!result.isEmpty()) {
            emp = result.get(0);
        } else {
            emp = new Employee();
            emp.setGivenName(firstName);
            // persist the Employee
            beginTransaction(em);
            try{
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }

        int firstIndex = 1;
        int lastIndex = firstName.length();
        List<?> employees = em.createQuery("SELECT object(e) FROM XMLEmployee e where e.firstName = substring(:p1, :p2, :p3)").
            setParameter("p1", firstName).
            setParameter("p2", firstIndex).
            setParameter("p3", lastIndex).
            getResultList();

        // clean up
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        assertFalse("employees.isEmpty()==true ", employees.isEmpty());
    }

    public void testUnidirectionalPersist() {
        String lastName = "testUnidirectionalPersist";

        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName);

        // clear cache
        clearCache();

        // read the persisted employees back
        EntityManager em = createEntityManager();
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session;
        if (isOnServer()) {
            session = getPersistenceUnitServerSession();
        } else {
            session = ((EntityManagerImpl)em).getServerSession();
        }
        closeEntityManager(em);

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            //clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Persisted " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        String errorMsg = "";
        for(int i=0; i<employeesPersisted.size(); i++) {
            for(int j=0; j<employeesRead.size(); j++) {
                if(employeesPersisted.get(i).getFirstName().equals(employeesRead.get(j).getFirstName())) {
                    if(!session.compareObjects(employeesPersisted.get(i), employeesRead.get(j))) {
                        errorMsg += "Employee " + employeesPersisted.get(i).getFirstName() +"  was not persisted correctly.";
                    }
                }
            }
        }

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalUpdate() {
        String lastName = "testUnidirectionalUpdate";
        // em used for both persist and update
        EntityManager em = createEntityManager();
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName, em);
        // update persisted employees:
        beginTransaction(em);
        try {
            // add a new dealer to the first employee
            employeesPersisted.get(0).addDealer(new Dealer("New", lastName));
            // remove a dealer from the second employee
            employeesPersisted.get(1).getDealers().remove(1);
            // move a dealer from the first to the second employee
            employeesPersisted.get(1).addDealer(employeesPersisted.get(0).getDealers().remove(0));
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        // clear cache
        clearCache();

        em = createEntityManager();
        // read the updated employees back
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session;
        if (isOnServer()) {
            session = getPersistenceUnitServerSession();
        } else {
            session = ((EntityManagerImpl)em).getServerSession();
        }

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            // clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Updated " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        beginTransaction(em);
        String errorMsg = "";
        try{
            for(int i=0; i<employeesPersisted.size(); i++) {
                for(int j=0; j<employeesRead.size(); j++) {
                    Employee emp1 = em.find(Employee.class, employeesPersisted.get(i).getId());
                    Employee emp2 = em.find(Employee.class, employeesRead.get(j).getId());
                    if(emp1.getFirstName().equals(emp2.getFirstName())) {
                        if(!session.compareObjects(emp1, emp2)) {
                            errorMsg += "Employee " + emp1.getFirstName() +"  was not updated correctly.";
                        }
                    }
                }
            }
        } finally {
           if(this.isTransactionActive(em)) {
               rollbackTransaction(em);
              }
               closeEntityManager(em);
        }

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalFetchJoin() {
        String lastName = "testUnidirectionalFetchJoin";

        // persist employees
        persistEmployeesWithUnidirectionalMappings(lastName);

        // clear cache
        clearCache();

        EntityManager em = createEntityManager();
        // read the persisted employees back - without fetch join
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        closeEntityManager(em);

        // clear cache
        clearCache();

        // read the persisted employees back - with fetch join.
        em = createEntityManager();
        List<Employee> employeesReadWithFetchJoin = em.createQuery("SELECT e FROM XMLEmployee e JOIN FETCH e.dealers WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session;
        if (isOnServer()) {
            session = getPersistenceUnitServerSession();
        } else {
            session = ((EntityManagerImpl)em).getServerSession();
        }

        closeEntityManager(em);

        // verify that the persisted and read employees are the same.
        // The comparison cascades to all references and requires the same state of indirection:
        // it fails in case an object has triggered indirection for particular attribute and compared object's indirection for this attribute is not triggered.
        // The expected result of join fetch query is Employee.dealers being triggered - so need to trigger it on the control collection (getDealers.size() does that);
        // also the expected result should have an object for each row returned - therefore number of inclusions of each Employee equals its dealers.size()
        List<Employee> employeesControl = new ArrayList<>();
        for(int i=0; i<employeesRead.size(); i++) {
            int nDialers = employeesRead.get(i).getDealers().size();
            for(int j=0; j<nDialers; j++) {
                employeesControl.add(employeesRead.get(i));
            }
        }
        String errorMsg = JoinedAttributeTestHelper.compareCollections(employeesControl, employeesReadWithFetchJoin, session.getClassDescriptor(Employee.class), session);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalTargetLocking_AddRemoveTarget() {
        String lastName = "testUnidirectionalTargetLocking_ART";

        EntityManager em = createEntityManager();
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName, em);

        // remove a dealer from the second employee:
        Dealer dealer;
        beginTransaction(em);
    try {
            Employee emp1 = em.find(Employee.class, employeesPersisted.get(1).getId());
            dealer = emp1.getDealers().remove(1);
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        String errorMsg = "";

        // verify the version both in the cache and in the db
        int version2 = getVersion(em, dealer);
        if(version2 != 2) {
            errorMsg += "In the cache the removed dealer's version is " + version2 + " (2 was expected); ";
        }
        // add the dealer to the first employee:
        //version2 = getDealerVersionFromDB(em, dealer);
        beginTransaction(em);
        try {
            Dealer dealer2 = em.find(Dealer.class, dealer.getId());
            em.refresh(dealer2);
            version2 = getVersion(em, dealer2);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        if(version2 != 2) {
            errorMsg += "In the db the removed dealer's version is " + version2 + " (2 was expected); ";
        }
        beginTransaction(em);
        Dealer dealer3;
        try {
            Employee emp2 = em.find(Employee.class, employeesPersisted.get(1).getId());
            dealer3 = em.find(Dealer.class, dealer.getId());
            emp2.getDealers().add(dealer3);
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        // verify the version both in the cache and in the db
        int version3 = getVersion(em, dealer3);
        if(version3 != 3) {
            errorMsg += "In the cache the added dealer's version is " + version3 + " (3 was expected); ";
        }

        beginTransaction(em);
        try {
            Dealer dealer4 = em.find(Dealer.class, dealer3.getId());
            em.refresh(dealer4);
            version3 = getVersion(em, dealer4);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        if(version3 != 3) {
            errorMsg += "In the db the added dealer's version is " + version3 + " (3 was expected)";
        }

        closeEntityManager(em);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalTargetLocking_DeleteSource() {
        String lastName = "testUnidirectionalTargetLocking_DS";

        // persist employees (there should be two of them)
        List<Employee> persistedEmployees = persistEmployeesWithUnidirectionalMappings(lastName);
        // cache their dealers' ids
        ArrayList<Integer> dealersIds = new ArrayList<>();
        for(int i=0; i<persistedEmployees.size(); i++) {
            Employee emp = persistedEmployees.get(i);
            for(int j=0; j<emp.getDealers().size(); j++) {
                dealersIds.add(emp.getDealers().get(j).getId());
            }
        }

        // clear cache
        clearCache();

        EntityManager em = createEntityManager();
        beginTransaction(em);
        // read the persisted employees
        List<Employee> readEmployees = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();

        // trigger indirection on the second employee's dealers
        readEmployees.get(1).getDealers().size();

        // delete the Employees (there should be two of them).

        try {
            for(Employee emp:  readEmployees) {
                em.remove(emp);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        // find employees' dealers and verify their versions - all should be 2.
        beginTransaction(em);
        String errorMsg = "";
        try{
            for(int i=0; i<dealersIds.size(); i++) {
                Dealer dealer = em.find(Dealer.class, dealersIds.get(i));

                // verify the version both in the cache and in the db
                int version2 = getVersion(em, dealer);
                if(version2 != 2) {
                    errorMsg += "In the cache dealer "+dealer.getFirstName()+"'s version is " + version2 + " (2 was expected); ";
                }
                em.refresh(dealer);

                version2 = getVersion(em, dealer);
                if(version2 != 2) {
                    errorMsg += "In the db dealer "+dealer.getFirstName()+"'s version is " + version2 + " (2 was expected); ";
                }
            }
        } finally {
           if(this.isTransactionActive(em)) {
               rollbackTransaction(em);
           }
        }

        closeEntityManager(em);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    protected int getVersion(EntityManager em, Dealer dealer) {
        Vector<Integer> pk = new Vector<>(1);
        pk.add(dealer.getId());

        if (isOnServer()) {
            return getPersistenceUnitServerSession().getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, getPersistenceUnitServerSession());
        } else {
            return ((EntityManagerImpl) em).getServerSession().getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, ((EntityManagerImpl) em).getServerSession());
        }
    }

    protected List<Employee> createEmployeesWithUnidirectionalMappings(String lastName) {
        int n = 2;
        List<Employee> employees = new ArrayList<>(n);
        for(int i=0; i<n; i++) {
            Employee emp = new Employee();
            emp.setGivenName(Integer.toString(i+1));
            emp.setFamilyName(lastName);
            employees.add(emp);
            for(int j=0; j<n; j++) {
                Dealer dealer = new Dealer();
                dealer.setFirstName(emp.getFirstName() + "_" + (j + 1));
                dealer.setLastName(lastName);
                emp.addDealer(dealer);
                for(int k=0; k<n; k++) {
                    Customer customer = new Customer();
                    customer.setFirstName(dealer.getFirstName() + "_" + (k + 1));
                    customer.setLastName(lastName);
                    dealer.addCustomer(customer);
                }
            }
        }
        return employees;
    }

    protected List<Employee> persistEmployeesWithUnidirectionalMappings(String lastName) {
        EntityManager em = createEntityManager();
        try {
            return persistEmployeesWithUnidirectionalMappings(lastName, em);
        } finally {
            closeEntityManager(em);
        }
    }

    protected List<Employee> persistEmployeesWithUnidirectionalMappings(String lastName, EntityManager em) {
        List<Employee> employees = createEmployeesWithUnidirectionalMappings(lastName);
        beginTransaction(em);
        try {
            for(int i=0; i<employees.size(); i++) {
                em.persist(employees.get(i));
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        return employees;
    }

    protected void deleteEmployeesWithUnidirectionalMappings(String lastName) {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.createQuery("DELETE FROM XMLAdvancedCustomer c WHERE c.lastName = '"+lastName+"'").executeUpdate();
            em.createQuery("DELETE FROM XMLDealer d WHERE d.lastName = '"+lastName+"'").executeUpdate();
            Query q = em.createQuery("SELECT e FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            clearCache();
        }
    }

    public void testXMLEntityMappingsWriteOut() {
        try {
            XMLEntityMappings mappings = new XMLEntityMappings();
            mappings.setPersistenceUnitMetadata(new XMLPersistenceUnitMetadata());
            mappings.getPersistenceUnitMetadata().setPersistenceUnitDefaults(new XMLPersistenceUnitDefaults());
            XMLPersistenceUnitMetadata persistenceUnitMetadata = new XMLPersistenceUnitMetadata();
            persistenceUnitMetadata.setPersistenceUnitDefaults(new XMLPersistenceUnitDefaults());
            mappings.setPersistenceUnitMetadata(persistenceUnitMetadata);
            EntityAccessor entity = new EntityAccessor();
            mappings.setEntities(new ArrayList<>());
            entity.setAttributes(new XMLAttributes());
            entity.getAttributes().setBasicCollections(new ArrayList<>());
            entity.getAttributes().getBasicCollections().add(new BasicCollectionAccessor());
            entity.getAttributes().setOneToManys(new ArrayList<>());
            OneToManyAccessor oneToMany = new OneToManyAccessor();
            oneToMany.setCascade(new CascadeMetadata());
            entity.getAttributes().getOneToManys().add(oneToMany);
            entity.getAttributes().setBasics(new ArrayList<>());
            entity.getAttributes().getBasics().add(new BasicAccessor());

            mappings.getEntities().add(entity);

            XMLEntityMappingsWriter writer = new XMLEntityMappingsWriter();
            FileOutputStream fileOut = new FileOutputStream("XMLWriteOutTest.xml");
            XMLEntityMappingsWriter.write(mappings, fileOut);
            fileOut.close();

            FileInputStream fileIn = new FileInputStream(fileOut.getFD());
            BufferedReader in = new BufferedReader(new InputStreamReader(fileIn));
            HashSet<String> readStrings = new HashSet<>();
            while (in.ready()) {
                readStrings.add(in.readLine());
            }
            in.close();

            // Now look for those empty types that we should not see when not explicitly set ...
            assertFalse("Found cascade on delete element", readStrings.contains("<cascade-on-delete"));
            assertFalse("Found non cacheable element", readStrings.contains("<noncacheable"));
            assertFalse("Found private owned element", readStrings.contains("<private-owned"));
            assertFalse("Found xml mapping metadata complete element", readStrings.contains("<xml-mapping-metadata-complete"));
            assertFalse("Found exclude default mappings element", readStrings.contains("<exclude-default-mappings"));
            assertFalse("Found delimeted identifiers element", readStrings.contains("<delimited-identifiers"));
            assertFalse("Found exclude default listeners element", readStrings.contains("<exclude-default-listeners"));
            assertFalse("Found exclude superclass listeners element", readStrings.contains("<exclude-superclass-listeners"));
            assertFalse("Found return update element", readStrings.contains("<return-update"));
            assertFalse("Found cascade all element", readStrings.contains("<cascade-all"));
            assertFalse("Found cascade persist element", readStrings.contains("<cascade-persist"));
            assertFalse("Found cascade merge element", readStrings.contains("<cascade-merge"));
            assertFalse("Found cascade remove element", readStrings.contains("<cascade-remove"));
            assertFalse("Found cascade refresh element", readStrings.contains("<cascade-refresh"));
            assertFalse("Found cascade detach element", readStrings.contains("<cascade-detach"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred: " + e.getMessage());
        }
    }

    public void testDeleteAll(){
        Map<Class<?>, ClassDescriptor> descriptors = getPersistenceUnitServerSession().getDescriptors();
        ClassDescriptor descriptor = descriptors.get(Department.class);
        OneToManyMapping mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("equipment");
        assertFalse("<delete-all> xml did not work correctly", mapping.mustDeleteReferenceObjectsOneByOne());
    }

    public void testInheritFromNonMapped(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Dealer dealer = new Dealer();
        dealer.setBusinessId("112233");

        try {
            em.persist(dealer);

            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting dealer: " + exception.getMessage());
        }

        clearCache();
        em.clear();
        em = createEntityManager();

        dealer = em.find(Dealer.class, dealer.getId());
        assertEquals("An inherited attribute from a non-mapped object was not persisted.", "112233", dealer.getBusinessId());

    }
}
