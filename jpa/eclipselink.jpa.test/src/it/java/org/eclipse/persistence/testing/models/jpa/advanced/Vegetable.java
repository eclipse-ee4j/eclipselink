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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_VEGETABLE")
public class Vegetable implements Serializable {
    private VegetablePK id;
    private double cost;
    private String[] tags;
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

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="VEGETABLE_NAME")),
        @AttributeOverride(name="color", column=@Column(name="VEGETABLE_COLOR"))
    })
    public VegetablePK getId() {
        return id;
    }

    @Column(columnDefinition="char(1)")
    public char getType() {
        return type;
    }

    public void setType(char aType) {
        this.type = aType;
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

    public String toString() {
        return "Vegetable[id=" + getId() + "]";
    }
}
