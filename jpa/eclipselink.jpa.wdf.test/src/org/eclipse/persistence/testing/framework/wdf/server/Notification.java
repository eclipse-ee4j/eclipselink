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

import java.io.Serializable;

import org.junit.runner.notification.RunNotifier;

/**
 * A recordable notification.
 */
public interface Notification extends Serializable {

    /**
     * Notify a run notifier of this notification.
     * @param notifier
     */
    public void notify(RunNotifier notifier);

}
