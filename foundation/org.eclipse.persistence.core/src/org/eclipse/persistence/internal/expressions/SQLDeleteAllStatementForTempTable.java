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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.util.Collection;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * @author Andrei Ilitchev
 * @since TOPLink/Java 1.0
 */
public class SQLDeleteAllStatementForTempTable extends SQLModifyAllStatementForTempTable {
    protected DatabaseTable targetTable;
    protected List<DatabaseField> targetPrimaryKeyFields;

    public void setTargetTable(DatabaseTable targetTable) {
        this.targetTable = targetTable;
    }
    public DatabaseTable getTargetTable() {
        return targetTable;
    }
    public void setTargetPrimaryKeyFields(List<DatabaseField> targetPrimaryKeyFields) {
        this.targetPrimaryKeyFields = targetPrimaryKeyFields;
    }
    public List<DatabaseField> getTargetPrimaryKeyFields() {
        return targetPrimaryKeyFields;
    }

    @Override
    protected Collection getUsedFields() {
        return new Vector(getPrimaryKeyFields());
    }

    @Override
    protected void writeUpdateOriginalTable(AbstractSession session, Writer writer) throws IOException {
        session.getPlatform().writeDeleteFromTargetTableUsingTempTableSql(writer, getTable(), getTargetTable(),
                                                        getPrimaryKeyFields(),
                                                        getTargetPrimaryKeyFields(), session.getPlatform());
    }
}
