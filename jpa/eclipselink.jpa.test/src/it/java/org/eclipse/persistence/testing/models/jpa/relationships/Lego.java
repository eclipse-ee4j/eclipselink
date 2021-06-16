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
//     03/26/2008-1.0M6 Guy Pelletier
//       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.relationships;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name="CMP3_LEGO")
public class Lego implements Manufacturer {
    private Integer id;
    private String name;

    public Lego() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="MANUFACTURER_TABLE_GENERATOR")
    @TableGenerator(
        name="MANUFACTURER_TABLE_GENERATOR",
        table="CMP3_MANUFACTURER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="MFT_SEQ"
    )
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
