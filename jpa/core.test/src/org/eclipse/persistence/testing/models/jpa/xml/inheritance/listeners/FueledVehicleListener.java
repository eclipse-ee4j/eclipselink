/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners;

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
