/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - December 15/2009 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
