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
 *     09/21/2012-2.5 Chris Delahunt 
 *       - 367452: JPA 2.1 Specification support for joins with ON clause
 */
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.Project;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;


public class CriteriaQueryTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;

    public CriteriaQueryTestSuite() {}
    
    public CriteriaQueryTestSuite(String name) {
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
        suite.setName("CriteriaQueryTestSuite");

        suite.addTest(new CriteriaQueryTestSuite("testSetup"));
        
        suite.addTest(new CriteriaQueryTestSuite("testOnClause"));
        suite.addTest(new CriteriaQueryTestSuite("testOnClauseWithLeftJoin"));
        suite.addTest(new CriteriaQueryTestSuite("testOnClauseCompareSQL"));

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
    
    // Bug 312146
    // Test join on clause
    public void testOnClause() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join e.address a on a.city = 'Ottawa'");
        List baseResult = query.getResultList();
        
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join address = root.join("address");
        address.on(qb.equal(address.get("city"), "Ottawa"));
        List testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    public void testOnClauseWithLeftJoin() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e left join e.address a on a.city = 'Ottawa' " +
        		"where a.postalCode is not null");
        List baseResult = query.getResultList();
        
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee>cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join address = root.join("address", JoinType.LEFT);
        address.on(qb.equal(address.get("city"), "Ottawa"));
        cq.where(qb.isNotNull(address.get("postalCode")));
        List testResult = em.createQuery(cq).getResultList();
        
        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause with a left join did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }
    
    /**
     * This test verifies that the SQL generated for a criteria query with joins and an on clause matches 
     * the SQL generated for the equivalent JPQL query, to ensure that excess table joins are not occurring that do
     * not affect the results.  
     */
    public void testOnClauseCompareSQL() {
    	EntityManager em = createEntityManager();
        JpaEntityManager jpaEM = JpaHelper.getEntityManager((EntityManager)createEntityManager().getDelegate());
        EJBQueryImpl query = (EJBQueryImpl)jpaEM.createQuery("Select e from Employee e left join e.manager m left join m.address a on a.city = 'Ottawa' " +
        		"where a.postalCode is not null");
        String baseSQL = query.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(), 
        				new org.eclipse.persistence.sessions.DatabaseRecord());        
        
        CriteriaBuilder qb = jpaEM.getCriteriaBuilder();
        CriteriaQuery<Employee>cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join address = root.join("manager", JoinType.LEFT).join("address", JoinType.LEFT);
        address.on(qb.equal(address.get("city"), "Ottawa"));
        cq.where(qb.isNotNull(address.get("postalCode")));
        EJBQueryImpl testQuery = (EJBQueryImpl)jpaEM.createQuery(cq);
        
        String testSQL = query.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(), 
				new org.eclipse.persistence.sessions.DatabaseRecord());

        closeEntityManager(em);

        if (!testSQL.equals(baseSQL)) {
            fail("Criteria query using ON clause did not match SQL used for a JPQL query; generated SQL was: \""
                    +testSQL + "\"  but we expected: \""+baseSQL+"\"");
        }
    }
}
