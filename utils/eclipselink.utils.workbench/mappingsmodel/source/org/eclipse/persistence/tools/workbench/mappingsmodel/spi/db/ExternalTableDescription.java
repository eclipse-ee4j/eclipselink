/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db;

/**
 * Interface defining a lightweight wrapper around the
 * database metadata required by the TopLink Mapping Workbench.
 * 
 * @see ExternalDatabase
 */
public interface ExternalTableDescription extends TableDescription {

	/**
	 * Returns the ExternalTable object corresponding to this
	 * ExternalTableDescription object.
	 * This allows the ExternalTableDescription object to postpone
	 * loading extra meta-data until it is actually needed by the
	 * TopLink Mapping Workbench. The name and
	 * description are required beforehand because
	 * they are used by the user to select an ExternalTable
	 * to load.
	 */
	ExternalTable getTable();

}
