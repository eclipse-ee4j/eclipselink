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
package org.eclipse.persistence.jpa.fetchoverrides.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_UF_TARGET")
public class UnqualifiedFetchTarget {
    @Id
    private int id;
    private String label;

    public UnqualifiedFetchTarget() {
    }

    public UnqualifiedFetchTarget(int id) {
        this.id = id;
        this.label = "target-" + id;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
