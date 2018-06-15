/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import java.io.Serializable;
import javax.persistence.*;

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
