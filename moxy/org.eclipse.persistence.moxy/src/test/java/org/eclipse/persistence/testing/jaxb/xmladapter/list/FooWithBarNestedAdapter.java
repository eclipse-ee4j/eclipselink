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
// Denise Smith - February 7 , 2013
package org.eclipse.persistence.testing.jaxb.xmladapter.list;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="fooWithBar")
@XmlType(name="fooWithBar")
public class FooWithBarNestedAdapter {
    @XmlElementRef(type=Bar.class)
    @XmlJavaTypeAdapter(value=MyNestedListAdapter.class)
    public List<String> items;

    public boolean equals(Object o) {
        FooWithBarNestedAdapter fwb;
        try {
            fwb = (FooWithBarNestedAdapter) o;
        } catch (ClassCastException cce) {
            return false;
        }
        return items.equals(fwb.items);
    }

    private static class MyNestedListAdapter extends XmlAdapter<Object, List<String>> {
        public static String VAL0 = "00";
        public static String VAL1 = "11";
        public static String VAL2 = "22";
        public static String EMPTY_STR = "";
        public static String SPACE = " ";


        public List<String> unmarshal(Object arg0) throws Exception {
            List<String> list = new ArrayList<String>();
            if (arg0 instanceof Bar) {
                String idstr = ((Bar)arg0).id;
                StringTokenizer stok = new StringTokenizer(idstr, SPACE);
                while (stok.hasMoreTokens()) {
                    list.add(stok.nextToken());
                }
            }
            return list;
        }

        public Object marshal(List<String> arg0) throws Exception {
            Bar bar = new Bar();
            String id = EMPTY_STR;
            if (arg0.get(0).equals(VAL0)) {
                id += arg0.get(0);
            }
            if (arg0.get(1).equals(VAL1)) {
                id += SPACE + arg0.get(1);
            }
            if (arg0.get(2).equals(VAL2)) {
                id += SPACE + arg0.get(2);
            }
            bar.id = id;
            return bar;
        }
    }
}
