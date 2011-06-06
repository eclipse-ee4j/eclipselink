/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 5/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.sessions.Record;

/**
 *  A simple class that contains a transformation mapping;
 */
public class Employee {
    protected String name;
    protected String[] normalHours;

    public Employee() {
        normalHours = new String[2];
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String[] getNormalHours() {
        return normalHours;
    }

    public void setNormalHours(String[] newHours) {
        normalHours = newHours;
    }

    @XmlTransient
    public String getStartTime() {
        return normalHours[0];
    }

    @XmlTransient
    public void setStartTime(String startTime) {
        normalHours[0] = startTime;
    }

    @XmlTransient
    public String getEndTime() {
        return normalHours[1];
    }

    @XmlTransient
    public void setEndTime(String endTime) {
        normalHours[1] = endTime;
    }

    /**
     * Used for testing method name based field transformer.
     */
    public String buildEndTimeValue() {
        return normalHours[1];
    }
    
    /**
     * Used for testing method name based attribute transformer.
     */
    public String[] buildNormalHoursValue(Record record) {
        String[] hours = new String[2];
        hours[0] = (String) record.get("normal-hours/start-time/text()");
        hours[1] = (String) record.get("normal-hours/end-time/text()");
        return hours;
    }
    
    @XmlTransient
    /**
     * Used for testing method name based attribute transformer exception
     * handling.  This method is invalid as it should have 0 params, or
     * 1 param (AbstractSession or Session)
     */
    public Object invalidTransformerMethod(String someParam) {
        return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;
        if (getStartTime() == emp.getStartTime() && getEndTime() == emp.getEndTime()) {
            return true;
        }
        return (getStartTime().equalsIgnoreCase(emp.getStartTime()) && getEndTime().equalsIgnoreCase(emp.getEndTime()) && getName().equalsIgnoreCase(emp.getName()));
    }

    public String toString() {
        return "Name: " + getName() + "/Start: " + getStartTime() + "/End: " + getEndTime();
    }
}
