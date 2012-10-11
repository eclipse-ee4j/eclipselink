package org.eclipse.persistence.jpa.rs.util;

import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;

public class JTATransactionWrapper extends TransactionWrapper {
    
    @Override
    public void beginTransaction(EntityManager em) {
        AbstractSession session = JpaHelper.getEntityManagerFactory(em).getDatabaseSession();
        session.getExternalTransactionController().beginTransaction(session);
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
