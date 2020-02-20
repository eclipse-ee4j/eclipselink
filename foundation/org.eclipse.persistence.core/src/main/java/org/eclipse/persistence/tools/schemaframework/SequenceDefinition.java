/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Services Ltd.
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
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * </p>
 */
public abstract class SequenceDefinition extends DatabaseObjectDefinition {
    protected Sequence sequence;

    public SequenceDefinition(String name) {
        super();
        this.name = name;
    }

    public SequenceDefinition(Sequence sequence) {
        super();
        this.sequence = sequence;
        this.name = sequence.getName();
    }

    /**
     * INTERAL:
     * Verify whether the sequence exists.
     */
    public abstract boolean checkIfExist(AbstractSession session) throws DatabaseException;

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
     * By default does nothing.
     */
    public void alterOnDatabase(AbstractSession session) throws EclipseLinkException {
    }

    /**
     * INTERNAL:
     * Execute the SQL required to alter sequence.
     * By default does nothing.
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
        // If the sequence does not already exist a stack trace will be logged
        // this temporarily  sets the level to FINEST to avoid having appear in the log
        // unnecessarily
        int logLevel = session.getLogLevel();
        session.setLogLevel(SessionLog.FINEST);
        boolean sequenceExists;
        try {
            sequenceExists = checkIfExist(session);
        } finally {
            //reset log level
            session.setLogLevel(logLevel);
        }
        
        if (sequenceExists) {
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
}
