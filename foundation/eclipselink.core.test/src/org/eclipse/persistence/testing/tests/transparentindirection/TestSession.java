/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.internal.sessions.AbstractRecord;


/**
 * stub Session used for testing (does not actually do anything)
 */
public class TestSession extends org.eclipse.persistence.internal.sessions.AbstractSession {
    // the result to be returned by the executeQuery(DatabaseQuery, DatabaseRecord)
    Object result;

    /**
     * set the object to be returned later
     */
    public TestSession(Object result) {
        this.result = result;
    }

    public Object executeQuery(org.eclipse.persistence.queries.DatabaseQuery query, AbstractRecord row) {
        return result;
    }
}
