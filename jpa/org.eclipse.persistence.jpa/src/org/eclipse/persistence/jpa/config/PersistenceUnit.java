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
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.config;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface PersistenceUnit {

    public Mappings addMappings();
    public ClassLoader getClassLoader();
    public PersistenceUnitInfo getPersistenceUnitInfo();
    public String getName();
    public PersistenceUnit setClass(String cls);
    public PersistenceUnit setExcludeUnlistedClasses(Boolean setExcludeUnlistedClasses);
    public PersistenceUnit setJarFile(String jarFile); // pointless
    public PersistenceUnit setJtaDataSource(String jtaDataSource);
    public PersistenceUnit setMappingFile(String mappingFile); // pointless.
    public PersistenceUnit setName(String name);
    public PersistenceUnit setNonJtaDataSource(String nonJtaDataSource);
    public PersistenceUnit setProperty(String name, Object value);
    public PersistenceUnit setProvider(String provider);
    public PersistenceUnit setSharedCacheMode(String sharedCacheMode);
    public PersistenceUnit setTransactionType(PersistenceUnitTransactionType transactionType);
    public PersistenceUnit setValidationMode(String validationMode);

}