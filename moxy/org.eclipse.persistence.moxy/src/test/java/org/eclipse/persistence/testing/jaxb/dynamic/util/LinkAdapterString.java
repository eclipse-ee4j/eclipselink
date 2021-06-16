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
//     rbarkhouse - 2.4 - initial implementation
 package org.eclipse.persistence.testing.jaxb.dynamic.util;

import java.util.StringTokenizer;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;

public class LinkAdapterString extends XmlAdapter<String, DynamicEntity> {

    // ValueType = java.lang.String
    // BoundType = mynamespace.Address

    public static DynamicJAXBContext jc = null;

    public LinkAdapterString() {
        this(jc);
    }

    public LinkAdapterString(DynamicJAXBContext jc) {
        this.jc = jc;
    }

    @Override
    public DynamicEntity unmarshal(String v) throws Exception {
        DynamicEntity address = jc.newDynamicEntity("mynamespace.Address");

        StringTokenizer tokenizer = new StringTokenizer((String) v, "|");

        String street = tokenizer.nextToken();
        street = street.replace('_', ' ');
        String city = tokenizer.nextToken();
        String state = tokenizer.nextToken();
        String zip = tokenizer.nextToken();

        address.set("street", street);
        address.set("city", city);
        address.set("state", state);
        address.set("zip", zip);

        return address;
    }

    @Override
    public String marshal(DynamicEntity v) throws Exception {
        if(null == jc || null == v) {
            return null;
        }

        String street = v.get("street");
        street = street.replace(' ', '_');
        String city = v.get("city");
        String state = v.get("state");
        String zip = v.get("zip");

        return street + "|" + city + "|" + state + "|" + zip;
    }

}
