/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import java.sql.Time;

public class AdvancedReadTransformer implements AttributeTransformer {

    String attributeName;

    public AdvancedReadTransformer() {
        super();
    }

    /**
     * @param mapping - The mapping associated with this transformer. Only used if some special information is required.
     */
    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.attributeName = mapping.getAttributeName();
    }

    /**
     * @param dataRecord - The metadata being used to build the object.
     * @param session - the current session
     * @param object - The current object that the attribute is being built for.
     * @return - The attribute value to be built into the object containing this mapping.
     */
    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        if(attributeName.equals("overtimeHours")) {
            Time[] hours = new Time[2];
            /* This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
            hours[0] = session.getDatasourcePlatform().convertObject(dataRecord.get("START_OVERTIME"), Time.class);
            hours[1] = session.getDatasourcePlatform().convertObject(dataRecord.get("END_OVERTIME"), Time.class);
            return hours;
        } else {
            throw new RuntimeException("Unknown attribute");
        }
    }
}
