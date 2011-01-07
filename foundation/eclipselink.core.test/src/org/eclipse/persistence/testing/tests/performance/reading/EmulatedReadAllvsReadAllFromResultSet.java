/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read all vs reading directly from a result-set.
 */
public class EmulatedReadAllvsReadAllFromResultSet extends ReadAllvsReadAllFromResultSet {
    
    public void setup() {
        Session session = buildEmulatedSession();

        /*EmulatedConnection connection = (EmulatedConnection)((org.eclipse.persistence.internal.sessions.AbstractSession)session).getAccessor().getConnection();

        ReadAllQuery query = new ReadAllQuery(Address.class);
        getSession().executeQuery(query);
        String sql = query.getSQLString();
        Vector rows = getSession().executeSQL(sql);
        connection.putRows(sql, rows);*/

        getExecutor().swapSession(session);
        super.setup();
    }

    public void reset() throws Exception {
        getExecutor().resetSession();
        super.reset();
    }

    public PerformanceComparisonTestCase readAllFromResultSet() {
        PerformanceComparisonTestCase test = super.readAllFromResultSet();
        test.setAllowableDecrease(20);
        return test;
    }
}
