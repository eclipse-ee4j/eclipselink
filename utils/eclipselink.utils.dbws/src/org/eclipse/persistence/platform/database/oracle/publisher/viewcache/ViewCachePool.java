package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.util.ArrayList;

/**
 * ViewCachePool: a round-robin pool of ViewCache instances
 */
@SuppressWarnings("unchecked")
class ViewCachePool {
    ViewCachePool() {
        this(1);
    }

    ViewCachePool(int capacity) {
        m_capacity = capacity;
        m_pool = new ArrayList();
    }

    void setCapacity(int size) {
        m_capacity = size;
        while (isFull()) {
            remove();
        }
    }

    synchronized void add(ViewCache viewCache) {
        for (int i = 0; i < m_pool.size(); i++) {
            ViewCache v = (ViewCache)m_pool.get(i);
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
            ViewCache v = (ViewCache)m_pool.get(i);
            if (v.getUser().equalsIgnoreCase(userName)) {
                return v;
            }
        }
        return null;
    }

    ViewCache get(int i) {
        return (ViewCache)m_pool.get(i);
    }

    int size() {
        return m_pool.size();
    }

    synchronized ViewCache remove() {
        if (m_pool.size() == 0) {
            return null;
        }
        return (ViewCache)m_pool.remove(0);
    }

    synchronized void clear() {
        m_pool.clear();
    }

    void refresh() {
        int len = m_pool.size();
        for (int i = 0; i < len; i++) {
            ((ViewCache)m_pool.get(i)).refresh();
        }
    }

    private ArrayList m_pool;
    private int m_capacity;
}
