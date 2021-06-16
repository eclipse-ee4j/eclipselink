/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial API and implementation
package org.eclipse.persistence.platform.database.events;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * Defines the API for integration with a database event notification service.
 * This allows the EclipseLink cache to be invalidated by database change events.
 * This is used to support Oracle DCN (Database Change event Notification),
 * but could also be used by triggers or other services.
 *
 * @see org.eclipse.persistence.annotations.DatabaseChangeNotificationType
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public interface DatabaseEventListener {

    /**
     * Register for database change events and invalidate the session's cache.
     * This is called on session login.
     */
    void register(Session session);

    /**
     * Remove registration from database change events.
     * This is called on session logout.
     */
    void remove(Session session);

    /**
     * Initialize the descriptor to receive database change events.
     * This is called when the descriptor is initialized.
     */
    void initialize(ClassDescriptor descriptor, AbstractSession session);
}
