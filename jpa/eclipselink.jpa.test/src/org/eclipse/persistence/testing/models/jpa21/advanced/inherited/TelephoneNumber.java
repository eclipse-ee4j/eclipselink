/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa21.advanced.inherited;

import java.io.*;
import javax.persistence.*;

/**
 * <p><b>Purpose</b>: Describes an BeerConsumers's telephone number.
 * <p><b>Description</b>: Used in a 1:M relationship from a BeerConsumer.
 */
@Entity
@Table(name="JPA21_TELEPHONE")
@IdClass(org.eclipse.persistence.testing.models.jpa21.advanced.inherited.TelephoneNumberPK.class)
public class TelephoneNumber implements Serializable {
    private String type;
    private String number;
    private String areaCode;
    private BeerConsumer beerConsumer;

    public TelephoneNumber() {
        this.type = "Unknown";
        this.areaCode = "###";
        this.number = "#######";
        this.beerConsumer = null;
    }

    public TelephoneNumberPK buildPK(){
        TelephoneNumberPK pk = new TelephoneNumberPK();
        pk.setType(getType());
        pk.setNumber(getNumber());
        pk.setAreaCode(getAreaCode());
        return pk;
    }

    public boolean equals(Object telephoneNumber) {
        if (telephoneNumber.getClass() != TelephoneNumber.class) {
            return false;
        }

        return ((TelephoneNumber) telephoneNumber).buildPK().equals(buildPK());
    }

    @Id
    @Column(name="AREA_CODE")
    public String getAreaCode() {
        return areaCode;
    }

    @ManyToOne
    @JoinColumn(name="CONSUMER_ID", referencedColumnName="ID")
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }

    @Id
    @Column(name="TNUMBER")
    public String getNumber() {
        return number;
    }

    @Id
    @Column(name="TYPE")
    public String getType() {
        return type;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Example: TelephoneNumber[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("TelephoneNumber[");
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
