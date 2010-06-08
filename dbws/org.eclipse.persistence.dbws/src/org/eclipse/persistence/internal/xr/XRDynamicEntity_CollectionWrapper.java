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
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class XRDynamicEntity_CollectionWrapper extends XRDynamicEntity implements Collection<Object> {
    
    public static XRFieldInfo XRFI = new XRFieldInfo();
    static {
        XRFI.addFieldInfo("items", 0);
    }

    public XRDynamicEntity_CollectionWrapper() {
        super();
        super.set("items", new ArrayList<Object>());
    }

    @Override
    public XRFieldInfo getFieldInfo() {
        return XRFI;
    }

    public boolean add(Object e) {
        return ((Collection<Object>)_fields[0].fieldValue).add(e);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return ((Collection<Object>)_fields[0].fieldValue).addAll(c);
    }

    public void clear() {
        ((Collection<Object>)_fields[0].fieldValue).clear();
    }

    public boolean contains(Object o) {
        return ((Collection<Object>)_fields[0].fieldValue).contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return ((Collection<Object>)_fields[0].fieldValue).containsAll(c);
    }

    public boolean isEmpty() {
        return ((Collection<Object>)_fields[0].fieldValue).isEmpty();
    }

    public Iterator<Object> iterator() {
        return ((Collection<Object>)_fields[0].fieldValue).iterator();
    }

    public boolean remove(Object o) {
        return ((Collection<Object>)_fields[0].fieldValue).remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return ((Collection<Object>)_fields[0].fieldValue).removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return ((Collection<Object>)_fields[0].fieldValue).retainAll(c);
    }

    public int size() {
        return ((Collection<Object>)_fields[0].fieldValue).size();
    }

    public Object[] toArray() {
        return ((Collection<Object>)_fields[0].fieldValue).toArray();
    }

    public <T> T[] toArray(T[] a) {
        return (T[])((Collection<Object>)_fields[0].fieldValue).toArray(a);
    }
}