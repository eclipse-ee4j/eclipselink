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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.CascadeType.*;

import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.StoredProcedureParameter;
import static org.eclipse.persistence.annotations.Direction.OUT;
import static org.eclipse.persistence.annotations.Direction.IN_OUT;

/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
@Entity(name="Address")
@Table(name="CMP3_FA_ADDRESS")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findAllFieldAccessSQLAddresses", 
        query="select * from CMP3_FA_ADDRESS",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllFieldAccessSQLAddressesByCity_QuestionMark_Number", 
        query="select * from CMP3_FA_ADDRESS where city=?1",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllFieldAccessSQLAddressesByCity_QuestionMark", 
        query="select * from CMP3_FA_ADDRESS where city=?",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllFieldAccessSQLAddressesByCityAndCountry_QuestionMark_Number", 
        query="select * from CMP3_FA_ADDRESS where city=?1 and country=?2",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllFieldAccessSQLAddressesByCityAndCountry_QuestionMark", 
        query="select * from CMP3_FA_ADDRESS where city=? and country=?",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class
    )}
)
@NamedQuery(
    name="findAllFieldAccessAddressesByPostalCode", 
    query="SELECT OBJECT(address) FROM Address address WHERE address.postalCode = :postalcode"
)
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
    name="SProcAddress",
    resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class,
    procedureName="SProc_Read_Address",
    parameters={@StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Integer.class),
                @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="STREET", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="city_v", queryParameter="CITY", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="country_v", queryParameter="COUNTRY", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="province_v", queryParameter="PROVINCE", type=String.class),
                @StoredProcedureParameter(direction=OUT, name="p_code_v", queryParameter="P_CODE", type=String.class)}),
    @NamedStoredProcedureQuery(
    name="SProcInOut",
    resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address.class,
    procedureName="SProc_Read_InOut",
    parameters={@StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Long.class),
                @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="STREET", type=String.class)}),
    @NamedStoredProcedureQuery(
    name="SProcInOutReturningRawData",
    procedureName="SProc_Read_InOut",
    parameters={@StoredProcedureParameter(direction=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Long.class),
                @StoredProcedureParameter(direction=OUT, name="street_v", queryParameter="STREET", type=String.class)})
})
public class Address implements Serializable {
    @Id
    // BUG 5079973 - this should be a valid specification, that is the generator
    // trumps the AUTO strategy defaulting.
    @GeneratedValue(generator="FA_ADD_SEQ_GENERATOR")
    @SequenceGenerator(name="FA_ADD_SEQ_GENERATOR", sequenceName="ADDRESS_SEQ", allocationSize=25)
    @Column(name="ADDRESS_ID")
    private Integer id;
    
    private String street;
    private String city;
    private String province;
    
    @Column(name="P_CODE")
    private String postalCode;
    
    private String country;
    
    @OneToMany(cascade=ALL, mappedBy="address")
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

    public Address copy() {
        Address copy = new Address();
        copy.id = this.id;
        copy.street = this.street;
        copy.city = this.city;
        copy.province = this.province;
        copy.country = this.country;
        copy.postalCode = this.postalCode;
        return copy;
    }
    
    public TransferAddress transferCopy() {
        TransferAddress copy = new TransferAddress();
        copy.id = this.id;
        copy.street = this.street;
        copy.city = this.city;
        copy.province = this.province;
        copy.country = this.country;
        copy.postalCode = this.postalCode;
        return copy;
    }
    
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
    
    public Collection<Employee> getEmployees() { 
        return employees; 
    }
    
    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }
    
    public class TransferAddress {
        public Integer id;    
        public String street;
        public String city;
        public String province;
        public String postalCode;
        public String country;
    }
}
