/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa.config;

/**
 * Specify the use of batch writing to optimize transactions with multiple writes,
 * by default batch writing is not used.
 * 
 * <p>JPA persistence property Usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.BATCH_WRITING, BatchWriting.JDBC);</code>
 * 
 * <p>Property values are case-insensitive
 */
public class BatchWriting {
    public static final String  None = "None";
    public static final String  JDBC = "JDBC";
    public static final String  Buffered = "Buffered";
    public static final String  OracleJDBC = "Oracle-JDBC";
 
    public static final String DEFAULT = None;
}
