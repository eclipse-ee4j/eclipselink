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
//     tware - initial implementation
package org.eclipse.persistence.internal.sessions.cdi;

import javax.naming.NamingException;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Interface to control CDI-based injection in EntityListeners
 * Any references to CDI specific classes should be reserved for implementers to allow this
 * interface to load in an environment that does not include CDI.
 */
public interface InjectionManager<T> {

    String DEFAULT_CDI_INJECTION_MANAGER = "org.eclipse.persistence.internal.sessions.cdi.InjectionManagerImpl";

    T createManagedBeanAndInjectDependencies(Class<T> managedBeanClass) throws NamingException;

    void cleanUp(AbstractSession session);
}
