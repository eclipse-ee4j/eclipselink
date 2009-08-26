/*******************************************************************************
 * Copyright (c)  2008, Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     DaraniY  = 1.0 - Initialize contribution
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import java.util.Vector;
import javax.persistence.Cache;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * Implements the JPA Cache interface using the EclipseLink cache API through IdentityMapAccessor.
 * @author DaraniY
 */
public class CacheImpl implements Cache {

    private IdentityMapAccessor accessor;
    private EntityManagerFactoryImpl emf;
    private ServerSession serversession;

    public CacheImpl(EntityManagerFactoryImpl emf, IdentityMapAccessor accessor) {
        this.accessor = accessor;
        this.emf = emf;
        this.serversession = emf.getServerSession();
    }

    public boolean contains(Class cls, Object primaryKey) {
        this.emf.verifyOpen();
        return this.accessor.containsObjectInIdentityMap(createPKVector(cls, primaryKey), cls);
    }

    public void evict(Class cls, Object primaryKey) {
        this.emf.verifyOpen();
        this.accessor.invalidateObject(createPKVector(cls, primaryKey), cls);
    }

    public void evict(Class cls) {
        this.emf.verifyOpen();
        this.accessor.invalidateClass(cls);
    }

    public void evictAll() {
        this.emf.verifyOpen();
        this.accessor.invalidateAll();
    }

    private Vector createPKVector(Class cls, Object primaryKey){
        ClassDescriptor cdesc = this.serversession.getDescriptor(cls);
        CMP3Policy cmp = (CMP3Policy) (cdesc.getCMPPolicy());
        Vector pk = cmp.createPkVectorFromKey(primaryKey, this.serversession);
        return pk;
    }
}