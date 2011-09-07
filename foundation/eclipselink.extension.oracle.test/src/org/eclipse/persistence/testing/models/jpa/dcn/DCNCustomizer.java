/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.dcn;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;

public class DCNCustomizer implements SessionCustomizer {
    public void customize(Session session) {
        ((Server)session).setDatabaseEventListener(new OracleChangeNotificationListener());        
    }    
}

