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
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)  
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.sql.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.CascadeType.PERSIST;

@Embeddable
public class Record {
    @Column(name="R_DATE")
    private Date date;
    
    @Column(name="DESCR")
    private String description;
    
    @ManyToOne(cascade=PERSIST)
    @JoinColumn(name="L_ID")
    private Location location;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="VENUE_NAME")),
        @AttributeOverride(name="attendance", column=@Column(name="VENUE_ATTENDANCE"))
    })
    // Novice beer consumer is going to override VENUE_NAME to VENUE and use
    // VENUE_ATTENDANCE as is.
    // Expert beer consumer is going to override VENUE_ATTENDANCE to WITNESSES
    // and user VENUE_NAME as is.
    private Venue venue;

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Venue getVenue() {
        return venue;
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
    
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}
