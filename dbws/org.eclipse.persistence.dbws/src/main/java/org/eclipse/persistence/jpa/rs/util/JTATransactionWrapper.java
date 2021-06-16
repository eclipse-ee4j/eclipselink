/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.util;

import jakarta.persistence.EntityManager;

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
