/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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


package org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.owning;

import java.util.ArrayList;
import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.SecurityBadge;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsIncompleteOwningJUnitTestCase extends JUnitTestCase {
    private static Integer employeeId;
    
    public EntityMappingsIncompleteOwningJUnitTestCase() {
        super();
    }
    
    public EntityMappingsIncompleteOwningJUnitTestCase(String name) {
        super(name);
    }
    
    public void setUp() {try{super.setUp();}catch(Exception x){}}
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Owning Model");
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testSetup"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testCreateEmployee"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testReadEmployee"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testUpdateEmployee"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testDeleteEmployee"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession();
        new AdvancedTableCreator().replaceTables(session);
        clearCache();
    }
    
    public void testCreateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();		
            ArrayList projects = new ArrayList();
            projects.add(ModelExamples.projectExample1());
            projects.add(ModelExamples.projectExample2());
            employee.setProjects(projects);
            employee.setAddress(ModelExamples.addressExample1());
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
        assertTrue("Error deleting Employee", em.find(Employee.class, employeeId) == null);
    }

    public void testReadEmployee() {
        Employee employee = createEntityManager().find(Employee.class, employeeId);
        assertTrue("Error reading Employee", employee.getId() == employeeId);
    }

    public void testUpdateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = em.find(Employee.class, employeeId);
            employee.setSecurityBadge(new SecurityBadge(69));
            em.merge(employee);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        Employee newEmployee = em.find(Employee.class, employeeId);
        assertTrue("Error updating Employee's Security Badge", newEmployee.getSecurityBadge().getBadgeNumber() == 69);
    }

}
