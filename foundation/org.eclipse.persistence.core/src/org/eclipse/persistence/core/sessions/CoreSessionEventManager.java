/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.core.sessions;

/**
 * INTERNAL
 * A abstraction of sessuin event manager capturing behavior common to all
 * persistence types.
 */
public abstract class CoreSessionEventManager<
    SESSION_EVENT_LISTENER extends CoreSessionEventListener> {

    /**
     * PUBLIC:
     * Add the event listener to the session.
     * The listener will receive all events raised by this session.
     * Also unit of works acquire from this session will inherit the listeners.
     * If session is a broker then its members add the listener, too.
     */
    public abstract void addListener(SESSION_EVENT_LISTENER listener);

}
