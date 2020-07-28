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
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
package org.eclipse.persistence.testing.tests.dynamic.projectxml;

//javase imports
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createLogin;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.logLevel;

/*
 * Test cases verifying the use of employee-project.xml
 */
public class EmployeeProjectTestSuite  {

    public static final String PACKAGE_PREFIX =
        EmployeeProjectTestSuite.class.getPackage().getName();
    static final String PROJECT_XML =
        PACKAGE_PREFIX.replace('.', '/') + "/employee-project.xml";

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;

    @BeforeClass
    public static void setUp() {
        DynamicClassLoader dcl = new DynamicClassLoader(EmployeeProjectTestSuite.class.getClassLoader());
        Project project = null;
        try {
            project = DynamicTypeBuilder.loadDynamicProject(PROJECT_XML, createLogin(), dcl);
        }
        catch (IOException e) {
            //e.printStackTrace();
            fail("cannot find employee-project.xml");
        }
        session = project.createDatabaseSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel);
        }
        dynamicHelper = new DynamicHelper(session);
        session.login();
        new SchemaManager(session).replaceDefaultTables();
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE DX_SALARY");
        session.executeNonSelectingSQL("DROP TABLE DX_EMPLOYEE");
        session.executeNonSelectingSQL("DROP TABLE DX_ADDRESS");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearDatabase() {
        session.executeNonSelectingSQL("DELETE FROM DX_SALARY");
        session.executeNonSelectingSQL("DELETE FROM DX_EMPLOYEE");
        session.executeNonSelectingSQL("DELETE FROM DX_ADDRESS");
    }

    @Test
    public void createNewInstance() throws Exception {
        DynamicType employeeType = dynamicHelper.getType("Employee");
        DynamicType periodType = dynamicHelper.getType("EmploymentPeriod");

        DynamicEntity entity = employeeType.newDynamicEntity();
        // entity.set("id", 1);
        entity.set("firstName", "First");
        entity.set("lastName", "Last");
        entity.set("salary", 12345);

        DynamicEntity period = periodType.newDynamicEntity();
        period.set("startDate", Calendar.getInstance());

        entity.set("period", period);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(entity);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("Employee", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(1, ((Number) session.executeQuery(countQuery)).intValue());

        session.release();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void readAll() throws Exception {
        createNewInstance();
        DynamicType type = dynamicHelper.getType("Employee");

        List<DynamicEntity> allObjects = session.readAllObjects(type.getJavaClass());
        assertEquals(1, allObjects.size());
    }

}
