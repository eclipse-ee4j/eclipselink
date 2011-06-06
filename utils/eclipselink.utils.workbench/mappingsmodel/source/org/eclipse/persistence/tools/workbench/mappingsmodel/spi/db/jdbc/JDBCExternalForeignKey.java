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

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * 
 */
final class JDBCExternalForeignKey implements ExternalForeignKey {
	private final JDBCExternalTable table;
	private final String name;
	private final ExternalTableDescription targetTableDescription;
	private ExternalForeignKeyColumnPair[] columnPairs;		// pseudo-final


	// ********** constructor/initialization **********

	/**
	 * Construct a "skeleton" external foreign key with the data from the current
	 * row of the specified result set. The column pairs will be added later.
	 * @see java.sql.DatabaseMetaData#getImportedKeys(String, String, String)
	 */
	JDBCExternalForeignKey(JDBCExternalTable table, String name, ResultSet resultSet) {
		super();
		this.table = table;
		this.name = name;
		this.targetTableDescription = new JDBCExternalTableDescription(
				this.stringFrom(resultSet, 1),		// PKTABLE_CAT
				this.stringFrom(resultSet, 2),		// PKTABLE_SCHEM
				this.stringFrom(resultSet, 3),		// PKTABLE_NAME
				this.database()
			);
		// the column pairs will be added by the table, momentarily
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


	// ********** ExternalForeignKey implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey#getColumnPairs()
	 */
	public ExternalForeignKeyColumnPair[] getColumnPairs() {
		return this.columnPairs;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey#getTargetTableDescription()
	 */
	public ExternalTableDescription getTargetTableDescription() {
		return this.targetTableDescription;
	}


	// ********** behavior **********

	void addColumnPair(ResultSet resultSet) {
		int len = (this.columnPairs == null) ? 0 : this.columnPairs.length;
		JDBCExternalForeignKeyColumnPair[] temp = new JDBCExternalForeignKeyColumnPair[len + 1];
		if (len != 0) {
			System.arraycopy(this.columnPairs, 0, temp, 0, len);
		}
		temp[len] = new JDBCExternalForeignKeyColumnPair(this, resultSet);
		this.columnPairs = temp;
	}


	// ********** queries **********

	JDBCExternalColumn columnNamed(String columnName) {
		return this.table.columnNamed(columnName);
	}

	private JDBCExternalDatabase database() {
		return this.table.database();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.name);
	}

}
