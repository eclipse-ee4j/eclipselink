/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
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
