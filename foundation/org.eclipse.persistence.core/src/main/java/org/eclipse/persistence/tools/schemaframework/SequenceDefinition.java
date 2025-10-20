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

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

import java.io.Writer;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * </p>
 */
public abstract class SequenceDefinition extends DatabaseObjectDefinition {

    private int initialValue;
    private int preallocationSize;

    /**
     * @deprecated To be removed with no replacement.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    protected Sequence sequence;

    protected SequenceDefinition(String name) {
        super();
        setName(name);
        initialValue = 1;
        preallocationSize = 50;
    }

    /**
     * @deprecated Use {@linkplain #SequenceDefinition(String)} instead.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    protected SequenceDefinition(Sequence sequence) {
        super();
        this.sequence = sequence;
        setName(sequence.getName());
        setQualifier(sequence.getQualifier());
        initialValue = sequence.getInitialValue();
        preallocationSize = sequence.getPreallocationSize();
    }

    /**
     * INTERNAL:
     * Verify whether the sequence exists.
     * @deprecated Implement {@code DatabasePlatform.checkSequenceExists(...)} instead.
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
    public void alterOnDatabase(AbstractSession session) throws EclipseLinkException {
    }

    /**
     * INTERNAL:
     * Execute the SQL required to alter sequence.
     * By default, does nothing.
     */
    public void alter(AbstractSession session, Writer writer) throws ValidationException {
    }

    /**
     * INTERNAL:
     * Creates this sequence definition on the database.  If it already exists, the method will attempt
     * to alter it based on what the platform supports.
     */
    @Override
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        boolean exists = false;
        final boolean loggingOff = session.isLoggingOff();
        try {
            exists = session.getPlatform().checkSequenceExists(session, this, true);
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
