/*******************************************************************************
 * Copyright (c) 2011 Karsten Wutzke. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/04/2011-2.3 Guy Pelletier for Karsten Wutzke 
 *       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

public class ZipAreaId implements Serializable
{
    private String countryCode;

    private String zipCode;

    private String stateCode;

    private String cityName;

    public ZipAreaId()
    {
    }

    public ZipAreaId(String countryCode, String zipCode, String stateCode, String cityName)
    {
        this.countryCode = countryCode;
        this.zipCode = zipCode;
        this.stateCode = stateCode;
        this.cityName = cityName;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getStateCode()
    {
        return stateCode;
    }

    public void setStateCode(String stateCode)
    {
        this.stateCode = stateCode;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }
}
