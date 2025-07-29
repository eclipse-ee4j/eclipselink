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

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug 3947911
 * Ensure that when a non-existant partial attribute is specified in a query, the
 * correct exception is thrown.
 */
@SuppressWarnings("deprecation")
public class IncorrectPartialAttributeTest extends TestCase {
    protected boolean correctException = true;

    public IncorrectPartialAttributeTest() {
        setDescription("Ensure the proper exception is thrown when a query with a partial attribute that doesn't exist is run.");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.addPartialAttribute("nonExistant");
        try {
            getSession().executeQuery(query);
        } catch (QueryException exception) {
            if (exception.getErrorCode() == QueryException.SPECIFIED_PARTIAL_ATTRIBUTE_DOES_NOT_EXIST) {
                correctException = true;
            } else {
                correctException = false;
                throw exception;
            }
        }
    }

    @Override
    public void verify() {
        if (!correctException) {
            throw new TestErrorException("Specifying non-existant partial attributes does not result is the correct exception.");
        }
    }
}
