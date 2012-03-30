/*******************************************************************************
 * Copyright (c) 1998,Oracle. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.criteria;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Dealer;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;

/**
 * <p>
 * <b>Purpose</b>: Test advanced JPA Query functionality.
 * <p>
 * <b>Description</b>: This tests query hints, caching and query optimization.
 * <p>
 */
public class AdvancedCriteriaQueryTestSuite extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public AdvancedCriteriaQueryTestSuite() {
        super();
    }

    public AdvancedCriteriaQueryTestSuite(String name) {
        super(name);
    }

    // This method is run at the start of EVERY test case method.

    public void setUp() {

    }

    // This method is run at the end of EVERY test case method.

    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedCriteriaQueryTestSuite");
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSetup"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testInCollectionEntity"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testInCollectionPrimitives"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testProd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSize"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testJoinDistinct"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSome"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testWhereConjunction"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testWhereDisjunction"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testWhereConjunctionAndDisjunction"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testVerySimpleJoin"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testGroupByHaving"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testAlternateSelection"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryExists"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryNotExists"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryExistsAfterAnd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryExistsBeforeAnd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryNotExistsAfterAnd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryNotExistsBeforeAnd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryExistsNested"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryExistsNestedAfterAnd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubqueryExistsNestedAfterLiteralAnd"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSubQuery"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testInSubQuery"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testInLiteral"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testInlineInParameter"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSimpleJoin"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSimpleFetch"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testObjectResultType"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSimple"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSimpleWhere"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSimpleWhereObject"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testSharedWhere"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testTupleQuery"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testQueryCacheFirstCacheHits"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testQueryCacheOnlyCacheHits"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testQueryCacheOnlyCacheHitsOnSession"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testQueryExactPrimaryKeyCacheHits"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testQueryHintFetch"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testCursors"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testIsEmpty"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testNeg"));
        suite.addTest(new AdvancedCriteriaQueryTestSuite("testIsMember"));
    suite.addTest(new AdvancedCriteriaQueryTestSuite("testIsMemberEntity"));
    suite.addTest(new AdvancedCriteriaQueryTestSuite("testNullRestrictionGetRestriction"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        DatabaseSession session = JUnitTestCase.getServerSession();
        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();
        new AdvancedTableCreator().replaceTables(session);
        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();
        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());
        //Populate the tables
        employeePopulator.buildExamples();
        //Persist the examples in the database
        employeePopulator.persistExample(session);
    }
    

    
    public void testAlternateSelection() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {        
            em.createQuery("select p.teamLeader from Project p where p.name = 'Sales Reporting'").getResultList();
            Metamodel mm = em.getMetamodel();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Project> spouse = cquery.from(Project.class);
            cquery.where(qbuilder.equal(spouse.get("name"), "Sales Reporting")).select(spouse.<Employee> get("teamLeader"));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            assertTrue("Did not find the correct leaders of Project Swirly Dirly.", tquery.getResultList().size() > 1);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    /**
     * Test that a cache hit will occur on a primary key query.
     */
    public void testTupleQuery() {
        EntityManager em = createEntityManager();
        QuerySQLTracker counter = null;
        beginTransaction(em);
        try {
            // Load an employee into the cache.
            CriteriaBuilder qb = em.getCriteriaBuilder();

            Query query = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            CriteriaQuery<Tuple> cq = qb.createQuery(Tuple.class);
            Root from = cq.from(Employee.class);
            cq.multiselect(from.get("id"), from.get("firstName"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            TypedQuery<Tuple> typedQuery = em.createQuery(cq);

            typedQuery.setParameter("id", employee.getId());
            typedQuery.setParameter("firstName", employee.getFirstName());

            Tuple queryResult = typedQuery.getSingleResult();
            assertTrue("Query Results do not match selection", queryResult.get(0).equals(employee.getId()) && queryResult.get(1).equals(employee.getFirstName()));
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }
    
    public void testSharedWhere() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));

            cq.where(qb.equal(root.get("firstName"), qb.literal("Bob")));

            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            assertTrue("Did not return Employee", result.get(0).getClass().equals(Employee.class));
            assertTrue("Employee had wrong firstname", result.get(0).getFirstName().equalsIgnoreCase("bob"));

            CriteriaQuery<Employee> cq2 = em.getCriteriaBuilder().createQuery(Employee.class);
            cq2.where(cq.getRestriction());
            TypedQuery<Employee> tq2 = em.createQuery(cq);
            List<Employee> result2 = tq.getResultList();
            assertTrue("Employee's did not match with query with same where clause", comparer.compareObjects(result.get(0), result2.get(0)));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }		
    }

    public void testSimple(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            List<Employee> result = em.createQuery(cq).getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            assertTrue("Did not return Employee", result.get(0).getClass().equals(Employee.class));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void testGroupByHaving(){
        EntityManager em = createEntityManager();
        
        em.createQuery("Select e.address, count(e) from Employee e group by e.address having count(e.address) < 3").getResultList();
        beginTransaction(em);
        try {        
            Metamodel mm = em.getMetamodel();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = qbuilder.createQuery();
            Root<Employee> customer = cquery.from(Employee.class);
            EntityType<Employee> Customer_ = customer.getModel();
            EmbeddableType<EmploymentPeriod> Country_ = mm.embeddable(EmploymentPeriod.class);
            cquery.multiselect(customer.get(Customer_.getSingularAttribute("period", EmploymentPeriod.class)), qbuilder.count(customer)).groupBy(customer.get(Customer_.getSingularAttribute("period", EmploymentPeriod.class))).having(qbuilder.gt(qbuilder.count(customer.get(Customer_.getSingularAttribute("period", EmploymentPeriod.class))), 3));
            List<Object> result = em.createQuery(cquery).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testInLiteral(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            In<String> in = qb.in(emp.get("address").<String>get("city"));
            in.value("Ottawa").value("Halifax").value("Toronto");
            cq.where(in);
            List<Employee> result = em.createQuery(cq).getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }        
    }

    public void testInSubQuery(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            Subquery<String> sq = cq.subquery(String.class);
            Root<Address> sqEmp = sq.from(Address.class);
            sq.select(sqEmp.<String>get("city"));
            sq.where(qb.notLike(sqEmp.<String>get("city"), "5"));
            In<String> in = qb.in(emp.get("address").<String>get("city"));
            in.value(sq);
            cq.where(in);
            List<Employee> result = em.createQuery(cq).getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }
        
    public void testInCollectionEntity(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            Root<PhoneNumber> phone = cq.from(PhoneNumber.class);
            cq.where(qb.and(qb.equal(phone.get("areaCode"), "613"), phone.in(emp.<Collection<?>>get("phoneNumbers"))));
            Query query = em.createQuery(cq);
            List<Employee> result = query.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }
    public void testInCollectionPrimitives(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            Root<PhoneNumber> phone = cq.from(PhoneNumber.class);
            cq.where(qb.literal("Bug fixes").in(emp.<Collection<?>>get("responsibilities")));
            Query query = em.createQuery(cq);
            List<Employee> result = query.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }
    public void testInlineInParameter(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            cq.where(emp.get("address").<String>get("city").in(qb.parameter(String.class, "city")));
            Query query = em.createQuery(cq);
            query.setParameter("city", "Ottawa");
            List<Employee> result = query.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }

    public void testIsEmpty(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            cq.where(qb.isEmpty(emp.<Collection<PhoneNumber>>get("phoneNumbers")));
            List<Employee> result = em.createQuery(cq).getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            for (Employee e : result){
                assertTrue("PhoneNumbers Found", e.getPhoneNumbers().isEmpty());
            }
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void testNeg(){
        if (getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testNeg skipped for this platform, "
                    + "Symfoware doesn't allow arithmetic expression in subquery. (bug 304907)");
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            cq.where(qb.lessThan(qb.neg(qb.size(emp.<Collection<PhoneNumber>>get("phoneNumbers"))), 0));
            List<Employee> result = em.createQuery(cq).getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            for (Employee e : result){
                assertTrue("PhoneNumbers Found", ! e.getPhoneNumbers().isEmpty());
            }
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testNullIf(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            cq.where(qb.isNull(qb.nullif(qb.size(emp.<Collection<PhoneNumber>>get("phoneNumbers")), qb.parameter(Integer.class))));
            List<Employee> result = em.createQuery(cq).getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            for (Employee e : result){
                assertTrue("PhoneNumbers Found", ! e.getPhoneNumbers().isEmpty());
            }
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testNullRestrictionGetRestriction() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {        
            Metamodel mm = em.getMetamodel();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            cquery.getRestriction();
        }catch (NullPointerException ex){
            fail("'getRestriction()' with null restriction threw NullPointerException");
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testIsMember(){
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            cq.where(qb.isMember(qb.parameter(String.class,"1"), emp.<Collection<String>>get("responsibilities")));
            Query query = em.createQuery(cq);
            query.setParameter("1", "Sort files");
            List<Employee> result = query.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            for (Employee e : result){
                assertTrue("Employee Found without Responcibilities", e.getResponsibilities().contains("Sort files"));
            
            }
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
    }

    
    public void testIsMemberEntity(){
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> emp = cq.from(Employee.class);
            Root<PhoneNumber> phone = cq.from(PhoneNumber.class);
            cq.where(qb.and(qb.equal(phone.get("areaCode"), "416"), qb.isMember(phone, emp.<Collection<PhoneNumber>>get("phoneNumbers"))));
            Query query = em.createQuery(cq);
            List<Employee> result = query.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            for (Employee e : result){
                boolean areacode = false;
                for (PhoneNumber p : e.getPhoneNumbers()){
                    areacode = areacode || p.getAreaCode().equals("416");
                }
                assertTrue("No PhoneNumbers with '416'area code", areacode);
            
            }
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
    }

    public void testVerySimpleJoin(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));
            root.join("phoneNumbers");
            cq.distinct(true);
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            for (Employee emp : result){
                assertFalse("Found someone without a phone", emp.getPhoneNumbers().isEmpty());
            }
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSimpleJoin(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));
            root.join("phoneNumbers");
            cq.where(qb.isEmpty(root.<Collection<PhoneNumber>>get("phoneNumbers")));
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertTrue("Found employee but joins should have canceled isEmpty", result.isEmpty());
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSimpleWhere(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));
            cq.where(qb.equal(root.get("firstName"), qb.literal("Bob")));
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            assertTrue("Did not return Employee", result.get(0).getClass().equals(Employee.class));
            assertTrue("Employee had wrong firstname", result.get(0).getFirstName().equalsIgnoreCase("bob"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testWhereDisjunction(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            cq.where(qb.disjunction());
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertTrue("Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testWhereConjunction(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            cq.where(qb.conjunction());
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertFalse("Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void testJoinDistinct(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
        Root<Employee> customer = cquery.from(Employee.class);
        Fetch<Employee, Project> o = customer.fetch("phoneNumbers", JoinType.LEFT);
        cquery.where(customer.get("address").get("city").in("Ottawa", "Halifax"));
        cquery.select(customer).distinct(true);
        TypedQuery<Employee> tquery = em.createQuery(cquery);
        List<Employee> result = tquery.getResultList();
        assertFalse ("No results found", result.isEmpty());
        Long count = (Long)em.createQuery("Select count(e) from Employee e where e.address.city in ('Ottawa', 'Halifax')").getSingleResult();
        assertTrue("Incorrect number of results returned", result.size() == count);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }
    
    public void testWhereConjunctionAndDisjunction(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            cq.where(qb.and(qb.disjunction(), qb.conjunction()));
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertTrue("Employees were returned", result.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSimpleWhereObject(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));
            cq.where(qb.equal(root.get("firstName"), qb.literal("Bob")));
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            assertTrue("Did not return Employee", result.get(0).getClass().equals(Employee.class));
            assertTrue("Employee had wrong firstname", result.get(0).getFirstName().equalsIgnoreCase("bob"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSimpleFetch(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));
            root.fetch("projects");
            cq.where(qb.equal(root.get("firstName"), qb.literal("Bob")));
            TypedQuery<Employee> tq = em.createQuery(cq);
            List<Employee> result = tq.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream stream = new ObjectOutputStream(byteStream);

                stream.writeObject(result.get(0));
                stream.flush();
                byte arr[] = byteStream.toByteArray();
                ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
                ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);

                Employee emp = (Employee) inObjStream.readObject();
                assertTrue("Did not return Employee", emp.getClass().equals(Employee.class));
                assertTrue("Employee had wrong firstname", emp.getFirstName().equalsIgnoreCase("bob"));
                emp.getProjects().size(); //may cause exception
            } catch (IOException e) {
                fail("Failed during serialization");
            } catch (ClassNotFoundException e) {
                fail("Failed during serialization");
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
    }

    
    public void testSize(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
  //          em.createQuery("Select size(e.responsibilities) from Employee e").getResultList();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = qbuilder.createQuery(Object[].class);
            Root<Employee> customer = cquery.from(Employee.class);
            cquery.select(qbuilder.array(customer.get("id"), qbuilder.size(customer.<Collection<String>>get("responsibilities"))));
            TypedQuery<Object[]> tquery = em.createQuery(cquery);
            List<Object[]> result = tquery.getResultList();
            for(Object[] value : result){
                assertTrue("Incorrect responsibilities count", em.find(Employee.class, value[0]).getResponsibilities().size() == ((Integer)value[1]).intValue());
            }
        // No assert as version is not actually a mapped field in dealer.
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void testSome(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.createQuery("SELECT e from Employee e, IN(e.phoneNumbers) p where p.type = some(select p2.type from PhoneNumber p2 where p2.areaCode = '613')").getResultList();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
           // Get Root Customer
           Root<Employee> customer = cquery.from(Employee.class);


           // Join Customer-Order
           Join<Employee, PhoneNumber> orders= customer.join("phoneNumbers");


          // create Subquery instance
          Subquery<String> sq = cquery.subquery(String.class);

          // Create Roots
          Root<PhoneNumber> order = sq.from(PhoneNumber.class);

           // Create SubQuery
           sq.select(order.<String>get("type")).              
      where(qbuilder.equal(order.get("areaCode"), "613"));

        // Create Main Query with SubQuery         
      cquery.where(qbuilder.equal(orders.<String>get("type"), qbuilder.some(sq)));
      cquery.distinct(true);
      em.createQuery(cquery).getResultList();

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSubQuery(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            Join<Employee, Dealer> o = customer.join("dealers");
            cquery.select(customer).distinct(true);
            Subquery<Integer> sq = cquery.subquery(Integer.class);
            Root<Dealer> sqo = sq.from(Dealer.class);
            sq.select(qbuilder.min(sqo.<Integer>get("version")));
            cquery.where(qbuilder.equal(o.get("version"), sq));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            List<Employee> result = tquery.getResultList();
        // No assert as version is not actually a mapped field in dealer.
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    protected static Set<Integer> getIds(Collection<Employee> employees) {
        Set<Integer> ids = new HashSet<Integer>(employees.size());
        for (Employee emp : employees) {
            ids.add(emp.getId());
        }
        return ids;
    }
    protected void compareIds(List<Employee> jpqlEmployees, List<Employee> criteriaEmployees) {
        Set<Integer> jpqlIds = getIds(jpqlEmployees);
        Set<Integer> criteriaIds = getIds(criteriaEmployees);
        if (!jpqlIds.equals(criteriaIds)) {
            fail("jpql: " + jpqlIds + "; criteria: " + criteriaIds);
        }
    }
    
    public void testSubqueryExists() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM e.projects p)").getResultList();
            em.clear();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            // create correlated subquery
            Subquery<Project> sq = cquery.subquery(Project.class);
            Root<Employee> sqc = sq.correlate(customer);
            Path<Project> sqo = sqc.join("projects");
            sq.select(sqo);
            cquery.where(qbuilder.exists(sq));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone without projects", !emp.getProjects().isEmpty());
        }
    }

    public void testSubqueryNotExists() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE NOT EXISTS (SELECT p FROM e.projects p)").getResultList();
            em.clear();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            // create correlated subquery
            Subquery<Project> sq = cquery.subquery(Project.class);
            Root<Employee> sqc = sq.correlate(customer);
            Path<Project> sqo = sqc.join("projects");
            sq.select(sqo);
            cquery.where(qbuilder.not(qbuilder.exists(sq)));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone with projects", emp.getProjects().isEmpty());
        }
    }

    // cquery.where(qbuilder.and(isMale, qbuilder.exists(sq)));
    public void testSubqueryExistsAfterAnd() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE e.gender = org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender.Male AND EXISTS (SELECT p FROM e.projects p)").getResultList();
            em.clear();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            // create correlated subquery
            Subquery<Project> sq = cquery.subquery(Project.class);
            Root<Employee> sqc = sq.correlate(customer);
            Path<Project> sqo = sqc.join("projects");
            sq.select(sqo);
            Predicate isMale = qbuilder.equal(customer.get("gender"), Employee.Gender.Male);
            cquery.where(qbuilder.and(isMale, qbuilder.exists(sq)));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone not male", emp.getGender() != null && emp.isMale());
            assertTrue("Found someone without projects", !emp.getProjects().isEmpty());
        }
    }

    // cquery.where(qbuilder.and(qbuilder.exists(sq), isMale));
    public void testSubqueryExistsBeforeAnd() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM e.projects p) AND e.gender = org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender.Male").getResultList();
            em.clear();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            // create correlated subquery
            Subquery<Project> sq = cquery.subquery(Project.class);
            Root<Employee> sqc = sq.correlate(customer);
            Path<Project> sqo = sqc.join("projects");
            sq.select(sqo);
            Predicate isMale = qbuilder.equal(customer.get("gender"), Employee.Gender.Male);
            cquery.where(qbuilder.and(qbuilder.exists(sq), isMale));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone not male", emp.getGender() != null && emp.isMale());
            assertTrue("Found someone without projects", !emp.getProjects().isEmpty());
        }
    }

    // cquery.where(qbuilder.and(isFemale, qbuilder.not(qbuilder.exists(sq))));
    public void testSubqueryNotExistsAfterAnd() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE e.gender = org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender.Female AND NOT EXISTS (SELECT p FROM e.projects p)").getResultList();
            em.clear();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            // create correlated subquery
            Subquery<Project> sq = cquery.subquery(Project.class);
            Root<Employee> sqc = sq.correlate(customer);
            Path<Project> sqo = sqc.join("projects");
            sq.select(sqo);
            Predicate isFemale = qbuilder.equal(customer.get("gender"), Employee.Gender.Female);
            cquery.where(qbuilder.and(isFemale, qbuilder.not(qbuilder.exists(sq))));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone not female", emp.getGender() != null && emp.isFemale());
            assertTrue("Found someone with projects", emp.getProjects().isEmpty());
        }
    }

    // cquery.where(qbuilder.and(qbuilder.not(qbuilder.exists(sq)), isFemale));
    public void testSubqueryNotExistsBeforeAnd() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE NOT EXISTS (SELECT p FROM e.projects p) AND e.gender = org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender.Female").getResultList();
            em.clear();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cquery = qbuilder.createQuery(Employee.class);
            Root<Employee> customer = cquery.from(Employee.class);
            // create correlated subquery
            Subquery<Project> sq = cquery.subquery(Project.class);
            Root<Employee> sqc = sq.correlate(customer);
            Path<Project> sqo = sqc.join("projects");
            sq.select(sqo);
            Predicate isFemale = qbuilder.equal(customer.get("gender"), Employee.Gender.Female);
            cquery.where(qbuilder.and(qbuilder.not(qbuilder.exists(sq)), isFemale));
            TypedQuery<Employee> tquery = em.createQuery(cquery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone not female", emp.getGender() != null && emp.isFemale());
            assertTrue("Found someone with projects", emp.getProjects().isEmpty());
        }
    }

    public void testSubqueryExistsNested() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p WHERE e.projects = p AND EXISTS (SELECT t FROM Employee t WHERE p.teamLeader = t))").getResultList();
            em.clear();
            
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> mainQuery = builder.createQuery(Employee.class);
            Subquery<Object> subQuery1 = mainQuery.subquery(Object.class);
            Subquery<Object> subQuery2 = subQuery1.subquery(Object.class);

            Root<Employee> mainEmployee = mainQuery.from(Employee.class);
            mainQuery.select(mainEmployee);
            
            Root<Project> sub1Project = subQuery1.from(Project.class);
            Join<Employee, Project> mainEmployeeProjects = mainEmployee.join("projects");

            Root<Employee> sub2Employee = subQuery2.from(Employee.class);
            Join<Employee, Employee> sub1ProjectTeamLeader = sub1Project.join("teamLeader");
            
            subQuery2.where(builder.equal(sub2Employee, sub1ProjectTeamLeader));
            subQuery1.where(builder.and(builder.exists(subQuery2), builder.equal(sub1Project, mainEmployeeProjects)));
            mainQuery.where(builder.exists(subQuery1));
            
            TypedQuery<Employee> tquery = em.createQuery(mainQuery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone without projects", !emp.getProjects().isEmpty());
            boolean atLeastOneProjectHasLeader = false;
            for (Project proj : emp.getProjects()) {
                if (proj.getTeamLeader() != null) {
                    atLeastOneProjectHasLeader = true;
                    break;
                }
            }
            assertTrue("None of employee's projects has a leader", atLeastOneProjectHasLeader);
        }
    }

    public void testSubqueryExistsNestedAfterAnd() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE e.gender = org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender.Male AND EXISTS (SELECT p FROM Project p WHERE 'Sales Reporting' <> p.name AND e.projects = p AND EXISTS (SELECT t FROM Employee t WHERE p.teamLeader = t))").getResultList();
            em.clear();
            
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> mainQuery = builder.createQuery(Employee.class);
            Subquery<Object> subQuery1 = mainQuery.subquery(Object.class);
            Subquery<Object> subQuery2 = subQuery1.subquery(Object.class);
            
            Root<Employee> mainEmployee = mainQuery.from(Employee.class);
            mainQuery.select(mainEmployee);
            
            Root<Project> sub1Project = subQuery1.from(Project.class);
            Join<Employee, Project> mainEmployeeProjects = mainEmployee.join("projects");

            Root<Employee> sub2Employee = subQuery2.from(Employee.class);
            Join<Employee, Employee> sub1ProjectTeamLeader = sub1Project.join("teamLeader");
                        
            subQuery2.where(builder.equal(sub2Employee, sub1ProjectTeamLeader));
            Predicate notSalesReporting = builder.not(builder.equal(builder.literal("Sales Reporting"), sub1Project.get("name")));
            subQuery1.where(builder.and(notSalesReporting, builder.and(builder.exists(subQuery2), builder.equal(sub1Project, mainEmployeeProjects))));
            Predicate isMale = builder.equal(mainEmployee.get("gender"), Employee.Gender.Male);
            mainQuery.where(builder.and(isMale, builder.exists(subQuery1)));
            
            TypedQuery<Employee> tquery = em.createQuery(mainQuery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone not male", emp.getGender() != null && emp.isMale());
            assertTrue("Found someone without projects", !emp.getProjects().isEmpty());
            boolean atLeastOneProjectHasLeader = false;
            for (Project proj : emp.getProjects()) {
                if (!proj.getName().equals("Sales Reporting")) {
                    if (proj.getTeamLeader() != null) {
                        atLeastOneProjectHasLeader = true;
                        break;
                    }
                }
            }
            assertTrue("None of employee's projects has a leader", atLeastOneProjectHasLeader);
        }
    }

    public void testSubqueryExistsNestedAfterLiteralAnd() {
        EntityManager em = createEntityManager();
        List<Employee> jpqlEmployees;
        List<Employee> criteriaEmployees;
        beginTransaction(em);
        try {
            jpqlEmployees = em.createQuery("SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p WHERE e.projects = p AND EXISTS (SELECT t FROM Employee t WHERE p.teamLeader = t))").getResultList();
            em.clear();
            
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> mainQuery = builder.createQuery(Employee.class);
            Subquery<Object> subQuery1 = mainQuery.subquery(Object.class);
            Subquery<Object> subQuery2 = subQuery1.subquery(Object.class);
            
            Root<Employee> mainEmployee = mainQuery.from(Employee.class);
            mainQuery.select(mainEmployee);
            
            Root<Project> sub1Project = subQuery1.from(Project.class);
            Join<Employee, Project> mainEmployeeProjects = mainEmployee.join("projects");

            Root<Employee> sub2Employee = subQuery2.from(Employee.class);
            Join<Employee, Employee> sub1ProjectTeamLeader = sub1Project.join("teamLeader");
            
            subQuery2.where(builder.equal(sub2Employee, sub1ProjectTeamLeader));
            Predicate oneEqualsOne = builder.equal(builder.literal(1), builder.literal(1));
            subQuery1.where(builder.and(oneEqualsOne, builder.and(builder.exists(subQuery2), builder.equal(sub1Project, mainEmployeeProjects))));
            Predicate twoEqualsTwo = builder.equal(builder.literal(2), builder.literal(2));
            mainQuery.where(builder.and(twoEqualsTwo, builder.exists(subQuery1)));
            
            TypedQuery<Employee> tquery = em.createQuery(mainQuery);
            criteriaEmployees = tquery.getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        compareIds(jpqlEmployees, criteriaEmployees);
        for (Employee emp : criteriaEmployees){
            assertTrue("Found someone without projects", !emp.getProjects().isEmpty());
            boolean atLeastOneProjectHasLeader = false;
            for (Project proj : emp.getProjects()) {
                if (proj.getTeamLeader() != null) {
                    atLeastOneProjectHasLeader = true;
                    break;
                }
            }
            assertTrue("None of employee's projects has a leader", atLeastOneProjectHasLeader);
        }
    }

    /**
     * Test cursored queries.
     */
    public void testCursors() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // Test cursored stream.
            Query query = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            query.setHint(QueryHints.CURSOR, true);
            query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 2);
            query.setHint(QueryHints.CURSOR_PAGE_SIZE, 5);
            query.setHint(QueryHints.CURSOR_SIZE, "Select count(*) from CMP3_EMPLOYEE");
            Cursor cursor = (Cursor)query.getSingleResult();
            cursor.nextElement();
            cursor.size();
            cursor.close();
            
            // Test cursor result API.
            JpaQuery jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            jpaQuery.setHint(QueryHints.CURSOR, true);
            cursor = jpaQuery.getResultCursor();
            cursor.nextElement();
            cursor.size();
            cursor.close();
            
            // Test scrollable cursor.
            jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            jpaQuery.setHint(QueryHints.SCROLLABLE_CURSOR, true);
            jpaQuery.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
            jpaQuery.setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.DEFAULT);
            ScrollableCursor scrollableCursor = (ScrollableCursor)jpaQuery.getResultCursor();
            scrollableCursor.next();
            scrollableCursor.close();
            
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Test the result type of various queries.
     */
    public void testObjectResultType() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);
            
            CriteriaBuilder qb = em.getCriteriaBuilder();
            // Test multi object, as an array.
            CriteriaQuery<?> cq = qb.createQuery(Object[].class);
            Root<Employee> from = cq.from(Employee.class);
            cq.multiselect(from, from.get("address"), from.get("id"));
            Parameter<String> firstNameParam = qb.parameter(from.<String>get("firstName").getModel().getBindableJavaType(), "firstName");
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")),qb.equal(from.get("firstName"), firstNameParam)));
            query = em.createQuery(cq);
            query.setParameter("id", employee.getId());
            query.setParameter(firstNameParam, employee.getFirstName());
            Object[] arrayResult = (Object[])query.getSingleResult();
            if ((arrayResult.length != 3) && (arrayResult[0] != employee) || (arrayResult[1] != employee.getAddress()) || (!arrayResult[2].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            List listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            if ((arrayResult.length != 3) || (arrayResult[0] != employee) || (arrayResult[1] != employee.getAddress()) || (!arrayResult[2].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test single object, as an array.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), (qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName")))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();
            if ((arrayResult.length != 1) || (!arrayResult[0].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            if ((arrayResult.length != 1) || (!arrayResult[0].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test multi object, as a Map.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.alias("employee"), from.get("address").alias("address"), from.get("id").alias("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Map mapResult = (Map)query.getSingleResult();
            if ((mapResult.size() != 3) ||(mapResult.get("employee") != employee) || (mapResult.get("address") != employee.getAddress()) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);
            if ((mapResult.size() != 3) ||(mapResult.get("employee") != employee) || (mapResult.get("address") != employee.getAddress()) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            
            // Test single object, as a Map.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id").alias("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            mapResult = (Map)query.getSingleResult();
            if ((mapResult.size() != 1) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);
            if ((mapResult.size() != 1) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            
            // Test single object, as an array.
            cq = qb.createQuery(Employee.class);
            from = cq.from(Employee.class);
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.QUERY_TYPE, QueryType.Report);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();
            if (arrayResult[0] != employee) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test single object, as value.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Object valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            listResult = query.getResultList();
            valueResult = listResult.get(0);
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            
            // Test multi object, as value.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"), from.get("firstName"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Value);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            
            // Test single object, as attribute.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Attribute);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            listResult = query.getResultList();
            valueResult = listResult.get(0);
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
        } finally {
            rollbackTransaction(em);
        }
    }

    /**
     * Test that a cache hit will occur on a primary key query.
     */
    public void testQueryExactPrimaryKeyCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")));
            query = em.createQuery(cq);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheByExactPrimaryKey);
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();
            if (queryResult != employee) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }
    
    public void testQueryHintFetch(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaQuery<Employee> cq = em.getCriteriaBuilder().createQuery(Employee.class);
            CriteriaBuilder qb = em.getCriteriaBuilder();
            Root<Employee> root = cq.from(em.getMetamodel().entity(Employee.class));
            cq.where(qb.equal(root.get("firstName"), qb.literal("Bob")));
            TypedQuery<Employee> tq = em.createQuery(cq);
            tq.setHint(QueryHints.FETCH, "e.projects");
            List<Employee> result = tq.getResultList();
            assertFalse("No Employees were returned", result.isEmpty());
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream stream = new ObjectOutputStream(byteStream);

                stream.writeObject(result.get(0));
                stream.flush();
                byte arr[] = byteStream.toByteArray();
                ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
                ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);

                Employee emp = (Employee) inObjStream.readObject();
                assertTrue("Did not return Employee", emp.getClass().equals(Employee.class));
                assertTrue("Employee had wrong firstname", emp.getFirstName().equalsIgnoreCase("bob"));
                emp.getProjects().size(); //may cause exception
            } catch (IOException e) {
                fail("Failed during serialization");
            } catch (ClassNotFoundException e) {
                fail("Failed during serialization");
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
    }

    public void testProd(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
  //          em.createQuery("Select size(e.responsibilities) from Employee e").getResultList();
            CriteriaBuilder qbuilder = em.getCriteriaBuilder();
            CriteriaQuery<BigInteger> cquery = qbuilder.createQuery(BigInteger.class);
            Root<Employee> customer = cquery.from(Employee.class);
            cquery.select(qbuilder.toBigInteger(qbuilder.prod(qbuilder.literal(BigInteger.valueOf(5)),customer.<Integer>get("salary"))));
            TypedQuery<BigInteger> tquery = em.createQuery(cquery);
            List<BigInteger> result = tquery.getResultList();
            for(BigInteger value : result){
                assertTrue("Incorrect arithmatic returned ", value.mod(BigInteger.valueOf(5)).equals(BigInteger.valueOf(0)));
            }
        // No assert as version is not actually a mapped field in dealer.
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Test that a cache hit will occur on a query.
     */
    public void testQueryCacheFirstCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName")));
            query = em.createQuery(cq);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();
            if (!queryResult.getFirstName().equals(employee.getFirstName())) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test that a cache hit will occur on a query.
     */
    public void testQueryCacheOnlyCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName")));
            query = em.createQuery(cq);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("firstName", employee.getFirstName());
            // Test that list works as well.
            query.getResultList();
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }
    
    /**
     * Test that a cache hit will occur on a query when the object is not in the unit of work/em.
     */
    public void testQueryCacheOnlyCacheHitsOnSession() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            cq = qb.createQuery(Employee.class);
             Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")));
            query = em.createQuery(cq);
            query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("id", employee.getId());
            if (query.getSingleResult() == null) {
                fail("Query did not check session cache.");
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            cq = qb.createQuery(Employee.class);
            from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")));
            query = em.createQuery(cq);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("id", employee.getId());
            if (query.getResultList().size() != 1) {
                fail("Query did not check session cache.");
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            if (counter != null) {
                counter.remove();
            }
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
}
