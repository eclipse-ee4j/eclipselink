/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.*;

@Entity
@Table(name="CMP3_PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy=TABLE)
    protected int id;

    @Column(name="NAME", nullable=false)
    protected String name;

    @Column(name="COUNTRY_CODE", insertable=true, updatable=true, nullable=false, length=3)
    protected String countryCode;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="codeNumber", column=@Column(name="BARCODE1")),
        @AttributeOverride(name="countryCode", column=@Column(name="COUNTRY_CODE", insertable=false, updatable=false))
    })
    protected BarCode barCode1;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="codeNumber", column=@Column(name="BARCODE2")),
        @AttributeOverride(name="countryCode", column=@Column(name="COUNTRY_CODE", insertable=false, updatable=false))
    })
    protected BarCode barCode2;

    public Product() {
        super();
        setName("INVALIDNAME");
        setCountryCode("ZZZ");
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public BarCode getBarCode1() {
        return barCode1;
    }

    public void setBarCode1(BarCode barCode1) {
        this.barCode1 = barCode1;
    }

    public BarCode getBarCode2() {
        return barCode2;
    }

    public void setBarCode2(BarCode barCode2) {
        this.barCode2 = barCode2;
    }

}
