/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
