/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.util.Vector;
import java.io.*;
import java.math.BigDecimal;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.TableSequence;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * <p>
 */
public class TableSequenceDefinition extends SequenceDefinition {

    /** Hold the name of the sequence table */
    public String sequenceTableName;

    /** Hold the name of the column in the sequence table which specifies the sequence name */
    public String sequenceNameFieldName;

    /** Hold the name of the column in the sequence table which specifies the sequence numeric value */
    public String sequenceCounterFieldName;
    
    public int initialValue = 0;

    public TableSequenceDefinition(String name, String sequenceTableName, String sequenceNameFieldName, String sequenceCounterFieldName, int initialValue) {
        super(name);
        setSequenceTableName(sequenceTableName);
        setSequenceCounterFieldName(sequenceCounterFieldName);
        setSequenceNameFieldName(sequenceNameFieldName);
        setInitialValue(initialValue);
    }

    public TableSequenceDefinition(TableSequence sequence) {
        this(sequence.getName(), sequence.getTableName(), sequence.getNameFieldName(), sequence.getCounterFieldName(), sequence.getInitialValue());
    }

    public TableSequenceDefinition(String name, TableSequence sequence) {
        this(name, sequence.getTableName(), sequence.getNameFieldName(), sequence.getCounterFieldName(), sequence.getInitialValue());
    }

    /**
     * INTERNAL:
     * Return the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("INSERT INTO ");
            writer.write(getSequenceTableName());
            writer.write("(" + getSequenceNameFieldName());
            writer.write(", " + getSequenceCounterFieldName());
            writer.write(") values (");
            writer.write("'" + getName() + "', "  + initialValue + ")");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the SQL to delete the row from the sequence table.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DELETE FROM ");
            writer.write(getSequenceTableName());
            writer.write(" WHERE " + getSequenceNameFieldName());
            writer.write(" = '" + getName() + "'");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERAL:
     * Execute the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        Vector results = session.executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM " + getSequenceTableName() + " WHERE " + getSequenceNameFieldName() + " = '" + getName() + "'"));
        return !results.isEmpty();
    }

    /**
     * PUBLIC:
     */
    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }
    
    /**
     * PUBLIC:
     */
    public void setSequenceTableName(String sequenceTableName) {
        this.sequenceTableName = sequenceTableName;
    }

    /**
     * PUBLIC:
     */
    public String getSequenceTableName() {
        return sequenceTableName;
    }

    /**
     * PUBLIC:
     */
    public void setSequenceCounterFieldName(String sequenceCounterFieldName) {
        this.sequenceCounterFieldName = sequenceCounterFieldName;
    }

    /**
     * PUBLIC:
     */
    public String getSequenceCounterFieldName() {
        return sequenceCounterFieldName;
    }

    /**
     * PUBLIC:
     */
    public void setSequenceNameFieldName(String sequenceNameFieldName) {
        this.sequenceNameFieldName = sequenceNameFieldName;
    }

    /**
     * PUBLIC:
     */
    public String getSequenceNameFieldName() {
        return sequenceNameFieldName;
    }

    /**
     * INTERNAL:
     * Return a TableDefinition specifying sequence table.
     */
    public TableDefinition buildTableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName(getSequenceTableName());
        definition.addPrimaryKeyField(getSequenceNameFieldName(), String.class, 50);
        definition.addField(getSequenceCounterFieldName(), BigDecimal.class);
        return definition;
    }
}