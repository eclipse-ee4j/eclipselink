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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity(name = "AgentOwnedParent")
@Table(name = "EA_OWNED_PARENT")
public class AgentOwnedParent {
    @Id
    public Long id;
    public String name;
    @Version
    public Integer version;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNED_PARENT_ID")
    public List<AgentOwnedChild> children = new ArrayList<>();
    @OneToMany(mappedBy = "inverseParent", cascade = CascadeType.ALL)
    public List<AgentOwnedChild> inverseChildren = new ArrayList<>();

    public AgentOwnedParent() {
    }

    public AgentOwnedParent(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
