/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//      Oracle - initial impl
package org.eclipse.persistence.tools.tuning;

import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.sessions.Session;

/**
 * Configures the persistence unit for debugging.
 * This disables caching and several performance optimizations.
 * The purpose is to provide a simplified debugging and development configuration.
 */
public class SafeModeTuner implements SessionTuner {
    public SafeModeTuner() {
    }

    /**
     * Allow any JPA persistence unit properties to be configured, prior to deployment.
     */
    public void tunePreDeploy(Map properties) {
        properties.put(PersistenceUnitProperties.WEAVING_INTERNAL, "false");
        properties.put(PersistenceUnitProperties.WEAVING_CHANGE_TRACKING, "false");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        properties.put(PersistenceUnitProperties.JDBC_BIND_PARAMETERS, "false");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.TEMPORAL_MUTABLE, "true");
    }

    /**
     * Allow any Session configuration to be tune after meta-data has been processed, but before connecting the session.
     */
    public void tuneDeploy(Session session) {

    }

    /**
     * Allow any Session configuration to be tune after deploying and connecting the session.
     */
    public void tunePostDeploy(Session session) {

    }
}
