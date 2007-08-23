/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jms;

import org.eclipse.persistence.internal.sessions.remote.RemoteCommand;
import org.eclipse.persistence.internal.sessions.remote.RemoteSessionController;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**  
 * Dummy implementation for testing purposes.
 */
public class RemoteCommandImpl implements RemoteCommand {
    public RemoteCommandImpl() {
    }

    public void execute(AbstractSession session, RemoteSessionController remoteSessionController) {
    }
}
