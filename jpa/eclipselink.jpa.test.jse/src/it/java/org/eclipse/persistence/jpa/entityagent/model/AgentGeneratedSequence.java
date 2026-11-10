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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.SequenceGenerator;

@Entity
@Table(name = "EA_GV_SEQUENCE")
@SequenceGenerator(name = "agent_sequence_ids", sequenceName = "EA_GV_SEQ", allocationSize = 1)
public class AgentGeneratedSequence extends AgentGeneratedRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_sequence_ids")
    public Long id;

    @Override
    public Object identifier() { return id; }

    @Override
    public void assignIdentifier(Object id) { this.id = (Long) id; }
}
