/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
