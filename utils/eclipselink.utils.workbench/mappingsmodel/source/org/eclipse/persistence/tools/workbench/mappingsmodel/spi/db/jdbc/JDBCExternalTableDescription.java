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

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * 
 */
final class JDBCExternalTableDescription implements ExternalTableDescription {
	private final JDBCExternalDatabase database;
	private final String catalogName;
	private final String schemaName;
	private final String name;
	private final String qualifiedName;	// cache the qualified name, since it will not change
	private ExternalTable externalTable;		// pseudo-final


	// ********** constructor/initialization **********

	/**
	 * Construct an external table description from the current row
	 * in the specified result set. The result set corresponds to the
	 * result set returned from DatabaseMetaData#getTables(String, String, String, String[]).
	 * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 */
	JDBCExternalTableDescription(ResultSet resultSet, JDBCExternalDatabase database) throws SQLException {
		this(
			// trim all the strings - some databases return fixed-length strings;
			// these calls *shouldn't* throw any SQL exceptions...
			trim(resultSet.getString(1)),		// TABLE_CAT
			trim(resultSet.getString(2)),		// TABLE_SCHEM
			trim(resultSet.getString(3)),		// TABLE_NAME
			database
		);
	}

	JDBCExternalTableDescription(String catalogName, String schemaName, String name, JDBCExternalDatabase database) {
		super();
		this.catalogName = catalogName;
		this.schemaName = schemaName;
		this.name = name;
		this.qualifiedName = this.buildQualifiedName();
		this.database = database;
	}

	/**
	 * trim down the specified string, to null if necessary
	 */
	private static String trim(String s) {
		if (s == null) {
			return null;
		}
		s = s.trim();
		return (s.length() == 0) ? null : s;
	}

	private String buildQualifiedName() {
		return NameTools.buildQualifiedDatabaseObjectName(this.catalogName, this.schemaName, this.name);
	}


	// ********** ExternalTableDescription implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription#getCatalogName()
	 */
	public String getCatalogName() {
		return this.catalogName;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription#getSchemaName()
	 */
	public String getSchemaName() {
		return this.schemaName;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription#getQualifiedName()
	 */
	public String getQualifiedName() {
		return this.qualifiedName;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription#getTable()
	 */
	public ExternalTable getTable() {
		if (this.externalTable == null) {
			this.externalTable = this.buildExternalTable();
		}
		return this.externalTable;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.TableDescription#getAdditionalInfo()
	 */
	public String getAdditionalInfo() {
		try {
			return this.metaData().getURL();
		} catch (SQLException ex) {
			return null;
		}
	}


	// ********** behavior **********

	private ExternalTable buildExternalTable() {
		try {
			return new JDBCExternalTable(this);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}


	// ********** queries **********

	DatabaseMetaData metaData() {
		return this.database.metaData();
	}

	JDBCExternalDatabase getDatabase() {
		return this.database;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.qualifiedName);
	}

}
