/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.nosql.adapters.mongo;

import java.util.Properties;

import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;

import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnectionFactory;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoJCAConnectionSpec;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * Provides connection information to the Mongo database.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoConnectionSpec extends EISConnectionSpec {

    /** Connection spec properties. */
    public static final String HOST = "mongo.host";
    public static final String PORT = "mongo.port";
    public static final String DB = "mongo.db";
    public static final String OPTIONS = "mongo.options";
    public static final String READ_PREFERENCE = "mongo.read-preference";
    public static final String WRITE_CONCERN = "mongo.write-concern";
    public static final String SERVER_SELECTION_TIMEOUT = "mongo.server-selection-timeout";
    public static final String AUTH_SOURCE = "mongo.auth-source";

    /**
     * PUBLIC:
     * Default constructor.
     */
    public MongoConnectionSpec() {
        super();
    }

    /**
     * Connect with the specified properties and return the Connection.
     */
    @Override
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        if ((this.connectionFactory == null) && (this.name == null)) {
            this.connectionFactory = createMongoConnectionFactory();
        }
        if (!properties.isEmpty()) {
            if (this.connectionSpec == null) {
                this.connectionSpec = new MongoJCAConnectionSpec();
            }
            MongoJCAConnectionSpec spec = (MongoJCAConnectionSpec)this.connectionSpec;
            String host = (String)properties.get(HOST);
            String port = (String)properties.get(PORT);
            String db = (String)properties.get(DB);
            String authSource = (String)properties.get(AUTH_SOURCE); //https://www.mongodb.com/docs/manual/reference/connection-string/#mongodb-urioption-urioption.authSource

            if (host != null) {
                if (host.indexOf(',') == -1) {
                    spec.getHosts().add(host);
                    if (port != null) {
                        spec.getPorts().add(Integer.valueOf(port));
                    }
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
                    while (startIndex < (port.length() - 1)) {
                        int endIndex = port.indexOf(',', startIndex);
                        if (endIndex == -1) {
                            endIndex = port.length();
                        }
                        String nextPort = port.substring(startIndex, endIndex);
                        spec.getPorts().add(Integer.valueOf(nextPort));
                        startIndex = endIndex + 1;
                    }
                }
            }
            if (db != null) {
                spec.setDB(db);
            }

            if ( authSource != null ) {
                spec.setAuthSource(authSource);
            }

            String user = (String)properties.get("user");
            Object password = properties.get("password");
            if (password instanceof String s) {
                password = s.toCharArray();
            }
            if ((user != null) && (!user.isEmpty())) {
                spec.setUser(user);
                spec.setPassword((char[])password);
            }

            // Allows setting of read preference as a property.
            Object preference = properties.get(READ_PREFERENCE);
            if (preference instanceof ReadPreference rp) {
                spec.setReadPreference(rp);
            } else if (preference instanceof String constant) {
                if (constant.equals("PRIMARY")) {
                    spec.setReadPreference(ReadPreference.primary());
                } else if (constant.equals("SECONDARY")) {
                    spec.setReadPreference(ReadPreference.secondary());
                } else {
                    throw new EISException("Invalid read preference property value: " + constant);
                }
            }

            // Allows setting of write concern as a property.
            Object concern = properties.get(WRITE_CONCERN);
            if (concern instanceof WriteConcern wc) {
                spec.setWriteConcern(wc);
            } else if (concern instanceof String constant) {
                switch (constant) {
                    case "ACKNOWLEDGED" -> spec.setWriteConcern(WriteConcern.ACKNOWLEDGED);
                    case "JOURNALED" -> spec.setWriteConcern(WriteConcern.JOURNALED);
                    case "MAJORITY" -> spec.setWriteConcern(WriteConcern.MAJORITY);
                    case "NONE" -> spec.setWriteConcern(/*FIXME: WriteConcern.NONE*/ new WriteConcern("none"));
                    case "UNACKNOWLEDGED" -> spec.setWriteConcern(WriteConcern.UNACKNOWLEDGED);
                    case "W1" -> spec.setWriteConcern(WriteConcern.W1);
                    case "W2" -> spec.setWriteConcern(WriteConcern.W2);
                    case "W3" -> spec.setWriteConcern(WriteConcern.W3);
                    default -> throw new EISException("Invalid read preference property value: " + constant);
                }
            }

            // Allows setting of options as a property.
            Object options = properties.get(OPTIONS);
            if (options instanceof Number n) {
                spec.setOptions(n.intValue());
            } else if (options instanceof String s) {
                spec.setOptions(Integer.parseInt(s));
            }

            // Allows setting of serverSelectionTimeout as a property.
            Object serverSelectionTimeout = properties.get(SERVER_SELECTION_TIMEOUT);
            if (serverSelectionTimeout instanceof Number n) {
                spec.setServerSelectionTimeout(n.intValue());
            } else if (serverSelectionTimeout instanceof String s) {
                spec.setServerSelectionTimeout(Integer.parseInt(s));
            }
        }

        return super.connectToDataSource(accessor, properties);
    }

    protected ConnectionFactory createMongoConnectionFactory() {
            return new MongoConnectionFactory();
    }
}
