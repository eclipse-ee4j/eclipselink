/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public class SpecialMap<T1, T2> implements Map<T1, T2> {

    private Map<T1, T2> map = new HashMap<T1, T2>();

    public SpecialMap() {
    }

    public SpecialMap(Map<T1, T2> map) {
        this.map = map;
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public T2 get(Object key) {
        return map.get(key);
    }

    public T2 put(T1 key, T2 value) {
        return map.put(key, value);
    }

    public T2 remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends T1, ? extends T2> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public Set<T1> keySet() {
        return map.keySet();
    }

    public Collection<T2> values() {
        return map.values();
    }

    public Set<java.util.Map.Entry<T1, T2>> entrySet() {
        return map.entrySet();
    }

}