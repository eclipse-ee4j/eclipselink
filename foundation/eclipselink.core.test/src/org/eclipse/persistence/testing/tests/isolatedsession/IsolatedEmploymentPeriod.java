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
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.sql.Date;

import java.io.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * <p><b>Purpose</b>: Defines the period an Employee worked for the organization
 *    <p><b>Description</b>: The period holds the start date and optionally the end date if the employee has left (null otherwise).
 *                                Maintained in an aggregate relationship of Employee
 *    @see Employee
 */
public class IsolatedEmploymentPeriod implements Serializable, ChangeTracker {
    // implements ChangeTracker for testing
    public Date startDate;
    public Date endDate;
    public PropertyChangeListener listener;

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

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType));
        }
    }

    public IsolatedEmploymentPeriod() {
    }

    /**
     * Return a new employment period instance.
     * The constructor's purpose is to allow only valid instances of a class to be created.
     * Valid means that the get/set and clone/toString methods should work on the instance.
     * Arguments to constructors should be avoided unless those arguments are required to put
     * the instance into a valid state, or represent the entire instance definition.
     */
    public IsolatedEmploymentPeriod(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        propertyChange("endDate", this.endDate, endDate);
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        propertyChange("startDate", this.startDate, startDate);
        this.startDate = startDate;
    }

    /**
     * Print the start & end date
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("IsolatedEmploymentPeriod: ");
        if (getStartDate() != null) {
            writer.write(getStartDate().toString());
        }
        writer.write("-");
        if (getEndDate() != null) {
            writer.write(getEndDate().toString());
        }
        return writer.toString();
    }

    public static IsolatedEmploymentPeriod EmploymentPeriodExample1() {
        IsolatedEmploymentPeriod employmentPeriod = new IsolatedEmploymentPeriod();
        java.util.Calendar startCalendar = java.util.Calendar.getInstance();
        startCalendar.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Calendar endCalendar = java.util.Calendar.getInstance();
        endCalendar.set(java.util.Calendar.MILLISECOND, 0);
        startCalendar.set(1996, 0, 1, 0, 0, 0);
        endCalendar.set(1993, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public static IsolatedEmploymentPeriod EmploymentPeriodExample2() {
        IsolatedEmploymentPeriod employmentPeriod = new IsolatedEmploymentPeriod();
        java.util.Calendar startCalendar = java.util.Calendar.getInstance();
        startCalendar.set(java.util.Calendar.MILLISECOND, 0);
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }
}
