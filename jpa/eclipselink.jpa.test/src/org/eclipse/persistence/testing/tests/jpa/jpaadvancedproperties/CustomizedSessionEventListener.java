/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class CustomizedSessionEventListener extends SessionEventAdapter {
    public boolean postBeginTransaction=false;
    public boolean preLogin=false;
    public boolean postLogin=false;
    public boolean postCommitTransaction=false;
    public boolean preCommitTransaction=false;
    public boolean preBeginTransaction=false;
    

    /**
     * TestSessionListener constructor comment.
     */
    public CustomizedSessionEventListener() {
        super();
    }


    public void postCommitTransaction(SessionEvent event) {
        postCommitTransaction=true;
    }
    
    public void preCommitTransaction(SessionEvent event) {
        preCommitTransaction=true;
    }

    public void preLogin(SessionEvent event) {
        preLogin=true;
    }

    public void postLogin(SessionEvent event) {
        postLogin=true;
    }
    
    public void preBeginTransaction(SessionEvent event) {
        preBeginTransaction=true;
    }

    public void postBeginTransaction(SessionEvent event){
        postBeginTransaction=true;
    }

}
