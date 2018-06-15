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
package org.eclipse.persistence.testing.oxm.inheritance;

import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class InheritanceVehicleDiffPrefixTestCases extends XMLWithJSONMappingTestCases {
    public InheritanceVehicleDiffPrefixTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/vehicle_different_prefix.xml");
        setWriteControlDocument("org/eclipse/persistence/testing/oxm/inheritance/vehicle.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/inheritance/vehicle.json");
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.InheritanceVehicleDiffPrefixTestCases" };
        TestRunner.main(arguments);
    }

    public Object getControlObject() {
        Vehicle vehicle = new Vehicle();
        vehicle.model = "Blah Blah";
        vehicle.manufacturer = "Some Place";
        vehicle.topSpeed = 10000;
        return vehicle;
    }

}
