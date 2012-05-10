/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced;

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
// TODO: JPA 2.1 reference
//import javax.persistence.NamedStoredProcedureQueries;
//import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
//TODO: JPA 2.1 reference
//import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Convert;

//TODO: JPA 2.1 references
//import static javax.persistence.ParameterMode.IN;
//import static javax.persistence.ParameterMode.INOUT;
//import static javax.persistence.ParameterMode.OUT;
//import static javax.persistence.ParameterMode.REF_CURSOR;

@Entity
@Table(name="JPA21_ADDRESS")
// TODO: JPA 2.1 references
/*
@NamedStoredProcedureQueries({
        
    @NamedStoredProcedureQuery(
        name = "ReadAddressUsingPositionalParameterAndSingleResultSet",
        resultClasses = org.eclipse.persistence.testing.models.jpa21.advanced.Address.class,
        procedureName = "Read_Address_Result_Set",
        parameters = @StoredProcedureParameter(mode=IN, type=Integer.class)
    ),
      
    @NamedStoredProcedureQuery(
        name = "ReadAddressWithResultClass",
        resultClasses = org.eclipse.persistence.testing.models.jpa21.advanced.Address.class,
        procedureName = "Read_Address",
        parameters = {
            @StoredProcedureParameter(mode=INOUT, name="ADDRESS_ID", type=Integer.class),
            @StoredProcedureParameter(mode=OUT, name="STREET", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="CITY", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="COUNTRY", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="PROVINCE", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="P_CODE", type=String.class)
        }
    ),
        
    @NamedStoredProcedureQuery(
        name = "ReadAddressWithPositionalParameters",
        resultSetMappings = "address-field-result-map-positional",
        procedureName = "Read_Address",
        parameters = {
            @StoredProcedureParameter(mode=INOUT, type=Integer.class),
            @StoredProcedureParameter(mode=OUT, type=String.class),
            @StoredProcedureParameter(mode=OUT, type=String.class),
            @StoredProcedureParameter(mode=OUT, type=String.class),
            @StoredProcedureParameter(mode=OUT, type=String.class),
            @StoredProcedureParameter(mode=OUT, type=String.class)
        }
    ),
        
    @NamedStoredProcedureQuery(
        name = "ReadAddressWithResultSetMapping",
        resultSetMappings = "address-field-result-map",
        procedureName = "Read_Address_Mapped",
        parameters = {
            @StoredProcedureParameter(mode=INOUT, name="address_id_v", type=Integer.class),
            @StoredProcedureParameter(mode=OUT, name="street_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="city_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="country_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="province_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="p_code_v", type=String.class)
        }
    ),
            
    @NamedStoredProcedureQuery(
        name = "ReadAddressWithResultSetFieldMapping",
        resultSetMappings = "address-column-result-map",
        procedureName = "Read_Address_Mapped",
        parameters = {
            @StoredProcedureParameter(mode=INOUT, name="address_id_v", type=Integer.class),
            @StoredProcedureParameter(mode=OUT, name="street_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="city_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="country_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="province_v", type=String.class),
            @StoredProcedureParameter(mode=OUT, name="p_code_v", type=String.class)
        }
     )
})

@SqlResultSetMappings({
    @SqlResultSetMapping(
        name = "address-field-result-map-positional",
        entities = {
            @EntityResult(
                entityClass = Address.class,
                fields = {
                    @FieldResult(name="id", column="1"),
                    @FieldResult(name="street", column="2"),
                    @FieldResult(name="city", column="3"),
                    @FieldResult(name="country", column="4"),
                    @FieldResult(name="province", column="5"),
                    @FieldResult(name="p_code", column="6")
                }
            )
        }),
            
    @SqlResultSetMapping(
        name = "address-field-result-map",
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
        }),
        
    @SqlResultSetMapping(
        name = "address-column-result-map",
        columns = {
            @ColumnResult(name = "address_id_v"),
            @ColumnResult(name = "street_v"),
            @ColumnResult(name = "city_v"),
            @ColumnResult(name = "country_v"),
            @ColumnResult(name = "province_v"),
            @ColumnResult(name = "p_code_v")
        }),
        
    @SqlResultSetMapping(
        name = "address-result-set-mapping",
        entities = {
            @EntityResult(entityClass=Address.class)
        }    
    )
})
*/
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
        employees = new Vector<Employee>();
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
