/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class LinkAdapter extends XmlAdapter<Object, DynamicEntity> {

    // ValueType = mynamespace.Link
    // BoundType = mynamespace.Address

    public static DynamicJAXBContext jc = null;

    public LinkAdapter() {
        this(jc);
    }

    public LinkAdapter(DynamicJAXBContext jc) {
        this.jc = jc;
    }

    @Override
    public DynamicEntity unmarshal(Object v) throws Exception {
        DynamicEntity link = (DynamicEntity) v;

        DynamicEntity address = jc.newDynamicEntity("mynamespace.Address");

        StringTokenizer tokenizer = new StringTokenizer(((Object)link.get("href")).toString(), "&", false);

        String street = tokenizer.nextToken();
        street = street.substring(street.lastIndexOf("=") + 1);
        street = street.replace('_', ' ');
        String city = tokenizer.nextToken();
        city = city.substring(city.lastIndexOf("=") + 1);
        String state = tokenizer.nextToken();
        state = state.substring(state.lastIndexOf("=") + 1);
        String zip = tokenizer.nextToken();
        zip = zip.substring(zip.lastIndexOf("=") + 1);

        address.set("street", street);
        address.set("city", city);
        address.set("state", state);
        address.set("zip", zip);

        return address;
    }

    @Override
    public Object marshal(DynamicEntity v) throws Exception {
        if(null == jc || null == v) {
            return null;
        }

        String street = v.get("street");
        street = street.replace(' ', '_');
        String city = v.get("city");
        String state = v.get("state");
        String zip = v.get("zip");

        DynamicEntity link = jc.newDynamicEntity("mynamespace.Link");
        link.set("rel", "self");
        link.set("href", "http://www.address.com/findAddress?str=" + street + "&city=" + city + "&state=" + state + "&zip=" + zip);
        return link;
    }

}