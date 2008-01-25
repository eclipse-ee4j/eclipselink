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

/**
 * A simple interface that describes what xpaths are allowable
 * in a particular context (usually a MWXpathContext)
 */
public interface MWXpathSpec
{
	/** Return whether xpaths for this spec may use a collection of nodes */
	boolean mayUseCollectionData();
	
	/** Return whether xpaths for this spec may use a simple (text only) node */
	boolean mayUseSimpleData();
	
	/** Return whether xpaths for this spec may use a complex (non-text) node */
	boolean mayUseComplexData();
}
