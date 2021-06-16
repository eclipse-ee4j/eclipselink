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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     11/23/2009-2.0 Guy Pelletier
//       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MasterCorporalId {
    @Column(name="NAME")
    String name;

    // In the derived id case from MasterCorporal this column name is ignored
    // In the non derived id case from MasterCorporalClone, this column name is used.
    @Column(name="SARGEANTPK")
    long sargeantPK;

    public String getName() {
        return name;
    }

    public long getSargeantPK() {
        return sargeantPK;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSargeantPK(long sargeantPK) {
        this.sargeantPK = sargeantPK;
    }
}

