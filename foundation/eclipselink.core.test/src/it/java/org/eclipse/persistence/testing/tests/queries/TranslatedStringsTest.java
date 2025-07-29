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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.List;

/**
 * Bug 4318924
 * Ensure it is possible to get the translated strings from a multiple call query.
 */
public class TranslatedStringsTest extends AutoVerifyTestCase {
    protected List translatedSQLStrings = null;
    protected List sqlStrings = null;
    protected List calls = null;
    protected String translatedSQLString = null;
    protected String sqlString = null;
    protected Call call;

    public TranslatedStringsTest() {
        setDescription("Ensure it is possible to get the translated strings from a multiple call query.");
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    @Override
    public void test() {
        Employee employee = new Employee();
        employee.setFirstName("Sonny");
        employee.setLastName("Corleone");
        employee.setSalary(1000);
        WriteObjectQuery query = new WriteObjectQuery(employee);

        getSession().getDescriptor(Employee.class).getEventManager().addListener(new DescriptorEventAdapter() {
                @Override
                public void postInsert(org.eclipse.persistence.descriptors.DescriptorEvent event) {
                    translatedSQLStrings = event.getQuery().getTranslatedSQLStrings(event.getSession(), event.getQuery().getTranslationRow());
                    sqlStrings = event.getQuery().getSQLStrings();
                    calls = event.getQuery().getDatasourceCalls();
                    translatedSQLString = event.getQuery().getTranslatedSQLString(event.getSession(), event.getQuery().getTranslationRow());
                    sqlString = event.getQuery().getSQLString();
                    call = event.getQuery().getCall();
                }
            });
        getSession().executeQuery(query);
    }

    @Override
    public void verify() {
        if (translatedSQLStrings.size() != 2) {
            throw new TestErrorException("Translated SQL Strings were not properly returned.");
        }
        if (sqlStrings.size() != 2) {
            throw new TestErrorException("SQL Strings were not properly returned.");
        }
        if (calls.size() != 2) {
            throw new TestErrorException("Calls were not properly returned.");
        }

        if (translatedSQLString != null) {
            throw new TestErrorException("Translated SQL String was returned even though it should be null..");
        }
        if (sqlString == null) {
            throw new TestErrorException("SQL String was not returned even though it should have been.");
        }
        if (call == null) {
            throw new TestErrorException("A Call was not returned even though it should have been..");
        }
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }
}
