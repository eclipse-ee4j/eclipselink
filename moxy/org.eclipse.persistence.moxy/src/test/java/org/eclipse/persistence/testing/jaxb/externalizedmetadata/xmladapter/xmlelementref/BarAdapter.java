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
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
