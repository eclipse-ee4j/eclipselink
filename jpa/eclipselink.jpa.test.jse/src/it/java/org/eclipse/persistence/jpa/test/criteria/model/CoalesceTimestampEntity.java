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
package org.eclipse.persistence.jpa.test.criteria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CoalesceTimestampEntity {

    @Id
    private Integer id;

    private String description;

    private java.util.Date dateValue;

    public CoalesceTimestampEntity() {
    }

    public CoalesceTimestampEntity(Integer id, String description, java.util.Date dateValue) {
        this.id = id;
        this.description = description;
        this.dateValue = dateValue;
    }
}
