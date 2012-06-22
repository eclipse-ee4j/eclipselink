/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 4/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.binder.adapter;

import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter that converts between Map<Integer, String> and MapEntry.
 * 
 */
public class MapEntryAdapter extends XmlAdapter<MapEntry, Map<Integer, String>> {
    public Map<Integer, String> unmarshal(MapEntry mapEntry) throws Exception {
        Map<Integer, String> map = new TreeMap<Integer, String>();
        while (mapEntry != null) {
            map.put(mapEntry.key, mapEntry.value);
            mapEntry = mapEntry.nextValue;
        }
        return map;
    }

    public MapEntry marshal(Map<Integer, String> map) throws Exception {
        MapEntry lastEntry = null;
        for (Map.Entry<Integer, String> mapEntry : map.entrySet()) {
            lastEntry = new MapEntry(mapEntry.getKey(), mapEntry.getValue(), lastEntry);
        }
        return lastEntry;
    }
}
