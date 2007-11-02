// Copyright (c) 1998, 2007, Oracle. All rights reserved.  
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
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
            // The first sequence value TopLink may assign is startWith - getIncrement() + 1.
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
