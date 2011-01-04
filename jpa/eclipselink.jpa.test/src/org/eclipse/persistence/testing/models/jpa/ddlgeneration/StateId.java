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

public class StateId implements Serializable
{
    private String countryCode;

    private String isoCode;

    public StateId()
    {
    }

    public StateId(String countryCode, String isoCode)
    {
        this.countryCode = countryCode;
        this.isoCode = isoCode;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getIsoCode()
    {
        return isoCode;
    }

    public void setIsoCode(String isoCode)
    {
        this.isoCode = isoCode;
    }
}
