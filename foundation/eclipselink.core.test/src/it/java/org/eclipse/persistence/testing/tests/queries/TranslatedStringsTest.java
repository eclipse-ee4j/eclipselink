/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.*;
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

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    public void test() {
        Employee employee = new Employee();
        employee.setFirstName("Sonny");
        employee.setLastName("Corleone");
        employee.setSalary(1000);
        WriteObjectQuery query = new WriteObjectQuery(employee);

        getSession().getDescriptor(Employee.class).getEventManager().addListener(new DescriptorEventAdapter() {
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

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }
}
