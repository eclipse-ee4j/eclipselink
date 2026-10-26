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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EA_GV_OWNER")
public class AgentGeneratedOwner {
    @Id
    public Long id;
    public String name;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TARGET_ID")
    public AgentGeneratedTarget target;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "EA_GV_OWNER_TARGET", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    public List<AgentGeneratedTarget> targets = new ArrayList<>();
}
