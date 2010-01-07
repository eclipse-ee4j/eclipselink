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
package org.eclipse.persistence.testing.models.nativeapitest;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 * <p><b>Description</b>: Used in a 1:M relationship from an employee.
 * Test @PrimaryKey support with composite primary key.
 */
public class PhoneNumber implements Serializable {
    public enum PhoneStatus { ACTIVE, ASSIGNED, UNASSIGNED, DEAD }
    
    private String number;
    private String type;
    private Employee owner;
    private String areaCode;
    private Collection<PhoneStatus> status;
    
    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = null;
        this.status = new Vector<PhoneStatus>();
    }

    public void addStatus(PhoneStatus status) {
        getStatus().add(status);
    }
    
    public String getNumber() { 
        return number; 
    }
    
    public void setNumber(String number) { 
        this.number = number; 
    }
    
    public Collection<PhoneStatus> getStatus() {
        return status;
    }
    
    public void setStatus(Collection<PhoneStatus> status) {
        this.status = status;
    }
        
    public String getType() { 
        return type; 
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAreaCode() { 
        return areaCode; 
    }
    
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    
    public Employee getOwner() { 
        return owner; 
    }
    
    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    public void removeStatus(PhoneStatus status) {
        getStatus().remove(status);
    }
    
    /**
     * Uses a Vector as its primary key.
     */
    public List buildPK(){
        List pk = new Vector();
        pk.add(getOwner().getId());
        pk.add(getType());
        return pk;
    }
    
    /**
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber[");
        writer.write(getType());
        writer.write("]: (");
        writer.write(getAreaCode());
        writer.write(") ");

        int numberLength = getNumber().length();
        writer.write(getNumber().substring(0, Math.min(3, numberLength)));
        if (numberLength > 3) {
            writer.write("-");
            writer.write(getNumber().substring(3, Math.min(7, numberLength)));
        }

        return writer.toString();
    }
}
