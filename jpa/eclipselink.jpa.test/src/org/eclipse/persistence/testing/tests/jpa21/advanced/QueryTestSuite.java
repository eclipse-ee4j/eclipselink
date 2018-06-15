/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial implementation
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
//     03/01/2013-2.5 Chris Delahunt
//       - 402147: JPA query methods need to throw IllegalStateException if EM was closed
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Item;
import org.eclipse.persistence.testing.models.jpa21.advanced.Order;

public class QueryTestSuite extends JUnitTestCase {

    public QueryTestSuite() {}

    public QueryTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
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
        suite.addTest(new QueryTestSuite("testGetFlushMode"));
        suite.addTest(new QueryTestSuite("testCriteriaGetGroupList"));
        suite.addTest(new QueryTestSuite("testCriteriaIsNegated"));
        suite.addTest(new QueryTestSuite("testCriteriaGetJoinType"));
        suite.addTest(new QueryTestSuite("testConstructorResultQuery"));
        suite.addTest(new QueryTestSuite("testCriteriaIsCorelated"));
        suite.addTest(new QueryTestSuite("testQueryExceptionOnClosedEM"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
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


        TypedQuery<Employee> typedQuery = em.createQuery("SELECT employee FROM Employee employee WHERE employee.firstName = ?1 OR employee.lastName = ?2", Employee.class)/*.setParameter(1, "Bob").setParameter(2, "Way")*/;
        i = 0;
        ArrayList<Parameter> params = new ArrayList<Parameter>(typedQuery.getParameters());
        for (Parameter parameter: params){
            assertTrue("Parameter returned wrong position.", parameter.getPosition() != null );
            assertTrue("Parameter returned name.", parameter.getName() == null );
            i++;
            assertTrue("Parameter returned wrong type.", parameter.getParameterType().equals(String.class));
        }
        try{
            query.getParameter(1, Integer.class);
            fail("Exception not thrown for incorrect query type.");
        } catch (IllegalArgumentException e){}
        query = em.createNamedQuery("jpa21Employee.findAllEmployeesByFirstNameAndLastNamePos", Employee.class).setParameter(1, "Bob");
        try{
            query.getParameterValue(2);
            fail("Exception not thrown for unbound parameter.");
        } catch (IllegalStateException e){}
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

    public void testConstructorResultQuery() {
        if (getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

                Item item = new Item();
                item.setName("Nails");

                Order order = new Order();
                order.setQuantity(500);

                order.setItem(item);

                em.persist(item);
                em.persist(order);

                commitTransaction(em);

                // Mapped column results are:
                // @ColumnResult(name = "O_ID", type=String.class),
                // @ColumnResult(name = "O_QUANTITY"),
                // @ColumnResult(name = "O_ITEM_NAME")

                // NOTE: expecting O_ITEM_NAME but the query is returning O_ITEM.

                em.createNativeQuery("SELECT o.ORDER_ID AS 'O_ID', o.QUANTITY AS 'O_QUANTITY', i.NAME AS 'O_ITEM' FROM JPA21_ORDER o, JPA21_ITEM i WHERE o.ITEM_ID = i.ID", "OrderConstructorResult").getResultList();

                fail("No exceptions thrown. Expecting a QueryException (and not a NPE)");
            } catch (NullPointerException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                fail("Null pointer exception caught on constructor result query.");
            } catch (PersistenceException exc) {// QueryException.INVALID_CONTAINER_CLASS
                if (! (exc.getCause() instanceof QueryException)) {
                    throw exc;
                }
                QueryException e = (QueryException)exc.getCause();
                assertTrue(e.getErrorCode() == QueryException.COLUMN_RESULT_NOT_FOUND);
            } finally {
                closeEntityManager(em);
            }
        }
    }
    public void testGetFlushMode(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("select e from Employee e");
        try{
            query.getFlushMode();
        } catch (NullPointerException npe){
            fail("NPE thrown on getFlushMode()");
        }
    }

    public void testCriteriaGetGroupList(){
        EntityManager em = createEntityManager();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery query = qb.createQuery(Employee.class);
        List groupList = query.getGroupList();
        assertNotNull("getGroupList returned null.", groupList);
    }

    public void testCriteriaIsNegated(){
        EntityManager em = createEntityManager();
        CriteriaBuilder qbuilder = em.getCriteriaBuilder();

        Boolean result = qbuilder.equal(qbuilder.literal("1"), "1").isNegated();
        assertFalse(result);

        result = qbuilder.equal(qbuilder.literal("1"), "1").not().isNegated();
        assertTrue(result);

        result = qbuilder.not(qbuilder.equal(qbuilder.literal("1"), "1")).isNegated();
        assertTrue(result);

        result = qbuilder.disjunction().not().isNegated();
        assertTrue(result);
    }

    public void testCriteriaGetJoinType(){
        EntityManager em = createEntityManager();
        CriteriaBuilder qbuilder = em.getCriteriaBuilder();
        CriteriaQuery query = qbuilder.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);
        JoinType jt =  employee.join("phoneNumbers", JoinType.LEFT).getJoinType();
        assertEquals("The join type was incorect.", jt, JoinType.LEFT);
    }

    public void testCriteriaIsCorelated(){
        EntityManager em = createEntityManager();
        CriteriaBuilder qbuilder = em.getCriteriaBuilder();
        CriteriaQuery query = qbuilder.createQuery(Employee.class);
        From<Employee, Employee> employee = query.from(Employee.class);
        boolean isCorr = employee.isCorrelated();
        assertFalse(isCorr);
        try{
            employee.getCorrelationParent();
            fail("proper exception not thrown when calling getCorrelationParent on non-correlated from.");
        } catch (IllegalStateException e){}
    }


    /* bug 402147 - JPA query methods need to throw IllegalStateException if EM was closed
     * Tests the following methods:
     *   getParameter(String name, Class<T> type)
     *   getParameter(int position, Class<T> type)
     *   getParameter(String name)
     *   getParameter(int position)
     *   getParameterValue(String name)
     *   getParameterValue(int position)
     *   getParameters()
     *   getFirstResult()
     *   isBound(Parameter<?> param)
     *   getMaxResults()
     */
    public void testQueryExceptionOnClosedEM() {
        // closeEntityManager(em) does not close the entity manager when running on a server
        if (isOnServer()){
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e");
        closeEntityManager(em);
        List<String> failedMethodList = new ArrayList();
        try{
            query.getLockMode();
            failedMethodList.add("getLockMode");
        } catch (IllegalStateException e){}
        try{
            query.getParameter("doesntmatter");
            failedMethodList.add("getParameter(String)");
        } catch (IllegalStateException e){}
        try{
            query.getParameter(1);
            failedMethodList.add("getParameter(int)");
        } catch (IllegalStateException e){}
        try{
            query.getParameter("doesntmatter", Employee.class);
            failedMethodList.add("getParameter(String, class)");
        } catch (IllegalStateException e){}
        try{
            query.getParameter(1, Employee.class);
            failedMethodList.add("getParameter(int, class)");
        } catch (IllegalStateException e){}
        try{
            query.getParameterValue("doesntmatter");
            failedMethodList.add("getParameterValue(String)");
        } catch (IllegalStateException e){}
        try{
            query.getParameterValue(1);
            failedMethodList.add("getParameterValue(int)");
        } catch (IllegalStateException e){}
        try{
            query.getParameters();
            failedMethodList.add("getParameters()");
        } catch (IllegalStateException e){}
        try{
            query.getFirstResult();
            failedMethodList.add("getFirstResult()");
        } catch (IllegalStateException e){}
        try{
            query.isBound(null);
            failedMethodList.add("isBound(Parameter)");
        } catch (IllegalStateException e){}
        try{
            query.getMaxResults();
            failedMethodList.add("getMaxResults()");
        } catch (IllegalStateException e){}
        try{
            query.getHints();
            failedMethodList.add("getHints()");
        } catch (IllegalStateException e){}

        if (!failedMethodList.isEmpty()) {
            //format the string of methods that failed
            String methodList = null;
            for (String methodName: failedMethodList) {
                if (methodList==null) {
                    methodList = methodName;
                } else {
                    methodList = methodList +", "+methodName;
                }
            }
            this.fail("Expected IllegalStateException not thrown from Query methods "+methodList);
        }
    }


    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }
}
