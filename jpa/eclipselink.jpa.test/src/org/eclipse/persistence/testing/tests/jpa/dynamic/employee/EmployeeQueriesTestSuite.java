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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic.employee;

//javase imports
import java.util.Collection;
import java.util.List;

//java eXtension imports
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.server.Server;

//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class EmployeeQueriesTestSuite {

    //test fixtures
    static EntityManagerFactory emf = null;
    static JPADynamicHelper helper = null;
    static Server serverSession = null;
    static DynamicEmployeeSystem deSystem = null;
    
    @BeforeClass
    public static void setUp() throws Exception {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        boolean isMySQL = JpaHelper.getServerSession(emf).getDatasourcePlatform().
            getClass().getName().contains("MySQLPlatform");
        assumeTrue(isMySQL);
        helper = new JPADynamicHelper(emf);
        deSystem = DynamicEmployeeSystem.buildProject(helper);
        serverSession = JpaHelper.getServerSession(emf);
        deSystem.populate(helper, emf.createEntityManager());
    }

    @AfterClass
    public static void tearDown() {
        serverSession.executeNonSelectingSQL("DROP TABLE D_SALARY");
        serverSession.executeNonSelectingSQL("DROP TABLE D_PROJ_EMP");
        serverSession.executeNonSelectingSQL("DROP TABLE D_PROJECT");
        serverSession.executeNonSelectingSQL("DROP TABLE D_PHONE");
        serverSession.executeNonSelectingSQL("DROP TABLE D_EMPLOYEE");
        serverSession.executeNonSelectingSQL("DROP TABLE D_ADDRESS");
        helper = null;
        emf.close();
        emf = null;
    }
    
    @Test
    public void readAllEmployees_JPQL() {
        EntityManager em = emf.createEntityManager();
        List<DynamicEntity> emps = readAllEmployeesUsingJPQL(em);
        deSystem.assertSame(emps);
    }

    @Test
    public void joinFetchJPQL() {
        List<DynamicEntity> emps = joinFetchJPQL(emf.createEntityManager());
        assertNotNull(emps);
    }

    @Test
    public void joinFetchHint() {
        List<DynamicEntity> emps = joinFetchHint(emf.createEntityManager());
        assertNotNull(emps);
    }

    @Test
    public void minEmployeeId() {
        int minId = minimumEmployeeId(emf.createEntityManager());
        assertTrue(minId > 0);
    }

    @Test
    public void testGenderIn(){
        List<DynamicEntity> emps = findEmployeesUsingGenderIn(emf.createEntityManager());
        assertNotNull(emps);
    }

    @Test
    public void testReadAllExressions() {
        List<DynamicEntity> emps = findUsingNativeReadAllQuery(emf.createEntityManager());
        assertNotNull(emps);
    }

    public List<DynamicEntity> readAllEmployeesUsingJPQL(EntityManager em) {
        return em.createQuery("SELECT e FROM Employee e ORDER BY e.id ASC").getResultList();
    }

    public List<DynamicEntity> joinFetchJPQL(EntityManager em) {
        return em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address " +
            "ORDER BY e.lastName ASC, e.firstName ASC").getResultList();
    }

    public List<DynamicEntity> joinFetchHint(EntityManager em) {
        Query query = em.createQuery(
            "SELECT e FROM Employee e WHERE e.manager.address.city = 'Ottawa' " +
                "ORDER BY e.lastName ASC, e.firstName ASC");
        query.setHint(QueryHints.FETCH, "e.address");
        query.setHint(QueryHints.FETCH, "e.manager");
        query.setHint(QueryHints.FETCH, "e.manager.address");
        query.setHint(QueryHints.BATCH, "e.manager.phoneNumbers");
        List<DynamicEntity> emps = query.getResultList();
        for (DynamicEntity emp : emps) {
            emp.<DynamicEntity>get("manager").<Collection>get("phoneNumbers").size();
        }
        return emps;
    }

    public static int minimumEmployeeId(EntityManager em) {
        return ((Number)em.createQuery("SELECT MIN(e.id) FROM Employee e")
            .getSingleResult()).intValue();
    }

    public DynamicEntity minimumEmployee(EntityManager em) {
        Query q = em.createQuery(
            "SELECT e FROM Employee e WHERE e.id in (SELECT MIN(ee.id) FROM Employee ee)");
        return (DynamicEntity)q.getSingleResult();
    }

    public List<DynamicEntity> findEmployeesUsingGenderIn(EntityManager em) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.gender IN " +
            "(:GENDER1, :GENDER2)")
            .setParameter("GENDER1", "Male")
            .setParameter("GENDER2", "Female")
            .getResultList();
    }

    public List<DynamicEntity> findUsingNativeReadAllQuery(EntityManager em) {
        ClassDescriptor descriptor = serverSession.getDescriptorForAlias("Employee");
        ReadAllQuery raq = new ReadAllQuery(descriptor.getJavaClass());
        ExpressionBuilder eb = raq.getExpressionBuilder();
        raq.setSelectionCriteria(eb.get("gender").equal("Male"));
        Query query = JpaHelper.createQuery(raq, em);
        return query.getResultList();
    }

    public DynamicEntity minEmployeeWithAddressAndPhones(EntityManager em) {
        return (DynamicEntity)em.createQuery(
            "SELECT e FROM Employee e JOIN FETCH e.address WHERE e.id IN " +
                "(SELECT MIN(p.id) FROM PhoneNumber p)")
                .getSingleResult();
    }
}