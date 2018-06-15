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
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_ROCK_TIRE")
@DiscriminatorValue("Rock")
public class RockTireInfo extends OffRoadTireInfo {
    public enum Grip { REGULAR, SUPER, MEGA }

    protected Grip grip;

    public RockTireInfo() {}

    public Grip getGrip() {
        return grip;
    }

    public void setGrip(Grip grip) {
        this.grip = grip;
    }
}
