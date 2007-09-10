/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.Vector;
import java.util.Collection;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * @author Andrei Ilitchev
 * @since TOPLink/Java 1.0
 */
public class SQLDeleteAllStatementForTempTable extends SQLModifyAllStatementForTempTable {
    protected DatabaseTable targetTable;
    protected Collection targetPrimaryKeyFields;
    
    public void setTargetTable(DatabaseTable targetTable) {
        this.targetTable = targetTable;
    }
    public DatabaseTable getTargetTable() {
        return targetTable;
    }
    public void setTargetPrimaryKeyFields(Collection targetPrimaryKeyFields) {
        this.targetPrimaryKeyFields = targetPrimaryKeyFields;
    }
    public Collection getTargetPrimaryKeyFields() {
        return targetPrimaryKeyFields;
    }

    protected Collection getUsedFields() {
        return new Vector(getPrimaryKeyFields());
    }
    
    protected void writeUpdateOriginalTable(AbstractSession session, Writer writer) throws IOException {
        session.getPlatform().writeDeleteFromTargetTableUsingTempTableSql(writer, getTable(), getTargetTable(),
                                                        new Vector(getPrimaryKeyFields()), 
                                                        new Vector(getTargetPrimaryKeyFields()));
    }
}
