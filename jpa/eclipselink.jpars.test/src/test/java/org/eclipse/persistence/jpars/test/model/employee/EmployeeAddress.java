/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.jpars.test.model.employee;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
        @NamedQuery(
                name = "EmployeeAddress.getRegion",
                query = "SELECT u.postalCode, u.province, u.street FROM EmployeeAddress u ORDER BY u.id"
        ),
        @NamedQuery(
                name = "EmployeeAddress.getById",
                query = "SELECT u FROM EmployeeAddress u where u.id = :id"
        ),
        @NamedQuery(
                name = "EmployeeAddress.updatePostalCode",
                query = "UPDATE EmployeeAddress u SET u.postalCode = :postalCode where u.id = :id"
        ),
        @NamedQuery(
                name = "EmployeeAddress.getAll",
                query = "SELECT u FROM EmployeeAddress u"
        ),
        @NamedQuery(
                name = "EmployeeAddress.getPicture",
                query = "SELECT u.areaPicture FROM EmployeeAddress u where u.id = :id"
        )
})
@Entity
@Table(name = "JPARS_EMPLOYEEADDRESS")
public class EmployeeAddress {
    @Id
    @Column(name = "ADDRESS_ID")
    @GeneratedValue
    private int id;

    @Basic
    private String city;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String country;

    @Basic
    private String province;

    @Basic
    @Column(name = "P_CODE")
    private String postalCode;

    @Basic
    private String street;

    @Lob
    private byte[] areaPicture;

    public EmployeeAddress() {
    }

    public EmployeeAddress(String city, String country, String province,
            String postalCode, String street) {
        super();
        this.city = city;
        this.country = country;
        this.province = province;
        this.postalCode = postalCode;
        this.street = street;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int addressId) {
        this.id = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String pCode) {
        this.postalCode = pCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public byte[] getAreaPicture() {
        return areaPicture;
    }

    public void setAreaPicture(byte[] areaPicture) {
        this.areaPicture = areaPicture;
    }

    @Override
    public String toString() {
        return "id=" + id + ", city=" + city + ", country=" + country + ", province=" + province + ", postalCode=" + postalCode + ", street=" + street;
    }

}
