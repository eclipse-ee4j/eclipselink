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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
