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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db;

/**
 * Interface describing the meta-data describing a database foreign key
 * column pair required by the TopLink Mapping Workbench.
 * @see ExternalForeignKey
 * 
 */
public interface ExternalForeignKeyColumnPair {

	/**
	 * Return the "source" half of the column pair. This is the column
	 * on the source table that must contain a value that matches
	 * the value in the target column.
	 */
	ExternalColumn getSourceColumn();

	/**
	 * Return the "target" half of the column pair. This is typically
	 * a column that is part of the target table's primary key.
	 */
	ExternalColumn getTargetColumn();

}
