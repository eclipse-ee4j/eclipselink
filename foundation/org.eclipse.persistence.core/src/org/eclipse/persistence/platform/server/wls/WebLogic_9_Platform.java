/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.server.wls;

import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic9 specific behavior.
 *
 */
public class WebLogic_9_Platform extends WebLogicPlatform {
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebLogic_9_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }
}
