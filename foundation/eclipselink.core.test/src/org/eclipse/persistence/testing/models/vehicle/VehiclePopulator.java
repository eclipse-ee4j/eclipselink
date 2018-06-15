/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Vikram Bhatia - initial API and implementation
package org.eclipse.persistence.testing.models.vehicle;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve objects as the vehicle objects are an interconnection graph of objects.
 */
public class VehiclePopulator {
    protected PopulationManager populationManager;

    public VehiclePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
    }

    /**
     * Call all of the example methods in this system to guarantee that all our objects
     * are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no previous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(CarOwner.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(SportsCar.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(EngineType.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(FuelType.class);

        populationManager.registerObject(CarOwner.class, vehicleExample1(), "1");
    }

    public CarOwner vehicleExample1() {
        return CarOwner.example1();
    }
}
