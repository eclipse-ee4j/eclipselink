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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Inheritance(strategy=TABLE_PER_CLASS)
@Table(name="TPC_WEAPON")
@NamedQueries({
    @NamedQuery(
            name="findAllWeapons",
            query="SELECT OBJECT(weapon) FROM Weapon weapon"
    ),
    @NamedQuery(
            name="findAllWeaponsContainingDescription",
            query="SELECT OBJECT(weapon) FROM Weapon weapon WHERE weapon.description LIKE :description"
    )
})
public class Weapon {
    @Id
    @GeneratedValue(strategy=TABLE, generator="WEAPON_TABLE_GENERATOR")
    @TableGenerator(
        name="WEAPON_TABLE_GENERATOR",
        table="CMP3_WEAPON_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WEAPON_SEQ")
    @Column(name="SERIAL_NUMBER")
    private Integer serialNumber;

    @Column(name="DESCRIP")
    private String description;

    @OneToOne(mappedBy="weapon")
    private Assassin assassin;

    public Weapon() {}

    public Assassin getAssassin() {
        return assassin;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public boolean isWeapon() {
        return true;
    }

    public boolean isDirectWeapon() {
        return false;
    }

    public boolean isIndirectWeapon() {
        return false;
    }

    public void setAssassin(Assassin assassin) {
        this.assassin = assassin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
}
