/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;

/**
 * This is a test that verifys that session events are being thrown appropriately
 * for output parameters
 */
public class OutputParameterEventTest extends TestCase {
    public Vector events;
    public Employee employee;

    public OutputParameterEventTest(Employee emp) {
        this.employee = emp;
        this.events = new Vector();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
        }
    }

    public void setup() {
        // right now only the stored procedure is set up in SQLServer
        if (!(getSession().getPlatform().isSQLServer() || getSession().getPlatform().isSybase() || getSession().getPlatform().isSQLAnywhere())) {
            throw new TestWarningException("This test can only be run in SQLServer until EmployeeCustomeSQLSystem is modified");
        }
        getAbstractSession().beginTransaction();
        this.events = new Vector();
    }

    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();
        session.getEventManager().addListener(new StoredProcedureOutputListener(this.events));
        session.writeObject(this.employee);
    }

    /*
     * This verify method presumes that the stored procedure in EmployeeCustomSQLSystem.buildSQLInsertProcedure
     * has not changed and still returns an output parameter VERSION with value 952
     */
    public void verify() {
        if (this.events.isEmpty()) {
            throw new TestErrorException("No session events were thrown and some were expected");
        } else {
            if (((Number)((DatabaseRecord)((SessionEvent)events.firstElement()).getResult()).get("EMPLOYEE.VERSION")).intValue() != 952) {
                throw new TestErrorException("Wrong value returned");
            }

            if (((WriteObjectQuery)((SessionEvent)events.firstElement()).getQuery()).getObject() == null) {
                throw new TestErrorException("Object not set");
            }
        }
    }
}
