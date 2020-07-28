/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     05/05/2011-2.3 Chris Delahunt - Bug 344837: Extensibility - Metadata Repository
//     02/27/2012-2.4 dclarke - Bug 373120:  Add persistence unit property overrides to Metadata Repository
package org.eclipse.persistence.jpa.metadata;

import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <b>Purpose</b>: Interface used to support additional persistence unit
 * metadata being provided from outside of what was packaged within the
 * application. A MetadataSource will need to be registered with a persistence
 * unit.
 *
 * @see PersistenceUnitProperties#METADATA_SOURCE
 * @author cdelahunt, dclarke
 * @since EclipseLink 2.3.0
 */
public interface MetadataSource {

    /**
     * PUBLIC: This method is responsible for returning additional persistence
     * unit property overrides. It is called on initial deployment of the
     * persistence unit and when the persistence unit is reloaded to allow
     * customization of the persistence unit above and beyond what is packaged
     * in the persistence.xml and what is code into the application.
     * <p>
     * <b>IMPORTANT</b>: Although any property can be changed using this
     * approach it is important that users of this feature ensure compatible
     * configurations are supplied. As an example; overriding an application to
     * use RESOURCE_LOCAL when it was coded to use JTA would result in changes
     * not be written to the database.
     *
     * @since EclipseLink 2.4
     */
    public Map<String, Object> getPropertyOverrides(Map<String, Object> properties, ClassLoader classLoader, SessionLog log);

    /**
     * PUBLIC: This method is responsible for returning the object
     * representation of the object-relational mapping overrides. It is called
     * on initial deployment of the persistence unit and when the persistence
     * unit is reloaded to allow customization of the persistence unit above and
     * beyond what is packaged in the persistence.xml and what is code into the
     * application.
     *
     * <b>IMPORTANT</b>: This method is called during the predeploy stage
     * that is used both for weaving and runtime EMF deployment.  Runtime
     * properties might not be available during weaving, and mapping options
     * that require weaving changes should not be used.  For example adding a
     * lazy OneToOne mapping after weaving has already occurred will result in
     * an exception if they are expected to have been woven into the class.
     */
    public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log);
}
