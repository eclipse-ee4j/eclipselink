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

import java.util.EventListener;
import javax.persistence.PostPersist;

/**
 * A listener for the FueledVehicle entity.
 *
 * It implements the following annotations:
 * - PostPersist
 *
 * It overrides the following annotations:
 * - None
 *
 * It inherits the following annotations:
 * - PostLoad from Vehicle.
 */
public class FueledVehicleListener implements EventListener {
    public static int POST_PERSIST_COUNT = 0;

    @PostPersist
    public void postPersist(Object fueledVehicle) {
        POST_PERSIST_COUNT++;
    }
}
