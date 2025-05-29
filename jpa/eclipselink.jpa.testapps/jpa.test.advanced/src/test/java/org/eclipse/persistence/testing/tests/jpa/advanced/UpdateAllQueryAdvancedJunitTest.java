/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.tests.jpa.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.UpdateAllQueryTestHelper;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;

import java.util.HashMap;
import java.util.Vector;

public class UpdateAllQueryAdvancedJunitTest extends JUnitTestCase {

    static protected Class<?>[] classes = {Employee.class, Address.class, PhoneNumber.class, Project.class};
    static protected Vector<?>[] objectVectors = {null, null, null, null};

    static protected EmployeePopulator populator;

    public UpdateAllQueryAdvancedJunitTest() {
        super();
    }

    public UpdateAllQueryAdvancedJunitTest(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        if (getServerSession().getPlatform().isSymfoware()) {
            warning("UpdateAllQueryAdvancedJunitTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        clearCache();
        super.setUp();
        if(!compare()) {
            clear();
            populate();
        }
    }

    protected static DatabaseSession getDbSession() {
        return getServerSession();
    }

    protected static UnitOfWork acquireUnitOfWork() {
        return getDbSession().acquireUnitOfWork();
    }

    protected void clear() {
        UnitOfWork uow = acquireUnitOfWork();

        UpdateAllQuery updateEmployees = new UpdateAllQuery(Employee.class);
        updateEmployees.addUpdate("manager", null);
        updateEmployees.addUpdate("address", null);
        uow.executeQuery(updateEmployees);

        UpdateAllQuery updateProjects = new UpdateAllQuery(Project.class);
        updateProjects.addUpdate("teamLeader", null);
        uow.executeQuery(updateProjects);

        uow.executeQuery(new DeleteAllQuery(PhoneNumber.class));
        uow.executeQuery(new DeleteAllQuery(Address.class));
        uow.executeQuery(new DeleteAllQuery(Employee.class));
        uow.executeQuery(new DeleteAllQuery(Project.class));

        uow.commit();
        clearCache();
    }

    protected void populate() {
        if (populator == null) {
            populator = new EmployeePopulator(supportsStoredProcedures());
        }
        populator.buildExamples();
        populator.persistExample(getDbSession());
        clearCache();
        for(int i=0; i < classes.length; i++) {
            objectVectors[i] = getDbSession().readAllObjects(classes[i]);
        }
        clearCache();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("UpdateAllQueryAdvancedJunitTest");

        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testSetup"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testFirstNamePrefixBLAForAll"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testFirstNamePrefixBLAForSalary"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testDoubleSalaryForAll"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testDoubleSalaryForSalary"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testFirstNamePrefixBLADoubleSalaryForAll"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testFirstNamePrefixBLADoubleSalaryForSalary"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testFirstNamePrefixBLADoubleSalaryForSalaryForFirstName"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testAssignManagerName"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testAssignNullToAddress"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testAssignObjectToAddress"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testAssignExpressionToAddress"));
        suite.addTest(new UpdateAllQueryAdvancedJunitTest("testAggregate"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    @Override
    public void tearDown() {
        new UpdateAllQueryAdvancedJunitTest().clearCache();
    }

    public void testFirstNamePrefixBLAForAll() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateAllQueryInternal(updateQuery);
    }

    public void testFirstNamePrefixBLAForSalary() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000);
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateAllQueryInternal(updateQuery);
    }

    public void testDoubleSalaryForAll() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), Integer.valueOf(2)));
        updateAllQueryInternal(updateQuery);
    }

    public void testDoubleSalaryForSalary() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000);
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), Integer.valueOf(2)));
        updateAllQueryInternal(updateQuery);
    }

    public void testFirstNamePrefixBLADoubleSalaryForAll() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), Integer.valueOf(2)));
        updateAllQueryInternal(updateQuery);
    }

    public void testFirstNamePrefixBLADoubleSalaryForSalary() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000);
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), Integer.valueOf(2)));
        updateAllQueryInternal(updateQuery);
    }

    public void testFirstNamePrefixBLADoubleSalaryForSalaryForFirstName() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000).and(builder.get("firstName").like("J%"));
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), Integer.valueOf(2)));
        updateAllQueryInternal(updateQuery);
    }

    public void testAssignManagerName() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("manager").notNull();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", builder.get("manager").get("firstName"));
        updateAllQueryInternal(updateQuery);
    }

    public void testAssignNullToAddress() {
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("address", null);
        updateAllQueryInternal(updateQuery);
    }

    public void testAssignObjectToAddress() {
        Address address = new Address();
        address.setCountry("Canada");
        address.setProvince("Ontario");
        address.setCity("Ottawa");
        address.setStreet("O'Connor");
        UnitOfWork uow = acquireUnitOfWork();
        uow.registerNewObject(address);
        uow.commit();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("address", address);
        updateAllQueryInternal(updateQuery);
    }

    public void testAssignExpressionToAddress() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("manager").notNull();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("address", builder.get("manager").get("address"));
        updateAllQueryInternal(updateQuery);
    }

    public void testAggregate() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("manager").notNull();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate(builder.get("period").get("startDate"), builder.get("period").get("endDate"));
        updateQuery.addUpdate(builder.get("period").get("endDate"), builder.get("period").get("startDate"));
        updateAllQueryInternal(updateQuery);
    }

    protected static void updateAllQueryInternal(Class<?> referenceClass, HashMap updateClauses, Expression selectionExpression) {
        if (getServerSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("UpdateAllQueryAdvancedJunitTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        String errorMsg = UpdateAllQueryTestHelper.execute(getDbSession(), referenceClass, updateClauses, selectionExpression);
        if(errorMsg != null) {
            fail(errorMsg);
        }
    }

    protected static void updateAllQueryInternal(UpdateAllQuery uq) {
        if (getServerSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("UpdateAllQueryAdvancedJunitTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        getServerSession().beginTransaction();
        String errorMsg = UpdateAllQueryTestHelper.execute(getDbSession(), uq);
        getServerSession().commitTransaction();
        if(errorMsg != null) {
            fail(errorMsg);
        }
    }

    protected static boolean compare() {
        for(int i=0; i < classes.length; i++) {
            if(!compare(i)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean compare(int i) {
        if(objectVectors[i] == null) {
            return false;
        }
        Vector<?> currentVector = getDbSession().readAllObjects(classes[i]);
        if(currentVector.size() != objectVectors[i].size()) {
            return false;
        }
        ClassDescriptor descriptor = getDbSession().getDescriptor(classes[i]);
        for(int j=0; j < currentVector.size(); j++) {
            Object obj1 = objectVectors[i].elementAt(j);
            Object obj2 = currentVector.elementAt(j);
            if(!descriptor.getObjectBuilder().compareObjects(obj1, obj2, (org.eclipse.persistence.internal.sessions.AbstractSession)getDbSession())) {
                return false;
            }
        }
        return true;
    }
}
