/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/06/2020 - Will Dazey
 *       - 347987: Fix Attribute Override for Complex Embeddables
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
