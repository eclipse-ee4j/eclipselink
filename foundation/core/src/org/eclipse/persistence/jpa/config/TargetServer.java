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
 * Target server persistence property values.
 *
 * <p>JPA persistence property Usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.TargetServer, TargetServer.OC4J_11_1_1);</code>
 * 
 * <p>Property values are case-insensitive
 */
public class TargetServer {
    public static final String  None = "None";
    public static final String  OC4J = "OC4J";
    public static final String  OC4J_10_1_3 = "OC4J_10_1_3";
    public static final String  OC4J_11_1_1 = "OC4J_11_1_1";
    public static final String  SunAS9 = "SunAS9";
    public static final String  WebSphere = "WebSphere";
    public static final String  WebSphere_6_1 = "WebSphere_6_1";
    public static final String  WebLogic = "WebLogic";
    public static final String  WebLogic_9 = "WebLogic_9";
    public static final String  WebLogic_10 = "WebLogic_10";
    public static final String  JBoss = "JBoss";
 
    public static final String DEFAULT = None;
}
