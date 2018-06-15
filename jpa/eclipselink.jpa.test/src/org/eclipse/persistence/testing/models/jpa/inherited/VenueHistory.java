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
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import static javax.persistence.AccessType.PROPERTY;

@Embeddable
@Access(PROPERTY)
public class VenueHistory {
    private Integer yearBuilt;
    private String builder;

    @Column(name="BUILDER")
    public String getBuilder() {
        return builder;
    }

    @Column(name="YEAR_BUILT")
    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }
}
