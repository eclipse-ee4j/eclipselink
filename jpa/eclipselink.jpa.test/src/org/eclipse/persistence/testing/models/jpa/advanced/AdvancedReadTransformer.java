/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.sql.Time;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AdvancedReadTransformer implements AttributeTransformer {
    
    String attributeName;
    
    public AdvancedReadTransformer() {
        super();
    }
    
    /**
     * @param mapping - The mapping associated with this transformer. Only used if some special information is required.
     */
    public void initialize(AbstractTransformationMapping mapping) {
        this.attributeName = mapping.getAttributeName();
    }

    /**
     * @param record - The metadata being used to build the object.
     * @param session - the current session
     * @param object - The current object that the attribute is being built for.
     * @return - The attribute value to be built into the object containing this mapping.
     */
    public Object buildAttributeValue(Record record, Object object, Session session) {
        if(attributeName.equals("overtimeHours")) {
            Time[] hours = new Time[2];
            /** This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
            hours[0] = (Time) session.getDatasourcePlatform().convertObject(record.get("START_OVERTIME"), java.sql.Time.class);
            hours[1] = (Time) session.getDatasourcePlatform().convertObject(record.get("END_OVERTIME"), java.sql.Time.class);
            return hours;
        } else {
            throw new RuntimeException("Unknown attribute");
        }
    }
}
