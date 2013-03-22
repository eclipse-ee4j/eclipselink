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
 *     07/25/2008-1.0   Michael OBrien 
 *       - 242120: Let the DB exception in alterOnDatabase() propagate so as not to
 *                         cause an infinite loop when ddl generation updates fail on a JTA DS
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Sequence definition Native Sequence object.
 * <p>
 */
public class SequenceObjectDefinition extends SequenceDefinition {

    /**
     * INTERNAL:
     * Should be a sequence defining sequence object in the db:
     * either NativeSequence with shouldAcquireValueAfterInsert() returning false;
     * or DefaultSequence (only if case platform.getDefaultSequence() is a
     * NativeSequence with shouldAcquireValueAfterInsert() returning false).
     */
    public SequenceObjectDefinition(Sequence sequence) {
        super(sequence);
    }

    /**
     * INTERNAL:
     * Return the SQL required to create the Oracle sequence object.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) {
        try {
            // startWith value calculated using the initial value and increment.
            // The first time TopLink calls select nextval, the value equal to startWith is returned.
            // The first sequence value EclipseLink may assign is startWith - getIncrement() + 1.
            int startWith = sequence.getInitialValue() + sequence.getPreallocationSize() - 1;
            session.getPlatform().buildSequenceObjectCreationWriter(writer, getFullName(), sequence.getPreallocationSize(), startWith);
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
            session.getPlatform().buildSequenceObjectDeletionWriter(writer, getFullName());
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
            session.getPlatform().buildSequenceObjectAlterIncrementWriter(writer, getFullName(), sequence.getPreallocationSize());
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
        boolean isConnected = sequence.isConnected();
        // temporary connect sequence if it's not connected.
        if(!isConnected) {
            sequence.onConnect(session.getPlatform());
        }
        try {
            if(sequence.shouldUsePreallocation()) {
                sequence.getGeneratedVector(null, session);
            } else {
                sequence.getGeneratedValue(null, session);
            }
            return true;
        } catch (DatabaseException databaseException) {
            return false;
        } catch (ValidationException validationException) {
            // This exception indicates that the current preallocationSize
            // is greater than the returned value. 
            // It should be ignored because sequence actually exists in the db.
            if(validationException.getErrorCode() == ValidationException.SEQUENCE_SETUP_INCORRECTLY) {
                return true;
            } else {
                throw validationException;
            }
        } finally {
            if(!isConnected) {
                sequence.onDisconnect(session.getPlatform());
            }
        }
    }

    /**
     * INTERNAL:
     * Indicates whether alterIncrement is supported
     */
    public boolean isAlterSupported(AbstractSession session) {
        return session.getPlatform().isAlterSequenceObjectSupported();
    }

    /**
     * INTERNAL:
     * Execute the SQL required to alter sequence increment.
     * Assume that the sequence exists.
     */
    public void alterOnDatabase(AbstractSession session) throws EclipseLinkException {
        // Bug# 242120: Let the DatabaseException propagate and do not call 
        // createOnDatabase(session) which would cause an infinite loop on a JTA connection
        session.priviledgedExecuteNonSelectingCall(new SQLCall(buildAlterIncrementWriter(session, new StringWriter()).toString()));
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

    /**
     * INTERNAL:
     * Most major databases support a creator name scope.
     * This means whenever the database object is referenced, it must be qualified.
     */
    public String getFullName() {
        return sequence.getQualified(getName());
    }
}
