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

import java.io.*;
import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.sessions.Record;

/**
 * This implementation of the <code>XMLStreamPolicy</code> interface
 * simply returns the supplied stream (or streams).
 *
 * @see XMLCall
 * @see XMLAccessor
 * @see XMLFileAccessor
 *
 * @author Big Country
 * @since TopLink 4.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLStreamStreamPolicy implements XMLStreamPolicy {

    /** The read stream to be returned. */
    private Reader reader;

    /** The write stream to be returned. */
    private Writer writer;

    /** An enumeration of read streams to be returned. */
    private Enumeration readers;

    /**
     * Default constructor.
     */
    protected XMLStreamStreamPolicy() {
        super();
    }

    /**
     * Construct a policy for the specified read stream.
     */
    public XMLStreamStreamPolicy(Reader reader) {
        this();
        this.reader = reader;
    }

    /**
     * Construct a policy for the specified write stream.
     */
    public XMLStreamStreamPolicy(Writer writer) {
        this();
        this.writer = writer;
    }

    /**
     * Construct a policy for the specified read streams.
     */
    public XMLStreamStreamPolicy(Enumeration readers) {
        this();
        this.readers = readers;
    }

    /**
     * Delete the data for the specified root element and primary key.
     * Return the stream count (1 or 0).
     * Do nothing - subclasses might want to override....
     */
    public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return new Integer(0);
    }

    /**
     * If it exists, return a read stream on the data for the specified
     * root element and primary key.
     * If it does not exist, return null.
     */
    public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getReader();
    }

    /**
     * Return a write stream that will overwrite the data for the specified
     * root element and primary key.
     */
    public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getWriter();
    }

    /**
     * Return a new write stream for the specified
     * root element and primary key.
     */
    public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getWriter();
    }

    /**
     * Return the read stream.
     */
    protected Reader getReader() {
        return reader;
    }

    /**
     * Return the read streams.
     */
    protected Enumeration getReaders() {
        return readers;
    }

    /**
     * Return a read stream on the data for the specified
     * root element and primary key.
     * If the stream is not found return null.
     */
    public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getReader();
    }

    /**
     * Return an enumeration on a collection of streams,
     * one for every specified foreign key.
     * If a particular stream is not found the
     * enumeration will return null in its place.
     */
    public Enumeration getReadStreams(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getReaders();
    }

    /**
     * Return an enumeration on a collection of read streams,
     * one for *every* document with the specified root element.
     */
    public Enumeration getReadStreams(String rootElementName, Accessor accessor) throws XMLDataStoreException {
        return this.getReaders();
    }

    /**
     * Return the write stream.
     */
    protected Writer getWriter() {
        return writer;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        writer.write(org.eclipse.persistence.internal.helper.Helper.getShortClassName(this));
        writer.write("(");

        if ((this.getReader() == null) && (this.getReaders() == null) && (this.getWriter() == null)) {
            writer.write(ToStringLocalization.buildMessage("no_streams", (Object[])null));
        } else if (this.getReader() != null) {
            writer.write(ToStringLocalization.buildMessage("reader", (Object[])null));
        } else if (this.getReaders() != null) {
            writer.write(ToStringLocalization.buildMessage("multiple_readers", (Object[])null));
        } else if (this.getWriter() != null) {
            writer.write(ToStringLocalization.buildMessage("writer", (Object[])null));
        }

        writer.write(")");
        return sw.toString();
    }
}