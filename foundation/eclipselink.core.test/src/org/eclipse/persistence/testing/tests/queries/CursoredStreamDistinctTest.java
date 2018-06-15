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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class CursoredStreamDistinctTest extends TestCase {
    public int count;

    public CursoredStreamDistinctTest() {
        setDescription("Verify the size works correctly with distinct when single field is queried");
    }

    public void setup() {
        // Access does not like this distinct.
        if (getSession().getPlatform().isAccess()) {
            throw new TestWarningException("Access does not support distinct in counts.");
        }
    }

    public void test() {
        ExpressionBuilder eb = new ExpressionBuilder();
        ReportQuery q = new ReportQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class, eb);
        q.addAttribute("Name", eb.get("lastName"));
        q.useDistinct();
        q.setSelectionCriteria(eb.get("salary").greaterThan(1));
        q.useCursoredStream();
        CursoredStream cs = (CursoredStream)getSession().executeQuery(q);
        try {
            cs.read(1);
            this.count = cs.size();
        } finally {
            cs.close();
        }
    }

    public void verify() {
        ExpressionBuilder eb = new ExpressionBuilder();
        ReportQuery q = new ReportQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class, eb);
        q.addAttribute("Name", eb.get("lastName"));
        q.useDistinct();
        q.setSelectionCriteria(eb.get("salary").greaterThan(1));
        if (this.count != ((Vector)getSession().executeQuery(q)).size()) {
            throw new TestErrorException("Size is incorrect, when using distinct on a single field query");
        }
    }

}
