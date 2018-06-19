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
public class Bicycle extends Vehicle {
    public String description;

    /**
     * Bicycle constructor comment.
     */
    public Bicycle() {
        super();
    }

    public static Bicycle example1() {
        Bicycle example = new Bicycle();

        example.setCapacity(1);
        example.setDescription("Shiney Red Schwinn");
        example.setColour("Red");

        return example;
    }

    public static Bicycle example2() {
        //        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("INSERT INTO AGGVEHICLE VALUES (4, 2, 'Blue', 1, 'Ten speed', NULL, NULL)"));
        Bicycle example = new Bicycle();

        example.setCapacity(1);
        example.setDescription("Mountain Bike");
        example.setColour("Green");

        return example;
    }

    public static Bicycle example3() {
        Bicycle example = new Bicycle();
        example.setCapacity(1);
        example.setDescription("Ten speed");
        example.setColour("Blue");
        return example;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue java.lang.String
     */
    public void setDescription(String newValue) {
        this.description = newValue;
    }
}
