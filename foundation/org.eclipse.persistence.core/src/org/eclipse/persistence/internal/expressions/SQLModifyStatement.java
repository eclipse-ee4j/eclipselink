/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.expressions;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: Mirror SQL behavior.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Mirror SQL behavior.
 * <li> Print as SQL string.
 * </ul>
 *    @author Dorin Sandu
 *    @since TOPLink/Java 1.0
 */
public abstract class SQLModifyStatement extends SQLStatement {
    protected DatabaseTable table;
    protected AbstractRecord modifyRow;
    protected Vector returnFields;

    public AbstractRecord getModifyRow() {
        return modifyRow;
    }

    public Vector getReturnFields() {
        return returnFields;
    }

    public DatabaseTable getTable() {
        return table;
    }

    public void setModifyRow(AbstractRecord row) {
        modifyRow = row;
    }

    public void setReturnFields(Vector fields) {
        returnFields = fields;
    }

    public void setTable(DatabaseTable table) {
        this.table = table;
    }

    public DatabaseCall buildCall(AbstractSession session) {
        SQLCall sqlCall = buildCallWithoutReturning(session);
        if ((getReturnFields() == null) || getReturnFields().isEmpty()) {
            return sqlCall;
        } else {
            return session.getPlatform().buildCallWithReturning(sqlCall, getReturnFields());
        }
    }

    protected SQLCall buildCallWithoutReturning(AbstractSession session) {
        return null;
    }
}
