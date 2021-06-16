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
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import static jakarta.persistence.AccessType.PROPERTY;

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
