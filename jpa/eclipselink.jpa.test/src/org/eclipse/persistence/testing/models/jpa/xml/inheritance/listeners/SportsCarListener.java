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


package org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners;

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

    public void preRemove(Object sportsCar) {
        PRE_REMOVE_COUNT++;
    }

    public void postRemove(Object sportsCar) {
        POST_REMOVE_COUNT++;
    }

    public void preUpdate(Object sportsCar) {
        PRE_UPDATE_COUNT++;
    }

    public void postUpdate(Object sportsCar) {
        POST_UPDATE_COUNT++;
    }
}
