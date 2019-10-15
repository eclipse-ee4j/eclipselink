/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.*;
import java.util.Collection;
import java.util.Vector;

import javax.persistence.*;
import static javax.persistence.EnumType.STRING;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 *    <p><b>Description</b>: Used in a 1:M relationship from an employee.
 */
@SuppressWarnings("deprecation")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumberPK.class)
@Entity
@Table(name="CMP3_PHONENUMBER")
public class PhoneNumber implements Serializable {
    public enum PhoneStatus { ACTIVE, ASSIGNED, UNASSIGNED, DEAD }

    private String number;
    private String type;
    private Employee owner;
    private Integer id;
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

    @Id
    @Column(name="OWNER_ID", insertable=false, updatable=false)
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

    // Basic collection on an entity that uses a composite primary key.
    // We don't specify any of the primary key join columns on the collection
    // table because they should all default accordingly.
    @BasicCollection
    @CollectionTable(name="CMP3_PHONE_STATUS")
    @Enumerated(STRING)
    public Collection<PhoneStatus> getStatus() {
        return status;
    }

    public void setStatus(Collection<PhoneStatus> status) {
        this.status = status;
    }

    @Id
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

    @ManyToOne
    @JoinColumn(name="OWNER_ID", referencedColumnName="emp_id") // <- this is testing case insensitivity
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

    /**
     * Builds the PhoneNumberPK for this class
     */
    public PhoneNumberPK buildPK(){
        PhoneNumberPK pk = new PhoneNumberPK();
        pk.setId(this.getOwner().getId());
        pk.setType(this.getType());
        return pk;
    }
}
