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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.flashback;

import java.util.*;
import java.math.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.history.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * <b>Purpose:</b> Tests all those unique scenarios involving
 * Flashback that can not easily be generalized into a few
 * generic cases.
 */
public class FlashbackUnitTestSuite extends TestSuite {
    public FlashbackUnitTestSuite() {
        setDescription("Fast unit testing of the flashback feature.");
    }

    protected Object getSystemChangeNumber() {
        return ((FlashbackTestModel)getContainer()).getSystemChangeNumber();
    }

    protected Object getTimestamp() {
        return FlashbackTestModel.getTimestamp();
    }

    protected AsOfClause getAsOfClause() {
        return ((FlashbackTestModel)getContainer()).getAsOfClause();
    }

    public void _testAsOfStringLiteralTest() {
        if (getSession().getProject().hasGenericHistorySupport()) {
            return;
        }
        ValueReadQuery scnQuery = ((OraclePlatform)getSession().getPlatform()).getSystemChangeNumberQuery();
        long sCNNow = ((BigDecimal)getSession().executeQuery(scnQuery)).longValue();
        long testSCN = ((Number)getAsOfClause().getValue()).longValue();
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        AsOfClause sCNClause = new AsOfSCNClause(builder.value("" + sCNNow + " - " + (sCNNow - testSCN)));

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setAsOfClause(sCNClause);
        query.dontMaintainCache();
        Vector result = (Vector)getSession().executeQuery(query);
        if (result.size() != 12) {
            throw new TestErrorException("Expected 12 objects, read " + result.size());
        }
    }

    public void _testAsOfParameterTest() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.addArgument("ASOFCLAUSE");

        Vector arguments = new Vector();
        Object value = getAsOfClause().getValue();
        arguments.add(value);

        Expression parameterExp = query.getExpressionBuilder().getParameter("ASOFCLAUSE");
        if (getAsOfClause().isAsOfSCNClause()) {
            query.setAsOfClause(new AsOfSCNClause(parameterExp));
        } else {
            query.setAsOfClause(new AsOfClause(parameterExp));
        }

        Vector result = (Vector)getSession().executeQuery(query, arguments);
        if (result.size() != 12) {
            throw new TestErrorException("Expected 12 objects, read " + result.size());
        }

        /*arguments.set(0, new java.sql.Timestamp(System.currentTimeMillis()));
        result = (Vector)getSession().executeQuery(query, arguments);
        if (result.size() != 0) {
            throw new TestErrorException("Expected 0 objects, read " + result.size());
        }*/
    }

    public void _testAsOfCurrentTimeMillisTest() {
        if (getSession().getProject().hasGenericHistorySupport()) {
            return;
        }
        // Must get the current time from the database as a future or too long pass time will trigger an error.
        long value = ((Date)getSession().executeQuery(new ValueReadQuery("Select SYSDATE from DUAL"))).getTime();
        AsOfClause clause = new AsOfClause(value);
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.setAsOfClause(clause);

        Vector employees = (Vector)getSession().executeQuery(query);

        // Since the current time gets mapped to the SCN +/- 5 minutes,
        // the query of the current time may, or may contain employees,
        // so cannot verify the result, just make sure the query worked.
    }

    public void _testAsOfCurrentTimeMillisParameterTest() {
        if (getSession().getProject().hasGenericHistorySupport()) {
            return;
        }

        // Must get the current time from the database as a future or too long pass time will trigger an error.
        long value = ((Date)getSession().executeQuery(new ValueReadQuery("Select SYSDATE from DUAL"))).getTime();
        AsOfClause clause = new AsOfClause((new ExpressionBuilder()).getParameter("TIME"));
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.setAsOfClause(clause);
        query.addArgument("TIME");
        Vector arguments = new Vector();
        arguments.add(new Long(value));

        Vector employees = (Vector)getSession().executeQuery(query, arguments);
        if (employees.size() != 0) {
            throw new TestErrorException("Somehow the long value was not converted to a proper timestamp.  Check the SQL against: " + value);
        }
    }

    public void _testAsOfExpressionMathTest() {
        if (!getAsOfClause().isAsOfSCNClause()) {
            return;
        }
        ValueReadQuery scnQuery = ((OraclePlatform)getSession().getPlatform()).getSystemChangeNumberQuery();
        long sCNNow = ((BigDecimal)getSession().executeQuery(scnQuery)).longValue();
        long testSCN = ((BigDecimal)getAsOfClause().getValue()).longValue();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ExpressionMath.subtract(builder.value(sCNNow), builder.value(sCNNow - testSCN));

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setAsOfClause(new AsOfSCNClause(expression));
        query.dontMaintainCache();
        Vector result = (Vector)getSession().executeQuery(query);
        if (result.size() != 12) {
            throw new TestErrorException("Expected 12 objects, read " + result.size());
        }
    }

    /**
     * A query result can be cached normally if the builder is not as of
     * a past time.  However, if this condition is true but a a joined attribute
     * as of a past time is added, what will happen?
     * <p>What should happen is either an exception should be thrown, or the
     * joined attribute should be read as of the current time.  This test will find out
     * which.
     */
    public void _testCacheCorruptedByJoinedAttributeTest() {
        // Try reading all projects now connected to all projects as of a past time.
        try {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();

            ExpressionBuilder project = new ExpressionBuilder(LargeProject.class);
            ExpressionBuilder oldProject = new ExpressionBuilder(LargeProject.class);
            oldProject.asOf(getAsOfClause());
            Expression criteria = project.equal(oldProject).and(oldProject.get("teamLeader").notNull());
            ReadAllQuery query = new ReadAllQuery(LargeProject.class, criteria);
            query.addJoinedAttribute(oldProject.get("teamLeader"));
            Vector projects = (Vector)getSession().executeQuery(query);
            for (int i = 0; i < projects.size(); i++) {
                LargeProject next = (LargeProject)projects.get(i);
                if (next.getTeamLeader() != null) {
                    throw new TestErrorException("The dynamic query must still return objects as they exist now, even if a joined attribute is added as of a past time.");
                }
            }
        } finally {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    /**
     * Tests corruption from joined attributes again.
     * This test is simpler and more lethal than the previous one, for it cuts to
     * the core of the problem.  To tell if past rows are being returned, we can
     * not just look at the builder.
     */
    public void _testCacheCorruptedByJoinedAttribute2Test() {
        // Try reading all projects now connected to all projects as of a past time.
        try {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();

            ExpressionBuilder project = new ExpressionBuilder(LargeProject.class);
            Expression criteria = project.get("teamLeader").asOf(getAsOfClause()).notNull();
            ReadAllQuery query = new ReadAllQuery(LargeProject.class, criteria);
            query.addJoinedAttribute("teamLeader");
            Vector projects = (Vector)getSession().executeQuery(query);
            for (int i = 0; i < projects.size(); i++) {
                LargeProject next = (LargeProject)projects.get(i);
                if (next.getTeamLeader() != null) {
                    throw new TestErrorException("The dynamic query must still return objects as they exist now, even if a joined attribute is added on an attribute read as of a past time.");
                }
            }
        } finally {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    /**
     * Tests corruption from joined attributes again.
     * This test is simpler and more lethal than the previous one, for it cuts to
     * the core of the problem.  To tell if past rows are being returned, we can
     * not just look at the builder.
     */
    public void _testCacheCorruptedByBatchAttributeTest() {
        // Try reading all projects now connected to all projects as of a past time.
        try {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();

            ExpressionBuilder project = new ExpressionBuilder(LargeProject.class);
            Expression criteria = project.get("teamLeader").asOf(getAsOfClause()).notNull();
            ReadAllQuery query = new ReadAllQuery(LargeProject.class, criteria);
            query.addBatchReadAttribute("teamLeader");
            Vector projects = (Vector)getSession().executeQuery(query);
            for (int i = 0; i < projects.size(); i++) {
                LargeProject next = (LargeProject)projects.get(i);
                if (next.getTeamLeader() != null) {
                    throw new TestErrorException("The dynamic query must still return objects as they exist now, even if a batched attribute is added on an attribute read as of a past time.");
                }
            }
        } finally {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    /**
     * This is testing for a condition that should never happen.
     */
    public void _testCacheIsolationTest() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Session hs = getSession().acquireHistoricalSession(getAsOfClause());
        try {
            hs.readAllObjects(Employee.class);
            Vector employees = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getAllFromIdentityMap(null, Employee.class, null);
            if ((employees != null) && (employees.size() > 0)) {
                throw new TestErrorException("" + employees.size() + " objects read in a HistoricalSession were cached in the global session.");
            }
        } finally {
            hs.release();
        }
    }

    /**
     * This is testing for a condition that should never happen.
     */
    public void _testCacheIsolationAcrossRelationshipsTest() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Session hs = getSession().acquireHistoricalSession(getAsOfClause());
        try {
            Vector pastEmployees = hs.readAllObjects(Employee.class);
            for (int i = 0; i < pastEmployees.size(); i++) {
                Employee employee = (Employee)pastEmployees.get(i);
                employee.getProjects();
                employee.getAddress();
                employee.getPhoneNumbers();
            }
            Vector projects = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getAllFromIdentityMap(null, Project.class, null);
            Vector addresses = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getAllFromIdentityMap(null, Address.class, null);
            Vector phoneNumbers = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getAllFromIdentityMap(null, PhoneNumber.class, null);

            if ((projects.size() > 0) || (addresses.size() > 0) || (phoneNumbers.size() > 0)) {
                throw new TestErrorException("" + projects.size() + " projects, " + addresses.size() + " addresses, and " + phoneNumbers.size() + " phone numbers read in a HistoricalSession were cached in the global session.");
            }
        } finally {
            hs.release();
        }
    }

    public void _testCannotExecuteWriteInHistoricalSessionExceptionTest() {
        Session hs = getSession().acquireHistoricalSession(getAsOfClause());
        try {
            ((org.eclipse.persistence.internal.sessions.AbstractSession)hs).writeObject(new Employee());
            throw new TestErrorException("Exception not thrown in executing a write in a TimeAwareSession.");
        } catch (ValidationException ve) {
            if (ve.getErrorCode() != QueryException.INVALID_QUERY_ON_HISTORICAL_SESSION) {
                throw new TestErrorException("Wrong exception thrown.", ve);
            }
        } catch (TestErrorException tee) {
            throw tee;
        } catch (Exception e) {
            throw new TestErrorException("Exception not thrown in executing a write in a Historical session.  Instead triggered: ", e);
        } finally {
            hs.release();
        }
    }

    public void _testDynamicQueryUsingParallelExpressionTest() {
        // Try reading all projects now connected to all projects as of a past time.
        try {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();

            ExpressionBuilder project = new ExpressionBuilder(LargeProject.class);
            ExpressionBuilder oldProject = new ExpressionBuilder(LargeProject.class);
            oldProject.asOf(getAsOfClause());
            Expression criteria = project.equal(oldProject).and(oldProject.get("teamLeader").notNull());
            Vector projects = getSession().readAllObjects(LargeProject.class, criteria);
            if (projects.size() != 3) {
                throw new TestErrorException("Expected to read 6 projects, instead read: " + projects.size());
            }
            for (int i = 0; i < projects.size(); i++) {
                LargeProject next = (LargeProject)projects.get(i);
                if (next.getTeamLeader() != null) {
                    throw new TestErrorException("The dynamic query must still return objects as they exist now.");
                }
            }
        } finally {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void _testDynamicQueryUsingQueryKeyTest() {
        // Try reading all projects now connected to all projects as of a past time.
        try {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
            ExpressionBuilder builder = new ExpressionBuilder(LargeProject.class);
            Expression joinCriteria = builder.getField("PROJECT.PROJ_ID").equal(builder.getParameter("PROJECT.PROJ_ID"));
            OneToOneQueryKey self = new OneToOneQueryKey();
            self.setName("this");
            self.setJoinCriteria(joinCriteria);
            self.setReferenceClass(LargeProject.class);
            getSession().getDescriptor(LargeProject.class).addQueryKey(self);

            ExpressionBuilder project = new ExpressionBuilder(LargeProject.class);
            Expression oldProject = project.get("this");
            oldProject.asOf(getAsOfClause());
            Expression criteria = oldProject.get("teamLeader").notNull();
            Vector projects = getSession().readAllObjects(LargeProject.class, criteria);
            if (projects.size() != 3) {
                throw new TestErrorException("Expected to read 6 projects, instead read: " + projects.size());
            }
            for (int i = 0; i < projects.size(); i++) {
                LargeProject next = (LargeProject)projects.get(i);
                if (next.getTeamLeader() != null) {
                    throw new TestErrorException("The dynamic query must still return objects as they exist now.");
                }
            }
        } finally {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void _testNoNestedHistoricalSessionsAllowedExceptionTest() {
        Session hs = getSession().acquireHistoricalSession(getAsOfClause());
        try {
            Session nhs = hs.acquireHistoricalSession(getAsOfClause());
            nhs.release();
            throw new TestErrorException("Exception not thrown in acquiring a nested HistoricalSession.");
        } catch (ValidationException ve) {
            if (ve.getErrorCode() != ValidationException.CANNOT_ACQUIRE_HISTORICAL_SESSION) {
                throw new TestErrorException("Wrong exception thrown.", ve);
            }
        } catch (TestErrorException tee) {
            throw tee;
        } catch (Exception e) {
            throw new TestErrorException("Exception not thrown in reading past versions of objects into the global cache.  Instead triggered: ", e);
        } finally {
            hs.release();
        }
    }

    public void _testNoTransactionsInHistoricalSessionExceptionTest() {
        HistoricalSession hs = (HistoricalSession)getSession().acquireHistoricalSession(getAsOfClause());
        try {
            hs.beginTransaction();
            hs.rollbackTransaction();
            throw new TestErrorException("Exception not thrown in beginning a transaction.");
        } catch (ValidationException ve1) {
            if (ve1.getErrorCode() != ValidationException.OPERATION_NOT_SUPPORTED) {
                throw new TestErrorException("Wrong exception thrown.", ve1);
            } else {
                try {
                    hs.commitTransaction();
                } catch (ValidationException ve2) {
                    if (ve2.getErrorCode() != ValidationException.OPERATION_NOT_SUPPORTED) {
                        throw new TestErrorException("Wrong exception thrown.", ve2);
                    } else {
                        try {
                            hs.rollbackTransaction();
                        } catch (ValidationException ve3) {
                            if (ve3.getErrorCode() != ValidationException.OPERATION_NOT_SUPPORTED) {
                                throw new TestErrorException("Wrong exception thrown.", ve3);
                            } else {
                                try {
                                    hs.acquireUnitOfWork();
                                } catch (ValidationException ve4) {
                                    if (ve4.getErrorCode() != ValidationException.OPERATION_NOT_SUPPORTED) {
                                        throw new TestErrorException("Wrong exception thrown.", ve4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (TestErrorException tee) {
            throw tee;
        } catch (Exception e) {
            throw new TestErrorException("Exception not thrown in reading past versions of objects into the global cache.  Instead triggered: ", e);
        } finally {
            hs.release();
        }
    }

    public void _testParameterBindingTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").equal(builder.getParameter("FIRSTNAME"));
        Vector arguments = new Vector();
        arguments.add("Bob");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setAsOfClause(getAsOfClause());
        query.dontMaintainCache();
        query.addArgument("FIRSTNAME");
        Employee result = (Employee)getSession().executeQuery(query, arguments);
        if (!result.getFirstName().equals("Bob")) {
            throw new TestErrorException("Expected Bob, read " + result);
        }
        arguments.set(0, "Sarah");
        result = (Employee)getSession().executeQuery(query, arguments);
        if (!result.getFirstName().equals("Sarah")) {
            throw new TestErrorException("Expected Sarah, read " + result);
        }
    }

    /**
     * Tests the entire point of a TimeAwareSession, which is to allow
     * caching of read results, which in turn guarantees that circular references
     * form a circle, not an infinite loop.
     */
    public void _testSuccessfulCachingTest() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Session hs = getSession().acquireHistoricalSession(getAsOfClause());
        try {
            Employee pastManager = (Employee)hs.readObject(Employee.class, new ExpressionBuilder().anyOf("managedEmployees").get("id").greaterThan(0));
            Vector employees = pastManager.getManagedEmployees();
            for (int i = 0; i < employees.size(); i++) {
                Employee employee = (Employee)employees.get(i);
                if (employee.getManager() != pastManager) {
                    throw new TestErrorException("Objects read in a HistoricalSession are not being cached properly by identity.");
                }
            }
        } finally {
            hs.release();
        }
    }

    public void _testOuterJoinTest() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Outer joins do not currently work with history");
        }
        ReadAllQuery query = new ReadAllQuery(LargeProject.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.getAllowingNull("teamLeader").get("firstName").equal("Sarah");
        expression = expression.or(builder.get("name").equal("TOPEmployee Management"));
        query.setSelectionCriteria(expression);
        query.dontMaintainCache();
        query.setAsOfClause(getAsOfClause());
        Vector result = (Vector)getSession().executeQuery(query);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        if ((result == null) || (result.size() != 2)) {
            throw new TestErrorException("Expected 2 objects, read " + result.size());
        }
    }

    public void _testHistoricalQueriesMustPreserveGlobalCacheExceptionTest() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Bob"));
        query.setAsOfClause(new AsOfSCNClause(new Long(0)));
        try {
            getSession().executeQuery(query);
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
            throw new TestErrorException("Exception not thrown in reading past versions of objects into the global cache.");
        } catch (QueryException qe) {
            if (qe.getErrorCode() != QueryException.HISTORICAL_QUERIES_MUST_PRESERVE_GLOBAL_CACHE) {
                throw new TestErrorException("Wrong exception thrown.", qe);
            }
        } catch (TestErrorException tee) {
            throw tee;
        } catch (Exception e) {
            throw new TestErrorException("Exception not thrown in reading past versions of objects into the global cache.  Instead triggered: ", e);
        }
    }

    public void _testHistoricalQueriesOnlySupportedOnOracleExceptionTest() {
        boolean genericHistory = getSession().getProject().hasGenericHistorySupport();
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Bob"));
        query.setAsOfClause(getAsOfClause());
        query.dontMaintainCache();
        DatabaseLogin oldLogin = getSession().getLogin();
        DatabaseLogin newLogin = (DatabaseLogin)oldLogin.clone();
        newLogin.useAccess();
        try {
            getAbstractSession().setLogin(newLogin);
            getSession().executeQuery(query);
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
            if (!genericHistory) {
                throw new TestErrorException("Exception not thrown in reading past versions of objects outside of Oracle.");
            }
        } catch (QueryException qe) {
            if (qe.getErrorCode() != QueryException.HISTORICAL_QUERIES_ONLY_SUPPORTED_ON_ORACLE) {
                throw new TestErrorException("Wrong exception thrown.", qe);
            }
        } catch (TestErrorException tee) {
            throw tee;
        } catch (Exception e) {
            throw new TestErrorException("Exception not thrown in reading past versions of objects outside of Oracle.  Instead triggered: " + e.toString(), e);
        } finally {
            getAbstractSession().setLogin(oldLogin);
        }
    }

    public void _testHistoricalSessionOnlySupportedOnOracleExceptionTest() {
        if (getSession().getProject().hasGenericHistorySupport()) {
            return;
        }
        DatabaseLogin oldLogin = getSession().getLogin();
        DatabaseLogin newLogin = (DatabaseLogin)oldLogin.clone();
        newLogin.useAccess();
        try {
            getAbstractSession().setLogin(newLogin);
            Session hs = getSession().acquireHistoricalSession(getAsOfClause());
            hs.release();
            throw new TestErrorException("Exception not thrown in acquiring a HistoricalSession outside of Oracle.");
        } catch (ValidationException ve) {
            if (ve.getErrorCode() != ValidationException.HISTORICAL_SESSION_ONLY_SUPPORTED_ON_ORACLE) {
                throw new TestErrorException("Wrong exception thrown.", ve);
            }
        } catch (TestErrorException tee) {
            throw tee;
        } catch (Exception e) {
            throw new TestErrorException("Exception not thrown in reading past versions of objects into the global cache.  Instead triggered: ", e);
        } finally {
            getAbstractSession().setLogin(oldLogin);
        }
    }

    public void addTests() {
        addTest(new UnitTestCase("AsOfCurrentTimeMillisTest"));
        // broken addTest(new UnitTestCase("AsOfCurrentTimeMillisParameterTest"));
        addTest(new UnitTestCase("AsOfExpressionMathTest"));
        addTest(new UnitTestCase("AsOfParameterTest"));
        addTest(new UnitTestCase("AsOfStringLiteralTest"));
        // broken addTest(new UnitTestCase("CacheCorruptedByJoinedAttributeTest"));
        //broken addTest(new UnitTestCase("CacheCorruptedByJoinedAttribute2Test"));
        addTest(new UnitTestCase("CacheCorruptedByBatchAttributeTest"));
        addTest(new UnitTestCase("CacheIsolationTest"));
        addTest(new UnitTestCase("CacheIsolationAcrossRelationshipsTest"));
        addTest(new UnitTestCase("CannotExecuteWriteInHistoricalSessionExceptionTest"));
        addTest(new UnitTestCase("DynamicQueryUsingQueryKeyTest"));
        addTest(new UnitTestCase("DynamicQueryUsingParallelExpressionTest"));
        addTest(new UnitTestCase("NoNestedHistoricalSessionsAllowedExceptionTest"));
        addTest(new UnitTestCase("NoTransactionsInHistoricalSessionExceptionTest"));
        addTest(new UnitTestCase("SuccessfulCachingTest"));
        addTest(new UnitTestCase("OuterJoinTest"));
        addTest(new UnitTestCase("HistoricalQueriesMustPreserveGlobalCacheExceptionTest"));
        addTest(new UnitTestCase("HistoricalQueriesOnlySupportedOnOracleExceptionTest"));
        addTest(new UnitTestCase("HistoricalSessionOnlySupportedOnOracleExceptionTest"));
    }
}
