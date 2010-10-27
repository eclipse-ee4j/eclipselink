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
 *     10/28/2010-2.2 Guy Pelletier 
 *       - 3223850: Primary key metadata issues
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.persistence.*;
import static javax.persistence.EnumType.STRING;

import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * <p>
 * <b>Purpose</b>: Describes an Employee's phone number.
 * <p>
 * <b>Description</b>: Used in a 1:M relationship from an employee. Test @PrimaryKey
 * support with composite primary key.
 */
@SuppressWarnings("deprecation")
@Entity(name = "PhoneNumber")
@Table(name = "CMP3_FA_PHONENUMBER")
@PrimaryKey(columns = { @Column(name = "OWNER_ID"), @Column(name = "TYPE") })
public class PhoneNumber extends PhoneNumberMappedSuperclass implements Serializable {
    public enum PhoneStatus {
        ACTIVE, ASSIGNED, UNASSIGNED, DEAD
    }

    @Column(name = "NUMB")
    private String number;
    @Column(name = "TYPE")
    private String type;
    @ManyToOne
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "EMP_ID")
    private Employee owner;
    @Column(name = "AREA_CODE")
    private String areaCode;

    @BasicCollection
    @CollectionTable(name = "CMP3_FA_PHONE_STATUS")
    @Enumerated(STRING)
    private Collection<PhoneStatus> status;

    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = null;
        this.status = new ArrayList<PhoneStatus>();
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

    // Basic collection on an entity that uses a composite primary key.
    // We don't specify any of the primary key join columns on the collection
    // table because they should all default accordingly.
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
    public List buildPK() {
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
        if (!(this instanceof FetchGroupTracker)) {
            writer.write(getAreaCode());
            writer.write(") ");

            int numberLength = getNumber().length();
            writer.write(getNumber().substring(0, Math.min(3, numberLength)));
            if (numberLength > 3) {
                writer.write("-");
                writer.write(getNumber().substring(3, Math.min(7, numberLength)));
            }
        }

        return writer.toString();
    }
}
