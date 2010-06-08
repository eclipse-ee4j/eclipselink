/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;


/**
 * This type was created in VisualAge.
 */
public class HomeAddress extends Address1 {
    protected int apartmentNumber;

    /**
     * HomeAddress constructor comment.
     */
    public HomeAddress() {
        super();
    }

    /**
     * This method was created in VisualAge.
     */
    public static HomeAddress example1() {
        HomeAddress example = new HomeAddress();
        example.setApartmentNumber(714);
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
    public static HomeAddress example2() {
        HomeAddress example = new HomeAddress();
        example.setApartmentNumber(715);
        example.setBuildingNumber(210);
        example.setCity("Ottawa");
        example.setCountry("Canada");
        example.setStreetName("Woodridge");
        example.setPostalCode("k2b1g9");
        return example;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public int getApartmentNumber() {
        return apartmentNumber;
    }

    /**
     * This method was created in VisualAge.
     */
    public void setApartmentNumber(int thisApartmentNumber) {
        apartmentNumber = thisApartmentNumber;
    }
}
