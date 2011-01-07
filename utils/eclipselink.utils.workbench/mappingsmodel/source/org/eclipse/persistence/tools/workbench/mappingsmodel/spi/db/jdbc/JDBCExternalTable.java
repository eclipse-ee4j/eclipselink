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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTable;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * 
 * see comment about defensive programming at JDBCExternalDatabase
 */
final class JDBCExternalTable implements ExternalTable {
	private final JDBCExternalTableDescription tableDescription;
	private final JDBCExternalColumn[] columns;
	private final ExternalForeignKey[] foreignKeys;

	private static final JDBCExternalForeignKey[] EMPTY_FOREIGN_KEYS = new JDBCExternalForeignKey[0];


	// ********** constructor/initialization **********

	/**
	 * 
	 */
	JDBCExternalTable(JDBCExternalTableDescription tableDescription) throws SQLException {
		super();
		this.tableDescription = tableDescription;
		this.columns = this.buildColumns();
		this.markPrimaryKeyColumns();
		this.foreignKeys = this.buildForeignKeys();
	}

	/**
	 * query the database for the table's columns
	 * @see java.sql.DatabaseMetaData#getColumns(String, String, String, String)
	 */
	private JDBCExternalColumn[] buildColumns() throws SQLException {
		Collection cols = new ArrayList();
		ResultSet resultSet = this.metaData().getColumns(
				this.tableDescription.getCatalogName(),
				this.tableDescription.getSchemaName(),
				this.tableDescription.getName(),
				null
			);
		while (resultSet.next()) {
			cols.add(new JDBCExternalColumn(resultSet));
		}
		resultSet.close();
		return (JDBCExternalColumn[]) cols.toArray(new JDBCExternalColumn[cols.size()]);
	}

	private void markPrimaryKeyColumns() {
		Collection pkColNames = this.primaryKeyColumnNames();
		for (int i = this.columns.length; i-- > 0; ) {
			JDBCExternalColumn column = this.columns[i];
			column.setPrimaryKey(pkColNames.contains(column.getName()));
		}
	}

	/**
	 * defensive wrapper
	 */
	private Collection primaryKeyColumnNames() {
		try {
			return this.primaryKeyColumnNames2();
		} catch (SQLException ex) {
			// defensive - this is not fatal if unsupported by driver
		}
		return Collections.EMPTY_SET;
	}

	/**
	 * return the names of the table's primary key columns
	 * @see java.sql.DatabaseMetaData#getPrimaryKeys(String, String, String)
	 */
	private Collection primaryKeyColumnNames2() throws SQLException {
		Collection names = new HashSet();
		ResultSet resultSet = this.metaData().getPrimaryKeys(
				this.tableDescription.getCatalogName(),
				this.tableDescription.getSchemaName(),
				this.tableDescription.getName()
			);
		while (resultSet.next()) {
			// trim all the strings - some databases return fixed-length strings
			names.add(resultSet.getString(4).trim());		// COLUMN_NAME
		}
		resultSet.close();
		return names;
	}

	/**
	 * defensive wrapper
	 */
	private JDBCExternalForeignKey[] buildForeignKeys() {
		try {
			return this.buildForeignKeys2();
		} catch (SQLException ex) {
			// defensive - this is not fatal if unsupported by driver
		}
		return EMPTY_FOREIGN_KEYS;
	}

	/**
	 * query the database for the table's foreign keys
	 * @see java.sql.DatabaseMetaData#getImportedKeys(String, String, String)
	 */
	private JDBCExternalForeignKey[] buildForeignKeys2() throws SQLException {
		Collection fKeys = new ArrayList();
		ResultSet resultSet = this.metaData().getImportedKeys(
				this.tableDescription.getCatalogName(),
				this.tableDescription.getSchemaName(),
				this.tableDescription.getName()
			);
		while (resultSet.next()) {
			String fKeyName = resultSet.getString(12).trim();		// FK_NAME
			JDBCExternalForeignKey fKey = this.foreignKeyNamed(fKeyName, fKeys);
			if (fKey == null) {
				fKey = new JDBCExternalForeignKey(this, fKeyName, resultSet);
				fKeys.add(fKey);
			}
			fKey.addColumnPair(resultSet);
		}
		resultSet.close();
		return (JDBCExternalForeignKey[]) fKeys.toArray(new JDBCExternalForeignKey[fKeys.size()]);
	}

	/**
	 * search the specified collection of foreign keys for the foreign
	 * key with the specified name; assume(?) that foreign keys with
	 * the same name have the same target table
	 */
	private JDBCExternalForeignKey foreignKeyNamed(String fKeyName, Collection fKeys) {
		for (Iterator stream = fKeys.iterator(); stream.hasNext(); ) {
			JDBCExternalForeignKey fKey = (JDBCExternalForeignKey) stream.next();
			if (fKey.getName().equals(fKeyName)) {
				return fKey;
			}
		}
		return null;
	}


	// ********** ExternalTable implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTable#getColumns()
	 */
	public ExternalColumn[] getColumns() {
		return this.columns;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTable#getForeignKeys()
	 */
	public ExternalForeignKey[] getForeignKeys() {
		return this.foreignKeys;
	}


	// ********** queries **********

	private DatabaseMetaData metaData() {
		return this.tableDescription.metaData();
	}

	JDBCExternalDatabase database() {
		return this.tableDescription.getDatabase();
	}

	JDBCExternalColumn columnNamed(String columnName) {
		for (int i = this.columns.length; i-- > 0; ) {
			JDBCExternalColumn column = this.columns[i];
			if (column.getName().equals(columnName)) {
				return column;
			}
		}
		return null;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.tableDescription.getQualifiedName());
	}

}
