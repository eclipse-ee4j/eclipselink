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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)       
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class Location {
    private String id;
    private String city;
    private String country;
    
    public Location() {}
    
    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
}
