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
package org.eclipse.persistence.testing.models.aggregate;


/**
 * This type was created in VisualAge.
 */
public class WorkingAddress extends Address1 {

    /**
     * WorkingAddress constructor comment.
     */
    public WorkingAddress() {
        super();
    }

    /**
     * This method was created in VisualAge.
     */
    public static WorkingAddress example1() {
        WorkingAddress example = new WorkingAddress();

        //    example.setApartmentNumber(700);
        example.setBuildingNumber(220);
        example.setCity("Ottawa");
        example.setCountry("Canada");
        example.setStreetName("Woodridge");
        example.setPostalCode("k2b1g9");
        return example;
    }

    /**
     * This method was created in VisualAge.
     */
    public static WorkingAddress example2() {
        WorkingAddress example = new WorkingAddress();

        //    example.setApartmentNumber(750);
        example.setBuildingNumber(220);
        example.setCity("Ottawa");
        example.setCountry("Canada");
        example.setStreetName("Woodridge");
        example.setPostalCode("k2b1g9");
        return example;
    }
}
