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
