/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.core.sessions;

import java.io.Serializable;
import java.util.List;
import org.eclipse.persistence.core.descriptors.CoreDescriptor;

/**
 * INTERNAL
 * A abstraction of project capturing behavior common to all persistence types.
 */
public abstract class CoreProject <
   DESCRIPTOR extends CoreDescriptor,
   LOGIN extends CoreLogin,
   SESSION extends CoreSession
   > implements Serializable {

    public abstract void addDescriptor(DESCRIPTOR descriptor);

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this project to actual class-based settings.
     * This will also reset any class references to the version of the class from the class loader.
     */
    public abstract void convertClassNamesToClasses(ClassLoader classLoader);

    /**
     * INTERNAL:
     * Factory method to create session.
     * This returns an implementor of the CoreSession interface, which can be used to login
     * and add descriptors from other projects.  The CoreSession interface however should be used for
     * reading and writing once connected for complete portability.
     */
    public abstract SESSION createDatabaseSession();

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * This return the Login interface and may need to be cast to the datasource specific implementation.
     */
    public abstract LOGIN getDatasourceLogin();

    /**
     * PUBLIC:
     * Return the descriptor specified for the class.
     */
    public abstract DESCRIPTOR getDescriptor(Class theClass);

    /**
     * INTERNAL:
     * Return the descriptors in the order added.
     * Used to maintain consistent order in XML.
     */
    public abstract List<DESCRIPTOR> getOrderedDescriptors();


    /**
     * PUBLIC:
     * Set the login to be used to connect to the database for this project.
     */
    public abstract void setLogin(LOGIN datasourceLogin);

}
