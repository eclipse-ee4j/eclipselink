/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mnorman - sparse merge for DynamicEntity's
 *       https://bugs.eclipse.org/bugs/show_bug.cgi?id=280667
 *       https://bugs.eclipse.org/bugs/show_bug.cgi?id=316996
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic.employee;

//javase imports

//java eXtension imports
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.server.Server;

//domain (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import org.eclipse.persistence.testing.tests.jpa.dynamic.QuerySQLTracker;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class EmployeeSparseMergeTestSuite {

    //test fixtures
    static EntityManagerFactory emf = null;
    static JPADynamicHelper helper = null;
    static Server serverSession = null;
    static DynamicEmployeeSystem deSystem = null;
    static QuerySQLTracker qTracker = null;
    
    @BeforeClass
    public static void setUp() throws Exception {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        boolean isMySQL = JpaHelper.getServerSession(emf).getDatasourcePlatform().
            getClass().getName().contains("MySQLPlatform");
        assumeTrue(isMySQL);
        helper = new JPADynamicHelper(emf);
        deSystem = DynamicEmployeeSystem.buildProject(helper);
        serverSession = JpaHelper.getServerSession(emf);
        serverSession.executeNonSelectingSQL(
            "update sequence set SEQ_COUNT = 0 where SEQ_NAME = 'EMP_SEQ'");
        deSystem.populate(helper, emf.createEntityManager());
        serverSession.getIdentityMapAccessor().initializeAllIdentityMaps();
        qTracker = QuerySQLTracker.install(serverSession);
        // QuerySQLTracker only works if logging is set to FINE 
        serverSession.setLogLevel(SessionLog.FINE);
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
    public void mergeSparseDynamicEntityWithFetchGroup() {
        EntityManager em = emf.createEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        DynamicType employeeType = helper.getType("Employee");
        // Create a new 'sparse' Employee
        DynamicEntity sparseEmployee = employeeType.newDynamicEntity();
        // and manually set an EntityFetchGroup
        EntityFetchGroup efg = new EntityFetchGroup(new String[0]);
        ((FetchGroupTracker)sparseEmployee)._persistence_setFetchGroup(efg);
        sparseEmployee.set("id", minimumEmployeeId(em));
        /* pretend sparseEmployee is read in from sparse XML document:
          <employee>
              <id>nnn</id>
              <firstname>Mike</firstname>
              <lastname>Norman</lastname>
              <salary>12345</salary>
          </employee>
        */
        sparseEmployee.set("firstName", "Mike");
        sparseEmployee.set("lastName", "Norman");
        sparseEmployee.set("salary",Integer.valueOf(12345));
        em.getTransaction().begin();
        em.merge(sparseEmployee);
        em.getTransaction().commit();
        assertEquals("Incorrect # of UPDATE calls", 2, qTracker.getTotalSQLUPDATECalls());
    }

    public static int minimumEmployeeId(EntityManager em) {
        return ((Number)em.createQuery("SELECT MIN(e.id) FROM Employee e")
            .getSingleResult()).intValue();
    }
}