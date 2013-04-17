/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;

public class JTATransactionWrapper extends TransactionWrapper {
    
    @Override
    public void beginTransaction(EntityManager em) {
        AbstractSession session = JpaHelper.getEntityManagerFactory(em).getDatabaseSession();
        session.getExternalTransactionController().beginTransaction(session);
        // EMs obtained outside the transaction is now prevented from participating or flushing to the trans unless join is called. 
        if (!em.isJoinedToTransaction()) {
            em.joinTransaction();
        }
    }

    @Override
    public void commitTransaction(EntityManager em) {
        AbstractSession session = JpaHelper.getEntityManagerFactory(em).getDatabaseSession();
        session.getExternalTransactionController().commitTransaction(session);
    }

    @Override
    public void rollbackTransaction(EntityManager em) {
        AbstractSession session = JpaHelper.getEntityManagerFactory(em).getDatabaseSession();
        session.getExternalTransactionController().rollbackTransaction(session);
    }
}
