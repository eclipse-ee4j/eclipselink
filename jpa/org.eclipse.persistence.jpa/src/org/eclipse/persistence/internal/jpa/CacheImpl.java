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
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 *@inheritDoc
 *@author DaraniY
 */
public class CacheImpl {

    private IdentityMapAccessor imap;
    private EntityManagerFactoryImpl emf;
    private ServerSession serversession;

    public CacheImpl(EntityManagerFactoryImpl emf, IdentityMapAccessor imap) {
        this.imap = imap;
        this.emf = emf;
        this.serversession = emf.getServerSession();
    }

    public boolean contains(Class cls, Object primaryKey) {
        emf.verifyOpen();
        return imap.containsObjectInIdentityMap(createPKVector(cls, primaryKey), cls);
    }

    public void evict(Class cls, Object primaryKey) {
        emf.verifyOpen();
        imap.invalidateObject(createPKVector(cls, primaryKey), cls);
    }

    public void evict(Class cls) {
        emf.verifyOpen();
        imap.invalidateClass(cls);
    }

    public void evictAll() {
        emf.verifyOpen();
        imap.invalidateAll();
    }

    private Vector createPKVector(Class cls, Object primaryKey){
        ClassDescriptor cdesc = serversession.getDescriptor(cls);
        CMP3Policy cmp = (CMP3Policy) (cdesc.getCMPPolicy());
        Vector pk = cmp.createPkVectorFromKey(primaryKey, (AbstractSession) serversession);
        return pk;
    }
}
