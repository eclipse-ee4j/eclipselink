/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ExtendedMap3 implements Map<Foo, Bar> {

    private LinkedHashMap<Foo, Bar> map =  new LinkedHashMap<Foo, Bar>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Bar get(Object key) {
        return map.get(key);
    }

    @Override
    public Bar put(Foo key, Bar value) {
        return map.put(key, value);
    }

    @Override
    public Bar remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Foo, ? extends Bar> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Foo> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Bar> values() {
        return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<Foo, Bar>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Map<Foo, Bar >test = (Map<Foo, Bar>) obj;
        if(size() != test.size()) {
            return false;
        }
        for(Map.Entry<Foo, Bar> entry : entrySet()) {
            if(!entry.getValue().equals(test.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

}
