/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/14/2011-2.3.1 Guy Pelletier
//       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.tools.schemaframework;

import java.util.List;
import java.util.Vector;
import java.io.*;
import java.math.BigDecimal;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.TableSequence;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * </p>
 */
public class TableSequenceDefinition extends SequenceDefinition {
    protected TableDefinition tableDefinition;
    protected boolean deleteSchema;

    /**
     * INTERNAL:
     * Should be a sequence defining table sequence in the db:
     * either TableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is a TableSequence).
     */
    public TableSequenceDefinition(Sequence sequence, boolean deleteSchema) {
        super(sequence);

        this.deleteSchema = deleteSchema;
    }

    /**
     * INTERNAL:
     * Return the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("INSERT INTO ");
            writer.write(getSequenceTableQualifiedName());
            writer.write("(" + getSequenceNameFieldName());
            writer.write(", " + getSequenceCounterFieldName());
            writer.write(") values (");
            writer.write("'" + getName() + "', "  + Integer.toString(sequence.getInitialValue() - 1) + ")");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the SQL to delete the row from the sequence table. If we're
     * dealing with create creation, then delegate to the table so that is
     * dropped outright since we will delete the schema.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        if (shouldDropTableDefinition()) {
            return tableDefinition.buildDeletionWriter(session, writer);
        } else {
            try {
                writer.write("DELETE FROM ");
                writer.write(getSequenceTableQualifiedName());
                writer.write(" WHERE " + getSequenceNameFieldName());
                writer.write(" = '" + getName() + "'");
            } catch (IOException ioException) {
                throw ValidationException.fileError(ioException);
            }
            return writer;
        }
    }

    /**
     * INTERAL:
     * Execute the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT * FROM ");
        buffer.append(getSequenceTableQualifiedName());
        buffer.append(" WHERE ");
        buffer.append(getSequenceNameFieldName());
        buffer.append(" = '");
        buffer.append(getName());
        buffer.append("'");
        Vector results = session.priviledgedExecuteSelectingCall(new org.eclipse.persistence.queries.SQLCall(buffer.toString()));
        return !results.isEmpty();
    }

    /**
     * INTERNAL:
     * Execute the DDL to drop the database schema for this object.
     * Does nothing at this level, subclasses that support this must override
     * this method.
     *
     * @see TableDefinition
     */
    @Override
    public void dropDatabaseSchema(AbstractSession session, Writer writer) throws EclipseLinkException {
        tableDefinition.dropDatabaseSchema(session, writer);
    }

    /**
     * INTERNAL:
     * Execute the DDL to drop the database schema for this object.
     * Does nothing at this level, subclasses that support this must override
     * this method.
     *
     * @see TableDefinition
     */
    @Override
    public void dropDatabaseSchemaOnDatabase(AbstractSession session) throws EclipseLinkException {
        tableDefinition.dropDatabaseSchemaOnDatabase(session);
    }

    /**
     * PUBLIC:
     * Return the schema associated with this table sequence.
     */
    @Override
    public String getDatabaseSchema() {
        return getSequenceTable().getTableQualifier();
    }

    /**
     * PUBLIC:
     */
    public String getSequenceCounterFieldName() {
        return getTableSequence().getCounterFieldName();
    }

    /**
     * PUBLIC:
     */
    public String getSequenceNameFieldName() {
        return getTableSequence().getNameFieldName();
    }

    /**
     * Return the database table for the sequence.
     */
    public DatabaseTable getSequenceTable() {
        return getTableSequence().getTable();
    }

    /**
     * PUBLIC:
     */
    public List<IndexDefinition> getSequenceTableIndexes() {
        return getTableSequence().getTableIndexes();
    }

    /**
     * PUBLIC:
     */
    public String getSequenceTableName() {
        return getSequenceTable().getName();
    }

    /**
     * PUBLIC:
     */
    public String getSequenceTableQualifier() {
        return getSequenceTable().getTableQualifier();
    }

    /**
     * PUBLIC:
     */
    public String getSequenceTableQualifiedName() {
        return getSequenceTable().getQualifiedName();
    }

    /**
     * INTERNAL:
     * Return a TableDefinition specifying sequence table.
     * Cache the table definition for re-use (during CREATE and DROP)
     */
    public TableDefinition buildTableDefinition() {
        if (tableDefinition == null) {
            tableDefinition = new TableDefinition();
            tableDefinition.setTable(getSequenceTable());
            tableDefinition.setName(getSequenceTableName());
            tableDefinition.setQualifier(getSequenceTableQualifier());
            tableDefinition.addPrimaryKeyField(getSequenceNameFieldName(), String.class, 50);
            tableDefinition.addField(getSequenceCounterFieldName(), BigDecimal.class);
            tableDefinition.setIndexes(getSequenceTableIndexes());
        }

        return tableDefinition;
    }

    protected TableSequence getTableSequence() {
        if(sequence instanceof TableSequence) {
            return (TableSequence)sequence;
        } else {
            return (TableSequence)((DefaultSequence)sequence).getDefaultSequence();
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isTableSequenceDefinition() {
        return true;
    }

    /**
     * Execute any statements required before the deletion of the object
     * @param session
     * @param dropSchemaWriter
     */
    public void preDropObject(AbstractSession session, Writer dropSchemaWriter, boolean createSQLFiles) {
        if ((session.getPlatform().shouldCreateIndicesForPrimaryKeys()) || (session.getPlatform().shouldCreateIndicesOnUniqueKeys())) {
            // Do not drop index when database is Symfoware. Index is required for primary keys or unique keys.
            return;
        }
        buildTableDefinition().preDropObject(session, dropSchemaWriter, createSQLFiles);
    }

    /**
     * INTERNAL:
     * Returns true if the table definition should be dropped during buildDeletionWriter.
     */
    protected boolean shouldDropTableDefinition() {
        return (deleteSchema && hasDatabaseSchema());
    }
}
