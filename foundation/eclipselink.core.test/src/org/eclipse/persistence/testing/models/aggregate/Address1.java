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
public abstract class Address1 {
    //	
    protected int buildingNumber;
    protected String country;
    protected String city;
    protected String streetName;
    protected String postalCode;

    /**
     * This method was created in VisualAge.
     */
    public Address1() {
        super();
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public int getBuildingNumber() {
        return buildingNumber;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getCity() {
        return city;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getCountry() {
        return country;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public void setBuildingNumber(int thisBuildingNumber) {
        buildingNumber = thisBuildingNumber;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public void setCity(String thisCity) {
        city = thisCity;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public void setCountry(String thisCountry) {
        country = thisCountry;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public void setPostalCode(String thisPostalCode) {
        postalCode = thisPostalCode;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public void setStreetName(String thisStreetName) {
        streetName = thisStreetName;
    }
}
