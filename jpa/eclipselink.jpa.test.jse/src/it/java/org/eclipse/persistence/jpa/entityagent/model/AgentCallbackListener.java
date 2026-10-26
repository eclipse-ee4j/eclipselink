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

import jakarta.persistence.PreInsert;

public class AgentCallbackListener {
    @PreInsert
    public void beforeInsert(AgentCallbackRecord record) {
        record.events.add("listener-pre-insert");
        if ("fail-before-insert".equals(record.name)) {
            throw new IllegalStateException("callback failure");
        }
    }
}
