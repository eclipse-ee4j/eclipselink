/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM Corporation. All rights reserved.
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
//     11/28/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import java.util.Locale;
import java.util.Map;

import jakarta.persistence.SchemaManager;
import jakarta.persistence.SchemaValidationException;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.tools.schemaframework.TableValidationException;

import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.getConfigPropertyAsString;

// Maps EclipseLink core API to jakarta.persistence.SchemaManager
/**
 * Schema management operations for the persistence unit.
 */
class SchemaManagerImpl implements SchemaManager {

    // EclipseLink schema manager
    private final org.eclipse.persistence.tools.schemaframework.SchemaManager schemaManager;
    // Persistence unit properties
    final Map<String, ?> props;

    SchemaManagerImpl(DatabaseSessionImpl session, Map<String, ?> props) {
        this.schemaManager = new org.eclipse.persistence.tools.schemaframework.SchemaManager(session);
        this.props = props;
    }

    @Override
    public void create(boolean createSchemas) {
        if (createSchemas) {
            schemaManager.createDefaultTables(true);
        }
    }

    @Override
    public void drop(boolean dropSchemas) {
        if (dropSchemas) {
            schemaManager.dropDefaultTables();
        }
    }

    @Override
    public void validate() throws SchemaValidationException {
        ValidationFailure failures = new ValidationFailure();
        String mode = getConfigPropertyAsString(PersistenceUnitProperties.SCHEMA_VALIDATION_MODE,
                                                props,
                                                PersistenceUnitProperties.SCHEMA_VALIDATION_MODE_SIMPLE)
                .toLowerCase(Locale.ROOT);
        boolean full = PersistenceUnitProperties.SCHEMA_VALIDATION_MODE_FULL.equals(mode);
        if (!schemaManager.validateDefaultTables(failures, true, full)) {
            throw new SchemaValidationException(
                    ExceptionLocalization.buildMessage("schema_validation_failed"),
                    failures.result().toArray(new TableValidationException[0]));
        }
    }

    @Override
    public void truncate() {
        schemaManager.truncateDefaultTables(false);
    }

}
