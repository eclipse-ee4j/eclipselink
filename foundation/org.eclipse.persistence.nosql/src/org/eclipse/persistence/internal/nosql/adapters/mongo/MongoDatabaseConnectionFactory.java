/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * Connection factory for Mongo JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoDatabaseConnectionFactory implements ConnectionFactory {
    protected transient MongoClient mongo;
    protected transient MongoDatabase db;

    /**
     * Default constructor.
     */
    public MongoDatabaseConnectionFactory() {
    }

    /**
     * Create a factory from an external mongo instance.
     */
    public MongoDatabaseConnectionFactory(MongoClient mongo) {
        this.mongo = mongo;
    }

    /**
     * Create a factory from an external mongo instance.
     */
    public MongoDatabaseConnectionFactory(MongoDatabase db) {
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
            MongoClient mongo = this.mongo;
            if (mongo == null) {
                isExternal = false;
                List<MongoCredential> credentialsList =  new ArrayList<>();
                if ((connectionSpec.getUser() != null) && (connectionSpec.getUser().length() > 0)) {
                    MongoCredential credential = MongoCredential.createCredential(connectionSpec.getUser(), connectionSpec.getDB(), connectionSpec.getPassword());
                    credentialsList.add(credential);
                }
                Builder optionsBuilder = new MongoClientOptions.Builder();
                optionsBuilder.serverSelectionTimeout(connectionSpec.getServerSelectionTimeout());
                if (connectionSpec.getReadPreference() != null) {
                    optionsBuilder.readPreference(connectionSpec.getReadPreference());
                }
                if (connectionSpec.getWriteConcern() != null) {
                    optionsBuilder.writeConcern(connectionSpec.getWriteConcern());
                }
                optionsBuilder.codecRegistry(MongoCodecs.codecRegistry());
                MongoClientOptions options = optionsBuilder.build();
                if (servers.isEmpty()) {
                    mongo = new MongoClient(new ServerAddress(), credentialsList, options);
                } else {
                    mongo = new MongoClient(servers, credentialsList, options);
                }
            }
            db = mongo.getDatabase(connectionSpec.getDB());
            try {
                db.runCommand(new Document("ping", 1)); // check connection
            } catch (Exception exception) {
                ResourceException resourceException = new ResourceException(exception);
                resourceException.initCause(exception);
                throw resourceException;
            }
        }

        return new MongoDatabaseConnection(mongo, db, isExternal, connectionSpec);
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
