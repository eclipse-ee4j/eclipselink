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
// dmccann - December 30/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class FooWithBar {
    @XmlElementRef(type=Bar.class)
    @XmlJavaTypeAdapter(value=MyListAdapter.class)
    public List<String> items;

    public boolean equals(Object o) {
        FooWithBar fwb;
        try {
            fwb = (FooWithBar) o;
        } catch (ClassCastException cce) {
            return false;
        }
        return items.equals(fwb.items);
    }
}
