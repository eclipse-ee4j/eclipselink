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
import jakarta.persistence.Table;

@Entity
@Table(name = "EA_VALIDATION_BOOTSTRAP")
public class AgentValidationBootstrapRecord {
    @Id
    public Integer id;
    public String name;

    public AgentValidationBootstrapRecord() {
    }

    public AgentValidationBootstrapRecord(int id) {
        this.id = id;
        name = "record-" + id;
    }
}
