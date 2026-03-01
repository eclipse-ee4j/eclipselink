/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.Sequence;
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
    private String sequenceCounterFieldName;
    private String sequenceNameFieldName;
    private List<IndexDefinition> sequenceTableIndexes;
    private String sequenceTableName;
    private String sequenceTableQualifier;

    /**
     * INTERNAL:
     * Should be a sequence defining table sequence in the db:
     * either TableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is a TableSequence).
     * @deprecated Use {@linkplain #TableSequenceDefinition(String, boolean)} instead.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    @SuppressWarnings({"removal"})
    public TableSequenceDefinition(Sequence sequence, boolean deleteSchema) {
        super(sequence);
        this.deleteSchema = deleteSchema;
    }

    /**
     * INTERNAL:
     * Should be a sequence defining table sequence in the db:
     * either TableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is a TableSequence).
     */
    public TableSequenceDefinition(String name, boolean deleteSchema) {
        super(name);
        this.deleteSchema = deleteSchema;
    }

    /**
     * INTERNAL:
     * Return the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("INSERT INTO ");
            writer.write(getSequenceTableQualifiedName());
            writer.write("(" + getSequenceNameFieldName());
            writer.write(", " + getSequenceCounterFieldName());
            writer.write(") values (");
            writer.write("'" + getName() + "', "  + (getInitialValue() - 1) + ")");
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
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
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
     * INTERNAL:
     * Execute the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     * @deprecated Implement {@code DatabasePlatform.checkSequenceExists(...)} instead.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    @SuppressWarnings({"removal"})
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT * FROM ");
        buffer.append(getSequenceTableQualifiedName());
        buffer.append(" WHERE ");
        buffer.append(getSequenceNameFieldName());
        buffer.append(" = '");
        buffer.append(getName());
        buffer.append("'");
        List<?> results = session.priviledgedExecuteSelectingCall(new SQLCall(buffer.toString()));
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
    @Deprecated(forRemoval = true, since = "4.0.9")
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
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void dropDatabaseSchemaOnDatabase(AbstractSession session) throws EclipseLinkException {
        tableDefinition.dropDatabaseSchemaOnDatabase(session);
    }

    /**
     * Return the schema associated with this table sequence.
     */
    @Override
    public String getDatabaseSchema() {
        return sequenceTableQualifier;
    }

    /**
     */
    public String getSequenceCounterFieldName() {
        return sequenceCounterFieldName;
    }

    /**
     */
    public String getSequenceNameFieldName() {
        return sequenceNameFieldName;
    }

    /**
     * Return the database table for the sequence.
     * @deprecated To be removed with no replacement.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public DatabaseTable getSequenceTable() {
        return getTableSequence().getTable();
    }

    /**
     */
    public List<IndexDefinition> getSequenceTableIndexes() {
        if  (sequenceTableIndexes == null) {
            sequenceTableIndexes = new ArrayList<>();
        }
        return sequenceTableIndexes;
    }

    /**
     */
    public String getSequenceTableName() {
        return sequenceTableName;
    }

    /**
     */
    public String getSequenceTableQualifier() {
        return sequenceTableQualifier;
    }

    /**
     */
    public String getSequenceTableQualifiedName() {
        return getSequenceTableQualifier().isBlank() ? getSequenceTableName() : getSequenceTableQualifier() + '.' + getSequenceTableName();
    }

    /**
     * INTERNAL:
     * Return a TableDefinition specifying sequence table.
     * Cache the table definition for re-use (during CREATE and DROP)
     */
    @Override
    public TableDefinition buildTableDefinition() {
        if (tableDefinition == null) {
            tableDefinition = new TableDefinition() {
                // default implementation gets the schema from the DatabaseTable
                // which we do not want to have here to avoid cyclic references
                @Override
                public String getDatabaseSchema() {
                    return getQualifier();
                }
            };
            tableDefinition.setName(getSequenceTableName());
            tableDefinition.setQualifier(getSequenceTableQualifier());
            tableDefinition.addPrimaryKeyField(getSequenceNameFieldName(), String.class, 50);
            tableDefinition.addField(getSequenceCounterFieldName(), BigDecimal.class);
            tableDefinition.setIndexes(getSequenceTableIndexes());
        }

        return tableDefinition;
    }

    /**
     * @deprecated To be removed with no replacement.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    protected TableSequence getTableSequence() {
        if(sequence.isTable()) {
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
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void preDropObject(AbstractSession session, Writer dropSchemaWriter, boolean createSQLFiles) {
        if ((session.getPlatform().shouldCreateIndicesForPrimaryKeys()) || (session.getPlatform().shouldCreateIndicesOnUniqueKeys())) {
            // Do not drop index when database is Symfoware. Index is required for primary keys or unique keys.
            return;
        }
        buildTableDefinition().preDropObject(session, dropSchemaWriter, createSQLFiles);
    }

    public void setSequenceCounterFieldName(String sequenceCounterFieldName) {
        this.sequenceCounterFieldName = sequenceCounterFieldName;
    }

    public void setSequenceNameFieldName(String sequenceNameFieldName) {
        this.sequenceNameFieldName = sequenceNameFieldName;
    }

    public void setSequenceTableIndexes(List<IndexDefinition> sequenceTableIndexes) {
        this.sequenceTableIndexes = sequenceTableIndexes;
    }

    public void setSequenceTableName(String sequenceTableName) {
        this.sequenceTableName = sequenceTableName;
    }

    public void setSequenceTableQualifier(String sequenceTableQualifier) {
        this.sequenceTableQualifier = sequenceTableQualifier;
    }

    /**
     * INTERNAL:
     * Returns true if the table definition should be dropped during buildDeletionWriter.
     */
    protected boolean shouldDropTableDefinition() {
        return (deleteSchema && hasDatabaseSchema());
    }
}
