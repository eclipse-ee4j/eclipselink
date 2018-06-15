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
import javax.persistence.PostLoad;
import org.eclipse.persistence.testing.models.jpa.inheritance.Vehicle;

/**
 * A listener for the Vehicle entity.
 *
 * It implements the following annotations:
 * - PostLoad
 *
 * It overrides the following annotations:
 * - None
 *
 * It inherits the following annotations:
 * - None
 */
public class VehicleListener implements EventListener {
    public static int POST_LOAD_COUNT = 0;

    @PostLoad
    public void postLoad(Vehicle vehicle) {
        POST_LOAD_COUNT++;
    }
}
