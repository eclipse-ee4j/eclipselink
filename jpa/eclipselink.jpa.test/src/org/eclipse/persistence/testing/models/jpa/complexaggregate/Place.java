/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

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
