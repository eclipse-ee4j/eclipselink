/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     06/26/2018 - Will Dazey
//       - 532160 : Add support for non-extension OracleXPlatform classes
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.util.Map;

/**
 * <p><b>Purpose:</b>
 * Provides Oracle version specific behavior when 
 * org.eclipse.persistence.oracle bundle is not available.
 */
public class Oracle10Platform extends Oracle9Platform {

    public Oracle10Platform(){
        super();
    }

    /**
     * INTERNAL:
     * Add XMLType as the default database type for org.w3c.dom.Documents.
     * Add TIMESTAMP, TIMESTAMP WITH TIME ZONE and TIMESTAMP WITH LOCAL TIME ZONE
     */
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypes = super.buildDatabaseTypes();
        // Offset classes contain an offset from UTC/Greenwich in the ISO-8601 calendar system so TZ should be included
        // TIMESTAMP WITH TIME ZONE is supported since 10g
        fieldTypes.put(java.time.OffsetDateTime.class, new FieldDefinition.DatabaseType("TIMESTAMP WITH TIME ZONE"));
        fieldTypes.put(java.time.OffsetTime.class, new FieldDefinition.DatabaseType("TIMESTAMP WITH TIME ZONE"));
        return fieldTypes;
    }

    /**
     * Build the hint string used for first rows.
     * <p>
     * Allows it to be overridden
     */
    @Override
    protected String buildFirstRowsHint(int max){
        //bug 374136: override setting the FIRST_ROWS hint as this is not needed on Oracle10g
        return "";
    }
    
    /**
     * INTERNAL:
     * Indicate whether app. server should unwrap connection
     * to use lob locator.
     * No need to unwrap connection because
     * writeLob method doesn't use oracle proprietary classes.
     */
    @Override
    public boolean isNativeConnectionRequiredForLobLocator() {
        return false;
    }
}
