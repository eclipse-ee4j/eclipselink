/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class BarAdapter extends XmlAdapter<Object, String> {
    public BarAdapter() {}

    public String unmarshal(Object arg0) throws Exception {
        if (arg0 instanceof Bar) {
            return ((Bar)arg0).id;
        }
        return Integer.toString(((FooBar)arg0).id);
    }

    public Object marshal(String arg0) throws Exception {
        if (arg0.equals("66")) {
            Bar bar = new Bar();
            bar.id = arg0;
            return bar;
        }
        FooBar fooBar = new FooBar();
        fooBar.id = Integer.parseInt(arg0);
        return fooBar;
    }
}
