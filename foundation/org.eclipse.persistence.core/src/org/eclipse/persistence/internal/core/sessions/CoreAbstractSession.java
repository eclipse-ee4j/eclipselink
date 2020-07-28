/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.sessions;

import java.util.Map;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.sessions.CoreLogin;
import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.core.sessions.CoreSessionEventManager;
import org.eclipse.persistence.internal.core.databaseaccess.CorePlatform;

public abstract class CoreAbstractSession<
    DESCRIPTOR extends CoreDescriptor,
    LOGIN extends CoreLogin,
    PLATFORM extends CorePlatform,
    PROJECT extends CoreProject,
    SESSION_EVENT_MANAGER extends CoreSessionEventManager> implements CoreSession<DESCRIPTOR, LOGIN, PLATFORM, PROJECT, SESSION_EVENT_MANAGER> {

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    public abstract PLATFORM getDatasourcePlatform();

    /**
     * ADVANCED:
     * Return all registered descriptors.
     */
    public abstract Map<Class, DESCRIPTOR> getDescriptors();

    /**
     * INTERNAL:
     * Return the database platform currently connected to
     * for specified class.
     * The platform is used for database specific behavior.
     */
    public abstract PLATFORM getPlatform(Class domainClass);

}
