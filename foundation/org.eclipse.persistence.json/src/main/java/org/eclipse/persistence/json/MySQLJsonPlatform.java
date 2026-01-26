/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     13/01/2022-4.0.0 Tomas Kraus
//       - 1391: JSON support in JPA
package org.eclipse.persistence.json;

import java.util.Map;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

public class MySQLJsonPlatform extends JsonPlatform {

/**
 * Update the mapping of JSON class types to MySQL database types for the schema framework.
 *
 * @param fieldTypeMapping {@code Map} with mappings to be updated.
 */
    @Override
    public void updateFieldTypes(final Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping) {
        fieldTypeMapping.put(jakarta.json.JsonObject.class, new FieldDefinition.DatabaseType("JSON"));
        fieldTypeMapping.put(jakarta.json.JsonArray.class, new FieldDefinition.DatabaseType("JSON"));
        fieldTypeMapping.put(jakarta.json.JsonValue.class, new FieldDefinition.DatabaseType("JSON"));
    }

    /**
     * MySQL specific custom JSON parameter marker.
     *
     * @return JSON parameter marker
     */
    @Override
    public String customParameterMarker() {
        return "CAST(? AS JSON)";
    }

}
