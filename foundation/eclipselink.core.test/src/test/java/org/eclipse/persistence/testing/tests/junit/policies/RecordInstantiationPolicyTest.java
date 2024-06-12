/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.junit.policies;

import org.eclipse.persistence.internal.descriptors.RecordInstantiationPolicy;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RecordInstantiationPolicyTest {

    private final float FLOAT_ATTR = 1.1F;
    private final Instant INSTANT_ATTR = Instant.parse("2024-05-27T10:15:30.00Z");

    @Test
    public void newRecordTest() {
        Class<NestedDetailRecord> clazz = NestedDetailRecord.class;
        //Values order is important. It must be same as order of Record attributes.
        List<Object> values = new ArrayList<>();
        values.add(FLOAT_ATTR);
        values.add(INSTANT_ATTR);
        RecordInstantiationPolicy<NestedDetailRecord> recordInstantiationPolicy = new RecordInstantiationPolicy<>(clazz);
        recordInstantiationPolicy.setValues(values);
        NestedDetailRecord nestedDetailRecord = (NestedDetailRecord) recordInstantiationPolicy.buildNewInstance();
        assertEquals(FLOAT_ATTR, nestedDetailRecord.floatAttribute(), 0);
        assertEquals(INSTANT_ATTR, nestedDetailRecord.instantAttribute());
    }

    @Test
    public void newRecordWithNullTest() {
        Class<NestedDetailRecord> clazz = NestedDetailRecord.class;
        //Values order is important. It must be same as order of Record attributes.
        List<Object> values = new ArrayList<>();
        values.add(FLOAT_ATTR);
        values.add(null);
        RecordInstantiationPolicy<NestedDetailRecord> recordInstantiationPolicy = new RecordInstantiationPolicy<>(clazz);
        recordInstantiationPolicy.setValues(values);
        NestedDetailRecord nestedDetailRecord = (NestedDetailRecord) recordInstantiationPolicy.buildNewInstance();
        assertEquals(FLOAT_ATTR, nestedDetailRecord.floatAttribute(), 0);
        assertNull(nestedDetailRecord.instantAttribute());
    }
}
