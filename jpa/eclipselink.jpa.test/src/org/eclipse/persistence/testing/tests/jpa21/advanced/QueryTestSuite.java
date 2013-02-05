/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;

public class QueryTestSuite extends JUnitTestCase {

    public QueryTestSuite() {}
    
    public QueryTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("QueryTestSuite");
        
        suite.addTest(new QueryTestSuite("testSetup"));
        
        suite.addTest(new QueryTestSuite("testQueryParameterPositional"));
        suite.addTest(new QueryTestSuite("testQueryParameterNamed"));
        suite.addTest(new QueryTestSuite("testTypedQueryParameter"));
        suite.addTest(new QueryTestSuite("testLockMode"));
        suite.addTest(new QueryTestSuite("testIncorrectCreateCriteriaQuery"));
        
        return suite;
    }    
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testQueryParameterPositional(){
        EntityManager em = createEntityManager();
        Query query = em.createNamedQuery("jpa21Employee.findAllEmployeesByFirstNameAndLastNamePos").setParameter(1, "Bob").setParameter(2, "Way");
        int i = 0;
        for (Parameter parameter: query.getParameters()){
            assertTrue("Parameter returned wrong position.", parameter.getPosition() != null );
            assertTrue("Parameter returned name.", parameter.getName() == null );
            i++;
            assertTrue("Parameter returned wrong type.", parameter.getParameterType().equals(String.class));
        }
        try{
            query.getParameter(1, Integer.class);
            fail("Exception not thrown for incorrect query type.");
        } catch (IllegalStateException e){}
        query = em.createNamedQuery("jpa21Employee.findAllEmployeesByFirstNameAndLastNamePos").setParameter(1, "Bob");
        try{
            query.getParameterValue(2);
            fail("Exception not thrown for unbound parameter.");
        } catch (IllegalArgumentException e){}
    }
    
    public void testQueryParameterNamed(){
        EntityManager em = createEntityManager();
        Query query = em.createNamedQuery("jpa21Employee.findAllEmployeesByFirstNameAndLastNameName").setParameter("firstName", "Bob").setParameter("lastName", "Way");
        int i = 0;
        for (Parameter parameter: query.getParameters()){
            assertTrue("Parameter returned wrong position.", parameter.getPosition() == null );
            assertTrue("Parameter returned name.", parameter.getName() != null );
            i++;
            assertTrue("Parameter returned wrong type.", parameter.getParameterType().equals(String.class));
        }
    }
    
    public void testTypedQueryParameter(){
        EntityManager em = createEntityManager();
        TypedQuery<Employee> query = em.createQuery("select e from Employee e where e.firstName = :firstName", Employee.class);
        Parameter parameter = query.getParameter("firstName");
        assertTrue("Parameter did not return correct type", parameter.getParameterType().equals(String.class));
    }
    
    public void testLockMode(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("update Employee e set e.firstName = 'Tom'");
        try{
            query.getLockMode();
            fail("Exception not thrown when getting lock mode from an update query.");
        } catch (IllegalStateException e){}
        try{
            query.setLockMode(LockModeType.OPTIMISTIC);
            fail("Exception not thrown when setting lock mode for an update query.");
        } catch (IllegalStateException e){}
    }
    
    public void testIncorrectCreateCriteriaQuery(){
        EntityManager em = createEntityManager();
        try {
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = qbuilder.createQuery();
            em.createQuery(cquery);
            fail("IllegalArgumentException not thrown for incorrect create of CriteraQuery.");
        } catch (IllegalArgumentException e){}
    }
    
}
