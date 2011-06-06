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
package org.eclipse.persistence.testing.models.performance;

import java.io.*;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 * <p><b>Description</b>: Used in a 1:M relationship from an employee. Since many people have various numbers
 * they can be contacted at the type describes where the phone number could reach the Employee.
 */
public class PhoneNumber implements Serializable {

    /** Sequence id, added for Hibernate as it has issues with composite ids and caching. */
    protected long id;
    
    /** Holds values such as Home, Work, Cellular, Pager, Fax, etc.  Since the combination of the Employee's ID and
    the type field are what makes the entry in the database unique the type fields must be unique within an
    Employee's Vector of PhoneNumbers.*/
    protected String type;
    protected String areaCode;

    /** 7 digit number with no hyphen, this is added during toString() only*/
    protected String number;

    /** Owner maintains the 1:1 mapping to an Employee (required for 1:M relationship in Employee) */
    protected Employee owner;

    public PhoneNumber() {
        this("home", "###", "#######");
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

    public Employee getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }
    
    public long getId() {
        return id;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Required for Hibernate cache to work.
     */
    public int hashCode() {
        return getType().hashCode();
    }

    /**
     * Required for Hibernate cache to work.
     */
    public boolean equals(Object object) {
        if (object instanceof PhoneNumber) {
            PhoneNumber phone = (PhoneNumber)object;
            if (! getType().equals(phone.getType())) {
                return false;
            }
            if ((getOwner() == null) || (phone.getOwner() == null) || (getOwner().getId() != phone.getOwner().getId())) {
                return false;
            }
            return true;
        } else {
            return false;
        }
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
        writer.write(this.getNumber().substring(0, 3));
        writer.write("-");
        writer.write(this.getNumber().substring(3, 7));
        return writer.toString();
    }
}
