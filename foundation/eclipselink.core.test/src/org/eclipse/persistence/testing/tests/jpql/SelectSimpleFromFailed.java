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

import org.eclipse.persistence.exceptions.*;

class SelectSimpleFromFailed extends JPQLExceptionTest {
    String errorMessage = null;

    public void setup() {
        this.expectedException = JPQLException.syntaxErrorAt(null, 0, 0, null, null);

        String ejbqlString = "SELECT OBJECT(emp) Frow AccountBean account";
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
