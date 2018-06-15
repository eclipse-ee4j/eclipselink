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

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyAdapter extends XmlAdapter<Object, String> {
    public static String VAL0 = "00";
    public static String VAL1 = "11";
    public static String VAL2 = "22";
    public static String EMPTY_STR = "";

    public MyAdapter() {}

    public String unmarshal(Object arg0) throws Exception {
        String id = EMPTY_STR;
        if (arg0 instanceof Bar) {
            id = ((Bar)arg0).id;
        }
        return id;
    }

    public Object marshal(String arg0) throws Exception {
        Bar bar = new Bar();
        bar.id = arg0;
        return bar;
    }
}
