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

import java.util.ArrayList;
import java.util.List;

public class AgentXmlCallbackRecord {
    public Integer id;
    public String name;
    public String note;
    public Integer version;
    public List<String> events = new ArrayList<>();

    public AgentXmlCallbackRecord() {
    }

    public AgentXmlCallbackRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    void beforeInsert() { events.add("xml-pre-insert"); }
    void afterInsert() { events.add("xml-post-insert"); }
    void beforeUpdate() { events.add("xml-pre-update"); }
    void afterUpdate() { events.add("xml-post-update"); }
    void beforeUpsert() { events.add("xml-pre-upsert"); }
    void afterUpsert() { events.add("xml-post-upsert"); }
    void beforeDelete() { events.add("xml-pre-delete"); }
    void afterDelete() { events.add("xml-post-delete"); }
    void afterLoad() { events.add("xml-post-load:" + name); }
}
