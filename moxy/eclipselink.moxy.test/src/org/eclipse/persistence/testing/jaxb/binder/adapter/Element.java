/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - June 4/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.binder.adapter;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
