/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 5/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class NormalHoursAttributeTransformer implements AttributeTransformer {
    public void initialize(AbstractTransformationMapping mapping) {}
    
    public Object buildAttributeValue(Record record, Object instance, Session session) {
        String[] hours = new String[2];
        hours[0] = (String) record.get("normal-hours/start-time/text()");
        hours[1] = (String) record.get("normal-hours/end-time/text()");
        return hours;
    }
}
