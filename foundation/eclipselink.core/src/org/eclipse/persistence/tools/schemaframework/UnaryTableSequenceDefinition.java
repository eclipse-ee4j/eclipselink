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
import org.eclipse.persistence.sequencing.UnaryTableSequence;

/**
 * <p>
 * <b>Purpose</b>: Creates / drops an unary sequence table:
 * the name of the table is sequence name; its only field is named unarySequenceCounterFieldName
 * <p>
 */
public class UnaryTableSequenceDefinition extends SequenceDefinition {
    public String sequenceCounterFieldName;

    public UnaryTableSequenceDefinition(String name, String sequenceCounterFieldName) {
        super(name);
        setSequenceCounterFieldName(sequenceCounterFieldName);
    }

    public UnaryTableSequenceDefinition(UnaryTableSequence sequence) {
        this(sequence.getName(), sequence.getCounterFieldName());
    }

    public UnaryTableSequenceDefinition(String name, UnaryTableSequence sequence) {
        this(name, sequence.getCounterFieldName());
    }

    /**
     * INTERNAL:
     * Return the SQL required to create the unary sequence table.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("INSERT INTO ");
            writer.write(getName());
            writer.write("(" + getSequenceCounterFieldName());
            writer.write(") values (0)");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the SQL to delete the unary sequence table.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DELETE FROM ");
            writer.write(getName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERAL:
     * Verify whether the sequence exists.
     * Assume that the unary sequence table exists.
     */
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        Vector results = session.executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM " + getName()));
        return !results.isEmpty();
    }

    /**
     * PUBLIC:
     * Return the name of the only field of this table
     */
    public String getSequenceCounterFieldName() {
        return sequenceCounterFieldName;
    }

    /**
     * PUBLIC:
     * Set the name of the only field of this table
     */
    public void setSequenceCounterFieldName(String sequenceCounterFieldName) {
        this.sequenceCounterFieldName = sequenceCounterFieldName;
    }

    /**
     * INTERNAL:
     * Return a TableDefinition specifying a unary sequence table.
     */
    public TableDefinition buildTableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName(getName());
        definition.addField(getSequenceCounterFieldName(), BigDecimal.class);
        return definition;
    }
}