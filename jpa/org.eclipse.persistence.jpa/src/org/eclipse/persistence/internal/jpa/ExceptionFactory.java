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
package org.eclipse.persistence.internal.jpa;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.transaction.SystemException;
import javax.transaction.RollbackException;

public class ExceptionFactory {
    public ExceptionFactory() {
    }

    protected String stackTraceString(Exception ex) {
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter, true);
        ex.printStackTrace(writer);
        writer.close();
        return swriter.toString();
    }

    public SystemException newSystemException(String str) {
        return new SystemException(str);
    }

    public SystemException newSystemException(Exception ex) {
        return new SystemException("Real nested exception: " + stackTraceString(ex));
    }

    public RollbackException rollbackException(SQLException sqlEx) {
        return new RollbackException(sqlEx.toString());
    }

    public SystemException txMarkedForRollbackException() {
        return newSystemException("Transaction marked for rollback");
    }

    public SystemException txActiveException() {
        return newSystemException("Transaction is already active");
    }

    public SystemException txNotActiveException() {
        return newSystemException("No transaction is active");
    }

    public SystemException invalidStateException(int state) {
        return newSystemException("Cannot complete operation, invalid state: " + state);
    }
}
