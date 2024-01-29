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
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.platform.database.oracle.json;

import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.persistence.internal.databaseaccess.DatabaseJsonPlatform;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.spi.JsonPlatformProvider;
import org.eclipse.persistence.platform.database.oracle.Oracle21Platform;

/**
 * Oracle database platform JSON extension service provider.
 */
public class OracleJsonPlatformProvider implements JsonPlatformProvider {

    // Oracle platforms to JSON extension mapping.
    private static final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> PLATFORMS = initPlatforms();

    // Initialize Oracle platforms JSON extension mapping.
    private static Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> initPlatforms() {
        return Map.of(Oracle21Platform.class, Oracle21JsonPlatform::new);
    }

    /**
     * Get Oracle platforms to JSON extension mapping.
     *
     * @return Oracle platforms to JSON extension mapping
     */
    @Override
    public Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> platforms() {
        return PLATFORMS;
    }

}
