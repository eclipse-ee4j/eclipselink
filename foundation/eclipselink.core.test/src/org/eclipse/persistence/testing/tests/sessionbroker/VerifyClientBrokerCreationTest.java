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
 *     tware - Bug 241681 fixes for clientSessionBroker
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionbroker;

import java.util.Iterator;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.CustomServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * Bug 241681
 * Ensure ClientBroker contains proper variables
 * @author tware
 *
 */
public class VerifyClientBrokerCreationTest extends AutoVerifyTestCase {

    SessionBroker serverBroker = null;
    SessionBroker clientBroker = null;
    
    public void test(){
        serverBroker = new SessionBroker();

        ServerSession ssession1 = new ServerSession(ServerBrokerTestModel.getLogin1());
        ServerSession ssession2 = new ServerSession(ServerBrokerTestModel.getLogin2());

        ssession1.addDescriptors(new EmployeeProject1());
        ssession2.addDescriptors(new EmployeeProject2());

        serverBroker.registerSession("broker1", ssession1);
        serverBroker.registerSession("broker2", ssession2);

        serverBroker.setLog(getSession().getLog());
        serverBroker.setLogLevel(SessionLog.FINE);
        
        serverBroker.setShouldPropagateChanges(true);
        ServerPlatform platform = new CustomServerPlatform(serverBroker);
        platform.setExternalTransactionControllerClass(JTATransactionController.class);
        serverBroker.setServerPlatform(platform);

        serverBroker.login();

        clientBroker = serverBroker.acquireClientSessionBroker();

    }
    
    public void verify(){
        if (clientBroker == serverBroker){
            throw new TestErrorException("ClientBroker is == to serverBroker");
        }
        if (clientBroker.getAccessor() != serverBroker.getAccessor()){
            throw new TestErrorException("Accessor not properly copied on client broker creation");
        }
        if (clientBroker.getName() != serverBroker.getName()){
            throw new TestErrorException("Name not properly copied on client broker creation");
        }
        if (clientBroker.getSessionLog() != serverBroker.getSessionLog()){
            throw new TestErrorException("SessionLog not properly copied on client broker creation");
        }
        if (clientBroker.getProject() != serverBroker.getProject()){
            throw new TestErrorException("ShouldPropogateChanges not properly copied on client broker creation");
        }
        if (clientBroker.shouldPropagateChanges() != serverBroker.shouldPropagateChanges()){
            throw new TestErrorException("ShouldPropogateChanges not properly copied on client broker creation");
        }
        if (clientBroker.getParent() != serverBroker){
            throw new TestErrorException("Parent not properly set on client broker creation");
        }
        if (clientBroker.getCommandManager() != serverBroker.getCommandManager()){
            throw new TestErrorException("CommandManager not properly copied on client broker creation");
        }
        if (clientBroker.getCommitManager() != serverBroker.getCommitManager()){
            throw new TestErrorException("CommitManager not properly copied on client broker creation");
        }
        if (clientBroker.getExternalTransactionController() != serverBroker.getExternalTransactionController()){
            throw new TestErrorException("ShouldPropogateChanges not properly copied on client broker creation");
        }
        if (clientBroker.getServerPlatform() != serverBroker.getServerPlatform()){
            throw new TestErrorException("ShouldPropogateChanges not properly copied on client broker creation");
        }
        if (clientBroker.getSessionsByName().size() != serverBroker.getSessionsByName().size()){
            throw new TestErrorException("Incorrect number of sessions in client broker");
           
        }
        Iterator i = serverBroker.getSessionsByName().keySet().iterator();
        while (i.hasNext()){
            String key = (String)i.next();
            if (clientBroker.getSessionForName(key) == null) {
                throw new TestErrorException("Session " + key + " exists in server broker but not client broker.");
            }
        }
    }
    
}
