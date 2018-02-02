/*******************************************************************************
 * Copyright (c) 2015, 2018  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Tomas Kraus - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.junit.logging;

import org.eclipse.persistence.logging.LogLevelHelper;
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
