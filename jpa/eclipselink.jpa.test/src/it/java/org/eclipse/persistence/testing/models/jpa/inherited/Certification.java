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


package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.*;

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
