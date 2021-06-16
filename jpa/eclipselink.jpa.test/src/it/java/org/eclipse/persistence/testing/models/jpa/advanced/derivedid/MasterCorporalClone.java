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
//     11/23/2009-2.0 Guy Pelletier
//       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This model tests Example #1 of the mapsId cases.
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_MASTER_CORPORAL_CLONE")
public class MasterCorporalClone {
    @EmbeddedId
    MasterCorporalId id;

    public MasterCorporalId getId() {
        return id;
    }

    public void setId(MasterCorporalId id) {
        this.id = id;
    }
}

