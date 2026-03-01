/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestExecutor;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSystem;

import java.io.Writer;
import java.util.Hashtable;
import java.util.Vector;

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

    @Override
    public void addConfigureSystem(TestSystem system) {
        error();
    }

    @Override
    public void addLoadedModels(Vector models) {
        error();
    }

    @Override
    public void configureSystem(TestSystem system) throws Exception {
        error();
    }

    @Override
    public boolean configuredSystemsContainsInstanceOf(TestSystem system) {
        error();
        return false;
    }

    @Override
    public void doNotHandleErrors() {
        error();
    }

    @Override
    public void doNotLogResults() {
        error();
    }

    @Override
    public void doNotStopExecution() {
        error();
    }

    @Override
    public void forceConfigureSystem(TestSystem system) throws Exception {
        error();
    }

    @Override
    public Vector getConfiguredSystems() {
        error();
        return null;
    }

    @Override
    public TestModel getLoadedModel(String modelsName) {
        error();
        return null;
    }

    @Override
    public Hashtable getLoadedModels() {
        error();
        return null;
    }

    /**
     * Return the parent's log.
     */
    @Override
    public Writer getLog() {
        return parentTestExecutor.getLog();
    }

    /**
     * Return the session.
     */
    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void handleErrors() {
        error();
    }

    @Override
    public void initializeConfiguredSystems() {
        error();
    }

    @Override
    protected void logout() {
        error();
    }

    @Override
    public void logResults() {
        error();
    }

    @Override
    public void removeConfigureSystem(TestSystem system) {
        error();
    }

    @Override
    public void removeFromConfiguredSystemsInstanceOf(TestSystem system) {
        error();
    }

    @Override
    public void resetLoadedModels() {
        error();
    }

    /**
     * PUBLIC:
     * This method executes the test entity. This method sets the session by using test
     * entity default login and once the execution is over it explicitily logs out.
     */
    @Override
    public void runTest(junit.framework.Test test) throws Throwable {
        boolean hasSession = true;

        //    setShouldStopExecution(false);
        if (getSession() == null) {
            hasSession = false;
            acquireClientSession();
        }

        try {
            execute(test);
            //        logResultForTestEntity(testEntity);
        } finally {
            if (!hasSession) {
                releaseClientSession();
            }
        }
    }

    @Override
    public void setConfiguredSystems(Vector configuredSystems) {
        error();
    }

    @Override
    protected void setLoadedModels(Hashtable loadedModels) {
        error();
    }

    @Override
    public void setLog(Writer writer) {
        error();
    }

    @Override
    public void setSession(Session theSession) {
        session = theSession;
    }

    @Override
    public void setShouldHandleErrors(boolean aBoolean) {
        error();
    }

    @Override
    public void setShouldLogResults(boolean aBoolean) {
        error();
    }

    @Override
    public void setShouldStopExecution(boolean aBoolean) {
        error();
    }

    @Override
    public boolean shouldHandleErrors() {
        return parentTestExecutor.shouldHandleErrors();
    }

    @Override
    public boolean shouldLogResults() {
        return parentTestExecutor.shouldLogResults();
    }

    @Override
    public boolean shouldStopExecution() {
        return parentTestExecutor.shouldStopExecution();
    }

    @Override
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

    @Override
    protected void finalize() {
        releaseClientSession();
    }
}
