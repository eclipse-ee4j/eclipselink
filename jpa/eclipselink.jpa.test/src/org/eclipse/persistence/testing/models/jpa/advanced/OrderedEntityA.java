/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ORD_ENTITY_A")
public class OrderedEntityA {

    @Id
    protected Long id;

    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ENTITYZ_ID")
    private OrderedEntityZ entityZ;
    
    public OrderedEntityA() {
        super();
    }
    
    public OrderedEntityA(Long id, String description) {
        super();
        setId(id);
        setDescription(description);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrderedEntityZ getEntityZ() {
        return entityZ;
    }

    public void setEntityZ(OrderedEntityZ entityZ) {
        this.entityZ = entityZ;
    }
    
    public String toString() {
        return getClass().getSimpleName() + " id:[" + this.id + "] desc:[" + this.description + "] hashcode:[" + System.identityHashCode(this) + "]";
    }
}
