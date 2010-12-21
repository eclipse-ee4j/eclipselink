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
package org.eclipse.persistence.testing.models.jpa.xml.merge.advanced;

import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import java.util.*;
import static javax.persistence.CascadeType.*;

/**
 * This class is used to test XML and annotation merging. This class is mapped
 * in: eclipselink-xml-merge-model/orm-annotation-merge-advanced-entity-mappings.xml
 * 
 * Also there are no automated tests that go along with these models, see the
 * test suite: EntityMappingsMergeAdvancedJUnitTestCase. It tests through
 * inspecting descriptor settings only and by no means does extensive
 * validation of all the metadata and defaults.
 */
@Entity(name = "AnnMergeAddress")
@Table(name = "CMP3_ANN_MERGE_ADDRESS")
@NamedNativeQuery(name = "ann_merge_findAllSQLAddresses", query = "select * from CMP3_ANN_MERGE_ADDRESS", resultClass = Address.class)
@NamedQuery(name = "ann_merge_findAllAddressesByPostalCode", query = "SELECT OBJECT(address) FROM Address address WHERE address.postalCode = :postalcode")
@TableGenerator(name = "ANN_MERGE_ADDRESS_SEQUENCE_GENERATOR", table = "ANN_MERGE_ADDRESS_SEQ", pkColumnValue = "ANN_MERGE_ADDRESS_SEQ", allocationSize = 25)
public class Address implements Serializable {
    private Integer id;
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
        this.employees = new Vector<Employee>();
    }

    public Address(String street, String city, String province, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.employees = new Vector<Employee>();
    }

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "ANN_MERGE_ADDRESS_SEQUENCE_GENERATOR")
    @Column(name = "ANN_MERGE_ADDRESS_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Column(name = "ANN_MERGE_P_CODE")
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

    @OneToMany(cascade = ALL, mappedBy = "address")
    public Collection<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }
}
