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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * ReportQuery test for Scenario 1.7c
 * SELECT EMP_ID, TYPE FROM PHONE
 */
public class Scenario1_7c extends ReportQueryTestCase {
    Hashtable realObjects;

    public Scenario1_7c() {
        setDescription("Retrieve PKs and use result to get real objects");
    }

    @Override
    protected void buildExpectedResults() {
        Vector phoneNumbers = getSession().readAllObjects(PhoneNumber.class);
        realObjects = new Hashtable();

        for (Enumeration e = phoneNumbers.elements(); e.hasMoreElements(); ) {
            PhoneNumber phone = (PhoneNumber)e.nextElement();
            realObjects.put(phone, phone);
            Object[] result = new Object[0];
            addResult(result,
                      getSession().getDescriptor(PhoneNumber.class).getObjectBuilder().extractPrimaryKeyFromObject(phone,
                                                                                                                   getAbstractSession()));
        }
    }

    @Override
    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(PhoneNumber.class);
        reportQuery.retrievePrimaryKeys();
    }

    @Override
    protected void verify() {
        super.verify();

        if (results.size() != realObjects.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size didn't match the Real Objects");
        }

        for (int index = 0; index < results.size(); index++) {
            ReportQueryResult result = (ReportQueryResult)results.get(index);

            PhoneNumber readPhone = (PhoneNumber)result.readObject(PhoneNumber.class, getSession());
            if (!realObjects.contains(readPhone)) {
                throw new TestErrorException("ReportQuery test failed: The read objects was not exactly the same as the real.");
            }
        }
    }
}
