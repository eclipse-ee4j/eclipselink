/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     08/13/2010-2.2 Guy Pelletier
//       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GolfClubPK {
    @Column(name="HEAD_ID")
    private int headId;

    @Column(name="SHAFT_ID")
    private int shaftId;

    public int getHeadId() {
        return headId;
    }

    public int getShaftId() {
        return shaftId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public void setShaftId(int shaftId) {
        this.shaftId = shaftId;
    }
}
