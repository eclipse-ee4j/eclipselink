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
//     Gunnar Wagenknecht - external support
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.Reference;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import jakarta.resource.cci.ConnectionSpec;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResourceAdapterMetaData;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import org.bson.Document;

/**
 * Connection factory for Mongo JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoConnectionFactory implements ConnectionFactory {
    protected transient MongoClient mongo;
    protected transient MongoDatabase db;

    /**
     * Default constructor.
     */
    public MongoConnectionFactory() {
    }

    /**
     * Create a factory from an external mongo instance.
     */
    public MongoConnectionFactory(MongoClient mongo) {
        this.mongo = mongo;
    }

    /**
     * Create a factory from an external mongo instance.
     */
    public MongoConnectionFactory(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public Connection getConnection() throws ResourceException {
        return getConnection(new MongoJCAConnectionSpec());
    }

    @Override
    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        MongoJCAConnectionSpec connectionSpec = (MongoJCAConnectionSpec)spec;
        MongoDatabase db = this.db;
        boolean isExternal = true;
        if (db == null) {
            try {
                List<ServerAddress> servers = new ArrayList<>();
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
                MongoClient mongo = this.mongo;
                if (mongo == null) {
                    isExternal = false;
                    MongoCredential credential = null;
                    if ((connectionSpec.getUser() != null) && (!connectionSpec.getUser().isEmpty())) {
                        if ( connectionSpec.getAuthSource() != null ) {
                            credential = MongoCredential.createCredential(connectionSpec.getUser(), connectionSpec.getAuthSource(), connectionSpec.getPassword());
                        }
                        else {
                            credential = MongoCredential.createCredential(connectionSpec.getUser(), connectionSpec.getDB(), connectionSpec.getPassword());
                        }
                    }
                    MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
                    settingsBuilder.applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(connectionSpec.getServerSelectionTimeout(), TimeUnit.MILLISECONDS));
                    if (connectionSpec.getReadPreference() != null) {
                        settingsBuilder.readPreference(connectionSpec.getReadPreference());
                    }
                    if (connectionSpec.getWriteConcern() != null) {
                        settingsBuilder.writeConcern(connectionSpec.getWriteConcern());
                    }
                    settingsBuilder.codecRegistry(MongoCodecs.codecRegistry());
                    MongoClientSettings settings = null;
                    if (credential != null) {
                        settingsBuilder.credential(credential);
                    }
                    if (servers.isEmpty()) {
                        ServerAddress serverAddress = new ServerAddress();
                        settings = settingsBuilder
                                .applyConnectionString(new ConnectionString("mongodb+srv://" + serverAddress.getHost() + ":" + serverAddress.getPort()))
                                .build();
                        mongo = MongoClients.create(settings);
                    } else {
                        settings = settingsBuilder
                                .applyToClusterSettings(builder ->
                                        builder.hosts(servers))
                                .build();
                        mongo = MongoClients.create(settings);
                    }
                    this.mongo = mongo;
                }
                db = mongo.getDatabase(connectionSpec.getDB());
                db.runCommand(new Document("ping", 1)); // check connection
                if ((connectionSpec.getUser() != null) && (!connectionSpec.getUser().isEmpty())) {
                    try {
                        Method method = MongoDatabase.class.getMethod("authenticate", String.class, char[].class);
                        if (!(Boolean) method.invoke(db, connectionSpec.getUser(), connectionSpec.getPassword())) {
                            throw new ResourceException("authenticate failed for user: " + connectionSpec.getUser());
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new ResourceException("authenticate method not supported: " + e.getMessage(), e);
                    }
                }
            } catch (/*UnknownHost*/Exception exception) {
                ResourceException resourceException = new ResourceException(exception.toString());
                resourceException.initCause(exception);
                throw resourceException;
            }
        }
        return new MongoConnection(mongo, connectionSpec.getDB(), isExternal, connectionSpec);
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

    public MongoClient getMongo() {
        return mongo;
    }

    public void setMongo(MongoClient mongo) {
        this.mongo = mongo;
    }

    public MongoDatabase getDb() {
        return db;
    }

    public void setDb(MongoDatabase db) {
        this.db = db;
    }
}
