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
 *     02/04/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
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
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        if (checkIfExist(session)) {
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
