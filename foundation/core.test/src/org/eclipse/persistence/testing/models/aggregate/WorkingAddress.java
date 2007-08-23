/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

        //	example.setApartmentNumber(700);
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

        //	example.setApartmentNumber(750);
        example.setBuildingNumber(220);
        example.setCity("Ottawa");
        example.setCountry("Canada");
        example.setStreetName("Woodridge");
        example.setPostalCode("k2b1g9");
        return example;
    }
}