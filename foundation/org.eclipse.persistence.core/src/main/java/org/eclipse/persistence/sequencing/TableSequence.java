/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.sequencing;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.IndexDefinition;

import java.io.StringWriter;
import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Defines sequencing through using a SEQUENCE table.
 * <p>
 * <b>Description</b>
 * This is the default sequencing mechanism.
 * A table defaulting to SEQUENCE is used to generate unique ids.
 * The table has a name field (SEQ_NAME) storing each sequences name,
 * and a counter (SEQ_COUNT) storing the last sequence id generated.
 * There will be a row in the table for each sequence object.
 */
public class TableSequence extends QuerySequence {
    /** Default sequence table name
     * @deprecated
     * Use an empty string as a default sequence table name instead,
     * that triggers usage of platform.getDefaultSequenceTableName() when the sequence is connected.
     */
    @Deprecated
    public static final String defaultTableName = "SEQUENCE";

    /** Hold the database table */
    protected DatabaseTable table;

    /** Hold the name of the column in the sequence table which specifies the sequence numeric value */
    protected String counterFieldName = "SEQ_COUNT";

    /** Hold the name of the column in the sequence table which specifies the sequence name */
    protected String nameFieldName = "SEQ_NAME";

    public TableSequence() {
        super(false, true);
        setTableName("");
    }

    /**
     * Create a new sequence with the name.
     */
    public TableSequence(String name) {
        super(name, false, true);
        setTableName("");
    }

    /**
     * Create a new sequence with the name and sequence pre-allocation size.
     */
    public TableSequence(String name, int size) {
        super(name, size, false, true);
        setTableName("");
    }

    public TableSequence(String name, int size, int initialValue) {
        super(name, size, initialValue, false, true);
        setTableName("");
    }

    /**
     * Create a new sequence with the name, and the sequence table name.
     */
    public TableSequence(String name, String tableName) {
        this(name);
        setTableName(tableName);
    }

    /**
     * Create a new sequence with the name, and the sequence table information.
     */
    public TableSequence(String name, String tableName, String nameFieldName, String counterFieldName) {
        this(name);
        setTableName(tableName);
        setNameFieldName(nameFieldName);
        setCounterFieldName(counterFieldName);
    }

    public TableSequence(String name, int size, String tableName) {
        this(name, size);
        setTableName(tableName);
    }

    public TableSequence(String name, int size, String tableName, String nameFieldName, String counterFieldName) {
        this(name, size);
        setTableName(tableName);
        setNameFieldName(nameFieldName);
        setCounterFieldName(counterFieldName);
    }

    @Override
    public boolean isTable() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TableSequence other) {
            if (equalNameAndSize(this, other)) {
                return getTableName().equals(other.getTableName()) && getCounterFieldName().equals(other.getCounterFieldName()) && getNameFieldName().equals(other.getNameFieldName());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        String tableName = getTableName();
        String counterFieldName = getCounterFieldName();
        String nameFieldName = getNameFieldName();        
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 31 * result + (counterFieldName != null ? counterFieldName.hashCode() : 0);
        result = 31 * result + (nameFieldName != null ? nameFieldName.hashCode() : 0);
        return result;
    }

    public String getCounterFieldName() {
        return counterFieldName;
    }

    public void setCounterFieldName(String name) {
        counterFieldName = name;
    }

    public String getNameFieldName() {
        return nameFieldName;
    }

    public void setNameFieldName(String name) {
        nameFieldName = name;
    }

    public DatabaseTable getTable() {
        return table;
    }

    public List<IndexDefinition> getTableIndexes() {
        return getTable().getIndexes();
    }

    public String getTableName() {
        return getTable().getName();
    }

    public String getQualifiedTableName() {
        return getQualified(getTable().getName());
    }

    public void setTable(DatabaseTable table) {
        this.table = table;
    }

    public void setTableName(String name) {
        table = new DatabaseTable(name);
    }

    @Override
    public void onConnect() {
        if(this.table.getName().isEmpty()) {
            this.table.setName(((DatabasePlatform)getDatasourcePlatform()).getDefaultSequenceTableName());
        }
        if ((this.qualifier == null || this.qualifier.isEmpty()) &&
                (this.table.getTableQualifier() != null && !this.table.getTableQualifier().isEmpty())) {
            this.qualifier = this.table.getTableQualifier();
        }
        super.onConnect();
    }

    @Override
    protected ValueReadQuery buildSelectQuery() {
        ValueReadQuery query = new ValueReadQuery();
        query.addArgument(getNameFieldName());
        StringWriter writer = new StringWriter();
        writer.write("SELECT " + getCounterFieldName());
        writer.write(" FROM " + getQualifiedTableName());
        writer.write(" WHERE " + getNameFieldName());
        writer.write(" = #" + getNameFieldName());
        query.setSQLString(writer.toString());

        return query;
    }

    @Override
    protected DataModifyQuery buildUpdateQuery() {
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument(getNameFieldName());
        query.addArgument("PREALLOC_SIZE");
        StringWriter writer = new StringWriter();
        writer.write("UPDATE " + getQualifiedTableName());
        writer.write(" SET " + getCounterFieldName());
        writer.write(" = " + getCounterFieldName());
        writer.write(" + #PREALLOC_SIZE");
        writer.write(" WHERE " + getNameFieldName() + " = #" + getNameFieldName());
        query.setSQLString(writer.toString());

        return query;
    }
}
