/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.models.jpa22.jta;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Store values for {@link Animal} entity check.
 */
@Singleton
public class AnimalCheck {

    /** Entity manager injected from persistence context. */
    @PersistenceContext(unitName=AnimalDAO.PU_NAME)
    EntityManager em;

    /** Name attribute before refresh. */
    String preRefreshName;

    /** Name attribute after refresh. */
    String postRefreshName;

    /**
     * Process {@link Animal} entity modification event.
     *
     * @param event @link Animal} entity modification event
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onEvent(AnimalEvent event) {
        final int id = event.getOld().getId();
        Animal animal = em.find(Animal.class, id);
        preRefreshName = animal.getName();
        em.refresh(animal);
        postRefreshName = animal.getName();
    }

    public String getPreRefreshName() {
        return preRefreshName;
    }

    public String getPostRefreshName() {
        return postRefreshName;
    }

}
