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


package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;

/**
 * A listener for the SportsCar entity.
 *
 * It implements the following annotations:
 * - PreRemove
 * - PostRemove
 * - PreUpdate
 * - PostUpdate
 *
 * It overrides the following annotations:
 * - None
 *
 * It inherits the following annotations:
 * - PostLoad from Vehicle.
 * - PrePersist from ListenerSuperclass
 * - PostPersist from FueledVehicleListener
 */
public class SportsCarListener extends ListenerSuperclass {
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;

    @PreRemove
    public void preRemove(Object sportsCar) {
        PRE_REMOVE_COUNT++;
    }

    @PostRemove
    public void postRemove(Object sportsCar) {
        POST_REMOVE_COUNT++;
    }

    @PreUpdate
    public void preUpdate(Object sportsCar) {
        PRE_UPDATE_COUNT++;
    }

    @PostUpdate
    public void postUpdate(Object sportsCar) {
        POST_UPDATE_COUNT++;
    }
}
