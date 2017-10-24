/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
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
