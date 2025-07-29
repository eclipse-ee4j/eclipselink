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
package org.eclipse.persistence.testing.tests.history;

import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Vector;

/**
 * <b>Purpose:</b>Tests rolling back objects to past state using history.
 * <p>
 * This test also acts as a kind of reset method, as tables can continually
 * be rolled back to a starting state.
 */
public class QualifiedTableTest extends AutoVerifyTestCase {
    AsOfClause pastTime;
    Vector oldHistoricalTables;

    public QualifiedTableTest(AsOfClause pastTime) {
        super();
        this.pastTime = pastTime;
    }

    @Override
    public void test() {
        String user = getSession().getLogin().getUserName();

        org.eclipse.persistence.sessions.Session hs = getSession().acquireHistoricalSession(pastTime);
        Vector objects = hs.readAllObjects(Employee.class);

        for (Enumeration enumtr = objects.elements(); enumtr.hasMoreElements();) {
            Employee emp = (Employee)enumtr.nextElement();
            Vector projs = emp.getProjects();

            Vector respons = emp.getResponsibilitiesList();
        }
    }

    @Override
    public void verify() {
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
