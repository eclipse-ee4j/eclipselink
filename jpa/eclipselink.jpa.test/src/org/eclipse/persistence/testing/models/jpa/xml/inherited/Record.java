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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)       
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.sql.Date;

public class Record {
    private Date date;
    private String description;
    private Location location;
    
    public Date getDate() {
        return date;
    }
    
    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
}
