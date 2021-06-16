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
