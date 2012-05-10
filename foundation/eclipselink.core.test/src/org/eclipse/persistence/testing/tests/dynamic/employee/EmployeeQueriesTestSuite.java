/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.dynamic.employee;

//javase imports
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.logLevel;
import org.eclipse.persistence.testing.tests.dynamic.DynamicEmployeeEntityComparator;

public class EmployeeQueriesTestSuite {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    static DynamicEmployeeSystem deSystem = null;

    @BeforeClass
    public static void setUp() {
        session = createSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel); 
        }
        dynamicHelper = new DynamicHelper(session);
        deSystem = DynamicEmployeeSystem.buildProject(dynamicHelper);
        deSystem.populate(dynamicHelper);
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE D_SALARY");
        session.executeNonSelectingSQL("DROP TABLE D_PROJ_EMP");
        session.executeNonSelectingSQL("DROP TABLE D_PROJECT");
        session.executeNonSelectingSQL("DROP TABLE D_PHONE");
        session.executeNonSelectingSQL("DROP TABLE D_EMPLOYEE");
        session.executeNonSelectingSQL("DROP TABLE D_ADDRESS");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    /**
     * Retrieve all Employees, sorted by id
     */
    @SuppressWarnings("unchecked")
    @Test
    public void findAllEmployees() {
        ReadAllQuery query = dynamicHelper.newReadAllQuery("Employee");
        query.addAscendingOrdering("id");
        List<DynamicEntity> emps = (List<DynamicEntity>)session.executeQuery(query);
        assertSame(deSystem.employees(), emps);
    }

    /**
     * Retrieve all Employees and their Addresses, sorted by firstName, lastName
     */
    @SuppressWarnings("unchecked")
    @Test
    public void findAllEmployeesWithAddressJoin() {
        ReadAllQuery query = dynamicHelper.newReadAllQuery("Employee");
        query.addJoinedAttribute("address");
        query.addAscendingOrdering("lastName");
        query.addAscendingOrdering("firstName");
        List<DynamicEntity> emps = (List<DynamicEntity>)session.executeQuery(query);
        assertNotNull(emps);
    }

    /**
     * Retrieve Employee with lowest id
     */
    @Test
    public void findLowestEmployeeById() {
        ReportQuery query = dynamicHelper.newReportQuery("Employee", new ExpressionBuilder());
        query.addMinimum("id");
        query.setShouldReturnSingleValue(true);
        int minId = ((Number)session.executeQuery(query)).intValue();
        assertTrue(minId > 0);
    }

    /**
     * Retrieve all male Employees
     */
    @SuppressWarnings("unchecked")
    @Test
    public void findAllMaleEmployees() throws Exception {
        ReadAllQuery query = dynamicHelper.newReadAllQuery("Employee");
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.get("gender").equal("Male"));
        List<DynamicEntity> emps =  (List<DynamicEntity>)session.executeQuery(query);
        assertNotNull(emps);
    }

    public void assertSame(DynamicEntity[] employees, List<DynamicEntity> dbEmps) {
        assertEquals("Incorrect quantity of employees", employees.length, dbEmps.size());
        DynamicEmployeeEntityComparator dEmpComparator = new DynamicEmployeeEntityComparator();
        Collections.sort(dbEmps, dEmpComparator);
        List<DynamicEntity> sampleEmps = Arrays.asList(employees);
        Collections.sort(sampleEmps, dEmpComparator);
        for (int index = 0; index < employees.length; index++) {
            DynamicEntity emp = sampleEmps.get(index);
            DynamicEntity dbEmp = dbEmps.get(index);
            assertEquals("First name does not match on employees[" + index + "]", 
                emp.<String> get("firstName"), dbEmp.<String> get("firstName"));
            assertEquals("Last name does not match on employees[" + index + "]", 
                emp.<String> get("lastName"), dbEmp.<String> get("lastName"));
            assertEquals("Salary does not match on employees[" + index + "]", 
                emp.<Integer> get("salary"), dbEmp.<Integer> get("salary"));
        }
    }
}