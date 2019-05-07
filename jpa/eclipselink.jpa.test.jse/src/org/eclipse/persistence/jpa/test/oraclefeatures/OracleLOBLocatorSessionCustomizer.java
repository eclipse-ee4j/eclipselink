/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/06/2019 - Jody Grassel
 *       - 547023: Add LOB Locator support for core Oracle platform.
 ******************************************************************************/

package org.eclipse.persistence.jpa.test.oraclefeatures;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.platform.database.Oracle8Platform;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class OracleLOBLocatorSessionCustomizer implements SessionCustomizer {

    @Override
    public void customize(Session session) throws Exception {
        session.getEventManager().addListener(new OracleLobSessionEventAdapter());
    }
    
    private class OracleLobSessionEventAdapter extends SessionEventAdapter {
        /**
         * PUBLIC:
         * This Event is raised after the session logs in.
         */
        public void postLogin(SessionEvent event) {
            Session session = event.getSession();
            Platform dbPlatform = session.getDatasourcePlatform();
            if (dbPlatform instanceof Oracle8Platform) {
                ((Oracle8Platform) dbPlatform).setShouldUseLocatorForLOBWrite(true);
            }
        }
    }

}
