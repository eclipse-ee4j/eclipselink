/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class NormalHoursTransformer implements AttributeTransformer {

    public String[] buildAttributeValue(Record record, Object object, Session session) {
        String startTime = (String)record.get("normal-hours/start-time/text()");
        String endTime = (String)record.get("normal-hours/end-time/text()");

        return new String[]{startTime, endTime};

    }

    public void initialize(AbstractTransformationMapping mapping) {
        // TODO Auto-generated method stub

    }


}
