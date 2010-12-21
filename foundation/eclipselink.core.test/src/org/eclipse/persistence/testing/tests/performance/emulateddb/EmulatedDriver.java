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
package org.eclipse.persistence.testing.tests.performance.emulateddb;

import java.sql.*;

/**
 * Emulated database driver.
 */
public class EmulatedDriver implements Driver {
    static {
        try {
            DriverManager.registerDriver(new EmulatedDriver());
        } catch (Exception ignore) {}
    }
    
    /** Allow toggling of emulation. */
    public static boolean emulate = true;
    
    /** Cache the connection. */
    protected Connection connection;
    
    /**
     * Attempts to make a database connection to the given URL.
     * The driver should return "null" if it realizes it is the wrong kind
     * of driver to connect to the given URL.  This will be common, as when
     * the JDBC driver manager is asked to connect to a given URL it passes
     * the URL to each loaded driver in turn.
     *
     * <P>The driver should throw an <code>SQLException</code> if it is the right
     * driver to connect to the given URL but has trouble connecting to
     * the database.
     *
     * <P>The <code>java.util.Properties</code> argument can be used to pass
     * arbitrary string tag/value pairs as connection arguments.
     * Normally at least "user" and "password" properties should be
     * included in the <code>Properties</code> object.
     *
     * @param url the URL of the database to which to connect
     * @param info a list of arbitrary string tag/value pairs as
     * connection arguments. Normally at least a "user" and
     * "password" property should be included.
     * @return a <code>Connection</code> object that represents a
     *         connection to the URL
     * @exception SQLException if a database access error occurs
     */
    public Connection connect(String url, java.util.Properties info) throws SQLException {
        if (! acceptsURL(url)) {
            return null;
        }
        if (connection == null) {
            connection = new EmulatedConnection(DriverManager.getConnection(url.substring("emulate:".length(), url.length()), info));
        }
        return connection;
    }

    /**
     * Retrieves whether the driver thinks that it can open a connection
     * to the given URL.  Typically drivers will return <code>true</code> if they
     * understand the subprotocol specified in the URL and <code>false</code> if
     * they do not.
     *
     * @param url the URL of the database
     * @return <code>true</code> if this driver understands the given URL;
     *         <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    public boolean acceptsURL(String url) {
        return url.indexOf("emulate:") != -1;
    }

    /**
     * Gets information about the possible properties for this driver.
     * <P>
     * The <code>getPropertyInfo</code> method is intended to allow a generic
     * GUI tool to discover what properties it should prompt
     * a human for in order to get
     * enough information to connect to a database.  Note that depending on
     * the values the human has supplied so far, additional values may become
     * necessary, so it may be necessary to iterate though several calls
     * to the <code>getPropertyInfo</code> method.
     *
     * @param url the URL of the database to which to connect
     * @param info a proposed list of tag/value pairs that will be sent on
     *          connect open
     * @return an array of <code>DriverPropertyInfo</code> objects describing
     *          possible properties.  This array may be an empty array if
     *          no properties are required.
     * @exception SQLException if a database access error occurs
     */
    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) {
        return null;
    }

    /**
     * Retrieves the driver's major version number. Initially this should be 1.
     *
     * @return this driver's major version number
     */
    public int getMajorVersion() {
        return 1;
    }

    /**
     * Gets the driver's minor version number. Initially this should be 0.
     * @return this driver's minor version number
     */
    public int getMinorVersion() {
        return 0;
    }

    /**
     * Reports whether this driver is a genuine JDBC
     * Compliant<sup><font size=-2>TM</font></sup> driver.
     * A driver may only report <code>true</code> here if it passes the JDBC
     * compliance tests; otherwise it is required to return <code>false</code>.
     * <P>
     * JDBC compliance requires full support for the JDBC API and full support
     * for SQL 92 Entry Level.  It is expected that JDBC compliant drivers will
     * be available for all the major commercial databases.
     * <P>
     * This method is not intended to encourage the development of non-JDBC
     * compliant drivers, but is a recognition of the fact that some vendors
     * are interested in using the JDBC API and framework for lightweight
     * databases that do not support full database functionality, or for
     * special databases such as document information retrieval where a SQL
     * implementation may not be feasible.
     * @return <code>true</code> if this driver is JDBC Compliant; <code>false</code>
     *         otherwise
     */
    public boolean jdbcCompliant() {
        return true;
    }
}
