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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
