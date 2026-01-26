/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.platform.database;

import java.util.Map;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

public class Oracle21Platform extends Oracle19Platform {
    public Oracle21Platform() {
        super();
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypes = super.buildDatabaseTypes();
        fieldTypes.put(java.time.LocalDateTime.class, new FieldDefinition.DatabaseType("TIMESTAMP", 9));
        fieldTypes.put(java.time.LocalTime.class, new FieldDefinition.DatabaseType("TIMESTAMP", 9));
        return fieldTypes;
    }

    @Override
    public boolean supportsFractionalTime() {
        return true;
    }

}
