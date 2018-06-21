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
package org.eclipse.persistence.config;

/**
 * Logger type persistence property values.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.LoggerType, LoggerType.JavaLogger);</code>
 * <p>Property values are case-insensitive.
 *
 * @author Wonseok Kim
 */
public class LoggerType {
    public static final String DefaultLogger = "DefaultLogger";
    public static final String JavaLogger = "JavaLogger";
    public static final String ServerLogger = "ServerLogger";

    public static final String DEFAULT = DefaultLogger;
}
