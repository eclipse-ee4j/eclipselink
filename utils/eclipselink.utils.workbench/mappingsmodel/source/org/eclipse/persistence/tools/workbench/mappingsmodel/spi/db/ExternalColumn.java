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
 * Interface describing the meta-data describing a database column
 * required by the TopLink Mapping Workbench.
 * @see ExternalTable
 */
public interface ExternalColumn {

	/**
	 * Return the column's unqualified name.
	 */
	String getName();

	/**
	 * Return the name of the column's platform-specific datatype
	 * (e.g. "VARCHAR2").
	 */
	String getTypeName();

	/**
	 * Return the JDBC type code for the column's datatype
	 * (e.g. java.sql.Types.VARCHAR).
	 */
	int getJDBCTypeCode();

	/**
	 * Return the column's size. If the column does not have a size
	 * return zero.
	 */
	int getSize();

	/**
	 * Return the column's scale. If the column does not have a scale
	 * return zero. Typically a column defined as a decimal numeric
	 * datatype will have a scale.
	 */
	int getScale();

	/**
	 * Return whether the column allows null.
	 */
	boolean isNullable();

	/**
	 * Return whether the column is part of the table's primary key.
	 */
	boolean isPrimaryKey();

}
