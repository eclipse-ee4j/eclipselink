/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David McCann = 2.1 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper;

/**
 * Class for wrapping cache key lookup results.  The result of a key lookup will
 * be an application name and class loader, or a class loader.
 * 
 * Note that all instances of this class are expected to have a loader set.
 *  
 */
public class MapKeyLookupResult {
    private String applicationName;
    private ClassLoader classLoader;

    /**
     * This constructor should be used when caching on class loader.
     * 
     * @param loader
     */
    public MapKeyLookupResult(ClassLoader classLoader) {
        this.applicationName = null;
        this.classLoader = classLoader;
    }

    /**
     * This constructor should be used when caching on application name.  The loader
     * will be used to determine if a redeploy has occurred, i.e. same application
     * name but different class loaders.
     * 
     * @param applicationName
     * @param loader
     */
    public MapKeyLookupResult(String applicationName, ClassLoader classLoader) {
        this.applicationName = applicationName;
        this.classLoader = classLoader;
    }

    /**
     * Return the applicationName value.
     * 
     * @return
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Return the loader value.
     * 
     * @return
     */
    public ClassLoader getLoader() {
        return classLoader;
    }
}