/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     06/20/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls       
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     09/13/2013-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     09/27/2012-2.5 Guy Pelletier
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     11/05/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import static javax.persistence.ParameterMode.INOUT;
import static javax.persistence.ParameterMode.OUT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLCall;

import org.eclipse.persistence.testing.models.jpa21.advanced.Address;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeeDetails;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa21.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Project;
import org.eclipse.persistence.testing.models.jpa21.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;

import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.ConstructorResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class EntityManagerFactoryTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;

    public EntityManagerFactoryTestSuite() {}
    
    public EntityManagerFactoryTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache();
    }
    
    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }
        
        super.tearDown();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerFactoryTestSuite");
        
        suite.addTest(new EntityManagerFactoryTestSuite("testSetup"));
        
        // These tests call stored procedures that return a result set. 
        suite.addTest(new EntityManagerFactoryTestSuite("testAddNamedQuery"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getServerSession());
        clearCache();
    }
    
    public void testAddNamedQuery(){
        EntityManagerImpl em = (EntityManagerImpl)createEntityManager();
        EntityManagerFactory factory = em.getEntityManagerFactory();
        List<Object[]> names = em.createQuery("Select e.firstName, count(e.firstName) as c from Employee e group by e.firstName order by c").getResultList();
        String name = (String) names.get(names.size()-1)[0];
        Query query = em.createQuery("Select e from Employee e where e.firstName = :p1 order by e.id");
        query.setParameter("p1", name);
        assertTrue("Unable to retrieve parameter value from query", query.getParameterValue("p1").equals(name));
        List<Employee> firstResult = query.getResultList();
        factory.addNamedQuery("Select_Employee_by_first_name", query);
        closeEntityManager(em);
        em = (EntityManagerImpl) createEntityManager();
        
        Query namedQuery = em.createNamedQuery("Select_Employee_by_first_name");
        assertFalse("Named query retains parameter values from original query", namedQuery.isBound(namedQuery.getParameter("p1")));
        namedQuery.setParameter("p1", name);
        List<Employee> secondResult = namedQuery.getResultList();
        for (int i = firstResult.size()-1; i> -1; --i){
            assertEquals("Results do not match", firstResult.get(i).getId(), secondResult.get(i).getId());
        }
        names = em.createQuery("Select e.lastName, count(e.lastName) as c from Employee e group by e.lastName order by c").getResultList();
        name = (String) names.get(names.size()-1)[0];
        query = em.createQuery("Select e from Employee e where e.lastName = :p1 order by e.id");
        query.setParameter("p1", name);
        firstResult = query.getResultList();
        factory.addNamedQuery("Select_Employee_by_first_name", query);
        closeEntityManager(em);
        em = (EntityManagerImpl) createEntityManager();
        
        namedQuery = em.createNamedQuery("Select_Employee_by_first_name");
        assertFalse("Named query retains parameter values from original query", namedQuery.isBound(namedQuery.getParameter("p1")));
        namedQuery.setParameter("p1", name);
        secondResult = namedQuery.getResultList();
        for (int i = firstResult.size()-1; i> -1; --i){
            assertEquals("Results do not match", firstResult.get(i).getId(), secondResult.get(i).getId());
        }
    }
    
}
