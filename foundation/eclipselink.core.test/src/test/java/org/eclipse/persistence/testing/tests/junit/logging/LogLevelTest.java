/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      Tomas Kraus - Initial implementation
package org.eclipse.persistence.testing.tests.junit.logging;

import org.junit.Test;

/**
 * Unit tests for EclipseLink log levels enumeration.
 */
public class LogLevelTest {

    @Test
    public void testLength() {
        LogLevelHelper.testLength();
    }

    @Test
    public void testToValue() {
        LogLevelHelper.testToValueString();
        LogLevelHelper.testToValueInt();
        LogLevelHelper.testToValueIntFallBack();
    }

    @Test
    public void testShouldLog() {
        LogLevelHelper.testShouldLogOnLogLevel();
        LogLevelHelper.testShouldLogOnId();
    }

}
