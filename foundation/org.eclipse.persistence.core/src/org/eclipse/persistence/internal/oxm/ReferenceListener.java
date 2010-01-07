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
* mmacivor - January 09, 2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class ReferenceListener extends SessionEventAdapter {
    public static final String KEY = "REFERENCE_RESOLVER";
    /**
     * PUBLIC:
     * This event is raised on the unit of work after creation/acquiring.
     * This will be raised on nest units of work.
     */
    public void postAcquireUnitOfWork(SessionEvent event) {
    	event.getSession().setProperty(KEY, new ReferenceResolver());
    }
    
    public boolean equals(Object obj) {
    	return obj instanceof ReferenceListener;
    }
}
