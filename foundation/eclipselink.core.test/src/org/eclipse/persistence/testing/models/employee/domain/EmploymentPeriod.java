/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.employee.domain;

import java.sql.Date;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * <p><b>Purpose</b>: Defines the period an Employee worked for the organization
 * <p><b>Description</b>: The period holds the start date and optionally the end date if the employee has left (null otherwise).
 * Maintained in an aggregate relationship of Employee
 * @see Employee
 */
public class EmploymentPeriod implements Serializable, ChangeTracker, Cloneable {
    // implements ChangeTracker for testing
    public Date startDate;
    public Date endDate;
    public PropertyChangeListener listener;

    public EmploymentPeriod() {
    }

    /**
     * Return a new employment period instance.
     * The constructor's purpose is to allow only valid instances of a class to be created.
     * Valid means that the get/set and clone/toString methods should work on the instance.
     * Arguments to constructors should be avoided unless those arguments are required to put
     * the instance into a valid state, or represent the entire instance definition.
     */
    public EmploymentPeriod(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

    }
    
    public Object clone() 
    {
        try {
            return super.clone();
        } catch (Exception ignore) {}
        return null;
    }
    
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listener != null) {
            if (oldValue != newValue) {
                listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType, isChangeApplied));
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        Date oldDate = this.endDate;
        this.endDate = endDate;
        propertyChange("endDate", oldDate, endDate);
    }

    public void setStartDate(Date startDate) {
        Date oldDate = this.startDate;
        this.startDate = startDate;
        propertyChange("startDate", oldDate, startDate);
    }

    /**
     * Print the start & end date
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("EmploymentPeriod: ");
        if (getStartDate() != null) {
            writer.write(getStartDate().toString());
        }
        writer.write("-");
        if (getEndDate() != null) {
            writer.write(getEndDate().toString());
        }
        return writer.toString();
    }
    
    public boolean equals(Object period){
        if (this == period){
            return true;
        }
        if (period == null || !(period instanceof EmploymentPeriod)){
            return false;
        }
        EmploymentPeriod empPeriod = (EmploymentPeriod)period;
        if ((getStartDate() == null && empPeriod.getStartDate() != null) || (getStartDate() != null && empPeriod.getStartDate() == null)){
            return false;
        }
        if ((getEndDate() == null && empPeriod.getEndDate() != null) || (getEndDate() != null && empPeriod.getEndDate() == null)){
            return false;
        }
        boolean match = (getStartDate() == null && empPeriod.getStartDate() == null) || (getStartDate().equals(empPeriod.getStartDate()));
        match = match && ((getEndDate() == null && empPeriod.getEndDate() == null) || getEndDate().equals(empPeriod.getEndDate()));
        return  match;
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (this.getStartDate() != null ? this.getStartDate().hashCode() : 0);
        hash += (this.getEndDate() != null ? this.getEndDate().hashCode() : 0);
        return hash;
    }

}
