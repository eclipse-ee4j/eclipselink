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
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlVirtualAccessMethods;

@XmlVirtualAccessMethods
public class ExtObjectC {

    @XmlTransient
    private Map<String, Object> extensions;

    public Object get(String name) {
        if (extensions == null) {
            extensions = new HashMap<String, Object>();
        }
        return extensions.get(name);
    }

    public void set(String name, Object value) {
        if (extensions == null) {
            extensions = new HashMap<String, Object>();
        }
        extensions.put(name, value);
    }

}
