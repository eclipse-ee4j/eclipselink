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

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.testing.framework.*;

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

    public void test() {
        String user = getSession().getLogin().getUserName();

        org.eclipse.persistence.sessions.Session hs = getSession().acquireHistoricalSession(pastTime);
        Vector objects = (Vector)hs.readAllObjects(Employee.class);

        for (Enumeration enumtr = objects.elements(); enumtr.hasMoreElements();) {
            Employee emp = (Employee)enumtr.nextElement();
            Vector projs = emp.getProjects();

            Vector respons = emp.getResponsibilitiesList();
        }
    }

    public void verify() {
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}