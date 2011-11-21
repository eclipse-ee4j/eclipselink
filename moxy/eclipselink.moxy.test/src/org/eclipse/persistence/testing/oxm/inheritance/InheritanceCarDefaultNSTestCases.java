/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - November 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.inheritance;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class InheritanceCarDefaultNSTestCases extends XMLWithJSONMappingTestCases {
    public InheritanceCarDefaultNSTestCases(String name) throws Exception {
        super(name);
        Project p = new InheritanceProject(true);
        
        setProject(p);
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/car_defaultNS.xml");
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
