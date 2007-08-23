/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.sessionconsole;

import javax.sql.DataSource;

public class OracleConnectionPoolHelper {
    
    public static DataSource getOracleDataSource(String connectionString){
        oracle.jdbc.pool.OracleDataSource oracleDataSource = null;
        try {
            oracleDataSource = new oracle.jdbc.pool.OracleDataSource();
        } catch (java.sql.SQLException ex) {
            // failed to create Oracle data source
            return null;
        }
        oracleDataSource.setURL(connectionString);
        return oracleDataSource;
    }

}
