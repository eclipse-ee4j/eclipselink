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

    /**
     *
     */
    public XMLSequenceDefinition(XMLSequence sequence) {
        super(sequence);
    }

    public XMLSequenceDefinition(String name, XMLSequence sequence) {
        super(sequence);
        this.name = name;
    }

    /**
     * INTERAL:
     * Execute the SQL required to insert the sequence row into the sequence table.
     * Assume that the sequence table exists.
     */
    public boolean checkIfExist(AbstractSession session) throws DatabaseException {
        ValueReadQuery query = getXMLSequence().getSelectQuery();
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
        definition.setName(getXMLSequence().getRootElementName());
        definition.addField(getXMLSequence().getNameElementName(), String.class);
        definition.addField(getXMLSequence().getCounterElementName(), BigDecimal.class);
        return definition;
    }

    /**
     * Build and return a query for inserting a new sequence.
     */
    protected DatabaseQuery buildInsertSequenceQuery() {
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument(getXMLSequence().getNameElementName());
        query.addArgument(getXMLSequence().getCounterElementName());
        query.setCall(buildInsertSequenceCall());
        return query;
    }

    /**
     * Build and return a call for inserting a new sequence.
     */
    protected Call buildInsertSequenceCall() {
        XMLDataInsertCall call = new XMLDataInsertCall();
        call.setRootElementName(getXMLSequence().getRootElementName());
        call.setPrimaryKeyElementName(getXMLSequence().getNameElementName());
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

    protected XMLSequence getXMLSequence() {
        return (XMLSequence)sequence;
    }
}