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
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.*;

import deprecated.xml.XMLDataStoreException;
/**
 * override some stuff:
 *   the file extension
 *   the relative directory location
 */
class MWAccessor45 extends MWXMLFileAccessor45 implements LegacyMWAccessor4X {
	/** Support for storing some documents in a different directory, 
		with a different file extension */
	private Vector rootElementNameOverrides;
	private String overrideFileExtension;


/**
 * Constructor.
 */
MWAccessor45() {
	super();
}

/**
 * Add the name of an XML root element whose
 * documents will be stored in a non-standard directory
 * and whose files that have a non-standard file extension (.mwp).
 */
void addRootElementNameOverride(String name) {
	this.getRootElementNameOverrides().addElement(name);
}

/**
 * Return the directory that holds all the documents
 * with the specified root element name.
 */
public File buildDocumentDirectory(String rootElementName) throws XMLDataStoreException {
	if (this.getRootElementNameOverrides().contains(rootElementName)) {
		return this.overrideBuildDocumentDirectory();
	} else {
		return super.buildDocumentDirectory(rootElementName);
	}
}

/**
 * Return the file extension that will be
 * appended to the primary key element value(s) to generate the
 * complete file name.
 */
public String getFileExtension(String rootElementName) {
	if (this.getRootElementNameOverrides().contains(rootElementName)) {
		return this.getOverrideFileExtension();
	} else {
		return super.getFileExtension(rootElementName);
	}
}

/**
 * Return the non-standard file extension (.mwp).
 */
String getOverrideFileExtension() {
	return overrideFileExtension;
}

/**
 * Return the names of an XML root elements whose
 * documents will be stored in a non-standard directory
 * and whose files that have a non-standard file extension (.mwp).
 */
Vector getRootElementNameOverrides() {
	return rootElementNameOverrides;
}

/**
 *	Initialize the accessor.
 */
protected void initialize() {
	super.initialize();
	rootElementNameOverrides = new Vector();
	overrideFileExtension = "";
}

/**
 * Return the non-standard directory for storing
 * XML documents.
 */
protected File overrideBuildDocumentDirectory() throws XMLDataStoreException {
	File directory = this.getBaseDirectory();
	this.checkDirectory(directory);
	return directory;
}

/**
 * Set the non-standard file extension (.mwp).
 */
void setOverrideFileExtension(String overrideFileExtension) {
	this.overrideFileExtension = overrideFileExtension;
}

}
