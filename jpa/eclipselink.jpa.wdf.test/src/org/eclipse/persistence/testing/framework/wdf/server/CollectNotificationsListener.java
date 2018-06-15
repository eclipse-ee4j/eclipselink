/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation
package org.eclipse.persistence.testing.framework.wdf.server;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Implementation of RunListener, which records serializable notifications so
 * that they can be replayed on the can be replayed on the client.
 */
final class CollectNotificationsListener extends RunListener {
    private final List<Notification> notifications = new ArrayList<Notification>();

    CollectNotificationsListener() {
    }

    /**
     * Get the list of notifications recorded.
     * @return the list of notifications recorded
     */
    public List<Notification> getNotifications() {
        return notifications;
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        notifications.add(new TestAssumptionFailed(failure));
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        notifications.add(new TestFailed(failure));
    }

    @Override
    public void testFinished(Description description) throws Exception {
        notifications.add(new TestFinished(description));
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        notifications.add(new TestIgnored(description));
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        notifications.add(new TestRunFinished(result));
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        notifications.add(new TestRunStarted(description));
    }

    @Override
    public void testStarted(Description description) throws Exception {
        notifications.add(new TestStarted(description));
    }
}
