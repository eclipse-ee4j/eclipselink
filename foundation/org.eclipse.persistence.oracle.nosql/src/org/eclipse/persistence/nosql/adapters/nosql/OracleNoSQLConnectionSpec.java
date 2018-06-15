/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.nosql.adapters.nosql;

import java.util.Properties;

import javax.resource.cci.Connection;

import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLConnectionFactory;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLJCAConnectionSpec;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Provides connection information to the Oracle NoSQL database.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLConnectionSpec extends EISConnectionSpec {

    /** Connection spec properties. */
    public static String STORE = "nosql.store";
    public static String HOST = "nosql.host";

    /**
     * PUBLIC:
     * Default constructor.
     */
    public OracleNoSQLConnectionSpec() {
        super();
    }

    /**
     * Connect with the specified properties and return the Connection.
     */
    @Override
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        if ((this.connectionFactory == null) && (this.name == null)) {
            this.connectionFactory = new OracleNoSQLConnectionFactory();
        }
        if (!properties.isEmpty()) {
            if (this.connectionSpec == null) {
                this.connectionSpec = new OracleNoSQLJCAConnectionSpec();
            }
            OracleNoSQLJCAConnectionSpec spec = (OracleNoSQLJCAConnectionSpec)this.connectionSpec;
            String store = (String)properties.get(STORE);
            if (store != null) {
                spec.setStore(store);
            }
            String host = (String)properties.get(HOST);
            if (host !=  null) {
                if (host.indexOf(',') == -1) {
                    spec.getHosts().add(host);
                } else {
                    int startIndex = 0;
                    while (startIndex < (host.length() - 1)) {
                        int endIndex = host.indexOf(',', startIndex);
                        if (endIndex == -1) {
                            endIndex = host.length();
                        }
                        String nextHost = host.substring(startIndex, endIndex);
                        spec.getHosts().add(nextHost);
                        startIndex = endIndex + 1;
                    }
                }
            }
        }

        return super.connectToDataSource(accessor, properties);
    }
}
