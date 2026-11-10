/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_BC_GROUP")
public class BooleanCriteriaGroup {
    @Id
    public Integer id;

    @Column(name = "FLAG_ACTIVE")
    public Boolean active;

    public BooleanCriteriaGroup() {
    }

    public BooleanCriteriaGroup(int id, Boolean active) {
        this.id = id;
        this.active = active;
    }
}
