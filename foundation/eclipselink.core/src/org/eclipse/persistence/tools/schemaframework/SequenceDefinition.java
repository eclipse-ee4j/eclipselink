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

import java.io.Writer;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating sequences on the different platforms,
 * and allow optional parameters to be specified.
 * <p>
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
     */
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        if (checkIfExist(session)) {
            alterOnDatabase(session);
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