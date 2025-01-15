/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.jpa.advanced.entities.EntityFloat;

class EntityFloatPopulator {

    private EntityFloatPopulator() {
        throw new UnsupportedOperationException("No instances of EntityFloatPopulator are allowed");
    }

    static EntityFloat[] ENTITY_FLOAT = new EntityFloat[] {
            // Tallest and smallest length
            new EntityFloat(70071, 17.0f, 17.1f, 7.7f, "testOLGH28289#70071"),
            // Tallest and largest length
            new EntityFloat(70077, 77.0f, 17.7f, 7.7f, "testOLGH28289#70077"),
            new EntityFloat(70007, 70.0f, 10.7f, 0.7f, "testOLGH28289#70007")
    };

    static void populate(Session session) {
        List<Object> entities = Arrays.asList(ENTITY_FLOAT);
        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        unitOfWork.removeAllReadOnlyClasses();
        unitOfWork.registerAllObjects(entities);
        unitOfWork.commit();
    }

}
