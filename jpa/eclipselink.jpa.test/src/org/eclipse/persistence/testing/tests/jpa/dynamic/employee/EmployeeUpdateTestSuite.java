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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic.employee;

//javase imports
import java.util.Date;

//java eXtension imports
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.server.Server;

//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.QuerySQLTracker;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class EmployeeUpdateTestSuite {

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
      qTracker = null;
      emf.close();
      emf = null;
  }
  
  @Test
  public void testMultipleTransactionsModifyInstance() {
      EntityManager em = emf.createEntityManager();
      qTracker.reset();
      DynamicEntity minEmp = minimumEmployee(em);
      em.getTransaction().begin();
      minEmp.set("firstName", "Delete");
      minEmp.set("lastName", "Delete");
      minEmp.set("salary", 9);
      em.persist(minEmp);
      em.getTransaction().commit();
      assertEquals(1, qTracker.getTotalCalls("ReadAll"));
      assertEquals(2, qTracker.getTotalSQLUPDATECalls());

      assertTrue(minEmp instanceof ChangeTracker);
      assertNotNull(((ChangeTracker)minEmp)._persistence_getPropertyChangeListener());
      assertTrue(((ChangeTracker)minEmp)._persistence_getPropertyChangeListener() instanceof
          AttributeChangeListener);

      qTracker.reset();
      DynamicEntity period = minEmp.get("period");
      period.set("startDate", new Date(System.currentTimeMillis()));
      period.set("endDate", new Date(System.currentTimeMillis() + 100000));
      em.getTransaction().begin();
      em.persist(minEmp); // no-op, already registered
      em.getTransaction().commit();
      assertEquals(1, qTracker.getTotalSQLUPDATECalls());
  }

  public DynamicEntity minimumEmployee(EntityManager em) {
      Query q = em.createQuery(
          "SELECT e FROM Employee e WHERE e.id in (SELECT MIN(ee.id) FROM Employee ee)");
      return (DynamicEntity)q.getSingleResult();
  }
}