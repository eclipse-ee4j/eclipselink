/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public class SpecialMap<T1, T2> implements Map<T1, T2> {

    private Map<T1, T2> map = new HashMap<T1, T2>();

    public SpecialMap() {
    }

    public SpecialMap(Map<T1, T2> map) {
        this.map = map;
    }

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
    public T2 get(Object key) {
        return map.get(key);
    }

    @Override
    public T2 put(T1 key, T2 value) {
        return map.put(key, value);
    }

    @Override
    public T2 remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends T1, ? extends T2> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<T1> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<T2> values() {
        return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<T1, T2>> entrySet() {
        return map.entrySet();
    }

}
