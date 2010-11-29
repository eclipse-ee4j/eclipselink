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
package org.eclipse.persistence.testing.models.jpa.partitioned;

import java.io.*;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * <p>
 * <b>Purpose</b>: Describes an Employee's phone number.
 * <p>
 * <b>Description</b>: Used in a 1:M relationship from an employee.
 */
@Entity
@Table(name = "PART_PHONENUMBER")
@IdClass(PhoneNumberPK.class)
@Partitioned("ValuePartitioningByLOCATION")
public class PhoneNumber implements Serializable {
    @Column(name = "NUMB")
    private String number;
    
    @Id
    @Column(name = "TYPE")
    private String type;
    
    @Id
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "OWNER_ID", referencedColumnName = "EMP_ID"),
        @JoinColumn(name = "LOCATION", referencedColumnName = "LOCATION")})
    private Employee owner;
    
    @Column(name = "AREA_CODE")
    private String areaCode;

    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = null;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    /**
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber[");
        writer.write(getType());
        writer.write("]: (");
        if (!(this instanceof FetchGroupTracker)) {
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
