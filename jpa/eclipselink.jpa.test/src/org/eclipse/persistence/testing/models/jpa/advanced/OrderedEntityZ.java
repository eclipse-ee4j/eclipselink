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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name="ORD_ENTITY_Z")
public class OrderedEntityZ {

    @Id
    protected Long id;
    
    protected String description;
    
    @OneToMany(mappedBy = "entityZ", fetch = FetchType.LAZY)
    @OrderColumn(name="ENTITYA_ORDER")
    private List<OrderedEntityA> entityAs = new ArrayList<OrderedEntityA>();
    
    public OrderedEntityZ() {
        super();
    }
    
    public OrderedEntityZ(Long id, String description) {
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
    
    public void addEntityA(OrderedEntityA entityA) {
        if (!getEntityAs().contains(entityA)) {
            getEntityAs().add(entityA);
        }
        entityA.setEntityZ(this);
    }
    
   public void removeEntityA(OrderedEntityA entityA) {
        if (getEntityAs().contains(entityA)) {
            getEntityAs().remove(entityA);
        }
        entityA.setEntityZ(null);
    }

    public List<OrderedEntityA> getEntityAs() {
        return entityAs;
    }
    
    public void setEntityAs(List<OrderedEntityA> entityAs) {
        this.entityAs = entityAs;
    }
    
    public String toString() {
        return getClass().getSimpleName() + " id:[" + this.id + "] desc:[" + this.description + "] entityAs:[" + this.entityAs + "] hashcode:[" + System.identityHashCode(this) + "]";
    }
}
