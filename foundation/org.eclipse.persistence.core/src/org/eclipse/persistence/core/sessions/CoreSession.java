/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.sessions;

import java.util.Map;
import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.internal.core.databaseaccess.CorePlatform;

/**
 * INTERNAL
 * A abstraction of session capturing behavior common to all persistence types.
 */
public interface CoreSession<
    DESCRIPTOR extends CoreDescriptor,
    LOGIN extends CoreLogin,
    PLATFORM extends CorePlatform,
    PROJECT extends CoreProject,
    SESSION_EVENT_MANAGER extends CoreSessionEventManager> {

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * This return the Login interface and may need to be cast to the datasource specific implementation.
     */
    LOGIN getDatasourceLogin();

    /**
     * PUBLIC:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    public PLATFORM getDatasourcePlatform();

    /**
     * ADVANCED:
     * Return the descriptor specified for the class.
     * If the class does not have a descriptor but implements an interface that is also implemented
     * by one of the classes stored in the map, that descriptor will be stored under the
     * new class.
     */
    DESCRIPTOR getDescriptor(Class theClass);

    /**
     * ADVANCED:
     * Return the descriptor specified for the object's class.
     */
    DESCRIPTOR getDescriptor(Object domainObject);

    /**
     * ADVANCED:
     * Return all registered descriptors.
     */
    public Map<Class, DESCRIPTOR> getDescriptors();

    /**
     * PUBLIC:
     * Return the event manager.
     * The event manager can be used to register for various session events.
     */
    public SESSION_EVENT_MANAGER getEventManager();

    /**
     * PUBLIC:
     * Return the project.
     * The project includes the login and descriptor and other configuration information.
     */
    public PROJECT getProject();

    /**
     * PUBLIC:
     * Set the log level. 
     */
    public void setLogLevel(int logLevel);

}
