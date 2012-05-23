/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
