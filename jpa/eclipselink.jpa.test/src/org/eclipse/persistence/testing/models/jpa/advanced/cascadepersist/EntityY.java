/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table (name="ENTITYY_CP")
public class EntityY {
    @Id
    private int id;

    private String yname;

    @OneToOne
    private EntityX entityX;

    @ManyToOne
    private EntityZ yzEntityRelation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYname(String yname) {
        this.yname = yname;
    }

    public String getYname() {
        return yname;
    }

    public void setEntityX(EntityX entityX) {
        this.entityX = entityX;
    }

    public EntityX getEntityX() {
        return entityX;
    }

    public void setYzEntityRelation(EntityZ yzEntityRelation) {
        this.yzEntityRelation = yzEntityRelation;
    }

    public EntityZ getYzEntityRelation() {
        return yzEntityRelation;
    }
}
