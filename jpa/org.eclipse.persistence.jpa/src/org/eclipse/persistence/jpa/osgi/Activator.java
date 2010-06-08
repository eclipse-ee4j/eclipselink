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
 *     tware, ssmith = 1.0 - Activator for EclipseLink persistence
 *     tware - updates to allow support of gemini project
 ******************************************************************************/  
package org.eclipse.persistence.jpa.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator for JPA OSGi service.
 * 
 * This activator has two potential sets of behavior
 * If the org.eclipse.persistence.jpa.osgi fragment is available, it will forward calls to an Activator in that bundle
 * that makes our OSGi support occur in an EclipseLink-specific way - as we shipped in our 2.0 release.
 * In the absence of that fragment, this activator will behave in a way specific to the Eclipse Gemini project with
 * allows EclipseLink to function as required by the OSGi JPA specification
 * 
 * @author tware
 */
public class Activator implements BundleActivator {

    /**
     * Context is stored on this activator by its subclass for use 
     * in weaving.
     */
    protected static BundleContext context;
    
    private static final String ECLIPSELINK_SPECIFIC_ACTIVATOR = "org.eclipse.persistence.jpa.osgi.eclipselink.Activator";
    
    public static BundleContext getContext() {
        return Activator.context;
    }
    
    private BundleActivator eclipselinkActivator;
    /**
     * On start, we do two things
     * We register a listener for bundles and we start our JPA server
     */
    public void start(BundleContext context) throws Exception {
        initializeDeprecatedActivator();
        if (eclipselinkActivator != null){
            eclipselinkActivator.start(context);
        }
    }


    public void stop(BundleContext context) throws Exception {
        if (eclipselinkActivator != null){
            eclipselinkActivator.stop(context);
            eclipselinkActivator = null;
        }
    }
    
    private void initializeDeprecatedActivator(){
        try {
            Class activatorClass = this.getClass().getClassLoader().loadClass(ECLIPSELINK_SPECIFIC_ACTIVATOR);
            eclipselinkActivator = (BundleActivator)activatorClass.newInstance();
        } catch (ClassNotFoundException e){
        } catch (IllegalAccessException e){
        } catch (InstantiationException e){
        }
    }

}


