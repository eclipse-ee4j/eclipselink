/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework.jpa.junit;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.testing.framework.*;

public class EntityContainerTestBase extends AutoVerifyTestCase {

    @Override
    public void setup() {
        CMP3TestModel.createEntityManager();
        CMP3TestModel.getServerSession().setSessionLog(getSession().getSessionLog());
    }

    @Override
    public void reset() {
        if(getEntityManager().getTransaction().isActive()) {
            try {
                getEntityManager().getTransaction().rollback();
            } catch (Exception ex) {
            }
        }
        CMP3TestModel.closeAndRemoveEntityManager();
    }

    /**
     * Start a new JTS transaction.
     */
    @Override
    public void beginTransaction() {
        getTransaction().begin();
    }

    /**
     * Commit the existing JTS transaction.
     */
    @Override
    public void commitTransaction() {
        getTransaction().commit();
    }

    /**
     * Roll back the existing JTS transaction.
     */
    @Override
    public void rollbackTransaction() {
        if (getTransaction().isActive()){
            getTransaction().rollback();
        }
    }

    public EntityManager getEntityManager() {
        return CMP3TestModel.getEntityManager();
    }

    public jakarta.persistence.EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();

    }
}
