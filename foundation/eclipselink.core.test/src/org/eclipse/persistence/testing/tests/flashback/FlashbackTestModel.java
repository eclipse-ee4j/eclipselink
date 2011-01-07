/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.flashback;

import java.util.*;
import java.sql.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.tests.expressions.*;
import org.eclipse.persistence.testing.tests.clientserver.*;
import org.eclipse.persistence.testing.tests.sessionbroker.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

/**
 * Tests Oracle Flashback support.
 * Note this model is also subclassed for generic history support
 * and support both historical models.
 */
public class FlashbackTestModel extends TestModel {
    protected Number systemChangeNumber;
    public static java.sql.Timestamp timestamp;
    protected AsOfClause asOfClause;
    public final static boolean QUICK_SETUP = true;
    public final static boolean STANDARD_SETUP = true;

    public FlashbackTestModel() {
        setDescription("Tests the new flashback query tests.  Includes running the expression test suite as of a past time.");
    }

    public Expression adaptExpression(Expression expression) {
        return expression.asOf(getAsOfClause());
    }

    public void adaptQuery(ObjectLevelReadQuery query) {
        if (getSystemChangeNumber() != null) {
            query.setAsOfClause(getAsOfClause());
        }
    }

    public org.eclipse.persistence.sessions.Session adaptSession(Session session) {
        return session.acquireHistoricalSession(getAsOfClause());
    }

    public void addTests() {
        // Add ExpressionTestSuite...
        //
        //assert getSCNValue() != null;
        ExpressionTestSuite expressionTestSuite = new ExpressionTestSuite();

        //expressionTestSuite.setExecutor(getExecutor());
        //expressionTestSuite.addTests();
        for (Iterator iter = expressionTestSuite.getTests().iterator(); iter.hasNext();) {
            TestEntity baseTest = (TestEntity)iter.next();
            if (baseTest instanceof ReadAllExpressionTest) {
                ReadAllExpressionTest test = (ReadAllExpressionTest)baseTest;
                if (test.getExpression() == null) {
                    //System.out.println("This test does not have an expression yet: " + test.getName());
                    iter.remove();
                } else {
                    if (test.getReferenceClass().getPackage() != Employee.class.getPackage()) {
                        iter.remove();
                    } else {
                        // For these test setting it on the builder.
                        test.getQuery(true).dontMaintainCache();
                        test.getQuery().cascadeAllParts();
                        test.getExpression().getBuilder().asOf(getAsOfClause());
                    }
                }
            } else {
                iter.remove();
            }
        }
        addTest(expressionTestSuite);

        ExpressionSubSelectTestSuite subSelectTestSuite = new ExpressionSubSelectTestSuite();
        subSelectTestSuite.setExecutor(getExecutor());

        for (Iterator iter = subSelectTestSuite.getTests().iterator(); iter.hasNext();) {
            TestEntity baseTest = (TestEntity)iter.next();
            if (baseTest instanceof ReadAllExpressionTest) {
                ReadAllExpressionTest test = (ReadAllExpressionTest)baseTest;
                if (test.getExpression() == null) {
                    //System.out.println("This test does not have an expression yet: " + test.getName());
                    iter.remove();
                } else {
                    if ((test.getReferenceClass().getPackage() != Employee.class.getPackage()) || test.getName().equals("SubSelectCustomSQLTest")) {
                        iter.remove();
                    } else {
                        // For these must set on the entire expression, as multiple builders.
                        test.getQuery(true).dontMaintainCache();
                        test.getQuery().cascadeAllParts();
                        test.getExpression().asOf(getAsOfClause());
                    }
                }
            } else {
                iter.remove();
            }
        }
        addTest(subSelectTestSuite);

        /**ExpressionOuterJoinTestSuite outerJoinTestSuite = new ExpressionOuterJoinTestSuite();
        outerJoinTestSuite.setExecutor(getExecutor());
        outerJoinTestSuite.addTests();

        for (Iterator iter = outerJoinTestSuite.getTests().iterator(); iter.hasNext();) {
            TestEntity baseTest = (TestEntity)iter.next();
            if (baseTest instanceof ReadAllExpressionTest) {
                ReadAllExpressionTest test = (ReadAllExpressionTest)baseTest;
                if (test.getExpression() == null) {
                    //System.out.println("This test does not have an expression yet: " + test.getName());
                    iter.remove();
                } else {
                    if (test.getReferenceClass().getPackage() != Employee.class.getPackage()) {
                        iter.remove();
                    } else {
                        // For these test setting it on the builder.
                        test.getQuery(true).dontMaintainCache();
                        test.getExpression().getBuilder().asOf(getAsOfClause());
                    }
                }
            } else {
                iter.remove();
            }
        }
        addTest(outerJoinTestSuite);
        */

        // Run ReadObjectTestSuite using HistoricalSession, to test relationship
        // reading.
        TestSuite suite = EmployeeBasicTestModel.getReadObjectTestSuite();
        suite.setDescription("Tests object relationships in a HistoricalSession.");
        Vector suiteTests = suite.getTests();
        Vector newTests = new Vector(suiteTests.size());
        int numTests = suiteTests.size();
        HistoricalSessionTest hsTest;
        AutoVerifyTestCase wrappedTest;
        for (int i = 0; i < numTests; i++) {
            wrappedTest = (AutoVerifyTestCase)suiteTests.get(i);
            if (!(wrappedTest.getName().indexOf("Call") > 0)) {
                hsTest = new HistoricalSessionTest(wrappedTest, getAsOfClause());
                newTests.add(hsTest);
            }
        }
        suiteTests.clear();
        suiteTests.addAll(newTests);
        addTest(suite);

        // Run ReadObjectTestSuite using HistoricalSession acquired from either
        // a ClientSession or a ClientSessionBroker.
        suite = EmployeeBasicTestModel.getReadObjectTestSuite();
        suite.setName("AnySessionsTestSuite");
        suite.setDescription("Tests flashback on other session setups.");
        suiteTests = suite.getTests();
        newTests = new Vector(suiteTests.size());
        numTests = suiteTests.size();
        ClientSessionTestAdapter csTest;
        ClientSessionBrokerTestAdapter csbTest;
        boolean oneForYou = false;
        for (int i = 0; i < numTests; i++) {
            wrappedTest = (AutoVerifyTestCase)suiteTests.get(i);
            if (!(wrappedTest.getName().indexOf("Call") > 0)) {
                hsTest = new HistoricalSessionTest(wrappedTest, getAsOfClause());
                if (oneForYou) {
                    csTest = new ClientSessionTestAdapter(hsTest);
                    newTests.add(csTest);
                } else {
                    csbTest = new ClientSessionBrokerTestAdapter(hsTest);
                    newTests.add(csbTest);
                }
                oneForYou = !oneForYou;
            }
        }
        suiteTests.clear();
        suiteTests.addAll(newTests);
        addTest(suite);

        // Run read all test suite as of a past time using a HistoricalSession.
        // These tests came up with nothing useful at the time.
        suite = EmployeeBasicTestModel.getReadAllTestSuite();
        suiteTests = suite.getTests();
        newTests.clear();
        numTests = suiteTests.size();
        for (int i = 0; i < numTests; i++) {
            wrappedTest = (AutoVerifyTestCase)suiteTests.get(i);
            if (!(wrappedTest.getName().indexOf("Call") > 0)) {
                hsTest = new HistoricalSessionTest(wrappedTest, getAsOfClause());
                newTests.add(hsTest);
            }
        }
        suiteTests.clear();
        suiteTests.addAll(newTests);
        addTest(suite);
        // Now add some individual tests in here.
        TestSuite flashbackSuite = new FlashbackUnitTestSuite();
        flashbackSuite.setExecutor(getExecutor());
        addTest(flashbackSuite);
    }

    public void buildAsOfClause() {
        if (getSystemChangeNumber() != null) {
            asOfClause = new AsOfSCNClause(getSystemChangeNumber());
        } else {
            asOfClause = new AsOfClause(getTimestamp());
        }
    }

    /**
     * Find the system change number that reflects the initial fully
     * populated state of the database.
     * Assumes that when called the database is in its start state.
     */
    protected void calculateSystemChangeNumber() throws Exception {
        OraclePlatform platform = (OraclePlatform)getSession().getPlatform();
        ValueReadQuery scnQuery = platform.getSystemChangeNumberQuery();
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        int safetyCount = 1400;
        boolean validSCN = false;
        while ((safetyCount-- > 0) && !validSCN) {
            this.systemChangeNumber = (Number)getSession().executeQuery(scnQuery);
            //clonedQuery = (ReadAllQuery)query.clone();
            //query.setSelectionCriteria(query.getExpressionBuilder().get("salary").greaterThan(safetyCount));
            query.setAsOfClause(new AsOfSCNClause(systemChangeNumber));
            try {
                Vector result = (Vector)getSession().executeQuery(query);
                validSCN = true;
            } catch (Exception e) {
                // keep going...
                Thread.sleep(1000 * 30);
            }
        }
    }

    /**
     * By caching the result of this call, it is insured that
     * a new timestamp is not calculated after rows are deleted.
     * Furthermore, an asof time that maps to a desired snapshot of
     * the database must be had.
     */
    public void calculateTimestamp() {
        if (getTimestamp() == null) {
            OraclePlatform platform = (OraclePlatform)getSession().getPlatform();
            ValueReadQuery timestampQuery = platform.getTimestampQuery();
            ReadAllQuery query = new ReadAllQuery(Employee.class);
            query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").notEqual("Eun Kyung"));
            ReadAllQuery clonedQuery = null;
            int safetyCount = 1400;
            while ((safetyCount-- > 0) && (getTimestamp() == null)) {
                java.util.Date timestamp = (java.util.Date)getSession().executeQuery(timestampQuery);
                clonedQuery = query;
                clonedQuery.setAsOfClause(new AsOfClause(timestamp));
                Vector result = (Vector)getSession().executeQuery(clonedQuery);
                if (result.size() == 12) {
                    setTimestamp((java.sql.Timestamp)timestamp);
                } else {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new TestErrorException("Unable to wait for valid timstamp.", e);
                    }
                }
            }
        }
    }

    /**
     * This idea of calculateTimestamp is to get a snapshot at which time
     * the database is fully populated.
     * Tries going further and further back in time to find one.
     * Note if go back too far, or before the last time a table was altered, an
     * exception will result.
     */
    @SuppressWarnings("deprecation")
    public void calculateTimestampHopefully() {
        if (getTimestamp() != null) {
            return;
        }
        OraclePlatform platform = (OraclePlatform)getSession().getPlatform();
        ValueReadQuery timestampQuery = platform.getTimestampQuery();
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.dontMaintainCache();
        query.addPartialAttribute("id");
        ReadObjectQuery asOfQuery = null;
        try {
            long asOfTime = ((Timestamp)getSession().executeQuery(timestampQuery)).getTime();
            long timeToGoBack = 0;
            while (getTimestamp() == null) {
                asOfQuery = (ReadObjectQuery)query.clone();
                asOfQuery.setAsOfClause(new AsOfClause(new java.sql.Timestamp(asOfTime - timeToGoBack)));
                if (getSession().executeQuery(asOfQuery) != null) {
                    setTimestamp(new Timestamp(asOfTime - timeToGoBack));
                } else {
                    // go back 10 minutes each time.
                    timeToGoBack += ((timeToGoBack == 0) ? (1000 * 60) : timeToGoBack);
                }
            }
        } catch (Exception e) {
            System.out.println("Went too far back, so can't tell this.");
        }
    }

    private void configure() throws Exception {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Flashback is only supported on Oracle 9R2 or later databases.");
        }
        TestSystem system = new EmployeeSystem();

        if (STANDARD_SETUP) {
            system.run(getSession());
            // This delay here is nasty but inevitable.  Every time the tables get
            // recreated (i.e. by every test model) flashback is disabled for
            // 5 minutes.
            // Now sleeps for 5 seconds only - seem to be ok.
            System.out.println("Starting to sleep for 5 seconds.");
            Thread.sleep(1000 * 5);
            System.out.println("Have stopped sleeping for 5 seconds.");
            calculateSystemChangeNumber();
            //calculateTimestamp();
            buildAsOfClause();
            depopulate();
            return;
        }

        // Only in the non standard case, try to avoid the five minute setup hit.
        // Only useable when run individually at a separate time.
        boolean mustAddTables = false;
        Vector existingEmployees = null;

        // First check that descriptors exist?
        try {
            Object result = getSession().readObject(Employee.class);
        } catch (QueryException qe) {
            system.addDescriptors((DatabaseSession)getSession());
        } catch (Exception goNextStep) {
        }

        // Now do tables exist on database?
        try {
            existingEmployees = getSession().readAllObjects(Employee.class);
        } catch (DatabaseException de) {
            mustAddTables = true;
        }
        if (mustAddTables || !QUICK_SETUP) {
            if (mustAddTables) {
                system.createTables((DatabaseSession)getSession());
            }
            system.populate((DatabaseSession)getSession());
            // Must init identity maps as contains same objects as in
            // population manager!
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            // Wait for a snapshot that shows the newly added items.
            // This is going to take five minutes.
            calculateSystemChangeNumber();
            //calculateTimestamp();
            depopulate();
        } else {
            depopulate(true);
            system.populate((DatabaseSession)getSession());
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            calculateSystemChangeNumber();
            depopulate();

            //calculateTimestampHopefully();
            // Must at this time fill up the PopulationManager.
            // This is pretty useless though, as they all have the
            // wrong primary keys.
            //EmployeePopulator populator = new EmployeePopulator();
            //populator.buildExamples();
            //if (existingEmployees != null) {
            //depopulate();
            //}
        }
        buildAsOfClause();
        return;
    }

    public void depopulate() throws Exception {
        depopulate(false);
    }

    public void depopulate(boolean fully) throws Exception {
        try {
            // Now here is the tricky part: corrupt all the objects
            // that exist right now!  Or at least some of them.
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Vector employees = uow.readAllObjects(Employee.class);
            Vector projects = uow.readAllObjects(LargeProject.class);
            Vector smallProjects = uow.readAllObjects(SmallProject.class);
            Vector addresses = uow.readAllObjects(Address.class);
            for (Enumeration enumtr = employees.elements(); enumtr.hasMoreElements();) {
                Employee emp = (Employee)enumtr.nextElement();

                //emp.setAddress(null);
                emp.setProjects(null);
                emp.setManager(null);
                emp.setManagedEmployees(new Vector());
                emp.setAddress(null);
            }
            for (Enumeration enumtr = projects.elements(); enumtr.hasMoreElements();) {
                LargeProject project = (LargeProject)enumtr.nextElement();
                project.setTeamLeader(null);
            }

            //uow.commit();
            //getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            //uow = getSession().acquireUnitOfWork();
            //employees = (Vector)uow.readAllObjects(Employee.class);
            uow.deleteAllObjects(employees);
            if (fully) {
                uow.deleteAllObjects(projects);
                uow.deleteAllObjects(smallProjects);
                uow.deleteAllObjects(addresses);
            }
            uow.commit();
        } catch (EclipseLinkException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Could not depopulate table.");
            System.out.println("Exception:"+e);
            e.printStackTrace();
        }
    }

    public Number getSystemChangeNumber() {
        return systemChangeNumber;
    }

    public AsOfClause getAsOfClause() {
        return asOfClause;
    }

    public static java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public static long getTimestampValue() {
        return getTimestamp().getTime();
    }

    public void setAsOfClause(AsOfClause asOfClause) {
        this.asOfClause = asOfClause;
    }

    public static void setTimestamp(java.sql.Timestamp newTimestamp) {
        timestamp = newTimestamp;
    }

    /**
     * Assume setup() is called prior to addTests.  This seems bizarre
     * but is the way it works.
     */
    public void setup() {
        // Must do configuration here...
        if (getTimestamp() != null) {
            return;
        }
        try {
            configure();
        } catch (EclipseLinkException te) {
            throw te;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void reset() {
        if (getTimestamp() == null) {
            return;
        }
        try {
            if (QUICK_SETUP) {
                TestSystem system = new EmployeeSystem();
                system.populate((DatabaseSession)getSession());
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            }
        } catch (Exception ignore) {
        }
    }
}
