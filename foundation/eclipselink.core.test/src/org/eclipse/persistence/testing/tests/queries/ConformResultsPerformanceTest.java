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

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;

/**
 * Test for bug 2612366: Conform results in UnitOfWork extremely slow.
 * <p><b>Test Description:<b>
 * <ul>
 * <li>Conform results test using a wrapper policy.
 * <li>Insure that the number of times unwrap is called is linear to the
 * number of objects read in.
 * <li>The test input is small to allow this to be run as a short
 * regression test.  The relationship between input size and calls to unwrap is
 * the key.
 * </ul>
 * @author Stephen McRitchie
 */
public class ConformResultsPerformanceTest extends ConformResultsInUnitOfWorkTest {
    public long startTime;

    public ConformResultsPerformanceTest() {
        setShouldUseWrapperPolicy(true);
    }

    public void buildConformQuery() {
        conformedQuery = new ReadAllQuery(Employee.class);
        conformedQuery.conformResultsInUnitOfWork();
        conformedQuery.setSelectionCriteria(new ExpressionBuilder().get("lastName").notEqual("test"));
    }

    public void prepareTest() {
        EmployeeWrapperPolicy.timesUnwrapCalled = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * Insure that the conforming worked and that unwrap was called a liner number
     * of times.
     */
    public void verify() {
        long testTime = System.currentTimeMillis() - startTime;
        int size = ((Vector)result).size();
        if (EmployeeWrapperPolicy.timesUnwrapCalled > (2 * size)) {// Give some leeway
            throw new TestErrorException("Unwrap was called " + EmployeeWrapperPolicy.timesUnwrapCalled + " times on a result of size " + size);
        }
    }
}
