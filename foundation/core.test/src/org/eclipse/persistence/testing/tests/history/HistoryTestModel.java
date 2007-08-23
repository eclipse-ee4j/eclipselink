/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.history;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.flashback.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

public class HistoryTestModel extends FlashbackTestModel {

    int mode;
    public static int BASIC = 0;
    public static int PROJECT_XML = 1;
    public static int PROJECT_CLASS_GENERATED = 2;

    public HistoryTestModel(int mode) {
        setDescription("Tests the new flashback query tests, but using a Historical Schema.");
        this.mode = mode;
        if (mode == BASIC) {
            setName("HistoryTestModel");
        } else if (mode == PROJECT_XML) {
            setName("ProjectXMLHistoryTestModel");
        } else if (mode == PROJECT_CLASS_GENERATED) {
            setName("ProjectClassGeneratedHistoryTestModel.");
        }
    }

    public void addTests() {
        super.addTests();

        addTest(new RollbackObjectsTest(Employee.class, getAsOfClause()));
        addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(EmployeeBasicTestModel.getReadAllTestSuite());
        addTest(EmployeeBasicTestModel.getInsertObjectTestSuite());
        addTest(EmployeeBasicTestModel.getUpdateObjectTestSuite());
        addTest(EmployeeBasicTestModel.getDeleteObjectTestSuite());
        addTest(new IsolatedSessionHistoricalTest(getAsOfClause()));
    }

    private void configure() throws Exception {

        TestSystem system = new HistoricalEmployeeSystem(mode);

        system.run(getSession());
        buildAsOfClause();
        Thread.sleep(1000);
        depopulate();
        return;
    }

    public void buildAsOfClause() {
        //DatabasePlatform platform = (DatabasePlatform)getSession().getPlatform();
        //ValueReadQuery timestampQuery = platform.getTimestampQuery();
        //asOfClause = new AsOfClause(getSession().executeQuery(timestampQuery));
        asOfClause = new AsOfClause(new java.sql.Timestamp(System.currentTimeMillis()));
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

}
