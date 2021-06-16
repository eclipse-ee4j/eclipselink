/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial contribution for Bug 366748 - JPA 2.1 Injectable Entity Listeners
package org.eclipse.persistence.testing.models.jpa22.sessionbean;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.models.jpa22.entitylistener.EntityListener;
import org.eclipse.persistence.testing.models.jpa22.entitylistener.EntityListenerHolder;

@Stateless
public class EntityListenerTestBean implements InjectionTest {

    @PersistenceUnit(name="jpa22-sessionbean")
    private EntityManagerFactory emf;

    public boolean triggerInjection(){
        EntityListener.INJECTED_RETURN_VALUE = false;
        EntityListener.POST_CONSTRUCT_CALLS = 0;
        EntityManager em = emf.createEntityManager();
        EntityListenerHolder holder = new EntityListenerHolder();
        em.persist(holder);
        return EntityListener.INJECTED_RETURN_VALUE && EntityListener.POST_CONSTRUCT_CALLS == 1;
    }

    public boolean triggerPreDestroy(){
        EntityListener.PRE_DESTROY_CALLS = 0;
        emf.unwrap(JpaEntityManagerFactory.class).unwrap().close();
        return EntityListener.PRE_DESTROY_CALLS == 1;
    }
}
