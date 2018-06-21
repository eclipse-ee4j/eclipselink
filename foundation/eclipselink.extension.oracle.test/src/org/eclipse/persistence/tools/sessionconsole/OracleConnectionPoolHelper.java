/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
