/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
