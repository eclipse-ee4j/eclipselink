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
import org.eclipse.persistence.sequencing.UnaryTableSequence;

/**
 * <p>
 * <b>Purpose</b>: Creates / drops an unary sequence table:
 * the name of the table is sequence name; its only field is named unarySequenceCounterFieldName
 * <p>
 */
public class UnaryTableSequenceDefinition extends SequenceDefinition {
    /**
     * INTERNAL:
     * Should be a sequence defining unary table sequence in the db:
     * either UnaryTableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is an UnaryTableSequence).
     */
    public UnaryTableSequenceDefinition(Sequence sequence) {
        super(sequence);
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
            writer.write(") values ("+Integer.toString(sequence.getInitialValue() - 1)+")");
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
        Vector results = session.priviledgedExecuteSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM " + getName()));
        return !results.isEmpty();
    }

    /**
     * PUBLIC:
     * Return the name of the only field of this table
     */
    public String getSequenceCounterFieldName() {
        return getUnaryTableSequence().getCounterFieldName();
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
    
    protected UnaryTableSequence getUnaryTableSequence() {
        if(sequence instanceof UnaryTableSequence) {
            return (UnaryTableSequence)sequence;
        } else {
            return (UnaryTableSequence)((DefaultSequence)sequence).getDefaultSequence();
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isTableSequenceDefinition() {
        return true;
    }
}
