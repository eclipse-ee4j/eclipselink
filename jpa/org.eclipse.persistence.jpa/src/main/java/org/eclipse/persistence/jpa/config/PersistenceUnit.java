/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.jpa.config;

import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface PersistenceUnit {

    Mappings addMappings();
    ClassLoader getClassLoader();
    PersistenceUnitInfo getPersistenceUnitInfo();
    String getName();
    PersistenceUnit setClass(String cls);
    PersistenceUnit setExcludeUnlistedClasses(Boolean setExcludeUnlistedClasses);
    PersistenceUnit setJarFile(String jarFile); // pointless
    PersistenceUnit setJtaDataSource(String jtaDataSource);
    PersistenceUnit setMappingFile(String mappingFile); // pointless.
    PersistenceUnit setName(String name);
    PersistenceUnit setNonJtaDataSource(String nonJtaDataSource);
    PersistenceUnit setProperty(String name, Object value);
    PersistenceUnit setProvider(String provider);
    PersistenceUnit setSharedCacheMode(String sharedCacheMode);
    PersistenceUnit setTransactionType(PersistenceUnitTransactionType transactionType);
    PersistenceUnit setValidationMode(String validationMode);

}
