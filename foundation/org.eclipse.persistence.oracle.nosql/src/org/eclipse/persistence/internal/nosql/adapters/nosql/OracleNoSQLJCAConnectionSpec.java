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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + this.store + ")";
    }
}
