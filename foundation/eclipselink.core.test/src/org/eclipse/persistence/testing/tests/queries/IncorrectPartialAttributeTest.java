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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;

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

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

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

    public void verify() {
        if (!correctException) {
            throw new TestErrorException("Specifying non-existant partial attributes does not result is the correct exception.");
        }
    }
}
