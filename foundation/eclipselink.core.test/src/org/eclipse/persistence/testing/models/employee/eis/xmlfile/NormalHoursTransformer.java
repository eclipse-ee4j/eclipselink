/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.employee.eis.xmlfile;

import java.sql.Time;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class NormalHoursTransformer implements FieldTransformer, AttributeTransformer {
    public NormalHoursTransformer() {
    }

    public void initialize(AbstractTransformationMapping mapping) {
    }

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        Employee employee = (Employee)instance;
        if (fieldName.equalsIgnoreCase("START_TIME/text()")) {
            return employee.getStartTime();
        } else if (fieldName.equalsIgnoreCase("END_TIME/text()")) {
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
        hours[0] = (Time)session.getProject().getDatasourceLogin().getDatasourcePlatform().convertObject(row.get("START_TIME/text()"), Time.class);
        hours[1] = (Time)session.getProject().getDatasourceLogin().getDatasourcePlatform().convertObject(row.get("END_TIME/text()"), Time.class);

        return hours;
    }
}
