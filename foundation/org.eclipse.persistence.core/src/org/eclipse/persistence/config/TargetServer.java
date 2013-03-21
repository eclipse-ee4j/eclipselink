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
    public static final String  WebLogic = "WebLogic";
    public static final String  WebLogic_9 = "WebLogic_9";
    public static final String  WebLogic_10 = "WebLogic_10";
    public static final String  JBoss = "JBoss";
    public static final String  SAPNetWeaver_7_1 = "NetWeaver_7_1";
 
    public static final String DEFAULT = None;
}
