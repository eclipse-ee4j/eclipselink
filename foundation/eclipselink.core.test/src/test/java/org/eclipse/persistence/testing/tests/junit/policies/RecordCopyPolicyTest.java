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

import org.eclipse.persistence.descriptors.copying.RecordCopyPolicy;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class RecordCopyPolicyTest {

    private final int INT_ATTR = 1;
    private final String STRING_ATTR = "abcde";
    private final float FLOAT_ATTR = 1.1F;
    private final Instant INSTANT_ATTR = Instant.parse("2024-05-27T10:15:30.00Z");

    @Test
    public void cloneNestedRecordTest() {
        NestedDetailRecord nestedDetailRecord = new NestedDetailRecord(FLOAT_ATTR, INSTANT_ATTR);
        NestedMasterRecord source = new NestedMasterRecord(INT_ATTR, STRING_ATTR, nestedDetailRecord);
        RecordCopyPolicy recordCopyPolicy = new RecordCopyPolicy();
        NestedMasterRecord clone = (NestedMasterRecord)recordCopyPolicy.buildClone(source, null);
        assertEquals(source, clone);
    }

    @Test
    public void cloneNestedRecordWithNullTest() {
        NestedDetailRecord nestedDetailRecord = new NestedDetailRecord(FLOAT_ATTR, INSTANT_ATTR);
        NestedMasterRecord source = new NestedMasterRecord(INT_ATTR, null, nestedDetailRecord);
        RecordCopyPolicy recordCopyPolicy = new RecordCopyPolicy();
        NestedMasterRecord clone = (NestedMasterRecord)recordCopyPolicy.buildClone(source, null);
        assertEquals(source, clone);
    }
}
