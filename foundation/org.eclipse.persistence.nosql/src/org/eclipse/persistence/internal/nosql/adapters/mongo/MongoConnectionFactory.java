/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
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

    /**
     * Default constructor.
     */
    public MongoConnectionFactory() {
    }

    public Connection getConnection() throws ResourceException {
        return getConnection(new MongoJCAConnectionSpec());
    }

    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        MongoJCAConnectionSpec connectionSpec = (MongoJCAConnectionSpec)spec;
        DB db = null;
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
            Mongo mongo = null;
            if (servers.isEmpty()) {
                mongo = new Mongo();
            } else {
                mongo = new Mongo(servers);
            }
            db = mongo.getDB(connectionSpec.getDB());
            if ((connectionSpec.getUser() != null) && (connectionSpec.getUser().length() > 0)) {
                db.authenticate(connectionSpec.getUser(), connectionSpec.getPassword());
            }
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }

        return new MongoConnection(db, connectionSpec);
    }

    public ResourceAdapterMetaData getMetaData() {
        return new MongoAdapterMetaData();
    }

    public RecordFactory getRecordFactory() {
        return new MongoRecordFactory();
    }

    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    public void setReference(Reference reference) {
    }
}
