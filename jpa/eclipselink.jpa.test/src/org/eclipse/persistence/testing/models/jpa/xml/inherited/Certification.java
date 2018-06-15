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
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class Certification  {
    private Integer id;
    private String description;
    private BeerConsumer beerConsumer;

    public Certification() {}

    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    // Used as a map key (in xml) for certifications.
    public Integer getMapKey() {
        return getId();
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

    public void setMapKey(Integer mapKey) {
        // just ignore it ...
    }
}
