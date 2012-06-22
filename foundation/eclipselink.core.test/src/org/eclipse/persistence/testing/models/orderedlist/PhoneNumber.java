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
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.io.StringWriter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 * <p><b>Description</b>: Used in a AggregateCollection relationship from an employee. Since many people have various numbers
 * they can be contacted at the type describes where the phone number could reach the Employee.
 */
public class PhoneNumber implements ChangeTracker {
    // implements ChangeTracker for testing

    public String type;
    public String areaCode;

    /** 7 digit number with no hyphen, this is added during toString() only*/
    public String number;

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

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType, isChangeApplied));
        }
    }

    public PhoneNumber() {
        this("home", "###", "#######");
    }

    public PhoneNumber(String theAreaCode, String theNumber) {
        this("home", theAreaCode, theNumber);
    }
    
    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }


    public String getType() {
        return type;
    }

    public void setAreaCode(String areaCode) {
        propertyChange("areaCode", this.areaCode, areaCode);
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        propertyChange("number", this.number, number);
        this.number = number;
    }


    public void setType(String type) {
        propertyChange("type", this.type, type);
        this.type = type;
    }

    /**
     * Print the phone.
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber [");
        writer.write(getType());
        writer.write("]: (");
        writer.write(this.getAreaCode());
        writer.write(")");
        writer.write(this.getNumber().substring(0, Math.min(3, this.getNumber().length())));
        writer.write("-");
        writer.write(this.getNumber().substring(Math.min(3, this.getNumber().length()), Math.min(7, this.getNumber().length())));
        return writer.toString();
    }
}
