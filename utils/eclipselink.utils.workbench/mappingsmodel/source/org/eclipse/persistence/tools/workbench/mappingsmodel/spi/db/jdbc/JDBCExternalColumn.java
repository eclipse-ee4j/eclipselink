/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * 
 * see comment about defensive programming at JDBCExternalDatabase
 */
final class JDBCExternalColumn implements ExternalColumn {
	private final String name;
	private final String typeName;
	private final int jdbcTypeCode;
	private final int size;
	private final int scale;	// used by fixed point numbers only
	private final boolean nullable;
	private boolean primaryKey;	// pseudo-final


	// ********** constructor/initialization **********

	/**
	 * Construct an external column with the data from the current
	 * row of the specified result set.
	 * @see java.sql.DatabaseMetaData#getColumns(String, String, String, String)
	 */
	JDBCExternalColumn(ResultSet resultSet) throws SQLException {
		super();

		// the name is required, the other settings can be fudged
		this.name = resultSet.getString(4).trim();		// COLUMN_NAME
		if (this.name.length() == 0) {
			throw new IllegalStateException("empty column name");
		}

		this.typeName = this.typeNameFrom(resultSet);
		this.jdbcTypeCode = this.jdbcTypeCodeFrom(resultSet);
		this.size = this.sizeFrom(resultSet);
		this.scale = this.scaleFrom(resultSet);
		this.nullable = this.nullableFrom(resultSet);

		// 'primaryKey' will be set later by the table
	}

	private String typeNameFrom(ResultSet resultSet) {
		try {
			return this.trim(resultSet.getString(6));		// TYPE_NAME
		} catch (SQLException ex) {
			// defensive - return null if TYPE_NAME is not supported by the driver
			return null;
		}
	}

	private int jdbcTypeCodeFrom(ResultSet resultSet) {
		try {
			return resultSet.getInt(5);		// DATA_TYPE
		} catch (SQLException ex) {
			// defensive - return NULL(?) if DATA_TYPE is not supported by the driver
			return Types.NULL;
		}
	}

	private int sizeFrom(ResultSet resultSet) {
		try {
			return resultSet.getInt(7);		// COLUMN_SIZE
		} catch (SQLException ex) {
			// defensive - return 0 if COLUMN_SIZE is not supported by the driver
			return 0;
		}
	}

	private int scaleFrom(ResultSet resultSet) {
		try {
			return resultSet.getInt(9);		// DECIMAL_DIGITS
		} catch (SQLException ex) {
			// defensive - return 0 if DECIMAL_DIGITS is not supported by the driver
			return 0;
		}
	}

	/**
	 * use IS_NULLABLE, since NULLABLE is pretty much useless;
	 * confusing... see the JDK JavaDocs...
	 */
	private boolean nullableFrom(ResultSet resultSet) {
		String jdbc_IS_NULLABLE = null;
		try {
			jdbc_IS_NULLABLE = resultSet.getString(18);		// IS_NULLABLE
		} catch (SQLException ex) {
			// defensive - continue with 'null' if IS_NULLABLE is not supported by the driver
		}

		// trim all the strings - some databases return fixed-length strings
		// if this is "NO", the column is *definitely* required (NOT NULL);
		// if this is not "NO", we're not sure - drop through and return true
		if ((jdbc_IS_NULLABLE != null) && jdbc_IS_NULLABLE.trim().toUpperCase().equals("NO")) {
			return false;
		}

		// NULLABLE (column 11) cannot tell us anything *definite* except that
		// the column allows null (NULL); so just return true
		return true;
	}

	/**
	 * trim down the specified string, to null if necessary
	 */
	private String trim(String s) {
		if (s == null) {
			return null;
		}
		s = s.trim();
		return (s.length() == 0) ? null : s;
	}


	// ********** ExternalColumn implementation **********

	/**
	 * the name should never be null or empty
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * the type name can be null if we have problems reading the result set
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#getTypeName()
	 */
	public String getTypeName() {
		return this.typeName;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#getJDBCTypeCode()
	 */
	public int getJDBCTypeCode() {
		return this.jdbcTypeCode;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#getSize()
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#getScale()
	 */
	public int getScale() {
		return this.scale;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#isNullable()
	 */
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn#isPrimaryKey()
	 */
	public boolean isPrimaryKey() {
		return this.primaryKey;
	}


	// ********** queries **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.name);
	}


	// ********** behavior **********

	/**
	 * the primary key flag is set by the table soon after construction
	 */
	void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

}
