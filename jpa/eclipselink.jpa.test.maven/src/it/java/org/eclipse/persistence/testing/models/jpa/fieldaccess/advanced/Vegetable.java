/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="Vegetable")
@Table(name="CMP3_FA_VEGETABLE")
public class Vegetable implements Serializable {
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="vegetable_name")),
        @AttributeOverride(name="color", column=@Column(name="vegetable_color"))
    })

    private VegetablePK id;
    private double cost;
    private String[] tags;
    @Column (columnDefinition="char(1)")
    private char type = '0';

    public Vegetable() {}

    public boolean equals(Object otherVegetable) {
        if (otherVegetable instanceof Vegetable) {
            return getId().equals(((Vegetable) otherVegetable).getId());
        }

        return false;
    }

    public double getCost() {
        return cost;
    }

    public VegetablePK getId() {
        return id;
    }

    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setId(VegetablePK id) {
        this.id = id;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public char getType() {
        return type;
    }

    public void setType(char aType) {
        this.type = aType;
    }

    public String toString() {
        return "Vegetable[id=" + getId() + "]";
    }
}
