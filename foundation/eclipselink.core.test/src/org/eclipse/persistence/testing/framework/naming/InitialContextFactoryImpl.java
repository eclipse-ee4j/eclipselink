/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * A simple InitialContextFactory implementation.
 * Copied from essentials org.eclipse.persistence.essentials.internal.ejb.cmp3.naming package.
 */
public class InitialContextFactoryImpl implements InitialContextFactory {
    public InitialContextFactoryImpl() {
    }

    public Context getInitialContext(Hashtable environment) throws NamingException {
        return new InitialContextImpl(environment);
    }
}
