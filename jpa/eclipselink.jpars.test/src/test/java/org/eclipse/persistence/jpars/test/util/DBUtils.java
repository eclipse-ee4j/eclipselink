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
//      gonural - initial
package org.eclipse.persistence.jpars.test.util;

import jakarta.persistence.EntityManager;

public class DBUtils {

    /**
     * Instantiates a new dB utils.
     */
    public DBUtils() {

    }

    /**
     * Db delete.
     *
     * @param object the object
     * @param em the em
     */
    public static void dbDelete(Object object, EntityManager em) {
        em.getTransaction().begin();
        Object merged = em.merge(object);
        //em.refresh(merged);
        em.remove(merged);
        em.getTransaction().commit();
    }

    /**
     * Db create.
     *
     * @param object the object
     * @param em the em
     */
    public static void dbCreate(Object object, EntityManager em) {
        em.getTransaction().begin();
        em.persist(object);
        em.getTransaction().commit();
    }

    /**
     * Db read.
     *
     * @param <T> the generic type
     * @param id the id
     * @param resultClass the result class
     * @param em the em
     * @return the t
     */
    public static <T> T dbRead(Object id, Class<T> resultClass, EntityManager em) {
        em.getEntityManagerFactory().getCache().evictAll();
        return em.find(resultClass, id);
    }

    /**
     * Db update.
     *
     * @param object the object
     * @param em the em
     */
    public static void dbUpdate(Object object, EntityManager em) {
        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
    }
}
