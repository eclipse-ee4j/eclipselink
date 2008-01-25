/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.jdbc;

import java.sql.Connection;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabaseFactory;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Not much to say here: This is a factory for generating instances of
 * the "JDBC" implementation of ExternalDatabase, which uses a
 * java.sql.Connection ExternalTables etc.
 */
public final class JDBCExternalDatabaseFactory
	implements ExternalDatabaseFactory
{

	/** the singleton */
	private static ExternalDatabaseFactory INSTANCE;


	/**
	 * Singleton support.
	 */
	public static synchronized ExternalDatabaseFactory instance() {
		if (INSTANCE == null) {
			INSTANCE = new JDBCExternalDatabaseFactory();
		}
		return INSTANCE;
	}

	/**
	 * Private constructor - use the singleton.
	 */
	private JDBCExternalDatabaseFactory() {
		super();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabaseFactory#buildDatabase(java.sql.Connection)
	 */
	public ExternalDatabase buildDatabase(Connection connection) {
		return new JDBCExternalDatabase(connection);
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, "singleton");
	}

}
