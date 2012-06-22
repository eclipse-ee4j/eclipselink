/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     mkeith - Gemini JPA work 
 *     ssmith - EclipseLink integration
 ******************************************************************************/
package org.eclipse.persistence.jpa.osgi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * Dynamic proxy class to proxy the EMFBuilder service
 */
public class EMFBuilderServiceProxyHandler extends EMFServiceProxyHandler
                                           implements InvocationHandler {
            
    // Keep this to let us know if a factory has already been created via the EMF service
    protected EMFServiceProxyHandler emfService;
    
    public EMFBuilderServiceProxyHandler(PersistenceProvider provider,
                                         String puName,
                                         EMFServiceProxyHandler emfService) {
        super(provider, puName);
        this.emfService = emfService;
    }

    /*=========================*/
    /* InvocationProxy methods */
    /*=========================*/

    // Will only get calls for the method on the EntityManagerFactoryBuilder interface
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equals("hashCode"))
            return this.hashCode();

        // Must be a createEntityManagerFactory(String, Map) call
        
        // If we have a factory and it has already been closed, discard it
        synchronized (this) {
            if ((emf != null) && (!emf.isOpen()))
                emf = null;
    
            // If we have a local factory, return it
            if (emf != null) 
                return emf;
        }

        // If an EMF service is registered the EMF must be stored there
        if (emfService != null) {

            // Synchronize to ensure we share the same factory
            synchronized(emfService) {
                
                // If EMF service has one that is closed then discard it
                if ((emfService.getEMF() != null) && (!emfService.getEMF().isOpen())) {
                    emfService.setEMF(null);
                }
                // If it doesn't have one, then assign it one
                // The first arg must be the props Map
                Map<String,Object> props = (Map<String,Object>)args[0];
                if (emfService.getEMF() == null) {
                    emfService.setEMF(createEMF(props));
                }      
                // Create a proxy to the EMF in the EMFService
                return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                              new Class[] { EntityManagerFactory.class },
                                              emfService);
            }
        } else {
            // No EMF service (data source was not active). Create our own EMF since we don't have one
            synchronized (this) {
                if (emf == null) {
                    emf = createEMF((Map<String,Object>)args[0]);
                }
            }
            return emf;
        }
    }
}        
