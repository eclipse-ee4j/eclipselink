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
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostUpdate;

/**
 * A listener for the Bus entity.
 *
 * It implements the following annotations:
 * - PreRemove
 * - PostRemove
 * - PreUpdate
 * - PostUpdate
 *
 * It overrides the following annotations:
 * - PrePersist from ListenerSuperclass
 * - PostPersist from FueledVehicleListener
 *
 * It inherits the following annotations:
 * - PostLoad from Vehicle.
 */
public class BusListener extends ListenerSuperclass {
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;

    @Override
    @PrePersist
    public void prePersist(Object bus) {
        PRE_PERSIST_COUNT++;
    }

    @PostPersist
    // Protected access
    protected void postPersist(Object bus) {
        POST_PERSIST_COUNT++;
    }

    @PreRemove
    public void preRemove(Object bus) {
        PRE_REMOVE_COUNT++;
    }

    @PostRemove
    public void postRemove(Object bus) {
        POST_REMOVE_COUNT++;
    }

    @PreUpdate
    // Package access
    void preUpdate(Object bus) {
        PRE_UPDATE_COUNT++;
    }

    @PostUpdate
    // Private access
    private void postUpdate(Object bus) {
        POST_UPDATE_COUNT++;
    }
}
