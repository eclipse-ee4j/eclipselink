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
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnectionFactory;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoJCAConnectionSpec;

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
        setConnectionFactory(new MongoConnectionFactory());
        String host = (String)properties.get(HOST);
        if (host == null) {
            host = "localhost";
        }
        String port = (String)properties.get(PORT);
        String db = (String)properties.get(DB);
        if (getConnectionSpec() == null) {
            if (host.indexOf(',') == -1) {
                MongoJCAConnectionSpec spec = new MongoJCAConnectionSpec();
                setConnectionSpec(spec);                
            } else {
                MongoJCAConnectionSpec spec = new MongoJCAConnectionSpec();
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
                spec.setDB(db);
                setConnectionSpec(spec);
            }
        }

        return super.connectToDataSource(accessor, properties);
    }
}
