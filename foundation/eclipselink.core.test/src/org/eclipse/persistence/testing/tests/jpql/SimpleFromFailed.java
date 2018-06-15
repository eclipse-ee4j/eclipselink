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

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.testing.framework.TestErrorException;

class SimpleFromFailed extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    Exception error = null;

    public void setup() {
        String ejbqlString = "SELECT OBJECT(emp) Frow Employee emp";
        setEjbqlString(ejbqlString);

        super.setup();
    }

    public void test() throws Exception {
        error = null;
        try {
            getSession().logMessage("Running EJBQL -> " + getEjbqlString());
            setReturnedObjects(getSession().executeQuery(getQuery()));
        } catch (Exception e) {
            error = e;
        }
    }

    /**
     * verify(): Make sure the error message is the right one
     **/
    public void verify() throws Exception {
        if (error == null) {
            setTestException(new TestErrorException(getName() + " Verify Failed: " + "No error thrown"));
        }
        if (!(error instanceof JPQLException)) {
            setTestException(new TestErrorException("Frow should have thrown exception"));
        }
    }
}
