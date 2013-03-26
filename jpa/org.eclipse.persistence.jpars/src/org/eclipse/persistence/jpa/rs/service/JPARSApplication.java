/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.persistence.jpa.rs.exceptions.ClassNotFoundExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.ConversionExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.DatabaseExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.EntityExistsExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.EntityNotFoundExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IOExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IllegalAccessExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IllegalArgumentExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IllegalStateExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.InvocationTargetExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.JAXBExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSConfigurationExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.MalformedURLExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NamingExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NoResultExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NoSuchMethodExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NonUniqueResultExceptionExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.OptimisticLockExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.PersistenceExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.PessimisticLockExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.QueryTimeoutExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.RollbackExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.TransactionRequiredExceptionMapper;

/**
 * Config class for JPA-RS REST service.  This class should remain dependant only on classes from 
 * the specification since it is designed to work with both Jersey 1.x and Jersey 2.x.
 * 
 * @see ServicePathDefinition
 * @author tware
 *
 */
@ApplicationPath("/persistence/")
public class JPARSApplication extends Application {

    private final Set<Class<?>> classes;

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

        // Exception Mapping
        c.add(ClassNotFoundExceptionMapper.class);
        c.add(ConversionExceptionMapper.class);
        c.add(DatabaseExceptionMapper.class);
        c.add(EntityExistsExceptionMapper.class);
        c.add(EntityNotFoundExceptionMapper.class);
        c.add(IllegalAccessExceptionMapper.class);
        c.add(IllegalArgumentExceptionMapper.class);
        c.add(IllegalStateExceptionMapper.class);
        c.add(InvocationTargetExceptionMapper.class);
        c.add(IOExceptionMapper.class);
        c.add(JAXBExceptionMapper.class);
        c.add(JPARSExceptionMapper.class);
        c.add(MalformedURLExceptionMapper.class);
        c.add(NamingExceptionMapper.class);
        c.add(NonUniqueResultExceptionExceptionMapper.class);
        c.add(NoResultExceptionMapper.class);
        c.add(NoSuchMethodExceptionMapper.class);
        c.add(OptimisticLockExceptionMapper.class);
        c.add(PersistenceExceptionMapper.class);
        c.add(PessimisticLockExceptionMapper.class);
        c.add(QueryTimeoutExceptionMapper.class);
        c.add(RollbackExceptionMapper.class);
        c.add(TransactionRequiredExceptionMapper.class);
        c.add(JPARSConfigurationExceptionMapper.class);

        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
