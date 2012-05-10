/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;

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
        for (int index = 0; index < values.length; index++) {
            results.add(values[index]);
        }
        ReportQueryResult result = new ReportQueryResult(results, primaryKey);

        expectedResults.addElement(result);
    }

    protected abstract void buildExpectedResults() throws Exception;

    public String getName() {
        return super.getName() + ": " + getDescription();
    }

    protected void removeFromResult(ReportQueryResult result, Vector expected) {
        for (Enumeration e = expected.elements(); e.hasMoreElements();) {
            ReportQueryResult expRes = (ReportQueryResult)e.nextElement();
            if (result.equals(expRes)) {
                expected.removeElement(expRes);
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
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
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

protected  void setup() throws Exception
{
        results = new Vector();
        expectedResults = new Vector();
        buildExpectedResults();
    }

    public void test() {
        results = (Vector)getSession().executeQuery(reportQuery);
    }

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
        if (cloneResults.size() != 0) {
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
