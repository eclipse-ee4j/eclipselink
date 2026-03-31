/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.junit.jupiter.api.Test;

class DatabaseQueryArgumentFieldTest {

    @Test
    void rowFromArgumentsKeepsMoreSpecificFieldMetadataForDuplicateArgumentNames() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", Object.class);
        query.addArgument("value", Long.class);

        AbstractRecord row = query.rowFromArguments(Arrays.asList(null, null), null);

        assertEquals(Long.class, row.getField(new DatabaseField("value")).getType());
    }

    @Test
    void rowFromArgumentsDoesNotDowngradeExistingSpecificFieldMetadata() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", Long.class);
        query.addArgument("value", Object.class);

        AbstractRecord row = query.rowFromArguments(Arrays.asList(null, null), null);

        assertEquals(Long.class, row.getField(new DatabaseField("value")).getType());
    }
}
