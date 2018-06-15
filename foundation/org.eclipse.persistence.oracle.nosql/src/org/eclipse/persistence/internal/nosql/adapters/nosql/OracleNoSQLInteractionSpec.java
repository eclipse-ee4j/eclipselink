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

import javax.resource.cci.*;

import oracle.kv.Consistency;
import oracle.kv.Durability;
import oracle.kv.Version;

/**
 * Interaction spec for Oracle NoSQL JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLInteractionSpec implements InteractionSpec {
    protected OracleNoSQLOperation operation;
    protected Consistency consistency;
    protected Durability durability;
    protected long timeout;
    protected String key;
    protected Version version;

    /**
     * Default constructor.
     */
    public OracleNoSQLInteractionSpec() {
    }

    public OracleNoSQLOperation getOperation() {
        return operation;
    }

    public void setOperation(OracleNoSQLOperation operation) {
        this.operation = operation;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return getClass().getName() + "(" + getOperation() + ")";
    }

    public Consistency getConsistency() {
        return consistency;
    }

    public void setConsistency(Consistency consistency) {
        this.consistency = consistency;
    }

    public Durability getDurability() {
        return durability;
    }

    public void setDurability(Durability durability) {
        this.durability = durability;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
