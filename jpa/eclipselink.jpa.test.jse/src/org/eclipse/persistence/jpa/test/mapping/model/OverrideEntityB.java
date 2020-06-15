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
 *     07/30/2020 - Will Dazey
 *       - 564260: ElementCollection lowercase AttributeOverride is ignored
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

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