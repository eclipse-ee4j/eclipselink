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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;

import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import deprecated.xml.XMLDataStoreException;
import deprecated.xml.XMLFileAccessor;

/**
 * This override policy maintains an explicit list of names of
 * "core" MWClasses that should not be written out to XML files
 * (e.g. java.lang.Object, int, org.eclipse.persistence.indirection.ValueHolderInterface).
 */
final class CoreClassOverridePolicy50 extends DynamicClassOverridePolicy50 {
	/**
	 * classes that will not be written out and will be read in
	 * from dynamically generated XML (as opposed to files)
	 */
	private Set overrideClassNames;

	
	/**
	 * public constructor
	 */
	CoreClassOverridePolicy50(XMLFileAccessor parentAccessor, Session session, String rootElementName) {
		super(parentAccessor, session, rootElementName);
	}
	
	/**
	 * return true if the MWClass is in the override list
	 */
	boolean overridesStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
		return super.overridesStream(rootElementName, row, orderedPrimaryKeyElements)
				&& overrideClassNames.contains(this.buildClassName(row, orderedPrimaryKeyElements));
	}
	
	/**
	 * dynamically build the appropriate XML read stream in memory if appropriate
	 */
	public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		if (this.overridesStream(rootElementName, row, orderedPrimaryKeyElements)) {
			// not sure whether this is appropriate:
			// delete the stream if it is present, since it is not used and could be misleading
			this.getParentAccessor().deleteStream(rootElementName, row, orderedPrimaryKeyElements);
			return this.buildReadStream(rootElementName, row, orderedPrimaryKeyElements);
		} else {
			throw new UnsupportedAccessorOperationException50();
		}
	}
	
	/**
	 * build a MWClass that will be used to generate XML
	 */
	MWClass typeNamed(String className) throws XMLDataStoreException {
		if ("org.eclipse.persistence.publicinterface.Descriptor".equals(className)) {
			className = "org.eclipse.persistence.descriptors.ClassDescriptor";
		}
		return this.getProject().typeNamed(className);
	}
	
	/**
	 * initialize the newly-created instance
	 */
	void initialize() {
		super.initialize();
		overrideClassNames = new HashSet(10000);
	}
	
	/**
	 * return the overrides
	 */
	Iterator overrideClassNames() {
		return overrideClassNames.iterator();
	}
	
	/**
	 * add the specified class name to the set of overrides
	 */
	void addOverrideClassName(String className) {
		overrideClassNames.add(className);
	}
	
	/**
	 * add the specified class names to the set of overrides
	 */
	void addOverrideClassNames(Iterator classNames) {
		while (classNames.hasNext()) {
			this.addOverrideClassName((String) classNames.next());
		}
	}
	
}
