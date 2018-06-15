/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

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
        HashSet<Class<?>> c = new HashSet<Class<?>>();

        // Unversioned Resources (resources that do not have version in the url)
        c.add(org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceUnitResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.unversioned.EntityResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.unversioned.SingleResultQueryResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.unversioned.QueryResource.class);

        // Versioned Resources (resources that do have version in the url)
        c.add(org.eclipse.persistence.jpa.rs.resources.PersistenceResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.PersistenceUnitResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.EntityResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.SingleResultQueryResource.class);
        c.add(org.eclipse.persistence.jpa.rs.resources.QueryResource.class);

        // JPARS 2.0
        c.add(org.eclipse.persistence.jpa.rs.resources.MetadataResource.class);

        // Exception Mapping
        c.add(JPARSExceptionMapper.class);

        //
        classes = Collections.unmodifiableSet(c);
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.core.Application#getClasses()
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
