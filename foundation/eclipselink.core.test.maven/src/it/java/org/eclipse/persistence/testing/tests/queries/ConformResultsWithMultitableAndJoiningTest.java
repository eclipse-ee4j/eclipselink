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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * For bug 6450867
 * When Multitable mapping was queried against a superclass for a subclass result, using
 * a ReadObjectQuery and conforming, TopLink was throwing a TOPLINK-6069 error because
 * an incorrect descriptor (the superclass' descriptor) was set in the normalization process.
 */

public class ConformResultsWithMultitableAndJoiningTest extends ConformResultsInUnitOfWorkTest {

    protected String projectName = "Enterprise System";

    public ConformResultsWithMultitableAndJoiningTest() {
        super();
    }

    public void buildConformQuery() {
        conformedQuery = new ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        ExpressionBuilder builder = conformedQuery.getExpressionBuilder();
        // simple query for the LargeProject subclass
        Expression exp = builder.get("name").equal(projectName);
        conformedQuery.setSelectionCriteria(exp);
        conformedQuery.conformResultsInUnitOfWork();
    }

    public void prepareTest() {
        // no-op
    }

    public void verify() {
        if (result == null) {
            throw new TestErrorException("Result cannot be null");
        } else if (!(result instanceof org.eclipse.persistence.testing.models.employee.domain.LargeProject)) {
            throw new TestErrorException("Result is of the wrong class: " + result.getClass().getName());
        } else if (!((org.eclipse.persistence.testing.models.employee.domain.Project)result).getName().equals(projectName)) {
            throw new TestErrorException("Result is not expected object: " + result);
        }
    }

}

