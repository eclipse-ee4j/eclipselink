package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.util.ArrayList;
import static org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewCache.PARAMETER_USER;

@SuppressWarnings("unchecked")
public class CreateViewCacheThread extends Thread {

    ArrayList m_viewCacheParameters;
    ViewCache m_viewCache;

    CreateViewCacheThread(ViewCache viewCache, ArrayList viewCacheParameters) {
        m_viewCache = viewCache;
        m_viewCacheParameters = viewCacheParameters;
    }

    public void run() {
        try {
            if (m_viewCacheParameters == null || m_viewCacheParameters.isEmpty()) {
                m_viewCache.fetch(PARAMETER_USER, null);
            }
            else {
                for (int i = 0; i < m_viewCacheParameters.size(); i++) {
                    m_viewCache.fetch((String)m_viewCacheParameters.get(i), null);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
