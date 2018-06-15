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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import java.util.*;

/**
 * Bug 4639551
 * Test to insure using the report queries with ordering based on the database's random function
 * can be configured to return the correct number of results.
 *
 * Prior to this bug fix, the use of random ordering would automatically result in the random
 * number being placed in the select clause.  This would make it so no result of the query
 * was distinct, sometimes returning too many results.
 */
public class OrderByRandomTest extends AutoVerifyTestCase {
    protected Vector results = null;

    public OrderByRandomTest() {
        setDescription("Test order by with Random");
    }

    public void setup() {
        if(!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("This test does not work on the this platform");
        }
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        ExpressionBuilder eb = new ExpressionBuilder();
        ReportQuery rq = new ReportQuery(Employee.class, eb);
        rq.addAttribute("firstName");
        rq.addAttribute("lastName");
        Expression exp = eb.getFunction("dbms_random.value");
        exp.setSelectIfOrderedBy(false);
        rq.addOrdering(exp.ascending());
        rq.setSelectionCriteria(eb.anyOf("projects").get("teamLeader").isNull());
        results = (Vector)getSession().executeQuery(rq);
    }

    public void verify() {
        if (results.size() != 6) {
            throw new TestErrorException("The incorrect number of results was returned from a ReportQuery that included a random function.");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
