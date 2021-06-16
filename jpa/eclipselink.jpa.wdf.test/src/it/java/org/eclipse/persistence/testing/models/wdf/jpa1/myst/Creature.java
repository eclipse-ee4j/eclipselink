/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.myst;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_CREATURE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("0")
@SecondaryTable(name = "TMP_CREATURE_DETAILS", pkJoinColumns = @PrimaryKeyJoinColumn(name = "SECONDTABLE_ID", referencedColumnName = "ID"))
public class Creature implements Serializable {
    private static final long serialVersionUID = 1L;

    public Creature(String aName) {
        name = aName;
    }

    private Integer id;
    private String name;
    private String color;
    private Set<Cave> caves;
    private Weapon weapon;

    public Creature() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    protected void setId(Integer aId) {
        id = aId;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "COLOR", table = "TMP_CREATURE_DETAILS")
    public String getColor() {
        return color;
    }

    public void setColor(String aColor) {
        color = aColor;
    }

    @ManyToMany(mappedBy = "creatures", targetEntity = Cave.class)
    public Set<Cave> getCaves() {
        return caves;
    }

    public void setCaves(Set<Cave> aCaves) {
        caves = aCaves;
    }

    @ManyToOne
    @JoinColumn(name = "WEAPON")
    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Creature) {
            Creature other = (Creature) obj;
            return other.name.equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }
}
