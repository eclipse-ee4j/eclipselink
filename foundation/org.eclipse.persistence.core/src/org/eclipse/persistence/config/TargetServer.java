/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2016 IBM Corporation and/or its affiliates. All rights reserved.
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
//     Rick Curtis - Add WebSphere Liberty target server.
//     03/15/2016 Jody Grassel
//       - 489794: Add support for WebSphere EJBEmbeddable platform.
package org.eclipse.persistence.config;

/**
 * Target server persistence property values.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.TargetServer, TargetServer.OC4J);</code><br>
 * <p>JPA persistence unit persistence.xml  Usage:
 * <p><code>&lt;property name="eclipselink.target-server" value="OC4J"/&gt;</code></p>
 *
 * <p>Property values are case-insensitive
 */
public class TargetServer {
    public static final String  None = "None";
    public static final String  OC4J = "OC4J";
    @Deprecated
    public static final String  SunAS9 = "SunAS9";
    public static final String  Glassfish = "Glassfish";
    public static final String  WebSphere = "WebSphere";
    public static final String  WebSphere_6_1 = "WebSphere_6_1";
    public static final String  WebSphere_7 = "WebSphere_7";
    public static final String  WebSphere_EJBEmbeddable = "WebSphere_EJBEmbeddable";
    public static final String  WebSphere_Liberty = "WebSphere_Liberty";
    public static final String  WebLogic = "WebLogic";
    public static final String  WebLogic_9 = "WebLogic_9";
    public static final String  WebLogic_10 = "WebLogic_10";
    public static final String  WebLogic_12 = "WebLogic_12";
    public static final String  JBoss = "JBoss";
    public static final String  SAPNetWeaver_7_1 = "NetWeaver_7_1";

    public static final String DEFAULT = None;
}
