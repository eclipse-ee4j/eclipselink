/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware, ssmith - OSGi Persistence provider
 ******************************************************************************/  
package org.eclipse.persistence.jpa.osgi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.internal.jpa.deployment.osgi.OSGiPersistenceInitializationHelper;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;

/**
 * EclipseLink JPA provider for use in OSGI Environments
 */
public class PersistenceProvider extends org.eclipse.persistence.jpa.PersistenceProvider{
    
    public PersistenceProvider(){
    	initializationHelper = new OSGiPersistenceInitializationHelper(null);
    }
	
   
    public PersistenceProvider(String initializerClassName){
        // here we override the initialization helper used by super so that we get
        // OSGi specific behavior
        initializationHelper = new OSGiPersistenceInitializationHelper(initializerClassName);
    }
    
    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(
            PersistenceUnitInfo info, Map map) {
        throw EntityManagerSetupException.createContainerEntityManagerFactoryNotSupportedInOSGi();
    }
}
