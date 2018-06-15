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

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * A notification indicating that a test assumption failed.
 */
public class TestAssumptionFailed implements Notification {

    private static final long serialVersionUID = 1L;
    private final SerializableFailure failure;

    public TestAssumptionFailed(Failure aFailure) {
        failure = SerializableFailure.create(aFailure);
    }

    @Override
    public void notify(RunNotifier notifier) {
        notifier.fireTestAssumptionFailed(failure.restore());
    }

}
