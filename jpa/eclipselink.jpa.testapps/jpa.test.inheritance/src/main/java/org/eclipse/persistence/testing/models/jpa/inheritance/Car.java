/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_FUEL_VEH")
@DiscriminatorValue("C")
public class Car extends FueledVehicle {
    public static int PRE_PERSIST_COUNT = 0;

    @Override
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
