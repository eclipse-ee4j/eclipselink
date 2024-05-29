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
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RecordInstantiationPolicyTest {

    private final int INT_ATTR = 1;
    private final String STRING_ATTR = "abcde";
    private final float FLOAT_ATTR = 1.1F;
    private final Date DATE_ATTR = Date.from(Instant.parse("2024-05-27T10:15:30.00Z"));

    @Test
    public void newNestedRecordTest() {
        Class clazz = NestedMasterRecord.class;
        //Values order is important. It must be same as order of Record attributes.
        List values = new ArrayList<>();
        values.add(INT_ATTR);
        values.add(STRING_ATTR);
        values.add(FLOAT_ATTR);
        values.add(DATE_ATTR);
        RecordInstantiationPolicy recordInstantiationPolicy = new RecordInstantiationPolicy();
        NestedMasterRecord nestedMasterRecord = (NestedMasterRecord) recordInstantiationPolicy.newRecord(clazz, values);
        assertEquals(INT_ATTR, nestedMasterRecord.intAttribute());
        assertEquals(STRING_ATTR, nestedMasterRecord.stringAttribute());
        assertEquals(FLOAT_ATTR, nestedMasterRecord.nestedDetailRecord().floatAttribute(), 0f);
        assertEquals(DATE_ATTR, nestedMasterRecord.nestedDetailRecord().dateAttribute());
    }

    @Test
    public void newNestedRecordWithNullTest() {
        Class clazz = NestedMasterRecord.class;
        //Values order is important. It must be same as order of Record attributes.
        List values = new ArrayList<>();
        values.add(INT_ATTR);
        values.add(null);
        values.add(FLOAT_ATTR);
        values.add(DATE_ATTR);
        RecordInstantiationPolicy recordInstantiationPolicy = new RecordInstantiationPolicy();
        NestedMasterRecord nestedMasterRecord = (NestedMasterRecord) recordInstantiationPolicy.newRecord(clazz, values);
        assertEquals(INT_ATTR, nestedMasterRecord.intAttribute());
        assertNull(nestedMasterRecord.stringAttribute());
        assertEquals(FLOAT_ATTR, nestedMasterRecord.nestedDetailRecord().floatAttribute(), 0f);
        assertEquals(DATE_ATTR, nestedMasterRecord.nestedDetailRecord().dateAttribute());
    }
}
