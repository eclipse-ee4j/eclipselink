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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyMapAdapter extends XmlAdapter<MyMapType,Map<String, String>> {

@Override
public MyMapType marshal(Map<String, String> arg0) throws Exception {
   MyMapType myMapType = new MyMapType();
   for(Entry<String, String> entry : arg0.entrySet()) {
      MyMapEntryType myMapEntryType =
         new MyMapEntryType();
      myMapEntryType.key = entry.getKey();
      myMapEntryType.value = entry.getValue();
      myMapType.entry.add(myMapEntryType);
   }
   return myMapType;
}

@Override
public Map<String, String> unmarshal(MyMapType arg0) throws Exception {
   HashMap<String, String> hashMap = new HashMap<String, String>();
   for(MyMapEntryType myEntryType : arg0.entry) {
      hashMap.put(myEntryType.key, myEntryType.value);
   }
   return hashMap;
}

}