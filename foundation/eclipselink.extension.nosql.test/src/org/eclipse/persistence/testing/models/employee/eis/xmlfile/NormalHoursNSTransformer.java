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
package org.eclipse.persistence.testing.models.employee.eis.xmlfile;


// JDK imports
import java.sql.Time;

// TopLink imports
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class NormalHoursNSTransformer implements FieldTransformer, AttributeTransformer {
    XMLField startTimeField;
    XMLField endTimeField;
    NamespaceResolver nsResolver;

    public NormalHoursNSTransformer() {
        startTimeField = new XMLField("myns:START_TIME/text()");
        endTimeField = new XMLField("myns:END_TIME/text()");
    }

    public void initialize(AbstractTransformationMapping mapping) {
        nsResolver = ((org.eclipse.persistence.eis.EISDescriptor)mapping.getDescriptor()).getNamespaceResolver();
        startTimeField.setNamespaceResolver(nsResolver);
        endTimeField.setNamespaceResolver(nsResolver);
    }

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        Employee employee = (Employee)instance;
        if (fieldName.equalsIgnoreCase("myns:START_TIME/text()")) {
            return employee.getStartTime();
        } else if (fieldName.equalsIgnoreCase("myns:END_TIME/text()")) {
            return employee.getEndTime();
        }
        return null;
    }

    public Object buildAttributeValue(Record row, Object object, Session session) {
        Time[] hours = new Time[2];

        /**
         * This conversion allows for the database type not to match, i.e. may be a Timestamp or
         * String.
         */
        hours[0] = (Time)session.getProject().getDatasourceLogin().getDatasourcePlatform().convertObject(row.get(startTimeField), Time.class);
        hours[1] = (Time)session.getProject().getDatasourceLogin().getDatasourcePlatform().convertObject(row.get(endTimeField), Time.class);

        return hours;
    }
}
