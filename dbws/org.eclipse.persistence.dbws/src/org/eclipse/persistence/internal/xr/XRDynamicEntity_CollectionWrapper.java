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
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//EclipseLink imports

public class XRDynamicEntity_CollectionWrapper extends XRDynamicEntity implements Collection<Object> {

    static final String ITEMS_PROPERTY = "items";
    public static XRDynamicPropertiesManager DPM = new XRDynamicPropertiesManager();
    static {
        Set<String> propertiesNameSet = new HashSet<String>();
        propertiesNameSet.add(ITEMS_PROPERTY);
        DPM.setPropertyNames(propertiesNameSet);
    }

    public XRDynamicEntity_CollectionWrapper() {
        super();
        super.set(ITEMS_PROPERTY, new ArrayList<Object>());
    }

    @Override
    public XRDynamicPropertiesManager fetchPropertiesManager() {
        return DPM;
    }

    public boolean add(Object e) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).add(e);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).addAll(c);
    }

    public void clear() {
        ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).clear();
    }

    public boolean contains(Object o) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).containsAll(c);
    }

    public boolean isEmpty() {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).isEmpty();
    }

    public Iterator<Object> iterator() {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).iterator();
    }

    public boolean remove(Object o) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).retainAll(c);
    }

    public int size() {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).size();
    }

    public Object[] toArray() {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).toArray();
    }

    public <T> T[] toArray(T[] a) {
        return ((Collection<Object>)propertiesMap.get(ITEMS_PROPERTY).getValue()).toArray(a);
    }
}