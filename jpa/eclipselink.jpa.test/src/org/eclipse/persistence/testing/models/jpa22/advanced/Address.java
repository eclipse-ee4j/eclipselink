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
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     11/05/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.models.jpa22.advanced;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import static javax.persistence.CascadeType.*;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.Version;

import static javax.persistence.ParameterMode.IN;

@Entity
@Table(name="JPA22_ADDRESS")

@NamedStoredProcedureQuery(
    name = "ReadAllAddressesWithNoResultClass",
    procedureName = "Read_All_Addresses"
)

@NamedStoredProcedureQuery(
    name = "ReadNoAddresses",
    procedureName = "Read_No_Addresses"
)

@NamedStoredProcedureQuery(
    name = "ReadAddressWithResultClass",
    resultClasses = org.eclipse.persistence.testing.models.jpa22.advanced.Address.class,
    procedureName = "Read_Address",
    parameters = {
        @StoredProcedureParameter(mode=IN, name="address_id_v", type=Integer.class)
    }
)

@NamedStoredProcedureQuery(
    name = "ReadAddressMappedNamedFieldResult",
    resultSetMappings = "address-field-result-map-named",
    procedureName = "Read_Address_Mapped_Named",
    parameters = {
        @StoredProcedureParameter(mode=IN, name="address_id_v", type=Integer.class)
    }
)

@NamedStoredProcedureQuery(
    name = "ReadAddressMappedNumberedFieldResult",
    resultSetMappings = "address-field-result-map-numbered",
    procedureName = "Read_Address_Mapped_Numbered",
    parameters = {
        @StoredProcedureParameter(mode=IN, type=Integer.class)
    }
)

@NamedStoredProcedureQuery(
    name = "ReadAddressMappedNamedColumnResult",
    resultSetMappings = "address-column-result-map",
    procedureName = "Read_Address_Mapped_Named",
    parameters = {
        @StoredProcedureParameter(mode=IN, name="address_id_v", type=Integer.class)
    }
 )

@SqlResultSetMapping(
    name = "address-field-result-map-named",
    entities = {
        @EntityResult(
            entityClass = Address.class,
            fields = {
                @FieldResult(name="id", column="address_id_v"),
                @FieldResult(name="street", column="street_v"),
                @FieldResult(name="city", column="city_v"),
                @FieldResult(name="country", column="country_v"),
                @FieldResult(name="province", column="province_v"),
                @FieldResult(name="postalCode", column="p_code_v")
            }
        )
    })

@SqlResultSetMapping(
    name = "address-field-result-map-numbered",
    entities = {
        @EntityResult(
            entityClass = Address.class,
            fields = {
                @FieldResult(name="id", column="1"),
                @FieldResult(name="street", column="2"),
                @FieldResult(name="city", column="3"),
                @FieldResult(name="country", column="4"),
                @FieldResult(name="province", column="5"),
                @FieldResult(name="postalCode", column="6")
            }
        )
    })

@SqlResultSetMapping(
    name = "address-column-result-map",
    columns = {
        @ColumnResult(name = "address_id_v"),
        @ColumnResult(name = "street_v"),
        @ColumnResult(name = "city_v"),
        @ColumnResult(name = "country_v"),
        @ColumnResult(name = "province_v"),
        @ColumnResult(name = "p_code_v")
    })
public class Address implements Serializable {
    private int id;
    private Integer version;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private Collection<Employee> employees;

    public Address() {
        city = "";
        province = "";
        postalCode = "";
        street = "";
        country = "";
        employees = new Vector<>();
    }

    public Address(String street, String city, String province, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.employees = new Vector<>();
    }

    @Id
    @GeneratedValue(generator="ADDRESS_SEQ")
    @SequenceGenerator(name="ADDRESS_SEQ", allocationSize=25)
    @Column(name="ADDRESS_ID", length=23)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name="P_CODE")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @OneToMany(cascade=ALL, mappedBy="address")
    public Collection<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String toString() {
        return "Address: " + getId();
    }
}
