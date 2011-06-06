/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Executor used with simutaneous tests.
 * Allows acquiring client session.
 */
public class TestExecutorWithClientSession extends TestExecutor {
    protected TestExecutor parentTestExecutor;

    public TestExecutorWithClientSession(TestExecutor parentTestExecutor) {
        this.parentTestExecutor = parentTestExecutor;
    }

    protected void setsession(Session session) {
        super.setSession(session);
    }

    public void acquireClientSession() {
        releaseClientSession();
        setSession(getServerSession().acquireClientSession());
    }

    public void releaseClientSession() {
        if (getSession() != null) {
            getSession().release();
            setSession(null);
        }
    }

    public void addConfigureSystem(TestSystem system) {
        error();
    }

    public void addLoadedModels(Vector models) {
        error();
    }

    public void configureSystem(TestSystem system) throws Exception {
        error();
    }

    public boolean configuredSystemsContainsInstanceOf(TestSystem system) {
        error();
        return false;
    }

    public void doNotHandleErrors() {
        error();
    }

    public void doNotLogResults() {
        error();
    }

    public void doNotStopExecution() {
        error();
    }

    public void forceConfigureSystem(TestSystem system) throws Exception {
        error();
    }

    public Vector getConfiguredSystems() {
        error();
        return null;
    }

    public TestModel getLoadedModel(String modelsName) {
        error();
        return null;
    }

    public Hashtable getLoadedModels() {
        error();
        return null;
    }

    /**
     * Return the parent's log.
     */
    public Writer getLog() {
        return parentTestExecutor.getLog();
    }

    /**
     * Return the session.
     */
    public Session getSession() {
        return session;
    }

    public void handleErrors() {
        error();
    }

    public void initializeConfiguredSystems() {
        error();
    }

    protected void logout() {
        error();
    }

    public void logResults() {
        error();
    }

    public void removeConfigureSystem(TestSystem system) {
        error();
    }

    public void removeFromConfiguredSystemsInstanceOf(TestSystem system) {
        error();
    }

    public void resetLoadedModels() {
        error();
    }

    /**
     * PUBLIC:
     * This method executes the test entity. This method sets the session by using test
     * entity default login and once the execution is over it explicitily logs out.
     */
    public void runTest(junit.framework.Test test) throws Throwable {
        boolean hasSession = true;

        //	setShouldStopExecution(false);
        if (getSession() == null) {
            hasSession = false;
            acquireClientSession();
        }

        try {
            execute(test);
            //		logResultForTestEntity(testEntity);
        } finally {
            if (!hasSession) {
                releaseClientSession();
            }
        }
    }

    public void setConfiguredSystems(Vector configuredSystems) {
        error();
    }

    protected void setLoadedModels(Hashtable loadedModels) {
        error();
    }

    public void setLog(Writer writer) {
        error();
    }

    public void setSession(Session theSession) {
        session = theSession;
    }

    public void setShouldHandleErrors(boolean aBoolean) {
        error();
    }

    public void setShouldLogResults(boolean aBoolean) {
        error();
    }

    public void setShouldStopExecution(boolean aBoolean) {
        error();
    }

    public boolean shouldHandleErrors() {
        return parentTestExecutor.shouldHandleErrors();
    }

    public boolean shouldLogResults() {
        return parentTestExecutor.shouldLogResults();
    }

    public boolean shouldStopExecution() {
        return parentTestExecutor.shouldStopExecution();
    }

    public void stopExecution() {
        error();
    }

    private static void error() {
        System.out.println("Error in TestExecutorWithClientSession");
    }

    protected ServerSession getServerSession() {
        Session session = parentTestExecutor.getSession();
        if ((session == null) || !session.isServerSession()) {
            error();
        }
        ServerSession serverSession = (ServerSession)session;
        return serverSession;
    }

    protected void finalize() {
        releaseClientSession();
    }
}
