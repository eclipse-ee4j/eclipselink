/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.*;

import org.eclipse.persistence.oxm.annotations.XmlVirtualAccessMethods;

@XmlRootElement
@XmlVirtualAccessMethods(setMethod="put")
public class Employee {

    @XmlAttribute
    public int id;

    public String name;

    @XmlTransient
    public Map<String, Object> extensions = new HashMap<String, Object>();

    public Object get(String name) {
        return extensions.get(name);
    }

    public void put(String name, Object value) {
        extensions.put(name, value);
    }

    @Override
    public String toString() {
        return super.toString() + extensions;
    }

}
