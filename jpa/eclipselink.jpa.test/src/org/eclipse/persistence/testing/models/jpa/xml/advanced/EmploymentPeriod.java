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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.Date;
import java.io.*;

import javax.persistence.Entity;

import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;

/**
 * <p><b>Purpose</b>: Defines the period an Employee worked for the organization
 *    <p><b>Description</b>: The period holds the start date and optionally the 
 *    end date if the employee has left (null otherwise). Maintained in an 
 *    aggregate relationship of Employee
 *    @see Employee
 */
@Properties({
    // This @Property should be overridden by a property with the same name defined in xml.
    @Property(name="ToBeOverriddenByXml", value="false", valueType=Boolean.class),
    // This property should be set - there's no property with the same name defined in xml.
    @Property(name="ToBeProcessed", value="true", valueType=Boolean.class)
})
// This annotation was added to test bug 257756 ... since this class is
// marked as an embeddable in XML, this entity designation should be ignored.
@Entity
public class EmploymentPeriod implements Serializable {
    private Date startDate;
    private Date endDate;

    public EmploymentPeriod() {}

    /**
     * Return a new employment period instance.
     * The constructor's purpose is to allow only valid instances of a class to 
     * be created. Valid means that the get/set and clone/toString methods 
     * should work on the instance. Arguments to constructors should be avoided 
     * unless those arguments are required to put the instance into a valid 
     * state, or represent the entire instance definition.
     */
    public EmploymentPeriod(Date theStartDate, Date theEndDate) {
        startDate = theStartDate;
        endDate = theEndDate;
    }

	public Date getStartDate() { 
        return startDate; 
    }
    
	public void setStartDate(Date date) { 
        this.startDate = date; 
    }

	public Date getEndDate() { 
        return endDate; 
    }
    
	public void setEndDate(Date date) { 
        this.endDate = date; 
    }

    /**
     * Print the start & end date
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("EmploymentPeriod: ");
        
        if (this.getStartDate() != null) {
            writer.write(this.getStartDate().toString());
        }
        
        writer.write("-");
        
        if (this.getEndDate() != null) {
            writer.write(this.getEndDate().toString());
        }
        
        return writer.toString();
    }
}