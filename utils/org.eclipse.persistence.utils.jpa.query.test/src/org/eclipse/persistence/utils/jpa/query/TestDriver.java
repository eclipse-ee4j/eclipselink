/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A fake JDBC driver so that EclipseLink can think it can connect to a database.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class TestDriver implements Driver {

	/**
	 * {@inheritDoc}
	 */
	public boolean acceptsURL(String url) throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection connect(String url, Properties info) throws SQLException {
		return new TestConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMajorVersion() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMinorVersion() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean jdbcCompliant() {
		return true;
	}
}