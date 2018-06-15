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
//     Gunnar Wagenknecht - external support
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

/**
 * Connection factory for Mongo JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoConnectionFactory implements ConnectionFactory {
    protected transient Mongo mongo;
    protected transient DB db;

    /**
     * Default constructor.
     */
    public MongoConnectionFactory() {
    }

    /**
     * Create a factory from an external mongo instance.
     */
    public MongoConnectionFactory(Mongo mongo) {
        this.mongo = mongo;
    }

    /**
     * Create a factory from an external mongo instance.
     */
    public MongoConnectionFactory(DB db) {
        this.db = db;
    }

    @Override
    public Connection getConnection() throws ResourceException {
        return getConnection(new MongoJCAConnectionSpec());
    }

    @Override
    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        MongoJCAConnectionSpec connectionSpec = (MongoJCAConnectionSpec)spec;
        DB db = this.db;
        boolean isExternal = true;
        if (db == null) {
            try {
                List<ServerAddress> servers = new ArrayList<ServerAddress>();
                for (int index = 0; index < connectionSpec.getHosts().size(); index++) {
                    String host = connectionSpec.getHosts().get(index);
                    int port = ServerAddress.defaultPort();
                    if (connectionSpec.getPorts().size() > index) {
                        port = connectionSpec.getPorts().get(index);
                    }
                    ServerAddress server = new ServerAddress(host, port);
                    servers.add(server);
                }
                if (connectionSpec.getHosts().isEmpty()) {
                    ServerAddress server = new ServerAddress("localhost", ServerAddress.defaultPort());
                    servers.add(server);
                }
                Mongo mongo = this.mongo;
                if (mongo == null) {
                    isExternal = false;
                    if (servers.isEmpty()) {
                        mongo = new Mongo();
                    } else {
                        mongo = new Mongo(servers);
                    }
                }
                db = mongo.getDB(connectionSpec.getDB());
                if ((connectionSpec.getUser() != null) && (connectionSpec.getUser().length() > 0)) {
                    try {
                        Method method = DB.class.getMethod("authenticate", String.class, char[].class);
                        if (!(Boolean) method.invoke(db, connectionSpec.getUser(), connectionSpec.getPassword())) {
                            throw new ResourceException("authenticate failed for user: " + connectionSpec.getUser());
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new ResourceException("authenticate method not supported: " + e.getMessage(), e);
                    }
                }
                if (connectionSpec.getOptions() > 0) {
                    db.setOptions(connectionSpec.getOptions());
                }
                if (connectionSpec.getReadPreference() != null) {
                    db.setReadPreference(connectionSpec.getReadPreference());
                }
                if (connectionSpec.getWriteConcern() != null) {
                    db.setWriteConcern(connectionSpec.getWriteConcern());
                }
            } catch (/*UnknownHost*/Exception exception) {
                ResourceException resourceException = new ResourceException(exception.toString());
                resourceException.initCause(exception);
                throw resourceException;
            }
        }

        return new MongoConnection(db, isExternal, connectionSpec);
    }

    @Override
    public ResourceAdapterMetaData getMetaData() {
        return new MongoAdapterMetaData();
    }

    @Override
    public RecordFactory getRecordFactory() {
        return new MongoRecordFactory();
    }

    @Override
    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    @Override
    public void setReference(Reference reference) {
    }

    public Mongo getMongo() {
        return mongo;
    }

    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
