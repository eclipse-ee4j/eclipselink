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

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sessions.Record;

/**
 * This implementation of the <code>XMLStreamPolicy</code> interface
 * simply forwards any message to the accessor, which is assumed to be an
 * <code>XMLAccessor</code>.
 *
 * @see XMLCall
 * @see XMLAccessor
 * @see XMLFileAccessor
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLAccessorStreamPolicy implements XMLStreamPolicy {

    /**
     * Default constructor.
     */
    public XMLAccessorStreamPolicy() {
        super();
    }

    /**
     * Delete the data for the specified root element and primary key.
     * Return the stream count (1 or 0).
     * Simply forward the message to the accessor.
     */
    public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).deleteStream(rootElementName, row, orderedPrimaryKeyElements);
    }

    /**
     * If it exists, return a read stream on the data for the specified
     * root element and primary key.
     * If it does not exist, return null.
     * Simply forward the message to the accessor.
     */
    public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).getExistenceCheckStream(rootElementName, row, orderedPrimaryKeyElements);
    }

    /**
     * Return a write stream that will overwrite the data for the specified
     * root element and primary key.
     * Simply forward the message to the accessor.
     */
    public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements);
    }

    /**
     * Return a new write stream for the specified
     * root element and primary key.
     * Simply forward the message to the accessor.
     */
    public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).getNewWriteStream(rootElementName, row, orderedPrimaryKeyElements);
    }

    /**
     * Return a read stream on the data for the specified
     * root element and primary key.
     * If the stream is not found return null.
     * Simply forward the message to the accessor.
     */
    public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).getReadStream(rootElementName, row, orderedPrimaryKeyElements);
    }

    /**
     * Return an enumeration on a collection of streams,
     * one for every specified foreign key.
     * If a particular stream is not found the
     * enumeration will return null in its place.
     * Simply forward the message to the accessor.
     */
    public Enumeration getReadStreams(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).getReadStreams(rootElementName, foreignKeys, orderedForeignKeyElements);
    }

    /**
     * Return an enumeration on a collection of read streams,
     * one for *every* document with the specified root element.
     * Simply forward the message to the accessor.
     */
    public Enumeration getReadStreams(String rootElementName, Accessor accessor) throws XMLDataStoreException {
        return ((XMLAccessor)accessor).getReadStreams(rootElementName);
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this);
    }
}