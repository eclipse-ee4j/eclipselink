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

import org.eclipse.persistence.sessions.Session;

/**
 * Generic tuning interface.
 * Allow JPA and Session configuration to be tuned or auto-tuned for a specific purpose.
 * This facilitates a one-flag configuration option for configuring multiple settings, or performing dynamic tuning.
 */
public interface SessionTuner {
    /**
     * Allow any JPA persistence unit properties to be configured, prior to deployment.
     */
    void tunePreDeploy(Map properties);

    /**
     * Allow any Session configuration to be tune after meta-data has been processed, but before connecting the session.
     */
    void tuneDeploy(Session session);

    /**
     * Allow any Session configuration to be tune after deploying and connecting the session.
     */
    void tunePostDeploy(Session session);
}
