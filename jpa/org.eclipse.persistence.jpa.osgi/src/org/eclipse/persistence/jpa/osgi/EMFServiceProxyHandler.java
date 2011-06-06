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
 *     mkeith - Gemini JPA work 
 *     ssmith - EclipseLink integration
 ******************************************************************************/
package org.eclipse.persistence.jpa.osgi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * Dynamic proxy class to proxy the EMF service
 */
public class EMFServiceProxyHandler implements InvocationHandler, ServiceFactory {
    protected EntityManagerFactory emf;
    protected PersistenceProvider provider;
    protected String puName;
    
    public EMFServiceProxyHandler(PersistenceProvider provider, String puName) {
        this.provider = provider;
        this.puName = puName;
    }
    
    // May be called by the EMFBuilder service
    EntityManagerFactory getEMF() { 
        return this.emf; 
    }
    
    void setEMF(EntityManagerFactory emf) {
        this.emf = emf;
    }
   

    /*=========================*/
    /* InvocationProxy methods */
    /*=========================*/
    
    // Will only get calls for the methods on the EntityManagerFactory interface
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // If close() invoked then just ignore it
//        if (method.getName().equals("close"))
//            return null;

        // Invoke these methods on the actual proxy (not the object it's proxying)
        if (method.getName().equals("hashCode"))
            return this.hashCode();
        if (method.getName().equals("toString"))
            return this.toString();
        
        if (emf == null) {
            synchronized(this) {
                if (emf == null) {
                    emf = createEMF(new HashMap<String,Object>());
                }
            } 
        }
        // Invoke the EMF method that was called
        Object result = method.invoke(emf,args);
        
        // If the operation was to close the EMF then remove our ref to it
        synchronized(this) {
            if (!emf.isOpen()) 
                emf = null;
        }
        return result;
    }
    
    /*========================*/
    /* ServiceFactory methods */
    /*========================*/

    public Object getService(Bundle b, ServiceRegistration serviceReg) {
        // TODO Track client bundles that use this service and clean up if they leave
        return this;
    }
    
    public void ungetService(Bundle b, ServiceRegistration serviceReg, Object obj) {
        // EMF is shared, leave as is until the p-unit or the provider goes away
        // and the service is unregistered
    }
    
    /*================*/
    /* Helper methods */
    /*================*/

    // Use info from the cached pUnitInfo and create a new EMF to store locally
    protected EntityManagerFactory createEMF(Map<String,Object> props) {        
        EntityManagerFactory result = this.provider.createEntityManagerFactory(this.puName, props);
        if (result == null)
            fatalError("Proxy could not create EMF " + puName + " from provider " + provider, null);
        return result;
    }

    private void fatalError(String string, Object object) {
        // TODO EclipseLink Logging
        System.err.println(this.getClass().getName() + ": " + string + ": " + object);
        
    }

}        

