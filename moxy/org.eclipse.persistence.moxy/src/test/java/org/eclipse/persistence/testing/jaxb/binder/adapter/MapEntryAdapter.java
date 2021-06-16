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
// dmccann - June 4/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.binder.adapter;

import java.util.Map;
import java.util.TreeMap;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
