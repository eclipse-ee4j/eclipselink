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
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name="employee")
public class EmployeeTwoDates {
    
    @XmlElement(name="start-date")
    @XmlSchemaType(name = "date")
    public java.util.Calendar startDate;
    
    @XmlElement(name="start-time")
    public Calendar startTime;
    
    public boolean equals(Object o) {
        if(!(o instanceof EmployeeTwoDates) || o == null) {
            return false;
        } else {
            return ((EmployeeTwoDates)o).startDate.equals(this.startDate) && ((EmployeeTwoDates)o).startTime.equals(startTime);
        }
    }
    
    public String toString() {
        return "EMPLOYEE(" + startDate + " - " + startTime + ")";
    }
}
