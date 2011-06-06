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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.*;
import javax.persistence.*;

/**
 * <p><b>Purpose</b>: Defines the former employment of an Employee.
 *    <p><b>Description</b>: The former employment holds the name of the
 *    former company and an EmploymentPeriod. Maintained in an 
 *    aggregate relationship of Employee
 *    @see Employee
 */
@Embeddable
@Table(name="CMP3_FA_EMPLOYEE")
public class FormerEmployment implements Serializable {
	@Basic
    private String formerCompany;
	@Embedded
    private EmploymentPeriod period;

    public FormerEmployment() {}

    public FormerEmployment(String company, EmploymentPeriod period) {
        this.formerCompany = company;
        this.period = period;
    }
    
    public String getFormerCompany() { 
        return formerCompany; 
    }
    
    public void setFormerCompany(String company) { 
        this.formerCompany = company; 
    }
    
    public EmploymentPeriod getPeriod() { 
        return period; 
    }
    
    public void setPeriod(EmploymentPeriod period) { 
        this.period = period; 
    }
    

    /**
     * Print the company and the period
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("FormerEmployment: ");
        
        if (this.getFormerCompany() != null) {
            writer.write(this.getFormerCompany().toString());
        }
        
        writer.write(",");
        
        if (this.getPeriod() != null) {
            writer.write(this.getPeriod().toString());
        }
        
        return writer.toString();
    }
}
