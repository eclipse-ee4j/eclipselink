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
//     03/26/2008-1.0M6 Guy Pelletier
//       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.relationships;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="CMP3_MEGABRANDS")
public class MegaBrands implements Distributor {
    private Integer distributorId;
    private String name;

    public MegaBrands() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="DISTRIBUTOR_TABLE_GENERATOR")
    @TableGenerator(
        name="DISTRIBUTOR_TABLE_GENERATOR",
        table="CMP3_DISTRIBUTOR_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="DTR_SEQ"
    )
    public Integer getDistributorId() {
        return distributorId;
    }

    public String getName() {
        return name;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
