/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sequencing.*;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;

/**
 * This class extends the base TOPLink <code>SchemaManager</code>
 * to create XML stream sources and sequences for XML Projects.
 *
 * @author Les Davis
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLSchemaManager extends SchemaManager {

    /**
     * Construct a schema manager for the specified session.
     */
    public XMLSchemaManager(DatabaseSessionImpl session) {
        super(session);
    }

    /**
     * Construct a schema manager for the specified session.
     */
    public XMLSchemaManager(org.eclipse.persistence.sessions.DatabaseSession session) {
        super(session);
    }

    protected SequenceDefinition buildSequenceDefinition(Sequence sequence) {
        if (sequence instanceof DefaultSequence) {
            String name = sequence.getName();
            int size = sequence.getPreallocationSize();
            sequence = getSession().getDatasourcePlatform().getDefaultSequence();
            if (sequence instanceof XMLSequence) {
                XMLSequence xmlSequence = (XMLSequence)sequence;
                return new XMLSequenceDefinition(name, xmlSequence);
            } else {
                return null;
            }
        } else if (sequence instanceof XMLSequence) {
            XMLSequence xmlSequence = (XMLSequence)sequence;
            return new XMLSequenceDefinition(xmlSequence);
        } else {
            return null;
        }
    }

    public void createObject(DatabaseObjectDefinition databaseObjectDefinition) throws EclipseLinkException {
        if (shouldWriteToDatabase()) {
            if (databaseObjectDefinition instanceof TableDefinition) {
                this.createStreamSource((TableDefinition)databaseObjectDefinition);
            } else if (databaseObjectDefinition instanceof XMLSequenceDefinition) {
                databaseObjectDefinition.createOnDatabase(getSession());
            }
        }
    }

    public void dropObject(DatabaseObjectDefinition databaseObjectDefinition) throws EclipseLinkException {
        if (shouldWriteToDatabase()) {
            if (databaseObjectDefinition instanceof TableDefinition) {
                this.dropStreamSource((TableDefinition)databaseObjectDefinition);
            }
        }
    }

    /**
     * Delegate to the XML accessor.
     */
    protected void createStreamSource(TableDefinition tableDefinition) throws EclipseLinkException {
        XMLAccessor accessor = (XMLAccessor)this.getSession().getAccessor();
        accessor.createStreamSource(tableDefinition.getName());
    }

    /**
     * Delete all sequence files and the sequence directory.
     */
    protected void dropStreamSource(TableDefinition tableDefinition) {
        XMLAccessor accessor = (XMLAccessor)this.getSession().getAccessor();
        accessor.dropStreamSource(tableDefinition.getName());
    }
}