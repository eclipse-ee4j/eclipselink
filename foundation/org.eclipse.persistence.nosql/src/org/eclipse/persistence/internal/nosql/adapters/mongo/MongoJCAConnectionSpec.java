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

import javax.resource.cci.*;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * Defines connection information for connecting to Mongo.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoJCAConnectionSpec implements ConnectionSpec {

    /** Mongo database name. */
    protected String db = "mydb";

    /** Optional user name. */
    protected String user;
    /** Optional password. */
    protected char[] password;

    /** Hosts. */
    protected List<String> hosts = new ArrayList<String>();
    /** Ports. */
    protected List<Integer> ports = new ArrayList<Integer>();
    
    /** Database default query options. */
    protected int options;
    
    /** Database default read preference. */
    protected ReadPreference readPreference;
    
    /** Database default write concern. */
    protected WriteConcern writeConcern;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public MongoJCAConnectionSpec() {
    }

    /**
     * PUBLIC:
     * Construct the spec with the default directory.
     */
    public MongoJCAConnectionSpec(String db) {
        this.db = db;
    }
    
    public String getDB() {
        return db;
    }

    public void setDB(String db) {
        this.db = db;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    protected char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }
    
    public String toString() {
        return getClass().getSimpleName() + "(" + this.db + ")";
    }
    
    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
    }

    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }
}
