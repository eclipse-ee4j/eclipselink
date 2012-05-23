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
 * rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlVirtualAccessMethods;

@XmlRootElement
@XmlVirtualAccessMethods(setMethod="put")
public class Employee {

    @XmlAttribute
    public int id;
    @XmlElement(name = "first-name")
    public String firstName;
    @XmlElement(name = "last-name")
    public String lastName;

    @XmlElement(name = "phone-number")
    public List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @XmlTransient
    public Map<String, Object> extensions = new HashMap<String, Object>();

    @XmlTransient
    public Map<String, Integer> intExtensions = new HashMap<String, Integer>();

    public Object get(String name) {
        return extensions.get(name);
    }

    public void put(String name, Object value) {
        extensions.put(name, value);
    }

    public Integer getInt(String name) {
        return intExtensions.get(name);
    }

    public void setInt(String name, Integer value) {
        intExtensions.put(name, value);
    }

    @Override
    public String toString() {
        return super.toString() + extensions;
    }

}