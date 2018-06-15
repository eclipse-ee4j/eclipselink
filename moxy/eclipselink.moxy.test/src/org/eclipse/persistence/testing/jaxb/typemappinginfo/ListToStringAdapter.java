/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// mmacivor - December 15/2009 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ListToStringAdapter extends XmlAdapter<String, List<String>> {

    @Override
    public String marshal(List<String> v) throws Exception {
        return "marshalled string value";
    }

    @Override
    public List<String> unmarshal(String v) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        list.add("String1");
        list.add("String2");
        return list;
    }

}
