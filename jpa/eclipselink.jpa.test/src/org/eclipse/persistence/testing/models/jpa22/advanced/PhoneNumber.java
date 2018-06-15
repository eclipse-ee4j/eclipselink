/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.models.jpa22.advanced;

import java.io.Serializable;
import java.io.StringWriter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="JPA22_PHONENUMBER")
@IdClass(PhoneNumberPK.class)
public class PhoneNumber implements Serializable {
    private String number;
    private String type;
    private Employee owner;
    private Integer id;
    private String areaCode;
    
    private PhoneNumberDetails phoneNumberDetails;

    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String number) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = number;
        this.owner = null;
    }

    public PhoneNumberPK buildPK(){
        PhoneNumberPK pk = new PhoneNumberPK();
        pk.setId(this.getOwner().getId());
        pk.setType(this.getType());
        return pk;
    }

    @Column(name="AREA_CODE")
    public String getAreaCode() {
        return areaCode;
    }

    @Id
    @Column(name="OWNER_ID", insertable=false, updatable=false)
    public Integer getId() {
        return id;
    }

    @Column(name="NUMB")
    public String getNumber() {
        return number;
    }

    @ManyToOne
    @JoinColumn(name="OWNER_ID", referencedColumnName="emp_id") // <- this is testing case insensitivity
    public Employee getOwner() {
        return owner;
    }

    @Id
    @Column(name="TYPE")
    public String getType() {
        return type;
    }

    @OneToOne
    @PrimaryKeyJoinColumn(name = "OWNER_ID", referencedColumnName = "PHONE_NUMBER_ID")
    @PrimaryKeyJoinColumn(name = "TYPE", referencedColumnName = "PHONE_NUMBER_TYPE")
    public PhoneNumberDetails getPhoneNumberDetails() {
        return phoneNumberDetails;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setPhoneNumberDetails(PhoneNumberDetails phoneNumberDetails) {
        this.phoneNumberDetails = phoneNumberDetails;
    }

    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber[");
        writer.write(getType());
        writer.write("]: (");
        writer.write(getAreaCode());
        writer.write(") ");

        int numberLength = this.getNumber().length();
        writer.write(getNumber().substring(0, Math.min(3, numberLength)));
        if (numberLength > 3) {
            writer.write("-");
            writer.write(getNumber().substring(3, Math.min(7, numberLength)));
        }

        return writer.toString();
    }
}
