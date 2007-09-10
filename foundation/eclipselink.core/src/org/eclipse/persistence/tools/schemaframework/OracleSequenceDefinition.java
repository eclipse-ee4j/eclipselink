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

import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Support Oracle native sequence creation.
 *     Oracle has custom support for sequences.
 * <p>
 */
public class OracleSequenceDefinition extends SequenceDefinition {

    /** The increment can be used to support pre-allocation. */
    protected int increment;

    /** The start is the first sequence value that will be available for TopLink to use. */
    protected int start = 1;

    public OracleSequenceDefinition(String name, int preallocationSize) {
        super(name);
        setIncrement(preallocationSize);
    }

    public OracleSequenceDefinition(String name, int preallocationSize, int start) {
        super(name);
        // sequence value should be positive
        if(start <= 0) {
            start = 1;
        }
        setIncrement(preallocationSize);
        setStart(start);
	}
    
    public OracleSequenceDefinition(String name) {
        this(name, 1);
    }

    public OracleSequenceDefinition(NativeSequence sequence) {
        this(sequence.getName(), sequence.getPreallocationSize(), sequence.getInitialValue());
    }

    /**
     * INTERNAL:
     * Return the SQL required to create the Oracle sequence object.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) {
        try {
            writer.write("CREATE SEQUENCE ");
            writer.write(getFullName());
            if (getIncrement() != 1) {
                writer.write(" INCREMENT BY " + getIncrement());
            }
            // startWith value calculated using the initial value and increment.
            // The first time TopLink calls select nextval, the value equal to startWith is returned.
            // The first sequence value TopLink may assign is startWith - getIncrement() + 1.
            int startWith = getStart() + getIncrement() - 1;
            writer.write(" START WITH " + startWith);
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the SQL required to drop the Oracle sequence object.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) {
        try {
            writer.write("DROP SEQUENCE ");
            writer.write(getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the SQL required to alter INCREMENT BY
     */
    public Writer buildAlterIncrementWriter(AbstractSession session, Writer writer) {
        try {
            writer.write("ALTER SEQUENCE ");
            writer.write(getFullName());
            writer.write(" INCREMENT BY " + getIncrement());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Check if the sequence object already exists, in which case dont create it.
     */
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        try {
            session.executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT " + getName() + ".NEXTVAL FROM DUAL"));
        } catch (DatabaseException exception) {
            return false;
        }
        return true;
    }

    /**
     * The increment can be used to support pre-allocation.
     */
    public int getIncrement() {
        return increment;
    }

    /**
     * The increment can be used to support pre-allocation.
     */
    public void setIncrement(int increment) {
        this.increment = increment;
    }

    /**
     * The start used as a starting value for sequence
     */
    public int getStart() {
        return start;
    }

    /**
     * The start used as a starting value for sequence
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * The start used as a starting value for sequence
     */
    public void setStartAndIncrement(int value) {
        setStart(1);
        setIncrement(value);
    }

    /**
     * INTERNAL:
     * Indicates whether alterIncrement is supported
     */
    public boolean isAlterSupported() {
        return true;
    }

    /**
     * INTERNAL:
     * Execute the SQL required to alter sequence increment.
     * Assume that the sequence exists.
     */
    public void alterOnDatabase(AbstractSession session) throws EclipseLinkException {
        try {
            session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(buildAlterIncrementWriter(session, new StringWriter()).toString()));
        } catch (DatabaseException exception) {
            createOnDatabase(session);
        }
    }

    /**
     * INTERNAL:
     * Execute the SQL required to alter sequence increment.
     * Assume that the sequence exists.
     */
    public void alterIncrement(AbstractSession session, Writer schemaWriter) throws ValidationException {
        if (schemaWriter == null) {
            this.alterOnDatabase(session);
        } else {
            this.buildAlterIncrementWriter(session, schemaWriter);
        }
    }
}