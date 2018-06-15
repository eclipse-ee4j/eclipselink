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
//     Denise Smith - January 4th, 2010 - 2.0.1
package org.eclipse.persistence.testing.oxm.inheritance;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class InheritanceNoRootOnChildTestCases extends XMLWithJSONMappingTestCases {

      public InheritanceNoRootOnChildTestCases(String name) throws Exception {
            super(name);
            setProject(new InheritanceProject());
            setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
            setControlJSON("org/eclipse/persistence/testing/oxm/inheritance/car.json");
      }

      public Object getControlObject() {
            Car car = new Car();
            car.numberOfDoors = 2;
            car.milesPerGallon = 30;
            car.model = "Grand Am";
            car.manufacturer = "Pontiac";
            car.topSpeed = 220;

            return car;
        }

}
