/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
