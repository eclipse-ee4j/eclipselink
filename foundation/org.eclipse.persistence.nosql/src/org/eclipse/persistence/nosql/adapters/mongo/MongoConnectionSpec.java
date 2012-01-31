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
package org.eclipse.persistence.nosql.adapters.mongo;

import java.util.Properties;

import javax.resource.cci.Connection;

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
    public static String HOST = "mongo.host";
    public static String PORT = "mongo.port";
    public static String DB = "mongo.db";
    public static String OPTIONS = "mongo.options";
    public static String READ_PREFERENCE = "mongo.read-preference";
    public static String WRITE_CONCERN = "mongo.write-concern";

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
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        if ((this.connectionFactory == null) && (this.name == null)) {
            this.connectionFactory = new MongoConnectionFactory();
        }
        if (!properties.isEmpty()) {
            if (this.connectionSpec == null) {
                this.connectionSpec = new MongoJCAConnectionSpec();
            }
            MongoJCAConnectionSpec spec = (MongoJCAConnectionSpec)this.connectionSpec;
            String host = (String)properties.get(HOST);
            String port = (String)properties.get(PORT);
            String db = (String)properties.get(DB);
            if (host != null) {
                if (host.indexOf(',') == -1) {
                    spec.getHosts().add(host);
                    if (port != null) {
                        spec.getPorts().add(new Integer(port));
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
                        spec.getPorts().add(new Integer(nextPort));
                        startIndex = endIndex + 1;
                    }
                }
            }
            if (db != null) {
                spec.setDB(db);
            }
            
            // Allows setting of read preference as a property.
            Object preference = properties.get(READ_PREFERENCE);
            if (preference instanceof ReadPreference) {
                spec.setReadPreference((ReadPreference)preference);
            } else if (preference instanceof String) {
                String constant = (String)preference;
                if (constant.equals("PRIMARY")) {
                    spec.setReadPreference(ReadPreference.PRIMARY);
                } else if (constant.equals("SECONDARY")) {
                    spec.setReadPreference(ReadPreference.SECONDARY );
                } else {
                    throw new EISException("Invalid read preference property value: " + constant);                    
                }
            }
            
            // Allows setting of write concern as a property.
            Object concern = properties.get(WRITE_CONCERN);
            if (concern instanceof WriteConcern) {
                spec.setWriteConcern((WriteConcern)concern);
            } else if (concern instanceof String) {
                String constant = (String)concern;
                if (constant.equals("FSYNC_SAFE")) {
                    spec.setWriteConcern(WriteConcern.FSYNC_SAFE);
                } else if (constant.equals("JOURNAL_SAFE")) {
                    spec.setWriteConcern(WriteConcern.JOURNAL_SAFE);
                } else if (constant.equals("MAJORITY")) {
                    spec.setWriteConcern(WriteConcern.MAJORITY);
                } else if (constant.equals("NONE")) {
                    spec.setWriteConcern(WriteConcern.NONE);
                } else if (constant.equals("NORMAL")) {
                    spec.setWriteConcern(WriteConcern.NORMAL);
                } else if (constant.equals("REPLICAS_SAFE")) {
                    spec.setWriteConcern(WriteConcern.REPLICAS_SAFE);
                } else if (constant.equals("SAFE")) {
                    spec.setWriteConcern(WriteConcern.SAFE);
                } else {
                    throw new EISException("Invalid read preference property value: " + constant);                    
                }
            }
            
            // Allows setting of options as a property.
            Object options = properties.get(OPTIONS);
            if (options instanceof Number) {
                spec.setOptions(((Number)options).intValue());
            } else if (options instanceof String) {
                spec.setOptions(Integer.valueOf(((String)options)));
            }
        }

        return super.connectToDataSource(accessor, properties);
    }
}
