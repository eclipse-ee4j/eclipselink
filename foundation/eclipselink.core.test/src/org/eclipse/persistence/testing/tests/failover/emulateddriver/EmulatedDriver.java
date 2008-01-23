package org.eclipse.persistence.testing.tests.failover.emulateddriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.HashMap;
import java.util.Map;

public class EmulatedDriver implements Driver {

    protected Map rows;

    public EmulatedDriver(){
        this.rows = new HashMap();
    }

    public Connection connect(String url, java.util.Properties info) {
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

}
