/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     07/30/2020 - Will Dazey
//       - 564260: ElementCollection lowercase AttributeOverride is ignored
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "override_entity_b")
public class OverrideEntityB {

    @Id
    @Column(name="b_id")
    private Integer id;

    @ElementCollection
    @CollectionTable(name = "ct_override_entity_b", joinColumns = @JoinColumn(name = "entity_b_ct_entity_b")) // use default join column name
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "ct_b_override_value")),
        @AttributeOverride(name = "nestedValue.nestedValue", column = @Column(name = "ct_b_override_nested_value"))
    })
    private Set<OverrideEmbeddableB> simpleMappingEmbeddable;

    public OverrideEntityB() { }

    public OverrideEntityB(Integer id, Set<OverrideEmbeddableB> simpleMappingEmbeddable) {
        this.id = id;
        this.simpleMappingEmbeddable = simpleMappingEmbeddable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<OverrideEmbeddableB> getSimpleMappingEmbeddable() {
        return simpleMappingEmbeddable;
    }

    public void setSimpleMappingEmbeddable(Set<OverrideEmbeddableB> simpleMappingEmbeddable) {
        this.simpleMappingEmbeddable = simpleMappingEmbeddable;
    }
} 