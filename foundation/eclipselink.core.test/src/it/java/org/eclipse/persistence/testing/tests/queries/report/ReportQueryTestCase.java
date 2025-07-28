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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public abstract class ReportQueryTestCase extends AutoVerifyTestCase {
    protected ReportQuery reportQuery;
    protected Vector expectedResults;
    protected Vector results;
    protected boolean checkEntrySet = true;

    protected void addResult(Object[] values, Object primaryKey) {
        Vector results = new Vector(values.length);
        results.addAll(Arrays.asList(values));
        ReportQueryResult result = new ReportQueryResult(results, primaryKey);

        expectedResults.add(result);
    }

    protected abstract void buildExpectedResults() throws Exception;

    @Override
    public String getName() {
        return super.getName() + ": " + getDescription();
    }

    protected void removeFromResult(ReportQueryResult result, Vector expected) {
        for (Enumeration e = expected.elements(); e.hasMoreElements();) {
            ReportQueryResult expRes = (ReportQueryResult)e.nextElement();
            if (result.equals(expRes)) {
                expected.remove(expRes);
                return;
            }
        }
        getSession().logMessage("missing element: " + result);
    }

    protected boolean isEntrySetOk(ReportQueryResult result) {
        try {
            Set set = result.entrySet();
            if (result.size() != set.size()) {
                return false;
            }
            List names = new ArrayList(result.getNames());
            List results = new ArrayList(result.getResults());
            for (Object o : set) {
                Map.Entry entry = (Map.Entry) o;
                Object key = entry.getKey();
                Object value = entry.getValue();
                int index = names.indexOf(key);
                if (index == -1) {
                    return false;
                }
                if (results.get(index) != value) {
                    return false;
                }
                names.remove(index);
                results.remove(index);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

@Override
protected  void setup() throws Exception
{
        results = new Vector();
        expectedResults = new Vector();
        buildExpectedResults();
    }

    @Override
    public void test() {
        results = (Vector)getSession().executeQuery(reportQuery);
    }

    @Override
    protected void verify() {
        getSession().logMessage("results: " + results);
        getSession().logMessage("expectedResults: " + expectedResults);
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }

        Vector cloneResults = (Vector)expectedResults.clone();
        for (Enumeration e = results.elements(); e.hasMoreElements();) {
            removeFromResult((ReportQueryResult)e.nextElement(), cloneResults);
        }
        if (!cloneResults.isEmpty()) {
            throw new TestErrorException("ReportQuery test failed: The result didn't match");
        }

        if (checkEntrySet) {
            for (Enumeration e = results.elements(); e.hasMoreElements();) {
                if (!isEntrySetOk((ReportQueryResult)e.nextElement())) {
                    throw new TestErrorException("Problem with ReportQueryResult.entrySet() method");
                }
            }
        }
    }
}
