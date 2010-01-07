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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ResultSetIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This external database derives all of its metadata from a client-
 * supplied JDBC connection.
 * 
 * All the JDBC implementation classes have an embarassing
 * preponderance of "defensive" programming that allow us to
 * work with even the most misbehaved of JDBC drivers. This is
 * because most of the exceptions encountered are the result
 * of bugs in the wide variety of JDBC drivers we interact with,
 * not bugs in Workbench code. (At least that's the hope....)
 * An alternative to the various bits of defensive programming,
 * we could pass in a listener whenever there is the possibility
 * of an (SQL) exception. The listener would be notified of any
 * exceptions, but we would continue processing with pre-
 * determined defaults.
 * ~bjv
 */
final class JDBCExternalDatabase implements ExternalDatabase {
	private final Connection connection;
	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	/**
	 * 
	 */
	JDBCExternalDatabase(Connection connection) {
		super();
		this.connection = connection;
	}


	// ********** ExternalDatabase implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase#getCatalogNames()
	 */
	public String[] getCatalogNames() {
		ResultSet resultSet;
		try {
			resultSet = this.metaData().getCatalogs();
		} catch (SQLException ex) {
			return EMPTY_STRING_ARRAY;		// defensive - this is not fatal if unsupported by driver
		}
		return this.buildStringArray(resultSet);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase#getSchemaNames()
	 */
	public String[] getSchemaNames() {
		ResultSet resultSet;
		try {
			resultSet = this.metaData().getSchemas();
		} catch (SQLException ex) {
			return EMPTY_STRING_ARRAY;		// defensive - this is not fatal if unsupported by driver
		}
		return this.buildStringArray(resultSet);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase#getTableTypeNames()
	 */
	public String[] getTableTypeNames() {
		ResultSet resultSet;
		try {
			resultSet = this.metaData().getTableTypes();
		} catch (SQLException ex) {
			return EMPTY_STRING_ARRAY;		// defensive - this is not fatal if unsupported by driver
		}
		return this.buildStringArray(resultSet);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase#getTableDescriptions(String, String, String, String[])
	 */
	public ExternalTableDescription[] getTableDescriptions(String catalogName, String schemaNamePattern, String tableNamePattern, String[] tableTypeNames) {
		// #setCatalog(String) is used by Sybase, MS SQL Server, and others(?)
		// to set a "local context" for later interactions with the server;
		// "If the driver does not support catalogs, it will silently ignore this request."
		if (catalogName != null) {
			try {
				this.getConnection().setCatalog(catalogName);
			} catch (SQLException ex) {
				// see comment above...
				throw new RuntimeException(ex);
			}
		}

		ResultSet resultSet;
		try {
			resultSet = this.metaData().getTables(catalogName, schemaNamePattern, tableNamePattern, tableTypeNames);
		} catch (SQLException ex) {
			// if #getTables() does not work, there's not much we can do...
			throw new RuntimeException(ex);
		}
		List tableDescriptions = CollectionTools.list(new ResultSetIterator(resultSet, new ExternalTableDescriptionResultSetAdapter()));
		return (ExternalTableDescription[]) tableDescriptions.toArray(new ExternalTableDescription[tableDescriptions.size()]);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase#getTableDescriptions()
	 */
	public ExternalTableDescription[] getTableDescriptions() {
		// query for *all* the tables on the database - could be painful...
		return this.getTableDescriptions(null, null, "%", null);
	}


	// ********** queries **********

	private Connection getConnection() {
		if (this.connection == null) {
			throw new IllegalStateException("not connected");
		}
		return this.connection;
	}

	DatabaseMetaData metaData() {
		try {
			return this.getConnection().getMetaData();
		} catch (SQLException ex) {
			// if we can't get meta-data, there's not much we can do...
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this);
	}


	// ********** behavior **********

	/**
	 * convert the specified single-column result set to an array of strings
	 */
	private String[] buildStringArray(ResultSet resultSet) {
		List strings = CollectionTools.list(new ResultSetIterator(resultSet, new StringResultSetAdapter()));
		return (String[]) strings.toArray(new String[strings.size()]);
	}


	// ********** inner classes **********

	/**
	 * Trim a single-column result set of strings.
	 */
	private static class StringResultSetAdapter implements ResultSetIterator.Adapter {
		public Object buildNext(ResultSet rs) throws SQLException {
			// result set column indexes are 1-based
			String string = rs.getString(1);
			// trim all the strings - some databases return fixed-length strings
			return (string == null) ? null : string.trim();
		}
	}

	/**
	 * Convert the rows into external table descriptions.
	 * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 */
	private class ExternalTableDescriptionResultSetAdapter implements ResultSetIterator.Adapter {
		public Object buildNext(ResultSet rs) throws SQLException {
			return new JDBCExternalTableDescription(rs, JDBCExternalDatabase.this);
		}
	}

}
