/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa22.sessionbean;

import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.models.jpa22.attributeconverter.AttributeConverter;
import org.eclipse.persistence.testing.models.jpa22.attributeconverter.AttributeConverterHolder;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

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
