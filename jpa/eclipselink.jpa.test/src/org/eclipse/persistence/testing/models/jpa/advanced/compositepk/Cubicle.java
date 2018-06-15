/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import static javax.persistence.GenerationType.*;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;

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
