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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Table;

@Entity(name = "AgentQueryGroup")
@Table(name = "EA_QUERY_GROUP")
@NamedEntityGraph(name = "EAQueryGroup.name", attributeNodes = @NamedAttributeNode("name"))
public class AgentQueryGroup {
    @Id
    public Integer id;
    public String name;
    @OneToMany(mappedBy = "group")
    public List<AgentQueryRecord> records = new ArrayList<>();

    public AgentQueryGroup() {
    }

    public AgentQueryGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
