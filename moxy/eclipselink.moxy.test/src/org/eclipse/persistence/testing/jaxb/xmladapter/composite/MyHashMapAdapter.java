/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
