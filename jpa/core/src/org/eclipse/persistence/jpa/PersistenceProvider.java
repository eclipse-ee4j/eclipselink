/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.jpa;

import org.eclipse.persistence.jpa.EntityManagerFactoryProvider;

/**
 * This is the TopLink EJB 3.0 provider
 */
public class PersistenceProvider extends EntityManagerFactoryProvider {

    /**
     * Returns whether the given persistence provider class is supported by this implementation
     * @param providerClassName
     * @return
     */
    public boolean isPersistenceProviderSupported(String providerClassName){
        return (providerClassName == null) || providerClassName.equals("") || providerClassName.equals(PersistenceProvider.class.getName());
    }

}
