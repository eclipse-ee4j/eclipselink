/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import java.io.File;

import deprecated.xml.XMLDataStoreException;


/**
 * provide public interface on methods needed for backward compatibility;
 * these methods are used by various legacy accessor methods
 */
public interface LegacyMWAccessor4X {

	File buildDocumentDirectory(String rootElementName) throws XMLDataStoreException;

	String getFileExtension(String rootElementName);

}
