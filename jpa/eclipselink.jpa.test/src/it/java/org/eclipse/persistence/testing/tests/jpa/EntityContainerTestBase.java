/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.testing.framework.*;

public class EntityContainerTestBase extends AutoVerifyTestCase {

    public void setup() {
        CMP3TestModel.createEntityManager();
        CMP3TestModel.getServerSession().setSessionLog(getSession().getSessionLog());
    }

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
    public void beginTransaction() {
        getTransaction().begin();
    }

    /**
     * Commit the existing JTS transaction.
     */
    public void commitTransaction() {
        getTransaction().commit();
    }

    /**
     * Roll back the existing JTS transaction.
     */
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
