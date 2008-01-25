/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;

/**
 * Represents an object that may appear in an XML project hierarchy
 */
public interface MWXmlNode
	extends MWNode
{
	// **************** Model synchronization *********************************
	
	/** Resolve my xpaths */
	void resolveXpaths();
	
	/** A schema has changed.  Update or resolve my xpaths. */
	void schemaChanged(SchemaChange change);
}
