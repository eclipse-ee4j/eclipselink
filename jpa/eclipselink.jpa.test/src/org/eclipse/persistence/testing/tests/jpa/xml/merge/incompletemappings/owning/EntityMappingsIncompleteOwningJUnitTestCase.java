/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.owning;

import java.util.ArrayList;
import javax.persistence.EntityManager;

import junit.framework.*;
import junit.extensions.TestSetup;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.Address;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.LargeProject;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.PhoneNumberPK;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.Project;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.SecurityBadge;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning.SmallProject;
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
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testCreateEmployee"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testReadEmployee"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testUpdateEmployee"));
        suite.addTest(new EntityMappingsIncompleteOwningJUnitTestCase("testDeleteEmployee"));
        
        return new TestSetup(suite) {
            
            protected void setUp(){  
            	DatabaseSession session = JUnitTestCase.getServerSession();   
                new AdvancedTableCreator().replaceTables(session);
			}
        
            protected void tearDown() {
                clearCache();
            }
        };
    }
    
    public void testCreateEmployee() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Employee employee = ModelExamples.employeeExample1();		
            ArrayList projects = new ArrayList();
            projects.add(ModelExamples.projectExample1());
            projects.add(ModelExamples.projectExample2());
            employee.setProjects(projects);
            employee.setAddress(ModelExamples.addressExample1());
            em.persist(employee);
            employeeId = employee.getId();
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        
    }
    
    public void testDeleteEmployee() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.remove(em.find(Employee.class, employeeId));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error deleting Employee", em.find(Employee.class, employeeId) == null);
    }

    public void testReadEmployee() {
        Employee employee = (Employee) createEntityManager().find(Employee.class, employeeId);
        assertTrue("Error reading Employee", employee.getId() == employeeId);
    }

    public void testUpdateEmployee() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Employee employee = (Employee) em.find(Employee.class, employeeId);
            employee.setSecurityBadge(new SecurityBadge(69));
            em.merge(employee);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        clearCache();
        Employee newEmployee = (Employee) em.find(Employee.class, employeeId);
        assertTrue("Error updating Employee's Security Badge", newEmployee.getSecurityBadge().getBadgeNumber() == 69);
    }

}
