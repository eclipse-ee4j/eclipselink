/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.config;

import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;

/**
 * JPA scripting API implementation helper class.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
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
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(
            Object[] pkList, ClassDescriptor descriptor, AbstractSession session) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Object, CacheKey> getAllCacheKeysFromIdentityMapWithEntityPK(
            Object[] arg0, ClassDescriptor arg1, AbstractSession arg2) {
        // TODO Auto-generated method stub
        return null;
    }

}

