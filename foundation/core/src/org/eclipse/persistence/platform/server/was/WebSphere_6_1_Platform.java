/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.was;

import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere 
 * 6.1-specific server behaviour.
 *
 * This platform has:
 * - No JMX MBean runtime services
 *
 */
public class WebSphere_6_1_Platform extends WebSpherePlatform {
    /**
     * INTERNAL:
     * Default Constructor: All behaviour for the default constructor is inherited
     */
    public WebSphere_6_1_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }
}
