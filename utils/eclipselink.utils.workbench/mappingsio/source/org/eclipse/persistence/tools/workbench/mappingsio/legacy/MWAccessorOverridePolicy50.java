/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import java.io.Reader;
import java.io.Writer;
import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import deprecated.xml.XMLDataStoreException;
/**
 * This interface is used by Accessor to override what streams
 * are used for reading, writing, and deleting. An implementation class
 * should implement each method and throw a UnsupportedAccessorOperationException
 * if it does not override the specified data.
 * 
 * @see Accessor
 * @see deprecated.xml.XMLAccessor
 */
interface MWAccessorOverridePolicy50 {

/**
 * @see deprecated.xml.XMLAccessor#deleteStream(String, DatabaseRow, Vector)
 */
Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException;

/**
 * @see deprecated.xml.XMLAccessor#getExistenceCheckStream(String, DatabaseRow, Vector)
 */
Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException;

/**
 * @see deprecated.xml.XMLAccessor#getExistingWriteStream(String, DatabaseRow, Vector)
 */
Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException;

/**
 * @see deprecated.xml.XMLAccessor#getNewWriteStream(String, DatabaseRow, Vector)
 */
Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException;

/**
 * @see deprecated.xml.XMLAccessor#getReadStream(String, DatabaseRow, Vector)
 */
Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException;

}
