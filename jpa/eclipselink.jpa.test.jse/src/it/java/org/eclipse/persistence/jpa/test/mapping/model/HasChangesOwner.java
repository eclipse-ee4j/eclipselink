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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Entity A from issue #2967, owning the bidirectional many-to-many relationship.
 */
@Entity
@Table(name = "HC_OWNER")
public class HasChangesOwner {

    @Id
    private long id;

    @ManyToMany
    @JoinTable(name = "HC_OWNER_TARGET", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    private List<HasChangesTarget> targets = new ArrayList<>();

    /**
     * Constructor used by the persistence provider.
     */
    public HasChangesOwner() {
    }

    /**
     * Create an owner with an assigned identifier and no relationships.
     */
    public HasChangesOwner(long id) {
        this.id = id;
    }

    /**
     * Return the targets on the owning side of the relationship.
     */
    public List<HasChangesTarget> getTargets() {
        return targets;
    }

    /**
     * Add a relationship on both ends, as required by the reporter's sequence.
     */
    public void addTarget(HasChangesTarget target) {
        targets.add(target);
        target.getOwners().add(this);
    }

    /**
     * Remove a relationship on both ends.
     */
    public void removeTarget(HasChangesTarget target) {
        targets.remove(target);
        target.getOwners().remove(this);
    }
}
