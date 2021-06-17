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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToOne;
import static jakarta.persistence.GenerationType.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name="CMP3_CUBICLE")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CubiclePK.class)
public class Cubicle {
    private int id;
    private String code;
    private Scientist scientist;

    public Cubicle() {}

    public Cubicle(String code) {
        this.code = code;
    }

    public Cubicle(int id, String code) {
        this.id = id;
        this.code = code;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="COMPOSITE_PK_TABLE_GENERATOR")
    @TableGenerator(
        name="COMPOSITE_PK_TABLE_GENERATOR",
        table="CMP3_COMPOSITE_PK_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CUBICLE_SEQ"
    )
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @OneToOne(mappedBy="cubicle")
    public Scientist getScientist() {
        return scientist;
    }

    public void setScientist(Scientist scientist) {
        this.scientist = scientist;
    }
}
