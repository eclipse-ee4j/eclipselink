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
 *     rbarkhouse - 2.4 - initial implementation
 ******************************************************************************/
 package org.eclipse.persistence.testing.jaxb.dynamic.util;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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