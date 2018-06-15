/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.models.jaxb.employee;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

/**
 * These methods (and this interface) do nothing.
 * Just used as a MWClass definition for the Employee.normalHours
 * transformation mapping.
 */

public final class NormalHoursTransformer
    implements AttributeTransformer, FieldTransformer
{
    public void initialize(AbstractTransformationMapping mapping) {}

    public Object buildAttributeValue(Record record, Object object, Session session) {
        return null;
    }

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        return null;
    }
}
