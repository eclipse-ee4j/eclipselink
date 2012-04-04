/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/16/2010-2.2 Guy Pelletier 
 *       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
 *     04/04/2012-2.3.3 Guy Pelletier 
 *       - 362180: ConcurrentModificationException on predeploy for AttributeOverride
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class TrackableTime {
    @Embedded
    private EmbeddedTimeCaption embeddedTimeCaption;

    @Column(name="ENDDATE")
    private String endDate;
    
    @Column(name="STARTDATE")
    private String startDate;

    public TrackableTime() {}

    public EmbeddedTimeCaption getEmbeddedTimeCaption() {
        return embeddedTimeCaption;
    }
    
    public String getEndDate() { 
        return endDate; 
    }
    
    public String getStartDate() { 
        return startDate; 
    }
    
    public void setEmbeddedTimeCaption(EmbeddedTimeCaption embeddedTimeCaption) {
        this.embeddedTimeCaption = embeddedTimeCaption;
    }
    
    public void setEndDate(String endDate) { 
        this.endDate = endDate; 
    }
    
    public void setStartDate(String startDate) { 
        this.startDate = startDate; 
    }

    public String toString() {
        return "TrackableTime: " + "[" + startDate + "] - [" + endDate + "]";
    }
}