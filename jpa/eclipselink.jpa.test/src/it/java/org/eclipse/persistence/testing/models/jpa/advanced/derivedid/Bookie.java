/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     11/16/2009-2.0 Guy Pelletier
//       - 288392: With Identity sequencing entity with dependant ID can be sent to DB multiple times
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name="JPA_BOOKIE")
public class Bookie {
    private List<CellNumber> cellNumbers;
    private Integer id;
    private String name;

    public Bookie() {
        cellNumbers = new ArrayList<CellNumber>();
    }

    public void addCellNumber(CellNumber cellNumber) {
        cellNumbers.add(cellNumber);
        cellNumber.setBookie(this);
    }

    @OneToMany(cascade=ALL, mappedBy="bookie")
    @PrivateOwned
    public List<CellNumber> getCellNumbers() {
        return cellNumbers;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="BOOKIE_ID")
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCellNumbers(List<CellNumber> cellNumbers) {
        this.cellNumbers = cellNumbers;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
