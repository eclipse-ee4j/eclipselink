/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.sql.Types;
import java.util.*;

import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.InsertObjectTest;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.tests.employee.EmployeeDeleteTest;
import org.eclipse.persistence.testing.tests.employee.ProjectDeleteTest;

public class CustomSQLTestModel extends TestModel {
    public CustomSQLTestModel() {
        setDescription("This model tests reading/writing/deleting  using Custom SQL with the employee demo.");
    }

    public CustomSQLTestModel(String description) {
        setDescription(description);
    }

    /**
     * This sets the custom SQL for the populate.
     */
    public void addForcedRequiredSystems() {
        getExecutor().removeConfigureSystem(new EmployeeSystem());

        // Force the database to be recreated using custom SQL.
        addForcedRequiredSystem(new EmployeeCustomSQLSystem());
        if (getSession().getPlatform().isOracle()) {
            addForcedRequiredSystem(new InsuranceORStoredProcedureSystem());
        }
        // Force field names to upper case for custom SQL tests on postgres.
        if (getSession().getPlatform().isPostgreSQL()) {
            getSession().getPlatform().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    public void addRequiredSystems() {
        addRequiredSystem(new org.eclipse.persistence.testing.models.legacy.LegacySystem());
    }

    /**
    * Ensure that the employee model is setup and change the employee descriptor to use custom SQL.
    */
    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getRefreshObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getSelectWithOutputParametersTestSuite());
        addTest(getOutputParametersTestSuite());
        addTest(getStoredProcedureTestSuite());
        addTest(getStoredFunctionTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLDeleteObjectTestSuite");
        suite.setDescription("This suite tests delete SQL.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0002")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(SmallProject.class, "0003")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(LargeProject.class, "0001")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLInsertObjectTestSuite");
        suite.setDescription("This suite tests insert SQL.");
        org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator system = new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator();

        suite.addTest(new InsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample5()));

        return suite;
    }

    public TestSuite getOutputParametersTestSuite() {
        TestSuite test = new TestSuite();
        test.setDescription("This Suite is used to test special Output Parameter functions ");
        test.setName("OutputParametersTestSuite");

        test.addTest(new OutputParameterEventTest((Employee)(new EmployeePopulator()).basicEmployeeExample10()));

        return test;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLReadAllTestSuite");
        suite.setDescription("This suite tests read all sql.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new CustomSQLSubQueryTest());

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLReadObjectTestSuite");
        suite.setDescription("This suite test read sql.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new CacheHitTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new CustomSQLCursoredStreamReadTest());

        // Add some test from the owenership to test multiple table and primary key
        //suite.addTest(new ReadObjectTest(
        //    manager.getObject(org.eclipse.persistence.testing.models.legacy.Employee.class, "example3")));
        return suite;
    }

    public TestSuite getRefreshObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("RefreshObjectTestSuite");
        suite.setDescription("This Suite contains tests that verify that Refreshing of Objects is working correctly");
        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();
        suite.addTest(new ReadObjectQueryTest((Employee)manager.getObject(employeeClass, "0003")));
        return suite;
    }

    public TestSuite getSelectWithOutputParametersTestSuite() {
        TestSuite test = new TestSuite();
        test.setDescription("This Suite is used to test special Output Parameter functions when using selecting queries");
        test.setName("SelectWithOutputParametersTestSuite");
        test.addTest(new DataReadQueryTest());
        // StoredProcWithOutputParamsAndResultSetTest(useCustomSQL, shouldBindAllParameters)
        test.addTest(new StoredProcWithOutputParamsAndResultSetTest(false, false));
        test.addTest(new StoredProcWithOutputParamsAndResultSetTest(false, true));
        test.addTest(new StoredProcWithOutputParamsAndResultSetTest(true, false));
        test.addTest(new StoredProcWithOutputParamsAndResultSetTest(true, true));
        return test;
    }

    public static TestSuite getStoredProcedureTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLStoredProcedureTestSuite");
        suite.setDescription("This suite tests certains aspects of StoredProcedures");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new CacheHitTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new StoredProcedureTest());
        suite.addTest(new StoredProcedureTest(false));
        suite.addTest(new StoredProcedureTimestampTest());
        suite.addTest(new StoredProcedureTimestampTest(false));

        // StoredProcedureTest_Inout_Out_In procUseCustomSQL(boolean shouldBindAllParameters)
        suite.addTest(StoredProcedureTest_Inout_Out_In.procUseCustomSQL(false));
        suite.addTest(StoredProcedureTest_Inout_Out_In.procUseCustomSQL(true));
        // StoredProcedureTest_Inout_Out_In proc(boolean useArgumentNames, boolean shouldBindAllParameters)
        suite.addTest(StoredProcedureTest_Inout_Out_In.proc(false, false));
        suite.addTest(StoredProcedureTest_Inout_Out_In.proc(false, true));
        suite.addTest(StoredProcedureTest_Inout_Out_In.proc(true, false));
        suite.addTest(StoredProcedureTest_Inout_Out_In.proc(true, true));

        // StoredProcedureTest_Inout_Out_In funcUseCustomSQL(boolean shouldBindAllParameters)
        suite.addTest(StoredProcedureTest_Inout_Out_In.funcUseCustomSQL(false));
        suite.addTest(StoredProcedureTest_Inout_Out_In.funcUseCustomSQL(true));
        // StoredProcedureTest_Inout_Out_In func(boolean useArgumentNames, boolean shouldBindAllParameters)
        suite.addTest(StoredProcedureTest_Inout_Out_In.func(false, false));
        suite.addTest(StoredProcedureTest_Inout_Out_In.func(false, true));
        suite.addTest(StoredProcedureTest_Inout_Out_In.func(true, false));
        suite.addTest(StoredProcedureTest_Inout_Out_In.func(true, true));
        suite.addTest(new StoredProcedureARRAYTest());

        suite.addTest(new StoredProcedureNullInOutTest());
        suite.addTest(new StoredProcedureNullInOutTest(false));
        suite.addTest(new StoredProcedureResultSetAndOutputTest());
        suite.addTest(new StoredProcedureResultSetAndOutputTest(false));

        suite.addTest(new StoredProcedureNoParametersTest());
        suite.addTest(new StoredProcedureObjectRelationalParameters(false));
        suite.addTest(new StoredProcedureObjectRelationalParameters(true));
        suite.addTest(new StoredProcedureVARRAYParametersTest(false));
        suite.addTest(new StoredProcedureVARRAYParametersTest(true));
        suite.addTest(new StoredProcedureORParametersClientSessionTest());
        suite.addTest(buildSQLTransactionTest());
        suite.addTest(build2OutCursorTest());
        suite.addTest(buildUnnamedCursorTest());
        suite.addTest(build2ResultSetTest());
        return suite;
    }

    public static TestSuite getStoredFunctionTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLStoredFunctionTestSuite");
        suite.setDescription("This suite tests certains aspects of StoredFunction");
        suite.addTest(buildStoredFunctionRefCursorTest());
        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomSQLUpdateObjectTestSuite");
        suite.setDescription("This suite tests update sql.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new WriteObjectTest(manager.getObject(SmallProject.class, "0003")));
        suite.addTest(new WriteObjectTest(manager.getObject(LargeProject.class, "0001")));
        suite.addTest(new SetCustomSQLQueryTest("UPDATE EMPLOYEE SET F_NAME = 'Fatima?' WHERE L_NAME = 'Smith'"));
        Vector v = new Vector();
        v.addElement("L_NAME");
        Vector myV = new Vector();
        myV.addElement("Smith");
        suite.addTest(new SetCustomSQLQueryTest("UPDATE EMPLOYEE SET F_NAME = 'Fatima' WHERE L_NAME = #L_NAME", v, myV));

        return suite;
    }

    /**
     * Revert the descriptors back to their old state.
     */
    public void reset() {
        getExecutor().removeConfigureSystem(new EmployeeCustomSQLSystem());

        (new EmployeeSystem()).addDescriptors(getDatabaseSession());
        (new org.eclipse.persistence.testing.models.mapping.MappingSystem()).addDescriptors(getDatabaseSession());
    }

    /**
     * Ensure that the employee model is setup and change the employee descriptor to use custom SQL.
     */
    public void setup() {
        // Setup complex mapping employee as well.
        ClassDescriptor empDescriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.legacy.Employee.class);
        empDescriptor.getQueryManager().setReadObjectSQLString("select LEG_EMP.*, LEG_ADD.* FROM LEG_EMP, LEG_ADD WHERE (((LEG_EMP.FNAME = #LEG_EMP.FNAME) AND (LEG_EMP.LNAME = #LEG_EMP.LNAME)) AND ((LEG_ADD.FIRST_NM = #LEG_EMP.FNAME) AND (LEG_ADD.LNAME = #LEG_EMP.LNAME)))");
    }

    /**
     * Test that transaction with only SQL queries commit.
     */
    public static TestCase buildSQLTransactionTest() {
        TestCase test = new TestCase() {
            public void test() {
                UnitOfWork uow = getSession().acquireUnitOfWork();
                uow.executeNonSelectingSQL("Insert into ADDRESS (ADDRESS_ID) values (999999)");
                uow.commit();
                try {
                    if (getAbstractSession().isInTransaction()
                            || (getSession().executeSQL("Select * from ADDRESS where ADDRESS_ID = 999999").size() == 0)) {
                        throwError("Database transaction not committed.");
                    }
                } finally {
                    uow = getSession().acquireUnitOfWork();
                    uow.executeNonSelectingSQL("Delete from ADDRESS where ADDRESS_ID = 999999");
                    uow.commit();
                }
            }
        };
        test.setName("SQLTransactionTest");
        return test;
    }

    /**
     * Test a procedure with 2 out cursors.
     */
    public static TestCase build2OutCursorTest() {
        TestCase test = new TestCase() {
            public void test() {
                if (!(getSession().getPlatform().isOracle())) {
                    throwWarning("This test can only be run in Oracle");
                }
                DataReadQuery query = new DataReadQuery();
                StoredProcedureCall call = new StoredProcedureCall();
                call.setProcedureName("Read_Emp_Add");
                call.getProcedureArgumentNames().add("CUR1");
                call.appendOutCursor(new DatabaseField("CUR1"));
                call.getProcedureArgumentNames().add("CUR2");
                call.appendOutCursor(new DatabaseField("CUR2"));
                query.setCall(call);
                List<Map> result = (List<Map>)getSession().executeQuery(query);
                if (!(result.get(0).get("CUR1") instanceof List)) {
                    throwError("CUR1 not in output");
                }
                if (!(result.get(0).get("CUR2") instanceof List)) {
                    throwError("CUR2 not in output");
                }
            }
        };
        test.setName("2OutCursorTest");
        return test;
    }

    /**
     * Test a procedure with unnamed cursor.
     */
    public static TestCase buildUnnamedCursorTest() {
        TestCase test = new TestCase() {
            public void test() {
                if (!(getSession().getPlatform().isOracle())) {
                    throwWarning("This test can only be run in Oracle");
                }
                DataReadQuery query = new DataReadQuery();
                StoredProcedureCall call = new StoredProcedureCall();
                call.setProcedureName("Read_All_Employees");
                call.useUnnamedCursorOutputAsResultSet();
                query.setCall(call);
                List<Map> result = (List<Map>)getSession().executeQuery(query);
                result.size();
                result = (List<Map>)getSession().executeQuery(query);
            }
        };
        test.setName("UnnamedCursorTest");
        return test;
    }

    /**
     * Test a procedure with multiple result sets
     */
    public static TestCase build2ResultSetTest() {
        TestCase test = new TestCase() {
            public void test() {
                if (!(getSession().getPlatform().isMySQL())) {
                    throwWarning("This test can only be run in MySQL");
                }
                DataReadQuery query = new DataReadQuery();
                StoredProcedureCall call = new StoredProcedureCall();
                call.setProcedureName("Read_Emp_Add");
                call.setHasMultipleResultSets(true);
                query.setCall(call);
                List<Map> result = (List<Map>)getSession().executeQuery(query);
                if (result.size() != (getSession().readAllObjects(Employee.class).size() + getSession().readAllObjects(Address.class).size())) {
                    throwError("Incorrect number of rows returned : " + result);
                }
                result = (List<Map>)getSession().executeQuery(query);
            }
        };
        test.setName("2ResultSetTest");
        return test;
    }

    /**
     * Test a stored function with ref cursor.
     */
    public static TestCase buildStoredFunctionRefCursorTest() {
        TestCase test = new TestCase() {
            public void test() {
                if (!(getSession().getPlatform().isOracle())) {
                    throwWarning("This test can only be run in Oracle");
                }
                StoredFunctionCall call = new StoredFunctionCall();
                call.setProcedureName("PackageFunction_ResultCursor.BUSINESS_DATE");
                call.addNamedArgument("P_CODE");
                call.addNamedArgument("P_LOOKUP_TBL", "P_LOOKUP_TBL", Types.ARRAY, "SF_LOOKUP_TBL");
                call.setResultCursor();

                DataReadQuery query = new DataReadQuery();
                query.setCall(call);
                query.addArgument("P_CODE");
                query.addArgument("P_LOOKUP_TBL");

                Object[] data = new Object[] {"5-Jul-13", 5L};

                List args = new ArrayList();
                args.add("CN");
                args.add(new Object[] {data});

                List<Map> result = (List<Map>) getSession().executeQuery(query, args);
                if (result == null || result.size() != 1) {
                    throwError("Incorrect number of rows returned: " + result);
                }
            }
        };
        test.setName("StoredFunctionRefCursorTest");
        return test;
    }
}
