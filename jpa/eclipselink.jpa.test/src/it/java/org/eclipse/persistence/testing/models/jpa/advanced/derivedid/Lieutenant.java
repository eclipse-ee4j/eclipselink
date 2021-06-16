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
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This model tests Example #6 of the mapsId cases (mapped from Second Lieutenant).
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_LIEUTENANT")
public class Lieutenant {
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="firstName", column=@Column(name="F_NAME")),
        @AttributeOverride(name="lastName", column=@Column(name="L_NAME"))
    })
    LieutenantId id;

    public LieutenantId getId() {
        return id;
    }

    public void setId(LieutenantId id) {
        this.id = id;
    }
}
