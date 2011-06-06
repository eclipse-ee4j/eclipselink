/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.CascadeType.*;

import org.eclipse.persistence.annotations.NamedStoredFunctionQuery;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.StoredProcedureParameter;
import org.eclipse.persistence.annotations.Convert;

import static org.eclipse.persistence.annotations.Direction.IN;
import static org.eclipse.persistence.annotations.Direction.OUT;
import static org.eclipse.persistence.annotations.Direction.IN_OUT;

/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
@Entity
@Table(name="CMP3_ADDRESS")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findAllSQLAddresses", 
        query="select * from CMP3_ADDRESS",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCity_QuestionMark_Number", 
        query="select * from CMP3_ADDRESS where city=?1",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCity_QuestionMark", 
        query="select * from CMP3_ADDRESS where city=?",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCityAndCountry_QuestionMark_Number", 
        query="select * from CMP3_ADDRESS where city=?1 and country=?2",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCityAndCountry_QuestionMark", 
        query="select * from CMP3_ADDRESS where city=? and country=?",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    )}
)
@NamedQuery(
    name="findAllAddressesByPostalCode", 
    query="SELECT OBJECT(address) FROM Address address WHERE address.postalCode = :postalcode"
)
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name="SProcAddress",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class,
        procedureName="SProc_Read_Address",
        parameters={
            @StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Integer.class),
            @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="STREET", type=String.class),
            @StoredProcedureParameter(direction=OUT, name="city_v", queryParameter="CITY", type=String.class),
            @StoredProcedureParameter(direction=OUT, name="country_v", queryParameter="COUNTRY", type=String.class),
            @StoredProcedureParameter(direction=OUT, name="province_v", queryParameter="PROVINCE", type=String.class),
            @StoredProcedureParameter(direction=OUT, name="p_code_v", queryParameter="P_CODE", type=String.class)
        }),
    @NamedStoredProcedureQuery(
            name="SProcAddressByIndex",
            resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class,
            procedureName="SProc_Read_Address",
            callByIndex=true,
            parameters={
                @StoredProcedureParameter(direction=IN_OUT, queryParameter="ADDRESS_ID", type=Integer.class),
                @StoredProcedureParameter(direction=OUT, queryParameter="STREET", type=String.class),
                @StoredProcedureParameter(direction=OUT, queryParameter="CITY", type=String.class),
                @StoredProcedureParameter(direction=OUT, queryParameter="COUNTRY", type=String.class),
                @StoredProcedureParameter(direction=OUT, queryParameter="PROVINCE", type=String.class),
                @StoredProcedureParameter(direction=OUT, queryParameter="P_CODE", type=String.class)
            }),
    @NamedStoredProcedureQuery(
            name="SProcAddressWithResultSetMapping",
            resultSetMapping="address-map",
            procedureName="SProc_Read_Address",
            parameters={
                @StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="address_id_v", type=Integer.class),
                @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="street_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="city_v", queryParameter="city_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="country_v", queryParameter="country_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="province_v", queryParameter="province_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="p_code_v", queryParameter="p_code_v", type=String.class)
            }),
    @NamedStoredProcedureQuery(
            name="SProcAddressWithResultSetFieldMapping",
            resultSetMapping="address-field-map",
            procedureName="SProc_Read_Address",
            parameters={
                @StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="address_id_v", type=Integer.class),
                @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="street_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="city_v", queryParameter="city_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="country_v", queryParameter="country_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="province_v", queryParameter="province_v", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="p_code_v", queryParameter="p_code_v", type=String.class)
            }),
    @NamedStoredProcedureQuery(
        name="SProcInOut",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class,
        procedureName="SProc_Read_InOut",
        parameters={
            @StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Long.class),
            @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="STREET", type=String.class)
        }),
    @NamedStoredProcedureQuery(
        name="SProcInOutReturningRawData",
        procedureName="SProc_Read_InOut",
        parameters={
                @StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Long.class),
                @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="STREET")
        }),
        // This query is used to test metadata processing of array types.  The stored procedure is not available on the database
        // Loading of this persistence unit will fail if the processing is not working
        @NamedStoredProcedureQuery(
                name="TestSprocForArrayInput-not-on-database",
                procedureName="SProc_Array_Test",
                parameters={
                        @StoredProcedureParameter(queryParameter="string1", direction=IN, type=String[].class),
                        @StoredProcedureParameter(queryParameter="int1", direction=IN, type=int[].class),
                        @StoredProcedureParameter(queryParameter="float1", direction=IN, type=float[].class),
                        @StoredProcedureParameter(queryParameter="char1", direction=IN, type=char[].class),
                        @StoredProcedureParameter(queryParameter="boolean1", direction=IN, type=boolean[].class),
                        @StoredProcedureParameter(queryParameter="short1", direction=IN, type=short[].class),
                        @StoredProcedureParameter(queryParameter="double1", direction=IN, type=double[].class),
                        @StoredProcedureParameter(queryParameter="byte1", direction=IN, type=byte[].class)}
       )
})
@NamedStoredFunctionQuery(
        name="StoredFunction_In",
        functionName="StoredFunction_In",
        parameters={
            @StoredProcedureParameter(direction=IN, name="P_IN", queryParameter="P_IN", type=Long.class)
        },
        returnParameter=@StoredProcedureParameter(queryParameter="RETURN", type=Long.class))
@SqlResultSetMappings({
    @SqlResultSetMapping(
        name = "address-map",
        entities = {
            @EntityResult(
                entityClass=Address.class,
                fields={
                    @FieldResult(name="ID", column="address_id_v"),
                    @FieldResult(name="street", column="street_v"),
                    @FieldResult(name="city", column="city_v"),
                    @FieldResult(name="country", column="country_v"),
                    @FieldResult(name="province", column="province_v"),
                    @FieldResult(name="postalCode", column="p_code_v")
                }
            )
        }),
    @SqlResultSetMapping(
        name = "address-field-map",
        columns = {
            @ColumnResult(name="address_id_v"),
            @ColumnResult(name="street_v"),
            @ColumnResult(name="city_v"),
            @ColumnResult(name="country_v"),
            @ColumnResult(name="province_v"),
            @ColumnResult(name="p_code_v")
        }),
    @SqlResultSetMapping(
        name = "address-case-sensitive-map",
        entities = {
            @EntityResult(
                entityClass=Address.class,
                fields={
                    @FieldResult(name="ID", column="aDdReSs_iD"),
                    @FieldResult(name="street", column="StReeT"),
                    @FieldResult(name="city", column="CiTy"),
                    @FieldResult(name="country", column="CoUnTrY"),
                    @FieldResult(name="province", column="PrOvInCe"),
                    @FieldResult(name="postalCode", column="P_cOdE")
                }
            )
        })
})
public class Address implements Serializable {
    private int id;
    private Integer version;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private AddressType type;
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
    // BUG 5079973 - this should be a valid specification, that is the generator
    // trumps the AUTO strategy defaulting.
    // BUG 6273529 - SHOULD BE ABLE TO SKIP SEQUENCENAME IN SEQUENCEGENERATOR
    // In case sequenceName is not specified generator's name used instead.
    @GeneratedValue(generator="ADDRESS_SEQ")
    @SequenceGenerator(name="ADDRESS_SEQ", allocationSize=25)
    @Column(name="ADDRESS_ID", length=23)
    public int getID() { 
        return id; 
    }

    public void setID(int id) { 
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

    @Basic
    @Convert("class-instance")
    public AddressType getType(){
        return type;
    }

    public void setType(AddressType type){
        this.type = type;
    }

    @Version
    public Integer getVersion() {
        return version; 
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
