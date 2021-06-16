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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class QueryAcrossV11Test extends org.eclipse.persistence.testing.framework.TestCase {
    public Exception storedException;
    public int expectedExceptionCode;

    public QueryAcrossV11Test() {
        super();
        setDescription("Test that the correct exception is thrown when a query across a one to one mapping is performed");
        storedException = null;
        expectedExceptionCode = org.eclipse.persistence.exceptions.QueryException.CANNOT_QUERY_ACROSS_VARIABLE_ONE_TO_ONE_MAPPING;
    }

    public void test() {
        try {
            getSession().readObject(Employee.class, new ExpressionBuilder().get("contact").get("id").equal(12));
        } catch (Exception e) {
            storedException = e;
        }
    }

    public void verify() {
        if (storedException == null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("NO EXCEPTION THROWN!!!  EXPECTING QueryException");
        }
        if (EclipseLinkException.class.isInstance(storedException)) {
            if (((EclipseLinkException)storedException).getErrorCode() == expectedExceptionCode) {
                return;
            }
        }
        throw new org.eclipse.persistence.testing.framework.TestErrorException("WRONG EXCEPTION THROWN!!!  EXPECTING QueryException");

    }
}
