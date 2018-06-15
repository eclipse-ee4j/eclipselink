/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
