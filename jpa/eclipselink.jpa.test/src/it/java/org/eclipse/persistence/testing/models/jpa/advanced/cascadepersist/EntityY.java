/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
