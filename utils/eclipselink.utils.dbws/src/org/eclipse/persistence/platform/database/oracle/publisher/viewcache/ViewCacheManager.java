package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.Connection;

public class ViewCacheManager {
    public ViewCacheManager(Connection conn) {
        m_conn = conn;
        m_viewCachePool = new ViewCachePool();
    }

    public void add(ViewCache viewCache) {
        m_viewCachePool.add(viewCache);
    }

    public ViewCache get(String schema) {
        return (ViewCache)m_viewCachePool.get(schema);
    }

    /**
     * The CLASSPATH_MARKER is used as a prefix to the ViewCacheDir. If it is used, it means we
     * should try and look up the view cache as a class path resource instead of as a file.
     */
    public static String CLASSPATH_MARKER = ":;";

    public void setViewCachePoolCapacity(int size) {
        m_viewCachePool.setCapacity(size);
    }

    public void clearViewCache() {
        m_viewCachePool.clear();
    }

    @SuppressWarnings("unused")
    private Connection m_conn;
    private ViewCachePool m_viewCachePool;

}