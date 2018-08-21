/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;

/**
 * Tests feature in bug 4104613 "setForceBatchStatementExecution" api with paramaterized batch writing.
 */
public class ParameterBatchWritingFlushQueryTest extends BatchWritingFlushQueryTest {
  protected Boolean usesBindAllParameters;

  public ParameterBatchWritingFlushQueryTest() {
    setDescription("Test for the forceBatchStatementExecution ModifyQuery option on Parameter batch writing mechanism.");
    EXPECTED_INITIAL_STATEMENTS = 0;
    EXPECTED_INITIAL_QUERIES = 1;
    EXPECTED_SECOND_STATEMENTS = 4;
    EXPECTED_SECOND_QUERIES = 2;
  }

  public void setup() {
    super.setup();
    DatabasePlatform platform = getSession().getPlatform();
    usesBindAllParameters = Boolean.valueOf(platform.shouldBindAllParameters());
    platform.setShouldBindAllParameters(true);
    platform.setUsesJDBCBatchWriting(true);
  }

  public void reset() {
    if (usesBindAllParameters != null) {
        DatabasePlatform platform = getSession().getPlatform();
        platform.setShouldBindAllParameters(usesBindAllParameters.booleanValue());
    }
    super.reset();
  }
}
