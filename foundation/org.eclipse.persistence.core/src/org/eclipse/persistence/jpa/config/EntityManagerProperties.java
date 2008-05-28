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
 *    Gyorke
 *    
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA.
 *        Now properties' names that could be used both in createEM and createEMF are the same. 
 ******************************************************************************/  
package org.eclipse.persistence.jpa.config;

import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;

/**
 * The class defines TopLink properties' names.
 * 
 * This properties are specific to an EnityManger and should be 
 * passed to createEntityManager methods of EntityManagerFactory.
 * 
 * Property values are usually case-insensitive with some common sense exceptions,
 * for instance class names.
 * 
 */
public class EntityManagerProperties {
	
    /**
     * Set to "true" this property forces persistence context to read through JTA-managed ("write") connection
     * in case there is an active transaction.
     * Valid values are case-insensitive "false" and "true"; "false" is default.
     * The property could also be set in persistence.xml or passed to createEntityManagerFactory,
     * in that case it affects all EntityManagers created by the factory. 
     */
    public static final String JOIN_EXISTING_TRANSACTION = PersistenceUnitProperties.JOIN_EXISTING_TRANSACTION;
    
    /**
     * Specifies whether there should be hard or soft references used within the Persistence Context.
     * Default is "HARD".  With soft references entities no longer referenced by the application
     * may be garbage collected freeing resources.  Any changes that have not been flushed in these
     * entities will be lost.
     * The property could also be set in persistence.xml or passed to createEntityManagerFactory,
     * in that case it affects all EntityManagers created by the factory. 
     * @see org.eclipse.persistence.sessions.factories.ReferenceMode
     */
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE= PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE;

    /**
     * This property is used to specify proxy type that should be passed to OarcleConnection.openProxySession method.
     * Requires Oracle jdbc version 10.1.0.2 or later.
     * Requires Oracle9Platform or later as a database platform 
     * (TARGET_DATABASE property value should be TargetDatabase.Oracle9 or later).
     * The valid values are:
     * OracleConnection.PROXYTYPE_USER_NAME, OracleConnection.PROXYTYPE_DISTINGUISHED_NAME, OracleConnection.PROXYTYPE_CERTIFICATE.
     * Property property corresponding to the specified type should be also provided:
     * OracleConnection.PROXY_USER_NAME, OracleConnection.PROXY_DISTINGUISHED_NAME, OracleConnection.PROXY_CERTIFICATE.
     * Typically these properties should be set into EntityManager (either through createEntityManager method or
     * using proprietary setProperties method on EntityManagerImpl) - that causes EntityManager to use proxy connection for
     * writing and reading inside transaction. 
     * If proxy-type and the corresponding proxy property set into EntityManagerFactory then all connections
     * created by the factory will be proxy connections.
     */
    public static final String ORACLE_PROXY_TYPE = PersistenceUnitProperties.ORACLE_PROXY_TYPE;
}
