/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmladapter.noarg;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MyAdapter extends XmlAdapter<String, Something> {
    public MyAdapter() {
    }

    @Override
    public Something unmarshal(String v) throws Exception {
        return new Something(v);
    }

    @Override
    public String marshal(Something v) throws Exception {
        return v.value;
    }
}
