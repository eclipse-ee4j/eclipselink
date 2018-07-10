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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Embedded;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_MUD_TIRE")
@DiscriminatorValue("Mud")
public class MudTireInfo extends OffRoadTireInfo {
    protected TireRating tireRating;
    protected int treadDepth;

    public MudTireInfo() {}

    @Column(name="TREAD_DEPTH")
    public int getTreadDepth() {
        return treadDepth;
    }

    public void setTreadDepth(int treadDepth) {
        this.treadDepth = treadDepth;
    }

    @Embedded
    public TireRating getTireRating() {
        return tireRating;
    }

    public void setTireRating(TireRating rating) {
        this.tireRating = rating;
    }
}
