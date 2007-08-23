/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyHashMapAdapter extends XmlAdapter<MyHashMapType, HashMap> {
    public MyHashMapType marshal(HashMap arg0) throws Exception {
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
    
    public HashMap unmarshal(MyHashMapType arg0) throws Exception {
        HashMap map = new HashMap<Integer, String>();
        for (Iterator typeIt = arg0.entry.iterator(); typeIt.hasNext(); ) {
            MyHashMapEntryType eType = (MyHashMapEntryType) typeIt.next();
            map.put(eType.key, eType.value);
        }
        return map;
    }
}
