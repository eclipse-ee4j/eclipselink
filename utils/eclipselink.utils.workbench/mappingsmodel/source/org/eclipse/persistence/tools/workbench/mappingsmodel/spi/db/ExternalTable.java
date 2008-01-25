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
 * Interface defining the meta-data	required by the TopLink
 * Mapping Workbench.
 * @see ExternalTableDescription
 */
public interface ExternalTable {

	/**
	 * Return an array of the table's columns.
	 */
	ExternalColumn[] getColumns();

	/**
	 * Return an array of the table's foreign keys.
	 */
	ExternalForeignKey[] getForeignKeys();

}
