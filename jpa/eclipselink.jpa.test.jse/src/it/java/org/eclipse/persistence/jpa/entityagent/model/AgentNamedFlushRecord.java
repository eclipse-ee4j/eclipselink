/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;

import org.eclipse.persistence.config.QueryHints;

@Entity
@Table(name = "EA_NAMED_FLUSH")
@NamedQueries({
        @NamedQuery(name = "agentNamedFlush", query = "select count(r) from AgentNamedFlushRecord r", flush = QueryFlushMode.FLUSH),
        @NamedQuery(name = "agentNamedNoFlush", query = "select count(r) from AgentNamedFlushRecord r", flush = QueryFlushMode.NO_FLUSH),
        @NamedQuery(name = "agentNamedDefaultFlush", query = "select count(r) from AgentNamedFlushRecord r"),
        @NamedQuery(name = "agentNamedFlushHint", query = "select count(r) from AgentNamedFlushRecord r",
                flush = QueryFlushMode.FLUSH, hints = @QueryHint(name = QueryHints.FLUSH, value = "false"))
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "agentNativeFlush", query = "SELECT COUNT(*) FROM EA_NAMED_FLUSH", flush = QueryFlushMode.FLUSH),
        @NamedNativeQuery(name = "agentNativeNoFlush", query = "SELECT COUNT(*) FROM EA_NAMED_FLUSH", flush = QueryFlushMode.NO_FLUSH),
        @NamedNativeQuery(name = "agentNativeDefaultFlush", query = "SELECT COUNT(*) FROM EA_NAMED_FLUSH")
})
public class AgentNamedFlushRecord {
    @Id
    public Integer id;

    public AgentNamedFlushRecord() {
    }

    public AgentNamedFlushRecord(int id) {
        this.id = id;
    }
}
