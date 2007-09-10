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

import java.io.Writer;
import java.util.*;
import java.math.BigDecimal;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 *
 */
public class XMLSequenceDefinition extends SequenceDefinition {
    protected XMLSequence sequence;

    /**
     *
     */
    public XMLSequenceDefinition(XMLSequence sequence) {
        super(sequence.getName());
        this.sequence = sequence;
    }

    public XMLSequenceDefinition(String name, XMLSequence sequence) {
        super(name);
        this.sequence = sequence;
    }

    /**
     * INTERAL:
     * Execute the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        ValueReadQuery query = sequence.getSelectQuery();
        Vector args = new Vector(1);
        args.addElement(getName());
        Object result = session.executeQuery(query, args);
        return result != null;
    }

    /**
     * INTERNAL:
     */
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        if (!checkIfExist(session)) {
            Vector args = new Vector(2);
            args.addElement(getName());
            args.addElement("0");
            session.executeQuery(buildInsertSequenceQuery(), args);
        }
    }

    /**
     * INTERNAL:
     */
    public void dropFromDatabase(AbstractSession session) throws EclipseLinkException {
    }

    /**
     * Answer a TableDefinition specifying the platforms sequence table.
     */
    public TableDefinition buildTableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName(sequence.getRootElementName());
        definition.addField(sequence.getNameElementName(), String.class);
        definition.addField(sequence.getCounterElementName(), BigDecimal.class);
        return definition;
    }

    /**
     * Build and return a query for inserting a new sequence.
     */
    protected DatabaseQuery buildInsertSequenceQuery() {
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument(sequence.getNameElementName());
        query.addArgument(sequence.getCounterElementName());
        query.setCall(buildInsertSequenceCall());
        return query;
    }

    /**
     * Build and return a call for inserting a new sequence.
     */
    protected Call buildInsertSequenceCall() {
        XMLDataInsertCall call = new XMLDataInsertCall();
        call.setRootElementName(sequence.getRootElementName());
        call.setPrimaryKeyElementName(sequence.getNameElementName());
        return call;
    }

    /**
     * INTERNAL:
     * Returns the writer used for creation of this object.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        // not used
        return writer;
    }

    /**
     * INTERNAL:
     * Returns the writer used for creation of this object.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        // not used
        return writer;
    }
}