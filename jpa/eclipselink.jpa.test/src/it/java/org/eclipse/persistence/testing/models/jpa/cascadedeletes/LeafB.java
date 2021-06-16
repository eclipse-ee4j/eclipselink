/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * Entity implementation class for Entity: LeafB
 *
 */
@Entity
public class LeafB implements Serializable, PersistentIdentity {

    @Id
    @GeneratedValue
    protected int id;

    public LeafB() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public boolean checkTreeForRemoval(EntityManager em){
        boolean exists = em.find(this.getClass(), this.getId())!= null;
        return exists;
    }
}
