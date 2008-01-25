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

import deprecated.xml.DefaultXMLFileAccessorFilePolicy;
import deprecated.xml.XMLDataStoreException;
import deprecated.xml.XMLFileAccessorFilePolicy;

/**
 * This file policy allows the base project file to be in a non-standard
 * directory (the base directory, instead of a sub-directory) and to
 * have a non-standard file extension (.mwp, instead of .xml).
 */
class MWProjectXMLFilePolicy50 extends DefaultXMLFileAccessorFilePolicy implements XMLFileAccessorFilePolicy {


/**
 * Default constructor.
 */
private MWProjectXMLFilePolicy50() {
	super();
}

/**
 * Public constructor.
 */
MWProjectXMLFilePolicy50(File projectDirectory, String projectFileNameExtension) {
	this();
	this.initialize(projectDirectory, projectFileNameExtension);
}

protected void initialize(File projectDirectory, String projectFileNameExtension) {
	this.setBaseDirectory(projectDirectory);
	this.setFileNameExtension(projectFileNameExtension);
}

/**
 * Override to simply return the base directory.
 */
protected File buildDocumentDirectoryUnchecked(String rootElementName) throws XMLDataStoreException {
	return this.getBaseDirectory();
}

}