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
// dmccann - December 30/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.list;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class FooWithBars {
    @XmlElementRef(type=Bar.class)
    @XmlJavaTypeAdapter(value=MyAdapter.class)
    public List<String> items;

    public boolean equals(Object o) {
        FooWithBars fwb;
        try {
            fwb = (FooWithBars) o;
        } catch (ClassCastException cce) {
            return false;
        }
        return items.equals(fwb.items);
    }
}
