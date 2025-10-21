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
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.tools.schemaframework;

import java.io.Writer;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * </p>
 */
public abstract class SequenceDefinition extends DatabaseObjectDefinition {

    protected int initialValue;
    protected int preallocationSize;

    @Deprecated(forRemoval = true, since = "4.0.9")
    protected Sequence sequence;

    protected SequenceDefinition(String name) {
        super(name);
        initialValue = 1;
        preallocationSize = 50;
    }

    @Deprecated(forRemoval = true, since = "4.0.9")
    protected SequenceDefinition(Sequence sequence) {
        super(sequence.getName(), sequence.getQualifier());
        this.sequence = sequence;
        initialValue = sequence.getInitialValue();
        preallocationSize = sequence.getPreallocationSize();
    }

    /**
     * INTERNAL:
     * Verify whether the sequence exists.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public abstract boolean checkIfExist(AbstractSession session) throws DatabaseException;

    public int getInitialValue() {
        return initialValue;
    }

    public int getPreallocationSize() {
        return preallocationSize;
    }

    /**
     * INTERNAL:
     * Indicates whether alter is supported
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public boolean isAlterSupported(AbstractSession session) {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isTableSequenceDefinition() {
        return false;
    }

    /**
     * INTERNAL:
     * By default, does nothing.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void alterOnDatabase(AbstractSession session) throws EclipseLinkException {
    }

    /**
     * INTERNAL:
     * Execute the SQL required to alter sequence.
     * By default, does nothing.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void alter(AbstractSession session, Writer writer) throws ValidationException {
    }

    /**
     * INTERNAL:
     * Creates this sequence definition on the database.  If it already exists, the method will attempt
     * to alter it based on what the platform supports.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        boolean exists = false;
        final boolean loggingOff = session.isLoggingOff();
        try {
            session.setLoggingOff(true);
            exists = checkIfExist(session);
        } finally {
            session.setLoggingOff(loggingOff);
        }
        if (exists) {
            if (this.isAlterSupported(session)) {
                alterOnDatabase(session);
            }
        } else {
            super.createOnDatabase(session);
        }
    }

    /**
     * INTERNAL:
     * Return a TableDefinition
     */
    public TableDefinition buildTableDefinition() {
        return null;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public void setPreallocationSize(int preallocationSize) {
        this.preallocationSize = preallocationSize;
    }
}
