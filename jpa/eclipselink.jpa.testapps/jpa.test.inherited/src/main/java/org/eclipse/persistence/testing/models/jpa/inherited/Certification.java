/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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


package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_CERTIFICATION")
public class Certification  {
    private Integer id;
    private String description;
    private BeerConsumer beerConsumer;

    public Certification() {}

    @ManyToOne
    @JoinColumn(name="CONSUMER_ID")
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }

    //@Basic
    public String getDescription() {
        return description;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="CERTIFICATION_TABLE_GENERATOR")
    @TableGenerator(
        name="CERTIFICATION_TABLE_GENERATOR",
        table="CMP3_BEER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CERTIFICATION_SEQ")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
