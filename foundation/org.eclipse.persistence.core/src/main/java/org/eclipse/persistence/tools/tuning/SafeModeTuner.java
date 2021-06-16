/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    @Override
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
    @Override
    public void tuneDeploy(Session session) {

    }

    /**
     * Allow any Session configuration to be tune after deploying and connecting the session.
     */
    @Override
    public void tunePostDeploy(Session session) {

    }
}
