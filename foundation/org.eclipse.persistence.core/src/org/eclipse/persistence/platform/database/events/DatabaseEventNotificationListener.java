/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.events;

import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * Defines the API for integration with a database event notification service.
 * This allows the EclipseLink cache to be invalidated by database change events.
 * This is used to support Oracle DCN (Database Change event Notification),
 * but could also be used by triggers or other services.
 * 
 * @see org.eclipse.persistence.descriptors.invalidation.DatabaseEventNotificationPolicy
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public interface DatabaseEventNotificationListener {

    void register(Session session);

    void remove(Session session);
        
}
