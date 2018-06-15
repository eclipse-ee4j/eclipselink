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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table (name="ENTITYX_CP")
public class EntityX {
    @Id
    private int id;

    private String xname;

    @OneToOne (mappedBy="entityX")
    private EntityY entityY;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setXname(String xname) {
        this.xname = xname;
    }

    public String getXname() {
        return xname;
    }

    public void setEntityY(EntityY entityY) {
        this.entityY = entityY;
    }

    public EntityY getEntityY() {
        return entityY;
    }
}
