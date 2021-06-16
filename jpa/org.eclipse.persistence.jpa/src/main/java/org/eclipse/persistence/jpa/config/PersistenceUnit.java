/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
