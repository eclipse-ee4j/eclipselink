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

import javax.persistence.*;

@Entity
@Table(name="CMP3_FUEL_VEH")
@DiscriminatorValue("C")
public class Car extends FueledVehicle {
    public static int PRE_PERSIST_COUNT = 0;

    public Object clone() {
        Object clone =  super.clone();
        clone.toString();
        return clone;
    }

    @PrePersist
    public void prePersist() {
        ++PRE_PERSIST_COUNT;
    }
}
