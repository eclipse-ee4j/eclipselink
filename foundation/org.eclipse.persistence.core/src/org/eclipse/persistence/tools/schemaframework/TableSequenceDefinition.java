/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.util.Vector;
import java.io.*;
import java.math.BigDecimal;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.TableSequence;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * <p>
 */
public class TableSequenceDefinition extends SequenceDefinition {

    /**
     * INTERNAL:
     * Should be a sequence defining table sequence in the db:
     * either TableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is a TableSequence).
     */
    public TableSequenceDefinition(Sequence sequence) {
        super(sequence);
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
            writer.write("'" + getName() + "', "  + Integer.toString(sequence.getInitialValue() - 1) + ")");
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
        Vector results = session.priviledgedExecuteSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM " + getSequenceTableName() + " WHERE " + getSequenceNameFieldName() + " = '" + getName() + "'"));
        return !results.isEmpty();
    }

    /**
     * PUBLIC:
     */
    public String getSequenceTableName() {
        return getTableSequence().getQualifiedTableName();
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
    
    protected TableSequence getTableSequence() {
        if(sequence instanceof TableSequence) {
            return (TableSequence)sequence;
        } else {
            return (TableSequence)((DefaultSequence)sequence).getDefaultSequence();
        }
    }
}
