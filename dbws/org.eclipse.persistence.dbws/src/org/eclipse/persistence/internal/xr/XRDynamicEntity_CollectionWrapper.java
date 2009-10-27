/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
public class XRDynamicEntity_CollectionWrapper extends XRDynamicEntity implements Collection<Object> {
    
    static Map<String, IndexInfo> names2idx = new HashMap<String, IndexInfo>();
    static {
        names2idx.put("items", new IndexInfo(0, false));
    }

    public XRDynamicEntity_CollectionWrapper() {
        super(names2idx);
        fields = new Object[1];
        // use ArrayList as the Collection type
        fields[0] = new ArrayList<Object>();
    }

    public boolean add(Object e) {
        return ((Collection<Object>)fields[0]).add(e);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return ((Collection<Object>)fields[0]).addAll(c);
    }

    public void clear() {
        ((Collection<Object>)fields[0]).clear();
    }

    public boolean contains(Object o) {
        return ((Collection<Object>)fields[0]).contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return ((Collection<Object>)fields[0]).containsAll(c);
    }

    public boolean isEmpty() {
        return ((Collection<Object>)fields[0]).isEmpty();
    }

    public Iterator<Object> iterator() {
        return ((Collection<Object>)fields[0]).iterator();
    }

    public boolean remove(Object o) {
        return ((Collection<Object>)fields[0]).remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return ((Collection<Object>)fields[0]).removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return ((Collection<Object>)fields[0]).retainAll(c);
    }

    public int size() {
        return ((Collection<Object>)fields[0]).size();
    }

    public Object[] toArray() {
        return ((Collection<Object>)fields[0]).toArray();
    }

    public <T> T[] toArray(T[] a) {
        return (T[])((Collection<Object>)fields[0]).toArray(a);
    }
}