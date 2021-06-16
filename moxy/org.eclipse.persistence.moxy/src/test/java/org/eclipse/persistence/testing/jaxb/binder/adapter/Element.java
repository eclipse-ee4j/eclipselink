/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 4/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.binder.adapter;

import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Element", propOrder = { "key", "value" })
public class Element {
    public Integer key;

    @XmlJavaTypeAdapter(MapEntryAdapter.class)
    public Map<Integer, String> value;

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("Element[key=");
        str.append(this.key);
        str.append(", value=");
        for (Integer key : value.keySet()) {
            str.append("(");
            str.append(key);
            str.append(",");
            str.append(value.get(key));
            str.append(")");
        }
        str.append("]");
        return str.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Element)) {
            return false;
        }
        Element elt = (Element) obj;
        if (elt.key.intValue() != this.key.intValue()) {
            return false;
        }
        if (elt.value.size() != this.value.size()) {
            return false;
        }
        for (Integer key : elt.value.keySet()) {
            if (!(elt.value.get(key).equals(this.value.get(key)))) {
                return false;
            }
        }
        return true;
    }
}
