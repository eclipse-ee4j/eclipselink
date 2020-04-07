/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name="CMP3_PLACE")
public class Place {

    @Id
    @GeneratedValue(strategy=TABLE, generator="PLACE_TABLE_GENERATOR")
    @TableGenerator(
        name="PLACE_TABLE_GENERATOR",
        table="CMP3_PLACE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PLACE_SEQ"
    )
    protected int id;

    @Column(name="COUNTRY_CODE", insertable=true, updatable=true)
    protected String countryCode;

    protected String name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="address", column=@Column(name="ADDRESS_1")),
        @AttributeOverride(name="countryCode", column=@Column(name="COUNTRY_CODE", insertable=false, updatable=false))
    })
    protected Location address1;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="address", column=@Column(name="ADDRESS_2")),
        @AttributeOverride(name="countryCode", column=@Column(name="COUNTRY_CODE", insertable=false, updatable=false))
    })
    protected Location address2;

    public Place() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getAddress1() {
        return address1;
    }

    public void setAddress1(Location address1) {
        this.address1 = address1;
    }

    public Location getAddress2() {
        return address2;
    }

    public void setAddress2(Location address2) {
        this.address2 = address2;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
