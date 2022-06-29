/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.models.jpa.sessionbean;

import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.models.jpa.attributeconverter.AttributeConverter;
import org.eclipse.persistence.testing.models.jpa.attributeconverter.AttributeConverterHolder;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@Stateless
public class AttributeConverterTestBean implements InjectionTest {

    @PersistenceUnit(name="jpa-sessionbean")
    private EntityManagerFactory emf;

    @Override
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

    @Override
    public boolean triggerPreDestroy(){
        cleanup();
        emf.unwrap(JpaEntityManagerFactory.class).getServerSession().cleanUpInjectionManager();
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
