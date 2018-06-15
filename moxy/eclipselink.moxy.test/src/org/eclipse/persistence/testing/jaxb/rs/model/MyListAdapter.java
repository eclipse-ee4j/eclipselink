/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.rs.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyListAdapter extends XmlAdapter<MyListType,List<Bar>> {
    @Override
    public List<Bar> unmarshal(MyListType v) throws Exception {
        return v.entry;
    }

    @Override
    public MyListType marshal(List<Bar> v) throws Exception {
        MyListType listType = new MyListType();
        listType.entry = v;
        return listType;
    }
}
