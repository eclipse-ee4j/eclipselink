/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/06/2011-2.3.1 Guy Pelletier 
 *       - 349906: NPE while using eclipselink in the application
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import java.util.Date;

/**
 * This class is mapped (as mapped-superclass) in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-superclasses.xml
 */
public class WorkPeriod {
    private Date startDate;
    private Date endDate;

    public WorkPeriod() {}

    public WorkPeriod(Date theStartDate, Date theEndDate) {
        startDate = theStartDate;
        endDate = theEndDate;
    }

    public Date getEndDate() { 
        return endDate; 
    }
    
    public Date getStartDate() { 
        return startDate; 
    }
    
    public void setEndDate(Date date) { 
        this.endDate = date; 
    }
    
    public void setStartDate(Date date) { 
        this.startDate = date; 
    }

}
