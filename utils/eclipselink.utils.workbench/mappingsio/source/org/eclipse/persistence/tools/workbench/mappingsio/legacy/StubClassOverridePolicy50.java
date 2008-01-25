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
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import deprecated.xml.XMLDataStoreException;

/**
 * This override policy prevents "stub" MWClasses from being
 * written out to files. It also returns a "stub" MWClass for ANY
 * read that does not have a corresponding file.
 * As a result, reads for MWClasses will NEVER return null.
 * But this will "corrupt" the MWClassRepository - see
 * MWClassRepository.findNewTypes(Session) for the fix.
 */
final class StubClassOverridePolicy50 extends DynamicClassOverridePolicy50 {
	/** the name of the element that indicates whether a MWClass is a "stub" */
	private String stubElementName;

	/** the value of the stub element if it is null */
	private Object stubNullValue;

	
	/**
	 * public constructor: we require an XMLFileAccessor because
	 * we will use it to determine whether a MWClass's file exists
	 * during read operations
	 */
	StubClassOverridePolicy50(MWAccessor50 parentAccessor, Session session, String rootElementName, String stubElementName, Object stubNullValue) {
		super(parentAccessor, session, rootElementName);
		this.stubElementName = stubElementName;
		this.stubNullValue = stubNullValue;
	}
	
	/**
	 * return true if the MWClass is a "stub"; this will only work for
	 * inserts and updates, where the "stub" field is populated;
	 * but we need to do something different for getReadStream();
	 * and deletes are unsupported...
	 */
	boolean overridesStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
		return super.overridesStream(rootElementName, row, orderedPrimaryKeyElements)
				&& this.rowIsForStubClass(row);
	}
	
	private boolean rowIsForStubClass(Record row) {
		Object stub = row.get(this.stubElementName);
		if (stub == null) {
			stub = this.stubNullValue;
		}
		return this.convertToBoolean(stub);
	}
	
	private boolean convertToBoolean(Object object) {
		return ((Boolean) ConversionManager.getDefaultManager().convertObject(object, Boolean.class)).booleanValue();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.persistence.MWAccessorOverridePolicy#getExistingWriteStream(java.lang.String, org.eclipse.persistence.publicinterface.DatabaseRow, java.util.Vector)
	 */
	public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		if (this.prepsExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements)) {
			// if we are trying to update a NON-stub class, make sure a file exists...
			this.getParentMWAccessor().prepForUpdate(this.file(rootElementName, row, orderedPrimaryKeyElements));
		}
		return super.getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements);
	}

	/**
	 * if we are trying to update an XML file for a "non-stub"
	 * class, make sure the file actually exists beforehand
	 */
	private boolean prepsExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
		return rootElementName.equals(this.getRootElementName())
				&& ! this.rowIsForStubClass(row);
	}
	
	/**
	 * dynamically build the appropriate XML read stream in memory if appropriate
	 */
	public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		if (this.overridesReadStream(rootElementName, row, orderedPrimaryKeyElements)) {
			return this.buildReadStream(rootElementName, row, orderedPrimaryKeyElements);
		} else {
			throw new UnsupportedAccessorOperationException50();
		}
	}
	
	/**
	 * if the XML file to be read does NOT exist, then this override
	 * is in effect and will dynamically build the XML for a "stub" of
	 * the requested MWClass
	 */
	private boolean overridesReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
		// is there a cleaner way to do this???
		return rootElementName.equals(this.getRootElementName())
			&& this.fileIsMissing(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * return whether the corresponding file is missing
	 */
	private boolean fileIsMissing(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
		// is there a cleaner way to do this???
		return ! this.file(rootElementName, row, orderedPrimaryKeyElements).exists();
	}
	
	/**
	 * return the corresponding file
	 */
	private File file(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
		// is there a cleaner way to do this???
		return this.getParentMWAccessor().getFilePolicy(rootElementName).getFile(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * since the constructor takes only a MWAccessor, we
	 * can cast it here without fear
	 */
	private MWAccessor50 getParentMWAccessor() {
		return (MWAccessor50) this.getParentAccessor();
	}
	
	/**
	 * build a MWClass that will be used to generate XML;
	 * here we only need a "stub" MWClass
	 */
	 MWClass typeNamed(String className) throws XMLDataStoreException {
		return this.getProject().typeNamed(className);
	}

}

