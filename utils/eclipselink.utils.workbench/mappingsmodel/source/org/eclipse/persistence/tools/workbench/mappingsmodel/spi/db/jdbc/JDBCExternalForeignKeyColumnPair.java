/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Associate a pair of columns from a foreign key that is made
 * up of, possibly, multiple pairs of columns.
 * Either column can be null if we have any problems gathering
 * up the meta-data.
 */
final class JDBCExternalForeignKeyColumnPair implements ExternalForeignKeyColumnPair {
	private final JDBCExternalForeignKey foreignKey;
	private final ExternalColumn sourceColumn;
	private final ExternalColumn targetColumn;


	// ********** constructor/initialization **********

	/**
	 * 
	 * @see java.sql.DatabaseMetaData#getImportedKeys(String, String, String)
	 */
	JDBCExternalForeignKeyColumnPair(JDBCExternalForeignKey foreignKey, ResultSet resultSet) {
		super();
		this.foreignKey = foreignKey;
		this.sourceColumn = this.columnNamed(this.stringFrom(resultSet, 8));		// FKCOLUMN_NAME
		this.targetColumn = this.buildLocalColumn(this.stringFrom(resultSet, 4));		// PKCOLUMN_NAME
	}

	private String stringFrom(ResultSet resultSet, int colIndex) {
		try {
			return this.trim(resultSet.getString(colIndex));
		} catch (SQLException ex) {
			// defensive - return null if the requested column is not supported by the driver
			return null;
		}
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


	// ********** ExternalForeignKeyColumnPair implementation **********

	/**
	 * the source column can be null if we have problems reading the result set
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair#getSourceColumn()
	 */
	public ExternalColumn getSourceColumn() {
		return this.sourceColumn;
	}

	/**
	 * the target column can be null if we have problems reading the result set
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair#getTargetColumn()
	 */
	public ExternalColumn getTargetColumn() {
		return this.targetColumn;
	}


	// ********** queries **********

	private JDBCExternalColumn columnNamed(String columnName) {
		return this.foreignKey.columnNamed(columnName);
	}

	private ExternalColumn buildLocalColumn(String columnName) {
		return (columnName == null) ? null : new TargetColumn(columnName);
	}

	private String sourceColumnName() {
		return (this.sourceColumn == null) ? null : this.sourceColumn.getName();
	}

	private String targetColumnName() {
		return (this.targetColumn == null) ? null : this.targetColumn.getName();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.sourceColumnName() + "=>" + this.targetColumnName());
	}


	// ********** member class **********

	/**
	 * this column can only return its name;
	 * all other operations are unsupported
	 */
	private static class TargetColumn implements ExternalColumn {
		private final String name;
		TargetColumn(String name) {
			super();
			if (name == null) {
				throw new NullPointerException();
			}
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
		public int getJDBCTypeCode() {
			throw new UnsupportedOperationException();
		}
		public String getTypeName() {
			throw new UnsupportedOperationException();
		}
		public int getSize() {
			throw new UnsupportedOperationException();
		}
		public int getScale() {
			throw new UnsupportedOperationException();
		}
		public boolean isNullable() {
			throw new UnsupportedOperationException();
		}
		public boolean isPrimaryKey() {
			throw new UnsupportedOperationException();
		}
		public String toString() {
			return StringTools.buildToStringFor(this, this.name);
		}
	}

}
