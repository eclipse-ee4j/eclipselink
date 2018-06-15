/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.myst;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

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
