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
 *     tware - initial implementation as part of extensibility feature
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.extensibility;

import java.io.*;
import java.util.Collection;
import java.util.Vector;

import javax.persistence.*;
import static javax.persistence.EnumType.STRING;

import org.eclipse.persistence.queries.FetchGroupTracker;
/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 *    <p><b>Description</b>: Used in a 1:M relationship from an employee.
 */
@Entity(name="ExtensibilityPhone")
//@Extensible
@Table(name="EXTENS_PHONE")
public class PhoneNumber implements Serializable {
    
    private String number;
    private String type;
    private Integer id;
    private String areaCode;
    private long version;
    
    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
    }

    
    @Id
    @GeneratedValue
    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="NUMB")
    public String getNumber() { 
        return number; 
    }
    
    public void setNumber(String number) { 
        this.number = number; 
    }
    
    @Column(name="TYPE")
    public String getType() { 
        return type; 
    }
    
    public void setType(String type) {
        this.type = type;
    }

    @Column(name="AREA_CODE")
    public String getAreaCode() { 
        return areaCode; 
    }
    
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Version
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    /**
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber[");
        writer.write(getType());
        writer.write("]: (");
        if(!(this instanceof FetchGroupTracker)) {
            writer.write(getAreaCode());
            writer.write(") ");
    
            int numberLength = this.getNumber().length();
            writer.write(getNumber().substring(0, Math.min(3, numberLength)));
            if (numberLength > 3) {
                writer.write("-");
                writer.write(getNumber().substring(3, Math.min(7, numberLength)));
            }
        }

        return writer.toString();
    }

}
