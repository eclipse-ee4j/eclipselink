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
package org.eclipse.persistence.jpa.test.mapping.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity C from issue #2967, persisted only to exercise the reported workaround.
 */
@Entity
@Table(name = "HC_UNRELATED")
public class HasChangesUnrelated {

    @Id
    private long id;

    /**
     * Constructor used by the persistence provider.
     */
    public HasChangesUnrelated() {
    }

    /**
     * Create an entity without relationships to either A or B.
     */
    public HasChangesUnrelated(long id) {
        this.id = id;
    }
}
