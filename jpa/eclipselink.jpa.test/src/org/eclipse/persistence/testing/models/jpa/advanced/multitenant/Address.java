/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/1/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 9)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.CascadeType.*;

import org.eclipse.persistence.annotations.Multitenant;

import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

@Entity(name="MOB_ADDRESS")
@Table(name="JPA_MAFIOSO_ADDRESS")
@Multitenant(SINGLE_TABLE)
public class Address implements Serializable {
    private int id;
    private Integer version;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private Collection<Mafioso> mafiosos;

    public Address() {
        city = "";
        province = "";
        postalCode = "";
        street = "";
        country = "";
        mafiosos = new Vector<Mafioso>();
    }

    public Address(String street, String city, String province, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.mafiosos = new Vector<Mafioso>();
    }

    public String getCity() { 
        return city; 
    }
    
    public String getCountry() { 
        return country; 
    }

    @Id
    @GeneratedValue
    @Column(name="ADDRESS_ID")
    public int getId() { 
        return id; 
    }
    
    @OneToMany(cascade=ALL, mappedBy="address")
    public Collection<Mafioso> getMafiosos() { 
        return mafiosos; 
    }

    @Column(name="P_CODE")
    public String getPostalCode() { 
        return postalCode; 
    }

    public String getProvince() { 
        return province; 
    }

    public String getStreet() { 
        return street; 
    }
    
    @Version
    public Integer getVersion() {
        return version; 
    }

    public void setCity(String city) { 
        this.city = city; 
    }
    
    public void setCountry(String country) { 
        this.country = country;
    }

    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setMafiosos(Collection<Mafioso> mafiosos) {
        this.mafiosos = mafiosos;
    }

    public void setPostalCode(String postalCode) { 
        this.postalCode = postalCode; 
    }
    
    public void setProvince(String province) { 
        this.province = province; 
    }
    
    public void setStreet(String street) { 
        this.street = street; 
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String toString() {
        return "Address[" + getId() + "] : " + street + ", " + city + ", " + province + ", " + postalCode + ", " + country;
    }
}

