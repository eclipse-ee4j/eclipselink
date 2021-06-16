/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa22.sessionbean;

import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.models.jpa22.attributeconverter.AttributeConverter;
import org.eclipse.persistence.testing.models.jpa22.attributeconverter.AttributeConverterHolder;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@Stateless
public class AttributeConverterTestBean implements InjectionTest {

    @PersistenceUnit(name="jpa22-sessionbean")
    private EntityManagerFactory emf;

    public boolean triggerInjection(){
        cleanup();
        EntityManager em = emf.createEntityManager();
        AttributeConverterHolder holder = new AttributeConverterHolder();
        em.persist(holder);
        em.flush();
        em.clear();
        em.find(AttributeConverterHolder.class, holder.getId());
        return AttributeConverter.INJECTED_RETURN_VALUE && AttributeConverter.CONVERT_TO_DB_CALLS == 1 && AttributeConverter.CONVERT_TO_ENTITY_CALLS == 1;
    }

    public boolean triggerPreDestroy(){
        cleanup();
        emf.unwrap(JpaEntityManagerFactory.class).unwrap().close();
        return AttributeConverter.PRE_DESTROY_CALLS == 1;
    }

    private void cleanup() {
        AttributeConverter.INJECTED_RETURN_VALUE = false;
        AttributeConverter.CONVERT_TO_DB_CALLS = 0;
        AttributeConverter.CONVERT_TO_ENTITY_CALLS = 0;
        AttributeConverter.POST_CONSTRUCT_CALLS = 0;
        AttributeConverter.PRE_DESTROY_CALLS = 0;
    }
}
