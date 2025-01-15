/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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


package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import jakarta.persistence.PostPersist;

import java.util.EventListener;

/**
 * A listener for the FueledVehicle entity.
 * <p>
 * It implements the following annotations:
 * - PostPersist
 * <p>
 * It overrides the following annotations:
 * - None
 * <p>
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
