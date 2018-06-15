/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mnorman - sparse merge for DynamicEntity's
//       https://bugs.eclipse.org/bugs/show_bug.cgi?id=280667
//       https://bugs.eclipse.org/bugs/show_bug.cgi?id=316996
//
package org.eclipse.persistence.testing.tests.jpa.dynamic.employee;

import static org.eclipse.persistence.logging.SessionLog.FINE;
import static org.eclipse.persistence.logging.SessionLog.WARNING;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;
import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.tests.jpa.config.ConfigPUTestSuite;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import org.eclipse.persistence.testing.tests.jpa.dynamic.QuerySQLTracker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeeSparseMergeTestSuite {

    //test fixtures
    static EntityManagerFactory emf = null;
    static JPADynamicHelper helper = null;
    static Server serverSession = null;
    static DynamicEmployeeSystem deSystem = null;
    static QuerySQLTracker qTracker = null;
    private static SessionLog log = null;

    /**
     * {@code SEQ_COUNT} sequence clean up (initialize it to {@code 0} value) SQL statement call.
     * @param serverSession Current server session.
     * @throws DatabaseException when sequence does not exist.
     */
    private static void runCleanSequence(final Server serverSession) throws DatabaseException {
        if (serverSession.getPlatform().isMySQL()) {
            serverSession.executeNonSelectingSQL("UPDATE EMPLOYEE_SEQ SET SEQ_COUNT = 0 WHERE SEQ_NAME = 'EMP_SEQ'");
        } else {
            serverSession.executeNonSelectingSQL("UPDATE SEQUENCE SET SEQ_COUNT = 0 WHERE SEQ_NAME = 'EMP_SEQ'");
        }
    }

    /**
     * {@code SEQ_COUNT} sequence initialization.
     * @param serverSession Current server session.
     * @throws DatabaseException when sequence initialization failed.
     */
    private static void initSequence(final Server serverSession) throws DatabaseException {
        boolean retry = false;
        // This may pass if SEQ_COUNT already exists.
        try {
            runCleanSequence(serverSession);
            log.log(FINE, "SEQ_COUNT sequence already exists and was cleaned up.");
        } catch (DatabaseException ex) {
            log.log(FINE, "SEQ_COUNT 1st sequence cleanup attempt failed: " + ex.getMessage());
            retry = true;
        }
        // SEQ_COUNT does not exist so it must be created before clean up.
        if (retry) {
            // Test suite depends on SEQ_COUNT which is part of common JPA tests.
            log.log(FINE, "Running SEQ_COUNT sequence initialization.");
            ConfigPUTestSuite suite = new ConfigPUTestSuite();
            suite.setUp();
            suite.testCreateConfigPU();
            suite.testVerifyConfigPU();
            try {
                runCleanSequence(serverSession);
                log.log(FINE, "SEQ_COUNT sequence cleaned up after being created.");
            } catch (DatabaseException ex) {
                log.log(WARNING, "SEQ_COUNT sequence cleanup failed after initialization: " + ex.getMessage());
                throw ex;
            }
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        helper = new JPADynamicHelper(emf);
        deSystem = DynamicEmployeeSystem.buildProject(helper);
        serverSession = JpaHelper.getServerSession(emf);
        log = serverSession.getSessionLog();
        initSequence(serverSession);
        deSystem.populate(helper, emf.createEntityManager());
        serverSession.getIdentityMapAccessor().initializeAllIdentityMaps();
        qTracker = QuerySQLTracker.install(serverSession);
        // QuerySQLTracker only works if logging is set to FINE
        serverSession.setLogLevel(SessionLog.FINE);
    }

    @AfterClass
    public static void tearDown() {
        serverSession.executeNonSelectingSQL("DELETE FROM D_PROJ_EMP");
        serverSession.executeNonSelectingSQL("DELETE FROM D_PHONE");
        serverSession.executeNonSelectingSQL("DELETE FROM D_SALARY");
        serverSession.executeNonSelectingSQL("DELETE FROM D_PROJECT");
        serverSession.executeNonSelectingSQL("DELETE FROM D_EMPLOYEE");
        serverSession.executeNonSelectingSQL("DELETE FROM D_ADDRESS");
        try{
            serverSession.executeNonSelectingSQL("DROP TABLE D_SALARY");
            serverSession.executeNonSelectingSQL("DROP TABLE D_PROJ_EMP");
            serverSession.executeNonSelectingSQL("DROP TABLE D_PROJECT");
            serverSession.executeNonSelectingSQL("DROP TABLE D_PHONE");
            serverSession.executeNonSelectingSQL("DROP TABLE D_EMPLOYEE");
            serverSession.executeNonSelectingSQL("DROP TABLE D_ADDRESS");
        } catch (Exception e){
            e.printStackTrace();
        }
        helper = null;
        emf.close();
        emf = null;
        log = null;
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
