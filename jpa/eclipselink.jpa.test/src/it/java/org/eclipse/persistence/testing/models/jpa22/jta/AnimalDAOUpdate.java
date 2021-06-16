/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     10/24/2017-3.0 Tomas Kraus
//       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
package org.eclipse.persistence.testing.models.jpa22.jta;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * EJB used to test 2nd step of JTA 1.1 statements synchronization.
 */
@Stateful
public class AnimalDAOUpdate {

    /** Entity manager injected from persistence context. */
    @PersistenceContext(unitName=AnimalDAO.PU_NAME)
    EntityManager em;

    /**
     * Update {@link Animal} entity.
     * @param id primary key of entity being updated
     * @param name new name to set
     * @param listener listener to call or {@code null} to skip listener call
     * @return updated and persisted {@link Animal} entity
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Animal updateAnimal(final int id, final String name, final AnimalCheck listener) {
        Animal animal = em.find(Animal.class, Integer.valueOf(id));
        Animal eventOld = animal.clone();
        animal.setName(name);
        Animal eventNew = animal.clone();
        AbstractSessionLog.getLog().log(SessionLog.INFO, "EM persist {0}", new Object[] { new String(animal.toString()) }, false);
        em.persist(animal);
        if (listener != null) {
            listener.onEvent(AnimalEvent.update(eventOld, eventNew));
        }
        return animal;
    }

}
