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
package org.eclipse.persistence.config;

/**
 * Cache coordination protocol persistence property values.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.COORDINATION_PROTOCOL, CacheCoordinationProtocol.RMI);</code>
 * <p>Property values are case-insensitive.
 *
 * @see PersistenceUnitProperties#COORDINATION_PROTOCOL
 */
public class CacheCoordinationProtocol {
    public static final String RMI = "rmi";
    public static final String RMIIIOP = "rmi-iiop";
    public static final String JMS = "jms";
    public static final String JMSPublishing = "jms-publishing";
    public static final String JGROUPS = "jgroups";
}
