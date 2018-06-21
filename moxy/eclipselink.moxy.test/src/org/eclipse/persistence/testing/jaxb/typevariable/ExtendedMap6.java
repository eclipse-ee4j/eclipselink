/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ExtendedMap6<KEY extends Foo, VALUE extends Bar> implements Map<KEY, VALUE> {

    private LinkedHashMap<KEY, VALUE> map =  new LinkedHashMap<KEY, VALUE>();

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
    public VALUE get(Object key) {
        return map.get(key);
    }

    @Override
    public VALUE put(KEY key, VALUE value) {
        return map.put(key, value);
    }

    @Override
    public VALUE remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends KEY, ? extends VALUE> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<KEY> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<VALUE> values() {
        return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<KEY, VALUE>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Map<KEY, VALUE >test = (Map<KEY, VALUE>) obj;
        if(size() != test.size()) {
            return false;
        }
        for(Map.Entry<KEY, VALUE> entry : entrySet()) {
            if(!entry.getValue().equals(test.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

}
