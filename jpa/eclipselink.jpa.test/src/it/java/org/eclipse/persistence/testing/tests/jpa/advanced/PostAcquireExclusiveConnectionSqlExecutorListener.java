/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class PostAcquireExclusiveConnectionSqlExecutorListener extends
SessionEventAdapter{
    String sqlToExecute = "SELECT * FROM CMP3_VEGETABLE WHERE VEGETABLE_NAME='nonexistent'";

    public void setSqlToExecute(String sqlString) {
        this.sqlToExecute = sqlString;
    }

    public String getSqlToExecute() {
        return this.sqlToExecute;
    }

    public void postAcquireExclusiveConnection(SessionEvent event) {
        event.getSession().executeSelectingCall(new SQLCall(sqlToExecute));
    }
}
