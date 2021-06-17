/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/06/2020 - Will Dazey
//       - 347987: Fix Attribute Override for Complex Embeddables
//     07/30/2020 - Will Dazey
//       - 564260: ElementCollection lowercase AttributeOverride is ignored
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "OVERRIDE_ENTITY_A")
public class OverrideEntityA {

    @EmbeddedId
    private OverrideEmbeddableIdA id;

    @Embedded
    //OverrideEmbeddableA.value must have the same field name and Column name as OverrideEmbeddableIdA.value
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "OVERRIDE_VALUE")),
        @AttributeOverride(name = "nestedValue.nestedValue", column = @Column(name = "OVERRIDE_NESTED_VALUE"))
    })
    private OverrideEmbeddableA embeddedField1;

    @ElementCollection
    @CollectionTable(name = "CT_OVERRIDE_ENTITY_A") // use default join column name
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "CT_A_OVERRIDE_VALUE")),
        @AttributeOverride(name = "nestedValue.nestedValue", column = @Column(name = "CT_A_OVERRIDE_NESTED_VALUE"))
    })
    private Set<OverrideEmbeddableA> simpleMappingEmbeddable;

    public OverrideEmbeddableIdA getId() {
        return id;
    }

    public void setId(OverrideEmbeddableIdA id) {
        this.id = id;
    }

    public OverrideEmbeddableA getEmbeddedField1() {
        return embeddedField1;
    }

    public void setEmbeddedField1(OverrideEmbeddableA embeddedField1) {
        this.embeddedField1 = embeddedField1;
    }

    public Set<OverrideEmbeddableA> getSimpleMappingEmbeddable() {
        return simpleMappingEmbeddable;
    }

    public void setSimpleMappingEmbeddable(Set<OverrideEmbeddableA> simpleMappingEmbeddable) {
        this.simpleMappingEmbeddable = simpleMappingEmbeddable;
    }
} 
