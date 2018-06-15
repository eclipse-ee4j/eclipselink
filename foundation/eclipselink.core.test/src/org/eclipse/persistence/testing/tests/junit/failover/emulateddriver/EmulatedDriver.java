/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.junit.failover.emulateddriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EmulatedDriver implements Driver {

    protected Map rows;

    public static boolean fullFailure = false;

    public EmulatedDriver(){
        this.rows = new HashMap();
    }

    public Connection connect(String url, java.util.Properties info) throws SQLException{
        if (fullFailure) throw new SQLException("Connections unavailable");
        return new EmulatedConnection(this);
    }

    public Map getRows() {
        return rows;
    }

    public void setRows(Map rows) {
        this.rows = rows;
    }

    public boolean acceptsURL(String url) {
        return true;
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) {
        return null;
    }

    public Logger getParentLogger(){return null;}

}
