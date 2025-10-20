/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.schemaframework;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Creates / drops an unary sequence table:
 * the name of the table is sequence name; its only field is named unarySequenceCounterFieldName
 * </p>
 */
public class UnaryTableSequenceDefinition extends TableSequenceDefinition {
    /**
     * INTERNAL:
     * Should be a sequence defining unary table sequence in the db:
     * either UnaryTableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is an UnaryTableSequence).
     * @deprecated Use {@linkplain #UnaryTableSequenceDefinition(String, boolean)} instead.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public UnaryTableSequenceDefinition(Sequence sequence) {
        this(sequence, false);
    }

    /**
     * @deprecated Use {@linkplain #UnaryTableSequenceDefinition(String, boolean)} instead.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    @SuppressWarnings({"removal"})
    public UnaryTableSequenceDefinition(Sequence sequence, boolean deleteSchema) {
        super(sequence, deleteSchema);
    }

    /**
     * INTERNAL:
     * Should be a sequence defining unary table sequence in the db:
     * either UnaryTableSequence
     * DefaultSequence (only if case platform.getDefaultSequence() is an UnaryTableSequence).
     */
    public UnaryTableSequenceDefinition(String name, boolean deleteSchema) {
        super(name, deleteSchema);
    }

    /**
     * INTERNAL:
     * Return the SQL required to create the unary sequence table.
     */
    @Override
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("INSERT INTO ");
            writer.write(getName());
            writer.write("(" + getSequenceCounterFieldName());
            writer.write(") values ("+ (getInitialValue() - 1) +")");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the SQL to delete the unary sequence table.
     */
    @Override
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
     * INTERNAL:
     * Verify whether the sequence exists.
     * Assume that the unary sequence table exists.
     * @deprecated Implement {@code DatabasePlatform.checkSequenceExists(...)} instead.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    @SuppressWarnings({"removal"})
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        List<?> results = session.priviledgedExecuteSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM " + getName()));
        return !results.isEmpty();
    }

    /**
     * INTERNAL:
     * Return a TableDefinition specifying a unary sequence table.
     */
    @Override
    public TableDefinition buildTableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName(getName());
        definition.addField(getSequenceCounterFieldName(), BigDecimal.class);
        return definition;
    }

    /**
     * @deprecated To be removed with no replacement.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    protected UnaryTableSequence getUnaryTableSequence() {
        if(sequence.isUnaryTable()) {
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
