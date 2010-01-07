/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - added for EclipseLink 2.0 release
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.was;

import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere 
 * 7 -specific server behavior.
 *
 * This platform has:
 * - No JMX MBean runtime services
 *
 */
public class WebSphere_7_Platform extends WebSphere_6_1_Platform {
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebSphere_7_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }
}