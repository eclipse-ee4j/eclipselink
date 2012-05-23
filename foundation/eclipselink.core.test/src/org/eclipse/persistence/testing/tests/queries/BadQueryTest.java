/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
