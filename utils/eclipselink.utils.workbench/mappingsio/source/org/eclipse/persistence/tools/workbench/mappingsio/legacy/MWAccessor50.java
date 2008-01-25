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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.io.NullWriter;

import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import deprecated.sdk.SDKDataStoreException;
import deprecated.xml.DefaultXMLFileAccessorFilePolicy;
import deprecated.xml.XMLDataStoreException;
import deprecated.xml.XMLFileAccessor;
/**
 * provide hooks for alternative stream sources
 */
class MWAccessor50 extends XMLFileAccessor {

	/** override policies */
	private List overrides;
	
	private Writer nullWriter;
	
		
		
	/**
	 * Constructor.
	 */
	MWAccessor50() {
		super();
		overrides = new ArrayList();
		nullWriter = NullWriter.instance();
	}
	
	/**
	 * Return the current set of override policies.
	 */
	Iterator overrides() {
		return overrides.iterator();
	}
	
	/**
	 * Add a policy that overrides what streams are used for
	 * reading, writing, and deleting. The list of policies is order-sensitive.
	 */
	void addOverridePolicy(MWAccessorOverridePolicy50 overridePolicy) {
		overrides.add(overridePolicy);
	}
		
	/**
	 * Use an override policy if appropriate.
	 */
	public Integer deleteStream(String rootElementName, DatabaseRecord row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		for (Iterator stream = overrides(); stream.hasNext(); ) {
			MWAccessorOverridePolicy50 policy = (MWAccessorOverridePolicy50) stream.next();
			try {
				return policy.deleteStream(rootElementName, row, orderedPrimaryKeyElements);
			} catch (UnsupportedAccessorOperationException50 ex) {
				// try the next override policy
			}
		}
		return super.deleteStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * Use an override policy if appropriate.
	 */
	public Reader getExistenceCheckStream(String rootElementName, DatabaseRecord row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		for (Iterator stream = overrides(); stream.hasNext(); ) {
			MWAccessorOverridePolicy50 policy = (MWAccessorOverridePolicy50) stream.next();
			try {
				return policy.getExistenceCheckStream(rootElementName, row, orderedPrimaryKeyElements);
			} catch (UnsupportedAccessorOperationException50 ex) {
				// try the next override policy
			}
		}
		return super.getExistenceCheckStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * Use an override policy if appropriate.
	 */
	public Writer getExistingWriteStream(String rootElementName, DatabaseRecord row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
	
		for (Iterator stream = overrides(); stream.hasNext(); ) {
			MWAccessorOverridePolicy50 policy = (MWAccessorOverridePolicy50) stream.next();
			try {
				return policy.getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements);
			} catch (UnsupportedAccessorOperationException50 ex) {
				// try the next override policy
			}
		}
		return super.getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * Use an override policy if appropriate.
	 */
	public Writer getNewWriteStream(String rootElementName, DatabaseRecord row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		for (Iterator stream = overrides(); stream.hasNext(); ) {
			MWAccessorOverridePolicy50 policy = (MWAccessorOverridePolicy50) stream.next();
			try {
				return policy.getNewWriteStream(rootElementName, row, orderedPrimaryKeyElements);
			} catch (UnsupportedAccessorOperationException50 ex) {
				// try the next override policy
			}
		}
		return super.getNewWriteStream(rootElementName, row, orderedPrimaryKeyElements);
	
	}
	
	/**
	 * Use an override policy if appropriate.
	 */
	public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		for (Iterator stream = overrides(); stream.hasNext(); ) {
			MWAccessorOverridePolicy50 policy = (MWAccessorOverridePolicy50) stream.next();
			try {
				return policy.getReadStream(rootElementName, row, orderedPrimaryKeyElements);
			} catch (UnsupportedAccessorOperationException50 ex) {
				// try the next override policy
			}
		}
		return super.getReadStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * Hack-o-rama: When we convert a class from "stub" class
	 * to a "non-stub" class, TopLink will want to perform an update
	 * (as opposed to an insert). So we create a stub file
	 * that makes TopLink think it is performing an update.
	 */
	void prepForUpdate(File file) throws XMLDataStoreException {
		try {
			file.createNewFile();
		} catch (IOException ex) {
			throw XMLDataStoreException.ioException(ex);
		}
	}
	
	void setBaseDirectoryNameOverride(String baseDirectoryName) {
		((DefaultXMLFileAccessorFilePolicy) this.getDefaultFilePolicy()).setBaseDirectoryName(baseDirectoryName);
	}

	void setFileNameExtensionOverride(String fileNameExtension) {
		((DefaultXMLFileAccessorFilePolicy) this.getDefaultFilePolicy()).setFileNameExtension(fileNameExtension);
	}

	void setCreatesDirectoriesAsNeededOverride(boolean createsDirectoriesAsNeeded) {
		((DefaultXMLFileAccessorFilePolicy) this.getDefaultFilePolicy()).setCreatesDirectoriesAsNeeded(createsDirectoriesAsNeeded);
	}
}
