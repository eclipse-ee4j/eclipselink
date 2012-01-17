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
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import java.util.ArrayList;
import java.util.List;

import javax.resource.cci.*;

/**
 * Defines connection information for connecting to Oracle NoSQL.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLJCAConnectionSpec implements ConnectionSpec {

    /** NoSQL store name. */
    protected String store;

    /** NoSQL hosts and ports (localhost:5000). */
    protected List<String> hosts;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public OracleNoSQLJCAConnectionSpec() {
        this.store = "kvstore";
        this.hosts = new ArrayList<String>();
    }

    /**
     * PUBLIC:
     * Construct the spec with the default directory.
     */
    public OracleNoSQLJCAConnectionSpec(String store, String host) {
        this.store = store;
        this.hosts = new ArrayList<String>();
        this.hosts.add(host);
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + this.store + ")";
    }
}
