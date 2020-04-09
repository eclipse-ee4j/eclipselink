/*
 * Copyright (c) 2012, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.TableGenerator;

@MappedSuperclass
public class Utensil {
    @Id
    @GeneratedValue(strategy=TABLE, generator="UTENSIL_ID_GENERATOR")
    @TableGenerator(
        name="UTENSIL_ID_GENERATOR",
        table="JPA22_PK_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="UTENSIL_SEQ",
        indexes=@Index(
            name="TABLE_GENERATOR_INDEX",
            columnList="SEQ_COUNT, SEQ_NAME"
        )
    )
    public Integer id;

    @Column(name="SERIAL_TAG")
    public String serialTag;

    public Utensil() {}

    public Integer getId() {
        return id;
    }

    public String getSerialTag() {
        return serialTag;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSerialTag(String serialTag) {
        this.serialTag = serialTag;
    }
}
