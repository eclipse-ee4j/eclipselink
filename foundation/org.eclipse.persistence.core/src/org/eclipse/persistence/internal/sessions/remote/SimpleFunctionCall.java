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
package org.eclipse.persistence.internal.sessions.remote;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: RemoteFunctionCall's interface implementors that
 * don't use remoteSessionController parameter in execute method
 * may extend this class
 * <p>
 * <b>Description</b>: Convenience abstract class
 * <p>
 */
public abstract class SimpleFunctionCall implements RemoteFunctionCall {

    /**
    * INTERNAL:
    * This method is used by remote session controller to execute the function call
    */
    @Override
    public Object execute(AbstractSession session, RemoteSessionController remoteSessionController) {
        return execute(session);
    }

    /**
    * PROTECTED:
    * Convenience method
    */
    protected abstract Object execute(AbstractSession session);
}
