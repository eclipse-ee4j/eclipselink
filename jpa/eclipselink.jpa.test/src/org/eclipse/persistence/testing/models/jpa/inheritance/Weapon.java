/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Inheritance(strategy=TABLE_PER_CLASS)
@Table(name="TPC_WEAPON")
@NamedQueries({
    @NamedQuery(
            name="findAllWeapons",
            query="SELECT OBJECT(w) FROM Weapon w"
    ),
    @NamedQuery(
            name="findAllWeaponsContainingDescription",
            query="SELECT OBJECT(w) FROM Weapon w WHERE w.description LIKE :description"
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
