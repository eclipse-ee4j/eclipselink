/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - April 7/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * Allows actions to be performed upon receipt of various Session events.
 * 
 * @see SessionEventAdapter
 */
public class SessionEventListener extends SessionEventAdapter {
    private boolean shouldValidateInstantiationPolicy;
    
    /**
     * The default constructor.
     */
    public SessionEventListener() {
        shouldValidateInstantiationPolicy = false;
    }
    
    /**
     * PUBLIC:
     * This Event is raised before the session logs in.
     */
    public void preLogin(SessionEvent event) {
        if (!shouldValidateInstantiationPolicy) {
            event.getSession().getIntegrityChecker().dontCheckInstantiationPolicy();
        }
    }
    
    /**
     * PUBLIC:
     * Indicates if each descriptor's instantiation policy should be validated
     * during initialization.
     * 
     * @param value
     * @see InstantiationPolicy
     * @see XMLDescriptor
     */
    public void setShouldValidateInstantiationPolicy(boolean value) {
        shouldValidateInstantiationPolicy = value;
    }
}
