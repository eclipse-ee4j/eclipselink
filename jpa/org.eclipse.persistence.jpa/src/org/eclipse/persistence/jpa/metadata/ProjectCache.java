/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/01/2012-2.5 Chris Delahunt - Bug 371950 - Metadata caching
 ******************************************************************************/
package org.eclipse.persistence.jpa.metadata;

import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;

/**
 * <b>Purpose</b>: Interface used to support caching the project generated from metadata 
 * avoiding costs associated processing the same metadata over multiple deployments.
 * 
 * @see PersistenceUnitProperties#PROJECT_CACHE
 * @author cdelahunt
 * @since EclipseLink 2.4.1
 */
public interface ProjectCache {

    /**
     * PUBLIC: This method is responsible for returning the cached metadata as represented
     * by a Project instance.  This instance will have limited processing performed to turn
     * string instances into classes during deployment. 
     * 
     * The classloader provided is the application loader.  Please note that using it to load 
     * application classes (Entities) may prevent them from being dynamically woven.
     * 
     * @since EclipseLink 2.4.1
     */
    public Project retrieveProject(Map properties, ClassLoader loader, SessionLog log);

    /**
     * PUBLIC: This method is responsible for caching a project instance representing the 
     * application metadata.   
     * 
     * @since EclipseLink 2.4.1
     */
    public void storeProject(Project project, Map properties, SessionLog log);


}
