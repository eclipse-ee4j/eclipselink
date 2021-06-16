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
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;

/**
 * Test query error handling.
 */
public class BadQueryTest extends TestCase {
    public BadQueryTest() {
        setDescription("This test query error handling.");
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        try {
            getSession().executeQuery(query);
        } catch (EclipseLinkException exception) {
            if (exception.getErrorCode() != QueryException.REFERENCE_CLASS_MISSING) {
                throw exception;
            }
        }

        WriteObjectQuery query2 = new WriteObjectQuery();
        try {
            getSession().executeQuery(query2);
        } catch (EclipseLinkException exception) {
            if (exception.getErrorCode() != QueryException.OBJECT_TO_MODIFY_NOT_SPECIFIED) {
                throw exception;
            }
        }
    }
}
