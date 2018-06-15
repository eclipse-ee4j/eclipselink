/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Tomas Kraus - Initial implementation
package org.eclipse.persistence.testing.tests.junit.logging;

import org.junit.Test;

/**
 * Unit tests for EclipseLink logging categories enumeration.
 */
public class LogCategoryTest {

    @Test
    public void testLength() {
        LogCategoryHelper.testLength();
    }

    @Test
    public void testToValue() {
        LogCategoryHelper.testToValue();
    }

    @Test
    public void testGetNameSpace() throws ReflectiveOperationException {
        LogCategoryHelper.testGetNameSpace();
    }

}
