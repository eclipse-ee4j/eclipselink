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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * This model tests Example #4 of the mapsId cases (mapped from LieutenantGeneral)
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_GENERAL")
public class General {
    @Id
    @Column(name="GENERAL_ID")
    @GeneratedValue(strategy=TABLE, generator="GENERAL_TABLE_GENERATOR")
    @TableGenerator(
        name="GENERAL_TABLE_GENERATOR",
        table="JPA_GENERAL_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="GENERAL_SEQ",
        initialValue=50
    )
    Integer generalId;

    public Integer getGeneralId() {
        return generalId;
    }

    public void setGeneralId(Integer generalId) {
        this.generalId = generalId;
    }
}
