/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6.0 - initial implementation
 ******************************************************************************/
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
