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

// Contributors:
// rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
