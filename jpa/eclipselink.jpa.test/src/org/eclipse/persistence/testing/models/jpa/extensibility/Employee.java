/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation as part of extensibility feature
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.extensibility;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.VirtualAccessMethods;

@Entity(name="ExtensibilityEmployee")
@Table(name="EXTENS_EMP")
@VirtualAccessMethods(get="getExt", set="putExt")
public class Employee {

    @Id
    @GeneratedValue
    private int id;

    private String firstName;

    private String lastName;
    
    @ManyToOne(cascade=CascadeType.ALL)
    private Address address;

    @Version
    private long version;

    @Transient
    private Map<String, Object> extensions = new HashMap<String, Object>();
    
    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    

    protected Map<String, Object> getExtensions() {
        return this.extensions;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExt(String name) {
        return (T) getExtensions().get(name);
    }

    public Object putExt(String name, Object value)
    {
        return getExtensions().put(name, value);
    }
    
    public String toString(){
        return "Employee " + id + " - " + firstName + " " + lastName;
    }
}
