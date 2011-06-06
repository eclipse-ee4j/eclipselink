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
 * rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlVirtualAccessMethods;

@XmlVirtualAccessMethods
public class ExtObjectB {

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