/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateMapping;

import java.util.Map;


//This is an assisting class for AggregateMapping related tests
public class TestAggregateMapping extends AggregateMapping {

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    @Override
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {

    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    @Override
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {

    }

    @Override
    public Object getAttributeValueFromObject(Object object) {
        return null;
    }

    @Override
    public void setReferenceDescriptor(ClassDescriptor aDescriptor) {
        referenceDescriptor = aDescriptor;
    }
}
