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
 *     tware - initial implementation as part of extensibility feature
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.extensibility;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.VirtualAccessMethods;

@Entity(name="ExtensibilityAddress")
@Table(name="EXTENS_ADDR")
@VirtualAccessMethods
public class Address {


    private int id;

    private String street1;

    private String city;

    private String country;

    private String postalCode;

    private Map<String, Object> extensions = new HashMap<String, Object>();

    public String getStreet() {
        return street1;
    }

    public void setStreet(String street) {
        this.street1 = street;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }

    protected Map<String, Object> getExtensions() {
        return this.extensions;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) getExtensions().get(name);
    }

    public Object set(String name, Object value)
    {
        return getExtensions().put(name, value);
    }
    
    public Object remove(String name) {
        String value = name;
        return getExtensions().remove(name);
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("Address: " + id + " - ");
        buf.append(street1 + ", ");
        buf.append(city + ", ");
        buf.append(country + ", ");
        buf.append(postalCode);
        Iterator i = extensions.keySet().iterator();
        while (i.hasNext()){
            buf.append("," + extensions.get(i.next()));
        }
        return buf.toString();
    }

}
