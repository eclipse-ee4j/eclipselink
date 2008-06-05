/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - June 4/2008 - 1.0M8 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.delegates;

import java.util.ArrayList;

import org.eclipse.persistence.sdo.SDOConstants;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * Abstract class for SDO helper delegators.
 * 
 */
public abstract class AbstractHelperDelegator {
    // Hold the context containing all helpers so that we can preserve inter-helper relationships
    protected HelperContext aHelperContext;

    /**
     * INTERNAL:
     * This method returns the application ClassLoader.
     * 
     * The parent application ClassLoader is returned when running in a J2EE client 
     * either in a web or ejb container to match a weak reference to a particular 
     * HelperContext.
     */
    protected ClassLoader getContextClassLoader() {

        /**
         * ClassLoader levels: 
         * 	oc4j:
         *  	0 - APP.web (servlet/jsp) or APP.wrapper (ejb) or
         *  	1 - APP.root (parent for helperContext)
         *  	2 - default.root
         *  	3 - system.root
         *  	4 - oc4j.10.1.3 (remote EJB) or org.eclipse.persistence:11.1.1.0.0
         *  	5 - api:1.4.0
         *  	6 - jre.extension:0.0.0
         *  	7 - jre.bootstrap:1.5.0_07 (with various J2SE versions)
         *  WebLogic:
         *  	0 - WEB & EJB module loaders (these can be customized up to 3 layers deep)
         *  	1 - Application root loader
         *  	2 - WebLogic filtering loader
         *  	3 - Root WebLogic loader
         *  	4 - URL loader
         *  	5 - System loader
         *  	5 - Extensions loader
         *  	6 - Bootstrap loader
         */

        // Kludge for running in OC4J (from WebServices group), and WebLogic
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String classLoaderName = classLoader.getClass().getName();
        // Handle oracle ClassLoader structure
        if (classLoaderName.startsWith("oracle")) {
        	// Check to see if we are running in a Servlet container or a local EJB container
            if ((classLoader.getParent() != null) //
            		&& ((classLoader.toString().indexOf(SDOConstants.CLASSLOADER_WEB_FRAGMENT) != -1) //
            		||  (classLoader.toString().indexOf(SDOConstants.CLASSLOADER_EJB_FRAGMENT) != -1))) {
                classLoader = classLoader.getParent();
            }
        // Handle WebLogic ClassLoader structure
        } else if (classLoaderName.startsWith("weblogic")) {
        	final int systemLoaderOffest = 4; // indicates the number of child loaders between system and application
        	ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        	ClassLoader currentLoader = classLoader;
        	ArrayList<ClassLoader> loaders = new ArrayList<ClassLoader>();
        	loaders.add(currentLoader);
        	while (currentLoader.getParent() != null) {
        		currentLoader = currentLoader.getParent();
            	loaders.add(currentLoader);
            	if (currentLoader.getParent() == systemLoader) {
            		if (loaders.size() >= 4) {
            			classLoader = loaders.get(loaders.size() - systemLoaderOffest); 
            			break;
            		}
            	}
        	}
        }
    	// else we are running in a J2SE client (toString() contains a JVM hash) or an unmatched container level
        return classLoader;
    }

    /**
     * Return the helperContext that this instance is associated with.
     * This context contains all helpers. If null, the default context
     * is returned. 
     * 
     * @return set helper context or, if null, the default context
     * @see commonj.sdo.helper.HelperContext
     * @see commonj.sdo.impl.HelperProvider
     */
    public HelperContext getHelperContext() {
        if (null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.  This context 
     * will contain all helpers, so inter-helper relationships are preserved.
     *
     * @param helperContext
     * @see commonj.sdo.helper.HelperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
