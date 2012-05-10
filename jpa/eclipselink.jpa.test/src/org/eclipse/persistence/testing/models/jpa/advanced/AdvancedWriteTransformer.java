/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.Session;

public class AdvancedWriteTransformer implements FieldTransformer {

    String attributeName;
    
    public AdvancedWriteTransformer() {
        super();
    }
    
    /**
     * Initialize this transformer. Only required if the user needs some special
     * information from the mapping in order to do the transformation
     * @param mapping - the mapping this transformer is associated with.
     */
    public void initialize(AbstractTransformationMapping mapping) {
        this.attributeName = mapping.getAttributeName();
    }

    /**
     * @param instance - an instance of the domain class which contains the attribute
     * @param session - the current session
     * @param fieldName - the name of the field being transformed. Used if the user wants to use this transformer for multiple fields.
     * @return - The value to be written for the field associated with this transformer
     */
    public Time buildFieldValue(Object instance, String fieldName, Session session) {
        if(attributeName.equals("overtimeHours")) {
            if(fieldName.equals("START_OVERTIME")) {
                return ((Employee)instance).getOvertimeHours()[0];
            } else if(fieldName.equals("END_OVERTIME")) {
                return ((Employee)instance).getOvertimeHours()[1];
            } else {
                throw new RuntimeException("Unknown field");
            }
        } else {
            throw new RuntimeException("Unknown attribute");
        }
    }
}
