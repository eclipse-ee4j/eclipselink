/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     @author  mobrien
 *     @since   EclipseLink 1.1 enh# 248748
 *     10/20/2008-1.1M4 Michael O'Brien 
 *       - 248748: Add WebLogic 10.3 specific JMX MBean attributes and functions
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/248748</link>
 *     06/11/2008-1.1M5 Michael O'Brien 
 *       - 248746: Add getModuleName() implementation and new getApplicationName()
 *     01/27/2009-1.1.1 Michael O'Brien 
 *       - 262583: removal of category arg get*EclipseLinkLogLevel functions - rev 3308
 *     03/31/2009-1.1.1 Michael O'Brien 
 *       - 270533: CCE on DefaultSessionLog cast narrowed for getLogFilename()
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.services.weblogic;

import java.util.Locale;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.services.RuntimeServices;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide facilities for managing an EclipseLink session external
 * to EclipseLink over JMX.
 */
public class WebLogicRuntimeServices extends RuntimeServices {

    static {
        PLATFORM_NAME = "WebLogic";
    }
    
    /**
     * PUBLIC:
     *  Default Constructor
     */
    public WebLogicRuntimeServices() {
        super();
    }

    /**
     *  PUBLIC:
     *  Create an instance of WebLogicRuntimeServices to be associated with the provided session
     *
     *  @param session The session to be used with these RuntimeServices
     */
    public WebLogicRuntimeServices(AbstractSession session) {
        super();
        this.session = session;
        this.updateDeploymentTimeData();
    }

    /**
     *  Create an instance of WebLogicRuntimeServices to be associated with the provided locale
     *
     *  The user must call setSession(Session) afterwards to define the session.
     */
    public WebLogicRuntimeServices(Locale locale) {
    }
}
