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
package org.eclipse.persistence.internal.jpa;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import jakarta.transaction.SystemException;
import jakarta.transaction.RollbackException;

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
