/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
