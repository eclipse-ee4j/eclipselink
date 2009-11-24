package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;

public class CacheAuditor extends CacheInterceptor {

    public CacheAuditor(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession);
    }

    @Override
    public Object clone() {
        return new CacheAuditor(targetIdentityMap, interceptedSession);
    }

    @Override
    protected CacheKeyInterceptor createCacheKeyInterceptor(CacheKey wrappedCacheKey) {
        return new CacheKeyInterceptor(wrappedCacheKey);
    }

    public void release() {
    }

}
