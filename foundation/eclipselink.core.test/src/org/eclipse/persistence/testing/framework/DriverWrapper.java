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
 *     ailitchev - Bug 256296: Reconnect fails when session loses connectivity; 
 *                 Bug 256284: Closing anEMF where the database is unavailable results in deployment exception on redeploy. 
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

/*
 * DriverWrapper works together with ConnectionWrapper.
 * This pair of classes allows to intercept calls to Driver and Connection methods.
 * DriverWrapper can imitate both the db going down (or losing network connection to it),
 * and coming back up (or network connection restored).
 * ConnectionWrapper have the same functionality, but applied to a single connection.
 * 
 * There's an example of ConnectionWrapper usage in EntityManagerJUnitTestSuite:
 * testEMCloseAndOpen and testEMFactoryCloseAndOpen.
 * 
 * To use DriverWrapper in jpa, initialize DriverWrapper - all methods are static - with the original driver name.
 * 
 * Then create EMF, using PersistenceUnit properties, substitute:
 * the original driver class for DriverWrapper (optional) and
 * the original url for "coded" (':' substituted for'*') url (otherwise DriverWrapper would not be called - the original driver would).
 * If created EMF uses DriverWrapper it will print out connection string that looks like "jdbc*oracle*thin*@localhost*1521*orcl".
 * 
 * DriverWrapper just passes all the calls to the wrapped driver (the one passed to initialize method):
 * connect method wraps the created connection into ConnectionWrapper and caches them.
 * 
 * Unless any of "break" methods called it should function in exactly the same way as original driver, the same for connections, too.
 * 
 * But of course real fun is in breaking: 
 * breakDriver breaks all the methods (that throw SQLException) of the Driver;
 * brealOldConnections breaks all connections produced so far;
 * breakNewConnections ensures that all newly produced connections are broken.
 * 
 * Any method called on broken connection results in SQLException.
 * 
 * The simple scenarios used in both EntityManagerJUnitTestSuite tests is imitation of db going down, then coming back:
 * going down: call breakDriver and breakOldConnections (calling breakNewConnections is possible but won't add anything -
 * as long as driver is broken there will be no new connections);
 * coming back: call repairDriver - now new functional new connections could be created, but all old connections are still broken. 
 * 
 * Also you can also break / repair individual connection.
 * If, say breakOldConnections was performed on DriwerWrapper and repair on ConnectionWrapper the chronologically last call wins.
 * There's no harm in breaking (or repairing) several times in a row.
 * 
 * You can pass custom exception string to each break method, otherwise defaults used (the string will be in SQLException, also visible in debugger).
 * 
 * Another usage that seems useful: stepping through the code in debugger you can trigger SQLException
 * to be thrown by any Connection method at will
 * be setting broken flag on ConnectionWrapper to true.
 * 
 * After the EMF using DriverWrapper is closed, call DriverWrapper.clear() to forget the wrapped driver and clear all the cached ConnectionWrappers.
 */
public class DriverWrapper implements Driver {

    // the wrapped driver name
    static String driverName;
    // the wrapped driver
    static Driver driver;

    // if set to true then methods called on the driver throw exception
    static boolean driverBroken;
    static String driverBrokenExceptionString;
    public static String defaultDriverBrokenExceptionString = "DriverWrapper: driver is broken";
    
    // if set to true then methods called on the connections already acquired throw exception
    static boolean oldConnectionsBroken;
    static String oldConnectionsBrokenExceptionString;
    public static String defaultOldConnectionsBrokenExceptionString =  "DriverWrapper: old connections are broken";
    
    // if set to true then methods called on the newly acquired connections will throw exception
    static boolean newConnectionsBroken;
    static String newConnectionsBrokenExceptionString;
    public static String defaultNewConnectionsBrokenExceptionString =  "DriverWrapper: new connections are broken";
    
    // all created ConnectionWrappers are cached
    static HashSet<ConnectionWrapper> connections = new HashSet();

    // register with DriverManager
    static {
        try {
            DriverManager.registerDriver(new DriverWrapper());
        } catch (SQLException ex) {
            throw new TestProblemException("registerDriver failed for DriverWrapper", ex);
        }
    }
    
    public static String codeUrl(String url) {
        return url.replace(':', '*');
    }
    public static String decodeUrl(String url) {
        return url.replace('*', ':');
    }
    public static void initialize(String newDriverName) {
        clear();
        driverName = newDriverName;
    }
    
    public static void breakDriver() {
        breakDriver(defaultDriverBrokenExceptionString);
    }
    public static void breakDriver(String exceptionString) {
        driverBroken = true;
        driverBrokenExceptionString = exceptionString;
    }
    public static void repairDriver() {
        driverBroken = false;
        driverBrokenExceptionString = null;
    }    
    
    public static void breakOldConnections() {
        breakOldConnections(defaultOldConnectionsBrokenExceptionString);
    }
    public static void breakOldConnections(String exceptionString) {
        oldConnectionsBroken = true;
        oldConnectionsBrokenExceptionString = exceptionString;
        Iterator<ConnectionWrapper> it = connections.iterator();
        while(it.hasNext()) {
            it.next().breakConnection(oldConnectionsBrokenExceptionString);
        }
    }
    public static void repairOldConnections() {
        oldConnectionsBroken = false;
        oldConnectionsBrokenExceptionString = null;
        Iterator<ConnectionWrapper> it = connections.iterator();
        while(it.hasNext()) {
            it.next().repairConnection();
        }
    }    
    
    public static void breakNewConnections() {
        breakNewConnections(defaultNewConnectionsBrokenExceptionString);
    }
    public static void breakNewConnections(String exceptionString) {
        newConnectionsBroken = true;
        newConnectionsBrokenExceptionString = exceptionString;
    }
    public static void repairNewConnections() {
        newConnectionsBroken = false;
        newConnectionsBrokenExceptionString = null;
    }
    
    public static void breakAll() {
        breakDriver();
        breakNewConnections();
        breakOldConnections();
    }
    
    public static void repairAll() {
        repairDriver();
        repairNewConnections();
        repairOldConnections();
    }
    
    public static void clear() {
        repairAll();
        Iterator<ConnectionWrapper> it = connections.iterator();
        while(it.hasNext()) {
            try {
                it.next().close();
            } catch (SQLException ex) {
                //ignore
            }
        }
        connections.clear();
        driver = null;
        driverName = null;
    }
    
    static Driver getDriver() {
        if(driver == null) {
            try {
                driver = (Driver)Class.forName(driverName, true, Thread.currentThread().getContextClassLoader()).newInstance();
            } catch (Exception ex) {
                throw new TestProblemException("DriverWrapper: failed to instantiate " + driverName, ex);
            }
        }
        return driver;
    }

    public static boolean driverBroken() {
        return driverBroken;
    }
    public static String getDriverBrokenExceptionString() {
        return driverBrokenExceptionString;
    }
    
    public static boolean oldConnectionsBroken() {
        return oldConnectionsBroken;
    }
    public static String getOldConnectionsBrokenExceptionString() {
        return oldConnectionsBrokenExceptionString;
    }

    public static boolean newConnectionsBroken() {
        return newConnectionsBroken;
    }
    public static String getNewConnectionsBrokenExceptionString() {
        return newConnectionsBrokenExceptionString;
    }

    /*
     * The following methods implement Driver interface
     */
    public Connection connect(String url, java.util.Properties info) throws SQLException {
        if(driverBroken) {
            throw new SQLException(getDriverBrokenExceptionString());
        }
        String decodedUrl = decodeUrl(url);
        if(driverName != null) {
            Connection internalConn = getDriver().connect(decodedUrl, info);
            if (internalConn == null) {
                // The driver should return "null" if it realizes it is the wrong kind of driver to connect to the given URL.
                return null;
            }
            ConnectionWrapper conn = new ConnectionWrapper(internalConn);
            connections.add(conn);
            return conn;
        } else {
            // non-initialized DriverWrapper should be ignored by DriverManager.
            return null;
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        if(driverName != null) {
            if(driverBroken) {
                throw new SQLException(getDriverBrokenExceptionString());
            }
            String decodedUrl = decodeUrl(url);
            return getDriver().acceptsURL(decodedUrl);
        } else {
            // non-initialized DriverWrapper should be ignored by DriverManager.
            return false;
        }
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException {
        if(driverBroken) {
            throw new SQLException(getDriverBrokenExceptionString());
        }
        return getDriver().getPropertyInfo(url, info);
    }

    public int getMajorVersion() {
        return getDriver().getMajorVersion();
    }

    public int getMinorVersion() {
        return getDriver().getMinorVersion();
    }

    public boolean jdbcCompliant() {
        return getDriver().jdbcCompliant();
    }
} 
