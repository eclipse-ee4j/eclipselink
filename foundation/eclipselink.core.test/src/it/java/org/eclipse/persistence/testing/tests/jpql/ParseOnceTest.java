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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Vector;


/**
 * ParseOnceTest: Verify that a named query only parses
 * the EJBQLString once.
 */
public class ParseOnceTest extends JPQLTestCase {
    @Override
    public void setup() {
        Vector employeesUsed = getSomeEmployees();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp";
        setEjbqlString(ejbqlString);
        setOriginalOject(employeesUsed);
        super.setup();
        getSession().getProject().getJPQLParseCache().getCache().clear();
    }

    /**
     * Make sure the query's call is not prepared before the test, and is prepared after.
     */
    @Override
    public void test() throws Exception {
        if (((JPQLCallQueryMechanism)getQuery().getQueryMechanism()).getJPQLCall().isParsed()) {
            throw new TestErrorException("JPQLCall should not be parsed before the test");
        }
        super.test();
        if (getQuery().getQueryMechanism().isJPQLCallQueryMechanism() && (!((JPQLCallQueryMechanism)getQuery().getQueryMechanism()).getJPQLCall().isParsed())) {
            throw new TestErrorException("JPQLCall should be parsed after the test");
        }
    }
}
