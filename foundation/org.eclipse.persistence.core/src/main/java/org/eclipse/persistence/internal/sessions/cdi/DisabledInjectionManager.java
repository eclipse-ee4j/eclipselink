/*
 * Copyright (c) 2012, 2023 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.AbstractSession;

import javax.naming.NamingException;

public class DisabledInjectionManager<T> implements InjectionManager<T> {

    @Override
    public T createManagedBeanAndInjectDependencies(Class<T> managedBeanClass) throws NamingException {
        return null;
    }

    @Override
    public void cleanUp(AbstractSession session) {
    }

}
