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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

//javase imports
import java.util.ArrayList;

class ViewCachePool {

    protected ArrayList<ViewCache> m_pool;
    protected int m_capacity;

    ViewCachePool() {
        this(1);
    }

    ViewCachePool(int capacity) {
        m_capacity = capacity;
        m_pool = new ArrayList<ViewCache>();
    }

    void setCapacity(int size) {
        m_capacity = size;
        while (isFull()) {
            remove();
        }
    }

    synchronized void add(ViewCache viewCache) {
        for (int i = 0; i < m_pool.size(); i++) {
            ViewCache v = m_pool.get(i);
            if (v.getUser().equalsIgnoreCase(viewCache.getUser())) {
                m_pool.remove(i);
            }
        }
        if (isFull()) {
            remove();
        }
        m_pool.add(viewCache);
    }

    boolean isFull() {
        return m_capacity != 0 && m_pool.size() >= m_capacity;
    }

    ViewCache get(String userName) {
        for (int i = 0; i < m_pool.size(); i++) {
            ViewCache v = m_pool.get(i);
            if (v.getUser().equalsIgnoreCase(userName)) {
                return v;
            }
        }
        return null;
    }

    ViewCache get(int i) {
        return m_pool.get(i);
    }

    int size() {
        return m_pool.size();
    }

    synchronized ViewCache remove() {
        if (m_pool.size() == 0) {
            return null;
        }
        return m_pool.remove(0);
    }

    synchronized void clear() {
        m_pool.clear();
    }

    void refresh() {
        int len = m_pool.size();
        for (int i = 0; i < len; i++) {
            m_pool.get(i).refresh();
        }
    }
}