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
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     02/18/2010-2.0.2 Guy Pelletier 
 *       - 294803: @Column(updatable=false) has no effect on @Basic mappings
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ServiceTime {
    @Column(name="END_DATE", insertable=false, updatable=true)
    private String endDate;
    
    @Column(name="START_DATE", updatable=false)
    private String startDate;

    public ServiceTime() {}

    public String getEndDate() { 
        return endDate; 
    }
    
    public String getStartDate() { 
        return startDate; 
    }
    
    public void setEndDate(String endDate) { 
        this.endDate = endDate; 
    }
    
    public void setStartDate(String startDate) { 
        this.startDate = startDate; 
    }

    public String toString() {
        return "ServiceTime: " + "[" + startDate + "] - [" + endDate + "]";
    }
}

