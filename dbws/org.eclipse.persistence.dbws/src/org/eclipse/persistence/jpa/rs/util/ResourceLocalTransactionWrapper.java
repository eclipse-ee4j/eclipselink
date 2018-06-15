/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.util;

import javax.persistence.EntityManager;

public class ResourceLocalTransactionWrapper extends TransactionWrapper {

    @Override
    public void beginTransaction(EntityManager em) {
        em.getTransaction().begin();

    }

    @Override
    public void commitTransaction(EntityManager em) {
        em.getTransaction().commit();

    }

    @Override
    public void rollbackTransaction(EntityManager em) {
        em.getTransaction().rollback();

    }

}
