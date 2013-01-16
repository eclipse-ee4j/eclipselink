/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Oracle - initial impl
 ******************************************************************************/
package org.eclipse.persistence.tools.tuning;

import java.util.Map;

import org.eclipse.persistence.sessions.Session;

/**
 * Default tuner.  This uses the standard/default configuration.
 */
public class StandardTuner implements SessionTuner {
    public StandardTuner() {        
    }
    
    /**
     * Allow any JPA persistence unit properties to be configured, prior to deployment.
     */
    public void tunePreDeploy(Map properties) {
        
    }

    /**
     * Allow any Session configuration to be tune after meta-data has been processed, but before connecting the session.
     */
    public void tuneDeploy(Session session) {
        
    }

    /**
     * Allow any Session configuration to be tune after deploying and connecting the session.
     */
    public void tunePostDeploy(Session session) {
        
    }
}
