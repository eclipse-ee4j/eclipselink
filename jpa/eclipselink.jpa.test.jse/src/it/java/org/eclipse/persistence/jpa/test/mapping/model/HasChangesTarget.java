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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Entity B from issue #2967, on the inverse side of the relationship.
 */
@Entity
@Table(name = "HC_TARGET")
public class HasChangesTarget {

    @Id
    private long id;

    @ManyToMany(mappedBy = "targets")
    private List<HasChangesOwner> owners = new ArrayList<>();

    /**
     * Constructor used by the persistence provider.
     */
    public HasChangesTarget() {
    }

    /**
     * Create an unrelated target with an assigned identifier.
     */
    public HasChangesTarget(long id) {
        this.id = id;
    }

    /**
     * Return the identifier used to check the final relationship contents.
     */
    public long getId() {
        return id;
    }

    /**
     * Return owners on the inverse side so both ends can be updated together.
     */
    public List<HasChangesOwner> getOwners() {
        return owners;
    }
}
