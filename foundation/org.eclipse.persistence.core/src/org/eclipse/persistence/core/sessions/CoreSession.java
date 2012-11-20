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
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.sessions;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;

public interface CoreSession<
    DESCRIPTOR extends CoreDescriptor,
    LOGIN extends CoreLogin> {

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * This return the Login interface and may need to be cast to the datasource specific implementation.
     */
    LOGIN getDatasourceLogin();

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

}
