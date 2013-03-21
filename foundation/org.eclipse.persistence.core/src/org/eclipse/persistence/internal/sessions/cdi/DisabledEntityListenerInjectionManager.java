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
 *     tware - initial implementation
 */
package org.eclipse.persistence.internal.sessions.cdi;

import javax.naming.NamingException;

import org.eclipse.persistence.internal.sessions.AbstractSession;

public class DisabledEntityListenerInjectionManager implements
        EntityListenerInjectionManager {

    @Override
    public Object createEntityListenerAndInjectDependancies(
            Class entityListenerClass) throws NamingException {
        return null;
    }

    @Override
    public void cleanUp(AbstractSession session) {
    }

}
