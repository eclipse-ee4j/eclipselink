/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.stream;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import deprecated.sdk.SDKAccessor;
import deprecated.sdk.SDKFieldValue;
import deprecated.xml.XMLAccessor;
import deprecated.xml.XMLDataStoreException;
import deprecated.xml.XMLStreamStreamPolicy;
import deprecated.xml.XMLTranslator;
import deprecated.xml.xerces.DefaultXMLTranslator;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>:
 * <p> New SDKAccessor for writing SDK output in to a single stream
 *
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLStreamAccessor extends SDKAccessor implements XMLAccessor {
    private XMLStreamDatabase xmlStreamDatabase;
    private XMLStreamStreamPolicy xmlStreamPolicy;
    private XMLTranslator translator;

    public XMLStreamAccessor() {
        super();
        this.translator = new DefaultXMLTranslator();
    }

    public void setXMLStreamDatabase(XMLStreamDatabase newXMLStreamDatabase) {
        this.xmlStreamDatabase = newXMLStreamDatabase;
    }

    /**
     * Everything in XML must be strings.
     */
    public Record convert(Record row, AbstractSession session) {
        if ((row == null) || row.isEmpty()) {
            return row;
        }
        DatabaseRecord result = new DatabaseRecord(row.size());
        for (Enumeration keys = ((AbstractRecord)row).keys(); keys.hasMoreElements();) {
            DatabaseField key = (DatabaseField)keys.nextElement();
            Object value = row.get(key);

            if (value instanceof SDKFieldValue) {
                // recurse through nested rows
                value = this.convert((SDKFieldValue)value, session);
            } else {
                // everything else must be converted to a string
                value = this.convert(value, String.class, session);
            }

            result.put(key, value);
        }
        return result;
    }

    /**
     * Convert an object to the specified class.
     */
    protected Object convert(Object value, Class javaClass, AbstractSession session) {
        return session.getDatasourcePlatform().convertObject(value, javaClass);
    }

    /**
     * Convert a nested collection of database rows.
     */
    protected Object convert(SDKFieldValue fieldValue, AbstractSession session) {
        Vector newElements = new Vector(fieldValue.getElements().size());
        for (Enumeration stream = fieldValue.getElements().elements(); stream.hasMoreElements();) {
            Object newElement = null;
            if (fieldValue.isDirectCollection()) {
                newElement = this.convert(stream.nextElement(), String.class, session);
            } else {
                newElement = this.convert((AbstractRecord)stream.nextElement(), session);
            }
            newElements.addElement(newElement);
        }
        return fieldValue.clone(newElements);
    }

    /**
     * Create a source for data streams for
     * the XML documents with the specified root element name.
     */
    public void createStreamSource(String rootElementName) throws XMLDataStoreException {
        this.xmlStreamDatabase.createStreamSource(rootElementName);
    }

    /**
     * Delete the data for the specified root element and primary key.
     * Return the stream count (1 or 0).
     */
    public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        return getWriteStreamPolicy(rootElementName, row, orderedPrimaryKeyElements).deleteStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Drop the source for data streams for
     * the XML documents with the specified root element name.
     */
    public void dropStreamSource(String rootElementName) throws XMLDataStoreException {
        this.xmlStreamDatabase.dropStreamSource(rootElementName);
    }

    /**
     * If it exists, return a read stream on the data for the specified
     * root element and primary key.
     * If it does not exist, return null.
     */
    public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        return getReadStreamPolicy(rootElementName, row, orderedPrimaryKeyElements).getExistenceCheckStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return a write stream that will overwrite the data for the specified
     * root element and primary key.
     */
    public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        return getWriteStreamPolicy(rootElementName, row, orderedPrimaryKeyElements).getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return a new write stream for the specified
     * root element and primary key.
     */
    public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        StringWriter writer = (StringWriter)getWriteStreamPolicy(rootElementName, row, orderedPrimaryKeyElements).getNewWriteStream(rootElementName, row, orderedPrimaryKeyElements, this);
        this.xmlStreamDatabase.putWriter(rootElementName, row, orderedPrimaryKeyElements, writer);
        return writer;
    }

    /**
     * Return a read stream on the data for the specified
     * root element and primary key.
     * If the stream is not found return null.
     */
    public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        return getReadStreamPolicy(rootElementName, row, orderedPrimaryKeyElements).getReadStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return an enumeration on a collection of read streams,
     * one for *every* document with the specified root element.
     */
    public Enumeration getReadStreams(String rootElementName) throws XMLDataStoreException {
        return getReadStreamPolicy(rootElementName).getReadStreams(rootElementName, this);
    }

    /**
     * Return an enumeration on a collection of streams,
     * one for every specified foreign key.
     * If a particular stream is not found the
     * enumeration will return null in its place.
     */
    public Enumeration getReadStreams(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements) throws XMLDataStoreException {
        return getReadStreamPolicy(rootElementName, foreignKeys, orderedForeignKeyElements).getReadStreams(rootElementName, foreignKeys, orderedForeignKeyElements, this);
    }

    /**
     * Return the default XML translator for all data store calls.
     */
    public XMLTranslator getXMLTranslator() {
        return this.translator;
    }

    /**
     * Set the default XML translator for all data store calls.
     */
    public void setXMLTranslator(XMLTranslator translator) {
        this.translator = translator;
    }

    /**
     *
     */
    private XMLStreamStreamPolicy getReadStreamPolicy(String rootElementName) {
        Enumeration readers = this.xmlStreamDatabase.getReaders(rootElementName);
        return new XMLStreamStreamPolicy(readers);
    }

    private XMLStreamStreamPolicy getReadStreamPolicy(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
        Reader reader = this.xmlStreamDatabase.getReader(rootElementName, row, orderedPrimaryKeyElements);
        return new XMLStreamStreamPolicy(reader);
    }

    private XMLStreamStreamPolicy getReadStreamPolicy(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements) {
        Enumeration readers = this.xmlStreamDatabase.getReaders(rootElementName, foreignKeys, orderedForeignKeyElements);
        return new XMLStreamStreamPolicy(readers);
    }

    /**
     *
     */
    private XMLStreamStreamPolicy getWriteStreamPolicy(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
        Writer writer = this.xmlStreamDatabase.getWriter(rootElementName, row, orderedPrimaryKeyElements);
        return new XMLStreamStreamPolicy(writer);
    }
}