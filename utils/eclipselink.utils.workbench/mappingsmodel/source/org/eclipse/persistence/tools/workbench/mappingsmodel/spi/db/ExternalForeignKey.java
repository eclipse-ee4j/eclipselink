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
 * Interface describing the meta-data describing a database foreign key
 * required by the TopLink Mapping Workbench.
 * @see ExternalTable
 */
public interface ExternalForeignKey {

	/**
	 * Return the foreign key's name. Often this name is system-generated.
	 */
	String getName();

	/**
	 * Return a description of the foreign key's target table.
	 */
	ExternalTableDescription getTargetTableDescription();

	/**
	 * Return an array of the foreign key's column pairs. These pairs match
	 * up a foreign key column on the source table with a primary key column
	 * on the target table.
	 */
	ExternalForeignKeyColumnPair[] getColumnPairs();

}
