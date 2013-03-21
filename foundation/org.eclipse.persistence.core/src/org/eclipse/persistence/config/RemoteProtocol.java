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
 * Remote JPA protocol persistence property values.
 * 
 * <p>JPA persistence property usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.REMOTE_PROTOCOL, RemoteProtocol.RMI);</code>
 * <p>Property values are case-insensitive.
 * 
 * @see PersistenceUnitProperties#REMOTE_PROTOCOL
 */
public class RemoteProtocol {
    public static final String RMI = "rmi";
}
