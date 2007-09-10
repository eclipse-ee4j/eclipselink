/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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