/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//      tware - initial
package org.eclipse.persistence.jpa.rs.service;

import java.util.ServiceLoader;
import java.util.Set;

import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactory;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSExceptionMapper;

/**
 * Config class for JPA-RS REST service.  This class should remain dependent only on classes from
 * the specification since it is designed to work with both Jersey 1.x and Jersey 2.x.
 *
 * @author tware
 *
 */
@ApplicationPath("/persistence/")
public class JPARSApplication extends Application {

    private final Set<Class<?>> classes;

    /**
     * Instantiates a new jPARS application.
     */
    public JPARSApplication() {

        // Versioned Resources (resources that do have version in the url)

        //
        classes = Set.of(org.eclipse.persistence.jpa.rs.resources.PersistenceResource.class, org.eclipse.persistence.jpa.rs.resources.PersistenceUnitResource.class, org.eclipse.persistence.jpa.rs.resources.EntityResource.class, org.eclipse.persistence.jpa.rs.resources.SingleResultQueryResource.class, org.eclipse.persistence.jpa.rs.resources.QueryResource.class,

                // JPARS 2.0
                org.eclipse.persistence.jpa.rs.resources.MetadataResource.class,

                // Exception Mapping
                JPARSExceptionMapper.class);
    }

    /* (non-Javadoc)
     * @see jakarta.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    /**
     * Clean up.
     */
    @PreDestroy
    public void preDestroy() {
        DataStorage.destroy();

        ServiceLoader<PersistenceContextFactoryProvider> persistenceContextFactoryProviderLoader =
                ServiceLoader.load(PersistenceContextFactoryProvider.class, Thread.currentThread().getContextClassLoader());

        for (PersistenceContextFactoryProvider persistenceContextFactoryProvider : persistenceContextFactoryProviderLoader) {
            PersistenceContextFactory persistenceContextFactory = persistenceContextFactoryProvider.getPersistenceContextFactory(null);
            if (persistenceContextFactory != null) {
                persistenceContextFactory.close();
            }
        }
    }
}
