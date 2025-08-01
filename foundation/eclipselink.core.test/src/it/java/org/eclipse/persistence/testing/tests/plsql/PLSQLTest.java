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
package org.eclipse.persistence.testing.tests.plsql;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.TestCase;

import java.util.List;

/**
 * This model tests calling PLSQL stored procedures with PLSQL types.
 */
public class PLSQLTest extends TestCase {
    protected String queryName;
    protected Class<?> queryClass;
    protected List queryArguments;
    protected Object result;

    public PLSQLTest() {
    }

    public PLSQLTest(String queryName, Class<?> queryClass, List queryArguments) {
        this.queryName = queryName;
        this.queryClass = queryClass;
        this.queryArguments = queryArguments;
    }

    public PLSQLTest(String queryName, Class<?> queryClass, List queryArguments, Object result) {
        this.queryName = queryName;
        this.queryClass = queryClass;
        this.queryArguments = queryArguments;
        this.result = result;
    }

    /**
     * Execute the named query and compare the result with the expected result.
     */
    @Override
    public void test() {
        Object queryResult = null;
        try {
            queryResult = getSession().executeQuery(this.queryName, this.queryClass, this.queryArguments);
        } catch (RuntimeException exception) {
            if (this.result instanceof EclipseLinkException) {
                if (exception.getClass() == this.result.getClass()
                            && (((EclipseLinkException)exception).getErrorCode() == ((EclipseLinkException)this.result).getErrorCode())) {
                    return;
                }
            }
            throw exception;
        }
        if (this.result == null) {
            return;
        }
        if (this.result.getClass() != queryResult.getClass()) {
            if (queryResult instanceof List) {
                queryResult = ((List)queryResult).get(0);
            }
            if (this.result.getClass() != queryResult.getClass()) {
                throwError("Results do not match: " + queryResult + " expected: " + this.result);
            }
        }
        if (this.result instanceof DatabaseRecord record) {
            DatabaseRecord queryRecord = (DatabaseRecord)queryResult;
            for (DatabaseField field : record.getFields()) {
                Object value = record.get(field);
                Object queryValue = queryRecord.get(field);
                if (value instanceof Number) {
                    // Avoid Java number type in-equality.
                    value = value.toString();
                    queryValue = queryValue.toString();
                }
                if (!value.equals(queryValue)) {
                    throwError("Results do not match: " + queryValue + " expected: " + value);
                }
            }
        }
    }
}
