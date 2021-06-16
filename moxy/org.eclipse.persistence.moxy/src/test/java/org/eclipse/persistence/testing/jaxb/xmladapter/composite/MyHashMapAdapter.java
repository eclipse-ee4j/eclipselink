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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public final class MyHashMapAdapter extends XmlAdapter<MyHashMapType, LinkedHashMap> {
    public MyHashMapType marshal(LinkedHashMap arg0) throws Exception {
        MyHashMapType myHashMapType = new MyHashMapType();
        myHashMapType.entry = new ArrayList<MyHashMapEntryType>();

        for (Iterator keyIt = arg0.keySet().iterator(); keyIt.hasNext(); ) {
            MyHashMapEntryType eType = new MyHashMapEntryType();
            int key = (Integer) keyIt.next();
            eType.key = key;
            eType.value = (String) arg0.get(key);
            myHashMapType.entry.add(eType);
        }
        return myHashMapType;
    }

    public LinkedHashMap unmarshal(MyHashMapType arg0) throws Exception {
        LinkedHashMap map = new LinkedHashMap<Integer, String>();
        for (Iterator typeIt = arg0.entry.iterator(); typeIt.hasNext(); ) {
            MyHashMapEntryType eType = (MyHashMapEntryType) typeIt.next();
            map.put(eType.key, eType.value);
        }
        return map;
    }
}
