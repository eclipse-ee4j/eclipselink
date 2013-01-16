/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.config;

/**
 * Tuning persistence property values.
 * 
 * <p>JPA persistence property usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.TUNING, TunerType.Safe);</code>
 * <p>Property values are case-insensitive.
 * 
 * @see PersistenceUnitProperties#TUNING
 */
public class TunerType {
    public static final String Safe = "Safe";
    public static final String Standard = "Standard";
}
