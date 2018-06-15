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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * ParameterNameMismatchTest: Verify that if the query
 * string has a parameter which doesn't match one of
 * the arguments in the query, an exception will be
 * thrown.
 */
public class ParameterNameMismatchTest extends JPQLTestCase {
    public void setup() {
        // ?2 is used intentionally, with only "1" added to the query.
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp where emp.firstName = ?2";
        setEjbqlString(ejbqlString);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");

        super.setup();

        getQuery().setArguments(myArgumentNames);
    }

    /**
     * Run the test, and make sure the proper error is thrown.
     */
    public void test() throws Exception {
        try {
            super.test();
        } catch (Exception queryException) {
            int indexOfMessage = String.valueOf(queryException.getMessage()).indexOf("The parameter name [2] in the query's selection criteria does not match any parameter name defined in the query.");
            if (indexOfMessage == -1) {
                throw queryException;
            } else {
                return;
            }
        }

        // Throw exception if no exception is thrown in super.test().
        throw new TestErrorException("Invalid parameter error should have been thrown.");
    }

    /**
     * Verify: we aren't expecting any results, so do nothing.
     */
    public void verify() throws Exception {
    }
}
