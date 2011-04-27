/*******************************************************************************
 * Copyright (c) 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/19/2009-2.0 Gordon Yorke 
 *       - 239825: XML configuration for Interceptors and Default redirectors
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;

public class CacheAuditor extends CacheInterceptor {
    
    protected boolean shouldThrow;

    /**
     * @return the shouldThrow
     */
    public boolean isShouldThrow() {
        return shouldThrow;
    }

    /**
     * @param shouldThrow the shouldThrow to set
     */
    public void setShouldThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    public CacheAuditor(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession);
    }

    @Override
    public Object clone() {
        return new CacheAuditor(targetIdentityMap, interceptedSession);
    }

    @Override
    protected CacheKeyInterceptor createCacheKeyInterceptor(CacheKey wrappedCacheKey) {
        return new CacheKeyInterceptor(wrappedCacheKey){
            public void release() {
                wrappedKey.release();
                if (shouldThrow){
                    throw new javax.persistence.OptimisticLockException("FALSE-EXCEPTION");
                }
            }

        };
    }

    public void release() {
    }
    
    @Override
    public void lazyRelationshipLoaded(Object object, ForeignReferenceMapping mapping) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session) {
        // TODO Auto-generated method stub
        return null;
    }
}

