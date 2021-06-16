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
//     Denise Smith - March 2, 2010
package org.eclipse.persistence.testing.jaxb.xmladapter.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class MyObjectAdapter extends XmlAdapter<MyObject, HashMap<Integer, String>> {

    public MyObject marshal(HashMap<Integer, String> arg0) throws Exception {
        MyObject myObject = new MyObject();

        Iterator<Entry<Integer, String>> iter = arg0.entrySet().iterator();
        while(iter.hasNext()){
            Entry<Integer, String> next = iter.next();
            myObject.getKeys().add(next.getKey().toString());
            myObject.getValues().add(next.getValue());
        }
        Collections.sort(myObject.getKeys());
        Collections.sort(myObject.getValues());
        return myObject;
    }

    public HashMap<Integer, String> unmarshal(MyObject arg0) throws Exception {
        HashMap theMap = new HashMap<Integer, String>();
        List<String> keys = arg0.getKeys();
        List<String> values = arg0.getValues();
        for(int i=0; i<keys.size(); i++){
            Integer theInteger = Integer.parseInt(keys.get(i));
            theMap.put(theInteger, values.get(i));
        }
        return theMap;
    }

}
