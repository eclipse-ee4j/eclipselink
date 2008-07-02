/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 * Cache type persistence property values.
 *
 * <p>JPA persistence property Usage:
 * 
 * <p>for all entities append DEFAULT suffix to CACHE_TYPE_ prefix:
 * <p><code>properties.add(PersistenceUnitProperties.CACHE_TYPE_DEFAULT, CacheType.Weak);</code>
 * 
 * <p>for a single entity append either entity name or a full class name to CACHE_TYPE_ prefix:
 * <p><code>properties.add(PersistenceUnitProperties.CACHE_TYPE_ + "Employee", CacheType.Weak);</code>
 * <p><code>properties.add(PersistenceUnitProperties.CACHE_TYPE_ + "my.test.Employee", CacheType.Weak);</code>
 * 
 * <p>Values are case-insensitive.
 * "" could be used instead of default value CacheType.DEFAULT.
 * 
 * @see PersistenceUnitProperties
 */
public class CacheType {
    public static final String  Weak = "Weak";
    public static final String  Soft = "Soft";
    public static final String  SoftWeak = "SoftWeak";
    public static final String  HardWeak = "HardWeak";
    public static final String  Full = "Full";
    public static final String  NONE = "NONE";
 
    public static final String DEFAULT = SoftWeak;
}
