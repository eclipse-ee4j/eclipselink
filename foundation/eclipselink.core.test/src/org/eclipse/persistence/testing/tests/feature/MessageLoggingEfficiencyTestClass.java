/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;

/**
 * This class is only used within the MessageLoggingEfficiencyTest so that a TestErrorException is thrown
 *  whenever toString is called on an instance of this class.  This is for CR#2272.
 */
public class MessageLoggingEfficiencyTestClass {
    public MessageLoggingEfficiencyTestClass() {
    }

    public String toString() {
        throw new TestErrorException("toString method being called even when debug logging is disabled");
    }
}
