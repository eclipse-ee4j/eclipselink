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
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.was;

import org.eclipse.persistence.platform.server.JMXEnabledPlatform;
import org.eclipse.persistence.services.websphere.MBeanWebSphereRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere 7 -specific server behavior.
 *
 * This platform has:
 * - No JMX MBean runtime services
 *
 */
public class WebSphere_7_Platform extends WebSphere_6_1_Platform implements JMXEnabledPlatform {
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebSphere_7_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.enableRuntimeServices();
        // Create the JMX MBean specific to this platform for later registration
        this.prepareServerSpecificServicesMBean();
    }
    
    @Override
    public boolean isRuntimeServicesEnabledDefault() {
        return true;
    }
    
    /**
     * INTERNAL: 
     * prepareServerSpecificServicesMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for the
     * databaseSession.
     *
     * Default is to do nothing.
     * Implementing platform classes must override this function and supply
     * the server specific MBean instance for later registration by calling it in the constructor.  
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    @Override
    public void prepareServerSpecificServicesMBean() {
        // No check for an existing cached MBean - we will replace it if it exists
        if(shouldRegisterRuntimeBean) {
            this.setRuntimeServicesMBean(new MBeanWebSphereRuntimeServices(getDatabaseSession()));
        }
    }
}