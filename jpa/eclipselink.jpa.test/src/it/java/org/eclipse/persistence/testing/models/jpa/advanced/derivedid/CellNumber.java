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
//     11/16/2009-2.0 Guy Pelletier
//       - 288392: With Identity sequencing entity with dependant ID can be sent to DB multiple times
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(CellNumberPK.class)
@Table(name="JPA_CELLNUMBER")
public class CellNumber {
    private Bookie bookie;
    private Integer id;
    private String number;
    private String description;

    public CellNumber() {}

    public CellNumberPK buildPK(){
        CellNumberPK pk = new CellNumberPK();
        pk.setId(getBookie().getId());
        pk.setNumber(getNumber());
        return pk;
    }

    @ManyToOne
    @JoinColumn(name="BOOKIE_ID")
    public Bookie getBookie() {
        return bookie;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @Id
    @Column(name="BOOKIE_ID", insertable=false, updatable=false)
    public Integer getId() {
        return id;
    }

    @Id
    @Column(name="NUMB")
    public String getNumber() {
        return number;
    }

    public void setBookie(Bookie bookie) {
        this.bookie = bookie;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String toString() {
        return "CellNumber: " + number;
    }
}
