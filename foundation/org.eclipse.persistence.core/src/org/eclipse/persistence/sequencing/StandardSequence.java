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
 ******************************************************************************/  
package org.eclipse.persistence.sequencing;

import java.util.Vector;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.DatabaseException;

/**
 * <p>
 * <b>Purpose</b>: An abstract class providing default sequence behavior.
 * <p>
 */
public abstract class StandardSequence extends Sequence {
    public StandardSequence() {
        super();
    }

    public StandardSequence(String name) {
        super(name);
    }

    public StandardSequence(String name, int size) {
        super(name, size);
    }

    public StandardSequence(String name, int size, int initialValue) {
        super(name, size, initialValue);
    }
    
    public void onConnect() {
        // does nothing
    }

    public void onDisconnect() {
        // does nothing
    }

    protected abstract Number updateAndSelectSequence(Accessor accessor, AbstractSession writeSession, String seqName, int size);

    public abstract boolean shouldAcquireValueAfterInsert();

    public abstract boolean shouldUseTransaction();

    public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
        if (shouldUsePreallocation()) {
            return null;
        } else {
            Number value = updateAndSelectSequence(accessor, writeSession, seqName, 1);
            if (value == null) {
                throw DatabaseException.errorPreallocatingSequenceNumbers();
            }
            return value;
        }
    }

    public Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
        if (shouldUsePreallocation()) {
            Number value = updateAndSelectSequence(accessor, writeSession, seqName, size);
            if (value == null) {
                throw DatabaseException.errorPreallocatingSequenceNumbers();
            }
            return createVector(value, seqName, size);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     * given sequence = 10, size = 5 will create Vector (6,7,8,9,10)
     * @param seqName String is sequencing number field name
     * @param size int size of Vector to create.
     */
    protected Vector createVector(Number sequence, String seqName, int size) {
        long nextSequence = sequence.longValue();

        Vector sequencesForName = new Vector(size);
        nextSequence = nextSequence - size;

        // Check for incorrect values return to validate that the sequence is setup correctly.
        // PRS 36451 intvalue would wrap
        if (nextSequence < -1L) {
            throw ValidationException.sequenceSetupIncorrectly(seqName);
        }

        for (int index = size; index > 0; index--) {
            nextSequence = nextSequence + 1L;
            sequencesForName.add(nextSequence);
        }
        return sequencesForName;
    }

    public void setInitialValue(int initialValue) {
        // sequence value should be positive
        if (initialValue <= 0) {
            initialValue = 1;
        }
        super.setInitialValue(initialValue);
    }
}
