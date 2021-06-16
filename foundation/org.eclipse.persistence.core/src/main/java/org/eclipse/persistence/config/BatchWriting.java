/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.config;

import org.eclipse.persistence.internal.databaseaccess.BatchWritingMechanism;

/**
 * Specify the use of batch writing to optimize transactions with multiple writes,
 * by default batch writing is not used.
 * Batch writing allows multiple heterogeneous dynamic SQL statements to be sent to the database as a single
 * execution, or multiple homogeneous parameterized SQL statements to be executed as a single batch execution.
 * <p>Note that not all JDBC drivers, or databases support batch writing.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.BATCH_WRITING, BatchWriting.JDBC);</code>
 *
 * <p>Property values are case-insensitive
 *
 * <ul>
 * <li>JDBC - JDBC batch API's are used (dynamic, or parameterized).
 * <li>Bufferred - dynamic SQL is concatenated into a batch SQL string.
 * <li>Oracle-JDBC - Oracle JDBC batch API's are used (allows row count to be returned for optimistic locking).
 * <li>&lt;custom-class&gt; - A custom class that extends the BatchWritingMechanism class.
 * </ul>
 * @see BatchWritingMechanism
 */
public class BatchWriting {
    public static final String  None = "None";
    public static final String  JDBC = "JDBC";
    public static final String  Buffered = "Buffered";
    public static final String  OracleJDBC = "Oracle-JDBC";

    public static final String DEFAULT = None;
}
