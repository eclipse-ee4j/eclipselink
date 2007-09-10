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
 * This interface defines methods needed for stream handling in the XML environment.
 *
 * @see XMLAccessor
 * @see XMLFileAccessor
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public interface XMLStreamPolicy extends Serializable {

    /**
     * Delete the data for the specified root element and primary key.
     * Return the stream count (1 or 0).
     */
    Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException;

    /**
     * If it exists, return a read stream on the data for the specified
     * root element and primary key.
     * If it does not exist, return null.
     */
    Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException;

    /**
     * Return a write stream that will overwrite the data for the specified
     * root element and primary key.
     */
    Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException;

    /**
     * Return a new write stream for the specified
     * root element and primary key.
     */
    Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException;

    /**
     * Return a read stream on the data for the specified
     * root element and primary key.
     * If the stream is not found return null.
     */
    Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException;

    /**
     * Return an enumeration on a collection of streams,
     * one for every specified foreign key.
     * If a particular stream is not found the
     * enumeration will return null in its place.
     */
    Enumeration getReadStreams(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements, Accessor accessor) throws XMLDataStoreException;

    /**
     * Return an enumeration on a collection of read streams,
     * one for *every* document with the specified root element.
     */
    Enumeration getReadStreams(String rootElementName, Accessor accessor) throws XMLDataStoreException;
}