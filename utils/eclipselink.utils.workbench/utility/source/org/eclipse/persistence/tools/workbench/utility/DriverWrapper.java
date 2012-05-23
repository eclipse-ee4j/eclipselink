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
package org.eclipse.persistence.tools.workbench.utility;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This implementation of the JDBC Driver interface delegates all calls
 * to a "real" JDBC driver. This wrapper simplifies the dynamic loading of a
 * JDBC driver that is not on the "Java" classpath (the classpath set at
 * the time the JVM starts up). This is because, if you use a URL class loader
 * to load a driver directly, the DriverManager will not allow your code
 * access to the driver. For security reasons, the driver must be loaded
 * by the same class loader chain that loaded the code that calls
 * DriverManager.getConnection().
 */
public class DriverWrapper implements Driver {
	/** the "real" JDBC driver */
	private Driver driver;


	// ********** constructors **********

	/**
	 * Wrap the specified driver.
	 */
	public DriverWrapper(Driver driver) {
		super();
		this.driver = driver;
	}

	/**
	 * Wrap the driver for the specified driver class.
	 */
	public DriverWrapper(Class driverClass)
		throws InstantiationException, IllegalAccessException
	{
		this((Driver) driverClass.newInstance());
	}

	/**
	 * Wrap the driver for the specified driver class and class loader
	 * that can load the driver.
	 */
	public DriverWrapper(String driverClassName, ClassLoader classLoader)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		this(Class.forName(driverClassName, true, classLoader));
	}

	/**
	 * Wrap the driver for the specified driver class and classpath
	 * entries required to load the driver.
	 */
	public DriverWrapper(String driverClassName, URL[] driverClasspath)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		this(driverClassName, new URLClassLoader(driverClasspath));
	}

	/**
	 * Wrap the driver for the specified driver class and classpath
	 * entry required to load the driver.
	 */
	public DriverWrapper(String driverClassName, URL driverClasspathEntry)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		this(driverClassName, new URL[] {driverClasspathEntry});
	}


	// ********** Driver implementation **********

	/**
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public Connection connect(String url, Properties info) throws SQLException {
		return this.driver.connect(url, info);
	}

	/**
	 * @see java.sql.Driver#acceptsURL(String)
	 */
	public boolean acceptsURL(String url) throws SQLException {
		return this.driver.acceptsURL(url);
	}

	/**
	 * @see java.sql.Driver#getPropertyInfo(String, java.util.Properties)
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return this.driver.getPropertyInfo(url, info);
	}

	/**
	 * @see java.sql.Driver#getMajorVersion()
	 */
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	/**
	 * @see java.sql.Driver#getMinorVersion()
	 */
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	/**
	 * @see java.sql.Driver#jdbcCompliant()
	 */
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

}
