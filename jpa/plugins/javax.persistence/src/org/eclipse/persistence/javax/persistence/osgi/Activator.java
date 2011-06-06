/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mkeith
 ******************************************************************************/  
package org.eclipse.persistence.javax.persistence.osgi;

import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for OSGi integration
 * 
 * @author mkeith, tware, ssmith
 */
public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(new OSGiProviderResolver(context));
    }

    public void stop(BundleContext context) throws Exception {
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(null);
    }
}
