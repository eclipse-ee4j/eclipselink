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
package org.eclipse.persistence.jpa.entityagent.model.callbacks;

import jakarta.persistence.EntityListener;
import jakarta.persistence.NamedNativeStatement;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PostInsert;

@EntityListener
@NamedNativeStatement(name = "agentNonentityDelete",
        statement = "delete from EA_DISCOVERY_RECORD where ID = 304")
public class AgentDiscoveredCallbackListener {
    @PreInsert
    public void beforeInsert(AgentDiscoveryRecord record) { record.events.add("discovered-pre"); }

    @PostInsert
    public void afterInsert(AgentDiscoveryRecord record) { record.events.add("discovered-post-record"); }

    @PostInsert
    public void afterAnyInsert(Object record) {
        if (record instanceof AgentDiscoveryRecord discovered) {
            discovered.events.add("discovered-post-object");
        }
    }
}
